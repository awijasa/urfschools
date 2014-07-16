<%
  import com.google.appengine.api.datastore.Entity
  import data.Class
  import data.ClassAttended
  import data.ClassFees
  import data.School
  import data.StudentWithLastEnrollment
  import data.Term
  import data.URFUser
  import data.UserPrivilege
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
  else {
%>
	<html>
	  <head>
	    <title>Class Save, Edit, and Delete Test</title>
	  </head>
	  <body>
	    <%
	      Entity schoolClass1
	      Entity schoolClass2
	      Entity schoolClass3
	      Entity schoolClass4
	      Entity schoolClassFees1
	      Entity schoolClassFees2
	      Entity schoolClassFees3
	      Entity schoolClassFees4
	      
	      /* For the Save Test, make sure that the School Classes to create have not existed. */
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) == null, "Class: { schoolName: Saint John's College MN, class: Freshman } has existed. Please change the test case."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) == null, "Class: { schoolName: Saint John's College MN, class: Sophomore } has existed. Please change the test case."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" ) == null, "Class: { schoolName: Saint John's College MN, class: Junior } has existed. Please change the test case."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" ) == null, "Class: { schoolName: Saint John's College MN, class: Senior } has existed. Please change the test case."
	      
	      /* Give the testing user an Admin Modify privilege. */
	      Entity urfUser = URFUser.findByEmail( user.getEmail() )
	      Entity urfUserBackup = URFUser.findByEmail( user.getEmail() )
	      
	      if( urfUser == null )
	        urfUser = new Entity( "URFUser" )
	        
	      urfUser.email = user.getEmail()
	      urfUser.adminPrivilege = "Modify"
	      urfUser.lastUpdateDate = new Date()
	      urfUser.lastUpdateUser = user.getEmail()
	      urfUser.save()
	      
	      /* Class Save Test with a missing action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "newClass1", "Freshman" )
	        .addQueryParam( "newClass2", "Sophomore" )
	        .addQueryParam( "newClass3", "Junior" )
	        .addQueryParam( "newClass4", "Senior" )
	        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) == null, "User was able to save a Class using no action parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) == null, "User was able to save a Class using no action parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) == null, "User was able to save a Class using no action parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" ) == null, "User was able to save a Class using no action parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" ) == null, "User was able to save a Class using no action parameter."
	    
	      /* Class Save Test with an invalid action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "create" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "newClass1", "Freshman" )
	        .addQueryParam( "newClass2", "Sophomore" )
	        .addQueryParam( "newClass3", "Junior" )
	        .addQueryParam( "newClass4", "Senior" )
	        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) == null, "User was able to save a Class using a create action parameter instead of save."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) == null, "User was able to save a Class using a create action parameter instead of save."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) == null, "User was able to save a Class using a create action parameter instead of save."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" ) == null, "User was able to save a Class using a create action parameter instead of save."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" ) == null, "User was able to save a Class using a create action parameter instead of save."
	      
	      /* Class Save Test with a missing schoolName parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "newClass1", "Freshman" )
	        .addQueryParam( "newClass2", "Sophomore" )
	        .addQueryParam( "newClass3", "Junior" )
	        .addQueryParam( "newClass4", "Senior" )
	        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) == null, "User was able to save a Class using no schoolName parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) == null, "User was able to save a Class using no schoolName parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) == null, "User was able to save a Class using no schoolName parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" ) == null, "User was able to save a Class using no schoolName parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" ) == null, "User was able to save a Class using no schoolName parameter."
	      
	      /* Class Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "newClass1", "Freshman" )
	        .addQueryParam( "newClass2", "Sophomore" )
	        .addQueryParam( "newClass3", "Junior" )
	        .addQueryParam( "newClass4", "Senior" )
	        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
	      
	      school = School.findByName( "Saint John's College MN" )
	      schoolClass1 = Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" )
	      schoolClass2 = Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" )
	      schoolClass3 = Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" )
	      schoolClass4 = Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" )
	      
	      Date lastUpdateDate = new Date( school.lastUpdateDate.getTime() )
	    
	      assert school != null, "User was unable to save a School."
	      assert school.name == "Saint John's College MN", "School: ${ school.name } from School.findByName( def name ) is not equal to Saint John's College MN."
	      assert school.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ school.lastUpdateDate.clearTime() } from School.findByName( def name ) is not equal to ${ new Date().clearTime() }."
	      
	      assert schoolClass1 != null, "User was unable to save a Class."
	      assert schoolClass1.schoolName == "Saint John's College MN", "School Name: ${ schoolClass1.schoolName } from Class.findBySchoolNameAndCLass( def schoolName, def class ) is not equal to Saint John's College MN."
	      assert schoolClass1.getProperty( "class" ) == "Freshman", "Class: ${ schoolClass1.getProperty( "class" ) } from Class.findBySchoolNameAndClass( def schoolName, def class ) is not equal to Freshman."
	      assert schoolClass1.level == 1, "Class Level: ${ schoolClass1.level } from Class.findBySchoolNameAndClass( def schoolName, def class ) is not equal to 1"
	      assert schoolClass1.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ schoolClass1.lastUpdateDate.clearTime() } from Class.findBySchoolNameAndClass( def schoolName, def class ) is not equal to ${ new Date().clearTime() }."
	      
	      assert schoolClass2 != null, "User was unable to save a Class."
	      assert schoolClass2.schoolName == "Saint John's College MN", "School Name: ${ schoolClass2.schoolName } from Class.findBySchoolNameAndCLass( def schoolName, def class ) is not equal to Saint John's College MN."
	      assert schoolClass2.getProperty( "class" ) == "Sophomore", "Class: ${ schoolClass2.getProperty( "class" ) } from Class.findBySchoolNameAndClass( def schoolName, def class ) is not equal to Sophomore."
	      assert schoolClass2.level == 2, "Class Level: ${ schoolClass2.level } from Class.findBySchoolNameAndClass( def schoolName, def class ) is not equal to 2"
	      assert schoolClass2.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ schoolClass2.lastUpdateDate.clearTime() } from Class.findBySchoolNameAndClass( def schoolName, def class ) is not equal to ${ new Date().clearTime() }."
	    
	      assert schoolClass3 != null, "User was unable to save a Class."
	      assert schoolClass3.schoolName == "Saint John's College MN", "School Name: ${ schoolClass3.schoolName } from Class.findBySchoolNameAndCLass( def schoolName, def class ) is not equal to Saint John's College MN."
	      assert schoolClass3.getProperty( "class" ) == "Junior", "Class: ${ schoolClass3.getProperty( "class" ) } from Class.findBySchoolNameAndClass( def schoolName, def class ) is not equal to Junior."
	      assert schoolClass3.level == 3, "Class Level: ${ schoolClass3.level } from Class.findBySchoolNameAndClass( def schoolName, def class ) is not equal to 3"
	      assert schoolClass3.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ schoolClass3.lastUpdateDate.clearTime() } from Class.findBySchoolNameAndClass( def schoolName, def class ) is not equal to ${ new Date().clearTime() }."
	      
	      assert schoolClass4 != null, "User was unable to save a Class."
	      assert schoolClass4.schoolName == "Saint John's College MN", "School Name: ${ schoolClass4.schoolName } from Class.findBySchoolNameAndCLass( def schoolName, def class ) is not equal to Saint John's College MN."
	      assert schoolClass4.getProperty( "class" ) == "Senior", "Class: ${ schoolClass4.getProperty( "class" ) } from Class.findBySchoolNameAndClass( def schoolName, def class ) is not equal to Senior."
	      assert schoolClass4.level == 4, "Class Level: ${ schoolClass4.level } from Class.findBySchoolNameAndClass( def schoolName, def class ) is not equal to 4"
	      assert schoolClass4.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ schoolClass4.lastUpdateDate.clearTime() } from Class.findBySchoolNameAndClass( def schoolName, def class ) is not equal to ${ new Date().clearTime() }."
	      
	      /* Save a Term for the Class Edit Test */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "Feb 1 1901" )
	        .addQueryParam( "endDate", "Apr 30 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "10000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "20000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "30000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "40000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "100000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "200000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "300000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "400000" ).toURL() )
	        
	      int schoolCount = School.list().size()
	      int classCount = Class.findBySchoolName( "Saint John's College MN" ).size()
	      int classFeesCount = ClassFees.findBySchoolName( "Saint John's College MN" ).size()
	      
	      /* Class Duplicate Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "newClass1", "Freshman" )
	        .addQueryParam( "newClass2", "Sophomore" )
	        .addQueryParam( "newClass3", "Junior" )
	        .addQueryParam( "newClass4", "Senior" )
	        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
	      
	      assert schoolCount == School.list().size(), "User was able to save a Duplicate School."
	      assert classCount == Class.findBySchoolName( "Saint John's College MN" ).size(), "User was able to save a Duplicate Class."
	      assert classFeesCount == ClassFees.findBySchoolName( "Saint John's College MN" ).size(), "User was able to save a Duplicate ClassFees"
	      
	      /* Class Edit Test with a missing action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "deleteJunior", "Junior" )
	        .addQueryParam( "deleteSenior", "Senior" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) != null, "User was able to edit a Class using no action parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) != null, "User was able to edit a Class using no action parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" ) != null, "User was able to edit a Class using no action parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" ) != null, "User was able to edit a Class using no action parameter."
	      
	      /* Class Edit Test with an invalid action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "modify" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "deleteJunior", "Junior" )
	        .addQueryParam( "deleteSenior", "Senior" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) != null, "User was able to edit a Class using an invalid action parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) != null, "User was able to edit a Class using an invalid action parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" ) != null, "User was able to edit a Class using an invalid action parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" ) != null, "User was able to edit a Class using an invalid action parameter."
	      
	      /* Class Edit Test with a missing schoolName parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "deleteJunior", "Junior" )
	        .addQueryParam( "deleteSenior", "Senior" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) != null, "User was able to edit a Class using a missing schoolName parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) != null, "User was able to edit a Class using a missing schoolName parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" ) != null, "User was able to edit a Class using a missing schoolName parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" ) != null, "User was able to edit a Class using a missing schoolName parameter."
	      
	      /* Class Edit Test with a missing lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "deleteJunior", "Junior" )
	        .addQueryParam( "deleteSenior", "Senior" ).toURL() )
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) != null, "User was able to edit a Class using a missing lastUpdateDate parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) != null, "User was able to edit a Class using a missing lastUpdateDate parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" ) != null, "User was able to edit a Class using a missing lastUpdateDate parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" ) != null, "User was able to edit a Class using a missing lastUpdateDate parameter."
	      
	      /* Class Edit Test with an invalid lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "deleteJunior", "Junior" )
	        .addQueryParam( "deleteSenior", "Senior" )
	        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) != null, "User was able to edit a Class using an invalid lastUpdateDate parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) != null, "User was able to edit a Class using an invalid lastUpdateDate parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" ) != null, "User was able to edit a Class using an invalid lastUpdateDate parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" ) != null, "User was able to edit a Class using an invalid lastUpdateDate parameter."
	    
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "firstName", "Adrian" )
	        .addQueryParam( "lastName", "Oey" )
	        .addQueryParam( "enrollmentSchool", "Saint John's College MN" )
	        .addQueryParam( "enrollmentTermSchool1", "1901 Term 1" )
	        .addQueryParam( "leaveTermSchool1", "1901 Term 1" )
	        .addQueryParam( "enrollmentFirstClassAttendedSchool1", "Junior" )
	        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Junior" )
	        .addQueryParam( "classAttended1901 Term 1", "Junior" ).toURL() )
	      
	      /* Class Edit Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "deleteFreshman", "Freshman" )
	        .addQueryParam( "deleteSophomore", "Sophomore" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	        
	      schoolClass1 = Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" )
	      schoolClass2 = Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" )
	      
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) == null, "User was unable to delete the Freshman class."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) == null, "User was unable to delete the Sophomore class."
	      assert schoolClass1 != null, "User deleted a wrong class: Junior."
	      assert schoolClass1.level == 1, "Junior class reordering failed" 
	      assert schoolClass2 != null, "User deleted a wrong class: Senior."
	      assert schoolClass2.level == 2, "Senior class reordering failed"
	      
	      schoolClassFees1 = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Junior", 1, 1901 )
	      schoolClassFees2 = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Senior", 1, 1901 )
	      
	      assert ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Freshman", 1, 1901 ) == null, "User was unable to delete the Freshman ClassFees."
	      assert ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Sophomore", 1, 1901 ) == null, "User was unable to delete the Sophomore ClassFees."
	      assert schoolClassFees1 != null, "User deleted a wrong ClassFees: Junior."
	      assert schoolClassFees1.classLevel == 1, "Junior ClassFees reordering failed" 
	      assert schoolClassFees2 != null, "User deleted a wrong ClassFees: Senior."
	      assert schoolClassFees2.classLevel == 2, "Senior ClassFees reordering failed"
	      
	      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0201aoey", "Saint John's College MN", 1, 1901 ).classLevel == 1, "Junior ClassAttended reordering failed"
	      
	      lastUpdateDate = School.findByName( "Saint John's College MN" ).lastUpdateDate
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "newClass3", "Freshman" )
	        .addQueryParam( "newClass4", "Sophomore" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      schoolClass3 = Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" )
	      schoolClass4 = Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" )
	      
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" ) != null, "User deleted a wrong class: Junior."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" ) != null, "User deleted a wrong class: Senior."
	      assert schoolClass3 != null, "User was unable to add the Freshman class."
	      assert schoolClass3.level == 3, "User was unable to assign level 3 to the Freshman class"
	      assert schoolClass4 != null, "User was unable to add the Sophomore class."
	      assert schoolClass4.level == 4, "User was unable to assign level 4 to the Sophomore class"
	      
	      schoolClassFees3 = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Freshman", 1, 1901 )
	      schoolClassFees4 = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Sophomore", 1, 1901 )
	      
	      assert ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Junior", 1, 1901 ) != null, "User deleted a wrong ClassFees: Junior."
	      assert ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Senior", 1, 1901 ) != null, "User deleted a wrong ClassFees: Senior."
	      assert schoolClassFees3 != null, "User was unable to add the Freshman ClassFees."
	      assert schoolClassFees3.classLevel == 3, "User was unable to assign level 3 to the Freshman ClassFees"
	      assert schoolClassFees4 != null, "User was unable to add the Sophomore ClassFees."
	      assert schoolClassFees4.classLevel == 4, "User was unable to assign level 4 to the Sophomore ClassFees"
	      
	      lastUpdateDate = School.findByName( "Saint John's College MN" ).lastUpdateDate
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "class1", "Freshman" )
	        .addQueryParam( "class2", "Sophomore" )
	        .addQueryParam( "class3", "Junior" )
	        .addQueryParam( "class4", "Senior" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      schoolClass1 = Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" )
	      schoolClass2 = Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" )
	      schoolClass3 = Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" )
	      schoolClass4 = Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" )
	      
	      assert schoolClass1.level == 1, "User was unable to assign level 1 to the Freshman class"
	      assert schoolClass2.level == 2, "User was unable to assign level 2 to the Sophomore class"
	      assert schoolClass3.level == 3, "User was unable to assign level 3 to the Junior class"
	      assert schoolClass4.level == 4, "User was unable to assign level 4 to the Senior class"
	      
	      schoolClassFees1 = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Freshman", 1, 1901 )
	      schoolClassFees2 = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Sophomore", 1, 1901 )
	      schoolClassFees3 = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Junior", 1, 1901 )
	      schoolClassFees4 = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Senior", 1, 1901 )
	      
	      assert schoolClassFees1.classLevel == 1, "User was unable to assign level 1 to the Freshman ClassFees"
	      assert schoolClassFees2.classLevel == 2, "User was unable to assign level 2 to the Sophomore ClassFees"
	      assert schoolClassFees3.classLevel == 3, "User was unable to assign level 3 to the Junior ClassFees"
	      assert schoolClassFees4.classLevel == 4, "User was unable to assign level 4 to the Senior ClassFees"
	      
	      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0201aoey", "Saint John's College MN", 1, 1901 ).classLevel == 3, "User was unable to assign level 3 to the Junior ClassAttended"
	      
	      lastUpdateDate = School.findByName( "Saint John's College MN" ).lastUpdateDate
	    
	      schoolCount = School.list().size()
	      classCount = Class.findBySchoolName( "Saint John's College MN" ).size()
	      classFeesCount = ClassFees.findBySchoolName( "Saint John's College MN" ).size()
	      
	      /* Class Duplicate Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "newClass1", "Freshman" )
	        .addQueryParam( "newClass2", "Sophomore" )
	        .addQueryParam( "newClass3", "Junior" )
	        .addQueryParam( "newClass4", "Senior" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      lastUpdateDate = School.findByName( "Saint John's College MN" ).lastUpdateDate
	      
	      assert schoolCount == School.list().size(), "User was able to save a Duplicate School through edit action."
	      assert classCount == Class.findBySchoolName( "Saint John's College MN" ).size(), "User was able to save a Duplicate Class through edit action."
	      assert classFeesCount == ClassFees.findBySchoolName( "Saint John's College MN" ).size(), "User was able to save a Duplicate ClassFees through edit action"
	    
	      Entity studentWithLastEnrollment = StudentWithLastEnrollment.findByStudentId( "0201aoey" )
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", studentWithLastEnrollment.getKey().getId() )
	        .addQueryParam( "studentId", "0201aoey" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", studentWithLastEnrollment.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      Entity term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 )
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", term.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      /* Class Delete Test with a missing action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "id", school.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) != null, "User was able to delete a Class using no action parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) != null, "User was able to delete the Freshman Class using no action parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) != null, "User was able to delete the Sophomore Class using no action parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" ) != null, "User was able to delete the Junior Class using no action parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" ) != null, "User was able to delete the Senior Class using no action parameter."
	      
	      /* Class Delete Test with an invalid action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "remove" )
	        .addQueryParam( "id", school.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) != null, "User was able to delete a Class using an invalid action parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) != null, "User was able to delete the Freshman Class using an invalid action parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) != null, "User was able to delete the Sophomore Class using an invalid action parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" ) != null, "User was able to delete the Junior Class using an invalid action parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" ) != null, "User was able to delete the Senior Class using an invalid action parameter."
	      
	      /* Class Delete Test with no id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) != null, "User was able to delete a Class using no id parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) != null, "User was able to delete the Freshman Class using no id parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) != null, "User was able to delete the Sophomore Class using no id parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" ) != null, "User was able to delete the Junior Class using no id parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" ) != null, "User was able to delete the Senior Class using no id parameter."
	      
	      /* Class Delete Test with an invalid id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", "-1" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) != null, "User was able to delete a Class using an invalid id parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) != null, "User was able to delete the Freshman Class using an invalid id parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) != null, "User was able to delete the Sophomore Class using an invalid id parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" ) != null, "User was able to delete the Junior Class using an invalid id parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" ) != null, "User was able to delete the Senior Class using an invalid id parameter."
	      
	      /* Class Delete Test with no lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", school.getKey().getId() ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) != null, "User was able to delete a Class using no lastUpdateDate parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) != null, "User was able to delete the Freshman Class using no lastUpdateDate parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) != null, "User was able to delete the Sophomore Class using no lastUpdateDate parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" ) != null, "User was able to delete the Junior Class using no lastUpdateDate parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" ) != null, "User was able to delete the Senior Class using no lastUpdateDate parameter."
	      
	      /* Class Delete Test with an invalid lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", school.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) != null, "User was able to delete a Class using an invalid lastUpdateDate parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) != null, "User was able to delete the Freshman Class using an invalid lastUpdateDate parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) != null, "User was able to delete the Sophomore Class using an invalid lastUpdateDate parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" ) != null, "User was able to delete the Junior Class using an invalid lastUpdateDate parameter."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" ) != null, "User was able to delete the Senior Class using an invalid lastUpdateDate parameter."
	      
	      /* Class Delete Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", school.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) == null, "User was unable to delete a Class."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) == null, "User was unable to delete the Freshman Class."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) == null, "User was unable to delete the Sophomore Class."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" ) == null, "User was unable to delete the Junior Class."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Senior" ) == null, "User was unable to delete the Senior Class."
	      
	      /* Restore the Admin Privilege of the current user. */
	      urfUser?.delete()
	      urfUser = null
	      urfUserBackup?.save()
	    %>
	    <p>Class Save, Edit, and Delete Test is successful.</p>
	  </body>
	</html>
<% } %>