/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data

import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.memcache.ConsistentLogAndContinueErrorHandler
import com.google.appengine.api.memcache.MemcacheService
import com.google.appengine.api.memcache.MemcacheServiceFactory
import com.google.appengine.api.search.IndexSpec
import com.google.appengine.api.search.Query
import com.google.appengine.api.search.QueryOptions
import com.google.appengine.api.search.Results
import com.google.appengine.api.search.ScoredDocument
import com.google.appengine.api.search.SearchServiceFactory
import com.google.appengine.api.search.SortExpression
import com.google.appengine.api.search.SortExpression.SortDirection
import com.google.appengine.api.search.SortOptions
import com.google.appengine.api.users.User
import com.google.appengine.api.users.UserService
import com.google.appengine.api.users.UserServiceFactory

import java.util.List;
import java.util.logging.Level

import javax.servlet.http.HttpSession

import query.SortOption

/**
 * Data and Meta Data Accessor for Enrollment ScoredDocument.
 * 
 * @author awijasa
 */

class EnrollmentDocument {
    def static deleteMemcache() {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "Enrollment" );
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        EnrollmentMemcache.list().each() {
            syncCache.delete( it.memcacheKey )
            it.delete()
        }
    }
	
	static Results<ScoredDocument> findBySchoolName( String schoolName ) {
		int limit = Enrollment.findBySchoolName( schoolName ).size()
		
		QueryOptions options = QueryOptions.newBuilder()
			.setLimit( limit )
			.setNumberFoundAccuracy( limit )
			.setOffset( 0 )
			.setFieldsToReturn(
					"studentId",
					"firstName",
					"lastName",
					"classesAttended",
					"firstClassAttended",
					"lastClassAttended",
					"termsEnrolled",
					"enrollTermYear",
					"enrollTermNo",
					"leaveTermYear",
					"leaveTermNo",
					"leaveTermEndDate",
					"birthDate",
					"village",
					"specialInfo",
					"genderCode",
					"schoolName",
					"leaveReasonCategory",
					"leaveReason",
					"tuitionFees",
					"boardingFees",
					"otherFees",
					"payments",
					"feesDue",
					"lastUpdateDate",
					"lastUpdateUser"
				)
			.build();
		
		Query query = Query.newBuilder().setOptions( options ).build( "schoolName: ${ schoolName }" );
		
		SearchServiceFactory.getSearchService().getIndex( IndexSpec.newBuilder().setName( "Enrollment" ).build() ).search( query );
	}
    
    static Results<ScoredDocument> findByLimitAndOffset( int limit, int offset, HttpSession session ) {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "Enrollment" );
		syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
		
		String searchQuery
		Results<ScoredDocument> enrollmentResults
		List<Entity> urfUserSchoolList
		String urfUserSchools = ""
		
		User user = UserServiceFactory.getUserService().getCurrentUser()
		Entity urfUser = URFUser.findByEmail( user?.getEmail() )
		
		if( urfUser == null )
			urfUserSchools = "[Test]"
		else {
			searchQuery = "schoolName = ( "
			urfUserSchoolList = UserPrivilege.findByUserEmail( urfUser.email )
			
			urfUserSchoolList.eachWithIndex(
				{ urfUserSchool, index ->
				
					// GString can not be used when dealing with Memcache.
					urfUserSchools += "[" + urfUserSchool.schoolName + "]"
					
					if( index == 0 )
						searchQuery += "\"${ urfUserSchool.schoolName }\""
					else
						searchQuery += " OR \"${ urfUserSchool.schoolName }\""
				}
			)
			
			searchQuery += " )"
		}
		
		// GString can not be used when dealing with Memcache.
		String memcacheKey = "enrollmentResults of " + urfUserSchools + " row " + ( offset + 1 ) + " to " + ( offset + limit )
		int filterCount = 0
		
		if( session.getAttribute( "enrollmentClassesAttendedFilter" ) != null && session.getAttribute( "enrollmentClassesAttendedFilter" ) != "" ) {
			String filter = "classesAttended = \"" + session.getAttribute( "enrollmentClassesAttendedFilter" ) + "\""
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if(
			session.getAttribute( "enrollmentFeesDueFilter" ) != null &&
			session.getAttribute( "enrollmentFeesDueFilter" ) != "" &&
			session.getAttribute( "enrollmentFeesDueFilterOperator" ) != null &&
			session.getAttribute( "enrollmentFeesDueFilterOperator" ) != ""
		) {
			String filter = "feesDue " + session.getAttribute( "enrollmentFeesDueFilterOperator" ) + " " + session.getAttribute( "enrollmentFeesDueFilter" )  
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if( session.getAttribute( "enrollmentFirstNameFilter" ) != null && session.getAttribute( "enrollmentFirstNameFilter" ) != "" ) {
			String filter = "firstName = \"" + session.getAttribute( "enrollmentFirstNameFilter" ) + "\""
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if( session.getAttribute( "enrollmentLastNameFilter" ) != null && session.getAttribute( "enrollmentLastNameFilter" ) != "" ) {
			String filter = "lastName = \"" + session.getAttribute( "enrollmentLastNameFilter" ) + "\""
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if( session.getAttribute( "enrollmentPeriodFilter" ) != null && session.getAttribute( "enrollmentPeriodFilter" ) != "" ) {
			String filter = "termsEnrolled = \"" + session.getAttribute( "enrollmentPeriodFilter" ) + "\""
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		Map<Integer, SortOption> sortOptionMap = new LinkedHashMap()
		
		if( session.getAttribute( "enrollmentFirstNameSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "enrollmentFirstNameSortOrder" ),
				new SortOption(
					fieldName: "firstName",
					fieldType: "text",
					sortDirection: session.getAttribute( "enrollmentFirstNameSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
				)
			)
		}
		
		if( session.getAttribute( "enrollmentLastNameSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "enrollmentLastNameSortOrder" ),
				new SortOption(
					fieldName: "lastName",
					fieldType: "text",
					sortDirection: session.getAttribute( "enrollmentLastNameSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
				)
			)
		}
		
		if( session.getAttribute( "enrollmentPeriodSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "enrollmentPeriodSortOrder" ),
				new SortOption(
					fieldName: "enrollTermStartDate",
					fieldType: "date",
					sortDirection: session.getAttribute( "enrollmentPeriodSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
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
			enrollmentResults = ( Results<ScoredDocument> )syncCache.get( memcacheKey )
		else {
			SortOptions.Builder sortOptionsBuilder = SortOptions.newBuilder()
			
			sortOptionMap.each(
				{ key, value ->
					SortExpression.Builder sortExpressionBuilder = SortExpression.newBuilder()
					sortExpressionBuilder.setExpression( value.getFieldName() )
					sortExpressionBuilder.setDirection( value.getSortDirection() )
					
					if( value.getFieldType() == "text" )
						sortExpressionBuilder.setDefaultValue( "" )
					else if( value.getFieldType() == "number" )
						sortExpressionBuilder.setDefaultValueNumeric( 0 )
					else
						sortExpressionBuilder.setDefaultValueDate( new Date() )
						
					sortOptionsBuilder.addSortExpression( sortExpressionBuilder )
				}
			)
			
			int enrollmentCount = Enrollment.getCount()
			SortOptions sortOptions

			if( enrollmentCount > 1 )
				sortOptions = sortOptionsBuilder.setLimit( enrollmentCount ).build()
			
			QueryOptions.Builder optionsBuilder = QueryOptions.newBuilder()
	            .setLimit( limit + 1 )
				.setNumberFoundAccuracy( limit + 1 )
	            .setOffset( offset )
	            .setFieldsToReturn(
						"studentId",
		            	"firstName",
		            	"lastName",
		            	"classesAttended",
		            	"firstClassAttended",
		            	"lastClassAttended",
		            	"termsEnrolled",
		            	"enrollTermYear",
		            	"enrollTermNo",
		            	"leaveTermYear",
		            	"leaveTermNo",
		            	"leaveTermEndDate",
		            	"birthDate",
		            	"village",
		            	"specialInfo",
		            	"genderCode",
						"schoolName",
						"leaveReasonCategory",
						"leaveReason",
						"tuitionFees",
						"boardingFees",
						"otherFees",
						"payments",
						"feesDue",
						"lastUpdateDate",
						"lastUpdateUser"
					)
	            
			if( enrollmentCount > 1 )	
				optionsBuilder.setSortOptions( sortOptions )
	            
			QueryOptions options = optionsBuilder.build();
            
	        Query query = Query.newBuilder().setOptions( options ).build( searchQuery );
	        
	        enrollmentResults = SearchServiceFactory.getSearchService().getIndex( IndexSpec.newBuilder().setName( "Enrollment" ).build() ).search( query );
			
			syncCache.put( memcacheKey, enrollmentResults )
			
			if( EnrollmentMemcache.findByMemcacheKey( memcacheKey ).size() == 0 ) {
				Entity enrollmentMemcache = new Entity( "EnrollmentMemcache" )
				enrollmentMemcache.memcacheKey = memcacheKey
				enrollmentMemcache.lastUpdateDate = new Date()
				
				if( user != null )
					enrollmentMemcache.lastUpdateUser = user.getEmail()
					
				enrollmentMemcache.save()
			}
		}
		
		return enrollmentResults
	}
	
	static List<ScoredDocument> findByLimitAndOffsetAndFirstNameAndLastNameAndBirthDateAndVillage( int limit, int offset, HttpSession session, String firstName, String lastName, Date birthDate, String village ) {
		String searchQuery
		Results<ScoredDocument> enrollmentResults
		
		int filterCount = 0
		
		Map<Integer, SortOption> sortOptionMap = new LinkedHashMap()
		
		if( session.getAttribute( "enrollmentFirstNameSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "enrollmentFirstNameSortOrder" ),
				new SortOption(
					fieldName: "firstName",
					fieldType: "text",
					sortDirection: session.getAttribute( "enrollmentFirstNameSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
				)
			)
		}
		
		if( session.getAttribute( "enrollmentLastNameSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "enrollmentLastNameSortOrder" ),
				new SortOption(
					fieldName: "lastName",
					fieldType: "text",
					sortDirection: session.getAttribute( "enrollmentLastNameSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
				)
			)
		}
		
		if( session.getAttribute( "enrollmentPeriodSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "enrollmentPeriodSortOrder" ),
				new SortOption(
					fieldName: "enrollTermStartDate",
					fieldType: "date",
					sortDirection: session.getAttribute( "enrollmentPeriodSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
				)
			)
		}
		
		SortOptions.Builder sortOptionsBuilder = SortOptions.newBuilder()
			
		sortOptionMap.each(
			{ key, value ->
				SortExpression.Builder sortExpressionBuilder = SortExpression.newBuilder()
				sortExpressionBuilder.setExpression( value.getFieldName() )
				sortExpressionBuilder.setDirection( value.getSortDirection() )
				
				if( value.getFieldType() == "text" )
					sortExpressionBuilder.setDefaultValue( "" )
				else if( value.getFieldType() == "number" )
					sortExpressionBuilder.setDefaultValueNumeric( 0 )
				else
					sortExpressionBuilder.setDefaultValueDate( new Date() )
					
				sortOptionsBuilder.addSortExpression( sortExpressionBuilder )
			}
		)
		
		int enrollmentCount = Enrollment.getCount()
		SortOptions sortOptions

		if( enrollmentCount > 1 )
			sortOptions = sortOptionsBuilder.setLimit( enrollmentCount ).build()
		
		QueryOptions.Builder optionsBuilder = QueryOptions.newBuilder()
			.setLimit( limit + 1 )
			.setNumberFoundAccuracy( limit + 1 )
			.setOffset( offset )
			.setFieldsToReturn(
					"studentId",
					"firstName",
					"lastName",
					"classesAttended",
					"firstClassAttended",
					"lastClassAttended",
					"termsEnrolled",
					"enrollTermYear",
					"enrollTermNo",
					"leaveTermYear",
					"leaveTermNo",
					"leaveTermEndDate",
					"birthDate",
					"village",
					"specialInfo",
					"genderCode",
					"schoolName",
					"leaveReasonCategory",
					"leaveReason",
					"tuitionFees",
					"boardingFees",
					"otherFees",
					"payments",
					"feesDue",
					"lastUpdateDate",
					"lastUpdateUser"
				)
			
		if( enrollmentCount > 1 )
			optionsBuilder.setSortOptions( sortOptions )
			
		QueryOptions options = optionsBuilder.build();
		
		Query query = Query.newBuilder().setOptions( options ).build( "firstName = \"${ firstName }\" AND lastName = \"${ lastName }\"" );
		
		enrollmentResults = SearchServiceFactory.getSearchService().getIndex( IndexSpec.newBuilder().setName( "Enrollment" ).build() ).search( query );
		
		Iterator<ScoredDocument> enrollmentResultsIterator = enrollmentResults.iterator()
		List<ScoredDocument> enrollmentList = new ArrayList()
		
		while( enrollmentResultsIterator.hasNext() ) {
			ScoredDocument enrollmentDocument = enrollmentResultsIterator.next()
			Set<String> enrollmentDocumentFieldNames = enrollmentDocument.getFieldNames()
			
			if( birthDate != null && enrollmentDocumentFieldNames.contains( "birthDate" ) && enrollmentDocument.getOnlyField( "birthDate" ).getDate() != birthDate ) {}
			else if( village != null && village != "" && village != "Village" && enrollmentDocumentFieldNames.contains( "village" ) && enrollmentDocument.getOnlyField( "village" ).getText() != village ) {}
			else
				enrollmentList.add( enrollmentDocument )
		}
		
		return enrollmentList
	}
    
    static Results<ScoredDocument> findByLimitAndOffsetAndURFUserEmail( int limit, int offset, String urfUserEmail, HttpSession session ) {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "Enrollment" );
		syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
		
		String searchQuery
		Results<ScoredDocument> enrollmentResults
		List<Entity> urfUserSchoolList
		String urfUserSchools = ""
		
		User user = UserServiceFactory.getUserService().getCurrentUser()
		Entity urfUser = URFUser.findByEmail( urfUserEmail )
		
		if( urfUser == null )
			urfUserSchools = "[Test]"
		else {
			searchQuery = "schoolName = ( "
			urfUserSchoolList = UserPrivilege.findByUserEmail( urfUser.email )
			
			urfUserSchoolList.eachWithIndex(
				{ urfUserSchool, index ->
				
					// GString can not be used when dealing with Memcache.
					urfUserSchools += "[" + urfUserSchool.schoolName + "]"
					
					if( index == 0 )
						searchQuery += "\"${ urfUserSchool.schoolName }\""
					else
						searchQuery += " OR \"${ urfUserSchool.schoolName }\""
				}
			)
			
			searchQuery += " )"
		}
		
		// GString can not be used when dealing with Memcache.
		String memcacheKey = "enrollmentResults of " + urfUserSchools + " row " + ( offset + 1 ) + " to " + ( offset + limit )
		int filterCount = 0
		
		if( session.getAttribute( "enrollmentClassesAttendedFilter" ) != null && session.getAttribute( "enrollmentClassesAttendedFilter" ) != "" ) {
			String filter = "classesAttended = \"" + session.getAttribute( "enrollmentClassesAttendedFilter" ) + "\""
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if(
			session.getAttribute( "enrollmentFeesDueFilter" ) != null &&
			session.getAttribute( "enrollmentFeesDueFilter" ) != "" &&
			session.getAttribute( "enrollmentFeesDueFilterOperator" ) != null &&
			session.getAttribute( "enrollmentFeesDueFilterOperator" ) != ""
		) {
			String filter = "feesDue " + session.getAttribute( "enrollmentFeesDueFilterOperator" ) + " " + session.getAttribute( "enrollmentFeesDueFilter" )  
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if( session.getAttribute( "enrollmentFirstNameFilter" ) != null && session.getAttribute( "enrollmentFirstNameFilter" ) != "" ) {
			String filter = "firstName = \"" + session.getAttribute( "enrollmentFirstNameFilter" ) + "\""
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if( session.getAttribute( "enrollmentLastNameFilter" ) != null && session.getAttribute( "enrollmentLastNameFilter" ) != "" ) {
			String filter = "lastName = \"" + session.getAttribute( "enrollmentLastNameFilter" ) + "\""
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if( session.getAttribute( "enrollmentPeriodFilter" ) != null && session.getAttribute( "enrollmentPeriodFilter" ) != "" ) {
			String filter = "termsEnrolled = \"" + session.getAttribute( "enrollmentPeriodFilter" ) + "\""
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		Map<Integer, SortOption> sortOptionMap = new LinkedHashMap()
		
		if( session.getAttribute( "enrollmentFirstNameSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "enrollmentFirstNameSortOrder" ),
				new SortOption(
					fieldName: "firstName",
					fieldType: "text",
					sortDirection: session.getAttribute( "enrollmentFirstNameSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
				)
			)
		}
		
		if( session.getAttribute( "enrollmentLastNameSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "enrollmentLastNameSortOrder" ),
				new SortOption(
					fieldName: "lastName",
					fieldType: "text",
					sortDirection: session.getAttribute( "enrollmentLastNameSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
				)
			)
		}
		
		if( session.getAttribute( "enrollmentPeriodSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "enrollmentPeriodSortOrder" ),
				new SortOption(
					fieldName: "enrollTermStartDate",
					fieldType: "date",
					sortDirection: session.getAttribute( "enrollmentPeriodSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
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
			enrollmentResults = ( Results<ScoredDocument> )syncCache.get( memcacheKey )
		else {
			SortOptions.Builder sortOptionsBuilder = SortOptions.newBuilder()
			
			sortOptionMap.each(
				{ key, value ->
					SortExpression.Builder sortExpressionBuilder = SortExpression.newBuilder()
					sortExpressionBuilder.setExpression( value.getFieldName() )
					sortExpressionBuilder.setDirection( value.getSortDirection() )
					
					if( value.getFieldType() == "text" )
						sortExpressionBuilder.setDefaultValue( "" )
					else if( value.getFieldType() == "number" )
						sortExpressionBuilder.setDefaultValueNumeric( 0 )
					else
						sortExpressionBuilder.setDefaultValueDate( new Date() )
						
					sortOptionsBuilder.addSortExpression( sortExpressionBuilder )
				}
			)
			
			int enrollmentCount = Enrollment.getCount()
			SortOptions sortOptions

			if( enrollmentCount > 1 )
				sortOptions = sortOptionsBuilder.setLimit( enrollmentCount ).build()
			
			QueryOptions.Builder optionsBuilder = QueryOptions.newBuilder()
	            .setLimit( limit + 1 )
				.setNumberFoundAccuracy( limit + 1 )
	            .setOffset( offset )
	            .setFieldsToReturn(
						"studentId",
		            	"firstName",
		            	"lastName",
		            	"classesAttended",
		            	"firstClassAttended",
		            	"lastClassAttended",
		            	"termsEnrolled",
		            	"enrollTermYear",
		            	"enrollTermNo",
		            	"leaveTermYear",
		            	"leaveTermNo",
		            	"leaveTermEndDate",
		            	"birthDate",
		            	"village",
		            	"specialInfo",
		            	"genderCode",
						"schoolName",
						"leaveReasonCategory",
						"leaveReason",
						"tuitionFees",
						"boardingFees",
						"otherFees",
						"payments",
						"feesDue",
						"lastUpdateDate",
						"lastUpdateUser"
					)
	            
			if( enrollmentCount > 1 )	
				optionsBuilder.setSortOptions( sortOptions )
	            
			QueryOptions options = optionsBuilder.build();
            
	        Query query = Query.newBuilder().setOptions( options ).build( searchQuery );
	        
	        enrollmentResults = SearchServiceFactory.getSearchService().getIndex( IndexSpec.newBuilder().setName( "Enrollment" ).build() ).search( query );
			
			syncCache.put( memcacheKey, enrollmentResults )
			
			if( EnrollmentMemcache.findByMemcacheKey( memcacheKey ).size() == 0 ) {
				Entity enrollmentMemcache = new Entity( "EnrollmentMemcache" )
				enrollmentMemcache.memcacheKey = memcacheKey
				enrollmentMemcache.lastUpdateDate = new Date()
				
				if( user != null )
					enrollmentMemcache.lastUpdateUser = urfUserEmail
					
				enrollmentMemcache.save()
			}
		}
		
		return enrollmentResults
    }
    
    static Results<ScoredDocument> findByStudentId( String studentId ) {
		int limit = Enrollment.getCount()
		
		if( limit == 0 )
			limit = 1000
		
        QueryOptions options = QueryOptions.newBuilder()
            .setLimit( limit )
			.setNumberFoundAccuracy( limit )
            .setOffset( 0 )
            .setFieldsToReturn(
					"studentId",
	            	"firstName",
	            	"lastName",
	            	"classesAttended",
					"firstClassAttended",
	            	"lastClassAttended",
	            	"termsEnrolled",
					"enrollTermYear",
	            	"enrollTermNo",
					"enrollTermStartDate",
	            	"leaveTermYear",
	            	"leaveTermNo",
	            	"leaveTermEndDate",
	            	"birthDate",
	            	"village",
	            	"specialInfo",
	            	"genderCode",
					"schoolName",
					"leaveReasonCategory",
					"leaveReason",
					"tuitionFees",
					"boardingFees",
					"otherFees",
					"payments",
					"feesDue",
					"lastUpdateDate",
					"lastUpdateUser"
				)
            .build();
        
        Query query = Query.newBuilder().setOptions( options ).build( "studentId: ${ studentId }" );
        
        return SearchServiceFactory.getSearchService().getIndex( IndexSpec.newBuilder().setName( "Enrollment" ).build() ).search( query );
    }
	
    static ScoredDocument findByStudentIdAndEnrollTermNoAndEnrollTermYear( String studentId, Number enrollTermNo, Number enrollTermYear ) {
		QueryOptions options = QueryOptions.newBuilder()
			.setLimit( 1 )
			.setNumberFoundAccuracy( 1 )
			.setOffset( 0 )
			.setFieldsToReturn(
					"studentId",
					"firstName",
					"lastName",
					"classesAttended",
					"firstClassAttended",
					"lastClassAttended",
					"termsEnrolled",
					"enrollTermYear",
					"enrollTermNo",
					"enrollTermStartDate",
					"leaveTermYear",
					"leaveTermNo",
					"leaveTermEndDate",
					"birthDate",
					"village",
					"specialInfo",
					"genderCode",
					"schoolName",
					"leaveReasonCategory",
					"leaveReason",
					"tuitionFees",
					"boardingFees",
					"otherFees",
					"payments",
					"feesDue",
					"lastUpdateDate",
					"lastUpdateUser"
				)
			.build();
		
		String searchQuery = "studentId = \"${ studentId }\" AND enrollTermNo = ${ enrollTermNo } AND enrollTermYear = ${ enrollTermYear }"
			
		Query query = Query.newBuilder().setOptions( options ).build( searchQuery );
		
		Results<ScoredDocument> enrollmentResults = SearchServiceFactory.getSearchService().getIndex( IndexSpec.newBuilder().setName( "Enrollment" ).build() ).search( query );
		
		if( enrollmentResults.getNumberReturned() == 0 )
			return null
		else
			return enrollmentResults.first()
	}
    
    static ScoredDocument findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, Number enrollTermNo, Number enrollTermYear ) {
		QueryOptions options = QueryOptions.newBuilder()
			.setLimit( 1 )
			.setNumberFoundAccuracy( 1 )
			.setOffset( 0 )
			.setFieldsToReturn(
					"studentId",
					"firstName",
					"lastName",
					"classesAttended",
					"firstClassAttended",
					"lastClassAttended",
					"termsEnrolled",
					"enrollTermYear",
					"enrollTermNo",
					"enrollTermStartDate",
					"leaveTermYear",
					"leaveTermNo",
					"leaveTermEndDate",
					"birthDate",
					"village",
					"specialInfo",
					"genderCode",
					"schoolName",
					"leaveReasonCategory",
					"leaveReason",
					"tuitionFees",
					"boardingFees",
					"otherFees",
					"payments",
					"feesDue",
					"lastUpdateDate",
					"lastUpdateUser"
				)
			.build();
		
		String searchQuery = "studentId = \"${ studentId }\" AND schoolName = \"${ schoolName }\" AND enrollTermNo = ${ enrollTermNo } AND enrollTermYear = ${ enrollTermYear }"
			
		Query query = Query.newBuilder().setOptions( options ).build( searchQuery );
		
		Results<ScoredDocument> enrollmentResults = SearchServiceFactory.getSearchService().getIndex( IndexSpec.newBuilder().setName( "Enrollment" ).build() ).search( query );
		
		if( enrollmentResults.getNumberReturned() == 0 )
			return null
		else
			return enrollmentResults.first()
	}
	
	static ScoredDocument findLastEnrollmentDocumentByStudentId( String studentId ) {
		int limit = Enrollment.getCount()
				
		if( limit == 0 )
			return null
		
		SortOptions sortOptions
		
		if( limit > 1 ) {
			sortOptions = SortOptions.newBuilder()
				.addSortExpression( SortExpression.newBuilder().setExpression( "leaveTermEndDate" ).setDirection( SortDirection.DESCENDING ).setDefaultValueDate( new Date() ) )
				.addSortExpression( SortExpression.newBuilder().setExpression( "enrollTermStartDate" ).setDirection( SortDirection.DESCENDING ).setDefaultValueDate( new Date() ) )
				.addSortExpression( SortExpression.newBuilder().setExpression( "schoolName" ).setDirection( SortDirection.DESCENDING ).setDefaultValue( "" ) )
				.setLimit( limit )
				.build()
		}
			
		QueryOptions.Builder optionsBuilder = QueryOptions.newBuilder()
			.setLimit( limit )
			.setNumberFoundAccuracy( limit )
			.setOffset( 0 )
			.setFieldsToReturn(
					"studentId",
					"firstName",
					"lastName",
					"classesAttended",
					"firstClassAttended",
					"lastClassAttended",
					"termsEnrolled",
					"enrollTermYear",
					"enrollTermNo",
					"enrollTermStartDate",
					"leaveTermYear",
					"leaveTermNo",
					"leaveTermEndDate",
					"birthDate",
					"village",
					"specialInfo",
					"genderCode",
					"schoolName",
					"leaveReasonCategory",
					"leaveReason",
					"tuitionFees",
					"boardingFees",
					"otherFees",
					"payments",
					"feesDue",
					"lastUpdateDate",
					"lastUpdateUser"
				)
			
		if( limit > 1 )
			optionsBuilder.setSortOptions( sortOptions )
			
		QueryOptions options = optionsBuilder.build()
		
		Query query = Query.newBuilder().setOptions( options ).build( "studentId: ${ studentId }" );
		
		Results<ScoredDocument> enrollmentResults = SearchServiceFactory.getSearchService().getIndex( IndexSpec.newBuilder().setName( "Enrollment" ).build() ).search( query );
		
		if( enrollmentResults.getNumberReturned() == 0 )
			return null
		else
			return enrollmentResults.first()
	}
    
    def static isRequired( def fieldName ) throws InvalidFieldException {
        if( ["studentId", "firstName", "classesAttended", "firstClassAttended", "lastClassAttended", "schoolName",
            "termsEnrolled", "enrollTermStartDate", "enrollTermYear", "enrollTermNo", "leaveTermEndDate", "lastUpdateDate", "lastUpdateUser"].contains( fieldName ) )
            return true
        else if( ["lastName", "birthDate", "village", "genderCode", "specialInfo", "leaveTermYear", "leaveTermNo",
            "leaveReasonCategory", "leaveReason"].contains( fieldName ) )
            return false
        else
            throw new InvalidFieldException( "EnrollmentDocument", fieldName )
    }
	
	static Results<ScoredDocument> list() {
		int enrollmentCount = Enrollment.list().size()
		
		QueryOptions options = QueryOptions.newBuilder()
            .setLimit( enrollmentCount )
			.setNumberFoundAccuracy( enrollmentCount )
            .setOffset( 0 )
            .setFieldsToReturn(
					"studentId",
	            	"firstName",
	            	"lastName",
	            	"classesAttended",
					"firstClassAttended",
	            	"lastClassAttended",
	            	"termsEnrolled",
					"enrollTermYear",
	            	"enrollTermNo",
					"enrollTermStartDate",
	            	"leaveTermYear",
	            	"leaveTermNo",
	            	"leaveTermEndDate",
	            	"birthDate",
	            	"village",
	            	"specialInfo",
	            	"genderCode",
					"schoolName",
					"leaveReasonCategory",
					"leaveReason",
					"tuitionFees",
					"boardingFees",
					"otherFees",
					"payments",
					"feesDue",
					"lastUpdateDate",
					"lastUpdateUser"
				)
            .build();
        
        Query query = Query.newBuilder().setOptions( options ).build( "" );
        
        SearchServiceFactory.getSearchService().getIndex( IndexSpec.newBuilder().setName( "Enrollment" ).build() ).search( query );
	}
}