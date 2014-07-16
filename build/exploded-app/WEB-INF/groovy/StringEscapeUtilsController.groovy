/*
Document   : Groovlet file: StringEscapeUtilsController.groovy
Created on : Wed Aug 26 13:10:00 CST 2012
Author     : awijasa
 */

import org.apache.commons.lang3.StringEscapeUtils

/* StringEscapeUtils method accessor. */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the StringEscapeUtilsController. */
if( user == null )
    response.sendError( response.SC_UNAUTHORIZED )
else {
    
    /* HTML Escape a String. */
    if( params.method.equals( "escapeHtml4" ) ) {
        try {
            print StringEscapeUtils.escapeHtml4( params.string )
        }
        catch( Exception e ) {
            
            /* Respond with an Internal Server error message if the escape fails. */
            response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
            response.setHeader( "Response-Phrase", e.getMessage() )
        }
    }
}