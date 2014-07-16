package web_inf.includes;out.print("""""");
  html.form( action: "javascript:void( 0 )", id: "new_parent_form" ) {
  	
  	/* First Name Text Field */
  	div( class: "add_parent_first_name_required_ind", "*" )
  	div( class: "add_parent_first_name_label ui-corner-all", "First Name" )
  	input( class: "add_parent_first_name", name: "firstName", type: "text", value: "First Name" )
  	
  	/* Last Name Text Field */
  	div( class: "add_parent_last_name_label ui-corner-all", "Last Name" )
  	input( autocomplete: "off", class: "add_parent_last_name", name: "lastName", type: "text", value: "Last Name" )
  	
  	/* Deceased Checkbox */
  	input( autocomplete: "off", id: "add_parent_deceased_toggle", name: "deceasedInd", type: "checkbox" )
  	label( class: "add_parent_deceased_label", for: "add_parent_deceased_toggle", "Deceased" )
  	
  	/* Village Text Field */
  	div( class: "add_parent_village_label ui-corner-all", "Village" )
  	input( autocomplete: "off", class: "add_parent_village", name: "village", type: "text", value: "Village" )
  	
  	/* Email Text Field */
  	div( class: "add_parent_email_label ui-corner-all", "Email" )
  	input( autocomplete: "off", class: "add_parent_email", name: "email", type: "email", value: "Email" )
  	
  	/* Profession Text Field */
  	div( class: "add_parent_profession_label ui-corner-all", "Profession" )
  	input( autocomplete: "off", class: "add_parent_profession", name: "profession", type: "text", value: "Profession" )
  	
  	/* Primary Phone Text Field */
  	div( class: "add_parent_primary_phone_label ui-corner-all", "Primary Phone" )
  	input( autocomplete: "off", class: "add_parent_primary_phone", name: "primaryPhone", type: "tel", value: "Primary Phone" )
  	
  	/* Secondary Phone Text Field */
  	div( class: "add_parent_secondary_phone_label ui-corner-all", "Secondary Phone" )
  	input( autocomplete: "off", class: "add_parent_secondary_phone", name: "secondaryPhone", type: "tel", value: "Secondary Phone" )
  	
  	/* Parent of Section */
  	div( class: "add_parent_parent_of_label", "Parent of" )
  	
  	/* Relationship List Box */
  	select( autocomplete: "off", class: "add_parent_relationship_list" ) {
  		option( "Relationship" )
  		
  		data.ParentalRelationship.list().each(
  			{
  				option( value: it.parentalRelationship, it.parentalRelationship )
  			}
  		)
  	}
  	
  	div( class: "add_parent_of_label", "of" )
  	
  	a( class: "add_parent_child_selector", href: "javascript:void( 0 )", "select" )
    img( class: "add_parent_child_selector_wait", src: "/images/ajax-loader.gif" )
  	
  	button( class: "add_parent_child_button", type: "button" )
  	
  	div( class: "add_parent_child_table_container" ) {
  		table( class: "add_parent_child_table" )
	}
  	
  	/* Guardian of Section */
  	div( class: "add_parent_guardian_of_label", "Guardian of" )
  	
  	/* Relationship Text Box */
  	div( class: "add_parent_relationship_label ui-corner-all", "Relationship" )
  	input( autocomplete: "off", class: "add_parent_relationship", name: "guardianRelationship", value: "Relationship" )
  	
  	div( class: "add_guardian_of_label", "of" )
  	
  	a( class: "add_parent_relative_selector", href: "javascript:void( 0 )", "select" )
  	img( class: "add_parent_relative_selector_wait", src: "/images/ajax-loader.gif" )
  	
  	button( class: "add_parent_relative_button", type: "button" )
  	
  	div( class: "add_parent_relative_table_container" ) {
  		table( class: "add_parent_relative_table" )
	}
	
	/* Add Parent Link */
  	if( data.URFUser.findByEmail( user.getEmail() ) ) {
	  	a( class: "add_parent_link", href: "javascript:void( 0 )", "add parent" )
	  	img( class: "add_parent_link_wait", src: "/images/ajax-loader.gif" )
  	}
  	
  	div( class: "required_symbol", "*" )
	div( class: "required_text", "required" )
	input( name: "action", type: "hidden", value: "save" )
  }
