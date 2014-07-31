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
 * Data and Meta Data Accessor for Student ScoredDocument.
 * 
 * @author awijasa
 */

class StudentDocument {
    def static deleteMemcache() {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "Student" );
        syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
        
        StudentMemcache.list().each() {
            syncCache.delete( it.memcacheKey )
            it.delete()
        }
    }
	
	static Results<ScoredDocument> findByLastEnrollmentSchool( String lastEnrollmentSchool ) {
		int limit = Student.list().size()
		
		QueryOptions options = QueryOptions.newBuilder()
			.setLimit( limit )
			.setNumberFoundAccuracy( limit )
			.setOffset( 0 )
			.setFieldsToReturn(
					"studentId",
					"firstName",
					"lastName",
					"classesAttended",
					"lastEnrollmentFirstClassAttended",
					"lastEnrollmentLastClassAttended",
					"termsEnrolled",
					"lastEnrollmentTermYear",
					"lastEnrollmentTermNo",
					"lastEnrollmentLeaveTermYear",
					"lastEnrollmentLeaveTermNo",
					"birthDate",
					"village",
					"specialInfo",
					"genderCode",
					"lastEnrollmentSchool",
					"lastEnrollmentLeaveReasonCategory",
					"lastEnrollmentLeaveReason",
					"tuitionFees",
					"boardingFees",
					"otherFees",
					"payments",
					"feesDue",
					"lastUpdateDate",
					"lastUpdateUser"
				)
			.build();
		
		Query query = Query.newBuilder().setOptions( options ).build( "lastEnrollmentSchool: \"${ lastEnrollmentSchool }\"" );
		
		SearchServiceFactory.getSearchService().getIndex( IndexSpec.newBuilder().setName( "Student" ).build() ).search( query );
	}
    
    static Results<ScoredDocument> findByLimitAndOffset( int limit, int offset, HttpSession session ) {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "Student" );
		syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
		
		String searchQuery
		Results<ScoredDocument> studentResults
		List<Entity> urfUserSchoolList
		String urfUserSchools = ""
		
		User user = UserServiceFactory.getUserService().getCurrentUser()
		Entity urfUser = URFUser.findByEmail( user?.getEmail() )
		
		if( urfUser == null ) {
			urfUserSchools = "[Test]"
			searchQuery = "lastEnrollmentSchool = \"Test\""
		}
		else {
			searchQuery = "lastEnrollmentSchool = ( "
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
		String memcacheKey = "studentResults of " + urfUserSchools + " row " + ( offset + 1 ) + " to " + ( offset + limit )
		int filterCount = 0
		
		if(
			session.getAttribute( "studentBirthDateFilter" ) != null &&
			session.getAttribute( "studentBirthDateFilter" ) != "" &&
			session.getAttribute( "studentBirthDateFilterOperator" ) != null &&
			session.getAttribute( "studentBirthDateFilterOperator" ) != ""
		) {
			String filter = "birthDate " + session.getAttribute( "studentBirthDateFilterOperator" ) + " " + Date.parse( "MMM d yy", session.getAttribute( "studentBirthDateFilter" ) ).format( "yyyy-M-d" )
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if( session.getAttribute( "studentClassesAttendedFilter" ) != null && session.getAttribute( "studentClassesAttendedFilter" ) != "" ) {
			String filter = "classesAttended = \"" + session.getAttribute( "studentClassesAttendedFilter" ) + "\""
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if( session.getAttribute( "studentEnrollmentPeriodFilter" ) != null && session.getAttribute( "studentEnrollmentPeriodFilter" ) != "" ) {
			String filter = "termsEnrolled = \"" + session.getAttribute( "studentEnrollmentPeriodFilter" ) + "\""
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if(
			session.getAttribute( "studentFeesDueFilter" ) != null &&
			session.getAttribute( "studentFeesDueFilter" ) != "" &&
			session.getAttribute( "studentFeesDueFilterOperator" ) != null &&
			session.getAttribute( "studentFeesDueFilterOperator" ) != ""
		) {
			String filter = "feesDue " + session.getAttribute( "studentFeesDueFilterOperator" ) + " " + session.getAttribute( "studentFeesDueFilter" )  
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if( session.getAttribute( "studentFirstNameFilter" ) != null && session.getAttribute( "studentFirstNameFilter" ) != "" ) {
			String filter = "firstName = \"" + session.getAttribute( "studentFirstNameFilter" ) + "\""
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if( session.getAttribute( "studentLastNameFilter" ) != null && session.getAttribute( "studentLastNameFilter" ) != "" ) {
			String filter = "lastName = \"" + session.getAttribute( "studentLastNameFilter" ) + "\""
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if( session.getAttribute( "studentVillageFilter" ) != null && session.getAttribute( "studentVillageFilter" ) != "" ) {
			String filter = "village = \"" + session.getAttribute( "studentVillageFilter" ) + "\""
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		Map<Integer, SortOption> sortOptionMap = new LinkedHashMap()
		
		if( session.getAttribute( "studentFirstNameSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "studentFirstNameSortOrder" ),
				new SortOption(
					fieldName: "firstName",
					fieldType: "text",
					sortDirection: session.getAttribute( "studentFirstNameSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
				)
			)
		}
		
		if( session.getAttribute( "studentLastNameSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "studentLastNameSortOrder" ),
				new SortOption(
					fieldName: "lastName",
					fieldType: "text",
					sortDirection: session.getAttribute( "studentLastNameSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
				)
			)
		}
		
		if( session.getAttribute( "studentEnrollmentPeriodSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "studentEnrollmentPeriodSortOrder" ),
				new SortOption(
					fieldName: "lastEnrollmentTermStartDate",
					fieldType: "date",
					sortDirection: session.getAttribute( "studentEnrollmentPeriodSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
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
			studentResults = ( Results<ScoredDocument> )syncCache.get( memcacheKey )
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
			
			int studentCount = Student.getCount()
			
			SortOptions sortOptions
			
			if( studentCount > 1 )
				sortOptions = sortOptionsBuilder.setLimit( studentCount ).build()
			
			QueryOptions.Builder optionsBuilder = QueryOptions.newBuilder()
	            .setLimit( limit + 1 )
				.setNumberFoundAccuracy( limit + 1 )
	            .setOffset( offset )
	            .setFieldsToReturn(
						"studentId",
		            	"firstName",
		            	"lastName",
		            	"classesAttended",
		            	"lastEnrollmentFirstClassAttended",
		            	"lastEnrollmentLastClassAttended",
		            	"termsEnrolled",
		            	"lastEnrollmentTermYear",
		            	"lastEnrollmentTermNo",
		            	"lastEnrollmentLeaveTermYear",
		            	"lastEnrollmentLeaveTermNo",
		            	"birthDate",
		            	"village",
		            	"specialInfo",
		            	"genderCode",
						"lastEnrollmentSchool",
						"lastEnrollmentLeaveReasonCategory",
						"lastEnrollmentLeaveReason",
						"tuitionFees",
						"boardingFees",
						"otherFees",
						"payments",
						"feesDue",
						"lastUpdateDate",
						"lastUpdateUser"
					)
	            
			if( studentCount > 1 )
				optionsBuilder.setSortOptions( sortOptions )
	            
			QueryOptions options = optionsBuilder.build();
            
	        Query query = Query.newBuilder().setOptions( options ).build( searchQuery );
	        
	        studentResults = SearchServiceFactory.getSearchService().getIndex( IndexSpec.newBuilder().setName( "Student" ).build() ).search( query );
			
			syncCache.put( memcacheKey, studentResults )
			
			if( StudentMemcache.findByMemcacheKey( memcacheKey ).size() == 0 ) {
				Entity studentMemcache = new Entity( "StudentMemcache" )
				studentMemcache.memcacheKey = memcacheKey
				studentMemcache.lastUpdateDate = new Date()
				
				if( user != null )
					studentMemcache.lastUpdateUser = user.getEmail()
					
				studentMemcache.save()
			}
		}
		
		return studentResults
	}
	
	static List<ScoredDocument> findByLimitAndOffsetAndFirstNameAndLastNameAndBirthDateAndVillage( int limit, int offset, HttpSession session, String firstName, String lastName, Date birthDate, String village ) {
		String searchQuery
		Results<ScoredDocument> studentResults
		
		int filterCount = 0
		
		Map<Integer, SortOption> sortOptionMap = new LinkedHashMap()
		
		if( session.getAttribute( "studentFirstNameSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "studentFirstNameSortOrder" ),
				new SortOption(
					fieldName: "firstName",
					fieldType: "text",
					sortDirection: session.getAttribute( "studentFirstNameSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
				)
			)
		}
		
		if( session.getAttribute( "studentLastNameSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "studentLastNameSortOrder" ),
				new SortOption(
					fieldName: "lastName",
					fieldType: "text",
					sortDirection: session.getAttribute( "studentLastNameSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
				)
			)
		}
		
		if( session.getAttribute( "studentEnrollmentPeriodSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "studentEnrollmentPeriodSortOrder" ),
				new SortOption(
					fieldName: "lastEnrollmentTermStartDate",
					fieldType: "date",
					sortDirection: session.getAttribute( "studentEnrollmentPeriodSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
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
		
		int studentCount = Student.getCount()
		SortOptions sortOptions
		
		if( studentCount > 1 )
			sortOptions = sortOptionsBuilder.setLimit( studentCount ).build()
		
		QueryOptions.Builder optionsBuilder = QueryOptions.newBuilder()
			.setLimit( limit + 1 )
			.setNumberFoundAccuracy( limit + 1 )
			.setOffset( offset )
			.setFieldsToReturn(
					"studentId",
					"firstName",
					"lastName",
					"classesAttended",
					"lastEnrollmentFirstClassAttended",
					"lastEnrollmentLastClassAttended",
					"termsEnrolled",
					"lastEnrollmentTermYear",
					"lastEnrollmentTermNo",
					"lastEnrollmentLeaveTermYear",
					"lastEnrollmentLeaveTermNo",
					"birthDate",
					"village",
					"specialInfo",
					"genderCode",
					"lastEnrollmentSchool",
					"lastEnrollmentLeaveReasonCategory",
					"lastEnrollmentLeaveReason",
					"tuitionFees",
					"boardingFees",
					"otherFees",
					"payments",
					"feesDue",
					"lastUpdateDate",
					"lastUpdateUser"
				)
				
		if( studentCount > 1 )
			optionsBuilder.setSortOptions( sortOptions )
			
		QueryOptions options = optionsBuilder.build();
		
		Query query = Query.newBuilder().setOptions( options ).build( "firstName = \"${ firstName }\" AND lastName = \"${ lastName }\"" );
		
		studentResults = SearchServiceFactory.getSearchService().getIndex( IndexSpec.newBuilder().setName( "Student" ).build() ).search( query );
		
		Iterator<ScoredDocument> studentResultsIterator = studentResults.iterator()
		List<ScoredDocument> studentList = new ArrayList()
		
		while( studentResultsIterator.hasNext() ) {
			ScoredDocument studentDocument = studentResultsIterator.next()
			Set<String> studentDocumentFieldNames = studentDocument.getFieldNames()
			
			if( birthDate != null && studentDocumentFieldNames.contains( "birthDate" ) && studentDocument.getOnlyField( "birthDate" ).getDate() != birthDate ) {}
			else if( village != null && village != "" && village != "Village" && studentDocumentFieldNames.contains( "village" ) && studentDocument.getOnlyField( "village" ).getText() != village ) {}
			else
				studentList.add( studentDocument )
		}
		
		return studentList
	}
    
    static Results<ScoredDocument> findByLimitAndOffsetAndURFUserEmail( int limit, int offset, String urfUserEmail, HttpSession session ) {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService( "Student" );
		syncCache.setErrorHandler( new ConsistentLogAndContinueErrorHandler( Level.INFO ) )
		
		String searchQuery
		Results<ScoredDocument> studentResults
		List<Entity> urfUserSchoolList
		String urfUserSchools = ""
		
		User user = UserServiceFactory.getUserService().getCurrentUser()
		Entity urfUser = URFUser.findByEmail( urfUserEmail )
		
		if( urfUser == null ) {
			urfUserSchools = "[Test]"
			searchQuery = "lastEnrollmentSchool = \"Test\""
		}
		else {
			searchQuery = "lastEnrollmentSchool = ( "
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
		String memcacheKey = "studentResults of " + urfUserSchools + " row " + ( offset + 1 ) + " to " + ( offset + limit )
		int filterCount = 0
		
		if(
			session.getAttribute( "studentBirthDateFilter" ) != null &&
			session.getAttribute( "studentBirthDateFilter" ) != "" &&
			session.getAttribute( "studentBirthDateFilterOperator" ) != null &&
			session.getAttribute( "studentBirthDateFilterOperator" ) != ""
		) {
			String filter = "birthDate " + session.getAttribute( "studentBirthDateFilterOperator" ) + " " + Date.parse( "MMM d yy", session.getAttribute( "studentBirthDateFilter" ) ).format( "yyyy-M-d" )
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if( session.getAttribute( "studentClassesAttendedFilter" ) != null && session.getAttribute( "studentClassesAttendedFilter" ) != "" ) {
			String filter = "classesAttended = \"" + session.getAttribute( "studentClassesAttendedFilter" ) + "\""
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if( session.getAttribute( "studentEnrollmentPeriodFilter" ) != null && session.getAttribute( "studentEnrollmentPeriodFilter" ) != "" ) {
			String filter = "termsEnrolled = \"" + session.getAttribute( "studentEnrollmentPeriodFilter" ) + "\""
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if(
			session.getAttribute( "studentFeesDueFilter" ) != null &&
			session.getAttribute( "studentFeesDueFilter" ) != "" &&
			session.getAttribute( "studentFeesDueFilterOperator" ) != null &&
			session.getAttribute( "studentFeesDueFilterOperator" ) != ""
		) {
			String filter = "feesDue " + session.getAttribute( "studentFeesDueFilterOperator" ) + " " + session.getAttribute( "studentFeesDueFilter" )  
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if( session.getAttribute( "studentFirstNameFilter" ) != null && session.getAttribute( "studentFirstNameFilter" ) != "" ) {
			String filter = "firstName = \"" + session.getAttribute( "studentFirstNameFilter" ) + "\""
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if( session.getAttribute( "studentLastNameFilter" ) != null && session.getAttribute( "studentLastNameFilter" ) != "" ) {
			String filter = "lastName = \"" + session.getAttribute( "studentLastNameFilter" ) + "\""
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		if( session.getAttribute( "studentVillageFilter" ) != null && session.getAttribute( "studentVillageFilter" ) != "" ) {
			String filter = "village = \"" + session.getAttribute( "studentVillageFilter" ) + "\""
			searchQuery += " AND ${ filter }"
			
			if( filterCount == 0 )
				memcacheKey += " where " + filter
			else
				memcacheKey += " AND " + filter
				
			filterCount++
		}
		
		Map<Integer, SortOption> sortOptionMap = new LinkedHashMap()
		
		if( session.getAttribute( "studentFirstNameSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "studentFirstNameSortOrder" ),
				new SortOption(
					fieldName: "firstName",
					fieldType: "text",
					sortDirection: session.getAttribute( "studentFirstNameSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
				)
			)
		}
		
		if( session.getAttribute( "studentLastNameSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "studentLastNameSortOrder" ),
				new SortOption(
					fieldName: "lastName",
					fieldType: "text",
					sortDirection: session.getAttribute( "studentLastNameSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
				)
			)
		}
		
		if( session.getAttribute( "studentEnrollmentPeriodSortDirection" ) != null ) {
			sortOptionMap.put(
				session.getAttribute( "studentEnrollmentPeriodSortOrder" ),
				new SortOption(
					fieldName: "lastEnrollmentTermStartDate",
					fieldType: "date",
					sortDirection: session.getAttribute( "studentEnrollmentPeriodSortDirection" ) == "asc"? SortDirection.ASCENDING: SortDirection.DESCENDING
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
			studentResults = ( Results<ScoredDocument> )syncCache.get( memcacheKey )
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
			
			int studentCount = Student.getCount()
			
			SortOptions sortOptions
			
			if( studentCount > 1 )
				sortOptions = sortOptionsBuilder.setLimit( studentCount ).build()
			
			QueryOptions.Builder optionsBuilder = QueryOptions.newBuilder()
	            .setLimit( limit + 1 )
				.setNumberFoundAccuracy( limit + 1 )
	            .setOffset( offset )
	            .setFieldsToReturn(
						"studentId",
		            	"firstName",
		            	"lastName",
		            	"classesAttended",
		            	"lastEnrollmentFirstClassAttended",
		            	"lastEnrollmentLastClassAttended",
		            	"termsEnrolled",
		            	"lastEnrollmentTermYear",
		            	"lastEnrollmentTermNo",
		            	"lastEnrollmentLeaveTermYear",
		            	"lastEnrollmentLeaveTermNo",
		            	"birthDate",
		            	"village",
		            	"specialInfo",
		            	"genderCode",
						"lastEnrollmentSchool",
						"lastEnrollmentLeaveReasonCategory",
						"lastEnrollmentLeaveReason",
						"tuitionFees",
						"boardingFees",
						"otherFees",
						"payments",
						"feesDue",
						"lastUpdateDate",
						"lastUpdateUser"
					)
	            
			if( studentCount > 1 )
				optionsBuilder.setSortOptions( sortOptions )
	            
			QueryOptions options = optionsBuilder.build();
            
	        Query query = Query.newBuilder().setOptions( options ).build( searchQuery );
	        
	        studentResults = SearchServiceFactory.getSearchService().getIndex( IndexSpec.newBuilder().setName( "Student" ).build() ).search( query );
			
			syncCache.put( memcacheKey, studentResults )
			
			if( StudentMemcache.findByMemcacheKey( memcacheKey ).size() == 0 ) {
				Entity studentMemcache = new Entity( "StudentMemcache" )
				studentMemcache.memcacheKey = memcacheKey
				studentMemcache.lastUpdateDate = new Date()
				
				if( user != null )
					studentMemcache.lastUpdateUser = urfUserEmail
					
				studentMemcache.save()
			}
		}
		
		return studentResults
    }
    
    static ScoredDocument findByStudentId( String studentId ) {
        QueryOptions options = QueryOptions.newBuilder()
            .setLimit( 1 )
			.setNumberFoundAccuracy( 1 )
            .setOffset( 0 )
            .setFieldsToReturn(
					"studentId",
	            	"firstName",
	            	"lastName",
	            	"classesAttended",
					"lastEnrollmentFirstClassAttended",
	            	"lastEnrollmentLastClassAttended",
	            	"termsEnrolled",
					"lastEnrollmentTermYear",
	            	"lastEnrollmentTermNo",
					"lastEnrollmentTermStartDate",
	            	"lastEnrollmentLeaveTermYear",
	            	"lastEnrollmentLeaveTermNo",
	            	"birthDate",
	            	"village",
	            	"specialInfo",
	            	"genderCode",
					"lastEnrollmentSchool",
					"lastEnrollmentLeaveReasonCategory",
					"lastEnrollmentLeaveReason",
					"tuitionFees",
					"boardingFees",
					"otherFees",
					"payments",
					"feesDue",
					"lastUpdateDate",
					"lastUpdateUser"
				)
            .build();
        
        Query query = Query.newBuilder().setOptions( options ).build( "studentId: \"${ studentId }\"" );
        
        Results<ScoredDocument> studentResults = SearchServiceFactory.getSearchService().getIndex( IndexSpec.newBuilder().setName( "Student" ).build() ).search( query );
		
		if( studentResults.getNumberReturned() == 0 )
			return null
		else
			return studentResults.first()
    }
    
    def static isRequired( def fieldName ) throws InvalidFieldException {
        if( ["studentId", "firstName", "classesAttended", "lastEnrollmentFirstClassAttended", "lastEnrollmentLastClassAttended", "lastEnrollmentSchool",
            "termsEnrolled", "lastEnrollmentTermStartDate", "lastEnrollmentTermYear", "lastEnrollmentTermNo", "lastUpdateDate", "lastUpdateUser"].contains( fieldName ) )
            return true
        else if( ["lastName", "birthDate", "village", "genderCode", "specialInfo", "lastEnrollmentLeaveTermYear", "lastEnrollmentLeaveTermNo",
            "lastEnrollmentLeaveReasonCategory", "lastEnrollmentLeaveReason"].contains( fieldName ) )
            return false
        else
            throw new InvalidFieldException( "StudentDocument", fieldName )
    }
	
	static Results<ScoredDocument> list() {
		int studentCount = Student.list().size()
		
		QueryOptions options = QueryOptions.newBuilder()
            .setLimit( studentCount )
			.setNumberFoundAccuracy( studentCount )
            .setOffset( 0 )
            .setFieldsToReturn(
					"studentId",
	            	"firstName",
	            	"lastName",
	            	"classesAttended",
					"lastEnrollmentFirstClassAttended",
	            	"lastEnrollmentLastClassAttended",
	            	"termsEnrolled",
					"lastEnrollmentTermYear",
	            	"lastEnrollmentTermNo",
					"lastEnrollmentTermStartDate",
	            	"lastEnrollmentLeaveTermYear",
	            	"lastEnrollmentLeaveTermNo",
	            	"birthDate",
	            	"village",
	            	"specialInfo",
	            	"genderCode",
					"lastEnrollmentSchool",
					"lastEnrollmentLeaveReasonCategory",
					"lastEnrollmentLeaveReason",
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
        
        SearchServiceFactory.getSearchService().getIndex( IndexSpec.newBuilder().setName( "Student" ).build() ).search( query );
	}
}