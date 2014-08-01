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
    <title>Student Edit Test</title>
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
      
      /* Save a Parent for a Student's Parent Deceased Ind Edit Test */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "firstName", "Darth" )
        .addQueryParam( "lastName", "Vader" )
        .addQueryParam( "deceasedInd", true )
        .addQueryParam( "newParentRelationship0", "Darth Vader" )
        .addQueryParam( "newChildId0", "0202aoey" ).toURL() )
        
      /* Save a Parent for a Student's Parent Deceased Ind Edit Test */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "firstName", "R2" )
        .addQueryParam( "lastName", "D2" )
        .addQueryParam( "newGuardianRelationship0", "Robot" )
        .addQueryParam( "newRelativeId0", "0202aoey" ).toURL() )
      
      /* Save a Fee for an Edit Test */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "name", "Admission" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "2" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      /* Save a Payment for an Edit Test */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "fundingSource", "Self" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "2" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      Date lastUpdateDate = studentDocument.getOnlyField( "lastUpdateDate" ).getDate()
      enrollmentMetaData = Enrollment.findMetaDataBySchoolName( "Saint John's College MN" )
      studentMetaData = Student.findMetaDataBySchoolName( "Saint John's College MN" )
      
      /* Student Edit Test with a missing action parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "firstName", "Grace" )
        .addQueryParam( "lastName", "Wijasa" )
        .addQueryParam( "birthDate", "Oct 10 1900" )
        .addQueryParam( "village", "Bugonzi" )
        .addQueryParam( "genderCode", "Y" )
        .addQueryParam( "specialInfo", "Luke's sister" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "enrollmentLeaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "enrollmentFirstClassAttended", "Sophomore" )
        .addQueryParam( "enrollmentLastClassAttended", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 2", "Sophomore" )
        .addQueryParam( "classAttended1903 Term 1", "Sophomore" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth MaulDeceasedInd", true )
        .addQueryParam( "rd2DeceasedInd", true )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      log.info "StudentDocument's lastUpdateDate is ${ studentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) }"
      
      assert studentDocument.getOnlyField( "firstName" ).getText() != "Grace", "User was able to edit a Student using a missing action parameter."
      assert studentDocument.getOnlyField( "lastName" ).getText() != "Wijasa", "User was able to edit a Student using a missing action parameter."
      assert studentDocument.getOnlyField( "birthDate" ).getDate() != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using a missing action parameter."
      assert studentDocument.getOnlyField( "village" ).getText() != "Bugonzi", "User was able to edit a Student using a missing action parameter."
      assert studentDocument.getOnlyField( "genderCode" ).getText() != "Y", "User was able to edit a Student using a missing action parameter."
      assert studentDocument.getOnlyField( "specialInfo" ).getText() != "Luke's sister", "User was able to edit a Student using a missing action parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit a Student using a missing action parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() != "Won a jackpot", "User was able to edit a Student using a missing action parameter."
      assert !studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was able to edit a Student using a missing action parameter"
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() != 1903, "User was able to edit a Student using a missing action parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() != 1, "User was able to edit a Student using a missing action parameter."
      assert !studentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was able to edit a Student using a missing action parameter"
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using a missing action parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using a missing action parameter."
      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 180000, "User was able to edit a Student using a missing action parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 280000, "User was able to edit a Student using a missing action parameter"
      
      enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" )
      
      assert enrollmentDocument.getOnlyField( "firstName" ).getText() != "Grace", "User was able to edit a Student using a missing action parameter."
      assert enrollmentDocument.getOnlyField( "lastName" ).getText() != "Wijasa", "User was able to edit a Student using a missing action parameter."
      assert enrollmentDocument.getOnlyField( "birthDate" ).getDate() != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using a missing action parameter."
      assert enrollmentDocument.getOnlyField( "village" ).getText() != "Bugonzi", "User was able to edit a Student using a missing action parameter."
      assert enrollmentDocument.getOnlyField( "genderCode" ).getText() != "Y", "User was able to edit a Student using a missing action parameter."
      assert enrollmentDocument.getOnlyField( "specialInfo" ).getText() != "Luke's sister", "User was able to edit a Student using a missing action parameter."
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit a Student using a missing action parameter."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() != "Won a jackpot", "User was able to edit a Student using a missing action parameter."
      assert !enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was able to edit a Student using a missing action parameter"
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() != 1903, "User was able to edit a Student using a missing action parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() != 1, "User was able to edit a Student using a missing action parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yyyy", "Apr 30 1903" ), "User was able to edit a Student using a missing action parameter."
      assert !enrollmentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was able to edit a Student using a missing action parameter"
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using a missing action parameter."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using a missing action parameter."
      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 180000, "User was able to edit a Student using a missing action parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 280000, "User was able to edit a Student using a missing action parameter"
      
      student = Student.findByStudentId( "0202aoey")
      
      assert student.firstName != "Grace", "User was able to edit a Student using a missing action parameter."
      assert student.lastName != "Wijasa", "User was able to edit a Student using a missing action parameter."
      assert student.birthDate != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using a missing action parameter."
      assert student.village != "Bugonzi", "User was able to edit a Student using a missing action parameter."
      assert student.genderCode != "Y", "User was able to edit a Student using a missing action parameter."
      assert student.specialInfo != "Luke's sister", "User was able to edit a Student using a missing action parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change StudentMetaData count using a missing action parameter"
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Maul" )
      
      assert anonymousParentalRelationship.deceasedInd != "Y", "User was able to edit a Student using a missing action parameter."
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Vader" )
      
      assert anonymousParentalRelationship.deceasedInd != "N", "User was able to edit a Student using a missing action parameter."
      
      parent = Parent.findByParentId( "dvader" )
      
      assert parent.deceasedInd != "N", "User was able to edit a Student using a missing action parameter."
      
      parent = Parent.findByParentId( "rd2" )
      
      assert parent.deceasedInd != "Y", "User was able to edit a Student using a missing action parameter."
      
      enrollment = Enrollment.findLastEnrollmentByStudentId( "0202aoey" )
      
      assert enrollment.leaveReasonCategory != "Becoming a Billionaire", "User was able to edit a Student using a missing action parameter."
      assert enrollment.leaveReason != "Won a jackpot", "User was able to edit a Student using a missing action parameter."
      assert enrollment.leaveTermYear != 1903, "User was able to edit a Student using a missing action parameter."
      assert enrollment.leaveTermNo != 1, "User was able to edit a Student using a missing action parameter."
      assert enrollment.firstClassAttended != "Sophomore", "User was able to edit a Student using a missing action parameter."
      assert enrollment.lastClassAttended != "Sophomore", "User was able to edit a Student using a missing action parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using a missing action parameter"
      
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 2, "User was able to edit a Student using a missing action parameter"
      
      /* Student Edit Test with an invalid action parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "modify" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "firstName", "Grace" )
        .addQueryParam( "lastName", "Wijasa" )
        .addQueryParam( "birthDate", "Oct 10 1900" )
        .addQueryParam( "village", "Bugonzi" )
        .addQueryParam( "genderCode", "Y" )
        .addQueryParam( "specialInfo", "Luke's sister" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "enrollmentLeaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "enrollmentFirstClassAttended", "Sophomore" )
        .addQueryParam( "enrollmentLastClassAttended", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 2", "Sophomore" )
        .addQueryParam( "classAttended1903 Term 1", "Sophomore" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth MaulDeceasedInd", true )
        .addQueryParam( "rd2DeceasedInd", true )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      log.info "StudentDocument's lastUpdateDate is ${ studentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) }"
      
      assert studentDocument.getOnlyField( "firstName" ).getText() != "Grace", "User was able to edit a Student using an invalid action parameter."
      assert studentDocument.getOnlyField( "lastName" ).getText() != "Wijasa", "User was able to edit a Student using an invalid action parameter."
      assert studentDocument.getOnlyField( "birthDate" ).getDate() != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using an invalid action parameter."
      assert studentDocument.getOnlyField( "village" ).getText() != "Bugonzi", "User was able to edit a Student using an invalid action parameter."
      assert studentDocument.getOnlyField( "genderCode" ).getText() != "Y", "User was able to edit a Student using an invalid action parameter."
      assert studentDocument.getOnlyField( "specialInfo" ).getText() != "Luke's sister", "User was able to edit a Student using an invalid action parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit a Student using an invalid action parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() != "Won a jackpot", "User was able to edit a Student using an invalid action parameter."
      assert !studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was able to edit a Student using an invalid action parameter"
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() != 1903, "User was able to edit a Student using an invalid action parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() != 1, "User was able to edit a Student using an invalid action parameter."
      assert !studentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was able to edit a Student using an invalid action parameter"
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using an invalid action parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using an invalid action parameter."
      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 180000, "User was able to edit a Student using an invalid action parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 280000, "User was able to edit a Student using an invalid action parameter"
      
      enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" )
      
      assert enrollmentDocument.getOnlyField( "firstName" ).getText() != "Grace", "User was able to edit a Student using an invalid action parameter."
      assert enrollmentDocument.getOnlyField( "lastName" ).getText() != "Wijasa", "User was able to edit a Student using an invalid action parameter."
      assert enrollmentDocument.getOnlyField( "birthDate" ).getDate() != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using an invalid action parameter."
      assert enrollmentDocument.getOnlyField( "village" ).getText() != "Bugonzi", "User was able to edit a Student using an invalid action parameter."
      assert enrollmentDocument.getOnlyField( "genderCode" ).getText() != "Y", "User was able to edit a Student using an invalid action parameter."
      assert enrollmentDocument.getOnlyField( "specialInfo" ).getText() != "Luke's sister", "User was able to edit a Student using an invalid action parameter."
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit a Student using an invalid action parameter."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() != "Won a jackpot", "User was able to edit a Student using an invalid action parameter."
      assert !enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was able to edit a Student using an invalid action parameter"
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() != 1903, "User was able to edit a Student using an invalid action parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() != 1, "User was able to edit a Student using an invalid action parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yyyy", "Apr 30 1903" ), "User was able to edit a Student using an invalid action parameter."
      assert !enrollmentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was able to edit a Student using an invalid action parameter"
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using an invalid action parameter."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using an invalid action parameter."
      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 180000, "User was able to edit a Student using an invalid action parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 280000, "User was able to edit a Student using an invalid action parameter"
      
      student = Student.findByStudentId( "0202aoey")
      
      assert student.firstName != "Grace", "User was able to edit a Student using an invalid action parameter."
      assert student.lastName != "Wijasa", "User was able to edit a Student using an invalid action parameter."
      assert student.birthDate != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using an invalid action parameter."
      assert student.village != "Bugonzi", "User was able to edit a Student using an invalid action parameter."
      assert student.genderCode != "Y", "User was able to edit a Student using an invalid action parameter."
      assert student.specialInfo != "Luke's sister", "User was able to edit a Student using an invalid action parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change StudentMetaData count using an invalid action parameter"
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Maul" )
      
      assert anonymousParentalRelationship.deceasedInd != "Y", "User was able to edit a Student using an invalid action parameter."
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Vader" )
      
      assert anonymousParentalRelationship.deceasedInd != "N", "User was able to edit a Student using an invalid action parameter."
      
      parent = Parent.findByParentId( "dvader" )
      
      assert parent.deceasedInd != "N", "User was able to edit a Student using an invalid action parameter."
      
      parent = Parent.findByParentId( "rd2" )
      
      assert parent.deceasedInd != "Y", "User was able to edit a Student using an invalid action parameter."
      
      enrollment = Enrollment.findLastEnrollmentByStudentId( "0202aoey" )
      
      assert enrollment.leaveReasonCategory != "Becoming a Billionaire", "User was able to edit a Student using an invalid action parameter."
      assert enrollment.leaveReason != "Won a jackpot", "User was able to edit a Student using an invalid action parameter."
      assert enrollment.leaveTermYear != 1903, "User was able to edit a Student using an invalid action parameter."
      assert enrollment.leaveTermNo != 1, "User was able to edit a Student using an invalid action parameter."
      assert enrollment.firstClassAttended != "Sophomore", "User was able to edit a Student using an invalid action parameter."
      assert enrollment.lastClassAttended != "Sophomore", "User was able to edit a Student using an invalid action parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using an invalid action parameter"
      
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 2, "User was able to edit a Student using an invalid action parameter"
      
      /* Student Edit Test with a missing studentId parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "firstName", "Grace" )
        .addQueryParam( "lastName", "Wijasa" )
        .addQueryParam( "birthDate", "Oct 10 1900" )
        .addQueryParam( "village", "Bugonzi" )
        .addQueryParam( "genderCode", "Y" )
        .addQueryParam( "specialInfo", "Luke's sister" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "enrollmentLeaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "enrollmentFirstClassAttended", "Sophomore" )
        .addQueryParam( "enrollmentLastClassAttended", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 2", "Sophomore" )
        .addQueryParam( "classAttended1903 Term 1", "Sophomore" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth MaulDeceasedInd", true )
        .addQueryParam( "rd2DeceasedInd", true )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      log.info "StudentDocument's lastUpdateDate is ${ studentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) }"
      
      assert studentDocument.getOnlyField( "firstName" ).getText() != "Grace", "User was able to edit a Student using no studentId parameter."
      assert studentDocument.getOnlyField( "lastName" ).getText() != "Wijasa", "User was able to edit a Student using no studentId parameter."
      assert studentDocument.getOnlyField( "birthDate" ).getDate() != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using no studentId parameter."
      assert studentDocument.getOnlyField( "village" ).getText() != "Bugonzi", "User was able to edit a Student using no studentId parameter."
      assert studentDocument.getOnlyField( "genderCode" ).getText() != "Y", "User was able to edit a Student using no studentId parameter."
      assert studentDocument.getOnlyField( "specialInfo" ).getText() != "Luke's sister", "User was able to edit a Student using no studentId parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit a Student using no studentId parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() != "Won a jackpot", "User was able to edit a Student using no studentId parameter."
      assert !studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was able to edit a Student using no studentId parameter"
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() != 1903, "User was able to edit a Student using no studentId parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() != 1, "User was able to edit a Student using no studentId parameter."
      assert !studentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was able to edit a Student using no studentId parameter"
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using no studentId parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using no studentId parameter."
      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 180000, "User was able to edit a Student using no studentId parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 280000, "User was able to edit a Student using no studentId parameter"
      
      enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" )
      
      assert enrollmentDocument.getOnlyField( "firstName" ).getText() != "Grace", "User was able to edit a Student using no studentId parameter."
      assert enrollmentDocument.getOnlyField( "lastName" ).getText() != "Wijasa", "User was able to edit a Student using no studentId parameter."
      assert enrollmentDocument.getOnlyField( "birthDate" ).getDate() != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using no studentId parameter."
      assert enrollmentDocument.getOnlyField( "village" ).getText() != "Bugonzi", "User was able to edit a Student using no studentId parameter."
      assert enrollmentDocument.getOnlyField( "genderCode" ).getText() != "Y", "User was able to edit a Student using no studentId parameter."
      assert enrollmentDocument.getOnlyField( "specialInfo" ).getText() != "Luke's sister", "User was able to edit a Student using no studentId parameter."
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit a Student using no studentId parameter."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() != "Won a jackpot", "User was able to edit a Student using no studentId parameter."
      assert !enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was able to edit a Student using no studentId parameter"
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() != 1903, "User was able to edit a Student using no studentId parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() != 1, "User was able to edit a Student using no studentId parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yyyy", "Apr 30 1903" ), "User was able to edit a Student using no studentId parameter."
      assert !enrollmentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was able to edit a Student using no studentId parameter"
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using no studentId parameter."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using no studentId parameter."
      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 180000, "User was able to edit a Student using no studentId parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 280000, "User was able to edit a Student using no studentId parameter"
      
      student = Student.findByStudentId( "0202aoey")
      
      assert student.firstName != "Grace", "User was able to edit a Student using no studentId parameter."
      assert student.lastName != "Wijasa", "User was able to edit a Student using no studentId parameter."
      assert student.birthDate != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using no studentId parameter."
      assert student.village != "Bugonzi", "User was able to edit a Student using no studentId parameter."
      assert student.genderCode != "Y", "User was able to edit a Student using no studentId parameter."
      assert student.specialInfo != "Luke's sister", "User was able to edit a Student using no studentId parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change StudentMetaData count using no studentId parameter"
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Maul" )
      
      assert anonymousParentalRelationship.deceasedInd != "Y", "User was able to edit a Student using no studentId parameter."
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Vader" )
      
      assert anonymousParentalRelationship.deceasedInd != "N", "User was able to edit a Student using no studentId parameter."
      
      parent = Parent.findByParentId( "dvader" )
      
      assert parent.deceasedInd != "N", "User was able to edit a Student using no studentId parameter."
      
      parent = Parent.findByParentId( "rd2" )
      
      assert parent.deceasedInd != "Y", "User was able to edit a Student using no studentId parameter."
      
      enrollment = Enrollment.findLastEnrollmentByStudentId( "0202aoey" )
      
      assert enrollment.leaveReasonCategory != "Becoming a Billionaire", "User was able to edit a Student using no studentId parameter."
      assert enrollment.leaveReason != "Won a jackpot", "User was able to edit a Student using no studentId parameter."
      assert enrollment.leaveTermYear != 1903, "User was able to edit a Student using no studentId parameter."
      assert enrollment.leaveTermNo != 1, "User was able to edit a Student using no studentId parameter."
      assert enrollment.firstClassAttended != "Sophomore", "User was able to edit a Student using no studentId parameter."
      assert enrollment.lastClassAttended != "Sophomore", "User was able to edit a Student using no studentId parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using no studentId parameter"
      
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 2, "User was able to edit a Student using no studentId parameter"
      
      /* Student Edit Test with a missing firstName parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "lastName", "Wijasa" )
        .addQueryParam( "birthDate", "Oct 10 1900" )
        .addQueryParam( "village", "Bugonzi" )
        .addQueryParam( "genderCode", "Y" )
        .addQueryParam( "specialInfo", "Luke's sister" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "enrollmentLeaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "enrollmentFirstClassAttended", "Sophomore" )
        .addQueryParam( "enrollmentLastClassAttended", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 2", "Sophomore" )
        .addQueryParam( "classAttended1903 Term 1", "Sophomore" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth MaulDeceasedInd", true )
        .addQueryParam( "rd2DeceasedInd", true )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      log.info "StudentDocument's lastUpdateDate is ${ studentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) }"
      
      assert studentDocument.getOnlyField( "lastName" ).getText() != "Wijasa", "User was able to edit a Student using no firstName parameter."
      assert studentDocument.getOnlyField( "birthDate" ).getDate() != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using no firstName parameter."
      assert studentDocument.getOnlyField( "village" ).getText() != "Bugonzi", "User was able to edit a Student using no firstName parameter."
      assert studentDocument.getOnlyField( "genderCode" ).getText() != "Y", "User was able to edit a Student using no firstName parameter."
      assert studentDocument.getOnlyField( "specialInfo" ).getText() != "Luke's sister", "User was able to edit a Student using no firstName parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit a Student using no firstName parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() != "Won a jackpot", "User was able to edit a Student using no firstName parameter."
      assert !studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was able to edit a Student using no firstName parameter"
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() != 1903, "User was able to edit a Student using no firstName parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() != 1, "User was able to edit a Student using no firstName parameter."
      assert !studentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was able to edit a Student using no firstName parameter"
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using no firstName parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using no firstName parameter."
      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 180000, "User was able to edit a Student using no firstName parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 280000, "User was able to edit a Student using no firstName parameter"
      
      enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" )
      
      assert enrollmentDocument.getOnlyField( "lastName" ).getText() != "Wijasa", "User was able to edit a Student using no firstName parameter."
      assert enrollmentDocument.getOnlyField( "birthDate" ).getDate() != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using no firstName parameter."
      assert enrollmentDocument.getOnlyField( "village" ).getText() != "Bugonzi", "User was able to edit a Student using no firstName parameter."
      assert enrollmentDocument.getOnlyField( "genderCode" ).getText() != "Y", "User was able to edit a Student using no firstName parameter."
      assert enrollmentDocument.getOnlyField( "specialInfo" ).getText() != "Luke's sister", "User was able to edit a Student using no firstName parameter."
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit a Student using no firstName parameter."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() != "Won a jackpot", "User was able to edit a Student using no firstName parameter."
      assert !enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was able to edit a Student using no firstName parameter"
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() != 1903, "User was able to edit a Student using no firstName parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() != 1, "User was able to edit a Student using no firstName parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yyyy", "Apr 30 1903" ), "User was able to edit a Student using no firstName parameter."
      assert !enrollmentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was able to edit a Student using no firstName parameter"
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using no firstName parameter."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using no firstName parameter."
      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 180000, "User was able to edit a Student using no firstName parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 280000, "User was able to edit a Student using no firstName parameter"
      
      student = Student.findByStudentId( "0202aoey")
      
      assert student.lastName != "Wijasa", "User was able to edit a Student using no firstName parameter."
      assert student.birthDate != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using no firstName parameter."
      assert student.village != "Bugonzi", "User was able to edit a Student using no firstName parameter."
      assert student.genderCode != "Y", "User was able to edit a Student using no firstName parameter."
      assert student.specialInfo != "Luke's sister", "User was able to edit a Student using no firstName parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change StudentMetaData count using no firstName parameter"
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Maul" )
      
      assert anonymousParentalRelationship.deceasedInd != "Y", "User was able to edit a Student using no firstName parameter."
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Vader" )
      
      assert anonymousParentalRelationship.deceasedInd != "N", "User was able to edit a Student using no firstName parameter."
      
      parent = Parent.findByParentId( "dvader" )
      
      assert parent.deceasedInd != "N", "User was able to edit a Student using no firstName parameter."
      
      parent = Parent.findByParentId( "rd2" )
      
      assert parent.deceasedInd != "Y", "User was able to edit a Student using no firstName parameter."
      
      enrollment = Enrollment.findLastEnrollmentByStudentId( "0202aoey" )
      
      assert enrollment.leaveReasonCategory != "Becoming a Billionaire", "User was able to edit a Student using no firstName parameter."
      assert enrollment.leaveReason != "Won a jackpot", "User was able to edit a Student using no firstName parameter."
      assert enrollment.leaveTermYear != 1903, "User was able to edit a Student using no firstName parameter."
      assert enrollment.leaveTermNo != 1, "User was able to edit a Student using no firstName parameter."
      assert enrollment.firstClassAttended != "Sophomore", "User was able to edit a Student using no firstName parameter."
      assert enrollment.lastClassAttended != "Sophomore", "User was able to edit a Student using no firstName parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using no firstName parameter"
      
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 2, "User was able to edit a Student using no firstName parameter"
      
      /* Student Edit Test with an invalid date Range. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "firstName", "Grace" )
        .addQueryParam( "lastName", "Wijasa" )
        .addQueryParam( "birthDate", "Oct 10 1900" )
        .addQueryParam( "village", "Bugonzi" )
        .addQueryParam( "genderCode", "Y" )
        .addQueryParam( "specialInfo", "Luke's sister" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "enrollmentLeaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1901 Term 3" )
        .addQueryParam( "enrollmentFirstClassAttended", "Sophomore" )
        .addQueryParam( "enrollmentLastClassAttended", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 2", "Sophomore" )
        .addQueryParam( "classAttended1903 Term 1", "Sophomore" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth MaulDeceasedInd", true )
        .addQueryParam( "rd2DeceasedInd", true )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      log.info "StudentDocument's lastUpdateDate is ${ studentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) }"
      
      assert studentDocument.getOnlyField( "firstName" ).getText() != "Grace", "User was able to edit a Student using an invalid date range."
      assert studentDocument.getOnlyField( "lastName" ).getText() != "Wijasa", "User was able to edit a Student using an invalid date range."
      assert studentDocument.getOnlyField( "birthDate" ).getDate() != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using an invalid date range."
      assert studentDocument.getOnlyField( "village" ).getText() != "Bugonzi", "User was able to edit a Student using an invalid date range."
      assert studentDocument.getOnlyField( "genderCode" ).getText() != "Y", "User was able to edit a Student using an invalid date range."
      assert studentDocument.getOnlyField( "specialInfo" ).getText() != "Luke's sister", "User was able to edit a Student using an invalid date range."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit a Student using an invalid date range."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() != "Won a jackpot", "User was able to edit a Student using an invalid date range."
      assert !studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was able to edit a Student using an invalid date range"
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() != 1901, "User was able to edit a Student using an invalid date range."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() != 3, "User was able to edit a Student using an invalid date range."
      assert !studentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was able to edit a Student using an invalid date range"
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using an invalid date range."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using an invalid date range."
      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 180000, "User was able to edit a Student using an invalid date range"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 280000, "User was able to edit a Student using an invalid date range"
      
      enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" )
      
      assert enrollmentDocument.getOnlyField( "firstName" ).getText() != "Grace", "User was able to edit a Student using an invalid date range."
      assert enrollmentDocument.getOnlyField( "lastName" ).getText() != "Wijasa", "User was able to edit a Student using an invalid date range."
      assert enrollmentDocument.getOnlyField( "birthDate" ).getDate() != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using an invalid date range."
      assert enrollmentDocument.getOnlyField( "village" ).getText() != "Bugonzi", "User was able to edit a Student using an invalid date range."
      assert enrollmentDocument.getOnlyField( "genderCode" ).getText() != "Y", "User was able to edit a Student using an invalid date range."
      assert enrollmentDocument.getOnlyField( "specialInfo" ).getText() != "Luke's sister", "User was able to edit a Student using an invalid date range."
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit a Student using an invalid date range."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() != "Won a jackpot", "User was able to edit a Student using an invalid date range."
      assert !enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was able to edit a Student using an invalid date range"
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() != 1901, "User was able to edit a Student using an invalid date range."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() != 3, "User was able to edit a Student using an invalid date range."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yyyy", "Oct 30 1901" ), "User was able to edit a Student using an invalid date range."
      assert !enrollmentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was able to edit a Student using an invalid date range"
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using an invalid date range."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using an invalid date range."
      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 180000, "User was able to edit a Student using an invalid date range"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 280000, "User was able to edit a Student using an invalid date range"
      
      student = Student.findByStudentId( "0202aoey")
      
      assert student.firstName != "Grace", "User was able to edit a Student using an invalid date range."
      assert student.lastName != "Wijasa", "User was able to edit a Student using an invalid date range."
      assert student.birthDate != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using an invalid date range."
      assert student.village != "Bugonzi", "User was able to edit a Student using an invalid date range."
      assert student.genderCode != "Y", "User was able to edit a Student using an invalid date range."
      assert student.specialInfo != "Luke's sister", "User was able to edit a Student using an invalid date range."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change StudentMetaData count using an invalid date range"
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Maul" )
      
      assert anonymousParentalRelationship.deceasedInd != "Y", "User was able to edit a Student using an invalid date range."
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Vader" )
      
      assert anonymousParentalRelationship.deceasedInd != "N", "User was able to edit a Student using an invalid date range."
      
      parent = Parent.findByParentId( "dvader" )
      
      assert parent.deceasedInd != "N", "User was able to edit a Student using an invalid date range."
      
      parent = Parent.findByParentId( "rd2" )
      
      assert parent.deceasedInd != "Y", "User was able to edit a Student using an invalid date range."
      
      enrollment = Enrollment.findLastEnrollmentByStudentId( "0202aoey" )
      
      assert enrollment.leaveReasonCategory != "Becoming a Billionaire", "User was able to edit a Student using an invalid date range."
      assert enrollment.leaveReason != "Won a jackpot", "User was able to edit a Student using an invalid date range."
      assert enrollment.leaveTermYear != 1901, "User was able to edit a Student using an invalid date range."
      assert enrollment.leaveTermNo != 3, "User was able to edit a Student using an invalid date range."
      assert enrollment.firstClassAttended != "Sophomore", "User was able to edit a Student using an invalid date range."
      assert enrollment.lastClassAttended != "Sophomore", "User was able to edit a Student using an invalid date range."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using an invalid date range"
      
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 2, "User was able to edit a Student using an invalid date range"
      
      /* Student Edit Test with a missing enrollmentFirstClassAttended parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "firstName", "Grace" )
        .addQueryParam( "lastName", "Wijasa" )
        .addQueryParam( "birthDate", "Oct 10 1900" )
        .addQueryParam( "village", "Bugonzi" )
        .addQueryParam( "genderCode", "Y" )
        .addQueryParam( "specialInfo", "Luke's sister" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "enrollmentLeaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "enrollmentLastClassAttended", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 2", "Sophomore" )
        .addQueryParam( "classAttended1903 Term 1", "Sophomore" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth MaulDeceasedInd", true )
        .addQueryParam( "rd2DeceasedInd", true )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      log.info "StudentDocument's lastUpdateDate is ${ studentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) }"
      
      assert studentDocument.getOnlyField( "firstName" ).getText() != "Grace", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert studentDocument.getOnlyField( "lastName" ).getText() != "Wijasa", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert studentDocument.getOnlyField( "birthDate" ).getDate() != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert studentDocument.getOnlyField( "village" ).getText() != "Bugonzi", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert studentDocument.getOnlyField( "genderCode" ).getText() != "Y", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert studentDocument.getOnlyField( "specialInfo" ).getText() != "Luke's sister", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() != "Won a jackpot", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert !studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was able to edit a Student using no enrollmentFirstClassAttended parameter"
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() != 1903, "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() != 1, "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert !studentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was able to edit a Student using no enrollmentFirstClassAttended parameter"
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 180000, "User was able to edit a Student using no enrollmentFirstClassAttended parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 280000, "User was able to edit a Student using no enrollmentFirstClassAttended parameter"
      
      enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" )
      
      assert enrollmentDocument.getOnlyField( "firstName" ).getText() != "Grace", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "lastName" ).getText() != "Wijasa", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "birthDate" ).getDate() != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "village" ).getText() != "Bugonzi", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "genderCode" ).getText() != "Y", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "specialInfo" ).getText() != "Luke's sister", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() != "Won a jackpot", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert !enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was able to edit a Student using no enrollmentFirstClassAttended parameter"
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() != 1903, "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() != 1, "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
	  assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yyyy", "Apr 30 1903" ), "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert !enrollmentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was able to edit a Student using no enrollmentFirstClassAttended parameter"
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 180000, "User was able to edit a Student using no enrollmentFirstClassAttended parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 280000, "User was able to edit a Student using no enrollmentFirstClassAttended parameter"
      
      student = Student.findByStudentId( "0202aoey")
      
      assert student.firstName != "Grace", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert student.lastName != "Wijasa", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert student.birthDate != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert student.village != "Bugonzi", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert student.genderCode != "Y", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert student.specialInfo != "Luke's sister", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change StudentMetaData count using no enrollmentFirstClassAttended parameter"
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Maul" )
      
      assert anonymousParentalRelationship.deceasedInd != "Y", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Vader" )
      
      assert anonymousParentalRelationship.deceasedInd != "N", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      
      parent = Parent.findByParentId( "dvader" )
      
      assert parent.deceasedInd != "N", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      
      parent = Parent.findByParentId( "rd2" )
      
      assert parent.deceasedInd != "Y", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      
      enrollment = Enrollment.findLastEnrollmentByStudentId( "0202aoey" )
      
      assert enrollment.leaveReasonCategory != "Becoming a Billionaire", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert enrollment.leaveReason != "Won a jackpot", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert enrollment.leaveTermYear != 1903, "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert enrollment.leaveTermNo != 1, "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert enrollment.lastClassAttended != "Sophomore", "User was able to edit a Student using no enrollmentFirstClassAttended parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using no enrollmentFirstClassAttended parameter"
      
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 2, "User was able to edit a Student using no enrollmentFirstClassAttended parameter"
      
      /* Student Edit Test with a missing enrollmentLastClassAttended parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "firstName", "Grace" )
        .addQueryParam( "lastName", "Wijasa" )
        .addQueryParam( "birthDate", "Oct 10 1900" )
        .addQueryParam( "village", "Bugonzi" )
        .addQueryParam( "genderCode", "Y" )
        .addQueryParam( "specialInfo", "Luke's sister" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "enrollmentLeaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "enrollmentFirstClassAttended", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 2", "Sophomore" )
        .addQueryParam( "classAttended1903 Term 1", "Sophomore" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth MaulDeceasedInd", true )
        .addQueryParam( "rd2DeceasedInd", true )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      log.info "StudentDocument's lastUpdateDate is ${ studentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) }"
      
      assert studentDocument.getOnlyField( "firstName" ).getText() != "Grace", "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert studentDocument.getOnlyField( "lastName" ).getText() != "Wijasa", "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert studentDocument.getOnlyField( "birthDate" ).getDate() != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert studentDocument.getOnlyField( "village" ).getText() != "Bugonzi", "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert studentDocument.getOnlyField( "genderCode" ).getText() != "Y", "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert studentDocument.getOnlyField( "specialInfo" ).getText() != "Luke's sister", "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() != "Won a jackpot", "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert !studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was able to edit a Student using no enrollmentLastClassAttended parameter"
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() != 1903, "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() != 1, "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert !studentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was able to edit a Student using no enrollmentLastClassAttended parameter"
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 180000, "User was able to edit a Student using no enrollmentLastClassAttended parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 280000, "User was able to edit a Student using no enrollmentLastClassAttended parameter"
      
      enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" )
      
      assert enrollmentDocument.getOnlyField( "firstName" ).getText() != "Grace", "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "lastName" ).getText() != "Wijasa", "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "birthDate" ).getDate() != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "village" ).getText() != "Bugonzi", "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "genderCode" ).getText() != "Y", "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "specialInfo" ).getText() != "Luke's sister", "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() != "Won a jackpot", "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert !enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was able to edit a Student using no enrollmentLastClassAttended parameter"
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() != 1903, "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() != 1, "User was able to edit a Student using no enrollmentLastClassAttended parameter."
	  assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yyyy", "Apr 30 1903" ), "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert !enrollmentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was able to edit a Student using no enrollmentLastClassAttended parameter"
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 180000, "User was able to edit a Student using no enrollmentLastClassAttended parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 280000, "User was able to edit a Student using no enrollmentLastClassAttended parameter"
      
      student = Student.findByStudentId( "0202aoey")
      
      assert student.firstName != "Grace", "User was able to edit a Student using no enrollmentLastClassAttendedSchool1 parameter."
      assert student.lastName != "Wijasa", "User was able to edit a Student using no enrollmentLastClassAttendedSchool1 parameter."
      assert student.birthDate != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using no enrollmentLastClassAttendedSchool1 parameter."
      assert student.village != "Bugonzi", "User was able to edit a Student using no enrollmentLastClassAttendedSchool1 parameter."
      assert student.genderCode != "Y", "User was able to edit a Student using no enrollmentLastClassAttendedSchool1 parameter."
      assert student.specialInfo != "Luke's sister", "User was able to edit a Student using no enrollmentLastClassAttendedSchool1 parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change StudentMetaData count using no enrollmentLastClassAttendedSchool1 parameter"
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Maul" )
      
      assert anonymousParentalRelationship.deceasedInd != "Y", "User was able to edit a Student using no enrollmentLastClassAttendedSchool1 parameter."
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Vader" )
      
      assert anonymousParentalRelationship.deceasedInd != "N", "User was able to edit a Student using no enrollmentLastClassAttendedSchool1 parameter."
      
      parent = Parent.findByParentId( "dvader" )
      
      assert parent.deceasedInd != "N", "User was able to edit a Student using no enrollmentLastClassAttendedSchool1 parameter."
      
      parent = Parent.findByParentId( "rd2" )
      
      assert parent.deceasedInd != "Y", "User was able to edit a Student using no enrollmentLastClassAttendedSchool1 parameter."
      
      enrollment = Enrollment.findLastEnrollmentByStudentId( "0202aoey" )
      
      assert enrollment.leaveReasonCategory != "Becoming a Billionaire", "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert enrollment.leaveReason != "Won a jackpot", "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert enrollment.leaveTermYear != 1903, "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert enrollment.leaveTermNo != 1, "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert enrollment.firstClassAttended != "Sophomore", "User was able to edit a Student using no enrollmentLastClassAttended parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using no enrollmentLastClassAttendedSchool1 parameter"
      
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 2, "User was able to edit a Student using no enrollmentLastClassAttended parameter"
      
      /* Student Edit Test with a missing lastUpdateDate parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "firstName", "Grace" )
        .addQueryParam( "lastName", "Wijasa" )
        .addQueryParam( "birthDate", "Oct 10 1900" )
        .addQueryParam( "village", "Bugonzi" )
        .addQueryParam( "genderCode", "Y" )
        .addQueryParam( "specialInfo", "Luke's sister" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "enrollmentLeaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "enrollmentFirstClassAttended", "Sophomore" )
        .addQueryParam( "enrollmentLastClassAttended", "Freshman" )
        .addQueryParam( "classAttended1902 Term 1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 2", "Sophomore" )
        .addQueryParam( "classAttended1903 Term 1", "Sophomore" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth MaulDeceasedInd", true )
        .addQueryParam( "rd2DeceasedInd", true ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      log.info "StudentDocument's lastUpdateDate is ${ studentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) }"
      
      assert studentDocument.getOnlyField( "firstName" ).getText() != "Grace", "User was able to edit a Student using no lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "genderCode" ).getText() != "Y", "User was able to edit a Student using no lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "specialInfo" ).getText() != "Luke's sister", "User was able to edit a Student using no lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit a Student using no lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() != "Won a jackpot", "User was able to edit a Student using no lastUpdateDate parameter."
      assert !studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was able to edit a Student using no lastUpdateDate parameter"
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() != 1903, "User was able to edit a Student using no lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() != 1, "User was able to edit a Student using no lastUpdateDate parameter."
      assert !studentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was able to edit a Student using no lastUpdateDate parameter"
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using no lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using no lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 180000, "User was able to edit a Student using no lastUpdateDate parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 280000, "User was able to edit a Student using no lastUpdateDate parameter"
      
      enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" )
      
      assert enrollmentDocument.getOnlyField( "firstName" ).getText() != "Grace", "User was able to edit a Student using no lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "lastName" ).getText() != "Wijasa", "User was able to edit a Student using no lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "birthDate" ).getDate() != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using no lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "village" ).getText() != "Bugonzi", "User was able to edit a Student using no lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "genderCode" ).getText() != "Y", "User was able to edit a Student using no lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "specialInfo" ).getText() != "Luke's sister", "User was able to edit a Student using no lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit a Student using no lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() != "Won a jackpot", "User was able to edit a Student using no lastUpdateDate parameter."
      assert !enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was able to edit a Student using no lastUpdateDate parameter"
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() != 1903, "User was able to edit a Student using no lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() != 1, "User was able to edit a Student using no lastUpdateDate parameter."
	  assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yyyy", "Apr 30 1903" ), "User was able to edit a Student using no lastUpdateDate parameter."
      assert !enrollmentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was able to edit a Student using no lastUpdateDate parameter"
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using no lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using no lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 180000, "User was able to edit a Student using no lastUpdateDate parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 280000, "User was able to edit a Student using no lastUpdateDate parameter"
      
      student = Student.findByStudentId( "0202aoey")
      
      assert student.firstName != "Grace", "User was able to edit a Student using no lastUpdateDate parameter."
      assert student.lastName != "Wijasa", "User was able to edit a Student using no lastUpdateDate parameter."
      assert student.birthDate != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using no lastUpdateDate parameter."
      assert student.village != "Bugonzi", "User was able to edit a Student using no lastUpdateDate parameter."
      assert student.genderCode != "Y", "User was able to edit a Student using no lastUpdateDate parameter."
      assert student.specialInfo != "Luke's sister", "User was able to edit a Student using no lastUpdateDate parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change StudentMetaData count using no lastUpdateDate parameter"
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Maul" )
      
      assert anonymousParentalRelationship.deceasedInd != "Y", "User was able to edit a Student using no lastUpdateDate parameter."
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Vader" )
      
      assert anonymousParentalRelationship.deceasedInd != "N", "User was able to edit a Student using no lastUpdateDate parameter."
      
      parent = Parent.findByParentId( "dvader" )
      
      assert parent.deceasedInd != "N", "User was able to edit a Student using no lastUpdateDate parameter."
      
      parent = Parent.findByParentId( "rd2" )
      
      assert parent.deceasedInd != "Y", "User was able to edit a Student using no lastUpdateDate parameter."
      
      enrollment = Enrollment.findLastEnrollmentByStudentId( "0202aoey" )
      
      assert enrollment.leaveReasonCategory != "Becoming a Billionaire", "User was able to edit a Student using no lastUpdateDate parameter."
      assert enrollment.leaveReason != "Won a jackpot", "User was able to edit a Student using no lastUpdateDate parameter."
      assert enrollment.leaveTermYear != 1903, "User was able to edit a Student using no lastUpdateDate parameter."
      assert enrollment.leaveTermNo != 1, "User was able to edit a Student using no lastUpdateDate parameter."
      assert enrollment.firstClassAttended != "Sophomore", "User was able to edit a Student using no lastUpdateDate parameter."
      assert enrollment.lastClassAttended != "Sophomore", "User was able to edit a Student using no lastUpdateDate parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using no lastUpdateDate parameter"
      
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 2, "User was able to edit a Student using no lastUpdateDate parameter"
      
      /* Student Edit Test with an invalid lastUpdateDate parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "firstName", "Grace" )
        .addQueryParam( "lastName", "Wijasa" )
        .addQueryParam( "birthDate", "Oct 10 1900" )
        .addQueryParam( "village", "Bugonzi" )
        .addQueryParam( "genderCode", "Y" )
        .addQueryParam( "specialInfo", "Luke's sister" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "enrollmentLeaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "enrollmentFirstClassAttended", "Sophomore" )
        .addQueryParam( "enrollmentLastClassAttended", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 2", "Sophomore" )
        .addQueryParam( "classAttended1903 Term 1", "Sophomore" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth MaulDeceasedInd", true )
        .addQueryParam( "rd2DeceasedInd", true )
        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      log.info "StudentDocument's lastUpdateDate is ${ studentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) }"
      
      assert studentDocument.getOnlyField( "firstName" ).getText() != "Grace", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "lastName" ).getText() != "Wijasa", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "birthDate" ).getDate() != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "village" ).getText() != "Bugonzi", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "genderCode" ).getText() != "Y", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "specialInfo" ).getText() != "Luke's sister", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() != "Won a jackpot", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert !studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was able to edit a Student using an invalid lastUpdateDate parameter"
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() != 1903, "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() != 1, "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert !studentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was able to edit a Student using an invalid lastUpdateDate parameter"
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 180000, "User was able to edit a Student using an invalid lastUpdateDate parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 280000, "User was able to edit a Student using an invalid lastUpdateDate parameter"
      
      enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" )
      
      assert enrollmentDocument.getOnlyField( "firstName" ).getText() != "Grace", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "lastName" ).getText() != "Wijasa", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "birthDate" ).getDate() != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "village" ).getText() != "Bugonzi", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "genderCode" ).getText() != "Y", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "specialInfo" ).getText() != "Luke's sister", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() != "Becoming a Billionaire", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() != "Won a jackpot", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert !enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was able to edit a Student using an invalid lastUpdateDate parameter"
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() != 1903, "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() != 1, "User was able to edit a Student using an invalid lastUpdateDate parameter."
	  assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yyyy", "Apr 30 1903" ), "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert !enrollmentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was able to edit a Student using an invalid lastUpdateDate parameter"
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() != "Sophomore", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 180000, "User was able to edit a Student using an invalid lastUpdateDate parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 280000, "User was able to edit a Student using an invalid lastUpdateDate parameter"
      
      student = Student.findByStudentId( "0202aoey")
      
      assert student.firstName != "Grace", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert student.lastName != "Wijasa", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert student.birthDate != Date.parse( "MMM d yy", "Oct 10 1900" ), "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert student.village != "Bugonzi", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert student.genderCode != "Y", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert student.specialInfo != "Luke's sister", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change StudentMetaData count using an invalid lastUpdateDate parameter"
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Maul" )
      
      assert anonymousParentalRelationship.deceasedInd != "Y", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Vader" )
      
      assert anonymousParentalRelationship.deceasedInd != "N", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      
      parent = Parent.findByParentId( "dvader" )
      
      assert parent.deceasedInd != "N", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      
      parent = Parent.findByParentId( "rd2" )
      
      assert parent.deceasedInd != "Y", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      
      enrollment = Enrollment.findLastEnrollmentByStudentId( "0202aoey" )
      
      assert enrollment.leaveReasonCategory != "Becoming a Billionaire", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert enrollment.leaveReason != "Won a jackpot", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert enrollment.leaveTermYear != 1903, "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert enrollment.leaveTermNo != 1, "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert enrollment.firstClassAttended != "Sophomore", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert enrollment.lastClassAttended != "Sophomore", "User was able to edit a Student using an invalid lastUpdateDate parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count using an invalid lastUpdateDate parameter"
      
      assert ClassAttended.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 2, "User was able to edit a Student using an invalid lastUpdateDate parameter"
      
	  /* Enter an additional Enrollment to test if a Student edit will also edit Student data in enrollments that are not the last */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
      	.addQueryParam( "action", "save" )
      	.addQueryParam( "studentId", "0202aoey" )
      	.addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "leaveReasonCategory", "Becoming a Millionaire" )
        .addQueryParam( "leaveReason", "Won a lottery" )
        .addQueryParam( "enrollTermSchool1", "1901 Term 2" )
        .addQueryParam( "leaveTermSchool1", "1901 Term 2" )
        .addQueryParam( "firstClassAttendedSchool1", "Freshman" )
        .addQueryParam( "lastClassAttendedSchool1", "Freshman" )
        .addQueryParam( "classAttended1901 Term 2", "Freshman" )
        .addQueryParam( "boardingInd1901 Term 2", true ).toURL() )
        
      enrollmentMetaData = Enrollment.findMetaDataBySchoolName( "Saint John's College MN" )
      
      /* Student Edit Test. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "firstName", "Grace" )
        .addQueryParam( "lastName", "Wijasa" )
        .addQueryParam( "birthDate", "Oct 10 1900" )
        .addQueryParam( "village", "Bugonzi" )
        .addQueryParam( "genderCode", "Y" )
        .addQueryParam( "specialInfo", "Luke's sister" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "enrollmentLeaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "enrollmentFirstClassAttended", "Sophomore" )
        .addQueryParam( "enrollmentLastClassAttended", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 2", "Sophomore" )
        .addQueryParam( "classAttended1903 Term 1", "Sophomore" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth MaulDeceasedInd", true )
        .addQueryParam( "rd2DeceasedInd", true )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      log.info "StudentDocument's lastUpdateDate is ${ studentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) }"
      
      assert studentDocument.getOnlyField( "firstName" ).getText() == "Grace", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "lastName" ).getText() == "Wijasa", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "birthDate" ).getDate() == Date.parse( "MMM d yy", "Oct 10 1900" ), "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "village" ).getText() == "Bugonzi", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "genderCode" ).getText() == "Y", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "specialInfo" ).getText() == "Luke's sister", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText() == "Saint John's College MN", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() == "Becoming a Billionaire", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() == "Won a jackpot", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 1" ), "User was unable to edit a Student"
      assert studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 2" ), "User was unable to edit a Student"
      assert studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was unable to edit a Student"
      assert studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber() == 1902, "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber() == 1, "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() == 1903, "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() == 1, "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was unable to edit a Student"
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() == "Sophomore", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() == "Sophomore", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() == 180000, "User was unable to edit a Student"
      assert studentDocument.getOnlyField( "boardingFees" ).getNumber() == 100000, "User was unable to edit a Student"
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 12345.67, "User was unable to edit a Student"
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 12345.67, "User was unable to edit a Student"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 280000, "User was unable to edit a Student"
      
      lastUpdateDate = studentDocument.getOnlyField( "lastUpdateDate" ).getDate()
      
      enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" )
      
      assert enrollmentDocument.getOnlyField( "firstName" ).getText() == "Grace", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "lastName" ).getText() == "Wijasa", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "birthDate" ).getDate() == Date.parse( "MMM d yy", "Oct 10 1900" ), "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "village" ).getText() == "Bugonzi", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "genderCode" ).getText() == "Y", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "specialInfo" ).getText() == "Luke's sister", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "schoolName" ).getText() == "Saint John's College MN", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() == "Becoming a Billionaire", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() == "Won a jackpot", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 1" ), "User was unable to edit a Student"
      assert enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 2" ), "User was unable to edit a Student"
      assert enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was unable to edit a Student"
      assert enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() == 1902, "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() == 1, "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() == 1903, "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() == 1, "User was unable to edit a Student."
	  assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() == Date.parse( "MMM d yyyy", "Apr 30 1903" ), "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was unable to edit a Student"
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() == "Sophomore", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() == "Sophomore", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() == 180000, "User was unable to edit a Student"
      assert enrollmentDocument.getOnlyField( "boardingFees" ).getNumber() == 100000, "User was unable to edit a Student"
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 12345.67, "User was unable to edit a Student"
      assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 12345.67, "User was unable to edit a Student"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 280000, "User was unable to edit a Student"
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndEnrollTermNoAndEnrollTermYear( "0202aoey", 2, 1901 )
      
      assert enrollmentDocument.getOnlyField( "firstName" ).getText() == "Grace", "User was unable to edit Student info in an Enrollment that is not the last"
      assert enrollmentDocument.getOnlyField( "lastName" ).getText() == "Wijasa", "User was unable to edit Student info in an Enrollment that is not the last"
      assert enrollmentDocument.getOnlyField( "birthDate" ).getDate() == Date.parse( "MMM d yy", "Oct 10 1900" ), "User was unable to edit Student info in an Enrollment that is not the last"
      assert enrollmentDocument.getOnlyField( "village" ).getText() == "Bugonzi", "User was unable to edit Student info in an Enrollment that is not the last"
      assert enrollmentDocument.getOnlyField( "genderCode" ).getText() == "Y", "User was unable to edit Student info in an Enrollment that is not the last"
      assert enrollmentDocument.getOnlyField( "specialInfo" ).getText() == "Luke's sister", "User was unable to edit Student info in an Enrollment that is not the last"
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() == "Becoming a Millionaire", "User was able to edit an Enrollment that is not the last"
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() == "Won a lottery", "User was able to edit an Enrollment that is not the last"
      assert enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1901 Term 2" ), "User was able to edit an Enrollment that is not the last"
      assert enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() == 1901, "User was able to edit an Enrollment that is not the last"
      assert enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() == 2, "User was able to edit an Enrollment that is not the last"
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() == 1901, "User was able to edit an Enrollment that is not the last"
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() == 2, "User was able to edit an Enrollment that is not the last"
	  assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() == Date.parse( "MMM d yyyy", "Jul 31 1901" ), "User was able to edit an Enrollment that is not the last"
      assert enrollmentDocument.getOnlyField( "classesAttended" ).getText().contains( "Freshman" ), "User was able to edit an Enrollment that is not the last"
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() == "Freshman", "User was able to edit an Enrollment that is not the last"
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() == "Freshman", "User was able to edit an Enrollment that is not the last"
      
      student = Student.findByStudentId( "0202aoey")
      
      assert student.firstName == "Grace", "User was unable to edit a Student."
      assert student.lastName == "Wijasa", "User was unable to edit a Student."
      assert student.birthDate == Date.parse( "MMM d yy", "Oct 10 1900" ), "User was unable to edit a Student."
      assert student.village == "Bugonzi", "User was unable to edit a Student."
      assert student.genderCode == "Y", "User was unable to edit a Student."
      assert student.specialInfo == "Luke's sister", "User was unable to edit a Student."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change StudentMetaData count"
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Maul" )
      
      assert anonymousParentalRelationship.deceasedInd == "Y", "User was unable to edit a Student."
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Vader" )
      
      assert anonymousParentalRelationship.deceasedInd == "N", "User was unable to edit a Student."
      
      parent = Parent.findByParentId( "dvader" )
      
      assert parent.deceasedInd == "N", "User was unable to edit a Student."
      
      parent = Parent.findByParentId( "rd2" )
      
      assert parent.deceasedInd == "Y", "User was unable to edit a Student."
      
      enrollment = Enrollment.findLastEnrollmentByStudentId( "0202aoey" )
      
      assert enrollment.schoolName == "Saint John's College MN", "User was unable to edit a Student."
      assert enrollment.leaveReasonCategory == "Becoming a Billionaire", "User was unable to edit a Student."
      assert enrollment.leaveReason == "Won a jackpot", "User was unable to edit a Student."
      assert enrollment.enrollTermYear == 1902, "User was unable to edit a Student."
      assert enrollment.enrollTermNo == 1, "User was unable to edit a Student."
      assert enrollment.leaveTermYear == 1903, "User was unable to edit a Student."
      assert enrollment.leaveTermNo == 1, "User was unable to edit a Student."
      assert enrollment.firstClassAttended == "Sophomore", "User was unable to edit a Student."
      assert enrollment.lastClassAttended == "Sophomore", "User was unable to edit a Student."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert classAttended != null, "User was unable to edit a Student"
      assert classAttended.studentId == "0202aoey", "User was unable to edit a Student"
      assert classAttended.schoolName == "Saint John's College MN", "User was unable to edit a Student"
      assert classAttended.enrollTermNo == 1, "User was unable to edit a Student"
      assert classAttended.enrollTermYear == 1902, "User was unable to edit a Student"
      assert classAttended.getProperty( "class" ) == "Sophomore", "User was unable to edit a Student"
      assert classAttended.classLevel == 2, "User was unable to edit a Student"
      assert classAttended.classTermNo == 1, "User was unable to edit a Student"
      assert classAttended.classTermYear == 1902, "User was unable to edit a Student"
      assert classAttended.tuitionFee == 60000, "User was unable to edit a Student"
      assert classAttended.boardingFee == 100000, "User was unable to edit a Student"
      assert classAttended.boardingInd == "Y", "User was unable to edit a Student"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 2, 1902 )
      
      assert classAttended != null, "User was unable to edit a Student"
      assert classAttended.studentId == "0202aoey", "User was unable to edit a Student"
      assert classAttended.schoolName == "Saint John's College MN", "User was unable to edit a Student"
      assert classAttended.enrollTermNo == 1, "User was unable to edit a Student"
      assert classAttended.enrollTermYear == 1902, "User was unable to edit a Student"
      assert classAttended.getProperty( "class" ) == "Sophomore", "User was unable to edit a Student"
      assert classAttended.classLevel == 2, "User was unable to edit a Student"
      assert classAttended.classTermNo == 2, "User was unable to edit a Student"
      assert classAttended.classTermYear == 1902, "User was unable to edit a Student"
      assert classAttended.tuitionFee == 60000, "User was unable to edit a Student"
      assert classAttended.boardingFee == 100000, "User was unable to edit a Student"
      assert classAttended.boardingInd == "N", "User was unable to edit a Student"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 1, 1903 )
      
      assert classAttended != null, "User was unable to edit a Student"
      assert classAttended.studentId == "0202aoey", "User was unable to edit a Student"
      assert classAttended.schoolName == "Saint John's College MN", "User was unable to edit a Student"
      assert classAttended.enrollTermNo == 1, "User was unable to edit a Student"
      assert classAttended.enrollTermYear == 1902, "User was unable to edit a Student"
      assert classAttended.getProperty( "class" ) == "Sophomore", "User was unable to edit a Student"
      assert classAttended.classLevel == 2, "User was unable to edit a Student"
      assert classAttended.classTermNo == 1, "User was unable to edit a Student"
      assert classAttended.classTermYear == 1903, "User was unable to edit a Student"
      assert classAttended.tuitionFee == 60000, "User was unable to edit a Student"
      assert classAttended.boardingFee == 100000, "User was unable to edit a Student"
      assert classAttended.boardingInd == "N", "User was unable to edit a Student"
      
	  /* Delete the Enrollment record created just to test if StudentController can edit an Enrollment that is not last */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
        
      enrollmentMetaData = Enrollment.findMetaDataBySchoolName( "Saint John's College MN" )
      
      /* Student Edit Test, setting leaveTerm to now in order to test Fee and Payment */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "firstName", "Grace" )
        .addQueryParam( "lastName", "Wijasa" )
        .addQueryParam( "birthDate", "Oct 10 1900" )
        .addQueryParam( "village", "Bugonzi" )
        .addQueryParam( "genderCode", "Y" )
        .addQueryParam( "specialInfo", "Luke's sister" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "enrollmentLeaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "Leave Term" )
        .addQueryParam( "enrollmentFirstClassAttended", "Sophomore" )
        .addQueryParam( "enrollmentLastClassAttended", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 2", "Sophomore" )
        .addQueryParam( "classAttended1903 Term 1", "Sophomore" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth MaulDeceasedInd", true )
        .addQueryParam( "rd2DeceasedInd", true )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      Set<String> studentDocumentFieldNames = studentDocument.getFieldNames()
      
      assert studentDocument.getOnlyField( "firstName" ).getText() == "Grace", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "lastName" ).getText() == "Wijasa", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "birthDate" ).getDate() == Date.parse( "MMM d yy", "Oct 10 1900" ), "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "village" ).getText() == "Bugonzi", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "genderCode" ).getText() == "Y", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "specialInfo" ).getText() == "Luke's sister", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText() == "Saint John's College MN", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText() == "Becoming a Billionaire", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText() == "Won a jackpot", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 1" ), "User was unable to edit a Student"
      assert studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 2" ), "User was unable to edit a Student"
      assert studentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was unable to edit a Student"
      assert studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber() == 1902, "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber() == 1, "User was unable to edit a Student."
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermYear" ), "User was unable to edit a Student."
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermNo" ), "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was unable to edit a Student"
      assert studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() == "Sophomore", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() == "Sophomore", "User was unable to edit a Student."
      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() == 180000, "User was unable to edit a Student"
      assert studentDocument.getOnlyField( "boardingFees" ).getNumber() == 100000, "User was unable to edit a Student"
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 12345.67, "User was unable to edit a Student"
      assert studentDocument.getOnlyField( "payments" ).getNumber() == 12345.67, "User was unable to edit a Student"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 280000, "User was unable to edit a Student"
      
      lastUpdateDate = studentDocument.getOnlyField( "lastUpdateDate" ).getDate()
      
      enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" )
      Set<String> enrollmentDocumentFieldNames = enrollmentDocument.getFieldNames()
      
      assert enrollmentDocument.getOnlyField( "firstName" ).getText() == "Grace", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "lastName" ).getText() == "Wijasa", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "birthDate" ).getDate() == Date.parse( "MMM d yy", "Oct 10 1900" ), "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "village" ).getText() == "Bugonzi", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "genderCode" ).getText() == "Y", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "specialInfo" ).getText() == "Luke's sister", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "schoolName" ).getText() == "Saint John's College MN", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText() == "Becoming a Billionaire", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "leaveReason" ).getText() == "Won a jackpot", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 1" ), "User was unable to edit a Student"
      assert enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1902 Term 2" ), "User was unable to edit a Student"
      assert enrollmentDocument.getOnlyField( "termsEnrolled" ).getText().contains( "1903 Term 1" ), "User was unable to edit a Student"
      assert enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() == 1902, "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() == 1, "User was unable to edit a Student."
      assert !enrollmentDocumentFieldNames.contains( "leaveTermYear" ), "User was unable to edit a Student."
      assert !enrollmentDocumentFieldNames.contains( "leaveTermNo" ), "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() == Date.parse( "MMM d yyyy", "Dec 31 2999" ), "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "classesAttended" ).getText().contains( "Sophomore" ), "User was unable to edit a Student"
      assert enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() == "Sophomore", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() == "Sophomore", "User was unable to edit a Student."
      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() == 180000, "User was unable to edit a Student"
      assert enrollmentDocument.getOnlyField( "boardingFees" ).getNumber() == 100000, "User was unable to edit a Student"
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 12345.67, "User was unable to edit a Student"
      assert enrollmentDocument.getOnlyField( "payments" ).getNumber() == 12345.67, "User was unable to edit a Student"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 280000, "User was unable to edit a Student"
      
      student = Student.findByStudentId( "0202aoey")
      
      assert student.firstName == "Grace", "User was unable to edit a Student."
      assert student.lastName == "Wijasa", "User was unable to edit a Student."
      assert student.birthDate == Date.parse( "MMM d yy", "Oct 10 1900" ), "User was unable to edit a Student."
      assert student.village == "Bugonzi", "User was unable to edit a Student."
      assert student.genderCode == "Y", "User was unable to edit a Student."
      assert student.specialInfo == "Luke's sister", "User was unable to edit a Student."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change StudentMetaData count"
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Maul" )
      
      assert anonymousParentalRelationship.deceasedInd == "Y", "User was unable to edit a Student."
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Vader" )
      
      assert anonymousParentalRelationship.deceasedInd == "N", "User was unable to edit a Student."
      
      parent = Parent.findByParentId( "dvader" )
      
      assert parent.deceasedInd == "N", "User was unable to edit a Student."
      
      parent = Parent.findByParentId( "rd2" )
      
      assert parent.deceasedInd == "Y", "User was unable to edit a Student."
      
      enrollment = Enrollment.findLastEnrollmentByStudentId( "0202aoey" )
      
      assert enrollment.schoolName == "Saint John's College MN", "User was unable to edit a Student."
      assert enrollment.leaveReasonCategory == "Becoming a Billionaire", "User was unable to edit a Student."
      assert enrollment.leaveReason == "Won a jackpot", "User was unable to edit a Student."
      assert enrollment.enrollTermYear == 1902, "User was unable to edit a Student."
      assert enrollment.enrollTermNo == 1, "User was unable to edit a Student."
      assert enrollment.leaveTermYear == null, "User was unable to edit a Student."
      assert enrollment.leaveTermNo == null, "User was unable to edit a Student."
      assert enrollment.firstClassAttended == "Sophomore", "User was unable to edit a Student."
      assert enrollment.lastClassAttended == "Sophomore", "User was unable to edit a Student."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change EnrollmentMetaData count"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert classAttended != null, "User was unable to edit a Student"
      assert classAttended.studentId == "0202aoey", "User was unable to edit a Student"
      assert classAttended.schoolName == "Saint John's College MN", "User was unable to edit a Student"
      assert classAttended.enrollTermNo == 1, "User was unable to edit a Student"
      assert classAttended.enrollTermYear == 1902, "User was unable to edit a Student"
      assert classAttended.getProperty( "class" ) == "Sophomore", "User was unable to edit a Student"
      assert classAttended.classLevel == 2, "User was unable to edit a Student"
      assert classAttended.classTermNo == 1, "User was unable to edit a Student"
      assert classAttended.classTermYear == 1902, "User was unable to edit a Student"
      assert classAttended.tuitionFee == 60000, "User was unable to edit a Student"
      assert classAttended.boardingFee == 100000, "User was unable to edit a Student"
      assert classAttended.boardingInd == "Y", "User was unable to edit a Student"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 2, 1902 )
      
      assert classAttended != null, "User was unable to edit a Student"
      assert classAttended.studentId == "0202aoey", "User was unable to edit a Student"
      assert classAttended.schoolName == "Saint John's College MN", "User was unable to edit a Student"
      assert classAttended.enrollTermNo == 1, "User was unable to edit a Student"
      assert classAttended.enrollTermYear == 1902, "User was unable to edit a Student"
      assert classAttended.getProperty( "class" ) == "Sophomore", "User was unable to edit a Student"
      assert classAttended.classLevel == 2, "User was unable to edit a Student"
      assert classAttended.classTermNo == 2, "User was unable to edit a Student"
      assert classAttended.classTermYear == 1902, "User was unable to edit a Student"
      assert classAttended.tuitionFee == 60000, "User was unable to edit a Student"
      assert classAttended.boardingFee == 100000, "User was unable to edit a Student"
      assert classAttended.boardingInd == "N", "User was unable to edit a Student"
      
      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0202aoey", "Saint John's College MN", 1, 1903 )
      
      assert classAttended != null, "User was unable to edit a Student"
      assert classAttended.studentId == "0202aoey", "User was unable to edit a Student"
      assert classAttended.schoolName == "Saint John's College MN", "User was unable to edit a Student"
      assert classAttended.enrollTermNo == 1, "User was unable to edit a Student"
      assert classAttended.enrollTermYear == 1902, "User was unable to edit a Student"
      assert classAttended.getProperty( "class" ) == "Sophomore", "User was unable to edit a Student"
      assert classAttended.classLevel == 2, "User was unable to edit a Student"
      assert classAttended.classTermNo == 1, "User was unable to edit a Student"
      assert classAttended.classTermYear == 1903, "User was unable to edit a Student"
      assert classAttended.tuitionFee == 60000, "User was unable to edit a Student"
      assert classAttended.boardingFee == 100000, "User was unable to edit a Student"
      assert classAttended.boardingInd == "N", "User was unable to edit a Student"
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "fundingSource", "Self" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1903" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
        
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
	  lastUpdateDate = studentDocument.getOnlyField( "lastUpdateDate" ).getDate()
    	        
      /* Student Edit Test, setting leaveTerm to 1902 Term 2 in order to test Payment */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "firstName", "Grace" )
        .addQueryParam( "lastName", "Wijasa" )
        .addQueryParam( "birthDate", "Oct 10 1900" )
        .addQueryParam( "village", "Bugonzi" )
        .addQueryParam( "genderCode", "Y" )
        .addQueryParam( "specialInfo", "Luke's sister" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "enrollmentLeaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1902 Term 2" )
        .addQueryParam( "enrollmentFirstClassAttended", "Sophomore" )
        .addQueryParam( "enrollmentLastClassAttended", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 2", "Sophomore" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth MaulDeceasedInd", true )
        .addQueryParam( "rd2DeceasedInd", true )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      studentDocumentFieldNames = studentDocument.getFieldNames()
      
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermYear" ), "User was able to edit a Student and leave an orphan Payment"
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermNo" ), "User was able to edit a Student and leave an orphan Payment"
      
      lastUpdateDate = studentDocument.getOnlyField( "lastUpdateDate" ).getDate()
      
      enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" )
      enrollmentDocumentFieldNames = enrollmentDocument.getFieldNames()
      
      assert !enrollmentDocumentFieldNames.contains( "leaveTermYear" ), "User was able to edit a Student and leave an orphan Payment"
      assert !enrollmentDocumentFieldNames.contains( "leaveTermNo" ), "User was able to edit a Student and leave an orphan Payment"
      
      enrollment = Enrollment.findLastEnrollmentByStudentId( "0202aoey" )
      
      assert enrollment.leaveTermYear != 1902, "User was able to edit a Student and leave an orphan Payment"
      assert enrollment.leaveTermNo != 2, "User was able to edit a Student and leave an orphan Payment"
      
      /* Student Edit Test, setting leaveTerm to 1903 Term 1 in order to test Payment */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "firstName", "Grace" )
        .addQueryParam( "lastName", "Wijasa" )
        .addQueryParam( "birthDate", "Oct 10 1900" )
        .addQueryParam( "village", "Bugonzi" )
        .addQueryParam( "genderCode", "Y" )
        .addQueryParam( "specialInfo", "Luke's sister" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "enrollmentLeaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "enrollmentFirstClassAttended", "Sophomore" )
        .addQueryParam( "enrollmentLastClassAttended", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 2", "Sophomore" )
        .addQueryParam( "classAttended1903 Term 1", "Sophomore" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth MaulDeceasedInd", true )
        .addQueryParam( "rd2DeceasedInd", true )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() == 1903, "User was unable to edit a Student with valid Payments"
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() == 1, "User was unable to edit a Student with valid Payments"
      
      lastUpdateDate = studentDocument.getOnlyField( "lastUpdateDate" ).getDate()
      
      enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" )
      
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() == 1903, "User was unable to edit a Student with valid Payments"
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() == 1, "User was unable to edit a Student with valid Payments"
      
      enrollment = Enrollment.findLastEnrollmentByStudentId( "0202aoey" )
      
      assert enrollment.leaveTermYear == 1903, "User was unable to edit a Student with valid Payments"
      assert enrollment.leaveTermNo == 1, "User was unable to edit a Student with valid Payments"
      
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
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1903" )
        .addQueryParam( "amount", "12,345.67" )
        .addQueryParam( "comment", "Won a lottery" ).toURL() )
        
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
	  lastUpdateDate = studentDocument.getOnlyField( "lastUpdateDate" ).getDate()
      
      /* Student Edit, setting leaveTerm to now in order to prepare the Student record to test Fee further */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "firstName", "Grace" )
        .addQueryParam( "lastName", "Wijasa" )
        .addQueryParam( "birthDate", "Oct 10 1900" )
        .addQueryParam( "village", "Bugonzi" )
        .addQueryParam( "genderCode", "Y" )
        .addQueryParam( "specialInfo", "Luke's sister" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "enrollmentLeaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "Leave Term" )
        .addQueryParam( "enrollmentFirstClassAttended", "Sophomore" )
        .addQueryParam( "enrollmentLastClassAttended", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 2", "Sophomore" )
        .addQueryParam( "classAttended1903 Term 1", "Sophomore" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth MaulDeceasedInd", true )
        .addQueryParam( "rd2DeceasedInd", true )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
	  lastUpdateDate = studentDocument.getOnlyField( "lastUpdateDate" ).getDate()
      
      /* Student Edit Test, setting leaveTerm to 1902 Term 2 in order to test Fee */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "firstName", "Grace" )
        .addQueryParam( "lastName", "Wijasa" )
        .addQueryParam( "birthDate", "Oct 10 1900" )
        .addQueryParam( "village", "Bugonzi" )
        .addQueryParam( "genderCode", "Y" )
        .addQueryParam( "specialInfo", "Luke's sister" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "enrollmentLeaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1902 Term 2" )
        .addQueryParam( "enrollmentFirstClassAttended", "Sophomore" )
        .addQueryParam( "enrollmentLastClassAttended", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 2", "Sophomore" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth MaulDeceasedInd", true )
        .addQueryParam( "rd2DeceasedInd", true )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      studentDocumentFieldNames = studentDocument.getFieldNames()
      
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermYear" ), "User was able to edit a Student and leave an orphan Fee"
      assert !studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermNo" ), "User was able to edit a Student and leave an orphan Fee"
      
      lastUpdateDate = studentDocument.getOnlyField( "lastUpdateDate" ).getDate()
      
      enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" )
      enrollmentDocumentFieldNames = enrollmentDocument.getFieldNames()
      
      assert !enrollmentDocumentFieldNames.contains( "leaveTermYear" ), "User was able to edit a Student and leave an orphan Fee"
      assert !enrollmentDocumentFieldNames.contains( "leaveTermNo" ), "User was able to edit a Student and leave an orphan Fee"
      
      enrollment = Enrollment.findLastEnrollmentByStudentId( "0202aoey" )
      
      assert enrollment.leaveTermYear != 1902, "User was able to edit a Student and leave an orphan Fee"
      assert enrollment.leaveTermNo != 2, "User was able to edit a Student and leave an orphan Fee"
      
      /* Student Edit Test, setting leaveTerm to 1903 Term 1 in order to test Fee */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "firstName", "Grace" )
        .addQueryParam( "lastName", "Wijasa" )
        .addQueryParam( "birthDate", "Oct 10 1900" )
        .addQueryParam( "village", "Bugonzi" )
        .addQueryParam( "genderCode", "Y" )
        .addQueryParam( "specialInfo", "Luke's sister" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "enrollmentLeaveReason", "Won a jackpot" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "enrollmentFirstClassAttended", "Sophomore" )
        .addQueryParam( "enrollmentLastClassAttended", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 2", "Sophomore" )
        .addQueryParam( "classAttended1903 Term 1", "Sophomore" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth MaulDeceasedInd", true )
        .addQueryParam( "rd2DeceasedInd", true )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber() == 1903, "User was unable to edit a Student with valid Fees"
      assert studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber() == 1, "User was unable to edit a Student with valid Fees"
      
      lastUpdateDate = studentDocument.getOnlyField( "lastUpdateDate" ).getDate()
      
      enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" )
      
      assert enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber() == 1903, "User was unable to edit a Student with valid Fees"
      assert enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber() == 1, "User was unable to edit a Student with valid Fees"
      
      enrollment = Enrollment.findLastEnrollmentByStudentId( "0202aoey" )
      
      assert enrollment.leaveTermYear == 1903, "User was unable to edit a Student with valid Fees"
      assert enrollment.leaveTermNo == 1, "User was unable to edit a Student with valid Fees"
      
      /* Student's Parent Deceased Ind Edit Test. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "firstName", "Grace" )
        .addQueryParam( "genderCode", "Y" )
        .addQueryParam( "enrollmentLeaveReasonCategory", "Becoming a Billionaire" )
        .addQueryParam( "leaveTerm", "1903 Term 1" )
        .addQueryParam( "enrollmentFirstClassAttended", "Sophomore" )
        .addQueryParam( "enrollmentLastClassAttended", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 1", "Sophomore" )
        .addQueryParam( "classAttended1902 Term 2", "Sophomore" )
        .addQueryParam( "classAttended1903 Term 1", "Sophomore" )
        .addQueryParam( "boardingInd1902 Term 1", true )
        .addQueryParam( "anonymousDarth MaulDeceasedInd", true )
        .addQueryParam( "anonymousDarth VaderDeceasedInd", true )
        .addQueryParam( "dvaderDeceasedInd", true )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      lastUpdateDate = studentDocument.lastUpdateDate
      
      anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Vader" )
      
      assert anonymousParentalRelationship.deceasedInd == "Y", "User was unable to edit a Student."
      
      parent = Parent.findByParentId( "dvader" )
      
      assert parent.deceasedInd == "Y", "User was unable to edit a Student."
      
      parent = Parent.findByParentId( "rd2" )
      
      assert parent.deceasedInd == "N", "User was unable to edit a Student."
    %>
    <p>Student Edit Test is successful.</p>
  </body>
</html>