package web_inf.includes;out.print("""""");
	html.form( action: "javascript:void( 0 )", id: "new_school_form" ) {
	  
	  /* School Name Text Field */
	  div( class: "add_school_name_required_ind", "*" )
	  div( class: "add_school_name_label ui-corner-all", "School Name" )
	  input( autocomplete: "off", class: "add_school_name", name: "schoolName", type: "text", value: "School Name" )
	  
	  /* New Class Text Field */
	  div( class: "add_school_class_label ui-corner-all", "New Class" )
	  input( autocomplete: "off", class: "add_school_class", name: "string", type: "text", value: "New Class" )
	  button( class: "add_school_class_button", type: "button" )
	  img( class: "add_school_class_button_wait", src: "/images/ajax-loader.gif" )
	  
	  if( data.URFUser.findByEmail( user.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
	    
	    /* Add School Link */
	    a( class: "add_school_link", href: "javascript:void( 0 )", "add school" )
	    img( class: "add_school_link_wait", src: "/images/ajax-loader.gif" )
	  }
	  
	  /* Classes associated to the School */
	  div( class: "add_school_classes_label", "Classes:" )
	  div( class: "add_school_classes" )
	  
	  span( class: "add_school_decrement_class_level_left add_school_disabled glyphicons glyphicons_arrow_up" )
	  span( class: "add_school_increment_class_level_left add_school_disabled glyphicons glyphicons_arrow_down" )
	  span( class: "add_school_delete_class_left add_school_disabled glyphicons glyphicons_trash" )
	  
	  span( class: "add_school_decrement_class_level_right add_school_disabled glyphicons glyphicons_arrow_up" )
	  span( class: "add_school_increment_class_level_right add_school_disabled glyphicons glyphicons_arrow_down" )
	  span( class: "add_school_delete_class_right add_school_disabled glyphicons glyphicons_trash" )
	  
	  div( class: "required_symbol", "*" )
	  div( class: "required_text", "required" )
	  input( name: "action", type: "hidden", value: "save" )
	}
;
out.print("""

<script type=\"text/javascript\">
  
  /* Initialize the School Name Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( \".add_school_name\", jQuery( \".add_school_name\" ).val() );
  
  /* Initialize the New Class Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( \".add_school_class\", jQuery( \".add_school_class\" ).val() );
  
  /* Assign Decrement Class Level, Increment Class Level, and Delete Class buttons into one variable */
  var addSchoolClassControls = jQuery( \".add_school_decrement_class_level_left\" +
	\", .add_school_decrement_class_level_right\" +
	\", .add_school_increment_class_level_left\" +
	\", .add_school_increment_class_level_right\" +
	\", .add_school_delete_class_left\" +
	\", .add_school_delete_class_right\" ).hover(
			function() {
				if( !jQuery( this ).hasClass( \"add_school_disabled\" ) ) {
					jQuery( this ).removeClass( \"glyphicons\" ).addClass( \"glyphicons_pale\" );
				}
			}
			, function() {
				if( !jQuery( this ).hasClass( \"add_school_disabled\" ) ) {
					jQuery( this ).removeClass( \"glyphicons_pale\" ).addClass( \"glyphicons\" );
				}
			}
		);
  
  /* Initialize the Add School Class Button. */
  jQuery( \".add_school_class_button\" ).button(
    {
      icons: {
        primary: \"ui-icon-plus\"
      }
      , text: false
    }
  ).click(
    function() {
    	
    	/* Bind a function to the click event of the Add School Class Button */
      var newClassSeq = jQuery( \".add_school_classes input:checkbox\" ).size() + 1;
      
      /*
       * To the placeholder for Classes associated to the School:
       * 1. Add a button for the New Class to display the added Class to the user.
       * 2. Add an input HTML element so that the new School form can save the new Class information through the SchoolController.
       */
      
      jQuery.ajax(
        {
          url: \"StringEscapeUtilsController.groovy\"
          , type: \"GET\"
          , data: jQuery( \"#new_school_form\" ).serialize() + \"&method=escapeHtml4\"
          , beforeSend: function() {
            
              /* Switch the \"+\" button with a spinning circle ball. */
              jQuery( \".add_school_class_button\" ).toggle()
              jQuery( \".add_school_class_button_wait\" ).toggle()
              
              /* Disable the \"add school\" link. */
              jQuery( \".add_school_link\" ).addClass( \"add_school_disabled\" );
            }
          , complete: function() {
            
              /* Switch the spinning circle ball with the \"+\" button. */
              jQuery( \".add_school_class_button\" ).toggle()
              jQuery( \".add_school_class_button_wait\" ).toggle()
              
              /* Enable the \"add school\" link. */
              jQuery( \".add_school_link\" ).removeClass( \"add_school_disabled\" );
            }
          , success: function( data ) {
            
            	var toggleButtonId = \"add_school_class\" + newClassSeq;
            	
              var addSchoolClasses = jQuery( \".add_school_classes\" ).append(
                '<input id=\"' + toggleButtonId + '\" type=\"checkbox\"/>' +
                '<label for=\"' + toggleButtonId + '\">' + data + '</label>' +
                '<input name=\"newClass' + newClassSeq + '\" type=\"hidden\" value=\"' + data + '\" />'
              );

                /* Add a Wrench icon to the newly added Class, so that the user gets the cue that he/she can click the Class. */
              jQuery( \"#\" + toggleButtonId ).button(
                {
                  icons: {
                  	secondary: \"ui-icon-wrench\"
                  }
                }
              ).change(
              	function() {
              		if( addSchoolClasses.find( \"input:checkbox:checked\" ).length == 0 ) {
              			addSchoolClassControls.addClass( \"add_school_disabled\" );
              		}
              		else {
              			addSchoolClassControls.removeClass( \"add_school_disabled\" );
              		}
              	}
              );
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
            
            /* Display an error message popup if the AJAX call returned an error. */
			if( jqXHR.getResponseHeader( \"Response-Phrase\" ) == null ) {
				alert( \"We're sorry. Our server is not responding. Please re-add the New Class.\");
			}
			else {
				alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
			}
          }
        }
      );
    }
  );
  
  /* Bind a click event to the Class Control Decrement Class Level buttons */
  jQuery( \".add_school_decrement_class_level_left, .add_school_decrement_class_level_right\" ).click(
  	function() {
  		if( !jQuery( this ).hasClass( \"add_school_disabled\" ) ) {
  			jQuery( \".add_school_classes input:checkbox:checked\" ).each(
  				function( index ) {
  					var checkboxLabel = jQuery( this ).next();
  					var hiddenInput = checkboxLabel.next();
  					var prevHiddenInput = jQuery( this ).prev();
  					var prevCheckbox = prevHiddenInput.prev().prev();
  					
  					if( jQuery( this ).index() > 0 && !prevCheckbox.prop( \"checked\" ) ) {
  						var checkboxId = jQuery( this ).attr( \"id\" );
  						var hiddenInputName = hiddenInput.attr( \"name\" );
  						
  						jQuery( this ).attr( \"id\", prevCheckbox.attr( \"id\" ) );
  						checkboxLabel.attr( \"for\", prevCheckbox.attr( \"id\" ) );
  						hiddenInput.attr( \"name\", prevHiddenInput.attr( \"name\" ) );
  						
  						prevCheckbox.attr( \"id\", checkboxId );
  						prevCheckbox.next().attr( \"for\", checkboxId );
  						prevHiddenInput.attr( \"name\", hiddenInputName );
  						
  						prevCheckbox.before( jQuery( this ) );
  						prevCheckbox.before( checkboxLabel );
  						prevCheckbox.before( hiddenInput );
  					}
  				}
  			);
  		}
  	}
  );
  
  /* Bind a click event to the Class Control Delete buttons */
  jQuery( \".add_school_delete_class_left, .add_school_delete_class_right\" ).click(
  	function() {
  		if( !jQuery( this ).hasClass( \"add_school_disabled\" ) ) {
  			var addSchoolClasses = jQuery( \".add_school_classes\" );
  			
	  		addSchoolClasses.find( \"input:checkbox:checked\" ).each(
	  			function() {
		  			var checkboxLabel = jQuery( this ).next();
		  			
		  			checkboxLabel.next().remove();
		  			checkboxLabel.remove();
		  			jQuery( this ).remove();
	  			}
	  		);
	  		
	  		addSchoolClasses.find( \"input:checkbox\" ).each(
	  			function( index ) {
	  				var checkboxIndex = index + 1;
	  				var checkboxId = \"add_school_class\" + checkboxIndex;
	  				var checkboxLabel = jQuery( this ).next();
	  				
	  				jQuery( this ).attr( \"id\", checkboxId );
	  				checkboxLabel.attr( \"for\", checkboxId );
	  				checkboxLabel.next().attr( \"name\", \"newClass\" + checkboxIndex );
	  			}
	  		);
	  		
	  		jQuery( this ).removeClass( \"glyphicons_pale\" ).addClass( \"glyphicons\" );
	  		
	  		addSchoolClassControls.addClass( \"add_school_disabled\" );
  		}
  	}
  );
  
  /* Bind a click event to the Class Control Increment Class Level buttons */
  jQuery( \".add_school_increment_class_level_left, .add_school_increment_class_level_right\" ).click(
  	function() {
  		if( !jQuery( this ).hasClass( \"add_school_disabled\" ) ) {
  			var addSchoolClassesCheckboxes = jQuery( \".add_school_classes input:checkbox\" );
  			
  			addSchoolClassesCheckboxes.each(
  				function( index ) {
  					var checkboxIndex = addSchoolClassesCheckboxes.length - ( index + 1 );
  					var checkbox = addSchoolClassesCheckboxes.eq( checkboxIndex );
  					var checkboxLabel = checkbox.next();
  					var hiddenInput = checkboxLabel.next();
  					var nextCheckbox = checkbox.next().next().next();
  					var nextHiddenInput = nextCheckbox.next().next();
  					
  					if( checkbox.prop( \"checked\" ) && checkbox.index() < ( addSchoolClassesCheckboxes.length * 3 - 3 ) && !nextCheckbox.prop( \"checked\" ) ) {
  						var checkboxId = checkbox.attr( \"id\" );
  						var hiddenInputName = hiddenInput.attr( \"name\" );
  						
  						checkbox.attr( \"id\", nextCheckbox.attr( \"id\" ) );
  						checkboxLabel.attr( \"for\", nextCheckbox.attr( \"id\" ) );
  						hiddenInput.attr( \"name\", nextHiddenInput.attr( \"name\" ) );
  						
  						nextCheckbox.attr( \"id\", checkboxId );
  						nextCheckbox.next().attr( \"for\", checkboxId );
  						nextHiddenInput.attr( \"name\", hiddenInputName );
  						
  						nextHiddenInput.after( hiddenInput );
  						nextHiddenInput.after( checkboxLabel );
  						nextHiddenInput.after( checkbox );
  					}
  				}
  			);
  		}
  	}
  );
  
  /* Initialize the Add School Link. */
  jQuery( \".add_school_link\" ).click(
    function() {
    	if( !jQuery( this ).hasClass( \"add_school_disabled\" ) ) {
	      jQuery.ajax(
	        {
	          url: \"SchoolController.groovy\"
	          , type: \"POST\"
	          , data: jQuery( \"#new_school_form\" ).serialize()
	          , beforeSend: function() {
	            
	              /* Switch the \"add school\" link with a spinning circle ball. */
	              jQuery( \".add_school_link\" ).toggle()
	              jQuery( \".add_school_link_wait\" ).toggle()
	            }
	          , complete: function() {
	            
	              /* Switch the spinning circle ball with the \"add school\" link. */
	              jQuery( \".add_school_link\" ).toggle()
	              jQuery( \".add_school_link_wait\" ).toggle()
	            }
	          , success: function( data ) {
	            
	              /* Insert the newly added School to the top of the School list table. */
	              jQuery( \".school_list table\" ).prepend( data );
	              
	              /* Refer to /js/listInit.js. */
	              initSchoolList();
	  
	              /* Scroll to the newly created School record */
	              jQuery( \"html, body\" ).animate(
	                {
	                  scrollTop: jQuery( \"#school_tab .list_record_form\" ).offset().top
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
    }
  );
</script>""");
