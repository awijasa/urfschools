package data

/**
 * Thrown if the only Enrollment of a Student is about to be deleted.
 */

class TheOnlyEnrollmentLeftException extends Exception {
    public TheOnlyEnrollmentLeftException( String studentId ) {
        super( "Please don't delete ${ studentId }'s one and only Enrollment record." )
    }
}