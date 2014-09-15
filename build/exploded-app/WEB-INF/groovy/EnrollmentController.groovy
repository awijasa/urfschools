/*
Document   : Groovlet file: EnrollmentController.groovy
Created on : Fri Aug 3 22:18:00 CST 2012
Author     : awijasa
 */

import java.util.Date;

import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.search.Document
import com.google.appengine.api.search.Field
import com.google.appengine.api.search.Index
import data.App
import data.AuthorizationException
import data.Class
import data.ClassAttended
import data.ClassFees
import data.EmptyRequiredFieldException
import data.Enrollment
import data.EnrollmentDocument
import data.EntityIsUsedException
import data.Fee
import data.InvalidClassLevelRangeException
import data.InvalidDateRangeException
import data.Payment
import data.School
import data.StaleRecordException
import data.Student
import data.StudentDocument
import data.Term
import data.TheOnlyEnrollmentLeftException
import data.OverlappedEnrollmentException
import data.URFUser
import data.UserPrivilege
import formatter.ListItemFormatter

/* Handle convert and drop operations on the Enrollment entity. */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the EnrollmentController. */
if( user == null && !localMode )
    response.sendError( response.SC_UNAUTHORIZED )
else if( params.action == "count" ) {
	if( URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
		try {
			Entity appEntity = App.get()
			
			if( appEntity == null ) {
				appEntity = new Entity( "App" )
				appEntity.name = "urfschools"
				appEntity.save()
			}
			
			School.list().each(
				{
					try {
						Entity enrollmentMetaData = new Entity( "EnrollmentMetaData", appEntity.getKey() )
						enrollmentMetaData.schoolName = it.name
						enrollmentMetaData.count = EnrollmentDocument.findBySchoolName( it.name ).getNumberReturned()
						enrollmentMetaData.save()
						println "${ it.name } Enrollments' record count (${ enrollmentMetaData.count }) has been recorded into EnrollmentMetaData"
					}
					catch( Exception e ) {
						println e.getMessage()
					}
				}
			)
		}
		catch( Exception e ) {
			println e.getMessage()
		}
	}
}

/*
 * Delete an Enrollment entity identified by the id parameter and its associated EnrollmentDocument entity when the action parameter value is "delete".
 */
