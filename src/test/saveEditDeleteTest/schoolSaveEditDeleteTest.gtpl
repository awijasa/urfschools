<%
  import com.google.appengine.api.datastore.Entity
  import data.Enrollment
  import data.School
  import data.Student
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
  else {
	html.html {
	  head {
	    title( "School Save, Edit, and Delete Test" )
	  }
	  html.body {
	      Entity school
	      
	      /* For the Save Test, make sure that the School to create has not existed. */
	      assert School.findByName( "Saint John's College MN" ) == null, "School: Saint John's College MN has existed. Please change the test case."
	      
	      /* School Save Test with a missing action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) == null, "User was able to save a School using no action parameter."
	      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ) == null, "User was able to save a StudentMetaData using no action parameter"
	      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ) == null, "User was able to save an EnrollmentMetaData using no action parameter"
	    
	      /* School Save Test with an invalid action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "create" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) == null, "User was able to save a School using a create action parameter instead of save."
		  assert Student.findMetaDataBySchoolName( "Saint John's College MN" ) == null, "User was able to save a StudentMetaData using a create action parameter instead of save"
	      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ) == null, "User was able to save an EnrollmentMetaData using a create action parameter instead of save"
	    	    
	      /* School Save Test with a missing schoolName parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) == null, "User was able to save a School using no schoolName parameter."
		  assert Student.findMetaDataBySchoolName( "Saint John's College MN" ) == null, "User was able to save a StudentMetaData using no schoolName parameter"
	      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ) == null, "User was able to save an EnrollmentMetaData using no schoolName parameter"
	    	    
	      /* School Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
	      
	      school = School.findByName( "Saint John's College MN" )
	      
	      Date lastUpdateDate = new Date( school.lastUpdateDate.getTime() )
	    
	      assert school != null, "User was unable to save a School."
	      assert school.name == "Saint John's College MN", "School: ${ school.name } from School.findByName( def name ) is not equal to Saint John's College MN."
	      assert school.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ school.lastUpdateDate.clearTime() } from School.findByName( def name ) is not equal to ${ new Date().clearTime() }."
		  assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == 0, "User was unable to save a StudentMetaData"
	      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == 0, "User was unable to save an EnrollmentMetaData"
	    	    
	      def schoolCount = School.list().size()
	      
	      /* School Duplicate Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
	      
	      assert schoolCount == School.list().size(), "User was able to save a Duplicate School."
	      
	      /* School Delete Test with a missing action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "id", school.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) != null, "User was able to delete a School using no action parameter."
		  assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == 0, "User was able to delete a StudentMetaData using no action parameter"
	      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == 0, "User was able to delete an EnrollmentMetaData using no action parameter"
	    	    
	      /* School Delete Test with an invalid action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "remove" )
	        .addQueryParam( "id", school.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) != null, "User was able to delete a School using an invalid action parameter."
		  assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == 0, "User was able to delete a StudentMetaData using an invalid action parameter"
	      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == 0, "User was able to delete an EnrollmentMetaData using an invalid action parameter"
	    	    	
	      /* School Delete Test with no id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) != null, "User was able to delete a School using no id parameter."
		  assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == 0, "User was able to delete a StudentMetaData using no id parameter"
	      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == 0, "User was able to delete an EnrollmentMetaData using no id parameter"
	    	    
	      /* School Delete Test with an invalid id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", "-1" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) != null, "User was able to delete a School using an invalid id parameter."
		  assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == 0, "User was able to delete a StudentMetaData using an invalid id parameter"
	      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == 0, "User was able to delete an EnrollmentMetaData using an invalid id parameter"
	    	    
	      /* School Delete Test with no lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", school.getKey().getId() ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) != null, "User was able to delete a School using no lastUpdateDate parameter."
		  assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == 0, "User was able to delete a StudentMetaData using no lastUpdateDate parameter"
	      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == 0, "User was able to delete an EnrollmentMetaData using no lastUpdateDate parameter"
	    	    
	      /* School Delete Test with an invalid lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", school.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) != null, "User was able to delete a School using an invalid lastUpdateDate parameter."
		  assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == 0, "User was able to delete a StudentMetaData using an invaid lastUpdateDate parameter"
	      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == 0, "User was able to delete an EnrollmentMetaData using an invalid lastUpdateDate parameter"
	    
	      /* School Delete Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", school.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) == null, "User was unable to delete a School."
		  assert Student.findMetaDataBySchoolName( "Saint John's College MN" ) == null, "User was unable to delete a StudentMetaData"
	      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ) == null, "User was unable to delete an EnrollmentMetaData"
	    
	      p( "School Save and Delete Test is successful." )
	  }
	}
 }
%>