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
import com.google.appengine.api.users.User
import com.google.appengine.api.users.UserServiceFactory
import java.util.logging.Level

/**
 * Data and Meta Data Accessor for Parent Entity.
 *
 * @author awijasa
 */

class Parent {
	static void deleteMemcache() {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "Parent" );
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        ParentMemcache.list().each() {
            syncCache.delete( it.memcacheKey )
            it.delete()
        }
    }

	static List<Entity> findByLimitAndOffset( int limit, int offset ) {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "Parent" );
        User user = UserServiceFactory.getUserService().getCurrentUser()
        
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        List<Entity> parentList
        
        // GString can not be used when dealing with Memcache.
        String memcacheKey = "parentList row " + ( offset + 1 ) + " to " + ( offset + limit )
        
        if( syncCache.contains( memcacheKey ) )
            parentList = ( List<Entity> )syncCache.get( memcacheKey )
        else {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
            Query q = new Query( "Parent" )
            q.addSort( "firstName", SortDirection.ASCENDING )
            q.addSort( "lastName", SortDirection.ASCENDING )
            parentList = datastore.prepare( q ).asList( FetchOptions.Builder.withLimit( limit + 1 ).offset( offset ) )
            
            syncCache.put( memcacheKey, parentList )
            
            if( ParentMemcache.findByMemcacheKey( memcacheKey ).size() == 0 ) {
                Entity parentMemcache = new Entity( "ParentMemcache" )
                parentMemcache.memcacheKey = memcacheKey
                parentMemcache.lastUpdateDate = new Date()
                parentMemcache.lastUpdateUser = user?.getEmail()
                    
                parentMemcache.save()
            }
        }
        
        return parentList
    }

	static Entity findByParentId( String parentId ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Parent" )
        q.setFilter( new FilterPredicate( "parentId", Query.FilterOperator.EQUAL, parentId ) )
        
        List<Entity> parentList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
        
        if( parentList.size() == 0 )
            return null
        else
            return parentList.first()
    }
	
	static String[] getPrimaryKey() {
		return ["parentId"]
	}
    
    static boolean isRequired( String fieldName ) throws InvalidFieldException {
        if( ["parentId", "firstName", "lastUpdateDate", "lastUpdateUser"].contains( fieldName ) )
            return true
        else if( ["lastName", "deceasedInd", "village", "primaryPhone", "secondaryPhone", "email", "profession"].contains( fieldName ) )
            return false
        else
            throw new InvalidFieldException( "Parent", fieldName )
    }
	
	static boolean isUsed( Entity parent ) {
		Payment.findByFundingSource( parent.parentId ).size() > 0
	}
    
    static List<Entity> list() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Parent" )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
}