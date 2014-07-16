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
import java.util.logging.Level

/**
 * Data and Meta Data Accessor for EnrollmentWithStudent Entity.
 * 
 * @author awijasa
 */

class EnrollmentWithStudent {
    static void deleteMemcache() {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "EnrollmentWithStudent" );
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        EnrollmentWithStudentMemcache.list().each() {
            syncCache.delete( it.memcacheKey )
            it.delete()
        }
    }
    
    static List<Entity> findByLimitAndOffset( int limit, int offset ) {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "EnrollmentWithStudent" );
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        List<Entity> enrollmentWithStudentList
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
        def memcacheKey = "enrollmentWithStudentList of " + urfUserSchools + " row " + ( offset + 1 ) + " to " + ( offset + limit )
        
        if( syncCache.contains( memcacheKey ) )
            enrollmentWithStudentList = ( List<Entity> )syncCache.get( memcacheKey )
        else {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
            Query q = new Query( "EnrollmentWithStudent" )
            
            if( urfUser == null )
                q.addFilter( "schoolName", FilterOperator.EQUAL, "Test" )
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
            
            q.addSort( "enrollTermYear", SortDirection.DESCENDING )
            q.addSort( "enrollTermNo", SortDirection.DESCENDING )
            q.addSort( "firstName", SortDirection.ASCENDING )
            q.addSort( "lastName", SortDirection.ASCENDING )
            enrollmentWithStudentList = datastore.prepare( q ).asList( FetchOptions.Builder.withLimit( limit + 1 ).offset( offset ) )
            
            syncCache.put( memcacheKey, enrollmentWithStudentList )
            
            if( EnrollmentWithStudentMemcache.findByMemcacheKey( memcacheKey ).size() == 0 ) {
                def enrollmentWithStudentMemcache = new Entity( "EnrollmentWithStudentMemcache" )
                enrollmentWithStudentMemcache.memcacheKey = memcacheKey
                enrollmentWithStudentMemcache.lastUpdateDate = new Date()
                
                if( user != null )
                    enrollmentWithStudentMemcache.lastUpdateUser = user.getEmail()
                    
                enrollmentWithStudentMemcache.save()
            }
        }
        
        return enrollmentWithStudentList
    }
    
    static List<Entity> findByLimitAndOffsetAndURFUserEmail( int limit, int offset, String urfUserEmail ) {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "EnrollmentWithStudent" );
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        List<Entity> enrollmentWithStudentList
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
        def memcacheKey = "enrollmentWithStudentList of " + urfUserSchools + " row " + ( offset + 1 ) + " to " + ( offset + limit )
        
        if( syncCache.contains( memcacheKey ) )
            enrollmentWithStudentList = ( List<Entity> )syncCache.get( memcacheKey )
        else {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
            Query q = new Query( "EnrollmentWithStudent" )
            
            if( urfUserEmail == null )
                q.addFilter( "schoolName", FilterOperator.EQUAL, "Test" )
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
            
            q.addSort( "enrollTermYear", SortDirection.DESCENDING )
            q.addSort( "enrollTermNo", SortDirection.DESCENDING )
            q.addSort( "firstName", SortDirection.ASCENDING )
            q.addSort( "lastName", SortDirection.ASCENDING )
            enrollmentWithStudentList = datastore.prepare( q ).asList( FetchOptions.Builder.withLimit( limit + 1 ).offset( offset ) )
            
            syncCache.put( memcacheKey, enrollmentWithStudentList )
            
            if( EnrollmentWithStudentMemcache.findByMemcacheKey( memcacheKey ).size() == 0 ) {
                def enrollmentWithStudentMemcache = new Entity( "EnrollmentWithStudentMemcache" )
                enrollmentWithStudentMemcache.memcacheKey = memcacheKey
                enrollmentWithStudentMemcache.lastUpdateDate = new Date()
                
                if( urfUserEmail != null )
                    enrollmentWithStudentMemcache.lastUpdateUser = urfUserEmail
                    
                enrollmentWithStudentMemcache.save()
            }
        }
        
        return enrollmentWithStudentList
    }
    
    static List<Entity> findByStudentId( String studentId ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "EnrollmentWithStudent" )
        q.addFilter( "studentId", Query.FilterOperator.EQUAL, studentId )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
    
    static Entity findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "EnrollmentWithStudent" )
        FilterPredicate studentIdFilter = new FilterPredicate( "studentId", FilterOperator.EQUAL, studentId )
        FilterPredicate schoolNameFilter = new FilterPredicate( "schoolName", FilterOperator.EQUAL, schoolName )
        FilterPredicate enrollTermNoFilter = new FilterPredicate( "enrollTermNo", FilterOperator.EQUAL, enrollTermNo )
        FilterPredicate enrollTermYearFilter = new FilterPredicate( "enrollTermYear", FilterOperator.EQUAL, enrollTermYear )
        q.setFilter( CompositeFilterOperator.and( studentIdFilter, schoolNameFilter, enrollTermNoFilter, enrollTermYearFilter ) )
        List<Entity> enrollmentWithStudentList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
        
        if( enrollmentWithStudentList.size() == 0 )
            return null
        else
            return enrollmentWithStudentList.first()
    }
    
    static Entity findLastEnrollmentWithStudentByStudentId( String studentId ) {
        Entity lastEnrollmentWithTerms = EnrollmentWithTerms.findLastEnrollmentWithTermsByStudentId( studentId )
        
		if( lastEnrollmentWithTerms == null )
			return null
		else {
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
	        Query q = new Query( "EnrollmentWithStudent" )
	        FilterPredicate studentIdFilter = new FilterPredicate( "studentId", FilterOperator.EQUAL, studentId )
	        FilterPredicate schoolNameFilter = new FilterPredicate( "schoolName", FilterOperator.EQUAL, lastEnrollmentWithTerms.schoolName )
	        FilterPredicate enrollTermNoFilter = new FilterPredicate( "enrollTermNo", FilterOperator.EQUAL, lastEnrollmentWithTerms.enrollTermNo )
	        FilterPredicate enrollTermYearFilter = new FilterPredicate( "enrollTermYear", FilterOperator.EQUAL, lastEnrollmentWithTerms.enrollTermYear )
	        q.setFilter( CompositeFilterOperator.and( studentIdFilter, schoolNameFilter, enrollTermNoFilter, enrollTermYearFilter ) )
	        List<Entity> enrollmentWithStudentList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	        
	        if( enrollmentWithStudentList.size() == 0 )
	            return null
	        else
	            return enrollmentWithStudentList.first()
		}
    }
    
    static boolean isRequired( String fieldName ) throws InvalidFieldException {
        if( ["studentId", "firstName", "schoolName", "firstClassAttended", "lastClassAttended", "enrollTermYear", "enrollTermNo", "lastUpdateDate", "lastUpdateUser"]
            .contains( fieldName ) )
            return true
        else if( ["lastName", "birthDate", "village", "genderCode", "specialInfo", "leaveTermYear", "leaveTermNo", "leaveReasonCategory",
            "leaveReason"].contains( fieldName ) )
            return false
        else
            throw new InvalidFieldException( "EnrollmentWithStudent", fieldName )
    }
    
    def static list() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "EnrollmentWithStudent" )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
}