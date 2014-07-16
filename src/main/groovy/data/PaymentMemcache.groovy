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
import com.google.appengine.api.datastore.Query.FilterPredicate

/**
 * Data and Meta Data Accessor for PaymentMemcache Entity.
 * 
 * @author awijasa
 */

class PaymentMemcache {
    static List<Entity> findByMemcacheKey( def memcacheKey ) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "PaymentMemcache" )
        q.setFilter( new FilterPredicate( "memcacheKey", FilterOperator.EQUAL, memcacheKey ) )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
    
    static List<Entity> list() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
        Query q = new Query( "PaymentMemcache" )
        datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
    }
}