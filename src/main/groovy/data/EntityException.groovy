package data

import com.google.appengine.api.datastore.Entity

class EntityException extends Exception {
    public EntityException( def errorMessage ) {
        super( errorMessage )
    }
    
    def static getFormattedPrimaryKeyList( def entity, def primaryKeyList ) {
        def primaryKey = ""
        
        primaryKeyList.each() {
            primaryKey += "${ it }: ${ entity.getProperty( it ) }, "
        }
        
        primaryKey.substring( 0, primaryKey.length() - 2 )
    }
}