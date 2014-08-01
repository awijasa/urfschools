<%
  import com.google.appengine.api.datastore.Entity
  import com.google.appengine.api.search.Document
  import data.AnonymousParentalRelationship
  import data.Class
  import data.ClassAttended
  import data.Enrollment
  import data.EnrollmentDocument
  import data.Fee
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
    <title>Student Delete Test</title>
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
      
      /* Gender Delete Test. */
      Entity gender = Gender.findByCode( "Y" )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", gender.getKey().getId() )
        .addQueryParam( "lastUpdateDate", gender.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert Gender.findByCode( "Y" ) != null, "User was able to delete a Gender that is still used by a Student."
      
      /* LeaveReasonCategory Delete Test. */
      Entity leaveReasonCategory = LeaveReasonCategory.findByCategory( "Becoming a Billionaire" )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", leaveReasonCategory.getKey().getId() )
        .addQueryParam( "lastUpdateDate", leaveReasonCategory.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert LeaveReasonCategory.findByCategory( "Becoming a Billionaire" ) != null, "User was able to delete a LeaveReasonCategory that is still used by a Student."
    
      /* School Delete Test. */
      Entity school = School.findByName( "Saint John's College MN" )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", school.getKey().getId() )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "userEmail", user?.getEmail() )
        .addQueryParam( "lastUpdateDate", school.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert School.findByName( "Saint John's College MN" ) != null, "User was able to delete a School that is still used by a Student."
      
      /* Term Delete Test. */
      Entity term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1902 )
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", term.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", term.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1902 ) != null, "User was able to delete a Term that is still used by a Student."
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey1" )
      
      Date lastUpdateDateOfDuplicate1 = new Date( studentDocument.getOnlyField( "lastUpdateDate" ).getDate().getTime() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey2" )
      
      Date lastUpdateDateOfDuplicate2 = new Date( studentDocument.getOnlyField( "lastUpdateDate" ).getDate().getTime() )
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      Date lastUpdateDate = new Date( studentDocument.getOnlyField( "lastUpdateDate" ).getDate().getTime() )
      
      enrollmentMetaData = Enrollment.findMetaDataBySchoolName( "Saint John's College MN" )
      studentMetaData = Student.findMetaDataBySchoolName( "Saint John's College MN" )
      
      /* Student Delete Test with a missing action parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "id", studentDocument.getId() )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) != null, "User was able to delete a Student using no action parameter."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) != null, "User was able to delete a Student using no action parameter."
      assert Student.findByStudentId( "0202aoey" ) != null, "User was able to delete a Student using no action parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using no action parameter"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using no action parameter."
      assert Relationship.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using no action parameter."
      assert Enrollment.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using no action parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using no action parameter"
      assert ClassAttended.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using no action parameter"
      
      /* Student Delete Test with an invalid action parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "remove" )
        .addQueryParam( "id", studentDocument.getId() )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) != null, "User was able to delete a Student using an invalid action parameter."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) != null, "User was able to delete a Student using an invalid action parameter."
     assert Student.findByStudentId( "0202aoey" ) != null, "User was able to delete a Student using an invalid action parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using an invalid action parameter"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using an invalid action parameter."
      assert Relationship.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using an invalid action parameter."
      assert Enrollment.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using an invalid action parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using an invalid action parameter"
      assert ClassAttended.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using an invalid action parameter"
      
      /* Student Delete Test with no id parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) != null, "User was able to delete a Student using no id parameter."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) != null, "User was able to delete a Student using no id parameter."
      assert Student.findByStudentId( "0202aoey" ) != null, "User was able to delete a Student using no id parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using no id parameter"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using no id parameter."
      assert Relationship.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using no id parameter."
      assert Enrollment.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using no id parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using no id parameter"
      assert ClassAttended.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using no id parameter"
      
      /* Student Delete Test with an invalid id parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", "-1" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) != null, "User was able to delete a Student using an invalid id parameter."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) != null, "User was able to delete a Student using an invalid id parameter."
      assert Student.findByStudentId( "0202aoey" ) != null, "User was able to delete a Student using an invalid id parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using an invalid id parameter"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using an invalid id parameter."
      assert Relationship.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using an invalid id parameter."
      assert Enrollment.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using an invalid id parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using an invalid id parameter"
      assert ClassAttended.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using an invalid id parameter"
      
      /* Student Delete Test with no nextTwentyOffset parameter. */
      /*urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", studentDocument.getId() )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) != null, "User was able to delete a Student using no nextTwentyOffset parameter."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) != null, "User was able to delete a Student using no nextTwentyOffset parameter."
      assert Student.findByStudentId( "0202aoey" ) != null, "User was able to delete a Student using no nextTwentyOffset parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using no nextTwentyOffset parameter"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using no nextTwentyOffset parameter."
      assert Relationship.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using no nextTwentyOffset parameter."
      assert Enrollment.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using no nextTwentyOffset parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using no nextTwentyOffset parameter"
      assert ClassAttended.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using no nextTwentyOffset parameter"
      */
      
      /* Student Delete Test with no lastUpdateDate parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", studentDocument.getId() )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "nextTwentyOffset", 20 ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) != null, "User was able to delete a Student using no lastUpdateDate parameter."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) != null, "User was able to delete a Student using no lastUpdateDate parameter."
      assert Student.findByStudentId( "0202aoey" ) != null, "User was able to delete a Student using no lastUpdateDate parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using no lastUpdateDate parameter"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using no lastUpdateDate parameter."
      assert Relationship.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using no lastUpdateDate parameter."
      assert Enrollment.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using no lastUpdateDate parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using no lastUpdateDate parameter"
      assert ClassAttended.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using no lastUpdateDate parameter"
      
      /* Student Delete Test with an invalid lastUpdateDate parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", studentDocument.getId() )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) != null, "User was able to delete a Student using an invalid lastUpdateDate parameter."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) != null, "User was able to delete a Student using an invalid lastUpdateDate parameter."
      assert Student.findByStudentId( "0202aoey" ) != null, "User was able to delete a Student using an invalid lastUpdateDate parameter."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count, "User was able to change the StudentMetaData count using an invalid lastUpdateDate parameter"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using an invalid lastUpdateDate parameter."
      assert Relationship.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using an invalid lastUpdateDate parameter."
      assert Enrollment.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using an invalid lastUpdateDate parameter."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count, "User was able to change the EnrollmentMetaData count using an invalid lastUpdateDate parameter"
      assert ClassAttended.findByStudentId( "0202aoey" ).size() > 0, "User was able to delete a Student using an invalid lastUpdateDate parameter"
    
      /* Student Delete Test. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", studentDocument.getId() )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "User was unable to delete a Student."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey" ) == null, "User was unable to delete a Student"
      assert Student.findByStudentId( "0202aoey" ) == null, "User was unable to delete a Student."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count - 1, "User was not able to change the StudentMetaData count"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey" ).size() == 0, "User was unable to delete a Student."
      assert Relationship.findByStudentId( "0202aoey" ).size() == 0, "User was unable to delete a Student."
      assert Enrollment.findByStudentId( "0202aoey" ).size() == 0, "User was unable to delete a Student."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count - 1, "User was unable to change the EnrollmentMetaData count"
      assert ClassAttended.findByStudentId( "0202aoey" ).size() == 0, "User was unable to delete a Student"
      assert Fee.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was unable to delete Fees associated to a Student"
      assert Payment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 ).size() == 0, "User was unable to delete Payments associated to a Student"
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", StudentDocument.findByStudentId( "0202aoey1" ).getId() )
        .addQueryParam( "studentId", "0202aoey1" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDateOfDuplicate1.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey1" ) == null, "User was unable to delete a Student."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey1" ) == null, "User was unable to delete a Student"
      assert Student.findByStudentId( "0202aoey1" ) == null, "User was unable to delete a Student."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count - 2, "User was not able to change the StudentMetaData count"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey1" ).size() == 0, "User was unable to delete a Student."
      assert Relationship.findByStudentId( "0202aoey1" ).size() == 0, "User was unable to delete a Student."
      assert Enrollment.findByStudentId( "0202aoey1" ).size() == 0, "User was unable to delete a Student."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count - 2, "User was unable to change the EnrollmentMetaData count"
      assert ClassAttended.findByStudentId( "0202aoey1" ).size() == 0, "User was unable to delete a Student"
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", StudentDocument.findByStudentId( "0202aoey2" ).getId() )
        .addQueryParam( "studentId", "0202aoey2" )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDateOfDuplicate2.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert StudentDocument.findByStudentId( "0202aoey2" ) == null, "User was unable to delete a Student."
      assert EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0202aoey2" ) == null, "User was unable to delete a Student"
      assert Student.findByStudentId( "0202aoey2" ) == null, "User was unable to delete a Student."
      assert Student.findMetaDataBySchoolName( "Saint John's College MN" ).count == studentMetaData.count - 3, "User was not able to change the StudentMetaData count"
      assert AnonymousParentalRelationship.findByStudentId( "0202aoey2" ).size() == 0, "User was unable to delete a Student."
      assert Relationship.findByStudentId( "0202aoey2" ).size() == 0, "User was unable to delete a Student."
      assert Enrollment.findByStudentId( "0202aoey2" ).size() == 0, "User was unable to delete a Student."
      assert Enrollment.findMetaDataBySchoolName( "Saint John's College MN" ).count == enrollmentMetaData.count - 3, "User was unable to change the EnrollmentMetaData count"
      assert ClassAttended.findByStudentId( "0202aoey2" ).size() == 0, "User was unable to delete a Student"
      
      /* Delete the Genders created for the Student Save, Edit, and Delete Test. */
      gender = Gender.findByCode( "Y" )
    
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", gender.getKey().getId() )
        .addQueryParam( "lastUpdateDate", gender.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      gender = Gender.findByCode( "X" )
    
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", gender.getKey().getId() )
        .addQueryParam( "lastUpdateDate", gender.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      /* Delete the LeaveReasonCategories created for the Student Save, Edit, and Delete Test. */
      leaveReasonCategory = LeaveReasonCategory.findByCategory( "Becoming a Billionaire" )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", leaveReasonCategory.getKey().getId() )
        .addQueryParam( "lastUpdateDate", leaveReasonCategory.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      leaveReasonCategory = LeaveReasonCategory.findByCategory( "Becoming a Millionaire" )
    
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", leaveReasonCategory.getKey().getId() )
        .addQueryParam( "lastUpdateDate", leaveReasonCategory.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      /* Delete the ParentalRelationships created for the Student Save, Edit, and Delete Test. */
      Entity parentalRelationship = ParentalRelationship.findByParentalRelationship( "Darth Maul" )
    
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", parentalRelationship.getKey().getId() )
        .addQueryParam( "lastUpdateDate", parentalRelationship.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      parentalRelationship = ParentalRelationship.findByParentalRelationship( "Darth Vader" )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", parentalRelationship.getKey().getId() )
        .addQueryParam( "lastUpdateDate", parentalRelationship.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      /* Delete the Terms created for the Student Save, Edit, and Delete Test. */
      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 2, 1901 )
      
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
        
        term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1903 )
      
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
      parent = Parent.findByParentId( "dvader" )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", parent.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", parent.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
        
      parent = Parent.findByParentId( "rd2" )
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", parent.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", parent.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
    %>
    <p>Student Delete Test is successful.</p>
  </body>
</html>