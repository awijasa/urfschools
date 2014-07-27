function initMain() { 
	
	/* Preload glyphicons-halflings-pale.png */
	var glyphiconsBlue = new Image();
    glyphiconsBlue.src = "/img/glyphicons-halflings-blue.png";
	
    /* Preload glyphicons-halflings-pale.png */
	var glyphiconsPale = new Image();
    glyphiconsPale.src = "/img/glyphicons-halflings-pale.png";
	
	/* Draw Filter and Sort By Buttons at the header. */
    var headerFilterSortBy = jQuery( ".header_filter, .header_sortby" );

    headerFilterSortBy.button({
        icons: {
          secondary: "ui-icon-carat-1-s"
        }
    });

    /* Draw Filter and Sort By Buttons at the middle. */
    var middleFilterSortBy = jQuery( ".middle_filter, .middle_sortby" );

    middleFilterSortBy.button({
        icons: {
          secondary: "ui-icon-carat-1-s"
        }
    });

    /* Initialize the list - download radio buttons. */
    jQuery( ".list_record_radio" ).buttonset();

    /* Initialize the add new - upload radio buttons. */
    jQuery( ".new_record_radio" ).buttonset();

    /* Initialize the tabs. */
    var tabs = jQuery( "#tabs" ).tabs(
      {

        /* Cache the tabs' contents. */
        beforeLoad: function( event, ui ) {
            if ( ui.tab.data( "loaded" ) ) {
            	jQuery( ".progress_bar" ).fadeOut();
                event.preventDefault();
                return;
            }
            else {
            	jQuery( ".progress_bar" ).fadeIn();
            }

            ui.jqXHR.success(function() {
                ui.tab.data( "loaded", true );
            });
        }
        
        , load: function( event, ui ) {
        	jQuery( ".progress_bar" ).fadeOut();
        }
      }
    ).tabs( "option", "active", 0 )

    tabs.find( "ul li a" ).css( "font-size", "0.8em" );
    
    jQuery( ".progress_bar" ).progressbar(
		{
			value: false
		}
	);

    /* Initialize the control to refresh an individual tab. */
    tabs.find( ".tab_refresh" ).click(
      function() {
        if( !tabs.tabs( "option", "disabled" ) ) {
          var refreshTabButton = jQuery( this );
          var tabIndex = refreshTabButton.parent().index();

          /* Activate the tab that contains the clicked refresh button. */
          tabs.tabs( "option", "active", tabIndex );

          var tabPanel = tabs.find( ".ui-tabs-panel" ).eq( tabIndex );
          var tabAnchors = tabs.find( "ul li a" );
          var tabAnchorHrefs = new Array();

          /* Disable the tabs. However, they are still clickable if their anchors' href attributes are not cleared. */
          tabs.tabs( "disable" );

          /* Record the current tab anchors' href attributes, so that they can be restored later. */
          tabAnchors.each(
            function( index, element ) {
              tabAnchorHrefs[index] = jQuery( this ).attr( "href" );
            }
          );

          /* Clear the tab anchors' href attributes, so that the user can't click the tabs while they are disabled. */
          tabAnchors.attr( "href", "javascript:void( 0 )" );

          refreshTabButton.removeClass( "ui-icon-arrowrefresh-1-e" ).addClass( "ui-icon-clock" );
          tabPanel.fadeOut( "slow" );

          jQuery.ajax(
            {
              url: tabAnchorHrefs[tabIndex]
              , type: "GET"
              , success: function( data ) {

                  /* Reveal the refreshed tab panel. */
                  tabPanel.html( data );
                  
                  setTimeout(
	        		  function() {
		                  tabPanel.fadeIn( "slow" );
		                  refreshTabButton.removeClass( "ui-icon-clock" ).addClass( "ui-icon-arrowrefresh-1-e" );
		                  
		                  if( tabPanel.find( "#student_tab" ).length > 0 ) {
		                	  reloadStudentDialogs( true, false, false );
		                  }
		                  else if( tabPanel.find( "#enrollment_tab" ).length > 0 ) {
		                	  reloadStudentDialogs( false, true, false );
		                  }
		                  else if( tabPanel.find( "#fee_tab" ).length > 0 ) {
		                	  var addFeeListEnrollment = jQuery( ".add_fee_list_enrollment" );
		                	  
		                	  if( addFeeListEnrollment.find( ".add_fee_list_enrollment_first_request" ).length == 0 ) {
		                		  addFeeListEnrollment.prepend( '<div class="add_fee_list_enrollment_first_request" style="display: none">Yes</div>' );
		                	  }
		                	  
		                	  var addFeeDialogClassAttended = jQuery( ".add_fee_dialog_class_attended" );
		                	  
		                	  if( addFeeDialogClassAttended.find( ".add_fee_dialog_class_attended_first_request" ).length == 0 ) {
		                		  addFeeDialogClassAttended.prepend( '<div class="add_fee_dialog_class_attended_first_request" style="display: none">Yes</div>' );
		                	  }
		                  }
		                  else if( tabPanel.find( "#parent_tab" ).length > 0 ) {
		                	  reloadStudentDialogs( false, false, true );
		                  }
		                  else if( tabPanel.find( "#payment_tab" ).length > 0 ) {
		                	  var addPaymentListEnrollment = jQuery( ".add_payment_list_enrollment" );
		                	  
		                	  if( addPaymentListEnrollment.find( ".add_payment_list_enrollment_first_request" ).length == 0 ) {
		                		  addPaymentListEnrollment.prepend( '<div class="add_payment_list_enrollment_first_request" style="display: none">Yes</div>' );
		                	  }
		                	  
		                	  var addPaymentListFundingSource = jQuery( ".add_payment_list_funding_source" );
		                	  
		                	  if( addPaymentListFundingSource.find( ".add_payment_list_funding_source_first_request" ).length == 0 ) {
		                		  addPaymentListFundingSource.prepend( '<div class="add_payment_list_funding_source_first_request" style="display: none">Yes</div>' );
		                	  }
		                	  
		                	  var addPaymentDialogClassAttended = jQuery( ".add_payment_dialog_class_attended" );
		                	  
		                	  if( addPaymentDialogClassAttended.find( ".add_payment_dialog_class_attended_first_request" ).length == 0 ) {
		                		  addPaymentDialogClassAttended.prepend( '<div class="add_payment_dialog_class_attended_first_request" style="display: none">Yes</div>' );
		                	  }
		                  }
		
		                  /* Restore the tab anchor href attributes so the user can click them again. */
		                  tabAnchors.each(
		                    function( index, element ) {
		                      jQuery( this ).attr( "href", tabAnchorHrefs[index] );
		                    }
		                  );
		
		                  /* Enable the tabs. */
		                  tabs.tabs( "enable" );
	        		  }
	        		  , 500
        		  );
                }
              , error: function( jqXHR, textStatus, errorThrown ) {
                  alert( textStatus + " " + errorThrown );
                }
            }
          );
        }
      }
    );

    /* Change the height of the tabs if the user is using a mobile device. */
    var isMobile = navigator.userAgent.match( /(iPad)|(iPhone)|(iPod)|(android)|(webOS)/i );

    if( isMobile ) {
      jQuery( "#tabs" ).css( "height", "1922px" );
    }

    var filterSortByDialogLeftPosition;

    filterSortByDialogLeftPosition = jQuery( ".main_content" ).position().left + 144;

    var headerFilterSortByDialog = jQuery( ".header_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 120
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 360
      }
    );

    headerFilterSortBy.click(
      function() {
        if( headerFilterSortByDialog.dialog( "isOpen" ) ) {
          headerFilterSortByDialog.dialog( "close" );
        }
        else {
          headerFilterSortByDialog.dialog( "option", "position",
        	{
        		my: "left-294 top+13.5"
        		, of: jQuery( ".header_sortby" )
        		, collision: "fit"
        	}
          );
          headerFilterSortByDialog.dialog( "open" );
        }
      }
    );

    var middleFilterSortByDialog = jQuery( ".middle_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 120
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 360
      }
    );

    middleFilterSortBy.click(
      function() {
        if( middleFilterSortByDialog.dialog( "isOpen" ) ) {
          middleFilterSortByDialog.dialog( "close" );
        }
        else {
          middleFilterSortByDialog.dialog( "option", "position", [filterSortByDialogLeftPosition, 534 - jQuery( window ).scrollTop()] );
          middleFilterSortByDialog.dialog( "open" );
        }
      }
    );

    jQuery( ".download_radio_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 120
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 360
      }
    );

    jQuery( ".upload_radio_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 120
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 360
      }
    );
    
    jQuery( ".add_enrollment_list_student" ).dialog(
	    {
	      autoOpen: false
	      , height: 1605
	      , hide: "fade"
	      , modal: true
	      , show: "fade"
	      , width: 1012
	    }
    );
    
    jQuery( ".add_fee_dialog_class_attended" ).dialog(
		{
			autoOpen: false
			, height: 700
			, hide: "fade"
			, modal: true
			, show: "fade"
			, width: 1012
		}
	);
        
    jQuery( ".add_fee_list_enrollment" ).dialog(
	    {
	      autoOpen: false
	      , height: 1605
	      , hide: "fade"
	      , modal: true
	      , show: "fade"
	      , width: 1012
	    }
    );
    
    jQuery( ".add_parent_child_list_student" ).dialog(
		{
	        autoOpen: false
	        , height: 1605
	        , hide: "fade"
	        , modal: true
	        , show: "fade"
	        , width: 1012
		}
    );
    
    jQuery( ".add_parent_relative_list_student" ).dialog(
	    {
	      autoOpen: false
	      , height: 1605
	      , hide: "fade"
	      , modal: true
	      , show: "fade"
	      , width: 1012
	    }
    );
    
    jQuery( ".add_payment_dialog_class_attended" ).dialog(
		{
			autoOpen: false
			, height: 700
			, hide: "fade"
			, modal: true
			, show: "fade"
			, width: 1012
		}
	);
    
    jQuery( ".add_payment_list_enrollment" ).dialog(
    	    {
    	      autoOpen: false
    	      , height: 1605
    	      , hide: "fade"
    	      , modal: true
    	      , show: "fade"
    	      , width: 1012
    	    }
        );
    
    jQuery( ".add_payment_list_funding_source" ).dialog(
		{
			autoOpen: false
			, height: 770
			, hide: "fade"
			, modal: true
			, show: "fade"
			, width: 1012
		}
	);
    
    jQuery( ".add_student_matching_list_student" ).dialog(
    		{
    	        autoOpen: false
    	        , height: 1014
    	        , hide: "fade"
    	        , modal: true
    	        , show: "fade"
    	        , width: 1012
    		}
        );
    
    jQuery( ".dialog_class_attended" ).dialog(
		{
			autoOpen: false
			, height: 700
			, hide: "fade"
			, modal: true
			, show: "fade"
			, width: 1012
		}
	);
    
    jQuery( ".dialog_enrollment_lookup" ).dialog(
	    {
	      autoOpen: false
	      , height: 805
	      , hide: "fade"
	      , modal: true
	      , show: "fade"
	      , width: 1012
	    }
    );
    
    jQuery( ".dialog_list_fee" ).dialog(
		{
			autoOpen: false
			, height: 820
			, hide: "fade"
			, modal: true
			, show: "fade"
			, width: 1012
		}
	);
    
    jQuery( ".dialog_list_parent" ).dialog(
		{
			autoOpen: false
			, height: 700
			, hide: "fade"
			, modal: true
			, show: "fade"
			, width: 1012
		}
	);
    
    jQuery( ".dialog_list_payment" ).dialog(
		{
			autoOpen: false
			, height: 820
			, hide: "fade"
			, modal: true
			, show: "fade"
			, width: 1012
		}
	);
    
    jQuery( ".dialog_parent_lookup" ).dialog(
	    {
	      autoOpen: false
	      , height: 700
	      , hide: "fade"
	      , modal: true
	      , show: "fade"
	      , width: 1012
	    }
    );
    
    jQuery( ".dialog_student_lookup" ).dialog(
	    {
	      autoOpen: false
	      , height: 805
	      , hide: "fade"
	      , modal: true
	      , show: "fade"
	      , width: 1012
	    }
    );
    
    jQuery( ".list_parent_child_list_student" ).dialog(
		{
			autoOpen: false
			, height: 1605
			, hide: "fade"
			, modal: true
			, show: "fade"
			, width: 1012
		}
	);
    
    jQuery( ".list_parent_relative_list_student" ).dialog(
		{
			autoOpen: false
			, height: 1605
			, hide: "fade"
			, modal: true
			, show: "fade"
			, width: 1012
		}
	);
    
    var enrollmentBirthDateFilterSortByDialog = jQuery( ".enrollment_birth_date_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , open: function() {
	    		if( jQuery( this ).find( ".enrollment_filter_sortby_dialog_filter_panel:visible" ).length == 1 ) {
	    			showEnrollmentFilterPanel( jQuery( this ) );
	    		}
	    	}
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var enrollmentBoardingFeesFilterSortByDialog = jQuery( ".enrollment_boarding_fees_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var enrollmentClassesAttendedFilterSortByDialog = jQuery( ".enrollment_classes_attended_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , open: function() {
	    		if( jQuery( this ).find( ".enrollment_filter_sortby_dialog_filter_panel:visible" ).length == 1 ) {
	    			showEnrollmentFilterPanel( jQuery( this ) );
	    		}
	    	}
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var enrollmentPeriodFilterSortByDialog = jQuery( ".enrollment_period_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , open: function() {
	    		if( jQuery( this ).find( ".enrollment_filter_sortby_dialog_filter_panel:visible" ).length == 1 ) {
	    			showEnrollmentFilterPanel( jQuery( this ) );
	    		}
	    	}
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var enrollmentFeesDueFilterSortByDialog = jQuery( ".enrollment_fees_due_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , open: function() {
        		if( jQuery( this ).find( ".enrollment_filter_sortby_dialog_filter_panel:visible" ).length == 1 ) {
        			showEnrollmentFilterPanel( jQuery( this ) );
        		}
        	}
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var enrollmentFirstNameFilterSortByDialog = jQuery( ".enrollment_first_name_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , open: function() {
        		if( jQuery( this ).find( ".enrollment_filter_sortby_dialog_filter_panel:visible" ).length == 1 ) {
        			showEnrollmentFilterPanel( jQuery( this ) );
        		}
        	}
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var enrollmentGenderFilterSortByDialog = jQuery( ".enrollment_gender_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var enrollmentStudentIdFilterSortByDialog = jQuery( ".enrollment_student_id_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var enrollmentLastNameFilterSortByDialog = jQuery( ".enrollment_last_name_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
    	, open: function() {
	    		if( jQuery( this ).find( ".enrollment_filter_sortby_dialog_filter_panel:visible" ).length == 1 ) {
	    			showEnrollmentFilterPanel( jQuery( this ) );
	    		}
	    	}
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var enrollmentLeaveReasonFilterSortByDialog = jQuery( ".enrollment_leave_reason_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var enrollmentOtherFeesFilterSortByDialog = jQuery( ".enrollment_other_fees_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var enrollmentPaymentsFilterSortByDialog = jQuery( ".enrollment_payments_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var enrollmentSchoolFilterSortByDialog = jQuery( ".enrollment_school_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var enrollmentSpecialInfoFilterSortByDialog = jQuery( ".enrollment_special_info_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var enrollmentSponsoredFilterSortByDialog = jQuery( ".enrollment_sponsored_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var enrollmentTuitionFeesFilterSortByDialog = jQuery( ".enrollment_tuition_fees_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var enrollmentVillageFilterSortByDialog = jQuery( ".enrollment_village_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , open: function() {
	    		if( jQuery( this ).find( ".enrollment_filter_sortby_dialog_filter_panel:visible" ).length == 1 ) {
	    			showEnrollmentFilterPanel( jQuery( this ) );
	    		}
	    	}
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var studentBirthDateFilterSortByDialog = jQuery( ".student_birth_date_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , open: function() {
	    		if( jQuery( this ).find( ".student_filter_sortby_dialog_filter_panel:visible" ).length == 1 ) {
	    			showStudentFilterPanel( jQuery( this ) );
	    		}
	    	}
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var studentBoardingFeesFilterSortByDialog = jQuery( ".student_boarding_fees_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var studentClassesAttendedFilterSortByDialog = jQuery( ".student_classes_attended_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
    	, open: function() {
	    		if( jQuery( this ).find( ".student_filter_sortby_dialog_filter_panel:visible" ).length == 1 ) {
	    			showStudentFilterPanel( jQuery( this ) );
	    		}
	    	}
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var studentEnrollmentPeriodFilterSortByDialog = jQuery( ".student_enrollment_period_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , open: function() {
	    		if( jQuery( this ).find( ".student_filter_sortby_dialog_filter_panel:visible" ).length == 1 ) {
	    			showStudentFilterPanel( jQuery( this ) );
	    		}
	    	}
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var studentFeesDueFilterSortByDialog = jQuery( ".student_fees_due_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , open: function() {
        		if( jQuery( this ).find( ".student_filter_sortby_dialog_filter_panel:visible" ).length == 1 ) {
        			showStudentFilterPanel( jQuery( this ) );
        		}
        	}
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var studentFirstNameFilterSortByDialog = jQuery( ".student_first_name_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , open: function() {
        		if( jQuery( this ).find( ".student_filter_sortby_dialog_filter_panel:visible" ).length == 1 ) {
        			showStudentFilterPanel( jQuery( this ) );
        		}
        	}
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var studentGenderFilterSortByDialog = jQuery( ".student_gender_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var studentIdFilterSortByDialog = jQuery( ".student_id_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var studentLastNameFilterSortByDialog = jQuery( ".student_last_name_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
    	, open: function() {
	    		if( jQuery( this ).find( ".student_filter_sortby_dialog_filter_panel:visible" ).length == 1 ) {
	    			showStudentFilterPanel( jQuery( this ) );
	    		}
	    	}
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var studentLeaveReasonFilterSortByDialog = jQuery( ".student_leave_reason_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var studentOtherFeesFilterSortByDialog = jQuery( ".student_other_fees_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var studentPaymentsFilterSortByDialog = jQuery( ".student_payments_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var studentSchoolFilterSortByDialog = jQuery( ".student_school_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var studentSpecialInfoFilterSortByDialog = jQuery( ".student_special_info_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var studentSponsoredFilterSortByDialog = jQuery( ".student_sponsored_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var studentTuitionFeesFilterSortByDialog = jQuery( ".student_tuition_fees_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    var studentVillageFilterSortByDialog = jQuery( ".student_village_filter_sortby_dialog" ).dialog(
      {
        autoOpen: false
        , draggable: false
        , height: 58
        , hide: "blind"
        , open: function() {
	    		if( jQuery( this ).find( ".student_filter_sortby_dialog_filter_panel:visible" ).length == 1 ) {
	    			showStudentFilterPanel( jQuery( this ) );
	    		}
	    	}
        , resizable: false
        , show: "blind"
        , width: 120
      }
    );
    
    jQuery( ".enrollment_filter_sortby_dialog_asc_sort_direction_button" ).off( "hover" ).hover(
			function() {
				jQuery( this ).removeClass( "glyphicons" ).addClass( "glyphicons_pale" );
			}
			, function() {
				jQuery( this ).removeClass( "glyphicons_pale" ).addClass( "glyphicons" );
			}
	);
	
	jQuery( ".enrollment_filter_sortby_dialog_dsc_sort_direction_button" ).off( "hover" ).hover(
			function() {
				jQuery( this ).removeClass( "glyphicons" ).addClass( "glyphicons_pale" );
			}
			, function() {
				jQuery( this ).removeClass( "glyphicons_pale" ).addClass( "glyphicons" );
			}
	);
	
	jQuery( ".enrollment_filter_sortby_dialog_filter_apply" ).off( "click" ).click(
			function() {
				jQuery( this ).parent().submit();
			}
	);
	
	jQuery( ".enrollment_filter_sortby_dialog_filter_button" ).off( "click" ).click(
			function() {
				if( jQuery( this ).hasClass( "glyphicons_blue" ) ) {
					jQuery( this ).removeClass( "glyphicons_blue" ).addClass( "glyphicons_pale" );
					
					var enrollmentFilterSortByDialog = jQuery( this ).parent();
					
					enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_panel" ).fadeOut();
					
					jQuery( this ).parent().animate(
							{
								height: "31px"
							}
					).parent().animate(
							{
								width: "150px"
							}
					);
				}
				else {
					jQuery( this ).removeClass( "glyphicons" ).removeClass( "glyphicons_pale" ).addClass( "glyphicons_blue" );
					
					showEnrollmentFilterPanel( jQuery( this ).parent() );
				}
			}
	).off( "hover" ).hover(
			function() {
				if( !jQuery( this ).hasClass( "glyphicons_blue" ) ) {
					jQuery( this ).removeClass( "glyphicons" ).addClass( "glyphicons_pale" );
				}
			}
			, function() {
				if( !jQuery( this ).hasClass( "glyphicons_blue" ) ) {
					jQuery( this ).removeClass( "glyphicons_pale" ).addClass( "glyphicons" );
				}
			}
	);
	
	jQuery( ".enrollment_filter_sortby_dialog_filter_form" ).off( "submit" ).submit(
			function( event ) {
				event.preventDefault();
				
				var enrollmentFilterSortByDialogFilterForm = jQuery( this );
				
				jQuery.ajax(
						{
							url: "SessionController.groovy"
							, type: "POST"
							, data: enrollmentFilterSortByDialogFilterForm.serialize()
							, beforeSend: function() {
									enrollmentFilterSortByDialogFilterForm.find( ".enrollment_filter_sortby_dialog_filter_apply" ).hide();
									enrollmentFilterSortByDialogFilterForm.find( ".enrollment_filter_sortby_dialog_filter_apply_wait" ).show();
								}
							, success: function() {
									var enrollmentFilterSortByDialog = enrollmentFilterSortByDialogFilterForm.parent().parent();
									var listEnrollmentLabel = enrollmentFilterSortByDialog.dialog( "option", "position" ).of;
									var listEnrollment;
									
									if( listEnrollmentLabel.parent().attr( "name" ) == "enrollment_details_form" ) {
										listEnrollment = listEnrollmentLabel.parent().parent().parent().parent();
									}
									else {
										listEnrollment = listEnrollmentLabel.parent();
									}
									
									jQuery.ajax(
											{
												url: "/listEnrollment.gtpl"
												, type: "GET"
												, data: jQuery.param(
														{
															limit: listEnrollment.find( ".list_enrollment_next_twenty_limit" ).text()
															, offset: 0
														}
													)
												, complete: function() {
														enrollmentFilterSortByDialogFilterForm.find( ".enrollment_filter_sortby_dialog_filter_apply" ).show();
														enrollmentFilterSortByDialogFilterForm.find( ".enrollment_filter_sortby_dialog_filter_apply_wait" ).hide();
														enrollmentFilterSortByDialog.dialog( "close" );
													}
												, success: function( data ) {
														reloadEnrollmentDialogs( true, true );
													
														listEnrollment.toggle();
														listEnrollment.children().remove();
														listEnrollment.append( data );
														listEnrollment.toggle();
													}
												, error: function( jqXHR, textStatus, errorThrown ) {
														alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
													}
											}
									);
								}
							, error: function( jqXHR, textStatus, errorThrown ) {
									alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
									
									enrollmentFilterSortByDialogFilterForm.find( ".enrollment_filter_sortby_dialog_filter_apply" ).show();
									enrollmentFilterSortByDialogFilterForm.find( ".enrollment_filter_sortby_dialog_filter_apply_wait" ).hide();
								}
						}
				);
			}
	);
	
	jQuery( ".enrollment_filter_sortby_dialog_filter_less" ).off( "click" ).click(
			function() {
				var enrollmentFilterSortByDialog = jQuery( this ).parents( ".enrollment_filter_sortby_dialog_filter_panel" ).parent();
				
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_less_row" ).hide();
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_more_row" ).fadeIn();
				
				showEnrollmentFilterPanel( enrollmentFilterSortByDialog );
				
				if( !enrollmentFilterSortByDialog.hasClass( "enrollment_first_name_filter_sortby_dialog" ) ) {
					enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_first_name_filter_row" ).fadeOut();
				}
				
				if( !enrollmentFilterSortByDialog.hasClass( "enrollment_last_name_filter_sortby_dialog" ) ) {
					enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_last_name_filter_row" ).fadeOut();
				}
				
				if( !enrollmentFilterSortByDialog.hasClass( "enrollment_classes_attended_filter_sortby_dialog" ) ) {
					enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_classes_attended_filter_row" ).fadeOut();
				}
				
				if( !enrollmentFilterSortByDialog.hasClass( "enrollment_period_filter_sortby_dialog" ) ) {
					enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_enrollment_period_filter_row" ).fadeOut();
				}
				
				if( !enrollmentFilterSortByDialog.hasClass( "enrollment_birth_date_filter_sortby_dialog" ) ) {
					enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_birth_date_filter_row" ).fadeOut();
				}
				
				if( !enrollmentFilterSortByDialog.hasClass( "enrollment_village_filter_sortby_dialog" ) ) {
					enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_village_filter_row" ).fadeOut();
				}
				
				if( !enrollmentFilterSortByDialog.hasClass( "enrollment_fees_due_filter_sortby_dialog" ) ) {
					enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_fees_due_filter_row" ).fadeOut();
				}
			}
	);
	
	jQuery( ".enrollment_filter_sortby_dialog_filter_more" ).off( "click" ).click(
			function() {
				var enrollmentFilterSortByDialog = jQuery( this ).parents( ".enrollment_filter_sortby_dialog_filter_panel" ).parent();
				
				showFullEnrollmentFilterPanel( enrollmentFilterSortByDialog );
				
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_more_row" ).hide();
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_less_row" ).fadeIn();
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_first_name_filter_row" ).fadeIn();
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_last_name_filter_row" ).fadeIn();
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_classes_attended_filter_row" ).fadeIn();
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_enrollment_period_filter_row" ).fadeIn();
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_birth_date_filter_row" ).fadeIn();
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_village_filter_row" ).fadeIn();
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_fees_due_filter_row" ).fadeIn();
			}
	);
	
	jQuery( ".enrollment_filter_sortby_dialog_lookup_button" ).off( "hover" ).hover(
			function() {
				jQuery( this ).removeClass( "ui-icon" ).addClass( "ui-icon-pale" );
			}
			, function() {
				jQuery( this ).removeClass( "ui-icon-pale" ).addClass( "ui-icon" );
			}
	);
	
	jQuery( ".enrollment_filter_sortby_dialog_sort_button" ).off( "hover" ).hover(
			function() {
				jQuery( this ).removeClass( "glyphicons" ).addClass( "glyphicons_pale" );
			}
			, function() {
				jQuery( this ).removeClass( "glyphicons_pale" ).addClass( "glyphicons" );
			}
	);
    
    jQuery( ".student_filter_sortby_dialog_asc_sort_direction_button" ).off( "hover" ).hover(
			function() {
				jQuery( this ).removeClass( "glyphicons" ).addClass( "glyphicons_pale" );
			}
			, function() {
				jQuery( this ).removeClass( "glyphicons_pale" ).addClass( "glyphicons" );
			}
	);
	
	jQuery( ".student_filter_sortby_dialog_dsc_sort_direction_button" ).off( "hover" ).hover(
			function() {
				jQuery( this ).removeClass( "glyphicons" ).addClass( "glyphicons_pale" );
			}
			, function() {
				jQuery( this ).removeClass( "glyphicons_pale" ).addClass( "glyphicons" );
			}
	);
	
	jQuery( ".student_filter_sortby_dialog_filter_apply" ).off( "click" ).click(
			function() {
				jQuery( this ).parent().submit();
			}
	);
	
	jQuery( ".student_filter_sortby_dialog_filter_button" ).off( "click" ).click(
			function() {
				if( jQuery( this ).hasClass( "glyphicons_blue" ) ) {
					jQuery( this ).removeClass( "glyphicons_blue" ).addClass( "glyphicons_pale" );
					
					var studentFilterSortByDialog = jQuery( this ).parent();
					
					studentFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_panel" ).fadeOut();
					
					jQuery( this ).parent().animate(
							{
								height: "31px"
							}
					).parent().animate(
							{
								width: "150px"
							}
					);
				}
				else {
					jQuery( this ).removeClass( "glyphicons" ).removeClass( "glyphicons_pale" ).addClass( "glyphicons_blue" );
					
					showStudentFilterPanel( jQuery( this ).parent() );
				}
			}
	).off( "hover" ).hover(
			function() {
				if( !jQuery( this ).hasClass( "glyphicons_blue" ) ) {
					jQuery( this ).removeClass( "glyphicons" ).addClass( "glyphicons_pale" );
				}
			}
			, function() {
				if( !jQuery( this ).hasClass( "glyphicons_blue" ) ) {
					jQuery( this ).removeClass( "glyphicons_pale" ).addClass( "glyphicons" );
				}
			}
	);
	
	jQuery( ".student_filter_sortby_dialog_filter_form" ).off( "submit" ).submit(
			function( event ) {
				event.preventDefault();
				
				var studentFilterSortByDialogFilterForm = jQuery( this );
				
				jQuery.ajax(
						{
							url: "SessionController.groovy"
							, type: "POST"
							, data: studentFilterSortByDialogFilterForm.serialize()
							, beforeSend: function() {
									studentFilterSortByDialogFilterForm.find( ".student_filter_sortby_dialog_filter_apply" ).hide();
									studentFilterSortByDialogFilterForm.find( ".student_filter_sortby_dialog_filter_apply_wait" ).show();
								}
							, success: function() {
									var studentFilterSortByDialog = studentFilterSortByDialogFilterForm.parent().parent();
									var listStudentLabel = studentFilterSortByDialog.dialog( "option", "position" ).of;
									var listStudent;
									
									if( listStudentLabel.parent().attr( "name" ) == "student_details_form" ) {
										listStudent = listStudentLabel.parent().parent().parent().parent();
									}
									else {
										listStudent = listStudentLabel.parent();
									}
									
									jQuery.ajax(
											{
												url: "/listStudent.gtpl"
												, type: "GET"
												, data: jQuery.param(
														{
															limit: listStudent.find( ".list_student_next_twenty_limit" ).text()
															, offset: 0
														}
													)
												, complete: function() {
														studentFilterSortByDialogFilterForm.find( ".student_filter_sortby_dialog_filter_apply" ).show();
														studentFilterSortByDialogFilterForm.find( ".student_filter_sortby_dialog_filter_apply_wait" ).hide();
														studentFilterSortByDialog.dialog( "close" );
													}
												, success: function( data ) {
														reloadStudentDialogs( true, true, true );
													
														listStudent.toggle();
														listStudent.children().remove();
														listStudent.append( data );
														listStudent.toggle();
													}
												, error: function( jqXHR, textStatus, errorThrown ) {
														alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
													}
											}
									);
								}
							, error: function( jqXHR, textStatus, errorThrown ) {
									alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
									
									studentFilterSortByDialogFilterForm.find( ".student_filter_sortby_dialog_filter_apply" ).show();
									studentFilterSortByDialogFilterForm.find( ".student_filter_sortby_dialog_filter_apply_wait" ).hide();
								}
						}
				);
			}
	);
	
	jQuery( ".student_filter_sortby_dialog_filter_less" ).off( "click" ).click(
			function() {
				var studentFilterSortByDialog = jQuery( this ).parents( ".student_filter_sortby_dialog_filter_panel" ).parent();
				
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_less_row" ).hide();
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_more_row" ).fadeIn();
				
				showStudentFilterPanel( studentFilterSortByDialog );
				
				if( !studentFilterSortByDialog.hasClass( "student_first_name_filter_sortby_dialog" ) ) {
					studentFilterSortByDialog.find( ".student_filter_sortby_dialog_first_name_filter_row" ).fadeOut();
				}
				
				if( !studentFilterSortByDialog.hasClass( "student_last_name_filter_sortby_dialog" ) ) {
					studentFilterSortByDialog.find( ".student_filter_sortby_dialog_last_name_filter_row" ).fadeOut();
				}
				
				if( !studentFilterSortByDialog.hasClass( "student_classes_attended_filter_sortby_dialog" ) ) {
					studentFilterSortByDialog.find( ".student_filter_sortby_dialog_classes_attended_filter_row" ).fadeOut();
				}
				
				if( !studentFilterSortByDialog.hasClass( "student_enrollment_period_filter_sortby_dialog" ) ) {
					studentFilterSortByDialog.find( ".student_filter_sortby_dialog_enrollment_period_filter_row" ).fadeOut();
				}
				
				if( !studentFilterSortByDialog.hasClass( "student_birth_date_filter_sortby_dialog" ) ) {
					studentFilterSortByDialog.find( ".student_filter_sortby_dialog_birth_date_filter_row" ).fadeOut();
				}
				
				if( !studentFilterSortByDialog.hasClass( "student_village_filter_sortby_dialog" ) ) {
					studentFilterSortByDialog.find( ".student_filter_sortby_dialog_village_filter_row" ).fadeOut();
				}
				
				if( !studentFilterSortByDialog.hasClass( "student_fees_due_filter_sortby_dialog" ) ) {
					studentFilterSortByDialog.find( ".student_filter_sortby_dialog_fees_due_filter_row" ).fadeOut();
				}
			}
	);
	
	jQuery( ".student_filter_sortby_dialog_filter_more" ).off( "click" ).click(
			function() {
				var studentFilterSortByDialog = jQuery( this ).parents( ".student_filter_sortby_dialog_filter_panel" ).parent();
				
				showFullStudentFilterPanel( studentFilterSortByDialog );
				
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_more_row" ).hide();
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_less_row" ).fadeIn();
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_first_name_filter_row" ).fadeIn();
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_last_name_filter_row" ).fadeIn();
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_classes_attended_filter_row" ).fadeIn();
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_enrollment_period_filter_row" ).fadeIn();
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_birth_date_filter_row" ).fadeIn();
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_village_filter_row" ).fadeIn();
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_fees_due_filter_row" ).fadeIn();
			}
	);
	
	jQuery( ".student_filter_sortby_dialog_lookup_button" ).off( "hover" ).hover(
			function() {
				jQuery( this ).removeClass( "ui-icon" ).addClass( "ui-icon-pale" );
			}
			, function() {
				jQuery( this ).removeClass( "ui-icon-pale" ).addClass( "ui-icon" );
			}
	);
	
	jQuery( ".student_filter_sortby_dialog_sort_button" ).off( "hover" ).hover(
			function() {
				jQuery( this ).removeClass( "glyphicons" ).addClass( "glyphicons_pale" );
			}
			, function() {
				jQuery( this ).removeClass( "glyphicons_pale" ).addClass( "glyphicons" );
			}
	);
    
    jQuery( "html" ).click(
    	function( event ) {
    		if( event.target.className != "list_enrollment_birth_date_label" && enrollmentBirthDateFilterSortByDialog.dialog( "isOpen" ) ) {
    			enrollmentBirthDateFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_enrollment_boarding_fees_label" && enrollmentBoardingFeesFilterSortByDialog.dialog( "isOpen" ) ) {
    			enrollmentBoardingFeesFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_enrollment_classes_attended_label" && enrollmentClassesAttendedFilterSortByDialog.dialog( "isOpen" ) ) {
    			enrollmentClassesAttendedFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_enrollment_period_label" && enrollmentPeriodFilterSortByDialog.dialog( "isOpen" ) ) {
    			enrollmentPeriodFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_enrollment_fees_due_label" && enrollmentFeesDueFilterSortByDialog.dialog( "isOpen" ) ) {
    			enrollmentFeesDueFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_enrollment_first_name_label" && enrollmentFirstNameFilterSortByDialog.dialog( "isOpen" ) ) {
    			enrollmentFirstNameFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_enrollment_gender_label" && enrollmentGenderFilterSortByDialog.dialog( "isOpen" ) ) {
    			enrollmentGenderFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_enrollment_student_id_label" && enrollmentStudentIdFilterSortByDialog.dialog( "isOpen" ) ) {
    			enrollmentStudentIdFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_enrollment_last_name_label" && enrollmentLastNameFilterSortByDialog.dialog( "isOpen" ) ) {
    			enrollmentLastNameFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_enrollment_leave_reason_label" && enrollmentLeaveReasonFilterSortByDialog.dialog( "isOpen" ) ) {
    			enrollmentLeaveReasonFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_enrollment_other_fees_label" && enrollmentOtherFeesFilterSortByDialog.dialog( "isOpen" ) ) {
    			enrollmentOtherFeesFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_enrollment_payments_label" && enrollmentPaymentsFilterSortByDialog.dialog( "isOpen" ) ) {
    			enrollmentPaymentsFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_enrollment_school_label" && enrollmentSchoolFilterSortByDialog.dialog( "isOpen" ) ) {
    			enrollmentSchoolFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_enrollment_special_info_label" && enrollmentSpecialInfoFilterSortByDialog.dialog( "isOpen" ) ) {
    			enrollmentSpecialInfoFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_enrollment_sponsored_label" && enrollmentSponsoredFilterSortByDialog.dialog( "isOpen" ) ) {
    			enrollmentSponsoredFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_enrollment_tuition_fees_label" && enrollmentTuitionFeesFilterSortByDialog.dialog( "isOpen" ) ) {
    			enrollmentTuitionFeesFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_enrollment_village_label" && enrollmentVillageFilterSortByDialog.dialog( "isOpen" ) ) {
    			enrollmentVillageFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_student_birth_date_label" && studentBirthDateFilterSortByDialog.dialog( "isOpen" ) ) {
    			studentBirthDateFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_student_boarding_fees_label" && studentBoardingFeesFilterSortByDialog.dialog( "isOpen" ) ) {
    			studentBoardingFeesFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_student_classes_attended_label" && studentClassesAttendedFilterSortByDialog.dialog( "isOpen" ) ) {
    			studentClassesAttendedFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_student_enrollment_period_label" && studentEnrollmentPeriodFilterSortByDialog.dialog( "isOpen" ) ) {
    			studentEnrollmentPeriodFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_student_fees_due_label" && studentFeesDueFilterSortByDialog.dialog( "isOpen" ) ) {
    			studentFeesDueFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_student_first_name_label" && studentFirstNameFilterSortByDialog.dialog( "isOpen" ) ) {
    			studentFirstNameFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_student_gender_label" && studentGenderFilterSortByDialog.dialog( "isOpen" ) ) {
    			studentGenderFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_student_id_label" && studentIdFilterSortByDialog.dialog( "isOpen" ) ) {
    			studentIdFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_student_last_name_label" && studentLastNameFilterSortByDialog.dialog( "isOpen" ) ) {
    			studentLastNameFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_student_leave_reason_label" && studentLeaveReasonFilterSortByDialog.dialog( "isOpen" ) ) {
    			studentLeaveReasonFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_student_other_fees_label" && studentOtherFeesFilterSortByDialog.dialog( "isOpen" ) ) {
    			studentOtherFeesFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_student_payments_label" && studentPaymentsFilterSortByDialog.dialog( "isOpen" ) ) {
    			studentPaymentsFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_student_school_label" && studentSchoolFilterSortByDialog.dialog( "isOpen" ) ) {
    			studentSchoolFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_student_special_info_label" && studentSpecialInfoFilterSortByDialog.dialog( "isOpen" ) ) {
    			studentSpecialInfoFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_student_sponsored_label" && studentSponsoredFilterSortByDialog.dialog( "isOpen" ) ) {
    			studentSponsoredFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_student_tuition_fees_label" && studentTuitionFeesFilterSortByDialog.dialog( "isOpen" ) ) {
    			studentTuitionFeesFilterSortByDialog.dialog( "close" );
    		}
    		if( event.target.className != "list_student_village_label" && studentVillageFilterSortByDialog.dialog( "isOpen" ) ) {
    			studentVillageFilterSortByDialog.dialog( "close" );
    		}
    	}
    );
}

function showEnrollmentFilterPanel( enrollmentFilterSortByDialog ) {
	enrollmentFilterSortByDialog.css( "overflow", "hidden" ).animate(
			{
				height: "140px"
			}
	).parent().animate(
			{
				width: "405px"
			}
			, function() {
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_panel" ).fadeIn();
				
				if( enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_more_row:visible" ).length == 0 ) {
					showFullEnrollmentFilterPanel( enrollmentFilterSortByDialog );
				}
				else {
					var hiddenPopulatedSearchFieldCount = 0;
					
					enrollmentFilterSortByDialog.find( "input[type='text']:hidden" ).each(
						function() {
							if( jQuery( this ).val() != "" ) {
								hiddenPopulatedSearchFieldCount++;
							}
						}
					);
					
					if( hiddenPopulatedSearchFieldCount > 0 ) {
						enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_more" ).click();
					}
				}
				
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_birth_date_filter" ).datepicker(
				    {
				        changeMonth: true
				        , changeYear: true
				        , dateFormat: "M d yy"
				        , maxDate: "+1Y"
				        , onSelect: function( dateText, inst ) {
    				          jQuery( this ).css( "color", "#000000" );
    				          jQuery( this ).css( "font-style", "normal" );
    				        }
				        , yearRange: "-40:+0"
				    }
    			);
				
				jQuery( ".ui-datepicker" ).off( "click" ).click(
					function( event ) {
						event.stopPropagation();
					}
				);
				
				/* Prevent iOS7 browser input focus bug */
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_first_name_filter:visible" ).focus().blur();
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_last_name_filter:visible" ).focus().blur();
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_classes_attended_filter:visible" ).focus().blur();
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_enrollment_period_filter:visible" ).focus().blur();
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_village_filter:visible" ).focus().blur();
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_fees_due_filter:visible" ).focus().blur();
			}
	);
}

function showFullEnrollmentFilterPanel( enrollmentFilterSortByDialog ) {
	enrollmentFilterSortByDialog.css( "overflow", "hidden" ).animate(
			{
				height: "405px"
			}
	).parent().animate(
			{
				width: "405px"
			}
			, function() {
				enrollmentFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_panel" ).fadeIn();
			}
	);
}

function showFullStudentFilterPanel( studentFilterSortByDialog ) {
	studentFilterSortByDialog.css( "overflow", "hidden" ).animate(
			{
				height: "405px"
			}
	).parent().animate(
			{
				width: "405px"
			}
			, function() {
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_panel" ).fadeIn();
			}
	);
}

function showStudentFilterPanel( studentFilterSortByDialog ) {
	studentFilterSortByDialog.css( "overflow", "hidden" ).animate(
			{
				height: "140px"
			}
	).parent().animate(
			{
				width: "405px"
			}
			, function() {
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_panel" ).fadeIn();
				
				if( studentFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_more_row:visible" ).length == 0 ) {
					showFullStudentFilterPanel( studentFilterSortByDialog );
				}
				else {
					var hiddenPopulatedSearchFieldCount = 0;
					
					studentFilterSortByDialog.find( "input[type='text']:hidden" ).each(
						function() {
							if( jQuery( this ).val() != "" ) {
								hiddenPopulatedSearchFieldCount++;
							}
						}
					);
					
					if( hiddenPopulatedSearchFieldCount > 0 ) {
						studentFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_more" ).click();
					}
				}
				
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_birth_date_filter" ).datepicker(
				    {
				        changeMonth: true
				        , changeYear: true
				        , dateFormat: "M d yy"
				        , maxDate: "+1Y"
				        , onSelect: function( dateText, inst ) {
    				          jQuery( this ).css( "color", "#000000" );
    				          jQuery( this ).css( "font-style", "normal" );
    				        }
				        , yearRange: "-40:+0"
				    }
    			);
				
				jQuery( ".ui-datepicker" ).off( "click" ).click(
					function( event ) {
						event.stopPropagation();
					}
				);
				
				/* Prevent iOS7 browser input focus bug */
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_first_name_filter:visible" ).focus().blur();
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_last_name_filter:visible" ).focus().blur();
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_classes_attended_filter:visible" ).focus().blur();
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_enrollment_period_filter:visible" ).focus().blur();
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_village_filter:visible" ).focus().blur();
				studentFilterSortByDialog.find( ".student_filter_sortby_dialog_fees_due_filter:visible" ).focus().blur();
			}
	);
}