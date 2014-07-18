<%
  import com.google.appengine.api.datastore.Entity
  import com.google.appengine.api.search.Document
  import data.ClassAttended
  import data.ClassFees
  import data.EnrollmentDocument
  import data.School
  import data.StudentDocument
  import data.Term
  import data.URFUser
  import data.UserPrivilege
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
  else {
%>
	<html>
	  <head>
	    <title>Term Edit Test</title>
	  </head>
	  <body>
	    <%
	      Entity classAttended
	      Entity freshmanClassFees
	      Entity juniorClassFees
	      Entity seniorClassFees
	      Entity sophomoreClassFees
	      Entity term
	      
	      /* Give the testing user an Admin Modify privilege. */
	      Entity urfUser = URFUser.findByEmail( user.getEmail() )
	      Entity urfUserBackup = URFUser.findByEmail( user.getEmail() )
	      
	      if( urfUser == null )
	        urfUser = new Entity( "URFUser" )
	        
	      urfUser.email = user.getEmail()
	      urfUser.adminPrivilege = "Modify"
	      urfUser?.lastUpdateDate = new Date()
	      urfUser.lastUpdateUser = user.getEmail()
	      urfUser.save()
	      
	      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 )
	      
	      Date lastUpdateDate = new Date( term?.lastUpdateDate?.getTime() )
	      
	      /* Term Edit Test with a missing action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "startDate", "Feb 2 1901" )
	        .addQueryParam( "endDate", "Apr 29 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "50000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "60000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "70000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "80000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "500000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "600000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "700000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "800000" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 )
	      assert term.startDate != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit a Term using a missing action parameter."
	      assert term.endDate != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit a Term using a missing action parameter."
	      
	      freshmanClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Freshman", 1, 1901 )
	      
	      assert freshmanClassFees.tuitionFee != 50000, "User was able to edit a ClassFees using a missing action parameter"
	      assert freshmanClassFees.boardingFee != 500000, "User was able to edit a ClassFees using a missing action parameter"
	      
	      sophomoreClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Sophomore", 1, 1901 )
	      
	      assert sophomoreClassFees.tuitionFee != 60000, "User was able to edit a ClassFees using a missing action parameter"
	      assert sophomoreClassFees.boardingFee != 600000, "User was able to edit a ClassFees using a missing action parameter"
	      
	      juniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Junior", 1, 1901 )
	      
	      assert juniorClassFees.tuitionFee != 70000, "User was able to edit a ClassFees using a missing action parameter"
	      assert juniorClassFees.boardingFee != 700000, "User was able to edit a ClassFees using a missing action parameter"
	      
	      seniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Senior", 1, 1901 )
	      
	      assert seniorClassFees.tuitionFee != 80000, "User was able to edit a ClassFees using a missing action parameter"
	      assert seniorClassFees.boardingFee != 800000, "User was able to edit a ClassFees using a missing action parameter"
	      
	      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert classAttended.tuitionFee != 70000, "User was able to edit a ClassAttended using a missing action parameter"
	      assert classAttended.boardingFee != 700000, "User was able to edit a ClassAttended using a missing action parameter"
	      
	      Document enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an EnrollmentDocument using a missing action parameter"
	      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit an EnrollmentDocument using a missing action parameter"
	      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using a missing action parameter"
	      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using a missing action parameter"
	      
		  Document studentDocument = StudentDocument.findByStudentId( "0201aoey" )
	      
	      assert studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an StudentDocument using a missing action parameter"
	      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an StudentDocument using a missing action parameter"
	      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an StudentDocument using a missing action parameter"
	      
	      /* Term Edit Test with an invalid action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "modify" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "startDate", "Feb 2 1901" )
	        .addQueryParam( "endDate", "Apr 29 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "50000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "60000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "70000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "80000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "500000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "600000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "700000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "800000" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 )
	      assert term.startDate != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit a Term using an invalid action parameter."
	      assert term.endDate != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit a Term using an invalid action parameter."
	      
	      freshmanClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Freshman", 1, 1901 )
	      
	      assert freshmanClassFees.tuitionFee != 50000, "User was able to edit a ClassFees using an invalid action parameter"
	      assert freshmanClassFees.boardingFee != 500000, "User was able to edit a ClassFees using an invalid action parameter"
	      
	      sophomoreClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Sophomore", 1, 1901 )
	      
	      assert sophomoreClassFees.tuitionFee != 60000, "User was able to edit a ClassFees using an invalid action parameter"
	      assert sophomoreClassFees.boardingFee != 600000, "User was able to edit a ClassFees using an invalid action parameter"
	      
	      juniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Junior", 1, 1901 )
	      
	      assert juniorClassFees.tuitionFee != 70000, "User was able to edit a ClassFees using an invalid action parameter"
	      assert juniorClassFees.boardingFee != 700000, "User was able to edit a ClassFees using an invalid action parameter"
	      
	      seniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Senior", 1, 1901 )
	      
	      assert seniorClassFees.tuitionFee != 80000, "User was able to edit a ClassFees using an invalid action parameter"
	      assert seniorClassFees.boardingFee != 800000, "User was able to edit a ClassFees using an invalid action parameter"
	      
	      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert classAttended.tuitionFee != 70000, "User was able to edit a ClassAttended using an invalid action parameter"
	      assert classAttended.boardingFee != 700000, "User was able to edit a ClassAttended using an invalid action parameter"
	      
		  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an EnrollmentDocument using an invalid action parameter"
	      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit an EnrollmentDocument using an invalid action parameter"
	      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using an invalid action parameter"
	      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using an invalid action parameter"
	      
		  studentDocument = StudentDocument.findByStudentId( "0201aoey" )
	      
	      assert studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an StudentDocument using an invalid action parameter"
	      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an StudentDocument using an invalid action parameter"
	      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an StudentDocument using an invalid action parameter"
	      
	      /* Term Edit Test with a missing id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "Feb 2 1901" )
	        .addQueryParam( "endDate", "Apr 29 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "50000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "60000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "70000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "80000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "500000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "600000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "700000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "800000" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 )
	      assert term.startDate != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit a Term using no id parameter."
	      assert term.endDate != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit a Term using no id parameter."
	      
	      freshmanClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Freshman", 1, 1901 )
	      
	      assert freshmanClassFees.tuitionFee != 50000, "User was able to edit a ClassFees using no id parameter"
	      assert freshmanClassFees.boardingFee != 500000, "User was able to edit a ClassFees using no id parameter"
	      
	      sophomoreClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Sophomore", 1, 1901 )
	      
	      assert sophomoreClassFees.tuitionFee != 60000, "User was able to edit a ClassFees using no id parameter"
	      assert sophomoreClassFees.boardingFee != 600000, "User was able to edit a ClassFees using no id parameter"
	      
	      juniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Junior", 1, 1901 )
	      
	      assert juniorClassFees.tuitionFee != 70000, "User was able to edit a ClassFees using no id parameter"
	      assert juniorClassFees.boardingFee != 700000, "User was able to edit a ClassFees using no id parameter"
	      
	      seniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Senior", 1, 1901 )
	      
	      assert seniorClassFees.tuitionFee != 80000, "User was able to edit a ClassFees using no id parameter"
	      assert seniorClassFees.boardingFee != 800000, "User was able to edit a ClassFees using no id parameter"
	      
	      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert classAttended.tuitionFee != 70000, "User was able to edit a ClassAttended using no id parameter"
	      assert classAttended.boardingFee != 700000, "User was able to edit a ClassAttended using no id parameter"
	      
		  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an EnrollmentDocument using no id parameter"
	      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit an EnrollmentDocument using no id parameter"
	      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using no id parameter"
	      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using no id parameter"
	      
		  studentDocument = StudentDocument.findByStudentId( "0201aoey" )
	      
	      assert studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an StudentDocument using no id parameter"
	      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an StudentDocument using no id parameter"
	      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an StudentDocument using no id parameter"
	      
	      /* Term Edit Test with an invalid id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", "-1" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "Feb 2 1901" )
	        .addQueryParam( "endDate", "Apr 29 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "50000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "60000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "70000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "80000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "500000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "600000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "700000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "800000" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 )
	      assert term.startDate != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit a Term using an invalid id parameter."
	      assert term.endDate != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit a Term using an invalid id parameter."
	      
	      freshmanClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Freshman", 1, 1901 )
	      
	      assert freshmanClassFees.tuitionFee != 50000, "User was able to edit a ClassFees using an invalid id parameter"
	      assert freshmanClassFees.boardingFee != 500000, "User was able to edit a ClassFees using an invalid id parameter"
	      
	      sophomoreClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Sophomore", 1, 1901 )
	      
	      assert sophomoreClassFees.tuitionFee != 60000, "User was able to edit a ClassFees using an invalid id parameter"
	      assert sophomoreClassFees.boardingFee != 600000, "User was able to edit a ClassFees using an invalid id parameter"
	      
	      juniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Junior", 1, 1901 )
	      
	      assert juniorClassFees.tuitionFee != 70000, "User was able to edit a ClassFees using an invalid id parameter"
	      assert juniorClassFees.boardingFee != 700000, "User was able to edit a ClassFees using an invalid id parameter"
	      
	      seniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Senior", 1, 1901 )
	      
	      assert seniorClassFees.tuitionFee != 80000, "User was able to edit a ClassFees using an invalid id parameter"
	      assert seniorClassFees.boardingFee != 800000, "User was able to edit a ClassFees using an invalid id parameter"
	      
	      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert classAttended.tuitionFee != 70000, "User was able to edit a ClassAttended using an invalid id parameter"
	      assert classAttended.boardingFee != 700000, "User was able to edit a ClassAttended using an invalid id parameter"
	      
		  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an EnrollmentDocument using an invalid id parameter"
	      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit an EnrollmentDocument using an invalid id parameter"
	      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using an invalid id parameter"
	      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using an invalid id parameter"
	      
		  studentDocument = StudentDocument.findByStudentId( "0201aoey" )
	      
	      assert studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an StudentDocument using an invalid id parameter"
	      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an StudentDocument using an invalid id parameter"
	      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an StudentDocument using an invalid id parameter"
	      
	      /* Term Edit Test with a missing startDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "endDate", "Apr 29 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "50000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "60000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "70000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "80000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "500000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "600000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "700000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "800000" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 )
	      assert term.startDate != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit a Term using no startDate parameter."
	      assert term.endDate != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit a Term using no startDate parameter."
	      
	      freshmanClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Freshman", 1, 1901 )
	      
	      assert freshmanClassFees.tuitionFee != 50000, "User was able to edit a ClassFees using a missing startDate parameter"
	      assert freshmanClassFees.boardingFee != 500000, "User was able to edit a ClassFees using a missing startDate parameter"
	      
	      sophomoreClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Sophomore", 1, 1901 )
	      
	      assert sophomoreClassFees.tuitionFee != 60000, "User was able to edit a ClassFees using a missing startDate parameter"
	      assert sophomoreClassFees.boardingFee != 600000, "User was able to edit a ClassFees using a missing startDate parameter"
	      
	      juniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Junior", 1, 1901 )
	      
	      assert juniorClassFees.tuitionFee != 70000, "User was able to edit a ClassFees using a missing startDate parameter"
	      assert juniorClassFees.boardingFee != 700000, "User was able to edit a ClassFees using a missing startDate parameter"
	      
	      seniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Senior", 1, 1901 )
	      
	      assert seniorClassFees.tuitionFee != 80000, "User was able to edit a ClassFees using a missing startDate parameter"
	      assert seniorClassFees.boardingFee != 800000, "User was able to edit a ClassFees using a missing startDate parameter"
	      
	      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert classAttended.tuitionFee != 70000, "User was able to edit a ClassAttended using a missing startDate parameter"
	      assert classAttended.boardingFee != 700000, "User was able to edit a ClassAttended using a missing startDate parameter"
	      
		  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an EnrollmentDocument using a missing startDate parameter"
	      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit an EnrollmentDocument using a missing startDate parameter"
	      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using a missing startDate parameter"
	      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using a missing startDate parameter"
	      
		  studentDocument = StudentDocument.findByStudentId( "0201aoey" )
	      
	      assert studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an StudentDocument using a missing startDate parameter"
	      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an StudentDocument using a missing startDate parameter"
	      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an StudentDocument using a missing startDate parameter"
	      
	      /* Term Edit Test with an invalid startDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "startDate", "2/2/1901" )
	        .addQueryParam( "endDate", "Apr 29 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "50000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "60000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "70000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "80000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "500000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "600000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "700000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "800000" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 )
	      assert term.startDate != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit a Term using an invalid startDate parameter."
	      assert term.endDate != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit a Term using an invalid startDate parameter."
	      
	      freshmanClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Freshman", 1, 1901 )
	      
	      assert freshmanClassFees.tuitionFee != 50000, "User was able to edit a ClassFees using an invalid startDate parameter"
	      assert freshmanClassFees.boardingFee != 500000, "User was able to edit a ClassFees using an invalid startDate parameter"
	      
	      sophomoreClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Sophomore", 1, 1901 )
	      
	      assert sophomoreClassFees.tuitionFee != 60000, "User was able to edit a ClassFees using an invalid startDate parameter"
	      assert sophomoreClassFees.boardingFee != 600000, "User was able to edit a ClassFees using an invalid startDate parameter"
	      
	      juniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Junior", 1, 1901 )
	      
	      assert juniorClassFees.tuitionFee != 70000, "User was able to edit a ClassFees using an invalid startDate parameter"
	      assert juniorClassFees.boardingFee != 700000, "User was able to edit a ClassFees using an invalid startDate parameter"
	      
	      seniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Senior", 1, 1901 )
	      
	      assert seniorClassFees.tuitionFee != 80000, "User was able to edit a ClassFees using an invalid startDate parameter"
	      assert seniorClassFees.boardingFee != 800000, "User was able to edit a ClassFees using an invalid startDate parameter"
	      
	      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert classAttended.tuitionFee != 70000, "User was able to edit a ClassAttended using an invalid startDate parameter"
	      assert classAttended.boardingFee != 700000, "User was able to edit a ClassAttended using an invalid startDate parameter"
	      
		  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an EnrollmentDocument using an invalid startDate parameter"
	      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit an EnrollmentDocument using an invalid startDate parameter"
	      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using an invalid startDate parameter"
	      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using an invalid startDate parameter"
	      
		  studentDocument = StudentDocument.findByStudentId( "0201aoey" )
	      
	      assert studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an StudentDocument using an invalid startDate parameter"
	      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an StudentDocument using an invalid startDate parameter"
	      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an StudentDocument using an invalid startDate parameter"
	      
	      /* Term Edit Test with a missing endDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "startDate", "Feb 2 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "50000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "60000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "70000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "80000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "500000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "600000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "700000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "800000" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 )
	      assert term.startDate != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit a Term using no endDate parameter."
	      assert term.endDate != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit a Term using no endDate parameter."
	      
	      freshmanClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Freshman", 1, 1901 )
	      
	      assert freshmanClassFees.tuitionFee != 50000, "User was able to edit a ClassFees using no endDate parameter"
	      assert freshmanClassFees.boardingFee != 500000, "User was able to edit a ClassFees using no endDate parameter"
	      
	      sophomoreClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Sophomore", 1, 1901 )
	      
	      assert sophomoreClassFees.tuitionFee != 60000, "User was able to edit a ClassFees using no endDate parameter"
	      assert sophomoreClassFees.boardingFee != 600000, "User was able to edit a ClassFees using no endDate parameter"
	      
	      juniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Junior", 1, 1901 )
	      
	      assert juniorClassFees.tuitionFee != 70000, "User was able to edit a ClassFees using no endDate parameter"
	      assert juniorClassFees.boardingFee != 700000, "User was able to edit a ClassFees using no endDate parameter"
	      
	      seniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Senior", 1, 1901 )
	      
	      assert seniorClassFees.tuitionFee != 80000, "User was able to edit a ClassFees using no endDate parameter"
	      assert seniorClassFees.boardingFee != 800000, "User was able to edit a ClassFees using no endDate parameter"
	      
	      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert classAttended.tuitionFee != 70000, "User was able to edit a ClassAttended using no endDate parameter"
	      assert classAttended.boardingFee != 700000, "User was able to edit a ClassAttended using no endDate parameter"
	      
		  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an EnrollmentDocument using no endDate parameter"
	      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit an EnrollmentDocument using no endDate parameter"
	      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using no endDate parameter"
	      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using no endDate parameter"
	      
		  studentDocument = StudentDocument.findByStudentId( "0201aoey" )
	      
	      assert studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an StudentDocument using no endDate parameter"
	      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an StudentDocument using no endDate parameter"
	      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an StudentDocument using no endDate parameter"
	      
	      /* Term Edit Test with an invalid endDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "startDate", "Feb 2 1901" )
	        .addQueryParam( "endDate", "4/29/1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "50000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "60000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "70000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "80000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "500000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "600000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "700000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "800000" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 )
	      assert term.startDate != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit a Term using an invalid endDate parameter."
	      assert term.endDate != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit a Term using an invalid endDate parameter."
	      
	      freshmanClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Freshman", 1, 1901 )
	      
	      assert freshmanClassFees.tuitionFee != 50000, "User was able to edit a ClassFees using an invalid endDate parameter"
	      assert freshmanClassFees.boardingFee != 500000, "User was able to edit a ClassFees using an invalid endDate parameter"
	      
	      sophomoreClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Sophomore", 1, 1901 )
	      
	      assert sophomoreClassFees.tuitionFee != 60000, "User was able to edit a ClassFees using an invalid endDate parameter"
	      assert sophomoreClassFees.boardingFee != 600000, "User was able to edit a ClassFees using an invalid endDate parameter"
	      
	      juniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Junior", 1, 1901 )
	      
	      assert juniorClassFees.tuitionFee != 70000, "User was able to edit a ClassFees using an invalid endDate parameter"
	      assert juniorClassFees.boardingFee != 700000, "User was able to edit a ClassFees using an invalid endDate parameter"
	      
	      seniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Senior", 1, 1901 )
	      
	      assert seniorClassFees.tuitionFee != 80000, "User was able to edit a ClassFees using an invalid endDate parameter"
	      assert seniorClassFees.boardingFee != 800000, "User was able to edit a ClassFees using an invalid endDate parameter"
	      
	      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert classAttended.tuitionFee != 70000, "User was able to edit a ClassAttended using an invalid endDate parameter"
	      assert classAttended.boardingFee != 700000, "User was able to edit a ClassAttended using an invalid endDate parameter"
	      
		  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an EnrollmentDocument using an invalid endDate parameter"
	      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit an EnrollmentDocument using an invalid endDate parameter"
	      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using an invalid endDate parameter"
	      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using an invalid endDate parameter"
	      
		  studentDocument = StudentDocument.findByStudentId( "0201aoey" )
	      
	      assert studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an StudentDocument using an invalid endDate parameter"
	      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an StudentDocument using an invalid endDate parameter"
	      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an StudentDocument using an invalid endDate parameter"
	      
	      /* Term Edit Test with an invalid date range. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "startDate", "Feb 2 1901" )
	        .addQueryParam( "endDate", "Feb 1 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "50000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "60000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "70000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "80000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "500000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "600000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "700000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "800000" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 )
	      assert term.startDate != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit a Term using an invalid date range."
	      assert term.endDate != Date.parse( "MMM d yy", "Feb 1 1901" ), "User was able to edit a Term using an invalid date range."
	      
	      freshmanClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Freshman", 1, 1901 )
	      
	      assert freshmanClassFees.tuitionFee != 50000, "User was able to edit a ClassFees using an invalid date range"
	      assert freshmanClassFees.boardingFee != 500000, "User was able to edit a ClassFees using an invalid date range"
	      
	      sophomoreClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Sophomore", 1, 1901 )
	      
	      assert sophomoreClassFees.tuitionFee != 60000, "User was able to edit a ClassFees using an invalid date range"
	      assert sophomoreClassFees.boardingFee != 600000, "User was able to edit a ClassFees using an invalid date range"
	      
	      juniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Junior", 1, 1901 )
	      
	      assert juniorClassFees.tuitionFee != 70000, "User was able to edit a ClassFees using an invalid date range"
	      assert juniorClassFees.boardingFee != 700000, "User was able to edit a ClassFees using an invalid date range"
	      
	      seniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Senior", 1, 1901 )
	      
	      assert seniorClassFees.tuitionFee != 80000, "User was able to edit a ClassFees using an invalid date range"
	      assert seniorClassFees.boardingFee != 800000, "User was able to edit a ClassFees using an invalid date range"
	      
	      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert classAttended.tuitionFee != 70000, "User was able to edit a ClassAttended using an invalid date range"
	      assert classAttended.boardingFee != 700000, "User was able to edit a ClassAttended using an invalid date range"
	      
		  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an EnrollmentDocument using an invalid date range parameter"
	      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit an EnrollmentDocument using an invalid date range parameter"
	      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using an invalid date range parameter"
	      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using an invalid date range parameter"
	      
		  studentDocument = StudentDocument.findByStudentId( "0201aoey" )
	      
	      assert studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an StudentDocument using an invalid date range parameter"
	      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an StudentDocument using an invalid date range parameter"
	      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an StudentDocument using an invalid date range parameter"
	      
	      /* Term Edit Test with a missing lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "startDate", "Feb 2 1901" )
	        .addQueryParam( "endDate", "Apr 29 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "50000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "60000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "70000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "80000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "500000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "600000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "700000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "800000" ).toURL() )
	      
	      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 )
	      assert term.startDate != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit a Term using a missing lastUpdateDate parameter."
	      assert term.endDate != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit a Term using a missing lastUpdateDate parameter."
	      
	      freshmanClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Freshman", 1, 1901 )
	      
	      assert freshmanClassFees.tuitionFee != 50000, "User was able to edit a ClassFees using a missing lastUpdateDate parameter"
	      assert freshmanClassFees.boardingFee != 500000, "User was able to edit a ClassFees using a missing lastUpdateDate parameter"
	      
	      sophomoreClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Sophomore", 1, 1901 )
	      
	      assert sophomoreClassFees.tuitionFee != 60000, "User was able to edit a ClassFees using a missing lastUpdateDate parameter"
	      assert sophomoreClassFees.boardingFee != 600000, "User was able to edit a ClassFees using a missing lastUpdateDate parameter"
	      
	      juniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Junior", 1, 1901 )
	      
	      assert juniorClassFees.tuitionFee != 70000, "User was able to edit a ClassFees using a missing lastUpdateDate parameter"
	      assert juniorClassFees.boardingFee != 700000, "User was able to edit a ClassFees using a missing lastUpdateDate parameter"
	      
	      seniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Senior", 1, 1901 )
	      
	      assert seniorClassFees.tuitionFee != 80000, "User was able to edit a ClassFees using a missing lastUpdateDate parameter"
	      assert seniorClassFees.boardingFee != 800000, "User was able to edit a ClassFees using a missing lastUpdateDate parameter"
	      
	      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert classAttended.tuitionFee != 70000, "User was able to edit a ClassAttended using a missing lastUpdateDate parameter"
	      assert classAttended.boardingFee != 700000, "User was able to edit a ClassAttended using a missing lastUpdateDate parameter"
	      
		  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an EnrollmentDocument using a missing lastUpdateDate parameter"
	      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit an EnrollmentDocument using a missing lastUpdateDate parameter"
	      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using a missing lastUpdateDate parameter"
	      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using a missing lastUpdateDate parameter"
	      
		  studentDocument = StudentDocument.findByStudentId( "0201aoey" )
	      
	      assert studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an StudentDocument using a missing lastUpdateDate parameter"
	      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an StudentDocument using a missing lastUpdateDate parameter"
	      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an StudentDocument using a missing lastUpdateDate parameter"
	      
	      /* Term Edit Test with an invalid lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "startDate", "Feb 2 1901" )
	        .addQueryParam( "endDate", "Apr 29 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "50000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "60000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "70000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "80000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "500000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "600000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "700000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "800000" )
	        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 )
	      assert term.startDate != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit a Term using an invalid lastUpdateDate parameter."
	      assert term.endDate != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit a Term using an invalid lastUpdateDate parameter."
	      
	      freshmanClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Freshman", 1, 1901 )
	      
	      assert freshmanClassFees.tuitionFee != 50000, "User was able to edit a ClassFees using an invalid lastUpdateDate parameter"
	      assert freshmanClassFees.boardingFee != 500000, "User was able to edit a ClassFees using an invalid lastUpdateDate parameter"
	      
	      sophomoreClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Sophomore", 1, 1901 )
	      
	      assert sophomoreClassFees.tuitionFee != 60000, "User was able to edit a ClassFees using an invalid lastUpdateDate parameter"
	      assert sophomoreClassFees.boardingFee != 600000, "User was able to edit a ClassFees using an invalid lastUpdateDate parameter"
	      
	      juniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Junior", 1, 1901 )
	      
	      assert juniorClassFees.tuitionFee != 70000, "User was able to edit a ClassFees using an invalid lastUpdateDate parameter"
	      assert juniorClassFees.boardingFee != 700000, "User was able to edit a ClassFees using an invalid lastUpdateDate parameter"
	      
	      seniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Senior", 1, 1901 )
	      
	      assert seniorClassFees.tuitionFee != 80000, "User was able to edit a ClassFees using an invalid lastUpdateDate parameter"
	      assert seniorClassFees.boardingFee != 800000, "User was able to edit a ClassFees using an invalid lastUpdateDate parameter"
	      
	      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert classAttended.tuitionFee != 70000, "User was able to edit a ClassAttended using an invalid lastUpdateDate parameter"
	      assert classAttended.boardingFee != 700000, "User was able to edit a ClassAttended using an invalid lastUpdateDate parameter"
	      
		  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an EnrollmentDocument using an invalid lastUpdateDate parameter"
	      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit an EnrollmentDocument using an invalid lastUpdateDate parameter"
	      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using an invalid lastUpdateDate parameter"
	      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using an invalid lastUpdateDate parameter"
	      
		  studentDocument = StudentDocument.findByStudentId( "0201aoey" )
	      
	      assert studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an StudentDocument using an invalid lastUpdateDate parameter"
	      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an StudentDocument using an invalid lastUpdateDate parameter"
	      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an StudentDocument using an invalid lastUpdateDate parameter"
	    
	      /* Term Edit Test with an invalid tuitionFee parameter */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "startDate", "Feb 2 1901" )
	        .addQueryParam( "endDate", "Apr 29 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "Fifty Thousand" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "60000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "70000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "80000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "500000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "600000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "700000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "800000" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	        
	      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 )
	      assert term.startDate != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit a Term using an invalid tuitionFee parameter."
	      assert term.endDate != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit a Term using an invalid tuitionFee parameter."
	      
	      freshmanClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Freshman", 1, 1901 )
	      
	      assert freshmanClassFees.tuitionFee != "50K", "User was able to edit a ClassFees using an invalid tuitionFee parameter"
	      assert freshmanClassFees.boardingFee != 500000, "User was able to edit a ClassFees using an invalid tuitionFee parameter"
	      
	      sophomoreClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Sophomore", 1, 1901 )
	      
	      assert sophomoreClassFees.tuitionFee != 60000, "User was able to edit a ClassFees using an invalid tuitionFee parameter"
	      assert sophomoreClassFees.boardingFee != 600000, "User was able to edit a ClassFees using an invalid tuitionFee parameter"
	      
	      juniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Junior", 1, 1901 )
	      
	      assert juniorClassFees.tuitionFee != 70000, "User was able to edit a ClassFees using an invalid tuitionFee parameter"
	      assert juniorClassFees.boardingFee != 700000, "User was able to edit a ClassFees using an invalid tuitionFee parameter"
	      
	      seniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Senior", 1, 1901 )
	      
	      assert seniorClassFees.tuitionFee != 80000, "User was able to edit a ClassFees using an invalid tuitionFee parameter"
	      assert seniorClassFees.boardingFee != 800000, "User was able to edit a ClassFees using an invalid tuitionFee parameter"
	      
	      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert classAttended.tuitionFee != 70000, "User was able to edit a ClassAttended using an invalid tuitionFee parameter"
	      assert classAttended.boardingFee != 700000, "User was able to edit a ClassAttended using an invalid tuitionFee parameter"
	      
		  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an EnrollmentDocument using an invalid tuitionFee parameter"
	      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit an EnrollmentDocument using an invalid tuitionFee parameter"
	      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using an invalid tuitionFee parameter"
	      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using an invalid tuitionFee parameter"
	      
		  studentDocument = StudentDocument.findByStudentId( "0201aoey" )
	      
	      assert studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an StudentDocument using an invalid tuitionFee parameter"
	      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an StudentDocument using an invalid tuitionFee parameter"
	      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an StudentDocument using an invalid tuitionFee parameter"
	      
	      /* Term Edit Test with an invalid boardingFee parameter */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "startDate", "Feb 2 1901" )
	        .addQueryParam( "endDate", "Apr 29 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "50000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "60000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "70000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "80000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "500000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "600000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "700000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "Eight Hundred Thousand" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	        
	      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 )
	      assert term.startDate != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit a Term using an invalid boardingFee parameter."
	      assert term.endDate != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit a Term using an invalid boardingFee parameter."
	      
	      freshmanClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Freshman", 1, 1901 )
	      
	      assert freshmanClassFees.boardingFee != 50000, "User was able to edit a ClassFees using an invalid boardingFee parameter"
	      assert freshmanClassFees.boardingFee != 500000, "User was able to edit a ClassFees using an invalid boardingFee parameter"
	      
	      sophomoreClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Sophomore", 1, 1901 )
	      
	      assert sophomoreClassFees.boardingFee != 60000, "User was able to edit a ClassFees using an invalid boardingFee parameter"
	      assert sophomoreClassFees.boardingFee != 600000, "User was able to edit a ClassFees using an invalid boardingFee parameter"
	      
	      juniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Junior", 1, 1901 )
	      
	      assert juniorClassFees.boardingFee != 70000, "User was able to edit a ClassFees using an invalid boardingFee parameter"
	      assert juniorClassFees.boardingFee != 700000, "User was able to edit a ClassFees using an invalid boardingFee parameter"
	      
	      seniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Senior", 1, 1901 )
	      
	      assert seniorClassFees.boardingFee != 80000, "User was able to edit a ClassFees using an invalid boardingFee parameter"
	      assert seniorClassFees.boardingFee != "800K", "User was able to edit a ClassFees using an invalid boardingFee parameter"
	      
	      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert classAttended.tuitionFee != 70000, "User was able to edit a ClassAttended using an invalid boardingFee parameter"
	      assert classAttended.boardingFee != 700000, "User was able to edit a ClassAttended using an invalid boardingFee parameter"
	      
		  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an EnrollmentDocument using an invalid boardingFee parameter"
	      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() != Date.parse( "MMM d yy", "Apr 29 1901" ), "User was able to edit an EnrollmentDocument using an invalid boardingFee parameter"
	      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using an invalid boardingFee parameter"
	      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an EnrollmentDocument using an invalid boardingFee parameter"
	      
		  studentDocument = StudentDocument.findByStudentId( "0201aoey" )
	      
	      assert studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate() != Date.parse( "MMM d yy", "Feb 2 1901" ), "User was able to edit an StudentDocument using an invalid boardingFee parameter"
	      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() != 70000, "User was able to edit an StudentDocument using an invalid boardingFee parameter"
	      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 70000, "User was able to edit an StudentDocument using an invalid boardingFee parameter"
	      
	      /* Term Edit Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "startDate", "Feb 2 1901" )
	        .addQueryParam( "endDate", "Apr 29 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "50000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "60000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "70000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "80000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "500000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "600000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "700000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "800000" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 )
	      
	      lastUpdateDate = term?.lastUpdateDate
	    
	      assert term.startDate == Date.parse( "MMM d yy", "Feb 2 1901" ), "User was unable to edit a Term."
	      assert term.endDate == Date.parse( "MMM d yy", "Apr 29 1901" ), "User was unable to edit a Term."
	      
	      freshmanClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Freshman", 1, 1901 )
	      
	      assert freshmanClassFees.tuitionFee == 50000, "User was unable to edit a ClassFees"
	      assert freshmanClassFees.boardingFee == 500000, "User was unable to edit a ClassFees"
	      
	      sophomoreClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Sophomore", 1, 1901 )
	      
	      assert sophomoreClassFees.tuitionFee == 60000, "User was unable to edit a ClassFees"
	      assert sophomoreClassFees.boardingFee == 600000, "User was unable to edit a ClassFees"
	      
	      juniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Junior", 1, 1901 )
	      
	      assert juniorClassFees.tuitionFee == 70000, "User was unable to edit a ClassFees"
	      assert juniorClassFees.boardingFee == 700000, "User was unable to edit a ClassFees"
	      
	      seniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Senior", 1, 1901 )
	      
	      assert seniorClassFees.tuitionFee == 80000, "User was unable to edit a ClassFees"
	      assert seniorClassFees.boardingFee == 800000, "User was unable to edit a ClassFees"
	      
	      classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert classAttended.tuitionFee == 70000, "User was unable to edit a ClassAttended"
	      assert classAttended.boardingFee == 700000, "User was unable to edit a ClassAttended"
	      
		  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0201aoey", "Saint John's College MN", 1, 1901 )
	      
	      assert enrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() == Date.parse( "MMM d yy", "Feb 2 1901" ), "User was unable to edit an EnrollmentDocument"
	      assert enrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() == Date.parse( "MMM d yy", "Apr 29 1901" ), "User was unable to edit an EnrollmentDocument"
	      assert enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() == 70000, "User was unable to edit an EnrollmentDocument"
	      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 70000, "User was unable to edit an EnrollmentDocument"
	      
		  studentDocument = StudentDocument.findByStudentId( "0201aoey" )
	      
	      assert studentDocument.getOnlyField( "lastEnrollmentTermStartDate" ).getDate() == Date.parse( "MMM d yy", "Feb 2 1901" ), "User was unable to edit a StudentDocument"
	      assert studentDocument.getOnlyField( "tuitionFees" ).getNumber() == 70000, "User was unable to edit a StudentDocument"
	      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 70000, "User was unable to edit a StudentDocument"
	    
	      /* Overlapping Term Edit Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 2 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "May 1 1901" )
	        .addQueryParam( "endDate", "Jul 31 1901" ).toURL() )
	      
	      termCount = Term.list().size()
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "startDate", "Apr 30 1901" )
	        .addQueryParam( "endDate", "May 15 1901" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      assert termCount == Term.list().size(), "User was able to edit a Term into an Overlapping Term."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "startDate", "May 15 1901" )
	        .addQueryParam( "endDate", "Jul 15 1901" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      assert termCount == Term.list().size(), "User was able to edit a Term into an Overlapping Term."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "startDate", "Jul 15 1901" )
	        .addQueryParam( "endDate", "Aug 15 1901" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      assert termCount == Term.list().size(), "User was able to edit a Term into an Overlapping Term."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "startDate", "Apr 15 1901" )
	        .addQueryParam( "endDate", "Aug 15 1901" ).toURL() )
	      
	      assert termCount == Term.list().size(), "User was able to edit a Term into an Overlapping Term."
	    
	      studentDocument = StudentDocument.findByStudentId( "0201aoey" )
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", studentDocument.getId() )
	        .addQueryParam( "studentId", "0201aoey" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", studentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	    
	      /* Restore the Admin Privilege of the current user. */
	      urfUser?.delete()
	      urfUser = null
	      urfUserBackup?.save()
	    %>
	    <p>Term Edit Test is successful.</p>
	  </body>
	</html>
<% } %>