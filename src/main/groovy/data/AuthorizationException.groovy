package data

/**
 * Thrown if there is any User's School Data Authorization issue.
 */

class AuthorizationException extends Exception {
    public AuthorizationException( def schoolName ) {
        super( "You are not authorized to modify ${ schoolName } data." )
    }
}