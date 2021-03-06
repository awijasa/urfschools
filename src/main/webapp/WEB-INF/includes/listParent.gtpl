<%
  if( user == null ) redirect "/index.gtpl"
  else {
	  html.a( class: "list_parent_first_name_label", href: "javascript:void( 0 )", "First Name" )
	  html.div( class: "list_parent_first_name_label", "First Name" )
	  html.a( class: "list_parent_last_name_label", href: "javascript:void( 0 )", "Last Name" )
	  html.div( class: "list_parent_last_name_label", "Last Name" )
	  html.a( class: "list_parent_deceased_label", href: "javascript:void( 0 )", "Deceased" )
	  html.div( class: "list_parent_deceased_label", "Deceased" )
	  html.a( class: "list_parent_village_label", href: "javascript:void( 0 )", "Village" )
	  html.div( class: "list_parent_village_label", "Village" )
	  html.a( class: "list_parent_primary_phone_label", href: "javascript:void( 0 )", "Primary Phone" )
	  html.div( class: "list_parent_primary_phone_label", "Primary Phone" )
	  html.div( class: "actions_label", "Actions" )
	
	  int activeSeqNo = 0
	  int limit = Integer.parseInt( params.limit )
	  int offset
	  
	  if( params.offset != null )
	    offset = Integer.parseInt( params.offset )
	    
	  if( params.parentId != null && params.isLookup == null ) {
	    offset = 0
	    
	    List<com.google.appengine.api.datastore.Entity> parentList
	    
	    if( params.studentId == null || params.studentId == "" )
	    	parentList = data.Parent.findByLimitAndOffset( limit, offset, session )
	    else {
	    	parentList = new ArrayList()
	    	
	    	data.Relationship.findByStudentIdAndLimitAndOffset( params.studentId, limit, offset ).each(
	    		{
	    			parentList.add( data.Parent.findByParentId( it.parentId ) )
	    		}
	    	)
    	}
	    
	    while( parentList.size() > 0 ) {
	      boolean isOffsetFound = false
	      int sizeOfListDisplayed = parentList.size() == limit + 1? parentList.size() - 1: parentList.size()
	    
	      for( int i = 0; i < sizeOfListDisplayed; i++ ) {
	        com.google.appengine.api.datastore.Entity parent = parentList.get( i )
	
	        if( parent.parentId.equals( params.parentId ) ) {
	          activeSeqNo = i
	          isOffsetFound = true
	          break
	        }
	      }
	
	      if( isOffsetFound )
	        break
	
	      offset += limit
	
	      if( params.studentId == null || params.studentId == "" )
	      	parentList = data.Parent.findByLimitAndOffset( limit, offset, session )
	      else {
	      	parentList = new ArrayList()
	      	
	      	data.Relationship.findByStudentIdAndLimitAndOffset( params.studentId, limit, offset ).each(
	    		{
	    			parentList.add( data.Parent.findByParentId( it.parentId ) )
	    		}
	    	)
	      }
	    }
	  }
	
	  if( offset > 0 ) {
	  	html.a( class: "list_parent_prev_twenty", href: "javascript:void( 0 )", "Prev ${ limit } Parents" )
	  	html.img( class: "list_parent_prev_twenty_wait", src: "/images/ajax-loader.gif" )
		}
		
		html.div( class: "list_parent_prev_twenty_limit", style: "display: none;", limit )
		html.div( class: "list_parent_prev_twenty_offset", style: "display: none;", offset - limit )
		
		List<com.google.appengine.api.datastore.Entity> parentList
		
		html.div( class: "parent_list" ) {
			if( params.isLookup == "Y" ) {
				parentList = new ArrayList()
				mkp.yieldUnescaped( formatter.ListItemFormatter.getParentListItem( data.Parent.findByParentId( params.parentId ), params.studentId ) )
			}
			else if( params.studentId != null && params.studentId != "" ) {
				parentList = new ArrayList()
				
				data.Relationship.findByStudentIdAndLimitAndOffset( params.studentId, limit, offset ).eachWithIndex(
					{ obj, i ->
						com.google.appengine.api.datastore.Entity parent = data.Parent.findByParentId( obj.parentId )
						parentList.add( parent )
						
						if( i < limit )
							mkp.yieldUnescaped( formatter.ListItemFormatter.getParentListItem( parent, params.studentId ) )
					}
				)
			}
			else {
				parentList = data.Parent.findByLimitAndOffset( limit, offset, session )
				parentList.eachWithIndex(
					{ obj, i ->
						if( i < limit )
							mkp.yieldUnescaped( formatter.ListItemFormatter.getParentListItem( obj, null ) )
					}
				)
			}
		}
		
		if( parentList.size() > limit )
			html.a( class: "list_parent_next_twenty", href: "javascript:void( 0 )", "Next ${ limit } Parents" )
		else
			html.a( class: "list_parent_next_twenty", href: "javascript:void( 0 )", style: "display: none;", "Next ${ limit } Parents" )
			
		html.img( class: "list_parent_next_twenty_wait", src: "/images/ajax-loader.gif" )
		
		html.div( class: "list_funding_source_school ui-corner-all ui-widget-content" ) {
			a( class: "list_funding_source_school_link", href: "javascript:void( 0 )", "School" )
		}
		
		html.div( class: "list_funding_source_self ui-corner-all ui-widget-content" ) {
			a( class: "list_funding_source_self_link", href: "javascript:void( 0 )", "Self" )
		}
		
		html.div( class: "list_parent_next_twenty_limit", style: "display: none;", limit )
		html.div( class: "list_parent_next_twenty_offset", style: "display: none;", offset + limit )
		
		html.div( class: "list_parent_active_seq_no", style: "display: none;", activeSeqNo )
		html.div( class: "list_parent_student_id", style: "display: none;", params.studentId?: "" )
		html.div( class: "list_parent_window_scroll_left", style: "display: none", params.windowScrollLeft )
  		html.div( class: "list_parent_window_scroll_top", style: "display: none", params.windowScrollTop )
	}
