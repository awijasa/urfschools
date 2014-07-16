<%
  import com.google.appengine.api.datastore.Entity
  import data.Gender
  import data.URFUser
  import data.UserPrivilege
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
  else {
%>
	<html>
	  <head>
	    <title>Gender Save, Edit, and Delete Test</title>
	  </head>
	  <body>
	    <%
	      def gender
	      
	      /* For the Save Test, make sure that the Gender to create has not existed. */
	      assert Gender.findByCode( "X" ) == null, "Gender Code: X has existed. Please change the test case."
	      
	      /* Give the testing user an Admin Read privilege. */
	      def urfUser = URFUser.findByEmail( user.getEmail() )
	      def urfUserBackup = URFUser.findByEmail( user.getEmail() )
	      
	      if( urfUser == null )
	        urfUser = new Entity( "URFUser" )
	        
	      urfUser.email = user.getEmail()
	      urfUser.adminPrivilege = "Read"
	      urfUser.lastUpdateDate = new Date()
	      urfUser.lastUpdateUser = user.getEmail()
	      urfUser.save()
	      
	      /* Gender Save Test with a missing action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "code", "X" )
	        .addQueryParam( "desc", "Tamale" ).toURL() )
	      assert Gender.findByCode( "X" ) == null, "User was able to save a Gender using no action parameter."
	    
	      /* Gender Save Test with an invalid action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "create" )
	        .addQueryParam( "code", "X" )
	        .addQueryParam( "desc", "Tamale" ).toURL() )
	      assert Gender.findByCode( "X" ) == null, "User was able to save a Gender using a create action parameter instead of save."
	      
	      /* Gender Save Test with a missing code parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "desc", "Tamale" ).toURL() )
	      assert Gender.findByCode( "X" ) == null, "User was able to save a Gender using no code parameter."
	      
	      /* Gender Save Test with a missing desc parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "code", "X" ).toURL() )
	      assert Gender.findByCode( "X" ) == null, "User was able to save a Gender using no desc parameter."
	      
	      /* Gender Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "code", "X" )
	        .addQueryParam( "desc", "Tamale" ).toURL() )
	      
	      gender = Gender.findByCode( "X" )
	      
	      Date lastUpdateDate = new Date( gender.lastUpdateDate.getTime() )
	    
	      assert gender != null, "User was unable to save a Gender."
	      assert gender.code == "X", "Code: ${ gender.code } from Gender.findByCode( def code ) is not equal to X."
	      assert gender.desc == "Tamale", "Desc: ${ gender.desc } from Gender.findByCode( def code ) is not equal to Tamale."
	      assert gender.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ gender.lastUpdateDate.clearTime() } from Gender.findByCode( def code ) is not equal to ${ new Date().clearTime }."
	      
	      def genderCount = Gender.list().size()
	      
	      /* Gender Duplicate Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "code", "X" )
	        .addQueryParam( "desc", "Tamale" ).toURL() )
	      
	      assert genderCount == Gender.list().size(), "User was able to save a Duplicate Gender."
	      
	      /* Gender Edit Test with a missing action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "id", gender.getKey().getId() )
	        .addQueryParam( "desc", "Heterosexual" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      gender = Gender.findByCode( "X" )
	      
	      assert gender?.desc != "Heterosexual", "User was able to edit a Gender using no action parameter."
	      
	      /* Gender Edit Test with an invalid action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "modify" )
	        .addQueryParam( "id", gender.getKey().getId() )
	        .addQueryParam( "desc", "Heterosexual" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      gender = Gender.findByCode( "X" )
	      
	      assert gender?.desc != "Heterosexual", "User was able to edit a Gender using an invalid action parameter."
	      
	      /* Gender Edit Test with a missing id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "code", "X" )
	        .addQueryParam( "desc", "Heterosexual" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      gender = Gender.findByCode( "X" )
	      
	      assert gender?.desc != "Heterosexual", "User was able to edit a Gender using no id parameter."
	      
	      /* Gender Edit Test with an invalid id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", "-1" )
	        .addQueryParam( "desc", "Heterosexual" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      gender = Gender.findByCode( "X" )
	      
	      assert gender?.desc != "Heterosexual", "User was able to edit a Gender using an invalid id parameter."
	      
	      /* Gender Edit Test with a missing lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", gender.getKey().getId() )
	        .addQueryParam( "desc", "Heterosexual" ).toURL() )
	      
	      gender = Gender.findByCode( "X" )
	      
	      assert gender?.desc != "Heterosexual", "User was able to edit a Gender using a missing lastUpdateDate parameter."
	      
	      /* Gender Edit Test with an invalid lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", gender.getKey().getId() )
	        .addQueryParam( "desc", "Heterosexual" )
	        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      gender = Gender.findByCode( "X" )
	      
	      assert gender?.desc != "Heterosexual", "User was able to edit a Gender using an invalid lastUpdateDate parameter."
	      
	      /* Gender Edit Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "edit" )
	        .addQueryParam( "id", gender.getKey().getId() )
	        .addQueryParam( "desc", "Heterosexual" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      
	      gender = Gender.findByCode( "X" )
	      
	      lastUpdateDate = new Date( gender.lastUpdateDate.getTime() )
	      
	      assert gender != null, "User was unable to edit a Gender."
	      assert gender.code == "X", "Code: ${ gender.code } from Gender.findByCode( def code ) is not equal to X."
	      assert gender.desc == "Heterosexual", "Desc: ${ gender.desc } from Gender.findByCode( def code ) is not equal to Heterosexual."
	      assert gender.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ gender.lastUpdateDate.clearTime() } from Gender.findByCode( def code ) is not equal to ${ new Date().clearTime }."
	    
	      /* Gender Delete Test with a missing action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "id", gender.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Gender.findByCode( "X" ) != null, "User was able to delete a Gender using no action parameter."
	      
	      /* Gender Delete Test with an invalid action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "remove" )
	        .addQueryParam( "id", gender.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Gender.findByCode( "X" ) != null, "User was able to delete a Gender using an invalid action parameter."
	      
	      /* Gender Delete Test with no id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "code", "X" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Gender.findByCode( "X" ) != null, "User was able to delete a Gender using no id parameter."
	      
	      /* Gender Delete Test with an invalid id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", "-1" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Gender.findByCode( "X" ) != null, "User was able to delete a Gender using an invalid id parameter."
	      
	      /* Gender Delete Test with a missing lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", gender.getKey().getId() ).toURL() )
	      assert Gender.findByCode( "X" ) != null, "User was able to delete a Gender using a missing lastUpdateDate parameter."
	      
	      /* Gender Delete Test with an invalid lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", gender.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Gender.findByCode( "X" ) != null, "User was able to delete a Gender using an invalid lastUpdateDate parameter."
	      
	      /* Gender Delete Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", gender.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert Gender.findByCode( "X" ) == null, "User was unable to delete a Gender."
	      
	      /* Restore the Admin Privilege of the current user. */
	      urfUser?.delete()
	      urfUser = null
	      urfUserBackup?.save()
	    %>
	    <p>Gender Save, Edit, and Delete Test is successful.</p>
	  </body>
	</html>
<% } %>