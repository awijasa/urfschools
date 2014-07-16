package data

/**
 * Thrown if the supplied Term overlaps any existing Term.
 */

class OverlappedTermException extends Exception {
    public OverlappedTermException( def overlappedTerm ) {
        super( "The supplied Term overlaps ${ overlappedTerm.year } Term ${ overlappedTerm.termNo } (${ overlappedTerm.startDate.format( "MMM d yyyy" ) } - ${ overlappedTerm.endDate.format( "MMM d yyyy" ) })" )
    }
}