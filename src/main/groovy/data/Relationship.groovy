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
import com.google.appengine.api.users.User
import com.google.appengine.api.users.UserServiceFactory

/**
 * Data and Meta Data Accessor for Relationship Entity.
 *
 * @author awijasa
 */

class Relationship {
	static List<Entity> findByParentId( String parentId ) {
		List<Entity> relationshipList
		
		Key ancestorKey = Parent.findByParentId( parentId )?.getKey()
		
		if( ancestorKey == null )
			relationshipList = new ArrayList()
		else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
	        Query q = new Query( "Relationship", ancestorKey )
	        
	        relationshipList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
		}
		
		return relationshipList
	}
	
	static List<Entity> findByParentalRelationship( String parentalRelationship ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Relationship" )
        q.setFilter( new FilterPredicate( "parentalRelationship", Query.FilterOperator.EQUAL, parentalRelationship ) )
        
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static Entity findByParentIdAndStudentId( String parentId, String studentId ) {
		Key ancestorKey = Parent.findByParentId( parentId )?.getKey()
		
		if( ancestorKey == null )
			return null
		else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
	        Query q = new Query( "Relationship", ancestorKey )
	        FilterPredicate studentIdFilter = new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId )
	        q.setFilter( studentIdFilter )
	        
	        List<Entity> relationshipList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	        
	        if( relationshipList.size() == 0 )
	        	return null
	        else
	        	return relationshipList.first()
		}
	}
	
	static List<Entity> findByStudentId( String studentId ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Relationship" )
        q.setFilter( new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId ) )
        
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	static List<Entity> findByStudentIdAndLimitAndOffset( String studentId, int limit, int offset ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Relationship" )
		q.setFilter( new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId ) )
		
		List<Entity> relationshipList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
		
		relationshipList.sort(
			{ x, y ->
				if( x.parentalRelationship == y.parentalRelationship )
					x.guardianRelationship <=> y.guardianRelationship
				else
					( x.parentalRelationship?: "zzz" ) <=> ( y.parentalRelationship?: "zzz" )
			}
		)
		
		int toIndex = offset + limit + 1
		
		if( relationshipList.size() < offset + limit + 1 )
			toIndex = relationshipList.size()
		
		return relationshipList.subList( offset, toIndex )
	}
	
	static List<Entity> findGuardianRelationshipsByParentAndUserPrivilege( Entity parent ) {
		List<Entity> relationshipList
		
		Key ancestorKey = parent?.getKey()
		
		if( ancestorKey == null )
			relationshipList = new ArrayList()
		else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
			Query q = new Query( "Relationship", ancestorKey )
			q.addSort( "guardianRelationship", SortDirection.ASCENDING )
			
			List<Entity> allRelatives = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
			
			User user = UserServiceFactory.getUserService().getCurrentUser()
			Entity urfUser = URFUser.findByEmail( user?.getEmail() )
			
			relationshipList = new ArrayList()
			
			allRelatives.each(
				{
					String schoolName = StudentDocument.findByStudentId( it.studentId ).getOnlyField( "lastEnrollmentSchool" ).getText()
					
					if( it.parentalRelationship == null ) {
						if( urfUser == null && schoolName == "Test" )
							relationshipList.add( it )
						else if( UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), schoolName ) != null )
							relationshipList.add( it )
					}
				}
			)
		}
		
		return relationshipList
	}

	static List<Entity> findGuardianRelationshipsByParentIdAndUserPrivilege( String parentId ) {
        List<Entity> relationshipList
		
		Key ancestorKey = Parent.findByParentId( parentId )?.getKey()
		
		if( ancestorKey == null )
			relationshipList = new ArrayList()
		else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
	        Query q = new Query( "Relationship", ancestorKey )
	        q.addSort( "guardianRelationship", SortDirection.ASCENDING )
	        
	        List<Entity> allRelatives = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	        
	        User user = UserServiceFactory.getUserService().getCurrentUser()
	        Entity urfUser = URFUser.findByEmail( user?.getEmail() )
	        
	        relationshipList = new ArrayList()
	        
	        allRelatives.each(
	        	{
	        		String schoolName = StudentDocument.findByStudentId( it.studentId ).getOnlyField( "lastEnrollmentSchool" ).getText()
	        		
	        		if( it.parentalRelationship == null ) {
	        			if( urfUser == null && schoolName == "Test" )
	        				relationshipList.add( it )
	        			else if( UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), schoolName ) != null )
	        				relationshipList.add( it )
					}
	        	}
	        )
		}
        
        return relationshipList
    }
	
	static List<Entity> findGuardianRelationshipsByStudentId( String studentId ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Relationship" )
		q.setFilter( new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId ) )
		q.addSort( "guardianRelationship", SortDirection.ASCENDING )
		
		List<Entity> allRelatives = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
		List<Entity> relationshipList = new ArrayList()
		
		allRelatives.each(
			{
				if( it.parentalRelationship == null )
					relationshipList.add( it )
			}
		)
		
		return relationshipList
	}
	
	static List<Entity> findParentalRelationshipsByParentAndUserPrivilege( Entity parent ) {
		List<Entity> relationshipList
		
		Key ancestorKey = parent?.getKey()
		
		if( ancestorKey == null )
			relationshipList = new ArrayList()
		else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
			Query q = new Query( "Relationship", ancestorKey )
			q.addSort( "parentalRelationship", SortDirection.ASCENDING )
			
			List<Entity> allChildren = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
			
			User user = UserServiceFactory.getUserService().getCurrentUser()
			Entity urfUser = URFUser.findByEmail( user?.getEmail() )
			
			relationshipList = new ArrayList()
			
			allChildren.each(
				{
					String schoolName = StudentDocument.findByStudentId( it.studentId ).getOnlyField( "lastEnrollmentSchool" ).getText()
					
					if( it.guardianRelationship == null )
						if( urfUser == null && schoolName == "Test" )
							relationshipList.add( it )
						else if( UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), schoolName ) != null )
							relationshipList.add( it )
				}
			)
		}
		
		return relationshipList
	}
	
	static List<Entity> findParentalRelationshipsByParentId( String parentId ) {
		List<Entity> relationshipList
		
		Key ancestorKey = Parent.findByParentId( parentId )?.getKey()
		
		if( ancestorKey == null )
			relationshipList = new ArrayList()
		else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
			Query q = new Query( "Relationship", ancestorKey )
			q.addSort( "parentalRelationship", SortDirection.ASCENDING )
			
			List<Entity> allChildren = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
			
			relationshipList = new ArrayList()
			
			allChildren.each(
				{
					if( it.guardianRelationship == null )
						relationshipList.add( it )
				}
			)
		}
		
		return relationshipList
	}
	
	static List<Entity> findParentalRelationshipsByParentIdAndUserPrivilege( String parentId ) {
		List<Entity> relationshipList
		
		Key ancestorKey = Parent.findByParentId( parentId )?.getKey()
		
		if( ancestorKey == null )
			relationshipList = new ArrayList()
		else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
			Query q = new Query( "Relationship", ancestorKey )
			q.addSort( "parentalRelationship", SortDirection.ASCENDING )
			
			List<Entity> allChildren = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
			
			User user = UserServiceFactory.getUserService().getCurrentUser()
			Entity urfUser = URFUser.findByEmail( user?.getEmail() )
			
			relationshipList = new ArrayList()
			
			allChildren.each(
				{
					String schoolName = StudentDocument.findByStudentId( it.studentId ).getOnlyField( "lastEnrollmentSchool" ).getText()
					
					if( it.guardianRelationship == null )
						if( urfUser == null && schoolName == "Test" )
							relationshipList.add( it )
						else if( UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), schoolName ) != null )
							relationshipList.add( it )
				}
			)
		}
		
		return relationshipList
	}
	
	static List<Entity> findParentalRelationshipsByStudentIdAndParentalRelationship( String studentId, String parentalRelationship ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Relationship" )
		FilterPredicate parentalRelationshipFilter = new FilterPredicate( "parentalRelationship", Query.FilterOperator.EQUAL, parentalRelationship )
		FilterPredicate studentIdFilter = new FilterPredicate( "studentId", Query.FilterOperator.EQUAL, studentId )
		q.setFilter( CompositeFilterOperator.and( parentalRelationshipFilter, studentIdFilter ) )
		
		return datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
    
	static String[] getPrimaryKey() {
    	return ["parentId", "studentId"]
    }
    
    static boolean isRequired( String fieldName ) throws InvalidFieldException {
        if( ["parentId", "studentId", "lastUpdateDate", "lastUpdateUser"].contains( fieldName ) )
            return true
        else if( ["parentalRelationship", "guardianRelationship"].contains( fieldName ) )
            return false
        else
            throw new InvalidFieldException( "Relationship", fieldName )
    }
    
    static List<Entity> list() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Relationship" )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
}