else if( params.action.equals( "delete" ) ) {
	List<Entity> classesAttendedList
	Entity enrollment
	Document enrollmentDocument
	Index enrollmentIndex = search.index( "Enrollment" )
	Entity enrollmentMetaDataBackup
	List<Entity> fees
	List<Entity> paymentsList
	Document studentDocument
	Index studentIndex = search.index( "Student" )
	
	try {
		if( Enrollment.findByStudentId( params.studentId ).size() == 1 )
			throw new TheOnlyEnrollmentLeftException( params.studentId )
		
		enrollmentDocument = enrollmentIndex.get( params.id )
		
		if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate() ||
			( params.lastUpdateUser?: "" ) != ( enrollmentDocument.getFieldNames().contains( "lastUpdateUser" )? enrollmentDocument.getOnlyField( "lastUpdateUser" ).getText(): "" ) )
			throw new StaleRecordException( params.action, "Enrollment" )
		
		enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear(
			enrollmentDocument.getOnlyField( "studentId" ).getAtom(),
			enrollmentDocument.getOnlyField( "schoolName" ).getText(),
			enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber(),
			enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber()
		)
		
		if( /* Not a Test Script */ user != null && UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), enrollment.schoolName )?.privilege != "Modify" )
			throw new AuthorizationException( enrollment.schoolName )
		
		/* The local AppEngine server can't process this block properly */
		if( !localMode ) {
			EnrollmentDocument.findByLimitAndOffset( 0, Integer.parseInt( params.nextTwentyOffset ), session ).each(
				{
					/*
					 * Respond with the HTML code that is required to display the EnrollmentDocument that can fill the empty space that resulted
					 * from the deleted record within an accordion.
					 */
					println ListItemFormatter.getEnrollmentListItem( it )
				}
			)
		}
		
		fees = Fee.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( enrollment.studentId, enrollment.schoolName, enrollment.enrollTermNo, enrollment.enrollTermYear )
		fees*.delete()
		
		paymentsList = Payment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( enrollment.studentId, enrollment.schoolName, enrollment.enrollTermNo, enrollment.enrollTermYear )
		paymentsList*.delete()
		
		classesAttendedList = ClassAttended.findByEnrollment( enrollment )
		classesAttendedList*.delete()
		
		if( Enrollment.isEqual( enrollment, Enrollment.findLastEnrollmentByStudentId( params.studentId ) ) ) {
			enrollment.delete()
			enrollmentIndex.delete( enrollmentDocument.getId() )
			StudentDocument.deleteMemcache()
			
			double boardingFeesNumber = 0.0
			String classesAttendedText = ""
			double feesDueNumber = 0.0
			Entity lastEnrollment = Enrollment.findLastEnrollmentByStudentId( params.studentId )
			double otherFeesNumber = 0.0
			double paymentsNumber = 0.0
			String termsEnrolledText = ""
			double tuitionFeesNumber = 0.0
			
			ClassAttended.findByEnrollment( lastEnrollment ).each(
				{
					if( it.boardingInd == "Y" )
						boardingFeesNumber += it.getProperty( "boardingFee" )?: 0
						
					classesAttendedText += "${ it.getProperty( "class" ) }\t"
					termsEnrolledText += "${ it.getProperty( "classTermYear" ) } Term ${ it.getProperty( "classTermNo" ) }\t"
					tuitionFeesNumber += it.getProperty( "tuitionFee" )?: 0
				}
			)
			
			Fee.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear(
				lastEnrollment.studentId,
				lastEnrollment.schoolName,
				lastEnrollment.enrollTermNo,
				lastEnrollment.enrollTermYear
			).each(
				{
					otherFeesNumber += it.getProperty( "amount" )?: 0
				}
			)
			
			Payment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear(
				lastEnrollment.studentId,
				lastEnrollment.schoolName,
				lastEnrollment.enrollTermNo,
				lastEnrollment.enrollTermYear
			).each(
				{
					paymentsNumber += it.getProperty( "amount" )?: 0
				}
			)
			
			feesDueNumber = tuitionFeesNumber + boardingFeesNumber + otherFeesNumber - paymentsNumber
			
			if( feesDueNumber < 0 )
				feesDueNumber = 0
			
			studentIndex = search.index( "Student" )
			studentDocument = StudentDocument.findByStudentId( params.studentId )
			Set<String> studentDocumentFieldNames = studentDocument.getFieldNames()
			
			studentIndex.put {
				document( id: studentDocument.getId() ) {
					studentId atom: studentDocument.getOnlyField( "studentId" ).getAtom()
					firstName text: studentDocument.getOnlyField( "firstName" ).getText()
					lastName text: studentDocumentFieldNames.contains( "lastName" )? studentDocument.getOnlyField( "lastName" ).getText(): null
					classesAttended text: classesAttendedText
					lastEnrollmentFirstClassAttended text: lastEnrollment.firstClassAttended
					lastEnrollmentLastClassAttended text: lastEnrollment.lastClassAttended
					termsEnrolled text: termsEnrolledText
					lastEnrollmentTermYear number: lastEnrollment.enrollTermYear
					lastEnrollmentTermNo number: lastEnrollment.enrollTermNo
					lastEnrollmentTermStartDate date: Term.findByTermSchoolAndTermNoAndYear(
							lastEnrollment.schoolName,
							lastEnrollment.enrollTermNo,
							lastEnrollment.enrollTermYear
						).startDate
					lastEnrollmentLeaveTermYear number: lastEnrollment.leaveTermYear
					lastEnrollmentLeaveTermNo number: lastEnrollment.leaveTermNo
					birthDate date: studentDocumentFieldNames.contains( "birthDate" )? studentDocument.getOnlyField( "birthDate" ).getDate(): null
					village text: studentDocumentFieldNames.contains( "village" )? studentDocument.getOnlyField( "village" ).getText(): null
					specialInfo text: studentDocumentFieldNames.contains( "specialInfo" )? studentDocument.getOnlyField( "specialInfo" ).getText(): null
					genderCode text: studentDocumentFieldNames.contains( "genderCode" )? studentDocument.getOnlyField( "genderCode" ).getText(): null
					lastEnrollmentSchool text: lastEnrollment.schoolName
					lastEnrollmentLeaveReasonCategory text: lastEnrollment.leaveReasonCategory
					lastEnrollmentLeaveReason text: lastEnrollment.leaveReason
					tuitionFees number: tuitionFeesNumber
					boardingFees number: boardingFeesNumber
					otherFees number: otherFeesNumber
					payments number: paymentsNumber
					feesDue number: feesDueNumber
					lastUpdateDate date: new Date()
					lastUpdateUser text: user?.getEmail()
				}
			}
		}
		else {
			enrollment.delete()
			enrollmentIndex.delete( enrollmentDocument.getId() )
		}
		
		Entity enrollmentMetaData = Enrollment.findMetaDataBySchoolName( enrollmentDocument.getOnlyField( "schoolName" ).getText() )
		enrollmentMetaDataBackup = Enrollment.findMetaDataBySchoolName( enrollmentDocument.getOnlyField( "schoolName" ).getText() )
		
		if( enrollmentMetaData.count > 0 ) {
			enrollmentMetaData.count = enrollmentMetaData.count - 1
			enrollmentMetaData.save()
		}
		
		EnrollmentDocument.deleteMemcache()
	}
	catch( Exception e ) {
		e.printStackTrace()
		/* Rollback the deleted Classes Attended, Enrollment and EnrollmentDocument. Rollback the edited StudentDocument */
		try { enrollment?.save() } catch( Exception saveException ) {}
		try { enrollmentIndex.put( enrollmentDocument ) } catch( Exception saveException ) {}
		try { enrollmentMetaDataBackup.save() } catch( Exception saveException ) {}
		try { studentIndex.put( studentDocument ) } catch( Exception saveException ) {}
		
		classesAttendedList.each(
			{
				try { it.save() } catch( Exception saveException ) {}
			}
		)
		
		fees.each(
			{
				try { it.save() } catch( Exception saveException ) {}
			}
		)
		
		paymentsList.each(
			{
				try { it.save() } catch( Exception saveException ) {}
			}
		)
		
		/* Respond with an Internal Server error message if the delete fails. */
		response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
		response.setHeader( "Response-Phrase", e.getMessage() )
	}
}

/*
 * Edit an Enrollment Entity, its EnrollmentDocument, and if necessary, its StudentDocument, when the action parameter value is
 * "edit".
 * 
 * Save a new Enrollment entity, its new EnrollmentDocument, and if necessary, update its StudentDocumment entity, when the action
 * parameter value is "save".
 */
