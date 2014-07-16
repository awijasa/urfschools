package web_inf.includes;out.print("""""");
	html.a( class: "list_gender_code_label", href: "javascript:void( 0 )", "Code" )
	html.a( class: "list_gender_desc_label", href: "javascript:void( 0 )", "Description" )
	html.div( class: "actions_label", "Actions" )
	
	html.div( class: "gender_list" ) {
	  table {
	      com.google.appengine.api.datastore.Entity urfUser = data.URFUser.findByEmail( user.getEmail() )
	      
	      if( urfUser == null || urfUser.adminPrivilege?.equals( "Read" ) || urfUser.adminPrivilege?.equals( "Modify" ) ) {
	        data.Gender.list().each() {
	          mkp.yieldUnescaped( formatter.ListItemFormatter.getGenderListItem( it ) )
	        }
	      }
	  }
	}
;
out.print("""

<script type=\"text/javascript\">
  initGenderList();
</script>""");
