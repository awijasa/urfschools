<%
  import com.google.appengine.api.datastore.Entity
  import com.google.appengine.api.memcache.MemcacheService
  import com.google.appengine.api.memcache.MemcacheServiceFactory
  import com.google.appengine.api.search.Document
  import data.Fee
  import data.FeeMemcache
  import data.School
  import data.StudentDocument
  import data.Term
  import data.UserPrivilege
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
  else {
%>
	<html>
	  <head>
	    <title>Fee Memcache Test</title>
	  </head>
	  <body>
	    <%
	      Entity fee
	      
	      if( School.findByName( "Saint John's College MN" ) == null ) {
	      	
	      	/* Save a School and Classes for the Fee to test */
		      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
		        .addQueryParam( "action", "save" )
		        .addQueryParam( "schoolName", "Saint John's College MN" )
		        .addQueryParam( "newClass1", "Freshman" )
		        .addQueryParam( "newClass2", "Sophomore" )
		        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
	      }
	      
	      if( Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1902 ) == null ) {
	      	
	      	/* Save a Term for the Fee to test */
		      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
		        .addQueryParam( "action", "save" )
		        .addQueryParam( "termSchool", "Saint John's College MN" )
		        .addQueryParam( "termNo", 1 )
		        .addQueryParam( "year", 1902 )
		        .addQueryParam( "startDate", "Feb 1 1902" )
		        .addQueryParam( "endDate", "Apr 30 1902" )
		        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", 60000 )
		        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", 60000 )
		        .addQueryParam( "boardingFeeSaint John's College MNFreshman", 100000 )
		        .addQueryParam( "boardingFeeSaint John's College MNSophomore", 100000 ).toURL() )
	      }
	      
	      if( StudentDocument.findByStudentId( "0202aoey" ) == null ) {
	      	
	      	/* Save a Student for the Fee to test */
	      	urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
		        .addQueryParam( "action", "save" )
		        .addQueryParam( "firstName", "Adrian" )
		        .addQueryParam( "lastName", "Oey" )
		        .addQueryParam( "birthDate", "Sep 9 1900" )
		        .addQueryParam( "village", "Kyetume" )
		        .addQueryParam( "enrollmentSchool", "Saint John's College MN" )
		        .addQueryParam( "enrollmentTermSchool1", "1902 Term 1" )
		        .addQueryParam( "enrollmentFirstClassAttendedSchool1", "Freshman" )
		        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Freshman" )
		        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
		        .addQueryParam( "boardingInd1902 Term 1", true ).toURL() )
	      }
	      
	      /* Create a new Fee if there is none existing. */
	      if( Fee.findByLimitAndOffset( 20, 0 ).size() == 0 ) {
	        urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
		        .addQueryParam( "studentId", "0202aoey" )
		        .addQueryParam( "name", "Admission" )
		        .addQueryParam( "schoolName", "Saint John's College MN" )
		        .addQueryParam( "enrollTermNo", "1" )
		        .addQueryParam( "enrollTermYear", "1902" )
		        .addQueryParam( "classTermNo", "1" )
		        .addQueryParam( "classTermYear", "1902" )
		        .addQueryParam( "amount", "12,345.67" )
		        .addQueryParam( "comment", "Won a lottery" ).toURL() )
		  	
		  	fee = Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 )
	      }
	      
	      String urfUserSchools = ""
	      UserPrivilege.findByUserEmail( user.getEmail() ).each() {
	        urfUserSchools += "[" + it.schoolName + "]"
	      }
	      
	      /* Clear the Memcache before testing the Fee Memcache. */
	      memcache.clearAll()
	      
	      FeeMemcache.list()*.delete()
	        
	      List<Entity> feeList = Fee.findByLimitAndOffset( 20, 0 )
	      MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService( "Fee" )
	      List<Entity> memcacheFeeList = memcacheService.get( "feeList of " + urfUserSchools + " row 1 to 20" )
	      
	      assert FeeMemcache.findByMemcacheKey( "feeList of " + urfUserSchools + " row 1 to 20" ), "feeList of ${ urfUserSchools } row 1 to 20 is not available as a FeeMemcache entity."
	      
	      /* Query Test: Confirm that the Fee Memcache contains the recently queried Fee. */
	      feeList.eachWithIndex(
	      	{ obj, i ->
		          Entity memcacheFee = memcacheFeeList.getAt( i )
		          assert obj.name == memcacheFee.name, "Name: ${ obj.name } from Fee.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheFee.name } from the Memcache."
		          assert obj.studentId == memcacheFee.studentId, "Student ID: ${ obj.studentId } from Fee.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheFee.studentId } from the Memcache."
		          assert obj.schoolName == memcacheFee.schoolName, "School Name: ${ obj.schoolName } from Fee.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheFee.schoolName } from the Memcache."
		          assert obj.enrollTermNo == memcacheFee.enrollTermNo, "Enrollment Term No: ${ obj.enrollTermNo } from Fee.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheFee.enrollTermNo } from the Memcache."
		          assert obj.enrollTermYear == memcacheFee.enrollTermYear, "Enrollment Term Year: ${ obj.enrollTermYear } from Fee.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheFee.enrollTermYear } from the Memcache."
		          assert obj.classTermNo == memcacheFee.classTermNo, "Class Term No: ${ obj.classTermNo } from Fee.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheFee.classTermNo } from the Memcache."
		          assert obj.classTermYear == memcacheFee.classTermYear, "Class Term Year: ${ obj.classTermYear } from Fee.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheFee.classTermYear } from the Memcache."
		          assert obj.amount == memcacheFee.amount, "Amount: ${ obj.amount } from Fee.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheFee.amount } from the Memcache."
		          assert obj.comment == memcacheFee.comment, "Comment: ${ obj.comment } from Fee.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheFee.comment } from the Memcache."
		          assert obj.lastUpdateDate == memcacheFee.lastUpdateDate, "Last Update Date: ${ obj.lastUpdateDate } from Fee.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheFee.lastUpdateDate } from the Memcache."
		          assert obj.lastUpdateUser == memcacheFee.lastUpdateUser, "Last Update User: ${ obj.lastUpdateUser } from Fee.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheFee.lastUpdateUser } from the Memcache."
	      	}
	      )
	      
	      /* Delete the Fee created just for the query test. */
	      fee?.delete()
	        
	      /* FeeController Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
	        .addQueryParam( "action", "save" ).toURL() )
	      assert memcacheService.contains( "feeList of " + urfUserSchools + " row 1 to 20" ), "feeList of ${ urfUserSchools } row 1 to 20 Memcache is not found. It must be retained when a Fee save encounters an error."
	      assert FeeMemcache.findByMemcacheKey( "feeList of " + urfUserSchools + " row 1 to 20" ), "feeList of ${ urfUserSchools } row 1 to 20 is not available as a FeeMemcache entity. It must be retained when a Fee save encounters an error."
	        
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "studentId", "0202aoey" )
	        .addQueryParam( "name", "Admission" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "enrollTermNo", "1" )
	        .addQueryParam( "enrollTermYear", "1902" )
	        .addQueryParam( "classTermNo", "1" )
	        .addQueryParam( "classTermYear", "1902" )
	        .addQueryParam( "amount", "12,345.67" )
	        .addQueryParam( "comment", "Won a lottery" ).toURL() )
	      
	      assert !memcacheService.contains( "feeList of " + urfUserSchools + " row 1 to 20" ), "feeList of ${ urfUserSchools } row 1 to 20 Memcache is found. It must be deleted when a new Fee is saved."
	      assert !FeeMemcache.findByMemcacheKey( "feeList of " + urfUserSchools + " row 1 to 20" ), "feeList of ${ urfUserSchools } row 1 to 20 is available as a FeeMemcache entity. It must be deleted when a Fee is saved."
	      
	      /* Create the Fee Memcache for the Delete Test. */
	      Fee.findByLimitAndOffset( 20, 0 )
	      
	      fee = Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 )
	      
	      Date lastUpdateDate = fee.lastUpdateDate
	      
	      /* FeeController Delete Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", "-1" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert memcacheService.contains( "feeList of " + urfUserSchools + " row 1 to 20" ), "feeList of ${ urfUserSchools } row 1 to 20 Memcache is not found. It must be retained when a Fee delete encounters an error."
	      assert FeeMemcache.findByMemcacheKey( "feeList of " + urfUserSchools + " row 1 to 20" ), "feeList of ${ urfUserSchools } row 1 to 20 FeeMemcache is not found. It must be retained when a Fee delete encounters an error."
	          
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", fee.getKey().getId() )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert !memcacheService.contains( "feeList of " + urfUserSchools + " row 1 to 20" ), "feeList of ${ urfUserSchools } row 1 to 20 Memcache is found. It must be deleted when a Fee is deleted."
	      assert !FeeMemcache.findByMemcacheKey( "feeList of " + urfUserSchools + " row 1 to 20" ), "feeList of ${ urfUserSchools } row 1 to 20 FeeMemcache is found. It must be deleted when a Fee is deleted."
	      
	      Document studentDocument = StudentDocument.findByStudentId( "0202aoey" )
	      
	      /* Delete the Student created for this Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", studentDocument.getId() )
	        .addQueryParam( "studentId", "0202aoey" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", studentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	        
	      Entity term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1902 )
	      
	      /* Delete the Term created for this Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", term.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	        
	      /* Delete the School created for this Test. */
	      Entity school = School.findByName( "Saint John's College MN" )
	    
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", school.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", school.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	    %>
	    <p>Fee Memcache Test is successful.</p>
	  </body>
	</html>
<% } %>