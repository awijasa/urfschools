<%
  import com.google.appengine.api.datastore.Entity
  import com.google.appengine.api.memcache.MemcacheService
  import com.google.appengine.api.memcache.MemcacheServiceFactory
  import com.google.appengine.api.search.Document
  import com.google.appengine.api.search.Index
  import com.google.appengine.api.search.Results
  import com.google.appengine.api.search.ScoredDocument
  import data.Class
  import data.EnrollmentDocument
  import data.EnrollmentMemcache
  import data.School
  import data.StudentDocument
  import data.StudentMemcache
  import data.Term
  import data.TermMemcache
  import data.URFUser
  import data.UserPrivilege
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
%>
<html>
  <head>
    <title>Memcache Test</title>
  </head>
  <body>
    <%
      def school
      def schoolClass1
      def schoolClass2
      def schoolClass3
      def schoolClass4
      
      /* For the Query Test, create a new School and four new Classes if there is none existing. */
      if( School.list().size() == 0 ) {
        school = new Entity( "School" )
        
        school.name = "Saint John's College MN"
        school.lastUpdateDate = new Date()
        school.lastUpdateUser = user.getEmail()
        school.save()
        
        schoolClass1 = new Entity( "Class" )
        
        schoolClass1.schoolName = "Saint John's College MN"
        schoolClass1.class = "Freshman"
        schoolClass1.level = 1
        schoolClass1.lastUpdateDate = new Date()
        schoolClass1.lastUpdateUser = user.getEmail()
        schoolClass1.save()
        
        schoolClass2.schoolName = "Saint John's College MN"
        schoolClass2.class = "Sophomore"
        schoolClass2.level = 2
        schoolClass2.lastUpdateDate = new Date()
        schoolClass2.lastUpdateUser = user.getEmail()
        schoolClass2.save()
        
        schoolClass3.schoolName = "Saint John's College MN"
        schoolClass3.class = "Junior"
        schoolClass3.level = 3
        schoolClass3.lastUpdateDate = new Date()
        schoolClass3.lastUpdateUser = user.getEmail()
        schoolClass3.save()
        
        schoolClass4.schoolName = "Saint John's College MN"
        schoolClass4.class = "Senior"
        schoolClass4.level = 4
        schoolClass4.lastUpdateDate = new Date()
        schoolClass4.lastUpdateUser = user.getEmail()
        schoolClass4.save()
      }
      
      /* Clear the Memcache before testing the School Memcache. */
      memcache.clearAll()
      
      def doesSchoolClassExist = false
      def schoolList = School.list()
      def memcacheSchoolList = memcache.get( "schoolList" )
      
      /* Query Test: Confirm that the School and Class Memcaches contains the recently queried School and Classes. */
      schoolList.eachWithIndex() { obj, i ->
          def memcacheSchool = memcacheSchoolList.getAt( i )
          assert obj.name == memcacheSchool.name, "Name: ${ obj.name } from School.list() is not equal to ${ memcacheSchool.name } from the Memcache."
          assert obj.lastUpdateDate == memcacheSchool.lastUpdateDate, "Last Update Date: ${ obj.lastUpdateDate } from School.list() is not equal to ${ memcacheSchool.lastUpdateDate } from the Memcache."
          assert obj.lastUpdateUser == memcacheSchool.lastUpdateUser, "Last Update User: ${ obj.lastUpdateUser } from School.list() is not equal to ${ memcacheSchool.lastUpdateUser } from the Memcache."
      
          def schoolClasses = Class.findBySchoolName( obj.name )
          def memcacheSchoolClasses = memcache.get( obj.name + " Classes" )
          
          schoolClasses.eachWithIndex() { schoolClass, j ->
            doesSchoolClassExist = true
            def memcacheSchoolClass = memcacheSchoolClasses.getAt( j )
            assert schoolClass.schoolName == memcacheSchoolClass.schoolName, "School Name: ${ schoolClass.schoolName } from Class.findBySchoolName( def schoolName ) is not equal to ${ memcacheSchoolClass.schoolName } from the Memcache."
            assert schoolClass.class == memcacheSchoolClass.class, "Class: ${ schoolClass.class } from Class.findBySchoolName( def schoolName ) is not equal to ${ memcacheSchoolClass.class } from the Memcache."
            assert schoolClass.level == memcacheSchoolClass.level, "Class Level: ${ schoolClass.level } from Class.findBySchoolName( def schoolName ) is not equal to ${ memcacheSchoolClass.level } from the Memcache."
            assert schoolClass.lastUpdateDate == memcacheSchoolClass.lastUpdateDate, "Last Update Date: ${ schoolClass.lastUpdateDate } from Class.findBySchoolName( def schoolName ) is not equal to ${ memcacheSchoolClass.lastUpdateDate } from the Memcache."
            assert schoolClass.lastUpdateUser == memcacheSchoolClass.lastUpdateUser, "Last Update User: ${ schoolClass.lastUpdateUser } from Class.findBySchoolName( def schoolName ) is not equal to ${ memcacheSchoolClass.lastUpdateUser } from the Memcache."
          }
      }
      
      assert doesSchoolClassExist, "No Class is available for testing."
      
      /* Delete the School and Classes created just for the query test. */
      school?.delete()
      schoolClass1?.delete()
      schoolClass2?.delete()
      schoolClass3?.delete()
      schoolClass4?.delete()
        
      /* School and Class Save Test. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "newClass1", "Freshman" )
        .addQueryParam( "newClass2", "Sophomore" )
        .addQueryParam( "newClass3", "Junior" )
        .addQueryParam( "newClass4", "Senior" )
        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
      assert memcache.contains( "schoolList" ), "schoolList Memcache is not found. It must be retained when a schoolName is not provided during a save."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "newClass1", "Freshman" )
        .addQueryParam( "newClass2", "Sophomore" )
        .addQueryParam( "newClass3", "Junior" )
        .addQueryParam( "newClass4", "Senior" )
        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
      assert !memcache.contains( "schoolList" ), "schoolList Memcache is found. It must be deleted when a new School & new Classes are saved."
      
      /* Create School and Class Memcaches for the Delete Test. */
      log.info( "Create School and Class Memcaches for the Delete Test" )
      School.list().each() {
      	log.info( it.name )
        if( it.name.equals( "Saint John's College MN" ) ) {
        	log.info( it.name )
          school = it
          
          Class.findBySchoolName( "Saint John's College MN" )
        }
      }
      
      log.info( "Assign school.lastUpdateDate to lastUpdateDate" )
      Date lastUpdateDate = school.lastUpdateDate
      
      /* School and Classes Delete Test. */
      log.info( "School and Classes Delete Test" )
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", "-1" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert memcache.contains( "schoolList" ), "schoolList Memcache is not found. It must be retained when a School & Classes delete encounters an error."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", school.getKey().getId() )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      assert !memcache.contains( "schoolList" ), "schoolList Memcache is found. It must be deleted when a School and its Classes are deleted."
      assert !memcache.contains( "Saint John's College MN Classes" ), "Saint John's College MN Classes Memcache is found. It must be deleted when a School and its Classes are deleted."
    
      Entity enrollmentTerm
      Document enrollmentDocument
      Index enrollmentIndex = search.index( "Enrollment" )
      Entity leaveTerm
      Entity schoolClass
      Document studentDocument
      Index studentIndex = search.index( "Student" )
      Entity userPrivilege
      
      /* For the StudentController Save Test, create a new School if there is none existing */
      if( School.findByName( "Test" ) == null ) {
      	urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "schoolName", "Test" )
	        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
      }
      
      /* For the StudentController Save Test, create new Classes if there is none existing */
      if( Class.findBySchoolNameAndClass( "Test", "T.1" ) == null ) {
      	schoolClass = new Entity( "Class", School.findByName( "Test" ).getKey() )
      	
      	schoolClass.schoolName = "Test"
      	schoolClass.setProperty( "class", "T.1" )
      	schoolClass.level = 1
      	schoolClass.lastUpdateDate = new Date()
      	schoolClass.lastUpdateUser = user?.getEmail()
      	schoolClass.save()
      }
      
      if( Class.findBySchoolNameAndClass( "Test", "T.6" ) == null ) {
      	schoolClass = new Entity( "Class", School.findByName( "Test" ).getKey() )
      	
      	schoolClass.schoolName = "Test"
      	schoolClass.setProperty( "class", "T.6" )
      	schoolClass.level = 6
      	schoolClass.lastUpdateDate = new Date()
      	schoolClass.lastUpdateUser = user?.getEmail()
      	schoolClass.save()
      }
      
      /* For the StudentController Save Test, create new Terms if there is none existing. */
      if( Term.findByTermSchoolAndTermNoAndYear( "Test", 1, 2012 ) == null ) {
        enrollmentTerm = new Entity( "Term" )
        
        enrollmentTerm.termSchool = "Test"
        enrollmentTerm.termNo = 1
        enrollmentTerm.year = 2012
        enrollmentTerm.startDate = Date.parse( "MMM d yy", "Feb 1 2012" )
        enrollmentTerm.endDate = Date.parse( "MMM d yy", "Apr 30 2012" )
        enrollmentTerm.lastUpdateDate = new Date()
        enrollmentTerm.lastUpdateUser = user.getEmail()
        enrollmentTerm.save()
      }
      
      if( Term.findByTermSchoolAndTermNoAndYear( "Test", 3, 2012 ) == null ) {
        leaveTerm = new Entity( "Term" )
        
        leaveTerm.termSchool = "Test"
        leaveTerm.termNo = 3
        leaveTerm.year = 2012
        leaveTerm.startDate = Date.parse( "MMM d yy", "Aug 1 2012" )
        leaveTerm.endDate = Date.parse( "MMM d yy", "Oct 31 2012" )
        leaveTerm.lastUpdateDate = new Date()
        leaveTerm.lastUpdateUser = user.getEmail()
        leaveTerm.save()
      }
      
      if( Term.findByTermSchoolAndTermNoAndYear( "Test", 1, 2013 ) == null ) {
        enrollmentTerm = new Entity( "Term" )
        
        enrollmentTerm.termSchool = "Test"
        enrollmentTerm.termNo = 1
        enrollmentTerm.year = 2013
        enrollmentTerm.startDate = Date.parse( "MMM d yy", "Feb 1 2013" )
        enrollmentTerm.endDate = Date.parse( "MMM d yy", "Apr 30 2013" )
        enrollmentTerm.lastUpdateDate = new Date()
        enrollmentTerm.lastUpdateUser = user.getEmail()
        enrollmentTerm.save()
      }
      
      if( Term.findByTermSchoolAndTermNoAndYear( "Test", 2, 2013 ) == null ) {
        leaveTerm = new Entity( "Term" )
        
        leaveTerm.termSchool = "Test"
        leaveTerm.termNo = 2
        leaveTerm.year = 2013
        leaveTerm.startDate = Date.parse( "MMM d yy", "May 1 2013" )
        leaveTerm.endDate = Date.parse( "MMM d yy", "Jul 31 2013" )
        leaveTerm.lastUpdateDate = new Date()
        leaveTerm.lastUpdateUser = user.getEmail()
        leaveTerm.save()
      }
        
      /* Create a new EnrollmentDocument if there is none existing. */
      if( EnrollmentDocument.findByLimitAndOffsetAndURFUserEmail( 20, 0, user.getEmail(), session ).getNumberReturned() == 0 ) {
    	enrollmentIndex.put {
  			document( id: "0899aoey" ) {
  				studentId atom: "0899aoey"
  				firstName text: "Adrian"
  				firstClassAttended text: "S.1"
  				lastClassAttended text: "S.6"
  				enrollTermYear number: 2999
  				enrollTermNo number: 3
  				enrollTermStartDate date: Date.parse( "MMM d yy", "Aug 1 2999" )
  				leaveTermEndDate date: Date.parse( "MMM d yy", "Dec 31 2999" )
  				schoolName text: "Test"
  				lastUpdateDate date: new Date()
  				lastUpdateUser text: user?.getEmail()
  			}
  		}
    	
    	enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0899aoey", "Test", 3, 2999 )
      }
        
      /* Create a new StudentDocument if there is none existing. */
      if( StudentDocument.findByLimitAndOffsetAndURFUserEmail( 20, 0, user.getEmail(), session ).getNumberReturned() == 0 ) {
        studentIndex.put {
			document( id: "0899aoey" ) {
				studentId atom: "0899aoey"
				firstName text: "Adrian"
				lastEnrollmentFirstClassAttended text: "S.1"
				lastEnrollmentLastClassAttended text: "S.6"
				lastEnrollmentTermYear number: 2999
				lastEnrollmentTermNo number: 3
				lastEnrollmentTermStartDate date: Date.parse( "MMM d yy", "Aug 1 2999" )
				lastEnrollmentSchool text: "Test"
				lastUpdateDate date: new Date()
				lastUpdateUser text: user?.getEmail()
			}
		}
		
		studentDocument = StudentDocument.findByStudentId( "0899aoey" )
      }
      
      /* For the Save and Delete Test, grant Modify privilege to the current user for the Test School. */
      if( UserPrivilege.findByUserEmailAndSchoolName( user.getEmail(), "Test" ) == null ) {
        userPrivilege = new Entity( "UserPrivilege" )
        userPrivilege.userEmail = user.getEmail()
        userPrivilege.schoolName = "Test"
        userPrivilege.privilege = "Modify"
        userPrivilege.lastUpdateDate = new Date()
        userPrivilege.lastUpdateUser = user.getEmail()
        userPrivilege.save()
        
        UserPrivilege.deleteMemcache( user.getEmail(), "Modify" )
      }
      
      def urfUserSchools = ""
      UserPrivilege.findByUserEmail( user.getEmail() ).each() {
        urfUserSchools += "[" + it.schoolName + "]"
      }
      
      /* Clear the Memcache before testing the EnrollmentDocument and StudentDocument Memcaches. */
      memcache.clearAll()
      
      EnrollmentMemcache.list()*.delete()
        
      StudentMemcache.list()*.delete()
      
      Results<ScoredDocument> enrollmentResults = EnrollmentDocument.findByLimitAndOffsetAndURFUserEmail( 20, 0, user.getEmail(), session )
      MemcacheService enrollmentDocumentMemcacheService = MemcacheServiceFactory.getMemcacheService( "Enrollment" )
      Results<ScoredDocument> memcacheEnrollmentResults = enrollmentDocumentMemcacheService.get( "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc" )
      
      assert EnrollmentMemcache.findByMemcacheKey( "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc" ), "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc is not available as a EnrollmentMemcache entity."
      
      /* Query Test: Confirm that the EnrollmentDocument Memcache contains the recently queried EnrollmentDocument. */
      enrollmentResults.eachWithIndex() { obj, i ->
          Document memcacheEnrollmentDocument = memcacheEnrollmentResults.getAt( i )
          assert obj.getOnlyField( "studentId" ).getAtom() == memcacheEnrollmentDocument.getOnlyField( "studentId" ).getAtom(), "Student ID: ${ obj.getOnlyField( "studentId" ).getAtom() } from EnrollmentDocument.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheEnrollmentDocument.getOnlyField( "studentId" ).getAtom() } from the Memcache."
          assert obj.getOnlyField( "firstName" ).getText() == memcacheEnrollmentDocument.getOnlyField( "firstName" ).getText(), "First Name: ${ obj.getOnlyField( "firstName" ).getText() } from EnrollmentDocument.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheEnrollmentDocument.getOnlyField( "firstName" ).getText() } from the Memcache."
          assert obj.getOnlyField( "firstClassAttended" ).getText() == memcacheEnrollmentDocument.getOnlyField( "firstClassAttended" ).getText(), "First Class Attended: ${ obj.getOnlyField( "firstClassAttended" ).getText() } from EnrollmentDocument.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheEnrollmentDocument.getOnlyField( "firstClassAttended" ).getText() } from the Memcache."
          assert obj.getOnlyField( "lastClassAttended" ).getText() == memcacheEnrollmentDocument.getOnlyField( "lastClassAttended" ).getText(), "Last Class Attended: ${ obj.getOnlyField( "lastClassAttended" ).getText() } from EnrollmentDocument.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheEnrollmentDocument.getOnlyField( "lastClassAttended" ).getText() } from the Memcache."
          assert obj.getOnlyField( "schoolName" ).getText() == memcacheEnrollmentDocument.getOnlyField( "schoolName" ).getText(), "School Name: ${ obj.getOnlyField( "schoolName" ).getText() } from EnrollmentDocument.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheEnrollmentDocument.getOnlyField( "schoolName" ).getText() } from the Memcache."
          assert obj.getOnlyField( "enrollTermYear" ).getNumber() == memcacheEnrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber(), "Enroll Term Year: ${ obj.getOnlyField( "enrollTermYear" ).getNumber() } from EnrollmentDocument.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheEnrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() } from the Memcache."
          assert obj.getOnlyField( "enrollTermNo" ).getNumber() == memcacheEnrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber(), "Enroll Term No: ${ obj.getOnlyField( "enrollTermNo" ).getNumber() } from EnrollmentDocument.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheEnrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() } from the Memcache."
          assert obj.getOnlyField( "lastUpdateDate" ).getDate() == memcacheEnrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate(), "Last Update Date: ${ obj.getOnlyField( "lastUpdateDate" ).getDate() } from EnrollmentDocument.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheEnrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate() } from the Memcache."
          assert obj.getFieldNames().contains( "lastUpdateUser" )? obj.getOnlyField( "lastUpdateUser" ).getText(): " " == memcacheEnrollmentDocument.getFieldNames().contains( "lastUpdateUser" )? memcacheEnrollmentDocument.getOnlyField( "lastUpdateUser" ).getText(): " ", "Last Update User: ${ obj.getFieldNames().contains( "lastUpdateUser" )? obj.getOnlyField( "lastUpdateUser" ).getText(): " " } from EnrollmentDocument.findByLimitAndOffset( int limit, int offset, HttpSession session ) is not equal to ${ memcacheEnrollmentDocument.getFieldNames().contains( "lastUpdateUser" )? memcacheEnrollmentDocument.getOnlyField( "lastUpdateUser" ).getText(): " " } from the Memcache."
      }
        
      Results<ScoredDocument> studentResults = StudentDocument.findByLimitAndOffsetAndURFUserEmail( 20, 0, user.getEmail(), session )
      memcacheService = MemcacheServiceFactory.getMemcacheService( "Student" )
      Results<ScoredDocument> memcacheStudentResults = memcacheService.get( "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc" )
      
      assert StudentMemcache.findByMemcacheKey( "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc" ), "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc is not available as a StudentMemcache entity."
      
      /* Query Test: Confirm that the Student Memcache contains the recently queried StudentDocument. */
      studentResults.eachWithIndex() { obj, i ->
          Document memcacheStudentDocument = memcacheStudentResults.getAt( i )
          assert obj.getOnlyField( "studentId" ).getAtom() == memcacheStudentDocument.getOnlyField( "studentId" ).getAtom(), "Student ID: ${ obj.getOnlyField( "studentId" ).getAtom() } from StudentDocument.findByLimitAndOffset( int limit, int offset, HttpSession session ) is not equal to ${ memcacheStudentDocument.getOnlyField( "studentId" ).getAtom() } from the Memcache."
          assert obj.getOnlyField( "firstName" ).getText() == memcacheStudentDocument.getOnlyField( "firstName" ).getText(), "First Name: ${ obj.getOnlyField( "firstName" ).getText() } from StudentDocument.findByLimitAndOffset( int limit, int offset, HttpSession session ) is not equal to ${ memcacheStudentDocument.getOnlyField( "firstName" ).getText() } from the Memcache."
          assert obj.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() == memcacheStudentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText(), "Last Enrollment First Class Attended: ${ obj.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() } from StudentDocument.findByLimitAndOffset( int limit, int offset, HttpSession session ) is not equal to ${ memcacheStudentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() } from the Memcache."
          assert obj.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() == memcacheStudentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText(), "Last Enrollment Last Class Attended: ${ obj.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() } from StudentDocument.findByLimitAndOffset( int limit, int offset, HttpSession session ) is not equal to ${ memcacheStudentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() } from the Memcache."
          assert obj.getOnlyField( "lastEnrollmentSchool" ).getText() == memcacheStudentDocument.getOnlyField( "lastEnrollmentSchool" ).getText(), "Last Enrollment School: ${ obj.getOnlyField( "lastEnrollmentSchool" ).getText() } from StudentDocument.findByLimitAndOffset( int limit, int offset, HttpSession session ) is not equal to ${ memcacheStudentDocument.getOnlyField( "lastEnrollmentSchool" ).getText() } from the Memcache."
          assert obj.getOnlyField( "lastEnrollmentTermYear" ).getNumber() == memcacheStudentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber(), "Last Enrollment Term Year: ${ obj.getOnlyField( "lastEnrollmentTermYear" ).getNumber() } from StudentDocument.findByLimitAndOffset( int limit, int offset, HttpSession session ) is not equal to ${ memcacheStudentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber() } from the Memcache."
          assert obj.getOnlyField( "lastEnrollmentTermNo" ).getNumber() == memcacheStudentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber(), "Last Enrollment Term No: ${ obj.getOnlyField( "lastEnrollmentTermNo" ).getNumber() } from StudentDocument.findByLimitAndOffset( int limit, int offset, HttpSession session ) is not equal to ${ memcacheStudentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber() } from the Memcache."
          assert obj.getOnlyField( "lastUpdateDate" ).getDate() == memcacheStudentDocument.getOnlyField( "lastUpdateDate" ).getDate(), "Last Update Date: ${ obj.getOnlyField( "lastUpdateDate" ).getDate() } from StudentDocument.findByLimitAndOffset( int limit, int offset, HttpSession session ) is not equal to ${ memcacheStudentDocument.getOnlyField( "lastUpdateDate" ).getDate() } from the Memcache."
          assert obj.getFieldNames().contains( "lastUpdateUser" )? obj.getOnlyField( "lastUpdateUser" ).getText(): " " == memcacheStudentDocument.getFieldNames().contains( "lastUpdateUser" )? memcacheStudentDocument.getOnlyField( "lastUpdateUser" ).getText(): " ", "Last Update User: ${ obj.getFieldNames().contains( "lastUpdateUser" )? obj.getOnlyField( "lastUpdateUser" ).getText(): " " } from StudentDocument.findByLimitAndOffset( int limit, int offset, HttpSession session ) is not equal to ${ memcacheStudentDocument.getFieldNames().contains( "lastUpdateUser" )? memcacheStudentDocument.getOnlyField( "lastUpdateUser" ).getText(): " " } from the Memcache."
      }
      
      /* Delete the Student created just for the query test. */
      if( enrollmentDocument != null )
    	  enrollmentIndex.delete( enrollmentDocument.getId() )
      
      if( studentDocument != null )
      	studentIndex.delete( studentDocument.getId() )
        
      /* StudentController Save Test. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "firstName", "Adrian" )
        .addQueryParam( "enrollmentSchool", "Test" )
        .addQueryParam( "enrollmentTermSchool1", "2012 Term 1" )
        .addQueryParam( "enrollmentFirstClassAttendedSchool1", "T.1" ).toURL() )
      assert enrollmentDocumentMemcacheService.contains( "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc" ), "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc Memcache is not found. It must be retained when an EnrollmentDocument save encounters an error."
      assert EnrollmentMemcache.findByMemcacheKey( "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc" ), "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc is not available as an EnrollmentMemcache entity. It must be retained when an EnrollmentDocument save encounters an error."
        
      assert memcacheService.contains( "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc" ), "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc Memcache is not found. It must be retained when a StudentDocument save encounters an error."
      assert StudentMemcache.findByMemcacheKey( "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc" ), "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc is not available as a StudentMemcache entity. It must be retained when a StudentDocument save encounters an error."
        
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "firstName", "Adrian" )
        .addQueryParam( "enrollmentSchool", "Test" )
        .addQueryParam( "enrollmentTermSchool1", "2012 Term 1" )
        .addQueryParam( "enrollmentTermSchool2", "Enrollment Term" )
        .addQueryParam( "leaveTermSchool1", "2012 Term 3" )
        .addQueryParam( "leaveTermSchool2", "Leave Term" )
        .addQueryParam( "enrollmentFirstClassAttendedSchool1", "T.1" )
        .addQueryParam( "enrollmentLastClassAttendedSchool1", "T.6" ).toURL() )
      
      assert !enrollmentDocumentMemcacheService.contains( "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc" ), "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc Memcache is found. It must be deleted when a new EnrollmentDocument is saved."
      assert !EnrollmentMemcache.findByMemcacheKey( "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc" ), "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc is available as an EnrollmentMemcache entity. It must be deleted when a new EnrollmentDocument is saved"
        
      assert !memcacheService.contains( "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc" ), "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc Memcache is found. It must be deleted when a new StudentDocument is saved."
      assert !StudentMemcache.findByMemcacheKey( "studentResults of " + urfUserSchools + " row 1 to 20" ), "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc is available as a StudentMemcache entity. It must be deleted when a new StudentDocument is saved"

		/* Create the EnrollmentDocument Memcaches for the EnrollmentController Save Test. */
      EnrollmentDocument.findByLimitAndOffsetAndURFUserEmail( 20, 0, user.getEmail(), session )
             
       /* Create the StudentDocument Memcaches for the EnrollmentController Save Test. */
      StudentDocument.findByLimitAndOffsetAndURFUserEmail( 20, 0, user.getEmail(), session )

		/* EnrollmentController Save Test. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0212adrian" )
        .addQueryParam( "schoolName", "Test" )
        .addQueryParam( "enrollTermSchool1", "2013 Term 1" )
        .addQueryParam( "firstClassAttendedSchool1", "T.1" ).toURL() )
      assert enrollmentDocumentMemcacheService.contains( "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc" ), "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc Memcache is not found. It must be retained when an EnrollmentDocument save encounters an error."
      assert EnrollmentMemcache.findByMemcacheKey( "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc" ), "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc is not available as an EnrollmentMemcache entity. It must be retained when an EnrollmentDocument save encounters an error."
        
      assert memcacheService.contains( "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc" ), "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc Memcache is not found. It must be retained when a StudentDocument save encounters an error."
      assert StudentMemcache.findByMemcacheKey( "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc" ), "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc is not available as a StudentMemcache entity. It must be retained when a StudentDocument save encounters an error."
        
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "studentId", "0212adrian" )
        .addQueryParam( "schoolName", "Test" )
        .addQueryParam( "enrollTermSchool1", "2013 Term 1" )
        .addQueryParam( "enrollTermSchool2", "Enrollment Term" )
        .addQueryParam( "leaveTermSchool1", "2013 Term 2" )
        .addQueryParam( "leaveTermSchool2", "Leave Term" )
        .addQueryParam( "firstClassAttendedSchool1", "T.1" )
        .addQueryParam( "lastClassAttendedSchool1", "T.6" ).toURL() )
      
      assert !enrollmentDocumentMemcacheService.contains( "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc" ), "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc Memcache is found. It must be deleted when a new EnrollmentDocument is saved."
      assert !EnrollmentMemcache.findByMemcacheKey( "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc" ), "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc is available as an EnrollmentMemcache entity. It must be deleted when a new EnrollmentDocument is saved"
        
      assert !memcacheService.contains( "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc" ), "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc Memcache is found. It must be deleted when a new StudentDocument is saved."
      assert !StudentMemcache.findByMemcacheKey( "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc" ), "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc is available as a StudentMemcache entity. It must be deleted when a new StudentDocument is saved"
		        
      /* Create the EnrollmentDocument Memcaches for the Delete Test. */
      EnrollmentDocument.findByLimitAndOffsetAndURFUserEmail( 20, 0, user.getEmail(), session )
      
      enrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0212adrian" )
       
       /* Create the StudentDocument Memcaches for the Delete Test. */
      StudentDocument.findByLimitAndOffsetAndURFUserEmail( 20, 0, user.getEmail(), session )
      
      lastUpdateDate = enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate()
      
      /* EnrollmentController Delete Test. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", "-1" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert enrollmentDocumentMemcacheService.contains( "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc" ), "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc Memcache is not found. It must be retained when an EnrollmentDocument delete encounters an error."
      assert EnrollmentMemcache.findByMemcacheKey( "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc" ), "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc EnrollmentMemcache is not found. It must be retained when an EnrollmentDocument delete encounters an error."
          
      assert memcacheService.contains( "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc" ), "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc Memcache is not found. It must be retained when a StudentDocument delete encounters an error."
      assert StudentMemcache.findByMemcacheKey( "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc" ), "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc StudentMemcache is not found. It must be retained when a StudentDocument delete encounters an error."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/EnrollmentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", enrollmentDocument.getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "studentId", enrollmentDocument.getOnlyField( "studentId" ).getAtom() )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert !enrollmentDocumentMemcacheService.contains( "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc" ), "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc Memcache is found. It must be deleted when an EnrollmentDocument is deleted."
      assert !EnrollmentMemcache.findByMemcacheKey( "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc" ), "enrollmentResults of " + urfUserSchools + " row 1 to 20 EnrollmentMemcache is found. It must be deleted when an EnrollmentDocument is deleted."
          
      assert !memcacheService.contains( "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc" ), "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc Memcache is found. It must be deleted when a StudentDocument is deleted."
      assert !StudentMemcache.findByMemcacheKey( "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc" ), "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc StudentMemcache is found. It must be deleted when a StudentDocument is deleted."
      
      /* Create the EnrollmentDocument Memcaches for the Delete Test. */
      EnrollmentDocument.findByLimitAndOffsetAndURFUserEmail( 20, 0, user.getEmail(), session )
      
      /* Create the StudentDocument Memcaches for the Delete Test. */
      StudentDocument.findByLimitAndOffsetAndURFUserEmail( 20, 0, user.getEmail(), session )
      
      studentDocument = StudentDocument.findByStudentId( "0212adrian" )
      
      lastUpdateDate = studentDocument.getOnlyField( "lastUpdateDate" ).getDate()
      
      /* StudentController Delete Test. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", "-1" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert enrollmentDocumentMemcacheService.contains( "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc" ), "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc Memcache is not found. It must be retained when an EnrollmentDocument delete encounters an error."
      assert EnrollmentMemcache.findByMemcacheKey( "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc" ), "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc EnrollmentMemcache is not found. It must be retained when an EnrollmentDocument delete encounters an error."
          
      assert memcacheService.contains( "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc" ), "studentResults of " + urfUserSchools + " row 1 to 20 Memcache order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc is not found. It must be retained when a StudentDocument delete encounters an error."
      assert StudentMemcache.findByMemcacheKey( "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc" ), "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc StudentMemcache is not found. It must be retained when a StudentDocument delete encounters an error."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", studentDocument.getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "studentId", studentDocument.getOnlyField( "studentId" ).getAtom() )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert !enrollmentDocumentMemcacheService.contains( "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc" ), "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc Memcache is found. It must be deleted when an EnrollmentDocument is deleted."
      assert !EnrollmentMemcache.findByMemcacheKey( "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc" ), "enrollmentResults of " + urfUserSchools + " row 1 to 20 order by enrollTermStartDate dsc, firstName asc, lastName asc EnrollmentMemcache is found. It must be deleted when an EnrollmentDocument is deleted."
          
      assert !memcacheService.contains( "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc" ), "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc Memcache is found. It must be deleted when a StudentDocument is deleted."
      assert !StudentMemcache.findByMemcacheKey( "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc" ), "studentResults of " + urfUserSchools + " row 1 to 20 order by lastEnrollmentTermStartDate dsc, firstName asc, lastName asc StudentMemcache is found. It must be deleted when a StudentDocument is deleted."
    
      /* Delete the School Data Privilege created for the StudentController and EnrollmentController Save and Delete Tests. */
      userPrivilege?.delete()
        
      def term
      def urfUser
      
      /* For the Term Save and Delete Tests, create a Test School if there is none existing. */
      if( School.findByName( "Test" ) == null ) {
        school = new Entity( "School" )
        
        school.name = "Test"
        school.lastUpdateDate = new Date()
        school.lastUpdateUser = user.getEmail()
        school.save()
      }
        
      /* For the Query Test, create a Test Term if there is none existing. */
      if( Term.findByTermSchool( "Test" ).size() == 0 ) {
        term = new Entity( "Term" )
        
        term.termSchool = "Test"
        term.termNo = 1
        term.year = 1901
        term.startDate = Date.parse( "MMM d yy", "Feb 1 1901" )
        term.endDate = Date.parse( "MMM d yy", "Apr 30 1901" )
        term.lastUpdateDate = new Date()
        term.lastUpdateUser = user.getEmail()
        term.save()
      }
      
      /* Make sure that the current user has the admin privilege to modify Terms. */
      urfUser = URFUser.findByEmail( user.getEmail() )
      def urfUserBackup = URFUser.findByEmail( user.getEmail() )
      
      if( urfUser == null || urfUser.adminPrivilege == null || urfUser.adminPrivilege.equals( "Read" ) ) {
        
        if( urfUser == null )
          urfUser = new Entity( "URFUser" )
        
        urfUser.email = user.getEmail()
        urfUser.adminPrivilege = "Modify"
        urfUser.lastUpdateDate = new Date()
        urfUser.lastUpdateUser = user.getEmail()
        urfUser.save()
      }
      
      /* Make sure that the current user has the privilege to modify the Test School data. */
      userPrivilege = UserPrivilege.findByUserEmailAndSchoolName( user.getEmail(), "Test" )
      userPrivilegeBackup = UserPrivilege.findByUserEmailAndSchoolName( user.getEmail(), "Test" )
          
      if( userPrivilege == null )
        userPrivilege = new Entity( "UserPrivilege" )
        
      userPrivilege.userEmail = user.getEmail()
      userPrivilege.schoolName = "Test"
      userPrivilege.privilege = "Modify"
      userPrivilege.lastUpdateDate = new Date()
      userPrivilege.lastUpdateUser = user.getEmail()
      userPrivilege.save()
      
      /* Clear the Memcache before testing the Term Memcaches. */
      memcache.clearAll()
      
      urfUserSchools = ""
      UserPrivilege.findByUserEmail( user.getEmail() ).each() {
        urfUserSchools += "[" + it.schoolName + "]"
      }
        
      def termList = Term.findByLimitAndOffset( 20, 0 )
      memcacheService = MemcacheServiceFactory.getMemcacheService( "Term" )
      def memcacheTermList = memcacheService.get( "termList of " + urfUserSchools + " row 1 to 20" )
      
      /* Query Test: Confirm that the Term Memcaches contain the recently queried Term. */
      termList.eachWithIndex() { obj, i ->
          def memcacheTerm = memcacheTermList.getAt( i )
          assert obj.termSchool == memcacheTerm.termSchool, "Term School: ${ obj.termSchool } from Term.findByLimitAndOffset( def limit, def offset ) is not equal to ${ memcacheTerm.termSchool } from the Memcache."
          assert obj.termNo == memcacheTerm.termNo, "Term No: ${ obj.termNo } from Term.findByLimitAndOffset( def limit, def offset ) is not equal to ${ memcacheTerm.termNo } from the Memcache."
          assert obj.year == memcacheTerm.year, "Year: ${ obj.year } from Term.findByLimitAndOffset( def limit, def offset ) is not equal to ${ memcacheTerm.year } from the Memcache."
          assert obj.startDate == memcacheTerm.startDate, "Start Date: ${ obj.startDate } from Term.findByLimitAndOffset( def limit, def offset ) is not equal to ${ memcacheTerm.startDate } from the Memcache."
          assert obj.endDate == memcacheTerm.endDate, "End Date: ${ obj.endDate } from Term.findByLimitAndOffset( def limit, def offset ) is not equal to ${ memcacheTerm.endDate } from the Memcache."
          assert obj.lastUpdateDate == memcacheTerm.lastUpdateDate, "Last Update Date: ${ obj.lastUpdateDate } from Term.findByLimitAndOffset( def limit, def offset ) is not equal to ${ memcacheTerm.lastUpdateDate } from the Memcache."
          assert obj.lastUpdateUser == memcacheTerm.lastUpdateUser, "Last Update User: ${ obj.lastUpdateUser } from Term.findByLimitAndOffset( def limit, def offset ) is not equal to ${ memcacheTerm.lastUpdateUser } from the Memcache."
      }
      
      /* Clear the Memcache before performing another Query Test on the Term Memcaches. */
      memcache.clearAll()
      
      def testTerms = Term.findByTermSchool( "Test" )
      def memcacheTestTerms = memcache.get( "Test Terms" )
      
      /* Query Test: Confirm that the Term Memcaches contain the recently queried Term. */
      testTerms.eachWithIndex() { obj, i ->
          def memcacheTestTerm = memcacheTestTerms.getAt( i )
          assert obj.termSchool == memcacheTestTerm.termSchool, "Term School: ${ obj.termSchool } from Term.findByTermSchool( def termSchool ) is not equal to ${ memcacheTestTerm.termSchool } from the Memcache."
          assert obj.termNo == memcacheTestTerm.termNo, "Term No: ${ obj.termNo } from Term.findByTermSchool( def termSchool ) is not equal to ${ memcacheTestTerm.termNo } from the Memcache."
          assert obj.year == memcacheTestTerm.year, "Year: ${ obj.year } from Term.findByTermSchool( def termSchool ) is not equal to ${ memcacheTestTerm.year } from the Memcache."
          assert obj.startDate == memcacheTestTerm.startDate, "Start Date: ${ obj.startDate } from Term.findByTermSchool( def termSchool ) is not equal to ${ memcacheTestTerm.startDate } from the Memcache."
          assert obj.endDate == memcacheTestTerm.endDate, "End Date: ${ obj.endDate } from Term.findByTermSchool( def termSchool ) is not equal to ${ memcacheTestTerm.endDate } from the Memcache."
          assert obj.lastUpdateDate == memcacheTestTerm.lastUpdateDate, "Last Update Date: ${ obj.lastUpdateDate } from Term.findByTermSchool( def termSchool ) is not equal to ${ memcacheTestTerm.lastUpdateDate } from the Memcache."
          assert obj.lastUpdateUser == memcacheTestTerm.lastUpdateUser, "Last Update User: ${ obj.lastUpdateUser } from Term.findByTermSchool( def termSchool ) is not equal to ${ memcacheTestTerm.lastUpdateUser } from the Memcache."
      }
      
      /* Delete the Term created just for the query tests. */
      term?.delete()
        
      /* Term Save Test. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "termSchool", "Test" )
        .addQueryParam( "termNo", 1 )
        .addQueryParam( "year", 1901 )
        .addQueryParam( "startDate", "Feb 1 1901" ).toURL() )
      assert memcache.contains( "Test Terms" ), "Test Terms Memcache is not found. It must be retained when a Term save encounters an error."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "termSchool", "Test" )
        .addQueryParam( "termNo", 1 )
        .addQueryParam( "year", 1901 )
        .addQueryParam( "startDate", "Feb 1 1901" )
        .addQueryParam( "endDate", "Apr 30 1901" ).toURL() )
      assert !memcache.contains( "Test Terms" ), "Test Terms Memcache is found. It must be deleted when a new Term is saved."
      
      /* Create the Term Memcache for another Term Save Test. */
      testTerms = Term.findByTermSchool( "Test" )
      
      /* Term Save Test. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "termSchool", "Test" )
        .addQueryParam( "termNo", 1 )
        .addQueryParam( "year", 1901 )
        .addQueryParam( "startDate", "Feb 1 1901" )
        .addQueryParam( "endDate", "Apr 30 1901" ).toURL() )
      assert memcache.contains( "Test Terms" ), "Test Terms Memcache is not found. It must be retained when a Term save encounters a DuplicateEntityException."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "termSchool", "Test" )
        .addQueryParam( "termNo", 2 )
        .addQueryParam( "year", 1901 )
        .addQueryParam( "startDate", "Mar 1 1901" )
        .addQueryParam( "endDate", "May 30 1901" ).toURL() )
      assert memcache.contains( "Test Terms" ), "Test Terms Memcache is not found. It must be retained when a Term save encounters an OverlappedTermException."
        
      /* Create the Term Memcaches for the Term Delete Test. */
      Term.findByTermSchool( "Test" ).each() {
        if( it.termNo == 1 && it.year == 1901 )
          term = it
      }
      
      lastUpdateDate = term.lastUpdateDate
      
      termList = Term.findByLimitAndOffset( 20, 0 )
      
      /* Term Delete Test. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", "-1" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert memcache.contains( "Test Terms" ), "Test Terms Memcache is not found. It must be retained when a Term delete encounters an error."
      assert memcacheService.contains( "termList of " + urfUserSchools + " row 1 to 20" ), "termList of " + urfUserSchools + " row 1 to 20 Memcache is not found. It must be retained when a Term delete encounters an error."
      assert TermMemcache.findByMemcacheKey( "termList of " + urfUserSchools + " row 1 to 20" ), "termList of " + urfUserSchools + " row 1 to 20 TermMemcache is not found. It must be retained when a Term delete encounters an error."
        
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", term.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", 20 )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert !memcache.contains( "Test Terms" ), "Test Terms Memcache is found. It must be deleted when a Term is deleted."
      assert !memcacheService.contains( "termList of " + urfUserSchools + " row 1 to 20" ), "termList of " + urfUserSchools + " row 1 to 20 Memcache is found. It must be deleted when a Term is deleted."
      assert !TermMemcache.findByMemcacheKey( "termList of " + urfUserSchools + " row 1 to 20" ), "termList of " + urfUserSchools + " row 1 to 20 TermMemcache is found. It must be deleted when a Term is deleted."
        
      /* Delete the Test School created only for the Term Save and Delete Tests. */
      school?.delete()
      
      /* Restore the current user's Admin Privilege. */
      urfUser?.delete()
      urfUser = null
      urfUserBackup?.save()
      
      /* Restore the current user's School Data Privilege. */
      userPrivilege?.delete()
      userPrivilege = null
      userPrivilegeBackup?.save()
      
      /* For the UserPrivilege Save and Delete Tests, create the Test School if none exists. */
      if( School.findByName( "Test" ) == null ) {
        school = new Entity( "School" )
        
        school.name = "Test"
        school.lastUpdateDate = new Date()
        school.lastUpdateUser = user.getEmail()
        school.save()
      }
        
      /* For the UserPrivilege Save and Delete Tests, grant the current user a Modify Admin Privilege. */
      urfUser = URFUser.findByEmail( user.getEmail() )
      urfUserBackup = URFUser.findByEmail( user.getEmail() )
      
      if( urfUser == null )
        urfUser = new Entity( "URFUser" )
        
      urfUser.email = user.getEmail()
      urfUser.adminPrivilege = "Modify"
      urfUser.lastUpdateDate = new Date()
      urfUser.lastUpdateUser = user.getEmail()
        
      /* For the Query Test, create a new UserPrivilege if there is none existing. */
      if( UserPrivilege.findByUserEmailAndSchoolName( "test@testmail.tst", "Test" ) == null ) {
        userPrivilege = new Entity( "UserPrivilege" )
        
        userPrivilege.userEmail = "test@testmail.tst"
        userPrivilege.schoolName = "Test"
        userPrivilege.privilege = "Read"
        userPrivilege.lastUpdateDate = new Date()
        userPrivilege.lastUpdateUser = user.getEmail()
        userPrivilege.save()
      }
      
      /* Clear the Memcache before testing the UserPrivilege Memcache. */
      memcache.clearAll()
      
      /* Query Test: Confirm that the UserPrivilege Memcaches contain the recently queried UserPrivileges. */
      def userPrivilegeList = UserPrivilege.findByUserEmail( "test@testmail.tst" )
      def memcacheUserPrivilegeList = memcache.get( "test@testmail.tst" + " UserPrivileges" )
      
      userPrivilegeList.eachWithIndex() { obj, i ->
          def memcacheUserPrivilege = memcacheUserPrivilegeList.getAt( i )
          assert obj.userEmail == memcacheUserPrivilege.userEmail, "User Email: ${ obj.userEmail } from UserPrivilege.findByUserEmail( def userEmail ) is not equal to ${ memcacheUserPrivilege.userEmail } from the Memcache."
          assert obj.schoolName == memcacheUserPrivilege.schoolName, "School Name: ${ obj.schoolName } from UserPrivilege.findByUserEmail( def userEmail ) is not equal to ${ memcacheUserPrivilege.schoolName } from the Memcache."
          assert obj.privilege == memcacheUserPrivilege.privilege, "Privilege: ${ obj.privilege } from UserPrivilege.findByUserEmail( def userEmail ) is not equal to ${ memcacheUserPrivilege.privilege } from the Memcache."
          assert obj.lastUpdateDate == memcacheUserPrivilege.lastUpdateDate, "Last Update Date: ${ obj.lastUpdateDate } from UserPrivilege.findByUserEmail( def userEmail ) is not equal to ${ memcacheUserPrivilege.lastUpdateDate } from the Memcache."
          assert obj.lastUpdateUser == memcacheUserPrivilege.lastUpdateUser, "Last Update User: ${ obj.lastUpdateUser } from UserPrivilege.findByUserEmail( def userEmail ) is not equal to ${ memcacheUserPrivilege.lastUpdateUser } from the Memcache."
      }
      
      userPrivilegeList = UserPrivilege.findByUserEmailAndPrivilege( "test@testmail.tst", "Read" )
      memcacheUserPrivilegeList = memcache.get( "test@testmail.tst" + " Read UserPrivileges" )
      
      userPrivilegeList.eachWithIndex() { obj, i ->
          def memcacheUserPrivilege = memcacheUserPrivilegeList.getAt( i )
          assert obj.userEmail == memcacheUserPrivilege.userEmail, "User Email: ${ obj.userEmail } from UserPrivilege.findByUserEmail( def userEmail ) is not equal to ${ memcacheUserPrivilege.userEmail } from the Memcache."
          assert obj.schoolName == memcacheUserPrivilege.schoolName, "School Name: ${ obj.schoolName } from UserPrivilege.findByUserEmail( def userEmail ) is not equal to ${ memcacheUserPrivilege.schoolName } from the Memcache."
          assert obj.privilege == memcacheUserPrivilege.privilege, "Privilege: ${ obj.privilege } from UserPrivilege.findByUserEmail( def userEmail ) is not equal to ${ memcacheUserPrivilege.privilege } from the Memcache."
          assert obj.lastUpdateDate == memcacheUserPrivilege.lastUpdateDate, "Last Update Date: ${ obj.lastUpdateDate } from UserPrivilege.findByUserEmail( def userEmail ) is not equal to ${ memcacheUserPrivilege.lastUpdateDate } from the Memcache."
          assert obj.lastUpdateUser == memcacheUserPrivilege.lastUpdateUser, "Last Update User: ${ obj.lastUpdateUser } from UserPrivilege.findByUserEmail( def userEmail ) is not equal to ${ memcacheUserPrivilege.lastUpdateUser } from the Memcache."
      }
      
      /* Delete the UserPrivilege created only for the Query Test. */
      userPrivilege?.delete()
        
      /* UserPrivilege Save Test. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
        .addQueryParam( "action", "save" )
        .addQueryParam( "email", "test@testmail.tst" )
        .addQueryParam( "adminPrivilege", "None" )
        .addQueryParam( "${ School.findByName( "Test" ).getKey().getId() }Privilege", "Read" ).toURL() )
      assert !memcache.contains( "test@testmail.tst UserPrivileges" ), "test@testmail.tst UserPrivileges Memcache is found. It must be deleted when a new URFUser is saved."
      assert !memcache.contains( "test@testmail.tst Read UserPrivileges" ), "test@testmail.tst Read UserPrivileges Memcache is found. It must be deleted when a new URFUser is saved."
      
      /* Create the UserPrivilege Memcaches for the UserPrivilege Delete Test. */
      userPrivilegeList = UserPrivilege.findByUserEmail( "test@testmail.tst" )
      userPrivilegeList = UserPrivilege.findByUserEmailAndPrivilege( "test@testmail.tst", "Read" )
      
      Entity mockURFUser = URFUser.findByEmail( "test@testmail.tst" )
        
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", "-1" )
        .addQueryParam( "nextTwentyOffset", "20" )
        .addQueryParam( "lastUpdateDate", mockURFUser.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert memcache.contains( "test@testmail.tst UserPrivileges" ), "test@testmail.tst UserPrivileges Memcache is not found. It must be retained when a URFUser delete encounters an error."
      assert memcache.contains( "test@testmail.tst Read UserPrivileges" ), "test@testmail.tst Read UserPrivileges Memcache is not found. It must be retained when a URFUser delete encounters an error."
      
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
        .addQueryParam( "action", "delete" )
        .addQueryParam( "id", mockURFUser.getKey().getId() )
        .addQueryParam( "nextTwentyOffset", "20" )
        .addQueryParam( "lastUpdateDate", mockURFUser.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      assert !memcache.contains( "test@testmail.tst UserPrivileges" ), "test@testmail.tst UserPrivileges Memcache is found. It must be deleted when a URFUser is deleted."
      assert !memcache.contains( "test@testmail.tst Read UserPrivileges" ), "test@testmail.tst Read UserPrivileges Memcache is found. It must be deleted when a URFUser is deleted."
    
      /* Delete the Test School created for the UserPrivilege Save and Delete Tests. */
      school?.delete()
      
      /* Restore the Admin Privilege of the current user. */
      urfUser?.delete()
      urfUser = null
      urfUserBackup?.save()
    %>
    <p>School & Classes Memcache Test is successful.</p>
    <p>EnrollmentDocument Memcache Test is successful.</p>
    <p>StudentDocument Memcache Test is successful.</p>
    <p>Term Memcache Test is successful.</p>
    <p>UserPrivilege Memcache Test is successful.</p>
  </body>
</html>