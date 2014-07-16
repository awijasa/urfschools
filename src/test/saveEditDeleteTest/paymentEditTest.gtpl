<%
  import com.google.appengine.api.datastore.Entity
  import com.google.appengine.api.search.Document
  import data.EnrollmentDocument
  import data.Payment
  import data.StudentDocument
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
%>
<html>
  <head>
    <title>Payment Edit Test</title>
  </head>
  <body>
    <%
      Entity payment
      
      payment = Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      Date lastUpdateDate = new Date( payment.lastUpdateDate.getTime() )
      
      /* Payment Edit Test with a missing action parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "id", payment.getKey().getId() )
        .addQueryParam( "amount", "76,543.21" )
        .addQueryParam( "comment", "Won Project Runway" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      payment = Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert payment.amount != 76543.21, "User was able to edit a Payment using a missing action parameter."
      assert payment.comment != "Won Project Runway", "User was able to edit a Payment using a missing action parameter."
      
	  Document enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollmentDocument.getOnlyField( "payments" ).getNumber() != 76543.21, "User was able to update a EnrollmentDocument's payments using a missing action parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 143456.79, "User was able to update a EnrollmentDocument's feesDue using a missing action parameter"
      
      Document studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "payments" ).getNumber() != 76543.21, "User was able to update a StudentDocument's payments using a missing action parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 143456.79, "User was able to update a StudentDocument's feesDue using a missing action parameter"
      
      /* Payment Edit Test with an invalid action parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "modify" )
        .addQueryParam( "id", payment.getKey().getId() )
        .addQueryParam( "amount", "76,543.21" )
        .addQueryParam( "comment", "Won Project Runway" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      payment = Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert payment.amount != 76543.21, "User was able to edit a Payment using an invalid action parameter."
      assert payment.comment != "Won Project Runway", "User was able to edit a Payment using an invalid action parameter."
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollmentDocument.getOnlyField( "payments" ).getNumber() != 76543.21, "User was able to update a EnrollmentDocument's payments using an invalid action parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 143456.79, "User was able to update a EnrollmentDocument's feesDue using an invalid action parameter"
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "payments" ).getNumber() != 76543.21, "User was able to update a StudentDocument's payments using an invalid action parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 143456.79, "User was able to update a StudentDocument's feesDue using an invalid action parameter"
            
      /* Payment Edit Test with a missing id parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "fundingSource", "koey" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "76,543.21" )
        .addQueryParam( "comment", "Won Project Runway" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      payment = Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert payment.amount != 76543.21, "User was able to edit a Payment using a missing id parameter."
      assert payment.comment != "Won Project Runway", "User was able to edit a Payment using a missing id parameter."
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
    	      
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() != 76543.21, "User was able to update a EnrollmentDocument's payments using a missing id parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 143456.79, "User was able to update a EnrollmentDocument's feesDue using a missing id parameter"
		      
	  studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "payments" ).getNumber() != 76543.21, "User was able to update a StudentDocument's payments using a missing id parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 143456.79, "User was able to update a StudentDocument's feesDue using a missing id parameter"
      
      /* Payment Edit Test with a missing amount parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "id", payment.getKey().getId() )
        .addQueryParam( "comment", "Won Project Runway" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      payment = Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert payment.amount != 76543.21, "User was able to edit a Payment using a missing amount parameter."
      assert payment.comment != "Won Project Runway", "User was able to edit a Payment using a missing amount parameter."
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
    	
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() != 76543.21, "User was able to update a EnrollmentDocument's payments using a missing amount parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 143456.79, "User was able to update a EnrollmentDocument's feesDue using a missing amount parameter"
		      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "payments" ).getNumber() != 76543.21, "User was able to update a StudentDocument's payments using a missing amount parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 143456.79, "User was able to update a StudentDocument's feesDue using a missing amount parameter"
      
      /* Payment Edit Test with a missing lastUpdateDate parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "id", payment.getKey().getId() )
        .addQueryParam( "comment", "Won Project Runway" ).toURL() )
      
      payment = Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert payment.amount != 76543.21, "User was able to edit a Payment using a missing lastUpdateDate parameter."
      assert payment.comment != "Won Project Runway", "User was able to edit a Payment using a missing lastUpdateDate parameter."
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
    
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() != 76543.21, "User was able to update a EnrollmentDocument's payments using a missing lastUpdateDate parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 143456.79, "User was able to update a EnrollmentDocument's feesDue using a missing lastUpdateDate parameter"
		      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "payments" ).getNumber() != 76543.21, "User was able to update a StudentDocument's payments using a missing lastUpdateDate parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 143456.79, "User was able to update a StudentDocument's feesDue using a missing lastUpdateDate parameter"
            
      /* Payment Edit Test with an invalid lastUpdateDate parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "id", payment.getKey().getId() )
        .addQueryParam( "comment", "Won Project Runway" )
        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      payment = Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert payment.amount != 76543.21, "User was able to edit a Payment using an invalid lastUpdateDate parameter."
      assert payment.comment != "Won Project Runway", "User was able to edit a Payment using an invalid lastUpdateDate parameter."
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
    
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() != 76543.21, "User was able to update a EnrollmentDocument's payments using an invalid lastUpdateDate parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 143456.79, "User was able to update a EnrollmentDocument's feesDue using an invalid lastUpdateDate parameter"
		      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "payments" ).getNumber() != 76543.21, "User was able to update a StudentDocument's payments using an invalid lastUpdateDate parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 143456.79, "User was able to update a StudentDocument's feesDue using an invalid lastUpdateDate parameter"
            
      /* Payment Edit Test */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "id", payment.getKey().getId() )
        .addQueryParam( "amount", "76,543.21" )
        .addQueryParam( "comment", "Won Project Runway" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      payment = Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert payment.amount == 76543.21, "User was unable to edit a Payment"
      assert payment.comment == "Won Project Runway", "User was unable to edit a Payment"
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
    		    
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 76543.21, "User was unable to update a EnrollmentDocument's payments"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 143456.79, "User was unable to update a EnrollmentDocument's feesDue"
		      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 76543.21, "User was unable to update a StudentDocument's payments"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 143456.79, "User was unable to update a StudentDocument's feesDue"
    %>
    <p>Payment Edit Test is successful.</p>
  </body>
</html>