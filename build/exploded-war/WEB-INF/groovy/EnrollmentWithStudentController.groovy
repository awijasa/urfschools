/*
Document   : Groovlet file: EnrollmentWithStudentController.groovy
Created on : Sat Feb 23 15:20:00 CST 2013
Author     : awijasa
 */

import com.google.appengine.api.datastore.Entity
import data.Enrollment
import data.EnrollmentWithStudent
import data.Student
import data.URFUser

/* Handle sync operation on the EnrollmentWithStudent entity. */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the EnrollmentWithStudentController. */
if( user == null && !localMode )
    response.sendError( response.SC_UNAUTHORIZED )
    
/* Allow the user to sync only if the request comes from a test script or an admin with Modify privilege. */
else if( user == null || URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
    
    /* Sync EnrollmentWithStudent entities with Student and Enrollment entities. */
    if( params.action.equals( "sync" ) ) {
        try {
            int deleteCount = 0
            int editCount = 0
            boolean isUpdateRequired = false
            int saveCount = 0
            
            EnrollmentWithStudent.list().each(
                {
                    Entity enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( it.studentId, it.schoolName, it.enrollTermNo, it.enrollTermYear )
                    Entity student = Student.findByStudentId( it.studentId )
                    
                    if( enrollment == null ) {
                        it.delete()
                        deleteCount++
                    }
                    else {
                        if( !( it.lastName?: "" ).equals( student.lastName?: "" ) ) {
                            it.lastName = student.lastName
                            isUpdateRequired = true
                        }
                            
                        if( !it.firstName.equals( student.firstName ) ) {
                            it.firstName = student.firstName
                            isUpdateRequired = true
                        }
                        
                        if( ( it.birthDate?: Date.parse( "MMM d yy", "Jan 1 1901" ) ) != ( student.birthDate?: Date.parse( "MMM d yy", "Jan 1 1901" ) ) ) {
                            it.birthDate = student.birthDate
                            isUpdateRequired = true
                        }
                        
                        if( !( it.village?: "" ).equals( student.village?: "" ) ) {
                            it.village = student.village
                            isUpdateRequired = true
                        }
                        
                        if( !( it.genderCode?: "" ).equals( student.genderCode?: "" ) ) {
                            it.genderCode = student.genderCode
                            isUpdateRequired = true
                        }
                        
                        if( !( it.specialInfo?: "" ).equals( student.specialInfo?: "" ) ) {
                            it.specialInfo = student.specialInfo
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
                    Entity student = Student.findByStudentId( it.studentId )
                    Entity enrollmentWithStudent = EnrollmentWithStudent.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( it.studentId, it.schoolName, it.enrollTermNo, it.enrollTermYear )
                    
                    if( enrollmentWithStudent == null ) {
                        enrollmentWithStudent = new Entity( "EnrollmentWithStudent" )
                        enrollmentWithStudent.studentId = it.studentId
                        enrollmentWithStudent.lastName = student.lastName
                        enrollmentWithStudent.firstName = student.firstName
                        enrollmentWithStudent.birthDate = student.birthDate
                        enrollmentWithStudent.village = student.village
                        enrollmentWithStudent.genderCode = student.genderCode
                        enrollmentWithStudent.specialInfo = student.specialInfo
                        enrollmentWithStudent.schoolName = it.schoolName
                        enrollmentWithStudent.enrollTermYear = it.enrollTermYear
                        enrollmentWithStudent.enrollTermNo = it.enrollTermNo
                        enrollmentWithStudent.leaveTermYear = it.leaveTermYear
                        enrollmentWithStudent.leaveTermNo = it.leaveTermNo
                        enrollmentWithStudent.firstClassAttended = it.firstClassAttended
                        enrollmentWithStudent.lastClassAttended = it.lastClassAttended
                        enrollmentWithStudent.leaveReasonCategory = it.leaveReasonCategory
                        enrollmentWithStudent.leaveReason = it.leaveReason
                        enrollmentWithStudent.lastUpdateDate = new Date()
                        enrollmentWithStudent.lastUpdateUser = user?.getEmail()
                        
                        enrollmentWithStudent.save()
                        
                        saveCount++
                    }
                }
            )
            
            println "Synced EnrollmentWithStudent entities with Student and Enrollment entities.<br>"
            println "Saved ${ saveCount } new EnrollmentWithStudent entities.<br>"
            println "Edited ${ editCount } EnrollmentWithStudent entities.<br>"
            println "Deleted ${ deleteCount } EnrollmentWithStudent entities."
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