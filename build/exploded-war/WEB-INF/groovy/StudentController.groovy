/*
Document   : Groovlet file: StudentController.groovy
Created on : Mon Jun 11 21:41:00 CST 2012
Author     : awijasa
 */

import com.google.appengine.api.datastore.Entity
import data.AnonymousParentalRelationship
import data.AuthorizationException
import data.Class
import data.ClassAttended
import data.ClassFees
import data.EmptyRequiredFieldException
import data.Enrollment
import data.EnrollmentWithStudent
import data.EnrollmentWithTerms
import data.EntityIsUsedException
import data.InvalidClassLevelRangeException
import data.InvalidDateRangeException
import data.Parent
import data.ParentalRelationship
import data.Relationship
import data.School
import data.StaleRecordException
import data.Student
import data.StudentWithLastEnrollment
import data.Term
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
    /*
     * Delete a StudentWithLastEnrollment entity identified by the id parameter and its associated Enrollment, EnrollmentWithStudent,
     * AnonymousParentalRelationship, and Student entities when the action parameter value is "delete".
     */
    if( params.action.equals( "delete" ) ) {
		List<Entity> anonymousParentalRelationships
		List<Entity> classesAttended
		List<Entity> enrollments
        List<Entity> enrollmentsWithStudent
		List<Entity> enrollmentsWithTerms
		List<Entity> relationships
		Entity student
        Entity studentWithLastEnrollment
        
        try {
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
            
            student.delete()
            
            enrollmentsWithStudent = EnrollmentWithStudent.findByStudentId( params.studentId )
            enrollmentsWithStudent*.delete()
			
			enrollmentsWithTerms = EnrollmentWithTerms.findByStudentId( params.studentId )
			enrollmentsWithTerms*.delete()
            
            studentWithLastEnrollment = datastore.get( "StudentWithLastEnrollment", Long.parseLong( params.id ) )
            
            if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != studentWithLastEnrollment.lastUpdateDate ||
                ( params.lastUpdateUser?: "" ) != ( studentWithLastEnrollment.lastUpdateUser?: "" ) )
                throw new StaleRecordException( params.action, "Student" )
            
            studentWithLastEnrollment.delete()
            
            StudentWithLastEnrollment.findByLimitAndOffset( 0, Integer.parseInt( params.nextTwentyOffset ) - 1 ).each() {
                /*
                 * Respond with the HTML code that is required to display the StudentWithLastEnrollment entity that can fill the empty space that resulted
                 * from the deleted record within an accordion.
                 */
                println ListItemFormatter.getStudentListItem( it, null )
            }
            
            EnrollmentWithStudent.deleteMemcache()
            StudentWithLastEnrollment.deleteMemcache()
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
            
			enrollmentsWithStudent.each(
				{
					try{ it.save() } catch( Exception saveException ) {}
				}
			)
			
			enrollmentsWithTerms.each(
				{
					try { it.save() } catch( Exception saveException ) {}
				}
			)
			
            try { studentWithLastEnrollment.save() } catch( Exception saveException ) {}
            
            /* Respond with an Internal Server error message if the delete fails. */
            response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
            response.setHeader( "Response-Phrase", e.getMessage() )
        }
    }
    
    /*
     * Save a new Student entity and its new Enrollment, AnonymousParentalRelationship, and StudentWithLastEnrollment entities when the action parameter
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
        String enrollmentLeaveReasonCategory
        String enrollmentLeaveReason
        
        Entity student
        Entity studentBackup
        String studentId
        Entity anonymousParentalRelationship
        Entity enrollment
        Entity enrollmentBackup
        Entity enrollmentWithStudent
        Entity enrollmentWithStudentBackup
		Entity enrollmentWithTerms
		Entity enrollmentWithTermsBackup
		List<Entity> classesAttended = new ArrayList()
		List<Entity> classesAttendedBackup = new ArrayList()
        List<Entity> parentBackupList = new ArrayList()
        Entity studentWithLastEnrollment
        Entity studentWithLastEnrollmentBackup
        
        try {
            
            /* If the firstName parameter value is "First Name" or empty, do not put it into the Student/StudentWithLastEnrollment entity's firstName field. */
            if( params.firstName != null && !params.firstName.equals( "First Name" ) && !params.firstName.equals( "" ) )
                firstName = params.firstName
                
            /* If the firstName parameter value is "First Name" or empty, and firstName is a required Student entity's field, throw an EmptyRequiredFieldException. */
            else if( Student.isRequired( "firstName" ) )
                throw new EmptyRequiredFieldException( "Student", "firstName" )

            /* If the lastName parameter value is "Last Name" or empty, do not put it into the Student/StudentWithLastEnrollment entity's lastName field. */
            if( params.lastName != null && !params.lastName.equals( "Last Name" ) && !params.lastName.equals( "" ) )
                lastName = params.lastName
                
            /* If the lastName parameter value is "Last Name" or empty, and lastName is a required Student entity's field, throw an EmptyRequiredFieldException. */
            else if( Student.isRequired( "lastName" ) )
                throw new EmptyRequiredFieldException( "Student", "lastName" )

            /* If the birthDate parameter value is "Birth Date" or empty, do not put it into the Student/StudentWithLastEnrollment entity's birthDate field. */
            if( params.birthDate != null && !params.birthDate.equals( "Birth Date" ) && !params.birthDate.equals( "" ) )
                birthDate = Date.parse( "MMM d yy", params.birthDate )
                
            /* If the birthDate parameter value is "Birth Date" or empty, and birthDate is a required Student entity's field, throw an EmptyRequiredFieldException. */
            else if( Student.isRequired( "birthDate" ) )
                throw new EmptyRequiredFieldException( "Student", "birthDate" )

            /* If the village parameter value is "Village" or empty, do not put it into the Student/StudentWithLastEnrollment entity's village field. */
            if( params.village != null && !params.village.equals( "Village" ) && !params.village.equals( "" ) )
                village = params.village
            
            /* If the village parameter value is "Village" or empty, and village is a required Student entity's field, throw an EmptyRequiredFieldException. */
            else if( Student.isRequired( "village" ) )
                throw new EmptyRequiredFieldException( "Student", "village" )

            /* If the genderCode parameter value is "Gender" or empty, do not put it into the Student/StudentWithLastEnrollment entity's genderCode field. */
            if( params.genderCode != null && !params.genderCode.equals( "Gender" ) && !params.genderCode.equals( "" ) )
                genderCode = params.genderCode
                
            /* If the genderCode parameter value is "Gender" or empty, and genderCode is a required Student entity's field, throw an EmptyRequiredFieldException. */
            else if( Student.isRequired( "genderCode" ) )
                throw new EmptyRequiredFieldException( "Student", "genderCode" )

            /* If the specialInfo parameter value is "Special Info/Condition" or empty, do not put it into the Student/StudentWithLastEnrollment entity's specialInfo field. */
            if( params.specialInfo != null && !params.specialInfo.equals( "Special Info/Condition" ) && !params.specialInfo.equals( "" ) )
                specialInfo = params.specialInfo
                
            /* If the specialInfo parameter value is "Special Info/Condition" or empty, and specialInfo is a required Student entity's field, throw an EmptyRequiredFieldException. */
            else if( Student.isRequired( "specialInfo" ) )
                throw new EmptyRequiredFieldException( "Student", "specialInfo" )

            if( params.action.equals( "save" ) ) {
            
                /* If the enrollmentSchool parameter value is "School" or empty, do not put it into the Enrollment/StudentWithLastEnrollment entity's schoolName/lastEnrollmentSchool field. */
                if( params.enrollmentSchool != null && !params.enrollmentSchool.equals( "School" ) && !params.enrollmentSchool.equals( "" ) )
                    enrollmentSchool = params.enrollmentSchool

                /* If the enrollmentSchool parameter value is "School" or empty, and schoolName is a required Enrollment entity's field, throw an EmptyRequiredFieldException. */
                else if( Enrollment.isRequired( "schoolName" ) )
                    throw new EmptyRequiredFieldException( "Enrollment", "schoolName" )
            }

            /* If the enrollmentLeaveReasonCategory parameter value is "Leave Reason Category" or empty, do not put it into the Enrollment/StudentWithLastEnrollment entity's leaveReasonCategory/lastEnrollmentLeaveReasonCategory field. */
            if( params.enrollmentLeaveReasonCategory != null && !params.enrollmentLeaveReasonCategory.equals( "Leave Reason Category" ) && !params.enrollmentLeaveReasonCategory.equals( "" ) )
                enrollmentLeaveReasonCategory = params.enrollmentLeaveReasonCategory
            
            /* If the enrollmentLeaveReasonCategory parameter value is "Leave Reason Category" or empty, and leaveReasonCategory is a required Enrollment entity's field, throw an EmptyRequiredFieldException. */
            else if( Enrollment.isRequired( "leaveReasonCategory" ) )
                throw new EmptyRequiredFieldException( "Enrollment", "leaveReasonCategory" )

            /* If the enrollmentLeaveReason parameter value is "Leave Reason" or empty, do not put it into the Enrollment/StudentWithLastEnrollment entity's leaveReason/lastEnrollmentLeaveReason field. */
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
                     * Enrollment/StudentWithLastEnrollment entity's enrollTermYear & enrollTermNo/lastEnrollmentTermYear & lastEnrollmentTermNo fields.
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
                     * Enrollment/StudentWithLastEnrollment entity's leaveTermYear & leaveTermNo/lastEnrollmentLeaveTermYear & lastEnrollmentLeaveTermNo fields.
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
                     * Enrollment/StudentWithLastEnrollment entity's firstClassAttended/lastEnrollmentFirstClassAttended field.
                     */
                    if( firstClassAttended != null && !firstClassAttended.equals( "First Class Attended" ) ) {
                        lastEnrollmentFirstClassAttended = firstClassAttended
                        isFirstClassAttendedEmpty = false
                    }

                    /*
                     * If the value of Last Class Attended parameter associated to the iterated School is "Last Class Attended", do not put it into the
                     * Enrollment/StudentWithLastEnrollment entity's lastClassAttended/lastEnrollmentLastClassAttended field.
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
                
                enrollmentWithStudent = EnrollmentWithStudent.findLastEnrollmentWithStudentByStudentId( studentId )
                enrollmentWithStudentBackup = EnrollmentWithStudent.findLastEnrollmentWithStudentByStudentId( studentId )
				
				enrollmentWithTerms = EnrollmentWithTerms.findLastEnrollmentWithTermsByStudentId( studentId )
				enrollmentWithTermsBackup = EnrollmentWithTerms.findLastEnrollmentWithTermsByStudentId( studentId )
                
                studentWithLastEnrollment = StudentWithLastEnrollment.findByStudentId( studentId )
                
                if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != studentWithLastEnrollment.lastUpdateDate ||
                    ( params.lastUpdateUser?: "" ) != ( studentWithLastEnrollment.lastUpdateUser?: "" ) )
                    throw new StaleRecordException( params.action, "Student" )
                
                studentWithLastEnrollmentBackup = StudentWithLastEnrollment.findByStudentId( studentId )
                
                String leaveTerm = request.getParameter( "leaveTerm" )
                String firstClassAttended = request.getParameter( "enrollmentFirstClassAttended" )
                String lastClassAttended = request.getParameter( "enrollmentLastClassAttended" )

                /* Obtain enrollment term information from the existing Enrollment entity. */
				lastEnrollmentDate = enrollmentWithTerms.enrollDate
                lastEnrollmentTermYear = enrollment.enrollTermYear
                lastEnrollmentTermNo = enrollment.enrollTermNo
                isEnrollmentTermEmpty = false

                /*
                 * If the value of Leave Term parameter associated to the iterated School is "Leave Term", do not put it into the
                 * Enrollment/StudentWithLastEnrollment entity's leaveTermYear & leaveTermNo/lastEnrollmentLeaveTermYear & lastEnrollmentLeaveTermNo fields.
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
                 * Enrollment/StudentWithLastEnrollment entity's firstClassAttended/lastEnrollmentFirstClassAttended field.
                 */
                if( firstClassAttended != null && !firstClassAttended.equals( "First Class Attended" ) ) {
                    lastEnrollmentFirstClassAttended = firstClassAttended
                    isFirstClassAttendedEmpty = false
                }

                /*
                 * If the value of Last Class Attended parameter associated to the iterated School is "Last Class Attended", do not put it into the
                 * Enrollment/StudentWithLastEnrollment entity's lastClassAttended/lastEnrollmentLastClassAttended field.
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
                enrollmentWithStudent = new Entity( "EnrollmentWithStudent" )
				enrollmentWithTerms = new Entity( "EnrollmentWithTerms" )
                studentWithLastEnrollment = new Entity( "StudentWithLastEnrollment" )
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
				
			/* Populate the EnrollmentWithStudent fields. */
            enrollmentWithStudent.studentId = studentId
            enrollmentWithStudent.firstName = firstName
            enrollmentWithStudent.lastName = lastName
            enrollmentWithStudent.birthDate = birthDate
            enrollmentWithStudent.village = village
            enrollmentWithStudent.genderCode = genderCode
            enrollmentWithStudent.specialInfo = specialInfo
            enrollmentWithStudent.schoolName = enrollmentSchool
            enrollmentWithStudent.enrollTermYear = lastEnrollmentTermYear
            enrollmentWithStudent.enrollTermNo = lastEnrollmentTermNo
            enrollmentWithStudent.leaveTermYear = lastEnrollmentLeaveTermYear
            enrollmentWithStudent.leaveTermNo = lastEnrollmentLeaveTermNo
            enrollmentWithStudent.firstClassAttended = enrollment.firstClassAttended
            enrollmentWithStudent.lastClassAttended = enrollment.lastClassAttended
            enrollmentWithStudent.leaveReasonCategory = enrollmentLeaveReasonCategory
            enrollmentWithStudent.leaveReason = enrollmentLeaveReason
            enrollmentWithStudent.lastUpdateDate = new Date()
            enrollmentWithStudent.lastUpdateUser = user?.getEmail()
            
            enrollmentWithStudent.save()
			
			/* Populate the EnrollmentWithTerms fields. */
			enrollmentWithTerms.studentId = studentId
			enrollmentWithTerms.schoolName = enrollmentSchool
			enrollmentWithTerms.enrollTermYear = lastEnrollmentTermYear
			enrollmentWithTerms.enrollTermNo = lastEnrollmentTermNo
			enrollmentWithTerms.enrollDate = lastEnrollmentDate
			enrollmentWithTerms.leaveTermYear = lastEnrollmentLeaveTermYear
			enrollmentWithTerms.leaveTermNo = lastEnrollmentLeaveTermNo
			enrollmentWithTerms.leaveDate = lastEnrollmentLeaveTerm?.endDate?: Date.parse( "MMM d yyyy", "Dec 31 2999" )
			enrollmentWithTerms.firstClassAttended = enrollment.firstClassAttended
			enrollmentWithTerms.lastClassAttended = enrollment.lastClassAttended
			enrollmentWithTerms.leaveReasonCategory = enrollmentLeaveReasonCategory
			enrollmentWithTerms.leaveReason = enrollmentLeaveReason
			enrollmentWithTerms.lastUpdateDate = new Date()
			enrollmentWithTerms.lastUpdateUser = user?.getEmail()
			
			enrollmentWithTerms.save()
			
            /* Populate the StudentWithLastEnrollment fields. */
            studentWithLastEnrollment.studentId = studentId
            studentWithLastEnrollment.firstName = firstName
            studentWithLastEnrollment.lastName = lastName
            studentWithLastEnrollment.birthDate = birthDate
            studentWithLastEnrollment.village = village
            studentWithLastEnrollment.genderCode = genderCode
            studentWithLastEnrollment.specialInfo = specialInfo
            studentWithLastEnrollment.lastEnrollmentSchool = enrollmentSchool
            studentWithLastEnrollment.lastEnrollmentTermYear = lastEnrollmentTermYear
            studentWithLastEnrollment.lastEnrollmentTermNo = lastEnrollmentTermNo
            studentWithLastEnrollment.lastEnrollmentLeaveTermYear = lastEnrollmentLeaveTermYear
            studentWithLastEnrollment.lastEnrollmentLeaveTermNo = lastEnrollmentLeaveTermNo
            studentWithLastEnrollment.lastEnrollmentFirstClassAttended = enrollment.firstClassAttended
            studentWithLastEnrollment.lastEnrollmentLastClassAttended = enrollment.lastClassAttended
            studentWithLastEnrollment.lastEnrollmentLeaveReasonCategory = enrollmentLeaveReasonCategory
            studentWithLastEnrollment.lastEnrollmentLeaveReason = enrollmentLeaveReason
            studentWithLastEnrollment.lastUpdateDate = new Date()
            
            if( user != null )
                studentWithLastEnrollment.lastUpdateUser = user.getEmail()
                
            studentWithLastEnrollment.save()
			
			parentBackupList = new ArrayList<Entity>()
			
			/* Iterate through each ParentalRelationship. */
			ParentalRelationship.list().each() {
				
				/* Create an AnonymousParentalRelationship for each ParentalRelationship. */
				if( params.action.equals( "save" ) )
					anonymousParentalRelationship = new Entity( "AnonymousParentalRelationship", studentWithLastEnrollment.getKey() )
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
			
			EnrollmentWithStudent.deleteMemcache()
            StudentWithLastEnrollment.deleteMemcache()
            
            /* Respond with the HTML code that is required to display the new StudentWithLastEnrollment entity within an accordion. */
            println ListItemFormatter.getStudentListItem( studentWithLastEnrollment, enrollment )
        }
        catch( Exception e ) {
            
            if( params.action.equals( "save" ) ) {
                
                /* If the save fails, delete the records that have been created during the save process. */
                try{
                    if( studentWithLastEnrollment != null ) {
                        studentWithLastEnrollment.delete()
                        StudentWithLastEnrollment.deleteMemcache()
                    }
                } catch( Exception deleteException ) {}
				
				classesAttended.each(
					{
						try { it.delete() } catch( Exception deleteException ) {}
					}
				)
                
				try { enrollmentWithTerms?.delete() } catch( Exception deleteException ) {}
				
                try { enrollmentWithStudent?.delete() } catch( Exception deleteException ) {}

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
                
                try { enrollmentWithStudentBackup.save() } catch( Exception backupRestoreException ) {}
				try { enrollmentWithTermsBackup.save() } catch( Exception backupRestoreException ) {}
                try { studentWithLastEnrollmentBackup.save() } catch( Exception backupRestoreException ) {}
                
                /* Respond with an Internal Server error message if the save fails. */
                response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
                response.setHeader( "Response-Phrase", e.getMessage() )
            }
        }
    }
}