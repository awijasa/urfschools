<%
  import com.google.appengine.api.datastore.Entity
  import com.google.appengine.api.search.Document
  import data.AnonymousParentalRelationship
  import data.Class
  import data.Gender
  import data.LeaveReasonCategory
  import data.Parent
  import data.ParentalRelationship
  import data.Payment
  import data.Relationship
  import data.School
  import data.StudentDocument
  import data.Term
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
  else {
%>
	<html>
	  <head>
	    <title>Parent Save, Edit, and Delete Test</title>
	  </head>
	  <body>
	    <%
	      Entity parent
	      Entity relationship
	      
	      /* For the Save Test, make sure that the Student to create has not existed. */
	      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "Student: 0202aoey has existed. Please change the test case."
	      assert StudentDocument.findByStudentId( "0202coey" ) == null, "Student: 0202coey has existed. Please change the test case."
	      assert StudentDocument.findByStudentId( "0202goey" ) == null, "Student: 0202goey has existed. Please change the test case."
	      assert StudentDocument.findByStudentId( "0202soey" ) == null, "Student: 0202soey has existed. Please change the test case."
	      assert Gender.findByCode( "X" ) == null, "Gender: X has existed. Please change the test case."
	      assert LeaveReasonCategory.findByCategory( "Becoming a Millionaire" ) == null, "Leave Reason Category: Becoming a Millionaire has existed. Please change the test case."
	      assert ParentalRelationship.findByParentalRelationship( "Darth Vader" ) == null, "Parental Relationship: Darth Vader has existed. Please change the test case."
	      assert ParentalRelationship.findByParentalRelationship( "Darth Father" ) == null, "Parental Relationship: Darth Father has existed. Please change the test case."
	      assert School.findByName( "Saint John's College MN" ) == null, "School: Saint John's College MN has existed. Please change the test case."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Freshman" ) == null, "Class: { schoolName: Saint John's College MN, class: Freshman } has existed. Please change the test case."
	      assert Class.findBySchoolNameAndClass( "Saint John's College MN", "Sophomore" ) == null, "Class: { schoolName: Saint John's College MN, class: Sophomore } has existed. Please change the test case."
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1902 ) == null, "Term: { termSchool: Saint John's College MN, termNo: 1, year: 1902 } has existed. Please change the test case."
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 2, 1902 ) == null, "Term: { termSchool: Saint John's College MN, termNo: 2, year: 1902 } has existed. Please change the test case."
	      
	      /* Save a Gender for the Student to be saved. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "code", "X" )
	        .addQueryParam( "desc", "Tamale" ).toURL() )
	      
	      /* Save a LeaveReasonCategory for the Student to be saved. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "category", "Becoming a Millionaire" ).toURL() )
	    
	      /* Save a ParentalRelationship for the Student to be saved. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "parentalRelationship", "Darth Vader" ).toURL() )
	        
	    /* Save a ParentalRelationship for the Parent to be saved. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "parentalRelationship", "Darth Father" ).toURL() )
	    
	      /* Save a School and Classes for the Student to be saved. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "newClass1", "Freshman" )
	        .addQueryParam( "newClass2", "Sophomore" )
	        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
	      
	      /* Save Terms for the Student to be saved. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "year", 1902 )
	        .addQueryParam( "startDate", "Feb 1 1902" )
	        .addQueryParam( "endDate", "Apr 30 1902" ).toURL() )
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 2 )
	        .addQueryParam( "year", 1902 )
	        .addQueryParam( "startDate", "May 1 1902" )
	        .addQueryParam( "endDate", "Jul 31 1902" ).toURL() )
	        
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
	        .addQueryParam( "anonymousDarth FatherDeceasedInd", true )
	        .addQueryParam( "anonymousDarth VaderDeceasedInd", true ).toURL() )
	        
	    urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "firstName", "Christian" )
	        .addQueryParam( "lastName", "Oey" )
	        .addQueryParam( "birthDate", "Mar 9 1900" )
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
	        .addQueryParam( "anonymousDarth FatherDeceasedInd", true )
	        .addQueryParam( "anonymousDarth VaderDeceasedInd", true ).toURL() )
	        
	    urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "firstName", "Grace" )
	        .addQueryParam( "lastName", "Oey" )
	        .addQueryParam( "birthDate", "Jul 11 1900" )
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
	        .addQueryParam( "anonymousDarth VaderDeceasedInd", true ).toURL() )
	        
	    urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "firstName", "Simin" )
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
	        .addQueryParam( "anonymousDarth VaderDeceasedInd", true ).toURL() )
	      
	      /* Student Save Test with a missing action parameter. */
	      
	      log.info "Parent Save Test with a missing action parameter."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "firstName", "Keng Tjoan" )
	        .addQueryParam( "lastName", "Oey" )
	        .addQueryParam( "village", "Kyetume" )
	        .addQueryParam( "primaryPhone", "+6281212121212" )
	        .addQueryParam( "secondaryPhone", "+6282121212121" )
	        .addQueryParam( "email", "kengtjoanoey@yahoo.com" )
	        .addQueryParam( "profession", "Millionaire" )
	        .addQueryParam( "newParentRelationship0", "Darth Father" )
	        .addQueryParam( "newChildId0", "0202aoey" )
	        .addQueryParam( "newParentRelationship1", "Darth Father" )
	        .addQueryParam( "newChildId1", "0202coey" )
	        .addQueryParam( "newGuardianRelationship0", "Father-in-law" )
	        .addQueryParam( "newRelativeId0", "0202goey" )
	        .addQueryParam( "newGuardianRelationship1", "Employer" )
	        .addQueryParam( "newRelativeId1", "0202soey" ).toURL() )
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Father" ).deceasedInd == "Y", "User was able to modify the AnonymousParentalRelationship using no action parameter."
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202coey", "Darth Father" ).deceasedInd == "Y", "User was able to modify the AnonymousParentalRelationship using no action parameter."
	      assert Parent.findByParentId( "koey" ) == null, "User was able to save a Parent using no action parameter."
	      assert Relationship.findByParentId( "koey" ).size() == 0, "User was able to save a Relationship using no action parameter."
	          
	      /* Parent Save Test with an invalid action parameter. */
	      
	      log.info "Parent Save Test with an invalid action parameter."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "create" )
	        .addQueryParam( "firstName", "Keng Tjoan" )
	        .addQueryParam( "lastName", "Oey" )
	        .addQueryParam( "village", "Kyetume" )
	        .addQueryParam( "primaryPhone", "+6281212121212" )
	        .addQueryParam( "secondaryPhone", "+6282121212121" )
	        .addQueryParam( "email", "kengtjoanoey@yahoo.com" )
	        .addQueryParam( "profession", "Millionaire" )
	        .addQueryParam( "newParentRelationship0", "Darth Father" )
	        .addQueryParam( "newChildId0", "0202aoey" )
	        .addQueryParam( "newParentRelationship1", "Darth Father" )
	        .addQueryParam( "newChildId1", "0202coey" )
	        .addQueryParam( "newGuardianRelationship0", "Father-in-law" )
	        .addQueryParam( "newRelativeId0", "0202goey" )
	        .addQueryParam( "newGuardianRelationship1", "Employer" )
	        .addQueryParam( "newRelativeId1", "0202soey" ).toURL() )
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Father" ).deceasedInd == "Y", "User was able to modify the AnonymousParentalRelationship using an invalid action parameter."
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202coey", "Darth Father" ).deceasedInd == "Y", "User was able to modify the AnonymousParentalRelationship using an invalid action parameter."
	      assert Parent.findByParentId( "koey" ) == null, "User was able to save a Parent using an invalid action parameter."
	      assert Relationship.findByParentId( "koey" ).size() == 0, "User was able to save a Relationship using an invalid action parameter."
	            
	      /* Parent Save Test with a missing firstName parameter. */
	      
	      log.info "Parent Save Test with a missing firstName parameter."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "lastName", "Oey" )
	        .addQueryParam( "village", "Kyetume" )
	        .addQueryParam( "primaryPhone", "+6281212121212" )
	        .addQueryParam( "secondaryPhone", "+6282121212121" )
	        .addQueryParam( "email", "kengtjoanoey@yahoo.com" )
	        .addQueryParam( "profession", "Millionaire" )
	        .addQueryParam( "newParentRelationship0", "Darth Father" )
	        .addQueryParam( "newChildId0", "0202aoey" )
	        .addQueryParam( "newParentRelationship1", "Darth Father" )
	        .addQueryParam( "newChildId1", "0202coey" )
	        .addQueryParam( "newGuardianRelationship0", "Father-in-law" )
	        .addQueryParam( "newRelativeId0", "0202goey" )
	        .addQueryParam( "newGuardianRelationship1", "Employer" )
	        .addQueryParam( "newRelativeId1", "0202soey" ).toURL() )
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Father" ).deceasedInd == "Y", "User was able to modify the AnonymousParentalRelationship using no firstName parameter."
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202coey", "Darth Father" ).deceasedInd == "Y", "User was able to modify the AnonymousParentalRelationship using no firstName parameter."
	      assert Parent.findByParentId( "koey" ) == null, "User was able to save a Parent using no firstName parameter."
	      assert Relationship.findByParentId( "koey" ).size() == 0, "User was able to save a Relationship using no firstName parameter."
	            
	      /* Parent Save Test with a missing newChildId0 parameter. */
	      
	      log.info "Parent Save Test with a missing newChildId0 parameter."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "firstName", "Keng Tjoan" )
	        .addQueryParam( "lastName", "Oey" )
	        .addQueryParam( "village", "Kyetume" )
	        .addQueryParam( "primaryPhone", "+6281212121212" )
	        .addQueryParam( "secondaryPhone", "+6282121212121" )
	        .addQueryParam( "email", "kengtjoanoey@yahoo.com" )
	        .addQueryParam( "profession", "Millionaire" )
	        .addQueryParam( "newParentRelationship0", "Darth Father" )
	        .addQueryParam( "newParentRelationship1", "Darth Father" )
	        .addQueryParam( "newChildId1", "0202coey" )
	        .addQueryParam( "newGuardianRelationship0", "Father-in-law" )
	        .addQueryParam( "newRelativeId0", "0202goey" )
	        .addQueryParam( "newGuardianRelationship1", "Employer" )
	        .addQueryParam( "newRelativeId1", "0202soey" ).toURL() )
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Father" ).deceasedInd == "Y", "User was able to modify the AnonymousParentalRelationship using no newChildId0 parameter."
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202coey", "Darth Father" ).deceasedInd == "Y", "User was able to modify the AnonymousParentalRelationship using no newChildId0 parameter."
	      assert Parent.findByParentId( "koey" ) == null, "User was able to save a Parent using no newChildId0 parameter."
	      assert Relationship.findByParentId( "koey" ).size() == 0, "User was able to save a Relationship using no newChildId0 parameter."
	      
	      /* Parent Save Test with a missing newRelativeId0 parameter. */
	      
	      log.info "Parent Save Test with a missing newRelativeId0 parameter."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "firstName", "Keng Tjoan" )
	        .addQueryParam( "lastName", "Oey" )
	        .addQueryParam( "village", "Kyetume" )
	        .addQueryParam( "primaryPhone", "+6281212121212" )
	        .addQueryParam( "secondaryPhone", "+6282121212121" )
	        .addQueryParam( "email", "kengtjoanoey@yahoo.com" )
	        .addQueryParam( "profession", "Millionaire" )
	        .addQueryParam( "newParentRelationship0", "Darth Father" )
	        .addQueryParam( "newChildId0", "0202aoey" )
	        .addQueryParam( "newParentRelationship1", "Darth Father" )
	        .addQueryParam( "newChildId1", "0202coey" )
	        .addQueryParam( "newGuardianRelationship0", "Father-in-law" )
	        .addQueryParam( "newGuardianRelationship1", "Employer" )
	        .addQueryParam( "newRelativeId1", "0202soey" ).toURL() )
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Father" ).deceasedInd == "Y", "User was able to modify the AnonymousParentalRelationship using no newRelativeId0 parameter."
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202coey", "Darth Father" ).deceasedInd == "Y", "User was able to modify the AnonymousParentalRelationship using no newRelativeId0 parameter."
	      assert Parent.findByParentId( "koey" ) == null, "User was able to save a Parent using no newRelativeId0 parameter."
	      assert Relationship.findByParentId( "koey" ).size() == 0, "User was able to save a Relationship using no newRelativeId0 parameter."   
	       
	      /* Parent Save Test with newChildId0 parameter that is identical to newChildId1. */
	      
	      log.info "Parent Save Test with newChildId0 parameter that is identical to newChildId1."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "firstName", "Keng Tjoan" )
	        .addQueryParam( "lastName", "Oey" )
	        .addQueryParam( "village", "Kyetume" )
	        .addQueryParam( "primaryPhone", "+6281212121212" )
	        .addQueryParam( "secondaryPhone", "+6282121212121" )
	        .addQueryParam( "email", "kengtjoanoey@yahoo.com" )
	        .addQueryParam( "profession", "Millionaire" )
	        .addQueryParam( "newParentRelationship0", "Darth Father" )
	        .addQueryParam( "newChildId0", "0202aoey" )
	        .addQueryParam( "newParentRelationship1", "Darth Father" )
	        .addQueryParam( "newChildId1", "0202aoey" )
	        .addQueryParam( "newGuardianRelationship0", "Father-in-law" )
	        .addQueryParam( "newRelativeId0", "0202goey" )
	        .addQueryParam( "newGuardianRelationship1", "Employer" )
	        .addQueryParam( "newRelativeId1", "0202soey" ).toURL() )
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Father" ).deceasedInd == "Y", "User was able to modify the AnonymousParentalRelationship using a newChildId0 parameter that is identical to newChildId1."
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202coey", "Darth Father" ).deceasedInd == "Y", "User was able to modify the AnonymousParentalRelationship using a newChildId0 parameter that is identical to newChildId1."
	      assert Parent.findByParentId( "koey" ) == null, "User was able to save a Parent using a newChildId0 parameter that is identical to newChildId1."
	      assert Relationship.findByParentId( "koey" ).size() == 0, "User was able to save a Relationship using a newChildId0 parameter that is identical to newChildId1."    
	      
	      /* Parent Save Test with newRelativeId0 parameter that is identical to newRelativeId1. */
	      
	      log.info "Parent Save Test with newRelativeId0 parameter that is identical to newRelativeId1."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "firstName", "Keng Tjoan" )
	        .addQueryParam( "lastName", "Oey" )
	        .addQueryParam( "village", "Kyetume" )
	        .addQueryParam( "primaryPhone", "+6281212121212" )
	        .addQueryParam( "secondaryPhone", "+6282121212121" )
	        .addQueryParam( "email", "kengtjoanoey@yahoo.com" )
	        .addQueryParam( "profession", "Millionaire" )
	        .addQueryParam( "newParentRelationship0", "Darth Father" )
	        .addQueryParam( "newChildId0", "0202aoey" )
	        .addQueryParam( "newParentRelationship1", "Darth Father" )
	        .addQueryParam( "newChildId1", "0202coey" )
	        .addQueryParam( "newGuardianRelationship0", "Father-in-law" )
	        .addQueryParam( "newRelativeId0", "0202goey" )
	        .addQueryParam( "newGuardianRelationship1", "Employer" )
	        .addQueryParam( "newRelativeId1", "0202goey" ).toURL() )
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Father" ).deceasedInd == "Y", "User was able to modify the AnonymousParentalRelationship using a newRelativeId0 parameter that is identical to newRelativeId1."
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202coey", "Darth Father" ).deceasedInd == "Y", "User was able to modify the AnonymousParentalRelationship using a newRelativeId0 parameter that is identical to newRelativeId1."
	      assert Parent.findByParentId( "koey" ) == null, "User was able to save a Parent using a newRelativeId0 parameter that is identical to newRelativeId1."
	      assert Relationship.findByParentId( "koey" ).size() == 0, "User was able to save a Relationship using a newRelativeId0 parameter that is identical to newRelativeId1."    
	      
	      /* Parent Save Test with newChildId0 parameter that is identical to newRelativeId0. */
	      
	      log.info "Parent Save Test with newChildId0 parameter that is identical to newRelativeId0."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "firstName", "Keng Tjoan" )
	        .addQueryParam( "lastName", "Oey" )
	        .addQueryParam( "village", "Kyetume" )
	        .addQueryParam( "primaryPhone", "+6281212121212" )
	        .addQueryParam( "secondaryPhone", "+6282121212121" )
	        .addQueryParam( "email", "kengtjoanoey@yahoo.com" )
	        .addQueryParam( "profession", "Millionaire" )
	        .addQueryParam( "newParentRelationship0", "Darth Father" )
	        .addQueryParam( "newChildId0", "0202aoey" )
	        .addQueryParam( "newParentRelationship1", "Darth Father" )
	        .addQueryParam( "newChildId1", "0202coey" )
	        .addQueryParam( "newGuardianRelationship0", "Father-in-law" )
	        .addQueryParam( "newRelativeId0", "0202aoey" )
	        .addQueryParam( "newGuardianRelationship1", "Employer" )
	        .addQueryParam( "newRelativeId1", "0202soey" ).toURL() )
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Father" ).deceasedInd == "Y", "User was able to modify the AnonymousParentalRelationship using a newChildId0 parameter that is identical to newRelativeId0."
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202coey", "Darth Father" ).deceasedInd == "Y", "User was able to modify the AnonymousParentalRelationship using a newChildId0 parameter that is identical to newRelativeId0."
	      assert Parent.findByParentId( "koey" ) == null, "User was able to save a Parent using a newChildId0 parameter that is identical to newRelativeId0."
	      assert Relationship.findByParentId( "koey" ).size() == 0, "User was able to save a Relationship using a newChildId0 parameter that is identical to newRelativeId0."    
	      
	      /* Parent Save Test. */
	      
	      log.info "Parent Save Test."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "firstName", "Keng Tjoan" )
	        .addQueryParam( "lastName", "Oey" )
	        .addQueryParam( "village", "Kyetume" )
	        .addQueryParam( "primaryPhone", "+6281212121212" )
	        .addQueryParam( "secondaryPhone", "+6282121212121" )
	        .addQueryParam( "email", "kengtjoanoey@yahoo.com" )
	        .addQueryParam( "profession", "Millionaire" )
	        .addQueryParam( "newParentRelationship0", "Darth Father" )
	        .addQueryParam( "newChildId0", "0202aoey" )
	        .addQueryParam( "newParentRelationship1", "Darth Father" )
	        .addQueryParam( "newChildId1", "0202coey" )
	        .addQueryParam( "newGuardianRelationship0", "Father-in-law" )
	        .addQueryParam( "newRelativeId0", "0202goey" )
	        .addQueryParam( "newGuardianRelationship1", "Employer" )
	        .addQueryParam( "newRelativeId1", "0202soey" ).toURL() )
	      
	      log.info "Testing Parent value."
	      
	      parent = Parent.findByParentId( "koey" )
	      
	      Date lastUpdateDate = new Date( parent.lastUpdateDate.getTime() )
	      
	      assert parent != null, "User was unable to save a Parent."
	      assert parent.parentId == "koey", "Parent ID: ${ parent.parentId } from Parent.findByParentId( String parentId ) is not equal to koey."
	      assert parent.firstName == "Keng Tjoan", "First Name: ${ parent.firstName } from Parent.findByParentId( String parentId ) is not equal to Keng Tjoan."
	      assert parent.lastName == "Oey", "Last Name: ${ parent.lastName } from Parent.findByParentId( String parentId ) is not equal to Oey."
	      assert parent.deceasedInd == "N", "Deceased Ind: ${ parent.deceasedInd } from Parent.findByParentId( String parentId ) is not equal to N."
	      assert parent.village == "Kyetume", "Village: ${ parent.village } from Parent.findByParentId( String parentId ) is not equal to Kyetume."
	      assert parent.primaryPhone == "+6281212121212", "Primary Phone: ${ parent.primaryPhone } from Parent.findByParentId( String parentId ) is not equal to +6281212121212."
	      assert parent.secondaryPhone == "+6282121212121", "Secondary Phone: ${ parent.secondaryPhone } from Parent.findByParentId( String parentId ) is not equal to +6282121212121."
	      assert parent.email == "kengtjoanoey@yahoo.com", "Email: ${ parent.email } from Parent.findByParentId( String parentId ) is not equal to kengtjoanoey@yahoo.com."
	      assert parent.profession == "Millionaire", "Profession: ${ parent.profession } from Parent.findByParentId( String parentId ) is not equal to Millionaire."
	      assert parent.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ parent.lastUpdateDate.clearTime() } from Parent.findByParentId( String parentId ) is not equal to ${ new Date().clearTime() }."
	      
	      log.info "Testing the AnonymousParentalRelationships"
	      
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Father" ).deceasedInd == "N", "User was unable to modify the AnonymousParentalRelationship."
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202coey", "Darth Father" ).deceasedInd == "N", "User was unable to modify the AnonymousParentalRelationship."
	      
	      log.info "Testing the first Parental Relationship."
	      
	      relationship = Relationship.findByParentIdAndStudentId( "koey", "0202aoey" )
	      
	      assert relationship != null, "User was unable to save the first Parental Relationship."
	      assert relationship.parentId == "koey", "Parent ID: ${ relationship.parentId } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to koey."
	      assert relationship.studentId == "0202aoey", "Student ID: ${ relationship.studentId } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to 0202aoey."
	      assert relationship.parentalRelationship == "Darth Father", "Parental Relationship: ${ relationship.parentalRelationship } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to Darth Father."
	      assert relationship.guardianRelationship == null, "Guardian Relationship: ${ relationship.guardianRelationship } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to null."
	      assert relationship.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ relationship.lastUpdateDate.clearTime() } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to ${ new Date().clearTime() }."
	      
	      log.info "Testing the second Parental Relationship."
	      
	      relationship = Relationship.findByParentIdAndStudentId( "koey", "0202coey" )
	      
	      assert relationship != null, "User was unable to save the second Parental Relationship."
	      assert relationship.parentId == "koey", "Parent ID: ${ relationship.parentId } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to koey."
	      assert relationship.studentId == "0202coey", "Student ID: ${ relationship.studentId } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to 0202coey."
	      assert relationship.parentalRelationship == "Darth Father", "Parental Relationship: ${ relationship.parentalRelationship } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to Darth Father."
	      assert relationship.guardianRelationship == null, "Guardian Relationship: ${ relationship.guardianRelationship } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to null."
	      assert relationship.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ relationship.lastUpdateDate.clearTime() } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to ${ new Date().clearTime() }."
	      
	      log.info "Testing the first Guardian Relationship."
	      
	      relationship = Relationship.findByParentIdAndStudentId( "koey", "0202goey" )
	      
	      assert relationship != null, "User was unable to save the first Guardian Relationship."
	      assert relationship.parentId == "koey", "Parent ID: ${ relationship.parentId } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to koey."
	      assert relationship.studentId == "0202goey", "Student ID: ${ relationship.studentId } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to 0202goey."
	      assert relationship.parentalRelationship == null, "Parental Relationship: ${ relationship.parentalRelationship } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to null."
	      assert relationship.guardianRelationship == "Father-in-law", "Guardian Relationship: ${ relationship.guardianRelationship } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to Father-in-law."
	      assert relationship.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ relationship.lastUpdateDate.clearTime() } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to ${ new Date().clearTime() }."
	      
	      log.info "Testing the second Guardian Relationship."
	      
	      relationship = Relationship.findByParentIdAndStudentId( "koey", "0202soey" )
	      
	      assert relationship != null, "User was unable to save the second Guardian Relationship."
	      assert relationship.parentId == "koey", "Parent ID: ${ relationship.parentId } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to koey."
	      assert relationship.studentId == "0202soey", "Student ID: ${ relationship.studentId } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to 0202soey."
	      assert relationship.parentalRelationship == null, "Parental Relationship: ${ relationship.parentalRelationship } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to null."
	      assert relationship.guardianRelationship == "Employer", "Guardian Relationship: ${ relationship.guardianRelationship } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to Employer."
	      assert relationship.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ relationship.lastUpdateDate.clearTime() } from Relationship.findByParentIdAndStudentId( String parentId, String studentId ) is not equal to ${ new Date().clearTime() }."
	            
	      /* Parent Duplicate Parent ID and Empty Non-Required Field Save Test. */
	      
	      log.info "Parent Duplicate Parent ID and Empty Non-Required Field Save Test."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "firstName", "Keng Tjoan" )
	        .addQueryParam( "lastName", "Oey" ).toURL() )
	      
	      log.info "Testing Parent value."
	      
	      parent = Parent.findByParentId( "koey1" )
	      
	      Date lastUpdateDateOfDuplicate1 = new Date( parent.lastUpdateDate.getTime() )
	      
	      assert parent != null, "User was unable to save a Parent."
	      assert parent.parentId == "koey1", "Parent ID: ${ parent.parentId } from Parent.findByParentId( String parentId ) is not equal to koey1."
	      assert parent.firstName == "Keng Tjoan", "First Name: ${ parent.firstName } from Parent.findByParentId( String parentId ) is not equal to Keng Tjoan."
	      assert parent.lastName == "Oey", "Last Name: ${ parent.lastName } from Parent.findByParentId( String parentId ) is not equal to Oey."
	      assert parent.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ parent.lastUpdateDate.clearTime() } from Parent.findByParentId( String parentId ) is not equal to ${ new Date().clearTime() }."
	            
	      /* Parent Duplicate Parent ID and Empty Non-Required Field Save Test. */
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "firstName", "Keng Tjoan" )
	        .addQueryParam( "lastName", "Oey" ).toURL() )
	      
	      log.info "Testing Parent value."
	      
	      parent = Parent.findByParentId( "koey2" )
	      
	      Date lastUpdateDateOfDuplicate2 = new Date( parent.lastUpdateDate.getTime() )
	      
	      assert parent != null, "User was unable to save a Parent."
	      assert parent.parentId == "koey2", "Parent ID: ${ parent.parentId } from Parent.findByParentId( String parentId ) is not equal to koey2."
	      assert parent.firstName == "Keng Tjoan", "First Name: ${ parent.firstName } from Parent.findByParentId( String parentId ) is not equal to Keng Tjoan."
	      assert parent.lastName == "Oey", "Last Name: ${ parent.lastName } from Parent.findByParentId( String parentId ) is not equal to Oey."
	      assert parent.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ parent.lastUpdateDate.clearTime() } from Parent.findByParentId( String parentId ) is not equal to ${ new Date().clearTime() }."
	      
	      parent = Parent.findByParentId( "koey" )
	      
	      /* Parent Edit Test with a missing action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	      	.addQueryParam( "id", parent.getKey().getId() )
	        .addQueryParam( "firstName", "Ek" )
	        .addQueryParam( "lastName", "Wijasa" )
	        .addQueryParam( "deceasedInd", true )
	        .addQueryParam( "village", "Bogor" )
	        .addQueryParam( "primaryPhone", "+6282121212121" )
	        .addQueryParam( "secondaryPhone", "+6282212121212" )
	        .addQueryParam( "email", "ekoey@yahoo.com" )
	        .addQueryParam( "profession", "Billionaire" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Father" ).deceasedInd != "Y", "User was able to edit the AnonymousParentalRelationship using a missing action parameter."
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202coey", "Darth Father" ).deceasedInd != "Y", "User was able to edit the AnonymousParentalRelationship using a missing action parameter."
	      
	      parent = Parent.findByParentId( "koey" )
	      
	      assert parent.firstName != "Ek", "User was able to edit a Parent using a missing action parameter."
	      assert parent.lastName != "Wijasa", "User was able to edit a Parent using a missing action parameter."
	      assert parent.deceasedInd != "Y", "User was able to edit a Parent using a missing action parameter."
	      assert parent.village != "Bogor", "User was able to edit a Parent using a missing action parameter."
	      assert parent.primaryPhone != "+6282121212121", "User was able to edit a Parent using a missing action parameter."
	      assert parent.secondaryPhone != "+6282212121212", "User was able to edit a Parent using a missing action parameter."
	      assert parent.email != "ekoey@yahoo.com", "User was able to edit a Parent using a missing action parameter."
	      assert parent.profession != "Billionaire", "User was able to edit a Parent using a missing action parameter."
	            
	      /* Parent Edit Test with an invalid action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	      	.addQueryParam( "action", "modify" )
	      	.addQueryParam( "id", parent.getKey().getId() )
	        .addQueryParam( "firstName", "Ek" )
	        .addQueryParam( "lastName", "Wijasa" )
	        .addQueryParam( "deceasedInd", true )
	        .addQueryParam( "village", "Bogor" )
	        .addQueryParam( "primaryPhone", "+6282121212121" )
	        .addQueryParam( "secondaryPhone", "+6282212121212" )
	        .addQueryParam( "email", "ekoey@yahoo.com" )
	        .addQueryParam( "profession", "Billionaire" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Father" ).deceasedInd != "Y", "User was able to edit the AnonymousParentalRelationship using an invalid action parameter."
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202coey", "Darth Father" ).deceasedInd != "Y", "User was able to edit the AnonymousParentalRelationship using an invalid action parameter."
	      
	      parent = Parent.findByParentId( "koey" )
	      
	      assert parent.firstName != "Ek", "User was able to edit a Parent using an invalid action parameter."
	      assert parent.lastName != "Wijasa", "User was able to edit a Parent using an invalid action parameter."
	      assert parent.deceasedInd != "Y", "User was able to edit a Parent using an invalid action parameter."
	      assert parent.village != "Bogor", "User was able to edit a Parent using an invalid action parameter."
	      assert parent.primaryPhone != "+6282121212121", "User was able to edit a Parent using an invalid action parameter."
	      assert parent.secondaryPhone != "+6282212121212", "User was able to edit a Parent using an invalid action parameter."
	      assert parent.email != "ekoey@yahoo.com", "User was able to edit a Parent using an invalid action parameter."
	      assert parent.profession != "Billionaire", "User was able to edit a Parent using an invalid action parameter."
	      
	      /* Parent Edit Test with a missing id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	      	.addQueryParam( "action", "edit" )
	      	.addQueryParam( "firstName", "Ek" )
	        .addQueryParam( "lastName", "Wijasa" )
	        .addQueryParam( "deceasedInd", true )
	        .addQueryParam( "village", "Bogor" )
	        .addQueryParam( "primaryPhone", "+6282121212121" )
	        .addQueryParam( "secondaryPhone", "+6282212121212" )
	        .addQueryParam( "email", "ekoey@yahoo.com" )
	        .addQueryParam( "profession", "Billionaire" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Father" ).deceasedInd != "Y", "User was able to edit the AnonymousParentalRelationship using a missing id parameter."
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202coey", "Darth Father" ).deceasedInd != "Y", "User was able to edit the AnonymousParentalRelationship using a missing id parameter."
	      
	      parent = Parent.findByParentId( "koey" )
	      
	      assert parent.firstName != "Ek", "User was able to edit a Parent using a missing id parameter."
	      assert parent.lastName != "Wijasa", "User was able to edit a Parent using a missing id parameter."
	      assert parent.deceasedInd != "Y", "User was able to edit a Parent using a missing id parameter."
	      assert parent.village != "Bogor", "User was able to edit a Parent using a missing id parameter."
	      assert parent.primaryPhone != "+6282121212121", "User was able to edit a Parent using a missing id parameter."
	      assert parent.secondaryPhone != "+6282212121212", "User was able to edit a Parent using a missing id parameter."
	      assert parent.email != "ekoey@yahoo.com", "User was able to edit a Parent using a missing id parameter."
	      assert parent.profession != "Billionaire", "User was able to edit a Parent using a missing id parameter."
	      
	      /* Parent Edit Test with a missing firstName parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	      	.addQueryParam( "action", "edit" )
	      	.addQueryParam( "id", parent.getKey().getId() )
	      	.addQueryParam( "lastName", "Wijasa" )
	        .addQueryParam( "deceasedInd", true )
	        .addQueryParam( "village", "Bogor" )
	        .addQueryParam( "primaryPhone", "+6282121212121" )
	        .addQueryParam( "secondaryPhone", "+6282212121212" )
	        .addQueryParam( "email", "ekoey@yahoo.com" )
	        .addQueryParam( "profession", "Billionaire" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Father" ).deceasedInd != "Y", "User was able to edit the AnonymousParentalRelationship using a missing firstName parameter."
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202coey", "Darth Father" ).deceasedInd != "Y", "User was able to edit the AnonymousParentalRelationship using a missing firstName parameter."
	      
	      parent = Parent.findByParentId( "koey" )
	      
	      assert parent.lastName != "Wijasa", "User was able to edit a Parent using a missing firstName parameter."
	      assert parent.deceasedInd != "Y", "User was able to edit a Parent using a missing firstName parameter."
	      assert parent.village != "Bogor", "User was able to edit a Parent using a missing firstName parameter."
	      assert parent.primaryPhone != "+6282121212121", "User was able to edit a Parent using a missing firstName parameter."
	      assert parent.secondaryPhone != "+6282212121212", "User was able to edit a Parent using a missing firstName parameter."
	      assert parent.email != "ekoey@yahoo.com", "User was able to edit a Parent using a missing firstName parameter."
	      assert parent.profession != "Billionaire", "User was able to edit a Parent using a missing firstName parameter."
	      
	      /* Parent Edit Test with a missing lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	      	.addQueryParam( "action", "edit" )
	      	.addQueryParam( "id", parent.getKey().getId() )
	      	.addQueryParam( "firstName", "Ek" )
	      	.addQueryParam( "lastName", "Wijasa" )
	        .addQueryParam( "deceasedInd", true )
	        .addQueryParam( "village", "Bogor" )
	        .addQueryParam( "primaryPhone", "+6282121212121" )
	        .addQueryParam( "secondaryPhone", "+6282212121212" )
	        .addQueryParam( "email", "ekoey@yahoo.com" )
	        .addQueryParam( "profession", "Billionaire" ).toURL() )
	      
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Father" ).deceasedInd != "Y", "User was able to edit the AnonymousParentalRelationship using a missing lastUpdateDate parameter."
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202coey", "Darth Father" ).deceasedInd != "Y", "User was able to edit the AnonymousParentalRelationship using a missing lastUpdateDate parameter."
	      
	      parent = Parent.findByParentId( "koey" )
	      
	      assert parent.firstName != "Ek", "User was able to edit a Parent using a missing lastUpdateDate parameter."
	      assert parent.lastName != "Wijasa", "User was able to edit a Parent using a missing lastUpdateDate parameter."
	      assert parent.deceasedInd != "Y", "User was able to edit a Parent using a missing lastUpdateDate parameter."
	      assert parent.village != "Bogor", "User was able to edit a Parent using a missing lastUpdateDate parameter."
	      assert parent.primaryPhone != "+6282121212121", "User was able to edit a Parent using a missing lastUpdateDate parameter."
	      assert parent.secondaryPhone != "+6282212121212", "User was able to edit a Parent using a missing lastUpdateDate parameter."
	      assert parent.email != "ekoey@yahoo.com", "User was able to edit a Parent using a missing lastUpdateDate parameter."
	      assert parent.profession != "Billionaire", "User was able to edit a Parent using a missing lastUpdateDate parameter."
	      
	      /* Parent Edit Test with an invalid lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	      	.addQueryParam( "action", "edit" )
	      	.addQueryParam( "id", parent.getKey().getId() )
	      	.addQueryParam( "firstName", "Ek" )
	      	.addQueryParam( "lastName", "Wijasa" )
	        .addQueryParam( "deceasedInd", true )
	        .addQueryParam( "village", "Bogor" )
	        .addQueryParam( "primaryPhone", "+6282121212121" )
	        .addQueryParam( "secondaryPhone", "+6282212121212" )
	        .addQueryParam( "email", "ekoey@yahoo.com" )
	        .addQueryParam( "profession", "Billionaire" )
	        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Father" ).deceasedInd != "Y", "User was able to edit the AnonymousParentalRelationship using an invalid lastUpdateDate parameter."
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202coey", "Darth Father" ).deceasedInd != "Y", "User was able to edit the AnonymousParentalRelationship using an invalid lastUpdateDate parameter."
	      
	      parent = Parent.findByParentId( "koey" )
	      
	      assert parent.firstName != "Ek", "User was able to edit a Parent using an invalid lastUpdateDate parameter."
	      assert parent.lastName != "Wijasa", "User was able to edit a Parent using an invalid lastUpdateDate parameter."
	      assert parent.deceasedInd != "Y", "User was able to edit a Parent using an invalid lastUpdateDate parameter."
	      assert parent.village != "Bogor", "User was able to edit a Parent using an invalid lastUpdateDate parameter."
	      assert parent.primaryPhone != "+6282121212121", "User was able to edit a Parent using an invalid lastUpdateDate parameter."
	      assert parent.secondaryPhone != "+6282212121212", "User was able to edit a Parent using an invalid lastUpdateDate parameter."
	      assert parent.email != "ekoey@yahoo.com", "User was able to edit a Parent using an invalid lastUpdateDate parameter."
	      assert parent.profession != "Billionaire", "User was able to edit a Parent using an invalid lastUpdateDate parameter."
	      
	      /* Save a Payment for a deleteChildId Edit Test */
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
	      
	      /* Parent Edit Test with an existing Payment, deleting Child: 0202aoey and Relative: 0202goey. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	      	.addQueryParam( "action", "edit" )
	      	.addQueryParam( "id", parent.getKey().getId() )
	      	.addQueryParam( "firstName", "Ek" )
	      	.addQueryParam( "lastName", "Wijasa" )
	        .addQueryParam( "deceasedInd", true )
	        .addQueryParam( "village", "Bogor" )
	        .addQueryParam( "primaryPhone", "+6282121212121" )
	        .addQueryParam( "secondaryPhone", "+6282212121212" )
	        .addQueryParam( "email", "ekoey@yahoo.com" )
	        .addQueryParam( "profession", "Billionaire" )
	        .addQueryParam( "deleteChildId0", "0202aoey" )
	        .addQueryParam( "deleteRelativeId0", "0202goey" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Father" ).deceasedInd != "Y", "User was able to edit the AnonymousParentalRelationship with an existing Payment"
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202coey", "Darth Father" ).deceasedInd != "Y", "User was able to edit the AnonymousParentalRelationship with an existing Payment"
	      
	      parent = Parent.findByParentId( "koey" )
	      
	      assert parent.firstName != "Ek", "User was able to edit a Parent with an existing Payment"
	      assert parent.lastName != "Wijasa", "User was able to edit a Parent with an existing Payment"
	      assert parent.deceasedInd != "Y", "User was able to edit a Parent with an existing Payment"
	      assert parent.village != "Bogor", "User was able to edit a Parent with an existing Payment"
	      assert parent.primaryPhone != "+6282121212121", "User was able to edit a Parent with an existing Payment"
	      assert parent.secondaryPhone != "+6282212121212", "User was able to edit a Parent with an existing Payment"
	      assert parent.email != "ekoey@yahoo.com", "User was able to edit a Parent with an existing Payment"
	      assert parent.profession != "Billionaire", "User was able to edit a Parent with an existing Payment"
	      
	      assert Relationship.findByParentIdAndStudentId( "koey", "0202aoey" ) != null, "User was able to delete the Parental Relationship with 0202aoey with an existing Payment"
	      assert Relationship.findByParentIdAndStudentId( "koey", "0202coey" ) != null, "User accidentally deleted the Parental Relationship with 0202coey."
	      assert Relationship.findByParentIdAndStudentId( "koey", "0202goey" ) != null, "User was able to delete the Guardian Relationship with 0202goey with an existing Payment"
	      assert Relationship.findByParentIdAndStudentId( "koey", "0202soey" ) != null, "User accidentally deleted the Guardian Relationship with 0202soey."
	      
	      Entity payment = Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202aoey", "Saint John's College MN", 1, 1902 )
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", payment.getKey().getId() )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", payment.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      /* Parent Edit Test, deleting Child: 0202aoey and Relative: 0202goey. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	      	.addQueryParam( "action", "edit" )
	      	.addQueryParam( "id", parent.getKey().getId() )
	      	.addQueryParam( "firstName", "Ek" )
	      	.addQueryParam( "lastName", "Wijasa" )
	        .addQueryParam( "deceasedInd", true )
	        .addQueryParam( "village", "Bogor" )
	        .addQueryParam( "primaryPhone", "+6282121212121" )
	        .addQueryParam( "secondaryPhone", "+6282212121212" )
	        .addQueryParam( "email", "ekoey@yahoo.com" )
	        .addQueryParam( "profession", "Billionaire" )
	        .addQueryParam( "deleteChildId0", "0202aoey" )
	        .addQueryParam( "deleteRelativeId0", "0202goey" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202aoey", "Darth Father" ).deceasedInd == "Y", "User was unable to edit the AnonymousParentalRelationship."
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202coey", "Darth Father" ).deceasedInd == "Y", "User was unable to edit the AnonymousParentalRelationship."
	      
	      parent = Parent.findByParentId( "koey" )
	      
	      assert parent.firstName == "Ek", "User was unable to edit a Parent."
	      assert parent.lastName == "Wijasa", "User was unable to edit a Parent."
	      assert parent.deceasedInd == "Y", "User was unable to edit a Parent."
	      assert parent.village == "Bogor", "User was unable to edit a Parent."
	      assert parent.primaryPhone == "+6282121212121", "User was unable to edit a Parent."
	      assert parent.secondaryPhone == "+6282212121212", "User was unable to edit a Parent."
	      assert parent.email == "ekoey@yahoo.com", "User was unable to edit a Parent."
	      assert parent.profession == "Billionaire", "User was unable to edit a Parent."
	      
	      lastUpdateDate = parent.lastUpdateDate
	      
	      assert Relationship.findByParentIdAndStudentId( "koey", "0202aoey" ) == null, "User was unable to delete the Parental Relationship with 0202aoey."
	      assert Relationship.findByParentIdAndStudentId( "koey", "0202coey" ) != null, "User accidentally deleted the Parental Relationship with 0202coey."
	      assert Relationship.findByParentIdAndStudentId( "koey", "0202goey" ) == null, "User was unable to delete the Guardian Relationship with 0202goey."
	      assert Relationship.findByParentIdAndStudentId( "koey", "0202soey" ) != null, "User accidentally deleted the Guardian Relationship with 0202soey."
	      
	      /* Parent Edit Test, adding Child: 0202goey and Relative: 0202aoey. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	      	.addQueryParam( "action", "edit" )
	      	.addQueryParam( "id", parent.getKey().getId() )
	      	.addQueryParam( "firstName", "Ek" )
	      	.addQueryParam( "lastName", "Wijasa" )
	        .addQueryParam( "deceasedInd", true )
	        .addQueryParam( "village", "Bogor" )
	        .addQueryParam( "primaryPhone", "+6282121212121" )
	        .addQueryParam( "secondaryPhone", "+6282212121212" )
	        .addQueryParam( "email", "ekoey@yahoo.com" )
	        .addQueryParam( "profession", "Billionaire" )
	        .addQueryParam( "newParentRelationship0", "Darth Father" )
	        .addQueryParam( "newChildId0", "0202goey" )
	        .addQueryParam( "newGuardianRelationship0", "Father-in-law" )
	        .addQueryParam( "newRelativeId0", "0202aoey" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202goey", "Darth Father" ).deceasedInd == "Y", "User was unable to edit the AnonymousParentalRelationship."
	      assert AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( "0202coey", "Darth Father" ).deceasedInd == "Y", "User was unable to edit the AnonymousParentalRelationship."
	      
	      parent = Parent.findByParentId( "koey" )
	      
	      lastUpdateDate = parent.lastUpdateDate
	      
	      relationship = Relationship.findByParentIdAndStudentId( "koey", "0202aoey" )
	      assert relationship != null, "User was unable to add the Guardian Relationship with 0202aoey."
	      assert relationship.guardianRelationship == "Father-in-law", "User was unable to add the Guardian Relationshp with 0202aoey."
	      
	      assert Relationship.findByParentIdAndStudentId( "koey", "0202coey" ) != null, "User accidentally deleted the Parental Relationship with 0202coey."
	      
	      relationship = Relationship.findByParentIdAndStudentId( "koey", "0202goey" )
	      assert relationship != null, "User was unable to add the Parental Relationship with 0202goey."
	      assert relationship.parentalRelationship == "Darth Father", "User was unable to add the Parental Relationship with 0202goey."
	      
	      assert Relationship.findByParentIdAndStudentId( "koey", "0202soey" ) != null, "User accidentally deleted the Guardian Relationship with 0202soey."
	    
	      /* ParentalRelationship Delete Test. */
	      Entity parentalRelationship = ParentalRelationship.findByParentalRelationship( "Darth Father" )
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", parentalRelationship.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", parentalRelationship.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert ParentalRelationship.findByParentalRelationship( "Darth Father" ) != null, "User was able to delete a ParentalRelationship that is still used by a Parent."
	    
	      /* Student Delete Test. */
	      Document studentDocument = StudentDocument.findByStudentId( "0202aoey" )
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", studentDocument.getId() )
	        .addQueryParam( "studentId", "0202aoey" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", studentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Relationship.findByParentIdAndStudentId( "koey", "0202aoey" ) == null, "User was unable to delete Relationships associated to the deleted Student."
	      assert StudentDocument.findByStudentId( "0202aoey" ) == null, "User was unable to delete a Student who is still used by a Relationship."
	      
	      /* Parent Delete Test with a missing action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "id", parent.getKey().getId() )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Parent.findByParentId( "koey" ) != null, "User was able to delete a Parent using no action parameter."
	      assert Relationship.findByParentId( "koey" ).size() > 0, "User was able to delete a Parent using no action parameter."
	      
	      /* Parent Delete Test with an invalid action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "remove" )
	        .addQueryParam( "id", parent.getKey().getId() )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Parent.findByParentId( "koey" ) != null, "User was able to delete a Parent using an invalid action parameter."
	      assert Relationship.findByParentId( "koey" ).size() > 0, "User was able to delete a Parent using an invalid action parameter."
	      
	      /* Parent Delete Test with no id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Parent.findByParentId( "koey" ) != null, "User was able to delete a Parent using no id parameter."
	      assert Relationship.findByParentId( "koey" ).size() > 0, "User was able to delete a Parent using no id parameter."
	      
	      /* Parent Delete Test with an invalid id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", "-1" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Parent.findByParentId( "koey" ) != null, "User was able to delete a Parent using an invalid id parameter."
	      assert Relationship.findByParentId( "koey" ).size() > 0, "User was able to delete a Parent using an invalid id parameter."
	      
	      /* Parent Delete Test with no nextTwentyOffset parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", parent.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Parent.findByParentId( "koey" ) != null, "User was able to delete a Parent using no nextTwentyOffset parameter."
	      assert Relationship.findByParentId( "koey" ).size() > 0, "User was able to delete a Parent using no nextTwentyOffset parameter."
	      
	      /* Parent Delete Test with no lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", parent.getKey().getId() )
	        .addQueryParam( "nextTwentyOffset", 20 ).toURL() )
	      assert Parent.findByParentId( "koey" ) != null, "User was able to delete a Parent using no lastUpdateDate parameter."
	      assert Relationship.findByParentId( "koey" ).size() > 0, "User was able to delete a Parent using no lastUpdateDate parameter."
	      
	      /* Parent Delete Test with an invalid lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", parent.getKey().getId() )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Parent.findByParentId( "koey" ) != null, "User was able to delete a Parent using an invalid lastUpdateDate parameter."
	      assert Relationship.findByParentId( "koey" ).size() > 0, "User was able to delete a Parent using an invalid lastUpdateDate parameter."
	    
	      /* Save a Payment for a Parent Delete Test */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "studentId", "0202coey" )
	        .addQueryParam( "fundingSource", "koey" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "enrollTermNo", "1" )
	        .addQueryParam( "enrollTermYear", "1902" )
	        .addQueryParam( "classTermNo", "1" )
	        .addQueryParam( "classTermYear", "1902" )
	        .addQueryParam( "amount", "12,345.67" )
	        .addQueryParam( "comment", "Won a lottery" ).toURL() )
	      
	      /* Parent Delete Test with an existing Payment */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", parent.getKey().getId() )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Parent.findByParentId( "koey" ) != null, "User was able to delete a Parent with an existing Payment"
	      assert Relationship.findByParentId( "koey" ).size() != 0, "User was able to delete a Parent with an existing Payment"
	      
	      payment = Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "koey", "0202coey", "Saint John's College MN", 1, 1902 )
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/PaymentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", payment.getKey().getId() )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", payment.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      /* Parent Delete Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", parent.getKey().getId() )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Parent.findByParentId( "koey" ) == null, "User was unable to delete a Parent."
	      assert Relationship.findByParentId( "koey" ).size() == 0, "User was unable to delete a Parent."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", Parent.findByParentId( "koey1" ).getKey().getId() )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDateOfDuplicate1.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Parent.findByParentId( "koey1" ) == null, "User was unable to delete a Parent."
	      assert Relationship.findByParentId( "koey1" ).size() == 0, "User was unable to delete a Parent."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", Parent.findByParentId( "koey2" ).getKey().getId() )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDateOfDuplicate2.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Parent.findByParentId( "koey2" ) == null, "User was unable to delete a Parent."
	      assert Relationship.findByParentId( "koey2" ).size() == 0, "User was unable to delete a Parent."
	      
	      /* Delete the Students created for the Parent Save, Edit, and Delete Test. */
	      studentDocument = StudentDocument.findByStudentId( "0202coey" )
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", studentDocument.getId() )
	        .addQueryParam( "studentId", "0202coey" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", studentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	        
	    studentDocument = StudentDocument.findByStudentId( "0202goey" )
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", studentDocument.getId() )
	        .addQueryParam( "studentId", "0202goey" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", studentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	        
	    studentDocument = StudentDocument.findByStudentId( "0202soey" )
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", studentDocument.getId() )
	        .addQueryParam( "studentId", "0202soey" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", studentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      /* Delete the Genders created for the Parent Save, Edit, and Delete Test. */
	      Entity gender = Gender.findByCode( "X" )
	    
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", gender.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", gender.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      /* Delete the LeaveReasonCategories created for the Parent Save, Edit, and Delete Test. */
	      Entity leaveReasonCategory = LeaveReasonCategory.findByCategory( "Becoming a Millionaire" )
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", leaveReasonCategory.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", leaveReasonCategory.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      /* Delete the ParentalRelationships created for the Parent Save, Edit, and Delete Test. */
	      parentalRelationship = ParentalRelationship.findByParentalRelationship( "Darth Vader" )
	    
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", parentalRelationship.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", parentalRelationship.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      parentalRelationship = ParentalRelationship.findByParentalRelationship( "Darth Father" )
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", parentalRelationship.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", parentalRelationship.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      /* Delete the Terms created for the Parent Save, Edit, and Delete Test. */
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
	        
	    /* Delete the Schools and Classes created for the Parent Save, Edit, and Delete Test. */
	      Entity school = School.findByName( "Saint John's College MN" )
	    
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", school.getKey().getId() )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "lastUpdateDate", school.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	    %>
	    <p>Parent Save, Edit, and Delete Test is successful.</p>
	  </body>
	</html>
<% } %>