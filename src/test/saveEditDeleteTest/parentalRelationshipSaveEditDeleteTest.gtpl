<%
  import com.google.appengine.api.datastore.Entity
  import data.ParentalRelationship
  import data.URFUser
  import data.UserPrivilege
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
  else {
%>
	<html>
	  <head>
	    <title>Parental Relationship Save, Edit, and Delete Test</title>
	  </head>
	  <body>
	    <%
	      def parentalRelationship
	      
	      /* For the Save Test, make sure that the ParentalRelationship to create has not existed. */
	      assert ParentalRelationship.findByParentalRelationship( "Darth Vader" ) == null, "ParentalRelationship: Darth Vader has existed. Please change the test case."
	      
	      /* Give the testing user an Admin Read privilege. */
	      Entity urfUser = URFUser.findByEmail( user.getEmail() )
	      Entity urfUserBackup = URFUser.findByEmail( user.getEmail() )
	      
	      if( urfUser == null )
	        urfUser = new Entity( "URFUser" )
	        
	      urfUser.email = user.getEmail()
	      urfUser.adminPrivilege = "Read"
	      urfUser.lastUpdateDate = new Date()
	      urfUser.lastUpdateUser = user.getEmail()
	      urfUser.save()
	      
	      /* ParentalRelationship Save Test with a missing action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
	        .addQueryParam( "parentalRelationship", "Darth Vader" ).toURL() )
	      assert ParentalRelationship.findByParentalRelationship( "Darth Vader" ) == null, "User was able to save a ParentalRelationship using no action parameter."
	    
	      /* ParentalRelationship Save Test with an invalid action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
	        .addQueryParam( "action", "create" )
	        .addQueryParam( "parentalRelationship", "Darth Vader" ).toURL() )
	      assert ParentalRelationship.findByParentalRelationship( "Darth Vader" ) == null, "User was able to save a ParentalRelationship using a create action parameter instead of save."
	      
	      /* ParentalRelationship Save Test with a missing parentalRelationship parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
	        .addQueryParam( "action", "save" ).toURL() )
	      assert ParentalRelationship.findByParentalRelationship( "Darth Vader" ) == null, "User was able to save a ParentalRelationship using no parentalRelationship parameter."
	      
	      /* ParentalRelationship Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "parentalRelationship", "Darth Vader" ).toURL() )
	      
	      parentalRelationship = ParentalRelationship.findByParentalRelationship( "Darth Vader" )
	      
	      Date lastUpdateDate = new Date( parentalRelationship.lastUpdateDate.getTime() )
	    
	      assert parentalRelationship != null, "User was unable to save a ParentalRelationship."
	      assert parentalRelationship.parentalRelationship == "Darth Vader", "Parental Relationship: ${ parentalRelationship.parentalRelationship } from ParentalRelationship.findByParentalRelationship( def parentalRelationship ) is not equal to Darth Vader."
	      assert parentalRelationship.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ parentalRelationship.lastUpdateDate.clearTime() } from ParentalRelationship.findByParentalRelationship( def parentalRelationship ) is not equal to ${ new Date().clearTime }."
	      
	      def parentalRelationshipCount = ParentalRelationship.list().size()
	      
	      /* ParentalRelationship Duplicate Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "parentalRelationship", "Darth Vader" ).toURL() )
	      
	      assert parentalRelationshipCount == ParentalRelationship.list().size(), "User was able to save a Duplicate ParentalRelationship."
	      
	      /* ParentalRelationship Delete Test with a missing action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
	        .addQueryParam( "id", parentalRelationship.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert ParentalRelationship.findByParentalRelationship( "Darth Vader" ) != null, "User was able to delete a ParentalRelationship using no action parameter."
	      
	      /* ParentalRelationship Delete Test with an invalid action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
	        .addQueryParam( "action", "remove" )
	        .addQueryParam( "id", parentalRelationship.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert ParentalRelationship.findByParentalRelationship( "Darth Vader" ) != null, "User was able to delete a ParentalRelationship using an invalid action parameter."
	      
	      /* ParentalRelationship Delete Test with no id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "parentalRelationship", "Darth Vader" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert ParentalRelationship.findByParentalRelationship( "Darth Vader" ) != null, "User was able to delete a ParentalRelationship using no id parameter."
	      
	      /* ParentalRelationship Delete Test with an invalid id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", "-1" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert ParentalRelationship.findByParentalRelationship( "Darth Vader" ) != null, "User was able to delete a ParentalRelationship using an invalid id parameter."
	      
	      /* ParentalRelationship Delete Test with no lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", parentalRelationship.getKey().getId() ).toURL() )
	      assert ParentalRelationship.findByParentalRelationship( "Darth Vader" ) != null, "User was able to delete a ParentalRelationship using no lastUpdateDate parameter."
	      
	      /* ParentalRelationship Delete Test with an invalid lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", parentalRelationship.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert ParentalRelationship.findByParentalRelationship( "Darth Vader" ) != null, "User was able to delete a ParentalRelationship using an invalid lastUpdateDate parameter."
	      
	      /* ParentalRelationship Delete Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", parentalRelationship.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert ParentalRelationship.findByParentalRelationship( "Darth Vader" ) == null, "User was unable to delete a ParentalRelationship."
	      
	      /* Restore the Admin Privilege of the current user. */
	      urfUser?.delete()
	      urfUser = null
	      urfUserBackup?.save()
	    %>
	    <p>ParentalRelationship Save and Delete Test is successful.</p>
	  </body>
	</html>
<% } %>