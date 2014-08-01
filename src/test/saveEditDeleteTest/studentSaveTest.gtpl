<%
  import com.google.appengine.api.datastore.Entity
  import com.google.appengine.api.search.Document
  import data.AnonymousParentalRelationship
  import data.Class
  import data.ClassAttended
  import data.Enrollment
  import data.EnrollmentDocument
  import data.Gender
  import data.LeaveReasonCategory
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
    <title>Student Save Test</title>
  </head>
  <body>
    <%
      Entity anonymousParentalRelationship
      Entity classAttended
      Entity enrollment
      Document enrollmentDocument
      Entity enrollmentMetaData
      Entity parent
      Entity student
      Document studentDocument
      Entity studentMetaData
      
      /* For the Save Test, make sure that the Student to create has not existed. */
      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "Student: 0202aoey has existed. Please change the test case."
      assert StudentDocument.findByStudentId( "0202aoey1" ) == null, "Student: 0202aoey1 has existed. Please change the test case."
      assert StudentDocument.findByStudentId( "0202aoey2" ) == null, "Student: 0202aoey2 has existed. Please change the test case."
      assert Gender.findByCode( "X" ) == null, "Gender: X has existed. Please change the test case."
      assert Gender.findByCode( "Y" ) == null, "Gender: Y has existed. Please change the test case."
      assert LeaveReasonCategory.findByCategory( "Becoming a Millionaire" ) == null, "Leave Reason Category: Becoming a Millionaire has existed. Please change the test case."
      assert LeaveReasonCategory.findByCategory( "Becoming a Billionaire" ) == null, "Leave Reason Category: Becoming a Billionaire has existed. Please change the test case."
      assert Parent.findByParentId( "dvader" ) == null, "Parent: dvader has existed. Please change the test case."
      assert Parent.findByParentId( "rd2" ) == null, "Parent: rd2 has existed. Please change the test case."
      assert ParentalRelationship.findByParentalRelationship( "Darth Maul" ) == null, "Parental Relationship: Darth Maul has existed. Please change the test case."
      assert ParentalRelationship.findByParentalRelationship( "Darth Vader" ) == null, "Parental Relationship: Darth Vader has existed. Please change the test case."
      assert School.findByName( "Saint John's College MN" ) == null, "School: Saint John's College MN has existed. Please change the test case."
      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) == null, "Class: { schoolName: Saint John's College MN, class: Freshman } has existed. Please change the test case."
      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) == null, "Class: { schoolName: Saint John's College MN, class: Sophomore } has existed. Please change the test case."
      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 3, 1901 ) == null, "Term: { termSchool: Saint John's College MN, termNo: 3, year: 1901 } has existed. Please change the test case."
      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1902 ) == null, "Term: { termSchool: Saint John's College MN, termNo: 1, year: 1902 } has existed. Please change the test case."
      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 2, 1902 ) == null, "Term: { termSchool: Saint John's College MN, termNo: 2, year: 1902 } has existed. Please change the test case."
      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1903 ) == null, "Term: { termSchool: Saint John's College MN, termNo: 1, year: 1903 } has existed. Please change the test case."
      
      /* Save a Gender for the Student to be saved. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "code", "X" )
        .addQueryParam( "desc", "Tamale" ).toURL() )
      
      /* Save a Gender for the Student to be saved. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "code", "Y" )
        .addQueryParam( "desc", "Yak" ).toURL() )
      
      /* Save a LeaveReasonCategory for the Student to be saved. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "category", "Becoming a Millionaire" ).toURL() )
      
      /* Save a LeaveReasonCategory for the Student to be saved. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "category", "Becoming a Billionaire" ).toURL() )
    
      /* Save a ParentalRelationship for the Student to be saved. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "parentalRelationship", "Darth Vader" ).toURL() )
      
      /* Save a ParentalRelationship for the Student to be saved. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "parentalRelationship", "Darth Maul" ).toURL() )
    
      /* Save a School and Classes for the Student to be saved. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "newClass1", "Freshman" )
        .addQueryParam( "newClass2", "Sophomore" )
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
        .addQueryParam( "boardingFeeSaint John's College MNFreshman", 100000 )
        .addQueryParam( "boardingFeeSaint John's College MNSophomore", 100000 ).toURL() )
        
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "termSchool", "Saint John's College MN" )
        .addQueryParam( "termNo", 3 )
        .addQueryParam( "year", 1901 )
        .addQueryParam( "startDate", "Aug 1 1901" )
        .addQueryParam( "endDate", "Oct 31 1901" )
        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", 60000 )
        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", 60000 )
        .addQueryParam( "boardingFeeSaint John's College MNFreshman", 100000 )
        .addQueryParam( "boardingFeeSaint John's College MNSophomore", 100000 ).toURL() )
      
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
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "termSchool", "Saint John's College MN" )
        .addQueryParam( "termNo", 1 )
        .addQueryParam( "year", 1903 )
        .addQueryParam( "startDate", "Feb 1 1903" )
        .addQueryParam( "endDate", "Apr 30 1903" )
        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", 60000 )
        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", 60000 )
        .addQueryParam( "boardingFeeSaint John's College MNFreshman", 100000 )
        .addQueryParam( "boardingFeeSaint John's College MNSophomore", 100000 ).toURL() )
      
      enrollmentMetaData = Enrollment.findMetaDataBySchoolName( "Saint John's College MN" )
      studentMetaData = Student.findMetaDataBySchoolName( "Saint John's College MN" )
      
      /* Student Save Test with a missing action parameter. */
      
      log.info "Student Save Test with a missing action parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
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
        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth VaderDeceasedInd", true ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "User was able to save a StudentDocument using no action parameter."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) == null, "User was able to save an EnrollmentDocument using no action parameter."
      assert Student.findByStudentId( "0202aoey" ) == null, "User was able to save a Student using no action parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using no action parameter"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() == 0, "User was able to save an AnonymousParentalRelationship using no action parameter."
      assert Enrollment.findLastEnrollmentByStudentId( "0202aoey" ) == null, "User was able to save an Enrollment using no action parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using no action parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a ClassAttended using no action parameter"
          
      /* Student Save Test with an invalid action parameter. */
      
      log.info "Student Save Test with an invalid action parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "create" )
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
        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth VaderDeceasedInd", true ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "User was able to save a StudentDocument using an invalid action parameter."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) == null, "User was able to save an EnrollmentDocument using an invalid action parameter."
      assert Student.findByStudentId( "0202aoey" ) == null, "User was able to save a Student using an invalid action parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using an invalid action parameter"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() == 0, "User was able to save an AnonymousParentalRelationship using an invalid action parameter."
      assert Enrollment.findLastEnrollmentByStudentId( "0202aoey" ) == null, "User was able to save an Enrollment using an invalid action parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using an invalid action parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a ClassAttended using an invalid action parameter"
      
      /* Student Save Test with a missing firstName parameter. */
      
      log.info "Student Save Test with a missing firstName parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "save" )
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
        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth VaderDeceasedInd", true ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "User was able to save a StudentDocument using no firstName parameter."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) == null, "User was able to save an EnrollmentDocument using no firstName parameter."
      assert Student.findByStudentId( "0202aoey" ) == null, "User was able to save a Student using no firstName parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using no firstName parameter"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() == 0, "User was able to save an AnonymousParentalRelationship using no firstName parameter."
      assert Enrollment.findLastEnrollmentByStudentId( "0202aoey" ) == null, "User was able to save an Enrollment using no firstName parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using no firstName parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a ClassAttended using no firstName parameter"
      
      /* Student Save Test with an invalid birthDate parameter. */
      
      log.info "Student Save Test with an invalid birthDate parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "firstName", "Adrian" )
        .addQueryParam( "lastName", "Oey" )
        .addQueryParam( "birthDate", "9/9/1900" )
        .addQueryParam( "village", "Kyetume" )
        .addQueryParam( "genderCode", "X" )
        .addQueryParam( "specialInfo", "Luke's brother" )
        .addQueryParam( "enrollmentSchool", "Saint John's College MN" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "enrollmentLeaveReason", "Won a lottery" )
        .addQueryParam( "enrollmentTermSchool1", "1902 Term 1" )
        .addQueryParam( "leaveTermSchool1", "1902 Term 2" )
        .addQueryParam( "enrollmentFirstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth VaderDeceasedInd", true ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "User was able to save a StudentDocument using an invalid birthDate parameter."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) == null, "User was able to save an EnrollmentDocument using an invalid birthDate parameter."
      assert Student.findByStudentId( "0202aoey" ) == null, "User was able to save a Student using an invalid birthDate parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using an invalid birthDate parameter"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() == 0, "User was able to save an AnonymousParentalRelationship using an invalid birthDate parameter."
      assert Enrollment.findLastEnrollmentByStudentId( "0202aoey" ) == null, "User was able to save an Enrollment using an invalid birthDate parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using an invalid birthDate parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a ClassAttended using an invalid birthDate parameter"
      
      /* Student Save Test with a missing enrollmentSchool parameter. */
      
      log.info "Student Save Test with a missing enrollmentSchool parameter."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "firstName", "Adrian" )
        .addQueryParam( "lastName", "Oey" )
        .addQueryParam( "birthDate", "Sep 9 1900" )
        .addQueryParam( "village", "Kyetume" )
        .addQueryParam( "genderCode", "X" )
        .addQueryParam( "specialInfo", "Luke's brother" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "enrollmentLeaveReason", "Won a lottery" )
        .addQueryParam( "enrollmentTermSchool1", "1902 Term 1" )
        .addQueryParam( "leaveTermSchool1", "1902 Term 2" )
        .addQueryParam( "enrollmentFirstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth VaderDeceasedInd", true ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "User was able to save a StudentDocument using no enrollmentSchool parameter."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) == null, "User was able to save an EnrollmentDocument using no enrollmentSchool parameter."
      assert Student.findByStudentId( "0202aoey" ) == null, "User was able to save a Student using no enrollmentSchool parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using no enrollmentSchool parameter"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() == 0, "User was able to save an AnonymousParentalRelationship using no enrollmentSchool parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using no enrollmentSchool parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a ClassAttended using no enrollmentSchool parameter"
      
      /* Student Save Test with a missing enrollmentTermSchool1 parameter. */
      
      log.info "Student Save Test with a missing enrollmentTermSchool1 parameter."
      
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
        .addQueryParam( "leaveTermSchool1", "1902 Term 2" )
        .addQueryParam( "enrollmentFirstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth VaderDeceasedInd", true ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "User was able to save a StudentDocument using no enrollmentTermSchool1 parameter."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) == null, "User was able to save an EnrollmentDocument using no enrollmentTermSchool1 parameter."
      assert Student.findByStudentId( "0202aoey" ) == null, "User was able to save a Student using no enrollmentTermSchool1 parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using no enrollmentTermSchool1 parameter"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() == 0, "User was able to save an AnonymousParentalRelationship using no enrollmentTermSchool1 parameter."
      assert Enrollment.findLastEnrollmentByStudentId( "0202aoey" ) == null, "User was able to save an Enrollment using no enrollmentTermSchool1 parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using no enrollmentTermSchool1 parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a ClassAttended using no enrollmentTermSchool1 parameter"
      
      /* Student Save Test with an invalid enrollmentTermSchool1 parameter. */
      
      log.info "Student Save Test with an invalid enrollmentTermSchool1 parameter."
      
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
        .addQueryParam( "enrollmentTermSchool1", "Nineteen O Two Term One" )
        .addQueryParam( "leaveTermSchool1", "1902 Term 2" )
        .addQueryParam( "enrollmentFirstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth VaderDeceasedInd", true ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "User was able to save a StudentDocument using an invalid enrollmentTermSchool1 parameter: Nineteen O One Term One."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) == null, "User was able to save an EnrollmentDocument using an invalid enrollmentTermSchool1 parameter: Nineteen O One Term One"
      assert Student.findByStudentId( "0202aoey" ) == null, "User was able to save a Student using an invalid enrollmentTermSchool1 parameter: Nineteen O One Term One."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using an invalid enrollmentTermSchool1 parameter: Nineteen O One Term One"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() == 0, "User was able to save an AnonymousParentalRelationship using an invalid enrollmentTermSchool1 parameter: Nineteen O One Term One."
      assert Enrollment.findLastEnrollmentByStudentId( "0202aoey" ) == null, "User was able to save an Enrollment using an invalid enrollmentTermSchool1 parameter: Nineteen O One Term One."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using an invalid enrollmentTermSchool1 parameter: Nineteen O One Term One"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a ClassAttended using an invalid enrollmentTermSchool1 parameter: Nineteen O One Term One"
      
      /* Student Save Test with an invalid enrollmentTermSchool1 parameter. */
      
      log.info "Student Save Test with an invalid enrollmentTermSchool1 parameter."
      
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
        .addQueryParam( "enrollmentTermSchool1", "1 Term 1902" )
        .addQueryParam( "leaveTermSchool1", "1902 Term 2" )
        .addQueryParam( "enrollmentFirstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth VaderDeceasedInd", true ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "User was able to save a StudentDocument using an invalid enrollmentTermSchool1 parameter: 1 Term 1901."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) == null, "User was able to save an EnrollmentDocument using an invalid enrollmentTermSchool1 parameter: 1 Term 1901"
      assert Student.findByStudentId( "0202aoey" ) == null, "User was able to save a Student using an invalid enrollmentTermSchool1 parameter: 1 Term 1901."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using an invalid enrollmentTermSchool1 parameter"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() == 0, "User was able to save an AnonymousParentalRelationship using an invalid enrollmentTermSchool1 parameter: 1 Term 1901."
      assert Enrollment.findLastEnrollmentByStudentId( "0202aoey" ) == null, "User was able to save an Enrollment using an invalid enrollmentTermSchool1 parameter: 1 Term 1901."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using an invalid enrollmentTermSchool1 parameter: 1 Term 1901"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a ClassAttended using an invalid enrollmentTermSchool1 parameter"
      
      /* Student Save Test with an invalid leaveTermSchool1 parameter. */
      
      log.info "Student Save Test with an invalid leaveTermSchool1 parameter."
      
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
        .addQueryParam( "leaveTermSchool1", "Nineteen O Two Term Two" )
        .addQueryParam( "enrollmentFirstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth VaderDeceasedInd", true ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "User was able to save a StudentDocument using an invalid leaveTermSchool1 parameter: Nineteen O One Term Two."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) == null, "User was able to save an EnrollmentDocument using an invalid leaveTermSchool1 parameter: Nineteen O One Term Two"
      assert Student.findByStudentId( "0202aoey" ) == null, "User was able to save a Student using an invalid leaveTermSchool1 parameter: Nineteen O One Term Two."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using an invalid leaveTermSchool1 parameter: Nineteen O One Term Two"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() == 0, "User was able to save an AnonymousParentalRelationship using an invalid leaveTermSchool1 parameter: Nineteen O One Term Two."
      assert Enrollment.findLastEnrollmentByStudentId( "0202aoey" ) == null, "User was able to save an Enrollment using an invalid leaveTermSchool1 parameter: Nineteen O One Term Two."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using an invalid leaveTermSchool1 parameter: Nineteen O One Term Two"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a ClassAttended using an invalid leaveTermSchool1 parameter: Nineteen O One Term Two"
      
      /* Student Save Test with an invalid leaveTermSchool1 parameter. */
      
      log.info "Student Save Test with an invalid leaveTermSchool1 parameter."
      
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
        .addQueryParam( "leaveTermSchool1", "2 Term 1902" )
        .addQueryParam( "enrollmentFirstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth VaderDeceasedInd", true ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "User was able to save a StudentDocument using an invalid leaveTermSchool1 parameter: 2 Term 1901."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) == null, "User was able to save an EnrollmentDocument using an invalid leaveTermSchool1 parameter: 2 Term 1901"
      assert Student.findByStudentId( "0202aoey" ) == null, "User was able to save a Student using an invalid leaveTermSchool1 parameter: 2 Term 1901."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using an invalid leaveTermSchool1 parameter"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() == 0, "User was able to save an AnonymousParentalRelationship using an invalid leaveTermSchool1 parameter: 2 Term 1901."
      assert Enrollment.findLastEnrollmentByStudentId( "0202aoey" ) == null, "User was able to save an Enrollment using an invalid leaveTermSchool1 parameter: 2 Term 1901."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using an invalid leaveTermSchool1 parameter: 2 Term 1901"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a ClassAttended using an invalid leaveTermSchool1 parameter: 2 Term 1901"
      
      /* Student Save Test with an invalid term range. */
      
      log.info "Student Save Test with an invalid term range."
      
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
        .addQueryParam( "enrollmentTermSchool1", "1902 Term 2" )
        .addQueryParam( "leaveTermSchool1", "1902 Term 1" )
        .addQueryParam( "enrollmentFirstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth VaderDeceasedInd", true ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "User was able to save a StudentDocument using an invalid term range."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) == null, "User was able to save an EnrollmentDocument using an invalid term range"
      assert Student.findByStudentId( "0202aoey" ) == null, "User was able to save a Student using an invalid term range."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using an invalid term range"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() == 0, "User was able to save an AnonymousParentalRelationship using an invalid term range."
      assert Enrollment.findLastEnrollmentByStudentId( "0202aoey" ) == null, "User was able to save an Enrollment using an invalid term range."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using an invalid term range"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 2, 1902 ).size() == 0, "User was able to save a ClassAttended using an invalid term range"
      
      /* Student Save Test with a missing enrollmentFirstClassAttendedSchool1 parameter. */
      
      log.info "Student Save Test with a missing enrollmentFirstClassAttendedSchool1 parameter."
      
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
        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth VaderDeceasedInd", true ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "User was able to save a StudentDocument using no enrollmentFirstClassAttendedSchool1 parameter."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) == null, "User was able to save an EnrollmentDocument using no enrollmentFirstClassAttendedSchool1 parameter"
      assert Student.findByStudentId( "0202aoey" ) == null, "User was able to save a Student using no enrollmentFirstClassAttendedSchool1 parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using no enrollmentFirstClassAttendedSchool1 parameter"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() == 0, "User was able to save an AnonymousParentalRelationship using no enrollmentFirstClassAttendedSchool1 parameter."
      assert Enrollment.findLastEnrollmentByStudentId( "0202aoey" ) == null, "User was able to save an Enrollment using no enrollmentFirstClassAttendedSchool1 parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using no enrollmentFirstClassAttendedSchool1 parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a ClassAttended using no enrollmentFirstClassAttendedSchool1 parameter"
      
      /* Student Save Test with a missing enrollmentLastClassAttendedSchool1 parameter. */
      
      log.info "Student Save Test with a missing enrollmentLastClassAttendedSchool1 parameter."
      
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
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth VaderDeceasedInd", true ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "User was able to save a StudentDocument using no enrollmentLastClassAttendedSchool1 parameter."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) == null, "User was able to save an EnrollmentDocument using no enrollmentLastClassAttendedSchool1 parameter"
      assert Student.findByStudentId( "0202aoey" ) == null, "User was able to save a Student using no enrollmentLastClassAttendedSchool1 parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using no enrollmentLastClassAttendedSchool1 parameter"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() == 0, "User was able to save an AnonymousParentalRelationship using no enrollmentLastClassAttendedSchool1 parameter."
      assert Enrollment.findLastEnrollmentByStudentId( "0202aoey" ) == null, "User was able to save an Enrollment using no enrollmentLastClassAttendedSchool1 parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using no enrollmentLastClassAttendedSchool1 parameter"
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was able to save a ClassAttended using no enrollmentLastClassAttendedSchool1 parameter"
      
      /* Student Save Test. */
      
      log.info "Student Save Test."
      
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
        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth VaderDeceasedInd", true ).toURL() )
      
      log.info "Testing StudentDocument value."
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      Date lastUpdateDate = new Date( studentDocument.getOnlyField( "lastUpdateDate" ).getDate().getTime() )
      
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
      assert studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 1" ), "Terms Enrolled: ${ studentDocument.getOnlyField( "termsEnrolled" ).getText() } from StudentDocument.findByStudentId( String studentId ) does not contain 1902 Term 1"
      assert studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 2" ), "Terms Enrolled: ${ studentDocument.getOnlyField( "termsEnrolled" ).getText() } from StudentDocument.findByStudentId( String studentId ) does not contain 1902 Term 2"
      assert studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber() == 1902, "Last Enrollment Term Year: ${ studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to 1902."
      assert studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber() == 1, "Last Enrollment Term No: ${ studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to 1."
      assert studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate() == Date.parse( "MMM d yy", "Feb 1 1902" ), "Last Enrollment Term Start Date: ${ studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate().format( "MMM d yy" ) } from StudentDocument.findByStudentId( String studentId ) is not equal to Feb 1 1902"
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() == 1902, "Last Enrollment Leave Term Year: ${ studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to 1902."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() == 2, "Last Enrollment Leave Term No: ${ studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to 2."
      assert studentDocument.getOnlyField( "classesAttended" ).getText().contains( "Freshman" ), "Classes Attended: ${ studentDocument.getOnlyField( "classesAttended" ).getText() } from StudentDocument.findByStudentId( String studentId ) does not contain Freshman"
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() == "Freshman", "Last Enrollment First Class Attended: ${ studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Freshman."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() == "Freshman", "LastEnrollment Last Class Attended: ${ studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Freshman."
      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() == 120000, "Tuition Fees: ${ studentDocument.getOnlyField( "tuitionFees" ).getNumber() } from StudentDocument.findByStudentId( String studentId ) is not equal to 120000"
      assert studentDocument.getOnlyField( "boardingFees" ).getNumber() == 100000, "Boarding Fees: ${ studentDocument.getOnlyField( "boardingFees" ).getNumber() } from StudentDocument.findByStudentId( String studentId ) is not equal to 200000"
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "Other Fees: ${ studentDocument.getOnlyField( "otherFees" ).getNumber() } from StudentDocument.findByStudentId( String studentId ) is not equal to 0"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 220000, "Fees Due: ${ studentDocument.getOnlyField( "feesDue" ).getNumber() } from StudentDocument.findByStudentId( String studentId ) is not equal to 320000"
      assert studentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() == new Date().clearTime(), "Last Update Date: ${ studentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() } from StudentDocument.findByStudentId( def studentId ) is not equal to ${ new Date().clearTime() }."
      
      log.info "Testing EnrollmentDocument value."
      
      enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" )
      
      assert enrollmentDocument != null, "User was unable to save an EnrollmentDocument."
      assert enrollmentDocument.getOnlyField( "studentId" ).getAtom() == "0202aoey", "Student ID: ${ enrollmentDocument.getOnlyField( "studentId" ).getAtom() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 0202aoey."
      assert enrollmentDocument.getOnlyField( "firstName" ).getText() == "Adrian", "First Name: ${ enrollmentDocument.getOnlyField( "firstName" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to Adrian."
      assert enrollmentDocument.getOnlyField( "lastName" ).getText() == "Oey", "Last Name: ${ enrollmentDocument.getOnlyField( "lastName" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to Oey."
      assert enrollmentDocument.getOnlyField( "birthDate" ).getDate() == Date.parse( "MMM d yy", "Sep 9 1900" ), "Birth Date: ${ enrollmentDocument.getOnlyField( "birthDate" ).getDate().format( "MMM d yy" ) } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to Sep 9 1900."
      assert enrollmentDocument.getOnlyField( "village" ).getText() == "Kyetume", "Village: ${ enrollmentDocument.getOnlyField( "village" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to Kyetume."
      assert enrollmentDocument.getOnlyField( "genderCode" ).getText() == "X", "Gender Code: ${ enrollmentDocument.getOnlyField( "genderCode" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to X."
      assert enrollmentDocument.getOnlyField( "specialInfo" ).getText() == "Luke's brother", "Special Info: ${ enrollmentDocument.getOnlyField( "specialInfo" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to Luke's brother."
      assert enrollmentDocument.getOnlyField( "schoolName" ).getText() == "Saint John's College MN", "School Name: ${ enrollmentDocument.getOnlyField( "schoolName" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to Saint John's College MN."
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() == "Becoming a Millionaire", "Leave Reason Category: ${ enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to Becoming a Millionaire."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() == "Won a lottery", "Leave Reason: ${ enrollmentDocument.getOnlyField( "leaveReason" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to Won a lottery."
      assert enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 1" ), "Terms Enrolled: ${ enrollmentDocument.getOnlyField( "termsEnrolled" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) does not contain 1902 Term 1"
      assert enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 2" ), "Terms Enrolled: ${ enrollmentDocument.getOnlyField( "termsEnrolled" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) does not contain 1902 Term 2"
      assert enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() == 1902, "Enrollment Term Year: ${ enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 1902."
      assert enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() == 1, "Enrollment Term No: ${ enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 1."
      assert enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() == Date.parse( "MMM d yy", "Feb 1 1902" ), "Enrollment Term Start Date: ${ enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate().format( "MMM d yy" ) } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to Feb 1 1902"
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() == 1902, "Leave Term Year: ${ enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 1902."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() == 2, "Leave Term No: ${ enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 2."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() == Date.parse( "MMM d yyyy", "Jul 31 1902" ), "Leave Date: ${ enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() } from enrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to Jul 31 1902."
      assert enrollmentDocument.getOnlyField( "classesAttended" ).getText().contains( "Freshman" ), "Classes Attended: ${ enrollmentDocument.getOnlyField( "classesAttended" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) does not contain Freshman"
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() == "Freshman", "First Class Attended: ${ enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to Freshman."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() == "Freshman", "Last Class Attended: ${ enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to Freshman."
      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() == 120000, "Tuition Fees: ${ enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 120000"
      assert enrollmentDocument.getOnlyField( "boardingFees" ).getNumber() == 100000, "Boarding Fees: ${ enrollmentDocument.getOnlyField( "boardingFees" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 200000"
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "Other Fees: ${ enrollmentDocument.getOnlyField( "otherFees" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 0"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 220000, "Fees Due: ${ enrollmentDocument.getOnlyField( "feesDue" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 320000"
      assert enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() == new Date().clearTime(), "Last Update Date: ${ enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to ${ new Date().clearTime() }."
      
      log.info "Testing Student value."
      
      student = Student.findByStudentId( "0202aoey" )
      
      assert student != null, "User was unable to save a Student."
      assert student.studentId == "0202aoey", "Student ID: ${ student.studentId } from Student.findByStudentId( def studentId ) is not equal to 0202aoey."
      assert student.firstName == "Adrian", "First Name: ${ student.firstName } from Student.findByStudentId( def studentId ) is not equal to Adrian."
      assert student.lastName == "Oey", "Last Name: ${ student.lastName } from Student.findByStudentId( def studentId ) is not equal to Oey."
      assert student.birthDate == Date.parse( "MMM d yy", "Sep 9 1900" ), "Birth Date: ${ student.birthDate.format( "MMM d yy" ) } from Student.findByStudentId( def studentId ) is not equal to Sep 9 1900."
      assert student.village == "Kyetume", "Village: ${ student.village } from Student.findByStudentId( def studentId ) is not equal to Kyetume."
      assert student.genderCode == "X", "Gender Code: ${ student.genderCode } from Student.findByStudentId( def studentId ) is not equal to X."
      assert student.specialInfo == "Luke's brother", "Special Info: ${ student.specialInfo } from Student.findByStudentId( def studentId ) is not equal to Luke's brother."
      assert student.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ student.lastUpdateDate.clearTime() } from Student.findByStudentId( def studentId ) is not equal to ${ new Date().clearTime() }."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count + 1, "Student Meta Data Count: ${ Student.findMetaDataBySchoolName( "Saint John's College MN" ).count } from Student.findByStudentId( String studentId ) is not equal to ${ studentMetaData.count + 1 }"
      
      log.info "Testing AnonymousParentalRelationship values."
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Vader" )
      
      assert anonymousParentalRelationship != null, "User was unable to save an AnonymousParentalRelationship."
      assert anonymousParentalRelationship.studentId == "0202aoey", "Student ID: ${ anonymousParentalRelationship.studentId } from AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( def studentId, def parentalRelationship ) is not equal to 0202aoey."
      assert anonymousParentalRelationship.parentalRelationship == "Darth Vader", "Parental Relationship: ${ anonymousParentalRelationship.parentalRelationship } from AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( def studentId, def parentalRelationship ) is not equal to Darth Vader."
      assert anonymousParentalRelationship.deceasedInd == "Y", "Deceased Ind: ${ anonymousParentalRelationship.deceasedInd } from AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( def studentId, def parentalRelationship ) is not equal to Y."
      assert anonymousParentalRelationship.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ anonymousParentalRelationship.lastUpdateDate.clearTime() } from AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( def studentId, def parentalRelationship ) is not equal to ${ new Date().clearTime() }."
      
      AnonymousParentalRelationship.findByStudentId( "0202aoey" ).each() {
        if( !it.parentalRelationship.equals( "Darth Vader" ) )
          assert it.deceasedInd == "N", "${ it.parentalRelationship } Deceased Ind: ${ it.deceasedInd } from AnonymousParentalRelationship.findByStudentId( def studentId ) is not equal to N."
      }
      
      log.info "Testing Enrollment value."
      
      enrollment = Enrollment.findLastEnrollmentByStudentId( "0202aoey" )
      
      assert enrollment != null, "User was unable to save an Enrollment."
      assert enrollment.studentId == "0202aoey", "Student ID: ${ enrollment.studentId } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to 0202aoey."
      assert enrollment.schoolName == "Saint John's College MN", "School Name: ${ enrollment.schoolName } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to Saint John's College MN."
      assert enrollment.leaveReasonCategory == "Becoming a Millionaire", "Leave Reason Category: ${ enrollment.leaveReasonCategory } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to Becoming a Millionaire."
      assert enrollment.leaveReason == "Won a lottery", "Leave Reason: ${ enrollment.leaveReason } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to Won a lottery."
      assert enrollment.enrollTermYear == 1902, "Enrollment Term Year: ${ enrollment.enrollTermYear } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to 1902."
      assert enrollment.enrollTermNo == 1, "Enrollment Term No: ${ enrollment.enrollTermNo } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to 1."
      assert enrollment.leaveTermYear == 1902, "Leave Term Year: ${ enrollment.leaveTermYear } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to 1902."
      assert enrollment.leaveTermNo == 2, "Leave Term No: ${ enrollment.leaveTermNo } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to 2."
      assert enrollment.firstClassAttended == "Freshman", "First Class Attended: ${ enrollment.firstClassAttended } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to Freshman."
      assert enrollment.lastClassAttended == "Freshman", "Last Class Attended: ${ enrollment.lastClassAttended } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to Freshman."
      assert enrollment.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ enrollment.lastUpdateDate.clearTime() } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to ${ new Date().clearTime() }."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count + 1, "Enrollment Meta Data Count: ${ Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count } from Enrollment.findMetaDataBySchoolName( String schoolName ) is not equal to ${ enrollmentMetaData.count + 1 }"
      
      log.info "Testing ClassAttended value"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert classAttended != null, "User was unable to save a ClassAttended"
      assert classAttended.studentId == "0202aoey", "Student ID: ${ classAttended.studentId } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 0202aoey"
      assert classAttended.schoolName == "Saint John's College MN", "School Name: ${ classAttended.schoolName } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Saint John's College MN"
      assert classAttended.enrollTermNo == 1, "Enrollment Term No: ${ classAttended.enrollTermNo } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1"
      assert classAttended.enrollTermYear == 1902, "Enrollment Term Year: ${ classAttended.enrollTermYear } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1902"
      assert classAttended.getProperty( "class" ) == "Freshman", "Class: ${ classAttended.getProperty( "class" ) } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Freshman"
      assert classAttended.classLevel == 1, "Class Level: ${ classAttended.classLevel } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1"
      assert classAttended.classTermNo == 1, "Class Term No: ${ classAttended.classTermNo } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1"
      assert classAttended.classTermYear == 1902, "Class Term Year: ${ classAttended.classTermYear } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1902"
      assert classAttended.tuitionFee == 60000, "Tuition Fee: ${ classAttended.tuitionFee } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 60000"
      assert classAttended.boardingFee == 100000, "Boarding Fee: ${ classAttended.boardingFee } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 100000"
      assert classAttended.boardingInd == "Y", "Boarding Ind: ${ classAttended.boardingInd } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Y"
      assert classAttended.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ classAttended.lastUpdateDate.clearTime() } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to ${ new Date().clearTime() }"
      
      log.info "Testing ClassAttended value"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 2, 1902 )
      
      assert classAttended != null, "User was unable to save a ClassAttended"
      assert classAttended.studentId == "0202aoey", "Student ID: ${ classAttended.studentId } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 0202aoey"
      assert classAttended.schoolName == "Saint John's College MN", "School Name: ${ classAttended.schoolName } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Saint John's College MN"
      assert classAttended.enrollTermNo == 1, "Enrollment Term No: ${ classAttended.enrollTermNo } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1"
      assert classAttended.enrollTermYear == 1902, "Enrollment Term Year: ${ classAttended.enrollTermYear } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1902"
      assert classAttended.getProperty( "class" ) == "Freshman", "Class: ${ classAttended.getProperty( "class" ) } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Freshman"
      assert classAttended.classLevel == 1, "Class Level: ${ classAttended.classLevel } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1"
      assert classAttended.classTermNo == 2, "Class Term No: ${ classAttended.classTermNo } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 2"
      assert classAttended.classTermYear == 1902, "Class Term Year: ${ classAttended.classTermYear } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1902"
      assert classAttended.tuitionFee == 60000, "Tuition Fee: ${ classAttended.tuitionFee } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 60000"
      assert classAttended.boardingFee == 100000, "Boarding Fee: ${ classAttended.boardingFee } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 100000"
      assert classAttended.boardingInd == "N", "Boarding Ind: ${ classAttended.boardingInd } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to N"
      assert classAttended.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ classAttended.lastUpdateDate.clearTime() } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to ${ new Date().clearTime() }"
      
      /* Student Duplicate Student ID and Empty Non-Required Field Save Test. */
      
      log.info "Student Duplicate Student ID and Empty Non-Required Field Save Test."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "firstName", "Adrian" )
        .addQueryParam( "lastName", "Oey" )
        .addQueryParam( "enrollmentSchool", "Saint John's College MN" )
        .addQueryParam( "enrollmentTermSchool1", "1902 Term 1" )
        .addQueryParam( "enrollmentFirstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "boardingInd1902 Term 1", true ).toURL() )
      
      log.info "Testing StudentDocument value."
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey1" )
      Set<String> studentDocumentFieldNames = studentDocument.getFieldNames()
      
      Date lastUpdateDateOfDuplicate1 = new Date( studentDocument.getOnlyField( "lastUpdateDate" ).getDate().getTime() )
      
      assert studentDocument != null, "User was unable to save a StudentDocument."
      assert studentDocument.getOnlyField( "studentId" ).getAtom() == "0202aoey1", "Student ID: ${ studentDocument.getOnlyField( "studentId" ).getAtom() } from StudentDocument.findByStudentId( def studentId ) is not equal to 0202aoey1."
      assert studentDocument.getOnlyField( "firstName" ).getText() == "Adrian", "First Name: ${ studentDocument.getOnlyField( "firstName" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Adrian."
      assert studentDocument.getOnlyField( "lastName" ).getText() == "Oey", "Last Name: ${ studentDocument.getOnlyField( "lastName" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Oey."
      assert !studentDocumentFieldNames.contains( "birthDate" ), "Birth Date: ${ studentDocument.getOnlyField( "birthDate" ).getDate().format( "MMM d yy" ) } from StudentDocument.findByStudentId( def studentId ) is not equal to null."
      assert !studentDocumentFieldNames.contains( "village" ), "Village: ${ studentDocument.getOnlyField( "village" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to null."
      assert !studentDocumentFieldNames.contains( "genderCode" ), "Gender Code: ${ studentDocument.getOnlyField( "genderCode" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to null."
      assert !studentDocumentFieldNames.contains( "specialInfo" ), "Special Info: ${ studentDocument.getOnlyField( "specialInfo" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to null."
      assert studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText() == "Saint John's College MN", "Last Enrollment School: ${ studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Saint John's College MN."
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveReasonCategory" ), "Last Enrollment Leave Reason Category: ${ studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to null."
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveReason" ), "Last Enrollment Leave Reason: ${ studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to null."
      assert studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 1" ), "Terms Enrolled: ${ studentDocument.getOnlyField( "termsEnrolled" ).getText() } from StudentDocument.findByStudentId( String studentId ) does not contain 1902 Term 1"
      assert studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 2" ), "Terms Enrolled: ${ studentDocument.getOnlyField( "termsEnrolled" ).getText() } from StudentDocument.findByStudentId( String studentId ) does not contain 1902 Term 2"
      assert studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber() == 1902, "Last Enrollment Term Year: ${ studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to 1902."
      assert studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber() == 1, "Last Enrollment Term No: ${ studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to 1."
      assert studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate() == Date.parse( "MMM d yy", "Feb 1 1902" ), "Last Enrollment Term Start Date: ${ studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate().format( "MMM d yy" ) } is not equal to Feb 1 1902"
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermYear" ), "Last Enrollment Leave Term Year: ${ studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to null."
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermNo" ), "Last Enrollment Leave Term No: ${ studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to null."
      assert studentDocument.getOnlyField( "classesAttended" ).getText().contains( "Freshman" ), "Classes Attended: ${ studentDocument.getOnlyField( "classesAttended" ).getText() } from StudentDocument.findByStudentId( String studentId ) does not contain Freshman"
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() == "Freshman", "Last Enrollment First Class Attended: ${ studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Freshman."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() == "Freshman", "LastEnrollment Last Class Attended: ${ studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Freshman."
      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() == 120000, "Tuition Fees: ${ studentDocument.getOnlyField( "tuitionFees" ).getNumber() } from StudentDocument.findByStudentId( String studentId ) is not equal to 120000"
      assert studentDocument.getOnlyField( "boardingFees" ).getNumber() == 100000, "Boarding Fees: ${ studentDocument.getOnlyField( "boardingFees" ).getNumber() } from StudentDocument.findByStudentId( String studentId ) is not equal to 200000"
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "Other Fees: ${ studentDocument.getOnlyField( "otherFees" ).getNumber() } from StudentDocument.findByStudentId( String studentId ) is not equal to 0"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 220000, "Fees Due: ${ studentDocument.getOnlyField( "feesDue" ).getNumber() } from StudentDocument.findByStudentId( String studentId ) is not equal to 320000"
      assert studentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() == new Date().clearTime(), "Last Update Date: ${ studentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() } from StudentDocument.findByStudentId( def studentId ) is not equal to ${ new Date().clearTime() }."
      
      log.info "Testing EnrollmentDocument value."
      
      enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey1" )
      Set<String> enrollmentDocumentFieldNames = enrollmentDocument.getFieldNames()
      
      assert enrollmentDocument != null, "User was unable to save an EnrollmentDocument."
      assert enrollmentDocument.getOnlyField( "studentId" ).getAtom() == "0202aoey1", "Student ID: ${ enrollmentDocument.getOnlyField( "studentId" ).getAtom() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 0202aoey1."
      assert enrollmentDocument.getOnlyField( "firstName" ).getText() == "Adrian", "First Name: ${ enrollmentDocument.getOnlyField( "firstName" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to Adrian."
      assert enrollmentDocument.getOnlyField( "lastName" ).getText() == "Oey", "Last Name: ${ enrollmentDocument.getOnlyField( "lastName" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to Oey."
      assert !enrollmentDocumentFieldNames.contains( "birthDate" ), "Birth Date: ${ enrollmentDocument.getOnlyField( "birthDate" ).getDate().format( "MMM d yy" ) } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to null."
      assert !enrollmentDocumentFieldNames.contains( "village" ), "Village: ${ enrollmentDocument.getOnlyField( "village" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to null."
      assert !enrollmentDocumentFieldNames.contains( "genderCode" ), "Gender Code: ${ enrollmentDocument.getOnlyField( "genderCode" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to null."
      assert !enrollmentDocumentFieldNames.contains( "specialInfo" ), "Special Info: ${ enrollmentDocument.getOnlyField( "specialInfo" ).getText() } from EnrollmentDocument.findByStudentId( String studentId ) is not equal to null."
      assert enrollmentDocument.getOnlyField( "schoolName" ).getText() == "Saint John's College MN", "School Name: ${ enrollmentDocument.getOnlyField( "schoolName" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to Saint John's College MN."
      assert !enrollmentDocumentFieldNames.contains( "leaveReasonCategory" ), "Leave Reason Category: ${ enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to null."
      assert !enrollmentDocumentFieldNames.contains( "leaveReason" ), "Leave Reason: ${ enrollmentDocument.getOnlyField( "leaveReason" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to null."
      assert enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 1" ), "Terms Enrolled: ${ enrollmentDocument.getOnlyField( "termsEnrolled" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) does not contain 1902 Term 1"
      assert enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 2" ), "Terms Enrolled: ${ enrollmentDocument.getOnlyField( "termsEnrolled" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) does not contain 1902 Term 2"
      assert enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() == 1902, "Enrollment Term Year: ${ enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 1902."
      assert enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() == 1, "Enrollment Term No: ${ enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 1."
      assert enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() == Date.parse( "MMM d yy", "Feb 1 1902" ), "Enrollment Term Start Date: ${ enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate().format( "MMM d yy" ) } is not equal to Feb 1 1902"
      assert !enrollmentDocumentFieldNames.contains( "leaveTermYear" ), "Leave Term Year: ${ enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to null."
      assert !enrollmentDocumentFieldNames.contains( "leaveTermNo" ), "Leave Term No: ${ enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to null."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() == Date.parse( "MMM d yyyy", "Dec 31 2999" ), "Leave Date: ${ enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to Dec 31 2999."
      assert enrollmentDocument.getOnlyField( "classesAttended" ).getText().contains( "Freshman" ), "Classes Attended: ${ enrollmentDocument.getOnlyField( "classesAttended" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) does not contain Freshman"
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() == "Freshman", "First Class Attended: ${ enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to Freshman."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() == "Freshman", "Last Class Attended: ${ enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to Freshman."
      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() == 120000, "Tuition Fees: ${ enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 120000"
      assert enrollmentDocument.getOnlyField( "boardingFees" ).getNumber() == 100000, "Boarding Fees: ${ enrollmentDocument.getOnlyField( "boardingFees" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 200000"
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "Other Fees: ${ enrollmentDocument.getOnlyField( "otherFees" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 0"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 220000, "Fees Due: ${ enrollmentDocument.getOnlyField( "feesDue" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 320000"
      assert enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() == new Date().clearTime(), "Last Update Date: ${ enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to ${ new Date().clearTime() }."

      log.info "Testing Student value."
      
      student = Student.findByStudentId( "0202aoey1" )
      
      assert student != null, "User was unable to save a Student."
      assert student.studentId == "0202aoey1", "Student ID: ${ student.studentId } from Student.findByStudentId( def studentId ) is not equal to 0202aoey1."
      assert student.firstName == "Adrian", "First Name: ${ student.firstName } from Student.findByStudentId( def studentId ) is not equal to Adrian."
      assert student.lastName == "Oey", "Last Name: ${ student.lastName } from Student.findByStudentId( def studentId ) is not equal to Oey."
      assert student.birthDate == null, "Birth Date: ${ student.birthDate?.format( "MMM d yy" ) } from Student.findByStudentId( def studentId ) is not equal to null."
      assert student.village == null, "Village: ${ student.village } from Student.findByStudentId( def studentId ) is not equal to null."
      assert student.genderCode == null, "Gender Code: ${ student.genderCode } from Student.findByStudentId( def studentId ) is not equal to null."
      assert student.specialInfo == null, "Special Info: ${ student.specialInfo } from Student.findByStudentId( def studentId ) is not equal to null."
      assert student.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ student.lastUpdateDate.clearTime() } from Student.findByStudentId( def studentId ) is not equal to ${ new Date().clearTime() }."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count + 2, "Student Meta Data Count: ${ Student.findMetaDataBySchoolName( "Saint John's College MN" ).count } from Student.findByStudentId( String studentId ) is not equal to ${ studentMetaData.count + 2 }"
      
      log.info "Testing AnonymousParentalRelationship values."
      
      AnonymousParentalRelationship.findByStudentId( "0202aoey1" ).each() {
        assert it.deceasedInd == "N", "${ it.parentalRelationship } Deceased Ind: ${ it.deceasedInd } from AnonymousParentalRelationship.findByStudentId( def studentId ) is not equal to N."
      }
      
      log.info "Testing Enrollment value."
      
      enrollment = Enrollment.findLastEnrollmentByStudentId( "0202aoey1" )
      
      assert enrollment != null, "User was unable to save an Enrollment."
      assert enrollment.studentId == "0202aoey1", "Student ID: ${ enrollment.studentId } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to 0202aoey1."
      assert enrollment.schoolName == "Saint John's College MN", "School Name: ${ enrollment.schoolName } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to Saint John's College MN."
      assert enrollment.leaveReasonCategory == null, "Leave Reason Category: ${ enrollment.leaveReasonCategory } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to null."
      assert enrollment.leaveReason == null, "Leave Reason: ${ enrollment.leaveReason } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to null."
      assert enrollment.enrollTermYear == 1902, "Enrollment Term Year: ${ enrollment.enrollTermYear } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to 1902."
      assert enrollment.enrollTermNo == 1, "Enrollment Term No: ${ enrollment.enrollTermNo } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to 1."
      assert enrollment.leaveTermYear == null, "Leave Term Year: ${ enrollment.leaveTermYear } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to null."
      assert enrollment.leaveTermNo == null, "Leave Term No: ${ enrollment.leaveTermNo } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to null."
      assert enrollment.firstClassAttended == "Freshman", "First Class Attended: ${ enrollment.firstClassAttended } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to Freshman."
      assert enrollment.lastClassAttended == "Freshman", "Last Class Attended: ${ enrollment.lastClassAttended } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to Freshman."
      assert enrollment.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ enrollment.lastUpdateDate.clearTime() } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to ${ new Date().clearTime() }."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count + 2, "Enrollment Meta Data Count: ${ Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count } from Enrollment.findMetaDataBySchoolName( String schoolName ) is not equal to ${ enrollmentMetaData.count + 1 }"
      
      log.info "Testing ClassAttended value"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey1", "Saint John's College MN", 1, 1902 )
      
      assert classAttended != null, "User was unable to save a ClassAttended"
      assert classAttended.studentId == "0202aoey1", "Student ID: ${ classAttended.studentId } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 0202aoey1"
      assert classAttended.schoolName == "Saint John's College MN", "School Name: ${ classAttended.schoolName } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Saint John's College MN"
      assert classAttended.enrollTermNo == 1, "Enrollment Term No: ${ classAttended.enrollTermNo } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1"
      assert classAttended.enrollTermYear == 1902, "Enrollment Term Year: ${ classAttended.enrollTermYear } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1902"
      assert classAttended.getProperty( "class" ) == "Freshman", "Class: ${ classAttended.getProperty( "class" ) } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Freshman"
      assert classAttended.classLevel == 1, "Class Level: ${ classAttended.classLevel } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1"
      assert classAttended.classTermNo == 1, "Class Term No: ${ classAttended.classTermNo } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1"
      assert classAttended.classTermYear == 1902, "Class Term Year: ${ classAttended.classTermYear } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1902"
      assert classAttended.tuitionFee == 60000, "Tuition Fee: ${ classAttended.tuitionFee } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 60000"
      assert classAttended.boardingFee == 100000, "Boarding Fee: ${ classAttended.boardingFee } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 100000"
      assert classAttended.boardingInd == "Y", "Boarding Ind: ${ classAttended.boardingInd } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Y"
      assert classAttended.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ classAttended.lastUpdateDate.clearTime() } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to ${ new Date().clearTime() }"
      
      log.info "Testing ClassAttended value"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey1", "Saint John's College MN", 2, 1902 )
      
      assert classAttended != null, "User was unable to save a ClassAttended"
      assert classAttended.studentId == "0202aoey1", "Student ID: ${ classAttended.studentId } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 0202aoey1"
      assert classAttended.schoolName == "Saint John's College MN", "School Name: ${ classAttended.schoolName } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Saint John's College MN"
      assert classAttended.enrollTermNo == 1, "Enrollment Term No: ${ classAttended.enrollTermNo } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1"
      assert classAttended.enrollTermYear == 1902, "Enrollment Term Year: ${ classAttended.enrollTermYear } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1902"
      assert classAttended.getProperty( "class" ) == "Freshman", "Class: ${ classAttended.getProperty( "class" ) } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Freshman"
      assert classAttended.classLevel == 1, "Class Level: ${ classAttended.classLevel } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1"
      assert classAttended.classTermNo == 2, "Class Term No: ${ classAttended.classTermNo } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 2"
      assert classAttended.classTermYear == 1902, "Class Term Year: ${ classAttended.classTermYear } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1902"
      assert classAttended.tuitionFee == 60000, "Tuition Fee: ${ classAttended.tuitionFee } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 60000"
      assert classAttended.boardingFee == 100000, "Boarding Fee: ${ classAttended.boardingFee } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 100000"
      assert classAttended.boardingInd == "N", "Boarding Ind: ${ classAttended.boardingInd } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to N"
      assert classAttended.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ classAttended.lastUpdateDate.clearTime() } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to ${ new Date().clearTime() }"
      
      /* Student Duplicate Student ID and Empty Non-Required Field Save Test. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "firstName", "Adrian" )
        .addQueryParam( "lastName", "Oey" )
        .addQueryParam( "enrollmentSchool", "Saint John's College MN" )
        .addQueryParam( "enrollmentTermSchool1", "1902 Term 1" )
        .addQueryParam( "enrollmentFirstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Freshman" )
        .addQueryParam( "classAttended1902 Term 2", "Freshman" )
        .addQueryParam( "boardingInd1902 Term 1", true ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey2" )
      studentDocumentFieldNames = studentDocument.getFieldNames()
      
      Date lastUpdateDateOfDuplicate2 = new Date( studentDocument.getOnlyField( "lastUpdateDate" ).getDate().getTime() )
      
      assert studentDocument != null, "User was unable to save a StudentDocument."
      assert studentDocument.getOnlyField( "studentId" ).getAtom() == "0202aoey2", "Student ID: ${ studentDocument.getOnlyField( "studentId" ).getAtom() } from StudentDocument.findByStudentId( def studentId ) is not equal to 0202aoey1."
      assert studentDocument.getOnlyField( "firstName" ).getText() == "Adrian", "First Name: ${ studentDocument.getOnlyField( "firstName" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Adrian."
      assert studentDocument.getOnlyField( "lastName" ).getText() == "Oey", "Last Name: ${ studentDocument.getOnlyField( "lastName" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Oey."
      assert !studentDocumentFieldNames.contains( "birthDate" ), "Birth Date: ${ studentDocument.getOnlyField( "birthDate" ).getDate().format( "MMM d yy" ) } from StudentDocument.findByStudentId( def studentId ) is not equal to null."
      assert !studentDocumentFieldNames.contains( "village" ), "Village: ${ studentDocument.getOnlyField( "village" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to null."
      assert !studentDocumentFieldNames.contains( "genderCode" ), "Gender Code: ${ studentDocument.getOnlyField( "genderCode" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to null."
      assert !studentDocumentFieldNames.contains( "specialInfo" ), "Special Info: ${ studentDocument.getOnlyField( "specialInfo" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to null."
      assert studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText() == "Saint John's College MN", "Last Enrollment School: ${ studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Saint John's College MN."
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveReasonCategory" ), "Last Enrollment Leave Reason Category: ${ studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to null."
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveReason" ), "Last Enrollment Leave Reason: ${ studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to null."
      assert studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 1" ), "Terms Enrolled: ${ studentDocument.getOnlyField( "termsEnrolled" ).getText() } from StudentDocument.findByStudentId( String studentId ) does not contain 1902 Term 1"
      assert studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 2" ), "Terms Enrolled: ${ studentDocument.getOnlyField( "termsEnrolled" ).getText() } from StudentDocument.findByStudentId( String studentId ) does not contain 1902 Term 2"
      assert studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber() == 1902, "Last Enrollment Term Year: ${ studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to 1902."
      assert studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber() == 1, "Last Enrollment Term No: ${ studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to 1."
      assert studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate() == Date.parse( "MMM d yy", "Feb 1 1902" ), "Last Enrollment Term Start Date: ${ studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate().format( "MMM d yy" ) } is not equal to Feb 1 1902"
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermYear" ), "Last Enrollment Leave Term Year: ${ studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to null."
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermNo" ), "Last Enrollment Leave Term No: ${ studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() } from StudentDocument.findByStudentId( def studentId ) is not equal to null."
      assert studentDocument.getOnlyField( "classesAttended" ).getText().contains( "Freshman" ), "Classes Attended: ${ studentDocument.getOnlyField( "classesAttended" ).getText() } from StudentDocument.findByStudentId( String studentId ) does not contain Freshman"
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() == "Freshman", "Last Enrollment First Class Attended: ${ studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Freshman."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() == "Freshman", "LastEnrollment Last Class Attended: ${ studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() } from StudentDocument.findByStudentId( def studentId ) is not equal to Freshman."
      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() == 120000, "Tuition Fees: ${ studentDocument.getOnlyField( "tuitionFees" ).getNumber() } from StudentDocument.findByStudentId( String studentId ) is not equal to 120000"
      assert studentDocument.getOnlyField( "boardingFees" ).getNumber() == 100000, "Boarding Fees: ${ studentDocument.getOnlyField( "boardingFees" ).getNumber() } from StudentDocument.findByStudentId( String studentId ) is not equal to 200000"
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "Other Fees: ${ studentDocument.getOnlyField( "otherFees" ).getNumber() } from StudentDocument.findByStudentId( String studentId ) is not equal to 0"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 220000, "Fees Due: ${ studentDocument.getOnlyField( "feesDue" ).getNumber() } from StudentDocument.findByStudentId( String studentId ) is not equal to 320000"
      assert studentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() == new Date().clearTime(), "Last Update Date: ${ studentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() } from StudentDocument.findByStudentId( def studentId ) is not equal to ${ new Date().clearTime() }."
      
      enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey2" )
      enrollmentDocumentFieldNames = enrollmentDocument.getFieldNames()
      
      assert enrollmentDocument != null, "User was unable to save an EnrollmentDocument."
      assert enrollmentDocument.getOnlyField( "studentId" ).getAtom() == "0202aoey2", "Student ID: ${ enrollmentDocument.getOnlyField( "studentId" ).getAtom() } from EnrollmentDocument.findLastEnrollmentWithDocumentByStudentId( String studentId ) is not equal to 0202aoey2."
      assert enrollmentDocument.getOnlyField( "firstName" ).getText() == "Adrian", "First Name: ${ enrollmentDocument.getOnlyField( "firstName" ).getText() } from EnrollmentDocument.findLastEnrollmentWithDocumentByStudentId( String studentId ) is not equal to Adrian."
      assert enrollmentDocument.getOnlyField( "lastName" ).getText() == "Oey", "Last Name: ${ enrollmentDocument.getOnlyField( "lastName" ).getText() } from EnrollmentDocument.findLastEnrollmentWithDocumentByStudentId( String studentId ) is not equal to Oey."
      assert !enrollmentDocumentFieldNames.contains( "birthDate" ), "Birth Date: ${ enrollmentDocument.getOnlyField( "birthDate" ).getDate().format( "MMM d yy" ) } from EnrollmentDocument.findLastEnrollmentWithDocumentByStudentId( String studentId ) is not equal to null."
      assert !enrollmentDocumentFieldNames.contains( "village" ), "Village: ${ enrollmentDocument.getOnlyField( "village" ).getText() } from EnrollmentDocument.findLastEnrollmentWithDocumentByStudentId( String studentId ) is not equal to null."
      assert !enrollmentDocumentFieldNames.contains( "genderCode" ), "Gender Code: ${ enrollmentDocument.getOnlyField( "genderCode" ).getText() } from EnrollmentDocument.findLastEnrollmentWithDocumentByStudentId( String studentId ) is not equal to null."
      assert !enrollmentDocumentFieldNames.contains( "specialInfo" ), "Special Info: ${ enrollmentDocument.getOnlyField( "specialInfo" ).getText() } from EnrollmentDocument.findLastEnrollmentWithDocumentByStudentId( String studentId ) is not equal to null."
      assert enrollmentDocument.getOnlyField( "schoolName" ).getText() == "Saint John's College MN", "School Name: ${ enrollmentDocument.getOnlyField( "schoolName" ).getText() } from EnrollmentDocument.findLastEnrollmentWithDocumentByStudentId( String studentId ) is not equal to Saint John's College MN."
      assert !enrollmentDocumentFieldNames.contains( "leaveReasonCategory" ), "Leave Reason Category: ${ enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() } from EnrollmentDocument.findLastEnrollmentWithDocumentByStudentId( String studentId ) is not equal to null."
      assert !enrollmentDocumentFieldNames.contains( "leaveReason" ), "Leave Reason: ${ enrollmentDocument.getOnlyField( "leaveReason" ).getText() } from EnrollmentDocument.findLastEnrollmentWithDocumentByStudentId( String studentId ) is not equal to null."
      assert enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 1" ), "Terms Enrolled: ${ enrollmentDocument.getOnlyField( "termsEnrolled" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) does not contain 1902 Term 1"
      assert enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 2" ), "Terms Enrolled: ${ enrollmentDocument.getOnlyField( "termsEnrolled" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) does not contain 1902 Term 2"
      assert enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() == 1902, "Enrollment Term Year: ${ enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() } from EnrollmentDocument.findLastEnrollmentWithDocumentByStudentId( String studentId ) is not equal to 1902."
      assert enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() == 1, "Enrollment Term No: ${ enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() } from EnrollmentDocument.findLastEnrollmentWithDocumentByStudentId( String studentId ) is not equal to 1."
      assert enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() == Date.parse( "MMM d yy", "Feb 1 1902" ), "Enrollment Term Start Date: ${ enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate().format( "MMM d yy" ) } is not equal to Feb 1 1902"
      assert !enrollmentDocumentFieldNames.contains( "leaveTermYear" ), "Leave Term Year: ${ enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() } from EnrollmentDocument.findLastEnrollmentWithDocumentByStudentId( String studentId ) is not equal to null."
      assert !enrollmentDocumentFieldNames.contains( "leaveTermNo" ), "Leave Term No: ${ enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() } from EnrollmentDocument.findLastEnrollmentWithDocumentByStudentId( String studentId ) is not equal to null."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() == Date.parse( "MMM d yyyy", "Dec 31 2999" ), "Leave Date: ${ enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to Dec 31 2999."
      assert enrollmentDocument.getOnlyField( "classesAttended" ).getText().contains( "Freshman" ), "Classes Attended: ${ enrollmentDocument.getOnlyField( "classesAttended" ).getText() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) does not contain Freshman"
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() == "Freshman", "First Class Attended: ${ enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() } from EnrollmentDocument.findLastEnrollmentWithDocumentByStudentId( String studentId ) is not equal to Freshman."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() == "Freshman", "Last Class Attended: ${ enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() } from EnrollmentDocument.findLastEnrollmentWithDocumentByStudentId( String studentId ) is not equal to Freshman."
      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() == 120000, "Tuition Fees: ${ enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 120000"
      assert enrollmentDocument.getOnlyField( "boardingFees" ).getNumber() == 100000, "Boarding Fees: ${ enrollmentDocument.getOnlyField( "boardingFees" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 200000"
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 0, "Other Fees: ${ enrollmentDocument.getOnlyField( "otherFees" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 0"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 220000, "Fees Due: ${ enrollmentDocument.getOnlyField( "feesDue" ).getNumber() } from EnrollmentDocument.findLastEnrollmentDocumentByStudentId( String studentId ) is not equal to 320000"
      assert enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() == new Date().clearTime(), "Last Update Date: ${ enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().clearTime() } from EnrollmentDocument.findLastEnrollmentWithDocumentByStudentId( String studentId ) is not equal to ${ new Date().clearTime() }."
    
      student = Student.findByStudentId( "0202aoey2" )
      
      assert student != null, "User was unable to save a Student."
      assert student.studentId == "0202aoey2", "Student ID: ${ student.studentId } from Student.findByStudentId( def studentId ) is not equal to 0202aoey2."
      assert student.firstName == "Adrian", "First Name: ${ student.firstName } from Student.findByStudentId( def studentId ) is not equal to Adrian."
      assert student.lastName == "Oey", "Last Name: ${ student.lastName } from Student.findByStudentId( def studentId ) is not equal to Oey."
      assert student.birthDate == null, "Birth Date: ${ student.birthDate?.format( "MMM d yy" ) } from Student.findByStudentId( def studentId ) is not equal to null."
      assert student.village == null, "Village: ${ student.village } from Student.findByStudentId( def studentId ) is not equal to null."
      assert student.genderCode == null, "Gender Code: ${ student.genderCode } from Student.findByStudentId( def studentId ) is not equal to null."
      assert student.specialInfo == null, "Special Info: ${ student.specialInfo } from Student.findByStudentId( def studentId ) is not equal to null."
      assert student.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ student.lastUpdateDate.clearTime() } from Student.findByStudentId( def studentId ) is not equal to ${ new Date().clearTime() }."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count + 3, "Student Meta Data Count: ${ Student.findMetaDataBySchoolName( "Saint John's College MN" ).count } from Student.findByStudentId( String studentId ) is not equal to ${ studentMetaData.count + 3 }"
      
      AnonymousParentalRelationship.findByStudentId( "0202aoey2" ).each() {
        assert it.deceasedInd == "N", "${ it.parentalRelationship } Deceased Ind: ${ it.deceasedInd } from AnonymousParentalRelationship.findByStudentId( def studentId ) is not equal to N."
      }
      
      enrollment = Enrollment.findLastEnrollmentByStudentId( "0202aoey2" )
      
      assert enrollment != null, "User was unable to save an Enrollment."
      assert enrollment.studentId == "0202aoey2", "Student ID: ${ enrollment.studentId } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to 0202aoey2."
      assert enrollment.schoolName == "Saint John's College MN", "School Name: ${ enrollment.schoolName } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to Saint John's College MN."
      assert enrollment.leaveReasonCategory == null, "Leave Reason Category: ${ enrollment.leaveReasonCategory } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to null."
      assert enrollment.leaveReason == null, "Leave Reason: ${ enrollment.leaveReason } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to null."
      assert enrollment.enrollTermYear == 1902, "Enrollment Term Year: ${ enrollment.enrollTermYear } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to 1902."
      assert enrollment.enrollTermNo == 1, "Enrollment Term No: ${ enrollment.enrollTermNo } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to 1."
      assert enrollment.leaveTermYear == null, "Leave Term Year: ${ enrollment.leaveTermYear } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to null."
      assert enrollment.leaveTermNo == null, "Leave Term No: ${ enrollment.leaveTermNo } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to null."
      assert enrollment.firstClassAttended == "Freshman", "First Class Attended: ${ enrollment.firstClassAttended } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to Freshman."
      assert enrollment.lastClassAttended == "Freshman", "Last Class Attended: ${ enrollment.lastClassAttended } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to Freshman."
      assert enrollment.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ enrollment.lastUpdateDate.clearTime() } from Enrollment.findLastEnrollmentByStudentId( def studentId ) is not equal to ${ new Date().clearTime() }."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count + 3, "Enrollment Meta Data Count: ${ Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count } from Enrollment.findMetaDataBySchoolName( String schoolName ) is not equal to ${ enrollmentMetaData.count + 1 }"
      
      log.info "Testing ClassAttended value"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey2", "Saint John's College MN", 1, 1902 )
      
      assert classAttended != null, "User was unable to save a ClassAttended"
      assert classAttended.studentId == "0202aoey2", "Student ID: ${ classAttended.studentId } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 0202aoey2"
      assert classAttended.schoolName == "Saint John's College MN", "School Name: ${ classAttended.schoolName } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Saint John's College MN"
      assert classAttended.enrollTermNo == 1, "Enrollment Term No: ${ classAttended.enrollTermNo } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1"
      assert classAttended.enrollTermYear == 1902, "Enrollment Term Year: ${ classAttended.enrollTermYear } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1902"
      assert classAttended.getProperty( "class" ) == "Freshman", "Class: ${ classAttended.getProperty( "class" ) } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Freshman"
      assert classAttended.classLevel == 1, "Class Level: ${ classAttended.classLevel } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1"
      assert classAttended.classTermNo == 1, "Class Term No: ${ classAttended.classTermNo } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1"
      assert classAttended.classTermYear == 1902, "Class Term Year: ${ classAttended.classTermYear } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1902"
      assert classAttended.tuitionFee == 60000, "Tuition Fee: ${ classAttended.tuitionFee } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 60000"
      assert classAttended.boardingFee == 100000, "Boarding Fee: ${ classAttended.boardingFee } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 100000"
      assert classAttended.boardingInd == "Y", "Boarding Ind: ${ classAttended.boardingInd } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Y"
      assert classAttended.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ classAttended.lastUpdateDate.clearTime() } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to ${ new Date().clearTime() }"
      
      log.info "Testing ClassAttended value"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey2", "Saint John's College MN", 2, 1902 )
      
      assert classAttended != null, "User was unable to save a ClassAttended"
      assert classAttended.studentId == "0202aoey2", "Student ID: ${ classAttended.studentId } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 0202aoey2"
      assert classAttended.schoolName == "Saint John's College MN", "School Name: ${ classAttended.schoolName } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Saint John's College MN"
      assert classAttended.enrollTermNo == 1, "Enrollment Term No: ${ classAttended.enrollTermNo } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1"
      assert classAttended.enrollTermYear == 1902, "Enrollment Term Year: ${ classAttended.enrollTermYear } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1902"
      assert classAttended.getProperty( "class" ) == "Freshman", "Class: ${ classAttended.getProperty( "class" ) } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to Freshman"
      assert classAttended.classLevel == 1, "Class Level: ${ classAttended.classLevel } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1"
      assert classAttended.classTermNo == 2, "Class Term No: ${ classAttended.classTermNo } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 2"
      assert classAttended.classTermYear == 1902, "Class Term Year: ${ classAttended.classTermYear } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 1902"
      assert classAttended.tuitionFee == 60000, "Tuition Fee: ${ classAttended.tuitionFee } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 60000"
      assert classAttended.boardingFee == 100000, "Boarding Fee: ${ classAttended.boardingFee } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to 100000"
      assert classAttended.boardingInd == "N", "Boarding Ind: ${ classAttended.boardingInd } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to N"
      assert classAttended.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ classAttended.lastUpdateDate.clearTime() } from ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) is not equal to ${ new Date().clearTime() }"
    %>
    <p>Student Save Test is successful.</p>
  </body>
</html>