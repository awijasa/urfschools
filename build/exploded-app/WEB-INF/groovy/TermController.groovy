/*
Document   : Groovlet file: TermController.groovy
Created on : Wed Aug 8 19:42:00 CST 2012
Author     : awijasa
 */

import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.search.Document
import com.google.appengine.api.search.Field
import com.google.appengine.api.search.Index
import data.AuthorizationException
import data.Class
import data.ClassAttended
import data.ClassFees
import data.DuplicateEntityException
import data.EmptyRequiredFieldException
import data.EnrollmentDocument
import data.EntityIsUsedException
import data.InvalidDateRangeException
import data.InvalidValueException
import data.OverlappedTermException
import data.StaleRecordException
import data.StudentDocument
import data.Term
import data.URFUser
import data.UserPrivilege
import formatter.ListItemFormatter
import java.text.DecimalFormat

/* Handle create, update, and delete operations on the Term entity. */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the TermController. */
if( user == null && !localMode )
    response.sendError( response.SC_UNAUTHORIZED )
else {
    
    /* Allow the user to delete, edit, and save only if the request comes from a test script or an admin with Modify privilege. */
    if( user == null || URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
    
        /* Delete a Term entity identified by the id parameter when the action parameter value is "delete". */
        if( params.action.equals( "delete" ) ) {
            List<Entity> classFeesBackup = new ArrayList()
			Entity term
            
            try {
                term = datastore.get( "Term", Long.parseLong( params.id ) )
                def termSchool = term.termSchool

                if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != term.lastUpdateDate ||
                    ( params.lastUpdateUser?: "" ) != ( term.lastUpdateUser?: "" ) )
                    throw new StaleRecordException( params.action, "Term" )
                
                if( Term.isUsed( term ) )
                    throw new EntityIsUsedException( term, Term.getPrimaryKey() )

                /* Allow the user to delete only if the request comes from a test script or a user with Modify privilege for the Term's School. */
                if( /* Test Script */ user == null ||
                    /* User with Modify privilege for the Term's School. */
                    UserPrivilege.findByUserEmailAndSchoolName( user.getEmail(), termSchool )?.privilege?.equals( "Modify" ) ) {
					
					Term.findByLimitAndOffset( 0, Integer.parseInt( params.nextTwentyOffset ) ).each() {
						/*
						 * Respond with the HTML code that is required to display the Term entity that can fill the empty space that resulted from the deleted record
						 * within an accordion.
						 */
						println ListItemFormatter.getTermListItem( it )
					}
					
					ClassFees.findBySchoolNameAndTermNoAndTermYear( term.termSchool, term.termNo, term.year ).each(
						{
							if( ClassFees.isUsed( it ) )
								throw new EntityIsUsedException( it, ClassFees.getPrimaryKey() )
								
							classFeesBackup.add( it )
							it.delete()
						}
					)
                    
					term.delete()
                }
                else
                    throw new AuthorizationException( termSchool )

                Term.deleteMemcache( termSchool )
            }
            catch( Exception e ) {
                term?.save()
				
				classFeesBackup.each(
					{
						try { it.save() } catch( Exception saveException ) {}
					}
				)
                
                /* Respond with an Internal Server error message if the delete fails. */
                response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
                response.setHeader( "Response-Phrase", e.getMessage() )
            }
        }

        /* Edit an existing Term entity when the action parameter value is "edit" or Save a new Term entity when the action parameter value is "save". */
        else if( params.action.equals( "edit" ) || params.action.equals( "save" ) ) {
			List<Entity> classesAttendedBackup = new ArrayList()
			List<Entity> classFeesBackup = new ArrayList()
			Map<String, Document> enrollmentDocumentBackups = new LinkedHashMap()
			Index enrollmentIndex = search.index( "Enrollment" )
			List<Entity> newClassFees = new ArrayList()
			Map<String, Document> studentDocumentBackups = new LinkedHashMap()
			Index studentIndex = search.index( "Student" )
			Entity term
			Entity termBackup
			
            try {
                if( params.action.equals( "edit" ) ) {
					termBackup = datastore.get( "Term", Long.parseLong( params.id ) )
                    term = datastore.get( "Term", Long.parseLong( params.id ) )
                    
                    if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != term.lastUpdateDate ||
                        ( params.lastUpdateUser?: "" ) != ( term.lastUpdateUser?: "" ) )
                        throw new StaleRecordException( params.action, "Term" )
                }
                else {
                    term = new Entity( "Term" )

                    /* If the termSchool parameter value is "School" or empty, do not put it into the Term entity's termSchool field. */
                    if( params.termSchool != null && !params.termSchool.equals( "School" ) && !params.termSchool.equals( "" ) )
                        term.termSchool = params.termSchool

                    /* If the termSchool parameter value is "School" or empty, and termSchool is a required Term entity's field, throw an EmptyRequiredFieldException. */
                    else if( Term.isRequired( "termSchool" ) )
                        throw new EmptyRequiredFieldException( "Term", "termSchool" )

                    /* If the termNo parameter value is "Term No" or empty, do not put it into the Term entity's termNo field. */
                    if( params.termNo != null && !params.termNo.equals( "Term No" ) && !params.termNo.equals( "" ) )
                        term.termNo = Integer.parseInt( params.termNo )

                    /* If the termNo parameter value is "Term No" or empty, and termNo is a required Term entity's field, throw an EmptyRequiredFieldException. */
                    else if( Term.isRequired( "termNo" ) )
                        throw new EmptyRequiredFieldException( "Term", "termNo" )

                    /* If the termNo parameter value is less than zero, throw an InvalidValueException. */
                    if( ( term.termNo?: 0 ) < 0 )
                        throw new InvalidValueException( "Term No", term.termNo )
                    
                    /* If the year parameter value is "Year" or empty, do not put it into the Term entity's year field. */
                    if( params.year != null && !params.year.equals( "Year" ) && !params.year.equals( "" ) )
                        term.year = Integer.parseInt( params.year )

                    /* If the termNo parameter value is "Term No" or empty, and termNo is a required Term entity's field, throw an EmptyRequiredFieldException. */
                    else if( Term.isRequired( "year" ) )
                        throw new EmptyRequiredFieldException( "Term", "year" )
                        
                    /* If the year parameter value is less than one, throw an InvalidValueException. */
                    if( ( term.year?: 1 ) < 1 )
                        throw new InvalidValueException( "Year", term.year )
                }

                /* If the startDate parameter value is "Start Date" or empty, do not put it into the Term entity's startDate field. */
                if( params.startDate != null && !params.startDate.equals( "Start Date" ) && !params.startDate.equals( "" ) )
                    term.startDate = Date.parse( "MMM d yy", params.startDate )

                /* If the startDate parameter value is "Start Date" or empty, and startDate is a required Term entity's field, throw an EmptyRequiredFieldException. */
                else if( Term.isRequired( "startDate" ) )
                    throw new EmptyRequiredFieldException( "Term", "startDate" )

                /* If the endDate parameter value is "End Date" or empty, do not put it into the Term entity's endDate field. */
                if( params.endDate != null && !params.endDate.equals( "End Date" ) && !params.endDate.equals( "" ) ) {
                    def endDate = Date.parse( "MMM d yy", params.endDate )

                    if( endDate >= term.startDate )
                        term.endDate = Date.parse( "MMM d yy", params.endDate )
                    else
                        throw new InvalidDateRangeException( "Term", term.startDate, endDate )
                }

                /* If the endDate parameter value is "End Date" or empty, and endDate is a required Term entity's field, throw an EmptyRequiredFieldException. */
                else if( Term.isRequired( "endDate" ) )
                    throw new EmptyRequiredFieldException( "Term", "endDate" )

                term.lastUpdateDate = new Date()

                if( user != null )
                    term.lastUpdateUser = user.getEmail()

                if( params.action.equals( "edit" ) || Term.findByTermSchoolAndTermNoAndYear( term.termSchool, term.termNo, term.year ) == null ) {
                    def overlappedTerm = Term.findOverlappedTerm( term )

                    if( overlappedTerm == null ) {
                        
                        /*
                         * Allow the user to edit and save only if the request comes from a test script or a user with Modify privilege for the Term's School.
                         */
                        if( /* Test Script */ user == null ||
                            /* User with Modify privilege for the Term's School */
                            UserPrivilege.findByUserEmailAndSchoolName( user.getEmail(), term.termSchool )?.privilege?.equals( "Modify" ) ) {
                            term.save()
                        }
                        else
                            throw new AuthorizationException( term.termSchool )
                    }
                    else
                        throw new OverlappedTermException( overlappedTerm )
                }
                else
                    throw new DuplicateEntityException( term, ["termSchool", "termNo", "year"] )
					
				DecimalFormat currencyFormat = new DecimalFormat( "#,##0.##" )
					
				if( params.action == "save" ) {
					Class.findBySchoolName( term.termSchool ).each(
						{
							Entity classFees = new Entity( "ClassFees", term.getKey() )
							
							/* If the termSchool parameter value is "School" or empty, do not put it into the ClassFees entity's schoolName field. */
							if( params.termSchool != null && !params.termSchool.equals( "School" ) && !params.termSchool.equals( "" ) )
								classFees.schoolName = params.termSchool
			
							/* If the termSchool parameter value is "School" or empty, and schoolName is a required ClassFees entity's field, throw an EmptyRequiredFieldException. */
							else if( ClassFees.isRequired( "schoolName" ) )
								throw new EmptyRequiredFieldException( "ClassFees", "schoolName" )
								
							/* If the Class.class property value is empty, do not put it into the ClassFees entity's class field. */
							if( ( it.getProperty( "class" )?: "" ) != "" )
								classFees.setProperty( "class", it.getProperty( "class" ) )
			
							/* If the Class.class parameter value is empty, and class is a required ClassFees entity's field, throw an EmptyRequiredFieldException. */
							else if( ClassFees.isRequired( "class" ) )
								throw new EmptyRequiredFieldException( "ClassFees", "class" )
								
							/* If the Class.level property value is empty, do not put it into the ClassFees entity's classLevel field. */
							if( ( it.level?: "" ) != "" )
								classFees.classLevel = it.level
			
							/* If the Class.level parameter value is empty, and classLevel is a required ClassFees entity's field, throw an EmptyRequiredFieldException. */
							else if( ClassFees.isRequired( "classLevel" ) )
								throw new EmptyRequiredFieldException( "ClassFees", "classLevel" )
							
							/* If the termNo parameter value is "Term No" or empty, do not put it into the ClassFees entity's termNo field. */
							if( params.termNo != null && !params.termNo.equals( "Term No" ) && !params.termNo.equals( "" ) )
								classFees.termNo = Integer.parseInt( params.termNo )
		
							/* If the termNo parameter value is "Term No" or empty, and termNo is a required ClassFees entity's field, throw an EmptyRequiredFieldException. */
							else if( ClassFees.isRequired( "termNo" ) )
								throw new EmptyRequiredFieldException( "ClassFees", "termNo" )
		
							/* If the termNo parameter value is less than zero, throw an InvalidValueException. */
							if( ( classFees.termNo?: 0 ) < 0 )
								throw new InvalidValueException( "Term No", classFees.termNo )
							
							/* If the year parameter value is "Year" or empty, do not put it into the ClassFees entity's termYear field. */
							if( params.year != null && !params.year.equals( "Year" ) && !params.year.equals( "" ) )
								classFees.termYear = Integer.parseInt( params.year )
		
							/* If the termNo parameter value is "Term No" or empty, and termNo is a required Term entity's field, throw an EmptyRequiredFieldException. */
							else if( ClassFees.isRequired( "termYear" ) )
								throw new EmptyRequiredFieldException( "ClassFees", "termYear" )
								
							/* If the year parameter value is less than one, throw an InvalidValueException. */
							if( ( classFees.termYear?: 1 ) < 1 )
								throw new InvalidValueException( "Year", classFees.termYear )
								
							String tuitionFeeParamName = "tuitionFee${ term.termSchool }${ it.getProperty( "class" ) }"
							
							/* If the tuitionFee parameter value is empty, do not put it into the ClassFees entity's tuitionFee field. */
							if( ( request.getParameter( tuitionFeeParamName )?: "" ) != "" )
								classFees.tuitionFee = currencyFormat.parse( request.getParameter( tuitionFeeParamName ) )
			
							/* If the tuitionFee parameter value is empty, and tuitionFee is a required ClassFees entity's field, throw an EmptyRequiredFieldException. */
							else if( ClassFees.isRequired( "tuitionFee" ) )
								throw new EmptyRequiredFieldException( "ClassFees", "tuitionFee" )
								
							String boardingFeeParamName = "boardingFee${ term.termSchool }${ it.getProperty( "class" ) }"
							
							/* If the boardingFee parameter value is empty, do not put it into the ClassFees entity's boardingFee field. */
							if( ( request.getParameter( boardingFeeParamName )?: "" ) != "" )
								classFees.boardingFee = currencyFormat.parse( request.getParameter( boardingFeeParamName ) )
			
							/* If the boardingFee parameter value is empty, and boardingFee is a required ClassFees entity's field, throw an EmptyRequiredFieldException. */
							else if( ClassFees.isRequired( "boardingFee" ) )
								throw new EmptyRequiredFieldException( "ClassFees", "boardingFee" )
								
							classFees.lastUpdateDate = new Date()
							classFees.lastUpdateUser = user?.getEmail()
							classFees.save()
							
							newClassFees.add( classFees )
						}
					)
				}
				else {
					classFeesBackup = ClassFees.findBySchoolNameAndTermNoAndTermYear( term.termSchool, term.termNo, term.year )
					
					ClassFees.findBySchoolNameAndTermNoAndTermYear( term.termSchool, term.termNo, term.year ).each(
						{ classFees ->
							Number tuitionFee
							String tuitionFeeParamName = "tuitionFee${ term.termSchool }${ classFees.getProperty( "class" ) }"
							
							/* If the tuitionFee parameter value is empty, do not put it into the ClassFees entity's tuitionFee field. */
							if( ( request.getParameter( tuitionFeeParamName )?: "" ) != "" )
								tuitionFee = currencyFormat.parse( request.getParameter( tuitionFeeParamName ) )
			
							/* If the tuitionFee parameter value is empty, and tuitionFee is a required ClassFees entity's field, throw an EmptyRequiredFieldException. */
							else if( ClassFees.isRequired( "tuitionFee" ) )
								throw new EmptyRequiredFieldException( "ClassFees", "tuitionFee" )
								
							Number boardingFee
							String boardingFeeParamName = "boardingFee${ term.termSchool }${ classFees.getProperty( "class" ) }"
							
							/* If the boardingFee parameter value is empty, do not put it into the ClassFees entity's boardingFee field. */
							if( ( request.getParameter( boardingFeeParamName )?: "" ) != "" )
								boardingFee = currencyFormat.parse( request.getParameter( boardingFeeParamName ) )
			
							/* If the boardingFee parameter value is empty, and boardingFee is a required ClassFees entity's field, throw an EmptyRequiredFieldException. */
							else if( ClassFees.isRequired( "boardingFee" ) )
								throw new EmptyRequiredFieldException( "ClassFees", "boardingFee" )
								
							classFees.tuitionFee = tuitionFee
							classFees.boardingFee = boardingFee
							classFees.lastUpdateDate = new Date()
							classFees.lastUpdateUser = user?.getEmail()
							classFees.save()
							
							classesAttendedBackup.addAll( ClassAttended.findBySchoolNameAndClassAndClassTermNoAndClassTermYear( classFees.schoolName, classFees.getProperty( "class" ), classFees.termNo, classFees.termYear ) )
							
							ClassAttended.findBySchoolNameAndClassAndClassTermNoAndClassTermYear( classFees.schoolName, classFees.getProperty( "class" ), classFees.termNo, classFees.termYear ).each(
								{
									Number tuitionFeeDiff = ( tuitionFee?: 0 ) - ( it.tuitionFee?: 0 )
									Number boardingFeeDiff = 0
									
									if( it.boardingInd == "Y" )
										boardingFeeDiff = ( boardingFee?: 0 ) - ( it.boardingFee?: 0 )
									
									it.tuitionFee = tuitionFee
									it.boardingFee = boardingFee
									it.lastUpdateDate = new Date()
									it.lastUpdateUser = user?.getEmail()
									it.save()
									
									Document enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear(
										it.studentId,
										it.schoolName,
										it.enrollTermNo,
										it.enrollTermYear
									)
									
									Set<String> enrollmentDocumentFieldNames = enrollmentDocument.getFieldNames()
									
									if(
										tuitionFeeDiff != 0 ||
										boardingFeeDiff != 0 ||
										(
											enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() == term.termNo &&
											enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() == term.year &&
											enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() != term.startDate
										) ||
										(
											enrollmentDocumentFieldNames.contains( "leaveTermNo" ) &&
											enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() == term.termNo &&
											enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() == term.year &&
											enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != term.endDate
										)
									) {
										
										if( !enrollmentDocumentBackups.containsKey( enrollmentDocument.getId() ) )
											enrollmentDocumentBackups.put( enrollmentDocument.getId(), enrollmentDocument )
											
										/* Populate the EnrollmentDocument fields. */
										Number newTuitionFees = enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() + tuitionFeeDiff
										Number newBoardingFees = enrollmentDocument.getOnlyField( "boardingFees" ).getNumber() + boardingFeeDiff
										Number newFeesDue = newTuitionFees + newBoardingFees + enrollmentDocument.getOnlyField( "otherFees" ).getNumber() - enrollmentDocument.getOnlyField( "payments" ).getNumber()
		
										if( newFeesDue < 0 )
											newFeesDue = 0
										
										Document.Builder enrollmentDocBuilder = Document.newBuilder()
											
										enrollmentDocBuilder.setId( "" + enrollmentDocument.getId() )
											.addField( Field.newBuilder().setName( "studentId" ).setAtom( enrollmentDocument.getOnlyField( "studentId" ).getAtom() ) )
											.addField( Field.newBuilder().setName( "firstName" ).setText( enrollmentDocument.getOnlyField( "firstName" ).getText() ) )
											.addField( Field.newBuilder().setName( "schoolName" ).setText( enrollmentDocument.getOnlyField( "schoolName" ).getText() ) )
											.addField( Field.newBuilder().setName( "termsEnrolled" ).setText( enrollmentDocument.getOnlyField( "termsEnrolled" ).getText() ) )
											.addField( Field.newBuilder().setName( "enrollTermYear" ).setNumber( enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() ) )
											.addField( Field.newBuilder().setName( "enrollTermNo" ).setNumber( enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() ) )
											.addField( Field.newBuilder().setName( "classesAttended" ).setText( enrollmentDocument.getOnlyField( "classesAttended" ).getText() ) )
											.addField( Field.newBuilder().setName( "firstClassAttended" ).setText( enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() ) )
											.addField( Field.newBuilder().setName( "lastClassAttended" ).setText( enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() ) )
											.addField( Field.newBuilder().setName( "tuitionFees" ).setNumber( newTuitionFees ) )
											.addField( Field.newBuilder().setName( "boardingFees" ).setNumber( newBoardingFees ) )
											.addField( Field.newBuilder().setName( "otherFees" ).setNumber( enrollmentDocument.getOnlyField( "otherFees" ).getNumber() ) )
											.addField( Field.newBuilder().setName( "payments" ).setNumber( enrollmentDocument.getOnlyField( "payments" ).getNumber() ) )
											.addField( Field.newBuilder().setName( "feesDue" ).setNumber( newFeesDue ) )
											.addField( Field.newBuilder().setName( "lastUpdateDate" ).setDate( new Date() ) )
										
										if(
											enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() == term.termNo &&
											enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() == term.year
										)
											enrollmentDocBuilder.addField( Field.newBuilder().setName( "enrollTermStartDate" ).setDate( term.startDate ) )
										else
											enrollmentDocBuilder.addField( Field.newBuilder().setName( "enrollTermStartDate" ).setDate( enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() ) )
											
										if(
											enrollmentDocumentFieldNames.contains( "leaveTermNo" ) &&
											enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() == term.termNo &&
											enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() == term.year
										)
											enrollmentDocBuilder.addField( Field.newBuilder().setName( "leaveTermEndDate" ).setDate( term.endDate ) )
										else
											enrollmentDocBuilder.addField( Field.newBuilder().setName( "leaveTermEndDate" ).setDate( enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() ) )
											
										if( enrollmentDocumentFieldNames.contains( "lastName" ) )
											enrollmentDocBuilder.addField( Field.newBuilder().setName( "lastName" ).setText( enrollmentDocument.getOnlyField( "lastName" ).getText() ) )
												
										if( enrollmentDocumentFieldNames.contains( "birthDate" ) )
											enrollmentDocBuilder.addField( Field.newBuilder().setName( "birthDate" ).setDate( enrollmentDocument.getOnlyField( "birthDate" ).getDate() ) )
												
										if( enrollmentDocumentFieldNames.contains( "village" ) )
											enrollmentDocBuilder.addField( Field.newBuilder().setName( "village" ).setText( enrollmentDocument.getOnlyField( "village" ).getText() ) )
												
										if( enrollmentDocumentFieldNames.contains( "genderCode" ) )
											enrollmentDocBuilder.addField( Field.newBuilder().setName( "genderCode" ).setText( enrollmentDocument.getOnlyField( "genderCode" ).getText() ) )
												
										if( enrollmentDocumentFieldNames.contains( "specialInfo" ) )
											enrollmentDocBuilder.addField( Field.newBuilder().setName( "specialInfo" ).setText( enrollmentDocument.getOnlyField( "specialInfo" ).getText() ) )
												
										if( enrollmentDocumentFieldNames.contains( "leaveTermYear" ) )
											enrollmentDocBuilder.addField( Field.newBuilder().setName( "leaveTermYear" ).setNumber( enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() ) )
												
										if( enrollmentDocumentFieldNames.contains( "leaveTermNo" ) )
											enrollmentDocBuilder.addField( Field.newBuilder().setName( "leaveTermNo" ).setNumber( enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() ) )
											
										if( enrollmentDocumentFieldNames.contains( "leaveReasonCategory" ) )
											enrollmentDocBuilder.addField( Field.newBuilder().setName( "leaveReasonCategory" ).setText( enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() ) )
											
										if( enrollmentDocumentFieldNames.contains( "leaveReason" ) )
											enrollmentDocBuilder.addField( Field.newBuilder().setName( "leaveReason" ).setText( enrollmentDocument.getOnlyField( "leaveReason" ).getText() ) )
												
										if( user != null )
								            enrollmentDocBuilder.addField( Field.newBuilder().setName( "lastUpdateUser" ).setText( user.getEmail() ) )
								            
								        enrollmentIndex.put( enrollmentDocBuilder.build() )
								        
								        Document studentDocument = StudentDocument.findByStudentId( it.studentId )
										
										Set<String> studentDocumentFieldNames = studentDocument.getFieldNames()
										
										if(
											studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText() == enrollmentDocument.getOnlyField( "schoolName" ).getText() &&
											studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber() == enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() &&
											studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber() == enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber()
										) {
											
											if( !studentDocumentBackups.containsKey( studentDocument.getId() ) )
												studentDocumentBackups.put( studentDocument.getId(), studentDocument )
												
											/* Populate the StudentDocument fields. */
											Document.Builder studentDocBuilder = Document.newBuilder()
												
											studentDocBuilder.setId( "" + studentDocument.getId() )
												.addField( Field.newBuilder().setName( "studentId" ).setAtom( studentDocument.getOnlyField( "studentId" ).getAtom() ) )
												.addField( Field.newBuilder().setName( "firstName" ).setText( studentDocument.getOnlyField( "firstName" ).getText() ) )
												.addField( Field.newBuilder().setName( "lastEnrollmentSchool" ).setText( studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText() ) )
												.addField( Field.newBuilder().setName( "termsEnrolled" ).setText( studentDocument.getOnlyField( "termsEnrolled" ).getText() ) )
												.addField( Field.newBuilder().setName( "lastEnrollmentTermYear" ).setNumber( studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber() ) )
												.addField( Field.newBuilder().setName( "lastEnrollmentTermNo" ).setNumber( studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber() ) )
												.addField( Field.newBuilder().setName( "classesAttended" ).setText( studentDocument.getOnlyField( "classesAttended" ).getText() ) )
												.addField( Field.newBuilder().setName( "lastEnrollmentFirstClassAttended" ).setText( studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() ) )
												.addField( Field.newBuilder().setName( "lastEnrollmentLastClassAttended" ).setText( studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() ) )
												.addField( Field.newBuilder().setName( "tuitionFees" ).setNumber( newTuitionFees ) )
												.addField( Field.newBuilder().setName( "boardingFees" ).setNumber( newBoardingFees ) )
												.addField( Field.newBuilder().setName( "otherFees" ).setNumber( studentDocument.getOnlyField( "otherFees" ).getNumber() ) )
												.addField( Field.newBuilder().setName( "payments" ).setNumber( studentDocument.getOnlyField( "payments" ).getNumber() ) )
												.addField( Field.newBuilder().setName( "feesDue" ).setNumber( newFeesDue ) )
												.addField( Field.newBuilder().setName( "lastUpdateDate" ).setDate( new Date() ) )
											
											if(
												studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber() == term.termNo &&
												studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber() == term.year
											)
												studentDocBuilder.addField( Field.newBuilder().setName( "lastEnrollmentTermStartDate" ).setDate( term.startDate ) )
											else
												studentDocBuilder.addField( Field.newBuilder().setName( "lastEnrollmentTermStartDate" ).setDate( studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate() ) )
												
											if( studentDocumentFieldNames.contains( "lastName" ) )
												studentDocBuilder.addField( Field.newBuilder().setName( "lastName" ).setText( studentDocument.getOnlyField( "lastName" ).getText() ) )
													
											if( studentDocumentFieldNames.contains( "birthDate" ) )
												studentDocBuilder.addField( Field.newBuilder().setName( "birthDate" ).setDate( studentDocument.getOnlyField( "birthDate" ).getDate() ) )
													
											if( studentDocumentFieldNames.contains( "village" ) )
												studentDocBuilder.addField( Field.newBuilder().setName( "village" ).setText( studentDocument.getOnlyField( "village" ).getText() ) )
													
											if( studentDocumentFieldNames.contains( "genderCode" ) )
												studentDocBuilder.addField( Field.newBuilder().setName( "genderCode" ).setText( studentDocument.getOnlyField( "genderCode" ).getText() ) )
													
											if( studentDocumentFieldNames.contains( "specialInfo" ) )
												studentDocBuilder.addField( Field.newBuilder().setName( "specialInfo" ).setText( studentDocument.getOnlyField( "specialInfo" ).getText() ) )
													
											if( studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermYear" ) )
												studentDocBuilder.addField( Field.newBuilder().setName( "lastEnrollmentLeaveTermYear" ).setNumber( studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() ) )
													
											if( studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermNo" ) )
												studentDocBuilder.addField( Field.newBuilder().setName( "lastEnrollmentLeaveTermNo" ).setNumber( studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() ) )
												
											if( studentDocumentFieldNames.contains( "leaveReasonCategory" ) )
												studentDocBuilder.addField( Field.newBuilder().setName( "leaveReasonCategory" ).setText( studentDocument.getOnlyField( "leaveReasonCategory" ).getText() ) )
												
											if( studentDocumentFieldNames.contains( "leaveReason" ) )
												studentDocBuilder.addField( Field.newBuilder().setName( "leaveReason" ).setText( studentDocument.getOnlyField( "leaveReason" ).getText() ) )
													
											if( user != null )
									            studentDocBuilder.addField( Field.newBuilder().setName( "lastUpdateUser" ).setText( user.getEmail() ) )
									            
									        studentIndex.put( studentDocBuilder.build() )
										}
									}
								}
							)
						}
					)
				}

                /* Respond with the HTML code that is required to display the new Term entity within a table. */
                println ListItemFormatter.getTermListItem( term )
                
                Term.deleteMemcache( term.termSchool )
                
                if( enrollmentDocumentBackups.size() > 0 )
                	EnrollmentDocument.deleteMemcache()
                
                if( studentDocumentBackups.size() > 0 )
                	StudentDocument.deleteMemcache()
            }
            catch( Exception e ) {
				if( params.action == "save" ) {
					try { term?.delete() } catch( Exception deleteException ) {}
					
					newClassFees.each(
						{
							try { it.delete() } catch( Exception deleteException ) {}
						}
					)
				}
				else {
					try { termBackup?.save() } catch( Exception saveException ) {}
					
					classFeesBackup.each(
						{
							try { it.save() } catch( Exception saveException ) {}
						}
					)
					
					classesAttendedBackup.each(
						{
							try { it.save() } catch( Exception saveException ) {}
						}
					)
					
					enrollmentDocumentBackups.each(
						{ enrollmentDocumentId, enrollmentDocument ->
							enrollmentIndex.put( enrollmentDocument )
						}
					)
					
					studentDocumentBackups.each(
						{ studentDocumentId, studentDocument ->
							studentIndex.put( studentDocument )
						}
					)
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