else if( params.action.equals( "edit" ) || params.action.equals( "save" ) ) {
	String schoolNameString
	String leaveReasonCategoryString
	String leaveReasonString
	String studentIdString
	String classesAttendedText = ""
	String termsEnrolledText = ""
	double boardingFeesNumber = 0.0
	double tuitionFeesNumber = 0.0
	double otherFeesNumber = 0.0
	double paymentsNumber = 0.0
	
	List<Entity> classesAttendedList = new ArrayList()
	List<Entity> classesAttendedBackup = new ArrayList()
	Entity enrollment
	Entity enrollmentBackup
	Document.Builder enrollmentDocBuilder = Document.newBuilder()
	Document enrollmentDocument
	Document enrollmentDocumentBackup
	Index enrollmentIndex = search.index( "Enrollment" )
	Entity enrollmentMetaData
	Entity enrollmentMetaDataBackup
	Document studentDocument
	Index studentIndex = search.index( "Student" )
	
	try {
		if( params.action.equals( "save" ) ) {
			
			/* If the studentId parameter value is empty, raise an error. */
			if( params.studentId != null && !params.studentId.equals( "" ) )
				studentIdString = params.studentId
			else
				throw new EmptyRequiredFieldException( "Enrollment", "studentId" )
		
			/* If the schoolName parameter value is "School" or empty, do not put it into the Enrollment/EnrollmentDocument/StudentWithDocument entity's schoolName/lastEnrollmentSchool field. */
			if( params.schoolName != null && !params.schoolName.equals( "School" ) && !params.schoolName.equals( "" ) )
				schoolNameString = params.schoolName

			/* If the schoolName parameter value is "School" or empty, and schoolName is a required Enrollment entity's field, throw an EmptyRequiredFieldException. */
			else if( Enrollment.isRequired( "schoolName" ) )
				throw new EmptyRequiredFieldException( "Enrollment", "schoolName" )
		}

		/* If the leaveReasonCategory parameter value is "Leave Reason Category" or empty, do not put it into the Enrollment/EnrollmentDocument/StudentDocument entity's leaveReasonCategory/lastEnrollmentLeaveReasonCategory field. */
		if( params.leaveReasonCategory != null && !params.leaveReasonCategory.equals( "Leave Reason Category" ) && !params.leaveReasonCategory.equals( "" ) )
			leaveReasonCategoryString = params.leaveReasonCategory
		
		/* If the leaveReasonCategory parameter value is "Leave Reason Category" or empty, and leaveReasonCategory is a required Enrollment entity's field, throw an EmptyRequiredFieldException. */
		else if( Enrollment.isRequired( "leaveReasonCategory" ) )
			throw new EmptyRequiredFieldException( "Enrollment", "leaveReasonCategory" )

		/* If the leaveReason parameter value is "Leave Reason" or empty, do not put it into the Enrollment/EnrollmentDocument/StudentDocument entity's leaveReason/lastEnrollmentLeaveReason field. */
		if( params.leaveReason != null && !params.leaveReason.equals( "Leave Reason" ) && !params.leaveReason.equals( "" ) )
			leaveReasonString = params.leaveReason
			
		/* If the leaveReason parameter value is "Leave Reason" or empty, and leaveReason is a required Enrollment entity's field, throw an EmptyRequiredFieldException. */
		else if( Enrollment.isRequired( "leaveReason" ) )
			throw new EmptyRequiredFieldException( "Enrollment", "leaveReason" )

		boolean isEnrollTermEmpty = true
		boolean isLeaveTermEmpty = true
		boolean isFirstClassAttendedEmpty = true
		boolean isLastClassAttendedEmpty = true
		String firstClassAttendedString
		String lastClassAttendedString
		Entity leaveTerm
		Integer leaveTermNoInt
		Integer leaveTermYearInt
		Date enrollDate
		Integer enrollTermNoInt
		Integer enrollTermYearInt
		
		if( params.action.equals( "save" ) ) {
		
			/* Iterate through each School. */
			School.list().eachWithIndex() { school, i ->
				String enrollTerm = request.getParameter( "enrollTermSchool${ i + 1 }" )
				String leaveTermParamValue = request.getParameter( "leaveTermSchool${ i + 1 }" )
				String firstClassAttendedParamValue = request.getParameter( "firstClassAttendedSchool${ i + 1 }" )
				String lastClassAttendedParamValue = request.getParameter( "lastClassAttendedSchool${ i + 1 }" )

				/*
				 * If the value of Enrollment Term parameter associated to the iterated School is "Enrollment Term", do not put it into the
				 * Enrollment/EnrollmentDocument/StudentDocument entity's enrollTermYear & enrollTermNo/lastEnrollmentTermYear & lastEnrollmentTermNo fields.
				 */
				if( enrollTerm != null && !enrollTerm.equals( "Enrollment Term" ) ) {
					String[] enrollTermComponents = enrollTerm.split( " " )
					enrollTermYearInt = Integer.parseInt( enrollTermComponents[0] )
					enrollTermNoInt = Integer.parseInt( enrollTermComponents[2] )
					enrollDate = Term.findByTermSchoolAndTermNoAndYear( schoolNameString, enrollTermNoInt, enrollTermYearInt ).startDate
					isEnrollTermEmpty = false
				}

				/*
				 * If the value of Leave Term parameter associated to the iterated School is "Leave Term", do not put it into the
				 * Enrollment/EnrollmentDocument/StudentDocument entity's leaveTermYear & leaveTermNo/lastEnrollmentLeaveTermYear & lastEnrollmentLeaveTermNo fields.
				 */
				if( leaveTermParamValue != null && !leaveTermParamValue.equals( "Leave Term" ) ) {
					String[] leaveTermComponents = leaveTermParamValue.split( " " )
					leaveTermYearInt = Integer.parseInt( leaveTermComponents[0] )
					leaveTermNoInt = Integer.parseInt( leaveTermComponents[2] )
					leaveTerm = Term.findByTermSchoolAndTermNoAndYear( schoolNameString, leaveTermNoInt, leaveTermYearInt )
					isLeaveTermEmpty = false
				}

				/*
				 * If the value of First Class Attended parameter associated to the iterated School is "First Class Attended", do not put it into the
				 * Enrollment/EnrollmentDocument/StudentDocument entity's firstClassAttended/lastEnrollmentFirstClassAttended field.
				 */
				if( firstClassAttendedParamValue != null && !firstClassAttendedParamValue.equals( "First Class Attended" ) ) {
					firstClassAttendedString = firstClassAttendedParamValue
					isFirstClassAttendedEmpty = false
				}

				/*
				 * If the value of Last Class Attended parameter associated to the iterated School is "Last Class Attended", do not put it into the
				 * Enrollment/StudentDocument entity's lastClassAttended/lastEnrollmentLastClassAttended field.
				 */
				if( lastClassAttendedParamValue != null && !lastClassAttendedParamValue.equals( "Last Class Attended" ) ) {
					lastClassAttendedString = lastClassAttendedParamValue
					isLastClassAttendedEmpty = false
				}
			}
		}
		else {
			enrollmentDocumentBackup = enrollmentIndex.get( params.id )
			
			schoolNameString = enrollmentDocumentBackup.getOnlyField( "schoolName" ).getText()
			studentIdString = enrollmentDocumentBackup.getOnlyField( "studentId" ).getAtom()
			
			/* Obtain enrollment term information from the existing EnrollmentDocument entity. */
			enrollTermYearInt = enrollmentDocumentBackup.getOnlyField( "enrollTermYear" ).getNumber()
			enrollTermNoInt = enrollmentDocumentBackup.getOnlyField( "enrollTermNo" ).getNumber()
			isEnrollTermEmpty = false
			
			enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( studentIdString, schoolNameString, enrollTermNoInt, enrollTermYearInt )
			enrollmentBackup = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( studentIdString, schoolNameString, enrollTermNoInt, enrollTermYearInt )
			
			enrollDate = enrollmentDocumentBackup.getOnlyField( "enrollTermStartDate" ).getDate()
			
			if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != enrollmentDocumentBackup.getOnlyField( "lastUpdateDate" ).getDate() ||
				( params.lastUpdateUser?: "" ) != ( enrollmentDocumentBackup.getFieldNames().contains( "lastUpdateUser" )? enrollmentDocumentBackup.getOnlyField( "lastUpdateUser" ).getText(): "" ) )
				throw new StaleRecordException( params.action, "Enrollment" )
			
			String leaveTermParamValue = request.getParameter( "leaveTerm" )
			firstClassAttendedString = request.getParameter( "firstClassAttended" )
			lastClassAttendedString = request.getParameter( "lastClassAttended" )

			/*
			 * If the value of Leave Term parameter associated to the iterated School is "Leave Term", do not put it into the
			 * Enrollment/EnrollmentDocument/StudentDocument entity's leaveTermYear & leaveTermNo/lastEnrollmentLeaveTermYear & lastEnrollmentLeaveTermNo fields.
			 */
			if( leaveTermParamValue != null && !leaveTermParamValue.equals( "Leave Term" ) ) {
				String[] leaveTermComponents = leaveTermParamValue.split( " " )
				leaveTermYearInt = Integer.parseInt( leaveTermComponents[0] )
				leaveTermNoInt = Integer.parseInt( leaveTermComponents[2] )
				leaveTerm = Term.findByTermSchoolAndTermNoAndYear( schoolNameString, leaveTermNoInt, leaveTermYearInt )
				isLeaveTermEmpty = false
			}
			
			if( !isLeaveTermEmpty ) {
				List<Entity> classTermsToRemove = new ArrayList()
				
				if( enrollment.leaveTermNo == null )
					classTermsToRemove = Term.findByTermSchoolAndTermRange( enrollment.schoolName, leaveTerm, null, new Date() )
				else if( enrollment.leaveTermNo != leaveTermNoInt || enrollment.leaveTermYear != leaveTermYearInt ) {
					Entity oldLeaveTerm = Term.findByTermSchoolAndTermNoAndYear( enrollment.schoolName, enrollment.leaveTermNo, enrollment.leaveTermYear )
					
					if( oldLeaveTerm.startDate > leaveTerm.startDate )
						classTermsToRemove = Term.findByTermSchoolAndTermRange( enrollment.schoolName, leaveTerm, oldLeaveTerm,	new Date() )
				}
				
				if( classTermsToRemove.size() > 0 )
					classTermsToRemove.remove( 0 )
				
				for( Entity classTermToRemove: classTermsToRemove ) {
					if( Fee.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear(
							enrollment.studentId,
							enrollment.schoolName,
							classTermToRemove.termNo,
							classTermToRemove.year
						).size() > 0 || Payment.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear(
							enrollment.studentId,
							enrollment.schoolName,
							classTermToRemove.termNo,
							classTermToRemove.year
						).size() > 0
					) {
						Entity classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear(
								enrollment.studentId
								, enrollment.schoolName
								, classTermToRemove.termNo
								, classTermToRemove.year
							)
						
						throw new EntityIsUsedException( classAttended, ClassAttended.getPrimaryKey() )
					}
				}
			}

			/*
			 * If the value of First Class Attended parameter associated to the iterated School is "First Class Attended", do not put it into the
			 * Enrollment/EnrollmentDocument/StudentDocument entity's firstClassAttended/lastEnrollmentFirstClassAttended field.
			 */
			if( firstClassAttendedString != null && firstClassAttendedString != "First Class Attended" )
				isFirstClassAttendedEmpty = false

			/*
			 * If the value of Last Class Attended parameter associated to the iterated School is "Last Class Attended", do not put it into the
			 * Enrollment/EnrollmentDocument/StudentDocument entity's lastClassAttended/lastEnrollmentLastClassAttended field.
			 */
			if( lastClassAttendedString != null && lastClassAttendedString != "Last Class Attended" )
				isLastClassAttendedEmpty = false
		}
		
		if( isEnrollTermEmpty && Enrollment.isRequired( "enrollTermYear" ) )
			throw new EmptyRequiredFieldException( "Enrollment", "enrollTermYear" )
			
		if( isEnrollTermEmpty && Enrollment.isRequired( "enrollTermNo" ) )
			throw new EmptyRequiredFieldException( "Enrollment", "enrollTermNo" )
			
		if( isLeaveTermEmpty && Enrollment.isRequired( "leaveTermYear" ) )
			throw new EmptyRequiredFieldException( "Enrollment", "leaveTermYear" )
			
		if( isLeaveTermEmpty && Enrollment.isRequired( "leaveTermNo" ) )
			throw new EmptyRequiredFieldException( "Enrollment", "leaveTermNo" )
		
		if( isFirstClassAttendedEmpty && Enrollment.isRequired( "firstClassAttended" ) )
			throw new EmptyRequiredFieldException( "Enrollment", "firstClassAttended" )
			
		if( isLastClassAttendedEmpty && Enrollment.isRequired( "lastClassAttended" ) )
			throw new EmptyRequiredFieldException( "Enrollment", "lastClassAttended" )
		
		if( !isLeaveTermEmpty ) {
			if( leaveTerm.startDate < enrollDate )
				throw new InvalidDateRangeException( "Enrollment Term - Leave Term", enrollDate, leaveTerm.startDate )
		}

		if( params.action.equals( "save" ) )
			enrollment = new Entity( "Enrollment" )
		
		studentDocument = StudentDocument.findByStudentId( studentIdString )
		
		String schoolPrivilege = UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), schoolNameString )?.privilege
		
		/*
		 * Allow the user to save/edit only if the request comes from a test script or a user with Modify privilege for the Enrollment School.
		 */
		if( /* Test Script */ user == null || schoolPrivilege?.equals( "Modify" ) ) {
			/* Populate the Enrollment fields. */
			enrollment.studentId = studentIdString
			enrollment.schoolName = schoolNameString
			enrollment.enrollTermYear = enrollTermYearInt
			enrollment.enrollTermNo = enrollTermNoInt
			enrollment.leaveTermYear = leaveTermYearInt
			enrollment.leaveTermNo = leaveTermNoInt
			enrollment.firstClassAttended = firstClassAttendedString
			enrollment.lastClassAttended = lastClassAttendedString
			enrollment.leaveReasonCategory = leaveReasonCategoryString
			enrollment.leaveReason = leaveReasonString
			enrollment.lastUpdateDate = new Date()
			
			if( user != null )
				enrollment.lastUpdateUser = user.getEmail()
				
			Entity overlappedEnrollment = Enrollment.findOverlappedEnrollment( enrollment )
			
			if( overlappedEnrollment != null )
				throw new OverlappedEnrollmentException( overlappedEnrollment )
				
			enrollment.save()
			
			List<String> enrollmentParams = request.getParameterNames().toList()
			
			for( String paramName: enrollmentParams ) {
				if( paramName.startsWith( "classAttended" ) ) {
					classesAttendedBackup = ClassAttended.findByEnrollment( enrollment )
					classesAttendedBackup*.delete()
					break;
				}
			}
			
			int classAttendedIndex = 0
			
			for( String paramName: enrollmentParams ) {
				
				if( paramName.startsWith( "classAttended" ) ) {
					
					if( classAttendedIndex == 0 ) {
						enrollment.firstClassAttended = request.getParameter( paramName )
						enrollment.lastClassAttended = request.getParameter( paramName )
						enrollment.save()
					}
					
					int classTermNo = Integer.parseInt( paramName.substring( 23 ) )
					int classTermYear = Integer.parseInt( paramName.substring( 13, 17 ) )
					Entity classFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( enrollment.schoolName, request.getParameter( paramName ), classTermNo, classTermYear )
					
					Entity classAttended = new Entity( "ClassAttended", enrollment.getKey() )
					classAttended.studentId = enrollment.studentId
					classAttended.schoolName = enrollment.schoolName
					classAttended.classTermNo = classTermNo
					classAttended.classTermYear = classTermYear
					classAttended.setProperty( "class", request.getParameter( paramName ) )
					classAttended.classLevel = classFees.classLevel
					classAttended.enrollTermNo = enrollment.enrollTermNo
					classAttended.enrollTermYear = enrollment.enrollTermYear
					classAttended.tuitionFee = classFees.tuitionFee
					classAttended.boardingFee = classFees.boardingFee
					classAttended.boardingInd = request.getParameter( "boardingInd${ classTermYear } Term ${ classTermNo }" )? "Y": "N"
					classAttended.lastUpdateDate = new Date()
					classAttended.lastUpdateUser = user?.getEmail()
					classAttended.save()
					
					classesAttendedList.add( classAttended )
					
					if( classAttended.boardingInd == "Y" )
						boardingFeesNumber += classAttended.boardingFee?: 0
					
					classesAttendedText += "${ classAttended.getProperty( "class" ) }\t"
					termsEnrolledText += "${ classAttended.classTermYear } Term ${ classAttended.classTermNo }\t"
					tuitionFeesNumber += classAttended.tuitionFee?: 0
					
					if( classAttended.classLevel < Class.findBySchoolNameAndClass( enrollment.schoolName, enrollment.firstClassAttended ).level ) {
						enrollment.firstClassAttended = classAttended.getProperty( "class" )
						enrollment.save()
					}
					else if( classAttended.classLevel > Class.findBySchoolNameAndClass( enrollment.schoolName, enrollment.lastClassAttended ).level ) {
						enrollment.lastClassAttended = classAttended.getProperty( "class" )
						enrollment.save()
					}
					
					classAttendedIndex++
				}
			}
		}
		else if( schoolPrivilege == null || schoolPrivilege.equals( "Read" ) )
			throw new AuthorizationException( schoolNameString )
			
		if( Class.findBySchoolNameAndClass( enrollment.schoolName, enrollment.firstClassAttended ).level > Class.findBySchoolNameAndClass( enrollment.schoolName, enrollment.lastClassAttended ).level )
			throw new InvalidClassLevelRangeException( "First Class Attended - Last Class Attended", enrollment.firstClassAttended, enrollment.lastClassAttended )
		
		Entity student = Student.findByStudentId( studentIdString )
		
		/* Populate the EnrollmentDocument fields. */
		double feesDueNumber = tuitionFeesNumber + boardingFeesNumber + otherFeesNumber - paymentsNumber
		
		if( feesDueNumber < 0 )
			feesDueNumber = 0
		
		Date leaveTermEndDate = leaveTerm?.endDate
		
		if( leaveTermEndDate == null )
			leaveTermEndDate = Date.parse( "MMM d yyyy", "Dec 31 2999" )
			
		/* Populate the EnrollmentDocument fields. */
		enrollmentDocBuilder.setId( "" + enrollment.getKey().getId() )
			.addField( Field.newBuilder().setName( "studentId" ).setAtom( studentIdString ) )
			.addField( Field.newBuilder().setName( "firstName" ).setText( student.firstName ) )
			.addField( Field.newBuilder().setName( "schoolName" ).setText( schoolNameString ) )
			.addField( Field.newBuilder().setName( "termsEnrolled" ).setText( termsEnrolledText ) )
			.addField( Field.newBuilder().setName( "enrollTermYear" ).setNumber( enrollTermYearInt ) )
			.addField( Field.newBuilder().setName( "enrollTermNo" ).setNumber( enrollTermNoInt ) )
			.addField( Field.newBuilder().setName( "enrollTermStartDate" ).setDate( enrollDate ) )
			.addField( Field.newBuilder().setName( "leaveTermEndDate" ).setDate( leaveTermEndDate ) )
			.addField( Field.newBuilder().setName( "classesAttended" ).setText( classesAttendedText ) )
			.addField( Field.newBuilder().setName( "firstClassAttended" ).setText( enrollment.firstClassAttended ) )
			.addField( Field.newBuilder().setName( "lastClassAttended" ).setText( enrollment.lastClassAttended ) )
			.addField( Field.newBuilder().setName( "tuitionFees" ).setNumber( tuitionFeesNumber ) )
			.addField( Field.newBuilder().setName( "boardingFees" ).setNumber( boardingFeesNumber ) )
			.addField( Field.newBuilder().setName( "otherFees" ).setNumber( otherFeesNumber ) )
			.addField( Field.newBuilder().setName( "payments" ).setNumber( paymentsNumber ) )
			.addField( Field.newBuilder().setName( "feesDue" ).setNumber( feesDueNumber ) )
			.addField( Field.newBuilder().setName( "lastUpdateDate" ).setDate( new Date() ) )
		
		if( student.lastName != null )
			enrollmentDocBuilder.addField( Field.newBuilder().setName( "lastName" ).setText( student.lastName ) )
				
		if( student.birthDate != null )
			enrollmentDocBuilder.addField( Field.newBuilder().setName( "birthDate" ).setDate( student.birthDate ) )
				
		if( student.village != null )
			enrollmentDocBuilder.addField( Field.newBuilder().setName( "village" ).setText( student.village ) )
				
		if( student.genderCode != null )
			enrollmentDocBuilder.addField( Field.newBuilder().setName( "genderCode" ).setText( student.genderCode ) )
				
		if( student.specialInfo != null )
			enrollmentDocBuilder.addField( Field.newBuilder().setName( "specialInfo" ).setText( student.specialInfo ) )
				
		if( leaveTermYearInt != null )
			enrollmentDocBuilder.addField( Field.newBuilder().setName( "leaveTermYear" ).setNumber( leaveTermYearInt ) )
				
		if( leaveTermNoInt != null )
			enrollmentDocBuilder.addField( Field.newBuilder().setName( "leaveTermNo" ).setNumber( leaveTermNoInt ) )
			
		if( leaveReasonCategoryString != null )
			enrollmentDocBuilder.addField( Field.newBuilder().setName( "leaveReasonCategory" ).setText( leaveReasonCategoryString ) )
			
		if( leaveReasonString != null )
			enrollmentDocBuilder.addField( Field.newBuilder().setName( "leaveReason" ).setText( leaveReasonString ) )
				
		if( user != null )
            enrollmentDocBuilder.addField( Field.newBuilder().setName( "lastUpdateUser" ).setText( user.getEmail() ) )
            
        enrollmentDocument = enrollmentDocBuilder.build()
		enrollmentIndex.put( enrollmentDocument )
		
		if( params.action == "save" ) {
			enrollmentMetaData = Enrollment.findMetaDataBySchoolName( schoolNameString )
			enrollmentMetaDataBackup = Enrollment.findMetaDataBySchoolName( schoolNameString )
			
			enrollmentMetaData.count = enrollmentMetaData.count + 1
			enrollmentMetaData.save()
		}
		
		Document lastEnrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( studentIdString )
				
		lastEnrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( studentIdString )
		
		if(
			lastEnrollmentDocument.getOnlyField( "schoolName" ).getText() == schoolNameString &&
			lastEnrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() == enrollTermYearInt &&
			lastEnrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() == enrollTermNoInt
		) {
			
			/* Populate the StudentDocument fields. */
			Set<String> studentDocumentFieldNames = studentDocument.getFieldNames()
				
			/* Populate the StudentDocument fields. */
			studentIndex.put {
				document( id: studentDocument.getId() ) {
					studentId atom: studentDocument.getOnlyField( "studentId" ).getAtom()
					firstName text: studentDocument.getOnlyField( "firstName" ).getText()
					lastName text: studentDocumentFieldNames.contains( "lastName" )? studentDocument.getOnlyField( "lastName" ).getText(): null
					classesAttended text: classesAttendedText
					lastEnrollmentFirstClassAttended text: enrollment.firstClassAttended
					lastEnrollmentLastClassAttended text: enrollment.lastClassAttended
					termsEnrolled text: termsEnrolledText
					lastEnrollmentTermYear number: enrollTermYearInt
					lastEnrollmentTermNo number: enrollTermNoInt
					lastEnrollmentTermStartDate date: enrollDate
					lastEnrollmentLeaveTermYear number: leaveTermYearInt
					lastEnrollmentLeaveTermNo number: leaveTermNoInt
					birthDate date: studentDocumentFieldNames.contains( "birthDate" )? studentDocument.getOnlyField( "birthDate" ).getDate(): null
					village text: studentDocumentFieldNames.contains( "village" )? studentDocument.getOnlyField( "village" ).getText(): null
					specialInfo text: studentDocumentFieldNames.contains( "specialInfo" )? studentDocument.getOnlyField( "specialInfo" ).getText(): null
					genderCode text: studentDocumentFieldNames.contains( "genderCode" )? studentDocument.getOnlyField( "genderCode" ).getText(): null
					lastEnrollmentSchool text: schoolNameString
					lastEnrollmentLeaveReasonCategory text: leaveReasonCategoryString
					lastEnrollmentLeaveReason text: leaveReasonString
					tuitionFees number: tuitionFeesNumber
					boardingFees number: boardingFeesNumber
					otherFees number: otherFeesNumber
					payments number: paymentsNumber
					feesDue number: feesDueNumber
					lastUpdateDate date: new Date()
					lastUpdateUser text: user?.getEmail()
				}
			}
		}
		
		EnrollmentDocument.deleteMemcache()
		StudentDocument.deleteMemcache()
		
		/* Respond with the HTML code that is required to display the new EnrollmentDocument within an accordion. */
		println ListItemFormatter.getEnrollmentListItem( enrollmentDocument )
	}
	catch( Exception e ) {
		log.info e.getMessage()
		try{
			if( studentDocument != null ) {
				studentIndex.put( studentDocument )
				StudentDocument.deleteMemcache()
			}
		} catch( Exception backupRestoreException ) {}
		
		if( params.action.equals( "save" ) ) {
			
			/* If the save fails, delete the records that have been created during the save process. */
			classesAttendedList.each(
				{
					try { it.delete() } catch( Exception deleteException ) {}
				}
			)
			
			try {
				if( enrollment != null ) {
					enrollmentIndex.delete( "" + enrollment.getKey()?.getId() )
					EnrollmentDocument.deleteMemcache()
				}
			} catch( Exception deleteException ) {}
			
			try { enrollmentMetaDataBackup?.save() } catch( Exception backupRestoreException ) {}
			
			try{ if( enrollment != null ) enrollment.delete() } catch( Exception deleteException ) {}
			
			/* Respond with an Internal Server error message if the save fails. */
			response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
			response.setHeader( "Response-Phrase", e.getMessage() )
		}
		else {
			
			/* If the edit fails, delete the records that have been created during the edit process. */
			classesAttendedList.each(
				{
					try { it.delete() } catch( Exception deleteException ) {}
				}
			)
			
			/* If the edit fails, save the records that have been deleted during the edit process. */
			classesAttendedBackup.each(
				{
					try { it.save() } catch( Exception saveException ) {}
				}
			)
			
			/* Restore the old values of Enrollment and EnrollmentDocument */
			try{ enrollmentBackup?.save() } catch( Exception backupRestoreException ) {}
			
			try {
				if( enrollmentDocumentBackup != null ) {
					enrollmentIndex.put( enrollmentDocumentBackup )
					EnrollmentDocument.deleteMemcache()
				}
			} catch( Exception backupRestoreException ) {}
			
			/* Respond with an Internal Server error message if the save fails. */
			response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
			response.setHeader( "Response-Phrase", e.getMessage() )
		}
	}
}
    
