/*
Document   : Groovlet file: StudentController.groovy
Created on : Mon Jun 11 21:41:00 CST 2012
Author     : awijasa
 */

import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.search.Document
import com.google.appengine.api.search.Document.Builder
import com.google.appengine.api.search.Field
import com.google.appengine.api.search.Index
import com.google.appengine.api.search.Results
import com.google.appengine.api.search.ScoredDocument
import data.AnonymousParentalRelationship
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
import data.Parent
import data.ParentalRelationship
import data.Payment
import data.Relationship
import data.School
import data.StaleRecordException
import data.Student
import data.StudentDocument
import data.Term
import data.URFUser
import data.UserPrivilege
import formatter.ListItemFormatter

/* Handle create, update, and delete operations on the Student entity. */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the StudentController. */
if( user == null && !localMode )
    response.sendError( response.SC_UNAUTHORIZED )
else {
    
	if( params.action == "count" ) {
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
						Entity studentMetaData = new Entity( "StudentMetaData", appEntity.getKey() )
						studentMetaData.schoolName = it.name
						studentMetaData.count = StudentDocument.findByLastEnrollmentSchool( it.name ).getNumberReturned()
						studentMetaData.save()
						println "${ it.name } Students' record count (${ studentMetaData.count }) has been recorded into StudentMetaData"
					}
				)
			}
			catch( Exception e ) {
				println e.getMessage()
			}
		}
		else
			response.sendError( response.SC_UNAUTHORIZED )
	}
	
	/*
     * Delete a StudentDocument identified by the id parameter and its associated Enrollment, EnrollmentWithStudent,
     * AnonymousParentalRelationship, and Student entities when the action parameter value is "delete".
     */
    else if( params.action.equals( "delete" ) ) {
		List<Entity> anonymousParentalRelationships
		List<Entity> classesAttended
		Index enrollmentIndex = search.index( "Enrollment" )
		Map<String, Entity> enrollmentMetaDataBackups = new LinkedHashMap()
		List<Entity> enrollments
        Results<ScoredDocument> enrollmentResults
		List<Entity> fees
		List<Entity> payments
		List<Entity> relationships
		Entity student
		Document studentDocument
		Index studentIndex = search.index( "Student" )
		Entity studentMetaDataBackup
        
        try {
			
			/* The local AppEngine server can't process this block properly */
			if( !localMode ) {
				StudentDocument.findByLimitAndOffset( 0, Integer.parseInt( params.nextTwentyOffset ), session ).each(
					{
						/*
						 * Respond with the HTML code that is required to display the StudentDocument that can fill the empty space that resulted
						 * from the deleted record within an accordion.
						 */
						println ListItemFormatter.getStudentListItem( it, null )
					}
				)
			}
			
			classesAttended = ClassAttended.findByStudentId( params.studentId )
			classesAttended.each(
				{
					/* Allow the user to delete only if the request comes from a test script or a user with Modify privilege for the Student's School. */
					if( /* Test Script */ user == null ||
						/* User with Modify privilege for the Student's Last Enrollment School. */
						UserPrivilege.findByUserEmailAndSchoolName( user.getEmail(), it.schoolName )?.privilege?.equals( "Modify" ) )
						it.delete()
					else
						throw new AuthorizationException( it.schoolName )
				}
			)
			
			enrollments = Enrollment.findByStudentId( params.studentId )
            enrollments.each() {
                
                /* Allow the user to delete only if the request comes from a test script or a user with Modify privilege for the Student's School. */
                if( /* Test Script */ user == null ||
                    /* User with Modify privilege for the Student's Last Enrollment School. */
                    UserPrivilege.findByUserEmailAndSchoolName( user.getEmail(), it.schoolName )?.privilege?.equals( "Modify" ) )
                    it.delete()
                else
                    throw new AuthorizationException( it.schoolName )
            }

			anonymousParentalRelationships = AnonymousParentalRelationship.findByStudentId( params.studentId )
            anonymousParentalRelationships.each() {
                it.delete()
            }
			
			relationships = Relationship.findByStudentId( params.studentId )
			relationships*.delete()

			student = Student.findByStudentId( params.studentId )
            
            if( Student.isUsed( student ) )
				throw new EntityIsUsedException( student, Student.getPrimaryKey() )
            
            student?.delete()
            
            enrollmentResults = EnrollmentDocument.findByStudentId( params.studentId )
            enrollmentResults.each(
				{
					enrollmentIndex.delete( it.getId() )
					
					Entity enrollmentMetaData = Enrollment.findMetaDataBySchoolName( it.getOnlyField( "schoolName" ).getText() )
					
					if( !enrollmentMetaDataBackups.containsKey( it.getOnlyField( "schoolName" ).getText() ) )
						enrollmentMetaDataBackups.put( it.getOnlyField( "schoolName" ).getText(), Enrollment.findMetaDataBySchoolName( it.getOnlyField( "schoolName" ).getText() ) )
					
					if( enrollmentMetaData.count > 0 ) {
						enrollmentMetaData.count = enrollmentMetaData.count - 1
						enrollmentMetaData.save()
					}
				}
			)
			
			studentDocument = studentIndex.get( params.id )
            
			fees = Fee.findByStudentId( studentDocument.getOnlyField( "studentId" ).getAtom() )
			
			fees.each(
				{
					/* Allow the user to delete only if the request comes from a test script or a user with Modify privilege for the Student's School. */
					if( /* Test Script */ user == null ||
						/* User with Modify privilege for the Student's Last Enrollment School. */
						UserPrivilege.findByUserEmailAndSchoolName( user.getEmail(), it.schoolName )?.privilege?.equals( "Modify" ) )
						it.delete()
					else
						throw new AuthorizationException( it.schoolName )
				}
			)
			
			payments = Payment.findByStudentId( studentDocument.getOnlyField( "studentId" ).getAtom() )
			
			payments.each(
				{
					/* Allow the user to delete only if the request comes from a test script or a user with Modify privilege for the Student's School. */
					if( /* Test Script */ user == null ||
						/* User with Modify privilege for the Student's Last Enrollment School. */
						UserPrivilege.findByUserEmailAndSchoolName( user.getEmail(), it.schoolName )?.privilege?.equals( "Modify" ) )
						it.delete()
					else
						throw new AuthorizationException( it.schoolName )
				}
			)
			
			if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != studentDocument.getOnlyField( "lastUpdateDate" ).getDate() ||
                ( params.lastUpdateUser?: "" ) != ( studentDocument.getFieldNames().contains( "lastUpdateUser" )? studentDocument.getOnlyField( "lastUpdateUser" ).getText(): "" ) )
                throw new StaleRecordException( params.action, "Student" )
            
            studentIndex.delete( studentDocument.getId() )
			
			Entity studentMetaData = Student.findMetaDataBySchoolName( studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText() )
			studentMetaDataBackup = Student.findMetaDataBySchoolName( studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText() )
			
			if( studentMetaData.count > 0 ) {
				studentMetaData.count = studentMetaData.count - 1
				studentMetaData.save()
			}
            
			EnrollmentDocument.deleteMemcache()
			Fee.deleteMemcache()
			Payment.deleteMemcache()
            StudentDocument.deleteMemcache()
        }
        catch( Exception e ) {
			
        	/* Rollback the deleted Classes Attended, Enrollments, Anonymous Parental Relationships, and Student. */
            classesAttended.each(
				{
					try { it.save() } catch( Exception saveException ) {}
				}
			)
			
			enrollments.each(
				{
					try { it.save() } catch( Exception saveException ) {}
				}
			)
			
            anonymousParentalRelationships.each(
				{
					try { it.save() } catch( Exception saveException ) {}
				}
			)
			
			relationships.each(
				{
					try { it.save() } catch( Exception saveException ) {}
				}
			)
            
			try { student.save() } catch( Exception saveException ) {}
            
			enrollmentResults.each(
				{
					try{ enrollmentIndex.put( it ) } catch( Exception saveException ) {}
				}
			)
			
			enrollmentMetaDataBackups.each(
				{ schoolName, enrollmentMetaData ->
					try { enrollmentMetaData.save() } catch( Exception saveException ) {}
				}
			)
			
			try { studentIndex.put( studentDocument ) } catch( Exception saveException ) {}
			
			try { studentMetaDataBackup.save() } catch( Exception saveException ) {}
			
			fees.each(
				{
					try { it.save() } catch( Exception saveException ) {}
				}
			)
			
			payments.each(
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
     * Save a new Student entity and its new Enrollment, AnonymousParentalRelationship, and StudentDocuments when the action parameter
     * value is "save".
     */
    else if( params.action.equals( "edit" ) || params.action.equals( "save" ) ) {
        String firstName
        String lastName
        Date birthDate
        String village
        String genderCode
        String specialInfo
        String enrollmentSchool
		String classesAttendedText
		String termsEnrolledText
        String enrollmentLeaveReasonCategory
        String enrollmentLeaveReason
		double boardingFeesNumber
		double tuitionFeesNumber
		double otherFeesNumber = 0.0
		double paymentsNumber = 0.0
        
        Entity student
        Entity studentBackup
        String studentId
        Entity anonymousParentalRelationship
        Entity enrollment
        Entity enrollmentBackup
		Builder enrollmentDocBuilder = Document.newBuilder()
		Document enrollmentDocument
        Document enrollmentDocumentBackup
		Index enrollmentIndex = search.index( "Enrollment" )
		Entity enrollmentMetaData
		Entity enrollmentMetaDataBackup
		List<Entity> classesAttended = new ArrayList()
		List<Entity> classesAttendedBackup = new ArrayList()
		List<Entity> parentBackupList = new ArrayList()
		Builder studentDocBuilder = Document.newBuilder()
		Document studentDocument
		Document studentDocumentBackup
		Index studentIndex = search.index( "Student" )
		Entity studentMetaData
		Entity studentMetaDataBackup
        
        try {
            
            /* If the firstName parameter value is "First Name" or empty, do not put it into the Student/StudentDocument's firstName field. */
            if( params.firstName != null && !params.firstName.equals( "First Name" ) && !params.firstName.equals( "" ) )
                firstName = params.firstName
                
            /* If the firstName parameter value is "First Name" or empty, and firstName is a required Student entity's field, throw an EmptyRequiredFieldException. */
            else if( Student.isRequired( "firstName" ) )
                throw new EmptyRequiredFieldException( "Student", "firstName" )

            /* If the lastName parameter value is "Last Name" or empty, do not put it into the Student/StudentDocument's lastName field. */
            if( params.lastName != null && !params.lastName.equals( "Last Name" ) && !params.lastName.equals( "" ) )
                lastName = params.lastName
                
            /* If the lastName parameter value is "Last Name" or empty, and lastName is a required Student entity's field, throw an EmptyRequiredFieldException. */
            else if( Student.isRequired( "lastName" ) )
                throw new EmptyRequiredFieldException( "Student", "lastName" )

            /* If the birthDate parameter value is "Birth Date" or empty, do not put it into the Student/StudentDocument's birthDate field. */
            if( params.birthDate != null && !params.birthDate.equals( "Birth Date" ) && !params.birthDate.equals( "" ) )
                birthDate = Date.parse( "MMM d yy", params.birthDate )
                
            /* If the birthDate parameter value is "Birth Date" or empty, and birthDate is a required Student entity's field, throw an EmptyRequiredFieldException. */
            else if( Student.isRequired( "birthDate" ) )
                throw new EmptyRequiredFieldException( "Student", "birthDate" )

            /* If the village parameter value is "Village" or empty, do not put it into the Student/StudentDocument's village field. */
            if( params.village != null && !params.village.equals( "Village" ) && !params.village.equals( "" ) )
                village = params.village
            
            /* If the village parameter value is "Village" or empty, and village is a required Student entity's field, throw an EmptyRequiredFieldException. */
            else if( Student.isRequired( "village" ) )
                throw new EmptyRequiredFieldException( "Student", "village" )

            /* If the genderCode parameter value is "Gender" or empty, do not put it into the Student/StudentDocument's genderCode field. */
            if( params.genderCode != null && !params.genderCode.equals( "Gender" ) && !params.genderCode.equals( "" ) )
                genderCode = params.genderCode
                
            /* If the genderCode parameter value is "Gender" or empty, and genderCode is a required Student entity's field, throw an EmptyRequiredFieldException. */
            else if( Student.isRequired( "genderCode" ) )
                throw new EmptyRequiredFieldException( "Student", "genderCode" )

            /* If the specialInfo parameter value is "Special Info/Condition" or empty, do not put it into the Student/StudentDocument's specialInfo field. */
            if( params.specialInfo != null && !params.specialInfo.equals( "Special Info/Condition" ) && !params.specialInfo.equals( "" ) )
                specialInfo = params.specialInfo
                
            /* If the specialInfo parameter value is "Special Info/Condition" or empty, and specialInfo is a required Student entity's field, throw an EmptyRequiredFieldException. */
            else if( Student.isRequired( "specialInfo" ) )
                throw new EmptyRequiredFieldException( "Student", "specialInfo" )

            if( params.action.equals( "save" ) ) {
            
                /* If the enrollmentSchool parameter value is "School" or empty, do not put it into the Enrollment/StudentDocument's schoolName/lastEnrollmentSchool field. */
                if( params.enrollmentSchool != null && !params.enrollmentSchool.equals( "School" ) && !params.enrollmentSchool.equals( "" ) )
                    enrollmentSchool = params.enrollmentSchool

                /* If the enrollmentSchool parameter value is "School" or empty, and schoolName is a required Enrollment entity's field, throw an EmptyRequiredFieldException. */
                else if( Enrollment.isRequired( "schoolName" ) )
                    throw new EmptyRequiredFieldException( "Enrollment", "schoolName" )
            }

            /* If the enrollmentLeaveReasonCategory parameter value is "Leave Reason Category" or empty, do not put it into the Enrollment/StudentDocument's leaveReasonCategory/lastEnrollmentLeaveReasonCategory field. */
            if( params.enrollmentLeaveReasonCategory != null && !params.enrollmentLeaveReasonCategory.equals( "Leave Reason Category" ) && !params.enrollmentLeaveReasonCategory.equals( "" ) )
                enrollmentLeaveReasonCategory = params.enrollmentLeaveReasonCategory
            
            /* If the enrollmentLeaveReasonCategory parameter value is "Leave Reason Category" or empty, and leaveReasonCategory is a required Enrollment entity's field, throw an EmptyRequiredFieldException. */
            else if( Enrollment.isRequired( "leaveReasonCategory" ) )
                throw new EmptyRequiredFieldException( "Enrollment", "leaveReasonCategory" )

            /* If the enrollmentLeaveReason parameter value is "Leave Reason" or empty, do not put it into the Enrollment/StudentDocument's leaveReason/lastEnrollmentLeaveReason field. */
            if( params.enrollmentLeaveReason != null && !params.enrollmentLeaveReason.equals( "Leave Reason" ) && !params.enrollmentLeaveReason.equals( "" ) )
                enrollmentLeaveReason = params.enrollmentLeaveReason
                
            /* If the enrollmentLeaveReason parameter value is "Leave Reason" or empty, and leaveReason is a required Enrollment entity's field, throw an EmptyRequiredFieldException. */
            else if( Enrollment.isRequired( "leaveReason" ) )
                throw new EmptyRequiredFieldException( "Enrollment", "leaveReason" )

            boolean isEnrollmentTermEmpty = true
            boolean isLeaveTermEmpty = true
            boolean isFirstClassAttendedEmpty = true
            boolean isLastClassAttendedEmpty = true
			Date lastEnrollmentDate
            String lastEnrollmentFirstClassAttended
            String lastEnrollmentLastClassAttended
			Entity lastEnrollmentLeaveTerm
            Integer lastEnrollmentLeaveTermNo
            Integer lastEnrollmentLeaveTermYear
            Integer lastEnrollmentTermNo
            Integer lastEnrollmentTermYear
            
            if( params.action.equals( "save" ) ) {
            
                /* Iterate through each School. */
                School.list().eachWithIndex() { school, i ->
                    String enrollmentTerm = request.getParameter( "enrollmentTermSchool${ i + 1 }" )
                    String leaveTerm = request.getParameter( "leaveTermSchool${ i + 1 }" )
                    String firstClassAttended = request.getParameter( "enrollmentFirstClassAttendedSchool${ i + 1 }" )
                    String lastClassAttended = request.getParameter( "enrollmentLastClassAttendedSchool${ i + 1 }" )

                    /*
                     * If the value of Enrollment Term parameter associated to the iterated School is "Enrollment Term", do not put it into the
                     * Enrollment/StudentDocument's enrollTermYear & enrollTermNo/lastEnrollmentTermYear & lastEnrollmentTermNo fields.
                     */
                    if( enrollmentTerm != null && !enrollmentTerm.equals( "Enrollment Term" ) ) {
                        String[] enrollmentTermComponents = enrollmentTerm.split( " " )
                        lastEnrollmentTermYear = Integer.parseInt( enrollmentTermComponents[0] )
                        lastEnrollmentTermNo = Integer.parseInt( enrollmentTermComponents[2] )
						lastEnrollmentDate = Term.findByTermSchoolAndTermNoAndYear( enrollmentSchool, lastEnrollmentTermNo, lastEnrollmentTermYear ).startDate
                        isEnrollmentTermEmpty = false
                    }

                    /*
                     * If the value of Leave Term parameter associated to the iterated School is "Leave Term", do not put it into the
                     * Enrollment/StudentDocument's leaveTermYear & leaveTermNo/lastEnrollmentLeaveTermYear & lastEnrollmentLeaveTermNo fields.
                     */
                    if( leaveTerm != null && !leaveTerm.equals( "Leave Term" ) ) {
                        String[] leaveTermComponents = leaveTerm.split( " " )
                        lastEnrollmentLeaveTermYear = Integer.parseInt( leaveTermComponents[0] )
                        lastEnrollmentLeaveTermNo = Integer.parseInt( leaveTermComponents[2] )
						lastEnrollmentLeaveTerm = Term.findByTermSchoolAndTermNoAndYear( enrollmentSchool, lastEnrollmentLeaveTermNo, lastEnrollmentLeaveTermYear )
                        isLeaveTermEmpty = false
                    }

                    /*
                     * If the value of First Class Attended parameter associated to the iterated School is "First Class Attended", do not put it into the
                     * Enrollment/StudentDocument's firstClassAttended/lastEnrollmentFirstClassAttended field.
                     */
                    if( firstClassAttended != null && !firstClassAttended.equals( "First Class Attended" ) ) {
                        lastEnrollmentFirstClassAttended = firstClassAttended
                        isFirstClassAttendedEmpty = false
                    }

                    /*
                     * If the value of Last Class Attended parameter associated to the iterated School is "Last Class Attended", do not put it into the
                     * Enrollment/StudentDocument's lastClassAttended/lastEnrollmentLastClassAttended field.
                     */
                    if( lastClassAttended != null && !lastClassAttended.equals( "Last Class Attended" ) ) {
                        lastEnrollmentLastClassAttended = lastClassAttended
                        isLastClassAttendedEmpty = false
                    }
                }
            }
            else {
                /* If the studentId parameter value is empty, raise an error. */
                if( params.studentId != null && !params.studentId.equals( "" ) )
                    studentId = params.studentId
                else
                    throw new EmptyRequiredFieldException( "Student", "studentId" )
                
                student = Student.findByStudentId( studentId )
                studentBackup = Student.findByStudentId( studentId )
                
                enrollment = Enrollment.findLastEnrollmentByStudentId( studentId )
                enrollmentBackup = Enrollment.findLastEnrollmentByStudentId( studentId )
                
                enrollmentSchool = enrollment.schoolName
                
                enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( studentId )
                enrollmentDocumentBackup = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( studentId )
				
				studentDocument = StudentDocument.findByStudentId( studentId )
				boardingFeesNumber = studentDocument.getOnlyField( "boardingFees" ).getNumber()
				classesAttendedText = studentDocument.getOnlyField( "classesAttended" ).getText()
				otherFeesNumber = studentDocument.getOnlyField( "otherFees" ).getNumber()
				paymentsNumber = studentDocument.getOnlyField( "payments" ).getNumber()
				termsEnrolledText = studentDocument.getOnlyField( "termsEnrolled" ).getText()
				tuitionFeesNumber = studentDocument.getOnlyField( "tuitionFees" ).getNumber()
                
                if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != studentDocument.getOnlyField( "lastUpdateDate" ).getDate() ||
                    ( params.lastUpdateUser?: "" ) != ( studentDocument.getFieldNames().contains( "lastUpdateUser" )? studentDocument.getOnlyField( "lastUpdateUser" ).getText(): "" ) )
                    throw new StaleRecordException( params.action, "Student" )
                
                studentDocumentBackup = StudentDocument.findByStudentId( studentId )
                
                String leaveTerm = request.getParameter( "leaveTerm" )
                String firstClassAttended = request.getParameter( "enrollmentFirstClassAttended" )
                String lastClassAttended = request.getParameter( "enrollmentLastClassAttended" )

                /* Obtain enrollment term information from the existing Enrollment entity. */
				lastEnrollmentDate = enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate()
                lastEnrollmentTermYear = enrollment.enrollTermYear
                lastEnrollmentTermNo = enrollment.enrollTermNo
                isEnrollmentTermEmpty = false

                /*
                 * If the value of Leave Term parameter associated to the iterated School is "Leave Term", do not put it into the
                 * Enrollment/StudentDocument's leaveTermYear & leaveTermNo/lastEnrollmentLeaveTermYear & lastEnrollmentLeaveTermNo fields.
                 */
                if( leaveTerm != null && !leaveTerm.equals( "Leave Term" ) ) {
                    String[] leaveTermComponents = leaveTerm.split( " " )
                    lastEnrollmentLeaveTermYear = Integer.parseInt( leaveTermComponents[0] )
                    lastEnrollmentLeaveTermNo = Integer.parseInt( leaveTermComponents[2] )
					lastEnrollmentLeaveTerm = Term.findByTermSchoolAndTermNoAndYear( enrollmentSchool, lastEnrollmentLeaveTermNo, lastEnrollmentLeaveTermYear )
                    isLeaveTermEmpty = false
                }
				
				if( !isLeaveTermEmpty ) {
					List<Entity> classTermsToRemove = new ArrayList()
					
					if( enrollment.leaveTermNo == null )
						classTermsToRemove = Term.findByTermSchoolAndTermRange( enrollment.schoolName, lastEnrollmentLeaveTerm, null, new Date() )
					else if( enrollment.leaveTermNo != lastEnrollmentLeaveTermNo || enrollment.leaveTermYear != lastEnrollmentLeaveTermYear ) {
						Entity oldLeaveTerm = Term.findByTermSchoolAndTermNoAndYear( enrollment.schoolName, enrollment.leaveTermNo, enrollment.leaveTermYear )
						
						if( oldLeaveTerm.startDate > lastEnrollmentLeaveTerm.startDate )
							classTermsToRemove = Term.findByTermSchoolAndTermRange( enrollment.schoolName, lastEnrollmentLeaveTerm, oldLeaveTerm, new Date() )
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
                 * Enrollment/StudentDocument's firstClassAttended/lastEnrollmentFirstClassAttended field.
                 */
                if( firstClassAttended != null && !firstClassAttended.equals( "First Class Attended" ) ) {
                    lastEnrollmentFirstClassAttended = firstClassAttended
                    isFirstClassAttendedEmpty = false
                }

                /*
                 * If the value of Last Class Attended parameter associated to the iterated School is "Last Class Attended", do not put it into the
                 * Enrollment/StudentDocument's lastClassAttended/lastEnrollmentLastClassAttended field.
                 */
                if( lastClassAttended != null && !lastClassAttended.equals( "Last Class Attended" ) ) {
                    lastEnrollmentLastClassAttended = lastClassAttended
                    isLastClassAttendedEmpty = false
                }
            }
            
            if( isEnrollmentTermEmpty && Enrollment.isRequired( "enrollTermYear" ) )
                throw new EmptyRequiredFieldException( "Enrollment", "enrollTermYear" )
                
            if( isEnrollmentTermEmpty && Enrollment.isRequired( "enrollTermNo" ) )
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
                if( lastEnrollmentLeaveTerm.startDate < lastEnrollmentDate )
                    throw new InvalidDateRangeException( "Enrollment Term - Leave Term", lastEnrollmentDate, lastEnrollmentLeaveTerm.startDate )
            }

            if( params.action.equals( "save" ) ) {
				
                /* Build a Student ID. */
                studentId = lastEnrollmentDate.format( "MMyy" )

                if( lastName == null )
                    studentId += firstName.toLowerCase()
                else
                    studentId += firstName.toLowerCase().substring( 0, 1 ) + lastName.toLowerCase()

                def baseStudentId = studentId

                for( int i = 1; Student.findByStudentId( studentId ) != null; i++ )
                    studentId = baseStudentId + i
                
				student = new Entity( "Student" )
                enrollment = new Entity( "Enrollment" )
            }
            
            /* Populate the Student fields. */
            student.studentId = studentId
            student.firstName = firstName
            student.lastName = lastName
            student.birthDate = birthDate
            student.village = village
            student.genderCode = genderCode
            student.specialInfo = specialInfo
            student.lastUpdateDate = new Date()
            
            if( user != null )
                student.lastUpdateUser = user.getEmail()
                
            student.save()
			
			String schoolPrivilege = UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), enrollmentSchool )?.privilege
            
            /*
             * Allow the user to save/edit only if the request comes from a test script or a user with Modify privilege for the Last Enrollment School.
             */
            if( /* Test Script */ user == null || schoolPrivilege?.equals( "Modify" ) ) {
                
				
                /* Populate the Enrollment fields. */
                enrollment.studentId = studentId
                enrollment.schoolName = enrollmentSchool
                enrollment.enrollTermYear = lastEnrollmentTermYear
                enrollment.enrollTermNo = lastEnrollmentTermNo
                enrollment.leaveTermYear = lastEnrollmentLeaveTermYear
                enrollment.leaveTermNo = lastEnrollmentLeaveTermNo
                enrollment.firstClassAttended = lastEnrollmentFirstClassAttended
                enrollment.lastClassAttended = lastEnrollmentLastClassAttended
                enrollment.leaveReasonCategory = enrollmentLeaveReasonCategory
                enrollment.leaveReason = enrollmentLeaveReason
                enrollment.lastUpdateDate = new Date()
				
				if( user != null )
					enrollment.lastUpdateUser = user.getEmail()
					
				enrollment.save()
				
				List<String> studentParams = request.getParameterNames().toList()
				
				for( String paramName: studentParams ) {
					if( paramName.startsWith( "classAttended" ) ) {
						classesAttendedBackup = ClassAttended.findByEnrollment( enrollment )
						classesAttendedBackup*.delete()
						break;
					}
				}
				
				int classAttendedIndex = 0
				
				for( String paramName: studentParams ) {
					
					if( paramName.startsWith( "classAttended" ) ) {
						
						if( classAttendedIndex == 0 ) {
							enrollment.firstClassAttended = request.getParameter( paramName )
							enrollment.lastClassAttended = request.getParameter( paramName )
							enrollment.save()
							
							boardingFeesNumber = 0.0
							classesAttendedText = ""
							termsEnrolledText = ""
							tuitionFeesNumber = 0.0
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
						
						classesAttended.add( classAttended )
						
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
                throw new AuthorizationException( enrollmentSchool )
			
			if( Class.findBySchoolNameAndClass( enrollment.schoolName, enrollment.firstClassAttended ).level > Class.findBySchoolNameAndClass( enrollment.schoolName, enrollment.lastClassAttended ).level )
				throw new InvalidClassLevelRangeException( "First Class Attended - Last Class Attended", enrollment.firstClassAttended, enrollment.lastClassAttended )
				
			/* Populate the EnrollmentDocument fields. */
            double feesDueNumber = tuitionFeesNumber + boardingFeesNumber + otherFeesNumber - paymentsNumber
			
			if( feesDueNumber < 0 )
				feesDueNumber = 0
			
            enrollmentDocBuilder.setId( "" + enrollment.getKey().getId() )
				.addField( Field.newBuilder().setName( "studentId" ).setAtom( studentId ) )
				.addField( Field.newBuilder().setName( "firstName" ).setText( firstName ) )
				.addField( Field.newBuilder().setName( "schoolName" ).setText( enrollmentSchool ) )
				.addField( Field.newBuilder().setName( "termsEnrolled" ).setText( termsEnrolledText ) )
				.addField( Field.newBuilder().setName( "enrollTermYear" ).setNumber( lastEnrollmentTermYear ) )
				.addField( Field.newBuilder().setName( "enrollTermNo" ).setNumber( lastEnrollmentTermNo ) )
				.addField( Field.newBuilder().setName( "enrollTermStartDate" ).setDate( lastEnrollmentDate ) )
				.addField( Field.newBuilder().setName( "leaveTermEndDate" ).setDate( lastEnrollmentLeaveTerm?.endDate?: Date.parse( "MMM d yyyy", "Dec 31 2999" ) ) )
				.addField( Field.newBuilder().setName( "classesAttended" ).setText( classesAttendedText ) )
				.addField( Field.newBuilder().setName( "firstClassAttended" ).setText( enrollment.firstClassAttended ) )
				.addField( Field.newBuilder().setName( "lastClassAttended" ).setText( enrollment.lastClassAttended ) )
				.addField( Field.newBuilder().setName( "tuitionFees" ).setNumber( tuitionFeesNumber ) )
				.addField( Field.newBuilder().setName( "boardingFees" ).setNumber( boardingFeesNumber ) )
				.addField( Field.newBuilder().setName( "otherFees" ).setNumber( otherFeesNumber ) )
				.addField( Field.newBuilder().setName( "payments" ).setNumber( paymentsNumber ) )
				.addField( Field.newBuilder().setName( "feesDue" ).setNumber( feesDueNumber ) )
				.addField( Field.newBuilder().setName( "lastUpdateDate" ).setDate( new Date() ) )
			
			if( lastName != null )
				enrollmentDocBuilder.addField( Field.newBuilder().setName( "lastName" ).setText( lastName ) )
					
			if( birthDate != null )
				enrollmentDocBuilder.addField( Field.newBuilder().setName( "birthDate" ).setDate( birthDate ) )
					
			if( village != null )
				enrollmentDocBuilder.addField( Field.newBuilder().setName( "village" ).setText( village ) )
					
			if( genderCode != null )
				enrollmentDocBuilder.addField( Field.newBuilder().setName( "genderCode" ).setText( genderCode ) )
					
			if( specialInfo != null )
				enrollmentDocBuilder.addField( Field.newBuilder().setName( "specialInfo" ).setText( specialInfo ) )
					
			if( lastEnrollmentLeaveTermYear != null )
				enrollmentDocBuilder.addField( Field.newBuilder().setName( "leaveTermYear" ).setNumber( lastEnrollmentLeaveTermYear ) )
					
			if( lastEnrollmentLeaveTermNo != null )
				enrollmentDocBuilder.addField( Field.newBuilder().setName( "leaveTermNo" ).setNumber( lastEnrollmentLeaveTermNo ) )
				
			if( enrollmentLeaveReasonCategory != null )
				enrollmentDocBuilder.addField( Field.newBuilder().setName( "leaveReasonCategory" ).setText( enrollmentLeaveReasonCategory ) )
				
			if( enrollmentLeaveReason != null )
				enrollmentDocBuilder.addField( Field.newBuilder().setName( "leaveReason" ).setText( enrollmentLeaveReason ) )
					
			if( user != null )
                enrollmentDocBuilder.addField( Field.newBuilder().setName( "lastUpdateUser" ).setText( user.getEmail() ) )
                
            enrollmentDocument = enrollmentDocBuilder.build()
			enrollmentIndex.put( enrollmentDocument )
			
			if( params.action == "save" ) {
				enrollmentMetaData = Enrollment.findMetaDataBySchoolName( enrollmentSchool )
				enrollmentMetaDataBackup = Enrollment.findMetaDataBySchoolName( enrollmentSchool )
				
				enrollmentMetaData.count = enrollmentMetaData.count + 1
				enrollmentMetaData.save()
			}
			
            /* Populate the StudentDocument fields. */
			studentDocBuilder.setId( "" + student.getKey().getId() )
				.addField( Field.newBuilder().setName( "studentId" ).setAtom( studentId ) )
				.addField( Field.newBuilder().setName( "firstName" ).setText( firstName ) )
				.addField( Field.newBuilder().setName( "lastEnrollmentSchool" ).setText( enrollmentSchool ) )
				.addField( Field.newBuilder().setName( "termsEnrolled" ).setText( termsEnrolledText ) )
				.addField( Field.newBuilder().setName( "lastEnrollmentTermYear" ).setNumber( lastEnrollmentTermYear ) )
				.addField( Field.newBuilder().setName( "lastEnrollmentTermNo" ).setNumber( lastEnrollmentTermNo ) )
				.addField( Field.newBuilder().setName( "lastEnrollmentTermStartDate" ).setDate(
							Term.findByTermSchoolAndTermNoAndYear(
								enrollmentSchool,
								lastEnrollmentTermNo,
								lastEnrollmentTermYear
							).startDate
						)
					)
				.addField( Field.newBuilder().setName( "classesAttended" ).setText( classesAttendedText ) )
				.addField( Field.newBuilder().setName( "lastEnrollmentFirstClassAttended" ).setText( enrollment.firstClassAttended ) )
				.addField( Field.newBuilder().setName( "lastEnrollmentLastClassAttended" ).setText( enrollment.lastClassAttended ) )
				.addField( Field.newBuilder().setName( "tuitionFees" ).setNumber( tuitionFeesNumber ) )
				.addField( Field.newBuilder().setName( "boardingFees" ).setNumber( boardingFeesNumber ) )
				.addField( Field.newBuilder().setName( "otherFees" ).setNumber( otherFeesNumber ) )
				.addField( Field.newBuilder().setName( "payments" ).setNumber( paymentsNumber ) )
				.addField( Field.newBuilder().setName( "feesDue" ).setNumber( feesDueNumber ) )
				.addField( Field.newBuilder().setName( "lastUpdateDate" ).setDate( new Date() ) )
			
			if( lastName != null )
				studentDocBuilder.addField( Field.newBuilder().setName( "lastName" ).setText( lastName ) )
					
			if( birthDate != null )
				studentDocBuilder.addField( Field.newBuilder().setName( "birthDate" ).setDate( birthDate ) )
					
			if( village != null )
				studentDocBuilder.addField( Field.newBuilder().setName( "village" ).setText( village ) )
					
			if( genderCode != null )
				studentDocBuilder.addField( Field.newBuilder().setName( "genderCode" ).setText( genderCode ) )
					
			if( specialInfo != null )
				studentDocBuilder.addField( Field.newBuilder().setName( "specialInfo" ).setText( specialInfo ) )
					
			if( lastEnrollmentLeaveTermYear != null )
				studentDocBuilder.addField( Field.newBuilder().setName( "lastEnrollmentLeaveTermYear" ).setNumber( lastEnrollmentLeaveTermYear ) )
					
			if( lastEnrollmentLeaveTermNo != null )
				studentDocBuilder.addField( Field.newBuilder().setName( "lastEnrollmentLeaveTermNo" ).setNumber( lastEnrollmentLeaveTermNo ) )
				
			if( enrollmentLeaveReasonCategory != null )
				studentDocBuilder.addField( Field.newBuilder().setName( "lastEnrollmentLeaveReasonCategory" ).setText( enrollmentLeaveReasonCategory ) )
				
			if( enrollmentLeaveReason != null )
				studentDocBuilder.addField( Field.newBuilder().setName( "lastEnrollmentLeaveReason" ).setText( enrollmentLeaveReason ) )
					
			if( user != null )
                studentDocBuilder.addField( Field.newBuilder().setName( "lastUpdateUser" ).setText( user.getEmail() ) )
                
            studentDocument = studentDocBuilder.build()
			studentIndex.put( studentDocument )
			
			if( params.action == "save" ) {
				studentMetaData = Student.findMetaDataBySchoolName( enrollmentSchool )
				studentMetaDataBackup = Student.findMetaDataBySchoolName( enrollmentSchool )
				
				studentMetaData.count = studentMetaData.count + 1
				studentMetaData.save()
			}
			
			parentBackupList = new ArrayList<Entity>()
			
			/* Iterate through each ParentalRelationship. */
			ParentalRelationship.list().each() {
				
				/* Create an AnonymousParentalRelationship for each ParentalRelationship. */
				if( params.action.equals( "save" ) )
					anonymousParentalRelationship = new Entity( "AnonymousParentalRelationship", student.getKey() )
				else {
					anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( studentId, it.parentalRelationship )
					parentBackupList.add( AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( studentId, it.parentalRelationship ) )
				}
				
				String deceasedInd = request.getParameter( "anonymous${ it.parentalRelationship }DeceasedInd" )
				
				if( deceasedInd == "N" )
					anonymousParentalRelationship.deceasedInd = "N"
				else
					anonymousParentalRelationship.deceasedInd = deceasedInd? "Y": "N"
					
				anonymousParentalRelationship.studentId = studentId
				anonymousParentalRelationship.parentalRelationship = it.parentalRelationship
				anonymousParentalRelationship.lastUpdateDate = new Date()
				
				if( user != null )
					anonymousParentalRelationship.lastUpdateUser = user.getEmail()
					
				anonymousParentalRelationship.save()
			}
			
			if( params.action == "edit" ) {
				Relationship.findByStudentId( studentId ).each(
					{
						String deceasedInd = request.getParameter( "${ it.parentId }DeceasedInd" )? "Y": "N"
						Entity parent = Parent.findByParentId( it.parentId )
						parentBackupList.add( Parent.findByParentId( it.parentId ) )
						
						if( parent.deceasedInd != deceasedInd ) {
							parent.deceasedInd = deceasedInd
							parent.lastUpdateDate = new Date()
							parent.lastUpdateUser = user?.getEmail()
						}
						
						parent.save()
					}
				)
			}
			
			EnrollmentDocument.deleteMemcache()
            StudentDocument.deleteMemcache()
            
            /* Respond with the HTML code that is required to display the new StudentDocument within an accordion. */
            println ListItemFormatter.getStudentListItem( studentDocument, student )
        }
        catch( Exception e ) {
        	e.printStackTrace()
            if( params.action.equals( "save" ) ) {
                
                /* If the save fails, delete the records that have been created during the save process. */
                try{
                    if( studentDocument != null ) {
                        studentIndex.delete( studentDocument.getId() )
                        StudentDocument.deleteMemcache()
                    }
                } catch( Exception deleteException ) {}
				
				try { studentMetaDataBackup?.save() } catch( Exception backupRestoreException ) {}
				
				classesAttended.each(
					{
						try { it.delete() } catch( Exception deleteException ) {}
					}
				)
                
				try{
                    if( enrollmentDocument != null ) {
                        enrollmentIndex.delete( enrollmentDocument.getId() )
                        EnrollmentDocument.deleteMemcache()
                    }
                } catch( Exception deleteException ) {}
				
				try { enrollmentMetaDataBackup?.save() } catch( Exception backupRestoreException ) {}

                try{ if( enrollment != null ) enrollment.delete() } catch( Exception deleteException ) {}
                
				AnonymousParentalRelationship.findByStudentId( studentId ).each() {
                    it.delete()
                }
                
				try{ if( student != null ) student.delete() } catch( Exception deleteException ) {}

                /* Respond with an Internal Server error message if the save fails. */
                response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
                response.setHeader( "Response-Phrase", e.getMessage() )
            }
            else {
                
                /* Restore the old values of Student, Enrollment, and Anonymous Parental Relationships. */
                try{ studentBackup.save() } catch( Exception backupRestoreException ) {}
                try{ enrollmentBackup.save() } catch( Exception backupRestoreException ) {}
				
				classesAttended.each(
					{
						try { it.save() } catch( Exception saveException ) {}
					}
				)
				
				classesAttendedBackup.each(
					{
						try { it.save() } catch( Exception saveException ) {}
					}
				)
                
                parentBackupList.each() {
                    try{ it.save() } catch( Exception backupRestoreException ) {}
                }
                
                try {
                	if( enrollmentDocumentBackup != null ) {
                		enrollmentIndex.put( enrollmentDocumentBackup )
                		EnrollmentDocument.deleteMemcache()
                	}
                }
                catch( Exception backupRestoreException ) {}
                
				try {
					if( studentDocumentBackup != null ) {
						studentIndex.put( studentDocumentBackup )
						StudentDocument.deleteMemcache()
					}
				}
				catch( Exception backupRestoreException ) {}
                
                /* Respond with an Internal Server error message if the save fails. */
                response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
                response.setHeader( "Response-Phrase", e.getMessage() )
            }
        }
    }
}