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
 * Data and Meta Data Accessor for Payment Entity.
 * 
 * @author awijasa
 */

class Payment {
	static void deleteMemcache() {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "Payment" );
		syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
		
		PaymentMemcache.list().each() {
			syncCache.delete( it.memcacheKey )
			it.delete()
		}
	}
	
	static List<Entity> findByFundingSource( String fundingSource ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Payment" )
		q.setFilter( new FilterPredicate( "fundingSource", Query.FilterOperator.EQUAL, fundingSource ) )
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static List<Entity> findByFundingSourceAndSchoolNameAndClassTermNoAndClassTermYear( String fundingSource, String schoolName, Number classTermNo, Number classTermYear ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Payment" )
		q.setFilter( CompositeFilterOperator.and(
			new FilterPredicate( "fundingSource", Query.FilterOperator.EQUAL, fundingSource )
			, new FilterPredicate( "schoolName", Query.FilterOperator.EQUAL, schoolName )
			, new FilterPredicate( "classTermNo", Query.FilterOperator.EQUAL, classTermNo )
			, new FilterPredicate( "classTermYear", Query.FilterOperator.EQUAL, classTermYear )
		) )
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static List<Entity> findByFundingSourceAndStudentId( String fundingSource, String studentId ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Payment" )
		q.setFilter( CompositeFilterOperator.and(
			new FilterPredicate( "fundingSource", Query.FilterOperator.EQUAL, fundingSource )
			, new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId )
		) )
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static List<Entity> findByFundingSourceAndStudentIdAndClassTermNoAndClassTermYear( String fundingSource, String studentId, Number classTermNo, Number classTermYear ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Payment" )
		q.setFilter( CompositeFilterOperator.and(
			new FilterPredicate( "fundingSource", Query.FilterOperator.EQUAL, fundingSource )
			, new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId )
			, new FilterPredicate( "classTermNo", Query.FilterOperator.EQUAL, classTermNo )
			, new FilterPredicate( "classTermYear", Query.FilterOperator.EQUAL, classTermYear )
		) )
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static Entity findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String fundingSource, String studentId, String schoolName, Number classTermNo, Number classTermYear ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Payment" )
		q.setFilter( CompositeFilterOperator.and(
			new FilterPredicate( "fundingSource", Query.FilterOperator.EQUAL, fundingSource )
			, new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId )
			, new FilterPredicate( "schoolName", Query.FilterOperator.EQUAL, schoolName )
			, new FilterPredicate( "classTermNo", Query.FilterOperator.EQUAL, classTermNo )
			, new FilterPredicate( "classTermYear", Query.FilterOperator.EQUAL, classTermYear )
		) )
		List<Entity> paymentList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
		
		if( paymentList.size() == 0 )
			return null
		else
			return paymentList.first()
	}
	
	static List<Entity> findByFundingSourceAndStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String fundingSource, String studentId, String schoolName, Number enrollTermNo, Number enrollTermYear ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Payment" )
		q.setFilter( CompositeFilterOperator.and(
			new FilterPredicate( "fundingSource", Query.FilterOperator.EQUAL, fundingSource )
			, new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId )
			, new FilterPredicate( "schoolName", Query.FilterOperator.EQUAL, schoolName )
			, new FilterPredicate( "enrollTermNo", Query.FilterOperator.EQUAL, enrollTermNo )
			, new FilterPredicate( "enrollTermYear", Query.FilterOperator.EQUAL, enrollTermYear )
		) )
		
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static List<Entity> findByLimitAndOffset( int limit, int offset ) {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "Payment" );
		syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
		
		List<Entity> paymentList
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
		String memcacheKey = "paymentList of " + urfUserSchools + " row " + ( offset + 1 ) + " to " + ( offset + limit )
		
		if( syncCache.contains( memcacheKey ) )
			paymentList = ( List<Entity> )syncCache.get( memcacheKey )
		else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
			Query q = new Query( "Payment" )
			
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
			q.addSort( "fundingSource", SortDirection.ASCENDING )
			paymentList = datastore.prepare( q ).asList( FetchOptions.Builder.withLimit( limit + 1 ).offset( offset ) )
			
			syncCache.put( memcacheKey, paymentList )
			
			if( PaymentMemcache.findByMemcacheKey( memcacheKey ).size() == 0 ) {
				Entity paymentMemcache = new Entity( "PaymentMemcache" )
				paymentMemcache.memcacheKey = memcacheKey
				paymentMemcache.lastUpdateDate = new Date()
				paymentMemcache.lastUpdateUser = user?.getEmail()
					
				paymentMemcache.save()
			}
		}
		
		return paymentList
	}
	
	static List<Entity> findByLimitAndOffsetAndURFUserEmail( int limit, int offset, String urfUserEmail ) {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "Payment" );
		syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
		
		List<Entity> paymentList
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
		String memcacheKey = "paymentList of " + urfUserSchools + " row " + ( offset + 1 ) + " to " + ( offset + limit )
		
		if( syncCache.contains( memcacheKey ) )
			paymentList = ( List<Entity> )syncCache.get( memcacheKey )
		else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
			Query q = new Query( "Payment" )
			
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
			q.addSort( "fundingSource", SortDirection.ASCENDING )
			paymentList = datastore.prepare( q ).asList( FetchOptions.Builder.withLimit( limit + 1 ).offset( offset ) )
			
			syncCache.put( memcacheKey, paymentList )
			
			if( PaymentMemcache.findByMemcacheKey( memcacheKey ).size() == 0 ) {
				Entity paymentMemcache = new Entity( "PaymentMemcache" )
				paymentMemcache.memcacheKey = memcacheKey
				paymentMemcache.lastUpdateDate = new Date()
				paymentMemcache.lastUpdateUser = urfUserEmail
					
				paymentMemcache.save()
			}
		}
		
		return paymentList
	}
	
	static List<Entity> findByStudentId( String studentId ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Payment" )
		q.setFilter( new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId ) )
		
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static List<Entity> findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Payment" )
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
		Query q = new Query( "Payment" )
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
		Query q = new Query( "Payment" )
		q.setFilter( CompositeFilterOperator.and(
			new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId )
			, new FilterPredicate( "schoolName", Query.FilterOperator.EQUAL, schoolName )
			, new FilterPredicate( "enrollTermNo", Query.FilterOperator.EQUAL, enrollTermNo )
			, new FilterPredicate( "enrollTermYear", Query.FilterOperator.EQUAL, enrollTermYear )
		) )
		q.addSort( "classTermYear", SortDirection.DESCENDING )
		q.addSort( "classTermNo", SortDirection.DESCENDING )
		q.addSort( "fundingSource", SortDirection.ASCENDING )
		
		datastore.prepare( q ).asList( FetchOptions.Builder.withLimit( limit + 1 ).offset( offset ) )
	}
	
    static List<String> getPrimaryKey() {
        return ["fundingSource", "studentId", "schoolName", "classTermNo", "classTermYear"]
    }
    
    static boolean isRequired( String fieldName ) throws InvalidFieldException {
        if( ["fundingSource", "studentId", "schoolName", "enrollTermNo", "enrollTermYear", "classTermNo", "classTermYear", "amount", "lastUpdateDate", "lastUpdateUser"]
            .contains( fieldName ) )
            return true
        else if( ["comment"].contains( fieldName ) )
            return false
        else
            throw new InvalidFieldException( "Payment", fieldName )
    }
    
    def static list() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Payment" )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
}