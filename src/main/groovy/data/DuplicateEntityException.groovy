package data

import com.google.appengine.api.datastore.Entity

/**
 * Thrown if the Entity supplied has existed.
 */

class DuplicateEntityException extends EntityException {
    public DuplicateEntityException( def entity, def primaryKeyList ) {
        super( "${ entity.getKind() } with ${ getFormattedPrimaryKeyList( entity, primaryKeyList ) } has existed." )
    }
}