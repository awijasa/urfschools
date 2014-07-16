<%
  import com.google.appengine.api.datastore.Entity
  import data.LeaveReasonCategory
  import data.URFUser
  import data.UserPrivilege
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
  else {
%>
	<html>
	  <head>
	    <title>Leave Reason Category Save, Edit, and Delete Test</title>
	  </head>
	  <body>
	    <%
	      def leaveReasonCategory
	      
	      /* For the Save Test, make sure that the LeaveReasonCategory to create has not existed. */
	      assert LeaveReasonCategory.findByCategory( "Becoming a Millionaire" ) == null, "LeaveReasonCategory: Becoming a Millionaire has existed. Please change the test case."
	      
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
	      
	      /* LeaveReasonCategory Save Test with a missing action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
	        .addQueryParam( "category", "Becoming a Millionaire" ).toURL() )
	      assert LeaveReasonCategory.findByCategory( "Becoming a Millionaire" ) == null, "User was able to save a LeaveReasonCategory using no action parameter."
	    
	      /* LeaveReasonCategory Save Test with an invalid action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
	        .addQueryParam( "action", "create" )
	        .addQueryParam( "category", "Becoming a Millionaire" ).toURL() )
	      assert LeaveReasonCategory.findByCategory( "Becoming a Millionaire" ) == null, "User was able to save a LeaveReasonCategory using a create action parameter instead of save."
	      
	      /* LeaveReasonCategory Save Test with a missing category parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
	        .addQueryParam( "action", "save" ).toURL() )
	      assert LeaveReasonCategory.findByCategory( "Becoming a Millionaire" ) == null, "User was able to save a LeaveReasonCategory using no category parameter."
	      
	      /* LeaveReasonCategory Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "category", "Becoming a Millionaire" ).toURL() )
	      
	      leaveReasonCategory = LeaveReasonCategory.findByCategory( "Becoming a Millionaire" )
	      
	      Date lastUpdateDate = new Date( leaveReasonCategory.lastUpdateDate.getTime() )
	    
	      assert leaveReasonCategory != null, "User was unable to save a LeaveReasonCategory."
	      assert leaveReasonCategory.category == "Becoming a Millionaire", "Category: ${ leaveReasonCategory.category } from LeaveReasonCategory.findByCategory( def category ) is not equal to Becoming a Millionaire."
	      assert leaveReasonCategory.lastUpdateDate.clearTime() == new Date().clearTime(), "Last Update Date: ${ leaveReasonCategory.lastUpdateDate.clearTime() } from LeaveReasonCategory.findByCategory( def category ) is not equal to ${ new Date().clearTime }."
	      
	      def leaveReasonCategoryCount = LeaveReasonCategory.list().size()
	      
	      /* LeaveReasonCategory Duplicate Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "category", "Becoming a Millionaire" ).toURL() )
	      
	      assert leaveReasonCategoryCount == LeaveReasonCategory.list().size(), "User was able to save a Duplicate LeaveReasonCategory."
	      
	      /* LeaveReasonCategory Delete Test with a missing action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
	        .addQueryParam( "id", leaveReasonCategory.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert LeaveReasonCategory.findByCategory( "Becoming a Millionaire" ) != null, "User was able to delete a LeaveReasonCategory using no action parameter."
	      
	      /* LeaveReasonCategory Delete Test with an invalid action parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
	        .addQueryParam( "action", "remove" )
	        .addQueryParam( "id", leaveReasonCategory.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert LeaveReasonCategory.findByCategory( "Becoming a Millionaire" ) != null, "User was able to delete a LeaveReasonCategory using an invalid action parameter."
	      
	      /* LeaveReasonCategory Delete Test with no id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "category", "Becoming a Millionaire" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert LeaveReasonCategory.findByCategory( "Becoming a Millionaire" ) != null, "User was able to delete a LeaveReasonCategory using no id parameter."
	      
	      /* LeaveReasonCategory Delete Test with an invalid id parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", "-1" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert LeaveReasonCategory.findByCategory( "Becoming a Millionaire" ) != null, "User was able to delete a LeaveReasonCategory using an invalid id parameter."
	      
	      /* LeaveReasonCategory Delete Test with no lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", leaveReasonCategory.getKey().getId() ).toURL() )
	      assert LeaveReasonCategory.findByCategory( "Becoming a Millionaire" ) != null, "User was able to delete a LeaveReasonCategory using no lastUpdateDate parameter."
	      
	      /* LeaveReasonCategory Delete Test with an invalid lastUpdateDate parameter. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", leaveReasonCategory.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert LeaveReasonCategory.findByCategory( "Becoming a Millionaire" ) != null, "User was able to delete a LeaveReasonCategory using an invalid id parameter."
	    
	      /* LeaveReasonCategory Delete Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", leaveReasonCategory.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert LeaveReasonCategory.findByCategory( "Becoming a Millionaire" ) == null, "User was unable to delete a LeaveReasonCategory."
	      
	      /* Restore the Admin Privilege of the current user. */
	      urfUser?.delete()
	      urfUser = null
	      urfUserBackup?.save()
	    %>
	    <p>LeaveReasonCategory Save and Delete Test is successful.</p>
	  </body>
	</html>
<% } %>