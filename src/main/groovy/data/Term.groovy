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
import com.google.appengine.api.users.UserServiceFactory
import java.util.logging.Level

/**
 * Data and Meta Data Accessor for Term Entity.
 *
 * @author awijasa
 */

class Term {
    def static deleteMemcache( def termSchool ) {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        // GString can not be used when dealing with Memcache.
        syncCache.delete( termSchool + " Terms" )
        
        syncCache = MemcacheServiceFactory.getMemcacheService( "Term" );
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        TermMemcache.list().each() {
            syncCache.delete( it.memcacheKey )
            it.delete()
        }
    }
    
    def static findByLimitAndOffset( def limit, def offset ) {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "Term" );
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        def termList
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
        def memcacheKey = "termList of " + urfUserSchools + " row " + ( offset + 1 ) + " to " + ( offset + limit )
        
        if( syncCache.contains( memcacheKey ) )
            termList = ( List<Entity> )syncCache.get( memcacheKey )
        else {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
            Query q = new Query( "Term" )
            
            if( urfUser == null )
                q.addFilter( "termSchool", FilterOperator.EQUAL, "Test" )
            else {
                def termSchoolFilterList = new ArrayList()
                
                urfUserSchoolList.each() {
                    termSchoolFilterList.add new FilterPredicate( "termSchool", FilterOperator.EQUAL, it.schoolName )
                }
                
                if( termSchoolFilterList.size() == 0 )
                    return termSchoolFilterList
                else if( termSchoolFilterList.size() == 1 )
                    q.setFilter( termSchoolFilterList.first() )
                else
                    q.setFilter( CompositeFilterOperator.or( termSchoolFilterList ) )
            }
            
            q.addSort( "termSchool", SortDirection.ASCENDING )
            q.addSort( "startDate", SortDirection.DESCENDING )
            termList = datastore.prepare( q ).asList( FetchOptions.Builder.withLimit( limit + 1 ).offset( offset ) )
            
            syncCache.put( memcacheKey, termList )
            
            if( TermMemcache.findByMemcacheKey( memcacheKey ).size() == 0 ) {
                def termMemcache = new Entity( "TermMemcache" )
                termMemcache.memcacheKey = memcacheKey
                termMemcache.lastUpdateDate = new Date()
                
                if( user != null )
                    termMemcache.lastUpdateUser = user.getEmail()
                    
                termMemcache.save()
            }
        }
        