;
out.print("""

<script type=\"text/javascript\">
  
  /* Initialize the First Name Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( \".add_parent_first_name\", jQuery( \".add_parent_first_name\" ).val() );
  
  /* Initialize the Last Name Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( \".add_parent_last_name\", jQuery( \".add_parent_last_name\" ).val() );
  
  /* Initialize the Village Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( \".add_parent_village\", jQuery( \".add_parent_village\" ).val() );
  
  /* Initialize the Email Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( \".add_parent_email\", jQuery( \".add_parent_email\" ).val() );
  
  /* Initialize the Profession Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( \".add_parent_profession\", jQuery( \".add_parent_profession\" ).val() );
  
  /* Initialize the Primary Phone Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( \".add_parent_primary_phone\", jQuery( \".add_parent_primary_phone\" ).val() );
  
  /* Initialize the Secondary Phone Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( \".add_parent_secondary_phone\", jQuery( \".add_parent_secondary_phone\" ).val() );
  
  /* Initialize the Relationship Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( \".add_parent_relationship\", jQuery( \".add_parent_relationship\" ).val() );
  
  /* Initialize the Add Child Button. */
  jQuery( \".add_parent_child_button\" ).button(
    {
      icons: {
        primary: \"ui-icon-plus\"
      }
      , text: false
    }
  ).off( \"click\" ).click(
  	function() {
  		if( jQuery( \".add_parent_relationship_list option:selected\" ).index() > 0 && jQuery( \".add_parent_child_selector\" ).text() != \"select\" ) {
			var parentalRelationship;
			var studentId;
			
			jQuery.ajax(
				{
				  url: \"StringEscapeUtilsController.groovy\"
				  , async: false
				  , type: \"GET\"
				  , data: jQuery.param(
				  		{
				  			method: \"escapeHtml4\"
				  			, string: jQuery( \".add_parent_relationship_list option:selected\" ).text()
				  		}
				  	)
				  , success: function( data ) {
						parentalRelationship = data;
					}
				  , error: function( jqXHR, textStatus, errorThrown ) {
			
					/* Display an error message popup if the AJAX call returned an error. */
					alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
				  }
				}
			  );
			  
			jQuery.ajax(
				{
				  url: \"StringEscapeUtilsController.groovy\"
				  , async: false
				  , type: \"GET\"
				  , data: jQuery.param(
				  		{
				  			method: \"escapeHtml4\"
				  			, string: jQuery( \".add_parent_child_selector\" ).text()
				  		}
				  	)
				  , success: function( data ) {
						studentId = data;
					}
				  , error: function( jqXHR, textStatus, errorThrown ) {
			
					/* Display an error message popup if the AJAX call returned an error. */
					alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
				  }
				}
			  );
			
			jQuery( \".add_parent_child_table\" ).prepend(
				\"<tr>\" +
					'<td class=\"add_parent_child_table_relationship\">' +
						parentalRelationship +
						'<input name=\"newParentRelationship' + jQuery( \".add_parent_child_table_relationship\" ).length + '\" type=\"hidden\" value=\"' + parentalRelationship + '\" />' +
					\"</td>\" +
					'<td class=\"add_parent_child_table_selector\">of ' +
						'<a href=\"javascript:void( 0 )\">' +
							studentId +
						\"</a>\" +
						'<img class=\"add_parent_child_table_selector_wait\", src=\"/images/ajax-loader.gif\" />' +
						'<input name=\"newChildId' + jQuery( \".add_parent_child_table_selector\" ).length + '\" type=\"hidden\" value=\"' + studentId + '\" />' +
					\"</td>\" +
					\"<td>\" +
						'<button class=\"add_parent_child_delete_button\" type=\"button\" />' +
					\"</td>\" +
				\"</tr>\"
			);
			
			jQuery( \".add_parent_child_table_selector a\" ).off( \"click\" ).click(
				function() {
					var addParentChildTableSelectorAnchor = jQuery( this );
					var addParentChildTableSelectorWait = jQuery( this ).parent().find( \".add_parent_child_table_selector_wait\" );
	
					jQuery.ajax(
					  {
						url: \"/listStudent.gtpl\"
						, type: \"GET\"
						, data: jQuery.param(
							{
								isDialog: \"Y\"
								, isLookup: \"Y\"
							  , limit: 20
							  , offset: 0
							  , studentId: jQuery( this ).text()
							}
						  )
						, beforeSend: function() {
							addParentChildTableSelectorAnchor.toggle();
							addParentChildTableSelectorWait.toggle();
						  }
						, complete: function() {
							addParentChildTableSelectorAnchor.toggle();
							addParentChildTableSelectorWait.toggle();
						  }
						, success: function( data ) {
							dialogStudentLookup.dialog( \"open\" );
							dialogStudentLookup.children().remove();
							dialogStudentLookup.append( data );
						  }
						, error: function( jqXHR, textStatus, errorThrown ) {
							alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
						  }
					  }
					);
				}
			  );
		
			jQuery( \".add_parent_child_delete_button\" ).button(
				{
					icons: {
						primary: \"ui-icon-trash\"
					}
					, text: false
				}
			).off( \"click\" ).click(
				function() {
					jQuery( this ).parent().parent().remove();
				}
			);
		}
  	}
  );
  
  /* Initialize the Add Relative Button. */
  jQuery( \".add_parent_relative_button\" ).button(
    {
      icons: {
        primary: \"ui-icon-plus\"
      }
      , text: false
    }
  ).off( \"click\" ).click(
  	function() {
  		if( jQuery( \".add_parent_relationship\" ).val() != \"Relationship\" && jQuery( \".add_parent_relationship\" ).val() != \"\" && jQuery( \".add_parent_relative_selector\" ).text() != \"select\" ) {
			var parentalRelationship;
			var studentId;
			
			jQuery.ajax(
				{
				  url: \"StringEscapeUtilsController.groovy\"
				  , async: false
				  , type: \"GET\"
				  , data: jQuery.param(
				  		{
				  			method: \"escapeHtml4\"
				  			, string: jQuery( \".add_parent_relationship\" ).val()
				  		}
				  	)
				  , success: function( data ) {
						parentalRelationship = data;
					}
				  , error: function( jqXHR, textStatus, errorThrown ) {
			
					/* Display an error message popup if the AJAX call returned an error. */
					alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
				  }
				}
			  );
			  
			jQuery.ajax(
				{
				  url: \"StringEscapeUtilsController.groovy\"
				  , async: false
				  , type: \"GET\"
				  , data: jQuery.param(
				  		{
				  			method: \"escapeHtml4\"
				  			, string: jQuery( \".add_parent_relative_selector\" ).text()
				  		}
				  	)
				  , success: function( data ) {
						studentId = data;
					}
				  , error: function( jqXHR, textStatus, errorThrown ) {
			
					/* Display an error message popup if the AJAX call returned an error. */
					alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
				  }
				}
			  );
			
			jQuery( \".add_parent_relative_table\" ).prepend(
				\"<tr>\" +
					'<td class=\"add_parent_relative_table_relationship\">' +
						parentalRelationship +
						'<input name=\"newGuardianRelationship' + jQuery( \".add_parent_relative_table_relationship\" ).length + '\" type=\"hidden\" value=\"' + parentalRelationship + '\" />' +
					\"</td>\" +
					'<td class=\"add_parent_relative_table_selector\">of ' +
						'<a href=\"javascript:void( 0 )\">' +
							studentId +
						\"</a>\" +
						'<img class=\"add_parent_relative_table_selector_wait\", src=\"/images/ajax-loader.gif\" />' +
						'<input name=\"newRelativeId' + jQuery( \".add_parent_relative_table_selector\" ).length + '\" type=\"hidden\" value=\"' + studentId + '\" />' +
					\"</td>\" +
					\"<td>\" +
						'<button class=\"add_parent_relative_delete_button\" type=\"button\" />' +
					\"</td>\" +
				\"</tr>\"
			);
			
			jQuery( \".add_parent_relative_table_selector a\" ).off( \"click\" ).click(
				function() {
					var addParentRelativeTableSelectorAnchor = jQuery( this );
					var addParentRelativeTableSelectorWait = jQuery( this ).parent().find( \".add_parent_relative_table_selector_wait\" );
	
					jQuery.ajax(
					  {
						url: \"/listStudent.gtpl\"
						, type: \"GET\"
						, data: jQuery.param(
							{
								isDialog: \"Y\"
								, isLookup: \"Y\"
							  , limit: 20
							  , offset: 0
							  , studentId: jQuery( this ).text()
							}
						  )
						, beforeSend: function() {
							addParentRelativeTableSelectorAnchor.toggle();
							addParentRelativeTableSelectorWait.toggle();
						  }
						, complete: function() {
							addParentRelativeTableSelectorAnchor.toggle();
							addParentRelativeTableSelectorWait.toggle();
						  }
						, success: function( data ) {
							dialogStudentLookup.dialog( \"open\" );
							dialogStudentLookup.children().remove();
							dialogStudentLookup.append( data );
						  }
						, error: function( jqXHR, textStatus, errorThrown ) {
							alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
						  }
					  }
					);
				}
			  );
		
			jQuery( \".add_parent_relative_delete_button\" ).button(
				{
					icons: {
						primary: \"ui-icon-trash\"
					}
					, text: false
				}
			).off( \"click\" ).click(
				function() {
					jQuery( this ).parent().parent().remove();
				}
			);
		}
  	}
  );
  
  var addParentChildListStudent = jQuery( \".add_parent_child_list_student\" );
  
  var addParentRelativeListStudent = jQuery( \".add_parent_relative_list_student\" );
  
  var dialogStudentLookup = jQuery( \".dialog_student_lookup\" );
    
  jQuery( \".add_parent_child_selector\" ).click(
    function() {
    	var addParentChildListStudentFirstRequest = jQuery( \".add_parent_child_list_student_first_request\" );
    	
      if( jQuery( this ).text() == \"select\" ) {
      	if( addParentChildListStudentFirstRequest.text() == \"Yes\" ) {
      		jQuery.ajax(
			  {
				url: \"/listStudent.gtpl\"
				, type: \"GET\"
				, data: jQuery.param(
					{
					  isDialog: \"Y\"
					  , limit: 20
					  , offset: 0
					  , windowScrollLeft: jQuery( window ).scrollLeft()
					  , windowScrollTop: jQuery( window ).scrollTop()
					}
				  )
				, beforeSend: function() {
					jQuery( \".add_parent_child_selector\" ).toggle();
					jQuery( \".add_parent_child_selector_wait\" ).toggle();
				  }
				, complete: function() {
					jQuery( \".add_parent_child_selector\" ).toggle();
					jQuery( \".add_parent_child_selector_wait\" ).toggle();
				  }
				, success: function( data ) {
					addParentChildListStudent.dialog( \"open\" );
					addParentChildListStudent.children().remove();
					addParentChildListStudent.append( data );
				  }
				, error: function( jqXHR, textStatus, errorThrown ) {
					alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
				  }
			  }
			);
			
      		addParentChildListStudentFirstRequest.remove();
      	}
      	else {	
        	addParentChildListStudent.dialog( \"open\" );
		}
      }
      else {
        jQuery.ajax(
          {
            url: \"/listStudent.gtpl\"
            , type: \"GET\"
            , data: jQuery.param(
                {
                  isDialog: \"Y\"
                  , limit: 20
                  , offset: 0
                  , studentId: jQuery( this ).text()
                  , windowScrollLeft: jQuery( window ).scrollLeft()
				  , windowScrollTop: jQuery( window ).scrollTop()
                }
              )
            , beforeSend: function() {
                jQuery( \".add_parent_child_selector\" ).toggle();
                jQuery( \".add_parent_child_selector_wait\" ).toggle();
              }
            , complete: function() {
                jQuery( \".add_parent_child_selector\" ).toggle();
                jQuery( \".add_parent_child_selector_wait\" ).toggle();
              }
            , success: function( data ) {
            	addParentChildListStudent.dialog( \"open\" );
                addParentChildListStudent.children().remove();
                addParentChildListStudent.append( data );
              }
            , error: function( jqXHR, textStatus, errorThrown ) {
                alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
              }
          }
        );
      }
    }
  );
  
  jQuery( \".add_parent_relative_selector\" ).click(
    function() {
    	var addParentRelativeListStudentFirstRequest = jQuery( \".add_parent_relative_list_student_first_request\" );
    	
      if( jQuery( this ).text() == \"select\" ) {
      	if( addParentRelativeListStudentFirstRequest.text() == \"Yes\" ) {
      		jQuery.ajax(
			  {
				url: \"/listStudent.gtpl\"
				, type: \"GET\"
				, data: jQuery.param(
					{
					  isDialog: \"Y\"
					  , limit: 20
					  , offset: 0
					  , windowScrollLeft: jQuery( window ).scrollLeft()
					  , windowScrollTop: jQuery( window ).scrollTop()
					}
				  )
				, beforeSend: function() {
					jQuery( \".add_parent_relative_selector\" ).toggle();
					jQuery( \".add_parent_relative_selector_wait\" ).toggle();
				  }
				, complete: function() {
					jQuery( \".add_parent_relative_selector\" ).toggle();
					jQuery( \".add_parent_relative_selector_wait\" ).toggle();
				  }
				, success: function( data ) {
					addParentRelativeListStudent.dialog( \"open\" );
					addParentRelativeListStudent.children().remove();
					addParentRelativeListStudent.append( data );
				  }
				, error: function( jqXHR, textStatus, errorThrown ) {
					alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
				  }
			  }
			);
			
			addParentRelativeListStudentFirstRequest.remove();
      	}
      	else {
        	addParentRelativeListStudent.dialog( \"open\" );
		}
      }
      else {
        jQuery.ajax(
          {
            url: \"/listStudent.gtpl\"
            , type: \"GET\"
            , data: jQuery.param(
                {
                  isDialog: \"Y\"
                  , limit: 20
                  , offset: 0
                  , studentId: jQuery( this ).text()
                  , windowScrollLeft: jQuery( window ).scrollLeft()
				  , windowScrollTop: jQuery( window ).scrollTop()
                }
              )
            , beforeSend: function() {
                jQuery( \".add_parent_relative_selector\" ).toggle();
                jQuery( \".add_parent_relative_selector_wait\" ).toggle();
              }
            , complete: function() {
                jQuery( \".add_parent_relative_selector\" ).toggle();
                jQuery( \".add_parent_relative_selector_wait\" ).toggle();
              }
            , success: function( data ) {
            	addParentRelativeListStudent.dialog( \"open\" );
                addParentRelativeListStudent.children().remove();
                addParentRelativeListStudent.append( data );
              }
            , error: function( jqXHR, textStatus, errorThrown ) {
                alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
              }
          }
        );
      }
    }
  );
  
  /* Initialize the Add Parent Link. */
  jQuery( \".add_parent_link\" ).click(
    function() {
      jQuery.ajax(
        {
          url: \"ParentController.groovy\"
          , type: \"POST\"
          , data: jQuery( \"#new_parent_form\" ).serialize()
          , beforeSend: function() {
            
              /* Switch the \"add parent\" link with a spinning circle ball. */
              jQuery( \".add_parent_link\" ).toggle()
              jQuery( \".add_parent_link_wait\" ).toggle()
            }
          , complete: function() {
            
              /* Switch the spinning circle ball with the \"add parent\" link. */
              jQuery( \".add_parent_link\" ).toggle()
              jQuery( \".add_parent_link_wait\" ).toggle()
            }
          , success: function( data ) {
              var parentList = jQuery( \".list_record_form .parent_list\" );
              var parentListH3 = parentList.children( \"h3\" );
              
              if( parentListH3.length == 20 ) {
                parentListH3.last().remove();
                parentList.children( \"div\" ).last().remove();
                parentList.parents( \".list_record_form\" ).find( \".list_parent_next_twenty\" ).css( \"display\", \"inline\" );
              }
            
              /* Insert the newly added Parent to the top of the Parent list accordion. */
              parentList.prepend( data ).accordion( \"refresh\" ).accordion( \"option\", \"active\", 0 ).scrollTop( 0 );
              
              autoResizeFieldBasedOnWidth( parentList.find( \".list_parent_details\" ).eq( 0 ), \".list_parent_email\", 210 );
              
              /* Refer to /js/listInit.js. */
              initParentList();
  
              setTimeout(
                function() {
                  
                  /* Scroll to the newly created Parent record */
                  jQuery( \"html, body\" ).animate(
                    {
                      scrollTop: parentList.parents( \".list_record_form\" ).offset().top
                    }
                    , \"slow\"
                  );
                }
                , 500
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
