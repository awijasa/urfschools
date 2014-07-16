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
 * Data and Meta Data Accessor for LeaveReasonCategory Entity.
 *
 * @author awijasa
 */

class LeaveReasonCategory {
    def static deleteMemcache() {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        syncCache.delete( "leaveReasonCategoryList" )
    }
    
    def static findByCategory( def category ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "LeaveReasonCategory" )
        q.addFilter( "category", FilterOperator.EQUAL, category )
        def leaveReasonCategoryList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
        
        if( leaveReasonCategoryList.size() == 0 )
            return null
        else
            return leaveReasonCategoryList.first()
    }
    
    def static getPrimaryKey() {
        ["category"]
    }
    
    def static isRequired( def fieldName ) throws InvalidFieldException {
        if( ["category", "lastUpdateDate", "lastUpdateUser"].contains( fieldName ) )
            return true
        else
            throw new InvalidFieldException( "LeaveReasonCategory", fieldName )
    }
    
    def static isUsed( def leaveReasonCategory ) {
        Enrollment.findByLeaveReasonCategory( leaveReasonCategory.category ).size() > 0
    }
    
    def static list() {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        def leaveReasonCategoryList
        
        if( syncCache.contains( "leaveReasonCategoryList" ) )
            leaveReasonCategoryList = ( List<Entity> )syncCache.get( "leaveReasonCategoryList" )
        else {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
            Query q = new Query( "LeaveReasonCategory" )
            q.addSort( "category", SortDirection.ASCENDING )
            leaveReasonCategoryList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
            
            syncCache.put( "leaveReasonCategoryList", leaveReasonCategoryList )
        }
        
        return leaveReasonCategoryList
    }
}