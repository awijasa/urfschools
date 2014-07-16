/*
Document   : Groovlet file: ParentalRelationshipController.groovy
Created on : Sun Jun 3 15:02:00 CST 2012
Author     : awijasa
 */

import com.google.appengine.api.datastore.Entity
import data.AnonymousParentalRelationship
import data.DuplicateEntityException
import data.EmptyRequiredFieldException
import data.EntityIsUsedException
import data.ParentalRelationship
import data.StaleRecordException
import data.URFUser
import formatter.ListItemFormatter

/* Handle create, update, and delete operations on the ParentalRelationship entity. */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the ParentalRelationshipController through urfschools.appspot.com. */
if( user == null && !localMode )
    response.sendError( response.SC_UNAUTHORIZED )
else {
    
    /* Allow the user to delete and save only if the request comes from a test script or an admin with Modify privilege. */
    if( /* Test Script */ user == null || /* Admin with Modify privilege */ URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
    
        /* Delete a ParentalRelationship entity identified by the id parameter when the action parameter value is "delete". */
        if( params.action.equals( "delete" ) ) {
            try {
                def parentalRelationship = datastore.get( "ParentalRelationship", Long.parseLong( params.id ) )

                if( ParentalRelationship.isUsed( parentalRelationship ) )
                    throw new EntityIsUsedException( parentalRelationship, ParentalRelationship.getPrimaryKey() )
                    
                if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != parentalRelationship.lastUpdateDate ||
                    ( params.lastUpdateUser?: "" ) != ( parentalRelationship.lastUpdateUser?: "" ) )
                    throw new StaleRecordException( params.action, "ParentalRelationship" )

                AnonymousParentalRelationship.findByParentalRelationship( parentalRelationship.parentalRelationship )*.delete()
                
                parentalRelationship.delete()

                ParentalRelationship.deleteMemcache()
            }
            catch( Exception e ) {

                /* Respond with an Internal Server error message if the delete fails. */
                response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
                response.setHeader( "Response-Phrase", e.getMessage() )
            }
        }

        /* Save a new ParentalRelationship entity when the action parameter value is "save". */
        else if( params.action.equals( "save" ) ) {
            try {
                def parentalRelationship = new Entity( "ParentalRelationship" )

                /* If the parentalRelationship parameter value is "Parental Relationship" or empty, do not put it into the ParentalRelationship entity's parentalRelationship field. */
                if( params.parentalRelationship != null && !params.parentalRelationship.equals( "Parental Relationship" ) && !params.parentalRelationship.equals( "" ) )
                    parentalRelationship.parentalRelationship = params.parentalRelationship

                /* If the parentalRelationship parameter value is "Parental Relationship" or empty, and parentalRelationship is a required ParentalRelationship entity's field, throw an EmptyRequiredFieldException. */
                else if( ParentalRelationship.isRequired( "parentalRelationship" ) )
                    throw new EmptyRequiredFieldException( "ParentalRelationship", "parentalRelationship" )

                parentalRelationship.lastUpdateDate = new Date()

                if( user != null )
                    parentalRelationship.lastUpdateUser = user.getEmail()

                if( ParentalRelationship.findByParentalRelationship( parentalRelationship.parentalRelationship ) != null )
                    throw new DuplicateEntityException( parentalRelationship, ParentalRelationship.getPrimaryKey() )

                parentalRelationship.save()

                ParentalRelationship.deleteMemcache()

                /* Respond with the HTML code that is required to display the new ParentalRelationship entity within a table. */
                println ListItemFormatter.getParentalRelationshipListItem( parentalRelationship )
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