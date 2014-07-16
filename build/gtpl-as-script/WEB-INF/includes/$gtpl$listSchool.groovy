package web_inf.includes;out.print("""""");
	html.a( class: "list_school_name_label", href: "javascript:void( 0 )", "School Name" )
	html.a( class: "list_school_classes_label", href: "javascript:void( 0 )", "Classes" )
	html.div( class: "actions_label", "Actions" )
	
	html.div( class: "school_list" ) {
	  table {
	      com.google.appengine.api.datastore.Entity urfUser = data.URFUser.findByEmail( user.getEmail() )
	      
	      if( urfUser == null || urfUser.adminPrivilege?.equals( "Read" ) || urfUser.adminPrivilege?.equals( "Modify" ) ) {
	        data.School.list().eachWithIndex(
	        	{ school, index ->
	          		mkp.yieldUnescaped( formatter.ListItemFormatter.getSchoolListItem( school, index + 1, null )?: "" )
	        	}
        	)
	      }
	  }
	}
;
out.print("""

<script type=\"text/javascript\">
  initSchoolList();
</script>""");
