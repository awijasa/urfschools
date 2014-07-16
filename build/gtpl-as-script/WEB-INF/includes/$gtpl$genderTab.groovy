package web_inf.includes;out.print("""""");
	if( user == null ) redirect "/index.gtpl"
	else {
;
out.print("""
		<div id=\"gender_tab\">
		  <div class=\"new_record_form ui-corner-all\">
		    """);
/* include#begin \WEB-INF\includes\addGender.gtpl */
	html.form( action: "javascript:void( 0 )", id: "new_gender_form" ) {
	  
	  /* Code Text Field */
	  div( class: "add_gender_code_required_ind", "*" )
	  div( class: "add_gender_code_label ui-corner-all", "Code" )
	  input( autocomplete: "off", class: "add_gender_code", name: "code", type: "text", value: "Code" )
	  
	  /* Description Text Field */
	  div( class: "add_gender_desc_required_ind", "*" )
	  div( class: "add_gender_desc_label ui-corner-all", "Description" )
	  input( autocomplete: "off", class: "add_gender_desc", name: "desc", type: "text", value: "Description" )
	  
	  if( data.URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
	    
	    /* Add Gender Link */
	    a( class: "add_gender_link", href: "javascript:void( 0 )", "add gender" )
	    img( class: "add_gender_link_wait", src: "/images/ajax-loader.gif" )
	  }
	  
	  div( class: "required_symbol", "*" )
	  div( class: "required_text", "required" )
	  input( name: "action", type: "hidden", value: "save" )
	}
;
out.print("""

<script type=\"text/javascript\">
  
  /* Initialize the Code Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( \".add_gender_code\", jQuery( \".add_gender_code\" ).val() );
  
  /* Initialize the Description Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( \".add_gender_desc\", jQuery( \".add_gender_desc\" ).val() );
  
  /* Initialize the Add Gender Link. */
  jQuery( \".add_gender_link\" ).click(
    function() {
      jQuery.ajax(
        {
          url: \"GenderController.groovy\"
          , type: \"POST\"
          , data: jQuery( \"#new_gender_form\" ).serialize()
          , beforeSend: function() {
            
              /* Switch the \"add gender\" link with a spinning circle ball. */
              jQuery( \".add_gender_link\" ).toggle()
              jQuery( \".add_gender_link_wait\" ).toggle()
            }
          , complete: function() {
            
              /* Switch the spinning circle ball with the \"add gender\" link. */
              jQuery( \".add_gender_link\" ).toggle()
              jQuery( \".add_gender_link_wait\" ).toggle()
            }
          , success: function( data ) {
            
              /* Insert the newly added Gender to the top of the Gender list table. */
              jQuery( \".gender_list table\" ).prepend( data );
              
              /* Refer to /js/listInit.js. */
              initGenderList();
  
              /* Scroll to the newly created Gender record */
              jQuery( \"html, body\" ).animate(
                {
                  scrollTop: jQuery( \"#gender_tab .list_record_form\" ).offset().top
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
/* include#end   \WEB-INF\includes\addGender.gtpl */
out.print("""
		  </div>
		
		  <div class=\"new_record_radio\">
		        <input type=\"radio\" id=\"add_gender_radio\" name=\"new_gender_radio\" checked=\"checked\" /><label for=\"add_gender_radio\">add new</label>
		        <input type=\"radio\" id=\"upload_genders_radio\" name=\"new_gender_radio\" /><label for=\"upload_genders_radio\">upload</label>
		  </div>
		
		  <button class=\"middle_filter\">filter</button>
		  <button class=\"middle_sortby\">sort by</button>
		
		  <div class=\"list_record_form ui-corner-all\">
		    """);
/* include#begin \WEB-INF\includes\listGender.gtpl */
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
/* include#end   \WEB-INF\includes\listGender.gtpl */
out.print("""
		  </div>
		
		  <div class=\"list_record_radio\">
		        <input type=\"radio\" id=\"list_genders_radio\" name=\"list_gender_radio\" checked=\"checked\" /><label for=\"list_genders_radio\">list</label>
		        <input type=\"radio\" id=\"download_genders_radio\" name=\"list_gender_radio\" /><label for=\"download_genders_radio\">download</label>
		  </div>
		</div>
"""); } ;
out.print("""

<script type=\"text/javascript\">
  var filterSortByDialogLeftPosition;
  
  filterSortByDialogLeftPosition = jQuery( \".main_content\" ).position().left + 144;
  
  var middleFilterSortByDialog = jQuery( \".middle_filter_sortby_dialog\" )

  var middleFilterSortBy = jQuery( \"#gender_tab\" ).find( \".middle_filter, .middle_sortby\" );

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
          		, of: jQuery( \"#gender_tab .middle_sortby\" )
          		, collision: \"fit\"
          	}
          );
          middleFilterSortByDialog.dialog( \"open\" );
        }
      }
    );
      
  var downloadRadioDialog = jQuery( \".download_radio_dialog\" );

  /* Initialize the list - download radio buttons. */
  var listRecordRadio = jQuery( \"#gender_tab .list_record_radio\" ).buttonset();
  
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
  var newRecordRadio = jQuery( \"#gender_tab .new_record_radio\" ).buttonset();
  
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
