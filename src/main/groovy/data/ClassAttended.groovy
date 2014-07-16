/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data

import java.util.List;

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

/**
 * Data and Meta Data Accessor for ClassAttended Entity.
 * 
 * @author awijasa
 */

class ClassAttended {
    
	static List<Entity> findByEnrollment( Entity enrollment ) {
		List<Entity> classAttendedList
		
		Key ancestorKey = enrollment?.getKey()
		
		if( ancestorKey == null )
			classAttendedList = new ArrayList()
		else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
			Query q = new Query( "ClassAttended", ancestorKey )
			q.addSort( "classLevel", SortDirection.ASCENDING )
			q.addSort( "classTermYear", SortDirection.ASCENDING )
			q.addSort( "classTermNo", SortDirection.ASCENDING )
			classAttendedList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
		}
		
		return classAttendedList
	}
	
	static List<Entity> findBySchoolNameAndClass( String schoolName, String schoolClass ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "ClassAttended" )
		
		q.setFilter( CompositeFilterOperator.and(
				new FilterPredicate( "schoolName", FilterOperator.EQUAL, schoolName ),
				new FilterPredicate( "class", FilterOperator.EQUAL, schoolClass )
			)
		)
		
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static List<Entity> findBySchoolNameAndClassAndClassTermNoAndClassTermYear( String schoolName, String schoolClass, Number classTermNo, Number classTermYear ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "ClassAttended" )
		
		q.setFilter( CompositeFilterOperator.and(
				new FilterPredicate( "schoolName", FilterOperator.EQUAL, schoolName ),
				new FilterPredicate( "class", FilterOperator.EQUAL, schoolClass ),
				new FilterPredicate( "classTermNo", FilterOperator.EQUAL, classTermNo ),
				new FilterPredicate( "classTermYear", FilterOperator.EQUAL, classTermYear )
			)
		)
		
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static List<Entity> findBySchoolNameAndClassTermNoAndClassTermYear( String schoolName, Number classTermNo, Number classTermYear ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "ClassAttended" )
		
		q.setFilter( CompositeFilterOperator.and(
				new FilterPredicate( "schoolName", FilterOperator.EQUAL, schoolName ),
				new FilterPredicate( "classTermNo", FilterOperator.EQUAL, classTermNo ),
				new FilterPredicate( "classTermYear", FilterOperator.EQUAL, classTermYear )
			)
		)
		
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static List<Entity> findByStudentId( String studentId ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "ClassAttended" )
		q.setFilter( new FilterPredicate( "studentId", FilterOperator.EQUAL, studentId ) )
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static List<Entity> findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, Number enrollTermNo, Number enrollTermYear ) {
		List<Entity> classAttendedList
		
		Key ancestorKey = Enrollment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( studentId, schoolName, enrollTermNo, enrollTermYear )?.getKey()
		
		if( ancestorKey == null )
			classAttendedList = new ArrayList()
		else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
			Query q = new Query( "ClassAttended", ancestorKey )
			classAttendedList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
		}
		
		return classAttendedList
	}
	
	static Entity findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( String studentId, String schoolName, Number classTermNo, Number classTermYear ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "ClassAttended" )
		
		q.setFilter( CompositeFilterOperator.and(
				new FilterPredicate( "studentId", FilterOperator.EQUAL, studentId ),
				new FilterPredicate( "schoolName", FilterOperator.EQUAL, schoolName ),
				new FilterPredicate( "classTermNo", FilterOperator.EQUAL, classTermNo ),
				new FilterPredicate( "classTermYear", FilterOperator.EQUAL, classTermYear )
			)
		)
		
		List<Entity> classAttendedList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
		
		if( classAttendedList.size() == 0 )
			return null
		else
			return classAttendedList.first()
	}
	
	static String[] getPrimaryKey() {
		["studentId", "schoolName", "classTermNo", "classTermYear"]
	}
	
	static boolean isRequired( String fieldName ) throws InvalidFieldException {
        if( ["studentId", "schoolName", "enrollTermNo", "enrollTermYear", "class", "classLevel", "classTermNo", "classTermYear", "boardingInd", "lastUpdateDate", "lastUpdateUser"].contains( fieldName ) )
            return true
		else if( ["tuitionFee", "boardingFee"] )
			return false
        else
            throw new InvalidFieldException( "ClassAttended", fieldName )
    }
	
	static boolean isUsed( Entity classAttended ) {
		return false
	}
}