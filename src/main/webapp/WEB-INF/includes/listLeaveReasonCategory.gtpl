<%
	html.a( class: "list_leave_reason_category_label", href: "javascript:void( 0 )", "Category" )
	html.div( class: "actions_label", "Actions" )
	
	html.div( class: "leave_reason_category_list" ) {
	  table {
	      com.google.appengine.api.datastore.Entity urfUser = data.URFUser.findByEmail( user.getEmail() )
	      
	      if( urfUser == null || urfUser.adminPrivilege?.equals( "Read" ) || urfUser.adminPrivilege?.equals( "Modify" ) ) {
	        data.LeaveReasonCategory.list().each() {
	          mkp.yieldUnescaped( formatter.ListItemFormatter.getLeaveReasonCategoryListItem( it ) )
	        }
	      }
	  }
	}
%>

<script type="text/javascript">
  initLeaveReasonCategoryList();
</script>