/* Allow the user to convert and drop only if the request comes from a test script or an admin with Modify privilege. */
else if( /* Test Script */ user == null || /* Admin with Modify privilege */ URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
    
    /* Convert one Entity field to another Entity field. */
    if( params.action.equals( "convert" ) ) {
        try {
            
            /* Convert some date fields to term fields. */
            if( params.from.equals( "date" ) && params.to.equals( "term" ) ) {
                Enrollment.list().each() {
                    
                    /* Convert enrollDate field to enrollTermYear and enrollTermNo fields. */
                    def enrollTerm = Term.findByTermSchoolAndDate( it.schoolName, it.enrollDate )
                    if( enrollTerm != null ) {
                        it.enrollTermYear = enrollTerm.year
                        it.enrollTermNo = enrollTerm.termNo
                    }
                    
                    /* Convert leaveDate field to leaveTermYear and leaveTermNo fields. */
                    def leaveTerm = Term.findByTermSchoolAndDate( it.schoolName, it.leaveDate )
                    if( leaveTerm != null ) {
                        it.leaveTermYear = leaveTerm.year
                        it.leaveTermNo = leaveTerm.termNo
                    }
                    
                    it.save()
                }
            }
            
            println "Converted Enrollment ${ params.from } values to ${ params.to } values."
        }
        catch( Exception e ) {
            println e.getMessage()
        }
    }
    
    /* Drop an Enrollment entity field identified by the fieldName parameter when the action parameter value is "drop". */
    else if( params.action.equals( "drop" ) ) {
        try {
            Enrollment.list().each() {
                it.removeProperty( params.fieldName )
                it.save()
            }
            
            println "Dropped ${ params.fieldName } from all Enrollment entities."
        }
        catch( Exception e ) {
            println e.getMessage()
        }
    }
    
    /* Sync Student data in EnrollmentDocument with StudentDocument */
    else if( params.action == "sync" ) {
    	Index enrollmentIndex = search.index( "Enrollment" )
    			
    	EnrollmentDocument.list().each(
			{
				Set<String> itFieldNames = it.getFieldNames()
				Document studentDocument = StudentDocument.findByStudentId( it.getOnlyField( "studentId" ).getAtom() )
				Set<String> studentDocumentFieldNames = studentDocument.getFieldNames()
						
				if(
					it.getOnlyField( "firstName" ).getText() != studentDocument.getOnlyField( "firstName" ).getText() ||
					itFieldNames.contains( "lastName" ) != studentDocumentFieldNames.contains( "lastName" ) ||
					(
						itFieldNames.contains( "lastName" ) && studentDocumentFieldNames.contains( "lastName" ) &&
						it.getOnlyField( "lastName" ).getText() != studentDocument.getOnlyField( "lastName" ).getText()
					) ||
					itFieldNames.contains( "birthDate" ) != studentDocumentFieldNames.contains( "birthDate" ) ||
					(
						itFieldNames.contains( "birthDate" ) && studentDocumentFieldNames.contains( "birthDate" ) &&
						it.getOnlyField( "birthDate" ).getDate() != studentDocument.getOnlyField( "birthDate" ).getDate()
					) ||
					itFieldNames.contains( "village" ) != studentDocumentFieldNames.contains( "village" ) ||
					(
						itFieldNames.contains( "village" ) && studentDocumentFieldNames.contains( "village" ) &&
						it.getOnlyField( "village" ).getText() != studentDocument.getOnlyField( "village" ).getText()
					) ||
					itFieldNames.contains( "genderCode" ) != studentDocumentFieldNames.contains( "genderCode" ) ||
					(
						itFieldNames.contains( "genderCode" ) && studentDocumentFieldNames.contains( "genderCode" ) &&
						it.getOnlyField( "genderCode" ).getText() != studentDocument.getOnlyField( "genderCode" ).getText()
					) ||
					itFieldNames.contains( "specialInfo" ) != studentDocumentFieldNames.contains( "specialInfo" ) ||
					(
						itFieldNames.contains( "specialInfo" ) && studentDocumentFieldNames.contains( "specialInfo" ) &&
						it.getOnlyField( "specialInfo" ).getText() != studentDocument.getOnlyField( "specialInfo" ).getText()
					)
				) {
					Document.Builder enrollmentDocBuilder = Document.newBuilder()
					enrollmentDocBuilder.setId( it.getId() )
						.addField( Field.newBuilder().setName( "studentId" ).setAtom( it.getOnlyField( "studentId" ).getAtom() ) )
						.addField( Field.newBuilder().setName( "firstName" ).setText( studentDocument.getOnlyField( "firstName" ).getText() ) )
						.addField( Field.newBuilder().setName( "schoolName" ).setText( it.getOnlyField( "schoolName" ).getText() ) )
						.addField( Field.newBuilder().setName( "termsEnrolled" ).setText( it.getOnlyField( "termsEnrolled" ).getText() ) )
						.addField( Field.newBuilder().setName( "enrollTermYear" ).setNumber( it.getOnlyField( "enrollTermYear" ).getNumber() ) )
						.addField( Field.newBuilder().setName( "enrollTermNo" ).setNumber( it.getOnlyField( "enrollTermNo" ).getNumber() ) )
						.addField( Field.newBuilder().setName( "enrollTermStartDate" ).setDate( it.getOnlyField( "enrollTermStartDate" ).getDate() ) )
						.addField( Field.newBuilder().setName( "leaveTermEndDate" ).setDate( it.getOnlyField( "leaveTermEndDate" ).getDate() ) )
						.addField( Field.newBuilder().setName( "classesAttended" ).setText( it.getOnlyField( "classesAttended" ).getText() ) )
						.addField( Field.newBuilder().setName( "firstClassAttended" ).setText( it.getOnlyField( "firstClassAttended" ).getText() ) )
						.addField( Field.newBuilder().setName( "lastClassAttended" ).setText( it.getOnlyField( "lastClassAttended" ).getText() ) )
						.addField( Field.newBuilder().setName( "tuitionFees" ).setNumber( it.getOnlyField( "tuitionFees" ).getNumber() ) )
						.addField( Field.newBuilder().setName( "boardingFees" ).setNumber( it.getOnlyField( "boardingFees" ).getNumber() ) )
						.addField( Field.newBuilder().setName( "otherFees" ).setNumber( it.getOnlyField( "otherFees" ).getNumber() ) )
						.addField( Field.newBuilder().setName( "payments" ).setNumber( it.getOnlyField( "payments" ).getNumber() ) )
						.addField( Field.newBuilder().setName( "feesDue" ).setNumber( it.getOnlyField( "feesDue" ).getNumber() ) )
						.addField( Field.newBuilder().setName( "lastUpdateDate" ).setDate( new Date() ) )
					
					if( studentDocumentFieldNames.contains( "lastName" ) )
						enrollmentDocBuilder.addField( Field.newBuilder().setName( "lastName" ).setText( studentDocument.getOnlyField( "lastName" ).getText() ) )
							
					if( studentDocumentFieldNames.contains( "birthDate" ) )
						enrollmentDocBuilder.addField( Field.newBuilder().setName( "birthDate" ).setDate( studentDocument.getOnlyField( "birthDate" ).getDate() ) )
							
					if( studentDocumentFieldNames.contains( "village" ) )
						enrollmentDocBuilder.addField( Field.newBuilder().setName( "village" ).setText( studentDocument.getOnlyField( "village" ).getText() ) )
							
					if( studentDocumentFieldNames.contains( "genderCode" ) )
						enrollmentDocBuilder.addField( Field.newBuilder().setName( "genderCode" ).setText( studentDocument.getOnlyField( "genderCode" ).getText() ) )
							
					if( studentDocumentFieldNames.contains( "specialInfo" ) )
						enrollmentDocBuilder.addField( Field.newBuilder().setName( "specialInfo" ).setText( studentDocument.getOnlyField( "specialInfo" ).getText() ) )
							
					if( itFieldNames.contains( "leaveTermYear" ) )
						enrollmentDocBuilder.addField( Field.newBuilder().setName( "leaveTermYear" ).setNumber( it.getOnlyField( "leaveTermYear" ).getNumber() ) )
							
					if( itFieldNames.contains( "leaveTermNo" ) )
						enrollmentDocBuilder.addField( Field.newBuilder().setName( "leaveTermNo" ).setNumber( it.getOnlyField( "leaveTermNo" ).getNumber() ) )
						
					if( itFieldNames.contains( "leaveReasonCategory" ) )
						enrollmentDocBuilder.addField( Field.newBuilder().setName( "leaveReasonCategory" ).setText( it.getOnlyField( "leaveReasonCategory" ).getText() ) )
						
					if( itFieldNames.contains( "leaveReason" ) )
						enrollmentDocBuilder.addField( Field.newBuilder().setName( "leaveReason" ).setText( it.getOnlyField( "leaveReason" ).getText() ) )
							
					if( user != null )
			            enrollmentDocBuilder.addField( Field.newBuilder().setName( "lastUpdateUser" ).setText( user.getEmail() ) )
			            
			        enrollmentIndex.put( enrollmentDocBuilder.build() )
			        
			        println( "<p>Synced ${ it.getOnlyField( "studentId" ).getAtom() }</p>" )
				}
			}
    	)
    }
}
else {
    response.setStatus( response.SC_UNAUTHORIZED )
    response.setHeader( "Response-Phrase", "Unauthorized" )
}