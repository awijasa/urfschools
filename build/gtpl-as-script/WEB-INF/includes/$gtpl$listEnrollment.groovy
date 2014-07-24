package web_inf.includes;out.print("""""");
  if( user == null ) redirect "/index.gtpl"
  else {
	html.a( class: "list_enrollment_first_name_label", href: "javascript:void( 0 )", "First Name" )
	html.a( class: "list_enrollment_last_name_label", href: "javascript:void( 0 )", "Last Name" )
	html.a( class: "list_enrollment_sponsored_label", href: "javascript:void( 0 )", "Sponsored" )
	html.a( class: "list_enrollment_classes_attended_label", href: "javascript:void( 0 )", "Classes Attended" )
	html.a( class: "list_enrollment_period_label", href: "javascript:void( 0 )", "Enrollment Period" )
	html.div( class: "actions_label", "Actions" )
	
	int activeSeqNo = 0
	int enrollTermNo
	int enrollTermYear
	int limit = Integer.parseInt( params.limit )
	int offset
	
	if( params.enrollTermNo != null )
		enrollTermNo = Integer.parseInt( params.enrollTermNo )
		
	if( params.enrollTermYear != null )
		enrollTermYear = Integer.parseInt( params.enrollTermYear )
		
	if( params.offset != null )
	    offset = Integer.parseInt( params.offset )
	    
	  if( params.studentId != null && params.schoolName != null && params.enrollTermNo != null && params.enrollTermYear != null && params.isLookup == null ) {
	    offset = 0
	    
	    com.google.appengine.api.search.Results<com.google.appengine.api.search.ScoredDocument> enrollmentResults = data.EnrollmentDocument.findByLimitAndOffset( limit, offset, session )
	    
	    while( enrollmentResults.getNumberReturned() > 0 ) {
	      boolean isOffsetFound = false
	      int sizeOfListDisplayed = enrollmentResults.getNumberReturned() == limit + 1? enrollmentResults.getNumberReturned() - 1: enrollmentResults.getNumberReturned()
	    
	      Collection enrollmentCollection = enrollmentResults.getResults()
	      
	      for( int i = 0; i < sizeOfListDisplayed; i++ ) {
	        com.google.appengine.api.search.ScoredDocument enrollment = enrollmentCollection.getAt( i )
	
	        if( enrollment.getOnlyField( "studentId" ).getAtom() == params.studentId &&
	        	enrollment.getOnlyField( "schoolName" ).getText() == params.schoolName &&
	        	enrollment.getOnlyField( "enrollTermNo" ).getNumber() == enrollTermNo &&
	        	enrollment.getOnlyField( "enrollTermYear" ).getNumber() == enrollTermYear ) {
	          activeSeqNo = i
	          isOffsetFound = true
	          break
	        }
	      }
	
	      if( isOffsetFound )
	        break
	
	      offset += limit
	
	      enrollmentResults = data.EnrollmentDocument.findByLimitAndOffset( limit, offset, session )
	      
	      if( enrollmentResults.getNumberReturned() == 0 ) {
	      	activeSeqNo = 0
	      	offset = 0
	      }
	    }
	  }

  	if( offset > 0 ) {
	    html.a( class: "list_enrollment_prev_twenty", href: "javascript:void( 0 )", "Prev 20 Enrollments" )
	    html.img( class: "list_enrollment_prev_twenty_wait", src: "/images/ajax-loader.gif" )
	}
    
    html.div( class: "list_enrollment_prev_twenty_limit", style: "display: none;", limit )
    html.div( class: "list_enrollment_prev_twenty_offset", style: "display: none;", offset - limit )
      
	com.google.appengine.api.search.Results<com.google.appengine.api.search.ScoredDocument> enrollmentResults
	
	html.div( class: "enrollment_list" ) {
		if( params.isLookup == "Y" ) {
	  		mkp.yieldUnescaped( formatter.ListItemFormatter.getEnrollmentListItem( data.EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( params.studentId, params.schoolName, enrollTermNo, enrollTermYear ) ) )
		}
		else {
		    enrollmentResults = data.EnrollmentDocument.findByLimitAndOffset( limit, offset, session )
		    enrollmentResults.eachWithIndex(
		    	{ obj, i ->
			      if( i < limit )
			        mkp.yieldUnescaped( formatter.ListItemFormatter.getEnrollmentListItem( obj ) )
			    }
			)
	    }
	}

	if( ( enrollmentResults?.getNumberReturned()?: 0 ) > limit )
  		html.a( class: "list_enrollment_next_twenty", href: "javascript:void( 0 )", "Next 20 Enrollments" )
	else
  		html.a( class: "list_enrollment_next_twenty", href: "javascript:void( 0 )", style: "display: none;", "Next 20 Enrollments" )
  		
	html.img( class: "list_enrollment_next_twenty_wait", src: "/images/ajax-loader.gif" )
  
  	html.div( class: "list_enrollment_next_twenty_limit", style: "display: none;", limit )
  	html.div( class: "list_enrollment_next_twenty_offset", style: "display: none;", offset + limit )
  	
  	html.div( class: "list_enrollment_active_seq_no", style: "display: none;", activeSeqNo )
  	html.div( class: "list_enrollment_window_scroll_left", style: "display: none", params.windowScrollLeft )
  	html.div( class: "list_enrollment_window_scroll_top", style: "display: none", params.windowScrollTop )
	}
