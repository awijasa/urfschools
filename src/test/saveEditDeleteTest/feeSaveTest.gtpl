<%
  import com.google.appengine.api.datastore.Entity
  import com.google.appengine.api.search.Document
  import data.EnrollmentDocument
  import data.Fee
  import data.ParentalRelationship
  import data.School
  import data.StudentDocument
  import data.Term
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
%>
<html>
  <head>
    <title>Fee Save Test</title>
  </head>
  <body>
    <%
      Entity fee
      
      /* For the Save Test, make sure that the Fee to create has not existed. */
      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "Student: 0202aoey has existed. Please change the test case."
      assert School.findByName( "Saint John's College MN" ) == null, "School: Saint John's College MN has existed. Please change the test case."
      
      /* Save a School and Classes for the Fee to be saved. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "newClass1", "Freshman" )
        .addQueryParam( "newClass2", "Sophomore" )
        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
      
      /* Save Terms for the Fee to be saved. */
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
      
      /* Fee Save Test with a missing action parameter. */
      
      log.info "Fee Save Test with a missing action parameter."
      
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
        
      Document enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      Document studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 ) == null, "User was able to save a Fee using no action parameter."
	  assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a EnrollmentDocument's otherFees using no action parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a EnrollmentDocument's feesDue using no action parameter"
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a StudentDocument's otherFees using no action parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a StudentDocument's feesDue using no action parameter"
      
      /* Fee Save Test with an invalid action parameter. */
      
      log.info "Fee Save Test with an invalid action parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "create" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "name", "Admission" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 ) == null, "User was able to save a Fee using an invalid action parameter."
	  assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a EnrollmentDocument's otherFees using an invalid action parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a EnrollmentDocument's feesDue using an invalid action parameter"
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a StudentDocument's otherFees using an invalid action parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a StudentDocument's feesDue using an invalid action parameter"
      
      /* Fee Save Test with a missing studentId parameter. */
      
      log.info "Fee Save Test with a missing studentId parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "name", "Admission" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Fee.findByNameAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a Fee using a missing studentId parameter."
	  assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a EnrollmentDocument's otherFees using a missing studentId parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a EnrollmentDocument's feesDue using a missing studentId parameter"
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a StudentDocument's otherFees using a missing studentId parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a StudentDocument's feesDue using a missing studentId parameter"
      
      /* Fee Save Test with a missing name parameter. */
      
      log.info "Fee Save Test with a missing name parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
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
      
      assert Fee.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a Fee using a missing name parameter."
	  assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a EnrollmentDocument's otherFees using a missing name parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a EnrollmentDocument's feesDue using a missing name parameter"
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a StudentDocument's otherFees using a missing name parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a StudentDocument's feesDue using a missing name parameter"
      
      /* Fee Save Test with a missing schoolName parameter. */
      
      log.info "Fee Save Test with a missing schoolName parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "name", "Admission" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndEnrollTermNoAndEnrollTermYear( "0202aoey", 1, 1902 )
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Fee.findByNameAndStudentIdAndClassTermNoAndClassTermYear( "Admission", "0202aoey", 1, 1902 ).size() == 0, "User was able to save a Fee using a missing schoolName parameter."
	  assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a EnrollmentDocument's otherFees using a missing schoolName parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a EnrollmentDocument's feesDue using a missing schoolName parameter"
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a StudentDocument's otherFees using a missing schoolName parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a StudentDocument's feesDue using a missing schoolName parameter"
      
      /* Fee Save Test with a missing enrollTermNo parameter. */
      
      log.info "Fee Save Test with a missing enrollTermNo parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/Feeontroller.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "name", "Admission" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 ) == null, "User was able to save a Fee using a missing enrollTermNo parameter."
	  assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a EnrollmentDocument's otherFees using a missing enrollTermNo parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a EnrollmentDocument's feesDue using a missing enrollTermNo parameter"
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a StudentDocument's otherFees using a missing enrollTermNo parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a StudentDocument's feesDue using a missing enrollTermNo parameter"
      
      /* Fee Save Test with a missing enrollTermYear parameter. */
      
      log.info "Fee Save Test with a missing enrollTermYear parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "name", "Admission" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 ) == null, "User was able to save a Fee using a missing enrollTermYear parameter."
	  assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a EnrollmentDocument's otherFees using a missing enrollTermYear parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a EnrollmentDocument's feesDue using a missing enrollTermYear parameter"
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a StudentDocument's otherFees using a missing enrollTermYear parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a StudentDocument's feesDue using a missing enrollTermYear parameter"
      
      /* Fee Save Test with a missing classTermNo parameter. */
      
      log.info "Fee Save Test with a missing classTermNo parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "create" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "name", "Admission" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Fee.findByNameAndStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a Fee using a missing classTermNo parameter."
	  assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a EnrollmentDocument's otherFees using a missing classTermNo parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a EnrollmentDocument's feesDue using a missing classTermNo parameter"
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a StudentDocument's otherFees using a missing classTermNo parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a StudentDocument's feesDue using a missing classTermNo parameter"
      
      /* Fee Save Test with a missing classTermYear parameter. */
      
      log.info "Fee Save Test with a missing classTermYear parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "name", "Admission" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Fee.findByNameAndStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a Fee using a missing classTermYear parameter."
	  assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a EnrollmentDocument's otherFees using a missing classTermYear parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a EnrollmentDocument's feesDue using a missing classTermYear parameter"
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a StudentDocument's otherFees using a missing classTermYear parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a StudentDocument's feesDue using a missing classTermYear parameter"
      
      /* Fee Save Test with a missing amount parameter. */
      
      log.info "Fee Save Test with a missing amount parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "name", "Admission" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 ) == null, "User was able to save a Fee using a missing amount parameter."
	  assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a EnrollmentDocument's otherFees using a missing amount parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a EnrollmentDocument's feesDue using a missing amount parameter"
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "User was able to update a StudentDocument's otherFees using a missing amount parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 232345.67, "User was able to update a StudentDocument's feesDue using a missing amount parameter"
      
      /* Fee Save Test */
      
      log.info "Fee Save Test"
      
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
      fee = Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert fee != null, "User was unable to save a Fee"
      assert fee.enrollTermNo == 1, "Enrollment Term No: ${ fee.enrollTermNo } is not equal to 1"
      assert fee.enrollTermYear == 1902, "Enrollment Term Year: ${ fee.enrollTermYear } is not equal to 1902"
      assert fee.amount == 12345.67, "Amount: ${ fee.amount } is not equal to 12345.67"
      assert fee.comment == "Won a lottery", "Comment: ${ fee.comment } is not equal to \"Won a lottery\""
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
	  assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 12345.67, "StudentDocument's otherFees: ${ enrollmentDocument.getOnlyField( "otherFees" ).getNumber() } is not equal to 12345.67"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 232345.67, "StudentDocument's feesDue: ${ enrollmentDocument.getOnlyField( "feesDue" ).getNumber() } is not equal to 232345.67"
    	      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 12345.67, "StudentDocument's otherFees: ${ studentDocument.getOnlyField( "otherFees" ).getNumber() } is not equal to 12345.67"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 232345.67, "StudentDocument's feesDue: ${ studentDocument.getOnlyField( "feesDue" ).getNumber() } is not equal to 232345.67"
      
      /* Duplicate Fee Save Test */
      
      log.info "Duplicate Fee Save Test"
      
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
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert Fee.findByNameAndStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 1, "User was able to save a Duplicate Fee"
	  assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() != 24691.34, "User was able to update a EnrollmentDocument's otherFees using a Duplicate Payment"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 244691.34, "User was able to update a EnrollmentDocument's feesDue using a Duplicate Payment"
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() != 24691.34, "User was able to update a StudentDocument's otherFees using a Duplicate Payment"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 244691.34, "User was able to update a StudentDocument's feesDue using a Duplicate Payment"
    %>
    <p>Fee Save Test is successful.</p>
  </body>
</html>