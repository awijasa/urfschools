package data

import com.google.appengine.api.datastore.Entity

/**
 * Thrown if the Entity supplied is used by another Entity.
 */

class EntityIsUsedException extends EntityException {
    public EntityIsUsedException( def entity, def primaryKeyList ) {
        super( "${ entity.getKind() } with ${ getFormattedPrimaryKeyList( entity, primaryKeyList ) } is still used by another Entity." )
    }
}