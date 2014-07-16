/*
Document   : Groovlet file: FeeController.groovy
Created on : Thu Apr 17 22:24:00 CDT 2014
Author     : awijasa
 */

import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.search.Document
import com.google.appengine.api.search.Index
import data.AuthorizationException
import data.DuplicateEntityException
import data.EmptyRequiredFieldException
import data.EnrollmentDocument
import data.Fee
import data.StaleRecordException
import data.StudentDocument
import data.UserPrivilege
import formatter.ListItemFormatter
import java.text.DecimalFormat

/* Handle save, edit, and delete operations on the Fee entity. */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the FeeController. */
if( user == null && !localMode )
    response.sendError( response.SC_UNAUTHORIZED )
    
/*
 * Delete a Fee entity identified by the id parameter when the action parameter value is "delete".
 */
else if( params.action.equals( "delete" ) ) {
	Document enrollmentDocument
	Index enrollmentIndex
	Entity fee
	Document studentDocument
	Index studentIndex
	
	try {
		fee = datastore.get( "Fee", Long.parseLong( params.id ) )
		
		if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != fee.lastUpdateDate ||
			( params.lastUpdateUser?: "" ) != ( fee.lastUpdateUser?: "" ) )
			throw new StaleRecordException( params.action, "Fee" )
		
		if( /* Not a Test Script */ user != null && UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), fee.schoolName )?.privilege != "Modify" )
			throw new AuthorizationException( fee.schoolName )
		
		if( ( params.studentId?: "" ) == "" || ( params.schoolName?: "" ) == "" || ( params.enrollTermNo?: "" ) == "" || ( params.enrollTermYear?: "" ) == "" ) {
			Fee.findByLimitAndOffset( 0, Integer.parseInt( params.nextTwentyOffset ) ).each(
				{
					/*
					 * Respond with the HTML code that is required to display the Fee entity that can fill the empty space that resulted
					 * from the deleted record within an accordion.
					 */
					println ListItemFormatter.getFeeListItem( it )
				}
			)
		}
		else {
			Fee.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYearAndLimitAndOffset(
				params.studentId,
				params.schoolName,
				Integer.parseInt( params.enrollTermNo ),
				Integer.parseInt( params.enrollTermYear ),
				0,
				Integer.parseInt( params.nextTwentyOffset )
			).each(
				{
					/*
					 * Respond with the HTML code that is required to display the Fee entity that can fill the empty space that resulted
					 * from the deleted record within an accordion.
					 */
					println ListItemFormatter.getFeeListItem( it )
				}
			)
		}
		
		fee.delete()
		Fee.deleteMemcache()
		
		enrollmentIndex = search.index( "Enrollment" )
		enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( fee.studentId, fee.schoolName, fee.enrollTermNo, fee.enrollTermYear )
		
		Set<String> enrollmentDocumentFieldNames = enrollmentDocument.getFieldNames()
		double newOtherFees = enrollmentDocument.getOnlyField( "otherFees" ).getNumber() - fee.amount
		double newFeesDue = enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() + enrollmentDocument.getOnlyField( "boardingFees" ).getNumber() + newOtherFees - enrollmentDocument.getOnlyField( "payments" ).getNumber()
		
		if( newFeesDue < 0 )
			newFeesDue = 0
		
		enrollmentIndex.put {
			document( id: enrollmentDocument.getId() ) {
				studentId atom: enrollmentDocument.getOnlyField( "studentId" ).getAtom()
				firstName text: enrollmentDocument.getOnlyField( "firstName" ).getText()
				lastName text: enrollmentDocumentFieldNames.contains( "lastName" )? enrollmentDocument.getOnlyField( "lastName" ).getText(): null
				classesAttended text: enrollmentDocument.getOnlyField( "classesAttended" ).getText()
				firstClassAttended text: enrollmentDocument.getOnlyField( "firstClassAttended" ).getText()
				lastClassAttended text: enrollmentDocument.getOnlyField( "lastClassAttended" ).getText()
				termsEnrolled text: enrollmentDocument.getOnlyField( "termsEnrolled" ).getText()
				enrollTermYear number: enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber()
				enrollTermNo number: enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber()
				enrollTermStartDate date: enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate()
				leaveTermYear number: enrollmentDocumentFieldNames.contains( "leaveTermYear" )? enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber(): null
				leaveTermNo number: enrollmentDocumentFieldNames.contains( "leaveTermNo" )? enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber(): null
				leaveTermEndDate date: enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate()
				birthDate date: enrollmentDocumentFieldNames.contains( "birthDate" )? enrollmentDocument.getOnlyField( "birthDate" ).getDate(): null
				village text: enrollmentDocumentFieldNames.contains( "village" )? enrollmentDocument.getOnlyField( "village" ).getText(): null
				specialInfo text: enrollmentDocumentFieldNames.contains( "specialInfo" )? enrollmentDocument.getOnlyField( "specialInfo" ).getText(): null
				genderCode text: enrollmentDocumentFieldNames.contains( "genderCode" )? enrollmentDocument.getOnlyField( "genderCode" ).getText(): null
				schoolName text: enrollmentDocument.getOnlyField( "schoolName" ).getText()
				leaveReasonCategory text: enrollmentDocumentFieldNames.contains( "leaveReasonCategory" )? enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText(): null
				leaveReason text: enrollmentDocumentFieldNames.contains( "leaveReason" )? enrollmentDocument.getOnlyField( "leaveReason" ).getText(): null
				tuitionFees number: enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber()
				boardingFees number: enrollmentDocument.getOnlyField( "boardingFees" ).getNumber()
				otherFees number: newOtherFees
				payments number: enrollmentDocument.getOnlyField( "payments" ).getNumber()
				feesDue number: newFeesDue
				lastUpdateDate date: new Date()
				lastUpdateUser text: user?.getEmail()
			}
		}
		
		EnrollmentDocument.deleteMemcache()
		
		studentIndex = search.index( "Student" )
		studentDocument = StudentDocument.findByStudentId( fee.studentId )
		
		if(
			studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber() == fee.enrollTermYear &&
			studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber() == fee.enrollTermNo
		) {
			Set<String> studentDocumentFieldNames = studentDocument.getFieldNames()
			
			studentIndex.put {
				document( id: studentDocument.getId() ) {
					studentId atom: studentDocument.getOnlyField( "studentId" ).getAtom()
					firstName text: studentDocument.getOnlyField( "firstName" ).getText()
					lastName text: studentDocumentFieldNames.contains( "lastName" )? studentDocument.getOnlyField( "lastName" ).getText(): null
					classesAttended text: studentDocument.getOnlyField( "classesAttended" ).getText()
					lastEnrollmentFirstClassAttended text: studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText()
					lastEnrollmentLastClassAttended text: studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText()
					termsEnrolled text: studentDocument.getOnlyField( "termsEnrolled" ).getText()
					lastEnrollmentTermYear number: studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber()
					lastEnrollmentTermNo number: studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber()
					lastEnrollmentTermStartDate date: studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate()
					lastEnrollmentLeaveTermYear number: studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermYear" )? studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber(): null
					lastEnrollmentLeaveTermNo number: studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermNo" )? studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber(): null
					birthDate date: studentDocumentFieldNames.contains( "birthDate" )? studentDocument.getOnlyField( "birthDate" ).getDate(): null
					village text: studentDocumentFieldNames.contains( "village" )? studentDocument.getOnlyField( "village" ).getText(): null
					specialInfo text: studentDocumentFieldNames.contains( "specialInfo" )? studentDocument.getOnlyField( "specialInfo" ).getText(): null
					genderCode text: studentDocumentFieldNames.contains( "genderCode" )? studentDocument.getOnlyField( "genderCode" ).getText(): null
					lastEnrollmentSchool text: studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText()
					lastEnrollmentLeaveReasonCategory text: studentDocumentFieldNames.contains( "lastEnrollmentLeaveReasonCategory" )? studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText(): null
					lastEnrollmentLeaveReason text: studentDocumentFieldNames.contains( "lastEnrollmentLeaveReason" )? studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText(): null
					tuitionFees number: studentDocument.getOnlyField( "tuitionFees" ).getNumber()
					boardingFees number: studentDocument.getOnlyField( "boardingFees" ).getNumber()
					otherFees number: newOtherFees
					payments number: studentDocument.getOnlyField( "payments" ).getNumber()
					feesDue number: newFeesDue
					lastUpdateDate date: new Date()
					lastUpdateUser text: user?.getEmail()
				}
			}
			
			StudentDocument.deleteMemcache()
		}
	}
	catch( Exception e ) {
		
		/* Rollback the deleted Fee */
		try { fee?.save() } catch( Exception saveException ) {}
		
		/* Rollback the updated EnrollmentDocument */
		try { enrollmentIndex?.put( enrollmentDocument ) } catch( Exception putException ) {}
		
		/* Rollback the updated StudentDocument */
		try { studentIndex?.put( studentDocument ) } catch( Exception putException ) {}
		
		/* Respond with an Internal Server error message if the delete fails. */
		response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
		response.setHeader( "Response-Phrase", e.getMessage() )
	}
}

