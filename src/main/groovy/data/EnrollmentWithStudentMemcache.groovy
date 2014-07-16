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
import com.google.appengine.api.datastore.Query.FilterOperator

/**
 * Data and Meta Data Accessor for EnrollmentWithStudentMemcache Entity.
 * 
 * @author awijasa
 */

class EnrollmentWithStudentMemcache {
    def static findByMemcacheKey( def memcacheKey ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "EnrollmentWithStudentMemcache" )
        q.addFilter( "memcacheKey", FilterOperator.EQUAL, memcacheKey )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
    
    def static list() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "EnrollmentWithStudentMemcache" )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
}