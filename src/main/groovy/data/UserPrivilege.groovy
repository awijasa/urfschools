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
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.Query.CompositeFilterOperator
import com.google.appengine.api.datastore.Query.FilterOperator
import com.google.appengine.api.datastore.Query.FilterPredicate
import com.google.appengine.api.datastore.Query.SortDirection
import com.google.appengine.api.memcache.ConsistentLogAndContinueErrorHandler
import com.google.appengine.api.memcache.MemcacheService
import com.google.appengine.api.memcache.MemcacheServiceFactory
import java.util.logging.Level

/**
 * Data and Meta Data Accessor for UserPrivilege Entity.
 *
 * @author awijasa
 */

class UserPrivilege {
    def static deleteMemcache( def userEmail ) {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        // GString can not be used when dealing with Memcache.
        syncCache.delete( userEmail + " UserPrivileges" )
        syncCache.delete( userEmail + " Read UserPrivileges" )
        syncCache.delete( userEmail + " Modify UserPrivileges" )
    }
    
    def static deleteMemcache( def userEmail, def privilege ) {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        // GString can not be used when dealing with Memcache.
        syncCache.delete( userEmail + " UserPrivileges" )
        syncCache.delete( userEmail + " " + privilege + " UserPrivileges" )
    }
    
    def static findBySchoolName( def schoolName ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "UserPrivilege" )
        q.addFilter( "schoolName", FilterOperator.EQUAL, schoolName )
        
        return datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
    
	static List<Entity> findByURFUser( Entity urfUser ) {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
		
		List<Entity> userPrivilegeList
		
		// GString can not be used when dealing with Memcache.
		if( syncCache.contains( urfUser.email + " UserPrivileges" ) )
			userPrivilegeList = ( List<Entity> )syncCache.get( urfUser.email + " UserPrivileges" )
		else {
			Key ancestorKey = urfUser?.getKey()
			
			if( ancestorKey == null )
				userPrivilegeList = new ArrayList()
			else {
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
				Query q = new Query( "UserPrivilege", ancestorKey )
				q.addSort( "schoolName", SortDirection.ASCENDING )
				userPrivilegeList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	
				// GString can not be used when dealing with Memcache.
				syncCache.put( urfUser.email + " UserPrivileges", userPrivilegeList )
			}
		}
		
		return userPrivilegeList
	}
	
	static List<Entity> findByURFUserAndPrivilege( Entity urfUser, String privilege ) {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
		
		List<Entity> userPrivilegeList
		
		// GString can not be used when dealing with Memcache.
		if( syncCache.contains( urfUser.email + " " + privilege + " UserPrivileges" ) )
			userPrivilegeList = ( List<Entity> )syncCache.get( urfUser.email + " " + privilege + " UserPrivileges" )
		else {
			Key ancestorKey = urfUser?.getKey()
			
			if( ancestorKey == null )
				userPrivilegeList = new ArrayList()
			else {
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
				Query q = new Query( "UserPrivilege", ancestorKey )
				FilterPredicate privilegeFilter = new FilterPredicate( "privilege", FilterOperator.EQUAL, privilege )
				q.setFilter( privilegeFilter )
				q.addSort( "schoolName", SortDirection.ASCENDING )
				
				userPrivilegeList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	
				// GString can not be used when dealing with Memcache.
				syncCache.put( urfUser.email + " " + privilege + " UserPrivileges", userPrivilegeList )
			}
		}
		
		return userPrivilegeList
	}
	
	static Entity findByURFUserAndSchoolName( Entity urfUser, String schoolName ) {
		Key ancestorKey = urfUser?.getKey()
		
		if( ancestorKey == null )
			return null
		else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
			Query q = new Query( "UserPrivilege", ancestorKey )
			FilterPredicate schoolNameFilter = new FilterPredicate( "schoolName", FilterOperator.EQUAL, schoolName )
			q.setFilter( schoolNameFilter )
			
			List<Entity> userPrivileges = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
			
			if( userPrivileges.size() == 0 )
				return null
			else
				return userPrivileges.first()
		}
	}
	
	static List<Entity> findByUserEmail( String userEmail ) {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        List<Entity> userPrivilegeList
        
        // GString can not be used when dealing with Memcache.
        if( syncCache.contains( userEmail + " UserPrivileges" ) )
            userPrivilegeList = ( List<Entity> )syncCache.get( userEmail + " UserPrivileges" )
        else {
			Key ancestorKey = URFUser.findByEmail( userEmail )?.getKey()
			
			if( ancestorKey == null )
				userPrivilegeList = new ArrayList()
			else {
	            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
	            Query q = new Query( "UserPrivilege", ancestorKey )
	            q.addSort( "schoolName", SortDirection.ASCENDING )
	            userPrivilegeList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	
	            // GString can not be used when dealing with Memcache.
	            syncCache.put( userEmail + " UserPrivileges", userPrivilegeList )
			}
        }
        
        return userPrivilegeList
    }
    
    static List<Entity> findByUserEmailAndPrivilege( String userEmail, String privilege ) {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        List<Entity> userPrivilegeList
        
        // GString can not be used when dealing with Memcache.
        if( syncCache.contains( userEmail + " " + privilege + " UserPrivileges" ) )
            userPrivilegeList = ( List<Entity> )syncCache.get( userEmail + " " + privilege + " UserPrivileges" )
        else {
			Key ancestorKey = URFUser.findByEmail( userEmail )?.getKey()
			
			if( ancestorKey == null )
				userPrivilegeList = new ArrayList()
			else {
	            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
	            Query q = new Query( "UserPrivilege", ancestorKey )
	            FilterPredicate privilegeFilter = new FilterPredicate( "privilege", FilterOperator.EQUAL, privilege )
	            q.setFilter( privilegeFilter )
	            q.addSort( "schoolName", SortDirection.ASCENDING )
	            
	            userPrivilegeList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	
	            // GString can not be used when dealing with Memcache.
	            syncCache.put( userEmail + " " + privilege + " UserPrivileges", userPrivilegeList )
			}
        }
        
        return userPrivilegeList
    }
    
    static Entity findByUserEmailAndSchoolName( String userEmail, String schoolName ) {
		Key ancestorKey = URFUser.findByEmail( userEmail )?.getKey()
		
		if( ancestorKey == null )
			return null
		else {
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
	        Query q = new Query( "UserPrivilege", ancestorKey )
	        FilterPredicate schoolNameFilter = new FilterPredicate( "schoolName", FilterOperator.EQUAL, schoolName )
	        q.setFilter( schoolNameFilter )
	        
	        List<Entity> userPrivileges = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	        
	        if( userPrivileges.size() == 0 )
	            return null
	        else
	            return userPrivileges.first()
		}
    }
    
    def static getPrimaryKey() {
        ["userEmail", "schoolName"]
    }
    
    def static isRequired( def fieldName ) throws InvalidFieldException {
        if( ["userEmail", "schoolName", "privilege", "lastUpdateDate", "lastUpdateUser"].contains( fieldName ) )
            return true
        else
            throw new InvalidFieldException( "UserPrivilege", fieldName )
    }
    
    def static list() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "UserPrivilege" )
        
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
}