package data

/**
 * Thrown if the user is modifying a stale record.
 */

class StaleRecordException extends Exception {
    public StaleRecordException( String action, String tab ) {
        super( "Failed to ${ action } this stale record. Please reload the ${ tab } tab and try again." )
    }
}