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
import com.google.appengine.api.users.User
import com.google.appengine.api.users.UserService
import com.google.appengine.api.users.UserServiceFactory

/**
 * Data and Meta Data Accessor for Student Entity.
 *
 * @author awijasa
 */

class Student {
    def static findByGenderCode( def genderCode ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Student" )
        q.addFilter( "genderCode", Query.FilterOperator.EQUAL, genderCode )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
	
	def static findByStudentId( def studentId ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Student" )
        q.addFilter( "studentId", Query.FilterOperator.EQUAL, studentId )
        
        def studentList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
        
        if( studentList.size() == 0 )
            return null
        else
            return studentList.first()
    }
	
	static Entity findMetaDataBySchoolName( String schoolName ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "StudentMetaData", App.get().getKey() )
		q.setFilter( new FilterPredicate( "schoolName", FilterOperator.EQUAL, schoolName ) )
		List<Entity> studentMetaDataList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
		
		if( studentMetaDataList.size() == 0 )
			return null
		else
			return studentMetaDataList.first()
	}
    
    static int getCount() {
		int count = 0
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "StudentMetaData", App.get().getKey() )
		
		List<Entity> urfUserSchoolList
		
		User user = UserServiceFactory.getUserService().getCurrentUser()
		Entity urfUser = URFUser.findByEmail( user?.getEmail() )
		
		if( urfUser == null )
			q.setFilter( new FilterPredicate( "schoolName", FilterOperator.EQUAL, "Test" ) )
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
	
	static String[] getPrimaryKey() {
    	["studentId"]
    }
    
    def static isRequired( def fieldName ) throws InvalidFieldException {
        if( ["studentId", "firstName", "lastUpdateDate", "lastUpdateUser"].contains( fieldName ) )
            return true
        else if( ["lastName", "birthDate", "village", "specialInfo", "genderCode"].contains( fieldName ) )
            return false
        else
            throw new InvalidFieldException( "Student", fieldName )
    }
    
    static boolean isUsed( Entity student ) {
        Relationship.findByStudentId( student?.studentId ).size() > 0
    }
    
    def static list() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "Student" )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
}