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

/**
 * Data and Meta Data Accessor for EnrollmentWithTerms Entity.
 * 
 * @author awijasa
 */

class EnrollmentWithTerms {
    static List<Entity> findByStudentId( String studentId ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "EnrollmentWithTerms" )
        q.setFilter( new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId ) )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
    
    static Entity findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, def enrollTermNo, def enrollTermYear ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "EnrollmentWithTerms" )
        FilterPredicate studentIdFilter = new FilterPredicate( "studentId", FilterOperator.EQUAL, studentId )
        FilterPredicate schoolNameFilter = new FilterPredicate( "schoolName", FilterOperator.EQUAL, schoolName )
        FilterPredicate enrollTermNoFilter = new FilterPredicate( "enrollTermNo", FilterOperator.EQUAL, enrollTermNo )
        FilterPredicate enrollTermYearFilter = new FilterPredicate( "enrollTermYear", FilterOperator.EQUAL, enrollTermYear )
        q.setFilter( CompositeFilterOperator.and( studentIdFilter, schoolNameFilter, enrollTermNoFilter, enrollTermYearFilter ) )
        List<Entity> enrollmentWithTermsList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
        
        if( enrollmentWithTermsList.size() == 0 )
            return null
        else
            return enrollmentWithTermsList.first()
    }
    
    static Entity findLastEnrollmentWithTermsByStudentId( String studentId ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "EnrollmentWithTerms" )
        q.setFilter( new FilterPredicate( "studentId", FilterOperator.EQUAL, studentId ) )
        q.addSort( "leaveDate", Query.SortDirection.DESCENDING )
        q.addSort( "enrollDate", Query.SortDirection.DESCENDING )
		q.addSort( "schoolName", Query.SortDirection.DESCENDING )
        
        List<Entity> lastEnrollmentWithTerms = datastore.prepare( q ).asList( FetchOptions.Builder.withLimit( 1 ) )
        
        if( lastEnrollmentWithTerms.size() == 0 ) {
            return null
        }
        else
            return lastEnrollmentWithTerms.first()
    }
    
    static boolean isRequired( String fieldName ) throws InvalidFieldException {
        if( ["studentId", "schoolName", "firstClassAttended", "lastClassAttended", "enrollTermYear", "enrollTermNo", "enrollDate", "leaveDate", "lastUpdateDate",
            "lastUpdateUser"].contains( fieldName ) )
            return true
        else if( ["leaveTermYear", "leaveTermNo", "leaveReasonCategory", "leaveReason"].contains( fieldName ) )
            return false
        else
            throw new InvalidFieldException( "EnrollmentWithTerms", fieldName )
    }
    
    def static list() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "EnrollmentWithTerms" )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
}