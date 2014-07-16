/*
Document   : Groovlet file: GenderController.groovy
Created on : Thu May 31 18:41:00 CST 2012
Author     : awijasa
 */

import com.google.appengine.api.datastore.Entity
import data.DuplicateEntityException
import data.EmptyRequiredFieldException
import data.EntityIsUsedException
import data.Gender
import data.StaleRecordException
import data.URFUser
import formatter.ListItemFormatter

/* Handle create, update, and delete operations on the Gender entity. */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the GenderController through urfschools.appspot.com. */
if( user == null && !localMode )
    response.sendError( response.SC_UNAUTHORIZED )
else {
    
    /* Allow the user to delete, edit, and save only if the request comes from a test script or an admin with Modify privilege. */
    if( /* Test Script */ user == null || /* Admin with Modify privilege */ URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
    
        /* Delete a Gender entity identified by the id parameter when the action parameter value is "delete". */
        if( params.action.equals( "delete" ) ) {
            try {
                def gender = datastore.get( "Gender", Long.parseLong( params.id ) )

                if( Gender.isUsed( gender ) )
                    throw new EntityIsUsedException( gender, Gender.getPrimaryKey() )
                    
                if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != gender.lastUpdateDate ||
                    ( params.lastUpdateUser?: "" ) != ( gender.lastUpdateUser?: "" ) )
                    throw new StaleRecordException( params.action, "Gender" )

                gender.delete()

                Gender.deleteMemcache()
            }
            catch( Exception e ) {

                /* Respond with an Internal Server error message if the delete fails. */
                response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
                response.setHeader( "Response-Phrase", e.getMessage() )
            }
        }

        /* Save a new Gender entity when the action parameter value is "save". */
        else if( params.action.equals( "edit" ) || params.action.equals( "save" ) ) {
            try {
                def gender

                if( params.action.equals( "edit" ) ) {
                    gender = datastore.get( "Gender", Long.parseLong( params.id ) )
                    
                    if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != gender.lastUpdateDate ||
                        ( params.lastUpdateUser?: "" ) != ( gender.lastUpdateUser?: "" ) )
                        throw new StaleRecordException( params.action, "Gender" )
                }
                else {
                    gender = new Entity( "Gender" )

                    /* If the code parameter value is "Code" or empty, do not put it into the Gender entity's code field. */
                    if( params.code != null && !params.code.equals( "Code" ) && !params.code.equals( "" ) )
                        gender.code = params.code

                    /* If the code parameter value is "Code" or empty, and code is a required Gender entity's field, throw an EmptyRequiredFieldException. */
                    else if( Gender.isRequired( "code" ) )
                        throw new EmptyRequiredFieldException( "Gender", "code" )
                }

                /* If the desc parameter value is "Description" or empty, do not put it into the Gender entity's desc field. */
                if( params.desc != null && !params.desc.equals( "Description" ) && !params.desc.equals( "" ) )
                    gender.desc = params.desc

                /* If the desc parameter value is "Description" or empty, and desc is a required Gender entity's field, throw an EmptyRequiredFieldException. */
                else if( Gender.isRequired( "desc" ) )
                    throw new EmptyRequiredFieldException( "Gender", "desc" )

                gender.lastUpdateDate = new Date()

                if( user != null )
                    gender.lastUpdateUser = user.getEmail()

                if( params.action.equals( "save" ) && Gender.findByCode( gender.code ) != null )
                    throw new DuplicateEntityException( gender, Gender.getPrimaryKey() )

                gender.save()

                Gender.deleteMemcache()

                /* Respond with the HTML code that is required to display the new Gender entity within a table. */
                println ListItemFormatter.getGenderListItem( gender )
            }
            catch( Exception e ) {

                /* Respond with an Internal Server error message if the save fails. */
                response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
                response.setHeader( "Response-Phrase", e.getMessage() )
            }
        }
    }
    else {
        response.setStatus( response.SC_UNAUTHORIZED )
        response.setHeader( "Response-Phrase", "Unauthorized" )
    }
}