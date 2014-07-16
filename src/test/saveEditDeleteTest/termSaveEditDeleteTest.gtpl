<%
  import com.google.appengine.api.datastore.Entity
  import data.ClassAttended
  import data.ClassFees
  import data.School
  import data.StudentWithLastEnrollment
  import data.Term
  import data.URFUser
  import data.UserPrivilege
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
  else {
%>
	<html>
	  <head>
	    <title>Term Save, Edit, and Delete Test</title>
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
	    
	      Entity studentWithLastEnrollment = StudentWithLastEnrollment.findByStudentId( "0201aoey" )
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/StudentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", studentWithLastEnrollment.getKey().getId() )
	        .addQueryParam( "studentId", "0201aoey" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", studentWithLastEnrollment.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      /* School Delete Test. */
	      
	      Entity school = School.findByName( "Saint John's College MN" )
	    
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", school.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", school?.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert School.findByName( "Saint John's College MN" ) != null, "User was able to delete a School that is still used by a Term."
	      
	      /* Term Delete Test with a missing action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) != null, "User was able to delete a Term using no action parameter."
	      assert ClassFees.findBySchoolNameAndTermNoAndTermYear( "Saint John's College MN", 1, 1901 ).size() > 0, "User was able to delete ClassFees using no action parameter"
	      
	      /* Term Delete Test with an invalid action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "remove" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) != null, "User was able to delete a Term using an invalid action parameter."
	      assert ClassFees.findBySchoolNameAndTermNoAndTermYear( "Saint John's College MN", 1, 1901 ).size() > 0, "User was able to delete ClassFees using an invalid action parameter"
	      
	      /* Term Delete Test with no id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "termNo", 1 )
	        .addQueryParam( "year", 1901 )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) != null, "User was able to delete a Term using no id parameter."
	      assert ClassFees.findBySchoolNameAndTermNoAndTermYear( "Saint John's College MN", 1, 1901 ).size() > 0, "User was able to delete ClassFees using no id parameter"
	      
	      /* Term Delete Test with an invalid id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", "-1" )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) != null, "User was able to delete a Term using an invalid id parameter."
	      assert ClassFees.findBySchoolNameAndTermNoAndTermYear( "Saint John's College MN", 1, 1901 ).size() > 0, "User was able to delete ClassFees using an invalid id parameter"
	      
	      /* Term Delete Test with no nextTwentyOffset parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) != null, "User was able to delete a Term using no nextTwentyOffset parameter."
	      assert ClassFees.findBySchoolNameAndTermNoAndTermYear( "Saint John's College MN", 1, 1901 ).size() > 0, "User was able to delete ClassFees using no nextTwentyOffset parameter"
	      
	      /* Term Delete Test with no lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "nextTwentyOffset", 20 ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) != null, "User was able to delete a Term using no lastUpdateDate parameter."
	      assert ClassFees.findBySchoolNameAndTermNoAndTermYear( "Saint John's College MN", 1, 1901 ).size() > 0, "User was able to delete ClassFees using no lastUpdateDate parameter"
	      
	      /* Term Delete Test with an invalid lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) != null, "User was able to delete a Term using an invalid lastUpdateDate parameter."
	      assert ClassFees.findBySchoolNameAndTermNoAndTermYear( "Saint John's College MN", 1, 1901 ).size() > 0, "User was able to delete ClassFees using an invalid lastUpdateDate parameter"
	    
	      /* Term Delete Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 1, 1901 ) == null, "User was unable to delete the Term: { termSchool: Saint John's College MN, termNo: 1, year: 1901 }."
	      assert ClassFees.findBySchoolNameAndTermNoAndTermYear( "Saint John's College MN", 1, 1901 ).size() == 0, "User was unable to delete ClassFees"
	      
	      term = Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 2, 1901 )
	    
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/TermController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", term.getKey().getId() )
	        .addQueryParam( "termSchool", "Saint John's College MN" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", term?.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Term.findByTermSchoolAndTermNoAndYear( "Saint John's College MN", 2, 1901 ) == null, "User was unable to delete the Term: { termSchool: Saint John's College MN, termNo: 2, year: 1901 }."
	      
	      /* Delete the School created for the Term Save, Edit, and Delete Test. */
	      
	      school = School.findByName( "Saint John's College MN" )
	    
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", school.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", school?.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	    
	      /* Restore the Admin Privilege of the current user. */
	      urfUser?.delete()
	      urfUser = null
	      urfUserBackup?.save()
	    %>
	    <p>Term Save, Edit, and Delete Test is successful.</p>
	  </body>
	</html>
<% } %>