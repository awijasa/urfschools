<%
  if( user == null ) redirect "/index.gtpl"
  else {
	html.a( class: "list_student_first_name_label", href: "javascript:void( 0 )", "First Name" )
	html.a( class: "list_student_last_name_label", href: "javascript:void( 0 )", "Last Name" )
	html.a( class: "list_student_sponsored_label", href: "javascript:void( 0 )", "Sponsored" )
	html.a( class: "list_student_classes_attended_label", href: "javascript:void( 0 )", "Classes Attended" )
	html.a( class: "list_student_enrollment_period_label", href: "javascript:void( 0 )", "Enrollment Period" )
	html.div( class: "actions_label", "Actions" )

	  int activeSeqNo = 0
	  int limit = Integer.parseInt( params.limit )
	  int offset
	  
	  if( params.offset != null )
	    offset = Integer.parseInt( params.offset )
	    
	  if( params.studentId != null && params.isLookup == null ) {
	    offset = 0
	    
	    com.google.appengine.api.search.Results<com.google.appengine.api.search.ScoredDocument> studentResults = data.StudentDocument.findByLimitAndOffset( limit, offset, session )
	    
	    while( studentResults.getNumberReturned() > 0 ) {
	      boolean isOffsetFound = false
	      int sizeOfListDisplayed = studentResults.getNumberReturned() == limit + 1? studentResults.getNumberReturned() - 1: studentResults.getNumberReturned()
	    
	      Collection studentCollection = studentResults.getResults()
	      
	      for( int i = 0; i < sizeOfListDisplayed; i++ ) {
	        com.google.appengine.api.search.ScoredDocument student = studentCollection.getAt( i )
	
	        if( student.getOnlyField( "studentId" ).getAtom() == params.studentId ) {
	          activeSeqNo = i
	          isOffsetFound = true
	          break
	        }
	      }
	
	      if( isOffsetFound )
	        break
	
	      offset += limit
	
	      studentResults = data.StudentDocument.findByLimitAndOffset( limit, offset, session )
	      
	      if( studentResults.getNumberReturned() == 0 ) {
	      	activeSeqNo = 0
	      	offset = 0
	      }
	    }
	  }
	
	  if( offset > 0 ) {
	    html.a( class: "list_student_prev_twenty", href: "javascript:void( 0 )", "Prev ${ limit } Students" )
	    html.img( class: "list_student_prev_twenty_wait", src: "/images/ajax-loader.gif" )
	  }
	    
	    int prevTwentyOffset = offset - limit
	    
	    if( prevTwentyOffset < 0 )
	    	prevTwentyOffset = 0
	    
	    html.div( class: "list_student_prev_twenty_limit", style: "display: none;", limit )
	    html.div( class: "list_student_prev_twenty_offset", style: "display: none;", prevTwentyOffset )
	      
		List<com.google.appengine.api.datastore.Entity> studentList
		com.google.appengine.api.search.Results<com.google.appengine.api.search.ScoredDocument> studentResults
		
		html.div( class: "student_list" ) {
	  		if( params.isLookup == "Y" ) {
		  		mkp.yieldUnescaped( formatter.ListItemFormatter.getStudentListItem( data.StudentDocument.findByStudentId( params.studentId ), null ) )
			}
			else if( params.isMatchingList != "Y" && ( params.firstName == null || params.firstName == "" || params.firstName == "First Name" ) ) {
				studentResults = data.StudentDocument.findByLimitAndOffset( limit, offset, session )
				studentResults.eachWithIndex() { obj, i ->
				  if( i < limit )
					mkp.yieldUnescaped( formatter.ListItemFormatter.getStudentListItem( obj, null ) )
				}
			}
			else {
				Date birthDate
				
				if( params.birthDate != null && params.birthDate != "" && params.birthDate != "Birth Date" )
					birthDate = Date.parse( "MMM d yy", params.birthDate )
				
				studentList = data.StudentDocument.findByLimitAndOffsetAndFirstNameAndLastNameAndBirthDateAndVillage( limit, offset, session, params.firstName, params.lastName, birthDate, params.village )
				studentList.eachWithIndex() { obj, i ->
				  if( i < limit )
					mkp.yieldUnescaped( formatter.ListItemFormatter.getStudentListItem( obj, null ) )
				}
			}	
		}
	
		if( ( studentResults?.getNumberReturned()?: 0 ) > limit )
	  		html.a( class: "list_student_next_twenty", href: "javascript:void( 0 )", "Next ${ limit } Students" )
	  	else if( ( studentList?.size()?: 0 ) > limit )
	  		html.a( class: "list_student_next_twenty", href: "javascript:void( 0 )", "Next ${ limit } Students" )
		else
	  		html.a( class: "list_student_next_twenty", href: "javascript:void( 0 )", style: "display: none;", "Next ${ limit } Students" )
	
	  html.img( class: "list_student_next_twenty_wait", src: "/images/ajax-loader.gif" )
	  
	  html.div( class: "list_student_create ui-corner-all ui-widget-content" ) {
			a( class: "list_student_create_link", href: "javascript:void( 0 )", "Create a New Student" )
		}
	  
	  html.div( class: "list_student_next_twenty_limit", style: "display: none;", limit )
	  html.div( class: "list_student_next_twenty_offset", style: "display: none;", offset + limit )
	  
	  html.div( class: "list_student_active_seq_no", style: "display: none;", activeSeqNo )
	  html.div( class: "list_student_birth_date_param", style: "display: none", params.birthDate )
	  html.div( class: "list_student_first_name_param", style: "display: none", params.firstName )
	  html.div( class: "list_student_last_name_param", style: "display: none", params.lastName )
	  html.div( class: "list_student_village_param", style: "display: none", params.village )
	  html.div( class: "list_student_window_scroll_left", style: "display: none", params.windowScrollLeft )
	  html.div( class: "list_student_window_scroll_top", style: "display: none", params.windowScrollTop )
  }
