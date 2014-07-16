<%
  import com.google.appengine.api.datastore.Entity
  import com.google.appengine.api.memcache.MemcacheService
  import com.google.appengine.api.memcache.MemcacheServiceFactory
  import data.LeaveReasonCategory
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
  else {
%>
	<html>
	  <head>
	    <title>Leave Reason Category Memcache Test</title>
	  </head>
	  <body>
	    <%
	      def leaveReasonCategory
	      
	      /* For the Query Test, create a new LeaveReasonCategory if there is none existing. */
	      if( LeaveReasonCategory.list().size() == 0 ) {
	        leaveReasonCategory = new Entity( "LeaveReasonCategory" )
	        
	        leaveReasonCategory.category = "Becoming a Millionaire"
	        leaveReasonCategory.lastUpdateDate = new Date()
	        leaveReasonCategory.lastUpdateUser = user.getEmail()
	        leaveReasonCategory.save()
	      }
	      
	      /* Clear the Memcache before testing the LeaveReasonCategory Memcache. */
	      memcache.clearAll()
	      
	      /* Query Test: Confirm that the LeaveReasonCategory Memcache contains the recently queried LeaveReasonCategories. */
	      def leaveReasonCategoryList = LeaveReasonCategory.list()
	      def memcacheLeaveReasonCategoryList = memcache.get( "leaveReasonCategoryList" )
	      
	      leaveReasonCategoryList.eachWithIndex() { obj, i ->
	          def memcacheLeaveReasonCategory = memcacheLeaveReasonCategoryList.getAt( i )
	          assert obj.category == memcacheLeaveReasonCategory.category, "Category: ${ obj.category } from LeaveReasonCategory.list() is not equal to ${ memcacheLeaveReasonCategory.category } from the Memcache."
	          assert obj.lastUpdateDate == memcacheLeaveReasonCategory.lastUpdateDate, "Last Update Date: ${ obj.lastUpdateDate } from LeaveReasonCategory.list() is not equal to ${ memcacheLeaveReasonCategory.lastUpdateDate } from the Memcache."
	          assert obj.lastUpdateUser == memcacheLeaveReasonCategory.lastUpdateUser, "Last Update User: ${ obj.lastUpdateUser } from LeaveReasonCategory.list() is not equal to ${ memcacheLeaveReasonCategory.lastUpdateUser } from the Memcache."
	      }
	      
	      /* Delete the LeaveReasonCategory created just for the query test. */
	      leaveReasonCategory?.delete()
	        
	      /* LeaveReasonCategory Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" ).addQueryParam( "action", "save" ).toURL() )
	      assert memcache.contains( "leaveReasonCategoryList" ), "leaveReasonCategoryList Memcache is not found. It must be retained when a LeaveReasonCategory save encounters an error."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" ).addQueryParam( "action", "save" ).addQueryParam( "category", "Becoming a Millionaire" ).toURL() )
	      assert !memcache.contains( "leaveReasonCategoryList" ), "leaveReasonCategoryList Memcache is found. It must be deleted when a new LeaveReasonCategory is saved."
	      
	      /* Create a LeaveReasonCategory Memcache for the Delete Test. */
	      LeaveReasonCategory.list().each() {
	        if( it.category.equals( "Becoming a Millionaire" ) )
	          leaveReasonCategory = it
	      }
	      
	      Date lastUpdateDate = leaveReasonCategory.lastUpdateDate
	      
	      /* LeaveReasonCategory Delete Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", "-1" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert memcache.contains( "leaveReasonCategoryList" ), "leaveReasonCategoryList Memcache is not found. It must be retained when a LeaveReasonCategory delete encounters an error."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/LeaveReasonCategoryController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", leaveReasonCategory.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert !memcache.contains( "leaveReasonCategoryList" ), "leaveReasonCategoryList Memcache is found. It must be deleted when a LeaveReasonCategory is deleted."
	    %>
	    <p>LeaveReasonCategory Memcache Test is successful.</p>
	  </body>
	</html>
<% } %>