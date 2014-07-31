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
import javax.servlet.http.HttpSession
import query.SortOption

/**
 * Data and Meta Data Accessor for Parent Entity.
 *
 * @author awijasa
 */

class Parent {
	static void deleteMemcache() {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "Parent" );
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        ParentMemcache.list().each() {
            syncCache.delete( it.memcacheKey )
            it.delete()
        }
    }

	static List<Entity> findByLimitAndOffset( int limit, int offset, HttpSession session ) {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "Parent" );
        User user = UserServiceFactory.getUserService().getCurrentUser()
        
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        List<Entity> parentList
        
        // GString can not be used when dealing with Memcache.
        String memcacheKey = "parentList row " + ( offset + 1 ) + " to " + ( offset + limit )
        
		int filterCount = 0
        List<FilterPredicate> filters = new ArrayList()
        
        if( session.getAttribute( "parentDeceasedIndFilter" ) != null && session.getAttribute( "parentDeceasedIndFilter" ) != "" ) {
			String filter = "deceasedInd = \"" + session.getAttribute( "parentDeceasedIndFilter" ) + "\""
			filters.add( new FilterPredicate( "deceasedInd", FilterOperator.EQUAL, session.getAttribute( "parentDeceasedIndFilter" ) ) )
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
        
        if( session.getAttribute( "parentEmailFilter" ) != null && session.getAttribute( "parentEmailFilter" ) != "" ) {
			String filter = "email = \"" + session.getAttribute( "parentEmailFilter" ) + "\""
			filters.add( new FilterPredicate( "email", FilterOperator.EQUAL, session.getAttribute( "parentEmailFilter" ) ) )
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
        
        if( session.getAttribute( "parentFirstNameFilter" ) != null && session.getAttribute( "parentFirstNameFilter" ) != "" ) {
			String filter = "firstName = \"" + session.getAttribute( "parentFirstNameFilter" ) + "\""
			filters.add( new FilterPredicate( "firstName", FilterOperator.EQUAL, session.getAttribute( "parentFirstNameFilter" ) ) )
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
        
        if( session.getAttribute( "parentIdFilter" ) != null && session.getAttribute( "parentIdFilter" ) != "" ) {
			String filter = "parentId = \"" + session.getAttribute( "parentIdFilter" ) + "\""
			filters.add( new FilterPredicate( "parentId", FilterOperator.EQUAL, session.getAttribute( "parentIdFilter" ) ) )
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
        
        if( session.getAttribute( "parentLastNameFilter" ) != null && session.getAttribute( "parentLastNameFilter" ) != "" ) {
			String filter = "lastName = \"" + session.getAttribute( "parentLastNameFilter" ) + "\""
			filters.add( new FilterPredicate( "lastName", FilterOperator.EQUAL, session.getAttribute( "parentLastNameFilter" ) ) )
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
        
        if( session.getAttribute( "parentPrimaryPhoneFilter" ) != null && session.getAttribute( "parentPrimaryPhoneFilter" ) != "" ) {
			String filter = "primaryPhone = \"" + session.getAttribute( "parentPrimaryPhoneFilter" ) + "\""
			filters.add( new FilterPredicate( "primaryPhone", FilterOperator.EQUAL, session.getAttribute( "parentPrimaryPhoneFilter" ) ) )
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
        
        if( session.getAttribute( "parentProfessionFilter" ) != null && session.getAttribute( "parentProfessionFilter" ) != "" ) {
			String filter = "profession = \"" + session.getAttribute( "parentProfessionFilter" ) + "\""
			filters.add( new FilterPredicate( "primaryPhone", FilterOperator.EQUAL, session.getAttribute( "parentPrimaryPhoneFilter" ) ) )
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
        
        if( session.getAttribute( "parentSecondaryPhoneFilter" ) != null && session.getAttribute( "parentSecondaryPhoneFilter" ) != "" ) {
			String filter = "secondaryPhone = \"" + session.getAttribute( "parentSecondaryPhoneFilter" ) + "\""
			filters.add( new FilterPredicate( "secondaryPhone", FilterOperator.EQUAL, session.getAttribute( "parentSecondaryPhoneFilter" ) ) )
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
        
        if( session.getAttribute( "parentVillageFilter" ) != null && session.getAttribute( "parentVillageFilter" ) != "" ) {
			String filter = "village = \"" + session.getAttribute( "parentVillageFilter" ) + "\""
			filters.add( new FilterPredicate( "village", FilterOperator.EQUAL, session.getAttribute( "parentVillageFilter" ) ) )
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
        
        Map<Integer, SortOption> sortOptionMap = new LinkedHashMap()
		
		if( session.getAttribute( "parentFirstNameSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "parentFirstNameSortOrder" ),
				new SortOption(
					fieldName: "firstName",
					fieldType: "text",
					sortDirection: session.getAttribute( "parentFirstNameSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
				)
			)
		}
		
		if( session.getAttribute( "parentLastNameSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "parentLastNameSortOrder" ),
				new SortOption(
					fieldName: "lastName",
					fieldType: "text",
					sortDirection: session.getAttribute( "parentLastNameSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
				)
			)
		}
		
		if( sortOptionMap.size() > 0 ) {
			memcacheKey += " order by "
			sortOptionMap = sortOptionMap.sort()
			
			sortOptionMap.each(
				{ key, value ->
					
					if( key == 1 )
						memcacheKey += value.getFieldName()
					else
						memcacheKey += ", " + value.getFieldName()
						
					if( value.getSortDirection() == SortDirection.ASCENDING )
						memcacheKey += " asc"
					else
						memcacheKey += " dsc"
				}
			)
		}
        
        if( syncCache.contains( memcacheKey ) )
            parentList = ( List<Entity> )syncCache.get( memcacheKey )
        else {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
            Query q = new Query( "Parent" )
            
            if( filters.size() == 1 )
            	q.setFilter( filters.first() )
            else if( filters.size() > 1 )
            	q.setFilter( CompositeFilterOperator.and( filters ) )            
            
            sortOptionMap.each(
				{ key, value ->
					q.addSort( value.getFieldName(), value.getSortDirection() )
				}
			)
			
            parentList = datastore.prepare( q ).asList( FetchOptions.Builder.withLimit( limit + 1 ).offset( offset ) )
            
            syncCache.put( memcacheKey, parentList )
            
            if( ParentMemcache.findByMemcacheKey( memcacheKey ).size() == 0 ) {
                Entity parentMemcache = new Entity( "ParentMemcache" )
                parentMemcache.memcacheKey = memcacheKey
                parentMemcache.lastUpdateDate = new Date()
                parentMemcache.lastUpdateUser = user?.getEmail()
                    
                parentMemcache.save()
            }
        }
        
        return parentList
    }

	static Entity findByParentId( String parentId ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Parent" )
        q.setFilter( new FilterPredicate( "parentId", Query.FilterOperator.EQUAL, parentId ) )
        
        List<Entity> parentList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
        
        if( parentList.size() == 0 )
            return null
        else
            return parentList.first()
    }
	
	static String[] getPrimaryKey() {
		return ["parentId"]
	}
    
    static boolean isRequired( String fieldName ) throws InvalidFieldException {
        if( ["parentId", "firstName", "lastUpdateDate", "lastUpdateUser"].contains( fieldName ) )
            return true
        else if( ["lastName", "deceasedInd", "village", "primaryPhone", "secondaryPhone", "email", "profession"].contains( fieldName ) )
            return false
        else
            throw new InvalidFieldException( "Parent", fieldName )
    }
	
	static boolean isUsed( Entity parent ) {
		Payment.findByFundingSource( parent.parentId ).size() > 0
	}
    
    static List<Entity> list() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Parent" )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
}