;
out.print("""

<script type=\"text/javascript\">
  initEnrollmentList();
  
  jQuery.ajax(
  	{
  		url: \"SessionController.groovy\"
  		, type: \"GET\"
  		, data: jQuery.param(
  				{
  					action: \"getAttributes\"
  				}
  			)
  		, success: function( data ) {
  				var enrollmentClassesAttendedFilter = jQuery( data ).find( \"enrollmentClassesAttendedFilter\" ).text();
  				var enrollmentFeesDueFilter = jQuery( data ).find( \"enrollmentFeesDueFilter\" ).text();
  				var enrollmentFeesDueFilterOperator = jQuery( data ).find( \"enrollmentFeesDueFilterOperator\" ).text();
  				var enrollmentFirstNameFilter = jQuery( data ).find( \"enrollmentFirstNameFilter\" ).text();
  				var enrollmentLastNameFilter = jQuery( data ).find( \"enrollmentLastNameFilter\" ).text();
  				var enrollmentPeriodFilter = jQuery( data ).find( \"enrollmentPeriodFilter\" ).text();
  				
  				jQuery( \".filters .enrollment_classes_attended_filter\" ).text( enrollmentClassesAttendedFilter );
  				jQuery( \".filters .enrollment_fees_due_filter\" ).text( enrollmentFeesDueFilter );
  				jQuery( \".filters .enrollment_fees_due_filter_operator\" ).text( enrollmentFeesDueFilterOperator );
  				jQuery( \".filters .enrollment_first_name_filter\" ).text( enrollmentFirstNameFilter );
  				jQuery( \".filters .enrollment_last_name_filter\" ).text( enrollmentLastNameFilter );
  				jQuery( \".filters .enrollment_period_filter\" ).text( enrollmentPeriodFilter );
  				jQuery( \".enrollment_filter_sortby_dialog_classes_attended_filter\" ).val( enrollmentClassesAttendedFilter );
  				jQuery( \".enrollment_filter_sortby_dialog_enrollment_period_filter\" ).val( enrollmentPeriodFilter );
  				jQuery( \".enrollment_filter_sortby_dialog_fees_due_filter\" ).val( enrollmentFeesDueFilter );
  				jQuery( \".enrollment_filter_sortby_dialog_fees_due_filter_operator option\" ).each(
  					function() {
  						if( jQuery( this ).text() == enrollmentFeesDueFilterOperator ) {
  							jQuery( this ).prop( \"selected\", true );
						}
						else {
							jQuery( this ).prop( \"selected\", false )
						}
					}
  				);
  				jQuery( \".enrollment_filter_sortby_dialog_first_name_filter\" ).val( enrollmentFirstNameFilter );
  				jQuery( \".enrollment_filter_sortby_dialog_last_name_filter\" ).val( enrollmentLastNameFilter );
  			}
  		, error: function( jqXHR, textStatus, errorThrown ) {
  				alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
  			}
  	}
  );
  
  var enrollmentTabListRecordForm = jQuery( \"#enrollment_tab .list_record_form\" );
  
  jQuery( \".list_enrollment_prev_twenty\" ).off( \"click\" ).click(
    function() {
      var listEnrollmentPrevTwenty = jQuery( this );
      var listEnrollment = listEnrollmentPrevTwenty.parent();
      var listEnrollmentPrevTwentyLimit = listEnrollment.find( \".list_enrollment_prev_twenty_limit\" );
      var listEnrollmentPrevTwentyOffset = listEnrollment.find( \".list_enrollment_prev_twenty_offset\" );
      var listEnrollmentPrevTwentyWait = listEnrollment.find( \".list_enrollment_prev_twenty_wait\" );
      
      jQuery.ajax(
        {
          url: \"/listEnrollment.gtpl\"
          , type: \"GET\"
          , data: jQuery.param(
              {
                limit: listEnrollmentPrevTwentyLimit.text()
                , offset: listEnrollmentPrevTwentyOffset.text()
                , windowScrollLeft: listEnrollment.find( \".list_enrollment_window_scroll_left\" ).text()
                , windowScrollTop: listEnrollment.find( \".list_enrollment_window_scroll_top\" ).text()
              }
            )
          , beforeSend: function() {
              listEnrollmentPrevTwenty.toggle();
              listEnrollmentPrevTwentyWait.toggle();
            }
          , success: function( data ) {
              listEnrollment.toggle();
              listEnrollment.children().remove();
              listEnrollment.append( data );
              listEnrollment.toggle();
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
              alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
            }
        }
      );
    }
  );
  
  jQuery( \".list_enrollment_next_twenty\" ).off( \"click\" ).click(
    function() {
      var listEnrollmentNextTwenty = jQuery( this );
      var listEnrollment = listEnrollmentNextTwenty.parent();
      var listEnrollmentNextTwentyLimit = listEnrollment.find( \".list_enrollment_next_twenty_limit\" );
      var listEnrollmentNextTwentyOffset = listEnrollment.find( \".list_enrollment_next_twenty_offset\" );
      var listEnrollmentNextTwentyWait = listEnrollment.find( \".list_enrollment_next_twenty_wait\" );
      
      jQuery.ajax(
        {
          url: \"/listEnrollment.gtpl\"
          , type: \"GET\"
          , data: jQuery.param(
              {
                limit: listEnrollmentNextTwentyLimit.text()
                , offset: listEnrollmentNextTwentyOffset.text()
                , windowScrollLeft: listEnrollment.find( \".list_enrollment_window_scroll_left\" ).text()
                , windowScrollTop: listEnrollment.find( \".list_enrollment_window_scroll_top\" ).text()
              }
            )
          , beforeSend: function() {
              listEnrollmentNextTwenty.toggle();
              listEnrollmentNextTwentyWait.toggle();
            }
          , success: function( data ) {
              listEnrollment.toggle();
              listEnrollment.children().remove();
              listEnrollment.append( data );
              listEnrollment.toggle();
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
              alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
            }
        }
      );
    }
  );
  
  var enrollmentLists = jQuery( \".enrollment_list\" );
    
  enrollmentLists.each(
  	function() {
	  var enrollmentList = jQuery( this );
	  
	  if( enrollmentList.parent().hasClass( \"list_record_form\" ) ) {
		  if( navigator.userAgent.match( /(android)|(iPad)|(iPhone)|(iPod)|(webOS)/i ) ) {
		    enrollmentList.accordion(
		      {
		        collapsible: false
		        , activate: function( event, ui ) {
			      		jQuery( \".list_enrollment_period_lookup\" ).fadeOut();
			      		ui.newHeader.find( \".list_enrollment_period_lookup\" ).fadeIn();
			      	}
		      }
		    ).show();
		  
		    enrollmentList.css( \"overflow\", \"visible\" );
		    enrollmentList.css( \"height\", \"1555px\" );
		    enrollmentTabListRecordForm.css( \"height\", \"1555px\" );
		    enrollmentTabListRecordForm.find( \".list_enrollment_next_twenty\" ).css( \"top\", \"1530px\" )
		    enrollmentTabListRecordForm.find( \".list_enrollment_next_twenty_wait\" ).css( \"top\", \"1530px\" )
		    enrollmentList.find( \".list_enrollment_period_lookup\" ).eq( 0 ).fadeIn();
		  }
		  else {
		    enrollmentList.accordion(
		      {
		        active: false
		        , collapsible: true
		        , activate: function( event, ui ) {
			      		jQuery( \".list_enrollment_period_lookup\" ).fadeOut();
			      		ui.newHeader.find( \".list_enrollment_period_lookup\" ).fadeIn();
			      	}
		      }
		    ).show();
		    
		    enrollmentList.scroll(
		      function() {
		        
		        /* If Scroll Bar position + computer screen's height >= web page's header + body heights OR Scroll Bar position = the top of Enrollment List section */
		        if( jQuery( window ).scrollTop() + jQuery( window ).height() >= enrollmentTabListRecordForm.offset().top + enrollmentTabListRecordForm.height() ||
		          jQuery( window ).scrollTop == enrollmentTabListRecordForm.offset().top ) {
		          enrollmentList.css( \"overflow\", \"auto\" );
		        }
		        else {
		          enrollmentList.css( \"overflow\", \"hidden\" );
		        }
		        
		        var activeListEnrollmentDetailsSeqNo = enrollmentList.accordion( \"option\", \"active\" );
                var listEnrollmentDetails = enrollmentList.find( \".list_enrollment_details\" );
                
                var enrollmentBirthDateFilterSortByDialog = jQuery( \".enrollment_birth_date_filter_sortby_dialog\" );
                
                if( enrollmentBirthDateFilterSortByDialog.dialog( \"isOpen\" ) ) {
                	
                	if( enrollmentBirthDateFilterSortByDialog.offset().top <= enrollmentList.offset().top ) {
                		enrollmentBirthDateFilterSortByDialog.dialog( \"close\" );
                	}
                	else {
	                  	enrollmentBirthDateFilterSortByDialog.dialog( \"option\", \"position\",
							{
								my: \"left-45 top+15\"
								, of: listEnrollmentDetails.eq( activeListEnrollmentDetailsSeqNo ).find( \".list_enrollment_birth_date_label\" )
								, collision: \"fit\"
							}
						);
					}
					}
					
					var enrollmentBoardingFeesFilterSortByDialog = jQuery( \".enrollment_boarding_fees_filter_sortby_dialog\" );
					
					if( enrollmentBoardingFeesFilterSortByDialog.dialog( \"isOpen\" ) ) {
						
						if( enrollmentBoardingFeesFilterSortByDialog.offset().top <= enrollmentList.offset().top ) {
							enrollmentBoardingFeesFilterSortByDialog.dialog( \"close\" );
						}
						else {
							enrollmentBoardingFeesFilterSortByDialog.dialog( \"option\", \"position\",
								{
									my: \"left-62 top+15\"
									, of: listEnrollmentDetails.eq( activeListEnrollmentDetailsSeqNo ).find( \".list_enrollment_boarding_fees_label\" )
									, collision: \"fit\"
								}
							);
						}
					}
					
					var enrollmentFeesDueFilterSortByDialog = jQuery( \".enrollment_fees_due_filter_sortby_dialog\" );
					
					if( enrollmentFeesDueFilterSortByDialog.dialog( \"isOpen\" ) ) {
						
						if( enrollmentFeesDueFilterSortByDialog.offset().top <= enrollmentList.offset().top ) {
							enrollmentFeesDueFilterSortByDialog.dialog( \"close\" );
						}
						else {
							enrollmentFeesDueFilterSortByDialog.dialog( \"option\", \"position\",
								{
									my: \"left-40 top+15\"
									, of: listEnrollmentDetails.eq( activeListEnrollmentDetailsSeqNo ).find( \".list_enrollment_fees_due_label\" )
									, collision: \"fit\"
								}
							);
						}
					}
					
					var enrollmentGenderFilterSortByDialog = jQuery( \".enrollment_gender_filter_sortby_dialog\" );
					
					if( enrollmentGenderFilterSortByDialog.dialog( \"isOpen\" ) ) {
						
						if( enrollmentGenderFilterSortByDialog.offset().top <= enrollmentList.offset().top ) {
							enrollmentGenderFilterSortByDialog.dialog( \"close\" );
						}
						else {
							enrollmentGenderFilterSortByDialog.dialog( \"option\", \"position\",
								{
									my: \"left-35 top+15\"
									, of: listEnrollmentDetails.eq( activeListEnrollmentDetailsSeqNo ).find( \".list_enrollment_gender_label\" )
									, collision: \"fit\"
								}
							);
						}
					}
					
					var enrollmentStudentIdFilterSortByDialog = jQuery( \".enrollment_student_id_filter_sortby_dialog\" );
					
					if( enrollmentStudentIdFilterSortByDialog.dialog( \"isOpen\" ) ) {
						
						if( enrollmentStudentIdFilterSortByDialog.offset().top <= enrollmentList.offset().top ) {
							enrollmentStudentIdFilterSortByDialog.dialog( \"close\" );
						}
						else {
							enrollmentStudentIdFilterSortByDialog.dialog( \"option\", \"position\",
								{
									my: \"left-10 top+15\"
									, of: listEnrollmentDetails.eq( activeListEnrollmentDetailsSeqNo ).find( \".list_enrollment_student_id_label\" )
									, collision: \"fit\"
								}
							);
						}
					}
					
					var enrollmentLeaveReasonFilterSortByDialog = jQuery( \".enrollment_leave_reason_filter_sortby_dialog\" );
					
					if( enrollmentLeaveReasonFilterSortByDialog.dialog( \"isOpen\" ) ) {
						
						if( enrollmentLeaveReasonFilterSortByDialog.offset().top <= enrollmentList.offset().top ) {
							enrollmentLeaveReasonFilterSortByDialog.dialog( \"close\" );
						}
						else {
							enrollmentLeaveReasonFilterSortByDialog.dialog( \"option\", \"position\",
								{
									my: \"left-65 top+15\"
									, of: listEnrollmentDetails.eq( activeListEnrollmentDetailsSeqNo ).find( \".list_enrollment_leave_reason_label\" )
									, collision: \"fit\"
								}
							);
						}
					}
					
					var enrollmentOtherFeesFilterSortByDialog = jQuery( \".enrollment_other_fees_filter_sortby_dialog\" );
					
					if( enrollmentOtherFeesFilterSortByDialog.dialog( \"isOpen\" ) ) {
						
						if( enrollmentOtherFeesFilterSortByDialog.offset().top <= enrollmentList.offset().top ) {
							enrollmentOtherFeesFilterSortByDialog.dialog( \"close\" );
						}
						else {
							enrollmentOtherFeesFilterSortByDialog.dialog( \"option\", \"position\",
								{
									my: \"left-48 top+15\"
									, of: listEnrollmentDetails.eq( activeListEnrollmentDetailsSeqNo ).find( \".list_enrollment_other_fees_label\" )
									, collision: \"fit\"
								}
							);
						}
					}
					
					var enrollmentPaymentsFilterSortByDialog = jQuery( \".enrollment_payments_filter_sortby_dialog\" );
					
					if( enrollmentPaymentsFilterSortByDialog.dialog( \"isOpen\" ) ) {
						
						if( enrollmentPaymentsFilterSortByDialog.offset().top <= enrollmentList.offset().top ) {
							enrollmentPaymentsFilterSortByDialog.dialog( \"close\" );
						}
						else {
							enrollmentPaymentsFilterSortByDialog.dialog( \"option\", \"position\",
								{
									my: \"left-43 top+15\"
									, of: listEnrollmentDetails.eq( activeListEnrollmentDetailsSeqNo ).find( \".list_enrollment_payments_label\" )
									, collision: \"fit\"
								}
							);
						}
					}
					
					var enrollmentSchoolFilterSortByDialog = jQuery( \".enrollment_school_filter_sortby_dialog\" );
					
					if( enrollmentSchoolFilterSortByDialog.dialog( \"isOpen\" ) ) {
						
						if( enrollmentSchoolFilterSortByDialog.offset().top <= enrollmentList.offset().top ) {
							enrollmentSchoolFilterSortByDialog.dialog( \"close\" );
						}
						else {
							enrollmentSchoolFilterSortByDialog.dialog( \"option\", \"position\",
								{
									my: \"left-33 top+15\"
									, of: listEnrollmentDetails.eq( activeListEnrollmentDetailsSeqNo ).find( \".list_enrollment_school_label\" )
									, collision: \"fit\"
								}
							);
						}
					}
					
					var enrollmentSpecialInfoFilterSortByDialog = jQuery( \".enrollment_special_info_filter_sortby_dialog\" );
					
					if( enrollmentSpecialInfoFilterSortByDialog.dialog( \"isOpen\" ) ) {
						
						if( enrollmentSpecialInfoFilterSortByDialog.offset().top <= enrollmentList.offset().top ) {
							enrollmentSpecialInfoFilterSortByDialog.dialog( \"close\" );
						}
						else {
							enrollmentSpecialInfoFilterSortByDialog.dialog( \"option\", \"position\",
								{
									my: \"left-99 top+15\"
									, of: listEnrollmentDetails.eq( activeListEnrollmentDetailsSeqNo ).find( \".list_enrollment_special_info_label\" )
									, collision: \"fit\"
								}
							);
						}
					}
					
					var enrollmentTuitionFeesFilterSortByDialog = jQuery( \".enrollment_tuition_fees_filter_sortby_dialog\" );
					
					if( enrollmentTuitionFeesFilterSortByDialog.dialog( \"isOpen\" ) ) {
						
						if( enrollmentTuitionFeesFilterSortByDialog.offset().top <= enrollmentList.offset().top ) {
							enrollmentTuitionFeesFilterSortByDialog.dialog( \"close\" );
						}
						else {
							enrollmentTuitionFeesFilterSortByDialog.dialog( \"option\", \"position\",
								{
									my: \"left-50 top+15\"
									, of: listEnrollmentDetails.eq( activeListEnrollmentDetailsSeqNo ).find( \".list_enrollment_tuition_fees_label\" )
									, collision: \"fit\"
								}
							);
						}
					}
					
					var enrollmentVillageFilterSortByDialog = jQuery( \".enrollment_village_filter_sortby_dialog\" );
					
					if( enrollmentVillageFilterSortByDialog.dialog( \"isOpen\" ) ) {
						
						if( enrollmentVillageFilterSortByDialog.offset().top <= enrollmentList.offset().top ) {
							enrollmentVillageFilterSortByDialog.dialog( \"close\" );
						}
						else {
							enrollmentVillageFilterSortByDialog.dialog( \"option\", \"position\",
								{
									my: \"left-33 top+15\"
									, of: listEnrollmentDetails.eq( activeListEnrollmentDetailsSeqNo ).find( \".list_enrollment_village_label\" )
									, collision: \"fit\"
								}
							);
						}
					}
		      }
		    );
		
		    jQuery( window ).scroll(
		      function() {
		        
		        /* If Scroll Bar position + computer screen's height >= web page's header + body heights OR Scroll Bar position = the top of Enrollment List section */
		        if( jQuery( window ).scrollTop() + jQuery( window ).height() >= enrollmentTabListRecordForm.offset().top + enrollmentTabListRecordForm.height() ||
		          jQuery( window ).scrollTop == enrollmentTabListRecordForm.offset().top ) {
		          enrollmentList.css( \"overflow\", \"auto\" );
		        }
		        else {
		          enrollmentList.css( \"overflow\", \"hidden\" );
		        }
		      }
		    );
		  }
	  }
	  else {
	  	var activeSeqNo = parseInt( enrollmentList.parent().find( \".list_enrollment_active_seq_no\" ).text() );
		
		enrollmentList.accordion(
		  {
		    active: activeSeqNo
		    , collapsible: false
		    , activate: function( event, ui ) {
		      		enrollmentList.find( \".list_enrollment_period_lookup\" ).fadeOut();
		      		ui.newHeader.find( \".list_enrollment_period_lookup\" ).fadeIn();
		      	}
		  }
		).show();
		
		enrollmentList.find( \".list_enrollment_period_lookup\" ).eq( activeSeqNo ).fadeIn();
	  }
  	}
  )
</script>""");
