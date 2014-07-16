/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.FetchOptions
import com.google.appengine.api.datastore.FetchOptions.Builder
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.Query.FilterOperator
import com.google.appengine.api.datastore.Query.SortDirection
import com.google.appengine.api.memcache.ConsistentLogAndContinueErrorHandler
import com.google.appengine.api.memcache.MemcacheService
import com.google.appengine.api.memcache.MemcacheServiceFactory
import java.util.logging.Level

/**
 * Data and Meta Data Accessor for ParentalRelationship Entity.
 *
 * @author awijasa
 */

class ParentalRelationship {
    def static deleteMemcache() {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        syncCache.delete( "parentalRelationshipList" )
    }
    
    def static findByParentalRelationship( def parentalRelationship ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "ParentalRelationship" )
        q.addFilter( "parentalRelationship", FilterOperator.EQUAL, parentalRelationship )
        def parentalRelationshipList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
        
        if( parentalRelationshipList.size() == 0 )
            return null
        else
            return parentalRelationshipList.first()
    }
    
    def static getPrimaryKey() {
        ["parentalRelationship"]
    }
    
    def static isRequired( def fieldName ) throws InvalidFieldException {
        if( ["parentalRelationship", "lastUpdateDate", "lastUpdateUser"].contains( fieldName ) )
            return true
        else
            throw new InvalidFieldException( "ParentalRelationship", fieldName )
    }
    
    static boolean isUsed( Entity parentalRelationship ) {
        Relationship.findByParentalRelationship( parentalRelationship.parentalRelationship ).size() > 0
    }
    
    def static list() {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        def parentalRelationshipList
        
        if( syncCache.contains( "parentalRelationshipList" ) )
            parentalRelationshipList = ( List<Entity> )syncCache.get( "parentalRelationshipList" )
        else {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
            Query q = new Query( "ParentalRelationship" )
            q.addSort( "parentalRelationship", SortDirection.ASCENDING )
            parentalRelationshipList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
            
            syncCache.put( "parentalRelationshipList", parentalRelationshipList )
        }
        
        return parentalRelationshipList
    }
}