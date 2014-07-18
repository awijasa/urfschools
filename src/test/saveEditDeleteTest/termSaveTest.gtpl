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
	    <title>Term Save Test</title>
	  </head>
	  <body>
	    <%
	      Entity classAttended
	      Entity freshmanClassFees
	      Entity juniorClassFees
	      Entity seniorClassFees
	      Entity sophomoreClassFees
	      Entity term
	      
	      /* For the Save Test, make sure that the Terms to create have not existed. */
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) == null, "Term: { termSchool: Saint John's College MN, termNo: 1, year: 1901 } has existed. Please change the test case."
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 2, 1901 ) == null, "Term: { termSchool: Saint John's College MN, termNo: 2, year: 1901 } has existed. Please change the test case."
	      
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
	      
	      /* Save a School for the Term to be saved. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "newClass1", "Freshman" )
	        .addQueryParam( "newClass2", "Sophomore" )
	        .addQueryParam( "newClass3", "Junior" )
	        .addQueryParam( "newClass4", "Senior" )
	        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
	    
	      /* Term Save Test with a missing action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "Feb 1 1901" )
	        .addQueryParam( "endDate", "Apr 30 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "10000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "20000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "30000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "40000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "100000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "200000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "300000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "400000" ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) == null, "User was able to save a Term using no action parameter."
	      assert ClassFees.findBySchoolName( "Saint John's College MN" ).size() == 0, "User was able to save a ClassFees using no action parameter"
	    
	      /* Term Save Test with an invalid action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "create" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "Feb 1 1901" )
	        .addQueryParam( "endDate", "Apr 30 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "10000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "20000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "30000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "40000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "100000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "200000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "300000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "400000" ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) == null, "User was able to save a Term using an invalid action parameter."
	      assert ClassFees.findBySchoolName( "Saint John's College MN" ).size() == 0, "User was able to save a ClassFees using an invalid action parameter"
	      
	      /* Term Save Test with a missing termSchool parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "Feb 1 1901" )
	        .addQueryParam( "endDate", "Apr 30 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "10000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "20000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "30000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "40000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "100000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "200000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "300000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "400000" ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) == null, "User was able to save a Term using no termSchool parameter."
	      assert ClassFees.findBySchoolName( "Saint John's College MN" ).size() == 0, "User was able to save a ClassFees using no termSchool parameter"
	      
	      /* Term Save Test with a missing termNo parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "Feb 1 1901" )
	        .addQueryParam( "endDate", "Apr 30 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "10000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "20000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "30000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "40000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "100000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "200000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "300000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "400000" ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) == null, "User was able to save a Term using no termNo parameter."
	      assert ClassFees.findBySchoolName( "Saint John's College MN" ).size() == 0, "User was able to save a ClassFees using no termNo parameter"
	      
	      /* Term Save Test with an invalid termNo parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", "one" )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "Feb 1 1901" )
	        .addQueryParam( "endDate", "Apr 30 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "10000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "20000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "30000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "40000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "100000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "200000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "300000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "400000" ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", "one", 1901 ) == null, "User was able to save a Term using an invalid termNo parameter."
	      assert ClassFees.findBySchoolName( "Saint John's College MN" ).size() == 0, "User was able to save a ClassFees using an invalid termNo parameter"
	    
	      /* Term Save Test with a missing year parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "startDate", "Feb 1 1901" )
	        .addQueryParam( "endDate", "Apr 30 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "10000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "20000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "30000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "40000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "100000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "200000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "300000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "400000" ).toURL() )
	      assert Term.findByTermSchoolAndDate( "Saint John's College MN", Date.parse( "MMM d yy", "Feb 1 1901" ) ) == null, "User was able to save a Term using no year parameter."
	      assert ClassFees.findBySchoolName( "Saint John's College MN" ).size() == 0, "User was able to save a ClassFees using no year parameter"
	    
	      /* Term Save Test with an invalid year parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "year", "Nineteen O One" )
	        .addQueryParam( "startDate", "Feb 1 1901" )
	        .addQueryParam( "endDate", "Apr 30 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "10000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "20000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "30000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "40000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "100000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "200000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "300000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "400000" ).toURL() )
	      assert Term.findByTermSchoolAndDate( "Saint John's College MN", Date.parse( "MMM d yy", "Feb 1 1901" ) ) == null, "User was able to save a Term using an invalid year parameter."
	      assert ClassFees.findBySchoolName( "Saint John's College MN" ).size() == 0, "User was able to save a ClassFees using an invalid year parameter"
	      
	      /* Term Save Test with a missing startDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "endDate", "Apr 30 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "10000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "20000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "30000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "40000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "100000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "200000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "300000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "400000" ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) == null, "User was able to save a Term using no startDate parameter."
	      assert ClassFees.findBySchoolName( "Saint John's College MN" ).size() == 0, "User was able to save a ClassFees using no startDate parameter"
	    
	      /* Term Save Test with an invalid startDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "2/1/1901" )
	        .addQueryParam( "endDate", "Apr 30 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "10000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "20000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "30000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "40000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "100000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "200000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "300000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "400000" ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) == null, "User was able to save a Term using an invalid startDate parameter."
	      assert ClassFees.findBySchoolName( "Saint John's College MN" ).size() == 0, "User was able to save a ClassFees using an invalid startDate parameter"
	      
	      /* Term Save Test with a missing endDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "Feb 1 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "10000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "20000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "30000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "40000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "100000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "200000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "300000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "400000" ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) == null, "User was able to save a Term using no endDate parameter."
	      assert ClassFees.findBySchoolName( "Saint John's College MN" ).size() == 0, "User was able to save a ClassFees using no endDate parameter"
	    
	      /* Term Save Test with an invalid endDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "Feb 1 1901" )
	        .addQueryParam( "endDate", "4/30/1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "10000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "20000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "30000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "40000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "100000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "200000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "300000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "400000" ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) == null, "User was able to save a Term using an invalid endDate parameter."
	      assert ClassFees.findBySchoolName( "Saint John's College MN" ).size() == 0, "User was able to save a ClassFees using an invalid endDate parameter"
	      
	      /* Term Save Test with an invalid date range. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "Feb 1 1901" )
	        .addQueryParam( "endDate", "Jan 31 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "10000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "20000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "30000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "40000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "100000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "200000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "300000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "400000" ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) == null, "User was able to save a Term using an invalid date range."
	      assert ClassFees.findBySchoolName( "Saint John's College MN" ).size() == 0, "User was able to save a ClassFees using an invalid date range"
	    
	      /* Term Save Test with an invalid tuitionFee */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "Feb 1 1901" )
	        .addQueryParam( "endDate", "Apr 30 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "10000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "Twenty Thousand" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "30000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "40000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "100000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "200000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "300000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "400000" ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) == null, "User was able to save a Term using an invalid tuitionFee."
	      assert ClassFees.findBySchoolName( "Saint John's College MN" ).size() == 0, "User was able to save a ClassFees using an invalid tuitionFee"
	      
	      /* Term Save Test with an invalid boardingFee */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "Feb 1 1901" )
	        .addQueryParam( "endDate", "Apr 30 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "10000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "20000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "30000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "40000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "One Hundred Thousand" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "200000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "300000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "400000" ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) == null, "User was able to save a Term using an invalid boardingFee."
	      assert ClassFees.findBySchoolName( "Saint John's College MN" ).size() == 0, "User was able to save a ClassFees using an invalid boardingFee"
	      
	      /* Term Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "Feb 1 1901" )
	        .addQueryParam( "endDate", "Apr 30 1901" )
	        .addQueryParam( "tuitionFeeSaint John's College MNFreshman", "10000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSophomore", "20000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNJunior", "30000" )
	        .addQueryParam( "tuitionFeeSaint John's College MNSenior", "40000" )
	        .addQueryParam( "boardingFeeSaint John's College MNFreshman", "100000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSophomore", "200000" )
	        .addQueryParam( "boardingFeeSaint John's College MNJunior", "300000" )
	        .addQueryParam( "boardingFeeSaint John's College MNSenior", "400000" ).toURL() )
	      
	      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 )
	      
	      Date lastUpdateDate = new Date( term?.lastUpdateDate?.getTime() )
	      
	      assert term != null, "User was unable to save a Term."
	      assert term.termSchool == "Saint John's College MN", "School: ${ term.termSchool } from Term.findByTermSchoolAndTermNoAndYear( def termSchool, def termNo, def year ) is not equal to Saint John's College MN."
	      assert term.termNo == 1, "Term No: ${ term.termNo } from Term.findByTermSchoolAndTermNoAndYear( def termSchool, def termNo, def year ) is not equal to 1."
	      assert term.year == 1901, "Year: ${ term.year } from Term.findByTermSchoolAndTermNoAndYear( def termSchool, def termNo, def year ) is not equal to 1901."
	      assert term.startDate == Date.parse( "MMM d yy", "Feb 1 1901" ), "Start Date: ${ term.startDate.format( "MMM d yy" ) } from Term.findByTermSchoolAndTermNoAndYear( def termSchool, def termNo, def year ) is not equal to Feb 1 1901."
	      assert term.endDate == Date.parse( "MMM d yy", "Apr 30 1901" ), "End Date: ${ term.endDate.format( "MMM d yy" ) } from Term.findByTermSchoolAndTermNoAndYear( def termSchool, def termNo, def year ) is not equal to Apr 30 1901."
	      assert term?.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ term?.lastUpdateDate.clearTime() } from Term.findByTermSchoolAndTermNoAndYear( def termSchool, def termNo, def year ) is not equal to ${ new Date().clearTime() }."
	      
	      freshmanClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Freshman", 1, 1901 )
	      
	      assert freshmanClassFees != null, "User was unable to save a Freshman ClassFees"
	      assert freshmanClassFees.schoolName == "Saint John's College MN", "School Name: ${ freshmanClassFees.schoolName } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to Saint John's College MN"
	      assert freshmanClassFees.getProperty( "class" ) == "Freshman", "Class: ${ freshmanClassFees.getProperty( "class" ) } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to Freshman"
	      assert freshmanClassFees.classLevel == 1, "Class Level: ${ freshmanClassFees.classLevel } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 1"
	      assert freshmanClassFees.termNo == 1, "Term No: ${ freshmanClassFees.termNo } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 1"
	      assert freshmanClassFees.termYear == 1901, "Year: ${ freshmanClassFees.termYear } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 1901"
	      assert freshmanClassFees.tuitionFee == 10000, "Tuition Fee: ${ freshmanClassFees.tuitionFee } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 10000"
	      assert freshmanClassFees.boardingFee == 100000, "Boarding Fee: ${ freshmanClassFees.boardingFee } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 100000"
	      assert freshmanClassFees?.lastUpdateDate?.clearTime() == new Date().clearTime(), "Last Update Date: ${ freshmanClassFees?.lastUpdateDate?.clearTime() } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to ${ new Date().clearTime() }"
	      
	      sophomoreClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Sophomore", 1, 1901 )
	      
	      assert sophomoreClassFees != null, "User was unable to save a Sophomore ClassFees"
	      assert sophomoreClassFees.schoolName == "Saint John's College MN", "School Name: ${ sophomoreClassFees.schoolName } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to Saint John's College MN"
	      assert sophomoreClassFees.getProperty( "class" ) == "Sophomore", "Class: ${ sophomoreClassFees.getProperty( "class" ) } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to Sophomore"
	      assert sophomoreClassFees.classLevel == 2, "Class Level: ${ sophomoreClassFees.classLevel } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 2"
	      assert sophomoreClassFees.termNo == 1, "Term No: ${ sophomoreClassFees.termNo } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 1"
	      assert sophomoreClassFees.termYear == 1901, "Year: ${ sophomoreClassFees.termYear } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 1901"
	      assert sophomoreClassFees.tuitionFee == 20000, "Tuition Fee: ${ sophomoreClassFees.tuitionFee } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 20000"
	      assert sophomoreClassFees.boardingFee == 200000, "Boarding Fee: ${ sophomoreClassFees.boardingFee } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 200000"
	      assert sophomoreClassFees?.lastUpdateDate?.clearTime() == new Date().clearTime(), "Last Update Date: ${ sophomoreClassFees?.lastUpdateDate?.clearTime() } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to ${ new Date().clearTime() }"
	      
	      juniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Junior", 1, 1901 )
	      
	      assert juniorClassFees != null, "User was unable to save a Junior ClassFees"
	      assert juniorClassFees.schoolName == "Saint John's College MN", "School Name: ${ juniorClassFees.schoolName } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to Saint John's College MN"
	      assert juniorClassFees.getProperty( "class" ) == "Junior", "Class: ${ juniorClassFees.getProperty( "class" ) } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to Junior"
	      assert juniorClassFees.classLevel == 3, "Class Level: ${ juniorClassFees.classLevel } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 3"
	      assert juniorClassFees.termNo == 1, "Term No: ${ juniorClassFees.termNo } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 1"
	      assert juniorClassFees.termYear == 1901, "Year: ${ juniorClassFees.termYear } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 1901"
	      assert juniorClassFees.tuitionFee == 30000, "Tuition Fee: ${ juniorClassFees.tuitionFee } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 30000"
	      assert juniorClassFees.boardingFee == 300000, "Boarding Fee: ${ juniorClassFees.boardingFee } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 300000"
	      assert juniorClassFees?.lastUpdateDate?.clearTime() == new Date().clearTime(), "Last Update Date: ${ juniorClassFees?.lastUpdateDate?.clearTime() } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to ${ new Date().clearTime() }"
	      
	      seniorClassFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( "Saint John's College MN", "Senior", 1, 1901 )
	      
	      assert seniorClassFees != null, "User was unable to save a Senior ClassFees"
	      assert seniorClassFees.schoolName == "Saint John's College MN", "School Name: ${ seniorClassFees.schoolName } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to Saint John's College MN"
	      assert seniorClassFees.getProperty( "class" ) == "Senior", "Class: ${ seniorClassFees.getProperty( "class" ) } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to Senior"
	      assert seniorClassFees.classLevel == 4, "Class Level: ${ seniorClassFees.classLevel } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 4"
	      assert seniorClassFees.termNo == 1, "Term No: ${ seniorClassFees.termNo } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 1"
	      assert seniorClassFees.termYear == 1901, "Year: ${ seniorClassFees.termYear } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 1901"
	      assert seniorClassFees.tuitionFee == 40000, "Tuition Fee: ${ seniorClassFees.tuitionFee } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 40000"
	      assert seniorClassFees.boardingFee == 400000, "Boarding Fee: ${ seniorClassFees.boardingFee } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to 400000"
	      assert seniorClassFees?.lastUpdateDate?.clearTime() == new Date().clearTime(), "Last Update Date: ${ seniorClassFees?.lastUpdateDate?.clearTime() } from ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) is not equal to ${ new Date().clearTime() }"
	      
	      Integer termCount = Term.list().size()
	      
	      /* Term Duplicate Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "Feb 1 1901" )
	        .addQueryParam( "endDate", "Apr 30 1901" ).toURL() )
	      
	      assert termCount == Term.list().size(), "User was able to save a Duplicate Term."
	      
	      /* Overlapping Term Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 2 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "Jan 31 1901" )
	        .addQueryParam( "endDate", "Feb 15 1901" ).toURL() )
	      
	      assert termCount == Term.list().size(), "User was able to save an Overlapping Term."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 2 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "Feb 15 1901" )
	        .addQueryParam( "endDate", "Apr 15 1901" ).toURL() )
	      
	      assert termCount == Term.list().size(), "User was able to save an Overlapping Term."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 2 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "Feb 15 1901" )
	        .addQueryParam( "endDate", "May 15 1901" ).toURL() )
	      
	      assert termCount == Term.list().size(), "User was able to save an Overlapping Term."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 2 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "startDate", "Jan 15 1901" )
	        .addQueryParam( "endDate", "May 15 1901" ).toURL() )
	      
	      assert termCount == Term.list().size(), "User was able to save an Overlapping Term."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "firstName", "Adrian" )
	        .addQueryParam( "lastName", "Oey" )
	        .addQueryParam( "enrollmentSchool", "Saint John's College MN" )
	        .addQueryParam( "enrollmentTermSchool1", "1901 Term 1" )
	        .addQueryParam( "leaveTermSchool1", "1901 Term 1" )
	        .addQueryParam( "enrollmentFirstClassAttendedSchool1", "Junior" )
	        .addQueryParam( "enrollmentLastClassAttendedSchool1", "Junior" )
	        .addQueryParam( "classAttended1901 Term 1", "Junior" ).toURL() )
	    
	      /* Restore the Admin Privilege of the current user. */
	      urfUser?.delete()
	      urfUser = null
	      urfUserBackup?.save()
	    %>
	    <p>Term Save Test is successful.</p>
	  </body>
	</html>
<% } %>