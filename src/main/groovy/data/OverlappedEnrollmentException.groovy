package data

import com.google.appengine.api.datastore.Entity

/**
 * Thrown if the supplied Enrollment overlaps any existing Enrollment.
 */

class OverlappedEnrollmentException extends Exception {
    public OverlappedEnrollmentException( Entity overlappedEnrollment ) {
        super( "The supplied Enrollment overlaps ${ overlappedEnrollment.studentId }'s ${ overlappedEnrollment.enrollTermYear } Term ${ overlappedEnrollment.enrollTermNo } (${ Term.findByTermSchoolAndTermNoAndYear( overlappedEnrollment.schoolName, overlappedEnrollment.enrollTermNo, overlappedEnrollment.enrollTermYear ).startDate.format( "MMM d yyyy" ) } - ${ Term.findByTermSchoolAndTermNoAndYear( overlappedEnrollment.schoolName, overlappedEnrollment.leaveTermNo, overlappedEnrollment.leaveTermYear )?.endDate?.format( "MMM d yyyy" )?: "now" })" )
    }
}