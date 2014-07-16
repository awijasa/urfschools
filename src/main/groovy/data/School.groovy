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
import com.google.appengine.api.datastore.Query.FilterPredicate
import com.google.appengine.api.datastore.Query.SortDirection
import com.google.appengine.api.memcache.ConsistentLogAndContinueErrorHandler
import com.google.appengine.api.memcache.MemcacheService
import com.google.appengine.api.memcache.MemcacheServiceFactory
import java.util.logging.Level

/**
 * Data and Meta Data Accessor for School Entity.
 *
 * @author awijasa
 */

class School {
    def static deleteMemcache() {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        syncCache.delete( "schoolList" )
    }
    
    static Entity findByName( String name ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "School" )
        q.setFilter( new FilterPredicate( "name", FilterOperator.EQUAL, name ) )
        List<Entity> schoolList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
        
        if( schoolList.size() == 0 )
            return null
        else
            return schoolList.first()
    }
    
    def static getPrimaryKey() {
        ["name"]
    }
    
    def static isRequired( def fieldName ) throws InvalidFieldException {
        if( ["name", "lastUpdateDate", "lastUpdateUser"].contains( fieldName ) )
            return true
        else
            throw new InvalidFieldException( "School", fieldName )
    }
    
    def static isUsed( def school ) {
        Term.findByTermSchool( school.name ).size() > 0 || Class.findBySchoolName( school.name ).size() > 0
    }
    
    def static list() {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        def schoolList
        
        if( syncCache.contains( "schoolList" ) )
            schoolList = ( List<Entity> )syncCache.get( "schoolList" )
        else {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
            Query q = new Query( "School" )
            q.addSort( "name", SortDirection.ASCENDING )
            schoolList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
            
            syncCache.put( "schoolList", schoolList )
        }
        
        return schoolList
    }
}