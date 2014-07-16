<%
  import com.google.appengine.api.datastore.Entity
  import com.google.appengine.api.memcache.MemcacheService
  import com.google.appengine.api.memcache.MemcacheServiceFactory
  import data.Parent
  import data.ParentMemcache
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
  else {
%>
	<html>
	  <head>
	    <title>Parent Memcache Test</title>
	  </head>
	  <body>
	    <%
	      Entity parent
	      
	      /* Create a new Parent if there is none existing. */
	      if( Parent.findByLimitAndOffset( 20, 0 ).size() == 0 ) {
	        parent = new Entity( "Parent" )
	        
	        parent.parentId = "koey"
	        parent.firstName = "Keng Tjoan"
	        parent.lastUpdateDate = new Date()
	        parent.lastUpdateUser = user.getEmail()
	        parent.save()
	      }
	      
	      /* Clear the Memcache before testing the Parent Memcache. */
	      memcache.clearAll()
	      
	      ParentMemcache.list()*.delete()
	        
	      List<Entity> parentList = Parent.findByLimitAndOffset( 20, 0 )
	      MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService( "Parent" )
	      List<Entity> memcacheParentList = memcacheService.get( "parentList row 1 to 20" )
	      
	      assert ParentMemcache.findByMemcacheKey( "parentList row 1 to 20" ), "parentList row 1 to 20 is not available as a ParentMemcache entity."
	      
	      /* Query Test: Confirm that the Parent Memcache contains the recently queried Parent. */
	      parentList.eachWithIndex() { obj, i ->
	          def memcacheParent = memcacheParentList.getAt( i )
	          assert obj.parentId == memcacheParent.parentId, "Parent ID: ${ obj.parentId } from Parent.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheParent.parentId } from the Memcache."
	          assert obj.firstName == memcacheParent.firstName, "First Name: ${ obj.firstName } from Parent.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheParent.firstName } from the Memcache."
	          assert obj.lastUpdateDate == memcacheParent.lastUpdateDate, "Last Update Date: ${ obj.lastUpdateDate } from Parent.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheParent.lastUpdateDate } from the Memcache."
	          assert obj.lastUpdateUser == memcacheParent.lastUpdateUser, "Last Update User: ${ obj.lastUpdateUser } from Parent.findByLimitAndOffset( int limit, int offset ) is not equal to ${ memcacheParent.lastUpdateUser } from the Memcache."
	      }
	      
	      /* Delete the Parent created just for the query test. */
	      parent?.delete()
	        
	      /* ParentController Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "save" ).toURL() )
	    assert memcacheService.contains( "parentList row 1 to 20" ), "parentList row 1 to 20 Memcache is not found. It must be retained when a Parent save encounters an error."
	      assert ParentMemcache.findByMemcacheKey( "parentList row 1 to 20" ), "parentList row 1 to 20 is not available as a ParentMemcache entity. It must be retained when a Parent save encounters an error."
	        
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "save" )
	        .addQueryParam( "firstName", "Keng Tjoan" ).toURL() )
	      
	      assert !memcacheService.contains( "parentList row 1 to 20" ), "parentList row 1 to 20 Memcache is found. It must be deleted when a new Parent is saved."
	      assert !ParentMemcache.findByMemcacheKey( "parentList row 1 to 20" ), "parentList row 1 to 20 is available as a ParentMemcache entity. It must be deleted when a Parent save encounters an error."
	
			/* Create the Parent Memcache for the Delete Test. */
	      Parent.findByLimitAndOffset( 20, 0 )
	      
	      parent = Parent.findByParentId( "keng tjoan" )
	      
	      Date lastUpdateDate = parent.lastUpdateDate
	      
	      /* ParentController Delete Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", "-1" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert memcacheService.contains( "parentList row 1 to 20" ), "parentList row 1 to 20 Memcache is not found. It must be retained when a Parent delete encounters an error."
	      assert ParentMemcache.findByMemcacheKey( "parentList row 1 to 20" ), "parentList row 1 to 20 ParentMemcache is not found. It must be retained when a Parent delete encounters an error."
	          
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", parent.getKey().getId() )
	        .addQueryParam( "nextTwentyOffset", 20 )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert !memcacheService.contains( "parentList row 1 to 20" ), "parentList row 1 to 20 Memcache is found. It must be deleted when a Parent is deleted."
	      assert !ParentMemcache.findByMemcacheKey( "parentList row 1 to 20" ), "parentList row 1 to 20 ParentMemcache is found. It must be deleted when a Parent is deleted."
	    %>
	    <p>Parent Memcache Test is successful.</p>
	  </body>
	</html>
<% } %>