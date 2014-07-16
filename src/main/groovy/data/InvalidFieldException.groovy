package data

/**
 * Thrown if the fieldName is an invalid one in the entity specified.
 */

class InvalidFieldException extends Exception {
    public InvalidFieldException( def entityName, def fieldName ) {
        super( "${ fieldName } does not exist in ${ entityName }" )
    }
}