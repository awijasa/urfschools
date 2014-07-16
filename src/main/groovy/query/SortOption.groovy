package query

import com.google.appengine.api.search.SortExpression.SortDirection

class SortOption {
	String fieldName
	String fieldType
	SortDirection sortDirection
}
