package web_inf.includes;out.print("""""");
	html.form( action: "URFUserController.groovy", id: "new_urfuser_form" ) {
	  
	  /* Email Text Field */
	  div( class: "add_urfuser_email_required_ind", "*" )
	  div( class: "add_urfuser_email_label ui-corner-all", "Email" )
	  input( autocomplete: "off", class: "add_urfuser_email", name: "email", type: "text", value: "Email" )
	  
	  /* Sponsor Data Privilege List Box */
	  select( autocomplete: "off", class: "add_urfuser_sponsor_data_privilege", name: "sponsorDataPrivilege" ) {
	    option( "Sponsor Data Privilege" )
	    option( "Read" )
	    option( "Modify" )
	  }
	  
	  /* Admin Privilege List Box */
	  select( autocomplete: "off", class: "add_urfuser_admin_privilege", name: "adminPrivilege" ) {
	    option( "Admin Privilege" )
	    option( "Read" )
	    option( "Modify" )
	  }
	  
	  /* School Data Privilege Fieldset */
	  div( class: "add_urfuser_school_data_privilege" ) {
	    fieldset( class: "ui-corner-all" ) {
	      legend( "School Data Privilege" )
	      table( class: "add_urfuser_school_data_privilege_table" ) {
	        data.School.list().each(
	        	{ school ->
		          tr {
		            td( school.name )
		            td {
		              label {
		                input( autocomplete: "off", name: "${ school.getKey().getId() }Privilege", type: "radio", checked: "checked", value: "None" )
		                mkp.yield( "None" )
		              }
		            }
		            td {
		              label {
		                input( autocomplete: "off", name: "${ school.getKey().getId() }Privilege", type: "radio", value: "Read" )
		                mkp.yield( "Read" )
		              }
		            }
		            td {
		              label {
		                input( autocomplete: "off", name: "${ school.getKey().getId() }Privilege", type: "radio", value: "Modify" )
		                mkp.yield( "Modify" )
		              }
		            }
		          }
	          	}
	        )
	      }
	    }
	  }
	  
	  if( data.URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
	  
	    /* Add User Link */
	    a( class: "add_urfuser_link", href: "javascript:void( 0 )", "add user" )
	    img( class: "add_urfuser_link_wait", src: "/images/ajax-loader.gif" )
	  }
	  
	  div( class: "required_symbol", "*" )
	  div( class: "required_text", "required" )
	  input( name: "action", type: "hidden", value: "save" )
	}
;
out.print("""

<script type=\"text/javascript\">
  
  /* Initialize the Email Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( \".add_urfuser_email\", jQuery( \".add_urfuser_email\" ).val() );
  
  /* Initialize the Add User link. */
  jQuery( \".add_urfuser_link\" ).click(
    function() {
      jQuery.ajax(
        {
          url: \"URFUserController.groovy\"
          , type: \"POST\"
          , data: jQuery( \"#new_urfuser_form\" ).serialize()
          , beforeSend: function() {
            
              /* Switch the \"add user\" link with a spinning circle ball. */
              jQuery( \".add_urfuser_link\" ).toggle()
              jQuery( \".add_urfuser_link_wait\" ).toggle()
            }
          , complete: function() {
            
              /* Switch the spinning circle ball with the \"add user\" link. */
              jQuery( \".add_urfuser_link\" ).toggle()
              jQuery( \".add_urfuser_link_wait\" ).toggle()
            }
          , success: function( data ) {
              var urfUserList = jQuery( \".urfuser_list\" );
              var urfUserListH3 = urfUserList.children( \"h3\" );
              
              if( urfUserListH3.length == 20 ) {
                urfUserListH3.last().remove();
                urfUserList.children( \"div\" ).last().remove();
                jQuery( \".list_urfuser_next_twenty\" ).css( \"display\", \"inline\" );
              }
            
              /* Insert the newly added User to the top of the User list table. */
              urfUserList.prepend( data ).accordion( \"refresh\" ).accordion( \"option\", \"active\", 0 ).scrollTop( 0 );
              
              /* Refer to /js/listInit.js. */
              initURFUserList();
  
              setTimeout(
                function() {
                  /* Scroll to the newly created User record */
                  jQuery( \"html, body\" ).animate(
                    {
                      scrollTop: jQuery( \"#user_tab .list_record_form\" ).offset().top
                    }
                    , \"slow\"
                  );
                }
                , 500
              );
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
            alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
          }
        }
      );
    }
  );
</script>""");
