package web_inf.includes;out.print("""""");
	if( user == null ) redirect "/index.gtpl"
	else {
;
out.print("""
		<div id=\"parental_relationship_tab\">
		  <div class=\"new_record_form ui-corner-all\">
		    """);
/* include#begin \WEB-INF\includes\addParentalRelationship.gtpl */
	html.form( action: "javascript:void( 0 )", id: "new_parental_relationship_form" ) {
	  
	  /* Parental Relationship Text Field */
	  div( class: "add_parental_relationship_required_ind", "*" )
	  div( class: "add_parental_relationship_label ui-corner-all", "Parental Relationship" )
	  input( autocomplete: "off", class: "add_parental_relationship", name: "parentalRelationship", type: "text", value: "Parental Relationship" )
	  
	  if( data.URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
	  
	    /* Add Parental Relationship Link */
	    a( class: "add_parental_relationship_link", href: "javascript:void( 0 )", "add parental relationship" )
	    img( class: "add_parental_relationship_link_wait", src: "/images/ajax-loader.gif" )
	  }
	  
	  div( class: "required_symbol", "*" )
	  div( class: "required_text", "required" )
	  input( name: "action", type: "hidden", value: "save" )
	}
;
out.print("""

<script type=\"text/javascript\">
  
  /* Initialize the Parental Relationship Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( \".add_parental_relationship\", jQuery( \".add_parental_relationship\" ).val() );
  
  /* Initialize the Add Parental Relationship Link. */
  jQuery( \".add_parental_relationship_link\" ).click(
    function() {
      jQuery.ajax(
        {
          url: \"ParentalRelationshipController.groovy\"
          , type: \"POST\"
          , data: jQuery( \"#new_parental_relationship_form\" ).serialize()
          , beforeSend: function() {
            
              /* Switch the \"add parental relationship\" link with a spinning circle ball. */
              jQuery( \".add_parental_relationship_link\" ).toggle()
              jQuery( \".add_parental_relationship_link_wait\" ).toggle()
            }
          , complete: function() {
            
              /* Switch the spinning circle ball with the \"add parental relationship\" link. */
              jQuery( \".add_parental_relationship_link\" ).toggle()
              jQuery( \".add_parental_relationship_link_wait\" ).toggle()
            }
          , success: function( data ) {
            
              /* Insert the newly added Parental Relationship to the top of the Parental Relationship list table. */
              jQuery( \".parental_relationship_list table\" ).prepend( data );
              
              /* Refer to /js/listInit.js. */
              initParentalRelationshipList();
  
              /* Scroll to the newly created Parental Relationship record */
              jQuery( \"html, body\" ).animate(
                {
                  scrollTop: jQuery( \"#parental_relationship_tab .list_record_form\" ).offset().top
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
/* include#end   \WEB-INF\includes\addParentalRelationship.gtpl */
out.print("""
		  </div>
		
		  <div class=\"new_record_radio\">
		        <input type=\"radio\" id=\"add_parental_relationship_radio\" name=\"new_parental_relationship_radio\" checked=\"checked\" /><label for=\"add_parental_relationship_radio\">add new</label>
		        <input type=\"radio\" id=\"upload_parental_relationships_radio\" name=\"new_parental_relationship_radio\" /><label for=\"upload_parental_relationships_radio\">upload</label>
		  </div>
		
		  <button class=\"middle_filter\">filter</button>
		  <button class=\"middle_sortby\">sort by</button>
		
		  <div class=\"list_record_form ui-corner-all\">
		    """);
/* include#begin \WEB-INF\includes\listParentalRelationship.gtpl */
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
;
out.print("""

<script type=\"text/javascript\">
  initParentalRelationshipList();
</script>""");
/* include#end   \WEB-INF\includes\listParentalRelationship.gtpl */
out.print("""
		  </div>
		
		  <div class=\"list_record_radio\">
		        <input type=\"radio\" id=\"list_parental_relationships_radio\" name=\"list_parental_relationship_radio\" checked=\"checked\" /><label for=\"list_parental_relationships_radio\">list</label>
		        <input type=\"radio\" id=\"download_parental_relationships_radio\" name=\"list_parental_relationship_radio\" /><label for=\"download_parental_relationships_radio\">download</label>
		  </div>
		</div>
"""); } ;
out.print("""

<script type=\"text/javascript\">
  var filterSortByDialogLeftPosition;
  
  filterSortByDialogLeftPosition = jQuery( \".main_content\" ).position().left + 144;
  
  var middleFilterSortByDialog = jQuery( \".middle_filter_sortby_dialog\" )

  var middleFilterSortBy = jQuery( \"#parental_relationship_tab\" ).find( \".middle_filter, .middle_sortby\" );

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
          		, of: jQuery( \"#parental_relationship_tab .middle_sortby\" )
          		, collision: \"fit\"
          	}
          );
          middleFilterSortByDialog.dialog( \"open\" );
        }
      }
    );
      
  var downloadRadioDialog = jQuery( \".download_radio_dialog\" );

  /* Initialize the list - download radio buttons. */
  var listRecordRadio = jQuery( \"#parental_relationship_tab .list_record_radio\" ).buttonset();
  
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
  var newRecordRadio = jQuery( \"#parental_relationship_tab .new_record_radio\" ).buttonset();
  
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
