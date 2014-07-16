import com.google.appengine.api.datastore.Entity
import data.Class
import data.School
import data.URFUser

/* Handle sync operation on the Class entity */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the ClassController. */
if( user == null && !localMode )
	response.sendError( response.SC_UNAUTHORIZED )
else {
	
	/* Allow the user to delete, edit, and save only if the request comes from a test script or an admin with Modify privilege. */
	if( /* Test Script */ user == null || /* Admin with Modify privilege */ URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
		
		/* Add ancestors to Class entities */
		if( params.action == "sync" ) {
			Class.list().each(
				{
					if( it.getParent() == null ) {
						Entity schoolClass = new Entity( "Class", School.findByName( it.schoolName ).getKey() )
						schoolClass.setPropertiesFrom( it )
						schoolClass.lastUpdateDate = new Date()
						schoolClass.lastUpdateUser = user.getEmail()
						schoolClass.save()
						it.delete()
						Class.deleteMemcache( it.schoolName )
					}
				}
			)
		}
		
		println "Added ancestors to Class entities"
	}
	else {
		response.setStatus( response.SC_UNAUTHORIZED )
		response.setHeader( "Response-Phrase", "Unauthorized" )
	}
}