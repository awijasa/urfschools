<%
  import com.google.appengine.api.datastore.Entity
  import com.google.appengine.api.search.Document
  import data.AnonymousParentalRelationship
  import data.ClassAttended
  import data.Enrollment
  import data.EnrollmentDocument
  import data.Fee
  import data.Parent
  import data.ParentalRelationship
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
    <title>Fee Delete Test</title>
  </head>
  <body>
    <%
      Entity fee = Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      Date lastUpdateDate = fee.lastUpdateDate
      
      /* Fee Delete Test with a missing action parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "id", fee.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete a Fee using no action parameter."
      
      Document enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 76543.21, "User was able to update a EnrollmentDocument's otherFees using no action parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 296543.21, "User was able to update a EnrollmentDocument's feesDue using no action parameter"
      
      Document studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 76543.21, "User was able to update a StudentDocument's otherFees using no action parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 296543.21, "User was able to update a StudentDocument's feesDue using no action parameter"
      
      /* Fee Delete Test with an invalid action parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "remove" )
        .addQueryParam( "id", fee.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete a Fee using an invalid action parameter."
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 76543.21, "User was able to update a EnrollmentDocument's otherFees using an invalid action parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 296543.21, "User was able to update a EnrollmentDocument's feesDue using an invalid action parameter"
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 76543.21, "User was able to update a StudentDocument's otherFees using an invalid action parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 296543.21, "User was able to update a StudentDocument's feesDue using an invalid action parameter"
      
      /* Fee Delete Test with no id parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "name", "Admission" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete a Fee using no id parameter."
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 76543.21, "User was able to update a EnrollmentDocument's otherFees using no id parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 296543.21, "User was able to update a EnrollmentDocument's feesDue using no id parameter"
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 76543.21, "User was able to update a StudentDocument's otherFees using no id parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 296543.21, "User was able to update a StudentDocument's feesDue using no id parameter"
      
      /* Fee Delete Test with an invalid id parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", "-1" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete a Fee using an invalid id parameter."
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 76543.21, "User was able to update a EnrollmentDocument's otherFees using an invalid id parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 296543.21, "User was able to update a EnrollmentDocument's feesDue using an invalid id parameter"
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 76543.21, "User was able to update a StudentDocument's otherFees using an invalid id parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 296543.21, "User was able to update a StudentDocument's feesDue using an invalid id parameter"
      
      /* Fee Delete Test with no nextTwentyOffset parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", fee.getKey().getId() )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete a Fee using no nextTwentyOffset parameter."
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 76543.21, "User was able to update a EnrollmentDocument's otherFees using no nextTwentyOffset parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 296543.21, "User was able to update a EnrollmentDocument's feesDue using no nextTwentyOffset parameter"
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 76543.21, "User was able to update a StudentDocument's otherFees using no nextTwentyOffset parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 296543.21, "User was able to update a StudentDocument's feesDue using no nextTwentyOffset parameter"
      
      /* Fee Delete Test with no lastUpdateDate parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", fee.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 ).toURL() )
      assert Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete a Fee using no lastUpdateDate parameter."
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 76543.21, "User was able to update a EnrollmentDocument's otherFees using no lastUpdateDate parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 296543.21, "User was able to update a EnrollmentDocument's feesDue using no lastUpdateDate parameter"
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 76543.21, "User was able to update a StudentDocument's otherFees using no lastUpdateDate parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 296543.21, "User was able to update a StudentDocument's feesDue using no lastUpdateDate parameter"
      
      /* Fee Delete Test with an invalid lastUpdateDate parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", fee.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete a Fee using an invalid lastUpdateDate parameter."
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 76543.21, "User was able to update a EnrollmentDocument's otherFees using an invalid lastUpdateDate parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 296543.21, "User was able to update a EnrollmentDocument's feesDue using an invalid lastUpdateDate parameter"
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 76543.21, "User was able to update a StudentDocument's otherFees using an invalid lastUpdateDate parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 296543.21, "User was able to update a StudentDocument's feesDue using an invalid lastUpdateDate parameter"
      
      /* Fee Delete Test. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", fee.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 ) == null, "User was unable to delete a Fee"
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was unable to update a EnrollmentDocument's otherFees"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 220000, "User was unable to update a EnrollmentDocument's feesDue"
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was unable to update a StudentDocument's otherFees"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 220000, "User was unable to update a StudentDocument's feesDue"
      
      /* Delete the Student created for the Fee Save, Edit, and Delete Test */
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
    %>
    <p>Fee Delete Test is successful.</p>
  </body>
</html>