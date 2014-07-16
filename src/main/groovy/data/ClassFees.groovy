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
 * Data and Meta Data Accessor for ClassFees Entity.
 * 
 * @author awijasa
 */

class ClassFees {
    
	static List<Entity> findBySchoolName( String schoolName ) {
		List<Entity> classFeesList
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "ClassFees" )
		q.setFilter( new FilterPredicate( "schoolName", FilterOperator.EQUAL, schoolName ) )
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static List<Entity> findBySchoolNameAndClass( String schoolName, String schoolClass ) {
		List<Entity> classFeesList
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "ClassFees" )
		FilterPredicate schoolNameFilter = new FilterPredicate( "schoolName", FilterOperator.EQUAL, schoolName )
		FilterPredicate classFilter = new FilterPredicate( "class", FilterOperator.EQUAL, schoolClass )
		q.setFilter( CompositeFilterOperator.and( schoolNameFilter, classFilter ) )
		classFeesList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
		
		return classFeesList
	}
	
	static Entity findBySchoolNameAndClassAndTermNoAndTermYear( String schoolName, String schoolClass, Number termNo, Number termYear ) {
		List<Entity> classFeesList
		
		Key ancestorKey = Term.findByTermSchoolAndTermNoAndYear( schoolName, termNo, termYear )?.getKey()
			
		if( ancestorKey == null )
			return null
		else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
			Query q = new Query( "ClassFees", ancestorKey )
			q.setFilter( new FilterPredicate( "class", FilterOperator.EQUAL, schoolClass ) )
			q.addSort( "classLevel", SortDirection.ASCENDING )
			classFeesList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
			
			if( classFeesList.size() == 0 )
				return null
			else
				return classFeesList.first()
		}
	}
	
	static List<Entity> findBySchoolNameAndTermNoAndTermYear( String schoolName, Number termNo, Number termYear ) {
		List<Entity> classFeesList
		
		Key ancestorKey = Term.findByTermSchoolAndTermNoAndYear( schoolName, termNo, termYear )?.getKey()
			
		if( ancestorKey == null )
			classFeesList = new ArrayList()
		else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
			Query q = new Query( "ClassFees", ancestorKey )
			q.addSort( "classLevel", SortDirection.ASCENDING )
			classFeesList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
		}
		
		return classFeesList
	}
	
	static List<Entity> findByTerm( Entity term ) {
		List<Entity> classFeesList
		
		Key ancestorKey = term?.getKey()
			
		if( ancestorKey == null )
			classFeesList = new ArrayList()
		else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
			Query q = new Query( "ClassFees", ancestorKey )
			q.addSort( "classLevel", SortDirection.ASCENDING )
			classFeesList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
		}
		
		return classFeesList
	}
	
	static String[] getPrimaryKey() {
		["schoolName", "class", "termNo", "termYear"]
	}
	
	static boolean isRequired( String fieldName ) throws InvalidFieldException {
        if( ["schoolName", "class", "classLevel", "termNo", "termYear", "lastUpdateDate", "lastUpdateUser"].contains( fieldName ) )
            return true
		else if( ["tuitionFee", "boardingFee"] )
			return false
        else
            throw new InvalidFieldException( "Class", fieldName )
    }
	
	static boolean isUsed( Entity classFees ) {
		ClassAttended.findBySchoolNameAndClassAndClassTermNoAndClassTermYear( classFees.schoolName, classFees.getProperty( "class" ), classFees.termNo, classFees.termYear ).size() > 0
	}
}