/*
 * Edit a Fee Entity when the action parameter value is "edit".
 * 
 * Save a new Fee entity when the action parameter value is "save".
 */
else if( params.action.equals( "edit" ) || params.action.equals( "save" ) ) {
	Number amount
	Integer classTermNo
	Integer classTermYear
	String comment
	Integer enrollTermNoInt
	Integer enrollTermYearInt
	String name
	String schoolNameString
	String studentIdString
	
	Document enrollmentDocument
	Index enrollmentIndex
	Entity fee
	Entity feeBackup
	Document studentDocument
	Index studentIndex
	
	try {
		if( params.action.equals( "save" ) ) {
			fee = new Entity( "Fee" )
			
			/* If the name parameter value is empty, do not put it into the Fee entity's name field */
			if( params.name != null && params.name != "Name" && params.fundingSource != "" )
				name = params.name
			
			/* If the name parameter value is empty, and name is a required Fee entity's field, throw an EmptyRequiredFieldException. */
			else if( Fee.isRequired( "name" ) )
				throw new EmptyRequiredFieldException( "Fee", "name" )
			
			/* If the studentId parameter value is empty, do not put it into the Fee entity's studentId field */
			if( params.studentId != null && !params.studentId.equals( "" ) )
				studentIdString = params.studentId
				
			/* If the studentId parameter value is empty, and studentId is a required Fee entity's field, throw an EmptyRequiredFieldException. */
			else if( Fee.isRequired( "studentId" ) )
				throw new EmptyRequiredFieldException( "Fee", "studentId" )
		
			/* If the schoolName parameter value is empty, do not put it into the Fee entity's schoolName field. */
			if( params.schoolName != null && !params.schoolName.equals( "" ) )
				schoolNameString = params.schoolName

			/* If the schoolName parameter value is empty, and schoolName is a required Fee entity's field, throw an EmptyRequiredFieldException. */
			else if( Fee.isRequired( "schoolName" ) )
				throw new EmptyRequiredFieldException( "Fee", "schoolName" )
				
			/* If the enrollTermNo parameter value is empty, do not put it into the Fee entity's enrollTermNo field. */
			if( params.enrollTermNo != null && params.enrollTermNo != "" )
				enrollTermNoInt = Integer.parseInt( params.enrollTermNo )
			
			/* If the enrollTermNo parameter value is empty, and enrollTermNo is a required Fee entity's field, throw an EmptyRequiredFieldException. */
			else if( Fee.isRequired( "enrollTermNo" ) )
				throw new EmptyRequiredFieldException( "Fee", "enrollTermNo" )
	
			/* If the enrollTermYear parameter value is empty, do not put it into the Fee entity's enrollTermYear field. */
			if( params.enrollTermYear != null && params.enrollTermYear != "" )
				enrollTermYearInt = Integer.parseInt( params.enrollTermYear )
				
			/* If the enrollTermYear parameter value is empty, and enrollTermYear is a required Fee entity's field, throw an EmptyRequiredFieldException. */
			else if( Fee.isRequired( "enrollTermYear" ) )
				throw new EmptyRequiredFieldException( "Fee", "enrollTermYear" )
				
			/* If the classTermNo parameter value is empty, do not put it into the Fee entity's classTermNo field. */
			if( params.classTermNo != null && params.classTermNo != "" )
				classTermNo = Integer.parseInt( params.classTermNo )

			/* If the classTermNo parameter value is empty, and classTermNo is a required Fee entity's field, throw an EmptyRequiredFieldException. */
			else if( Fee.isRequired( "classTermNo" ) )
				throw new EmptyRequiredFieldException( "Fee", "classTermNo" )
				
			/* If the classTermYear parameter value is empty, do not put it into the Fee entity's classTermYear field. */
			if( params.classTermYear != null && params.classTermYear != "" )
				classTermYear = Integer.parseInt( params.classTermYear )

			/* If the classTermYear parameter value is empty, and classTermYear is a required Fee entity's field, throw an EmptyRequiredFieldException. */
			else if( Fee.isRequired( "classTermYear" ) )
				throw new EmptyRequiredFieldException( "Fee", "classTermYear" )
				
			fee.name = name
			fee.studentId = studentIdString
			fee.schoolName = schoolNameString
			fee.enrollTermNo = enrollTermNoInt
			fee.enrollTermYear = enrollTermYearInt
			fee.classTermNo = classTermNo
			fee.classTermYear = classTermYear
				
			if( Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( name, studentIdString, schoolNameString, classTermNo, classTermYear ) != null )
				throw new DuplicateEntityException( fee, Fee.getPrimaryKey() )
		}
		else {
			fee = datastore.get( "Fee", Long.parseLong( params.id ) )
			feeBackup = datastore.get( "Fee", Long.parseLong( params.id ) )
			
			name = fee.name
			studentIdString = fee.studentId
			schoolNameString = fee.schoolName
			enrollTermNoInt = fee.enrollTermNo
			enrollTermYearInt = fee.enrollTermYear
			classTermNo = fee.classTermNo
			classTermYear = fee.classTermYear
			
			if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != fee.lastUpdateDate ||
				( params.lastUpdateUser?: "" ) != ( fee.lastUpdateUser?: "" ) )
				throw new StaleRecordException( params.action, "Fee" )
		}

		DecimalFormat currencyFormat = new DecimalFormat( "#,##0.##" )
			
		/* If the amount parameter value is empty, do not put it into the Fee entity's amount field. */
		if( params.amount != null && params.amount != "Amount" && params.amount != "" )
			amount = currencyFormat.parse( params.amount )
		
		/* If the amount parameter value is empty, and amount is a required Fee entity's field, throw an EmptyRequiredFieldException. */
		else if( Fee.isRequired( "amount" ) )
			throw new EmptyRequiredFieldException( "Fee", "amount" )

		/* If the comment parameter value is empty, do not put it into the Fee entity's comment field. */
		if( params.comment != null && params.comment != "Comment" && params.comment != "" )
			comment = params.comment
			
		/* If the comment parameter value is empty, and comment is a required Fee entity's field, throw an EmptyRequiredFieldException. */
		else if( Fee.isRequired( "comment" ) )
			throw new EmptyRequiredFieldException( "Fee", "comment" )

		String schoolPrivilege = UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), schoolNameString )?.privilege
		
		/*
		 * Allow the user to save/edit only if the request comes from a test script or a user with Modify privilege for the Fee School.
		 */
		if( /* Test Script */ user == null || schoolPrivilege?.equals( "Modify" ) ) {
			/* Populate the Fee fields. */
			fee.amount = amount
			fee.comment = comment
			fee.lastUpdateDate = new Date()
			fee.lastUpdateUser = user?.getEmail()
			
			fee.save()
		}
		else
			throw new AuthorizationException( schoolNameString )
				
		Fee.deleteMemcache()
		
		enrollmentIndex = search.index( "Enrollment" )
		enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( fee.studentId, fee.schoolName, fee.enrollTermNo, fee.enrollTermYear )
		
		Set<String> enrollmentDocumentFieldNames = enrollmentDocument.getFieldNames()
		double newOtherFees
		
		if( params.action == "save" )
			newOtherFees = enrollmentDocument.getOnlyField( "otherFees" ).getNumber() + fee.amount
		else
			newOtherFees = enrollmentDocument.getOnlyField( "otherFees" ).getNumber() + ( fee.amount - feeBackup.amount )
		
		double newFeesDue = enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() + enrollmentDocument.getOnlyField( "boardingFees" ).getNumber() + newOtherFees - enrollmentDocument.getOnlyField( "payments" ).getNumber()
		
		if( newFeesDue < 0 )
			newFeesDue = 0
			
		enrollmentIndex.put {
			document( id: enrollmentDocument.getId() ) {
				studentId atom: enrollmentDocument.getOnlyField( "studentId" ).getAtom()
				firstName text: enrollmentDocument.getOnlyField( "firstName" ).getText()
				lastName text: enrollmentDocumentFieldNames.contains( "lastName" )? enrollmentDocument.getOnlyField( "lastName" ).getText(): null
				classesAttended text: enrollmentDocument.getOnlyField( "classesAttended" ).getText()
				firstClassAttended text: enrollmentDocument.getOnlyField( "firstClassAttended" ).getText()
				lastClassAttended text: enrollmentDocument.getOnlyField( "lastClassAttended" ).getText()
				termsEnrolled text: enrollmentDocument.getOnlyField( "termsEnrolled" ).getText()
				enrollTermYear number: enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber()
				enrollTermNo number: enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber()
				enrollTermStartDate date: enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate()
				leaveTermYear number: enrollmentDocumentFieldNames.contains( "leaveTermYear" )? enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber(): null
				leaveTermNo number: enrollmentDocumentFieldNames.contains( "leaveTermNo" )? enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber(): null
				leaveTermEndDate date: enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate()
				birthDate date: enrollmentDocumentFieldNames.contains( "birthDate" )? enrollmentDocument.getOnlyField( "birthDate" ).getDate(): null
				village text: enrollmentDocumentFieldNames.contains( "village" )? enrollmentDocument.getOnlyField( "village" ).getText(): null
				specialInfo text: enrollmentDocumentFieldNames.contains( "specialInfo" )? enrollmentDocument.getOnlyField( "specialInfo" ).getText(): null
				genderCode text: enrollmentDocumentFieldNames.contains( "genderCode" )? enrollmentDocument.getOnlyField( "genderCode" ).getText(): null
				schoolName text: enrollmentDocument.getOnlyField( "schoolName" ).getText()
				leaveReasonCategory text: enrollmentDocumentFieldNames.contains( "leaveReasonCategory" )? enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText(): null
				leaveReason text: enrollmentDocumentFieldNames.contains( "leaveReason" )? enrollmentDocument.getOnlyField( "leaveReason" ).getText(): null
				tuitionFees number: enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber()
				boardingFees number: enrollmentDocument.getOnlyField( "boardingFees" ).getNumber()
				otherFees number: newOtherFees
				payments number: enrollmentDocument.getOnlyField( "payments" ).getNumber()
				feesDue number: newFeesDue
				lastUpdateDate date: new Date()
				lastUpdateUser text: user?.getEmail()
			}
		}
		
		EnrollmentDocument.deleteMemcache()
		
		studentIndex = search.index( "Student" )
		studentDocument = StudentDocument.findByStudentId( fee.studentId )
		
		if(
			studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber() == fee.enrollTermYear &&
			studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber() == fee.enrollTermNo
		) {
			Set<String> studentDocumentFieldNames = studentDocument.getFieldNames()
				
			studentIndex.put {
				document( id: studentDocument.getId() ) {
					studentId atom: studentDocument.getOnlyField( "studentId" ).getAtom()
					firstName text: studentDocument.getOnlyField( "firstName" ).getText()
					lastName text: studentDocumentFieldNames.contains( "lastName" )? studentDocument.getOnlyField( "lastName" ).getText(): null
					classesAttended text: studentDocument.getOnlyField( "classesAttended" ).getText()
					lastEnrollmentFirstClassAttended text: studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText()
					lastEnrollmentLastClassAttended text: studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText()
					termsEnrolled text: studentDocument.getOnlyField( "termsEnrolled" ).getText()
					lastEnrollmentTermYear number: studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber()
					lastEnrollmentTermNo number: studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber()
					lastEnrollmentTermStartDate date: studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate()
					lastEnrollmentLeaveTermYear number: studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermYear" )? studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber(): null
					lastEnrollmentLeaveTermNo number: studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermNo" )? studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber(): null
					birthDate date: studentDocumentFieldNames.contains( "birthDate" )? studentDocument.getOnlyField( "birthDate" ).getDate(): null
					village text: studentDocumentFieldNames.contains( "village" )? studentDocument.getOnlyField( "village" ).getText(): null
					specialInfo text: studentDocumentFieldNames.contains( "specialInfo" )? studentDocument.getOnlyField( "specialInfo" ).getText(): null
					genderCode text: studentDocumentFieldNames.contains( "genderCode" )? studentDocument.getOnlyField( "genderCode" ).getText(): null
					lastEnrollmentSchool text: studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText()
					lastEnrollmentLeaveReasonCategory text: studentDocumentFieldNames.contains( "lastEnrollmentLeaveReasonCategory" )? studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText(): null
					lastEnrollmentLeaveReason text: studentDocumentFieldNames.contains( "lastEnrollmentLeaveReason" )? studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText(): null
					tuitionFees number: studentDocument.getOnlyField( "tuitionFees" ).getNumber()
					boardingFees number: studentDocument.getOnlyField( "boardingFees" ).getNumber()
					otherFees number: newOtherFees
					payments number: studentDocument.getOnlyField( "payments" ).getNumber()
					feesDue number: newFeesDue
					lastUpdateDate date: new Date()
					lastUpdateUser text: user?.getEmail()
				}
			}
			
			StudentDocument.deleteMemcache()
		}
		
		/* Respond with the HTML code that is required to display the new Fee entity within an accordion. */
		println ListItemFormatter.getFeeListItem( fee )
	}
	catch( Exception e ) {
		log.info e.getMessage()
		if( params.action == "save" )
			try { fee?.delete() } catch( Exception deleteException ) {}
		else
			try{ feeBackup?.save() } catch( Exception backupRestoreException ) {}
		
		try { enrollmentIndex?.put( enrollmentDocument ) } catch( Exception backupRestoreException ) {}
		
		try { studentIndex?.put( studentDocument ) } catch( Exception backupRestoreException ) {}
		
		/* Respond with an Internal Server error message if the save fails. */
		response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
		response.setHeader( "Response-Phrase", e.getMessage() )
	}
}