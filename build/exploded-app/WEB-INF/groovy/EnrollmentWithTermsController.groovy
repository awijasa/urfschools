/*
Document   : Groovlet file: EnrollmentWithTermsController.groovy
Created on : Sat Mar 30 18:49:00 CST 2013
Author     : awijasa
 */

import com.google.appengine.api.datastore.Entity
import data.Enrollment
import data.EnrollmentWithTerms
import data.Term
import data.URFUser

/* Handle sync operation on the EnrollmentWithTerms entity. */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the EnrollmentWithTermsController. */
if( user == null && !localMode )
    response.sendError( response.SC_UNAUTHORIZED )
    
/* Allow the user to sync only if the request comes from a test script or an admin with Modify privilege. */
else if( user == null || URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
    
    /* Sync EnrollmentWithTerms entities with Enrollment and Term entities. */
    if( params.action.equals( "sync" ) ) {
        try {
            int deleteCount = 0
            int editCount = 0
            boolean isUpdateRequired = false
            int saveCount = 0
            
            EnrollmentWithTerms.list().each(
                {
                    Entity enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( it.studentId, it.schoolName, it.enrollTermNo, it.enrollTermYear )
                    Entity enrollTerm = Term.findByTermSchoolAndTermNoAndYear( it.schoolName, it.enrollTermNo, it.enrollTermYear )
                    Entity leaveTerm = Term.findByTermSchoolAndTermNoAndYear( it.schoolName, it.leaveTermNo, it.leaveTermYear )
                    
                    if( enrollment == null ) {
                        it.delete()
                        deleteCount++
                    }
                    else {
                        if( ( it.enrollDate?: Date.parse( "MMM d yy", "Jan 1 1901" ) ) != ( enrollTerm.startDate?: Date.parse( "MMM d yy", "Jan 1 1901" ) ) ) {
                            it.enrollDate = enrollTerm.startDate
                            isUpdateRequired = true
                        }
                        
                        if( ( it.leaveTermYear?: 0 ) != ( enrollment.leaveTermYear?: 0 ) ) {
                            it.leaveTermYear = enrollment.leaveTermYear
                            isUpdateRequired = true
                        }
                        
                        if( ( it.leaveTermNo?: -1 ) != ( enrollment.leaveTermNo?: -1 ) ) {
                            it.leaveTermNo = enrollment.leaveTermNo
                            isUpdateRequired = true
                        }
                        
                        if( it.leaveDate != ( leaveTerm?.endDate?: Date.parse( "MMM d yy", "Dec 31 2999" ) ) ) {
                            it.leaveDate = leaveTerm?.endDate?: Date.parse( "MMM d yy", "Dec 31 2999" )
                            isUpdateRequired = true
                        }
                        
                        if( !( it.firstClassAttended?: "" ).equals( enrollment.firstClassAttended?: "" ) ) {
                            it.firstClassAttended = enrollment.firstClassAttended
                            isUpdateRequired = true
                        }
                        
                        if( !( it.lastClassAttended?: "" ).equals( enrollment.lastClassAttended?: "" ) ) {
                            it.lastClassAttended = enrollment.lastClassAttended
                            isUpdateRequired = true
                        }
                        
                        if( !( it.leaveReasonCategory?: "" ).equals( enrollment.leaveReasonCategory?: "" ) ) {
                            it.leaveReasonCategory = enrollment.leaveReasonCategory
                            isUpdateRequired = true
                        }
                        
                        if( !( it.leaveReason?: "" ).equals( enrollment.leaveReason?: "" ) ) {
                            it.leaveReason = enrollment.leaveReason
                            isUpdateRequired = true
                        }
                        
                        if( isUpdateRequired ) {
                            it.lastUpdateDate = new Date()
                            it.lastUpdateUser = user?.getEmail()
                            it.save()
                            
                            editCount++
                            isUpdateRequired = false
                        }
                    }
                }
            )
            
            Enrollment.list().each(
                {
                    Entity enrollTerm = Term.findByTermSchoolAndTermNoAndYear( it.schoolName, it.enrollTermNo, it.enrollTermYear )
                    Entity leaveTerm = Term.findByTermSchoolAndTermNoAndYear( it.schoolName, it.leaveTermNo, it.leaveTermYear )
                    Entity enrollmentWithTerms = EnrollmentWithTerms.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( it.studentId, it.schoolName, it.enrollTermNo, it.enrollTermYear )
                    
                    if( enrollmentWithTerms == null ) {
                        enrollmentWithTerms = new Entity( "EnrollmentWithTerms" )
                        enrollmentWithTerms.studentId = it.studentId
                        enrollmentWithTerms.schoolName = it.schoolName
                        enrollmentWithTerms.enrollTermYear = it.enrollTermYear
                        enrollmentWithTerms.enrollTermNo = it.enrollTermNo
                        enrollmentWithTerms.enrollDate = enrollTerm.startDate
                        enrollmentWithTerms.leaveTermYear = it.leaveTermYear
                        enrollmentWithTerms.leaveTermNo = it.leaveTermNo
                        enrollmentWithTerms.leaveDate = leaveTerm?.endDate
                        enrollmentWithTerms.firstClassAttended = it.firstClassAttended
                        enrollmentWithTerms.lastClassAttended = it.lastClassAttended
                        enrollmentWithTerms.leaveReasonCategory = it.leaveReasonCategory
                        enrollmentWithTerms.leaveReason = it.leaveReason
                        enrollmentWithTerms.lastUpdateDate = new Date()
                        enrollmentWithTerms.lastUpdateUser = user?.getEmail()
                        
                        enrollmentWithTerms.save()
                        
                        saveCount++
                    }
                }
            )
            
            println "Synced EnrollmentWithTerms entities with Enrollment and Term entities.<br>"
            println "Saved ${ saveCount } new EnrollmentWithTerms entities.<br>"
            println "Edited ${ editCount } EnrollmentWithTerms entities.<br>"
            println "Deleted ${ deleteCount } EnrollmentWithTerms entities."
        }
        catch( Exception e ) {
            println e.getMessage()
            println e.getStackTrace()
        }
    }
}
else {
    response.setStatus( response.SC_UNAUTHORIZED )
    response.setHeader( "Response-Phrase", "Unauthorized" )
}