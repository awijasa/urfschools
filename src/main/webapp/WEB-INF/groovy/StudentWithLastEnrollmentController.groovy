/*
Document   : Groovlet file: StudentWithLastEnrollmentController.groovy
Created on : Fri Aug 3 22:34:00 CST 2012
Author     : awijasa
 */

import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.search.Index
import data.ClassAttended
import data.Fee
import data.Payment
import data.Student
import data.StudentDocument
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
	else if( params.action == "index" ) {
		int deletedIndexCount = 0
		int indexCount = 0
		
		try {
			Index studentIndex = search.index( "Student" )
			
			Student.list().each(
				{
					studentIndex.delete( "" + it.getKey().getId() )
					deletedIndexCount++
				}
			)
			
			StudentDocument.deleteMemcache()
			
			println "Deleted ${ deletedIndexCount } Student Documents"
			
			StudentWithLastEnrollment.list().each(
				{ studentWithLastEnrollment ->
					double boardingFeesNumber = 0.0
					String classesAttendedText = ""
					double feesDueNumber
					double otherFeesNumber = 0.0
					double paymentsNumber = 0.0
					long studentEntityId = Student.findByStudentId( studentWithLastEnrollment.studentId ).getKey().getId()
					String termsEnrolledText = ""
					double tuitionFeesNumber = 0.0
					
					ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear(
						studentWithLastEnrollment.studentId,
						studentWithLastEnrollment.lastEnrollmentSchool,
						studentWithLastEnrollment.lastEnrollmentTermNo,
						studentWithLastEnrollment.lastEnrollmentTermYear
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
						studentWithLastEnrollment.studentId,
						studentWithLastEnrollment.lastEnrollmentSchool,
						studentWithLastEnrollment.lastEnrollmentTermNo,
						studentWithLastEnrollment.lastEnrollmentTermYear
					).each(
						{
							otherFeesNumber += it.getProperty( "amount" )?: 0
						}
					)
					
					Payment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear(
						studentWithLastEnrollment.studentId,
						studentWithLastEnrollment.lastEnrollmentSchool,
						studentWithLastEnrollment.lastEnrollmentTermNo,
						studentWithLastEnrollment.lastEnrollmentTermYear
					).each(
						{
							paymentsNumber += it.getProperty( "amount" )?: 0
						}
					)
					
					feesDueNumber = tuitionFeesNumber + boardingFeesNumber + otherFeesNumber - paymentsNumber
					
					if( feesDueNumber < 0 )
						feesDueNumber = 0
					
					studentIndex.put {
						document( id: studentEntityId ) {
							studentId atom: studentWithLastEnrollment.studentId
							firstName text: studentWithLastEnrollment.firstName
							lastName text: studentWithLastEnrollment.lastName
							classesAttended text: classesAttendedText
							lastEnrollmentFirstClassAttended text: studentWithLastEnrollment.lastEnrollmentFirstClassAttended
							lastEnrollmentLastClassAttended text: studentWithLastEnrollment.lastEnrollmentLastClassAttended
							termsEnrolled text: termsEnrolledText
							lastEnrollmentTermYear number: studentWithLastEnrollment.lastEnrollmentTermYear
							lastEnrollmentTermNo number: studentWithLastEnrollment.lastEnrollmentTermNo
							lastEnrollmentTermStartDate date: Term.findByTermSchoolAndTermNoAndYear(
									studentWithLastEnrollment.lastEnrollmentSchool,
									studentWithLastEnrollment.lastEnrollmentTermNo,
									studentWithLastEnrollment.lastEnrollmentTermYear
								).startDate
							lastEnrollmentLeaveTermYear number: studentWithLastEnrollment.lastEnrollmentLeaveTermYear
							lastEnrollmentLeaveTermNo number: studentWithLastEnrollment.lastEnrollmentLeaveTermNo
							birthDate date: studentWithLastEnrollment.birthDate
							village text: studentWithLastEnrollment.village
							specialInfo text: studentWithLastEnrollment.specialInfo
							genderCode text: studentWithLastEnrollment.genderCode
							lastEnrollmentSchool text: studentWithLastEnrollment.lastEnrollmentSchool
							lastEnrollmentLeaveReasonCategory text: studentWithLastEnrollment.lastEnrollmentLeaveReasonCategory
							lastEnrollmentLeaveReason text: studentWithLastEnrollment.lastEnrollmentLeaveReason
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
			
			println "Indexed ${ indexCount } StudentWithLastEnrollments"
		}
		catch( Exception e ) {
			println "Indexed ${ indexCount } StudentWithLastEnrollments"
			println e.getMessage()
		}
	}
}
else {
    response.setStatus( response.SC_UNAUTHORIZED )
    response.setHeader( "Response-Phrase", "Unauthorized" )
}