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
 * Data and Meta Data Accessor for Gender Entity.
 * 
 * @author awijasa
 */

class Gender {
    def static deleteMemcache() {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        syncCache.delete( "genderList" )
    }
    
    def static findByCode( def code ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Gender" )
        q.addFilter( "code", FilterOperator.EQUAL, code )
        def genderList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
        
        if( genderList.size() == 0 )
            return null
        else
            return genderList.first()
    }
    
    def static getPrimaryKey() {
        ["code"]
    }
    
    def static isRequired( def fieldName ) throws InvalidFieldException {
        if( [ "code", "desc", "lastUpdateDate", "lastUpdateUser"].contains( fieldName ) )
            return true
        else
            throw new InvalidFieldException( "Gender", fieldName )
    }
    
    def static isUsed( def gender ) {
        Student.findByGenderCode( gender.code ).size() > 0
    }
    
    def static list() {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        def genderList
        
        if( syncCache.contains( "genderList" ) )
            genderList = ( List<Entity> )syncCache.get( "genderList" )
        else {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
            Query q = new Query( "Gender" )
            q.addSort( "code", SortDirection.ASCENDING )
            genderList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
            
            syncCache.put( "genderList", genderList )
        }
        
        return genderList
    }
}