import com.google.appengine.api.datastore.Entity
import data.Parent
import data.Relationship
import data.URFUser

/* Handle sync operation on the Relationship entity */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the RelationshipController. */
if( user == null && !localMode )
	response.sendError( response.SC_UNAUTHORIZED )
else {
	
	/* Allow the user to delete, edit, and save only if the request comes from a test script or an admin with Modify privilege. */
	if( /* Test Script */ user == null || /* Admin with Modify privilege */ URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
		
		/* Add ancestors to Relationship entities */
		if( params.action == "sync" ) {
			Relationship.list().each(
				{
					if( it.getParent() == null ) {
						Entity relationship = new Entity( "Relationship", Parent.findByParentId( it.parentId ).getKey() )
						relationship.setPropertiesFrom( it )
						relationship.lastUpdateDate = new Date()
						relationship.lastUpdateUser = user.getEmail()
						relationship.save()
						it.delete()
					}
				}
			)
		}
		
		println "Added ancestors to Relationship entities"
	}
	else {
		response.setStatus( response.SC_UNAUTHORIZED )
		response.setHeader( "Response-Phrase", "Unauthorized" )
	}
}