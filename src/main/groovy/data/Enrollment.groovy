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
import com.google.appengine.api.search.Document
import com.google.appengine.api.users.User
import com.google.appengine.api.users.UserService
import com.google.appengine.api.users.UserServiceFactory

/**
 * Data and Meta Data Accessor for Enrollment Entity.
 * 
 * @author awijasa
 */

class Enrollment {
    def static findByLeaveReasonCategory( def leaveReasonCategory ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Enrollment" )
        q.addFilter( "leaveReasonCategory", Query.FilterOperator.EQUAL, leaveReasonCategory )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
    
	static List<Entity> findBySchoolName( String schoolName ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "Enrollment" )
		q.setFilter( new FilterPredicate( "schoolName", FilterOperator.EQUAL, schoolName ) )
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	}
	
	def static findBySchoolNameAndFirstClassAttended( def schoolName, def firstClassAttended ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Enrollment" )
        def schoolNameFilter = new FilterPredicate( "schoolName", FilterOperator.EQUAL, schoolName )
        def firstClassAttendedFilter = new FilterPredicate( "firstClassAttended", FilterOperator.EQUAL, firstClassAttended )
        q.setFilter( CompositeFilterOperator.and( schoolNameFilter, firstClassAttendedFilter ) )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
    
    def static findBySchoolNameAndLastClassAttended( def schoolName, def lastClassAttended ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Enrollment" )
        def schoolNameFilter = new FilterPredicate( "schoolName", FilterOperator.EQUAL, schoolName )
        def lastClassAttendedFilter = new FilterPredicate( "lastClassAttended", FilterOperator.EQUAL, lastClassAttended )
        q.setFilter( CompositeFilterOperator.and( schoolNameFilter, lastClassAttendedFilter ) )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
    
    def static findBySchoolNameAndEnrollTermNoAndEnrollTermYear( def schoolName, def enrollTermNo, def enrollTermYear ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Enrollment" )
        def schoolNameFilter = new FilterPredicate( "schoolName", FilterOperator.EQUAL, schoolName )
        def enrollTermNoFilter = new FilterPredicate( "enrollTermNo", FilterOperator.EQUAL, enrollTermNo )
        def enrollTermYearFilter = new FilterPredicate( "enrollTermYear", FilterOperator.EQUAL, enrollTermYear )
        q.setFilter( CompositeFilterOperator.and( schoolNameFilter, enrollTermNoFilter, enrollTermYearFilter ) )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
    
    def static findBySchoolNameAndLeaveTermNoAndLeaveTermYear( def schoolName, def leaveTermNo, def leaveTermYear ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Enrollment" )
        def schoolNameFilter = new FilterPredicate( "schoolName", FilterOperator.EQUAL, schoolName )
        def leaveTermNoFilter = new FilterPredicate( "leaveTermNo", FilterOperator.EQUAL, leaveTermNo )
        def leaveTermYearFilter = new FilterPredicate( "leaveTermYear", FilterOperator.EQUAL, leaveTermYear )
        q.setFilter( CompositeFilterOperator.and( schoolNameFilter, leaveTermNoFilter, leaveTermYearFilter ) )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
    
    def static findByStudentId( def studentId ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Enrollment" )
        q.addFilter( "studentId", Query.FilterOperator.EQUAL, studentId )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
    
    static Entity findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( String studentId, String schoolName, Number enrollTermNo, Number enrollTermYear ) {
        Integer enrollTermNoInt = enrollTermNo
        Integer enrollTermYearInt = enrollTermYear
        
    	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Enrollment" )
        FilterPredicate studentIdFilter = new FilterPredicate( "studentId", FilterOperator.EQUAL, studentId )
        FilterPredicate schoolNameFilter = new FilterPredicate( "schoolName", FilterOperator.EQUAL, schoolName )
        FilterPredicate enrollTermNoFilter = new FilterPredicate( "enrollTermNo", FilterOperator.EQUAL, enrollTermNoInt )
        FilterPredicate enrollTermYearFilter = new FilterPredicate( "enrollTermYear", FilterOperator.EQUAL, enrollTermYearInt )
        q.setFilter( CompositeFilterOperator.and( studentIdFilter, schoolNameFilter, enrollTermNoFilter, enrollTermYearFilter ) )
        List<Entity> enrollmentList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
        
        if( enrollmentList.size() == 0 )
            return null
        else
            return enrollmentList.first()
    }
    
    static Entity findLastEnrollmentByStudentId( String studentId ) {
        Document lastEnrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( studentId )
        
        if( lastEnrollmentDocument == null )
			return null
		else {
			Integer enrollTermNo = lastEnrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber()
			Integer enrollTermYear = lastEnrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber()
			
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
	        Query q = new Query( "Enrollment" )
	        FilterPredicate studentIdFilter = new FilterPredicate( "studentId", FilterOperator.EQUAL, studentId )
	        FilterPredicate schoolNameFilter = new FilterPredicate( "schoolName", FilterOperator.EQUAL, lastEnrollmentDocument.getOnlyField( "schoolName" ).getText() )
	        FilterPredicate enrollTermNoFilter = new FilterPredicate( "enrollTermNo", FilterOperator.EQUAL, enrollTermNo )
	        FilterPredicate enrollTermYearFilter = new FilterPredicate( "enrollTermYear", FilterOperator.EQUAL, enrollTermYear )
	        q.setFilter( CompositeFilterOperator.and( studentIdFilter, schoolNameFilter, enrollTermNoFilter, enrollTermYearFilter ) )
	        List<Entity> enrollmentList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
	        
	        if( enrollmentList.size() == 0 )
	            return null
	        else
	            return enrollmentList.first()
		}
    }
	
	static Entity findMetaDataBySchoolName( String schoolName ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "EnrollmentMetaData", App.get().getKey() )
		q.setFilter( new FilterPredicate( "schoolName", FilterOperator.EQUAL, schoolName ) )
		List<Entity> enrollmentMetaDataList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
		
		if( enrollmentMetaDataList.size() == 0 )
			return null
		else
			return enrollmentMetaDataList.first()
	}
	
	static int getCount() {
		int count = 0
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "EnrollmentMetaData", App.get().getKey() )
		
		List<Entity> urfUserSchoolList
		
		User user = UserServiceFactory.getUserService().getCurrentUser()
		Entity urfUser = URFUser.findByEmail( user?.getEmail() )
		
		if( urfUser == null )
			return 1000
		else {
			urfUserSchoolList = UserPrivilege.findByUserEmail( urfUser.email )
			
			List<FilterPredicate> schoolFilterList = new ArrayList()
				
			urfUserSchoolList.each(
				{
					schoolFilterList.add new FilterPredicate( "schoolName", FilterOperator.EQUAL, it.schoolName )
				}
			)
			
			if( schoolFilterList.size() == 0 )
				return new ArrayList()
			else if( schoolFilterList.size() == 1 )
				q.setFilter( schoolFilterList.first() )
			else
				q.setFilter( CompositeFilterOperator.or( schoolFilterList ) )
		}
		
		datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() ).each(
			{
				count += it.count
			}
		)
		
		return count
	}
    
    static Entity findOverlappedEnrollment( Entity suppliedEnrollment ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Enrollment" )
        q.setFilter( CompositeFilterOperator.and(
            new FilterPredicate( "studentId", FilterOperator.EQUAL, suppliedEnrollment.studentId )
            , new FilterPredicate( "schoolName", FilterOperator.EQUAL, suppliedEnrollment.schoolName ) ) )
    
        List<Entity> enrollmentsBySameStudentAtSameSchool = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
        
        for( Entity enrollment: enrollmentsBySameStudentAtSameSchool ) {
            Entity enrollTerm = Term.findByTermSchoolAndTermNoAndYear( enrollment.schoolName, enrollment.enrollTermNo, enrollment.enrollTermYear )
            Entity suppliedEnrollTerm = Term.findByTermSchoolAndTermNoAndYear( suppliedEnrollment.schoolName, suppliedEnrollment.enrollTermNo, suppliedEnrollment.enrollTermYear )
            Entity suppliedLeaveTerm = Term.findByTermSchoolAndTermNoAndYear( suppliedEnrollment.schoolName, suppliedEnrollment.leaveTermNo, suppliedEnrollment.leaveTermYear )
            
            if( enrollment.getKey().getId() != suppliedEnrollment.getKey()?.getId() ) {
				if( enrollTerm.startDate >= suppliedEnrollTerm.startDate &&
					enrollTerm.startDate <= ( ( suppliedLeaveTerm?.endDate )?: enrollTerm.startDate ) )
					return enrollment
				else if( enrollTerm.startDate <= suppliedEnrollTerm.startDate &&
					( ( Term.findByTermSchoolAndTermNoAndYear( enrollment.schoolName, enrollment.leaveTermNo, enrollment.leaveTermYear )?.endDate )?: suppliedEnrollTerm.startDate ) >= suppliedEnrollTerm.startDate )
					return enrollment
			}
        }
        
        return null
    }
    
    static List<String> getPrimaryKey() {
        return ["studentId", "schoolName", "enrollTermYear", "enrollTermNo"]
    }
    
    static boolean isEqual( Entity enrollment1, Entity enrollment2 ) {
        for( String pkProperty: getPrimaryKey() ) {
            if( enrollment1.getProperty( pkProperty ) != enrollment2.getProperty( pkProperty ) )
                return false
        }
        
        return true
    }
    
    def static isRequired( def fieldName ) throws InvalidFieldException {
        if( ["studentId", "schoolName", "firstClassAttended", "lastClassAttended", "enrollTermYear", "enrollTermNo", "lastUpdateDate", "lastUpdateUser"]
            .contains( fieldName ) )
            return true
        else if( ["leaveTermYear", "leaveTermNo", "leaveReasonCategory", "leaveReason"].contains( fieldName ) )
            return false
        else
            throw new InvalidFieldException( "Enrollment", fieldName )
    }
    
    def static list() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Enrollment" )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
}