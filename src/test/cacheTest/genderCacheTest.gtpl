<%
  import com.google.appengine.api.datastore.Entity
  import com.google.appengine.api.memcache.MemcacheService
  import com.google.appengine.api.memcache.MemcacheServiceFactory
  import data.Gender
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
  else {
%>
	<html>
	  <head>
	    <title>Gender Memcache Test</title>
	  </head>
	  <body>
	    <%
	      def gender
	      
	      /* For the Query Test, create a new Gender if there is none existing. */
	      if( Gender.list().size() == 0 ) {
	        gender = new Entity( "Gender" )
	        
	        gender.code = "X"
	        gender.desc = "Homosexual"
	        gender.lastUpdateDate = new Date()
	        gender.lastUpdateUser = user.getEmail()
	        gender.save()
	      }
	      
	      /* Clear the Memcache before testing the Gender Memcache. */
	      memcache.clearAll()
	      
	      /* Query Test: Confirm that the Gender Memcache contains the recently queried Genders. */
	      def genderList = Gender.list()
	      def memcacheGenderList = memcache.get( "genderList" )
	      
	      genderList.eachWithIndex() { obj, i ->
	          def memcacheGender = memcacheGenderList.getAt( i )
	          assert obj.code == memcacheGender.code, "Code: ${ obj.code } from Gender.list() is not equal to ${ memcacheGender.code } from the Memcache."
	          assert obj.desc == memcacheGender.desc, "Desc: ${ obj.desc } from Gender.list() is not equal to ${ memcacheGender.desc } from the Memcache."
	          assert obj.lastUpdateDate == memcacheGender.lastUpdateDate, "Last Update Date: ${ obj.lastUpdateDate } from Gender.list() is not equal to ${ memcacheGender.lastUpdateDate } from the Memcache."
	          assert obj.lastUpdateUser == memcacheGender.lastUpdateUser, "Last Update User: ${ obj.lastUpdateUser } from Gender.list() is not equal to ${ memcacheGender.lastUpdateUser } from the Memcache."
	      }
	      
	      /* Delete the Gender created just for the query test. */
	      gender?.delete()
	        
	      /* Gender Save Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" ).addQueryParam( "action", "save" ).addQueryParam( "desc", "Homosexual" ).toURL() )
	      assert memcache.contains( "genderList" ), "genderList Memcache is not found. It must be retained when a Gender save encounters an error."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" ).addQueryParam( "action", "save" ).addQueryParam( "code", "X" ).addQueryParam( "desc", "Homosexual" ).toURL() )
	      assert !memcache.contains( "genderList" ), "genderList Memcache is found. It must be deleted when a new Gender is saved."
	      
	      /* Create a Gender Memcache for the Delete Test. */
	      Gender.list().each() {
	        if( it.code.equals( "X" ) )
	          gender = it
	      }
	      
	      Date lastUpdateDate = gender.lastUpdateDate
	      
	      /* Gender Delete Test. */
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", "-1" )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert memcache.contains( "genderList" ), "genderList Memcache is not found. It must be retained when a Gender delete encounters an error."
	      
	      urlFetch.fetch( new URIBuilder( "http://localhost:8080/GenderController.groovy" )
	        .addQueryParam( "action", "delete" )
	        .addQueryParam( "id", gender.getKey().getId() )
	        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
	      assert !memcache.contains( "genderList" ), "genderList Memcache is found. It must be deleted when a Gender is deleted."
	    %>
	    <p>Gender Memcache Test is successful.</p>
	  </body>
	</html>
<% } %>