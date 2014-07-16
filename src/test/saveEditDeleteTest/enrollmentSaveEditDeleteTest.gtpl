<%
  import com.google.appengine.api.datastore.Entity
  import com.google.appengine.api.search.Document
  import data.Class
  import data.ClassAttended
  import data.Enrollment
  import data.EnrollmentDocument
  import data.Fee
  import data.Gender
  import data.LeaveReasonCategory
  import data.ParentalRelationship
  import data.Payment
  import data.School
  import data.Student
  import data.StudentDocument
  import data.Term
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
%>
<html>
  <head>
    <title>Enrollment Save, Edit, and Delete Test</title>
  </head>
  <body>
    <%
      Entity classAttended
      Entity enrollment
      Document enrollmentDocument
      Entity enrollmentMetaData
      Document studentDocument
      
      /* For the Save Test, make sure that the Enrollment to create has not existed. */
      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "Student: 0202aoey has existed. Please change the test case."
      assert Gender.findByCode( "X" ) == null, "Gender: X has existed. Please change the test case."
      assert LeaveReasonCategory.findByCategory( "Becoming a Millionaire" ) == null, "Leave Reason Category: Becoming a Millionaire has existed. Please change the test case."
      assert LeaveReasonCategory.findByCategory( "Becoming a Billionaire" ) == null, "Leave Reason Category: Becoming a Billionaire has existed. Please change the test case."
      assert ParentalRelationship.findByParentalRelationship( "Darth Vader" ) == null, "Parental Relationship: Darth Vader has existed. Please change the test case."
      assert School.findByName( "Saint John's College MN" ) == null, "School: Saint John's College MN has existed. Please change the test case."
      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) == null, "Class: { schoolName: Saint John's College MN, class: Freshman } has existed. Please change the test case."
      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) == null, "Class: { schoolName: Saint John's College MN, class: Sophomore } has existed. Please change the test case."
      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Junior" ) == null, "Class: { schoolName: Saint John's College MN, class: Junior } has existed. Please change the test case."
      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 2, 1901 ) == null, "Term: { termSchool: Saint John's College MN, termNo: 2, year: 1901 } has existed. Please change the test case."
      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 3, 1901 ) == null, "Term: { termSchool: Saint John's College MN, termNo: 3, year: 1901 } has existed. Please change the test case."
      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1902 ) == null, "Term: { termSchool: Saint John's College MN, termNo: 1, year: 1902 } has existed. Please change the test case."
      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 2, 1902 ) == null, "Term: { termSchool: Saint John's College MN, termNo: 2, year: 1902 } has existed. Please change the test case."
      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 3, 1902 ) == null, "Term: { termSchool: Saint John's College MN, termNo: 3, year: 1902 } has existed. Please change the test case."
      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1903 ) == null, "Term: { termSchool: Saint John's College MN, termNo: 1, year: 1903 } has existed. Please change the test case."
      
      /* Save a Gender for the Enrollment to be saved. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "code", "X" )
        .addQueryParam( "desc", "Tamale" ).toURL() )
      
      /* Save a LeaveReasonCategory for the Enrollment to be saved. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "category", "Becoming a Millionaire" ).toURL() )
        
		/* Save a LeaveReasonCategory for the Enrollment to be saved. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "category", "Becoming a Billionaire" ).toURL() )
      
      /* Save a ParentalRelationship for the Enrollment to be saved. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "parentalRelationship", "Darth Vader" ).toURL() )
      
      /* Save a School and Classes for the Enrollment to be saved. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "newClass1", "Freshman" )
        .addQueryParam( "newClass2", "Sophomore" )
        .addQueryParam( "newClass3", "Junior" )
        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
        
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "termSchool", "Saint John's College MN" )
        .addQueryParam( "termNo", 2 )
        .addQueryParam( "year", 1901 )
        .addQueryParam( "startDate", "May 1 1901" )
        .addQueryParam( "endDate", "Jul 31 1901" )
        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", 60000 )
        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", 60000 )
        .addQueryParam( "tuitionFeeSaint John's College MNJunior", 60000 )
        .addQueryParam( "boardingFeeSaint John's College MNFreshman", 100000 )
        .addQueryParam( "boardingFeeSaint John's College MNSophomore", 100000 )
        .addQueryParam( "boardingFeeSaint John's College MNJunior", 100000 ).toURL() )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "termSchool", "Saint John's College MN" )
        .addQueryParam( "termNo", 3 )
        .addQueryParam( "year", 1901 )
        .addQueryParam( "startDate", "Aug 1 1901" )
        .addQueryParam( "endDate", "Oct 31 1901" )
        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", 60000 )
        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", 60000 )
        .addQueryParam( "tuitionFeeSaint John's College MNJunior", 60000 )
        .addQueryParam( "boardingFeeSaint John's College MNFreshman", 100000 )
        .addQueryParam( "boardingFeeSaint John's College MNSophomore", 100000 )
        .addQueryParam( "boardingFeeSaint John's College MNJunior", 100000 ).toURL() )
      
      /* Save Terms for the Student to be saved. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "termSchool", "Saint John's College MN" )
        .addQueryParam( "termNo", 1 )
        .addQueryParam( "year", 1902 )
        .addQueryParam( "startDate", "Feb 1 1902" )
        .addQueryParam( "endDate", "Apr 30 1902" )
        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", 60000 )
        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", 60000 )
        .addQueryParam( "tuitionFeeSaint John's College MNJunior", 60000 )
        .addQueryParam( "boardingFeeSaint John's College MNFreshman", 100000 )
        .addQueryParam( "boardingFeeSaint John's College MNSophomore", 100000 )
        .addQueryParam( "boardingFeeSaint John's College MNJunior", 100000 ).toURL() )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "termSchool", "Saint John's College MN" )
        .addQueryParam( "termNo", 2 )
        .addQueryParam( "year", 1902 )
        .addQueryParam( "startDate", "May 1 1902" )
        .addQueryParam( "endDate", "Jul 31 1902" )
        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", 60000 )
        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", 60000 )
        .addQueryParam( "tuitionFeeSaint John's College MNJunior", 60000 )
        .addQueryParam( "boardingFeeSaint John's College MNFreshman", 100000 )
        .addQueryParam( "boardingFeeSaint John's College MNSophomore", 100000 )
        .addQueryParam( "boardingFeeSaint John's College MNJunior", 100000 ).toURL() )
        
		urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "termSchool", "Saint John's College MN" )
        .addQueryParam( "termNo", 3 )
        .addQueryParam( "year", 1902 )
        .addQueryParam( "startDate", "Aug 1 1902" )
        .addQueryParam( "endDate", "Oct 31 1902" )
        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", 60000 )
        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", 70000 )
        .addQueryParam( "tuitionFeeSaint John's College MNJunior", 70000 )
        .addQueryParam( "boardingFeeSaint John's College MNFreshman", 100000 )
        .addQueryParam( "boardingFeeSaint John's College MNSophomore", 110000 )
        .addQueryParam( "boardingFeeSaint John's College MNJunior", 110000 ).toURL() )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "termSchool", "Saint John's College MN" )
        .addQueryParam( "termNo", 1 )
        .addQueryParam( "year", 1903 )
        .addQueryParam( "startDate", "Feb 1 1903" )
        .addQueryParam( "endDate", "Apr 30 1903" )
        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", 60000 )
        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", 60000 )
        .addQueryParam( "tuitionFeeSaint John's College MNJunior", 60000 )
        .addQueryParam( "boardingFeeSaint John's College MNFreshman", 100000 )
        .addQueryParam( "boardingFeeSaint John's College MNSophomore", 100000 )
        .addQueryParam( "boardingFeeSaint John's College MNJunior", 100000 ).toURL() )
        
		/* Save a StudentDocument for the Enrollment to be saved. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "firstName", "Adrian" )
        .addQueryParam( "lastName", "Oey" )
        .addQueryParam( "birthDate", "Sep 9 1900" )
        .addQueryParam( "village", "Kyetume" )
        .addQueryParam( "genderCode", "X" )
        .addQueryParam( "specialInfo", "Luke's brother" )
        .addQueryParam( "enrollmentSchool", "Saint John's College MN" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "enrollmentLeaveReason", "Won a lottery" )
        .addQueryParam( "enrollmentTermSchool1", "1902 Term 1" )
        .addQueryParam( "leaveTermSchool1", "1902 Term 2" )
        .addQueryParam( "enrollmentFirstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Sophomore" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "boardingInd1902 Term 2", false )
        .addQueryParam( "anonymousDarth VaderDeceasedInd", true ).toURL() )
        
		Date lastUpdateDate = new Date( EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ).getOnlyField( "lastUpdateDate" ).getDate().getTime() )
      
      enrollmentMetaData = Enrollment.findMetaDataBySchoolName( "Saint John's College MN" )
      
      /* Enrollment Save Test with a missing action parameter. */
      
      log.info "Enrollment Save Test with a missing action parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "leaveReason", "Won a lottery" )
        .addQueryParam( "enrollTermSchool1", "1901 Term 3" )
        .addQueryParam( "leaveTermSchool1", "1901 Term 3" )
        .addQueryParam( "firstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "lastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1901 Term 3", "Freshman" )
        .addQueryParam( "boardingInd1901 Term 3", true ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an EnrollmentDocument using no action parameter."
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an Enrollment using no action parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData using no action parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save a ClassAttended using no action parameter"
      
      /* Enrollment Save Test with an invalid action parameter. */
      
      log.info "Enrollment Save Test with an invalid action parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "create" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "leaveReason", "Won a lottery" )
        .addQueryParam( "enrollTermSchool1", "1901 Term 3" )
        .addQueryParam( "leaveTermSchool1", "1901 Term 3" )
        .addQueryParam( "firstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "lastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1901 Term 3", "Freshman" )
        .addQueryParam( "boardingInd1901 Term 3", true ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an EnrollmentDocument using an invalid action parameter."
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an Enrollment using an invalid action parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData using an invalid action parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save a ClassAttended using an invalid action parameter"
      
      /* Enrollment Save Test with a missing studentId parameter. */
      
      log.info "Enrollment Save Test with a missing studentId parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "save" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "leaveReason", "Won a lottery" )
        .addQueryParam( "enrollTermSchool1", "1901 Term 3" )
        .addQueryParam( "leaveTermSchool1", "1901 Term 3" )
        .addQueryParam( "firstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "lastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1901 Term 3", "Freshman" )
        .addQueryParam( "boardingInd1901 Term 3", true ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an EnrollmentDocument using a missing studentId parameter."
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an Enrollment using a missing studentId parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData using an missing studentId parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save a ClassAttended using a missing studentId parameter"
      
      /* Enrollment Save Test with a missing schoolName parameter. */
      
      log.info "Enrollment Save Test with a missing schoolName parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "save" )
      	.addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "leaveReason", "Won a lottery" )
        .addQueryParam( "enrollTermSchool1", "1901 Term 3" )
        .addQueryParam( "leaveTermSchool1", "1901 Term 3" )
        .addQueryParam( "firstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "lastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1901 Term 3", "Freshman" )
        .addQueryParam( "boardingInd1901 Term 3", true ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an EnrollmentDocument using a missing schoolName parameter."
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an Enrollment using a missing schoolName parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData using an missing schoolName parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save a ClassAttended using a missing schoolName parameter"
    
      /* Enrollment Save Test with a missing enrollTermSchool1 parameter. */
      
      log.info "Enrollment Save Test with a missing enrollTermSchool1 parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "save" )
      	.addQueryParam( "studentId", "0202aoey" )
      	.addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "leaveReason", "Won a lottery" )
        .addQueryParam( "leaveTermSchool1", "1901 Term 3" )
        .addQueryParam( "firstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "lastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1901 Term 3", "Freshman" )
        .addQueryParam( "boardingInd1901 Term 3", true ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an EnrollmentDocument using a missing enrollTermSchol1 parameter."
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an Enrollment using a missing enrollTermSchool1 parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData using an missing enrollTermSchool1 parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save a ClassAttended using a missing enrollTermSchool1 parameter"
    
      /* Enrollment Save Test with an invalid enrollTermSchool1 parameter. */
      
      log.info "Enrollment Save Test with an invalid enrollTermSchool1 parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "save" )
      	.addQueryParam( "studentId", "0202aoey" )
      	.addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "leaveReason", "Won a lottery" )
        .addQueryParam( "enrollTermSchool1", "Nineteen O One Term Three" )
        .addQueryParam( "leaveTermSchool1", "1901 Term 3" )
        .addQueryParam( "firstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "lastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1901 Term 3", "Freshman" )
        .addQueryParam( "boardingInd1901 Term 3", true ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an EnrollmentDocument using an invalid enrollTermSchol1 parameter."
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an Enrollment using an invalid enrollTermSchool1 parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData using an invalid enrollTermSchool1 parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save a ClassAttended using an invalid enrollTermSchool1 parameter"
      
      /* Enrollment Save Test with an invalid enrollTermSchool1 parameter. */
      
      log.info "Enrollment Save Test with an invalid enrollTermSchool1 parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "save" )
      	.addQueryParam( "studentId", "0202aoey" )
      	.addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "leaveReason", "Won a lottery" )
        .addQueryParam( "enrollTermSchool1", "3 Term 1901" )
        .addQueryParam( "leaveTermSchool1", "1901 Term 3" )
        .addQueryParam( "firstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "lastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1901 Term 3", "Freshman" )
        .addQueryParam( "boardingInd1901 Term 3", true ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an EnrollmentDocument using an invalid enrollTermSchol1 parameter."
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an Enrollment using an invalid enrollTermSchool1 parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData using an invalid enrollTermSchool1 parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save a ClassAttended using an invalid enrollTermSchool1 parameter"
      
      /* Enrollment Save Test with an invalid leaveTermSchool1 parameter. */
      
      log.info "Enrollment Save Test with an invalid leaveTermSchool1 parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "save" )
      	.addQueryParam( "studentId", "0202aoey" )
      	.addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "leaveReason", "Won a lottery" )
        .addQueryParam( "enrollTermSchool1", "1901 Term 3" )
        .addQueryParam( "leaveTermSchool1", "Nineteen O One Term Three" )
        .addQueryParam( "firstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "lastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1901 Term 3", "Freshman" )
        .addQueryParam( "boardingInd1901 Term 3", true ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an EnrollmentDocument using an invalid leaveTermSchool1 parameter."
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an Enrollment using an invalid leaveTermSchool1 parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData using an invalid leaveTermSchool1 parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save a ClassAttended using an invalid leaveTermSchool1 parameter"
      
      /* Enrollment Save Test with an invalid leaveTermSchool1 parameter. */
      
      log.info "Enrollment Save Test with an invalid leaveTermSchool1 parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "save" )
      	.addQueryParam( "studentId", "0202aoey" )
      	.addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "leaveReason", "Won a lottery" )
        .addQueryParam( "enrollTermSchool1", "1901 Term 3" )
        .addQueryParam( "leaveTermSchool1", "3 Term 1901" )
        .addQueryParam( "firstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "lastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1901 Term 3", "Freshman" )
        .addQueryParam( "boardingInd1901 Term 3", true ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an EnrollmentDocument using an invalid leaveTermSchool1 parameter."
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an Enrollment using an invalid leaveTermSchool1 parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData using an invalid leaveTermSchool1 parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save a ClassAttended using an invalid leaveTermSchool1 parameter"
    
      /* Enrollment Save Test with an invalid term range. */
      
      log.info "Enrollment Save Test with an invalid term range."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "save" )
      	.addQueryParam( "studentId", "0202aoey" )
      	.addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "leaveReason", "Won a lottery" )
        .addQueryParam( "enrollTermSchool1", "1901 Term 3" )
        .addQueryParam( "leaveTermSchool1", "1901 Term 2" )
        .addQueryParam( "firstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "lastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1901 Term 3", "Freshman" )
        .addQueryParam( "boardingInd1901 Term 3", true ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an EnrollmentDocument using an invalid term range"
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an Enrollment using an invalid term range."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData using an invalid term range"
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save a ClassAttended using an invalid term range"
      
      /* Enrollment Save Test with a missing firstClassAttendedSchool1 parameter. */
      
      log.info "Enrollment Save Test with a missing firstClassAttendedSchool1 parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "save" )
      	.addQueryParam( "studentId", "0202aoey" )
      	.addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "leaveReason", "Won a lottery" )
        .addQueryParam( "enrollTermSchool1", "1901 Term 3" )
        .addQueryParam( "leaveTermSchool1", "1901 Term 3" )
        .addQueryParam( "lastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1901 Term 3", "Freshman" )
        .addQueryParam( "boardingInd1901 Term 3", true ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an EnrollmentDocument using a missing firstClassAttendedSchool1 parameter"
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an Enrollment using a missing firstClassAttendedSchool1 parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData using a missing firstClassAttendedSchool1 parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save a ClassAttended using a missing firstClassAttendedSchool1 parameter"
      
      /* Enrollment Save Test with a missing lastClassAttendedSchool1 parameter. */
      
      log.info "Enrollment Save Test with a missing lastClassAttendedSchool1 parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "save" )
      	.addQueryParam( "studentId", "0202aoey" )
      	.addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "leaveReason", "Won a lottery" )
        .addQueryParam( "enrollTermSchool1", "1901 Term 3" )
        .addQueryParam( "leaveTermSchool1", "1901 Term 3" )
        .addQueryParam( "firstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1901 Term 3", "Freshman" )
        .addQueryParam( "boardingInd1901 Term 3", true ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an EnrollmentDocument using a missing lastClassAttendedSchool1 parameter"
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an Enrollment using a missing lastClassAttendedSchool1 parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData using a missing lastClassAttendedSchool1 parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save a ClassAttended using a missing lastClassAttendedSchool1 parameter"
      
		/* Overlapping Enrollment Save Test. */
      
      log.info "Overlapping Enrollment Save Test 1."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "save" )
      	.addQueryParam( "studentId", "0202aoey" )
      	.addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "leaveReason", "Won a lottery" )
        .addQueryParam( "enrollTermSchool1", "1901 Term 3" )
        .addQueryParam( "leaveTermSchool1", "1902 Term 1" )
        .addQueryParam( "firstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "lastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1901 Term 3", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "boardingInd1901 Term 3", true )
        .addQueryParam( "boardingInd1902 Term 1", true ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an overlapping Enrollment"
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an overlapping Enrollment."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to save an overlapping Enrollment"
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an overlapping ClassAttended"
      
		log.info "Overlapping Enrollment Save Test 2."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "save" )
      	.addQueryParam( "studentId", "0202aoey" )
      	.addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "leaveReason", "Won a lottery" )
        .addQueryParam( "enrollTermSchool1", "1902 Term 1" )
        .addQueryParam( "leaveTermSchool1", "1902 Term 2" )
        .addQueryParam( "firstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "lastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "boardingInd1902 Term 2", false ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an overlapping Enrollment"
      assert Enrollment.findByStudentId( "0202aoey" ).size() == 1, "User was able to save an overlapping Enrollment."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to save an overlapping Enrollment"
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 2, 1902 ).getProperty( "class" ) == "Sophomore", "User was able to save an overlapping ClassAttended"

		log.info "Overlapping Enrollment Save Test 3."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "save" )
      	.addQueryParam( "studentId", "0202aoey" )
      	.addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "leaveReason", "Won a lottery" )
        .addQueryParam( "enrollTermSchool1", "1902 Term 2" )
        .addQueryParam( "leaveTermSchool1", "1902 Term 3" )
        .addQueryParam( "firstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "lastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "classAttended1902 Term 3", "Freshman" )
        .addQueryParam( "boardingInd1902 Term 2", false )
        .addQueryParam( "boardingInd1902 Term 3", false ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an overlapping Enrollment"
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 2, 1902 ) == null, "User was able to save an overlapping Enrollment."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to save an overlapping Enrollment"
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 2, 1902 ).getProperty( "class" ) == "Sophomore", "User was able to save an overlapping ClassAttended"
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1902 ) == null, "User was able to save an overlapping ClassAttended"
      
		log.info "Overlapping Enrollment Save Test 4."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "save" )
      	.addQueryParam( "studentId", "0202aoey" )
      	.addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "leaveReason", "Won a lottery" )
        .addQueryParam( "enrollTermSchool1", "1901 Term 3" )
        .addQueryParam( "leaveTermSchool1", "1902 Term 3" )
        .addQueryParam( "firstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "lastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1901 Term 3", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "classAttended1902 Term 3", "Freshman" )
        .addQueryParam( "boardingInd1901 Term 3", true )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "boardingInd1902 Term 2", false )
        .addQueryParam( "boardingInd1902 Term 3", false ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an overlapping Enrollment"
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an overlapping Enrollment."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to save an overlapping Enrollment"
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) == null, "User was able to save an overlapping ClassAttended"
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 2, 1902 ).getProperty( "class" ) == "Sophomore", "User was able to save an overlapping ClassAttended"
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1902 ) == null, "User was able to save an overlapping ClassAttended"

      /* Enrollment Prior to Existing Enrollment Save Test. */
      
      log.info "Enrollment Prior to Existing Enrollment Save Test."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "save" )
      	.addQueryParam( "studentId", "0202aoey" )
      	.addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "leaveReason", "Won a lottery" )
        .addQueryParam( "enrollTermSchool1", "1901 Term 3" )
        .addQueryParam( "leaveTermSchool1", "1901 Term 3" )
        .addQueryParam( "firstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "lastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1901 Term 3", "Freshman" )
        .addQueryParam( "boardingInd1901 Term 3", true ).toURL() )
      
      log.info "Testing StudentDocument value."
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument != null, "User was unable to save a StudentDocument."
      assert studentDocument.getOnlyField( "studentId" ).getAtom() == "0202aoey", "Student ID: ${ studentDocument.getOnlyField( "studentId" ).getAtom() } from StudentDocument.findByStudentId( def studentId ) is not equal to 0202aoey."
      assert studentDocument.getOnlyField( "firstName" ).getText() == "Adrian", "First Name: ${ studentDocument.getOnlyField( "firstName" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Adrian."
      assert studentDocument.getOnlyField( "lastName" ).getText() == "Oey", "Last Name: ${ studentDocument.getOnlyField( "lastName" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Oey."
      assert studentDocument.getOnlyField( "birthDate" ).getDate() == Date.parse( "MMM d yy", "Sep 9 1900" ), "Birth Date: ${ studentDocument.getOnlyField( "birthDate" ).getDate().format( "MMM d yy" ) } from StudentDocument.findByStudentId( def studentId ) is not equal to Sep 9 1900."
      assert studentDocument.getOnlyField( "village" ).getText() == "Kyetume", "Village: ${ studentDocument.getOnlyField( "village" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Kyetume."
      assert studentDocument.getOnlyField( "genderCode" ).getText() == "X", "Gender Code: ${ studentDocument.getOnlyField( "genderCode" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to X."
      assert studentDocument.getOnlyField( "specialInfo" ).getText() == "Luke's brother", "Special Info: ${ studentDocument.getOnlyField( "specialInfo" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Luke's brother."
      assert studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText() == "Saint John's College MN", "Last Enrollment School: ${ studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Saint John's College MN."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() == "Becoming a Millionaire", "Last Enrollment Leave Reason Category: ${ studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Becoming a Millionaire."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() == "Won a lottery", "Last Enrollment Leave Reason: ${ studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Won a lottery."
      assert studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber() == 1902, "Last Enrollment Term Year: ${ studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to 1902."
      assert studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber() == 1, "Last Enrollment Term No: ${ studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to 1."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() == 1902, "Last Enrollment Leave Term Year: ${ studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to 1902."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() == 2, "Last Enrollment Leave Term No: ${ studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to 2."
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() == "Freshman", "Last Enrollment First Class Attended: ${ studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Freshman."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() == "Sophomore", "Last Enrollment Last Class Attended: ${ studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Freshman."
      assert studentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() == new Date().clearTime(), "Last Update Date: ${ studentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() } from StudentDocument.findByStudentId( def studentId ) is not equal to ${ new Date().clearTime() }."
      
      log.info "Testing EnrollmentDocument value."
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 )
      
      assert enrollmentDocument != null, "User was unable to save a EnrollmentDocument."
      assert enrollmentDocument.getOnlyField( "studentId" ).getAtom() == "0202aoey", "Student ID: ${ enrollmentDocument.getOnlyField( "studentId" ).getAtom() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 0202aoey."
      assert enrollmentDocument.getOnlyField( "firstName" ).getText() == "Adrian", "First Name: ${ enrollmentDocument.getOnlyField( "firstName" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Adrian."
      assert enrollmentDocument.getOnlyField( "lastName" ).getText() == "Oey", "Last Name: ${ enrollmentDocument.getOnlyField( "lastName" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Oey."
      assert enrollmentDocument.getOnlyField( "birthDate" ).getDate() == Date.parse( "MMM d yy", "Sep 9 1900" ), "Birth Date: ${ enrollmentDocument.getOnlyField( "birthDate" ).getDate().format( "MMM d yy" ) } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Sep 9 1900."
      assert enrollmentDocument.getOnlyField( "village" ).getText() == "Kyetume", "Village: ${ enrollmentDocument.getOnlyField( "village" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Kyetume."
      assert enrollmentDocument.getOnlyField( "genderCode" ).getText() == "X", "Gender Code: ${ enrollmentDocument.getOnlyField( "genderCode" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to X."
      assert enrollmentDocument.getOnlyField( "specialInfo" ).getText() == "Luke's brother", "Special Info: ${ enrollmentDocument.getOnlyField( "specialInfo" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Luke's brother."
      assert enrollmentDocument.getOnlyField( "schoolName" ).getText() == "Saint John's College MN", "School Name: ${ enrollmentDocument.getOnlyField( "schoolName" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Saint John's College MN."
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() == "Becoming a Millionaire", "Leave Reason Category: ${ enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Becoming a Millionaire."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() == "Won a lottery", "Leave Reason: ${ enrollmentDocument.getOnlyField( "leaveReason" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Won a lottery."
      assert enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() == 1901, "Enrollment Term Year: ${ enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 1901."
      assert enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() == 3, "Enrollment Term No: ${ enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 3."
      assert enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() == Date.parse( "MMM d yyyy", "Aug 1 1901" ), "Enrollment Date: ${ enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate().format( "MMM d yy" ) } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Aug 1 1901."
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() == 1901, "Leave Term Year: ${ enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 1901."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() == 3, "Leave Term No: ${ enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 3."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() == Date.parse( "MMM d yyyy", "Oct 31 1901" ), "Leave Date: ${ enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate().format( "MMM d yy" ) } from enrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, Number enrollTermNo, Number enrollTermYear ) is not equal to Oct 31 1901."
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() == "Freshman", "First Class Attended: ${ enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Freshman."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() == "Freshman", "Last Class Attended: ${ enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Freshman."
      assert enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() == new Date().clearTime(), "Last Update Date: ${ enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to ${ new Date().clearTime() }."
      
      log.info "Testing Enrollment value."
      
      enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 )
      
      assert enrollment != null, "User was unable to save an Enrollment."
      assert enrollment.studentId == "0202aoey", "Student ID: ${ enrollment.studentId } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 0202aoey."
      assert enrollment.schoolName == "Saint John's College MN", "School Name: ${ enrollment.schoolName } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Saint John's College MN."
      assert enrollment.leaveReasonCategory == "Becoming a Millionaire", "Leave Reason Category: ${ enrollment.leaveReasonCategory } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Becoming a Millionaire."
      assert enrollment.leaveReason == "Won a lottery", "Leave Reason: ${ enrollment.leaveReason } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Won a lottery."
      assert enrollment.enrollTermYear == 1901, "Enrollment Term Year: ${ enrollment.enrollTermYear } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 1901."
      assert enrollment.enrollTermNo == 3, "Enrollment Term No: ${ enrollment.enrollTermNo } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 3."
      assert enrollment.leaveTermYear == 1901, "Leave Term Year: ${ enrollment.leaveTermYear } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 1901."
      assert enrollment.leaveTermNo == 3, "Leave Term No: ${ enrollment.leaveTermNo } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 3."
      assert enrollment.firstClassAttended == "Freshman", "First Class Attended: ${ enrollment.firstClassAttended } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Freshman."
      assert enrollment.lastClassAttended == "Freshman", "Last Class Attended: ${ enrollment.lastClassAttended } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Freshman."
      assert enrollment.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ enrollment.lastUpdateDate.clearTime() } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to ${ new Date().clearTime() }."    
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count + 1, "User was unable to change EnrollmentMetaData count"
      
      log.info "Testing ClassAttended value"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1901 )
      
      assert classAttended != null, "User was unable to save a ClassAttended"
      assert classAttended.studentId == "0202aoey", "Student ID: ${ classAttended.studentId } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 0202aoey"
      assert classAttended.schoolName == "Saint John's College MN", "School Name: ${ classAttended.schoolName } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Saint John's College MN"
      assert classAttended.enrollTermNo == 3, "Enrollment Term No: ${ classAttended.enrollTermNo } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 3"
      assert classAttended.enrollTermYear == 1901, "Enrollment Term Year: ${ classAttended.enrollTermYear } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1901"
      assert classAttended.getProperty( "class" ) == "Freshman", "Class: ${ classAttended.getProperty( "class" ) } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Freshman"
      assert classAttended.classLevel == 1, "Class Level: ${ classAttended.classLevel } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1"
      assert classAttended.classTermNo == 3, "Class Term No: ${ classAttended.classTermNo } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 3"
      assert classAttended.classTermYear == 1901, "Class Term Year: ${ classAttended.classTermYear } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1901"
      assert classAttended.tuitionFee == 60000, "Tuition Fee: ${ classAttended.tuitionFee } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 60000"
      assert classAttended.boardingFee == 100000, "Boarding Fee: ${ classAttended.boardingFee } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 100000"
      assert classAttended.boardingInd == "Y", "Boarding Ind: ${ classAttended.boardingInd } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Y"
      assert classAttended.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ classAttended.lastUpdateDate.clearTime() } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to ${ new Date().clearTime() }"
      
      /* Enrollment After the Existing Enrollment Save Test. */
      
      log.info "Enrollment After the Existing Enrollment Save Test."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "save" )
      	.addQueryParam( "studentId", "0202aoey" )
      	.addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "leaveReason", "Won a lottery" )
        .addQueryParam( "enrollTermSchool1", "1902 Term 3" )
        .addQueryParam( "leaveTermSchool1", "1902 Term 3" )
        .addQueryParam( "firstClassAttendedSchool1", "Sophomore" )
        .addQueryParam( "lastClassAttendedSchool1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 3", "Sophomore" )
        .addQueryParam( "boardingInd1902 Term 3", true ).toURL() )
      
      log.info "Testing StudentDocument value."
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument != null, "User was unable to save a StudentDocument."
      assert studentDocument.getOnlyField( "studentId" ).getAtom() == "0202aoey", "Student ID: ${ studentDocument.getOnlyField( "studentId" ).getAtom() } from StudentDocument.findByStudentId( def studentId ) is not equal to 0202aoey."
      assert studentDocument.getOnlyField( "firstName" ).getText() == "Adrian", "First Name: ${ studentDocument.getOnlyField( "firstName" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Adrian."
      assert studentDocument.getOnlyField( "lastName" ).getText() == "Oey", "Last Name: ${ studentDocument.getOnlyField( "lastName" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Oey."
      assert studentDocument.getOnlyField( "birthDate" ).getDate() == Date.parse( "MMM d yy", "Sep 9 1900" ), "Birth Date: ${ studentDocument.getOnlyField( "birthDate" ).getDate().format( "MMM d yy" ) } from StudentDocument.findByStudentId( def studentId ) is not equal to Sep 9 1900."
      assert studentDocument.getOnlyField( "village" ).getText() == "Kyetume", "Village: ${ studentDocument.getOnlyField( "village" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Kyetume."
      assert studentDocument.getOnlyField( "genderCode" ).getText() == "X", "Gender Code: ${ studentDocument.getOnlyField( "genderCode" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to X."
      assert studentDocument.getOnlyField( "specialInfo" ).getText() == "Luke's brother", "Special Info: ${ studentDocument.getOnlyField( "specialInfo" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Luke's brother."
      assert studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText() == "Saint John's College MN", "Last Enrollment School: ${ studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Saint John's College MN."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() == "Becoming a Millionaire", "Last Enrollment Leave Reason Category: ${ studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Becoming a Millionaire."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() == "Won a lottery", "Last Enrollment Leave Reason: ${ studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Won a lottery."
      assert studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber() == 1902, "Last Enrollment Term Year: ${ studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to 1902."
      assert studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber() == 3, "Last Enrollment Term No: ${ studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to 3."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() == 1902, "Last Enrollment Leave Term Year: ${ studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to 1902."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() == 3, "Last Enrollment Leave Term No: ${ studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to 3."
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() == "Sophomore", "Last Enrollment First Class Attended: ${ studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Sophomore."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() == "Sophomore", "LastEnrollment Last Class Attended: ${ studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Sophomore."
      assert studentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() == new Date().clearTime(), "Last Update Date: ${ studentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() } from StudentDocument.findByStudentId( def studentId ) is not equal to ${ new Date().clearTime() }."
      
      log.info "Testing EnrollmentDocument value."
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollmentDocument != null, "User was unable to save a EnrollmentDocument."
      assert enrollmentDocument.getOnlyField( "studentId" ).getAtom() == "0202aoey", "Student ID: ${ enrollmentDocument.getOnlyField( "studentId" ).getAtom() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 0202aoey."
      assert enrollmentDocument.getOnlyField( "firstName" ).getText() == "Adrian", "First Name: ${ enrollmentDocument.getOnlyField( "firstName" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Adrian."
      assert enrollmentDocument.getOnlyField( "lastName" ).getText() == "Oey", "Last Name: ${ enrollmentDocument.getOnlyField( "lastName" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Oey."
      assert enrollmentDocument.getOnlyField( "birthDate" ).getDate() == Date.parse( "MMM d yy", "Sep 9 1900" ), "Birth Date: ${ enrollmentDocument.getOnlyField( "birthDate" ).getDate().format( "MMM d yy" ) } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Sep 9 1900."
      assert enrollmentDocument.getOnlyField( "village" ).getText() == "Kyetume", "Village: ${ enrollmentDocument.getOnlyField( "village" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Kyetume."
      assert enrollmentDocument.getOnlyField( "genderCode" ).getText() == "X", "Gender Code: ${ enrollmentDocument.getOnlyField( "genderCode" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to X."
      assert enrollmentDocument.getOnlyField( "specialInfo" ).getText() == "Luke's brother", "Special Info: ${ enrollmentDocument.getOnlyField( "specialInfo" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Luke's brother."
      assert enrollmentDocument.getOnlyField( "schoolName" ).getText() == "Saint John's College MN", "School Name: ${ enrollmentDocument.getOnlyField( "schoolName" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Saint John's College MN."
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() == "Becoming a Millionaire", "Leave Reason Category: ${ enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Becoming a Millionaire."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() == "Won a lottery", "Leave Reason: ${ enrollmentDocument.getOnlyField( "leaveReason" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Won a lottery."
      assert enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() == 1902, "Enrollment Term Year: ${ enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 1902."
      assert enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() == 3, "Enrollment Term No: ${ enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 3."
      assert enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() == Date.parse( "MMM d yyyy", "Aug 1 1902" ), "Enrollment Date: ${ enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate().format( "MMM d yy" ) } from enrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Aug 1 1902."
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() == 1902, "Leave Term Year: ${ enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 1902."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() == 3, "Leave Term No: ${ enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 3."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() == Date.parse( "MMM d yyyy", "Oct 31 1902" ), "Leave Date: ${ enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate().format( "MMM d yy" ) } from enrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Oct 31 1902."
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() == "Sophomore", "First Class Attended: ${ enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Sophomore."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() == "Sophomore", "Last Class Attended: ${ enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Sophomore."
      assert enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() == new Date().clearTime(), "Last Update Date: ${ enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() } from EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to ${ new Date().clearTime() }."
      
      log.info "Testing Enrollment value."
      
      enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollment != null, "User was unable to save an Enrollment."
      assert enrollment.studentId == "0202aoey", "Student ID: ${ enrollment.studentId } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 0202aoey."
      assert enrollment.schoolName == "Saint John's College MN", "School Name: ${ enrollment.schoolName } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Saint John's College MN."
      assert enrollment.leaveReasonCategory == "Becoming a Millionaire", "Leave Reason Category: ${ enrollment.leaveReasonCategory } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Becoming a Millionaire."
      assert enrollment.leaveReason == "Won a lottery", "Leave Reason: ${ enrollment.leaveReason } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Won a lottery."
      assert enrollment.enrollTermYear == 1902, "Enrollment Term Year: ${ enrollment.enrollTermYear } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 1902."
      assert enrollment.enrollTermNo == 3, "Enrollment Term No: ${ enrollment.enrollTermNo } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 3."
      assert enrollment.leaveTermYear == 1902, "Leave Term Year: ${ enrollment.leaveTermYear } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 1902."
      assert enrollment.leaveTermNo == 3, "Leave Term No: ${ enrollment.leaveTermNo } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to 3."
      assert enrollment.firstClassAttended == "Sophomore", "First Class Attended: ${ enrollment.firstClassAttended } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Sophomore."
      assert enrollment.lastClassAttended == "Sophomore", "Last Class Attended: ${ enrollment.lastClassAttended } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to Sophomore."
      assert enrollment.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ enrollment.lastUpdateDate.clearTime() } from Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) is not equal to ${ new Date().clearTime() }."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count + 2, "User was unable to change EnrollmentMetaData count"
      
      log.info "Testing ClassAttended value"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert classAttended != null, "User was unable to save a ClassAttended"
      assert classAttended.studentId == "0202aoey", "Student ID: ${ classAttended.studentId } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 0202aoey"
      assert classAttended.schoolName == "Saint John's College MN", "School Name: ${ classAttended.schoolName } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Saint John's College MN"
      assert classAttended.enrollTermNo == 3, "Enrollment Term No: ${ classAttended.enrollTermNo } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 3"
      assert classAttended.enrollTermYear == 1902, "Enrollment Term Year: ${ classAttended.enrollTermYear } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1902"
      assert classAttended.getProperty( "class" ) == "Sophomore", "Class: ${ classAttended.getProperty( "class" ) } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Sophomore"
      assert classAttended.classLevel == 2, "Class Level: ${ classAttended.classLevel } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 2"
      assert classAttended.classTermNo == 3, "Class Term No: ${ classAttended.classTermNo } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 3"
      assert classAttended.classTermYear == 1902, "Class Term Year: ${ classAttended.classTermYear } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1902"
      assert classAttended.tuitionFee == 70000, "Tuition Fee: ${ classAttended.tuitionFee } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 70000"
      assert classAttended.boardingFee == 110000, "Boarding Fee: ${ classAttended.boardingFee } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 110000"
      assert classAttended.boardingInd == "Y", "Boarding Ind: ${ classAttended.boardingInd } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Y"
      assert classAttended.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ classAttended.lastUpdateDate.clearTime() } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to ${ new Date().clearTime() }"
      
      /* Save a Fee for an Edit Test */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "name", "Admission" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "3" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "3" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      /* Save a Payment for an Edit Test */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "fundingSource", "Self" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "3" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "3" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      enrollmentMetaData = Enrollment.findMetaDataBySchoolName( "Saint John's College MN" )
      
      /* Enrollment Edit Test with a missing action parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "leaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "leaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "firstClassAttended", "Freshman" )
        .addQueryParam( "lastClassAttended", "Freshman" )
        .addQueryParam( "classAttended1902 Term 3", "Freshman" )
        .addQueryParam( "classAttended1903 Term 1", "Freshman" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit an Enrollment using a missing action parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() != "Won a jackpot", "User was able to edit an Enrollment using a missing action parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() != 1903, "User was able to edit an Enrollment using a missing action parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() != 1, "User was able to edit an Enrollment using a missing action parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using a missing action parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using a missing action parameter."
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit an Enrollment using a missing action parameter."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() != "Won a jackpot", "User was able to edit an Enrollment using a missing action parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() != 1903, "User was able to edit an Enrollment using a missing action parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() != 1, "User was able to edit an Enrollment using a missing action parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yyyy", "Apr 30 1903" ), "User was able to edit an Enrollment using a missing action parameter."
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using a missing action parameter."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using a missing action parameter."
      
      enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollment.leaveReasonCategory != "Becoming a Billionaire", "User was able to edit an Enrollment using a missing action parameter."
      assert enrollment.leaveReason != "Won a jackpot", "User was able to edit an Enrollment using a missing action parameter."
      assert enrollment.leaveTermYear != 1903, "User was able to edit an Enrollment using a missing action parameter."
      assert enrollment.leaveTermNo != 1, "User was able to edit an Enrollment using a missing action parameter."
      assert enrollment.firstClassAttended != "Freshman", "User was able to edit an Enrollment using a missing action parameter."
      assert enrollment.lastClassAttended != "Freshman", "User was able to edit an Enrollment using a missing action parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using a missing action parameter"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert classAttended.getProperty( "class" ) != "Freshman", "User was able to edit a ClassAttended using a missing action parameter"
      assert classAttended.classLevel != 1, "User was able to edit a ClassAttended using a missing action parameter"
      assert classAttended.tuitionFee != 60000, "User was able to edit a ClassAttended using a missing action parameter"
      assert classAttended.boardingFee != 100000, "User was able to edit a ClassAttended using a missing action parameter"
      
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 1, 1903 ) == null, "User was able to edit a ClassAttended using a missing action parameter"
      
      /* Enrollment Edit Test with an invalid action parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "modify" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "leaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "leaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "firstClassAttended", "Freshman" )
        .addQueryParam( "lastClassAttended", "Freshman" )
        .addQueryParam( "classAttended1902 Term 3", "Freshman" )
        .addQueryParam( "classAttended1903 Term 1", "Freshman" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit an Enrollment using an invalid action parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() != "Won a jackpot", "User was able to edit an Enrollment using an invalid action parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() != 1903, "User was able to edit an Enrollment using an invalid action parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() != 1, "User was able to edit an Enrollment using an invalid action parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using an invalid action parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using an invalid action parameter."
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit an Enrollment using an invalid action parameter."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() != "Won a jackpot", "User was able to edit an Enrollment using an invalid action parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() != 1903, "User was able to edit an Enrollment using an invalid action parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() != 1, "User was able to edit an Enrollment using an invalid action parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ) != Date.parse( "MMM d yyyy", "Apr 30 1903" ), "User was able to edit an Enrollment using an invalid action parameter."
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using an invalid action parameter."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using an invalid action parameter."
      
      enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollment.leaveReasonCategory != "Becoming a Billionaire", "User was able to edit an Enrollment using an invalid action parameter."
      assert enrollment.leaveReason != "Won a jackpot", "User was able to edit an Enrollment using an invalid action parameter."
      assert enrollment.leaveTermYear != 1903, "User was able to edit an Enrollment using an invalid action parameter."
      assert enrollment.leaveTermNo != 1, "User was able to edit an Enrollment using an invalid action parameter."
      assert enrollment.firstClassAttended != "Freshman", "User was able to edit an Enrollment using an invalid action parameter."
      assert enrollment.lastClassAttended != "Freshman", "User was able to edit an Enrollment using an invalid action parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using an invalid action parameter"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert classAttended.getProperty( "class" ) != "Freshman", "User was able to edit a ClassAttended using an invalid action parameter"
      assert classAttended.classLevel != 1, "User was able to edit a ClassAttended using an invalid action parameter"
      assert classAttended.tuitionFee != 60000, "User was able to edit a ClassAttended using an invalid action parameter"
      assert classAttended.boardingFee != 100000, "User was able to edit a ClassAttended using an invalid action parameter"
      
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 1, 1903 ) == null, "User was able to edit a ClassAttended using an invalid action parameter"
      
      /* Enrollment Edit Test with a missing id parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "edit" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "enrollTerm", "1902 Term 3" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "leaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "firstClassAttended", "Freshman" )
        .addQueryParam( "lastClassAttended", "Freshman" )
        .addQueryParam( "classAttended1902 Term 3", "Freshman" )
        .addQueryParam( "classAttended1903 Term 1", "Freshman" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit an Enrollment using a missing id parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() != "Won a jackpot", "User was able to edit an Enrollment using a missing id parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() != 1903, "User was able to edit an Enrollment using a missing id parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() != 1, "User was able to edit an Enrollment using a missing id parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using a missing id parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using a missing id parameter."
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit an Enrollment using a missing id parameter."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() != "Won a jackpot", "User was able to edit an Enrollment using a missing id parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() != 1903, "User was able to edit an Enrollment using a missing id parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() != 1, "User was able to edit an Enrollment using a missing id parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yyyy", "Apr 30 1903" ), "User was able to edit an Enrollment using a missing id parameter."
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using a missing id parameter."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using a missing id parameter."
      
      enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollment.leaveReasonCategory != "Becoming a Billionaire", "User was able to edit an Enrollment using a missing id parameter."
      assert enrollment.leaveReason != "Won a jackpot", "User was able to edit an Enrollment using a missing id parameter."
      assert enrollment.leaveTermYear != 1903, "User was able to edit an Enrollment using a missing id parameter."
      assert enrollment.leaveTermNo != 1, "User was able to edit an Enrollment using a missing id parameter."
      assert enrollment.firstClassAttended != "Freshman", "User was able to edit an Enrollment using a missing id parameter."
      assert enrollment.lastClassAttended != "Freshman", "User was able to edit an Enrollment using a missing id parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using a missing id parameter"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert classAttended.getProperty( "class" ) != "Freshman", "User was able to edit a ClassAttended using a missing id parameter"
      assert classAttended.classLevel != 1, "User was able to edit a ClassAttended using a missing id parameter"
      assert classAttended.tuitionFee != 60000, "User was able to edit a ClassAttended using a missing id parameter"
      assert classAttended.boardingFee != 100000, "User was able to edit a ClassAttended using a missing id parameter"
      
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 1, 1903 ) == null, "User was able to edit a ClassAttended using a missing id parameter"
      
      /* Enrollment Edit Test with an invalid id parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "edit" )
        .addQueryParam( "id", "-1" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "leaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "firstClassAttended", "Freshman" )
        .addQueryParam( "lastClassAttended", "Freshman" )
        .addQueryParam( "classAttended1902 Term 3", "Freshman" )
        .addQueryParam( "classAttended1903 Term 1", "Freshman" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit an Enrollment using an invalid id parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() != "Won a jackpot", "User was able to edit an Enrollment using an invalid id parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() != 1903, "User was able to edit an Enrollment using an invalid id parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() != 1, "User was able to edit an Enrollment using an invalid id parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using an invalid id parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using an invalid id parameter."
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit an Enrollment using an invalid id parameter."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() != "Won a jackpot", "User was able to edit an Enrollment using an invalid id parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() != 1903, "User was able to edit an Enrollment using an invalid id parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() != 1, "User was able to edit an Enrollment using an invalid id parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yyyy", "Apr 30 1903" ), "User was able to edit an Enrollment using an invalid id parameter."
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using an invalid id parameter."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using an invalid id parameter."
      
      enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollment.leaveReasonCategory != "Becoming a Billionaire", "User was able to edit an Enrollment using an invalid id parameter."
      assert enrollment.leaveReason != "Won a jackpot", "User was able to edit an Enrollment using an invalid id parameter."
      assert enrollment.leaveTermYear != 1903, "User was able to edit an Enrollment using an invalid id parameter."
      assert enrollment.leaveTermNo != 1, "User was able to edit an Enrollment using an invalid id parameter."
      assert enrollment.firstClassAttended != "Freshman", "User was able to edit an Enrollment using an invalid id parameter."
      assert enrollment.lastClassAttended != "Freshman", "User was able to edit an Enrollment using an invalid id parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using an invalid id parameter"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert classAttended.getProperty( "class" ) != "Freshman", "User was able to edit a ClassAttended using an invalid id parameter"
      assert classAttended.classLevel != 1, "User was able to edit a ClassAttended using an invalid id parameter"
      assert classAttended.tuitionFee != 60000, "User was able to edit a ClassAttended using an invalid id parameter"
      assert classAttended.boardingFee != 100000, "User was able to edit a ClassAttended using an invalid id parameter"
      
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 1, 1903 ) == null, "User was able to edit a ClassAttended using an invalid id parameter"
      
      /* Enrollment Edit Test with an invalid term range. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "edit" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "leaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "leaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1901 Term 2" )
        .addQueryParam( "firstClassAttended", "Freshman" )
        .addQueryParam( "lastClassAttended", "Freshman" )
        .addQueryParam( "classAttended1902 Term 3", "Freshman" )
        .addQueryParam( "classAttended1901 Term 2", "Freshman" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit an Enrollment using an invalid term range."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() != "Won a jackpot", "User was able to edit an Enrollment using an invalid term range."
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() != 1901, "User was able to edit an Enrollment using an invalid term range."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() != 2, "User was able to edit an Enrollment using an invalid term range."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yyyy", "Jul 31 1902" ), "User was able to edit an Enrollment using an invalid term range."
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using an invalid term range."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using an invalid term range."
      
      enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollment.leaveReasonCategory != "Becoming a Billionaire", "User was able to edit an Enrollment using an invalid term range."
      assert enrollment.leaveReason != "Won a jackpot", "User was able to edit an Enrollment using an invalid term range."
      assert enrollment.leaveTermYear != 1901, "User was able to edit an Enrollment using an invalid term range."
      assert enrollment.leaveTermNo != 2, "User was able to edit an Enrollment using an invalid term range."
      assert enrollment.firstClassAttended != "Freshman", "User was able to edit an Enrollment using an invalid term range."
      assert enrollment.lastClassAttended != "Freshman", "User was able to edit an Enrollment using an invalid term range."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using an invalid term range"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert classAttended.getProperty( "class" ) != "Freshman", "User was able to edit a ClassAttended using an invalid term range"
      assert classAttended.classLevel != 1, "User was able to edit a ClassAttended using an invalid term range"
      assert classAttended.tuitionFee != 60000, "User was able to edit a ClassAttended using an invalid term range"
      assert classAttended.boardingFee != 100000, "User was able to edit a ClassAttended using an invalid term range"
      
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 2, 1901 ) == null, "User was able to edit a ClassAttended using an invalid term range"
      
      /* Enrollment Edit Test with a missing firstClassAttended parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "edit" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "leaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "leaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "lastClassAttended", "Freshman" )
        .addQueryParam( "classAttended1902 Term 3", "Freshman" )
        .addQueryParam( "classAttended1903 Term 1", "Freshman" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit an Enrollment using a missing firstClassAttended parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() != "Won a jackpot", "User was able to edit an Enrollment using a missing firstClassAttended parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() != 1903, "User was able to edit an Enrollment using a missing firstClassAttended parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() != 1, "User was able to edit an Enrollment using a missing firstClassAttended parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() != null, "User was able to edit an Enrollment using a missing firstClassAttended parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using a missing firstClassAttended parameter."
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit an Enrollment using a missing firstClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() != "Won a jackpot", "User was able to edit an Enrollment using a missing firstClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() != 1903, "User was able to edit an Enrollment using a missing firstClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() != 1, "User was able to edit an Enrollment using a missing firstClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yyyy", "Apr 30 1903" ), "User was able to edit an Enrollment using a missing firstClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() != null, "User was able to edit an Enrollment using a missing firstClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using a missing firstClassAttended parameter."
      
      enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollment.leaveReasonCategory != "Becoming a Billionaire", "User was able to edit an Enrollment using a missing firstClassAttended parameter."
      assert enrollment.leaveReason != "Won a jackpot", "User was able to edit an Enrollment using a missing firstClassAttended parameter."
      assert enrollment.leaveTermYear != 1903, "User was able to edit an Enrollment using a missing firstClassAttended parameter."
      assert enrollment.leaveTermNo != 1, "User was able to edit an Enrollment using a missing firstClassAttended parameter."
      assert enrollment.firstClassAttended != null, "User was able to edit an Enrollment using a missing firstClassAttended parameter."
      assert enrollment.lastClassAttended != "Freshman", "User was able to edit an Enrollment using a missing firstClassAttended parameter."
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert classAttended.getProperty( "class" ) != "Freshman", "User was able to edit a ClassAttended using a missing firstClassAttended parameter"
      assert classAttended.classLevel != 1, "User was able to edit a ClassAttended using a missing firstClassAttended parameter"
      assert classAttended.tuitionFee != 60000, "User was able to edit a ClassAttended using a missing firstClassAttended parameter"
      assert classAttended.boardingFee != 100000, "User was able to edit a ClassAttended using a missing firstClassAttended parameter"
      
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 1, 1903 ) == null, "User was able to edit a ClassAttended using a missing firstClassAttended parameter"
      
      /* Enrollment Edit Test with a missing lastClassAttended parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "edit" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "leaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "leaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "firstClassAttended", "Freshman" )
        .addQueryParam( "classAttended1902 Term 3", "Freshman" )
        .addQueryParam( "classAttended1903 Term 1", "Freshman" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit an Enrollment using a missing lastClassAttended parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() != "Won a jackpot", "User was able to edit an Enrollment using a missing lastClassAttended parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() != 1903, "User was able to edit an Enrollment using a missing lastClassAttended parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() != 1, "User was able to edit an Enrollment using a missing lastClassAttended parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using a missing lastClassAttended parameter."
      assert studentDocument.getFieldNames().contains( "lastEnrollmentLastClassAttended" ), "User was able to edit an Enrollment using a missing lastClassAttended parameter."
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit an Enrollment using a missing lastClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() != "Won a jackpot", "User was able to edit an Enrollment using a missing lastClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() != 1903, "User was able to edit an Enrollment using a missing lastClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() != 1, "User was able to edit an Enrollment using a missing lastClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yyyy", "Apr 30 1903" ), "User was able to edit an Enrollment using a missing lastClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using a missing lastClassAttended parameter."
      assert enrollmentDocument.getFieldNames().contains( "lastClassAttended" ), "User was able to edit an Enrollment using a missing lastClassAttended parameter."
      
      enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollment.leaveReasonCategory != "Becoming a Billionaire", "User was able to edit an Enrollment using a missing lastClassAttended parameter."
      assert enrollment.leaveReason != "Won a jackpot", "User was able to edit an Enrollment using a missing lastClassAttended parameter."
      assert enrollment.leaveTermYear != 1903, "User was able to edit an Enrollment using a missing lastClassAttended parameter."
      assert enrollment.leaveTermNo != 1, "User was able to edit an Enrollment using a missing lastClassAttended parameter."
      assert enrollment.firstClassAttended != "Freshman", "User was able to edit an Enrollment using a missing lastClassAttended parameter."
      assert enrollment.lastClassAttended != null, "User was able to edit an Enrollment using a missing lastClassAttended parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using a missing lastClassAttended parameter"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert classAttended.getProperty( "class" ) != "Freshman", "User was able to edit a ClassAttended using a missing lastClassAttended parameter"
      assert classAttended.classLevel != 1, "User was able to edit a ClassAttended using a missing lastClassAttended parameter"
      assert classAttended.tuitionFee != 60000, "User was able to edit a ClassAttended using a missing lastClassAttended parameter"
      assert classAttended.boardingFee != 100000, "User was able to edit a ClassAttended using a missing lastClassAttended parameter"
      
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 1, 1903 ) == null, "User was able to edit a ClassAttended using a missing lastClassAttended parameter"
      
      /* Enrollment Edit Test with a missing lastUpdateDate parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "edit" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "leaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "leaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "firstClassAttended", "Freshman" )
        .addQueryParam( "lastClassAttended", "Freshman" )
        .addQueryParam( "classAttended1902 Term 3", "Freshman" )
        .addQueryParam( "classAttended1903 Term 1", "Freshman" ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit an Enrollment using a missing lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() != "Won a jackpot", "User was able to edit an Enrollment using a missing lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() != 1903, "User was able to edit an Enrollment using a missing lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() != 1, "User was able to edit an Enrollment using a missing lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using a missing lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using a missing lastUpdateDate parameter."
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit an Enrollment using a missing lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() != "Won a jackpot", "User was able to edit an Enrollment using a missing lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() != 1903, "User was able to edit an Enrollment using a missing lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() != 1, "User was able to edit an Enrollment using a missing lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yyyy", "Apr 30 1903" ), "User was able to edit an Enrollment using a missing lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using a missing lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using a missing lastUpdateDate parameter."
      
      enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollment.leaveReasonCategory != "Becoming a Billionaire", "User was able to edit an Enrollment using a missing lastUpdateDate parameter."
      assert enrollment.leaveReason != "Won a jackpot", "User was able to edit an Enrollment using a missing lastUpdateDate parameter."
      assert enrollment.leaveTermYear != 1903, "User was able to edit an Enrollment using a missing lastUpdateDate parameter."
      assert enrollment.leaveTermNo != 1, "User was able to edit an Enrollment using a missing lastUpdateDate parameter."
      assert enrollment.firstClassAttended != "Freshman", "User was able to edit an Enrollment using a missing lastUpdateDate parameter."
      assert enrollment.lastClassAttended != "Freshman", "User was able to edit an Enrollment using a missing lastUpdateDate parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using a missing lastUpdateDate parameter"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert classAttended.getProperty( "class" ) != "Freshman", "User was able to edit a ClassAttended using a missing lastUpdateDate parameter"
      assert classAttended.classLevel != 1, "User was able to edit a ClassAttended using a missing lastUpdateDate parameter"
      assert classAttended.tuitionFee != 60000, "User was able to edit a ClassAttended using a missing lastUpdateDate parameter"
      assert classAttended.boardingFee != 100000, "User was able to edit a ClassAttended using a missing lastUpdateDate parameter"
      
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 1, 1903 ) == null, "User was able to edit a ClassAttended using a missing lastUpdateDate parameter"
      
      /* Enrollment Edit Test with an invalid lastUpdateDate parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "edit" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "leaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "leaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "firstClassAttended", "Freshman" )
        .addQueryParam( "lastClassAttended", "Freshman" )
        .addQueryParam( "classAttended1902 Term 3", "Freshman" )
        .addQueryParam( "classAttended1903 Term 1", "Freshman" )
        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit an Enrollment using an invalid lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() != "Won a jackpot", "User was able to edit an Enrollment using an invalid lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() != 1903, "User was able to edit an Enrollment using an invalid lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() != 1, "User was able to edit an Enrollment using an invalid lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using an invalid lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using an invalid lastUpdateDate parameter."
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit an Enrollment using an invalid lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() != "Won a jackpot", "User was able to edit an Enrollment using an invalid lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() != 1903, "User was able to edit an Enrollment using an invalid lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() != 1, "User was able to edit an Enrollment using an invalid lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yyyy", "Apr 30 1903" ), "User was able to edit an Enrollment using an invalid lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using an invalid lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() != "Freshman", "User was able to edit an Enrollment using an invalid lastUpdateDate parameter."
      
      enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollment.leaveReasonCategory != "Becoming a Billionaire", "User was able to edit an Enrollment using an invalid lastUpdateDate parameter."
      assert enrollment.leaveReason != "Won a jackpot", "User was able to edit an Enrollment using an invalid lastUpdateDate parameter."
      assert enrollment.leaveTermYear != 1903, "User was able to edit an Enrollment using an invalid lastUpdateDate parameter."
      assert enrollment.leaveTermNo != 1, "User was able to edit an Enrollment using an invalid lastUpdateDate parameter."
      assert enrollment.firstClassAttended != "Freshman", "User was able to edit an Enrollment using an invalid lastUpdateDate parameter."
      assert enrollment.lastClassAttended != "Freshman", "User was able to edit an Enrollment using an invalid lastUpdateDate parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using an invalid lastUpdateDate parameter"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert classAttended.getProperty( "class" ) != "Freshman", "User was able to edit a ClassAttended using an invalid lastUpdateDate parameter"
      assert classAttended.classLevel != 1, "User was able to edit a ClassAttended using an invalid lastUpdateDate parameter"
      assert classAttended.tuitionFee != 60000, "User was able to edit a ClassAttended using an invalid lastUpdateDate parameter"
      assert classAttended.boardingFee != 100000, "User was able to edit a ClassAttended using an invalid lastUpdateDate parameter"
      
      assert ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 1, 1903 ) == null, "User was able to edit a ClassAttended using an invalid lastUpdateDate parameter"
      
      /* Enrollment Edit Test. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "edit" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "leaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "leaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "firstClassAttended", "Freshman" )
        .addQueryParam( "lastClassAttended", "Freshman" )
        .addQueryParam( "classAttended1902 Term 3", "Freshman" )
        .addQueryParam( "classAttended1903 Term 1", "Freshman" )
        .addQueryParam( "lastUpdateDate", enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() == "Becoming a Billionaire", "User was unable to edit an Enrollment."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() == "Won a jackpot", "User was unable to edit an Enrollment."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() == 1903, "User was unable to edit an Enrollment."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() == 1, "User was unable to edit an Enrollment."
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() == "Freshman", "User was unable to edit an Enrollment."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() == "Freshman", "User was unable to edit an Enrollment."
      assert studentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() == new Date().clearTime(), "User was unable to edit an Enrollment."
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() == "Becoming a Billionaire", "User was unable to edit an Enrollment."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() == "Won a jackpot", "User was unable to edit an Enrollment."
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() == 1903, "User was unable to edit an Enrollment."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() == 1, "User was unable to edit an Enrollment."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() == Date.parse( "MMM d yyyy", "Apr 30 1903" ), "User was unable to edit an Enrollment."
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() == "Freshman", "User was unable to edit an Enrollment."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() == "Freshman", "User was unable to edit an Enrollment."
      assert enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() == new Date().clearTime(), "User was unable to edit an Enrollment."
      
      enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollment.leaveReasonCategory == "Becoming a Billionaire", "User was unable to edit an Enrollment."
      assert enrollment.leaveReason == "Won a jackpot", "User was unable to edit an Enrollment."
      assert enrollment.leaveTermYear == 1903, "User was unable to edit an Enrollment."
      assert enrollment.leaveTermNo == 1, "User was unable to edit an Enrollment."
      assert enrollment.firstClassAttended == "Freshman", "User was unable to edit an Enrollment."
      assert enrollment.lastClassAttended == "Freshman", "User was unable to edit an Enrollment."
      assert enrollment.lastUpdateDate.clearTime() == new Date().clearTime(), "User was unable to edit an Enrollment."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert classAttended.getProperty( "class" ) == "Freshman", "User was unable to edit a ClassAttended"
      assert classAttended.classLevel == 1, "User was unable to edit a ClassAttended"
      assert classAttended.tuitionFee == 60000, "User was unable to edit a ClassAttended"
      assert classAttended.boardingFee == 100000, "User was unable to edit a ClassAttended"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 1, 1903 )
      
      assert classAttended.getProperty( "class" ) == "Freshman", "User was unable to edit a ClassAttended"
      assert classAttended.classLevel == 1, "User was unable to edit a ClassAttended"
      assert classAttended.tuitionFee == 60000, "User was unable to edit a ClassAttended"
      assert classAttended.boardingFee == 100000, "User was unable to edit a ClassAttended"
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      /* Enrollment Edit Test, setting leaveTerm to now in order to test Fee and Payment */
      log.info "Enrollment Edit Test, setting leaveTerm to now in order to test Fee and Payment"
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "edit" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "leaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "leaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "Leave Term" )
        .addQueryParam( "firstClassAttended", "Freshman" )
        .addQueryParam( "lastClassAttended", "Freshman" )
        .addQueryParam( "classAttended1902 Term 3", "Freshman" )
        .addQueryParam( "classAttended1903 Term 1", "Freshman" )
        .addQueryParam( "lastUpdateDate", enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      Set<String> studentDocumentFieldNames = studentDocument.getFieldNames()
      
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() == "Becoming a Billionaire", "User was unable to edit an Enrollment."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() == "Won a jackpot", "User was unable to edit an Enrollment."
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermYear" ), "User was unable to edit an Enrollment."
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermNo" ), "User was unable to edit an Enrollment."
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() == "Freshman", "User was unable to edit an Enrollment."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() == "Freshman", "User was unable to edit an Enrollment."
      assert studentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() == new Date().clearTime(), "User was unable to edit an Enrollment."
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      Set<String> enrollmentDocumentFieldNames = enrollmentDocument.getFieldNames()
      
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() == "Becoming a Billionaire", "User was unable to edit an Enrollment."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() == "Won a jackpot", "User was unable to edit an Enrollment."
      assert !enrollmentDocumentFieldNames.contains( "leaveTermYear" ), "User was unable to edit an Enrollment."
      assert !enrollmentDocumentFieldNames.contains( "leaveTermNo" ), "User was unable to edit an Enrollment."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() == Date.parse( "MMM d yyyy", "Dec 31 2999" ), "User was unable to edit an Enrollment."
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() == "Freshman", "User was unable to edit an Enrollment."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() == "Freshman", "User was unable to edit an Enrollment."
      assert enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() == new Date().clearTime(), "User was unable to edit an Enrollment."
      
      enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollment.leaveReasonCategory == "Becoming a Billionaire", "User was unable to edit an Enrollment."
      assert enrollment.leaveReason == "Won a jackpot", "User was unable to edit an Enrollment."
      assert enrollment.leaveTermYear == null, "User was unable to edit an Enrollment."
      assert enrollment.leaveTermNo == null, "User was unable to edit an Enrollment."
      assert enrollment.firstClassAttended == "Freshman", "User was unable to edit an Enrollment."
      assert enrollment.lastClassAttended == "Freshman", "User was unable to edit an Enrollment."
      assert enrollment.lastUpdateDate.clearTime() == new Date().clearTime(), "User was unable to edit an Enrollment."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert classAttended.getProperty( "class" ) == "Freshman", "User was unable to edit a ClassAttended"
      assert classAttended.classLevel == 1, "User was unable to edit a ClassAttended"
      assert classAttended.tuitionFee == 60000, "User was unable to edit a ClassAttended"
      assert classAttended.boardingFee == 100000, "User was unable to edit a ClassAttended"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 1, 1903 )
      
      assert classAttended.getProperty( "class" ) == "Freshman", "User was unable to edit a ClassAttended"
      assert classAttended.classLevel == 1, "User was unable to edit a ClassAttended"
      assert classAttended.tuitionFee == 60000, "User was unable to edit a ClassAttended"
      assert classAttended.boardingFee == 100000, "User was unable to edit a ClassAttended"
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "fundingSource", "Self" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "3" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1903" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
        
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      /* Enrollment Edit Test, setting leaveTerm to 1902 Term 3 in order to test Payment */
      log.info "Enrollment Edit Test, setting leaveTerm to 1902 Term 3 in order to test Payment"
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "edit" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "leaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "leaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1902 Term 3" )
        .addQueryParam( "firstClassAttended", "Freshman" )
        .addQueryParam( "lastClassAttended", "Freshman" )
        .addQueryParam( "classAttended1902 Term 3", "Freshman" )
        .addQueryParam( "lastUpdateDate", enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      studentDocumentFieldNames = studentDocument.getFieldNames()
      
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermYear" ), "User was able to edit an Enrollment and leave an orphan Payment"
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermNo" ), "User was able to edit an Enrollment and leave an orphan Payment"
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      enrollmentDocumentFieldNames = enrollmentDocument.getFieldNames()
      
      assert !enrollmentDocumentFieldNames.contains( "leaveTermYear" ), "User was able to edit an Enrollment and leave an orphan Payment"
      assert !enrollmentDocumentFieldNames.contains( "leaveTermNo" ), "User was able to edit an Enrollment and leave an orphan Payment"
      
      enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollment.leaveTermYear != 1902, "User was able to edit an Enrollment and leave an orphan Payment"
      assert enrollment.leaveTermNo != 3, "User was able to edit an Enrollment and leave an orphan Payment"
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count and leave an orphan Payment"
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      /* Enrollment Edit Test, setting leaveTerm to 1903 Term 1 in order to test Payment */
      log.info "Enrollment Edit Test, setting leaveTerm to 1903 Term 1 in order to test Payment"
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "edit" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "leaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "leaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "firstClassAttended", "Freshman" )
        .addQueryParam( "lastClassAttended", "Freshman" )
        .addQueryParam( "classAttended1902 Term 3", "Freshman" )
        .addQueryParam( "classAttended1903 Term 1", "Freshman" )
        .addQueryParam( "lastUpdateDate", enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() == 1903, "User was unable to edit an Enrollment with valid Payments"
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() == 1, "User was unable to edit an Enrollment with valid Payments"
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() == 1903, "User was unable to edit an Enrollment with valid Payments"
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() == 1, "User was unable to edit an Enrollment with valid Payments"
      
      enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollment.leaveTermYear == 1903, "User was unable to edit an Enrollment with valid Payments"
      assert enrollment.leaveTermNo == 1, "User was unable to edit an Enrollment with valid Payments"
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count"
      
      Entity payment = Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Self", "0202aoey", "Saint John's College MN", 1, 1903 )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", payment.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", payment.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "name", "Admission" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "3" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1903" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
        
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
        
      /* Enrollment Edit Test, setting leaveTerm to now in order to prepare the Enrollment record to test Fee further */
      log.info "Enrollment Edit Test, setting leaveTerm to now in order to prepare the Enrollment record to test Fee further"
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "edit" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "leaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "leaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "Leave Term" )
        .addQueryParam( "firstClassAttended", "Freshman" )
        .addQueryParam( "lastClassAttended", "Freshman" )
        .addQueryParam( "classAttended1902 Term 3", "Freshman" )
        .addQueryParam( "classAttended1903 Term 1", "Freshman" )
        .addQueryParam( "lastUpdateDate", enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      /* Enrollment Edit Test, setting leaveTerm to 1902 Term 3 in order to test Fee */
      log.info "Enrollment Edit Test, setting leaveTerm to 1902 Term 3 in order to test Fee"
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "edit" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "leaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "leaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1902 Term 3" )
        .addQueryParam( "firstClassAttended", "Freshman" )
        .addQueryParam( "lastClassAttended", "Freshman" )
        .addQueryParam( "classAttended1902 Term 3", "Freshman" )
        .addQueryParam( "lastUpdateDate", enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      studentDocumentFieldNames = studentDocument.getFieldNames()
      
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermYear" ), "User was able to edit an Enrollment and leave an orphan Fee"
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermNo" ), "User was able to edit an Enrollment and leave an orphan Fee"
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      enrollmentDocumentFieldNames = enrollmentDocument.getFieldNames()
      
      assert !enrollmentDocumentFieldNames.contains( "leaveTermYear" ), "User was able to edit an Enrollment and leave an orphan Fee"
      assert !enrollmentDocumentFieldNames.contains( "leaveTermNo" ), "User was able to edit an Enrollment and leave an orphan Fee"
      
      enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollment.leaveTermYear != 1902, "User was able to edit an Enrollment and leave an orphan Fee"
      assert enrollment.leaveTermNo != 3, "User was able to edit an Enrollment and leave an orphan Fee"
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count and leave an orphan Fee"
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      /* Enrollment Edit Test, setting leaveTerm to 1903 Term 1 in order to test Payment */
      log.info "Enrollment Edit Test, setting leaveTerm to 1903 Term 1 in order to test Payment"
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "edit" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "leaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "leaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "firstClassAttended", "Freshman" )
        .addQueryParam( "lastClassAttended", "Freshman" )
        .addQueryParam( "classAttended1902 Term 3", "Freshman" )
        .addQueryParam( "classAttended1903 Term 1", "Freshman" )
        .addQueryParam( "lastUpdateDate", enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() == 1903, "User was unable to edit an Enrollment with valid Fees"
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() == 1, "User was unable to edit an Enrollment with valid Fees"
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() == 1903, "User was unable to edit an Enrollment with valid Fees"
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() == 1, "User was unable to edit an Enrollment with valid Fees"
      
      enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert enrollment.leaveTermYear == 1903, "User was unable to edit an Enrollment with valid Fees"
      assert enrollment.leaveTermNo == 1, "User was unable to edit an Enrollment with valid Fees"
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count"
      
      /* Overlapping Enrollment Edit Test. */
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      lastUpdateDate = enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate()
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "edit" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "leaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "leaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "firstClassAttended", "Sophomore" )
        .addQueryParam( "lastClassAttended", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 2", "Junior" )
        .addQueryParam( "classAttended1902 Term 3", "Junior" )
        .addQueryParam( "classAttended1903 Term 1", "Junior" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit an Enrollment into an overlapping one."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() != "Won a jackpot", "User was able to edit an Enrollment into an overlapping one."
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() != 1903, "User was able to edit an Enrollment into an overlapping one."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() != 1, "User was able to edit an Enrollment into an overlapping one."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yyyy", "Apr 30 1903" ), "User was able to edit an Enrollment into an overlapping one."
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() != "Sophomore", "User was able to edit an Enrollment into an overlapping one."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() != "Junior", "User was able to edit an Enrollment into an overlapping one."
      
      enrollment = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollment.leaveReasonCategory != "Becoming a Billionaire", "User was able to edit an Enrollment into an overlapping one."
      assert enrollment.leaveReason != "Won a jackpot", "User was able to edit an Enrollment into an overlapping one."
      assert enrollment.leaveTermYear != 1903, "User was able to edit an Enrollment into an overlapping one."
      assert enrollment.leaveTermNo != 1, "User was able to edit an Enrollment into an overlapping one."
      assert enrollment.firstClassAttended != "Sophomore", "User was able to edit an Enrollment into an overlapping one."
      assert enrollment.lastClassAttended != "Junior", "User was able to edit an Enrollment into an overlapping one."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert classAttended.getProperty( "class" ) != "Sophomore", "User was able to edit a ClassAttended into an overlapping one"
      assert classAttended.classLevel != 2, "User was able to edit a ClassAttended into an overlapping one"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 2, 1902 )
      
      assert classAttended.getProperty( "class" ) != "Junior", "User was able to edit a ClassAttended into an overlapping one"
      assert classAttended.classLevel != 3, "User was able to edit a ClassAttended into an overlapping one"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      
      assert classAttended.getProperty( "class" ) != "Junior", "User was able to edit a ClassAttended into an overlapping one"
      assert classAttended.classLevel != 3, "User was able to edit a ClassAttended into an overlapping one"
            
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 1, 1903 )
      
      assert classAttended.getProperty( "class" ) != "Junior", "User was able to edit a ClassAttended into an overlapping one"
      assert classAttended.classLevel != 3, "User was able to edit a ClassAttended into an overlapping one"
            
      /* Enrollment Delete Test with a missing action parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete an Enrollment using no action parameter."
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete an Enrollment using no action parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using no action parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() > 0, "User was able to delete Classes Attended using no action parameter"
      
      /* Enrollment Delete Test with an invalid action parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
        .addQueryParam( "action", "remove" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete an Enrollment using an invalid action parameter."
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete an Enrollment using an invalid action parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using an invalid action parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() > 0, "User was able to delete Classes Attended using an invalid action parameter"
      
      /* Enrollment Delete Test with no id parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "enrollTerm", "1902 Term 1" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete an Enrollment using no id parameter."
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete an Enrollment using no id parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using no id parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() > 0, "User was able to delete Classes Attended using no id parameter"
      
      /* Enrollment Delete Test with an invalid id parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", "-1" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete an Enrollment using an invalid id parameter."
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete an Enrollment using an invalid id parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using an invalid id parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() > 0, "User was able to delete Classes Attended using an invalid id parameter"
      
      /* Enrollment Delete Test with no nextTwentyOffset parameter. */
      /*urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete an Enrollment using no nextTwentyOffset parameter."
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete an Enrollment using no nextTwentyOffset parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using no nextTwentyOffset parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() > 0, "User was able to delete Classes Attended using no nextTwentyOffset parameter"
      */
      /* Enrollment Delete Test with no lastUpdateDate parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "nextTwentyOffset", 20 ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete an Enrollment using no lastUpdateDate parameter."
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete an Enrollment using no lastUpdateDate parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using no lastUpdateDate parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() > 0, "User was able to delete Classes Attended using no lastUpdateDate parameter"
      
      /* Enrollment Delete Test with an invalid lastUpdateDate parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete an Enrollment using an invalid lastUpdateDate parameter."
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ) != null, "User was able to delete an Enrollment using an invalid lastUpdateDate parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using an invalid lastUpdateDate parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() > 0, "User was able to delete Classes Attended using an invalid lastUpdateDate parameter"
      
      /* Enrollment Delete Test. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ) == null, "User was unable to delete an Enrollment."
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ) == null, "User was unable to delete an Enrollment."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count - 1, "User was unable to change EnrollmentMetaData count"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was unable to delete an Enrollment"
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 )
      lastUpdateDate = enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate()
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 ) == null, "User was unable to delete an Enrollment."
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 ) == null, "User was unable to delete an Enrollment."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count - 2, "User was unable to change EnrollmentMetaData count"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 ).size() == 0, "User was unable to delete an Enrollment"
      assert Fee.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 ).size() == 0, "User was unable to delete Fees associated to an Enrollment"
      assert Payment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1902 ).size() == 0, "User was unable to delete Payments associated to an Enrollment"
      
      enrollmentMetaData = Enrollment.findMetaDataBySchoolName( "Saint John's College MN" )
      
      enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 )
      lastUpdateDate = enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate()
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) != null, "User was able to delete the last and only Enrollment."
      assert Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ) != null, "User was able to delete the last and only Enrollment."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 3, 1901 ).size() > 0, "User was able to delete the last and only Enrollment"
      
      /* Delete the Student created for the Enrollment Save, Edit, and Delete Test. */
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", studentDocument.getId() )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", studentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      /* Delete the Gender created for the Enrollment Save, Edit, and Delete Test. */
      Entity gender = Gender.findByCode( "X" )
    
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", gender.getKey().getId() )
        .addQueryParam( "lastUpdateDate", gender.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      /* Delete the LeaveReasonCategories created for the Enrollment Save, Edit, and Delete Test. */
      Entity leaveReasonCategory = LeaveReasonCategory.findByCategory( "Becoming a Billionaire" )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", leaveReasonCategory.getKey().getId() )
        .addQueryParam( "lastUpdateDate", leaveReasonCategory.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      leaveReasonCategory = LeaveReasonCategory.findByCategory( "Becoming a Millionaire" )
    
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", leaveReasonCategory.getKey().getId() )
        .addQueryParam( "lastUpdateDate", leaveReasonCategory.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      /* Delete the ParentalRelationship created for the Enrollment Save, Edit, and Delete Test. */
      Entity parentalRelationship = ParentalRelationship.findByParentalRelationship( "Darth Vader" )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", parentalRelationship.getKey().getId() )
        .addQueryParam( "lastUpdateDate", parentalRelationship.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      /* Delete the Terms created for the Enrollment Save, Edit, and Delete Test. */
      Entity term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 2, 1901 )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", term.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", term.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 3, 1901 )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", term.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", term.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
        
		term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1902 )
      
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
        
        term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 3, 1902 )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", term.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", term.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
        
        term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1903 )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", term.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", term.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      /* Delete the Schools and Classes created for the Enrollment Save, Edit, and Delete Test. */
      Entity school = School.findByName( "Saint John's College MN" )
    
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", school.getKey().getId() )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "lastUpdateDate", school.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
    %>
    <p>Enrollment Save, Edit, and Delete Test is successful.</p>
  </body>
</html>