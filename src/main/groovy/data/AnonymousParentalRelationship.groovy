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

/**
 * Data and Meta Data Accessor for AnonymousParentalRelationship Entity.
 *
 * @author awijasa
 */

class AnonymousParentalRelationship {
    def static findByParentalRelationship( def parentalRelationship ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "AnonymousParentalRelationship" )
        q.addFilter( "parentalRelationship", FilterOperator.EQUAL, parentalRelationship )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
    
    static List<Entity> findByStudentId( String studentId ) {
		Key ancestorKey = Student.findByStudentId( studentId )?.getKey()
		List<Entity> anonymousParentalRelationshipList
		
		if( ancestorKey == null )
			anonymousParentalRelationshipList = new ArrayList()
		else {
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
	        Query q = new Query( "AnonymousParentalRelationship", ancestorKey )
	        q.addSort( "parentalRelationship", SortDirection.ASCENDING )
	        anonymousParentalRelationshipList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
		}
		
		return anonymousParentalRelationshipList
    }
    
    static Entity findByStudentIdAndParentalRelationship( String studentId, String parentalRelationship ) {
        Key ancestorKey = Student.findByStudentId( studentId )?.getKey()
		
		if( ancestorKey == null )
			return null
		else {
			FilterPredicate parentalRelationshipFilter = new FilterPredicate( "parentalRelationship", FilterOperator.EQUAL, parentalRelationship )
	        
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
	        Query q = new Query( "AnonymousParentalRelationship", ancestorKey ).setFilter( parentalRelationshipFilter )
	        
	        List<Entity> anonymousParentalRelationshipList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	        
	        if( anonymousParentalRelationshipList.size() == 0 )
	            return null
	        else
	            return anonymousParentalRelationshipList.first()
		}
    }
	
	static List<Entity> findByStudent( Entity student ) {
		Key ancestorKey = student?.getKey()
		List<Entity> anonymousParentalRelationshipList
		
		if( ancestorKey == null )
			anonymousParentalRelationshipList = new ArrayList()
		else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
			Query q = new Query( "AnonymousParentalRelationship", ancestorKey )
			q.addSort( "parentalRelationship", SortDirection.ASCENDING )
			anonymousParentalRelationshipList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
		}
		
		return anonymousParentalRelationshipList
	}
    
    def static isRequired( def fieldName ) throws InvalidFieldException {
        if( ["studentId", "parentalRelationship", "deceasedInd", "lastUpdateDate", "lastUpdateUser"].contains( fieldName ) )
            return true
        else
            throw new InvalidFieldException( "AnonymousParentalRelationship", fieldName )
    }
	
	static List<Entity> list() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "AnonymousParentalRelationship" )
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
}