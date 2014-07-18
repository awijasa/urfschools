<%
  import com.google.appengine.api.datastore.Entity
  import com.google.appengine.api.search.Document
  import data.ClassAttended
  import data.ClassFees
  import data.EnrollmentDocument
  import data.School
  import data.StudentDocument
  import data.Term
  import data.URFUser
  import data.UserPrivilege
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
  else {
%>
	<html>
	  <head>
	    <title>Term Delete Test</title>
	  </head>
	  <body>
	    <%
	      Entity classAttended
	      Entity freshmanClassFees
	      Entity juniorClassFees
	      Entity seniorClassFees
	      Entity sophomoreClassFees
	      Entity term
	      
	      /* Give the testing user an Admin Modify privilege. */
	      Entity urfUser = URFUser.findByEmail( user.getEmail() )
	      Entity urfUserBackup = URFUser.findByEmail( user.getEmail() )
	      
	      if( urfUser == null )
	        urfUser = new Entity( "URFUser" )
	        
	      urfUser.email = user.getEmail()
	      urfUser.adminPrivilege = "Modify"
	      urfUser?.lastUpdateDate = new Date()
	      urfUser.lastUpdateUser = user.getEmail()
	      urfUser.save()
	      
	      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 )
	      
	      Date lastUpdateDate = new Date( term?.lastUpdateDate?.getTime() )
	      
	      /* School Delete Test. */
	      
	      Entity school = School.findByName( "Saint John's College MN" )
	    
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", school.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", school?.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) != null, "User was able to delete a School that is still used by a Term."
	      
	      /* Term Delete Test with a missing action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) != null, "User was able to delete a Term using no action parameter."
	      assert ClassFees.findBySchoolNameAndTermNoAndTermYear( "Saint John's College MN", 1, 1901 ).size() > 0, "User was able to delete ClassFees using no action parameter"
	      
	      /* Term Delete Test with an invalid action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "remove" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) != null, "User was able to delete a Term using an invalid action parameter."
	      assert ClassFees.findBySchoolNameAndTermNoAndTermYear( "Saint John's College MN", 1, 1901 ).size() > 0, "User was able to delete ClassFees using an invalid action parameter"
	      
	      /* Term Delete Test with no id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) != null, "User was able to delete a Term using no id parameter."
	      assert ClassFees.findBySchoolNameAndTermNoAndTermYear( "Saint John's College MN", 1, 1901 ).size() > 0, "User was able to delete ClassFees using no id parameter"
	      
	      /* Term Delete Test with an invalid id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", "-1" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) != null, "User was able to delete a Term using an invalid id parameter."
	      assert ClassFees.findBySchoolNameAndTermNoAndTermYear( "Saint John's College MN", 1, 1901 ).size() > 0, "User was able to delete ClassFees using an invalid id parameter"
	      
	      /* Term Delete Test with no nextTwentyOffset parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) != null, "User was able to delete a Term using no nextTwentyOffset parameter."
	      assert ClassFees.findBySchoolNameAndTermNoAndTermYear( "Saint John's College MN", 1, 1901 ).size() > 0, "User was able to delete ClassFees using no nextTwentyOffset parameter"
	      
	      /* Term Delete Test with no lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "nextTwentyOffset", 20 ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) != null, "User was able to delete a Term using no lastUpdateDate parameter."
	      assert ClassFees.findBySchoolNameAndTermNoAndTermYear( "Saint John's College MN", 1, 1901 ).size() > 0, "User was able to delete ClassFees using no lastUpdateDate parameter"
	      
	      /* Term Delete Test with an invalid lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) != null, "User was able to delete a Term using an invalid lastUpdateDate parameter."
	      assert ClassFees.findBySchoolNameAndTermNoAndTermYear( "Saint John's College MN", 1, 1901 ).size() > 0, "User was able to delete ClassFees using an invalid lastUpdateDate parameter"
	    
	      /* Term Delete Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) == null, "User was unable to delete the Term: { termSchool: Saint John's College MN, termNo: 1, year: 1901 }."
	      assert ClassFees.findBySchoolNameAndTermNoAndTermYear( "Saint John's College MN", 1, 1901 ).size() == 0, "User was unable to delete ClassFees"
	      
	      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 2, 1901 )
	    
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", term?.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 2, 1901 ) == null, "User was unable to delete the Term: { termSchool: Saint John's College MN, termNo: 2, year: 1901 }."
	      
	      /* Delete the School created for the Term Save, Edit, and Delete Test. */
	      
	      school = School.findByName( "Saint John's College MN" )
	    
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", school.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", school?.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	    
	      /* Restore the Admin Privilege of the current user. */
	      urfUser?.delete()
	      urfUser = null
	      urfUserBackup?.save()
	    %>
	    <p>Term Delete Test is successful.</p>
	  </body>
	</html>
<% } %>