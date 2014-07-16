<%
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
%>

<script type="text/javascript">
  
  /* Initialize the Code Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( ".add_gender_code", jQuery( ".add_gender_code" ).val() );
  
  /* Initialize the Description Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( ".add_gender_desc", jQuery( ".add_gender_desc" ).val() );
  
  /* Initialize the Add Gender Link. */
  jQuery( ".add_gender_link" ).click(
    function() {
      jQuery.ajax(
        {
          url: "GenderController.groovy"
          , type: "POST"
          , data: jQuery( "#new_gender_form" ).serialize()
          , beforeSend: function() {
            
              /* Switch the "add gender" link with a spinning circle ball. */
              jQuery( ".add_gender_link" ).toggle()
              jQuery( ".add_gender_link_wait" ).toggle()
            }
          , complete: function() {
            
              /* Switch the spinning circle ball with the "add gender" link. */
              jQuery( ".add_gender_link" ).toggle()
              jQuery( ".add_gender_link_wait" ).toggle()
            }
          , success: function( data ) {
            
              /* Insert the newly added Gender to the top of the Gender list table. */
              jQuery( ".gender_list table" ).prepend( data );
              
              /* Refer to /js/listInit.js. */
              initGenderList();
  
              /* Scroll to the newly created Gender record */
              jQuery( "html, body" ).animate(
                {
                  scrollTop: jQuery( "#gender_tab .list_record_form" ).offset().top
                }
                , "slow"
              );
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
            
            /* Display an error message popup if the AJAX call returned an error. */
            alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
          }
        }
      );
    }
  );
</script>