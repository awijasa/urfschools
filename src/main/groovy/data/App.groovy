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

/**
 * Data and Meta Data Accessor for App Entity.
 *
 * @author awijasa
 */

class App {
    static Entity get() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService()
		Query q = new Query( "App" )
		List<Entity> appList = datastore.prepare( q ).asList( FetchOptions.Builder.withDefaults() )
		
		if( appList.size() == 0 )
			return null
		else
			return appList.first()
	}
}