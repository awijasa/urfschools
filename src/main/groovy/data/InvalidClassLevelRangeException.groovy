package data

/**
 * Thrown if the Class Level Range supplied is invalid.
 */

class InvalidClassLevelRangeException extends Exception {
    public InvalidClassLevelRangeException( String classLevelRangeName, String firstClassAttended, String lastClassAttended ) {
        super( "${ classLevelRangeName } Class Level Range is invalid: ${ firstClassAttended } - ${ lastClassAttended }" )
    }
}