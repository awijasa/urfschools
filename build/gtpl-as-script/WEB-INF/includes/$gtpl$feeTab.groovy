package web_inf.includes;out.print("""""");
	if( user == null ) redirect "/index.gtpl"
	else {
;
out.print("""
		<div id=\"fee_tab\">
		  <div class=\"new_record_form ui-corner-all\">
		    """);
/* include#begin \WEB-INF\includes\addFee.gtpl */
  LinkedList<com.google.appengine.api.datastore.Entity> userPrivilegeList = data.UserPrivilege.findByUserEmailAndPrivilege( user.getEmail(), "Modify" )
  
  html.form( action: "javascript:void( 0 )", id: "new_fee_form" ) {
    
    /* Enrollment Selector */
    div( class: "add_fee_enrollment", "Enrollment:" )
    a( class: "add_fee_enrollment_selector", href: "javascript:void( 0 )", "select" )
    input( autocomplete: "off", class: "add_fee_student_id", name: "studentId", type: "hidden" )
    input( autocomplete: "off", class: "add_fee_school_name", name: "schoolName", type: "hidden" )
    input( autocomplete: "off", class: "add_fee_enroll_term_no", name: "enrollTermNo", type: "hidden" )
    input( autocomplete: "off", class: "add_fee_enroll_term_year", name: "enrollTermYear", type: "hidden" )
    input( autocomplete: "off", class: "add_fee_leave_term_no", name: "leaveTermNo", type: "hidden" )
    input( autocomplete: "off", class: "add_fee_leave_term_year", name: "leaveTermYear", type: "hidden" )
    input( autocomplete: "off", class: "add_fee_first_class_attended", name: "firstClassAttended", type: "hidden" )
    input( autocomplete: "off", class: "add_fee_last_class_attended", name: "lastClassAttended", type: "hidden" )
    img( class: "add_fee_enrollment_selector_wait", src: "/images/ajax-loader.gif" )
    
    /* Name Text Field */
    div( class: "add_fee_name_required_ind", "*" )
    div( class: "add_fee_name_label ui-corner-all", "Name" ) 
    input( autocomplete: "off", class: "add_fee_name", name: "name", type: "text", value: "Name" )
    
    /* Term Selector */
    div( class: "add_fee_term", "Term:" )
    a( class: "add_fee_term_selector", href: "javascript:void( 0 )", "select" )
    input( autocomplete: "off", class: "add_fee_class_term_no", name: "classTermNo", type: "hidden" )
    input( autocomplete: "off", class: "add_fee_class_term_year", name: "classTermYear", type: "hidden" )
    img( class: "add_fee_term_selector_wait", src: "/images/ajax-loader.gif" )
    
    /* Amount Text Field */
    div( class: "add_fee_amount_currency", "UGX" )
    div( class: "add_fee_amount_required_ind", "*" )
    div( class: "add_fee_amount_label ui-corner-all", "Amount" ) 
    input( autocomplete: "off", class: "add_fee_amount", name: "amount", type: "text", value: "Amount" )
    
    /* Comment Text Area */
	div( class: "add_fee_comment_label ui-corner-all", "Comment" )
	textarea( autocomplete: "off", class: "add_fee_comment", name: "comment", "Comment" )
  
    /* Add Fee Link */
    if( data.URFUser.findByEmail( user.getEmail() ) ) {
	    a( class: "add_fee_link", href: "javascript:void( 0 )", "add fee" )
	    img( class: "add_fee_link_wait", src: "/images/ajax-loader.gif" )
    }
      
    div( class: "required_symbol", "*" )
    div( class: "required_text", "required" )
    input( name: "action", type: "hidden", value: "save" )
  }
;
out.print("""

<script type=\"text/javascript\">
	
	/* Initialize the Name Text Field. Refer to /js/fieldInit.js. */
	initTextFieldValueWithName( \".add_fee_name\", jQuery( \".add_fee_name\" ).val() );
	
	/* Initialize the Amount Text Field. Refer to /js/fieldInit.js. */
	initTextFieldValueWithName( \".add_fee_amount\", jQuery( \".add_fee_amount\" ).val() );
	
	/* Initialize the Comment Text Area. Refer to /js/fieldInit.js. */
  	initTextAreaValueWithName( \".add_fee_comment\", jQuery( \".add_fee_comment\" ).val() );
  	
  var addFeeDialogClassAttended = jQuery( \".add_fee_dialog_class_attended\" );
  
  jQuery( \".add_fee_term_selector\" ).click(
  	function() {
  		var addFeeDialogClassAttendedFirstRequest = jQuery( \".add_fee_dialog_class_attended_first_request\" );
  		
  		if( addFeeDialogClassAttendedFirstRequest.text() == \"Yes\" ) {
	  		jQuery.ajax(
	  			{
	  				url: \"/dialogClassAttended.gtpl\"
	  				, type: \"GET\"
	  				, data: jQuery.param(
							{
								action: \"view\"
								, studentId: jQuery( \".add_fee_student_id\" ).val()
								, termSchool: jQuery( \".add_fee_school_name\" ).val()
								, enrollTermNo: jQuery( \".add_fee_enroll_term_no\" ).val()
								, enrollTermYear: jQuery( \".add_fee_enroll_term_year\" ).val()
								, leaveTermNo: jQuery( \".add_fee_leave_term_no\" ).val()
								, leaveTermYear: jQuery( \".add_fee_leave_term_year\" ).val()
								, firstClassAttended: jQuery( \".add_fee_first_class_attended\" ).val()
								, lastClassAttended: jQuery( \".add_fee_last_class_attended\" ).val()
								, windowScrollLeft: jQuery( window ).scrollLeft()
				  				, windowScrollTop: jQuery( window ).scrollTop()
							}
						)
					, beforeSend: function() {
							jQuery( \".add_fee_term_selector\" ).toggle();
							jQuery( \".add_fee_term_selector_wait\" ).toggle();
						}
					, complete: function() {
							jQuery( \".add_fee_term_selector\" ).toggle();
							jQuery( \".add_fee_term_selector_wait\" ).toggle();
						}
					, success: function( data ) {
							addFeeDialogClassAttended.dialog( \"open\" );
							addFeeDialogClassAttended.children().remove();
							addFeeDialogClassAttended.append( data );
						}
					, error: function( jqXHR, textStatus, errorThrown ) {
						if( textStatus == null || jqXHR.getResponseHeader( \"Response-Phrase\" ) == null ) {
							alert( \"We're sorry. The servers did not respond on time. Please try again\" );
						}
						else {
							alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
						}
					  }
	  			}
	  		);
	  		
	  		addFeeDialogClassAttendedFirstRequest.remove();
  		}
  		else {
  			addFeeDialogClassAttended.dialog( \"open\" );
  		}
  	}
  );
  
  var addFeeListEnrollment = jQuery( \".add_fee_list_enrollment\" );
  
  jQuery( \".add_fee_enrollment_selector\" ).click(
    function() {
      if( jQuery( this ).text() == \"select\" ) {
      	var addFeeListEnrollmentFirstRequest = jQuery( \".add_fee_list_enrollment_first_request\" );
      	
      	if( addFeeListEnrollmentFirstRequest.text() == \"Yes\" ) {
      		jQuery.ajax(
			  {
				url: \"/listEnrollment.gtpl\"
				, type: \"GET\"
				, data: jQuery.param(
					{
						limit: 20
					  , offset: 0
					  , windowScrollLeft: jQuery( window ).scrollLeft()
					  , windowScrollTop: jQuery( window ).scrollTop()
					}
				  )
				, beforeSend: function() {
					jQuery( \".add_fee_enrollment_selector\" ).toggle();
					jQuery( \".add_fee_enrollment_selector_wait\" ).toggle();
				  }
				, complete: function() {
					jQuery( \".add_fee_enrollment_selector\" ).toggle();
					jQuery( \".add_fee_enrollment_selector_wait\" ).toggle();
				  }
				, success: function( data ) {
					addFeeListEnrollment.dialog( \"open\" );
					addFeeListEnrollment.children().remove();
					addFeeListEnrollment.append( data );
				  }
				, error: function( jqXHR, textStatus, errorThrown ) {
					if( textStatus == null || jqXHR.getResponseHeader( \"Response-Phrase\" ) == null ) {
						alert( \"We're sorry. The servers did not respond on time. Please try again\" );
					}
					else {
						alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
					}
				  }
			  }
			);
      	
      		addFeeListEnrollmentFirstRequest.remove();
      	}
      	else {
        	addFeeListEnrollment.dialog( \"open\" );
		}
      }
      else {
      	jQuery.ajax(
          {
            url: \"/listEnrollment.gtpl\"
            , type: \"GET\"
            , data: jQuery.param(
                {
                  limit: 20
                  , offset: 0
                  , studentId: jQuery( \".add_fee_student_id\" ).val()
                  , schoolName: jQuery( \".add_fee_school_name\" ).val()
                  , enrollTermNo: jQuery( \".add_fee_enroll_term_no\" ).val()
                  , enrollTermYear: jQuery( \".add_fee_enroll_term_year\" ).val()
                  , windowScrollLeft: jQuery( window ).scrollLeft()
				  , windowScrollTop: jQuery( window ).scrollTop()
                }
              )
            , beforeSend: function() {
                jQuery( \".add_fee_enrollment_selector\" ).toggle();
                jQuery( \".add_fee_enrollment_selector_wait\" ).toggle();
              }
            , complete: function() {
                jQuery( \".add_fee_enrollment_selector\" ).toggle();
                jQuery( \".add_fee_enrollment_selector_wait\" ).toggle();
              }
            , success: function( data ) {
                addFeeListEnrollment.children().remove();
                addFeeListEnrollment.append( data );
                addFeeListEnrollment.dialog( \"open\" );
              }
            , error: function( jqXHR, textStatus, errorThrown ) {
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
  
  /* Initialize the Add Fee Link. */
  jQuery( \".add_fee_link\" ).click(
    function() {
      jQuery.ajax(
        {
          url: \"FeeController.groovy\"
          , type: \"POST\"
          , data: jQuery( \"#new_fee_form\" ).serialize()
          , beforeSend: function() {
            
              /* Switch the \"add fee\" link with a spinning circle ball. */
              jQuery( \".add_fee_link\" ).toggle()
              jQuery( \".add_fee_link_wait\" ).toggle()
            }
          , complete: function() {
            
              /* Switch the spinning circle ball with the \"add fee\" link. */
              jQuery( \".add_fee_link\" ).toggle()
              jQuery( \".add_fee_link_wait\" ).toggle()
            }
          , success: function( data ) {
              var feeList = jQuery( \".list_record_form .fee_list\" );
              var feeListH3 = feeList.children( \"h3\" );
              
              if( feeListH3.length == 20 ) {
                feeListH3.last().remove();
                feeList.children( \"div\" ).last().remove();
                feeList.parents( \".list_record_form\" ).find( \".list_fee_next_twenty\" ).css( \"display\", \"inline\" );
              }
            
              /* Insert the newly added Fee to the top of the Fee list accordion. */
              feeList.prepend( data ).accordion( \"refresh\" ).accordion( \"option\", \"active\", 0 ).scrollTop( 0 );
              
              /* Refer to /js/listInit.js. */
              initFeeList();
              
              updateNextTwentyOffsetsAffectedByFee( data );
  
              setTimeout(
                function() {
                  
                  /* Scroll to the newly created Fee record */
                  jQuery( \"html, body\" ).animate(
                    {
                      scrollTop: feeList.parents( \".list_record_form\" ).offset().top
                    }
                    , \"slow\"
                  );
                }
                , 500
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
/* include#end   \WEB-INF\includes\addFee.gtpl */
out.print("""
		  </div>
		
		  <div class=\"new_record_radio\">
		        <input type=\"radio\" id=\"add_fee_radio\" name=\"new_fee_radio\" checked=\"checked\" /><label for=\"add_fee_radio\">add new</label>
		        <input type=\"radio\" id=\"upload_fees_radio\" name=\"new_fee_radio\" /><label for=\"upload_fees_radio\">upload</label>
		  </div>
		
		  <button class=\"middle_filter\">filter</button>
		  <button class=\"middle_sortby\">sort by</button>
		
		  <div class=\"list_record_form ui-corner-all\">
		    """); include '/WEB-INF/includes/listFee.gtpl?limit=20&offset=0' ;
out.print("""
		  </div>
		
		  <div class=\"list_record_radio\">
		        <input type=\"radio\" id=\"list_fees_radio\" name=\"list_fee_radio\" checked=\"checked\" /><label for=\"list_fees_radio\">list</label>
		        <input type=\"radio\" id=\"download_fees_radio\" name=\"list_fee_radio\" /><label for=\"download_fees_radio\">download</label>
		  </div>
		</div>
"""); } ;
out.print("""

<script type=\"text/javascript\">
  var middleFilterSortByDialog = jQuery( \".middle_filter_sortby_dialog\" );

  var middleFilterSortBy = jQuery( \"#fee_tab\" ).find( \".middle_filter, .middle_sortby\" );

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
          		, of: jQuery( \"#fee_tab .middle_sortby\" )
          		, collision: \"fit\"
          	}
          );
          middleFilterSortByDialog.dialog( \"open\" );
        }
      }
    );

    var downloadRadioDialog = jQuery( \".download_radio_dialog\" );

  /* Initialize the list - download radio buttons. */
  var listRecordRadio = jQuery( \"#fee_tab .list_record_radio\" ).buttonset();
  
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
  var newRecordRadio = jQuery( \"#fee_tab .new_record_radio\" ).buttonset();
  
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
