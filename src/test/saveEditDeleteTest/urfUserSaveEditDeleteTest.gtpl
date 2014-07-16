<%
  import com.google.appengine.api.datastore.Entity
  import data.School
  import data.URFUser
  import data.UserPrivilege
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
  else {
%>
	<html>
	  <head>
	    <title>URFUser Save, Edit, and Delete Test</title>
	  </head>
	  <body>
	    <%
	      def mockURFUser
	      def userPrivileges
	      
	      /* For the Save Test, make sure that the URFUser, UserPrivileges, and Schools to create have not existed. */
	      assert URFUser.findByEmail( "awijasa@xmail.com" ) == null, "URFUser: awijasa@xmail.com has existed. Please change the test case."
	      assert UserPrivilege.findByUserEmail( "awijasa@xmail.com" ).size() == 0, "awijasa@xmail.com's UserPrivilege has existed. Please change the test case."
	      assert School.findByName( "College of Saint Benedict MN" ) == null, "School: College of Saint Benedict MN has existed. Please change the test case."
	      assert School.findByName( "Saint John's College MN" ) == null, "School: Saint John's College MN has existed. Please change the test case."
	    
	      /* Give the testing user an Admin Read privilege. */
	      def urfUser = URFUser.findByEmail( user.getEmail() )
	      def urfUserBackup = URFUser.findByEmail( user.getEmail() )
	      
	      if( urfUser == null )
	        urfUser = new Entity( "URFUser" )
	        
	      urfUser.email = user.getEmail()
	      
	      if( urfUser.adminPrivilege != "Modify" )
	      	urfUser.adminPrivilege = "Read"
	      
	      urfUser.lastUpdateDate = new Date()
	      urfUser.lastUpdateUser = user.getEmail()
	      urfUser.save()
	      
	      /* Save Schools for the UserPrivileges to be saved. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "schoolName", "College of Saint Benedict MN" )
	        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
	    
	      /* URFUser Save Test with a missing action parameter. */
	      def urfUserControllerURIBuilder = new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "email", "awijasa@xmail.com" )
	        .addQueryParam( "sponsorDataPrivilege", "None" )
	        .addQueryParam( "adminPrivilege", "Read" )
	        
	      def schoolList = School.list()
	      def assignUserPrivilegesToSchools = { userPrivilegeSchool ->
	        if( userPrivilegeSchool.name.equals( "College of Saint Benedict MN" ) )
	          urfUserControllerURIBuilder.addQueryParam( "${ userPrivilegeSchool.getKey().getId() }Privilege", "Read" )
	        else
	          urfUserControllerURIBuilder.addQueryParam( "${ userPrivilegeSchool.getKey().getId() }Privilege", "None" )
	      }
	      
	      schoolList.each( assignUserPrivilegesToSchools )
	    
	      urlFetch.fetch( urfUserControllerURIBuilder.toURL() )
	      assert URFUser.findByEmail( "awijasa@xmail.com" ) == null, "User was able to save a URFUser using no action parameter."
	      assert UserPrivilege.findByUserEmail( "awijasa@xmail.com" ).size() == 0, "User was able to save a UserPrivilege using no action parameter."
	    
	      /* URFUser Save Test with an invalid action parameter. */
	      urfUserControllerURIBuilder = new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "action", "create" )
	        .addQueryParam( "email", "awijasa@xmail.com" )
	        .addQueryParam( "sponsorDataPrivilege", "None" )
	        .addQueryParam( "adminPrivilege", "Read" )
	        
	      schoolList.each( assignUserPrivilegesToSchools )
	      
	      urlFetch.fetch( urfUserControllerURIBuilder.toURL() )
	      assert URFUser.findByEmail( "awijasa@xmail.com" ) == null, "User was able to save a URFUser using an invalid action parameter."
	      assert UserPrivilege.findByUserEmail( "awijasa@xmail.com" ).size() == 0, "User was able to save a UserPrivilege using an invalid action parameter."
	      
	      /* URFUser Save Test with a missing email parameter. */
	      def urfUserCount = URFUser.list().size()
	      def userPrivilegeCount = UserPrivilege.list().size()
	    
	      urfUserControllerURIBuilder = new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "sponsorDataPrivilege", "None" )
	        .addQueryParam( "adminPrivilege", "Read" )
	        
	      schoolList.each( assignUserPrivilegesToSchools )
	      
	      urlFetch.fetch( urfUserControllerURIBuilder.toURL() )
	      assert URFUser.list().size() == urfUserCount, "User was able to save a URFUser using no email parameter."
	      assert UserPrivilege.list().size() == userPrivilegeCount, "User was able to save a UserPrivilege using no email parameter."
	      
	      /* URFUser Save Test. */
	      urfUserControllerURIBuilder = new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "email", "awijasa@xmail.com" )
	        .addQueryParam( "sponsorDataPrivilege", "None" )
	        .addQueryParam( "adminPrivilege", "Read" )
	        
	      schoolList.each( assignUserPrivilegesToSchools )
	      
	      urlFetch.fetch( urfUserControllerURIBuilder.toURL() )
	      
	      mockURFUser = URFUser.findByEmail( "awijasa@xmail.com" )
	      userPrivileges = UserPrivilege.findByUserEmail( "awijasa@xmail.com" )
	      
	      Date lastUpdateDate = new Date( mockURFUser.lastUpdateDate.getTime() )
	      
	      assert mockURFUser != null, "User was unable to save a URFUser."
	      assert mockURFUser.email == "awijasa@xmail.com", "Email: ${ mockURFUser.email } from URFUser.findByEmail( def email ) is not equal to awijasa@xmail.com."
	      assert mockURFUser.sponsorDataPrivilege == null, "Sponsor Data Privilege: ${ mockURFUser.sponsorDataPrivilege } from URFUser.findByEmail( def email ) is not equal to null."
	      assert mockURFUser.adminPrivilege == "Read", "Admin Privilege: ${ mockURFUser.adminPrivilege } from URFUser.findByEmail( def email ) is not equal to Read."
	      assert mockURFUser.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ mockURFUser.lastUpdateDate.clearTime() } from URFUser.findByEmail( def email ) is not equal to ${ new Date().clearTime() }."
	      assert userPrivileges.size() == 1, "User School Privileges size: ${ userPrivileges.size() } from UserPrivilege.findByUserEmail( def userEmail ).size() is not equal to 1."
	      assert userPrivileges.first().userEmail == "awijasa@xmail.com", "User Privilege Email: ${ userPrivileges.first().userEmail } from UserPrivilege.findByUserEmail( def userEmail ).first() is not equal to awijasa@xmail.com."
	      assert userPrivileges.first().schoolName == "College of Saint Benedict MN", "User Privilege School: ${ userPrivileges.first().schoolName } from UserPrivilege.findByUserEmail( def userEmail ).first() is not equal to College of Saint Benedict MN."
	      assert userPrivileges.first().privilege == "Read", "User Privilege: ${ userPrivileges.first().privilege } from UserPrivilege.findByUserEmail( def userEmail ).first() is not equal to Read."
	      assert userPrivileges.first().lastUpdateDate.clearTime() == new Date().clearTime(), "User Privilege Last Update Date: ${ userPrivileges.first().lastUpdateDate.clearTime() } from UserPrivilege.findByUserEmail( def userEmail ).first() is not equal to ${ new Date().clearTime() }."
	    
	      urfUserCount = URFUser.list().size()
	      userPrivilegeCount = UserPrivilege.list().size()
	      
	      /* URFUser Duplicate Save Test. */
	      urfUserControllerURIBuilder = new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "email", "awijasa@xmail.com" )
	        .addQueryParam( "sponsorDataPrivilege", "None" )
	        .addQueryParam( "adminPrivilege", "Read" )
	        
	      schoolList.each( assignUserPrivilegesToSchools )
	      
	      urlFetch.fetch( urfUserControllerURIBuilder.toURL() )
	      
	      assert urfUserCount == URFUser.list().size(), "User was able to save a Duplicate URFUser."
	      assert userPrivilegeCount == UserPrivilege.list().size(), "User was able to save a Duplicate UserPrivilege."
	      
	      /* URFUser Edit Test with a missing action parameter. */
	      urfUserControllerURIBuilder = new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "id", mockURFUser.getKey().getId() )
	        .addQueryParam( "sponsorDataPrivilege", "Read" )
	        .addQueryParam( "adminPrivilege", "None" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
	        
	      def editUserPrivileges = { userPrivilegeSchool ->
	        if( userPrivilegeSchool.name.equals( "Saint John's College MN" ) )
	          urfUserControllerURIBuilder.addQueryParam( "${ userPrivilegeSchool.getKey().getId() }Privilege", "Read" )
	        else
	          urfUserControllerURIBuilder.addQueryParam( "${ userPrivilegeSchool.getKey().getId() }Privilege", "None" )
	      }
	    
	      schoolList.each( editUserPrivileges )
	      
	      urlFetch.fetch( urfUserControllerURIBuilder.toURL() )
	      
	      mockURFUser = URFUser.findByEmail( "awijasa@xmail.com" )
	      userPrivilege = UserPrivilege.findByUserEmail( "awijasa@xmail.com" )
	      assert mockURFUser.sponsorDataPrivilege != "Read", "User was able to edit a URFUser using a missing action parameter."
	      assert mockURFUser.adminPrivilege != null, "User was able to edit a URFUser using a missing action parameter."
	      assert userPrivilege.size() == 1, "User was able to edit UserPrivileges using a missing action parameter."
	      assert userPrivilege.first().schoolName != "Saint John's College MN", "User was able to edit UserPrivileges using a missing action parameter."
	      assert userPrivilege.first().privilege == "Read", "User was able to edit UserPrivileges using a missing action parameter."
	      
	      /* URFUser Edit Test with an invalid action parameter. */
	      urfUserControllerURIBuilder = new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "action", "modify" )
	        .addQueryParam( "id", mockURFUser.getKey().getId() )
	        .addQueryParam( "sponsorDataPrivilege", "Read" )
	        .addQueryParam( "adminPrivilege", "None" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
	        
	      schoolList.each( editUserPrivileges )
	      
	      urlFetch.fetch( urfUserControllerURIBuilder.toURL() )
	      
	      mockURFUser = URFUser.findByEmail( "awijasa@xmail.com" )
	      userPrivilege = UserPrivilege.findByUserEmail( "awijasa@xmail.com" )
	      assert mockURFUser.sponsorDataPrivilege != "Read", "User was able to edit a URFUser using an invalid action parameter."
	      assert mockURFUser.adminPrivilege != null, "User was able to edit a URFUser using an invalid action parameter."
	      assert userPrivilege.size() == 1, "User was able to edit UserPrivileges using an invalid action parameter."
	      assert userPrivilege.first().schoolName != "Saint John's College MN", "User was able to edit UserPrivileges using an invalid action parameter."
	      assert userPrivilege.first().privilege == "Read", "User was able to edit UserPrivileges using an invalid action parameter."
	      
	      /* URFUser Edit Test with a missing id parameter. */
	      urfUserControllerURIBuilder = new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "email", "awijasa@xmail.com" )
	        .addQueryParam( "sponsorDataPrivilege", "Read" )
	        .addQueryParam( "adminPrivilege", "None" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
	        
	      schoolList.each( editUserPrivileges )
	      
	      urlFetch.fetch( urfUserControllerURIBuilder.toURL() )
	      
	      mockURFUser = URFUser.findByEmail( "awijasa@xmail.com" )
	      userPrivilege = UserPrivilege.findByUserEmail( "awijasa@xmail.com" )
	      assert mockURFUser.sponsorDataPrivilege != "Read", "User was able to edit a URFUser using no id parameter."
	      assert mockURFUser.adminPrivilege != null, "User was able to edit a URFUser using no id parameter."
	      assert userPrivilege.size() == 1, "User was able to edit UserPrivileges using no id parameter."
	      assert userPrivilege.first().schoolName != "Saint John's College MN", "User was able to edit UserPrivileges using no id parameter."
	      assert userPrivilege.first().privilege == "Read", "User was able to edit UserPrivileges using no id parameter."
	      
	      /* URFUser Edit Test with an invalid id parameter. */
	      urfUserControllerURIBuilder = new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", "-1" )
	        .addQueryParam( "sponsorDataPrivilege", "Read" )
	        .addQueryParam( "adminPrivilege", "None" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
	        
	      schoolList.each( editUserPrivileges )
	      
	      urlFetch.fetch( urfUserControllerURIBuilder.toURL() )
	      
	      mockURFUser = URFUser.findByEmail( "awijasa@xmail.com" )
	      userPrivilege = UserPrivilege.findByUserEmail( "awijasa@xmail.com" )
	      assert mockURFUser.sponsorDataPrivilege != "Read", "User was able to edit a URFUser using an invalid id parameter."
	      assert mockURFUser.adminPrivilege != null, "User was able to edit a URFUser using an invalid id parameter."
	      assert userPrivilege.size() == 1, "User was able to edit UserPrivileges using an invalid id parameter."
	      assert userPrivilege.first().schoolName != "Saint John's College MN", "User was able to edit UserPrivileges using an invalid id parameter."
	      assert userPrivilege.first().privilege == "Read", "User was able to edit UserPrivileges using an invalid id parameter."
	      
	      /* URFUser Edit Test with a missing lastUpdateDate parameter. */
	      urfUserControllerURIBuilder = new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", mockURFUser.getKey().getId() )
	        .addQueryParam( "sponsorDataPrivilege", "Read" )
	        .addQueryParam( "adminPrivilege", "None" )
	        
	      schoolList.each( editUserPrivileges )
	      
	      urlFetch.fetch( urfUserControllerURIBuilder.toURL() )
	      
	      mockURFUser = URFUser.findByEmail( "awijasa@xmail.com" )
	      userPrivilege = UserPrivilege.findByUserEmail( "awijasa@xmail.com" )
	      assert mockURFUser.sponsorDataPrivilege != "Read", "User was able to edit a URFUser using a missing lastUpdateDate parameter."
	      assert mockURFUser.adminPrivilege != null, "User was able to edit a URFUser using a missing lastUpdateDate parameter."
	      assert userPrivilege.size() == 1, "User was able to edit UserPrivileges using a missing lastUpdateDate parameter."
	      assert userPrivilege.first().schoolName != "Saint John's College MN", "User was able to edit UserPrivileges using a missing lastUpdateDate parameter."
	      assert userPrivilege.first().privilege == "Read", "User was able to edit UserPrivileges using a missing lastUpdateDate parameter."
	      
	      /* URFUser Edit Test with an invalid lastUpdateDate parameter. */
	      urfUserControllerURIBuilder = new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", mockURFUser.getKey().getId() )
	        .addQueryParam( "sponsorDataPrivilege", "Read" )
	        .addQueryParam( "adminPrivilege", "None" )
	        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
	        
	      schoolList.each( editUserPrivileges )
	      
	      urlFetch.fetch( urfUserControllerURIBuilder.toURL() )
	      
	      mockURFUser = URFUser.findByEmail( "awijasa@xmail.com" )
	      userPrivilege = UserPrivilege.findByUserEmail( "awijasa@xmail.com" )
	      assert mockURFUser.sponsorDataPrivilege != "Read", "User was able to edit a URFUser using an invalid lastUpdateDate parameter."
	      assert mockURFUser.adminPrivilege != null, "User was able to edit a URFUser using an invalid lastUpdateDate parameter."
	      assert userPrivilege.size() == 1, "User was able to edit UserPrivileges using an invalid lastUpdateDate parameter."
	      assert userPrivilege.first().schoolName != "Saint John's College MN", "User was able to edit UserPrivileges using an invalid lastUpdateDate parameter."
	      assert userPrivilege.first().privilege == "Read", "User was able to edit UserPrivileges using an invalid lastUpdateDate parameter."
	      
	      /* URFUser Edit Test. */
	      urfUserControllerURIBuilder = new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", mockURFUser.getKey().getId() )
	        .addQueryParam( "sponsorDataPrivilege", "Read" )
	        .addQueryParam( "adminPrivilege", "None" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
	        
	      schoolList.each( editUserPrivileges )
	      
	      urlFetch.fetch( urfUserControllerURIBuilder.toURL() )
	      
	      mockURFUser = URFUser.findByEmail( "awijasa@xmail.com" )
	      userPrivilege = UserPrivilege.findByUserEmail( "awijasa@xmail.com" )
	      
	      lastUpdateDate = mockURFUser.lastUpdateDate
	    
	      assert mockURFUser.sponsorDataPrivilege == "Read", "User was unable to edit a URFUser."
	      assert mockURFUser.adminPrivilege == null, "User was unable to edit a URFUser."
	      assert userPrivilege.size() == 1, "User was unable to edit UserPrivileges."
	      assert userPrivilege.first().schoolName == "Saint John's College MN", "User was unable to edit UserPrivileges."
	      assert userPrivilege.first().privilege == "Read", "User was unable to edit UserPrivileges."
	      
	      /* School Delete Test. */
	      Entity school = School.findByName( "Saint John's College MN" )
	    
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", school.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", school.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert UserPrivilege.findByUserEmailAndSchoolName( "awijasa@xmail.com", "Saint John's College MN" ) == null, "User was unable to delete a UserPrivilege that is related to the deleted School."
	      
	      /* Restore the deleted School. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "schoolName", "Saint John's College MN" )
	        .addQueryParam( "userEmail", user?.getEmail() ).toURL() )
	    
	      /* Restore the UserPrivilege deleted by the School Delete Test above. */
	      urfUserControllerURIBuilder = new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", mockURFUser.getKey().getId() )
	        .addQueryParam( "sponsorDataPrivilege", "Read" )
	        .addQueryParam( "adminPrivilege", "None" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
	        
	      schoolList = School.list()
	      schoolList.each( editUserPrivileges )
	      
	      urlFetch.fetch( urfUserControllerURIBuilder.toURL() )
	      
	      mockURFUser = URFUser.findByEmail( "awijasa@xmail.com" )
	      lastUpdateDate = mockURFUser.lastUpdateDate
	      
	      /* URFUser Delete Test with a missing action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "id", mockURFUser.getKey().getId() )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert URFUser.findByEmail( "awijasa@xmail.com" ) != null, "User was able to delete a URFUser with no action parameter."
	      assert UserPrivilege.findByUserEmail( "awijasa@xmail.com" ).size() != 0, "User was able to delete a UserPrivilege with no action parameter."
	      
	      /* URFUser Delete Test with an invalid action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "action", "remove" )
	        .addQueryParam( "id", mockURFUser.getKey().getId() )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert URFUser.findByEmail( "awijasa@xmail.com" ) != null, "User was able to delete a URFUser with an invalid action parameter."
	      assert UserPrivilege.findByUserEmail( "awijasa@xmail.com" ).size() != 0, "User was able to delete a UserPrivilege with an invalid action parameter."
	      
	      /* URFUser Delete Test with no id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert URFUser.findByEmail( "awijasa@xmail.com" ) != null, "User was able to delete a URFUser with no id parameter."
	      assert UserPrivilege.findByUserEmail( "awijasa@xmail.com" ).size() != 0, "User was able to delete a UserPrivilege with no id parameter."
	      
	      /* URFUser Delete Test with an invalid id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", "-1" )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert URFUser.findByEmail( "awijasa@xmail.com" ) != null, "User was able to delete a URFUser with an invalid id parameter."
	      assert UserPrivilege.findByUserEmail( "awijasa@xmail.com" ).size() != 0, "User was able to delete a UserPrivilege with an invalid id parameter."
	      
	      /* URFUser Delete Test with no nextTwentyOffset parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", mockURFUser.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert URFUser.findByEmail( "awijasa@xmail.com" ) != null, "User was able to delete a URFUser with no nextTwentyOffset parameter."
	      assert UserPrivilege.findByUserEmail( "awijasa@xmail.com" ).size() != 0, "User was able to delete a UserPrivilege with no nextTwentyOffset parameter."
	      
	      /* URFUser Delete Test with no lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", mockURFUser.getKey().getId() )
	        .addQueryParam( "nextTwentyOffset", 20 ).toURL() )
	      assert URFUser.findByEmail( "awijasa@xmail.com" ) != null, "User was able to delete a URFUser with no lastUpdateDate parameter."
	      assert UserPrivilege.findByUserEmail( "awijasa@xmail.com" ).size() != 0, "User was able to delete a UserPrivilege with no lastUpdateDate parameter."
	      
	      /* URFUser Delete Test with an invalid lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", mockURFUser.getKey().getId() )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert URFUser.findByEmail( "awijasa@xmail.com" ) != null, "User was able to delete a URFUser with an invalid lastUpdateDate parameter."
	      assert UserPrivilege.findByUserEmail( "awijasa@xmail.com" ).size() != 0, "User was able to delete a UserPrivilege with an invalid lastUpdateDate parameter."
	      
	      /* URFUser Delete Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/URFUserController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", mockURFUser.getKey().getId() )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert URFUser.findByEmail( "awijasa@xmail.com" ) == null, "User was unable to delete the URFUser: awijasa@xmail.com."
	      assert UserPrivilege.findByUserEmail( "awijasa@xmail.com" ).size() == 0, "User was unable to delete the UserPrivileges of awijasa@xmail.com."
	      
	      /* Delete the Schools created for the URFUser Save, Edit, and Delete Test. */
	      school = School.findByName( "College of Saint Benedict MN" )
	    
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", school.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", school.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      school = School.findByName( "Saint John's College MN" )
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/SchoolController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", school.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", school.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	    
	      /* Restore the Admin Privilege of the current user. */
	      urfUser?.delete()
	      urfUser = null
	      urfUserBackup?.save()
	    %>
	    <p>URFUser Save Test is successful.</p>
	  </body>
	</html>
<% } %>