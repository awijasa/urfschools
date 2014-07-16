package data

/**
 * Thrown if the Date Range supplied is invalid.
 */

class InvalidDateRangeException extends Exception {
    public InvalidDateRangeException( def dateRangeName, def startDate, def endDate ) {
        super( "${ dateRangeName } Date Range is invalid: ${ startDate.format( "MMM d yyyy" ) } - ${ endDate.format( "MMM d yyyy" ) }" )
    }
}