%>
<script type="text/javascript">
  initParentList();
  
  jQuery.ajax(
  	{
  		url: "SessionController.groovy"
  		, type: "GET"
  		, data: jQuery.param(
  				{
  					action: "getAttributes"
  				}
  			)
  		, success: function( data ) {
  				var parentDeceasedIndFilter = jQuery( data ).find( "parentDeceasedIndFilter" ).text();
  				var parentEmailFilter = jQuery( data ).find( "parentEmailFilter" ).text();
  				var parentFirstNameFilter = jQuery( data ).find( "parentFirstNameFilter" ).text();
  				var parentIdFilter = jQuery( data ).find( "parentIdFilter" ).text();
  				var parentLastNameFilter = jQuery( data ).find( "parentLastNameFilter" ).text();
  				var parentPrimaryPhoneFilter = jQuery( data ).find( "parentPrimaryPhoneFilter" ).text();
  				var parentProfessionFilter = jQuery( data ).find( "parentProfessionFilter" ).text();
  				var parentSecondaryPhoneFilter = jQuery( data ).find( "parentSecondaryPhoneFilter" ).text();
  				var parentVillageFilter = jQuery( data ).find( "parentVillageFilter" ).text();
  				
  				jQuery( ".filters .parent_deceased_ind_filter" ).text( parentDeceasedIndFilter );
  				jQuery( ".filters .parent_email_filter" ).text( parentEmailFilter );
  				jQuery( ".filters .parent_first_name_filter" ).text( parentFirstNameFilter );
  				jQuery( ".filters .parent_id_filter" ).text( parentIdFilter );
  				jQuery( ".filters .parent_last_name_filter" ).text( parentLastNameFilter );
  				jQuery( ".filters .parent_primary_phone_filter" ).text( parentPrimaryPhoneFilter );
  				jQuery( ".filters .parent_profession_filter" ).text( parentProfessionFilter );
  				jQuery( ".filters .parent_secondary_phone_filter" ).text( parentSecondaryPhoneFilter );
  				jQuery( ".filters .parent_village_filter" ).text( parentVillageFilter );
  				jQuery( ".parent_filter_sortby_dialog_deceased_ind_filter_operator option" ).each(
  	  					function() {
  	  						if( jQuery( this ).text() == parentDeceasedIndFilter ) {
  	  							jQuery( this ).prop( "selected", true );
  							}
  							else {
  								jQuery( this ).prop( "selected", false )
  							}
  						}
  	  				);
  				jQuery( ".parent_filter_sortby_dialog_email_filter" ).val( parentEmailFilter );
  				jQuery( ".parent_filter_sortby_dialog_first_name_filter" ).val( parentFirstNameFilter );
  				jQuery( ".parent_filter_sortby_dialog_last_name_filter" ).val( parentLastNameFilter );
  				jQuery( ".parent_filter_sortby_dialog_parent_id_filter" ).val( parentIdFilter );
  				jQuery( ".parent_filter_sortby_dialog_primary_phone_filter" ).val( parentPrimaryPhoneFilter );
  				jQuery( ".parent_filter_sortby_dialog_profession_filter" ).val( parentProfessionFilter );
  				jQuery( ".parent_filter_sortby_dialog_secondary_phone_filter" ).val( parentSecondaryPhoneFilter );
  				jQuery( ".parent_filter_sortby_dialog_village_filter" ).val( parentVillageFilter );
  			}
  		, error: function( jqXHR, textStatus, errorThrown ) {
	  			if( textStatus == null || jqXHR.getResponseHeader( "Response-Phrase" ) == null ) {
					alert( "We're sorry. The servers did not respond on time. Please try again" );
				}
				else {
					alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
				}
  			}
  	}
  );
  
  var parentTabListRecordForm = jQuery( "#parent_tab .list_record_form" );
  
  jQuery( ".list_parent_prev_twenty" ).off( "click" ).click(
    function() {
      var listParentPrevTwenty = jQuery( this );
      var listParent = listParentPrevTwenty.parent();
      var listParentPrevTwentyLimit = listParent.find( ".list_parent_prev_twenty_limit" );
      var listParentPrevTwentyOffset = listParent.find( ".list_parent_prev_twenty_offset" );
      var listParentPrevTwentyWait = listParent.find( ".list_parent_prev_twenty_wait" );
      
      jQuery.ajax(
        {
          url: "/listParent.gtpl"
          , type: "GET"
          , data: jQuery.param(
              {
                limit: listParentPrevTwentyLimit.text()
                , offset: listParentPrevTwentyOffset.text()
                , studentId: listParent.find( ".list_parent_student_id" ).text()
                , windowScrollLeft: listParent.find( ".list_parent_window_scroll_left" ).text()
                , windowScrollTop: listParent.find( ".list_parent_window_scroll_top" ).text()
              }
            )
          , beforeSend: function() {
              listParentPrevTwenty.toggle();
              listParentPrevTwentyWait.toggle();
            }
          , success: function( data ) {
              listParent.toggle();
              listParent.children().remove();
              listParent.append( data );
              listParent.toggle();
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
        	  if( textStatus == null || jqXHR.getResponseHeader( "Response-Phrase" ) == null ) {
					alert( "We're sorry. The servers did not respond on time. Please try again" );
				}
				else {
					alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
				}
            }
        }
      );
    }
  );
  
  jQuery( ".list_parent_next_twenty" ).off( "click" ).click(
    function() {
      var listParentNextTwenty = jQuery( this );
      var listParent = listParentNextTwenty.parent();
      var listParentNextTwentyLimit = listParent.find( ".list_parent_next_twenty_limit" );
      var listParentNextTwentyOffset = listParent.find( ".list_parent_next_twenty_offset" );
      var listParentNextTwentyWait = listParent.find( ".list_parent_next_twenty_wait" );
    
      jQuery.ajax(
        {
          url: "/listParent.gtpl"
          , type: "GET"
          , data: jQuery.param(
              {
                limit: listParentNextTwentyLimit.text()
                , offset: listParentNextTwentyOffset.text()
                , studentId: listParent.find( ".list_parent_student_id" ).text()
                , windowScrollLeft: listParent.find( ".list_parent_window_scroll_left" ).text()
                , windowScrollTop: listParent.find( ".list_parent_window_scroll_top" ).text()
              }
            )
          , beforeSend: function() {
              listParentNextTwenty.toggle();
              listParentNextTwentyWait.toggle();
            }
          , success: function( data ) {
              listParent.toggle();
              listParent.children().remove();
              listParent.append( data );
              listParent.toggle();
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
        	  if( textStatus == null || jqXHR.getResponseHeader( "Response-Phrase" ) == null ) {
					alert( "We're sorry. The servers did not respond on time. Please try again" );
				}
				else {
					alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
				}
            }
        }
      );
    }
  );
  
  var parentList = jQuery( ".parent_list" );
  
  parentList.each(
        function() {
          if( jQuery( this ).parent().hasClass( "list_record_form" ) ) {
            if( navigator.userAgent.match( /(android)|(iPad)|(iPhone)|(iPod)|(webOS)/i ) ) {
              jQuery( this ).accordion(
                {
                	activate: function( event, ui) {
					  autoResizeFieldBasedOnWidth( ui.newPanel, ".list_parent_email", 210 );
					}
                  , collapsible: false
                }
              ).show();
              
              autoResizeFieldBasedOnWidth( jQuery( this ).find( ".list_parent_details" ).eq( 0 ), ".list_parent_email", 210 );

              jQuery( this ).css( "overflow", "visible" );
              jQuery( ".list_record_form .parent_list" ).css( "height", "1450px" );
              jQuery( this ).parent().css( "height", "1450px" );
              jQuery( ".list_record_form .list_parent_next_twenty" ).css( "top", "1425px" )
              jQuery( ".list_record_form .list_parent_next_twenty_wait" ).css( "top", "1425px" )
            }
            else {
              jQuery( this ).accordion(
                {
                	activate: function( event, ui) {
					  autoResizeFieldBasedOnWidth( ui.newPanel, ".list_parent_email", 210 );
					}
                  , active: false
                  , collapsible: true
                }
              ).show();

              jQuery( this ).scroll(
                function() {

                  /* If Scroll Bar position + computer screen's height >= web page's header + body heights OR Scroll Bar position = the top of Parent List section */
                  if( jQuery( window ).scrollTop() + jQuery( window ).height() >= parentTabListRecordForm.offset().top + parentTabListRecordForm.height() ||
                    jQuery( window ).scrollTop == parentTabListRecordForm.offset().top ) {
                    parentList.css( "overflow", "auto" );
                  }
                  else {
                    parentList.css( "overflow", "hidden" );
                  }
                  
                  var activeListParentDetailsSeqNo = parentList.accordion( "option", "active" );
                  var listParentDetails = parentList.find( ".list_parent_details" );
                  
                  var parentEmailFilterSortByDialog = jQuery( ".parent_email_filter_sortby_dialog" );
					
					if( parentEmailFilterSortByDialog.dialog( "isOpen" ) ) {
						
						if( parentEmailFilterSortByDialog.offset().top <= parentList.offset().top ) {
							parentEmailFilterSortByDialog.dialog( "close" );
						}
						else {
							parentEmailFilterSortByDialog.dialog( "option", "position",
								{
									my: "left-25 top+15"
									, of: listParentDetails.eq( activeListParentDetailsSeqNo ).find( ".list_parent_email_label" )
									, collision: "fit"
								}
							);
						}
					}
					
					var parentIdFilterSortByDialog = jQuery( ".parent_id_filter_sortby_dialog" );
					
					if( parentIdFilterSortByDialog.dialog( "isOpen" ) ) {
						
						if( parentIdFilterSortByDialog.offset().top <= parentList.offset().top ) {
							parentIdFilterSortByDialog.dialog( "close" );
						}
						else {
							parentIdFilterSortByDialog.dialog( "option", "position",
								{
									my: "left-15 top+15"
									, of: listParentDetails.eq( activeListParentDetailsSeqNo ).find( ".list_parent_id_label" )
									, collision: "fit"
								}
							);
						}
					}
					
					var parentProfessionFilterSortByDialog = jQuery( ".parent_profession_filter_sortby_dialog" );
					
					if( parentProfessionFilterSortByDialog.dialog( "isOpen" ) ) {
						
						if( parentProfessionFilterSortByDialog.offset().top <= parentList.offset().top ) {
							parentProfessionFilterSortByDialog.dialog( "close" );
						}
						else {
							parentProfessionFilterSortByDialog.dialog( "option", "position",
								{
									my: "left-45 top+15"
									, of: listParentDetails.eq( activeListParentDetailsSeqNo ).find( ".list_parent_profession_label" )
									, collision: "fit"
								}
							);
						}
					}
					
					var parentSecondaryPhoneFilterSortByDialog = jQuery( ".parent_secondary_phone_filter_sortby_dialog" );
					
					if( parentSecondaryPhoneFilterSortByDialog.dialog( "isOpen" ) ) {
						
						if( parentSecondaryPhoneFilterSortByDialog.offset().top <= parentList.offset().top ) {
							parentSecondaryPhoneFilterSortByDialog.dialog( "close" );
						}
						else {
							parentSecondaryPhoneFilterSortByDialog.dialog( "option", "position",
								{
									my: "left-83 top+15"
									, of: listParentDetails.eq( activeListParentDetailsSeqNo ).find( ".list_parent_secondary_phone_label" )
									, collision: "fit"
								}
							);
						}
					}
                }
              );

              jQuery( window ).scroll(
                function() {

                  /* If Scroll Bar position + computer screen's height >= web page's header + body heights OR Scroll Bar position = the top of Parent List section */
                  if( jQuery( window ).scrollTop() + jQuery( window ).height() >= parentTabListRecordForm.offset().top + parentTabListRecordForm.height() ||
                    jQuery( window ).scrollTop == parentTabListRecordForm.offset().top ) {
                    parentList.css( "overflow", "auto" );
                  }
                  else {
                    parentList.css( "overflow", "hidden" );
                  }
                }
              );
            }
          }
          else {
          	var activeSeqNo = parseInt( jQuery( this ).parent().find( ".list_parent_active_seq_no" ).text() );
            
            jQuery( this ).accordion(
              {
              	activate: function( event, ui) {
				  autoResizeFieldBasedOnWidth( ui.newPanel, ".list_parent_email", 210 );
				}
                , active: activeSeqNo
                , collapsible: false
              }
            ).show();
            
            autoResizeFieldBasedOnWidth( jQuery( this ).find( ".list_parent_details" ).eq( activeSeqNo ), ".list_parent_email", 210 );
          }
        }
  );
</script>