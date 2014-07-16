package data

/**
 * Thrown if the Value supplied is invalid.
 */

class InvalidValueException extends Exception {
    public InvalidValueException( String fieldName, def value ) {
        super( "${ fieldName }'s value is invalid: ${ value }" )
    }
}