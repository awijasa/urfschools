package web_inf.includes;out.print("""""");
	if( user == null ) redirect "/index.gtpl"
	else {
;
out.print("""
		<div id=\"leave_reason_category_tab\">
		  <div class=\"new_record_form ui-corner-all\">
		    """);
/* include#begin \WEB-INF\includes\addLeaveReasonCategory.gtpl */
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
            alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
          }
        }
      );
    }
  );
</script>""");
/* include#end   \WEB-INF\includes\addLeaveReasonCategory.gtpl */
out.print("""
		  </div>
		
		  <div class=\"new_record_radio\">
		        <input type=\"radio\" id=\"add_leave_reason_category_radio\" name=\"new_leave_reason_category_radio\" checked=\"checked\" /><label for=\"add_leave_reason_category_radio\">add new</label>
		        <input type=\"radio\" id=\"upload_leave_reason_categories_radio\" name=\"new_leave_reason_category_radio\" /><label for=\"upload_leave_reason_categories_radio\">upload</label>
		  </div>
		
		  <button class=\"middle_filter\">filter</button>
		  <button class=\"middle_sortby\">sort by</button>
		
		  <div class=\"list_record_form ui-corner-all\">
		    """);
/* include#begin \WEB-INF\includes\listLeaveReasonCategory.gtpl */
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
;
out.print("""

<script type=\"text/javascript\">
  initLeaveReasonCategoryList();
</script>""");
/* include#end   \WEB-INF\includes\listLeaveReasonCategory.gtpl */
out.print("""
		  </div>
		
		  <div class=\"list_record_radio\">
		        <input type=\"radio\" id=\"list_leave_reason_categories_radio\" name=\"list_leave_reason_category_radio\" checked=\"checked\" /><label for=\"list_leave_reason_categories_radio\">list</label>
		        <input type=\"radio\" id=\"download_leave_reason_categories_radio\" name=\"list_leave_reason_category_radio\" /><label for=\"download_leave_reason_categories_radio\">download</label>
		  </div>
		</div>
"""); } ;
out.print("""

<script type=\"text/javascript\">
  var filterSortByDialogLeftPosition;
  
  filterSortByDialogLeftPosition = jQuery( \".main_content\" ).position().left + 144;
  
  var middleFilterSortByDialog = jQuery( \".middle_filter_sortby_dialog\" )

  var middleFilterSortBy = jQuery( \"#leave_reason_category_tab\" ).find( \".middle_filter, .middle_sortby\" );

  middleFilterSortBy.button({
      icons: {
        secondary: \"ui-icon-carat-1-s\"
      }
  })
    .click(
      function() {
        if( middleFilterSortByDialog.dialog( \"isOpen\" ) ) {
          middleFilterSortByDialog.dialog( \"close\" );
        }
        else {
          middleFilterSortByDialog.dialog( \"option\", \"position\",
          	{
          		my: \"left-294 top+13.5\"
          		, of: jQuery( \"#leave_reason_category_tab .middle_sortby\" )
          		, collision: \"fit\"
          	}
          );
          middleFilterSortByDialog.dialog( \"open\" );
        }
      }
    );
      
  var downloadRadioDialog = jQuery( \".download_radio_dialog\" );

  /* Initialize the list - download radio buttons. */
  var listRecordRadio = jQuery( \"#leave_reason_category_tab .list_record_radio\" ).buttonset();
  
  listRecordRadio.find( \"input:radio\" ).eq( 0 ).click(
    function() {
      if( downloadRadioDialog.dialog( \"isOpen\" ) ) {
        downloadRadioDialog.dialog( \"close\" );
      }
    }
  );
  
  listRecordRadio.find( \"input:radio\" ).eq( 1 ).click(
    function() {
      if( downloadRadioDialog.dialog( \"isOpen\" ) ) {
        downloadRadioDialog.dialog( \"close\" );
      }
      else {
        downloadRadioDialog.dialog( \"option\", \"position\",
        	{
          		my: \"left top+28\"
          		, of: jQuery( this )
          		, collision: \"fit\"
          	}
        );
        downloadRadioDialog.dialog( \"open\" );
      }
    }
  );

  var uploadRadioDialog = jQuery( \".upload_radio_dialog\" );

  /* Initialize the add new - upload radio buttons. */
  var newRecordRadio = jQuery( \"#leave_reason_category_tab .new_record_radio\" ).buttonset();
  
  newRecordRadio.find( \"input:radio\" ).eq( 0 ).click(
    function() {
      if( uploadRadioDialog.dialog( \"isOpen\" ) ) {
        uploadRadioDialog.dialog( \"close\" );
      }
    }
  );
  
  newRecordRadio.find( \"input:radio\" ).eq( 1 ).click(
    function() {
      if( uploadRadioDialog.dialog( \"isOpen\" ) ) {
        uploadRadioDialog.dialog( \"close\" );
      }
      else {
        uploadRadioDialog.dialog( \"option\", \"position\",
        	{
          		my: \"left top+28\"
          		, of: jQuery( this )
          		, collision: \"fit\"
          	}
        );
        uploadRadioDialog.dialog( \"open\" );
      }
    }
  );
</script>""");