%>
<script type="text/javascript">
  initStudentList();
  
  var addStudentMatchingListStudent = jQuery( ".add_student_matching_list_student" );
  var addStudentSchool = jQuery( ".add_student_school" );
  var listStudentFirstNameParam = addStudentMatchingListStudent.find( ".list_student_first_name_param" );
  
  if(
  	listStudentFirstNameParam.text() != null && listStudentFirstNameParam.text() != "" && listStudentFirstNameParam.text() != "First Name" &&
  	addStudentSchool.val() != null && addStudentSchool.val() != "" && addStudentSchool.val() != "School Name"
  ) {
  	addStudentMatchingListStudent.find( ".list_student_details" ).each(
  		function() {
  			if( addStudentSchool.val() == jQuery( this ).find( ".list_student_school" ).text() ) {
  				jQuery( this ).find( ".list_student_edit" ).show();
  				jQuery( this ).find( ".list_student_delete" ).show();
  				jQuery( this ).find( ".list_student_select" ).hide();
  			}
  		}
  	);
  }
  
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
  				var studentFeesDueFilter = jQuery( data ).find( "studentFeesDueFilter" ).text();
  				var studentFeesDueFilterOperator = jQuery( data ).find( "studentFeesDueFilterOperator" ).text();
  				var studentFirstNameFilter = jQuery( data ).find( "studentFirstNameFilter" ).text();
  				var studentLastNameFilter = jQuery( data ).find( "studentLastNameFilter" ).text();
  				
  				jQuery( ".filters .student_fees_due_filter" ).text( studentFeesDueFilter );
  				jQuery( ".filters .student_fees_due_filter_operator" ).text( studentFeesDueFilterOperator );
  				jQuery( ".filters .student_first_name_filter" ).text( studentFirstNameFilter );
  				jQuery( ".filters .student_last_name_filter" ).text( studentLastNameFilter );
  				jQuery( ".student_filter_sortby_dialog_fees_due_filter" ).val( studentFeesDueFilter );
  				jQuery( ".student_filter_sortby_dialog_fees_due_filter_operator option" ).each(
  					function() {
  						if( jQuery( this ).text() == studentFeesDueFilterOperator ) {
  							jQuery( this ).prop( "selected", true );
						}
						else {
							jQuery( this ).prop( "selected", false )
						}
					}
  				);
  				jQuery( ".student_filter_sortby_dialog_first_name_filter" ).val( studentFirstNameFilter );
  				jQuery( ".student_filter_sortby_dialog_last_name_filter" ).val( studentLastNameFilter );
  			}
  		, error: function( jqXHR, textStatus, errorThrown ) {
  				alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
  			}
  	}
  );
  
  var studentTabListRecordForm = jQuery( "#student_tab .list_record_form" );
  
  jQuery( ".list_student_prev_twenty" ).off( "click" ).click(
    function() {
      var listStudentPrevTwenty = jQuery( this );
      var listStudent = listStudentPrevTwenty.parent();
      var listStudentPrevTwentyLimit = listStudent.find( ".list_student_prev_twenty_limit" );
      var listStudentPrevTwentyOffset = listStudent.find( ".list_student_prev_twenty_offset" );
      var listStudentPrevTwentyWait = listStudent.find( ".list_student_prev_twenty_wait" );
      
      jQuery.ajax(
        {
          url: "/listStudent.gtpl"
          , type: "GET"
          , data: jQuery.param(
              {
                birthDate: listStudent.find( ".list_student_birth_date_param" ).text()
                , firstName: listStudent.find( ".list_student_first_name_param" ).text()
                , lastName: listStudent.find( ".list_student_last_name_param" ).text()
                , limit: listStudentPrevTwentyLimit.text()
                , offset: listStudentPrevTwentyOffset.text()
                , village: listStudent.find( ".list_student_village_param" ).text()
                , windowScrollLeft: listStudent.find( ".list_student_window_scroll_left" ).text()
                , windowScrollTop: listStudent.find( ".list_student_window_scroll_top" ).text()
              }
            )
          , beforeSend: function() {
              listStudentPrevTwenty.toggle();
              listStudentPrevTwentyWait.toggle();
            }
          , success: function( data ) {
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
  );
  
  jQuery( ".list_student_next_twenty" ).off( "click" ).click(
    function() {
      var listStudentNextTwenty = jQuery( this );
      var listStudent = listStudentNextTwenty.parent();
      var listStudentNextTwentyLimit = listStudent.find( ".list_student_next_twenty_limit" );
      var listStudentNextTwentyOffset = listStudent.find( ".list_student_next_twenty_offset" );
      var listStudentNextTwentyWait = listStudent.find( ".list_student_next_twenty_wait" );
    
      jQuery.ajax(
        {
          url: "/listStudent.gtpl"
          , type: "GET"
          , data: jQuery.param(
              {
                birthDate: listStudent.find( ".list_student_birth_date_param" ).text()
                , firstName: listStudent.find( ".list_student_first_name_param" ).text()
                , lastName: listStudent.find( ".list_student_last_name_param" ).text()
                , limit: listStudentNextTwentyLimit.text()
                , offset: listStudentNextTwentyOffset.text()
                , village: listStudent.find( ".list_student_village_param" ).text()
                , windowScrollLeft: listStudent.find( ".list_student_window_scroll_left" ).text()
                , windowScrollTop: listStudent.find( ".list_student_window_scroll_top" ).text()
              }
            )
          , beforeSend: function() {
              listStudentNextTwenty.toggle();
              listStudentNextTwentyWait.toggle();
            }
          , success: function( data ) {
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
  );
  
  var studentLists = jQuery( ".student_list" );
  
  studentLists.each(
        function() {
          if( jQuery( this ).parent().hasClass( "list_record_form" ) ) {
            if( navigator.userAgent.match( /(android)|(iPad)|(iPhone)|(iPod)|(webOS)/i ) ) {
              var studentList = jQuery( this );
              
              jQuery( this ).accordion(
                {
                  collapsible: false
                  , activate: function( event, ui ) {
                  		studentList.find( ".list_student_enrollment_period_lookup" ).fadeOut();
                  		ui.newHeader.find( ".list_student_enrollment_period_lookup" ).fadeIn();
                  	}
                }
              ).show();

              jQuery( this ).css( "overflow", "visible" );
              jQuery( ".list_record_form .student_list" ).css( "height", "1555px" );
              jQuery( this ).parent().css( "height", "1555px" );
              jQuery( ".list_record_form .list_student_next_twenty" ).css( "top", "1530px" )
              jQuery( ".list_record_form .list_student_next_twenty_wait" ).css( "top", "1530px" )
              jQuery( this ).find( ".list_student_enrollment_period_lookup" ).eq( 0 ).fadeIn();
            }
            else {
              var studentList = jQuery( this );
              
              jQuery( this ).accordion(
                {
                  active: false
                  , collapsible: true
                  , activate: function( event, ui ) {
                  		studentList.find( ".list_student_enrollment_period_lookup" ).fadeOut();
                  		ui.newHeader.find( ".list_student_enrollment_period_lookup" ).fadeIn();
                  	}
                }
              ).show();

              jQuery( this ).scroll(
                function() {

                  /* If Scroll Bar position + computer screen's height >= web page's header + body heights OR Scroll Bar position = the top of Student List section */
                  if( jQuery( window ).scrollTop() + jQuery( window ).height() >= studentTabListRecordForm.offset().top + studentTabListRecordForm.height() ||
                    jQuery( window ).scrollTop == studentTabListRecordForm.offset().top ) {
                    studentList.css( "overflow", "auto" );
                  }
                  else {
                    studentList.css( "overflow", "hidden" );
                  }
                  
                  var activeListStudentDetailsSeqNo = studentList.accordion( "option", "active" );
                  var listStudentDetails = studentList.find( ".list_student_details" );
                  
                  var studentBirthDateFilterSortByDialog = jQuery( ".student_birth_date_filter_sortby_dialog" );
                  
                  if( studentBirthDateFilterSortByDialog.dialog( "isOpen" ) ) {
                  	
                  	if( studentBirthDateFilterSortByDialog.offset().top <= studentList.offset().top ) {
                  		studentBirthDateFilterSortByDialog.dialog( "close" );
                  	}
                  	else {
	                  	studentBirthDateFilterSortByDialog.dialog( "option", "position",
							{
								my: "left-45 top+15"
								, of: listStudentDetails.eq( activeListStudentDetailsSeqNo ).find( ".list_student_birth_date_label" )
								, collision: "fit"
							}
						);
					}
					}
					
					var studentBoardingFeesFilterSortByDialog = jQuery( ".student_boarding_fees_filter_sortby_dialog" );
					
					if( studentBoardingFeesFilterSortByDialog.dialog( "isOpen" ) ) {
						
						if( studentBoardingFeesFilterSortByDialog.offset().top <= studentList.offset().top ) {
							studentBoardingFeesFilterSortByDialog.dialog( "close" );
						}
						else {
							studentBoardingFeesFilterSortByDialog.dialog( "option", "position",
								{
									my: "left-62 top+15"
									, of: listStudentDetails.eq( activeListStudentDetailsSeqNo ).find( ".list_student_boarding_fees_label" )
									, collision: "fit"
								}
							);
						}
					}
					
					var studentFeesDueFilterSortByDialog = jQuery( ".student_fees_due_filter_sortby_dialog" );
					
					if( studentFeesDueFilterSortByDialog.dialog( "isOpen" ) ) {
						
						if( studentFeesDueFilterSortByDialog.offset().top <= studentList.offset().top ) {
							studentFeesDueFilterSortByDialog.dialog( "close" );
						}
						else {
							studentFeesDueFilterSortByDialog.dialog( "option", "position",
								{
									my: "left-40 top+15"
									, of: listStudentDetails.eq( activeListStudentDetailsSeqNo ).find( ".list_student_fees_due_label" )
									, collision: "fit"
								}
							);
						}
					}
					
					var studentGenderFilterSortByDialog = jQuery( ".student_gender_filter_sortby_dialog" );
					
					if( studentGenderFilterSortByDialog.dialog( "isOpen" ) ) {
						
						if( studentGenderFilterSortByDialog.offset().top <= studentList.offset().top ) {
							studentGenderFilterSortByDialog.dialog( "close" );
						}
						else {
							studentGenderFilterSortByDialog.dialog( "option", "position",
								{
									my: "left-35 top+15"
									, of: listStudentDetails.eq( activeListStudentDetailsSeqNo ).find( ".list_student_gender_label" )
									, collision: "fit"
								}
							);
						}
					}
					
					var studentIdFilterSortByDialog = jQuery( ".student_id_filter_sortby_dialog" );
					
					if( studentIdFilterSortByDialog.dialog( "isOpen" ) ) {
						
						if( studentIdFilterSortByDialog.offset().top <= studentList.offset().top ) {
							studentIdFilterSortByDialog.dialog( "close" );
						}
						else {
							studentIdFilterSortByDialog.dialog( "option", "position",
								{
									my: "left-10 top+15"
									, of: listStudentDetails.eq( activeListStudentDetailsSeqNo ).find( ".list_student_id_label" )
									, collision: "fit"
								}
							);
						}
					}
					
					var studentLeaveReasonFilterSortByDialog = jQuery( ".student_leave_reason_filter_sortby_dialog" );
					
					if( studentLeaveReasonFilterSortByDialog.dialog( "isOpen" ) ) {
						
						if( studentLeaveReasonFilterSortByDialog.offset().top <= studentList.offset().top ) {
							studentLeaveReasonFilterSortByDialog.dialog( "close" );
						}
						else {
							studentLeaveReasonFilterSortByDialog.dialog( "option", "position",
								{
									my: "left-65 top+15"
									, of: listStudentDetails.eq( activeListStudentDetailsSeqNo ).find( ".list_student_leave_reason_label" )
									, collision: "fit"
								}
							);
						}
					}
					
					var studentOtherFeesFilterSortByDialog = jQuery( ".student_other_fees_filter_sortby_dialog" );
					
					if( studentOtherFeesFilterSortByDialog.dialog( "isOpen" ) ) {
						
						if( studentOtherFeesFilterSortByDialog.offset().top <= studentList.offset().top ) {
							studentOtherFeesFilterSortByDialog.dialog( "close" );
						}
						else {
							studentOtherFeesFilterSortByDialog.dialog( "option", "position",
								{
									my: "left-48 top+15"
									, of: listStudentDetails.eq( activeListStudentDetailsSeqNo ).find( ".list_student_other_fees_label" )
									, collision: "fit"
								}
							);
						}
					}
					
					var studentPaymentsFilterSortByDialog = jQuery( ".student_payments_filter_sortby_dialog" );
					
					if( studentPaymentsFilterSortByDialog.dialog( "isOpen" ) ) {
						
						if( studentPaymentsFilterSortByDialog.offset().top <= studentList.offset().top ) {
							studentPaymentsFilterSortByDialog.dialog( "close" );
						}
						else {
							studentPaymentsFilterSortByDialog.dialog( "option", "position",
								{
									my: "left-43 top+15"
									, of: listStudentDetails.eq( activeListStudentDetailsSeqNo ).find( ".list_student_payments_label" )
									, collision: "fit"
								}
							);
						}
					}
					
					var studentSchoolFilterSortByDialog = jQuery( ".student_school_filter_sortby_dialog" );
					
					if( studentSchoolFilterSortByDialog.dialog( "isOpen" ) ) {
						
						if( studentSchoolFilterSortByDialog.offset().top <= studentList.offset().top ) {
							studentSchoolFilterSortByDialog.dialog( "close" );
						}
						else {
							studentSchoolFilterSortByDialog.dialog( "option", "position",
								{
									my: "left-33 top+15"
									, of: listStudentDetails.eq( activeListStudentDetailsSeqNo ).find( ".list_student_school_label" )
									, collision: "fit"
								}
							);
						}
					}
					
					var studentSpecialInfoFilterSortByDialog = jQuery( ".student_special_info_filter_sortby_dialog" );
					
					if( studentSpecialInfoFilterSortByDialog.dialog( "isOpen" ) ) {
						
						if( studentSpecialInfoFilterSortByDialog.offset().top <= studentList.offset().top ) {
							studentSpecialInfoFilterSortByDialog.dialog( "close" );
						}
						else {
							studentSpecialInfoFilterSortByDialog.dialog( "option", "position",
								{
									my: "left-99 top+15"
									, of: listStudentDetails.eq( activeListStudentDetailsSeqNo ).find( ".list_student_special_info_label" )
									, collision: "fit"
								}
							);
						}
					}
					
					var studentTuitionFeesFilterSortByDialog = jQuery( ".student_tuition_fees_filter_sortby_dialog" );
					
					if( studentTuitionFeesFilterSortByDialog.dialog( "isOpen" ) ) {
						
						if( studentTuitionFeesFilterSortByDialog.offset().top <= studentList.offset().top ) {
							studentTuitionFeesFilterSortByDialog.dialog( "close" );
						}
						else {
							studentTuitionFeesFilterSortByDialog.dialog( "option", "position",
								{
									my: "left-50 top+15"
									, of: listStudentDetails.eq( activeListStudentDetailsSeqNo ).find( ".list_student_tuition_fees_label" )
									, collision: "fit"
								}
							);
						}
					}
					
					var studentVillageFilterSortByDialog = jQuery( ".student_village_filter_sortby_dialog" );
					
					if( studentVillageFilterSortByDialog.dialog( "isOpen" ) ) {
						
						if( studentVillageFilterSortByDialog.offset().top <= studentList.offset().top ) {
							studentVillageFilterSortByDialog.dialog( "close" );
						}
						else {
							studentVillageFilterSortByDialog.dialog( "option", "position",
								{
									my: "left-33 top+15"
									, of: listStudentDetails.eq( activeListStudentDetailsSeqNo ).find( ".list_student_village_label" )
									, collision: "fit"
								}
							);
						}
					}
                }
              );

              jQuery( window ).scroll(
                function() {

                  /* If Scroll Bar position + computer screen's height >= web page's header + body heights OR Scroll Bar position = the top of Student List section */
                  if( jQuery( window ).scrollTop() + jQuery( window ).height() >= studentTabListRecordForm.offset().top + studentTabListRecordForm.height() ||
                    jQuery( window ).scrollTop == studentTabListRecordForm.offset().top ) {
                    studentList.css( "overflow", "auto" );
                  }
                  else {
                    studentList.css( "overflow", "hidden" );
                  }
                }
              );
            }
          }
          else {
          	var activeSeqNo = parseInt( jQuery( this ).parent().find( ".list_student_active_seq_no" ).text() );
            var studentList = jQuery( this );
            
            jQuery( this ).accordion(
              {
                active: activeSeqNo
                , collapsible: false
                , activate: function( event, ui ) {
                  		studentList.find( ".list_student_enrollment_period_lookup" ).fadeOut();
                  		ui.newHeader.find( ".list_student_enrollment_period_lookup" ).fadeIn();
                  	}
              }
            ).show();
            
            jQuery( this ).find( ".list_student_enrollment_period_lookup" ).eq( activeSeqNo ).fadeIn();
          }
        }
  );
</script>