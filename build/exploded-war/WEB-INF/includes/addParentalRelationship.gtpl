<%
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
%>

<script type="text/javascript">
  
  /* Initialize the Parental Relationship Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( ".add_parental_relationship", jQuery( ".add_parental_relationship" ).val() );
  
  /* Initialize the Add Parental Relationship Link. */
  jQuery( ".add_parental_relationship_link" ).click(
    function() {
      jQuery.ajax(
        {
          url: "ParentalRelationshipController.groovy"
          , type: "POST"
          , data: jQuery( "#new_parental_relationship_form" ).serialize()
          , beforeSend: function() {
            
              /* Switch the "add parental relationship" link with a spinning circle ball. */
              jQuery( ".add_parental_relationship_link" ).toggle()
              jQuery( ".add_parental_relationship_link_wait" ).toggle()
            }
          , complete: function() {
            
              /* Switch the spinning circle ball with the "add parental relationship" link. */
              jQuery( ".add_parental_relationship_link" ).toggle()
              jQuery( ".add_parental_relationship_link_wait" ).toggle()
            }
          , success: function( data ) {
            
              /* Insert the newly added Parental Relationship to the top of the Parental Relationship list table. */
              jQuery( ".parental_relationship_list table" ).prepend( data );
              
              /* Refer to /js/listInit.js. */
              initParentalRelationshipList();
  
              /* Scroll to the newly created Parental Relationship record */
              jQuery( "html, body" ).animate(
                {
                  scrollTop: jQuery( "#parental_relationship_tab .list_record_form" ).offset().top
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