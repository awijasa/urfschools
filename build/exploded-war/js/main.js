function initMain() {
        
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
		                	  var listParentChildListStudent = jQuery( ".list_parent_child_list_student" );
		                	  
		                	  if( listParentChildListStudent.find( ".list_parent_child_list_student_first_request" ).length == 0 ) {
		                		  listParentChildListStudent.prepend( '<div class="list_parent_child_list_student_first_request" style="display: none">Yes</div>' );
		                	  }
		                	  
		                	  var listParentRelativeListStudent = jQuery( ".list_parent_relative_list_student" );
		                	  
		                	  if( listParentRelativeListStudent.find( ".list_parent_relative_list_student_first_request" ).length == 0 ) {
		                		  listParentRelativeListStudent.prepend( '<div class="list_parent_relative_list_student_first_request" style="display: none">Yes</div>' );
		                	  }
		                  }
		                  else if( tabPanel.find( "#enrollment_tab" ).length > 0 ) {
		                	  var addEnrollmentListStudent = jQuery( ".add_enrollment_list_student" );
		                	  
		                	  if( addEnrollmentListStudent.find( ".add_enrollment_list_student_first_request" ).length == 0 ) {
		                		  addEnrollmentListStudent.prepend( '<div class="add_enrollment_list_student_first_request" style="display: none">Yes</div>' );
		                	  }
		                  }
		                  else if( tabPanel.find( "#parent_tab" ).length > 0 ) {
		                	  var addParentChildListStudent = jQuery( ".add_parent_child_list_student" );
		                	  
		                	  if( addParentChildListStudent.find( ".add_parent_child_list_student_first_request" ).length == 0 ) {
		                		  addParentChildListStudent.prepend( '<div class="add_parent_child_list_student_first_request" style="display: none">Yes</div>' );
		                	  }
		                	  
		                	  var addParentRelativeListStudent = jQuery( ".add_parent_relative_list_student" );
		                	  
		                	  if( addParentRelativeListStudent.find( ".add_parent_relative_list_student_first_request" ).length == 0 ) {
		                		  addParentRelativeListStudent.prepend( '<div class="add_parent_relative_list_student_first_request" style="display: none">Yes</div>' );
		                	  }
		                	  
		                	  var listParentChildListStudent = jQuery( ".list_parent_child_list_student" );
		                	  
		                	  if( listParentChildListStudent.find( ".list_parent_child_list_student_first_request" ).length == 0 ) {
		                		  listParentChildListStudent.prepend( '<div class="list_parent_child_list_student_first_request" style="display: none">Yes</div>' );
		                	  }
		                	  
		                	  var listParentRelativeListStudent = jQuery( ".list_parent_relative_list_student" );
		                	  
		                	  if( listParentRelativeListStudent.find( ".list_parent_relative_list_student_first_request" ).length == 0 ) {
		                		  listParentRelativeListStudent.prepend( '<div class="list_parent_relative_list_student_first_request" style="display: none">Yes</div>' );
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
          headerFilterSortByDialog.dialog( "option", "position", [filterSortByDialogLeftPosition - jQuery( window ).scrollLeft(), 80 - jQuery( window ).scrollTop()] );
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
	      , height: 1500
	      , hide: "fade"
	      , modal: true
	      , position: "center"
	      , show: "fade"
	      , width: 1012
	    }
    );
    
    jQuery( ".add_parent_child_list_student" ).dialog(
		{
	        autoOpen: false
	        , height: 1500
	        , hide: "fade"
	        , modal: true
	        , position: "center"
	        , show: "fade"
	        , width: 1012
		}
    );
    
    jQuery( ".add_parent_relative_list_student" ).dialog(
	    {
	      autoOpen: false
	      , height: 1500
	      , hide: "fade"
	      , modal: true
	      , position: "center"
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
			, position: "center"
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
			, position: "center"
			, show: "fade"
			, width: 1012
		}
	);
    
    jQuery( ".dialog_student_lookup" ).dialog(
	    {
	      autoOpen: false
	      , height: 700
	      , hide: "fade"
	      , modal: true
	      , position: "center"
	      , show: "fade"
	      , width: 1012
	    }
    );
    
    jQuery( ".list_parent_child_list_student" ).dialog(
		{
			autoOpen: false
			, height: 1500
			, hide: "fade"
				, modal: true
				, position: "center"
					, show: "fade"
						, width: 1012
		}
	);
    
    jQuery( ".list_parent_relative_list_student" ).dialog(
		{
			autoOpen: false
			, height: 1500
			, hide: "fade"
				, modal: true
				, position: "center"
					, show: "fade"
						, width: 1012
		}
	);
}