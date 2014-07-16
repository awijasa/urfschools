<%
  import com.google.appengine.api.datastore.Entity
  import com.google.appengine.api.memcache.MemcacheService
  import com.google.appengine.api.memcache.MemcacheServiceFactory
  import data.ParentalRelationship
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
  else {
%>
	<html>
	  <head>
	    <title>Memcache Test</title>
	  </head>
	  <body>
	    <%
	      def parentalRelationship
	      
	      /* For the Query Test, create a new ParentalRelationship if there is none existing. */
	      if( ParentalRelationship.list().size() == 0 ) {
	        parentalRelationship = new Entity( "ParentalRelationship" )
	        
	        parentalRelationship.parentalRelationship = "Guardian"
	        parentalRelationship.lastUpdateDate = new Date()
	        parentalRelationship.lastUpdateUser = user.getEmail()
	        parentalRelationship.save()
	      }
	      
	      /* Clear the Memcache before testing the ParentalRelationship Memcache. */
	      memcache.clearAll()
	      
	      /* Query Test: Confirm that the ParentalRelationship Memcache contains the recently queried ParentalRelationships. */
	      def parentalRelationshipList = ParentalRelationship.list()
	      def memcacheParentalRelationshipList = memcache.get( "parentalRelationshipList" )
	      
	      parentalRelationshipList.eachWithIndex() { obj, i ->
	          def memcacheParentalRelationship = memcacheParentalRelationshipList.getAt( i )
	          assert obj.parentalRelationship == memcacheParentalRelationship.parentalRelationship, "Parental Relationship: ${ obj.parentalRelationship } from ParentalRelationship.list() is not equal to ${ memcacheParentalRelationship.parentalRelationship } from the Memcache."
	          assert obj.lastUpdateDate == memcacheParentalRelationship.lastUpdateDate, "Last Update Date: ${ obj.lastUpdateDate } from ParentalRelationship.list() is not equal to ${ memcacheParentalRelationship.lastUpdateDate } from the Memcache."
	          assert obj.lastUpdateUser == memcacheParentalRelationship.lastUpdateUser, "Last Update User: ${ obj.lastUpdateUser } from ParentalRelationship.list() is not equal to ${ memcacheParentalRelationship.lastUpdateUser } from the Memcache."
	      }
	      
	      /* Delete the Parental Relationship created just for the query test. */
	      parentalRelationship?.delete()
	        
	      /* ParentalRelationship Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" ).addQueryParam( "action", "save" ).toURL() )
	      assert memcache.contains( "parentalRelationshipList" ), "parentalRelationshipList Memcache is not found. It must be retained when a ParentalRelationship save encounters an error."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" ).addQueryParam( "action", "save" ).addQueryParam( "parentalRelationship", "Guardian" ).toURL() )
	      assert !memcache.contains( "parentalRelationshipList" ), "parentalRelationshipList Memcache is found. It must be deleted when a new ParentalRelationship is saved."
	      
	      /* Create a ParentalRelationship Memcache for the Delete Test. */
	      ParentalRelationship.list().each() {
	        if( it.parentalRelationship.equals( "Guardian" ) )
	          parentalRelationship = it
	      }
	      
	      Date lastUpdateDate = parentalRelationship.lastUpdateDate
	      
	      /* ParentalRelationship Delete Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", "-1" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert memcache.contains( "parentalRelationshipList" ), "parentalRelationshipList Memcache is not found. It must be retained when a ParentalRelationship delete encounters an error."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/ParentalRelationshipController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", parentalRelationship.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert !memcache.contains( "parentalRelationshipList" ), "parentalRelationshipList Memcache is found. It must be deleted when a ParentalRelationship is deleted."
	    %>
	    <p>ParentalRelationship Memcache Test is successful.</p>
	  </body>
	</html>
<% } %>