package data

/**
 * Thrown if a required field is empty.
 */

class EmptyRequiredFieldException extends Exception {
    public EmptyRequiredFieldException( def entityName, def fieldName ) {
        super( "${ fieldName } is a required ${ entityName } field" )
    }
}