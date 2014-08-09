package web_inf.includes;out.print("""""");
	html.form( action: "javascript:void( 0 )", id: "new_leave_reason_category_form" ) {
	  
	  /* Category Text Field */
	  div( class: "add_leave_reason_category_required_ind", "*" )
	  div( class: "add_leave_reason_category_label ui-corner-all", "Category" )
	  input( autocomplete: "off", class: "add_leave_reason_category", name: "category", type: "text", value: "Category" )
	  
	  if( data.URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
	    
	    /* Add Leave Reason Category Link */
	    a( class: "add_leave_reason_category_link", href: "javascript:void( 0 )", "add leave reason category" )
	    img( class: "add_leave_reason_category_link_wait", src: "/images/ajax-loader.gif" )
	  }
	  
	  div( class: "required_symbol", "*" )
	  div( class: "required_text", "required" )
	  input( name: "action", type: "hidden", value: "save" )
	}
;
out.print("""

<script type=\"text/javascript\">
  
  /* Initialize the Category Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( \".add_leave_reason_category\", jQuery( \".add_leave_reason_category\" ).val() );

  /* Initialize the Add Leave Reason Category Link. */
  jQuery( \".add_leave_reason_category_link\" ).click(
    function() {
      jQuery.ajax(
        {
          url: \"LeaveReasonCategoryController.groovy\"
          , type: \"POST\"
          , data: jQuery( \"#new_leave_reason_category_form\" ).serialize()
          , beforeSend: function() {
            
              /* Switch the \"add leave reason category\" link with a spinning circle ball. */
              jQuery( \".add_leave_reason_category_link\" ).toggle()
              jQuery( \".add_leave_reason_category_link_wait\" ).toggle()
            }
          , complete: function() {
            
              /* Switch the spinning circle ball with the \"add leave reason category\" link. */
              jQuery( \".add_leave_reason_category_link\" ).toggle()
              jQuery( \".add_leave_reason_category_link_wait\" ).toggle()
            }
          , success: function( data ) {
            
              /* Insert the newly added Category to the top of the Leave Reason Category list table. */
              jQuery( \".leave_reason_category_list table\" ).prepend( data );
              
              /* Refer to /js/listInit.js. */
              initLeaveReasonCategoryList();
  
              /* Scroll to the newly created Gender record */
              jQuery( \"html, body\" ).animate(
                {
                  scrollTop: jQuery( \"#leave_reason_category_tab .list_record_form\" ).offset().top
                }
                , \"slow\"
              );
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
            
            /* Display an error message popup if the AJAX call returned an error. */
        	  if( textStatus == null || jqXHR.getResponseHeader( \"Response-Phrase\" ) == null ) {
					alert( \"We're sorry. The servers did not respond on time. Please try again\" );
				}
				else {
					alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
				}
          }
        }
      );
    }
  );
</script>""");
