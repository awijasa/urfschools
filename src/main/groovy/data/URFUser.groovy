/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.FetchOptions
import com.google.appengine.api.datastore.FetchOptions.Builder
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.Query.FilterOperator
import com.google.appengine.api.datastore.Query.SortDirection

/**
 * Data and Meta Data Accessor for URFUser Entity.
 *
 * @author awijasa
 */

class URFUser {
    def static findByEmail( def email ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "URFUser" )
        q.addFilter( "email", FilterOperator.EQUAL, email )
        
        def urfUsers = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
        
        if( urfUsers.size() == 0 )
            return null
        else
            return urfUsers.first()
    }
    
    def static findByLimitAndOffset( def limit, def offset ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "URFUser" )
        q.addSort( "email", SortDirection.ASCENDING )
        
        datastore.prepare( q ).asList( FetchOptions.Builder.withLimit( limit + 1 ).offset( offset ) )
    }
    
    def static getPrimaryKey() {
        ["email"]
    }
    
    def static isRequired( def fieldName ) throws InvalidFieldException {
        if( ["email", "lastUpdateDate", "lastUpdateUser"].contains( fieldName ) )
            return true
        else if( ["adminPrivilege", "sponsorDataPrivilege"].contains( fieldName ) )
            return false
        else
            throw new InvalidFieldException( "URFUser", fieldName )
    }
    
    def static list() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "URFUser" )
        q.addSort( "email", SortDirection.ASCENDING )
        
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
}