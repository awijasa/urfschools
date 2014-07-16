/*
Document   : Groovlet file: SchoolController.groovy
Created on : Sun Jun 4 00:10:00 CST 2012
Author     : awijasa
 */

import com.google.appengine.api.datastore.Entity
import data.App
import data.Class
import data.ClassAttended
import data.ClassFees
import data.DuplicateEntityException
import data.EmptyRequiredFieldException
import data.Enrollment
import data.EntityIsUsedException
import data.School
import data.StaleRecordException
import data.Student
import data.Term
import data.URFUser
import data.UserPrivilege
import formatter.ListItemFormatter

/* Handle create, update, and delete operations on the School entity. */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the SchoolController through urfschools.appspot.com. */
if( user == null && !localMode )
    response.sendError( response.SC_UNAUTHORIZED )
else {
	Entity urfUser
	
	if( user == null && localMode )
		urfUser = URFUser.findByEmail( params.userEmail )
	else
		urfUser = URFUser.findByEmail( user?.getEmail() )
	
	if( params.action == "getSchoolListItem" ) {
		if( user == null || ( urfUser?.adminPrivilege != null && UserPrivilege.findByUserEmailAndSchoolName( user.getEmail(), params.schoolName )?.privilege != null ) )
			println ListItemFormatter.getSchoolListItem( School.findByName( params.schoolName ), Integer.parseInt( params.schoolIndex ), null )
	}
	
	/*
     * Allow the user to delete and edit only if the request comes from a test script or an admin with Modify privilege and a Modify privilege for the school
     * being deleted or edited.
     * 
     * Allow the user to save only if the request comes from a test script or an admin with Modify privilege.
     */
    else if( user == null || ( urfUser?.adminPrivilege?.equals( "Modify" ) && ( params.action.equals( "save" ) || UserPrivilege.findByUserEmailAndSchoolName( user.getEmail(), params.schoolName )?.privilege?.equals( "Modify" ) ) ) ) {
    
        /* Delete a School entity identified by the id parameter and its associated Class entities when the action parameter value is "delete". */
        if( params.action.equals( "delete" ) ) {
            List<Entity> classBackupList = new ArrayList()
			Entity enrollmentMetaData
            Entity school
			Entity studentMetaData

            try {
                school = datastore.get( "School", Long.parseLong( params.id ) )
                
                /* Delete the School Classes. */
                def classes = Class.findBySchoolName( school.name )
                classes.each() {
                    if( Class.isUsed( it ) )
                        throw new EntityIsUsedException( it, Class.getPrimaryKey() )

                    classBackupList.add it

                    it.delete()
                    
                    Class.deleteMemcache( school.name )
                }

                /* Delete the School. */
                if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != school.lastUpdateDate ||
                    ( params.lastUpdateUser?: "" ) != ( school.lastUpdateUser?: "" ) )
                    throw new StaleRecordException( params.action, "School" )
                
                if( School.isUsed( school ) )
                    throw new EntityIsUsedException( school, School.getPrimaryKey() )

                school.delete()
				enrollmentMetaData = Enrollment.findMetaDataBySchoolName( school.name )
				enrollmentMetaData.delete()
				studentMetaData = Student.findMetaDataBySchoolName( school.name )
				studentMetaData.delete()

                Class.deleteMemcache( school.name )
                School.deleteMemcache()
                
                /* Delete the User Privileges related to the School being deleted. */
                UserPrivilege.findBySchoolName( school.name ).each() {
                    it.delete()
                    UserPrivilege.deleteMemcache( it.userEmail )
                }
            }
            catch( Exception e ) {
                
                /* Rollback the deleted Classes. */
                classBackupList.each() { try{ it.save() } catch( Exception saveException ) {} }
                
                /* Rollback the deleted School. */
                school?.save()
				
				/* Rollback the deleted Enrollment Meta Data */
				enrollmentMetaData?.save()
				
				/* Rollback the deleted Student Meta Data */
				studentMetaData?.save()

				/* Respond with an Internal Server error message if the delete fails. */
                response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
                response.setHeader( "Response-Phrase", e.getMessage() )
            }
        }

        /*
         * Edit and existing School entity and its associated Class entities when the action parameter value is "edit".
         * Save a new School entity and its new Class entities when the action parameter value is "save".
         */
        else if( params.action.equals( "edit" ) || params.action.equals( "save" ) ) {
            List<Entity> addedClassFeesList = new ArrayList()
			List<Entity> addedClassList = new ArrayList()
            List<Entity> classAttendedBackupList = new ArrayList()
			List<Entity> classBackupList = new ArrayList()
			List<Entity> classFeesBackupList = new ArrayList()
			Entity enrollmentMetaData
            Entity school
			List<Entity> schoolTerms
			Entity studentMetaData

            try {
                def schoolName
				Entity userPrivilege

                /* If the schoolName parameter value is "School Name" or empty, do not put it into the School entity's name field. */
                if( params.schoolName != null && !params.schoolName.equals( "School Name" ) && !params.schoolName.equals( "" ) )
                    schoolName = params.schoolName

                /* If the schoolName parameter value is "School Name" or empty, and name is a required School entity's field, throw an EmptyRequiredFieldException. */
                else if( School.isRequired( "name" ) )
                    throw new EmptyRequiredFieldException( "School", "name" )

                if( params.action.equals( "edit" ) ) {
                    school = School.findByName( schoolName )
                    
                    if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != school.lastUpdateDate ||
                        ( params.lastUpdateUser?: "" ) != ( school.lastUpdateUser?: "" ) )
                        throw new StaleRecordException( params.action, "School" )
						
					schoolTerms = Term.findByTermSchool( schoolName )
                }
                else {
                    school = new Entity( "School" )
					school.name = schoolName
                    
                    if( params.action.equals( "save" ) && School.findByName( school.name ) )
                        throw new DuplicateEntityException( school, School.getPrimaryKey() )
						
					schoolTerms = new ArrayList()
                }
                
                school.lastUpdateDate = new Date()
                school.lastUpdateUser = user?.getEmail()
                school.save()
                School.deleteMemcache()
				
				if( params.action == "save" ) {
					Entity appEntity = App.get()
					
					if( appEntity == null ) {
						appEntity = new Entity( "App" )
						appEntity.name = "urfschools"
						appEntity.save()
					}
					
					enrollmentMetaData = new Entity( "EnrollmentMetaData", appEntity.getKey() )
					enrollmentMetaData.schoolName = school.name
					enrollmentMetaData.count = 0
					enrollmentMetaData.save()
					
					studentMetaData = new Entity( "StudentMetaData", appEntity.getKey() )
					studentMetaData.schoolName = school.name
					studentMetaData.count = 0
					studentMetaData.save()
				}
				
                /* Create new Class entities associated to the School entity. */

                /* Iterate through each parameter until the class<seq> parameters are found. */
                def classParams = request.getParameterNames()
				boolean isClassReorderingNeeded = false
				
                while( classParams.hasMoreElements() ) {
                    def paramName = classParams.nextElement();

                    if( paramName.equals( "schoolName" ) ) {}
                    else if( paramName.equals( "action" ) ) {}
                    else if( paramName.equals( "class" ) ) {}
                    else if( paramName.equals( "lastUpdateDate" ) ) {}
                    else if( paramName.equals( "lastUpdateUser" ) ) {}
                    else if( paramName == "string" ) {}
					else if( paramName == "userEmail" ) {}

                    else if( paramName.startsWith( "delete" ) ) {
						ClassFees.findBySchoolNameAndClass( schoolName, request.getParameter( paramName ) ).each(
							{
								if( ClassFees.isUsed( it ) )
									throw new EntityIsUsedException( it, ClassFees.getPrimaryKey() )
									
								classFeesBackupList.add( it )
								it.delete()
							}
						)
						
                        def classToDelete = Class.findBySchoolNameAndClass( schoolName, request.getParameter( paramName ) )

                        if( Class.isUsed( classToDelete ) )
                            throw new EntityIsUsedException( classToDelete, Class.getPrimaryKey() )

                        classBackupList.add classToDelete
                        classToDelete.delete()
                        Class.deleteMemcache( schoolName )
						
						isClassReorderingNeeded = true
                    }

                    /* If the class<seq> parameter value is empty, do not create any new Class entity. */
                    else if( !request.getParameter( paramName ).equals( "" ) ){
                        
						/* If the schoolName parameter value is "School Name" or empty, and schoolName is a required Class entity's field, throw an EmptyRequiredFieldException. */
                        if( ( params.schoolName == null || params.schoolName == "School Name" || params.schoolName == "" ) && Class.isRequired( "schoolName" ) )
                            throw new EmptyRequiredFieldException( "Class", "schoolName" )

						Entity classFees
						Entity schoolClass
						
						if( paramName.startsWith( "newClass" ) ) {
							schoolClass = new Entity( "Class", school.getKey() )
							schoolClass.schoolName = schoolName
							
							/* Assign the class<seq> parameter value to the Class entity's class field. */
							schoolClass.setProperty( "class", request.getParameter( paramName ) )
							schoolClass.level = Integer.parseInt( paramName.substring( 8 ) )
							
							schoolTerms.each(
								{
									classFees = new Entity( "ClassFees", it.getKey() )
									classFees.schoolName = schoolName
									
									/* Assign the class<seq> parameter value to the ClassFees entity's class field. */
									classFees.setProperty( "class", request.getParameter( paramName ) )
									classFees.classLevel = schoolClass.level
									classFees.termNo = it.termNo
									classFees.termYear = it.termYear
									classFees.lastUpdateDate = new Date()
									classFees.lastUpdateUser = user?.getEmail()
									classFees.save()
									
									addedClassFeesList.add( classFees )
								}
							)
						}
						else {
							classBackupList.add Class.findBySchoolNameAndClass( schoolName, request.getParameter( paramName ) )
							schoolClass = Class.findBySchoolNameAndClass( schoolName, request.getParameter( paramName ) )
							schoolClass.level = Integer.parseInt( paramName.substring( 5 ) )
							
							classFeesBackupList.add( ClassFees.findBySchoolNameAndClass( schoolName, request.getParameter( paramName ) ) )
							ClassFees.findBySchoolNameAndClass( schoolName, request.getParameter( paramName ) ).each(
								{
									it.classLevel = schoolClass.level
									it.lastUpdateDate = new Date()
									it.lastUpdateUser = user?.getEmail()
									it.save()
								}
							)
							
							classAttendedBackupList.add( ClassAttended.findBySchoolNameAndClass( schoolName, request.getParameter( paramName ) ) )
							ClassAttended.findBySchoolNameAndClass( schoolName, request.getParameter( paramName ) ).each(
								{
									it.classLevel = schoolClass.level
									it.lastUpdateDate = new Date()
									it.lastUpdateUser = user?.getEmail()
									it.save()
								}
							)
						}
							
						schoolClass.lastUpdateDate = new Date()

                        if( user != null )
                            schoolClass.lastUpdateUser = user.getEmail()

                        if( paramName.startsWith( "newClass" ) ) {
							addedClassList.add schoolClass
                        	
							if( Class.findBySchoolNameAndClass( schoolClass.schoolName, schoolClass.getProperty( "class" ) ) != null )
								throw new DuplicateEntityException( schoolClass, Class.getPrimaryKey() )
						}

                        schoolClass.save()

                        Class.deleteMemcache( params.schoolName )
                    }
                }
				
				if( isClassReorderingNeeded ) {
					Class.findBySchoolName( schoolName ).eachWithIndex(
						{ schoolClass, index ->
							schoolClass.level = index + 1
							schoolClass.lastUpdateDate = new Date()
							schoolClass.lastUpdateUser = user?.getEmail()
							schoolClass.save()
							
							ClassAttended.findBySchoolNameAndClass( schoolName, schoolClass.getProperty( "class" ) ).each(
								{
									it.classLevel = schoolClass.level
									it.lastUpdateDate = new Date()
									it.lastUpdateUser = user?.getEmail()
									it.save()
								}
							)
						}
					)
					
					Term.findByTermSchool( schoolName ).each(
						{ term ->
							ClassFees.findBySchoolNameAndTermNoAndTermYear( schoolName, term.termNo, term.year ).eachWithIndex(
								{ classFees, index ->
									classFees.classLevel = index + 1
									classFees.lastUpdateDate = new Date()
									classFees.lastUpdateUser = user?.getEmail()
									classFees.save()
								}
							)
						}
					)
				}
                
                /* Automatically add the Modify privilege to the School being added for the user. */
                if( params.action.equals( "save" ) ) {
					userPrivilege = new Entity( "UserPrivilege", urfUser?.getKey() )
					userPrivilege.userEmail = urfUser.email
                    userPrivilege.schoolName = schoolName
                    userPrivilege.privilege = "Modify"
                    userPrivilege.lastUpdateDate = new Date()
                    userPrivilege.lastUpdateUser = urfUser.email

                    userPrivilege.save()
                    UserPrivilege.deleteMemcache( urfUser.email, userPrivilege.privilege )
                }
				
				Integer schoolIndex
				
				if( params.schoolIndex == null )
					schoolIndex = null
				else
					schoolIndex = Integer.parseInt( params.schoolIndex )

                /* Respond with the HTML code that is required to display the new School entity with its associated Class entities within a table. */
                println ListItemFormatter.getSchoolListItem( school, schoolIndex, userPrivilege )?: ""
            }
            catch( Exception e ) {

                /* If the edit fails, restore the deleted Classes. */
                classBackupList.each() {
                    try { it.save() } catch( Exception saveException ) {}
                }
				
				/* If the edit fails, restore the edited/deleted ClassFees. */
				classFeesBackupList.each() {
                    try { it.save() } catch( Exception saveException ) {}
                }
				
				/* If the edit fails, restore the edited/deleted ClassAttended. */
				classAttendedBackupList.each(
					{
						try { it.save() } catch( Exception saveException ) {}
					}
				)

                /* If the edit/save fails, delete the classes that have been created during the edit/save process. */
                addedClassList.each() {
                    try { it.delete() } catch( Exception deleteException ) {}
                }
				
				/* If the edit/save fails, delete the ClassFees that have been created during the edit/save process. */
				addedClassFeesList.each() {
					try { it.delete() } catch( Exception deleteException ) {}
				}

                Class.deleteMemcache( params.schoolName )

                /* If the save fails, delete the School and StudentMetaData that have been created during the process. */
                if( params.action.equals( "save" ) ) {
                    try {
                        if( school != null ) {
                            school.delete()
                            School.deleteMemcache()
                        }
                    }
                    catch( Exception deleteException ) {}
					
					enrollmentMetaData?.delete()
					studentMetaData?.delete()
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