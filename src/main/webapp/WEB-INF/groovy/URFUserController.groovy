/*
Document   : Groovlet file: URFUserController.groovy
Created on : Sun Nov 4 11:56:00 SGT 2012
Author     : awijasa
 */

import com.google.appengine.api.datastore.Entity
import data.DuplicateEntityException
import data.EmptyRequiredFieldException
import data.EntityIsUsedException
import data.InvalidDateRangeException
import data.OverlappedTermException
import data.School
import data.StaleRecordException
import data.URFUser
import data.UserPrivilege
import formatter.ListItemFormatter
import java.text.DecimalFormat

/* Handle create, update, and delete operations on the URFUser entity. */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the URFUserController. */
if( user == null && !localMode )
    response.sendError( response.SC_UNAUTHORIZED )
else {
    
    /* Allow the user to delete, edit, and save only if the request comes from a test script or an admin with Modify privilege. */
    if( user == null || URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
        
        /* Delete a URFUser entity identified by the id parameter when the action parameter value is "delete". */
        if( params.action.equals( "delete" ) ) {
            def urfUser
            def userPrivileges
            
            try {
                urfUser = datastore.get( "URFUser", Long.parseLong( params.id ) )
                
                if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != urfUser.lastUpdateDate ||
                    ( params.lastUpdateUser?: "" ) != ( urfUser.lastUpdateUser?: "" ) )
                    throw new StaleRecordException( params.action, "URFUser" )
                
				URFUser.findByLimitAndOffset( 0, Integer.parseInt( params.nextTwentyOffset ) ).each() {
					/*
					 * Respond with the HTML code that is required to display the URFUser entity that can fill the empty space that resulted from the deleted
					 * record within an accordion.
					 */
					println ListItemFormatter.getURFUserListItem( it )
				}
					
				userPrivileges = UserPrivilege.findByUserEmail( urfUser.email )
                
                userPrivileges.each() {
                    it.delete()
                    
                    UserPrivilege.deleteMemcache( urfUser.email )
                }

                urfUser.delete()
            }
            catch( Exception e ) {
                urfUser?.save()
                userPrivileges*.save()

                /* Respond with an Internal Server error message if the delete fails. */
                response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
                response.setHeader( "Response-Phrase", e.getMessage() )
            }
        }

        /*
         * Edit an existing URFUser entity and its associated UserPrivilege entities when the action parameter value is "edit".
         * Save a new URFUser entity and its UserPrivilege entities when the action parameter value is "save".
         */
        else if( params.action.equals( "edit" ) || params.action.equals( "save" ) ) {
            def addedSchoolUserPrivileges = new ArrayList()
            def schoolUserPrivilegeBackups = new ArrayList()
            def urfUser
            def urfUserBackup

            try {
                if( params.action.equals( "edit" ) ) {
                    urfUser = datastore.get( "URFUser", Long.parseLong( params.id ) )
                    urfUserBackup = datastore.get( "URFUser", Long.parseLong( params.id ) )
                    
                    if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != urfUser.lastUpdateDate ||
                        ( params.lastUpdateUser?: "" ) != ( urfUser.lastUpdateUser?: "" ) )
                        throw new StaleRecordException( params.action, "URFUser" )
                }
                else {
                    urfUser = new Entity( "URFUser" )

                    /* If the email parameter value is "Email" or empty, do not put it into the URFUser entity's email field. */
                    if( params.email != null && !params.email.equals( "Email" ) && !params.email.equals( "" ) )
                        urfUser.email = params.email

                    /* If the email parameter value is "Email" or empty, and email is a required URFUser entity's field, throw an EmptyRequiredFieldException. */
                    else if( URFUser.isRequired( "email" ) )
                        throw new EmptyRequiredFieldException( "URFUser", "email" )
                }

                /* If the sponsorDataPrivilege parameter value is "Sponsor Data Privilege" or empty, do not put it into the URFUser entity's sponsorDataPrivilege field. */
                if( params.sponsorDataPrivilege != null && !params.sponsorDataPrivilege.equals( "Sponsor Data Privilege" ) && !params.sponsorDataPrivilege.equals( "" ) )
                    urfUser.sponsorDataPrivilege = params.sponsorDataPrivilege.equals( "None" )? null: params.sponsorDataPrivilege

                /* If the sponsorDataPrivilege parameter value is "Sponsor Data Privilege" or empty, and sponsorDataPrivilege is a required URFUser entity's field, throw an EmptyRequiredFieldException. */
                else if( URFUser.isRequired( "sponsorDataPrivilege" ) )
                    throw new EmptyRequiredFieldException( "URFUser", "sponsorDataPrivilege" )

                /* If the adminPrivilege parameter value is "Admin Privilege" or empty, do not put it into the URFUser entity's adminPrivilege field. */
                if( params.adminPrivilege != null && !params.adminPrivilege.equals( "Admin Privilege" ) && !params.adminPrivilege.equals( "" ) )
                    urfUser.adminPrivilege = params.adminPrivilege.equals( "None" )? null: params.adminPrivilege

                /* If the adminPrivilege parameter value is "Admin Privilege" or empty, and adminPrivilege is a required URFUser entity's field, throw an EmptyRequiredFieldException. */
                else if( URFUser.isRequired( "adminPrivilege" ) )
                    throw new EmptyRequiredFieldException( "URFUser", "adminPrivilege" )

                urfUser.lastUpdateDate = new Date()

                if( user != null )
                    urfUser.lastUpdateUser = user.getEmail()

                if( params.action.equals( "edit" ) || URFUser.findByEmail( urfUser.email ) == null )
                    urfUser.save()
                else
                    throw new DuplicateEntityException( urfUser, URFUser.getPrimaryKey() )

                School.list().each() {
                    def schoolDataPrivilegeParam = request.getParameter( "${ it.getKey().getId() }Privilege" )
                    def schoolUserPrivilege = UserPrivilege.findByUserEmailAndSchoolName( urfUser.email, it.name )

                    if( schoolUserPrivilege != null )
                        schoolUserPrivilegeBackups.add UserPrivilege.findByUserEmailAndSchoolName( urfUser.email, it.name )

                    if( schoolDataPrivilegeParam.equals( "None" ) )
                        schoolUserPrivilege?.delete()
                    else if( schoolDataPrivilegeParam.equals( "Read" ) || schoolDataPrivilegeParam.equals( "Modify" ) ) {
                        if( schoolUserPrivilege == null ) {
                            schoolUserPrivilege = new Entity( "UserPrivilege", urfUser?.getKey() )
                            schoolUserPrivilege.userEmail = urfUser.email
                            schoolUserPrivilege.schoolName = it.name

                            addedSchoolUserPrivileges.add schoolUserPrivilege
                        }

                        schoolUserPrivilege.privilege = schoolDataPrivilegeParam

                        schoolUserPrivilege.lastUpdateDate = new Date()

                        if( user != null )
                            schoolUserPrivilege.lastUpdateUser = user.getEmail()

                        schoolUserPrivilege.save()
                    }
                }

                UserPrivilege.deleteMemcache( urfUser.email )
                
                /* Respond with the HTML code that is required to display the new URFUser entity within a table. */
                println ListItemFormatter.getURFUserListItem( urfUser )
            }
            catch( Exception e ) {
                
                /* Restore the old UserPrivileges. */
                schoolUserPrivilegeBackups.each() {
                    try { it.save() } catch( Exception saveException ) {}
                }

                /* Delete the added UserPrivileges. */
                addedSchoolUserPrivileges.each() {
                    try{ it.delete() } catch( Exception deleteException ) {}
                }

                if( params.action.equals( "edit" ) ) {
                    
                    /* Restore the old URFUser. */
                    try { urfUserBackup?.save() } catch( Exception saveException ) {}
                }
                else {
                    
                    /* Delete the added URFUser. */
                    try { urfUser?.delete() } catch( Exception deleteException ) {}
                }

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