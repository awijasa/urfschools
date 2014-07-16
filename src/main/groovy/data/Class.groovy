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

import java.util.List;
import java.util.logging.Level

/**
 * Data and Meta Data Accessor for Class Entity.
 * 
 * @author awijasa
 */

class Class {
    def static deleteMemcache( def schoolName ) {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        // GString can not be used when dealing with Memcache.
        syncCache.delete( schoolName + " Classes" )
    }
	
	static List<Entity> findBySchool( Entity school ) {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
		
		List<Entity> schoolClasses
		
		// GString can not be used when dealing with Memcache.
		if( syncCache.contains( school.name + " Classes" ) )
			schoolClasses = ( List<Entity> )syncCache.get( school.name + " Classes" )
		else {
			Key ancestorKey = school?.getKey()
			
			if( ancestorKey == null )
				schoolClasses = new ArrayList()
			else {
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
				Query q = new Query( "Class", ancestorKey )
				q.addSort( "level", SortDirection.ASCENDING )
				schoolClasses = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
			}
			
			// GString can not be used when dealing with Memcache.
			syncCache.put( school.name + " Classes", schoolClasses )
		}
		
		return schoolClasses
	}
    
    static List<Entity> findBySchoolName( String schoolName ) {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        List<Entity> schoolClasses
        
        // GString can not be used when dealing with Memcache.
        if( syncCache.contains( schoolName + " Classes" ) )
            schoolClasses = ( List<Entity> )syncCache.get( schoolName + " Classes" )
        else {
			Key ancestorKey = School.findByName( schoolName )?.getKey()
			
			if( ancestorKey == null )
				schoolClasses = new ArrayList()
			else {
	            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
	            Query q = new Query( "Class", ancestorKey )
	            q.addSort( "level", SortDirection.ASCENDING )
	            schoolClasses = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
			}
            
            // GString can not be used when dealing with Memcache.
            syncCache.put( schoolName + " Classes", schoolClasses )
        }
        
        return schoolClasses
    }
    
    static Entity findBySchoolNameAndClass( String schoolName, String schoolClass ) {
		Key ancestorKey = School.findByName( schoolName )?.getKey()
		
		if( ancestorKey == null )
			return null
		else {
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
	        Query q = new Query( "Class", ancestorKey )
	        FilterPredicate classFilter = new FilterPredicate( "class", Query.FilterOperator.EQUAL, schoolClass )
	        q.setFilter( classFilter )
	        List<Entity> classList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	        
	        if( classList.size() == 0 )
	            return null
	        else
	            return classList.first()
		}
    }
	
	static List<Entity> findBySchoolNameAndLevelRange( String schoolName, Number levelFloor, Number levelCeiling ) {
		Key ancestorKey = School.findByName( schoolName )?.getKey()
		
		List<Entity> classList
		
		if( ancestorKey == null )
			classList = new ArrayList()
		else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
			Query q = new Query( "Class", ancestorKey )
			
			q.setFilter( CompositeFilterOperator.and(
					new FilterPredicate( "level", FilterOperator.GREATER_THAN_OR_EQUAL, levelFloor ),
					new FilterPredicate( "level", FilterOperator.LESS_THAN_OR_EQUAL, levelCeiling )
				)
			)
			
			classList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
		}
		
		return classList
	}
    
    def static getPrimaryKey() {
        ["schoolName", "class"]
    }
    
    def static isRequired( def fieldName ) throws InvalidFieldException {
        if( ["schoolName", "class", "level", "lastUpdateDate", "lastUpdateUser"].contains( fieldName ) )
            return true
        else
            throw new InvalidFieldException( "Class", fieldName )
    }
    
    static boolean isUsed( Entity schoolClass ) {
        ClassAttended.findBySchoolNameAndClass( schoolClass.schoolName, schoolClass.getProperty( "class" ) ).size() > 0
    }
	
	static List<Entity> list() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Class" )
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
}