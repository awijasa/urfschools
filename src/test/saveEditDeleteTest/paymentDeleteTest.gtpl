<%
  import com.google.appengine.api.datastore.Entity
  import com.google.appengine.api.search.Document
  import data.AnonymousParentalRelationship
  import data.ClassAttended
  import data.Enrollment
  import data.EnrollmentDocument
  import data.Parent
  import data.ParentalRelationship
  import data.Payment
  import data.Relationship
  import data.School
  import data.Student
  import data.StudentDocument
  import data.Term
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
%>
<html>
  <head>
    <title>Payment Delete Test</title>
  </head>
  <body>
    <%
      Entity payment = Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      Date lastUpdateDate = payment.lastUpdateDate
      
      /* Parent Delete Test. */
      Entity parent = Parent.findByParentId( "koey" )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", parent.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", parent.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert Parent.findByParentId( "koey" ) != null, "User was able to delete a Parent that is still used by a Payment."
      
      /* Payment Delete Test with a missing action parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "id", payment.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete a Payment using no action parameter."
      
	  Document enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 76543.21, "User was able to update a EnrollmentDocument's payments using a missing action parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 143456.79, "User was able to update a EnrollmentDocument's feesDue using a missing action parameter"
      
      Document studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 76543.21, "User was able to update a StudentDocument's payments using a missing action parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 143456.79, "User was able to update a StudentDocument's feesDue using a missing action parameter"
      
      /* Payment Delete Test with an invalid action parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "remove" )
        .addQueryParam( "id", payment.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete a Payment using an invalid action parameter."
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 76543.21, "User was able to update a EnrollmentDocument's payments using an invalid action parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 143456.79, "User was able to update a EnrollmentDocument's feesDue using an invalid action parameter"
		      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 76543.21, "User was able to update a StudentDocument's payments using an invalid action parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 143456.79, "User was able to update a StudentDocument's feesDue using an invalid action parameter"
      
      /* Payment Delete Test with no id parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "fundingSource", "koey" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete a Payment using no id parameter."
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
    	      
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 76543.21, "User was able to update a EnrollmentDocument's payments using no id parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 143456.79, "User was able to update a EnrollmentDocument's feesDue using no id parameter"
		      
	  studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 76543.21, "User was able to update a StudentDocument's payments using no id parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 143456.79, "User was able to update a StudentDocument's feesDue using no id parameter"
      
      /* Payment Delete Test with an invalid id parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", "-1" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete a Payment using an invalid id parameter."
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
    		    
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 76543.21, "User was able to update a EnrollmentDocument's payments using an invalid id parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 143456.79, "User was able to update a EnrollmentDocument's feesDue using an invalid id parameter"
		      
	  studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 76543.21, "User was able to update a StudentDocument's payments using an invalid id parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 143456.79, "User was able to update a StudentDocument's feesDue using an invalid id parameter"
      
      /* Payment Delete Test with no nextTwentyOffset parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", payment.getKey().getId() )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete a Payment using no nextTwentyOffset parameter."
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
    	    	
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 76543.21, "User was able to update a EnrollmentDocument's payments using no nextTwentyOffset parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 143456.79, "User was able to update a EnrollmentDocument's feesDue using no nextTwentyOffset parameter"
		      
	  studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 76543.21, "User was able to update a StudentDocument's payments using no nextTwentyOffset parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 143456.79, "User was able to update a StudentDocument's feesDue using no nextTwentyOffset parameter"
      
      /* Payment Delete Test with no lastUpdateDate parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", payment.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 ).toURL() )
      assert Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete a Payment using no lastUpdateDate parameter."
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
    	
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 76543.21, "User was able to update a EnrollmentDocument's payments using no lastUpdateDate parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 143456.79, "User was able to update a EnrollmentDocument's feesDue using no lastUpdateDate parameter"
		      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 76543.21, "User was able to update a StudentDocument's payments using no lastUpdateDate parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 143456.79, "User was able to update a StudentDocument's feesDue using no lastUpdateDate parameter"
      
      /* Payment Delete Test with an invalid lastUpdateDate parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", payment.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete a Payment using an invalid lastUpdateDate parameter."
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
    	
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 76543.21, "User was able to update a EnrollmentDocument's payments using an invalid lastUpdateDate parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 143456.79, "User was able to update a EnrollmentDocument's feesDue using an invalid lastUpdateDate parameter"
		    
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 76543.21, "User was able to update a StudentDocument's payments using an invalid lastUpdateDate parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 143456.79, "User was able to update a StudentDocument's feesDue using an invalid lastUpdateDate parameter"
    
      /* Payment Delete Test. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", payment.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 ) == null, "User was unable to delete a Payment."
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
    	
	  assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was unable to update a EnrollmentDocument's payments"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 220000, "User was unable to update a EnrollmentDocument's feesDue"
		      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 0, "User was unable to update a StudentDocument's payments"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 220000, "User was unable to update a StudentDocument's feesDue"
      
      /* Delete the Student created for the Payment Save, Edit, and Delete Test */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", studentDocument.getId() )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", studentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "User was unable to delete a Student."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) == null, "User was unable to delete a Student."
      assert Student.findByStudentId( "0202aoey" ) == null, "User was unable to delete a Student."
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() == 0, "User was unable to delete a Student."
      assert Relationship.findByStudentId( "0202aoey" ).size() == 0, "User was unable to delete a Student."
      assert Enrollment.findByStudentId( "0202aoey" ).size() == 0, "User was unable to delete a Student."
      assert ClassAttended.findByStudentId( "0202aoey" ).size() == 0, "User was unable to delete a Student"
      
      /* Delete the ParentalRelationships created for the Payment Save, Edit, and Delete Test. */
      Entity parentalRelationship = ParentalRelationship.findByParentalRelationship( "Darth Vader" )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", parentalRelationship.getKey().getId() )
        .addQueryParam( "lastUpdateDate", parentalRelationship.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      /* Delete the Terms created for the Student Save, Edit, and Delete Test. */
      Entity term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1902 )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", term.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", term.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
        
      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 2, 1902 )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", term.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", term.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
        
      /* Delete the Schools and Classes created for the Student Save, Edit, and Delete Test. */
      school = School.findByName( "Saint John's College MN" )
    
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", school.getKey().getId() )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "userEmail", user?.getEmail() )
        .addQueryParam( "lastUpdateDate", school.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
        
      /* Delete the Parents created for the Student Save, Edit, and Delete Test */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", parent.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", parent.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
    %>
    <p>Payment Delete Test is successful.</p>
  </body>
</html>