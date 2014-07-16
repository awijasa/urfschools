/*
Document   : Groovlet file: EnrollmentWithStudentController.groovy
Created on : Sat Feb 23 15:20:00 CST 2013
Author     : awijasa
 */

import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.search.Index
import data.ClassAttended
import data.Enrollment
import data.EnrollmentDocument
import data.EnrollmentWithStudent
import data.Fee
import data.Payment
import data.Student
import data.Term
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
	else if( params.action == "index" ) {
		int deletedIndexCount = 0
		int indexCount = 0
		
		try {
			Index enrollmentIndex = search.index( "Enrollment" )
			
			Enrollment.list().each(
				{
					enrollmentIndex.delete( "" + it.getKey().getId() )
					deletedIndexCount++
				}
			)
			
			EnrollmentDocument.deleteMemcache()
			
			println "Deleted ${ deletedIndexCount } Enrollment Documents"
			
			EnrollmentWithStudent.list().each(
				{ enrollmentWithStudent ->
					double boardingFeesNumber = 0.0
					String classesAttendedText = ""
					double feesDueNumber
					double otherFeesNumber = 0.0
					double paymentsNumber = 0.0
					long enrollmentEntityId = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear(
							enrollmentWithStudent.studentId,
							enrollmentWithStudent.schoolName,
							enrollmentWithStudent.enrollTermNo,
							enrollmentWithStudent.enrollTermYear
						).getKey().getId()
					String termsEnrolledText = ""
					double tuitionFeesNumber = 0.0
					
					ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear(
						enrollmentWithStudent.studentId,
						enrollmentWithStudent.schoolName,
						enrollmentWithStudent.enrollTermNo,
						enrollmentWithStudent.enrollTermYear
					).each(
						{
							if( it.boardingInd == "Y" )
								boardingFeesNumber += it.getProperty( "boardingFee" )?: 0
								
							classesAttendedText += "${ it.getProperty( "class" ) }\t"
							termsEnrolledText += "${ it.getProperty( "classTermYear" ) } Term ${ it.getProperty( "classTermNo" ) }\t"
							tuitionFeesNumber += it.getProperty( "tuitionFee" )?: 0
						}
					)
					
					Fee.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear(
						enrollmentWithStudent.studentId,
						enrollmentWithStudent.schoolName,
						enrollmentWithStudent.enrollTermNo,
						enrollmentWithStudent.enrollTermYear
					).each(
						{
							otherFeesNumber += it.getProperty( "amount" )?: 0
						}
					)
					
					Payment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear(
						enrollmentWithStudent.studentId,
						enrollmentWithStudent.schoolName,
						enrollmentWithStudent.enrollTermNo,
						enrollmentWithStudent.enrollTermYear
					).each(
						{
							paymentsNumber += it.getProperty( "amount" )?: 0
						}
					)
					
					feesDueNumber = tuitionFeesNumber + boardingFeesNumber + otherFeesNumber - paymentsNumber
					
					if( feesDueNumber < 0 )
						feesDueNumber = 0
					
					enrollmentIndex.put {
						document( id: enrollmentEntityId ) {
							studentId atom: enrollmentWithStudent.studentId
							firstName text: enrollmentWithStudent.firstName
							lastName text: enrollmentWithStudent.lastName
							classesAttended text: classesAttendedText
							firstClassAttended text: enrollmentWithStudent.firstClassAttended
							lastClassAttended text: enrollmentWithStudent.lastClassAttended
							termsEnrolled text: termsEnrolledText
							enrollTermYear number: enrollmentWithStudent.enrollTermYear
							enrollTermNo number: enrollmentWithStudent.enrollTermNo
							enrollTermStartDate date: Term.findByTermSchoolAndTermNoAndYear(
									enrollmentWithStudent.schoolName,
									enrollmentWithStudent.enrollTermNo,
									enrollmentWithStudent.enrollTermYear
								).startDate
							leaveTermYear number: enrollmentWithStudent.leaveTermYear
							leaveTermNo number: enrollmentWithStudent.leaveTermNo
							leaveTermEndDate date: Term.findByTermSchoolAndTermNoAndYear(
									enrollmentWithStudent.schoolName,
									enrollmentWithStudent.leaveTermNo,
									enrollmentWithStudent.leaveTermYear
								)?.endDate?: Date.parse( "MMM d yyyy", "Dec 31 2999" )
							birthDate date: enrollmentWithStudent.birthDate
							village text: enrollmentWithStudent.village
							specialInfo text: enrollmentWithStudent.specialInfo
							genderCode text: enrollmentWithStudent.genderCode
							schoolName text: enrollmentWithStudent.schoolName
							leaveReasonCategory text: enrollmentWithStudent.leaveReasonCategory
							leaveReason text: enrollmentWithStudent.leaveReason
							tuitionFees number: tuitionFeesNumber
							boardingFees number: boardingFeesNumber
							otherFees number: otherFeesNumber
							payments number: paymentsNumber
							feesDue number: feesDueNumber
							lastUpdateDate date: new Date()
							lastUpdateUser text: user?.getEmail()
						}
					}
					
					indexCount++
				}
			)
			
			println "Indexed ${ indexCount } EnrollmentWithStudents"
		}
		catch( Exception e ) {
			println "Indexed ${ indexCount } EnrollmentWithStudents"
			println e.getMessage()
		}
	}
}
else {
    response.setStatus( response.SC_UNAUTHORIZED )
    response.setHeader( "Response-Phrase", "Unauthorized" )
}