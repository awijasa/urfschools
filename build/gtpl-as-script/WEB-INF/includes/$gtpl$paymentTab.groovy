package web_inf.includes;out.print("""""");
	if( user == null ) redirect "/index.gtpl"
	else {
;
out.print("""
		<div id=\"payment_tab\">
		  <div class=\"new_record_form ui-corner-all\">
		    """);
/* include#begin \WEB-INF\includes\addPayment.gtpl */
  LinkedList<com.google.appengine.api.datastore.Entity> userPrivilegeList = data.UserPrivilege.findByUserEmailAndPrivilege( user.getEmail(), "Modify" )
  
  html.form( action: "javascript:void( 0 )", id: "new_payment_form" ) {
    
    /* Enrollment Selector */
    div( class: "add_payment_enrollment", "Enrollment:" )
    a( class: "add_payment_enrollment_selector", href: "javascript:void( 0 )", "select" )
    input( autocomplete: "off", class: "add_payment_student_id", name: "studentId", type: "hidden" )
    input( autocomplete: "off", class: "add_payment_school_name", name: "schoolName", type: "hidden" )
    input( autocomplete: "off", class: "add_payment_enroll_term_no", name: "enrollTermNo", type: "hidden" )
    input( autocomplete: "off", class: "add_payment_enroll_term_year", name: "enrollTermYear", type: "hidden" )
    input( autocomplete: "off", class: "add_payment_leave_term_no", name: "leaveTermNo", type: "hidden" )
    input( autocomplete: "off", class: "add_payment_leave_term_year", name: "leaveTermYear", type: "hidden" )
    input( autocomplete: "off", class: "add_payment_first_class_attended", name: "firstClassAttended", type: "hidden" )
    input( autocomplete: "off", class: "add_payment_last_class_attended", name: "lastClassAttended", type: "hidden" )
    img( class: "add_payment_enrollment_selector_wait", src: "/images/ajax-loader.gif" )
    
    /* Funding Source Selector */
    div( class: "add_payment_funding_source", "Funding Source:" )
    a( class: "add_payment_funding_source_selector", href: "javascript:void( 0 )", "select" )
    input( autocomplete: "off", class: "add_payment_funding_source", name: "fundingSource", type: "hidden" )
    img( class: "add_payment_funding_source_selector_wait", src: "/images/ajax-loader.gif" )
    
    /* Term Selector */
    div( class: "add_payment_term", "Term:" )
    a( class: "add_payment_term_selector", href: "javascript:void( 0 )", "select" )
    input( autocomplete: "off", class: "add_payment_class_term_no", name: "classTermNo", type: "hidden" )
    input( autocomplete: "off", class: "add_payment_class_term_year", name: "classTermYear", type: "hidden" )
    img( class: "add_payment_term_selector_wait", src: "/images/ajax-loader.gif" )
    
    /* Amount Text Field */
    div( class: "add_payment_amount_currency", "UGX" )
    div( class: "add_payment_amount_required_ind", "*" )
    div( class: "add_payment_amount_label ui-corner-all", "Amount" ) 
    input( autocomplete: "off", class: "add_payment_amount", name: "amount", type: "text", value: "Amount" )
    
    /* Comment Text Area */
	div( class: "add_payment_comment_label ui-corner-all", "Comment" )
	textarea( autocomplete: "off", class: "add_payment_comment", name: "comment", "Comment" )
  
    /* Add Payment Link */
    if( data.URFUser.findByEmail( user.getEmail() ) ) {
	    a( class: "add_payment_link", href: "javascript:void( 0 )", "add payment" )
	    img( class: "add_payment_link_wait", src: "/images/ajax-loader.gif" )
    }
      
    div( class: "required_symbol", "*" )
    div( class: "required_text", "required" )
    input( name: "action", type: "hidden", value: "save" )
  }
;
out.print("""

<script type=\"text/javascript\">
	
	/* Initialize the Amount Text Field. Refer to /js/fieldInit.js. */
	initTextFieldValueWithName( \".add_payment_amount\", jQuery( \".add_payment_amount\" ).val() );
	
	/* Initialize the Comment Text Area. Refer to /js/fieldInit.js. */
  	initTextAreaValueWithName( \".add_payment_comment\", jQuery( \".add_payment_comment\" ).val() );
  	
  var addPaymentDialogClassAttended = jQuery( \".add_payment_dialog_class_attended\" );
  
  jQuery( \".add_payment_term_selector\" ).click(
  	function() {
  		var addPaymentDialogClassAttendedFirstRequest = jQuery( \".add_payment_dialog_class_attended_first_request\" );
  		
  		if( addPaymentDialogClassAttendedFirstRequest.text() == \"Yes\" ) {
	  		jQuery.ajax(
	  			{
	  				url: \"/dialogClassAttended.gtpl\"
	  				, type: \"GET\"
	  				, data: jQuery.param(
							{
								action: \"view\"
								, studentId: jQuery( \".add_payment_student_id\" ).val()
								, termSchool: jQuery( \".add_payment_school_name\" ).val()
								, enrollTermNo: jQuery( \".add_payment_enroll_term_no\" ).val()
								, enrollTermYear: jQuery( \".add_payment_enroll_term_year\" ).val()
								, leaveTermNo: jQuery( \".add_payment_leave_term_no\" ).val()
								, leaveTermYear: jQuery( \".add_payment_leave_term_year\" ).val()
								, firstClassAttended: jQuery( \".add_payment_first_class_attended\" ).val()
								, lastClassAttended: jQuery( \".add_payment_last_class_attended\" ).val()
								, windowScrollLeft: jQuery( window ).scrollLeft()
				  				, windowScrollTop: jQuery( window ).scrollTop()
							}
						)
					, beforeSend: function() {
							jQuery( \".add_payment_term_selector\" ).toggle();
							jQuery( \".add_payment_term_selector_wait\" ).toggle();
						}
					, complete: function() {
							jQuery( \".add_payment_term_selector\" ).toggle();
							jQuery( \".add_payment_term_selector_wait\" ).toggle();
						}
					, success: function( data ) {
							addPaymentDialogClassAttended.dialog( \"open\" );
							addPaymentDialogClassAttended.children().remove();
							addPaymentDialogClassAttended.append( data );
						}
					, error: function( jqXHR, textStatus, errorThrown ) {
						alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
					  }
	  			}
	  		);
	  		
	  		addPaymentDialogClassAttendedFirstRequest.remove();
  		}
  		else {
  			addPaymentDialogClassAttended.dialog( \"open\" );
  		}
  	}
  );
  
  var addPaymentListEnrollment = jQuery( \".add_payment_list_enrollment\" );
  
  jQuery( \".add_payment_enrollment_selector\" ).click(
    function() {
      if( jQuery( this ).text() == \"select\" ) {
      	var addPaymentListEnrollmentFirstRequest = jQuery( \".add_payment_list_enrollment_first_request\" );
      	
      	if( addPaymentListEnrollmentFirstRequest.text() == \"Yes\" ) {
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
					jQuery( \".add_payment_enrollment_selector\" ).toggle();
					jQuery( \".add_payment_enrollment_selector_wait\" ).toggle();
				  }
				, complete: function() {
					jQuery( \".add_payment_enrollment_selector\" ).toggle();
					jQuery( \".add_payment_enrollment_selector_wait\" ).toggle();
				  }
				, success: function( data ) {
					addPaymentListEnrollment.dialog( \"open\" );
					addPaymentListEnrollment.children().remove();
					addPaymentListEnrollment.append( data );
				  }
				, error: function( jqXHR, textStatus, errorThrown ) {
					alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
				  }
			  }
			);
      	
      		addPaymentListEnrollmentFirstRequest.remove();
      	}
      	else {
        	addPaymentListEnrollment.dialog( \"open\" );
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
                  , studentId: jQuery( \".add_payment_student_id\" ).val()
                  , schoolName: jQuery( \".add_payment_school_name\" ).val()
                  , enrollTermNo: jQuery( \".add_payment_enroll_term_no\" ).val()
                  , enrollTermYear: jQuery( \".add_payment_enroll_term_year\" ).val()
                  , windowScrollLeft: jQuery( window ).scrollLeft()
				  , windowScrollTop: jQuery( window ).scrollTop()
                }
              )
            , beforeSend: function() {
                jQuery( \".add_payment_enrollment_selector\" ).toggle();
                jQuery( \".add_payment_enrollment_selector_wait\" ).toggle();
              }
            , complete: function() {
                jQuery( \".add_payment_enrollment_selector\" ).toggle();
                jQuery( \".add_payment_enrollment_selector_wait\" ).toggle();
              }
            , success: function( data ) {
                addPaymentListEnrollment.children().remove();
                addPaymentListEnrollment.append( data );
                addPaymentListEnrollment.dialog( \"open\" );
              }
            , error: function( jqXHR, textStatus, errorThrown ) {
                alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
              }
          }
        );
      }
    }
  );
  
  var addPaymentListFundingSource = jQuery( \".add_payment_list_funding_source\" );
  
  jQuery( \".add_payment_funding_source_selector\" ).click(
    function() {
      if( jQuery( this ).text() == \"select\" || jQuery( this ).text() == \"Self\" || jQuery( this ).text() == \"School\" ) {
      	var addPaymentListFundingSourceFirstRequest = jQuery( \".add_payment_list_funding_source_first_request\" );
      	
      	if( addPaymentListFundingSourceFirstRequest.text() == \"Yes\" ) {
	      	jQuery.ajax(
			  {
				url: \"/listParent.gtpl\"
				, type: \"GET\"
				, data: jQuery.param(
					{
						limit: 2
					  , offset: 0
					  , studentId: jQuery( \".add_payment_student_id\" ).val()
					  , windowScrollLeft: jQuery( window ).scrollLeft()
					  , windowScrollTop: jQuery( window ).scrollTop()
					}
				  )
				, beforeSend: function() {
					jQuery( \".add_payment_funding_source_selector\" ).toggle();
					jQuery( \".add_payment_funding_source_selector_wait\" ).toggle();
				  }
				, complete: function() {
					jQuery( \".add_payment_funding_source_selector\" ).toggle();
					jQuery( \".add_payment_funding_source_selector_wait\" ).toggle();
				  }
				, success: function( data ) {
					addPaymentListFundingSource.dialog( \"open\" );
					addPaymentListFundingSource.children().remove();
					addPaymentListFundingSource.append( data );
				  }
				, error: function( jqXHR, textStatus, errorThrown ) {
					alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
				  }
			  }
			);
			
			addPaymentListFundingSourceFirstRequest.remove();
		}
		else {
			addPaymentListFundingSource.dialog( \"open\" );
		}
      }
      else {
      	jQuery.ajax(
          {
            url: \"/listParent.gtpl\"
            , type: \"GET\"
            , data: jQuery.param(
                {
                  limit: 2
                  , offset: 0
                  , parentId: jQuery( \".add_payment_funding_source_selector\" ).text()
                  , studentId: jQuery( \".add_payment_student_id\" ).val()
                  , windowScrollLeft: jQuery( window ).scrollLeft()
				  , windowScrollTop: jQuery( window ).scrollTop()
                }
              )
            , beforeSend: function() {
                jQuery( \".add_payment_funding_source_selector\" ).toggle();
                jQuery( \".add_payment_funding_source_selector_wait\" ).toggle();
              }
            , complete: function() {
                jQuery( \".add_payment_funding_source_selector\" ).toggle();
                jQuery( \".add_payment_funding_source_selector_wait\" ).toggle();
              }
            , success: function( data ) {
                addPaymentListFundingSource.children().remove();
                addPaymentListFundingSource.append( data );
                addPaymentListFundingSource.dialog( \"open\" );
              }
            , error: function( jqXHR, textStatus, errorThrown ) {
                alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
              }
          }
        );
      }
    }
  );
  
  /* Initialize the Add Payment Link. */
  jQuery( \".add_payment_link\" ).click(
    function() {
      jQuery.ajax(
        {
          url: \"PaymentController.groovy\"
          , type: \"POST\"
          , data: jQuery( \"#new_payment_form\" ).serialize()
          , beforeSend: function() {
            
              /* Switch the \"add payment\" link with a spinning circle ball. */
              jQuery( \".add_payment_link\" ).toggle()
              jQuery( \".add_payment_link_wait\" ).toggle()
            }
          , complete: function() {
            
              /* Switch the spinning circle ball with the \"add payment\" link. */
              jQuery( \".add_payment_link\" ).toggle()
              jQuery( \".add_payment_link_wait\" ).toggle()
            }
          , success: function( data ) {
              var paymentList = jQuery( \".list_record_form .payment_list\" );
              var paymentListH3 = paymentList.children( \"h3\" );
              
              if( paymentListH3.length == 20 ) {
                paymentListH3.last().remove();
                paymentList.children( \"div\" ).last().remove();
                paymentList.parents( \".list_record_form\" ).find( \".list_payment_next_twenty\" ).css( \"display\", \"inline\" );
              }
            
              /* Insert the newly added Payment to the top of the Payment list accordion. */
              paymentList.prepend( data ).accordion( \"refresh\" ).accordion( \"option\", \"active\", 0 ).scrollTop( 0 );
              
              /* Refer to /js/listInit.js. */
              initPaymentList();
              
              updateNextTwentyOffsetsAffectedByPayment( data );
  
              setTimeout(
                function() {
                  
                  /* Scroll to the newly created Payment record */
                  jQuery( \"html, body\" ).animate(
                    {
                      scrollTop: paymentList.parents( \".list_record_form\" ).offset().top
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
/* include#end   \WEB-INF\includes\addPayment.gtpl */
out.print("""
		  </div>
		
		  <div class=\"new_record_radio\">
		        <input type=\"radio\" id=\"add_payment_radio\" name=\"new_payment_radio\" checked=\"checked\" /><label for=\"add_payment_radio\">add new</label>
		        <input type=\"radio\" id=\"upload_payments_radio\" name=\"new_payment_radio\" /><label for=\"upload_payments_radio\">upload</label>
		  </div>
		
		  <button class=\"middle_filter\">filter</button>
		  <button class=\"middle_sortby\">sort by</button>
		
		  <div class=\"list_record_form ui-corner-all\">
		    """); include '/WEB-INF/includes/listPayment.gtpl?limit=20&offset=0' ;
out.print("""
		  </div>
		
		  <div class=\"list_record_radio\">
		        <input type=\"radio\" id=\"list_payments_radio\" name=\"list_payment_radio\" checked=\"checked\" /><label for=\"list_payments_radio\">list</label>
		        <input type=\"radio\" id=\"download_payments_radio\" name=\"list_payment_radio\" /><label for=\"download_payments_radio\">download</label>
		  </div>
		</div>
"""); } ;
out.print("""

<script type=\"text/javascript\">
  var middleFilterSortByDialog = jQuery( \".middle_filter_sortby_dialog\" );

  var middleFilterSortBy = jQuery( \"#payment_tab\" ).find( \".middle_filter, .middle_sortby\" );

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
          		, of: jQuery( \"#payment_tab .middle_sortby\" )
          		, collision: \"fit\"
          	}
          );
          middleFilterSortByDialog.dialog( \"open\" );
        }
      }
    );

    var downloadRadioDialog = jQuery( \".download_radio_dialog\" );

  /* Initialize the list - download radio buttons. */
  var listRecordRadio = jQuery( \"#payment_tab .list_record_radio\" ).buttonset();
  
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
  var newRecordRadio = jQuery( \"#payment_tab .new_record_radio\" ).buttonset();
  
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
