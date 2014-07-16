<%
  import com.google.appengine.api.datastore.Entity
  import com.google.appengine.api.memcache.MemcacheService
  import com.google.appengine.api.memcache.MemcacheServiceFactory
  import com.google.appengine.api.search.Document
  import data.Payment
  import data.PaymentMemcache
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
	    <title>Payment Memcache Test</title>
	  </head>
	  <body>
	    <%
	      Entity payment
	      
	      if( School.findByName( "Saint John's College MN" ) == null ) {
	      	
	      	/* Save a School and Classes for the Payment to test */
		      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
		        .addQueryParam( "action", "save" )
		        .addQueryParam( "schoolName", "Saint John's College MN" )
		        .addQueryParam( "newClass1", "Freshman" )
		        .addQueryParam( "newClass2", "Sophomore" )
		        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
	      }
	      
	      if( Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1902 ) == null ) {
	      	
	      	/* Save a Term for the Payment to test */
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
	      	
	      	/* Save a Student for the Payment to test */
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
	      
	      /* Create a new Payment if there is none existing. */
	      if( Payment.findByLimitAndOffset( 20, 0 ).size() == 0 ) {
	        urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
		        .addQueryParam( "studentId", "0202aoey" )
		        .addQueryParam( "fundingSource", "Self" )
		        .addQueryParam( "schoolName", "Saint John's College MN" )
		        .addQueryParam( "enrollTermNo", "1" )
		        .addQueryParam( "enrollTermYear", "1902" )
		        .addQueryParam( "classTermNo", "1" )
		        .addQueryParam( "classTermYear", "1902" )
		        .addQueryParam( "amount", "12,345.67" )
		        .addQueryParam( "comment", "Won a lottery" ).toURL() )
		  	
		  	payment = Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Self", "0202aoey", "Saint John's College MN", 1, 1902 )
	      }
	      
	      String urfUserSchools = ""
	      UserPrivilege.findByUserEmail( user.getEmail() ).each() {
	        urfUserSchools += "[" + it.schoolName + "]"
	      }
	      
	      /* Clear the Memcache before testing the Payment Memcache. */
	      memcache.clearAll()
	      
	      PaymentMemcache.list()*.delete()
	        
	      List<Entity> paymentList = Payment.findByLimitAndOffset( 20, 0 )
	      MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService( "Payment" )
	      List<Entity> memcachePaymentList = memcacheService.get( "paymentList of " + urfUserSchools + " row 1 to 20" )
	      
	      assert PaymentMemcache.findByMemcacheKey( "paymentList of " + urfUserSchools + " row 1 to 20" ), "paymentList of ${ urfUserSchools } row 1 to 20 is not available as a PaymentMemcache entity."
	      
	      /* Query Test: Confirm that the Payment Memcache contains the recently queried Payment. */
	      paymentList.eachWithIndex(
	      	{ obj, i ->
		          Entity memcachePayment = memcachePaymentList.getAt( i )
		          assert obj.fundingSource == memcachePayment.fundingSource, "Funding Source: ${ obj.fundingSource } from Payment.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcachePayment.fundingSource } from the Memcache."
		          assert obj.studentId == memcachePayment.studentId, "Student ID: ${ obj.studentId } from Payment.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcachePayment.studentId } from the Memcache."
		          assert obj.schoolName == memcachePayment.schoolName, "School Name: ${ obj.schoolName } from Payment.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcachePayment.schoolName } from the Memcache."
		          assert obj.enrollTermNo == memcachePayment.enrollTermNo, "Enrollment Term No: ${ obj.enrollTermNo } from Payment.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcachePayment.enrollTermNo } from the Memcache."
		          assert obj.enrollTermYear == memcachePayment.enrollTermYear, "Enrollment Term Year: ${ obj.enrollTermYear } from Payment.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcachePayment.enrollTermYear } from the Memcache."
		          assert obj.classTermNo == memcachePayment.classTermNo, "Class Term No: ${ obj.classTermNo } from Payment.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcachePayment.classTermNo } from the Memcache."
		          assert obj.classTermYear == memcachePayment.classTermYear, "Class Term Year: ${ obj.classTermYear } from Payment.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcachePayment.classTermYear } from the Memcache."
		          assert obj.amount == memcachePayment.amount, "Amount: ${ obj.amount } from Payment.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcachePayment.amount } from the Memcache."
		          assert obj.comment == memcachePayment.comment, "Comment: ${ obj.comment } from Payment.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcachePayment.comment } from the Memcache."
		          assert obj.lastUpdateDate == memcachePayment.lastUpdateDate, "Last Update Date: ${ obj.lastUpdateDate } from Payment.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcachePayment.lastUpdateDate } from the Memcache."
		          assert obj.lastUpdateUser == memcachePayment.lastUpdateUser, "Last Update User: ${ obj.lastUpdateUser } from Payment.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcachePayment.lastUpdateUser } from the Memcache."
	      	}
	      )
	      
	      /* Delete the Payment created just for the query test. */
	      payment?.delete()
	        
	      /* PaymentController Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
	        .addQueryParam( "action", "save" ).toURL() )
	      assert memcacheService.contains( "paymentList of " + urfUserSchools + " row 1 to 20" ), "paymentList of ${ urfUserSchools } row 1 to 20 Memcache is not found. It must be retained when a Payment save encounters an error."
	      assert PaymentMemcache.findByMemcacheKey( "paymentList of " + urfUserSchools + " row 1 to 20" ), "paymentList of ${ urfUserSchools } row 1 to 20 is not available as a PaymentMemcache entity. It must be retained when a Payment save encounters an error."
	        
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "studentId", "0202aoey" )
	        .addQueryParam( "fundingSource", "Self" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "enrollTermNo", "1" )
	        .addQueryParam( "enrollTermYear", "1902" )
	        .addQueryParam( "classTermNo", "1" )
	        .addQueryParam( "classTermYear", "1902" )
	        .addQueryParam( "amount", "12,345.67" )
	        .addQueryParam( "comment", "Won a lottery" ).toURL() )
	      
	      assert !memcacheService.contains( "paymentList of " + urfUserSchools + " row 1 to 20" ), "paymentList of ${ urfUserSchools } row 1 to 20 Memcache is found. It must be deleted when a new Payment is saved."
	      assert !PaymentMemcache.findByMemcacheKey( "paymentList of " + urfUserSchools + " row 1 to 20" ), "paymentList of ${ urfUserSchools } row 1 to 20 is available as a PaymentMemcache entity. It must be deleted when a Payment is saved."
	      
	      /* Create the Payment Memcache for the Delete Test. */
	      Payment.findByLimitAndOffset( 20, 0 )
	      
	      payment = Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Self", "0202aoey", "Saint John's College MN", 1, 1902 )
	      
	      Date lastUpdateDate = payment.lastUpdateDate
	      
	      /* PaymentController Delete Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", "-1" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert memcacheService.contains( "paymentList of " + urfUserSchools + " row 1 to 20" ), "paymentList of ${ urfUserSchools } row 1 to 20 Memcache is not found. It must be retained when a Payment delete encounters an error."
	      assert PaymentMemcache.findByMemcacheKey( "paymentList of " + urfUserSchools + " row 1 to 20" ), "paymentList of ${ urfUserSchools } row 1 to 20 PaymentMemcache is not found. It must be retained when a Payment delete encounters an error."
	          
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", payment.getKey().getId() )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert !memcacheService.contains( "paymentList of " + urfUserSchools + " row 1 to 20" ), "paymentList of ${ urfUserSchools } row 1 to 20 Memcache is found. It must be deleted when a Payment is deleted."
	      assert !PaymentMemcache.findByMemcacheKey( "paymentList of " + urfUserSchools + " row 1 to 20" ), "paymentList of ${ urfUserSchools } row 1 to 20 PaymentMemcache is found. It must be deleted when a Payment is deleted."
	      
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
	    <p>Payment Memcache Test is successful.</p>
	  </body>
	</html>
<% } %>