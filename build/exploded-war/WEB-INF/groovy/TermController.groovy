/*
Document   : Groovlet file: TermController.groovy
Created on : Wed Aug 8 19:42:00 CST 2012
Author     : awijasa
 */

import com.google.appengine.api.datastore.Entity
import data.AuthorizationException
import data.Class
import data.ClassAttended
import data.ClassFees
import data.DuplicateEntityException
import data.EmptyRequiredFieldException
import data.EntityIsUsedException
import data.InvalidDateRangeException
import data.InvalidValueException
import data.OverlappedTermException
import data.StaleRecordException
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

                Term.findByLimitAndOffset( 0, Integer.parseInt( params.nextTwentyOffset ) - 1 ).each() {
                    /*
                     * Respond with the HTML code that is required to display the Term entity that can fill the empty space that resulted from the deleted record
                     * within an accordion.
                     */
                    println ListItemFormatter.getTermListItem( it )
                }

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
			List<Entity> newClassFees = new ArrayList()
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
                            Term.deleteMemcache( term.termSchool )
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
									it.tuitionFee = tuitionFee
									it.boardingFee = boardingFee
									it.lastUpdateDate = new Date()
									it.lastUpdateUser = user?.getEmail()
									it.save()
								}
							)
						}
					)
				}

                /* Respond with the HTML code that is required to display the new Term entity within a table. */
                println ListItemFormatter.getTermListItem( term )
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