<%
	html.a( class: "list_parental_relationship_label", href: "javascript:void( 0 )", "Parental Relationship" )
	html.div( class: "actions_label", "Actions" )
	
	html.div( class: "parental_relationship_list" ) {
	  table {
	      com.google.appengine.api.datastore.Entity urfUser = data.URFUser.findByEmail( user.getEmail() )
	      
	      if( urfUser == null || urfUser.adminPrivilege?.equals( "Read" ) || urfUser.adminPrivilege?.equals( "Modify" ) ) {
	        data.ParentalRelationship.list().each() {
	          mkp.yieldUnescaped( formatter.ListItemFormatter.getParentalRelationshipListItem( it ) )
	        }
	      }
	  }
	}
%>

<script type="text/javascript">
  initParentalRelationshipList();
</script>