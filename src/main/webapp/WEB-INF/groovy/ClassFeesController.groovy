import com.google.appengine.api.datastore.Entity
import data.Class
import data.ClassFees
import data.Term
import data.URFUser

/* Handle sync operation on the ClassFees entity */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the ClassFeesController. */
if( user == null && !localMode )
	response.sendError( response.SC_UNAUTHORIZED )
else {
	
	/* Allow the user to delete, edit, and save only if the request comes from a test script or an admin with Modify privilege. */
	if( /* Test Script */ user == null || /* Admin with Modify privilege */ URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
		
		/* Add ClassFees entities if the associated Class entities exist */
		if( params.action == "sync" ) {
			Term.list().each(
				{ term ->
					schoolClasses = Class.findBySchoolName( term.termSchool )
					
					if( ClassFees.findBySchoolNameAndTermNoAndTermYear( term.termSchool, term.termNo, term.year ).size() == 0 && schoolClasses.size() > 0 ) {
						schoolClasses.each(
							{ schoolClass ->
								Entity classFees = new Entity( "ClassFees", term.getKey() )
								classFees.schoolName = term.termSchool
								classFees.setProperty( "class", schoolClass.getProperty( "class" ) )
								classFees.classLevel = schoolClass.level
								classFees.termNo = term.termNo
								classFees.termYear = term.year
								classFees.tuitionFee = term.tuitionFee
								classFees.boardingFee = term.boardingFee
								classFees.lastUpdateDate = new Date()
								classFees.lastUpdateUser = user.getEmail()
								classFees.save()
							}
						)
					}
				}
			)
		}
		
		println "Added ClassFees entities if the associated Class entities exist"
	}
	else {
		response.setStatus( response.SC_UNAUTHORIZED )
		response.setHeader( "Response-Phrase", "Unauthorized" )
	}
}