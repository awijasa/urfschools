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
import com.google.appengine.api.datastore.Query.CompositeFilterOperator
import com.google.appengine.api.datastore.Query.FilterOperator
import com.google.appengine.api.datastore.Query.FilterPredicate
import com.google.appengine.api.datastore.Query.SortDirection
import com.google.appengine.api.memcache.ConsistentLogAndContinueErrorHandler
import com.google.appengine.api.memcache.MemcacheService
import com.google.appengine.api.memcache.MemcacheServiceFactory
import com.google.appengine.api.users.User
import com.google.appengine.api.users.UserServiceFactory

import java.util.List;
import java.util.logging.Level

/**
 * Data and Meta Data Accessor for Fee Entity.
 * 
 * @author awijasa
 */

class Fee {
	static void deleteMemcache() {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "Fee" );
		syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
		
		FeeMemcache.list().each() {
			syncCache.delete( it.memcacheKey )
			it.delete()
		}
	}
	
	static List<Entity> findByName( String name ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Fee" )
		q.setFilter( new FilterPredicate( "name", Query.FilterOperator.EQUAL, name ) )
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static List<Entity> findByNameAndSchoolNameAndClassTermNoAndClassTermYear( String name, String schoolName, Number classTermNo, Number classTermYear ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Fee" )
		q.setFilter( CompositeFilterOperator.and(
			new FilterPredicate( "name", Query.FilterOperator.EQUAL, name )
			, new FilterPredicate( "schoolName", Query.FilterOperator.EQUAL, schoolName )
			, new FilterPredicate( "classTermNo", Query.FilterOperator.EQUAL, classTermNo )
			, new FilterPredicate( "classTermYear", Query.FilterOperator.EQUAL, classTermYear )
		) )
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static List<Entity> findByNameAndStudentId( String name, String studentId ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Fee" )
		q.setFilter( CompositeFilterOperator.and(
			new FilterPredicate( "name", Query.FilterOperator.EQUAL, name )
			, new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId )
		) )
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static List<Entity> findByNameAndStudentIdAndClassTermNoAndClassTermYear( String name, String studentId, Number classTermNo, Number classTermYear ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Fee" )
		q.setFilter( CompositeFilterOperator.and(
			new FilterPredicate( "name", Query.FilterOperator.EQUAL, name )
			, new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId )
			, new FilterPredicate( "classTermNo", Query.FilterOperator.EQUAL, classTermNo )
			, new FilterPredicate( "classTermYear", Query.FilterOperator.EQUAL, classTermYear )
		) )
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static Entity findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String name, String studentId, String schoolName, Number classTermNo, Number classTermYear ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Fee" )
		q.setFilter( CompositeFilterOperator.and(
			new FilterPredicate( "name", Query.FilterOperator.EQUAL, name )
			, new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId )
			, new FilterPredicate( "schoolName", Query.FilterOperator.EQUAL, schoolName )
			, new FilterPredicate( "classTermNo", Query.FilterOperator.EQUAL, classTermNo )
			, new FilterPredicate( "classTermYear", Query.FilterOperator.EQUAL, classTermYear )
		) )
		List<Entity> feeList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
		
		if( feeList.size() == 0 )
			return null
		else
			return feeList.first()
	}
	
	static List<Entity> findByNameAndStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String name, String studentId, String schoolName, Number enrollTermNo, Number enrollTermYear ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Fee" )
		q.setFilter( CompositeFilterOperator.and(
			new FilterPredicate( "name", Query.FilterOperator.EQUAL, name )
			, new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId )
			, new FilterPredicate( "schoolName", Query.FilterOperator.EQUAL, schoolName )
			, new FilterPredicate( "enrollTermNo", Query.FilterOperator.EQUAL, enrollTermNo )
			, new FilterPredicate( "enrollTermYear", Query.FilterOperator.EQUAL, enrollTermYear )
		) )
		
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static List<Entity> findByLimitAndOffset( int limit, int offset ) {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "Fee" );
		syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
		
		List<Entity> feeList
		List<Entity> urfUserSchoolList
		String urfUserSchools = ""
		
		User user = UserServiceFactory.getUserService().getCurrentUser()
		Entity urfUser = URFUser.findByEmail( user?.getEmail() )
		
		if( urfUser == null )
			urfUserSchools = "[Test]"
		else {
			urfUserSchoolList = UserPrivilege.findByUserEmail( urfUser.email )
			
			urfUserSchoolList.each() {
				
				// GString can not be used when dealing with Memcache.
				urfUserSchools += "[" + it.schoolName + "]"
			}
		}
		
		// GString can not be used when dealing with Memcache.
		String memcacheKey = "feeList of " + urfUserSchools + " row " + ( offset + 1 ) + " to " + ( offset + limit )
		
		if( syncCache.contains( memcacheKey ) )
			feeList = ( List<Entity> )syncCache.get( memcacheKey )
		else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
			Query q = new Query( "Fee" )
			
			if( urfUser == null )
				q.setFilter( new FilterPredicate( "schoolName", FilterOperator.EQUAL, "Test" ) )
			else {
				ArrayList schoolFilterList = new ArrayList()
				
				urfUserSchoolList.each() {
					schoolFilterList.add new FilterPredicate( "schoolName", FilterOperator.EQUAL, it.schoolName )
				}
				
				if( schoolFilterList.size() == 0 )
					return schoolFilterList
				else if( schoolFilterList.size() == 1 )
					q.setFilter( schoolFilterList.first() )
				else
					q.setFilter( CompositeFilterOperator.or( schoolFilterList ) )
			}
			
			q.addSort( "classTermYear", SortDirection.DESCENDING )
			q.addSort( "classTermNo", SortDirection.DESCENDING )
			q.addSort( "studentId", SortDirection.ASCENDING )
			q.addSort( "name", SortDirection.ASCENDING )
			feeList = datastore.prepare( q ).asList( FetchOptions.Builder.withLimit( limit + 1 ).offset( offset ) )
			
			syncCache.put( memcacheKey, feeList )
			
			if( FeeMemcache.findByMemcacheKey( memcacheKey ).size() == 0 ) {
				Entity feeMemcache = new Entity( "FeeMemcache" )
				feeMemcache.memcacheKey = memcacheKey
				feeMemcache.lastUpdateDate = new Date()
				feeMemcache.lastUpdateUser = user?.getEmail()
					
				feeMemcache.save()
			}
		}
		
		return feeList
	}
	
	static List<Entity> findByLimitAndOffsetAndURFUserEmail( int limit, int offset, String urfUserEmail ) {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "Fee" );
		syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
		
		List<Entity> feeList
		List<Entity> urfUserSchoolList
		String urfUserSchools = ""
		
		if( urfUserEmail == null )
			urfUserSchools = "[Test]"
		else {
			urfUserSchoolList = UserPrivilege.findByUserEmail( urfUserEmail )
			
			urfUserSchoolList.each() {
				
				// GString can not be used when dealing with Memcache.
				urfUserSchools += "[" + it.schoolName + "]"
			}
		}
		
		// GString can not be used when dealing with Memcache.
		String memcacheKey = "feeList of " + urfUserSchools + " row " + ( offset + 1 ) + " to " + ( offset + limit )
		
		if( syncCache.contains( memcacheKey ) )
			feeList = ( List<Entity> )syncCache.get( memcacheKey )
		else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
			Query q = new Query( "Fee" )
			
			if( urfUserEmail == null )
				q.setFilter( new FilterPredicate( "schoolName", FilterOperator.EQUAL, "Test" ) )
			else {
				ArrayList schoolFilterList = new ArrayList()
				
				urfUserSchoolList.each() {
					schoolFilterList.add new FilterPredicate( "schoolName", FilterOperator.EQUAL, it.schoolName )
				}
				
				if( schoolFilterList.size() == 0 )
					return schoolFilterList
				else if( schoolFilterList.size() == 1 )
					q.setFilter( schoolFilterList.first() )
				else
					q.setFilter( CompositeFilterOperator.or( schoolFilterList ) )
			}
			
			q.addSort( "classTermYear", SortDirection.DESCENDING )
			q.addSort( "classTermNo", SortDirection.DESCENDING )
			q.addSort( "studentId", SortDirection.ASCENDING )
			q.addSort( "name", SortDirection.ASCENDING )
			feeList = datastore.prepare( q ).asList( FetchOptions.Builder.withLimit( limit + 1 ).offset( offset ) )
			
			syncCache.put( memcacheKey, feeList )
			
			if( FeeMemcache.findByMemcacheKey( memcacheKey ).size() == 0 ) {
				Entity feeMemcache = new Entity( "FeeMemcache" )
				feeMemcache.memcacheKey = memcacheKey
				feeMemcache.lastUpdateDate = new Date()
				feeMemcache.lastUpdateUser = urfUserEmail
					
				feeMemcache.save()
			}
		}
		
		return feeList
	}
	
	static List<Entity> findByStudentId( String studentId ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Fee" )
		q.setFilter( new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId ) )
		
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static List<Entity> findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Fee" )
		q.setFilter( CompositeFilterOperator.and(
			new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId )
			, new FilterPredicate( "schoolName", Query.FilterOperator.EQUAL, schoolName )
			, new FilterPredicate( "classTermNo", Query.FilterOperator.EQUAL, classTermNo )
			, new FilterPredicate( "classTermYear", Query.FilterOperator.EQUAL, classTermYear )
		) )
		
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static List<Entity> findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, Number enrollTermNo, Number enrollTermYear ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Fee" )
		q.setFilter( CompositeFilterOperator.and(
			new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId )
			, new FilterPredicate( "schoolName", Query.FilterOperator.EQUAL, schoolName )
			, new FilterPredicate( "enrollTermNo", Query.FilterOperator.EQUAL, enrollTermNo )
			, new FilterPredicate( "enrollTermYear", Query.FilterOperator.EQUAL, enrollTermYear )
		) )
		
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static List<Entity> findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYearAndLimitAndOffset( String studentId, String schoolName, Number enrollTermNo, Number enrollTermYear, int limit, int offset ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Fee" )
		q.setFilter( CompositeFilterOperator.and(
			new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId )
			, new FilterPredicate( "schoolName", Query.FilterOperator.EQUAL, schoolName )
			, new FilterPredicate( "enrollTermNo", Query.FilterOperator.EQUAL, enrollTermNo )
			, new FilterPredicate( "enrollTermYear", Query.FilterOperator.EQUAL, enrollTermYear )
		) )
		q.addSort( "classTermYear", SortDirection.DESCENDING )
		q.addSort( "classTermNo", SortDirection.DESCENDING )
		q.addSort( "name", SortDirection.ASCENDING )
		
		datastore.prepare( q ).asList( FetchOptions.Builder.withLimit( limit + 1 ).offset( offset ) )
	}
	
    static List<String> getPrimaryKey() {
        return ["name", "studentId", "schoolName", "classTermNo", "classTermYear"]
    }
    
    static boolean isRequired( String fieldName ) throws InvalidFieldException {
        if( ["name", "studentId", "schoolName", "enrollTermNo", "enrollTermYear", "classTermNo", "classTermYear", "amount", "lastUpdateDate", "lastUpdateUser"]
            .contains( fieldName ) )
            return true
        else if( ["comment"].contains( fieldName ) )
            return false
        else
            throw new InvalidFieldException( "Fee", fieldName )
    }
    
    def static list() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Fee" )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
}