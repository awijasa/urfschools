/*
Document   : Groovlet file: LeaveReasonCategoryController.groovy
Created on : Thu May 31 18:41:00 CST 2012
Author     : awijasa
 */

import com.google.appengine.api.datastore.Entity
import data.DuplicateEntityException
import data.EmptyRequiredFieldException
import data.EntityIsUsedException
import data.LeaveReasonCategory
import data.StaleRecordException
import data.URFUser
import formatter.ListItemFormatter

/* Handle create, update, and delete operations on the LeaveReasonCategory entity. */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the LeaveReasonCategoryController through urfschools.appspot.com. */
if( user == null && !localMode )
    response.sendError( response.SC_UNAUTHORIZED )
else {
    
    /* Allow the user to delete and save only if the request comes from a test script or an admin with Modify privilege. */
    if( /* Test Script */ user == null || /* Admin with Modify privilege */ URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
    
        /* Delete a LeaveReasonCategory entity identified by the id parameter when the action parameter value is "delete". */
        if( params.action.equals( "delete" ) ) {
            try {
                def leaveReasonCategory = datastore.get( "LeaveReasonCategory", Long.parseLong( params.id ) )

                if( LeaveReasonCategory.isUsed( leaveReasonCategory ) )
                    throw new EntityIsUsedException( leaveReasonCategory, LeaveReasonCategory.getPrimaryKey() )
                    
                if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != leaveReasonCategory.lastUpdateDate ||
                    ( params.lastUpdateUser?: "" ) != ( leaveReasonCategory.lastUpdateUser?: "" ) )
                    throw new StaleRecordException( params.action, "LeaveReasonCategory" )

                leaveReasonCategory.delete()

                LeaveReasonCategory.deleteMemcache()
            }
            catch( Exception e ) {

                /* Respond with an Internal Server error message if the delete fails. */
                response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
                response.setHeader( "Response-Phrase", e.getMessage() )
            }
        }

        /* Save a new LeaveReasonCategory entity when the action parameter value is "save". */
        else if( params.action.equals( "save" ) ) {
            try {
                def leaveReasonCategory = new Entity( "LeaveReasonCategory" )

                /* If the category parameter value is "Category" or empty, do not put it into the LeaveReasonCategory entity's category field. */
                if( params.category != null && !params.category.equals( "Category" ) && !params.category.equals( "" ) )
                    leaveReasonCategory.category = params.category

                /* If the category parameter value is "Category" or empty, and category is a required LeaveReasonCategory entity's field, throw an EmptyRequiredFieldException. */
                else if( LeaveReasonCategory.isRequired( "category" ) )
                    throw new EmptyRequiredFieldException( "LeaveReasonCategory", "category" )

                leaveReasonCategory.lastUpdateDate = new Date()

                if( user != null )
                    leaveReasonCategory.lastUpdateUser = user.getEmail()

                if( LeaveReasonCategory.findByCategory( leaveReasonCategory.category ) != null )
                    throw new DuplicateEntityException( leaveReasonCategory, LeaveReasonCategory.getPrimaryKey() )

                leaveReasonCategory.save()

                LeaveReasonCategory.deleteMemcache()

                /* Respond with the HTML code that is required to display the new LeaveReasonCategory entity within a table. */
                println ListItemFormatter.getLeaveReasonCategoryListItem( leaveReasonCategory )
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