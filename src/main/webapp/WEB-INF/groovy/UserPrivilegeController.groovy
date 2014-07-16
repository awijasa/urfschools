import com.google.appengine.api.datastore.Entity
import data.URFUser
import data.UserPrivilege

/* Handle sync operation on the UserPrivilege entity */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the UserPrivilegeController. */
if( user == null && !localMode )
	response.sendError( response.SC_UNAUTHORIZED )
else {
	
	/* Allow the user to sync only if the request comes from a test script or an admin with Modify privilege. */
	if( /* Test Script */ user == null || /* Admin with Modify privilege */ URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
		
		/* Add ancestors to UserPrivilege entities */
		if( params.action == "sync" ) {
			UserPrivilege.list().each(
				{
					if( it.getParent() == null ) {
						Entity userPrivilege = new Entity( "UserPrivilege", URFUser.findByEmail( it.userEmail ).getKey() )
						userPrivilege.setPropertiesFrom( it )
						userPrivilege.lastUpdateDate = new Date()
						userPrivilege.lastUpdateUser = user.getEmail()
						userPrivilege.save()
						it.delete()
						UserPrivilege.deleteMemcache( it.userEmail )
					}
				}
			)
		}
		
		println "Added ancestors to UserPrivilege entities"
	}
	else {
		response.setStatus( response.SC_UNAUTHORIZED )
		response.setHeader( "Response-Phrase", "Unauthorized" )
	}
}