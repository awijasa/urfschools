/*
Document   : Groovlet file: StudentWithLastEnrollmentController.groovy
Created on : Fri Aug 3 22:34:00 CST 2012
Author     : awijasa
 */

import com.google.appengine.api.datastore.Entity
import data.StudentWithLastEnrollment
import data.Term
import data.URFUser

/* Handle convert and drop operations on the StudentWithLastEnrollment entity. */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the StudentWithLastEnrollmentController. */
if( user == null && !localMode )
    response.sendError( response.SC_UNAUTHORIZED )
    
/* Allow the user to convert and drop only if the request comes from a test script or an admin with Modify privilege. */
else if( user == null || URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
    
    /* Convert one Entity field to another Entity field. */
    if( params.action.equals( "convert" ) ) {
        try {
            
            /* Convert some date fields to term fields. */
            if( params.from.equals( "date" ) && params.to.equals( "term" ) ) {
                StudentWithLastEnrollment.list().each() {
                    
                    /* Convert lastEnrollmentDate field to lastEnrollmentTermYear and lastEnrollmentTermNo fields. */
                    def lastEnrollmentTerm = Term.findByTermSchoolAndDate( it.lastEnrollmentSchool, it.lastEnrollmentDate )
                    if( lastEnrollmentTerm != null ) {
                        it.lastEnrollmentTermYear = lastEnrollmentTerm.year
                        it.lastEnrollmentTermNo = lastEnrollmentTerm.termNo
                    }
                    
                    /* Convert lastEnrollmentLeaveDate field to lastEnrollmentLeaveTermYear and lastEnrollmentLeaveTermNo fields. */
                    def lastEnrollmentLeaveTerm = Term.findByTermSchoolAndDate( it.lastEnrollmentSchool, it.lastEnrollmentLeaveDate )
                    if( lastEnrollmentLeaveTerm != null ) {
                        it.lastEnrollmentLeaveTermYear = lastEnrollmentLeaveTerm.year
                        it.lastEnrollmentLeaveTermNo = lastEnrollmentLeaveTerm.termNo
                    }
                    
                    it.save()
                }
            }
            
            println "Converted StudentWithLastEnrollment ${ params.from } values to ${ params.to } values."
        }
        catch( Exception e ) {
            println e.getStackTrace()
        }
    }
    
    /* Drop a StudentWithLastEnrollment entity field identified by the fieldName parameter when the action parameter value is "drop". */
    else if( params.action.equals( "drop" ) ) {
        try {
            StudentWithLastEnrollment.list().each() {
                it.removeProperty( params.fieldName )
                it.save()
            }
            
            println "Dropped ${ params.fieldName } from all StudentWithLastEnrollment entities."
        }
        catch( Exception e ) {
            println e.getMessage()
        }
    }
}
else {
    response.setStatus( response.SC_UNAUTHORIZED )
    response.setHeader( "Response-Phrase", "Unauthorized" )
}