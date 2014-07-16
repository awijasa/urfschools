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
import com.google.appengine.api.users.UserService
import com.google.appengine.api.users.UserServiceFactory
import java.util.logging.Level

/**
 * Data and Meta Data Accessor for StudentWithLastEnrollment Entity.
 * 
 * @author awijasa
 */

class StudentWithLastEnrollment {
    def static deleteMemcache() {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "StudentWithLastEnrollment" );
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        StudentWithLastEnrollmentMemcache.list().each() {
            syncCache.delete( it.memcacheKey )
            it.delete()
        }
    }
    
    def static findByLimitAndOffset( def limit, def offset ) {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "StudentWithLastEnrollment" );
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        def studentWithLastEnrollmentList
        def urfUserSchoolList
        def urfUserSchools = ""
        
        def user = UserServiceFactory.getUserService().getCurrentUser()
        def urfUser = URFUser.findByEmail( user?.getEmail() )
        
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
        def memcacheKey = "studentWithLastEnrollmentList of " + urfUserSchools + " row " + ( offset + 1 ) + " to " + ( offset + limit )
        
        if( syncCache.contains( memcacheKey ) )
            studentWithLastEnrollmentList = ( List<Entity> )syncCache.get( memcacheKey )
        else {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
            Query q = new Query( "StudentWithLastEnrollment" )
            
            if( urfUser == null )
                q.addFilter( "lastEnrollmentSchool", FilterOperator.EQUAL, "Test" )
            else {
                def lastEnrollmentSchoolFilterList = new ArrayList()
                
                urfUserSchoolList.each() {
                    lastEnrollmentSchoolFilterList.add new FilterPredicate( "lastEnrollmentSchool", FilterOperator.EQUAL, it.schoolName )
                }
                
                if( lastEnrollmentSchoolFilterList.size() == 0 )
                    return lastEnrollmentSchoolFilterList
                else if( lastEnrollmentSchoolFilterList.size() == 1 )
                    q.setFilter( lastEnrollmentSchoolFilterList.first() )
                else
                    q.setFilter( CompositeFilterOperator.or( lastEnrollmentSchoolFilterList ) )
            }
            
            q.addSort( "lastEnrollmentTermYear", SortDirection.DESCENDING )
            q.addSort( "lastEnrollmentTermNo", SortDirection.DESCENDING )
            q.addSort( "firstName", SortDirection.ASCENDING )
            q.addSort( "lastName", SortDirection.ASCENDING )
            studentWithLastEnrollmentList = datastore.prepare( q ).asList( FetchOptions.Builder.withLimit( limit + 1 ).offset( offset ) )
            
            syncCache.put( memcacheKey, studentWithLastEnrollmentList )
            
            if( StudentWithLastEnrollmentMemcache.findByMemcacheKey( memcacheKey ).size() == 0 ) {
                def studentWithLastEnrollmentMemcache = new Entity( "StudentWithLastEnrollmentMemcache" )
                studentWithLastEnrollmentMemcache.memcacheKey = memcacheKey
                studentWithLastEnrollmentMemcache.lastUpdateDate = new Date()
                
                if( user != null )
                    studentWithLastEnrollmentMemcache.lastUpdateUser = user.getEmail()
                    
                studentWithLastEnrollmentMemcache.save()
            }
        }
        
        return studentWithLastEnrollmentList
    }
    
    def static findByLimitAndOffsetAndURFUserEmail( def limit, def offset, def urfUserEmail ) {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "StudentWithLastEnrollment" );
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        def studentWithLastEnrollmentList
        def urfUserSchoolList
        def urfUserSchools = ""
        
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
        def memcacheKey = "studentWithLastEnrollmentList of " + urfUserSchools + " row " + ( offset + 1 ) + " to " + ( offset + limit )
        
        if( syncCache.contains( memcacheKey ) )
            studentWithLastEnrollmentList = ( List<Entity> )syncCache.get( memcacheKey )
        else {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
            Query q = new Query( "StudentWithLastEnrollment" )
            
            if( urfUserEmail == null )
                q.addFilter( "lastEnrollmentSchool", FilterOperator.EQUAL, "Test" )
            else {
                def lastEnrollmentSchoolFilterList = new ArrayList()
                
                urfUserSchoolList.each() {
                    lastEnrollmentSchoolFilterList.add new FilterPredicate( "lastEnrollmentSchool", FilterOperator.EQUAL, it.schoolName )
                }
                
                if( lastEnrollmentSchoolFilterList.size() == 0 )
                    return lastEnrollmentSchoolFilterList
                else if( lastEnrollmentSchoolFilterList.size() == 1 )
                    q.setFilter( lastEnrollmentSchoolFilterList.first() )
                else
                    q.setFilter( CompositeFilterOperator.or( lastEnrollmentSchoolFilterList ) )
            }
            
            q.addSort( "lastEnrollmentTermYear", SortDirection.DESCENDING )
            q.addSort( "lastEnrollmentTermNo", SortDirection.DESCENDING )
            q.addSort( "firstName", SortDirection.ASCENDING )
            q.addSort( "lastName", SortDirection.ASCENDING )
            studentWithLastEnrollmentList = datastore.prepare( q ).asList( FetchOptions.Builder.withLimit( limit + 1 ).offset( offset ) )
            
            syncCache.put( memcacheKey, studentWithLastEnrollmentList )
            
            if( StudentWithLastEnrollmentMemcache.findByMemcacheKey( memcacheKey ).size() == 0 ) {
                def studentWithLastEnrollmentMemcache = new Entity( "StudentWithLastEnrollmentMemcache" )
                studentWithLastEnrollmentMemcache.memcacheKey = memcacheKey
                studentWithLastEnrollmentMemcache.lastUpdateDate = new Date()
                
                if( urfUserEmail != null )
                    studentWithLastEnrollmentMemcache.lastUpdateUser = urfUserEmail
                    
                studentWithLastEnrollmentMemcache.save()
            }
        }
        
        return studentWithLastEnrollmentList
    }
    
    def static findByStudentId( def studentId ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "StudentWithLastEnrollment" )
        q.addFilter( "studentId", Query.FilterOperator.EQUAL, studentId )
        
        def studentWithLastEnrollmentList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
        
        if( studentWithLastEnrollmentList.size() == 0 )
            return null
        else
            return studentWithLastEnrollmentList.first()
    }
    
    def static isRequired( def fieldName ) throws InvalidFieldException {
        if( ["studentId", "firstName", "lastEnrollmentFirstClassAttended", "lastEnrollmentLastClassAttended", "lastEnrollmentSchool",
            "lastEnrollmentTermYear", "lastEnrollmentTermNo", "lastUpdateDate", "lastUpdateUser"].contains( fieldName ) )
            return true
        else if( ["lastName", "birthDate", "village", "genderCode", "specialInfo", "lastEnrollmentLeaveTermYear", "lastEnrollmentLeaveTermNo",
            "lastEnrollmentLeaveReasonCategory", "lastEnrollmentLeaveReason"].contains( fieldName ) )
            return false
        else
            throw new InvalidFieldException( "StudentWithLastEnrollment", fieldName )
    }
	
	static List<Entity> list() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "StudentWithLastEnrollment" )
		
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
}