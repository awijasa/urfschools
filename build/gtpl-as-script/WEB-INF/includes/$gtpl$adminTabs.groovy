package web_inf.includes;out.print("""""");
	if( user == null ) redirect "/index.gtpl"
	else {
		html.div( id: "admin_tabs" ) {
		  ul {
		    li {
		    	a( href: "/genderTab.gtpl" ) {
		    		span( "Gender" )
				}
				span( class: "tab_refresh ui-icon ui-icon-arrowrefresh-1-e", "Refresh Tab" )
			}
		    li {
		    	a( href: "/leaveReasonCategoryTab.gtpl" ) {
		    		span( "Leave Reason Category" )
		    	}
		    	span( class: "tab_refresh ui-icon ui-icon-arrowrefresh-1-e", "Refresh Tab" )
	    	}
		    li {
		    	a( href: "/parentalRelationshipTab.gtpl" ) {
		    		span( "Parental Relationship" )
		    	}
		    	span( class: "tab_refresh ui-icon ui-icon-arrowrefresh-1-e", "Refresh Tab" )
	    	}
		    li {
		    	a( href: "/schoolTab.gtpl" ) {
		    		span( "School" )
		    	}
		    	span( class: "tab_refresh ui-icon ui-icon-arrowrefresh-1-e", "Refresh Tab" )
	    	}
		    li {
		    	a( href: "/termTab.gtpl" ) {
		    		span( "Term" )
		    	}
		    	span( class: "tab_refresh ui-icon ui-icon-arrowrefresh-1-e", "Refresh Tab" )
		    }
		    li {
		    	a( href: "/userTab.gtpl" ) {
		    		span( "User" )
		    	}
		    	span( class: "tab_refresh ui-icon ui-icon-arrowrefresh-1-e", "Refresh Tab" )
		    }
		  }
		}
	}
;
out.print("""

<script type=\"text/javascript\">
  var adminTabs = jQuery( \"#admin_tabs\" ).tabs(
    {
            
      /* Cache the tabs' contents. */
      beforeLoad: function( event, ui ) {
          if ( ui.tab.data( \"loaded\" ) ) {
          		jQuery( \".progress_bar\" ).fadeOut();
              event.preventDefault();
              return;
          }
          else {
          	jQuery( \".progress_bar\" ).fadeIn();
          }

          ui.jqXHR.success(function() {
              ui.tab.data( \"loaded\", true );
          });
      }
      
      , load: function( event, ui ) {
      	jQuery( \".progress_bar\" ).fadeOut();
      }
    }
  ).tabs( \"option\", \"active\", 0 );

  /* Set the font size of the Admin Tabs' titles. */
  adminTabs.find( \"ul li a\" ).css( \"font-size\", \"0.8em\" );
  
  adminTabs.find( \".tab_refresh\" ).click(
    function() {
      if( !adminTabs.tabs( \"option\", \"disabled\" ) ) {
        var refreshTabButton = jQuery( this );
        var adminTabIndex = refreshTabButton.parent().index();

        adminTabs.tabs( \"option\", \"active\", adminTabIndex );

        var adminTabPanel = adminTabs.find( \".ui-tabs-panel\" ).eq( adminTabIndex );
        var adminTabAnchors = adminTabs.find( \"ul li a\" );
        var adminTabAnchorHrefs = new Array();
        var tabs = jQuery( \"#tabs\" );
        var tabAnchors = tabs.find( \"ul li a\" );
        var tabAnchorHrefs = new Array();

        adminTabs.tabs( \"disable\" );
        tabs.tabs( \"disable\" );

        adminTabAnchors.each(
          function( index, element ) {
            adminTabAnchorHrefs[index] = jQuery( this ).attr( \"href\" );
          }
        );

        tabAnchors.each(
          function( index, element ) {
            tabAnchorHrefs[index] = jQuery( this ).attr( \"href\" );
          }
        );

        adminTabAnchors.attr( \"href\", \"javascript:void( 0 )\" );
        tabAnchors.attr( \"href\", \"javascript:void( 0 )\" );

        refreshTabButton.removeClass( \"ui-icon-arrowrefresh-1-e\" ).addClass( \"ui-icon-clock\" );
        adminTabPanel.fadeOut( \"slow\" );

        jQuery.ajax(
          {
            url: adminTabAnchorHrefs[adminTabIndex]
            , type: \"GET\"
            , success: function( data ) {
                adminTabPanel.html( data );
                
                setTimeout(
                	function() {
		                adminTabPanel.fadeIn( \"slow\" );
		                refreshTabButton.removeClass( \"ui-icon-clock\" ).addClass( \"ui-icon-arrowrefresh-1-e\" );
		
		                adminTabAnchors.each(
		                  function( index, element ) {
		                    jQuery( this ).attr( \"href\", adminTabAnchorHrefs[index] );
		                  }
		                );
		
		                tabAnchors.each(
		                  function( index, element ) {
		                    jQuery( this ).attr( \"href\", tabAnchorHrefs[index] );
		                  }
		                );
		
		                adminTabs.tabs( \"enable\" );
		                tabs.tabs( \"enable\" );
	                }
	                , 500
                );
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
</script>""");
