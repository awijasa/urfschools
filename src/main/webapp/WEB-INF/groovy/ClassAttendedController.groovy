import com.google.appengine.api.datastore.Entity
import data.Class
import data.ClassFees
import data.Enrollment
import data.Term
import data.URFUser

/* Handle setup operation on the ClassAttended entity */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the ClassAttendedController. */
if( user == null && !localMode )
	response.sendError( response.SC_UNAUTHORIZED )
else {
	
	/* Allow the user to delete, edit, and save only if the request comes from a test script or an admin with Modify privilege. */
	if( /* Test Script */ user == null || /* Admin with Modify privilege */ URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
		
		/* Add ClassFees entities if the associated Class entities exist */
		if( params.action == "setup" ) {
			Enrollment.list().each(
				{ enrollment ->
					Entity enrollmentTerm = Term.findByTermSchoolAndTermNoAndYear( enrollment.schoolName, enrollment.enrollTermNo, enrollment.enrollTermYear )
					Entity firstClassAttended = Class.findBySchoolNameAndClass( enrollment.schoolName, enrollment.firstClassAttended )
					Entity lastClassAttended = Class.findBySchoolNameAndClass( enrollment.schoolName, enrollment.lastClassAttended )
					Entity leaveTerm = Term.findByTermSchoolAndTermNoAndYear( enrollment.schoolName, enrollment.leaveTermNo, enrollment.leaveTermYear )
					
					List<Number> yearsEnrolled = Term.findYearsByTermSchoolAndTermRange( enrollment.schoolName,	enrollmentTerm,	leaveTerm, new Date() )
					List<Entity> classes = Class.findBySchoolNameAndLevelRange( enrollment.schoolName, firstClassAttended.level, lastClassAttended.level )
					
					yearsEnrolled.eachWithIndex(
						{ yearEnrolled, index ->
							Term.findByTermSchoolAndYearAndTermRange( enrollment.schoolName, yearEnrolled, enrollmentTerm, leaveTerm, new Date() ).each(
								{ termEnrolled ->
									Entity classAttended = new Entity( "ClassAttended", enrollment.getKey() )
									Entity schoolClass
									
									if( index < classes.size() )
										schoolClass = classes.get( index )
									else
										schoolClass = lastClassAttended
										
									Entity classFees = ClassFees.findBySchoolNameAndClassAndTermNoAndTermYear( enrollment.schoolName, schoolClass.getProperty( "class" ), enrollment.enrollTermNo, enrollment.enrollTermYear )
									
									classAttended.studentId = enrollment.studentId
									classAttended.schoolName = enrollment.schoolName
									classAttended.enrollTermNo = enrollment.enrollTermNo
									classAttended.enrollTermYear = enrollment.enrollTermYear
									classAttended.setProperty( "class", schoolClass.getProperty( "class" ) )
									classAttended.classLevel = schoolClass.level
									classAttended.classTermNo = termEnrolled.termNo
									classAttended.classTermYear = termEnrolled.year
									classAttended.tuitionFee = classFees.tuitionFee
									classAttended.boardingFee = classFees.boardingFee
									classAttended.lastUpdateDate = new Date()
									classAttended.lastUpdateUser = user.getEmail()
									classAttended.save()
								}
							)
						}
					)
				}
			)
		}
		
		println "Added ClassAttended entities"
	}
	else {
		response.setStatus( response.SC_UNAUTHORIZED )
		response.setHeader( "Response-Phrase", "Unauthorized" )
	}
}