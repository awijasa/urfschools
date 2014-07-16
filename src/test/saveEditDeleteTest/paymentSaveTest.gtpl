<%
  import com.google.appengine.api.datastore.Entity
  import com.google.appengine.api.search.Document
  import data.EnrollmentDocument
  import data.ParentalRelationship
  import data.Payment
  import data.School
  import data.StudentDocument
  import data.Term
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
%>
<html>
  <head>
    <title>Payment Save Test</title>
  </head>
  <body>
    <%
      Entity payment
      
      /* For the Save Test, make sure that the Payment to create has not existed. */
      assert ParentalRelationship.findByParentalRelationship( "Darth Vader" ) == null, "Parental Relationship: Darth Vader has existed. Please change the test case."
      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "Student: 0202aoey has existed. Please change the test case."
      assert School.findByName( "Saint John's College MN" ) == null, "School: Saint John's College MN has existed. Please change the test case."
      
      /* Save a ParentalRelationship for the Payment to be saved. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "parentalRelationship", "Darth Vader" ).toURL() )
      
      /* Save a School and Classes for the Payment to be saved. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "newClass1", "Freshman" )
        .addQueryParam( "newClass2", "Sophomore" )
        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
      
      /* Save Terms for the Payment to be saved. */
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
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "termSchool", "Saint John's College MN" )
        .addQueryParam( "termNo", 2 )
        .addQueryParam( "year", 1902 )
        .addQueryParam( "startDate", "May 1 1902" )
        .addQueryParam( "endDate", "Jul 31 1902" )
        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", 60000 )
        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", 60000 )
        .addQueryParam( "boardingFeeSaint John's College MNFreshman", 100000 )
        .addQueryParam( "boardingFeeSaint John's College MNSophomore", 100000 ).toURL() )
        
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "firstName", "Adrian" )
        .addQueryParam( "lastName", "Oey" )
        .addQueryParam( "birthDate", "Sep 9 1900" )
        .addQueryParam( "village", "Kyetume" )
        .addQueryParam( "enrollmentSchool", "Saint John's College MN" )
        .addQueryParam( "enrollmentTermSchool1", "1902 Term 1" )
        .addQueryParam( "leaveTermSchool1", "1902 Term 2" )
        .addQueryParam( "enrollmentFirstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "boardingInd1902 Term 1", true ).toURL() )
      
      /* Save a Parent for the Payment to be saved */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "firstName", "Keng Tjoan" )
        .addQueryParam( "lastName", "Oey" )
        .addQueryParam( "newParentRelationship0", "Darth Vader" )
        .addQueryParam( "newChildId0", "0202aoey" ).toURL() )
      
      /* Payment Save Test with a missing action parameter. */
      
      log.info "Payment Save Test with a missing action parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "fundingSource", "koey" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      Document enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      Document studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 ) == null, "User was able to save a Payment using no action parameter."
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was able to update a EnrollmentDocument's payments using no action parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 207654.33, "User was able to update a EnrollmentDocument's feesDue using no action parameter"
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was able to update a StudentDocument's payments using no action parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 207654.33, "User was able to update a StudentDocument's feesDue using no action parameter"
      
      /* Payment Save Test with an invalid action parameter. */
      
      log.info "Payment Save Test with an invalid action parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "create" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "fundingSource", "koey" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 ) == null, "User was able to save a Payment using an invalid action parameter."
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was able to update a EnrollmentDocument's payments using an invalid action parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 207654.33, "User was able to update a EnrollmentDocument's feesDue using an invalid action parameter"
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was able to update a StudentDocument's payments using an invalid action parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 207654.33, "User was able to update a StudentDocument's feesDue using an invalid action parameter"
      
      /* Payment Save Test with a missing studentId parameter. */
      
      log.info "Payment Save Test with a missing studentId parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "fundingSource", "koey" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      assert Payment.findByFundingSourceAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a Payment using a missing studentId parameter."
      
      /* Payment Save Test with a missing fundingSource parameter. */
      
      log.info "Payment Save Test with a missing fundingSource parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Payment.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a Payment using a missing fundingSource parameter."
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was able to update a EnrollmentDocument's payments using a missing fundingSource parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 207654.33, "User was able to update a EnrollmentDocument's feesDue using a missing fundingSource parameter"
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was able to update a StudentDocument's payments using a missing fundingSource parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 207654.33, "User was able to update a StudentDocument's feesDue using a missing fundingSource parameter"
      
      /* Payment Save Test with a missing schoolName parameter. */
      
      log.info "Payment Save Test with a missing schoolName parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "fundingSource", "koey" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndEnrollTermNoAndEnrollTermYear( "0202aoey", 1, 1902 )
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Payment.findByFundingSourceAndStudentIdAndClassTermNoAndClassTermYear( "koey", "0202aoey", 1, 1902 ).size() == 0, "User was able to save a Payment using a missing schoolName parameter."
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was able to update a EnrollmentDocument's payments using a missing schoolName parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 207654.33, "User was able to update a EnrollmentDocument's feesDue using a missing schoolName parameter"
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was able to update a StudentDocument's payments using a missing schoolName parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 207654.33, "User was able to update a StudentDocument's feesDue using a missing schoolName parameter"
      
      /* Payment Save Test with a missing enrollTermNo parameter. */
      
      log.info "Payment Save Test with a missing enrollTermNo parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "fundingSource", "koey" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 ) == null, "User was able to save a Payment using a missing enrollTermNo parameter."
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was able to update a EnrollmentDocument's payments using a missing enrollTermNo parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 207654.33, "User was able to update a EnrollmentDocument's feesDue using a missing enrollTermNo parameter"
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was able to update a StudentDocument's payments using a missing enrollTermNo parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 207654.33, "User was able to update a StudentDocument's feesDue using a missing enrollTermNo parameter"
      
      /* Payment Save Test with a missing enrollTermYear parameter. */
      
      log.info "Payment Save Test with a missing enrollTermYear parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "fundingSource", "koey" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 ) == null, "User was able to save a Payment using a missing enrollTermYear parameter."
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was able to update a EnrollmentDocument's payments using a missing enrollTermYear parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 207654.33, "User was able to update a EnrollmentDocument's feesDue using a missing enrollTermYear parameter"
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was able to update a StudentDocument's payments using a missing enrollTermYear parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 207654.33, "User was able to update a StudentDocument's feesDue using a missing enrollTermYear parameter"
      
      /* Payment Save Test with a missing classTermNo parameter. */
      
      log.info "Payment Save Test with a missing classTermNo parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "create" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "fundingSource", "koey" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Payment.findByFundingSourceAndStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a Payment using a missing classTermNo parameter."
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was able to update a EnrollmentDocument's payments using a missing classTermNo parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 207654.33, "User was able to update a EnrollmentDocument's feesDue using a missing classTermNo parameter"
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was able to update a StudentDocument's payments using a missing classTermNo parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 207654.33, "User was able to update a StudentDocument's feesDue using a missing classTermNo parameter"
      
      /* Payment Save Test with a missing classTermYear parameter. */
      
      log.info "Payment Save Test with a missing classTermYear parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "fundingSource", "koey" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Payment.findByFundingSourceAndStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a Payment using a missing classTermYear parameter."
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was able to update a EnrollmentDocument's payments using a missing classTermYear parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 207654.33, "User was able to update a EnrollmentDocument's feesDue using a missing classTermYear parameter"
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was able to update a StudentDocument's payments using a missing classTermYear parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 207654.33, "User was able to update a StudentDocument's feesDue using a missing classTermYear parameter"
      
      /* Payment Save Test with a missing amount parameter. */
      
      log.info "Payment Save Test with a missing amount parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "fundingSource", "koey" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 ) == null, "User was able to save a Payment using a missing amount parameter."
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was able to update a EnrollmentDocument's payments using a missing amount parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 207654.33, "User was able to update a EnrollmentDocument's feesDue using a missing amount parameter"
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was able to update a StudentDocument's payments using a missing amount parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 207654.33, "User was able to update a StudentDocument's feesDue using a missing amount parameter"
      
      /* Payment Save Test */
      
      log.info "Payment Save Test"
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "fundingSource", "koey" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      payment = Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert payment != null, "User was unable to save a Payment"
      assert payment.enrollTermNo == 1, "Enrollment Term No: ${ payment.enrollTermNo } is not equal to 1"
      assert payment.enrollTermYear == 1902, "Enrollment Term Year: ${ payment.enrollTermYear } is not equal to 1902"
      assert payment.amount == 12345.67, "Amount: ${ payment.amount } is not equal to 12345.67"
      assert payment.comment == "Won a lottery", "Comment: ${ payment.comment } is not equal to \"Won a lottery\""
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
	  
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 12345.67, "EnrollmentDocument's payments: ${ enrollmentDocument.getOnlyField( "payments" ).getNumber() } is not equal to 12345.67"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 207654.33, "EnrollmentDocument's feesDue: ${ enrollmentDocument.getOnlyField( "feesDue" ).getNumber() } is not equal to 207654.33"
		      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 12345.67, "StudentDocument's payments: ${ studentDocument.getOnlyField( "payments" ).getNumber() } is not equal to 12345.67"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 207654.33, "StudentDocument's feesDue: ${ studentDocument.getOnlyField( "feesDue" ).getNumber() } is not equal to 207654.33"
      
      /* Duplicate Payment Save Test */
      
      log.info "Duplicate Payment Save Test"
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "fundingSource", "koey" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
	  studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Payment.findByFundingSourceAndStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 1, "User was able to save a Duplicate Payment"
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() != 24691.34, "User was able to update a EnrollmentDocument's payments using a Duplicate Payment"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 195308.66, "User was able to update a EnrollmentDocument's feesDue using a Duplicate Payment"
      assert studentDocument.getOnlyField( "payments" ).getNumber() != 24691.34, "User was able to update a StudentDocument's payments using a Duplicate Payment"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 195308.66, "User was able to update a StudentDocument's feesDue using a Duplicate Payment"
    %>
    <p>Payment Save Test is successful.</p>
  </body>
</html>