        return termList
    }
    
    def static findByTermSchool( def termSchool ) {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        def schoolTerms
        
        if( syncCache.contains( termSchool + " Terms" ) )
            schoolTerms = ( List<Entity> )syncCache.get( termSchool + " Terms" )
        else {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
            Query q = new Query( "Term" )
            q.addFilter( "termSchool", FilterOperator.EQUAL, termSchool )
            q.addSort( "startDate", SortDirection.DESCENDING )
            schoolTerms = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
            
            syncCache.put( termSchool + " Terms", schoolTerms )
        }
        
        return schoolTerms
    }
    
    def static findByTermSchoolAndDate( def termSchool, def date ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Term" )
        q.addFilter( "termSchool", FilterOperator.EQUAL, termSchool )
        q.addFilter( "startDate", FilterOperator.LESS_THAN_OR_EQUAL, date )
        
        def term
        
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() ).each() {
            if( it.endDate >= date )
                term = it
        }
        
        return term
    }
    
    def static findByTermSchoolAndTermNoAndYear( def termSchool, def termNo, def year ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Term" )
        q.addFilter( "termSchool", FilterOperator.EQUAL, termSchool )
        q.addFilter( "termNo", FilterOperator.EQUAL, termNo )
        q.addFilter( "year", FilterOperator.EQUAL, year )
        
        def termList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
        
        if( termList.size() == 0 )
            return null
        else
            return termList.first()
    }
    
    static List<Entity> findByTermSchoolAndTermRange( String termSchool, Entity startTerm, Entity endTerm, Date currentDate ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Term" )
        q.setFilter( CompositeFilterOperator.and(
            new FilterPredicate( "termSchool", FilterOperator.EQUAL, termSchool )
            , new FilterPredicate( "startDate", FilterOperator.GREATER_THAN_OR_EQUAL, startTerm.startDate )
            , new FilterPredicate( "startDate", FilterOperator.LESS_THAN_OR_EQUAL, currentDate ) ) )
		q.addSort( "startDate", SortDirection.ASCENDING )
        
        List<Entity> termsAfterStartTerm = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
        
        List<Entity> termsBetweenStartTermAndEndTerm = new ArrayList()
        
        if( endTerm == null )
            return termsAfterStartTerm
        else {
            for( Entity term: termsAfterStartTerm ) {
                if( term.endDate <= endTerm.endDate )
                    termsBetweenStartTermAndEndTerm.push( term )
				else
					break
            }
            
            return termsBetweenStartTermAndEndTerm
        }
    }
	
	static List<Entity> findByTermSchoolAndYearAndTermRange( String termSchool, Number year, Entity startTerm, Entity endTerm, Date currentDate ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Term" )
        q.setFilter( CompositeFilterOperator.and(
            new FilterPredicate( "termSchool", FilterOperator.EQUAL, termSchool )
			, new FilterPredicate( "year", FilterOperator.EQUAL, year )
            , new FilterPredicate( "startDate", FilterOperator.GREATER_THAN_OR_EQUAL, startTerm.startDate )
            , new FilterPredicate( "startDate", FilterOperator.LESS_THAN_OR_EQUAL, currentDate ) ) )
		q.addSort( "startDate", SortDirection.ASCENDING )
        
        List<Entity> termsAfterStartTerm = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
        
        List<Entity> termsBetweenStartTermAndEndTerm = new ArrayList()
        
        if( endTerm == null )
            return termsAfterStartTerm
        else {
            for( Entity term: termsAfterStartTerm ) {
                if( term.endDate <= endTerm.endDate )
                    termsBetweenStartTermAndEndTerm.push( term )
				else
					break
            }
            
            return termsBetweenStartTermAndEndTerm
        }
    }
    
    def static findOverlappedTerm( def suppliedTerm ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Term" )
        q.setFilter( CompositeFilterOperator.and(
            new FilterPredicate( "termSchool", FilterOperator.EQUAL, suppliedTerm.termSchool )
            , new FilterPredicate( "startDate", FilterOperator.GREATER_THAN_OR_EQUAL, suppliedTerm.startDate )
            , new FilterPredicate( "startDate", FilterOperator.LESS_THAN_OR_EQUAL, suppliedTerm.endDate ) ) )
    
        def termsWithStartDateBetweenSuppliedTermRange = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
        
        for( def term: termsWithStartDateBetweenSuppliedTermRange )
            if( term.termNo != suppliedTerm.termNo || term.year != suppliedTerm.year )
                return term
        
        q = new Query( "Term" )
        q.setFilter( CompositeFilterOperator.and(
            new FilterPredicate( "termSchool", FilterOperator.EQUAL, suppliedTerm.termSchool )
            , new FilterPredicate( "startDate", FilterOperator.LESS_THAN_OR_EQUAL, suppliedTerm.startDate ) ) )

        def termsWithStartDateBeforeSuppliedTermStartDate = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )

        for( def term: termsWithStartDateBeforeSuppliedTermStartDate )
            if( ( term.termNo != suppliedTerm.termNo || term.year != suppliedTerm.year ) && term.endDate >= suppliedTerm.startDate )
                return term

        return null
    }
	
	static List<Number> findYearsByTermSchoolAndTermRange( String termSchool, Entity startTerm, Entity endTerm, Date currentDate ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Term" )
		q.setFilter( CompositeFilterOperator.and(
			new FilterPredicate( "termSchool", FilterOperator.EQUAL, termSchool )
			, new FilterPredicate( "startDate", FilterOperator.GREATER_THAN_OR_EQUAL, startTerm.startDate )
			, new FilterPredicate( "startDate", FilterOperator.LESS_THAN_OR_EQUAL, currentDate ) ) )
		q.addSort( "startDate", SortDirection.ASCENDING )
		
		List<Entity> termsAfterStartTerm = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
		
		List<Number> yearsBetweenStartTermAndEndTerm = new ArrayList()
		
		for( Entity term: termsAfterStartTerm ) {
			if( endTerm == null || term.endDate <= endTerm.endDate ) {
				if( !yearsBetweenStartTermAndEndTerm.contains( term.year ) )
					yearsBetweenStartTermAndEndTerm.push( term.year )
			}
			else
				break
		}
		
		return yearsBetweenStartTermAndEndTerm
	}
    
    def static getPrimaryKey() {
        ["termSchool", "termNo", "year"]
    }
    
    def static isRequired( def fieldName ) throws InvalidFieldException {
        if( ["termSchool", "termNo", "year", "startDate", "endDate", "lastUpdateDate", "lastUpdateUser"].contains( fieldName ) )
            return true
        else if( ["tuitionFee", "boardingFee"].contains( fieldName ) )
            return false
        else
            throw new InvalidFieldException( "Term", fieldName )
    }
    
    static boolean isUsed( Entity term ) {
        ClassAttended.findBySchoolNameAndClassTermNoAndClassTermYear( term.termSchool, term.termNo, term.year ).size() > 0
    }
    
    def static list() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Term" )
        
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
}