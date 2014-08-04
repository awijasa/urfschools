/*
 * Initialize some Enrollment list elements:
 * 1. Edit Button.
 * 2. Delete Button.
 * 3. Enrollment Delete action.
 */
function initEnrollmentList() {
	jQuery( ".list_enrollment_last_update_on" ).each(
			function() {
				if( new Date( jQuery( this ).text() ) != "Invalid Date" ) {
					jQuery( this ).text( new Date( jQuery( this ).text() ).toLocaleDateString() );
				}
			}
	);

	jQuery( ".list_enrollment_cancel" ).button(
			{
				icons: {
					primary: "ui-icon-cancel"
				}
			, text: false
			}
	);

	var listEnrollmentDelete = jQuery( ".list_enrollment_delete" ).button(
			{
				icons: {
					primary: "ui-icon-trash"
				}
			, text: false
			}
	);

	var listEnrollmentEdit = jQuery( ".list_enrollment_edit" ).button(
			{
				icons: {
					primary: "ui-icon-pencil"
				}
			, text: false
			}
	);

	jQuery( ".list_enrollment_parents_lookup" ).button(
			{
				icons: {
					primary: "ui-icon-extlink"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				var dialogListParent = jQuery( ".dialog_list_parent" );
				var listEnrollmentParentsLookup = jQuery( this );
				var listEnrollmentDetails = listEnrollmentParentsLookup.parents( ".list_enrollment_details" );
				var studentId = listEnrollmentDetails.find( ".list_enrollment_student_id" ).text().substring( 2 );
				var enrollmentListH3 = listEnrollmentDetails.prev();
				var listEnrollmentParentsLookupWait = jQuery( this ).parent().find( ".list_enrollment_parents_lookup_wait" );

				jQuery.ajax(
						{
							url: "/listParent.gtpl"
							, type: "GET"
							, data: jQuery.param(
									{
										limit: 2
										, offset: 0
										, studentId: studentId
									}
							)
							, beforeSend: function() {
								listEnrollmentParentsLookup.hide();
								listEnrollmentParentsLookupWait.show();
							}
							, complete: function() {
								listEnrollmentParentsLookup.show();
								listEnrollmentParentsLookupWait.hide();
							}
							, success: function( data ) {
								dialogListParent.dialog( "option", "title", "Parents of " + enrollmentListH3.find( ".list_enrollment_first_name" ).text() + " " + enrollmentListH3.find( ".list_enrollment_last_name" ).text() + " (" + studentId + ")" );
								dialogListParent.dialog( "open" );
								dialogListParent.children().remove();
								dialogListParent.append( data );
							}
							, error: function( jqXHR, textStatus, errorThrown ) {
								alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
							}
						}
				);
			}
	);
	
	jQuery( ".list_enrollment_period_lookup" ).button(
			{
				icons: {
						primary: "ui-icon-extlink"
					}
				, text: false
			}
	).off( "click" ).click(
			function( event ) {
				event.stopPropagation();
				
				var action;
				var listEnrollmentPeriodLookup = jQuery( this );
				var enrollmentListH3 = listEnrollmentPeriodLookup.parents( "h3" );
				var listEnrollmentDetails = enrollmentListH3.next();
				var studentId = listEnrollmentDetails.find( ".list_enrollment_student_id" ).text().substring( 2 );
				var enrollmentTerm = enrollmentListH3.find( ".list_enrollment_term" ).text();
				var leaveTerm;
				var leaveTermNo;
				var leaveTermYear;
				var listEnrollmentBoardingFeesLabel = listEnrollmentDetails.find( ".list_enrollment_boarding_fees_label" );
				var listEnrollmentFirstClassAttended = enrollmentListH3.find( "select.list_enrollment_first_class_attended:visible" );
				var listEnrollmentLastClassAttended = enrollmentListH3.find( "select.list_enrollment_last_class_attended:visible" );
				var listEnrollmentPeriodLookupWait = jQuery( this ).parent().find( ".list_enrollment_period_lookup_wait" );
				var listEnrollmentTuitionFeesLabel = listEnrollmentDetails.find( ".list_enrollment_tuition_fees_label" );
				
				if( listEnrollmentFirstClassAttended.length == 0 ) {
					action = "view";
					leaveTerm = enrollmentListH3.find( "span.list_enrollment_leave_term" ).text();
				}
				else {
					action = "edit";
					leaveTerm = enrollmentListH3.find( "select.list_enrollment_leave_term" ).val();
					
					if( leaveTerm == "Leave Term" ) {
						leaveTerm = "now";
					}
				}
				
				if( listEnrollmentPeriodLookupWait.length == 0 ) {
					listEnrollmentPeriodLookupWait = listEnrollmentDetails.find( ".list_enrollment_period_lookup_wait" );
				}
				
				if( leaveTerm != "now" ) {
					leaveTermNo = leaveTerm.substring( 10 );
					leaveTermYear = leaveTerm.substring( 0, 4 );
				}
				
				jQuery.ajax(
						{
							url: "/dialogClassAttended.gtpl"
							, type: "GET"
							, data: jQuery.param(
									{
										action: action
										, studentId: studentId
										, termSchool: listEnrollmentDetails.find( ".list_enrollment_school" ).text()
										, enrollTermNo: enrollmentTerm.substring( 10 )
										, enrollTermYear: enrollmentTerm.substring( 0, 4 )
										, leaveTermNo: leaveTermNo
										, leaveTermYear: leaveTermYear
										, firstClassAttended: listEnrollmentFirstClassAttended.val()
										, lastClassAttended: listEnrollmentLastClassAttended.val()
									}
								)
							, beforeSend: function() {
									listEnrollmentBoardingFeesLabel.fadeOut();
									listEnrollmentPeriodLookup.fadeOut();
									listEnrollmentPeriodLookupWait.show();
									listEnrollmentTuitionFeesLabel.fadeOut();
								}
							, complete: function() {
									listEnrollmentBoardingFeesLabel.fadeIn();
									listEnrollmentPeriodLookup.fadeIn();
									listEnrollmentPeriodLookupWait.hide();
									listEnrollmentPeriodLookup.after( listEnrollmentPeriodLookupWait );
									listEnrollmentPeriodLookupWait.css( "left", listEnrollmentPeriodLookup.position().left + 13 + "px" ).css( "top", "12px" );
									listEnrollmentTuitionFeesLabel.fadeIn();
								}
							, success: function( data ) {
									var dialogClassAttended = jQuery( ".dialog_class_attended" );
									dialogClassAttended.dialog( "option", "title", "Classes Attended by " + enrollmentListH3.find( ".list_enrollment_first_name" ).text() + " " + enrollmentListH3.find( ".list_enrollment_last_name" ).text() + " (" + studentId + ") from " + enrollmentTerm + " to " + leaveTerm );
									dialogClassAttended.dialog( "open" );
									dialogClassAttended.children().remove();
									dialogClassAttended.append( data );
									var savingEditedEnrollment = false;
									
									var listEnrollmentDetailsForm = listEnrollmentDetails.find( "form" );
									
									var classAttendedSelects = listEnrollmentDetailsForm.find( "select[name^='classAttended']" ).each(
											function() {
												var dialogClassAttendedSelect = dialogClassAttended.find( "select[name='" + jQuery( this ).attr( "name" ) + "']" );
												dialogClassAttendedSelect.find( "option" ).prop( "selected", false );
												dialogClassAttendedSelect.find( "option[value='" + jQuery( this ).val() + "']" ).prop( "selected", true );
											}
									);
									
									var boardingIndInputs = listEnrollmentDetailsForm.find( "input[name^='boardingInd']" ).each(
											function() {
												dialogClassAttended.find( "input[name='" + jQuery( this ).attr( "name" ) + "']" ).prop( "checked", jQuery( this ).prop( "checked" ) );
											}
									);
									
									if( listEnrollmentPeriodLookupWait.position().top == 48 ) {
										savingEditedEnrollment = true;
										
										listEnrollmentDetails.find( ".list_enrollment_save" ).toggle();
										listEnrollmentDetails.find( ".list_enrollment_cancel" ).toggle();
										listEnrollmentDetails.find( ".list_enrollment_save_wait" ).toggle();
									}
									
									jQuery( ".dialog_class_attended_save_link_bottom, .dialog_class_attended_save_link_top" ).click(
										function() {
											classAttendedSelects.remove();
											boardingIndInputs.remove();
											
											dialogClassAttended.find( "input, select" ).each(
												function() {
													jQuery( this ).fadeOut();
													listEnrollmentDetailsForm.append( jQuery( this ) );
												}
											);
											
											if( savingEditedEnrollment ) {
												listEnrollmentPeriodLookup.fadeOut();
												listEnrollmentTuitionFeesLabel.fadeOut();
												listEnrollmentBoardingFeesLabel.fadeOut();
												listEnrollmentDetails.find( ".list_enrollment_save" ).hide();
												listEnrollmentDetails.find( ".list_enrollment_save_wait" ).show();
												listEnrollmentDetails.find( ".list_enrollment_cancel" ).hide();
											}
											
											dialogClassAttended.dialog( "close" );
											
											if( savingEditedEnrollment ) {
												saveEditedEnrollment( enrollmentListH3, listEnrollmentDetails );
											}
										}
									);
								}
							, error: function( jqXHR, textStatus, errorThrown ) {
									alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
								}
						}
				);
			}
	);

	/* Turn off the clicks previously bound to Enrollment list's save buttons, before binding the existing buttons and the unbound new buttons. */
	jQuery( ".list_enrollment_save" ).button(
			{
				icons: {
					primary: "ui-icon-disk"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				var enrollmentListItemHeader = jQuery( this ).parent().parent().prev();
				var enrollmentListItemDetails = jQuery( this ).parent().parent();

				var listEnrollmentPeriodLookup = enrollmentListItemHeader.find( ".list_enrollment_period_lookup" ).fadeOut();
				enrollmentListItemDetails.find( ".list_enrollment_tuition_fees_label" ).fadeOut();
				enrollmentListItemDetails.find( ".list_enrollment_boarding_fees_label" ).fadeOut();
				enrollmentListItemDetails.find( ".list_enrollment_save" ).toggle();
				enrollmentListItemDetails.find( ".list_enrollment_cancel" ).toggle();
				enrollmentListItemDetails.find( ".list_enrollment_save_wait" ).toggle();
				
				var leaveTerm = enrollmentListItemHeader.find( "select.list_enrollment_leave_term" ).val();
				
				if( leaveTerm == "Leave Term" ) {
					leaveTerm = "now";
				}

				if( enrollmentListItemDetails.find( "select[name^='classAttended']" ).length > 0 ) {
					saveEditedEnrollment( enrollmentListItemHeader, enrollmentListItemDetails );
				}
				else if( enrollmentListItemHeader.find( "span.list_enrollment_first_class_attended" ).text() != enrollmentListItemHeader.find( "select.list_enrollment_first_class_attended" ).val() ||
					enrollmentListItemHeader.find( "span.list_enrollment_last_class_attended" ).text() != enrollmentListItemHeader.find( "select.list_enrollment_last_class_attended" ).val() ||
					enrollmentListItemHeader.find( "span.list_enrollment_leave_term" ).text() != leaveTerm ) {
					enrollmentListItemHeader.find( ".list_enrollment_period_lookup_wait" ).css( "top", "48px" );
					listEnrollmentPeriodLookup.click();
				}
				else {
					saveEditedEnrollment( enrollmentListItemHeader, enrollmentListItemDetails );
				}
			}
	);

	/* Turn off the clicks previously bound to Enrollment list's cancel buttons, before binding the existing buttons and the unbound new buttons. */
	jQuery( ".list_enrollment_cancel" ).off( "click" ).click(
			function() {
				var enrollmentListItemHeader = jQuery( this ).parent().parent().prev();
				var enrollmentListItemDetails = jQuery( this ).parent().parent();

				enrollmentListItemHeader.find( ".list_enrollment_period_lookup" ).css( "left", "885px" );
				enrollmentListItemHeader.find( ".list_enrollment_period_lookup_wait" ).css( "left", "898px" );
				enrollmentListItemDetails.find( ".list_enrollment_save" ).toggle();
				enrollmentListItemDetails.find( ".list_enrollment_cancel" ).toggle();
				enrollmentListItemDetails.find( "select[name^='classAttended']" ).remove();
				enrollmentListItemDetails.find( "input[name^='boardingInd']" ).remove();
				toggleEnrollmentListItemElements( enrollmentListItemHeader, enrollmentListItemDetails );
			}
	);

	/* Turn off the clicks previously bound to Enrollment list's delete buttons, before binding the existing buttons and the unbound new buttons. */
	listEnrollmentDelete.off( "click" ).click(
			function() {
				if( confirm( "Do you really want to delete this Enrollment?" ) ) {
					var enrollmentList = jQuery( ".enrollment_list" );
					var enrollmentToDeleteDiv = jQuery( this ).parent().parent();

					jQuery.ajax(
							{
								url: "EnrollmentController.groovy"
								, type: "POST"
								, data: jQuery( this ).parent().serialize() + "&action=delete&nextTwentyOffset=" + jQuery( ".list_enrollment_next_twenty_offset" ).text()
								, beforeSend: function() {
										enrollmentToDeleteDiv.find( ".list_enrollment_delete" ).hide();
										enrollmentToDeleteDiv.find( ".list_enrollment_edit" ).hide();
										enrollmentToDeleteDiv.find( ".list_enrollment_save_wait" ).show();
									}
								, success: function( data ) {
										enrollmentList.toggle();
										enrollmentToDeleteDiv.prev( "h3" ).remove();
										enrollmentToDeleteDiv.remove();
	
										/* Insert the replacing Enrollment to the bottom of the Enrollment list accordion. */
										enrollmentList.append( data )
	
										/* Refer to /js/listInit.js. */
										initEnrollmentList();
	
										if( enrollmentList.children( "h3" ).length < 20 )
											jQuery( ".list_enrollment_next_twenty" ).css( "display", "none" );
	
										enrollmentList.accordion( "refresh" ).accordion( "option", "active", false );
										enrollmentList.toggle();
									}
								, error: function( jqXHR, textStatus, errorThrown ) {
										alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
										enrollmentToDeleteDiv.find( ".list_enrollment_delete" ).show();
										enrollmentToDeleteDiv.find( ".list_enrollment_edit" ).show();
										enrollmentToDeleteDiv.find( ".list_enrollment_save_wait" ).hide();
									}
							}
					);
				}
			}
	);

	/* Turn off the clicks previously bound to Enrollment list's edit buttons, before binding the existing buttons and the unbound new buttons. */
	listEnrollmentEdit.off( "click" ).click(
			function() {
				var enrollmentListItemHeader = jQuery( this ).parent().parent().prev();
				var enrollmentListItemDetails = jQuery( this ).parent().parent();

				/* Toggle the display elements with editable elements. */
				enrollmentListItemDetails.find( ".list_enrollment_save" ).toggle();
				enrollmentListItemDetails.find( ".list_enrollment_cancel" ).toggle();
				toggleEnrollmentListItemElements( enrollmentListItemHeader, enrollmentListItemDetails );

				var classesAttendedHyphen = enrollmentListItemHeader.find( ".list_enrollment_classes_attended_hyphen" );

				var firstClassList = enrollmentListItemHeader.find( "select.list_enrollment_first_class_attended" );

				var isAndroid = navigator.userAgent.match( /(android)/i );
				var isIOS = navigator.userAgent.match( /(iPad)|(iPhone)|(iPod)|(webOS)/i );
				var isMacFirefox = navigator.userAgent.match( /(Firefox)/i ) && navigator.platform.match( /(Mac)/i );
				var isWinSafari = navigator.userAgent.match( /(Safari)/i ) && !navigator.userAgent.match( /(Chrome)/i ) && navigator.platform.match( /(Win)/i );
				var listBoxSpacer = 5;

				if( isAndroid ) {
					listBoxSpacer = 35;
				}
				else if( isMacFirefox || isWinSafari ) {
					listBoxSpacer = 29;
				}
				else if( isIOS ) {
					listBoxSpacer = 25;
				}

				classesAttendedHyphen.css( "left", ( firstClassList.position().left + firstClassList.width() + listBoxSpacer ) + "px" );

				var lastClassList = enrollmentListItemHeader.find( "select.list_enrollment_last_class_attended" );

				lastClassList.css( "left", ( classesAttendedHyphen.position().left + 10 ) + "px" );
				enrollmentListItemHeader.find( ".list_enrollment_last_class_attended_required_ind" ).css( "left", ( classesAttendedHyphen.position().left + 1 ) + "px" );
				
				/* Assign edit field values based on their existing values */
				enrollmentListItemDetails.find( "textarea.list_enrollment_leave_reason" ).val( enrollmentListItemDetails.find( "div.list_enrollment_leave_reason" ).text().replace( "<br>", "\n" ) );

				/* Select the First Class Attended value based on the existing value. */
				var firstClassList = enrollmentListItemHeader.find( "select.list_enrollment_first_class_attended" );

				firstClassList.find( "option:selected" ).prop( "selected", false );
				firstClassList.find( 'option[value="' + enrollmentListItemHeader.find( "span.list_enrollment_first_class_attended" ).text() + '"]' ).prop( "selected", true );

				/* Select the Last Class Attended value based on the existing value. */
				var lastClassList = enrollmentListItemHeader.find( "select.list_enrollment_last_class_attended" )

				lastClassList.find( "option:selected" ).prop( "selected", false );
				lastClassList.find( 'option[value="' + enrollmentListItemHeader.find( "span.list_enrollment_last_class_attended" ).text() + '"]' ).prop( "selected", true );

				/* Select the Leave Term value based on the existing value. */
				var leaveTermList = enrollmentListItemHeader.find( "select.list_enrollment_leave_term" )

				leaveTermList.find( "option:selected" ).prop( "selected", false );
				leaveTermList.find( 'option[value="' + enrollmentListItemHeader.find( "span.list_enrollment_leave_term" ).text() + '"]' ).prop( "selected", true );
				
				enrollmentListItemHeader.find( ".list_enrollment_period_lookup" ).css( "left", ( leaveTermList.position().left + leaveTermList.width() + listBoxSpacer ) + "px" );
				enrollmentListItemHeader.find( ".list_enrollment_period_lookup_wait" ).css( "left", ( leaveTermList.position().left + leaveTermList.width() + listBoxSpacer + 13 ) + "px" );

				/* Select the Leave Reason Category value based on the existing value. */
				var leaveReasonCategoryList = enrollmentListItemDetails.find( "select.list_enrollment_leave_reason_category" );

				leaveReasonCategoryList.find( "option:selected" ).prop( "selected", false );
				leaveReasonCategoryList.find( 'option[value="' + enrollmentListItemDetails.find( "div.list_enrollment_leave_reason_category" ).text() + '"]' ).prop( "selected", true );

				jQuery( ".enrollment_list" ).accordion( "option", "collapsible", false );
			}
	);
	
	jQuery( ".list_enrollment_select" ).off( "click" ).click(
			function() {
				var enrollmentDetailsForm = jQuery( this ).parent();
				var enrollmentHeaderForm = enrollmentDetailsForm.parent().prev().find( "form" );
				var studentId = enrollmentDetailsForm.find( ".list_enrollment_student_id" ).text().substring( 2 );
				var schoolName = enrollmentDetailsForm.find( ".list_enrollment_school" ).text();
				var listEnrollmentTerm = enrollmentHeaderForm.find( ".list_enrollment_term" ).text();
				var enrollTermNo = listEnrollmentTerm.substring( 10 );
				var enrollTermYear = listEnrollmentTerm.substring( 0, 4 );
				var leaveTermNo = "";
				var leaveTermYear = "";
				var listEnrollmentLeaveTerm = enrollmentHeaderForm.find( "span.list_enrollment_leave_term" ).text();
				
				if( listEnrollmentLeaveTerm != "now" ) {
					leaveTermNo = listEnrollmentLeaveTerm.substring( 10 );
					leaveTermYear = listEnrollmentLeaveTerm.substring( 0, 4 );
				}

				if( enrollmentDetailsForm.parents( ".add_fee_list_enrollment" ).length > 0 ) {
					jQuery( ".add_fee_enrollment_selector" ).text( studentId );
					jQuery( ".add_fee_student_id" ).val( studentId );
					jQuery( ".add_fee_school_name" ).val( schoolName );
					jQuery( ".add_fee_enroll_term_no" ).val( enrollTermNo );
					jQuery( ".add_fee_enroll_term_year" ).val( enrollTermYear );
					jQuery( ".add_fee_leave_term_no" ).val( leaveTermNo );
					jQuery( ".add_fee_leave_term_year" ).val( leaveTermYear );
					jQuery( ".add_fee_first_class_attended" ).val( enrollmentHeaderForm.find( "span.list_enrollment_first_class_attended" ).text() );
					jQuery( ".add_fee_last_class_attended" ).val( enrollmentHeaderForm.find( "span.list_enrollment_last_class_attended" ).text() );
					jQuery( ".add_fee_list_enrollment" ).dialog( "close" );
					
					var addFeeListEnrollment = enrollmentDetailsForm.parents( ".add_fee_list_enrollment" );
					jQuery( "html, body" ).animate(
						{
							scrollLeft: addFeeListEnrollment.find( ".list_enrollment_window_scroll_left" ).text()
							, scrollTop: addFeeListEnrollment.find( ".list_enrollment_window_scroll_top" ).text()
						}
						, "slow"
					);
					
					jQuery( ".add_fee_term" ).fadeIn();
					jQuery( ".add_fee_term_selector" ).text( "select" ).fadeIn();
					jQuery( ".add_fee_class_term_no" ).val( "" );
					jQuery( ".add_fee_class_term_year" ).val( "" );
					
					var addFeeDialogClassAttended = jQuery( ".add_fee_dialog_class_attended" );
	              	  
	              	  if( addFeeDialogClassAttended.find( ".add_fee_dialog_class_attended_first_request" ).length == 0 ) {
	              		  addFeeDialogClassAttended.prepend( '<div class="add_fee_dialog_class_attended_first_request" style="display: none">Yes</div>' );
	              	  }
				}
				else if( enrollmentDetailsForm.parents( ".add_payment_list_enrollment" ).length > 0 ) {
					jQuery( ".add_payment_enrollment_selector" ).text( studentId );
					jQuery( ".add_payment_student_id" ).val( studentId );
					jQuery( ".add_payment_school_name" ).val( schoolName );
					jQuery( ".add_payment_enroll_term_no" ).val( enrollTermNo );
					jQuery( ".add_payment_enroll_term_year" ).val( enrollTermYear );
					jQuery( ".add_payment_leave_term_no" ).val( leaveTermNo );
					jQuery( ".add_payment_leave_term_year" ).val( leaveTermYear );
					jQuery( ".add_payment_first_class_attended" ).val( enrollmentHeaderForm.find( "span.list_enrollment_first_class_attended" ).text() );
					jQuery( ".add_payment_last_class_attended" ).val( enrollmentHeaderForm.find( "span.list_enrollment_last_class_attended" ).text() );
					jQuery( ".add_payment_list_enrollment" ).dialog( "close" );
					
					var addPaymentListEnrollment = enrollmentDetailsForm.parents( ".add_payment_list_enrollment" );
					jQuery( "html, body" ).animate(
						{
							scrollLeft: addPaymentListEnrollment.find( ".list_enrollment_window_scroll_left" ).text()
							, scrollTop: addPaymentListEnrollment.find( ".list_enrollment_window_scroll_top" ).text()
						}
						, "slow"
					);
					
					jQuery( "div.add_payment_funding_source" ).fadeIn();
					jQuery( ".add_payment_funding_source_selector" ).text( "select" ).fadeIn();
					jQuery( "input.add_payment_funding_source" ).val( "" );
					jQuery( ".add_payment_term" ).fadeIn();
					jQuery( ".add_payment_term_selector" ).text( "select" ).fadeIn();
					jQuery( ".add_payment_class_term_no" ).val( "" );
					jQuery( ".add_payment_class_term_year" ).val( "" );
					
					var addPaymentListFundingSource = jQuery( ".add_payment_list_funding_source" );
	              	  
	              	  if( addPaymentListFundingSource.find( ".add_payment_list_funding_source_first_request" ).length == 0 ) {
	              		  addPaymentListFundingSource.prepend( '<div class="add_payment_list_funding_source_first_request" style="display: none">Yes</div>' );
	              	  }
	              	  
	              	  var addPaymentDialogClassAttended = jQuery( ".add_payment_dialog_class_attended" );
	              	  
	              	  if( addPaymentDialogClassAttended.find( ".add_payment_dialog_class_attended_first_request" ).length == 0 ) {
	              		  addPaymentDialogClassAttended.prepend( '<div class="add_payment_dialog_class_attended_first_request" style="display: none">Yes</div>' );
	              	  }
				}
			}
	);
	
	jQuery( "a.list_enrollment_birth_date_label" ).off( "click" ).click(
			function( event ) {
				var enrollmentBirthDateFilterSortByDialog = jQuery( ".enrollment_birth_date_filter_sortby_dialog" );
				
				if( enrollmentBirthDateFilterSortByDialog.dialog( "isOpen" ) ) {
					enrollmentBirthDateFilterSortByDialog.dialog( "close" );
				}
				else {
					enrollmentBirthDateFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					enrollmentBirthDateFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-45 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					enrollmentBirthDateFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					enrollmentBirthDateFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( ".list_enrollment_boarding_fees_label" ).off( "click" ).click(
			function( event ) {
				var listEnrollmentBoardingFeesLabel = jQuery( this );
				var listEnrollmentDetails = listEnrollmentBoardingFeesLabel.parent().parent();
				var enrollmentBoardingFeesFilterSortByDialog = jQuery( ".enrollment_boarding_fees_filter_sortby_dialog" );
				
				if( enrollmentBoardingFeesFilterSortByDialog.dialog( "isOpen" ) ) {
					enrollmentBoardingFeesFilterSortByDialog.dialog( "close" );
				}
				else {
					enrollmentBoardingFeesFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					enrollmentBoardingFeesFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-62 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					enrollmentBoardingFeesFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					enrollmentBoardingFeesFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_lookup_button" ).off( "click" ).click(
							function() {
								var enrollmentListH3 = listEnrollmentDetails.prev();
								var listEnrollmentPeriodLookupWait = enrollmentListH3.find( ".list_enrollment_period_lookup_wait" );
								
								listEnrollmentBoardingFeesLabel.after( listEnrollmentPeriodLookupWait );
								listEnrollmentPeriodLookupWait.css( "left", "70px" ).css( "top", "300px" );
								
								enrollmentListH3.find( ".list_enrollment_period_lookup" ).click();
								
								enrollmentBoardingFeesFilterSortByDialog.dialog( "close" );
							}
					);
					
					if( jQuery( this ).parents( ".dialog_enrollment_lookup" ).length == 0 ) {	
						enrollmentBoardingFeesFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_button" ).show();
						enrollmentBoardingFeesFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_sort_button" ).show();
						enrollmentBoardingFeesFilterSortByDialog.dialog( "open" );
					}
					else {
						var enrollmentFilterSortByDialogFilterButton = enrollmentBoardingFeesFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_button" ).hide();
						enrollmentBoardingFeesFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_sort_button" ).hide();
						
						if( enrollmentFilterSortByDialogFilterButton.hasClass( "glyphicons_blue" ) ) {
							enrollmentFilterSortByDialogFilterButton.removeClass( "glyphicons_blue" ).addClass( "glyphicons" );
							enrollmentBoardingFeesFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_panel" ).hide();
						}
							
						enrollmentBoardingFeesFilterSortByDialog.dialog( "open" );
							
						enrollmentBoardingFeesFilterSortByDialog.animate(
								{
									height: "31px"
								}
						).parent().animate(
								{
									width: "150px"
								}
						);
					}
				}
			}
	);
	
	jQuery( "a.list_enrollment_classes_attended_label" ).off( "click" ).click(
			function( event ) {
				var enrollmentClassesAttendedFilterSortByDialog = jQuery( ".enrollment_classes_attended_filter_sortby_dialog" );
				
				if( enrollmentClassesAttendedFilterSortByDialog.dialog( "isOpen" ) ) {
					enrollmentClassesAttendedFilterSortByDialog.dialog( "close" );
				}
				else {
					enrollmentClassesAttendedFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					enrollmentClassesAttendedFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-75 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					enrollmentClassesAttendedFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					enrollmentClassesAttendedFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_enrollment_period_label" ).off( "click" ).click(
			function( event ) {
				var enrollmentPeriodFilterSortByDialog = jQuery( ".enrollment_period_filter_sortby_dialog" );
				
				if( enrollmentPeriodFilterSortByDialog.dialog( "isOpen" ) ) {
					enrollmentPeriodFilterSortByDialog.dialog( "close" );
				}
				else {
					enrollmentPeriodFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					enrollmentPeriodFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-75 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					enrollmentPeriodFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					if( jQuery( ".enrollment_period_sort_direction" ).text() == "asc" ) {
						enrollmentPeriodFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_asc_sort_direction_button" ).show();
						enrollmentPeriodFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_dsc_sort_direction_button" ).hide();
					}
					else if( jQuery( ".enrollment_period_sort_direction" ).text() == "dsc" ) {
						enrollmentPeriodFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_asc_sort_direction_button" ).hide();
						enrollmentPeriodFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_dsc_sort_direction_button" ).show();
					}
					else {
						enrollmentPeriodFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_asc_sort_direction_button" ).hide();
						enrollmentPeriodFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_dsc_sort_direction_button" ).hide();
					}
					
					enrollmentPeriodFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_sort_order" ).text( jQuery( ".enrollment_period_sort_order" ).text() );
					
					enrollmentPeriodFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_enrollment_fees_due_label" ).off( "click" ).click(
			function( event ) {
				var enrollmentFeesDueFilterSortByDialog = jQuery( ".enrollment_fees_due_filter_sortby_dialog" );
				
				if( enrollmentFeesDueFilterSortByDialog.dialog( "isOpen" ) ) {
					enrollmentFeesDueFilterSortByDialog.dialog( "close" );
				}
				else {
					enrollmentFeesDueFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					enrollmentFeesDueFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-40 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					enrollmentFeesDueFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					enrollmentFeesDueFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_enrollment_first_name_label" ).off( "click" ).click(
			function( event ) {
				var enrollmentFirstNameFilterSortByDialog = jQuery( ".enrollment_first_name_filter_sortby_dialog" );
				
				if( enrollmentFirstNameFilterSortByDialog.dialog( "isOpen" ) ) {
					enrollmentFirstNameFilterSortByDialog.dialog( "close" );
				}
				else {
					enrollmentFirstNameFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					enrollmentFirstNameFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-45 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					enrollmentFirstNameFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					if( jQuery( ".enrollment_first_name_sort_direction" ).text() == "asc" ) {
						enrollmentFirstNameFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_asc_sort_direction_button" ).show();
						enrollmentFirstNameFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_dsc_sort_direction_button" ).hide();
					}
					else if( jQuery( ".enrollment_first_name_sort_direction" ).text() == "dsc" ) {
						enrollmentFirstNameFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_asc_sort_direction_button" ).hide();
						enrollmentFirstNameFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_dsc_sort_direction_button" ).show();
					}
					else {
						enrollmentFirstNameFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_asc_sort_direction_button" ).hide();
						enrollmentFirstNameFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_dsc_sort_direction_button" ).hide();
					}
					
					enrollmentFirstNameFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_sort_order" ).text( jQuery( ".enrollment_first_name_sort_order" ).text() );
					
					enrollmentFirstNameFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_enrollment_gender_label" ).off( "click" ).click(
			function( event ) {
				var enrollmentGenderFilterSortByDialog = jQuery( ".enrollment_gender_filter_sortby_dialog" );
				
				if( enrollmentGenderFilterSortByDialog.dialog( "isOpen" ) ) {
					enrollmentGenderFilterSortByDialog.dialog( "close" );
				}
				else {
					enrollmentGenderFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					enrollmentGenderFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-35 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					enrollmentGenderFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					enrollmentGenderFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_enrollment_student_id_label" ).off( "click" ).click(
			function( event ) {
				var enrollmentStudentIdFilterSortByDialog = jQuery( ".enrollment_student_id_filter_sortby_dialog" );
				
				if( enrollmentStudentIdFilterSortByDialog.dialog( "isOpen" ) ) {
					enrollmentStudentIdFilterSortByDialog.dialog( "close" );
				}
				else {
					enrollmentStudentIdFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					enrollmentStudentIdFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-10 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					enrollmentStudentIdFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					enrollmentStudentIdFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_enrollment_last_name_label" ).off( "click" ).click(
			function( event ) {
				var enrollmentLastNameFilterSortByDialog = jQuery( ".enrollment_last_name_filter_sortby_dialog" );
				
				if( enrollmentLastNameFilterSortByDialog.dialog( "isOpen" ) ) {
					enrollmentLastNameFilterSortByDialog.dialog( "close" );
				}
				else {
					enrollmentLastNameFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					enrollmentLastNameFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-45 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					enrollmentLastNameFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					if( jQuery( ".enrollment_last_name_sort_direction" ).text() == "asc" ) {
						enrollmentLastNameFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_asc_sort_direction_button" ).show();
						enrollmentLastNameFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_dsc_sort_direction_button" ).hide();
					}
					else if( jQuery( ".enrollment_last_name_sort_direction" ).text() == "dsc" ) {
						enrollmentLastNameFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_asc_sort_direction_button" ).hide();
						enrollmentLastNameFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_dsc_sort_direction_button" ).show();
					}
					else {
						enrollmentLastNameFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_asc_sort_direction_button" ).hide();
						enrollmentLastNameFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_dsc_sort_direction_button" ).hide();
					}
					
					enrollmentLastNameFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_sort_order" ).text( jQuery( ".enrollment_last_name_sort_order" ).text() );
					
					enrollmentLastNameFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_enrollment_leave_reason_label" ).off( "click" ).click(
			function( event ) {
				var enrollmentLeaveReasonFilterSortByDialog = jQuery( ".enrollment_leave_reason_filter_sortby_dialog" );
				
				if( enrollmentLeaveReasonFilterSortByDialog.dialog( "isOpen" ) ) {
					enrollmentLeaveReasonFilterSortByDialog.dialog( "close" );
				}
				else {
					enrollmentLeaveReasonFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					enrollmentLeaveReasonFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-65 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					enrollmentLeaveReasonFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					enrollmentLeaveReasonFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( ".list_enrollment_other_fees_label" ).off( "click" ).click(
			function( event ) {
				var listEnrollmentOtherFeesLabel = jQuery( this );
				var enrollmentOtherFeesFilterSortByDialog = jQuery( ".enrollment_other_fees_filter_sortby_dialog" );
				
				if( enrollmentOtherFeesFilterSortByDialog.dialog( "isOpen" ) ) {
					enrollmentOtherFeesFilterSortByDialog.dialog( "close" );
				}
				else {
					enrollmentOtherFeesFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					enrollmentOtherFeesFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-48 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					enrollmentOtherFeesFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					enrollmentOtherFeesFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_lookup_button" ).off( "click" ).click(
							function() {
								var dialogListFee = jQuery( ".dialog_list_fee" );
								var listEnrollmentDetails = listEnrollmentOtherFeesLabel.parents( ".list_enrollment_details" );
								var studentId = listEnrollmentDetails.find( ".list_enrollment_student_id" ).text().substring( 2 );
								var enrollmentListH3 = listEnrollmentDetails.prev();
								var listEnrollmentTerm = enrollmentListH3.find( ".list_enrollment_term" ).text();
								var listEnrollmentOtherFeesWait = listEnrollmentDetails.find( ".list_enrollment_other_fees_wait" );

								jQuery.ajax(
										{
											url: "/listFee.gtpl"
											, type: "GET"
											, data: jQuery.param(
													{
														limit: 4
														, offset: 0
														, studentId: studentId
														, schoolName: listEnrollmentDetails.find( ".list_enrollment_school" ).text()
														, enrollTermNo: listEnrollmentTerm.substring( 10 )
														, enrollTermYear: listEnrollmentTerm.substring( 0, 4 )
													}
											)
											, beforeSend: function() {
												listEnrollmentOtherFeesLabel.hide();
												listEnrollmentOtherFeesWait.show();
												enrollmentOtherFeesFilterSortByDialog.dialog( "close" );
											}
											, complete: function() {
												listEnrollmentOtherFeesLabel.show();
												listEnrollmentOtherFeesWait.hide();
											}
											, success: function( data ) {
												dialogListFee.dialog( "option", "title", "Other Fees for " + enrollmentListH3.find( ".list_enrollment_first_name" ).text() + " " + enrollmentListH3.find( ".list_enrollment_last_name" ).text() + " (" + studentId + ") from " + listEnrollmentTerm + " to " + enrollmentListH3.find( "span.list_enrollment_leave_term" ).text() );
												dialogListFee.dialog( "open" );
												dialogListFee.children().remove();
												dialogListFee.append( data );
											}
											, error: function( jqXHR, textStatus, errorThrown ) {
												alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
											}
										}
								);
							}
					);
					
					if( jQuery( this ).parents( ".dialog_enrollment_lookup" ).length == 0 ) {	
						enrollmentOtherFeesFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_button" ).show();
						enrollmentOtherFeesFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_sort_button" ).show();
						enrollmentOtherFeesFilterSortByDialog.dialog( "open" );
					}
					else {
						var enrollmentFilterSortByDialogFilterButton = enrollmentOtherFeesFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_button" ).hide();
						enrollmentOtherFeesFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_sort_button" ).hide();
						
						if( enrollmentFilterSortByDialogFilterButton.hasClass( "glyphicons_blue" ) ) {
							enrollmentFilterSortByDialogFilterButton.removeClass( "glyphicons_blue" ).addClass( "glyphicons" );
							enrollmentOtherFeesFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_panel" ).hide();
						}
							
						enrollmentOtherFeesFilterSortByDialog.dialog( "open" );
							
						enrollmentOtherFeesFilterSortByDialog.animate(
								{
									height: "31px"
								}
						).parent().animate(
								{
									width: "150px"
								}
						);
					}
				}
			}
	);
	
	jQuery( ".list_enrollment_payments_label" ).off( "click" ).click(
			function( event ) {
				var listEnrollmentPaymentsLabel = jQuery( this );
				var enrollmentPaymentsFilterSortByDialog = jQuery( ".enrollment_payments_filter_sortby_dialog" );
				
				if( enrollmentPaymentsFilterSortByDialog.dialog( "isOpen" ) ) {
					enrollmentPaymentsFilterSortByDialog.dialog( "close" );
				}
				else {
					enrollmentPaymentsFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					enrollmentPaymentsFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-43 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					enrollmentPaymentsFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					enrollmentPaymentsFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_lookup_button" ).off( "click" ).click(
							function() {
								var dialogListPayment = jQuery( ".dialog_list_payment" );
								var listEnrollmentDetails = listEnrollmentPaymentsLabel.parents( ".list_enrollment_details" );
								var studentId = listEnrollmentDetails.find( ".list_enrollment_student_id" ).text().substring( 2 );
								var enrollmentListH3 = listEnrollmentDetails.prev();
								var listEnrollmentTerm = enrollmentListH3.find( ".list_enrollment_term" ).text();
								var listEnrollmentPaymentsWait = listEnrollmentDetails.find( ".list_enrollment_payments_wait" );

								jQuery.ajax(
										{
											url: "/listPayment.gtpl"
											, type: "GET"
											, data: jQuery.param(
													{
														limit: 4
														, offset: 0
														, studentId: studentId
														, schoolName: listEnrollmentDetails.find( ".list_enrollment_school" ).text()
														, enrollTermNo: listEnrollmentTerm.substring( 10 )
														, enrollTermYear: listEnrollmentTerm.substring( 0, 4 )
													}
											)
											, beforeSend: function() {
												listEnrollmentPaymentsLabel.hide();
												listEnrollmentPaymentsWait.show();
												enrollmentPaymentsFilterSortByDialog.dialog( "close" );
											}
											, complete: function() {
												listEnrollmentPaymentsLabel.show();
												listEnrollmentPaymentsWait.hide();
											}
											, success: function( data ) {
												dialogListPayment.dialog( "option", "title", "Payments for " + enrollmentListH3.find( ".list_enrollment_first_name" ).text() + " " + enrollmentListH3.find( ".list_enrollment_last_name" ).text() + " (" + studentId + ") from " + listEnrollmentTerm + " to " + enrollmentListH3.find( "span.list_enrollment_leave_term" ).text() );
												dialogListPayment.dialog( "open" );
												dialogListPayment.children().remove();
												dialogListPayment.append( data );
											}
											, error: function( jqXHR, textStatus, errorThrown ) {
												alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
											}
										}
								);
							}
					);
					
					if( jQuery( this ).parents( ".dialog_enrollment_lookup" ).length == 0 ) {	
						enrollmentPaymentsFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_button" ).show();
						enrollmentPaymentsFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_sort_button" ).show();
						enrollmentPaymentsFilterSortByDialog.dialog( "open" );
					}
					else {
						var enrollmentFilterSortByDialogFilterButton = enrollmentPaymentsFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_button" ).hide();
						enrollmentPaymentsFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_sort_button" ).hide();
						
						if( enrollmentFilterSortByDialogFilterButton.hasClass( "glyphicons_blue" ) ) {
							enrollmentFilterSortByDialogFilterButton.removeClass( "glyphicons_blue" ).addClass( "glyphicons" );
							enrollmentPaymentsFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_panel" ).hide();
						}
							
						enrollmentPaymentsFilterSortByDialog.dialog( "open" );
							
						enrollmentPaymentsFilterSortByDialog.animate(
								{
									height: "31px"
								}
						).parent().animate(
								{
									width: "150px"
								}
						);
					}
				}
			}
	);
	
	jQuery( "a.list_enrollment_school_label" ).off( "click" ).click(
			function( event ) {
				var enrollmentSchoolFilterSortByDialog = jQuery( ".enrollment_school_filter_sortby_dialog" );
				
				if( enrollmentSchoolFilterSortByDialog.dialog( "isOpen" ) ) {
					enrollmentSchoolFilterSortByDialog.dialog( "close" );
				}
				else {
					enrollmentSchoolFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					enrollmentSchoolFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-33 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					enrollmentSchoolFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					enrollmentSchoolFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_enrollment_special_info_label" ).off( "click" ).click(
			function( event ) {
				var enrollmentSpecialInfoFilterSortByDialog = jQuery( ".enrollment_special_info_filter_sortby_dialog" );
				
				if( enrollmentSpecialInfoFilterSortByDialog.dialog( "isOpen" ) ) {
					enrollmentSpecialInfoFilterSortByDialog.dialog( "close" );
				}
				else {
					enrollmentSpecialInfoFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					enrollmentSpecialInfoFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-99 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					enrollmentSpecialInfoFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					enrollmentSpecialInfoFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_enrollment_sponsored_label" ).off( "click" ).click(
			function( event ) {
				var enrollmentSponsoredFilterSortByDialog = jQuery( ".enrollment_sponsored_filter_sortby_dialog" );
				
				if( enrollmentSponsoredFilterSortByDialog.dialog( "isOpen" ) ) {
					enrollmentSponsoredFilterSortByDialog.dialog( "close" );
				}
				else {
					enrollmentSponsoredFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					enrollmentSponsoredFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-45 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					enrollmentSponsoredFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					enrollmentSponsoredFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( ".list_enrollment_tuition_fees_label" ).off( "click" ).click(
			function( event ) {
				var listEnrollmentTuitionFeesLabel = jQuery( this );
				var listEnrollmentDetails = listEnrollmentTuitionFeesLabel.parent().parent();
				var enrollmentTuitionFeesFilterSortByDialog = jQuery( ".enrollment_tuition_fees_filter_sortby_dialog" );
				
				if( enrollmentTuitionFeesFilterSortByDialog.dialog( "isOpen" ) ) {
					enrollmentTuitionFeesFilterSortByDialog.dialog( "close" );
				}
				else {
					enrollmentTuitionFeesFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					enrollmentTuitionFeesFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-50 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					enrollmentTuitionFeesFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					enrollmentTuitionFeesFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_lookup_button" ).off( "click" ).click(
							function() {
								var enrollmentListH3 = listEnrollmentDetails.prev();
								var listEnrollmentPeriodLookupWait = enrollmentListH3.find( ".list_enrollment_period_lookup_wait" );
								
								listEnrollmentTuitionFeesLabel.after( listEnrollmentPeriodLookupWait );
								listEnrollmentPeriodLookupWait.css( "left", "70px" ).css( "top", "265px" );
								
								enrollmentListH3.find( ".list_enrollment_period_lookup" ).click();
								
								enrollmentTuitionFeesFilterSortByDialog.dialog( "close" );
							}
					);
					
					if( jQuery( this ).parents( ".dialog_enrollment_lookup" ).length == 0 ) {	
						enrollmentTuitionFeesFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_button" ).show();
						enrollmentTuitionFeesFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_sort_button" ).show();
						enrollmentTuitionFeesFilterSortByDialog.dialog( "open" );
					}
					else {
						var enrollmentFilterSortByDialogFilterButton = enrollmentTuitionFeesFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_button" ).hide();
						enrollmentTuitionFeesFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_sort_button" ).hide();
						
						if( enrollmentFilterSortByDialogFilterButton.hasClass( "glyphicons_blue" ) ) {
							enrollmentFilterSortByDialogFilterButton.removeClass( "glyphicons_blue" ).addClass( "glyphicons" );
							enrollmentTuitionFeesFilterSortByDialog.find( ".enrollment_filter_sortby_dialog_filter_panel" ).hide();
						}
							
						enrollmentTuitionFeesFilterSortByDialog.dialog( "open" );
							
						enrollmentTuitionFeesFilterSortByDialog.animate(
								{
									height: "31px"
								}
						).parent().animate(
								{
									width: "150px"
								}
						);
					}
				}
			}
	);
	
	jQuery( "a.list_enrollment_village_label" ).off( "click" ).click(
			function( event ) {
				var enrollmentVillageFilterSortByDialog = jQuery( ".enrollment_village_filter_sortby_dialog" );
				
				if( enrollmentVillageFilterSortByDialog.dialog( "isOpen" ) ) {
					enrollmentVillageFilterSortByDialog.dialog( "close" );
				}
				else {
					enrollmentVillageFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					enrollmentVillageFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-33 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					enrollmentVillageFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					enrollmentVillageFilterSortByDialog.dialog( "open" );
				}
			}
	);
}

/*
 * Initialize some Fee list elements:
 * 1. Edit Button.
 * 2. Delete Button.
 * 3. Fee Delete action.
 */
function initFeeList() {
	
	jQuery( ".fee_list h3" ).find( "input" ).off( "keydown" ).keydown(
			function( event ) {
				event.stopPropagation();
			}
	);
	
	/* Turn off the clicks previously bound to Fee list's cancel buttons, before binding the existing buttons and the unbound new buttons. */
	var listFeeCancel = jQuery( ".list_fee_cancel" ).button(
			{
				icons: {
					primary: "ui-icon-cancel"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				if( !jQuery( this ).hasClass( "list_fee_disabled_button" ) ) {
					var feeListItemHeader = jQuery( this ).parent().parent().prev();
					var feeListItemDetails = jQuery( this ).parent().parent();
	
					feeListItemDetails.find( ".list_fee_save" ).toggle();
					feeListItemDetails.find( ".list_fee_cancel" ).toggle();
					toggleFeeListItemElements( feeListItemHeader, feeListItemDetails );
				}
			}
	);

	/* Turn off the clicks previously bound to Fee list's delete buttons, before binding the existing buttons and the unbound new buttons. */
	var listFeeDelete = jQuery( ".list_fee_delete" ).button(
			{
				icons: {
					primary: "ui-icon-trash"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				if( !jQuery( this ).hasClass( "list_fee_disabled_button" ) ) {
					if( confirm( "Do you really want to delete this Fee?" ) ) {
						var feeList = jQuery( this ).parents( ".fee_list" );
						var feeListContainer = feeList.parent();
						var feeToDeleteDiv = jQuery( this ).parent().parent();
	
						jQuery.ajax(
								{
									url: "FeeController.groovy"
									, type: "POST"
									, data: jQuery( this ).parent().serialize() + "&" +
										jQuery.param(
											{
												action: "delete"
												, nextTwentyOffset: feeListContainer.find( ".list_fee_next_twenty_offset" ).text()
												, studentId: feeListContainer.find( ".list_fee_student_id_param" ).text()
												, schoolName: feeListContainer.find( ".list_fee_school_name_param" ).text()
												, enrollTermNo: feeListContainer.find( ".list_fee_enroll_term_no_param" ).text()
												, enrollTermYear: feeListContainer.find( ".list_fee_enroll_term_year_param" ).text()
											}
										)
									, beforeSend: function() {
											feeToDeleteDiv.find( ".list_fee_delete" ).hide();
											feeToDeleteDiv.find( ".list_fee_edit" ).hide();
											feeToDeleteDiv.find( ".list_fee_save_wait" ).show();
										}
									, success: function( data ) {
											feeList.toggle();
											feeToDeleteDiv.prev( "h3" ).remove();
											feeToDeleteDiv.remove();
	
											/* Insert the replacing Fee to the bottom of the Fee list accordion. */
											feeList.append( data )
	
											/* Refer to /js/listInit.js. */
											initFeeList();
	
											if( feeList.children( "h3" ).length < parseInt( feeListContainer.find( ".list_fee_next_twenty_limit" ).text() ) )
												feeListContainer.find( ".list_fee_next_twenty" ).css( "display", "none" );
	
											feeList.accordion( "refresh" ).accordion( "option", "active", false );
											feeList.toggle();
	
										}
									, error: function( jqXHR, textStatus, errorThrown ) {
											alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
											feeToDeleteDiv.find( ".list_fee_delete" ).show();
											feeToDeleteDiv.find( ".list_fee_edit" ).show();
											feeToDeleteDiv.find( ".list_fee_save_wait" ).hide();
										}
								}
						);
					}
				}
			}
	);

	/* Turn off the clicks previously bound to Fee list's edit buttons, before binding the existing buttons and the unbound new buttons. */
	var listFeeEdit = jQuery( ".list_fee_edit" ).button(
			{
				icons: {
					primary: "ui-icon-pencil"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				if( !jQuery( this ).hasClass( "list_fee_disabled_button" ) ) {
					var feeListItemHeader = jQuery( this ).parent().parent().prev();
					var feeListItemDetails = jQuery( this ).parent().parent();
	
					/* Toggle the display elements with editable elements. */
					feeListItemDetails.find( ".list_fee_save" ).toggle();
					feeListItemDetails.find( ".list_fee_cancel" ).toggle();
					toggleFeeListItemElements( feeListItemHeader, feeListItemDetails );
	
					/* Assign edit field values based on their existing values */
					feeListItemHeader.find( "input.list_fee_amount" ).val( feeListItemHeader.find( "div.list_fee_amount" ).text() );
					feeListItemDetails.find( "textarea.list_fee_comment" ).val( feeListItemDetails.find( "div.list_fee_comment" ).text().replace( "<br>", "\n" ) );
					
					jQuery( this ).parents( ".fee_list" ).accordion( "option", "collapsible", false );
				}
			}
	);
	
	jQuery( ".list_fee_last_update_on" ).each(
			function() {
				if( new Date( jQuery( this ).text() ) != "Invalid Date" ) {
					jQuery( this ).text( new Date( jQuery( this ).text() ).toLocaleDateString() );
				}
			}
	);
	
	/* Turn off the clicks previously bound to Fee list's save buttons, before binding the existing buttons and the unbound new buttons. */
	var listFeeSave = jQuery( ".list_fee_save" ).button(
			{
				icons: {
					primary: "ui-icon-disk"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				if( !jQuery( this ).hasClass( "list_fee_disabled_button" ) ) {
					var feeListItemHeader = jQuery( this ).parent().parent().prev();
					var feeListItemDetails = jQuery( this ).parent().parent();
	
					feeListItemDetails.find( ".list_fee_save" ).toggle();
					feeListItemDetails.find( ".list_fee_cancel" ).toggle();
					feeListItemDetails.find( ".list_fee_save_wait" ).toggle();
	
					jQuery.ajax(
						{
							url: "FeeController.groovy"
							, type: "POST"
							, data: feeListItemDetails.find( "form" ).serialize() + "&" + feeListItemHeader.find( "form" ).serialize() + "&action=edit"
							, success: function( data ) {
	
									/* Assign display values based on their existing edit field values */
									feeListItemHeader.find( "div.list_fee_amount" ).text( jQuery( data ).find( "div.list_fee_amount" ).text() );
									feeListItemDetails.find( "div.list_fee_comment" ).replaceWith( jQuery( data ).find( "div.list_fee_comment" ) );
									feeListItemDetails.find( "div.list_fee_comment" ).toggle();
									
									/* Renew the Last Update information. */
									feeListItemDetails.find( ".list_fee_last_update_on" ).text( new Date( jQuery( data ).find( ".list_fee_last_update_on" ).text() ).toLocaleDateString() );
									feeListItemDetails.find( ".list_fee_last_update_date" ).val( jQuery( data ).find( ".list_fee_last_update_date" ).val() );
									feeListItemDetails.find( ".list_fee_last_update_by" ).text( jQuery( data ).find( ".list_fee_last_update_by" ).text() );
									feeListItemDetails.find( ".list_fee_last_update_user" ).val( jQuery( data ).find( ".list_fee_last_update_user" ).val() );
									
									/* Toggle the editable elements with display elements. */
									feeListItemDetails.find( ".list_fee_save_wait" ).toggle();
									toggleFeeListItemElements( feeListItemHeader, feeListItemDetails );
									
									updateNextTwentyOffsetsAffectedByFee( data );
								}
							, error: function( jqXHR, textStatus, errorThrown ) {
									feeListItemDetails.find( ".list_fee_save" ).toggle();
									feeListItemDetails.find( ".list_fee_cancel" ).toggle();
									feeListItemDetails.find( ".list_fee_save_wait" ).toggle();
									alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
								}
						}
					);
				}
			}
	);
	
	jQuery( "a.list_fee_student_id" ).off( "click" ).click(
			function( event ) {
				event.stopPropagation();
				
				var listFeeStudentId = jQuery( this );
				var feeHeaderForm = listFeeStudentId.parent();
				var listFeeStudentIdWait = feeHeaderForm.find( ".list_fee_student_id_wait" );

				jQuery.ajax(
					{
						url: "/listEnrollment.gtpl"
						, type: "GET"
						, data: jQuery.param(
								{
									isLookup: "Y"
									, limit: 20
									, offset: 0
									, studentId: jQuery( this ).text()
									, schoolName: feeHeaderForm.find( ".list_fee_school_name" ).val()
									, enrollTermNo: feeHeaderForm.find( ".list_fee_enroll_term_no" ).val()
									, enrollTermYear: feeHeaderForm.find( ".list_fee_enroll_term_year" ).val()
								}
							)
						, beforeSend: function() {
								listFeeCancel.addClass( "list_fee_disabled_button" );
								listFeeStudentId.hide();
								listFeeStudentIdWait.show();
								listFeeDelete.addClass( "list_fee_disabled_button" );
								listFeeEdit.addClass( "list_fee_disabled_button" );
								listFeeSave.addClass( "list_fee_disabled_button" );
							}
						, complete: function() {
								listFeeCancel.removeClass( "list_fee_disabled_button" );
								listFeeStudentId.show();
								listFeeStudentIdWait.hide();
								listFeeDelete.removeClass( "list_fee_disabled_button" );
								listFeeEdit.removeClass( "list_fee_disabled_button" );
								listFeeSave.removeClass( "list_fee_disabled_button" );
							}
						, success: function( data ) {
								var dialogEnrollmentLookup = jQuery( ".dialog_enrollment_lookup" ).dialog( "open" );
								dialogEnrollmentLookup.children().remove();
								dialogEnrollmentLookup.append( data );
							}
						, error: function( jqXHR, textStatus, errorThrown ) {
								alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
							}
					}
				);
			}
	);
}

/*
 * Initialize some Gender list elements:
 * 1. Edit Button.
 * 2. Delete Button.
 * 3. Gender Delete action.
 * 4. Gender list table row's highlight on focus.
 */
function initGenderList() {
	var listGenderCancel = jQuery( ".list_gender_cancel" ).button(
			{
				icons: {
					primary: "ui-icon-cancel"
				}
			, text: false
			}
	);

	var listGenderDelete = jQuery( ".list_gender_delete" ).button(
			{
				icons: {
					primary: "ui-icon-trash"
				}
			, text: false
			}
	);

	var listGenderEdit = jQuery( ".list_gender_edit" ).button(
			{
				icons: {
					primary: "ui-icon-pencil"
				}
			, text: false
			}
	);

	var listGenderSave = jQuery( ".list_gender_save" ).button(
			{
				icons: {
					primary: "ui-icon-disk"
				}
			, text: false
			}
	);

	/*
	 * Turn off the hovers previously bound to Gender list's rows, before binding the existing rows and the unbound new rows. Then, Highlight the associated
	 * Gender record when the cursor hovers on a Gender list's row.
	 */
	jQuery( ".gender_list tr" ).off( "hover" ).hover(
			function() {
				jQuery( this ).removeClass( "gender_list_tr" ).addClass( "gender_list_tr_hover" );
			}
			, function() {
				jQuery( this ).removeClass( "gender_list_tr_hover" ).addClass( "gender_list_tr" );
			}
	);

	listGenderCancel.off( "click" ).click(
			function() {
				var genderListItemTr = jQuery( this ).parent().parent().parent();
				genderListItemTr.find( ".list_gender_save, .list_gender_cancel" ).toggle();
				toggleGenderListItemElements( genderListItemTr );
			}
	);

	/*
	 * Turn off the clicks previously bound to Gender list's delete buttons, before binding the existing buttons and the unbound new buttons. Then,
	 * initialize the Gender list's Delete buttons.
	 */
	listGenderDelete.off( "click" ).click(
			function() {
				var genderListItem = jQuery( this ).parent().parent().parent();

				if( confirm( "Do you really want to delete the " + genderListItem.find( "span.list_gender_desc" ).text() + " Gender?" ) ) {
					jQuery.ajax(
							{
								url: "GenderController.groovy"
								, type: "POST"
								, data: jQuery( this ).parent().serialize()
								, beforeSend: function() {
										genderListItem.find( ".list_gender_delete" ).hide();
										genderListItem.find( ".list_gender_edit" ).hide();
									}
								, success: function( data ) {
										genderListItem.remove();
									}
								, error: function( jqXHR, textStatus, errorThrown ) {
										alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
										genderListItem.find( ".list_gender_delete" ).show();
										genderListItem.find( ".list_gender_edit" ).show();
									}
							}
					);
				}
			}
	);

	listGenderEdit.off( "click" ).click(
			function() {
				var genderListItemTr = jQuery( this ).parent().parent();
				genderListItemTr.find( ".list_gender_save, .list_gender_cancel" ).toggle();
				toggleGenderListItemElements( genderListItemTr );
			}
	);

	listGenderSave.off( "click" ).click(
			function() {
				var genderListItemTr = jQuery( this ).parent().parent();
				genderListItemTr.find( ".list_gender_save, .list_gender_cancel" ).toggle();

				jQuery.ajax(
						{
							url: "GenderController.groovy"
								, type: "POST"
									, data: genderListItemTr.find( "form[name='edit_gender_form']" ).serialize()
									, success: function( data ) {
										genderListItemTr.find( "span.list_gender_desc" ).text( genderListItemTr.find( "input.list_gender_desc" ).val() );
										genderListItemTr.find( ".list_gender_last_update_date" ).val( jQuery( data ).find( ".list_gender_last_update_date" ).val() );
										toggleGenderListItemElements( genderListItemTr );
									}
						, error: function( jqXHR, textStatus, errorThrown ) {
							alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
						}
						}
				);
			}
	);
}

/*
 * Initialize some Leave Reason Category list elements:
 * 1. Edit Button.
 * 2. Delete Button.
 * 3. Leave Reason Category Delete action.
 * 4. Leave Reason Category list table row's highlight on focus.
 */
function initLeaveReasonCategoryList() {
	
	jQuery( ".list_leave_reason_category_delete" ).button(
			{
				icons: {
					primary: "ui-icon-trash"
				}
			, text: false
			}
	);

	/*
	 * Turn off the hovers previously bound to Leave Reason Category list's rows, before binding the existing rows and the unbound new rows. Then, highlight
	 * the associated Leave Reason Category record when the cursor hovers on a Leave Reason Category list's row.
	 */
	jQuery( ".leave_reason_category_list tr" ).off( "hover" ).hover(
			function() {
				jQuery( this ).removeClass( "leave_reason_category_list_tr" ).addClass( "leave_reason_category_list_tr_hover" );
			}
			, function() {
				jQuery( this ).removeClass( "leave_reason_category_list_tr_hover" ).addClass( "leave_reason_category_list_tr" );
			}
	);

	/*
	 * Turn off the clicks previously bound to Leave Reason Category list's delete buttons, before binding the existing buttons and the unbound new
	 * buttons.
	 */
	jQuery( ".list_leave_reason_category_delete" ).off( "click" ).click(
			function() {
				if( confirm( 'Do you really want to delete the "' + jQuery( this ).parent().parent().prev().prev().find( "div" ).text() + '" Leave Reason Category?' ) ) {
					var leaveReasonCategoryListItem = jQuery( this ).parent().parent().parent();

					jQuery.ajax(
							{
								url: "LeaveReasonCategoryController.groovy"
								, type: "POST"
								, data: jQuery( this ).parent().serialize()
								, beforeSend: function() {
										leaveReasonCategoryListItem.find( ".list_leave_reason_category_delete" ).hide();
									}
								, success: function( data ) {
										leaveReasonCategoryListItem.remove();
									}
								, error: function( jqXHR, textStatus, errorThrown ) {
										alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
										leaveReasonCategoryListItem.find( ".list_leave_reason_category_delete" ).show();
									}
							}
					);
				}
			}
	);
}

/*
 * Initialize some Parent list elements:
 * 1. Edit Button.
 * 2. Delete Button.
 * 3. Parent Delete action.
 */
function initParentList() {

	/* Initialize the Relationship Text Field. Refer to /js/fieldInit.js. */
	initTextFieldValueWithName( ".list_parent_relationship", "Relationship" );

	jQuery( "label.list_parent_deceased_label input" ).off( "click" ).click(
			function( e ) {
				e.stopPropagation();
			}
	);

	jQuery( ".list_parent_last_update_on" ).each(
			function() {
				if( new Date( jQuery( this ).text() ) != "Invalid Date" ) {
					jQuery( this ).text( new Date( jQuery( this ).text() ).toLocaleDateString() );
				}
			}
	);

	var listParentCancel = jQuery( ".list_parent_cancel" ).button(
			{
				icons: {
					primary: "ui-icon-cancel"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				if( !jQuery( this ).hasClass( "list_parent_disabled_button" ) ) {
					var parentListItemHeader = jQuery( this ).parent().parent().prev();
					var parentListItemDetails = jQuery( this ).parent().parent();
	
					parentListItemDetails.find( ".list_parent_save" ).hide();
					parentListItemDetails.find( ".list_parent_cancel" ).hide();
	
					parentListItemDetails.find( ".list_parent_child_table tr" ).has( "input" ).each(
							function() {
								var listParentChildTableSelectorInput = jQuery( this ).find( ".list_parent_child_table_selector input" );
	
								if( listParentChildTableSelectorInput.attr( "name" ).indexOf( "newChildId" ) == 0 ) {
									jQuery( this ).remove();
								}
								else if( listParentChildTableSelectorInput.attr( "name" ).indexOf( "deleteChildId" ) == 0 ) {
									listParentChildTableSelectorInput.remove();
									jQuery( this ).show();
								}
							}
					);
	
					parentListItemDetails.find( ".list_parent_relative_table tr" ).has( "input" ).each(
							function() {
								var listParentRelativeTableSelectorInput = jQuery( this ).find( ".list_parent_relative_table_selector input" );
	
								if( listParentRelativeTableSelectorInput.attr( "name" ).indexOf( "newRelativeId" ) == 0 ) {
									jQuery( this ).remove();
								}
								else if( listParentRelativeTableSelectorInput.attr( "name" ).indexOf( "deleteRelativeId" ) == 0 ) {
									listParentRelativeTableSelectorInput.remove();
									jQuery( this ).show();
								}
							}
					);
	
					toggleParentListItemElements( parentListItemHeader, parentListItemDetails );
				}
			}
	);

	jQuery( ".list_parent_child_button" ).button(
			{
				icons: {
					primary: "ui-icon-plus"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				var parentDetailsForm = jQuery( this ).parent();
				var listParentChildSelector = parentDetailsForm.find( ".list_parent_child_selector" );
				var listParentRelationshipListSelectedOption = parentDetailsForm.find( ".list_parent_relationship_list option:selected" );

				if( listParentRelationshipListSelectedOption.index() > 0 && listParentChildSelector.text() != "select" ) {
					var parentalRelationship;
					var studentId;

					jQuery.ajax(
							{
								url: "StringEscapeUtilsController.groovy"
									, async: false
									, type: "GET"
										, data: jQuery.param(
												{
													method: "escapeHtml4"
														, string: listParentRelationshipListSelectedOption.text()
												}
										)
										, success: function( data ) {
											parentalRelationship = data;
										}
							, error: function( jqXHR, textStatus, errorThrown ) {

								/* Display an error message popup if the AJAX call returned an error. */
								alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
							}
							}
					);

					jQuery.ajax(
							{
								url: "StringEscapeUtilsController.groovy"
									, async: false
									, type: "GET"
										, data: jQuery.param(
												{
													method: "escapeHtml4"
														, string: listParentChildSelector.text()
												}
										)
										, success: function( data ) {
											studentId = data;
										}
							, error: function( jqXHR, textStatus, errorThrown ) {

								/* Display an error message popup if the AJAX call returned an error. */
								alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
							}
							}
					);

					parentDetailsForm.find( ".list_parent_child_table" ).prepend(
							"<tr>" +
							'<td class="list_parent_child_table_relationship">' +
							parentalRelationship +
							'<input name="newParentRelationship' + parentDetailsForm.find( ".list_parent_child_table_relationship" ).length + '" type="hidden" value="' + parentalRelationship + '" />' +
							"</td>" +
							'<td class="list_parent_child_table_selector">of ' +
							'<a href="javascript:void( 0 )">' +
							studentId +
							"</a>" +
							'<img class="list_parent_child_table_selector_wait", src="/images/ajax-loader.gif" />' +
							'<input name="newChildId' + parentDetailsForm.find( ".list_parent_child_table_selector" ).length + '" type="hidden" value="' + studentId + '" />' +
							"</td>" +
							"<td>" +
							'<button class="list_parent_child_delete_button" style="display: block" type="button" />' +
							"</td>" +
							"</tr>"
					);

					parentDetailsForm.find( ".list_parent_child_table_selector a" ).eq( 0 ).off( "click" ).click(
							function() {
								var listParentChildTableSelectorAnchor = jQuery( this );
								var listParentChildTableSelectorWait = jQuery( this ).parent().find( ".list_parent_child_table_selector_wait" );

								jQuery.ajax(
										{
											url: "/listStudent.gtpl"
												, type: "GET"
													, data: jQuery.param(
															{
																isDialog: "Y"
																, isLookup: "Y"
																	, limit: 20
																	, offset: 0
																	, studentId: jQuery( this ).text()
															}
													)
													, beforeSend: function() {
														listParentChildTableSelectorAnchor.hide();
														listParentChildTableSelectorWait.show();
													}
										, complete: function() {
											listParentChildTableSelectorAnchor.show();
											listParentChildTableSelectorWait.hide();
										}
										, success: function( data ) {
											var dialogStudentLookup = jQuery( ".dialog_student_lookup" ).dialog( "open" );
											dialogStudentLookup.children().remove();
											dialogStudentLookup.append( data );
										}
										, error: function( jqXHR, textStatus, errorThrown ) {
											alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
										}
										}
								);
							}
					);

					parentDetailsForm.find( ".list_parent_child_delete_button" ).eq( 0 ).button(
							{
								icons: {
									primary: "ui-icon-trash"
								}
							, text: false
							}
					).off( "click" ).click(
							function() {
								jQuery( this ).parent().parent().remove();
							}
					);
				}
			}
	);

	jQuery( ".list_parent_child_table_selector" ).not( ":has( input )" ).parent().find( ".list_parent_child_delete_button" ).button(
			{
				icons: {
					primary: "ui-icon-trash"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				var listParentChildTableTr = jQuery( this ).parent().parent();
				var listParentChildTableSelector = listParentChildTableTr.find( ".list_parent_child_table_selector" );

				listParentChildTableSelector.append(
						'<input name="deleteChildId' + listParentChildTableTr.index() + '" type="hidden" value="' + listParentChildTableSelector.find( "a" ).text() + '" />'
				);

				listParentChildTableTr.hide();
			}
	);
	
	jQuery( "a.list_parent_deceased_label" ).off( "click" ).click(
			function( event ) {
				var parentDeceasedIndFilterSortByDialog = jQuery( ".parent_deceased_ind_filter_sortby_dialog" );
				
				if( parentDeceasedIndFilterSortByDialog.dialog( "isOpen" ) ) {
					parentDeceasedIndFilterSortByDialog.dialog( "close" );
				}
				else {
					parentDeceasedIndFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					parentDeceasedIndFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-45 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					parentDeceasedIndFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					parentDeceasedIndFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	var listParentDelete = jQuery( ".list_parent_delete" ).button(
			{
				icons: {
					primary: "ui-icon-trash"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				if( !jQuery( this ).hasClass( "list_parent_disabled_button" ) ) {
					if( confirm( "Do you really want to delete this Parent?" ) ) {
						var parentList = jQuery( this ).parents( ".parent_list" );
						var parentListContainer = parentList.parent();
						var parentToDeleteDiv = jQuery( this ).parent().parent();
	
						jQuery.ajax(
								{
									url: "ParentController.groovy"
									, type: "POST"
									, data: jQuery( this ).parent().serialize() + "&" +
										jQuery.param(
											{
												action: "delete"
												, nextTwentyOffset: parentListContainer.find( ".list_parent_next_twenty_offset" ).text()
												, studentId: parentListContainer.find( ".list_parent_student_id" ).text()
											}
										)
									, beforeSend: function() {
											parentToDeleteDiv.find( ".list_parent_delete" ).hide();
											parentToDeleteDiv.find( ".list_parent_edit" ).hide();
											parentToDeleteDiv.find( ".list_parent_save_wait" ).show();
										}
									, success: function( data ) {
											parentList.toggle();
											parentToDeleteDiv.prev( "h3" ).remove();
											parentToDeleteDiv.remove();
	
											/* Insert the replacing Parent to the bottom of the Parent list accordion. */
											parentList.append( data )
	
											/* Refer to /js/listInit.js. */
											initParentList();
	
											if( parentList.children( "h3" ).length < parseInt( parentListContainer.find( ".list_parent_next_twenty_limit" ).text() ) )
												parentListContainer.find( ".list_parent_next_twenty" ).css( "display", "none" );
	
											parentList.accordion( "refresh" ).accordion( "option", "active", false );
											parentList.toggle();
	
										}
									, error: function( jqXHR, textStatus, errorThrown ) {
											alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
											parentToDeleteDiv.find( ".list_parent_delete" ).show();
											parentToDeleteDiv.find( ".list_parent_edit" ).show();
											parentToDeleteDiv.find( ".list_parent_save_wait" ).hide();
										}
								}
						);
					}
				}
			}
	);

	var listParentEdit = jQuery( ".list_parent_edit" ).button(
			{
				icons: {
					primary: "ui-icon-pencil"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				if( !jQuery( this ).hasClass( "list_parent_disabled_button" ) ) {
					var parentListItemHeader = jQuery( this ).parent().parent().prev();
					var parentListItemDetails = jQuery( this ).parent().parent();
	
					/* Toggle the display elements with editable elements. */
					parentListItemDetails.find( ".list_parent_save" ).show();
					parentListItemDetails.find( ".list_parent_cancel" ).show();
					toggleParentListItemElements( parentListItemHeader, parentListItemDetails );
	
					/* Assign edit field values based on their existing values */
					parentListItemHeader.find( "input.list_parent_first_name" ).val( parentListItemHeader.find( "div.list_parent_first_name" ).text() );
					parentListItemHeader.find( "input.list_parent_last_name" ).val( parentListItemHeader.find( "div.list_parent_last_name" ).text() );
	
					if( parentListItemHeader.find( ".list_parent_deceased" ).text() == "Y" ) {
						parentListItemHeader.find( "label.list_parent_deceased_label input" ).prop( "checked", true );
					}
					else {
						parentListItemHeader.find( "label.list_parent_deceased_label input" ).prop( "checked", false );
					}
	
					parentListItemHeader.find( "input.list_parent_village" ).val( parentListItemHeader.find( "div.list_parent_village" ).text() );
					parentListItemHeader.find( "input.list_parent_primary_phone" ).val( parentListItemHeader.find( "div.list_parent_primary_phone" ).text() );
					parentListItemDetails.find( "input.list_parent_email" ).val( parentListItemDetails.find( "div.list_parent_email" ).text() );
					parentListItemDetails.find( "input.list_parent_secondary_phone" ).val( parentListItemDetails.find( "div.list_parent_secondary_phone" ).text() );
					parentListItemDetails.find( "input.list_parent_profession" ).val( parentListItemDetails.find( "div.list_parent_profession" ).text() );
	
					jQuery( this ).parents( ".parent_list" ).accordion( "option", "collapsible", false );
				}
			}
	);
	
	jQuery( "a.list_parent_email_label" ).off( "click" ).click(
			function( event ) {
				var parentEmailFilterSortByDialog = jQuery( ".parent_email_filter_sortby_dialog" );
				
				if( parentEmailFilterSortByDialog.dialog( "isOpen" ) ) {
					parentEmailFilterSortByDialog.dialog( "close" );
				}
				else {
					parentEmailFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					parentEmailFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-25 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					parentEmailFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					parentEmailFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_parent_first_name_label" ).off( "click" ).click(
			function( event ) {
				var parentFirstNameFilterSortByDialog = jQuery( ".parent_first_name_filter_sortby_dialog" );
				
				if( parentFirstNameFilterSortByDialog.dialog( "isOpen" ) ) {
					parentFirstNameFilterSortByDialog.dialog( "close" );
				}
				else {
					parentFirstNameFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					parentFirstNameFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-45 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					parentFirstNameFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					if( jQuery( ".sort_options .parent_first_name_sort_direction" ).text() == "asc" ) {
						parentFirstNameFilterSortByDialog.find( ".parent_filter_sortby_dialog_asc_sort_direction_button" ).show();
						parentFirstNameFilterSortByDialog.find( ".parent_filter_sortby_dialog_dsc_sort_direction_button" ).hide();
					}
					else if( jQuery( ".sort_options .parent_first_name_sort_direction" ).text() == "dsc" ) {
						parentFirstNameFilterSortByDialog.find( ".parent_filter_sortby_dialog_asc_sort_direction_button" ).hide();
						parentFirstNameFilterSortByDialog.find( ".parent_filter_sortby_dialog_dsc_sort_direction_button" ).show();
					}
					else {
						parentFirstNameFilterSortByDialog.find( ".parent_filter_sortby_dialog_asc_sort_direction_button" ).hide();
						parentFirstNameFilterSortByDialog.find( ".parent_filter_sortby_dialog_dsc_sort_direction_button" ).hide();
					}
					
					parentFirstNameFilterSortByDialog.find( ".parent_filter_sortby_dialog_sort_order" ).text( jQuery( ".sort_options .parent_first_name_sort_order" ).text() );
					
					parentFirstNameFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_parent_id_label" ).off( "click" ).click(
			function( event ) {
				var parentIdFilterSortByDialog = jQuery( ".parent_id_filter_sortby_dialog" );
				
				if( parentIdFilterSortByDialog.dialog( "isOpen" ) ) {
					parentIdFilterSortByDialog.dialog( "close" );
				}
				else {
					parentIdFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					parentIdFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-15 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					parentIdFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					parentIdFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_parent_last_name_label" ).off( "click" ).click(
			function( event ) {
				var parentLastNameFilterSortByDialog = jQuery( ".parent_last_name_filter_sortby_dialog" );
				
				if( parentLastNameFilterSortByDialog.dialog( "isOpen" ) ) {
					parentLastNameFilterSortByDialog.dialog( "close" );
				}
				else {
					parentLastNameFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					parentLastNameFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-45 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					parentLastNameFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					if( jQuery( ".sort_options .parent_last_name_sort_direction" ).text() == "asc" ) {
						parentLastNameFilterSortByDialog.find( ".parent_filter_sortby_dialog_asc_sort_direction_button" ).show();
						parentLastNameFilterSortByDialog.find( ".parent_filter_sortby_dialog_dsc_sort_direction_button" ).hide();
					}
					else if( jQuery( ".sort_options .parent_last_name_sort_direction" ).text() == "dsc" ) {
						parentLastNameFilterSortByDialog.find( ".parent_filter_sortby_dialog_asc_sort_direction_button" ).hide();
						parentLastNameFilterSortByDialog.find( ".parent_filter_sortby_dialog_dsc_sort_direction_button" ).show();
					}
					else {
						parentLastNameFilterSortByDialog.find( ".parent_filter_sortby_dialog_asc_sort_direction_button" ).hide();
						parentLastNameFilterSortByDialog.find( ".parent_filter_sortby_dialog_dsc_sort_direction_button" ).hide();
					}
					
					parentLastNameFilterSortByDialog.find( ".parent_filter_sortby_dialog_sort_order" ).text( jQuery( ".sort_options .parent_last_name_sort_order" ).text() );
					
					parentLastNameFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_parent_primary_phone_label" ).off( "click" ).click(
			function( event ) {
				var parentPrimaryPhoneFilterSortByDialog = jQuery( ".parent_primary_phone_filter_sortby_dialog" );
				
				if( parentPrimaryPhoneFilterSortByDialog.dialog( "isOpen" ) ) {
					parentPrimaryPhoneFilterSortByDialog.dialog( "close" );
				}
				else {
					parentPrimaryPhoneFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					parentPrimaryPhoneFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-65 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					parentPrimaryPhoneFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					parentPrimaryPhoneFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_parent_profession_label" ).off( "click" ).click(
			function( event ) {
				var parentProfessionFilterSortByDialog = jQuery( ".parent_profession_filter_sortby_dialog" );
				
				if( parentProfessionFilterSortByDialog.dialog( "isOpen" ) ) {
					parentProfessionFilterSortByDialog.dialog( "close" );
				}
				else {
					parentProfessionFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					parentProfessionFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-45 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					parentProfessionFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					parentProfessionFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	var listParentSave = jQuery( ".list_parent_save" ).button(
			{
				icons: {
					primary: "ui-icon-disk"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				if( !jQuery( this ).hasClass( "list_parent_disabled_button" ) ) {
					var parentListItemHeader = jQuery( this ).parent().parent().prev();
					var parentListItemDetails = jQuery( this ).parent().parent();
	
					parentListItemDetails.find( ".list_parent_save" ).hide();
					parentListItemDetails.find( ".list_parent_cancel" ).hide();
					parentListItemDetails.find( ".list_parent_save_wait" ).toggle();
	
					jQuery.ajax(
							{
								url: "ParentController.groovy"
									, type: "POST"
										, data: jQuery( this ).parent().serialize() + "&" + parentListItemHeader.find( "form" ).serialize() + "&action=edit"
										, success: function( data ) {
	
											/* Assign display values based on their existing edit field values */
											parentListItemHeader.find( "div.list_parent_first_name" ).text( parentListItemHeader.find( "input.list_parent_first_name" ).val() );
											parentListItemHeader.find( "div.list_parent_last_name" ).text( parentListItemHeader.find( "input.list_parent_last_name" ).val() );
	
											if( parentListItemHeader.find( "label.list_parent_deceased_label input:checkbox" ).prop( "checked" ) ) {
												parentListItemHeader.find( "div.list_parent_deceased" ).text( "Y" );
											}
											else {
												parentListItemHeader.find( "div.list_parent_deceased" ).text( "N" );
											}
	
											parentListItemHeader.find( "div.list_parent_deceased" ).text( jQuery( data ).find( "div.list_parent_deceased" ).text() );
											parentListItemHeader.find( "div.list_parent_village" ).text( parentListItemHeader.find( "input.list_parent_village" ).val() );
											parentListItemHeader.find( "div.list_parent_primary_phone" ).text( parentListItemHeader.find( "input.list_parent_primary_phone" ).val() );
											parentListItemDetails.find( "div.list_parent_email" ).text( parentListItemDetails.find( "input.list_parent_email" ).val() );
											autoResizeFieldBasedOnWidth( parentListItemDetails, ".list_parent_email", 210 );
											parentListItemDetails.find( "div.list_parent_secondary_phone" ).text( parentListItemDetails.find( "input.list_parent_secondary_phone" ).val() );
											parentListItemDetails.find( "div.list_parent_profession" ).text( parentListItemDetails.find( "input.list_parent_profession" ).val() );
	
											/* Renew the Last Update information. */
											parentListItemDetails.find( ".list_parent_last_update_on" ).text( new Date( jQuery( data ).find( ".list_parent_last_update_on" ).text() ).toLocaleDateString() );
											parentListItemDetails.find( ".list_parent_last_update_date" ).val( jQuery( data ).find( ".list_parent_last_update_date" ).val() );
											parentListItemDetails.find( ".list_parent_last_update_by" ).text( jQuery( data ).find( ".list_parent_last_update_by" ).text() );
											parentListItemDetails.find( ".list_parent_last_update_user" ).val( jQuery( data ).find( ".list_parent_last_update_user" ).val() );
	
											parentListItemDetails.find( ".list_parent_child_table tr" ).has( "input" ).each(
													function() {
														var listParentChildTableSelectorInput = jQuery( this ).find( ".list_parent_child_table_selector input" );
	
														if( listParentChildTableSelectorInput.attr( "name" ).indexOf( "newChildId" ) == 0 ) {
															listParentChildTableSelectorInput.remove();
															jQuery( this ).find( ".list_parent_child_table_relationship input" ).remove();
														}
														else if( listParentChildTableSelectorInput.attr( "name" ).indexOf( "deleteChildId" ) == 0 ) {
															jQuery( this ).remove();
														}
													}
											);
	
											parentListItemDetails.find( ".list_parent_relative_table tr" ).has( "input" ).each(
													function() {
														var listParentRelativeTableSelectorInput = jQuery( this ).find( ".list_parent_relative_table_selector input" );
	
														if( listParentRelativeTableSelectorInput.attr( "name" ).indexOf( "newRelativeId" ) == 0 ) {
															listParentRelativeTableSelectorInput.remove();
															jQuery( this ).find( ".list_parent_relative_table_relationship input" ).remove();
														}
														else if( listParentRelativeTableSelectorInput.attr( "name" ).indexOf( "deleteRelativeId" ) == 0 ) {
															jQuery( this ).remove();
														}
													}
											);
	
											parentListItemDetails.find( ".list_parent_child_delete_button" ).off( "click" ).click(
													function() {
														var listParentChildTableTr = jQuery( this ).parent().parent();
														var listParentChildTableSelector = listParentChildTableTr.find( ".list_parent_child_table_selector" );
	
														listParentChildTableSelector.append(
																'<input name="deleteChildId' + listParentChildTableTr.index() + '" type="hidden" value="' + listParentChildTableSelector.find( "a" ).text() + '" />'
														);
	
														listParentChildTableTr.hide();
													}
											);
	
											parentListItemDetails.find( ".list_parent_relative_delete_button" ).off( "click" ).click(
													function() {
														var listParentRelativeTableTr = jQuery( this ).parent().parent();
														var listParentRelativeTableSelector = listParentRelativeTableTr.find( ".list_parent_relative_table_selector" );
	
														listParentRelativeTableSelector.append(
																'<input name="deleteRelativeId' + listParentRelativeTableTr.index() + '" type="hidden" value="' + listParentRelativeTableSelector.find( "a" ).text() + '" />'
														);
	
														listParentRelativeTableTr.hide();
													}
											);
	
											/* Toggle the editable elements with display elements. */
											parentListItemDetails.find( ".list_parent_save_wait" ).hide();
											toggleParentListItemElements( parentListItemHeader, parentListItemDetails );
											
											var listParentStaleInd = parentListItemDetails.find( ".list_parent_stale_ind" );
											var parentDeceasedIndFilter = jQuery( ".filters .parent_deceased_ind_filter" );
											
											if( listParentStaleInd.text() != "Y" && parentDeceasedIndFilter.text() != "" ) {
												
												if( jQuery( data ).find( ".list_parent_deceased" ).text() != parentDeceasedIndFilter.text() ) {
													var listParentNextTwentyOffset = parentListItemDetails.parent().parent().find( ".list_parent_next_twenty_offset" );
													listParentNextTwentyOffset.text( parseInt( listParentNextTwentyOffset.text() ) - 1 );
													
													listParentStaleInd.text( "Y" );
												}
											}
											
											var parentEmailFilter = jQuery( ".filters .parent_email_filter" );
											
											if( listParentStaleInd.text() != "Y" && parentEmailFilter.text() != "" ) {
												
												if( jQuery( data ).find( "div.list_parent_email" ).text() != parentEmailFilter.text() ) {
													var listParentNextTwentyOffset = parentListItemDetails.parent().parent().find( ".list_parent_next_twenty_offset" );
													listParentNextTwentyOffset.text( parseInt( listParentNextTwentyOffset.text() ) - 1 );
													
													listParentStaleInd.text( "Y" );
												}
											}
											
											var parentFirstNameFilter = jQuery( ".filters .parent_first_name_filter" );
											
											if( listParentStaleInd.text() != "Y" && parentFirstNameFilter.text() != "" ) {
												
												if( jQuery( data ).find( "div.list_parent_first_name" ).text() != parentFirstNameFilter.text() ) {
													var listParentNextTwentyOffset = parentListItemDetails.parent().parent().find( ".list_parent_next_twenty_offset" );
													listParentNextTwentyOffset.text( parseInt( listParentNextTwentyOffset.text() ) - 1 );
													
													listParentStaleInd.text( "Y" );
												}
											}
											
											var parentIdFilter = jQuery( ".filters .parent_id_filter" );
											
											if( listParentStaleInd.text() != "Y" && parentIdFilter.text() != "" ) {
												
												if( jQuery( data ).find( ".list_parent_id" ).text() != parentIdFilter.text() ) {
													var listParentNextTwentyOffset = parentListItemDetails.parent().parent().find( ".list_parent_next_twenty_offset" );
													listParentNextTwentyOffset.text( parseInt( listParentNextTwentyOffset.text() ) - 1 );
													
													listParentStaleInd.text( "Y" );
												}
											}
											
											var parentLastNameFilter = jQuery( ".filters .parent_last_name_filter" );
											
											if( listParentStaleInd.text() != "Y" && parentLastNameFilter.text() != "" ) {
												
												if( jQuery( data ).find( "div.list_parent_last_name" ).text() != parentLastNameFilter.text() ) {
													var listParentNextTwentyOffset = parentListItemDetails.parent().parent().find( ".list_parent_next_twenty_offset" );
													listParentNextTwentyOffset.text( parseInt( listParentNextTwentyOffset.text() ) - 1 );
													
													listParentStaleInd.text( "Y" );
												}
											}
											
											var parentPrimaryPhoneFilter = jQuery( ".filters .parent_primary_phone_filter" );
											
											if( listParentStaleInd.text() != "Y" && parentPrimaryPhoneFilter.text() != "" ) {
												
												if( jQuery( data ).find( "div.list_parent_primary_phone" ).text() != parentPrimaryPhoneFilter.text() ) {
													var listParentNextTwentyOffset = parentListItemDetails.parent().parent().find( ".list_parent_next_twenty_offset" );
													listParentNextTwentyOffset.text( parseInt( listParentNextTwentyOffset.text() ) - 1 );
													
													listParentStaleInd.text( "Y" );
												}
											}
											
											var parentProfessionFilter = jQuery( ".filters .parent_profession_filter" );
											
											if( listParentStaleInd.text() != "Y" && parentProfessionFilter.text() != "" ) {
												
												if( jQuery( data ).find( "div.list_parent_profession" ).text() != parentProfessionFilter.text() ) {
													var listParentNextTwentyOffset = parentListItemDetails.parent().parent().find( ".list_parent_next_twenty_offset" );
													listParentNextTwentyOffset.text( parseInt( listParentNextTwentyOffset.text() ) - 1 );
													
													listParentStaleInd.text( "Y" );
												}
											}
											
											var parentSecondaryPhoneFilter = jQuery( ".filters .parent_secondary_phone_filter" );
											
											if( listParentStaleInd.text() != "Y" && parentSecondaryPhoneFilter.text() != "" ) {
												
												if( jQuery( data ).find( "div.list_parent_secondary_phone" ).text() != parentSecondaryPhoneFilter.text() ) {
													var listParentNextTwentyOffset = parentListItemDetails.parent().parent().find( ".list_parent_next_twenty_offset" );
													listParentNextTwentyOffset.text( parseInt( listParentNextTwentyOffset.text() ) - 1 );
													
													listParentStaleInd.text( "Y" );
												}
											}
											
											var parentVillageFilter = jQuery( ".filters .parent_village_filter" );
											
											if( listParentStaleInd.text() != "Y" && parentVillageFilter.text() != "" ) {
												
												if( jQuery( data ).find( "div.list_parent_village" ).text() != parentVillageFilter.text() ) {
													var listParentNextTwentyOffset = parentListItemDetails.parent().parent().find( ".list_parent_next_twenty_offset" );
													listParentNextTwentyOffset.text( parseInt( listParentNextTwentyOffset.text() ) - 1 );
													
													listParentStaleInd.text( "Y" );
												}
											}
										}
							, error: function( jqXHR, textStatus, errorThrown ) {
								parentListItemDetails.find( ".list_parent_save" ).toggle();
								parentListItemDetails.find( ".list_parent_cancel" ).toggle();
								parentListItemDetails.find( ".list_parent_save_wait" ).toggle();
								alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
							}
							}
					);
				}
			}
	);
	
	jQuery( "a.list_parent_secondary_phone_label" ).off( "click" ).click(
			function( event ) {
				var parentSecondaryPhoneFilterSortByDialog = jQuery( ".parent_secondary_phone_filter_sortby_dialog" );
				
				if( parentSecondaryPhoneFilterSortByDialog.dialog( "isOpen" ) ) {
					parentSecondaryPhoneFilterSortByDialog.dialog( "close" );
				}
				else {
					parentSecondaryPhoneFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					parentSecondaryPhoneFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-83 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					parentSecondaryPhoneFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					parentSecondaryPhoneFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( ".list_parent_select" ).off( "click" ).click(
			function() {
				var listParentSelect = jQuery( this );
				var parentId = listParentSelect.parent().find( ".list_parent_id" ).text();

				if( listParentSelect.parents( ".add_payment_list_funding_source" ).length > 0 ) {
					jQuery( ".add_payment_funding_source_selector" ).text( parentId );
					jQuery( ".add_payment_funding_source" ).val( parentId );
					jQuery( ".add_payment_list_funding_source" ).dialog( "close" );
					
					var addPaymentListFundingSource = listParentSelect.parents( ".add_payment_list_funding_source" );
					jQuery( "html, body" ).animate(
						{
							scrollLeft: addPaymentListFundingSource.find( ".list_parent_window_scroll_left" ).text()
							, scrollTop: addPaymentListFundingSource.find( ".list_parent_window_scroll_top" ).text()
						}
						, "slow"
					);
				}
			}
	);
	
	jQuery( "a.list_parent_village_label" ).off( "click" ).click(
			function( event ) {
				var parentVillageFilterSortByDialog = jQuery( ".parent_village_filter_sortby_dialog" );
				
				if( parentVillageFilterSortByDialog.dialog( "isOpen" ) ) {
					parentVillageFilterSortByDialog.dialog( "close" );
				}
				else {
					parentVillageFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					parentVillageFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-30 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					parentVillageFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					parentVillageFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( ".list_funding_source_school_link" ).off( "click" ).click(
			function() {
				if( jQuery( this ).parents( ".add_payment_list_funding_source" ).length > 0 ) {
					jQuery( ".add_payment_funding_source_selector" ).text( "School" );
					jQuery( ".add_payment_funding_source" ).val( "School" );
					jQuery( ".add_payment_list_funding_source" ).dialog( "close" );
					
					var addPaymentListFundingSource = jQuery( this ).parents( ".add_payment_list_funding_source" );
					jQuery( "html, body" ).animate(
						{
							scrollLeft: addPaymentListFundingSource.find( ".list_parent_window_scroll_left" ).text()
							, scrollTop: addPaymentListFundingSource.find( ".list_parent_window_scroll_top" ).text()
						}
						, "slow"
					);
				}
			}
	);
	
	jQuery( ".list_funding_source_self_link" ).off( "click" ).click(
			function() {
				if( jQuery( this ).parents( ".add_payment_list_funding_source" ).length > 0 ) {
					jQuery( ".add_payment_funding_source_selector" ).text( "Self" );
					jQuery( ".add_payment_funding_source" ).val( "Self" );
					jQuery( ".add_payment_list_funding_source" ).dialog( "close" );
					
					var addPaymentListFundingSource = jQuery( this ).parents( ".add_payment_list_funding_source" );
					jQuery( "html, body" ).animate(
						{
							scrollLeft: addPaymentListFundingSource.find( ".list_parent_window_scroll_left" ).text()
							, scrollTop: addPaymentListFundingSource.find( ".list_parent_window_scroll_top" ).text()
						}
						, "slow"
					);
				}
			}
	);

	var listParentChildListStudent = jQuery( ".list_parent_child_list_student" );

	jQuery( ".list_parent_child_selector" ).off( "click" ).click(
			function() {
				var listParentChildListStudentFirstRequest = jQuery( ".list_parent_child_list_student_first_request" );
				var listParentChildSelector = jQuery( this );
				var listParentChildSelectorWait = jQuery( this ).parent().find( ".list_parent_child_selector_wait" );

				if( jQuery( this ).text() == "select" ) {
					if( listParentChildListStudentFirstRequest.text() == "Yes" ) {
						jQuery.ajax(
							{
								url: "/listStudent.gtpl"
								, type: "GET"
								, data: jQuery.param(
										{
											isDialog: "Y"
											, limit: 20
											, offset: 0
											, windowScrollLeft: jQuery( window ).scrollLeft()
											, windowScrollTop: jQuery( window ).scrollTop()
										}
									)
								, beforeSend: function() {
										listParentCancel.addClass( "list_parent_disabled_button" );
										listParentChildSelector.hide();
										listParentChildSelectorWait.show();
										listParentDelete.addClass( "list_parent_disabled_button" );
										listParentEdit.addClass( "list_parent_disabled_button" );
										listParentSave.addClass( "list_parent_disabled_button" );
									}
								, complete: function() {
										listParentCancel.removeClass( "list_parent_disabled_button" );
										listParentChildSelector.show();
										listParentChildSelectorWait.hide();
										listParentDelete.removeClass( "list_parent_disabled_button" );
										listParentEdit.removeClass( "list_parent_disabled_button" );
										listParentSave.removeClass( "list_parent_disabled_button" );
									}
								, success: function( data ) {
										listParentChildListStudent.dialog( "open" );
										listParentChildListStudent.children().remove();
										listParentChildListStudent.append( data );
									}
								, error: function( jqXHR, textStatus, errorThrown ) {
										alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
									}
							}
						);

						listParentChildListStudentFirstRequest.remove();
					}
					else {	
						listParentChildListStudent.dialog( "open" );
					}
				}
				else {
					jQuery.ajax(
						{
							url: "/listStudent.gtpl"
							, type: "GET"
							, data: jQuery.param(
									{
										isDialog: "Y"
										, limit: 20
										, offset: 0
										, studentId: jQuery( this ).text()
										, windowScrollLeft: jQuery( window ).scrollLeft()
										, windowScrollTop: jQuery( window ).scrollTop()
									}
								)
							, beforeSend: function() {
									listParentCancel.addClass( "list_parent_disabled_button" );
									listParentChildSelector.hide();
									listParentChildSelectorWait.show();
									listParentDelete.addClass( "list_parent_disabled_button" );
									listParentEdit.addClass( "list_parent_disabled_button" );
									listParentSave.addClass( "list_parent_disabled_button" );
								}
							, complete: function() {
									listParentCancel.removeClass( "list_parent_disabled_button" );
									listParentChildSelector.show();
									listParentChildSelectorWait.hide();
									listParentDelete.removeClass( "list_parent_disabled_button" );
									listParentEdit.removeClass( "list_parent_disabled_button" );
									listParentSave.removeClass( "list_parent_disabled_button" );
								}
							, success: function( data ) {
									listParentChildListStudent.dialog( "open" );
									listParentChildListStudent.children().remove();
									listParentChildListStudent.append( data );
								}
							, error: function( jqXHR, textStatus, errorThrown ) {
									alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
								}
						}
					);
				}
			}
	);

	jQuery( ".list_parent_child_table_selector a" ).off( "click" ).click(
			function() {
				var listParentChildTableSelectorAnchor = jQuery( this );
				var listParentChildTableSelectorWait = jQuery( this ).parent().find( ".list_parent_child_table_selector_wait" );

				jQuery.ajax(
					{
						url: "/listStudent.gtpl"
						, type: "GET"
						, data: jQuery.param(
								{
									isDialog: "Y"
									, isLookup: "Y"
									, limit: 20
									, offset: 0
									, studentId: jQuery( this ).text()
								}
							)
						, beforeSend: function() {
								listParentCancel.addClass( "list_parent_disabled_button" );
								listParentChildTableSelectorAnchor.hide();
								listParentChildTableSelectorWait.show();
								listParentDelete.addClass( "list_parent_disabled_button" );
								listParentEdit.addClass( "list_parent_disabled_button" );
								listParentSave.addClass( "list_parent_disabled_button" );
							}
						, complete: function() {
								listParentCancel.removeClass( "list_parent_disabled_button" );
								listParentChildTableSelectorAnchor.show();
								listParentChildTableSelectorWait.hide();
								listParentDelete.removeClass( "list_parent_disabled_button" );
								listParentEdit.removeClass( "list_parent_disabled_button" );
								listParentSave.removeClass( "list_parent_disabled_button" );
							}
						, success: function( data ) {
								var dialogStudentLookup = jQuery( ".dialog_student_lookup" ).dialog( "open" );
								dialogStudentLookup.children().remove();
								dialogStudentLookup.append( data );
							}
						, error: function( jqXHR, textStatus, errorThrown ) {
								alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
							}
					}
				);
			}
	);

	jQuery( ".list_parent_relative_button" ).button(
			{
				icons: {
					primary: "ui-icon-plus"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				var parentDetailsForm = jQuery( this ).parent();
				var listParentRelationship = parentDetailsForm.find( ".list_parent_relationship" );
				var listParentRelativeSelector = parentDetailsForm.find( ".list_parent_relative_selector" )

				if( listParentRelationship.val() != "Relationship" && listParentRelationship.val() != "" && listParentRelativeSelector.text() != "select" ) {
					var parentalRelationship;
					var studentId;

					jQuery.ajax(
							{
								url: "StringEscapeUtilsController.groovy"
									, async: false
									, type: "GET"
										, data: jQuery.param(
												{
													method: "escapeHtml4"
														, string: listParentRelationship.val()
												}
										)
										, success: function( data ) {
											parentalRelationship = data;
										}
							, error: function( jqXHR, textStatus, errorThrown ) {

								/* Display an error message popup if the AJAX call returned an error. */
								alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
							}
							}
					);

					jQuery.ajax(
							{
								url: "StringEscapeUtilsController.groovy"
									, async: false
									, type: "GET"
										, data: jQuery.param(
												{
													method: "escapeHtml4"
														, string: listParentRelativeSelector.text()
												}
										)
										, success: function( data ) {
											studentId = data;
										}
							, error: function( jqXHR, textStatus, errorThrown ) {

								/* Display an error message popup if the AJAX call returned an error. */
								alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
							}
							}
					);

					parentDetailsForm.find( ".list_parent_relative_table" ).prepend(
							"<tr>" +
							'<td class="list_parent_relative_table_relationship">' +
							parentalRelationship +
							'<input name="newGuardianRelationship' + parentDetailsForm.find( ".list_parent_relative_table_relationship" ).length + '" type="hidden" value="' + parentalRelationship + '" />' +
							"</td>" +
							'<td class="list_parent_relative_table_selector">of ' +
							'<a href="javascript:void( 0 )">' +
							studentId +
							"</a>" +
							'<img class="list_parent_relative_table_selector_wait", src="/images/ajax-loader.gif" />' +
							'<input name="newRelativeId' + parentDetailsForm.find( ".list_parent_relative_table_selector" ).length + '" type="hidden" value="' + studentId + '" />' +
							"</td>" +
							"<td>" +
							'<button class="list_parent_relative_delete_button" style="display: block" type="button" />' +
							"</td>" +
							"</tr>"
					);

					parentDetailsForm.find( ".list_parent_relative_table_selector a" ).eq( 0 ).off( "click" ).click(
							function() {
								var listParentRelativeTableSelectorAnchor = jQuery( this );
								var listParentRelativeTableSelectorWait = jQuery( this ).parent().find( ".list_parent_relative_table_selector_wait" );

								jQuery.ajax(
										{
											url: "/listStudent.gtpl"
												, type: "GET"
													, data: jQuery.param(
															{
																isDialog: "Y"
																, isLookup: "Y"
																	, limit: 20
																	, offset: 0
																	, studentId: jQuery( this ).text()
															}
													)
													, beforeSend: function() {
														listParentRelativeTableSelectorAnchor.hide();
														listParentRelativeTableSelectorWait.show();
													}
										, complete: function() {
											listParentRelativeTableSelectorAnchor.show();
											listParentRelativeTableSelectorWait.hide();
										}
										, success: function( data ) {
											var dialogStudentLookup = jQuery( ".dialog_student_lookup" ).dialog( "open" );
											dialogStudentLookup.children().remove();
											dialogStudentLookup.append( data );
										}
										, error: function( jqXHR, textStatus, errorThrown ) {
											alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
										}
										}
								);
							}
					);

					parentDetailsForm.find( ".list_parent_relative_delete_button" ).eq( 0 ).button(
							{
								icons: {
									primary: "ui-icon-trash"
								}
							, text: false
							}
					).off( "click" ).click(
							function() {
								jQuery( this ).parent().parent().remove();
							}
					);
				}
			}
	);

	jQuery( ".list_parent_relative_table_selector" ).not( ":has( input )" ).parent().find( ".list_parent_relative_delete_button" ).button(
			{
				icons: {
					primary: "ui-icon-trash"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				var listParentRelativeTableTr = jQuery( this ).parent().parent();
				var listParentRelativeTableSelector = listParentRelativeTableTr.find( ".list_parent_relative_table_selector" );

				listParentRelativeTableSelector.append(
						'<input name="deleteRelativeId' + listParentRelativeTableTr.index() + '" type="hidden" value="' + listParentRelativeTableSelector.find( "a" ).text() + '" />'
				);

				listParentRelativeTableTr.hide();
			}
	);

	var listParentRelativeListStudent = jQuery( ".list_parent_relative_list_student" );

	jQuery( ".list_parent_relative_selector" ).off( "click" ).click(
			function() {
				var listParentRelativeListStudentFirstRequest = jQuery( ".list_parent_relative_list_student_first_request" );
				var listParentRelativeSelector = jQuery( this );
				var listParentRelativeSelectorWait = jQuery( this ).parent().find( ".list_parent_relative_selector_wait" );

				if( jQuery( this ).text() == "select" ) {
					if( listParentRelativeListStudentFirstRequest.text() == "Yes" ) {
						jQuery.ajax(
							{
								url: "/listStudent.gtpl"
								, type: "GET"
								, data: jQuery.param(
										{
											isDialog: "Y"
											, limit: 20
											, offset: 0
											, windowScrollLeft: jQuery( window ).scrollLeft()
											, windowScrollTop: jQuery( window ).scrollTop()
										}
									)
								, beforeSend: function() {
										listParentCancel.addClass( "list_parent_disabled_button" );
										listParentDelete.addClass( "list_parent_disabled_button" );
										listParentEdit.addClass( "list_parent_disabled_button" );
										listParentRelativeSelector.hide();
										listParentRelativeSelectorWait.show();
										listParentSave.addClass( "list_parent_disabled_button" );
									}
								, complete: function() {
										listParentCancel.removeClass( "list_parent_disabled_button" );
										listParentDelete.removeClass( "list_parent_disabled_button" );
										listParentEdit.removeClass( "list_parent_disabled_button" );
										listParentRelativeSelector.show();
										listParentRelativeSelectorWait.hide();
										listParentSave.removeClass( "list_parent_disabled_button" );
									}
								, success: function( data ) {
										listParentRelativeListStudent.dialog( "open" );
										listParentRelativeListStudent.children().remove();
										listParentRelativeListStudent.append( data );
									}
								, error: function( jqXHR, textStatus, errorThrown ) {
										alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
									}
							}
						);

						listParentRelativeListStudentFirstRequest.remove();
					}
					else {
						listParentRelativeListStudent.dialog( "open" );
					}
				}
				else {
					jQuery.ajax(
						{
							url: "/listStudent.gtpl"
							, type: "GET"
							, data: jQuery.param(
									{
										isDialog: "Y"
										, limit: 20
										, offset: 0
										, studentId: jQuery( this ).text()
										, windowScrollLeft: jQuery( window ).scrollLeft()
										, windowScrollTop: jQuery( window ).scrollTop()
									}
								)
							, beforeSend: function() {
									listParentCancel.addClass( "list_parent_disabled_button" );
									listParentDelete.addClass( "list_parent_disabled_button" );
									listParentEdit.addClass( "list_parent_disabled_button" );
									listParentRelativeSelector.hide();
									listParentRelativeSelectorWait.show();
									listParentSave.addClass( "list_parent_disabled_button" );
								}
							, complete: function() {
									listParentCancel.removeClass( "list_parent_disabled_button" );
									listParentDelete.removeClass( "list_parent_disabled_button" );
									listParentEdit.removeClass( "list_parent_disabled_button" );
									listParentRelativeSelector.show();
									listParentRelativeSelectorWait.hide();
									listParentSave.removeClass( "list_parent_disabled_button" );
								}
							, success: function( data ) {
									listParentRelativeListStudent.dialog( "open" );
									listParentRelativeListStudent.children().remove();
									listParentRelativeListStudent.append( data );
								}
							, error: function( jqXHR, textStatus, errorThrown ) {
									alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
								}
						}
					);
				}
			}
	);

	jQuery( ".list_parent_relative_table_selector a" ).off( "click" ).click(
			function() {
				var listParentRelativeTableSelectorAnchor = jQuery( this );
				var listParentRelativeTableSelectorWait = jQuery( this ).parent().find( ".list_parent_relative_table_selector_wait" );

				jQuery.ajax(
						{
							url: "/listStudent.gtpl"
							, type: "GET"
							, data: jQuery.param(
									{
										isDialog: "Y"
										, isLookup: "Y"
											, limit: 20
											, offset: 0
											, studentId: jQuery( this ).text()
									}
								)
							, beforeSend: function() {
									listParentCancel.addClass( "list_parent_disabled_button" );
									listParentDelete.addClass( "list_parent_disabled_button" );
									listParentEdit.addClass( "list_parent_disabled_button" );
									listParentRelativeTableSelectorAnchor.hide();
									listParentRelativeTableSelectorWait.show();
									listParentSave.addClass( "list_parent_disabled_button" );
								}
							, complete: function() {
									listParentCancel.removeClass( "list_parent_disabled_button" );
									listParentDelete.removeClass( "list_parent_disabled_button" );
									listParentEdit.removeClass( "list_parent_disabled_button" );
									listParentRelativeTableSelectorAnchor.show();
									listParentRelativeTableSelectorWait.hide();
									listParentSave.removeClass( "list_parent_disabled_button" );
								}
							, success: function( data ) {
									var dialogStudentLookup = jQuery( ".dialog_student_lookup" ).dialog( "open" );
									dialogStudentLookup.children().remove();
									dialogStudentLookup.append( data );
								}
							, error: function( jqXHR, textStatus, errorThrown ) {
									alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
								}
						}
				);
			}
	);
	
	jQuery( ".parent_list h3" ).find( "input" ).off( "keydown" ).keydown(
			function( event ) {
				event.stopPropagation();
			}
	);
}

/*
 * Initialize some Parental Relationship list elements:
 * 1. Edit Button.
 * 2. Delete Button.
 * 3. Parental Relationship Delete action.
 * 4. Parental Relationship list table row's highlight on focus.
 */
function initParentalRelationshipList() {

	jQuery( ".list_parental_relationship_delete" ).button(
			{
				icons: {
					primary: "ui-icon-trash"
				}
			, text: false
			}
	);

	/* Turn off the hovers previously bound to Parental Relationship list's rows, before binding the existing rows and the unbound new rows. */
	jQuery( ".parental_relationship_list tr" ).off( "hover" ).hover(
			function() {
				jQuery( this ).removeClass( "parental_relationship_list_tr" ).addClass( "parental_relationship_list_tr_hover" );
			}
			, function() {
				jQuery( this ).removeClass( "parental_relationship_list_tr_hover" ).addClass( "parental_relationship_list_tr" );
			}
	);

	/*
	 * Turn off the clicks previously bound to Parental Relationship list's delete buttons, before binding the existing buttons and the unbound new
	 * buttons.
	 */
	jQuery( ".list_parental_relationship_delete" ).off( "click" ).click(
			function() {
				if( confirm( "Do you really want to delete the " + jQuery( this ).parent().parent().prev().prev().find( "div" ).text() + " Parental Relationship?" ) ) {
					var parentalRelationshipListItem = jQuery( this ).parent().parent().parent();

					jQuery.ajax(
							{
								url: "ParentalRelationshipController.groovy"
								, type: "POST"
								, data: jQuery( this ).parent().serialize()
								, beforeSend: function() {
										parentalRelationshipListItem.find( ".list_parental_relationship_delete" ).hide();
									}
								, success: function( data ) {
										parentalRelationshipListItem.remove();
									}
								, error: function( jqXHR, textStatus, errorThrown ) {
										alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
										parentalRelationshipListItem.find( ".list_parental_relationship_delete" ).show();
									}
							}
					);
				}
			}
	);
}

/*
 * Initialize some Payment list elements:
 * 1. Edit Button.
 * 2. Delete Button.
 * 3. Payment Delete action.
 */
function initPaymentList() {
	
	/* Turn off the clicks previously bound to Payment list's cancel buttons, before binding the existing buttons and the unbound new buttons. */
	var listPaymentCancel = jQuery( ".list_payment_cancel" ).button(
			{
				icons: {
					primary: "ui-icon-cancel"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				if( !jQuery( this ).hasClass( "list_payment_disabled_button" ) ) {
					var paymentListItemHeader = jQuery( this ).parent().parent().prev();
					var paymentListItemDetails = jQuery( this ).parent().parent();
	
					paymentListItemDetails.find( ".list_payment_save" ).toggle();
					paymentListItemDetails.find( ".list_payment_cancel" ).toggle();
					togglePaymentListItemElements( paymentListItemHeader, paymentListItemDetails );
				}
			}
	);

	/* Turn off the clicks previously bound to Payment list's delete buttons, before binding the existing buttons and the unbound new buttons. */
	var listPaymentDelete = jQuery( ".list_payment_delete" ).button(
			{
				icons: {
					primary: "ui-icon-trash"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				if( !jQuery( this ).hasClass( "list_payment_disabled_button" ) ) {
					if( confirm( "Do you really want to delete this Payment?" ) ) {
						var paymentList = jQuery( this ).parents( ".payment_list" );
						var paymentListContainer = paymentList.parent();
						var paymentToDeleteDiv = jQuery( this ).parent().parent();
	
						jQuery.ajax(
								{
									url: "PaymentController.groovy"
									, type: "POST"
									, data: jQuery( this ).parent().serialize() + "&" +
										jQuery.param(
											{
												action: "delete"
												, nextTwentyOffset: paymentListContainer.find( ".list_payment_next_twenty_offset" ).text()
												, studentId: paymentListContainer.find( ".list_payment_student_id_param" ).text()
												, schoolName: paymentListContainer.find( ".list_payment_school_name_param" ).text()
												, enrollTermNo: paymentListContainer.find( ".list_payment_enroll_term_no_param" ).text()
												, enrollTermYear: paymentListContainer.find( ".list_payment_enroll_term_year_param" ).text()
											}
										)
									, beforeSend: function() {
											paymentToDeleteDiv.find( ".list_payment_delete" ).hide();
											paymentToDeleteDiv.find( ".list_payment_edit" ).hide();
											paymentToDeleteDiv.find( ".list_payment_save_wait" ).show();
										}
									, success: function( data ) {
											paymentList.toggle();
											paymentToDeleteDiv.prev( "h3" ).remove();
											paymentToDeleteDiv.remove();
	
											/* Insert the replacing Payment to the bottom of the Payment list accordion. */
											paymentList.append( data )
	
											/* Refer to /js/listInit.js. */
											initPaymentList();
	
											if( paymentList.children( "h3" ).length < parseInt( paymentListContainer.find( ".list_payment_next_twenty_limit" ).text() ) )
												paymentListContainer.find( ".list_payment_next_twenty" ).css( "display", "none" );
	
											paymentList.accordion( "refresh" ).accordion( "option", "active", false );
											paymentList.toggle();
	
										}
									, error: function( jqXHR, textStatus, errorThrown ) {
											alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
											paymentToDeleteDiv.find( ".list_payment_delete" ).show();
											paymentToDeleteDiv.find( ".list_payment_edit" ).show();
											paymentToDeleteDiv.find( ".list_payment_save_wait" ).hide();
										}
								}
						);
					}
				}
			}
	);

	/* Turn off the clicks previously bound to Payment list's edit buttons, before binding the existing buttons and the unbound new buttons. */
	var listPaymentEdit = jQuery( ".list_payment_edit" ).button(
			{
				icons: {
					primary: "ui-icon-pencil"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				if( !jQuery( this ).hasClass( "list_payment_disabled_button" ) ) {
					var paymentListItemHeader = jQuery( this ).parent().parent().prev();
					var paymentListItemDetails = jQuery( this ).parent().parent();
	
					/* Toggle the display elements with editable elements. */
					paymentListItemDetails.find( ".list_payment_save" ).toggle();
					paymentListItemDetails.find( ".list_payment_cancel" ).toggle();
					togglePaymentListItemElements( paymentListItemHeader, paymentListItemDetails );
	
					/* Assign edit field values based on their existing values */
					paymentListItemHeader.find( "input.list_payment_amount" ).val( paymentListItemHeader.find( "div.list_payment_amount" ).text() );
					paymentListItemDetails.find( "textarea.list_payment_comment" ).val( paymentListItemDetails.find( "div.list_payment_comment" ).text().replace( "<br>", "\n" ) );
					
					jQuery( this ).parents( ".payment_list" ).accordion( "option", "collapsible", false );
				}
			}
	);
	
	jQuery( ".list_payment_last_update_on" ).each(
			function() {
				if( new Date( jQuery( this ).text() ) != "Invalid Date" ) {
					jQuery( this ).text( new Date( jQuery( this ).text() ).toLocaleDateString() );
				}
			}
	);
	
	/* Turn off the clicks previously bound to Payment list's save buttons, before binding the existing buttons and the unbound new buttons. */
	var listPaymentSave = jQuery( ".list_payment_save" ).button(
			{
				icons: {
					primary: "ui-icon-disk"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				if( !jQuery( this ).hasClass( "list_payment_disabled_button" ) ) {
					var paymentListItemHeader = jQuery( this ).parent().parent().prev();
					var paymentListItemDetails = jQuery( this ).parent().parent();
	
					paymentListItemDetails.find( ".list_payment_save" ).toggle();
					paymentListItemDetails.find( ".list_payment_cancel" ).toggle();
					paymentListItemDetails.find( ".list_payment_save_wait" ).toggle();
	
					jQuery.ajax(
						{
							url: "PaymentController.groovy"
							, type: "POST"
							, data: paymentListItemDetails.find( "form" ).serialize() + "&" + paymentListItemHeader.find( "form" ).serialize() + "&action=edit"
							, success: function( data ) {
	
									/* Assign display values based on their existing edit field values */
									paymentListItemHeader.find( "div.list_payment_amount" ).text( jQuery( data ).find( "div.list_payment_amount" ).text() );
									paymentListItemDetails.find( "div.list_payment_comment" ).replaceWith( jQuery( data ).find( "div.list_payment_comment" ) );
									paymentListItemDetails.find( "div.list_payment_comment" ).toggle();
									
									/* Renew the Last Update information. */
									paymentListItemDetails.find( ".list_payment_last_update_on" ).text( new Date( jQuery( data ).find( ".list_payment_last_update_on" ).text() ).toLocaleDateString() );
									paymentListItemDetails.find( ".list_payment_last_update_date" ).val( jQuery( data ).find( ".list_payment_last_update_date" ).val() );
									paymentListItemDetails.find( ".list_payment_last_update_by" ).text( jQuery( data ).find( ".list_payment_last_update_by" ).text() );
									paymentListItemDetails.find( ".list_payment_last_update_user" ).val( jQuery( data ).find( ".list_payment_last_update_user" ).val() );
									
									/* Toggle the editable elements with display elements. */
									paymentListItemDetails.find( ".list_payment_save_wait" ).toggle();
									togglePaymentListItemElements( paymentListItemHeader, paymentListItemDetails );
									
									updateNextTwentyOffsetsAffectedByPayment( data );
								}
							, error: function( jqXHR, textStatus, errorThrown ) {
									paymentListItemDetails.find( ".list_payment_save" ).toggle();
									paymentListItemDetails.find( ".list_payment_cancel" ).toggle();
									paymentListItemDetails.find( ".list_payment_save_wait" ).toggle();
									alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
								}
						}
					);
				}
			}
	);
	
	jQuery( "a.list_payment_funding_source" ).off( "click" ).click(
			function( event ) {
				event.stopPropagation();
				
				var listPaymentFundingSource = jQuery( this );
				var paymentHeaderForm = listPaymentFundingSource.parent();
				var listPaymentFundingSourceWait = paymentHeaderForm.find( ".list_payment_funding_source_wait" );

				jQuery.ajax(
					{
						url: "/listParent.gtpl"
						, type: "GET"
						, data: jQuery.param(
								{
									isLookup: "Y"
									, limit: 20
									, offset: 0
									, parentId: jQuery( this ).text()
									, studentId: paymentHeaderForm.find( "input.list_payment_student_id" ).val()
								}
							)
						, beforeSend: function() {
								listPaymentCancel.addClass( "list_payment_disabled_button" );
								listPaymentFundingSource.hide();
								listPaymentFundingSourceWait.show();
								listPaymentDelete.addClass( "list_payment_disabled_button" );
								listPaymentEdit.addClass( "list_payment_disabled_button" );
								listPaymentSave.addClass( "list_payment_disabled_button" );
							}
						, complete: function() {
								listPaymentCancel.removeClass( "list_payment_disabled_button" );
								listPaymentFundingSource.show();
								listPaymentFundingSourceWait.hide();
								listPaymentDelete.removeClass( "list_payment_disabled_button" );
								listPaymentEdit.removeClass( "list_payment_disabled_button" );
								listPaymentSave.removeClass( "list_payment_disabled_button" );
							}
						, success: function( data ) {
								var dialogParentLookup = jQuery( ".dialog_parent_lookup" ).dialog( "open" );
								dialogParentLookup.children().remove();
								dialogParentLookup.append( data );
							}
						, error: function( jqXHR, textStatus, errorThrown ) {
								alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
							}
					}
				);
			}
	);
	
	jQuery( "a.list_payment_student_id" ).off( "click" ).click(
			function( event ) {
				event.stopPropagation();
				
				var listPaymentStudentId = jQuery( this );
				var paymentHeaderForm = listPaymentStudentId.parent();
				var listPaymentStudentIdWait = paymentHeaderForm.find( ".list_payment_student_id_wait" );

				jQuery.ajax(
					{
						url: "/listEnrollment.gtpl"
						, type: "GET"
						, data: jQuery.param(
								{
									isLookup: "Y"
									, limit: 20
									, offset: 0
									, studentId: jQuery( this ).text()
									, schoolName: paymentHeaderForm.find( ".list_payment_school_name" ).val()
									, enrollTermNo: paymentHeaderForm.find( ".list_payment_enroll_term_no" ).val()
									, enrollTermYear: paymentHeaderForm.find( ".list_payment_enroll_term_year" ).val()
								}
							)
						, beforeSend: function() {
								listPaymentCancel.addClass( "list_payment_disabled_button" );
								listPaymentStudentId.hide();
								listPaymentStudentIdWait.show();
								listPaymentDelete.addClass( "list_payment_disabled_button" );
								listPaymentEdit.addClass( "list_payment_disabled_button" );
								listPaymentSave.addClass( "list_payment_disabled_button" );
							}
						, complete: function() {
								listPaymentCancel.removeClass( "list_payment_disabled_button" );
								listPaymentStudentId.show();
								listPaymentStudentIdWait.hide();
								listPaymentDelete.removeClass( "list_payment_disabled_button" );
								listPaymentEdit.removeClass( "list_payment_disabled_button" );
								listPaymentSave.removeClass( "list_payment_disabled_button" );
							}
						, success: function( data ) {
								var dialogEnrollmentLookup = jQuery( ".dialog_enrollment_lookup" ).dialog( "open" );
								dialogEnrollmentLookup.children().remove();
								dialogEnrollmentLookup.append( data );
							}
						, error: function( jqXHR, textStatus, errorThrown ) {
								alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
							}
					}
				);
			}
	);
	
	jQuery( ".payment_list h3" ).find( "input" ).off( "keydown" ).keydown(
			function( event ) {
				event.stopPropagation();
			}
	);
}

/*
 * Initialize some School list elements:
 * 1. Edit Button.
 * 2. Delete Button.
 * 3. School Delete action.
 * 4. School list table row's highlight on focus.
 */
function initSchoolList() {
	if( navigator.userAgent.match( /(Firefox)/i ) ) {
		jQuery( ".list_school_add_class" ).css( "position", "relative" ).css( "top", "3px" );

		jQuery( ".list_school_add_class_button" ).css( "top", "1px" );

		jQuery( ".list_school_add_class_button_wait" ).css( "top", "4px" );
	}

	/* Initialize the New Class Text Field. Refer to /js/fieldInit.js. */
	initTextFieldValueWithName( ".list_school_add_class", jQuery( ".list_school_add_class" ).val() );
	
	jQuery( ".list_school_classes_col" ).find( "input:checkbox" ).each(
		function() {
			var checkboxId = jQuery( this ).attr( "id" );
			var newCheckboxId = "list_school" + ( jQuery( this ).parent().parent().parent().index() + 1 ) + checkboxId.substring( checkboxId.indexOf( "_class" ) );
			jQuery( this ).attr( "id", newCheckboxId );
			jQuery( this ).next().attr( "for", newCheckboxId );
		}
	);

	/* Initialize the Add School Class Button. */
	jQuery( ".list_school_add_class_button" ).button(
			{
				icons: {
					primary: "ui-icon-plus"
				}
			, text: false
			}
	);

	var listSchoolClassesCheckboxes = jQuery( ".school_list .list_school_classes_col input:checkbox" ).each(
		function() {
			if( !jQuery( this ).next().hasClass( "ui-button" ) ) {
				jQuery( this ).off( "change" ).change(
					function() {
						var listSchoolClassesCol = jQuery( this ).parent();
						var listSchoolClassControlsSpan = listSchoolClassesCol.find( ".list_school_class_controls span" );
						
						if( listSchoolClassesCol.find( "input:checkbox:checked" ).length == 0 ) {
							listSchoolClassControlsSpan.addClass( "list_school_disabled" );
						}
						else {
							listSchoolClassControlsSpan.removeClass( "list_school_disabled" );
						}
					}
				).button( { disabled: true } );
			}
		}
	)
	
	listSchoolClassesCheckboxes.next().css( "opacity", "1" );

	jQuery( ".list_school_cancel" ).button(
			{
				icons: {
					primary: "ui-icon-cancel"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				if( !jQuery( this ).hasClass( "list_school_disabled" ) ) {
					var schoolListItemTr = jQuery( this ).parent().parent().parent();
					
					jQuery.ajax(
							{
								url: "SchoolController.groovy"
								, type: "GET"
								, data: jQuery.param(
										{
											action: "getSchoolListItem"
											, schoolName: schoolListItemTr.find( ".list_school_name_col" ).text()
											, schoolIndex: schoolListItemTr.index() + 1
										}
									)
								, beforeSend: function() {
										schoolListItemTr.find( ".list_school_save, .list_school_cancel" ).hide();
										schoolListItemTr.find( ".list_school_save_wait" ).show();
									}
								, complete: function() {
										schoolListItemTr.find( ".list_school_save_wait" ).hide();
									}
								, success: function( data ) {
										toggleSchoolListItemElements( schoolListItemTr );
										schoolListItemTr.find( ".list_school_classes_col" ).html( jQuery( data ).find( ".list_school_classes_col" ).html() );
										initSchoolList();
									}
								, error: function() {
										
										/* Display an error message popup if the AJAX call returned an error. */
										if( jqXHR.getResponseHeader( "Response-Phrase" ) == null ) {
											alert( "We're sorry. Our server is not responding. Please press the Cancel button again.");
										}
										else {
											alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
										}
										
										schoolListItemTr.find( ".list_school_save, .list_school_cancel" ).show();
									}
							}
					);
				}
			}
	);
	
	jQuery( ".list_school_class_controls span" ).off( "hover" ).hover(
		function() {
			if( !jQuery( this ).hasClass( "list_school_disabled" ) ) {
				jQuery( this ).removeClass( "glyphicons" ).addClass( "glyphicons_pale" );
			}
		}
		, function() {
			if( !jQuery( this ).hasClass( "list_school_disabled" ) ) {
				jQuery( this ).removeClass( "glyphicons_pale" ).addClass( "glyphicons" );
			}
		}
	);
	
	/* Bind a click event to the Class Control Decrement Class Level buttons */
	  jQuery( ".list_school_decrement_class_level" ).off( "click" ).click(
	  	function() {
	  		if( !jQuery( this ).hasClass( "list_school_disabled" ) ) {
	  			jQuery( this ).parent().parent().parent().find( "input:checkbox:checked" ).each(
	  				function( index ) {
	  					var checkboxLabel = jQuery( this ).next();
	  					var hiddenInput = checkboxLabel.next();
	  					var hiddenInputName = hiddenInput.attr( "name" );
	  					var prevHiddenInput = jQuery( this ).prev();
	  					var prevCheckbox = prevHiddenInput.prev().prev();
	  					
	  					if( jQuery( this ).index() > 0 && prevCheckbox.attr( "type" ) == "checkbox" && !prevCheckbox.prop( "checked" ) ) {
	  						var checkboxId = jQuery( this ).attr( "id" );
	  						
	  						jQuery( this ).attr( "id", prevCheckbox.attr( "id" ) );
	  						checkboxLabel.attr( "for", prevCheckbox.attr( "id" ) );
	  						hiddenInput.attr( "name", prevHiddenInput.attr( "name" ) );
	  						
	  						prevCheckbox.attr( "id", checkboxId );
	  						prevCheckbox.next().attr( "for", checkboxId );
	  						prevHiddenInput.attr( "name", hiddenInputName );
	  						
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
	  jQuery( ".list_school_delete_class" ).off( "click" ).click(
	  	function() {
	  		if( !jQuery( this ).hasClass( "list_school_disabled" ) ) {
	  			var listSchoolDeleteClass = jQuery( this );
	  			var listSchoolClassesCol = jQuery( this ).parent().parent().parent();
	  			var schoolListTr = listSchoolClassesCol.parent().parent();
	  			
		  		listSchoolClassesCol.find( "input:checkbox:checked" ).each(
		  			function() {
		  				var checkbox = jQuery( this );
			  			var checkboxLabel = jQuery( this ).next();
			  			
			  			jQuery.ajax(
							{
								url: "StringEscapeUtilsController.groovy"
								, type: "POST"
								, data: jQuery.param(
											{
												"string": checkboxLabel.text()
												, method: "escapeHtml4"
											}
									)
								, beforeSend: function() {
										listSchoolDeleteClass.removeClass( "glyphicons_pale" ).addClass( "glyphicons" );
					  			  		
					  			  		listSchoolClassesCol.find( ".list_school_class_controls span" ).addClass( "list_school_disabled" );
					  			  		
					  			  		schoolListTr.find( ".list_school_save, .list_school_cancel" ).addClass( "list_school_disabled" );
									}
								, success: function( data ) {
										listSchoolClassesCol.find( "form[name='edit_school_form']" ).append( '<input name="delete' + data + '" type="hidden" value="' + data + '" />' );
										checkboxLabel.next().remove();
							  			checkboxLabel.remove();
							  			checkbox.remove();
							  			
							  			if( listSchoolClassesCol.find( "input:checkbox:checked" ).length == 0 ) {
							  				listSchoolClassesCol.find( "input:checkbox" ).each(
						  			  			function( index ) {
						  			  				var schoolIndex = listSchoolClassesCol.parent().parent().index() + 1;
						  			  				var existingCheckboxIndex = index + 1;
						  			  				var existingCheckboxId = "list_school" + schoolIndex + "_class" + existingCheckboxIndex;
						  			  				var existingCheckboxLabel = jQuery( this ).next();
						  			  				var existingHiddenInput = existingCheckboxLabel.next();
						  			  				
						  			  				jQuery( this ).attr( "id", existingCheckboxId );
						  			  				existingCheckboxLabel.attr( "for", existingCheckboxId );
						  			  				
						  			  				if( existingHiddenInput.attr( "name" ).substring( 0, 3) == "new" ) {
						  			  					existingHiddenInput.attr( "name", "newClass" + existingCheckboxIndex );
						  			  				}
						  			  				else {
						  			  					existingHiddenInput.attr( "name", "class" + existingCheckboxIndex );
						  			  				}
						  			  			}
						  			  		);
							  				
							  				schoolListTr.find( ".list_school_save, .list_school_cancel" ).removeClass( "list_school_disabled" );
							  			}
									}
								, error: function( jqXHR, textStatus, errorThrown ) {
	
										/* Display an error message popup if the AJAX call returned an error. */
										if( jqXHR.getResponseHeader( "Response-Phrase" ) == null ) {
											alert( "We're sorry. Our server is not responding. Please delete the classes again.");
										}
										else {
											alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
										}
										
										listSchoolClassesCol.find( ".list_school_class_controls span" ).removeClass( "list_school_disabled" );
										schoolListTr.find( ".list_school_save, .list_school_cancel" ).removeClass( "list_school_disabled" );
									}
							}
						);
		  			}
		  		);
	  		}
	  	}
	  );
	  
	  jQuery( ".list_school_delete" ).button(
			{
				icons: {
						primary: "ui-icon-trash"
					}
				, text: false
			}
	).off( "click" ).click(
			function() {
				if( confirm( "Do you really want to delete the " + jQuery( this ).parent().parent().prev().prev().prev().find( "div" ).text() + " School?" ) ) {
					var schoolListItem = jQuery( this ).parent().parent().parent();

					jQuery.ajax(
							{
								url: "SchoolController.groovy"
								, type: "POST"
								, data: jQuery( this ).parent().serialize()
								, beforeSend: function() {
										schoolListItem.find( ".list_school_delete" ).hide();
										schoolListItem.find( ".list_school_edit" ).hide();
										schoolListItem.find( ".list_school_save_wait" ).show();
									}
								, success: function( data ) {
										schoolListItem.remove();
										initSchoolList();
									}
								, error: function( jqXHR, textStatus, errorThrown ) {
										alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
										schoolListItem.find( ".list_school_delete" ).show();
										schoolListItem.find( ".list_school_edit" ).show();
										schoolListItem.find( ".list_school_save_wait" ).hide();
									}
							}
					);
				}
			}
	);

	jQuery( ".list_school_edit" ).button(
			{
				icons: {
					primary: "ui-icon-pencil"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				var schoolListItemTr = jQuery( this ).parent().parent();
				var listSchoolClassesCol = schoolListItemTr.find( ".list_school_classes_col" );
				
				schoolListItemTr.find( ".list_school_save, .list_school_cancel" ).toggle();

				listSchoolClassesCol.find( "input:checkbox" ).button( "enable" ).button( "option", "icons"
						, {
							secondary: "ui-icon-wrench"
						}
				);

				toggleSchoolListItemElements( schoolListItemTr );
			}
	);
	
	/* Bind a click event to the Class Control Increment Class Level buttons */
	  jQuery( ".list_school_increment_class_level" ).off( "click" ).click(
	  	function() {
	  		if( !jQuery( this ).hasClass( "list_school_disabled" ) ) {
	  			var listSchoolClassesColCheckboxes = jQuery( this ).parent().parent().parent().find( "input:checkbox" );
	  			
	  			listSchoolClassesColCheckboxes.each(
	  				function( index ) {
	  					var checkboxIndex = listSchoolClassesColCheckboxes.length - ( index + 1 );
	  					var checkbox = listSchoolClassesColCheckboxes.eq( checkboxIndex );
	  					var checkboxLabel = checkbox.next();
	  					var hiddenInput = checkboxLabel.next();
	  					var hiddenInputName = hiddenInput.attr( "name" );
	  					var nextCheckbox = checkbox.next().next().next();
	  					var nextHiddenInput = nextCheckbox.next().next();
	  					
	  					if( checkbox.prop( "checked" ) && checkbox.index() < ( listSchoolClassesColCheckboxes.length * 3 - 3 ) && nextCheckbox.attr( "type" ) == "checkbox" && !nextCheckbox.prop( "checked" ) ) {
	  						var checkboxId = checkbox.attr( "id" );
	  						var nextCheckboxId = nextCheckbox.attr( "id" );
	  						
	  						checkbox.attr( "id", nextCheckbox.attr( "id" ) );
	  						checkboxLabel.attr( "for", nextCheckbox.attr( "id" ) );
	  						hiddenInput.attr( "name", nextHiddenInput.attr( "name" ) );
	  						
	  						nextCheckbox.attr( "id", checkboxId );
	  						nextCheckbox.next().attr( "for", checkboxId );
	  						nextHiddenInput.attr( "name", hiddenInputName );
	  						
	  						nextHiddenInput.after( hiddenInput );
	  						nextHiddenInput.after( checkboxLabel );
	  						nextHiddenInput.after( checkbox );
	  					}
	  				}
	  			);
	  		}
	  	}
	  );

	jQuery( ".list_school_save" ).button(
			{
				icons: {
					primary: "ui-icon-disk"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				
				if( !jQuery( this ).hasClass( "list_school_disabled" ) ) {
					var schoolListItemTr = jQuery( this ).parent().parent();
					var listSchoolClassesCol = schoolListItemTr.find( ".list_school_classes_col" );
					var listSchoolClassesColForm = listSchoolClassesCol.find( "form" );
					
					listSchoolClassesColForm.append( listSchoolClassesCol.children( "input:hidden" ).clone() );
					
					jQuery.ajax(
							{
								url: "SchoolController.groovy"
								, type: "POST"
								, data: schoolListItemTr.find( "form[name='edit_school_form']" ).serialize()
								, beforeSend: function() {
										schoolListItemTr.find( ".list_school_save, .list_school_cancel, .list_school_save_wait" ).toggle();
									}
								, complete: function() {
									schoolListItemTr.find( ".list_school_save_wait" ).toggle();
								}
								, success: function( data ) {
									toggleSchoolListItemElements( schoolListItemTr );
									listSchoolClassesCol.html( jQuery( data ).find( ".list_school_classes_col" ).html() );
									initSchoolList();
								}
								, error: function( jqXHR, textStatus, errorThrown ) {
									listSchoolClassesColForm.find( "input[name^='newClass']" ).remove();
									listSchoolClassesColForm.find( "input[name^='class']" ).remove();
									schoolListItemTr.find( ".list_school_save, .list_school_cancel" ).toggle();
									alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
								}
							}
					);
				}
			}
	);

	/*
	 * Turn off the hovers previously bound to School list's rows, before binding the existing rows and the unbound new rows. Then, highlight the associated
	 * School record when the cursor hovers on a School list's row.
	 */
	if( navigator.userAgent.match( /(iPad)|(iPhone)|(iPod)|(android)|(webOS)/i ) ) {
		jQuery( ".school_list tr" ).removeClass( "school_list_tr_hover" ).addClass( "school_list_tr" );
	}
	
	jQuery( ".school_list tr" ).off( "hover" ).hover(
			function() {
				jQuery( this ).removeClass( "school_list_tr" ).addClass( "school_list_tr_hover" );
			}
			, function() {
				jQuery( this ).removeClass( "school_list_tr_hover" ).addClass( "school_list_tr" );
			}
	);

	/* Bind a function to the click event of the Add School Class Button */
	jQuery( ".list_school_add_class_button" ).off( "click" ).click(
			function() {
				var newClassSeq = jQuery( this ).parent().parent().find( "input:checkbox" ).size() + 1;

				/*
				 * To the placeholder for Classes associated to the School:
				 * 1. Add a button for the New Class to display the added Class to the user.
				 * 2. Add an input HTML element so that the new School form can save the new Class information through the SchoolController.
				 */

				var schoolListItemTr = jQuery( this ).parent().parent().parent().parent();

				jQuery.ajax(
						{
							url: "StringEscapeUtilsController.groovy"
							, type: "POST"
							, data: jQuery( this ).parent().serialize() + "&method=escapeHtml4"
							, beforeSend: function() {

									/* Switch the "+" button with a spinning circle ball. */
									schoolListItemTr.find( ".list_school_add_class_button" ).toggle();
									schoolListItemTr.find( ".list_school_add_class_button_wait" ).toggle();
	
									/* Disable the Save and Cancel buttons. */
									schoolListItemTr.find( ".list_school_save" ).addClass( "list_school_disabled" );
									schoolListItemTr.find( ".list_school_cancel" ).addClass( "list_school_disabled" );
								}
							, complete: function() {
	
									/* Switch the "+" button with a spinning circle ball. */
									schoolListItemTr.find( ".list_school_add_class_button" ).toggle();
									schoolListItemTr.find( ".list_school_add_class_button_wait" ).toggle();
		
									/* Enable the Save and Cancel buttons. */
									schoolListItemTr.find( ".list_school_save" ).removeClass( "list_school_disabled" );
									schoolListItemTr.find( ".list_school_cancel" ).removeClass( "list_school_disabled" );
								}
							, success: function( data ) {
									var schoolListItemTrIndex = schoolListItemTr.index() + 1;
									var checkboxId = "list_school" + schoolListItemTrIndex + "_class" + newClassSeq;
									
									schoolListItemTr.find( ".list_school_classes_col" ).append(
											'<input id="' + checkboxId + '" type="checkbox" />' +
											'<label for="' + checkboxId + '">' + data + '</label>' +
											'<input name="newClass' + newClassSeq + '" type="hidden" value="' + data + '" />'
									);
		
									/* Add a Wrench icon to the newly added Class, so that the user gets the cue that he/she can modify the New Class. */
									schoolListItemTr.find( "#" + checkboxId ).button(
											{
												icons: {
													secondary: "ui-icon-wrench"
												}
											}
									).change(
		
											/* Enable/Disable the Class Control buttons */
											function() {
												var listSchoolClassesCol = jQuery( this ).parent();
												var listSchoolClassControlsSpan = listSchoolClassesCol.find( ".list_school_class_controls span" );
												
												if( listSchoolClassesCol.find( "input:checkbox:checked" ).length == 0 ) {
													listSchoolClassControlsSpan.addClass( "list_school_disabled" );
												}
												else {
													listSchoolClassControlsSpan.removeClass( "list_school_disabled" );
												}
											}
									);
								}
							, error: function( jqXHR, textStatus, errorThrown ) {
	
									/* Display an error message popup if the AJAX call returned an error. */
									if( jqXHR.getResponseHeader( "Response-Phrase" ) == null ) {
										alert( "We're sorry. Our server is not responding. Please re-add the New Class.");
									}
									else {
										alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
									}
								}
						}
				);
			}
	);
}

/*
 * Initialize some Student list elements:
 * 1. Edit Button.
 * 2. Delete Button.
 * 3. Student Delete action.
 */
function initStudentList() {
	var dialogListParent = jQuery( ".dialog_list_parent" );
	
	jQuery( "input.list_student_birth_date" ).datepicker(
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

	/* Turn off the clicks previously bound to Student list's cancel buttons, before binding the existing buttons and the unbound new buttons. */
	jQuery( ".list_student_cancel" ).button(
			{
				icons: {
					primary: "ui-icon-cancel"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				var studentListItemHeader = jQuery( this ).parent().parent().prev();
				var studentListItemDetails = jQuery( this ).parent().parent();

				studentListItemHeader.find( ".list_student_enrollment_period_lookup" ).css( "left", "885px" );
				studentListItemHeader.find( ".list_student_enrollment_period_lookup_wait" ).css( "left", "898px" );
				studentListItemDetails.find( ".list_student_save" ).toggle();
				studentListItemDetails.find( ".list_student_cancel" ).toggle();
				studentListItemDetails.find( "select[name^='classAttended']" ).remove();
				studentListItemDetails.find( "input[name^='boardingInd']" ).remove();
				toggleStudentListItemElements( studentListItemHeader, studentListItemDetails );
			}
	);

	/* Show the Class Attended Dialog if the user clicks "Create a New Student" when matching Students are found */
	jQuery( ".list_student_create_link" ).off( "click" ).click(
			function() {
				jQuery( ".add_student_matching_list_student" ).dialog( "close" );
				
				openAddStudentDialogClassAttended();
			}
	);
	
	/* Turn off the clicks previously bound to Student list's delete buttons, before binding the existing buttons and the unbound new buttons. */
	jQuery( ".list_student_delete" ).button(
			{
				icons: {
					primary: "ui-icon-trash"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				if( confirm( "Do you really want to delete this Student?" ) ) {
					var studentList = jQuery( ".list_record_form .student_list" );
					var studentToDeleteDiv = jQuery( this ).parent().parent();

					jQuery.ajax(
							{
								url: "StudentController.groovy"
								, type: "POST"
								, data: jQuery( this ).parent().serialize() + "&action=delete&nextTwentyOffset=" + jQuery( ".list_record_form .list_student_next_twenty_offset" ).text()
								, beforeSend: function() {
										studentToDeleteDiv.find( ".list_student_delete" ).hide();
										studentToDeleteDiv.find( ".list_student_edit" ).hide();
										studentToDeleteDiv.find( ".list_student_save_wait" ).show();
									}
								, success: function( data ) {
										studentList.toggle();
										studentToDeleteDiv.prev( "h3" ).remove();
										studentToDeleteDiv.remove();
	
										/* Insert the replacing Student to the bottom of the Student list accordion. */
										studentList.append( data )
	
										/* Refer to /js/listInit.js. */
										initStudentList();
	
										if( studentList.children( "h3" ).length < 20 )
											jQuery( ".list_record_form .list_student_next_twenty" ).css( "display", "none" );
	
										studentList.accordion( "refresh" ).accordion( "option", "active", false );
										studentList.toggle();
									}
								, error: function( jqXHR, textStatus, errorThrown ) {
										alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
										studentToDeleteDiv.find( ".list_student_delete" ).show();
										studentToDeleteDiv.find( ".list_student_edit" ).show();
										studentToDeleteDiv.find( ".list_student_save_wait" ).hide();
									}
							}
					);
				}
			}
	);

	/* Turn off the clicks previously bound to Student list's edit buttons, before binding the existing buttons and the unbound new buttons. */
	jQuery( ".list_student_edit" ).button(
			{
				icons: {
					primary: "ui-icon-pencil"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				var studentListItemHeader = jQuery( this ).parent().parent().prev();
				var studentListItemDetails = jQuery( this ).parent().parent();

				/* Toggle the display elements with editable elements. */
				studentListItemDetails.find( ".list_student_save" ).toggle();
				studentListItemDetails.find( ".list_student_cancel" ).toggle();
				toggleStudentListItemElements( studentListItemHeader, studentListItemDetails );

				var classesAttendedHyphen = studentListItemHeader.find( ".list_student_classes_attended_hyphen" );

				var firstClassList = studentListItemHeader.find( "select.list_student_first_class_attended" );

				var isAndroid = navigator.userAgent.match( /(android)/i );
				var isIOS = navigator.userAgent.match( /(iPad)|(iPhone)|(iPod)|(webOS)/i );
				var isMacFirefox = navigator.userAgent.match( /(Firefox)/i ) && navigator.platform.match( /(Mac)/i );
				var isWinSafari = navigator.userAgent.match( /(Safari)/i ) && !navigator.userAgent.match( /(Chrome)/i ) && navigator.platform.match( /(Win)/i );
				var listBoxSpacer = 5;

				if( isAndroid ) {
					listBoxSpacer = 35;
				}
				else if( isMacFirefox || isWinSafari ) {
					listBoxSpacer = 29;
				}
				else if( isIOS ) {
					listBoxSpacer = 25;
				}

				classesAttendedHyphen.css( "left", ( firstClassList.position().left + firstClassList.width() + listBoxSpacer ) + "px" );

				var lastClassList = studentListItemHeader.find( "select.list_student_last_class_attended" );

				lastClassList.css( "left", ( classesAttendedHyphen.position().left + 10 ) + "px" );
				
				studentListItemHeader.find( ".list_student_last_class_attended_required_ind" ).css( "left", ( classesAttendedHyphen.position().left + 1 ) + "px" );

				/* Assign edit field values based on their existing values */
				studentListItemHeader.find( "input.list_student_first_name" ).val( studentListItemHeader.find( "div.list_student_first_name" ).text() );
				studentListItemHeader.find( "input.list_student_last_name" ).val( studentListItemHeader.find( "div.list_student_last_name" ).text() );
				studentListItemDetails.find( "input.list_student_birth_date" ).val( studentListItemDetails.find( "div.list_student_birth_date" ).text() );
				studentListItemDetails.find( "input.list_student_village" ).val( studentListItemDetails.find( "div.list_student_village" ).text() );
				studentListItemDetails.find( "textarea.list_student_special_info" ).val( studentListItemDetails.find( "div.list_student_special_info" ).text().replace( "<br>", "\n" ) );
				studentListItemDetails.find( "textarea.list_student_leave_reason" ).val( studentListItemDetails.find( "div.list_student_leave_reason" ).text().replace( "<br>", "\n" ) );

				/* Select the First Class Attended value based on the existing value. */
				firstClassList.find( "option:selected" ).prop( "selected", false );
				firstClassList.find( 'option[value="' + studentListItemHeader.find( "span.list_student_first_class_attended" ).text() + '"]' ).prop( "selected", true );

				/* Select the Last Class Attended value based on the existing value. */
				lastClassList.find( "option:selected" ).prop( "selected", false );
				lastClassList.find( 'option[value="' + studentListItemHeader.find( "span.list_student_last_class_attended" ).text() + '"]' ).prop( "selected", true );

				/* Select the Leave Term value based on the existing value. */
				var leaveTermList = studentListItemHeader.find( "select.list_student_leave_term" )

				leaveTermList.find( "option:selected" ).prop( "selected", false );
				leaveTermList.find( 'option[value="' + studentListItemHeader.find( "span.list_student_leave_term" ).text() + '"]' ).prop( "selected", true );
				
				studentListItemHeader.find( ".list_student_enrollment_period_lookup" ).css( "left", ( leaveTermList.position().left + leaveTermList.width() + listBoxSpacer ) + "px" );
				studentListItemHeader.find( ".list_student_enrollment_period_lookup_wait" ).css( "left", ( leaveTermList.position().left + leaveTermList.width() + listBoxSpacer + 13 ) + "px" );

				/* Select the Gender value based on the existing value. */
				var genderList = studentListItemDetails.find( "select.list_student_gender" );

				genderList.find( "option:selected" ).prop( "selected", false );
				genderList.find( 'option[value="' + studentListItemDetails.find( "div.list_student_gender" ).text() + '"]' ).prop( "selected", true );

				/* Check the Parent Deceased checkbox based on the existing value. */
				studentListItemDetails.find( ".list_student_parent_deceased_toggle" ).each(
						function( index ) {
							if( jQuery( this ).prevAll( ".list_student_parent_deceased_ind" ).text() == "Y" ) {
								jQuery( this ).prop( "checked", true );
							}
							else {
								jQuery( this ).prop( "checked", false );
							}
							
							jQuery( this ).change();
						}
				);

				/* Select the Leave Reason Category value based on the existing value. */
				var leaveReasonCategoryList = studentListItemDetails.find( "select.list_student_leave_reason_category" );

				leaveReasonCategoryList.find( "option:selected" ).prop( "selected", false );
				leaveReasonCategoryList.find( 'option[value="' + studentListItemDetails.find( "div.list_student_leave_reason_category" ).text() + '"]' ).prop( "selected", true );

				jQuery( ".list_record_form .student_list" ).accordion( "option", "collapsible", false );
			}
	);
	
	jQuery( ".list_student_enrollment_period_lookup" ).button(
			{
				icons: {
					primary: "ui-icon-extlink"
				}
			, text: false
			}
	).off( "click" ).click(
			function( event ) {
				event.stopPropagation();
				
				var action
				var listStudentEnrollmentPeriodLookup = jQuery( this );
				var studentListH3 = listStudentEnrollmentPeriodLookup.parents( "h3" );
				var listStudentDetails = studentListH3.next();
				var studentId = listStudentDetails.find( ".list_student_id" ).text().substring( 2 );
				var enrollmentTerm = studentListH3.find( ".list_student_enrollment_term" ).text();
				var leaveTerm;
				var leaveTermNo;
				var leaveTermYear;
				var listStudentBoardingFeesLabel = listStudentDetails.find( ".list_student_boarding_fees_label" );
				var listStudentEnrollmentPeriodLookupWait = jQuery( this ).parent().find( ".list_student_enrollment_period_lookup_wait" );
				var listStudentFirstClassAttended = studentListH3.find( "select.list_student_first_class_attended:visible" );
				var listStudentLastClassAttended = studentListH3.find( "select.list_student_last_class_attended:visible" );
				var listStudentTuitionFeesLabel = listStudentDetails.find( ".list_student_tuition_fees_label" );
				
				if( listStudentFirstClassAttended.length == 0 ) {
					action = "view";
					leaveTerm = studentListH3.find( "span.list_student_leave_term" ).text();
				}
				else {
					action = "edit";
					leaveTerm = studentListH3.find( "select.list_student_leave_term" ).val();
					
					if( leaveTerm == "Leave Term" ) {
						leaveTerm = "now";
					}
				}
				
				if( listStudentEnrollmentPeriodLookupWait.length == 0 ) {
					listStudentEnrollmentPeriodLookupWait = listStudentDetails.find( ".list_student_enrollment_period_lookup_wait" );
				}
				
				if( leaveTerm != "now" ) {
					leaveTermNo = leaveTerm.substring( 10 );
					leaveTermYear = leaveTerm.substring( 0, 4 );
				}
				
				jQuery.ajax(
						{
							url: "/dialogClassAttended.gtpl"
							, type: "GET"
							, data: jQuery.param(
									{
										action: action
										, studentId: studentId
										, termSchool: listStudentDetails.find( ".list_student_school" ).text()
										, enrollTermNo: enrollmentTerm.substring( 10 )
										, enrollTermYear: enrollmentTerm.substring( 0, 4 )
										, leaveTermNo: leaveTermNo
										, leaveTermYear: leaveTermYear
										, firstClassAttended: listStudentFirstClassAttended.val()
										, lastClassAttended: listStudentLastClassAttended.val()
									}
								)
							, beforeSend: function() {
									listStudentBoardingFeesLabel.fadeOut();
									listStudentEnrollmentPeriodLookup.fadeOut();
									listStudentEnrollmentPeriodLookupWait.show();
									listStudentTuitionFeesLabel.fadeOut();
								}
							, complete: function() {
									listStudentBoardingFeesLabel.fadeIn();
									listStudentEnrollmentPeriodLookup.fadeIn();
									listStudentEnrollmentPeriodLookupWait.hide();
									listStudentEnrollmentPeriodLookup.after( listStudentEnrollmentPeriodLookupWait );
									listStudentEnrollmentPeriodLookupWait.css( "left", listStudentEnrollmentPeriodLookup.position().left + 13 + "px" ).css( "top", "12px" );
									listStudentTuitionFeesLabel.fadeIn();
								}
							, success: function( data ) {
									var dialogClassAttended = jQuery( ".dialog_class_attended" );
									var savingEditedStudent = false;
									
									dialogClassAttended.dialog( "option", "title", "Classes Attended by " + studentListH3.find( ".list_student_first_name" ).text() + " " + studentListH3.find( ".list_student_last_name" ).text() + " (" + studentId + ") from " + enrollmentTerm + " to " + leaveTerm );
									dialogClassAttended.dialog( "open" );
									dialogClassAttended.children().remove();
									dialogClassAttended.append( data );
									
									var listStudentDetailsForm = listStudentDetails.find( "form" );
									
									var classAttendedSelects = listStudentDetailsForm.find( "select[name^='classAttended']" ).each(
											function() {
												var dialogClassAttendedSelect = dialogClassAttended.find( "select[name='" + jQuery( this ).attr( "name" ) + "']" );
												dialogClassAttendedSelect.find( "option" ).prop( "selected", false );
												dialogClassAttendedSelect.find( "option[value='" + jQuery( this ).val() + "']" ).prop( "selected", true );
											}
									);
									
									var boardingIndInputs = listStudentDetailsForm.find( "input[name^='boardingInd']" ).each(
											function() {
												dialogClassAttended.find( "input[name='" + jQuery( this ).attr( "name" ) + "']" ).prop( "checked", jQuery( this ).prop( "checked" ) );
											}
									);
									
									if( listStudentEnrollmentPeriodLookupWait.position().top == 48 ) {
										savingEditedStudent = true;
										
										listStudentDetails.find( ".list_student_save" ).toggle();
										listStudentDetails.find( ".list_student_cancel" ).toggle();
										listStudentDetails.find( ".list_student_save_wait" ).toggle();
									}
									
									jQuery( ".dialog_class_attended_save_link_bottom, .dialog_class_attended_save_link_top" ).click(
										function() {
											classAttendedSelects.remove();
											boardingIndInputs.remove();
											
											dialogClassAttended.find( "input, select" ).each(
												function() {
													jQuery( this ).fadeOut();
													listStudentDetailsForm.append( jQuery( this ) );
												}
											);
											
											if( savingEditedStudent ) {
												listStudentEnrollmentPeriodLookup.fadeOut();
												listStudentTuitionFeesLabel.fadeOut();
												listStudentBoardingFeesLabel.fadeOut();
												listStudentDetails.find( ".list_student_save" ).hide();
												listStudentDetails.find( ".list_student_save_wait" ).show();
												listStudentDetails.find( ".list_student_cancel" ).hide();
											}
											
											dialogClassAttended.dialog( "close" );
											
											if( savingEditedStudent ) {
												saveEditedStudent( studentListH3, listStudentDetails );
											}
										}
									);
								}
							, error: function( jqXHR, textStatus, errorThrown ) {
									alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
								}
						}
				);
			}
	);
	
	jQuery( "a.list_student_birth_date_label" ).off( "click" ).click(
			function( event ) {
				var studentBirthDateFilterSortByDialog = jQuery( ".student_birth_date_filter_sortby_dialog" );
				
				if( studentBirthDateFilterSortByDialog.dialog( "isOpen" ) ) {
					studentBirthDateFilterSortByDialog.dialog( "close" );
				}
				else {
					studentBirthDateFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					studentBirthDateFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-45 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					studentBirthDateFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					studentBirthDateFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( ".list_student_boarding_fees_label" ).off( "click" ).click(
			function( event ) {
				var listStudentBoardingFeesLabel = jQuery( this );
				var listStudentDetails = listStudentBoardingFeesLabel.parent().parent();
				var studentBoardingFeesFilterSortByDialog = jQuery( ".student_boarding_fees_filter_sortby_dialog" );
				
				if( studentBoardingFeesFilterSortByDialog.dialog( "isOpen" ) ) {
					studentBoardingFeesFilterSortByDialog.dialog( "close" );
				}
				else {
					studentBoardingFeesFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					studentBoardingFeesFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-62 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					studentBoardingFeesFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					studentBoardingFeesFilterSortByDialog.find( ".student_filter_sortby_dialog_lookup_button" ).off( "click" ).click(
							function() {
								var studentListH3 = listStudentDetails.prev();
								var listStudentEnrollmentPeriodLookupWait = studentListH3.find( ".list_student_enrollment_period_lookup_wait" );
								
								listStudentBoardingFeesLabel.after( listStudentEnrollmentPeriodLookupWait );
								listStudentEnrollmentPeriodLookupWait.css( "left", "70px" ).css( "top", "300px" );
								
								studentListH3.find( ".list_student_enrollment_period_lookup" ).click();
								
								studentBoardingFeesFilterSortByDialog.dialog( "close" );
							}
					);
					
					if( jQuery( this ).parents( ".add_student_matching_list_student, .dialog_student_lookup" ).length == 0 ) {	
						studentBoardingFeesFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_button" ).show();
						studentBoardingFeesFilterSortByDialog.find( ".student_filter_sortby_dialog_sort_button" ).show();
						studentBoardingFeesFilterSortByDialog.dialog( "open" );
					}
					else {
						var studentFilterSortByDialogFilterButton = studentBoardingFeesFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_button" ).hide();
						studentBoardingFeesFilterSortByDialog.find( ".student_filter_sortby_dialog_sort_button" ).hide();
						
						if( studentFilterSortByDialogFilterButton.hasClass( "glyphicons_blue" ) ) {
							studentFilterSortByDialogFilterButton.removeClass( "glyphicons_blue" ).addClass( "glyphicons" );
							studentBoardingFeesFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_panel" ).hide();
						}
							
						studentBoardingFeesFilterSortByDialog.dialog( "open" );
							
						studentBoardingFeesFilterSortByDialog.animate(
								{
									height: "31px"
								}
						).parent().animate(
								{
									width: "150px"
								}
						);
					}
				}
			}
	);
	
	jQuery( "a.list_student_classes_attended_label" ).off( "click" ).click(
			function( event ) {
				var studentClassesAttendedFilterSortByDialog = jQuery( ".student_classes_attended_filter_sortby_dialog" );
				
				if( studentClassesAttendedFilterSortByDialog.dialog( "isOpen" ) ) {
					studentClassesAttendedFilterSortByDialog.dialog( "close" );
				}
				else {
					studentClassesAttendedFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					studentClassesAttendedFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-75 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					studentClassesAttendedFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					studentClassesAttendedFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_student_enrollment_period_label" ).off( "click" ).click(
			function( event ) {
				var studentEnrollmentPeriodFilterSortByDialog = jQuery( ".student_enrollment_period_filter_sortby_dialog" );
				
				if( studentEnrollmentPeriodFilterSortByDialog.dialog( "isOpen" ) ) {
					studentEnrollmentPeriodFilterSortByDialog.dialog( "close" );
				}
				else {
					studentEnrollmentPeriodFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					studentEnrollmentPeriodFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-75 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					studentEnrollmentPeriodFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					if( jQuery( ".student_enrollment_period_sort_direction" ).text() == "asc" ) {
						studentEnrollmentPeriodFilterSortByDialog.find( ".student_filter_sortby_dialog_asc_sort_direction_button" ).show();
						studentEnrollmentPeriodFilterSortByDialog.find( ".student_filter_sortby_dialog_dsc_sort_direction_button" ).hide();
					}
					else if( jQuery( ".student_enrollment_period_sort_direction" ).text() == "dsc" ) {
						studentEnrollmentPeriodFilterSortByDialog.find( ".student_filter_sortby_dialog_asc_sort_direction_button" ).hide();
						studentEnrollmentPeriodFilterSortByDialog.find( ".student_filter_sortby_dialog_dsc_sort_direction_button" ).show();
					}
					else {
						studentEnrollmentPeriodFilterSortByDialog.find( ".student_filter_sortby_dialog_asc_sort_direction_button" ).hide();
						studentEnrollmentPeriodFilterSortByDialog.find( ".student_filter_sortby_dialog_dsc_sort_direction_button" ).hide();
					}
					
					studentEnrollmentPeriodFilterSortByDialog.find( ".student_filter_sortby_dialog_sort_order" ).text( jQuery( ".student_enrollment_period_sort_order" ).text() );
					
					studentEnrollmentPeriodFilterSortByDialog.dialog( "open" );
				}
			}
	)
	
	jQuery( "a.list_student_fees_due_label" ).off( "click" ).click(
			function( event ) {
				var studentFeesDueFilterSortByDialog = jQuery( ".student_fees_due_filter_sortby_dialog" );
				
				if( studentFeesDueFilterSortByDialog.dialog( "isOpen" ) ) {
					studentFeesDueFilterSortByDialog.dialog( "close" );
				}
				else {
					studentFeesDueFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					studentFeesDueFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-40 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					studentFeesDueFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					studentFeesDueFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_student_first_name_label" ).off( "click" ).click(
			function( event ) {
				var studentFirstNameFilterSortByDialog = jQuery( ".student_first_name_filter_sortby_dialog" );
				
				if( studentFirstNameFilterSortByDialog.dialog( "isOpen" ) ) {
					studentFirstNameFilterSortByDialog.dialog( "close" );
				}
				else {
					studentFirstNameFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					studentFirstNameFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-45 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					studentFirstNameFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					if( jQuery( ".student_first_name_sort_direction" ).text() == "asc" ) {
						studentFirstNameFilterSortByDialog.find( ".student_filter_sortby_dialog_asc_sort_direction_button" ).show();
						studentFirstNameFilterSortByDialog.find( ".student_filter_sortby_dialog_dsc_sort_direction_button" ).hide();
					}
					else if( jQuery( ".student_first_name_sort_direction" ).text() == "dsc" ) {
						studentFirstNameFilterSortByDialog.find( ".student_filter_sortby_dialog_asc_sort_direction_button" ).hide();
						studentFirstNameFilterSortByDialog.find( ".student_filter_sortby_dialog_dsc_sort_direction_button" ).show();
					}
					else {
						studentFirstNameFilterSortByDialog.find( ".student_filter_sortby_dialog_asc_sort_direction_button" ).hide();
						studentFirstNameFilterSortByDialog.find( ".student_filter_sortby_dialog_dsc_sort_direction_button" ).hide();
					}
					
					studentFirstNameFilterSortByDialog.find( ".student_filter_sortby_dialog_sort_order" ).text( jQuery( ".student_first_name_sort_order" ).text() );
					
					studentFirstNameFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_student_gender_label" ).off( "click" ).click(
			function( event ) {
				var studentGenderFilterSortByDialog = jQuery( ".student_gender_filter_sortby_dialog" );
				
				if( studentGenderFilterSortByDialog.dialog( "isOpen" ) ) {
					studentGenderFilterSortByDialog.dialog( "close" );
				}
				else {
					studentGenderFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					studentGenderFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-35 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					studentGenderFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					studentGenderFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_student_id_label" ).off( "click" ).click(
			function( event ) {
				var studentIdFilterSortByDialog = jQuery( ".student_id_filter_sortby_dialog" );
				
				if( studentIdFilterSortByDialog.dialog( "isOpen" ) ) {
					studentIdFilterSortByDialog.dialog( "close" );
				}
				else {
					studentIdFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					studentIdFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-10 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					studentIdFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					studentIdFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_student_last_name_label" ).off( "click" ).click(
			function( event ) {
				var studentLastNameFilterSortByDialog = jQuery( ".student_last_name_filter_sortby_dialog" );
				
				if( studentLastNameFilterSortByDialog.dialog( "isOpen" ) ) {
					studentLastNameFilterSortByDialog.dialog( "close" );
				}
				else {
					studentLastNameFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					studentLastNameFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-45 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					studentLastNameFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					if( jQuery( ".student_last_name_sort_direction" ).text() == "asc" ) {
						studentLastNameFilterSortByDialog.find( ".student_filter_sortby_dialog_asc_sort_direction_button" ).show();
						studentLastNameFilterSortByDialog.find( ".student_filter_sortby_dialog_dsc_sort_direction_button" ).hide();
					}
					else if( jQuery( ".student_last_name_sort_direction" ).text() == "dsc" ) {
						studentLastNameFilterSortByDialog.find( ".student_filter_sortby_dialog_asc_sort_direction_button" ).hide();
						studentLastNameFilterSortByDialog.find( ".student_filter_sortby_dialog_dsc_sort_direction_button" ).show();
					}
					else {
						studentLastNameFilterSortByDialog.find( ".student_filter_sortby_dialog_asc_sort_direction_button" ).hide();
						studentLastNameFilterSortByDialog.find( ".student_filter_sortby_dialog_dsc_sort_direction_button" ).hide();
					}
					
					studentLastNameFilterSortByDialog.find( ".student_filter_sortby_dialog_sort_order" ).text( jQuery( ".student_last_name_sort_order" ).text() );
					
					studentLastNameFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_student_leave_reason_label" ).off( "click" ).click(
			function( event ) {
				var studentLeaveReasonFilterSortByDialog = jQuery( ".student_leave_reason_filter_sortby_dialog" );
				
				if( studentLeaveReasonFilterSortByDialog.dialog( "isOpen" ) ) {
					studentLeaveReasonFilterSortByDialog.dialog( "close" );
				}
				else {
					studentLeaveReasonFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					studentLeaveReasonFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-65 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					studentLeaveReasonFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					studentLeaveReasonFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( ".list_student_other_fees_label" ).off( "click" ).click(
			function( event ) {
				var listStudentOtherFeesLabel = jQuery( this );
				var studentOtherFeesFilterSortByDialog = jQuery( ".student_other_fees_filter_sortby_dialog" );
				
				if( studentOtherFeesFilterSortByDialog.dialog( "isOpen" ) ) {
					studentOtherFeesFilterSortByDialog.dialog( "close" );
				}
				else {
					studentOtherFeesFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					studentOtherFeesFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-48 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					studentOtherFeesFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					studentOtherFeesFilterSortByDialog.find( ".student_filter_sortby_dialog_lookup_button" ).off( "click" ).click(
							function() {
								var dialogListFee = jQuery( ".dialog_list_fee" );
								var listStudentDetails = listStudentOtherFeesLabel.parents( ".list_student_details" );
								var studentId = listStudentDetails.find( ".list_student_id" ).text().substring( 2 );
								var studentListH3 = listStudentDetails.prev();
								var listStudentEnrollmentTerm = studentListH3.find( ".list_student_enrollment_term" ).text();
								var listStudentOtherFeesWait = listStudentDetails.find( ".list_student_other_fees_wait" );

								jQuery.ajax(
										{
											url: "/listFee.gtpl"
											, type: "GET"
											, data: jQuery.param(
													{
														limit: 4
														, offset: 0
														, studentId: studentId
														, schoolName: listStudentDetails.find( ".list_student_school" ).text()
														, enrollTermNo: listStudentEnrollmentTerm.substring( 10 )
														, enrollTermYear: listStudentEnrollmentTerm.substring( 0, 4 )
													}
											)
											, beforeSend: function() {
												listStudentOtherFeesLabel.hide();
												listStudentOtherFeesWait.show();
												studentOtherFeesFilterSortByDialog.dialog( "close" );
											}
											, complete: function() {
												listStudentOtherFeesLabel.show();
												listStudentOtherFeesWait.hide();
											}
											, success: function( data ) {
												dialogListFee.dialog( "option", "title", "Other Fees for " + studentListH3.find( ".list_student_first_name" ).text() + " " + studentListH3.find( ".list_student_last_name" ).text() + " (" + studentId + ") from " + listStudentEnrollmentTerm + " to " + studentListH3.find( "span.list_student_leave_term" ).text() );
												dialogListFee.dialog( "open" );
												dialogListFee.children().remove();
												dialogListFee.append( data );
											}
											, error: function( jqXHR, textStatus, errorThrown ) {
												alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
											}
										}
								);
							}
					);
					
					if( jQuery( this ).parents( ".add_student_matching_list_student, .dialog_student_lookup" ).length == 0 ) {	
						studentOtherFeesFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_button" ).show();
						studentOtherFeesFilterSortByDialog.find( ".student_filter_sortby_dialog_sort_button" ).show();
						studentOtherFeesFilterSortByDialog.dialog( "open" );
					}
					else {
						var studentFilterSortByDialogFilterButton = studentOtherFeesFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_button" ).hide();
						studentOtherFeesFilterSortByDialog.find( ".student_filter_sortby_dialog_sort_button" ).hide();
						
						if( studentFilterSortByDialogFilterButton.hasClass( "glyphicons_blue" ) ) {
							studentFilterSortByDialogFilterButton.removeClass( "glyphicons_blue" ).addClass( "glyphicons" );
							studentOtherFeesFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_panel" ).hide();
						}
							
						studentOtherFeesFilterSortByDialog.dialog( "open" );
							
						studentOtherFeesFilterSortByDialog.animate(
								{
									height: "31px"
								}
						).parent().animate(
								{
									width: "150px"
								}
						);
					}
				}
			}
	);
	
	jQuery( ".list_student_payments_label" ).off( "click" ).click(
			function( event ) {
				var listStudentPaymentsLabel = jQuery( this );
				var studentPaymentsFilterSortByDialog = jQuery( ".student_payments_filter_sortby_dialog" );
				
				if( studentPaymentsFilterSortByDialog.dialog( "isOpen" ) ) {
					studentPaymentsFilterSortByDialog.dialog( "close" );
				}
				else {
					studentPaymentsFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					studentPaymentsFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-43 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					studentPaymentsFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					studentPaymentsFilterSortByDialog.find( ".student_filter_sortby_dialog_lookup_button" ).off( "click" ).click(
							function() {
								var dialogListPayment = jQuery( ".dialog_list_payment" );
								var listStudentDetails = listStudentPaymentsLabel.parents( ".list_student_details" );
								var studentId = listStudentDetails.find( ".list_student_id" ).text().substring( 2 );
								var studentListH3 = listStudentDetails.prev();
								var listStudentEnrollmentTerm = studentListH3.find( ".list_student_enrollment_term" ).text();
								var listStudentPaymentsWait = listStudentDetails.find( ".list_student_payments_wait" );

								jQuery.ajax(
										{
											url: "/listPayment.gtpl"
											, type: "GET"
											, data: jQuery.param(
													{
														limit: 4
														, offset: 0
														, studentId: studentId
														, schoolName: listStudentDetails.find( ".list_student_school" ).text()
														, enrollTermNo: listStudentEnrollmentTerm.substring( 10 )
														, enrollTermYear: listStudentEnrollmentTerm.substring( 0, 4 )
													}
											)
											, beforeSend: function() {
												listStudentPaymentsLabel.hide();
												listStudentPaymentsWait.show();
												studentPaymentsFilterSortByDialog.dialog( "close" );
											}
											, complete: function() {
												listStudentPaymentsLabel.show();
												listStudentPaymentsWait.hide();
											}
											, success: function( data ) {
												dialogListPayment.dialog( "option", "title", "Payments for " + studentListH3.find( ".list_student_first_name" ).text() + " " + studentListH3.find( ".list_student_last_name" ).text() + " (" + studentId + ") from " + listStudentEnrollmentTerm + " to " + studentListH3.find( "span.list_student_leave_term" ).text() );
												dialogListPayment.dialog( "open" );
												dialogListPayment.children().remove();
												dialogListPayment.append( data );
											}
											, error: function( jqXHR, textStatus, errorThrown ) {
												alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
											}
										}
								);
							}
					);
					
					if( jQuery( this ).parents( ".add_student_matching_list_student, .dialog_student_lookup" ).length == 0 ) {	
						studentPaymentsFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_button" ).show();
						studentPaymentsFilterSortByDialog.find( ".student_filter_sortby_dialog_sort_button" ).show();
						studentPaymentsFilterSortByDialog.dialog( "open" );
					}
					else {
						var studentFilterSortByDialogFilterButton = studentPaymentsFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_button" ).hide();
						studentPaymentsFilterSortByDialog.find( ".student_filter_sortby_dialog_sort_button" ).hide();
						
						if( studentFilterSortByDialogFilterButton.hasClass( "glyphicons_blue" ) ) {
							studentFilterSortByDialogFilterButton.removeClass( "glyphicons_blue" ).addClass( "glyphicons" );
							studentPaymentsFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_panel" ).hide();
						}
							
						studentPaymentsFilterSortByDialog.dialog( "open" );
							
						studentPaymentsFilterSortByDialog.animate(
								{
									height: "31px"
								}
						).parent().animate(
								{
									width: "150px"
								}
						);
					}
				}
			}
	);
	
	jQuery( "a.list_student_school_label" ).off( "click" ).click(
			function( event ) {
				var studentSchoolFilterSortByDialog = jQuery( ".student_school_filter_sortby_dialog" );
				
				if( studentSchoolFilterSortByDialog.dialog( "isOpen" ) ) {
					studentSchoolFilterSortByDialog.dialog( "close" );
				}
				else {
					studentSchoolFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					studentSchoolFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-33 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					studentSchoolFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					studentSchoolFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_student_special_info_label" ).off( "click" ).click(
			function( event ) {
				var studentSpecialInfoFilterSortByDialog = jQuery( ".student_special_info_filter_sortby_dialog" );
				
				if( studentSpecialInfoFilterSortByDialog.dialog( "isOpen" ) ) {
					studentSpecialInfoFilterSortByDialog.dialog( "close" );
				}
				else {
					studentSpecialInfoFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					studentSpecialInfoFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-99 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					studentSpecialInfoFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					studentSpecialInfoFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( "a.list_student_sponsored_label" ).off( "click" ).click(
			function( event ) {
				var studentSponsoredFilterSortByDialog = jQuery( ".student_sponsored_filter_sortby_dialog" );
				
				if( studentSponsoredFilterSortByDialog.dialog( "isOpen" ) ) {
					studentSponsoredFilterSortByDialog.dialog( "close" );
				}
				else {
					studentSponsoredFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					studentSponsoredFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-45 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					studentSponsoredFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					studentSponsoredFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( ".list_student_tuition_fees_label" ).off( "click" ).click(
			function( event ) {
				var listStudentTuitionFeesLabel = jQuery( this );
				var listStudentDetails = listStudentTuitionFeesLabel.parent().parent();
				var studentTuitionFeesFilterSortByDialog = jQuery( ".student_tuition_fees_filter_sortby_dialog" );
				
				if( studentTuitionFeesFilterSortByDialog.dialog( "isOpen" ) ) {
					studentTuitionFeesFilterSortByDialog.dialog( "close" );
				}
				else {
					studentTuitionFeesFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					studentTuitionFeesFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-50 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					studentTuitionFeesFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					studentTuitionFeesFilterSortByDialog.find( ".student_filter_sortby_dialog_lookup_button" ).off( "click" ).click(
							function() {
								var studentListH3 = listStudentDetails.prev();
								var listStudentEnrollmentPeriodLookupWait = studentListH3.find( ".list_student_enrollment_period_lookup_wait" );
								
								listStudentTuitionFeesLabel.after( listStudentEnrollmentPeriodLookupWait );
								listStudentEnrollmentPeriodLookupWait.css( "left", "70px" ).css( "top", "265px" );
								
								studentListH3.find( ".list_student_enrollment_period_lookup" ).click();
								
								studentTuitionFeesFilterSortByDialog.dialog( "close" );
							}
					);
					
					if( jQuery( this ).parents( ".add_student_matching_list_student, .dialog_student_lookup" ).length == 0 ) {	
						studentTuitionFeesFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_button" ).show();
						studentTuitionFeesFilterSortByDialog.find( ".student_filter_sortby_dialog_sort_button" ).show();
						studentTuitionFeesFilterSortByDialog.dialog( "open" );
					}
					else {
						var studentFilterSortByDialogFilterButton = studentTuitionFeesFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_button" ).hide();
						studentTuitionFeesFilterSortByDialog.find( ".student_filter_sortby_dialog_sort_button" ).hide();
						
						if( studentFilterSortByDialogFilterButton.hasClass( "glyphicons_blue" ) ) {
							studentFilterSortByDialogFilterButton.removeClass( "glyphicons_blue" ).addClass( "glyphicons" );
							studentTuitionFeesFilterSortByDialog.find( ".student_filter_sortby_dialog_filter_panel" ).hide();
						}
							
						studentTuitionFeesFilterSortByDialog.dialog( "open" );
							
						studentTuitionFeesFilterSortByDialog.animate(
								{
									height: "31px"
								}
						).parent().animate(
								{
									width: "150px"
								}
						);
					}
				}
			}
	);
	
	jQuery( "a.list_student_village_label" ).off( "click" ).click(
			function( event ) {
				var studentVillageFilterSortByDialog = jQuery( ".student_village_filter_sortby_dialog" );
				
				if( studentVillageFilterSortByDialog.dialog( "isOpen" ) ) {
					studentVillageFilterSortByDialog.dialog( "close" );
				}
				else {
					studentVillageFilterSortByDialog.parent().find( ".ui-dialog-titlebar" ).hide();
					studentVillageFilterSortByDialog.dialog( "option", "position",
						{
							my: "left-33 top+15"
							, of: jQuery( this )
							, collision: "fit"
						}
					);
					
					studentVillageFilterSortByDialog.click(
						function( event ) {
							event.stopPropagation();
						}
					);
					
					studentVillageFilterSortByDialog.dialog( "open" );
				}
			}
	);
	
	jQuery( ".list_student_last_update_on" ).each(
			function() {
				if( new Date( jQuery( this ).text() ) != "Invalid Date" ) {
					jQuery( this ).text( new Date( jQuery( this ).text() ).toLocaleDateString() );
				}
			}
	);
	
	jQuery( ".list_student_parent_deceased_toggle" ).change(
			function() {
				var listStudentAnonymousParentDeceasedInd = jQuery( this ).prevAll( ".list_student_anonymous_parent_deceased_ind" );
				if( jQuery( this ).prop( "checked" ) ) {
					listStudentAnonymousParentDeceasedInd.val( "Y" );
				}
				else {
					listStudentAnonymousParentDeceasedInd.val( "N" );
				}
			}
	);
	
	jQuery( ".list_student_parents_lookup" ).button(
			{
				icons: {
					primary: "ui-icon-extlink"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				var listStudentParentsLookup = jQuery( this );
				var listStudentDetails = listStudentParentsLookup.parents( ".list_student_details" );
				var studentId = listStudentDetails.find( ".list_student_id" ).text().substring( 2 );
				var studentListH3 = listStudentDetails.prev();
				var listStudentParentsLookupWait = jQuery( this ).parent().find( ".list_student_parents_lookup_wait" );

				jQuery.ajax(
						{
							url: "/listParent.gtpl"
							, type: "GET"
							, data: jQuery.param(
									{
										limit: 2
										, offset: 0
										, studentId: studentId
									}
							)
							, beforeSend: function() {
								listStudentParentsLookup.hide();
								listStudentParentsLookupWait.show();
							}
							, complete: function() {
								listStudentParentsLookup.show();
								listStudentParentsLookupWait.hide();
							}
							, success: function( data ) {
								dialogListParent.dialog( "option", "title", "Parents of " + studentListH3.find( ".list_student_first_name" ).text() + " " + studentListH3.find( ".list_student_last_name" ).text() + " (" + studentId + ")" );
								dialogListParent.dialog( "open" );
								dialogListParent.children().remove();
								dialogListParent.append( data );
							}
							, error: function( jqXHR, textStatus, errorThrown ) {
								alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
							}
						}
				);
			}
	);

	/* Turn off the clicks previously bound to Student list's save buttons, before binding the existing buttons and the unbound new buttons. */
	jQuery( ".list_student_save" ).button(
			{
				icons: {
					primary: "ui-icon-disk"
				}
			, text: false
			}
	).off( "click" ).click(
			function() {
				var studentListItemHeader = jQuery( this ).parent().parent().prev();
				var studentListItemDetails = jQuery( this ).parent().parent();

				var listStudentEnrollmentPeriodLookup = studentListItemHeader.find( ".list_student_enrollment_period_lookup" ).fadeOut();
				studentListItemDetails.find( ".list_student_tuition_fees_label" ).fadeOut();
				studentListItemDetails.find( ".list_student_boarding_fees_label" ).fadeOut();
				studentListItemDetails.find( ".list_student_save" ).toggle();
				studentListItemDetails.find( ".list_student_cancel" ).toggle();
				studentListItemDetails.find( ".list_student_save_wait" ).toggle();

				var leaveTerm = studentListItemHeader.find( "select.list_student_leave_term" ).val();
				
				if( leaveTerm == "Leave Term" ) {
					leaveTerm = "now";
				}

				if( studentListItemDetails.find( "select[name^='classAttended']" ).length > 0 ) {
					saveEditedStudent( studentListItemHeader, studentListItemDetails );
				}
				else if( studentListItemHeader.find( "span.list_student_first_class_attended" ).text() != studentListItemHeader.find( "select.list_student_first_class_attended" ).val() ||
					studentListItemHeader.find( "span.list_student_last_class_attended" ).text() != studentListItemHeader.find( "select.list_student_last_class_attended" ).val() ||
					studentListItemHeader.find( "span.list_student_leave_term" ).text() != leaveTerm ) {
					studentListItemHeader.find( ".list_student_enrollment_period_lookup_wait" ).css( "top", "48px" );
					listStudentEnrollmentPeriodLookup.click();
				}
				else {
					saveEditedStudent( studentListItemHeader, studentListItemDetails );
				}
			}
	);

	jQuery( ".list_student_select" ).off( "click" ).click(
			function() {
				var listStudentSelect = jQuery( this );
				var studentId = listStudentSelect.parent().find( ".list_student_id" ).text().substring( 2 );

				if( listStudentSelect.parents( ".add_student_matching_list_student" ).length > 0 ) {
					jQuery( ".add_student_matching_list_student" ).dialog( "close" );
					
					var tabs = jQuery( "#tabs" );
					
					if( jQuery( "#enrollment_tab" ).length == 0 ) {
						tabs.on(
							"tabsload"
							, function( event, ui ) {
									populateAddEnrollmentFieldsOfMatchingStudent( studentId )
									jQuery( "#tabs" ).off( "tabsload" );
								}
						);
					}
					else {
						populateAddEnrollmentFieldsOfMatchingStudent( studentId )
					}
					
					tabs.tabs( "option", "active", 1 );
					
					alert( "Instead of adding " + jQuery( ".add_student_first_name" ).val() + " " + jQuery( ".add_student_last_name" ).val() + " as a new Student, please add his/her new Enrollment" );
				}
				else if( listStudentSelect.parents( ".add_enrollment_list_student" ).length > 0 ) {
					jQuery( ".add_enrollment_student_selector" ).text( studentId );
					jQuery( ".add_enrollment_student_id" ).val( studentId );
					jQuery( ".add_enrollment_list_student" ).dialog( "close" );
					
					var addEnrollmentListStudent = listStudentSelect.parents( ".add_enrollment_list_student" );
					jQuery( "html, body" ).animate(
						{
							scrollLeft: addEnrollmentListStudent.find( ".list_student_window_scroll_left" ).text()
							, scrollTop: addEnrollmentListStudent.find( ".list_student_window_scroll_top" ).text()
						}
						, "slow"
					);
				}
				else if( listStudentSelect.parents( ".add_parent_child_list_student" ).length > 0 ) {
					jQuery( ".add_parent_child_selector" ).text( studentId );
					jQuery( ".add_parent_child_list_student" ).dialog( "close" );
					
					var addParentChildListStudent = listStudentSelect.parents( ".add_parent_child_list_student" );
					jQuery( "html, body" ).animate(
						{
							scrollLeft: addParentChildListStudent.find( ".list_student_window_scroll_left" ).text()
							, scrollTop: addParentChildListStudent.find( ".list_student_window_scroll_top" ).text()
						}
						, "slow"
					);
				}
				else if( listStudentSelect.parents( ".add_parent_relative_list_student" ).length > 0 ) {
					jQuery( ".add_parent_relative_selector" ).text( studentId );
					jQuery( ".add_parent_relative_list_student" ).dialog( "close" );
					
					var addParentRelativeListStudent = listStudentSelect.parents( ".add_parent_relative_list_student" );
					jQuery( "html, body" ).animate(
						{
							scrollLeft: addParentRelativeListStudent.find( ".list_student_window_scroll_left" ).text()
							, scrollTop: addParentRelativeListStudent.find( ".list_student_window_scroll_top" ).text()
						}
						, "slow"
					);
				}
				else if( listStudentSelect.parents( ".list_parent_child_list_student" ).length > 0 ) {
					var dialogListParent = jQuery( ".dialog_list_parent" );
					var parentList;
					
					if( dialogListParent.dialog( "isOpen" ) ) {
						parentList = dialogListParent.find( ".parent_list" );
					}
					else {
						parentList = jQuery( ".list_record_form .parent_list" );
					}

					parentList.find( ".list_parent_details" ).eq( parentList.accordion( "option", "active" ) ).find( ".list_parent_child_selector" ).text( studentId );
					jQuery( ".list_parent_child_list_student" ).dialog( "close" );
					
					var listParentChildListStudent = listStudentSelect.parents( ".list_parent_child_list_student" );
					jQuery( "html, body" ).animate(
						{
							scrollLeft: listParentChildListStudent.find( ".list_student_window_scroll_left" ).text()
							, scrollTop: listParentChildListStudent.find( ".list_student_window_scroll_top" ).text()
						}
						, "slow"
					);
				}
				else if( listStudentSelect.parents( ".list_parent_relative_list_student" ).length > 0 ) {
					var dialogListParent = jQuery( ".dialog_list_parent" );
					var parentList;
					
					if( dialogListParent.dialog( "isOpen" ) ) {
						parentList = dialogListParent.find( ".parent_list" );
					}
					else {
						parentList = jQuery( ".list_record_form .parent_list" );
					}

					parentList.find( ".list_parent_details" ).eq( parentList.accordion( "option", "active" ) ).find( ".list_parent_relative_selector" ).text( studentId );
					jQuery( ".list_parent_relative_list_student" ).dialog( "close" );
					
					var listParentRelativeListStudent = listStudentSelect.parents( ".list_parent_relative_list_student" );
					jQuery( "html, body" ).animate(
						{
							scrollLeft: listParentRelativeListStudent.find( ".list_student_window_scroll_left" ).text()
							, scrollTop: listParentRelativeListStudent.find( ".list_student_window_scroll_top" ).text()
						}
						, "slow"
					);
				}
			}
	);
	
	jQuery( ".student_list h3" ).find( "input" ).off( "keydown" ).keydown(
			function( event ) {
				event.stopPropagation();
			}
	);
}

function initTermList() {
	/* Initialize the Start Date and End Date Picker. */
	jQuery( "input.list_term_start_date, input.list_term_end_date" ).datepicker(
			{
				changeMonth: true
				, changeYear: true
				, dateFormat: "M d yy"
					, maxDate: "+3Y"
						, onSelect: function( dateText, inst ) {
							jQuery( this ).css( "color", "#000000" );
							jQuery( this ).css( "font-style", "normal" );
						}
			, yearRange: "-40:+3"
			}
	);
	
	jQuery( ".list_term_class_fees_header" ).each(
			function() {
				var listTermClassFees = jQuery( this ).next();
				
				jQuery( this ).find( "td" ).eq( 0 ).width( listTermClassFees.find( "td" ).eq( 0 ).width() );
				jQuery( this ).find( "td" ).eq( 1 ).width( listTermClassFees.find( "td" ).eq( 1 ).width() );
				jQuery( this ).find( "td" ).eq( 2 ).width( listTermClassFees.find( "td" ).eq( 2 ).width() );
			}
	);

	var listTermCancel = jQuery( ".list_term_cancel" ).button(
			{
				icons: {
					primary: "ui-icon-cancel"
				}
			, text: false
			}
	);

	var listTermDelete = jQuery( ".list_term_delete" ).button(
			{
				icons: {
					primary: "ui-icon-trash"
				}
			, text: false
			}
	);

	var listTermEdit = jQuery( ".list_term_edit" ).button(
			{
				icons: {
					primary: "ui-icon-pencil"
				}
			, text: false
			}
	);

	var listTermSave = jQuery( ".list_term_save" ).button(
			{
				icons: {
					primary: "ui-icon-disk"
				}
			, text: false
			}
	);

	/* Turn off the clicks previously bound to Term list's cancel buttons, before binding the existing buttons and the unbound new buttons. */
	listTermCancel.off( "click" ).click(
			function() {
				var termListItemDetails = jQuery( this ).parent().parent();
				var termListItemHeader = termListItemDetails.prev();

				termListItemDetails.find( ".list_term_save" ).toggle();
				termListItemDetails.find( ".list_term_cancel" ).toggle();
				toggleTermListItemElements( termListItemHeader, termListItemDetails );
			}
	);

	/* Turn off the clicks previously bound to Term list's delete buttons, before binding the existing buttons and the unbound new buttons. */
	listTermDelete.off( "click" ).click(
			function() {
				if( confirm( "Do you really want to delete this Term?" ) ) {
					var termList = jQuery( ".term_list" );
					var termListItemDiv = jQuery( this ).parent().parent();

					jQuery.ajax(
							{
								url: "TermController.groovy"
								, type: "POST"
								, data: jQuery( this ).parent().serialize() + "&action=delete&nextTwentyOffset=" + jQuery( ".list_term_next_twenty_offset" ).text()
								, beforeSend: function() {
										termListItemDiv.find( ".list_term_delete" ).hide();
										termListItemDiv.find( ".list_term_edit" ).hide();
										termListItemDiv.find( ".list_term_save_wait" ).show();
									}
								, success: function( data ) {
										termListItemDiv.prev( "h3" ).remove();
										termListItemDiv.remove();
	
										/* Insert the replacing Term to the bottom of the Term list accordion. */
										termList.append( data )
	
										/* Refer to /js/listInit.js. */
										initTermList();
	
										if( termList.children( "h3" ).length < 20 )
											jQuery( ".list_term_next_twenty" ).css( "display", "none" );
	
										termList.accordion( "refresh" ).accordion( "option", "active", false );
									}
								, error: function( jqXHR, textStatus, errorThrown ) {
										alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
										termListItemDiv.find( ".list_term_delete" ).show();
										termListItemDiv.find( ".list_term_edit" ).show();
										termListItemDiv.find( ".list_term_save_wait" ).hide();
									}
							}
					);
				}
			}
	);

	/* Turn off the clicks previously bound to Student list's edit buttons, before binding the existing buttons and the unbound new buttons. */
	listTermEdit.off( "click" ).click(
			function() {
				var termListItemDetails = jQuery( this ).parent().parent();
				var termListItemHeader = termListItemDetails.prev();

				/* Toggle the display elements with editable elements. */
				termListItemDetails.find( ".list_term_save" ).toggle();
				termListItemDetails.find( ".list_term_cancel" ).toggle();
				toggleTermListItemElements( termListItemHeader, termListItemDetails );

				/* Assign edit field values based on their existing values */
				termListItemHeader.find( "input.list_term_start_date" ).val( termListItemHeader.find( "div.list_term_start_date" ).text() );
				termListItemHeader.find( "input.list_term_end_date" ).val( termListItemHeader.find( "div.list_term_end_date" ).text() );
				
				termListItemDetails.find( "input.list_term_tuition_fee" ).each(
					function() {
						jQuery( this ).val( jQuery( this ).prev().text() );
					}
				);
				
				termListItemDetails.find( "input.list_term_boarding_fee" ).each(
					function() {
						jQuery( this ).val( jQuery( this ).prev().text() );
					}
				);

				jQuery( ".term_list" ).accordion( "option", "collapsible", false );
			}
	);

	listTermSave.off( "click" ).click(
			function() {
				var termListItemDetails = jQuery( this ).parent().parent();
				var termListItemHeader = termListItemDetails.prev();

				termListItemDetails.find( ".list_term_save" ).toggle();
				termListItemDetails.find( ".list_term_cancel" ).toggle();
				termListItemDetails.find( ".list_term_save_wait" ).toggle();

				jQuery.ajax(
						{
							url: "TermController.groovy"
								, type: "POST"
									, data: jQuery( this ).parent().serialize() + "&" + termListItemHeader.find( "form" ).serialize() + "&action=edit"
									, success: function( data ) {
										/* Assign display values based on their existing edit field values */
										termListItemHeader.find( "div.list_term_start_date" ).text( termListItemHeader.find( "input.list_term_start_date" ).val() );
										termListItemHeader.find( "div.list_term_end_date" ).text( termListItemHeader.find( "input.list_term_end_date" ).val() );
										
										termListItemDetails.find( "span.list_term_tuition_fee" ).each(
											function() {
												jQuery( this ).text( jQuery( this ).next().val() );
											}
										);
										
										termListItemDetails.find( "span.list_term_boarding_fee" ).each(
											function() {
												jQuery( this ).text( jQuery( this ).next().val() );
											}
										);

										termListItemDetails.find( ".list_term_last_update_date" ).val( jQuery( data ).find( ".list_term_last_update_date" ).val() );
										termListItemDetails.find( ".list_term_last_update_user" ).val( jQuery( data ).find( ".list_term_last_update_user" ).val() );

										/* Toggle the editable elements with display elements. */
										termListItemDetails.find( ".list_term_save_wait" ).toggle();
										toggleTermListItemElements( termListItemHeader, termListItemDetails );
									}
						, error: function( jqXHR, textStatus, errorThrown ) {
							termListItemDetails.find( ".list_term_save" ).toggle();
							termListItemDetails.find( ".list_term_cancel" ).toggle();
							termListItemDetails.find( ".list_term_save_wait" ).toggle();
							alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
						}
						}
				);
			}
	);
	
	jQuery( ".term_list h3" ).find( "input" ).off( "keydown" ).keydown(
			function( event ) {
				event.stopPropagation();
			}
	);
}

function initURFUserList() {
	var listURFUserCancel = jQuery( ".list_urfuser_cancel" ).button(
			{
				icons: {
					primary: "ui-icon-cancel"
				}
			, text: false
			}
	);

	var listURFUserDelete = jQuery( ".list_urfuser_delete" ).button(
			{
				icons: {
					primary: "ui-icon-trash"
				}
			, text: false
			}
	);

	var listURFUserEdit = jQuery( ".list_urfuser_edit" ).button(
			{
				icons: {
					primary: "ui-icon-pencil"
				}
			, text: false
			}
	);

	var listURFUserSave = jQuery( ".list_urfuser_save" ).button(
			{
				icons: {
					primary: "ui-icon-disk"
				}
			, text: false
			}
	);

	/* Turn off the clicks previously bound to URFUser list's cancel buttons, before binding the existing buttons and the unbound new buttons. */
	listURFUserCancel.off( "click" ).click(
			function() {
				var urfUserListItemDetails = jQuery( this ).parent().parent();
				var urfUserListItemHeader = urfUserListItemDetails.prev();

				urfUserListItemDetails.find( ".list_urfuser_save" ).toggle();
				urfUserListItemDetails.find( ".list_urfuser_cancel" ).toggle();
				toggleURFUserListItemElements( urfUserListItemHeader, urfUserListItemDetails );
			}
	);

	/* Turn off the clicks previously bound to URFUser list's delete buttons, before binding the existing buttons and the unbound new buttons. */
	listURFUserDelete.off( "click" ).click(
			function() {
				if( confirm( "Do you really want to delete this User?" ) ) {
					var urfUserList = jQuery( ".urfuser_list" );
					var urfUserListItemDiv = jQuery( this ).parent().parent();

					jQuery.ajax(
							{
								url: "URFUserController.groovy"
								, type: "POST"
								, data: jQuery( this ).parent().serialize() + "&action=delete&nextTwentyOffset=" + jQuery( ".list_urfuser_next_twenty_offset" ).text()
								, beforeSend: function() {
										urfUserListItemDiv.find( ".list_urfuser_delete" ).hide();
										urfUserListItemDiv.find( ".list_urfuser_edit" ).hide();
										urfUserListItemDiv.find( ".list_urfuser_save_wait" ).show();
									}
								, success: function( data ) {
										urfUserListItemDiv.prev( "h3" ).remove();
										urfUserListItemDiv.remove();
	
										/* Insert the replacing Term to the bottom of the Term list accordion. */
										urfUserList.append( data )
	
										/* Refer to /js/listInit.js. */
										initURFUserList();
	
										if( urfUserList.children( "h3" ).length < 20 )
											jQuery( ".list_urfuser_next_twenty" ).css( "display", "none" );
	
										urfUserList.accordion( "refresh" ).accordion( "option", "active", false );
									}
								, error: function( jqXHR, textStatus, errorThrown ) {
										alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
										urfUserListItemDiv.find( ".list_urfuser_delete" ).show();
										urfUserListItemDiv.find( ".list_urfuser_edit" ).show();
										urfUserListItemDiv.find( ".list_urfuser_save_wait" ).hide();
									}
							}
					);
				}
			}
	);

	/* Turn off the clicks previously bound to URFUser list's edit buttons, before binding the existing buttons and the unbound new buttons. */
	listURFUserEdit.off( "click" ).click(
			function() {
				var urfUserListItemDetails = jQuery( this ).parent().parent();
				var urfUserListItemDetailsSchoolDataPrivilegeTable = urfUserListItemDetails.find( ".list_urfuser_school_data_privilege_table" );
				var urfUserListItemHeader = urfUserListItemDetails.prev();

				/* Toggle the display elements with editable elements. */
				urfUserListItemDetails.find( ".list_urfuser_save" ).toggle();
				urfUserListItemDetails.find( ".list_urfuser_cancel" ).toggle();
				toggleURFUserListItemElements( urfUserListItemHeader, urfUserListItemDetails );

				/* Assign edit field values based on their existing values */
				urfUserListItemHeader.find( "select.list_urfuser_admin_privilege option" ).prop( "selected", false ).filter( 'option[value="' + urfUserListItemHeader.find( "div.list_urfuser_admin_privilege" ).text() + '"]').prop( "selected", true );
				urfUserListItemHeader.find( "select.list_urfuser_sponsor_data_privilege option" ).prop( "selected", false ).filter( 'option[value="' + urfUserListItemHeader.find( "div.list_urfuser_sponsor_data_privilege" ).text() + '"]').prop( "selected", true );
				urfUserListItemDetailsSchoolDataPrivilegeTable.find( "input:radio" ).prop( "checked", false );

				urfUserListItemDetailsSchoolDataPrivilegeTable.find( "tr" ).each(
						function() {
							var secondTd = jQuery( this ).find( "td" ).eq( 1 );
							var thirdTd = jQuery( this ).find( "td" ).eq( 2 );
							var fourthTd = jQuery( this ).find( "td" ).eq( 3 );

							if( secondTd.find( "span" ).text() == "None" ) {
								secondTd.find( "input:radio" ).prop( "checked", true );
							}
							else if( thirdTd.find( "span" ).text() == "Read" ) {
								thirdTd.find( "input:radio" ).prop( "checked", true );
							}
							else if( fourthTd.find( "span" ).text() == "Modify" ) {
								fourthTd.find( "input:radio" ).prop( "checked", true );
							}
						}
				);

				jQuery( ".urfuser_list" ).accordion( "option", "collapsible", false );
			}
	);

	listURFUserSave.off( "click" ).click(
			function() {
				var urfUserListItemDetails = jQuery( this ).parent().parent();
				var urfUserListItemHeader = urfUserListItemDetails.prev();

				urfUserListItemDetails.find( ".list_urfuser_save" ).toggle();
				urfUserListItemDetails.find( ".list_urfuser_cancel" ).toggle();
				urfUserListItemDetails.find( ".list_urfuser_save_wait" ).toggle();

				jQuery.ajax(
						{
							url: "URFUserController.groovy"
								, type: "POST"
									, data: jQuery( this ).parent().serialize() + "&" + urfUserListItemHeader.find( "form" ).serialize() + "&action=edit"
									, success: function( data ) {
										/* Assign display values based on their existing edit field values */
										urfUserListItemHeader.find( "div.list_urfuser_admin_privilege" ).text( urfUserListItemHeader.find( "select.list_urfuser_admin_privilege" ).val() );
										urfUserListItemHeader.find( "div.list_urfuser_sponsor_data_privilege" ).text( urfUserListItemHeader.find( "select.list_urfuser_sponsor_data_privilege" ).val() );
										urfUserListItemDetails.find( "div.list_urfuser_school_data_privilege" ).html( jQuery( data ).find( "div.list_urfuser_school_data_privilege" ).html() );

										urfUserListItemDetails.find( ".list_urfuser_last_update_date" ).val( jQuery( data ).find( ".list_urfuser_last_update_date" ).val() );
										urfUserListItemDetails.find( ".list_urfuser_last_update_user" ).val( jQuery( data ).find( ".list_urfuser_last_update_user" ).val() );

										/* Toggle the editable elements with display elements. */
										urfUserListItemDetails.find( ".list_urfuser_save_wait" ).toggle();
										toggleURFUserListItemElements( urfUserListItemHeader, urfUserListItemDetails );
										urfUserListItemDetails.find( ".list_urfuser_school_data_privilege" ).find( "label, span" ).toggle();
									}
						, error: function( jqXHR, textStatus, errorThrown ) {
							urfUserListItemDetails.find( ".list_urfuser_save" ).toggle();
							urfUserListItemDetails.find( ".list_urfuser_cancel" ).toggle();
							urfUserListItemDetails.find( ".list_urfuser_save_wait" ).toggle();
							alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
						}
						}
				);
			}
	);
}

function populateAddEnrollmentFieldsOfMatchingStudent( studentId ) {
	jQuery( ".add_enrollment_student_selector" ).text( studentId );
	jQuery( ".add_enrollment_student_id" ).val( studentId );
	
	var schoolSelectedIndex = jQuery( ".add_student_school" ).prop( "selectedIndex" );
	jQuery( ".add_enrollment_school" ).prop( "selectedIndex", schoolSelectedIndex ).change();
	jQuery( "#add_enrollment_term_school" + schoolSelectedIndex ).prop( "selectedIndex", jQuery( "#add_student_enrollment_term_school" + schoolSelectedIndex ).prop( "selectedIndex" ) );
	jQuery( "#add_enrollment_leave_term_school" + schoolSelectedIndex ).prop( "selectedIndex", jQuery( "#add_student_leave_term_school" + schoolSelectedIndex ).prop( "selectedIndex" ) );
	jQuery( "#add_enrollment_first_class_attended_school" + schoolSelectedIndex ).prop( "selectedIndex", jQuery( "#add_student_first_class_attended_school" + schoolSelectedIndex ).prop( "selectedIndex" ) );
	jQuery( "#add_enrollment_last_class_attended_school" + schoolSelectedIndex ).prop( "selectedIndex", jQuery( "#add_student_last_class_attended_school" + schoolSelectedIndex ).prop( "selectedIndex" ) );
	
	jQuery( ".add_enrollment_leave_reason_category" ).prop( "selectedIndex", jQuery( ".add_student_leave_reason_category" ).prop( "selectedIndex" ) )
	jQuery( ".add_enrollment_leave_reason" ).focus().val( jQuery( ".add_student_leave_reason" ).val() ).blur().focus().blur();
}

function reloadEnrollmentDialogs( onFeeTab, onPaymentTab ) {
	if( onFeeTab ) {
		var addFeeListEnrollment = jQuery( ".add_fee_list_enrollment" );
  	  
  	  if( addFeeListEnrollment.find( ".add_fee_list_enrollment_first_request" ).length == 0 ) {
  		  addFeeListEnrollment.prepend( '<div class="add_fee_list_enrollment_first_request" style="display: none">Yes</div>' );
  	  }
	}
	
	if( onPaymentTab ) {
		var addPaymentListEnrollment = jQuery( ".add_payment_list_enrollment" );
  	  
  	  if( addPaymentListEnrollment.find( ".add_payment_list_enrollment_first_request" ).length == 0 ) {
  		  addPaymentListEnrollment.prepend( '<div class="add_payment_list_enrollment_first_request" style="display: none">Yes</div>' );
  	  }
	}
}

function reloadStudentDialogs( onStudentTab, onEnrollmentTab, onParentTab ) {
	if( onEnrollmentTab ) {
		var addEnrollmentListStudent = jQuery( ".add_enrollment_list_student" );
  	  
  	  if( addEnrollmentListStudent.find( ".add_enrollment_list_student_first_request" ).length == 0 ) {
  		  addEnrollmentListStudent.prepend( '<div class="add_enrollment_list_student_first_request" style="display: none">Yes</div>' );
  	  }
	}
	
	if( onParentTab ) {
		var addParentChildListStudent = jQuery( ".add_parent_child_list_student" );
  	  
  	  if( addParentChildListStudent.find( ".add_parent_child_list_student_first_request" ).length == 0 ) {
  		  addParentChildListStudent.prepend( '<div class="add_parent_child_list_student_first_request" style="display: none">Yes</div>' );
  	  }
  	  
  	  var addParentRelativeListStudent = jQuery( ".add_parent_relative_list_student" );
  	  
  	  if( addParentRelativeListStudent.find( ".add_parent_relative_list_student_first_request" ).length == 0 ) {
  		  addParentRelativeListStudent.prepend( '<div class="add_parent_relative_list_student_first_request" style="display: none">Yes</div>' );
  	  }
	}
	
	if( onStudentTab || onParentTab ) {
		var listParentChildListStudent = jQuery( ".list_parent_child_list_student" );
  	  
  	  if( listParentChildListStudent.find( ".list_parent_child_list_student_first_request" ).length == 0 ) {
  		  listParentChildListStudent.prepend( '<div class="list_parent_child_list_student_first_request" style="display: none">Yes</div>' );
  	  }
  	  
  	  var listParentRelativeListStudent = jQuery( ".list_parent_relative_list_student" );
  	  
  	  if( listParentRelativeListStudent.find( ".list_parent_relative_list_student_first_request" ).length == 0 ) {
  		  listParentRelativeListStudent.prepend( '<div class="list_parent_relative_list_student_first_request" style="display: none">Yes</div>' );
  	  }
	}
}

function saveEditedEnrollment( enrollmentListItemHeader, enrollmentListItemDetails ) {
	jQuery.ajax(
		{
			url: "EnrollmentController.groovy"
			, type: "POST"
			, data: enrollmentListItemDetails.find( "form" ).serialize() + "&" + enrollmentListItemHeader.find( "form" ).serialize() + "&action=edit"
			, success: function( data ) {
					
					/* Assign display values based on their existing edit field values */
					enrollmentListItemDetails.find( "div.list_enrollment_leave_reason" ).replaceWith( jQuery( data ).find( "div.list_enrollment_leave_reason" ) );
					enrollmentListItemDetails.find( "div.list_enrollment_leave_reason" ).toggle();

					/* Display the First Class Attended value based on the selected value. */
					enrollmentListItemHeader.find( "span.list_enrollment_first_class_attended" ).text( jQuery( data ).find( "span.list_enrollment_first_class_attended" ).text() );

					/* Display the Last Class Attended value based on the selected value. */
					enrollmentListItemHeader.find( "span.list_enrollment_last_class_attended" ).text( jQuery( data ).find( "span.list_enrollment_last_class_attended" ).text() );

					/* Display the Leave Term value based on the selected value. */
					enrollmentListItemHeader.find( "span.list_enrollment_leave_term" ).text( enrollmentListItemHeader.find( "select.list_enrollment_leave_term" ).find( "option:selected" ).text() );

					/* Reset the Class Attended Lookup button to the original location */
					enrollmentListItemHeader.find( ".list_enrollment_period_lookup" ).css( "left", "885px" );
					enrollmentListItemHeader.find( ".list_enrollment_period_lookup_wait" ).css( "left", "898px" );
					
					/* Display the Leave Reason Category value based on the edit field value. */
					enrollmentListItemDetails.find( "div.list_enrollment_leave_reason_category" ).text( enrollmentListItemDetails.find( "select.list_enrollment_leave_reason_category" ).find( "option:selected" ).text() );

					/* Toggle the editable elements with display elements. */
					enrollmentListItemHeader.find( ".list_enrollment_period_lookup" ).css( "left", "885px" ).fadeIn();
					enrollmentListItemHeader.find( ".list_enrollment_period_lookup_wait" ).css( "left", "898px" );
					enrollmentListItemDetails.find( ".list_enrollment_tuition_fees_label" ).fadeIn();
					enrollmentListItemDetails.find( ".list_enrollment_boarding_fees_label" ).fadeIn();
					enrollmentListItemDetails.find( ".list_enrollment_save_wait" ).toggle();
					toggleEnrollmentListItemElements( enrollmentListItemHeader, enrollmentListItemDetails );

					enrollmentListItemDetails.find( ".list_enrollment_tuition_fees" ).text( jQuery( data ).find( ".list_enrollment_tuition_fees" ).text() );
					enrollmentListItemDetails.find( ".list_enrollment_boarding_fees" ).text( jQuery( data ).find( ".list_enrollment_boarding_fees" ).text() );
					enrollmentListItemDetails.find( ".list_enrollment_payments" ).text( jQuery( data ).find( ".list_enrollment_payments" ).text() );
					enrollmentListItemDetails.find( ".list_enrollment_fees_due" ).text( jQuery( data ).find( ".list_enrollment_fees_due" ).text() );

					/* Renew the Last Update information. */
					enrollmentListItemDetails.find( ".list_enrollment_last_update_on" ).text( new Date( jQuery( data ).find( ".list_enrollment_last_update_on" ).text() ).toLocaleDateString() );
					enrollmentListItemDetails.find( ".list_enrollment_last_update_date" ).val( jQuery( data ).find( ".list_enrollment_last_update_date" ).val() );
					enrollmentListItemDetails.find( ".list_enrollment_last_update_by" ).text( jQuery( data ).find( ".list_enrollment_last_update_by" ).text() );
					enrollmentListItemDetails.find( ".list_enrollment_last_update_user" ).val( jQuery( data ).find( ".list_enrollment_last_update_user" ).val() );
					
					enrollmentListItemDetails.find( "select[name^='classAttended']" ).remove();
					enrollmentListItemDetails.find( "input[name^='boardingInd']" ).remove();
					
					var listEnrollmentStaleInd = enrollmentListItemDetails.find( ".list_enrollment_stale_ind" );
					var enrollmentBirthDateFilter = jQuery( ".filters .enrollment_birth_date_filter" );
					
					if( listEnrollmentStaleInd.text() != "Y" && enrollmentBirthDateFilter.text() != "" ) {
						var listEnrollmentBirthDate = new Date( jQuery( data ).find( ".list_enrollment_birth_date" ).text() );
						var enrollmentBirthDateFilterDate = new Date( enrollmentBirthDateFilter.text() );
						var enrollmentBirthDateFilterOperator = jQuery( ".filters .enrollment_birth_date_filter_operator" ).text();
						
						if(
							( enrollmentBirthDateFilterOperator == "=" && listEnrollmentBirthDate != enrollmentBirthDateFilterDate ) ||
							( enrollmentBirthDateFilterOperator == ">" && listEnrollmentBirthDate <= enrollmentBirthDateFilterDate ) ||
							( enrollmentBirthDateFilterOperator == "<" && listEnrollmentBirthDate >= enrollmentBirthDateFilterDate )
						) {
							var listEnrollmentNextTwentyOffset = enrollmentListItemDetails.parent().parent().find( ".list_enrollment_next_twenty_offset" );
							listEnrollmentNextTwentyOffset.text( parseInt( listEnrollmentNextTwentyOffset.text() ) - 1 );
							
							listEnrollmentStaleInd.text( "Y" );
							
							reloadEnrollmentDialogs( true, true );
						}
					}
					
					var enrollmentClassesAttendedFilter = jQuery( ".filters .enrollment_classes_attended_filter" );
					
					if( listEnrollmentStaleInd.text() != "Y" && enrollmentClassesAttendedFilter.text() != "" ) {
						
						if( jQuery( data ).find( ".list_enrollment_classes_attended_text" ).text().indexOf( enrollmentClassesAttendedFilter.text() ) < 0 ) {
							var listEnrollmentNextTwentyOffset = enrollmentListItemDetails.parent().parent().find( ".list_enrollment_next_twenty_offset" );
							listEnrollmentNextTwentyOffset.text( parseInt( listEnrollmentNextTwentyOffset.text() ) - 1 );
							
							listEnrollmentStaleInd.text( "Y" );
							
							reloadEnrollmentDialogs( true, true );
						}
					}
					
					var enrollmentFeesDueFilter = jQuery( ".filters .enrollment_fees_due_filter" );
					
					if( listEnrollmentStaleInd.text() != "Y" && enrollmentFeesDueFilter.text() != "" ) {
						var listEnrollmentFeesDue = Math.round( parseFloat( jQuery( data ).find( ".list_enrollment_fees_due" ).text().replace( /,/g, "" ) ) * 100 ) / 100;
						var enrollmentFeesDueFilterFloat = Math.round( parseFloat( enrollmentFeesDueFilter.text().replace( /,/g, "" ) ) * 100 ) / 100;
						var enrollmentFeesDueFilterOperator = jQuery( ".filters .enrollment_fees_due_filter_operator" ).text();
						
						if(
							( enrollmentFeesDueFilterOperator == "=" && listEnrollmentFeesDue != enrollmentFeesDueFilterFloat ) ||
							( enrollmentFeesDueFilterOperator == ">" && listEnrollmentFeesDue <= enrollmentFeesDueFilterFloat ) ||
							( enrollmentFeesDueFilterOperator == "<" && listEnrollmentFeesDue >= enrollmentFeesDueFilterFloat )
						) {
							var listEnrollmentNextTwentyOffset = enrollmentListItemDetails.parent().parent().find( ".list_enrollment_next_twenty_offset" );
							listEnrollmentNextTwentyOffset.text( parseInt( listEnrollmentNextTwentyOffset.text() ) - 1 );
							
							listEnrollmentStaleInd.text( "Y" );
							
							reloadEnrollmentDialogs( true, true );
						}
					}
					
					var enrollmentPeriodFilter = jQuery( ".filters .enrollment_period_filter" );
					
					if( listEnrollmentStaleInd.text() != "Y" && enrollmentPeriodFilter.text() != "" ) {
						
						if( jQuery( data ).find( ".list_enrollment_terms" ).text().indexOf( enrollmentPeriodFilter.text() ) < 0 ) {
							var listEnrollmentNextTwentyOffset = enrollmentListItemDetails.parent().parent().find( ".list_enrollment_next_twenty_offset" );
							listEnrollmentNextTwentyOffset.text( parseInt( listEnrollmentNextTwentyOffset.text() ) - 1 );
							
							listEnrollmentStaleInd.text( "Y" );
							
							reloadEnrollmentDialogs( true, true );
						}
					}
					
					updateNextTwentyOffsetsAffectedByEnrollment( data );
				}
			, error: function( jqXHR, textStatus, errorThrown ) {
					enrollmentListItemDetails.find( ".list_enrollment_tuition_fees_label" ).fadeIn();
					enrollmentListItemDetails.find( ".list_enrollment_boarding_fees_label" ).fadeIn();
					enrollmentListItemDetails.find( ".list_enrollment_save" ).toggle();
					enrollmentListItemDetails.find( ".list_enrollment_cancel" ).toggle();
					enrollmentListItemDetails.find( ".list_enrollment_save_wait" ).toggle();
					enrollmentListItemDetails.find( "select[name^='classAttended']" ).remove();
					alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
				}
		}
	);
}

function saveEditedStudent( studentListItemHeader, studentListItemDetails ) {
	jQuery.ajax(
			{
				url: "StudentController.groovy"
				, type: "POST"
				, data: studentListItemDetails.find( "form" ).serialize() + "&" + studentListItemHeader.find( "form" ).serialize() + "&action=edit"
				, success: function( data ) {

						/* Assign display values based on their existing edit field values */
						studentListItemHeader.find( "div.list_student_first_name" ).text( studentListItemHeader.find( "input.list_student_first_name" ).val() );
						autoResizeFieldBasedOnWidth( studentListItemHeader, "div.list_student_first_name", 150 );
						studentListItemHeader.find( "div.list_student_last_name" ).text( studentListItemHeader.find( "input.list_student_last_name" ).val() );
						studentListItemDetails.find( "div.list_student_birth_date" ).text( studentListItemDetails.find( "input.list_student_birth_date" ).val() );
						studentListItemDetails.find( "div.list_student_village" ).text( studentListItemDetails.find( "input.list_student_village" ).val() );
						studentListItemDetails.find( "div.list_student_special_info" ).replaceWith( jQuery( data ).find( "div.list_student_special_info" ) );
						studentListItemDetails.find( "div.list_student_special_info" ).toggle();
						studentListItemDetails.find( "div.list_student_leave_reason" ).replaceWith( jQuery( data ).find( "div.list_student_leave_reason" ) );
						studentListItemDetails.find( "div.list_student_leave_reason" ).toggle();

						/* Display the First Class Attended value based on the selected value. */
						studentListItemHeader.find( "span.list_student_first_class_attended" ).text( jQuery( data ).find( "span.list_student_first_class_attended" ).text() );

						/* Display the Last Class Attended value based on the selected value. */
						studentListItemHeader.find( "span.list_student_last_class_attended" ).text( jQuery( data ).find( "span.list_student_last_class_attended" ).text() );

						/* Display the Leave Term value based on the selected value. */
						studentListItemHeader.find( "span.list_student_leave_term" ).text( studentListItemHeader.find( "select.list_student_leave_term" ).find( "option:selected" ).text() );

						/* Reset the Class Attended Lookup button to its original location */
						studentListItemHeader.find( ".list_student_enrollment_details_lookup" ).css( "left", "885px" );
						studentListItemHeader.find( ".list_student_enrollment_details_lookup_wait" ).css( "left", "898px" );
						
						/* Display the Gender value based on the selected value. */
						studentListItemDetails.find( "div.list_student_gender" ).text( studentListItemDetails.find( "select.list_student_gender" ).find( "option:selected" ).text() );

						/* Display the Parent Deceased Ind based on the checked value. */
						studentListItemDetails.find( ".list_student_parent_deceased_ind" ).each(
								function( index ) {
									if( jQuery( this ).nextAll( ".list_student_parent_deceased_toggle" ).prop( "checked" ) ) {
										jQuery( this ).text( "Y" );
									}
									else {
										jQuery( this ).text( "N" );
									}
								}
						);

						/* Display the Leave Reason Category value based on the edit field value. */
						studentListItemDetails.find( "div.list_student_leave_reason_category" ).text( studentListItemDetails.find( "select.list_student_leave_reason_category" ).find( "option:selected" ).text() );

						/* Display the fees based on the newly calculated values. */
						studentListItemDetails.find( ".list_student_tuition_fees" ).text( jQuery( data ).find( ".list_student_tuition_fees" ).text() );
						studentListItemDetails.find( ".list_student_boarding_fees" ).text( jQuery( data ).find( ".list_student_boarding_fees" ).text() );
						studentListItemDetails.find( ".list_student_payments" ).text( jQuery( data ).find( ".list_student_payments" ).text() );
						studentListItemDetails.find( ".list_student_fees_due" ).text( jQuery( data ).find( ".list_student_fees_due" ).text() );

						/* Renew the Last Update information. */
						studentListItemDetails.find( ".list_student_last_update_on" ).text( new Date( jQuery( data ).find( ".list_student_last_update_on" ).text() ).toLocaleDateString() );
						studentListItemDetails.find( ".list_student_last_update_date" ).val( jQuery( data ).find( ".list_student_last_update_date" ).val() );
						studentListItemDetails.find( ".list_student_last_update_by" ).text( jQuery( data ).find( ".list_student_last_update_by" ).text() );
						studentListItemDetails.find( ".list_student_last_update_user" ).val( jQuery( data ).find( ".list_student_last_update_user" ).val() );
						
						studentListItemDetails.find( "select[name^='classAttended']" ).remove();
						studentListItemDetails.find( "input[name^='boardingInd']" ).remove();

						/* Toggle the editable elements with display elements. */
						studentListItemHeader.find( ".list_student_enrollment_period_lookup" ).css( "left", "885px" ).fadeIn();
						studentListItemHeader.find( ".list_student_enrollment_period_lookup_wait" ).css( "left", "898px" );
						studentListItemDetails.find( ".list_student_tuition_fees_label" ).fadeIn();
						studentListItemDetails.find( ".list_student_boarding_fees_label" ).fadeIn();
						studentListItemDetails.find( ".list_student_save_wait" ).toggle();
						toggleStudentListItemElements( studentListItemHeader, studentListItemDetails );
						
						var listStudentStaleInd = studentListItemDetails.find( ".list_student_stale_ind" );
						var studentBirthDateFilter = jQuery( ".filters .student_birth_date_filter" );
						
						if( listStudentStaleInd.text() != "Y" && studentBirthDateFilter.text() != "" ) {
							var listStudentBirthDate = new Date( jQuery( data ).find( ".list_student_birth_date" ).text() );
							var studentBirthDateFilterDate = new Date( studentBirthDateFilter.text() );
							var studentBirthDateFilterOperator = jQuery( ".filters .student_birth_date_filter_operator" ).text();
							
							if(
								( studentBirthDateFilterOperator == "=" && listStudentBirthDate != studentBirthDateFilterDate ) ||
								( studentBirthDateFilterOperator == ">" && listStudentBirthDate <= studentBirthDateFilterDate ) ||
								( studentBirthDateFilterOperator == "<" && listStudentBirthDate >= studentBirthDateFilterDate )
							) {
								var listStudentNextTwentyOffset = studentListItemDetails.parent().parent().find( ".list_student_next_twenty_offset" );
								listStudentNextTwentyOffset.text( parseInt( listStudentNextTwentyOffset.text() ) - 1 );
								
								listStudentStaleInd.text( "Y" );
								
								reloadStudentDialogs( true, true, true );
							}
						}
						
						var studentClassesAttendedFilter = jQuery( ".filters .student_classes_attended_filter" );
						
						if( listStudentStaleInd.text() != "Y" && studentClassesAttendedFilter.text() != "" ) {
							
							if( jQuery( data ).find( ".list_student_classes_attended_text" ).text().indexOf( studentClassesAttendedFilter.text() ) < 0 ) {
								var listStudentNextTwentyOffset = studentListItemDetails.parent().parent().find( ".list_student_next_twenty_offset" );
								listStudentNextTwentyOffset.text( parseInt( listStudentNextTwentyOffset.text() ) - 1 );
								
								listStudentStaleInd.text( "Y" );
								
								reloadStudentDialogs( true, true, true );
							}
						}
						
						var studentEnrollmentPeriodFilter = jQuery( ".filters .student_enrollment_period_filter" );
						
						if( listStudentStaleInd.text() != "Y" && studentEnrollmentPeriodFilter.text() != "" ) {
							
							if( jQuery( data ).find( ".list_student_enrollment_terms" ).text().indexOf( studentEnrollmentPeriodFilter.text() ) < 0 ) {
								var listStudentNextTwentyOffset = studentListItemDetails.parent().parent().find( ".list_student_next_twenty_offset" );
								listStudentNextTwentyOffset.text( parseInt( listStudentNextTwentyOffset.text() ) - 1 );
								
								listStudentStaleInd.text( "Y" );
								
								reloadStudentDialogs( true, true, true );
							}
						}
						
						var studentFeesDueFilter = jQuery( ".filters .student_fees_due_filter" );
						
						if( listStudentStaleInd.text() != "Y" && studentFeesDueFilter.text() != "" ) {
							var listStudentFeesDue = Math.round( parseFloat( jQuery( data ).find( ".list_student_fees_due" ).text().replace( /,/g, "" ) ) * 100 ) / 100;
							var studentFeesDueFilterFloat = Math.round( parseFloat( studentFeesDueFilter.text().replace( /,/g, "" ) ) * 100 ) / 100;
							var studentFeesDueFilterOperator = jQuery( ".filters .student_fees_due_filter_operator" ).text();
							
							if(
								( studentFeesDueFilterOperator == "=" && listStudentFeesDue != studentFeesDueFilterFloat ) ||
								( studentFeesDueFilterOperator == ">" && listStudentFeesDue <= studentFeesDueFilterFloat ) ||
								( studentFeesDueFilterOperator == "<" && listStudentFeesDue >= studentFeesDueFilterFloat )
							) {
								var listStudentNextTwentyOffset = studentListItemDetails.parent().parent().find( ".list_student_next_twenty_offset" );
								listStudentNextTwentyOffset.text( parseInt( listStudentNextTwentyOffset.text() ) - 1 );
								
								listStudentStaleInd.text( "Y" );
								
								reloadStudentDialogs( true, true, true );
							}
						}
						
						var studentFirstNameFilter = jQuery( ".filters .student_first_name_filter" );
						
						if( listStudentStaleInd.text() != "Y" && studentFirstNameFilter.text() != "" ) {
							if( studentFirstNameFilter.text() != jQuery( data ).find( "div.list_student_first_name" ).text() ) {
								var listStudentNextTwentyOffset = studentListItemDetails.parent().parent().find( ".list_student_next_twenty_offset" );
								listStudentNextTwentyOffset.text( parseInt( listStudentNextTwentyOffset.text() ) - 1 );
								
								listStudentStaleInd.text( "Y" );
								
								reloadStudentDialogs( true, true, true );
							}
						}
						
						var studentLastNameFilter = jQuery( ".filters .student_last_name_filter" );
						
						if( listStudentStaleInd.text() != "Y" && studentLastNameFilter.text() != "" ) {
							if( studentLastNameFilter.text() != jQuery( data ).find( "div.list_student_last_name" ).text() ) {
								var listStudentNextTwentyOffset = studentListItemDetails.parent().parent().find( ".list_student_next_twenty_offset" );
								listStudentNextTwentyOffset.text( parseInt( listStudentNextTwentyOffset.text() ) - 1 );
								
								listStudentStaleInd.text( "Y" );
								
								reloadStudentDialogs( true, true, true );
							}
						}
						
						var studentVillageFilter = jQuery( ".filters .student_village_filter" );
						
						if( listStudentStaleInd.text() != "Y" && studentVillageFilter.text() != "" ) {
							if( studentVillageFilter.text() != jQuery( data ).find( "div.list_student_village" ).text() ) {
								var listStudentNextTwentyOffset = studentListItemDetails.parent().parent().find( ".list_student_next_twenty_offset" );
								listStudentNextTwentyOffset.text( parseInt( listStudentNextTwentyOffset.text() ) - 1 );
								
								listStudentStaleInd.text( "Y" );
								
								reloadStudentDialogs( true, true, true );
							}
						}
						
						updateNextTwentyOffsetsAffectedByStudent( data );
					}
				, error: function( jqXHR, textStatus, errorThrown ) {
						studentListItemDetails.find( ".list_student_tuition_fees_label" ).fadeIn();
						studentListItemDetails.find( ".list_student_boarding_fees_label" ).fadeIn();
						studentListItemDetails.find( ".list_student_save" ).toggle();
						studentListItemDetails.find( ".list_student_cancel" ).toggle();
						studentListItemDetails.find( ".list_student_save_wait" ).toggle();
						studentListItemDetails.find( "select[name^='classAttended']" ).remove();
						alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
					}
			}
	);
}

/* Toggle the display elements with editable elements. */
function toggleEnrollmentListItemElements( enrollmentListItemHeader, enrollmentListItemDetails ) {
	enrollmentListItemDetails.find( ".list_enrollment_edit" ).toggle();
	enrollmentListItemDetails.find( ".list_enrollment_delete" ).toggle();

	enrollmentListItemHeader.find( "div" ).not(
		".list_enrollment_classes_attended_text" +
		", .list_enrollment_first_name" +
		", .list_enrollment_last_name" +
		", .list_enrollment_period" +
		", .list_enrollment_sponsored" +
		", .list_enrollment_terms"
	).toggle();
	
	enrollmentListItemHeader.find( "span.list_enrollment_leave_term" ).toggle();
	enrollmentListItemHeader.find( "input" ).toggle();
	enrollmentListItemHeader.find( "select" ).toggle();

	enrollmentListItemDetails.find( ".list_enrollment_leave_reason" ).toggle();
	enrollmentListItemDetails.find( ".list_enrollment_leave_reason_category" ).toggle();
}

/* Toggle the display elements with editable elements. */
function toggleFeeListItemElements( feeListItemHeader, feeListItemDetails ) {
	feeListItemDetails.find( ".list_fee_edit" ).toggle();
	feeListItemDetails.find( ".list_fee_delete" ).toggle();

	feeListItemHeader.find( ".list_fee_amount_required_ind" ).toggle();
	feeListItemHeader.find( ".list_fee_amount" ).toggle();
	
	feeListItemDetails.find( ".list_fee_comment" ).toggle();
}

/* Toggle the display elements with editable elements. */
function toggleGenderListItemElements( genderListItemTr ) {
	genderListItemTr.find( ".list_gender_edit, .list_gender_delete" ).toggle();
	genderListItemTr.find( "input" ).toggle();
	genderListItemTr.find( "span.list_gender_desc" ).toggle();

	genderListItemTr.find( "input.list_gender_desc" ).val( genderListItemTr.find( "span.list_gender_desc" ).text() );
}

/* Toggle the display elements with editable elements. */
function toggleParentListItemElements( parentListItemHeader, parentListItemDetails ) {
	parentListItemDetails.find( ".list_parent_edit" ).toggle();
	parentListItemDetails.find( ".list_parent_delete" ).toggle();

	parentListItemHeader.find( "div" ).not( ".list_parent_stale_ind" ).toggle();
	parentListItemHeader.find( "input" ).toggle();
	parentListItemHeader.find( "label" ).toggle();

	parentListItemDetails.find( ".list_parent_email" ).toggle();
	parentListItemDetails.find( ".list_parent_secondary_phone" ).toggle();
	parentListItemDetails.find( ".list_parent_profession" ).toggle();
	parentListItemDetails.find( ".list_parent_relationship_list" ).toggle();
	parentListItemDetails.find( ".list_parent_of_label" ).toggle();
	parentListItemDetails.find( ".list_parent_child_selector" ).toggle();
	parentListItemDetails.find( ".list_parent_child_button" ).toggle();
	parentListItemDetails.find( ".list_parent_child_delete_button" ).toggle();
	parentListItemDetails.find( ".list_parent_relationship" ).toggle();
	parentListItemDetails.find( ".list_guardian_of_label" ).toggle();
	parentListItemDetails.find( ".list_parent_relative_selector" ).toggle();
	parentListItemDetails.find( ".list_parent_relative_button" ).toggle();
	parentListItemDetails.find( ".list_parent_relative_delete_button" ).toggle();
}

/* Toggle the display elements with editable elements. */
function togglePaymentListItemElements( paymentListItemHeader, paymentListItemDetails ) {
	paymentListItemDetails.find( ".list_payment_edit" ).toggle();
	paymentListItemDetails.find( ".list_payment_delete" ).toggle();

	paymentListItemHeader.find( ".list_payment_amount_required_ind" ).toggle();
	paymentListItemHeader.find( ".list_payment_amount" ).toggle();
	
	paymentListItemDetails.find( ".list_payment_comment" ).toggle();
}

/* Toggle the display elements with editable elements. */
function toggleSchoolListItemElements( schoolListItemTr ) {
	schoolListItemTr.find( ".list_school_edit, .list_school_delete" ).toggle();
	schoolListItemTr.find( ".list_school_add_class" ).toggle();
	schoolListItemTr.find( ".list_school_add_class_button" ).toggle();
	
	var listSchoolClassControls = schoolListItemTr.find( ".list_school_class_controls" );
	
	if( listSchoolClassControls.css( "display" ) == "none" ) {
		listSchoolClassControls.css( "display", "inline-block" );
	}
	else {
		listSchoolClassControls.hide();
	}
}

/* Toggle the display elements with editable elements. */
function toggleStudentListItemElements( studentListItemHeader, studentListItemDetails ) {
	studentListItemDetails.find( ".list_student_edit" ).toggle();
	studentListItemDetails.find( ".list_student_delete" ).toggle();

	studentListItemHeader.find( "div" ).not(
		".list_student_classes_attended_text" +
		", .list_student_enrollment_period" +
		", .list_student_enrollment_terms" +
		", .list_student_sponsored"
	).toggle();
	
	studentListItemHeader.find( "span.list_student_leave_term" ).toggle();
	studentListItemHeader.find( "input" ).toggle();
	studentListItemHeader.find( "select" ).toggle();

	studentListItemDetails.find( ".list_student_birth_date" ).toggle();
	studentListItemDetails.find( ".list_student_gender" ).toggle();
	studentListItemDetails.find( ".list_student_leave_reason" ).toggle();
	studentListItemDetails.find( ".list_student_leave_reason_category" ).toggle();
	studentListItemDetails.find( ".list_student_parent_deceased_ind" ).toggle();
	studentListItemDetails.find( ".list_student_parent_deceased_toggle" ).toggle();
	studentListItemDetails.find( ".list_student_special_info" ).toggle();
	studentListItemDetails.find( ".list_student_village" ).toggle();
}

/* Toggle the display elements with editable elements. */
function toggleTermListItemElements( termListItemHeader, termListItemDetails ) {
	termListItemDetails.find( ".list_term_edit" ).toggle();
	termListItemDetails.find( ".list_term_delete" ).toggle();

	termListItemHeader.find( ".list_term_start_date, .list_term_end_date" ).toggle();

	termListItemDetails.find( ".list_term_tuition_fee, .list_term_boarding_fee" ).toggle();
	
	var listTermClassFeesHeader = termListItemDetails.find( ".list_term_class_fees_header" );
	var listTermClassFees = termListItemDetails.find( ".list_term_class_fees" );
				
	listTermClassFeesHeader.find( "td" ).eq( 0 ).width( listTermClassFees.find( "td" ).eq( 0 ).width() );
	listTermClassFeesHeader.find( "td" ).eq( 1 ).width( listTermClassFees.find( "td" ).eq( 1 ).width() );
	listTermClassFeesHeader.find( "td" ).eq( 2 ).width( listTermClassFees.find( "td" ).eq( 2 ).width() );
}

/* Toggle the display elements with editable elements. */
function toggleURFUserListItemElements( urfUserListItemHeader, urfUserListItemDetails ) {
	urfUserListItemDetails.find( ".list_urfuser_edit" ).toggle();
	urfUserListItemDetails.find( ".list_urfuser_delete" ).toggle();

	urfUserListItemHeader.find( ".list_urfuser_admin_privilege" ).toggle();
	urfUserListItemHeader.find( ".list_urfuser_sponsor_data_privilege" ).toggle();

	urfUserListItemDetails.find( ".list_urfuser_school_data_privilege" ).find( "label, span" ).toggle();
}

function updateNextTwentyOffsetsAffectedByEnrollment( data ) {
	var studentClassesAttendedFilter = jQuery( ".filters .student_classes_attended_filter" );
	
	if( studentClassesAttendedFilter.text() != "" ) {
		
		if( jQuery( data ).find( ".list_enrollment_classes_attended_text" ).text().indexOf( studentClassesAttendedFilter.text() ) < 0 ) {
			jQuery( ".student_list" ).each(
				function() {
					var studentList = jQuery( this );
					
					jQuery( this ).find( ".list_student_details" ).each(
						function() {
							var listStudentStaleInd = jQuery( this ).find( ".list_student_stale_ind" );
							
							if(
								listStudentStaleInd.text() != "Y" &&
								jQuery( this ).find( ".list_student_id" ).text().substring( 2 ) == jQuery( data ).find( ".list_enrollment_student_id" ).text().substring( 2 ) &&
								jQuery( this ).prev().find( ".list_student_enrollment_term" ).text() == jQuery( data ).find( ".list_enrollment_term" ).text()
							) {
								var listStudentNextTwentyOffset = studentList.parent().find( ".list_student_next_twenty_offset" );
								listStudentNextTwentyOffset.text( parseInt( listStudentNextTwentyOffset.text() ) - 1 );
								
								listStudentStaleInd.text( "Y" );
								
								reloadStudentDialogs( true, true, true );
							}
						}
					);
				}
			)
		}
	}
	
	var studentEnrollmentPeriodFilter = jQuery( ".filters .student_enrollment_period_filter" );
	
	if( studentEnrollmentPeriodFilter.text() != "" ) {
		
		if( jQuery( data ).find( ".list_enrollment_terms" ).text().indexOf( studentEnrollmentPeriodFilter.text() ) < 0 ) {
			jQuery( ".student_list" ).each(
				function() {
					var studentList = jQuery( this );
					
					jQuery( this ).find( ".list_student_details" ).each(
						function() {
							var listStudentStaleInd = jQuery( this ).find( ".list_student_stale_ind" );
							
							if(
								listStudentStaleInd.text() != "Y" &&
								jQuery( this ).find( ".list_student_id" ).text().substring( 2 ) == jQuery( data ).find( ".list_enrollment_student_id" ).text().substring( 2 ) &&
								jQuery( this ).prev().find( ".list_student_enrollment_term" ).text() == jQuery( data ).find( ".list_enrollment_term" ).text()
							) {
								var listStudentNextTwentyOffset = studentList.parent().find( ".list_student_next_twenty_offset" );
								listStudentNextTwentyOffset.text( parseInt( listStudentNextTwentyOffset.text() ) - 1 );
								
								listStudentStaleInd.text( "Y" );
								
								reloadStudentDialogs( true, true, true );
							}
						}
					);
				}
			)
		}
	}
	
	var studentFeesDueFilter = jQuery( ".filters .student_fees_due_filter" );
	
	if( studentFeesDueFilter.text() != "" ) {
		var listEnrollmentFeesDue = Math.round( parseFloat( jQuery( data ).find( ".list_enrollment_fees_due" ).text().replace( /,/g, "" ) ) * 100 ) / 100;
		var studentFeesDueFilterFloat = Math.round( parseFloat( studentFeesDueFilter.text().replace( /,/g, "" ) ) * 100 ) / 100;
		var studentFeesDueFilterOperator = jQuery( ".filters .student_fees_due_filter_operator" ).text();
		
		if(
			( studentFeesDueFilterOperator == "=" && listEnrollmentFeesDue != studentFeesDueFilterFloat ) ||
			( studentFeesDueFilterOperator == ">" && listEnrollmentFeesDue <= studentFeesDueFilterFloat ) ||
			( studentFeesDueFilterOperator == "<" && listEnrollmentFeesDue >= studentFeesDueFilterFloat )
		) {
			jQuery( ".student_list" ).each(
				function() {
					var studentList = jQuery( this );
					
					jQuery( this ).find( ".list_student_details" ).each(
						function() {
							var listStudentStaleInd = jQuery( this ).find( ".list_student_stale_ind" );
							
							if(
								listStudentStaleInd.text() != "Y" &&
								jQuery( this ).find( ".list_student_id" ).text().substring( 2 ) == jQuery( data ).find( ".list_enrollment_student_id" ).text().substring( 2 ) &&
								jQuery( this ).prev().find( ".list_student_enrollment_term" ).text() == jQuery( data ).find( ".list_enrollment_term" ).text()
							) {
								var listStudentNextTwentyOffset = studentList.parent().find( ".list_student_next_twenty_offset" );
								listStudentNextTwentyOffset.text( parseInt( listStudentNextTwentyOffset.text() ) - 1 );
								
								listStudentStaleInd.text( "Y" );
								
								reloadStudentDialogs( true, true, true );
							}
						}
					);
				}
			)
		}
	}
}

function updateNextTwentyOffsetsAffectedByFee( data ) {
	var enrollmentFeesDueFilter = jQuery( ".filters .enrollment_fees_due_filter" );
	
	if( enrollmentFeesDueFilter.text() != "" ) {
		var listFeeFeesDue = Math.round( parseFloat( jQuery( data ).find( ".list_fee_fees_due" ).text() ) * 100 ) / 100;
		var enrollmentFeesDueFilterFloat = Math.round( parseFloat( enrollmentFeesDueFilter.text().replace( /,/g, "" ) ) * 100 ) / 100;
		var enrollmentFeesDueFilterOperator = jQuery( ".filters .enrollment_fees_due_filter_operator" ).text();
		
		if(
			( enrollmentFeesDueFilterOperator == "=" && listFeeFeesDue != enrollmentFeesDueFilterFloat ) ||
			( enrollmentFeesDueFilterOperator == ">" && listFeeFeesDue <= enrollmentFeesDueFilterFloat ) ||
			( enrollmentFeesDueFilterOperator == "<" && listFeeFeesDue >= enrollmentFeesDueFilterFloat )
		) {
			jQuery( ".enrollment_list" ).each(
				function() {
					var enrollmentList = jQuery( this );
					
					jQuery( this ).find( ".list_enrollment_details" ).each(
						function() {
							var listEnrollmentStaleInd = jQuery( this ).find( ".list_enrollment_stale_ind" );
							
							if(
								listEnrollmentStaleInd.text() != "Y" &&
								jQuery( this ).find( ".list_enrollment_student_id" ).text().substring( 2 ) == jQuery( data ).find( "input.list_fee_student_id" ).val() &&
								jQuery( this ).prev().find( ".list_enrollment_term" ).text() == jQuery( data ).find( ".list_fee_enroll_term_year" ).val() + " Term " + jQuery( data ).find( ".list_fee_enroll_term_no" ).val()
							) {
								var listEnrollmentNextTwentyOffset = enrollmentList.parent().find( ".list_enrollment_next_twenty_offset" );
								listEnrollmentNextTwentyOffset.text( parseInt( listEnrollmentNextTwentyOffset.text() ) - 1 );
								
								listEnrollmentStaleInd.text( "Y" );
								
								reloadEnrollmentDialogs( true, true );
							}
						}
					);
				}
			)
		}
	}
	
	var studentFeesDueFilter = jQuery( ".filters .student_fees_due_filter" );
	
	if( studentFeesDueFilter.text() != "" ) {
		var listFeeFeesDue = Math.round( parseFloat( jQuery( data ).find( ".list_fee_fees_due" ).text() ) * 100 ) / 100;
		var studentFeesDueFilterFloat = Math.round( parseFloat( studentFeesDueFilter.text().replace( /,/g, "" ) ) * 100 ) / 100;
		var studentFeesDueFilterOperator = jQuery( ".filters .student_fees_due_filter_operator" ).text();
		
		if(
			( studentFeesDueFilterOperator == "=" && listFeeFeesDue != studentFeesDueFilterFloat ) ||
			( studentFeesDueFilterOperator == ">" && listFeeFeesDue <= studentFeesDueFilterFloat ) ||
			( studentFeesDueFilterOperator == "<" && listFeeFeesDue >= studentFeesDueFilterFloat )
		) {
			jQuery( ".student_list" ).each(
				function() {
					var studentList = jQuery( this );
					
					jQuery( this ).find( ".list_student_details" ).each(
						function() {
							var listStudentStaleInd = jQuery( this ).find( ".list_student_stale_ind" );
							
							if(
								listStudentStaleInd.text() != "Y" &&
								jQuery( this ).find( ".list_student_id" ).text().substring( 2 ) == jQuery( data ).find( "input.list_fee_student_id" ).val() &&
								jQuery( this ).prev().find( ".list_student_enrollment_term" ).text() == jQuery( data ).find( ".list_fee_enroll_term_year" ).val() + " Term " + jQuery( data ).find( ".list_fee_enroll_term_no" ).val()
							) {
								var listStudentNextTwentyOffset = studentList.parent().find( ".list_student_next_twenty_offset" );
								listStudentNextTwentyOffset.text( parseInt( listStudentNextTwentyOffset.text() ) - 1 );
								
								listStudentStaleInd.text( "Y" );
								
								reloadStudentDialogs( true, true, true );
							}
						}
					);
				}
			)
		}
	}
}

function updateNextTwentyOffsetsAffectedByPayment( data ) {
	var enrollmentFeesDueFilter = jQuery( ".filters .enrollment_fees_due_filter" );
	
	if( enrollmentFeesDueFilter.text() != "" ) {
		var listPaymentFeesDue = Math.round( parseFloat( jQuery( data ).find( ".list_payment_fees_due" ).text() ) * 100 ) / 100;
		var enrollmentFeesDueFilterFloat = Math.round( parseFloat( enrollmentFeesDueFilter.text().replace( /,/g, "" ) ) * 100 ) / 100;
		var enrollmentFeesDueFilterOperator = jQuery( ".filters .enrollment_fees_due_filter_operator" ).text();
		
		if(
			( enrollmentFeesDueFilterOperator == "=" && listPaymentFeesDue != enrollmentFeesDueFilterFloat ) ||
			( enrollmentFeesDueFilterOperator == ">" && listPaymentFeesDue <= enrollmentFeesDueFilterFloat ) ||
			( enrollmentFeesDueFilterOperator == "<" && listPaymentFeesDue >= enrollmentFeesDueFilterFloat )
		) {
			jQuery( ".enrollment_list" ).each(
				function() {
					var enrollmentList = jQuery( this );
					
					jQuery( this ).find( ".list_enrollment_details" ).each(
						function() {
							var listEnrollmentStaleInd = jQuery( this ).find( ".list_enrollment_stale_ind" );
							
							if(
								listEnrollmentStaleInd.text() != "Y" &&
								jQuery( this ).find( ".list_enrollment_student_id" ).text().substring( 2 ) == jQuery( data ).find( "input.list_payment_student_id" ).val() &&
								jQuery( this ).prev().find( ".list_enrollment_term" ).text() == jQuery( data ).find( ".list_payment_enroll_term_year" ).val() + " Term " + jQuery( data ).find( ".list_payment_enroll_term_no" ).val()
							) {
								var listEnrollmentNextTwentyOffset = enrollmentList.parent().find( ".list_enrollment_next_twenty_offset" );
								listEnrollmentNextTwentyOffset.text( parseInt( listEnrollmentNextTwentyOffset.text() ) - 1 );
								
								listEnrollmentStaleInd.text( "Y" );
								
								reloadEnrollmentDialogs( true, true );
							}
						}
					);
				}
			)
		}
	}
	
	var studentFeesDueFilter = jQuery( ".filters .student_fees_due_filter" );
	
	if( studentFeesDueFilter.text() != "" ) {
		var listPaymentFeesDue = Math.round( parseFloat( jQuery( data ).find( ".list_payment_fees_due" ).text() ) * 100 ) / 100;
		var studentFeesDueFilterFloat = Math.round( parseFloat( studentFeesDueFilter.text().replace( /,/g, "" ) ) * 100 ) / 100;
		var studentFeesDueFilterOperator = jQuery( ".filters .student_fees_due_filter_operator" ).text();
		
		if(
			( studentFeesDueFilterOperator == "=" && listPaymentFeesDue != studentFeesDueFilterFloat ) ||
			( studentFeesDueFilterOperator == ">" && listPaymentFeesDue <= studentFeesDueFilterFloat ) ||
			( studentFeesDueFilterOperator == "<" && listPaymentFeesDue >= studentFeesDueFilterFloat )
		) {
			jQuery( ".student_list" ).each(
				function() {
					var studentList = jQuery( this );
					
					jQuery( this ).find( ".list_student_details" ).each(
						function() {
							var listStudentStaleInd = jQuery( this ).find( ".list_student_stale_ind" );
							
							if(
								listStudentStaleInd.text() != "Y" &&
								jQuery( this ).find( ".list_student_id" ).text().substring( 2 ) == jQuery( data ).find( "input.list_payment_student_id" ).val() &&
								jQuery( this ).prev().find( ".list_student_enrollment_term" ).text() == jQuery( data ).find( ".list_payment_enroll_term_year" ).val() + " Term " + jQuery( data ).find( ".list_payment_enroll_term_no" ).val()
							) {
								var listStudentNextTwentyOffset = studentList.parent().find( ".list_student_next_twenty_offset" );
								listStudentNextTwentyOffset.text( parseInt( listStudentNextTwentyOffset.text() ) - 1 );
								
								listStudentStaleInd.text( "Y" );
								
								reloadStudentDialogs( true, true, true );
							}
						}
					);
				}
			)
		}
	}
}

function updateNextTwentyOffsetsAffectedByStudent( data ) {
	var enrollmentBirthDateFilter = jQuery( ".filters .enrollment_birth_date_filter" );
	
	if( enrollmentBirthDateFilter.text() != "" ) {
		var listStudentBirthDate = new Date( jQuery( data ).find( ".list_student_birth_date" ).text() );
		var studentBirthDateFilterDate = new Date( studentBirthDateFilter.text() );
		var studentBirthDateFilterOperator = jQuery( ".filters .student_birth_date_filter_operator" ).text();
		
		if(
			( studentBirthDateFilterOperator == "=" && listStudentBirthDate != studentBirthDateFilterDate ) ||
			( studentBirthDateFilterOperator == ">" && listStudentBirthDate <= studentBirthDateFilterDate ) ||
			( studentBirthDateFilterOperator == "<" && listStudentBirthDate >= studentBirthDateFilterDate )
		) {
			jQuery( ".enrollment_list" ).each(
				function() {
					var enrollmentList = jQuery( this );
					
					jQuery( this ).find( ".list_enrollment_details" ).each(
						function() {
							var listEnrollmentStaleInd = jQuery( this ).find( ".list_enrollment_stale_ind" );
							
							if(
								listEnrollmentStaleInd.text() != "Y" &&
								jQuery( this ).find( ".list_enrollment_student_id" ).text().substring( 2 ) == jQuery( data ).find( ".list_student_id" ).text().substring( 2 ) &&
								jQuery( this ).prev().find( ".list_enrollment_term" ).text() == jQuery( data ).find( ".list_student_enrollment_term" ).text()
							) {
								var listEnrollmentNextTwentyOffset = enrollmentList.parent().find( ".list_enrollment_next_twenty_offset" );
								listEnrollmentNextTwentyOffset.text( parseInt( listEnrollmentNextTwentyOffset.text() ) - 1 );
								
								listEnrollmentStaleInd.text( "Y" );
								
								reloadEnrollmentDialogs( true, true );
							}
						}
					);
				}
			)
		}
	}
	
	var enrollmentClassesAttendedFilter = jQuery( ".filters .enrollment_classes_attended_filter" );
	
	if( enrollmentClassesAttendedFilter.text() != "" ) {
		
		if( jQuery( data ).find( ".list_student_classes_attended_text" ).text().indexOf( enrollmentClassesAttendedFilter.text() ) < 0 ) {
			jQuery( ".enrollment_list" ).each(
				function() {
					var enrollmentList = jQuery( this );
					
					jQuery( this ).find( ".list_enrollment_details" ).each(
						function() {
							var listEnrollmentStaleInd = jQuery( this ).find( ".list_enrollment_stale_ind" );
							
							if(
								listEnrollmentStaleInd.text() != "Y" &&
								jQuery( this ).find( ".list_enrollment_student_id" ).text().substring( 2 ) == jQuery( data ).find( ".list_student_id" ).text().substring( 2 ) &&
								jQuery( this ).prev().find( ".list_enrollment_term" ).text() == jQuery( data ).find( ".list_student_enrollment_term" ).text()
							) {
								var listEnrollmentNextTwentyOffset = enrollmentList.parent().find( ".list_enrollment_next_twenty_offset" );
								listEnrollmentNextTwentyOffset.text( parseInt( listEnrollmentNextTwentyOffset.text() ) - 1 );
								
								listEnrollmentStaleInd.text( "Y" );
								
								reloadEnrollmentDialogs( true, true );
							}
						}
					);
				}
			)
		}
	}
	
	var enrollmentFeesDueFilter = jQuery( ".filters .enrollment_fees_due_filter" );
	
	if( enrollmentFeesDueFilter.text() != "" ) {
		var listStudentFeesDue = Math.round( parseFloat( jQuery( data ).find( ".list_student_fees_due" ).text().replace( /,/g, "" ) ) * 100 ) / 100;
		var studentFeesDueFilterFloat = Math.round( parseFloat( studentFeesDueFilter.text().replace( /,/g, "" ) ) * 100 ) / 100;
		var studentFeesDueFilterOperator = jQuery( ".filters .student_fees_due_filter_operator" ).text();
		
		if(
			( studentFeesDueFilterOperator == "=" && listStudentFeesDue != studentFeesDueFilterFloat ) ||
			( studentFeesDueFilterOperator == ">" && listStudentFeesDue <= studentFeesDueFilterFloat ) ||
			( studentFeesDueFilterOperator == "<" && listStudentFeesDue >= studentFeesDueFilterFloat )
		) {
			jQuery( ".enrollment_list" ).each(
				function() {
					var enrollmentList = jQuery( this );
					
					jQuery( this ).find( ".list_enrollment_details" ).each(
						function() {
							var listEnrollmentStaleInd = jQuery( this ).find( ".list_enrollment_stale_ind" );
							
							if(
								listEnrollmentStaleInd.text() != "Y" &&
								jQuery( this ).find( ".list_enrollment_student_id" ).text().substring( 2 ) == jQuery( data ).find( ".list_student_id" ).text().substring( 2 ) &&
								jQuery( this ).prev().find( ".list_enrollment_term" ).text() == jQuery( data ).find( ".list_student_enrollment_term" ).text()
							) {
								var listEnrollmentNextTwentyOffset = enrollmentList.parent().find( ".list_enrollment_next_twenty_offset" );
								listEnrollmentNextTwentyOffset.text( parseInt( listEnrollmentNextTwentyOffset.text() ) - 1 );
								
								listEnrollmentStaleInd.text( "Y" );
								
								reloadEnrollmentDialogs( true, true );
							}
						}
					);
				}
			)
		}
	}
	
	var enrollmentFirstNameFilter = jQuery( ".filters .enrollment_first_name_filter" );
	
	if( enrollmentFirstNameFilter.text() != "" ) {
		
		if( enrollmentFirstNameFilter.text() != jQuery( data ).find( "div.list_student_first_name" ).text() ) {
			jQuery( ".enrollment_list" ).each(
				function() {
					var enrollmentList = jQuery( this );
					
					jQuery( this ).find( ".list_enrollment_details" ).each(
						function() {
							var listEnrollmentStaleInd = jQuery( this ).find( ".list_enrollment_stale_ind" );
							
							if(
								listEnrollmentStaleInd.text() != "Y" &&
								jQuery( this ).find( ".list_enrollment_student_id" ).text().substring( 2 ) == jQuery( data ).find( ".list_student_id" ).text().substring( 2 ) &&
								jQuery( this ).prev().find( ".list_enrollment_term" ).text() == jQuery( data ).find( ".list_student_enrollment_term" ).text()
							) {
								var listEnrollmentNextTwentyOffset = enrollmentList.parent().find( ".list_enrollment_next_twenty_offset" );
								listEnrollmentNextTwentyOffset.text( parseInt( listEnrollmentNextTwentyOffset.text() ) - 1 );
								
								listEnrollmentStaleInd.text( "Y" );
								
								reloadEnrollmentDialogs( true, true );
							}
						}
					);
				}
			)
		}
	}
	
	var enrollmentLastNameFilter = jQuery( ".filters .enrollment_last_name_filter" );
	
	if( enrollmentLastNameFilter.text() != "" ) {
		
		if( enrollmentLastNameFilter.text() != jQuery( data ).find( "div.list_student_last_name" ).text() ) {
			jQuery( ".enrollment_list" ).each(
				function() {
					var enrollmentList = jQuery( this );
					
					jQuery( this ).find( ".list_enrollment_details" ).each(
						function() {
							var listEnrollmentStaleInd = jQuery( this ).find( ".list_enrollment_stale_ind" );
							
							if(
								listEnrollmentStaleInd.text() != "Y" &&
								jQuery( this ).find( ".list_enrollment_student_id" ).text().substring( 2 ) == jQuery( data ).find( ".list_student_id" ).text().substring( 2 ) &&
								jQuery( this ).prev().find( ".list_enrollment_term" ).text() == jQuery( data ).find( ".list_student_enrollment_term" ).text()
							) {
								var listEnrollmentNextTwentyOffset = enrollmentList.parent().find( ".list_enrollment_next_twenty_offset" );
								listEnrollmentNextTwentyOffset.text( parseInt( listEnrollmentNextTwentyOffset.text() ) - 1 );
								
								listEnrollmentStaleInd.text( "Y" );
								
								reloadEnrollmentDialogs( true, true );
							}
						}
					);
				}
			)
		}
	}
	
	var enrollmentPeriodFilter = jQuery( ".filters .enrollment_period_filter" );
	
	if( enrollmentPeriodFilter.text() != "" ) {
		
		if( jQuery( data ).find( ".list_student_enrollment_terms" ).text().indexOf( enrollmentPeriodFilter.text() ) < 0 ) {
			jQuery( ".enrollment_list" ).each(
				function() {
					var enrollmentList = jQuery( this );
					
					jQuery( this ).find( ".list_enrollment_details" ).each(
						function() {
							var listEnrollmentStaleInd = jQuery( this ).find( ".list_enrollment_stale_ind" );
							
							if(
								listEnrollmentStaleInd.text() != "Y" &&
								jQuery( this ).find( ".list_enrollment_student_id" ).text().substring( 2 ) == jQuery( data ).find( ".list_student_id" ).text().substring( 2 ) &&
								jQuery( this ).prev().find( ".list_enrollment_term" ).text() == jQuery( data ).find( ".list_student_enrollment_term" ).text()
							) {
								var listEnrollmentNextTwentyOffset = enrollmentList.parent().find( ".list_enrollment_next_twenty_offset" );
								listEnrollmentNextTwentyOffset.text( parseInt( listEnrollmentNextTwentyOffset.text() ) - 1 );
								
								listEnrollmentStaleInd.text( "Y" );
								
								reloadEnrollmentDialogs( true, true );
							}
						}
					);
				}
			)
		}
	}
	
	var enrollmentVillageFilter = jQuery( ".filters .enrollment_village_filter" );
	
	if( enrollmentVillageFilter.text() != "" ) {
		
		if( enrollmentVillageFilter.text() != jQuery( data ).find( "div.list_student_village" ).text() ) {
			jQuery( ".enrollment_list" ).each(
				function() {
					var enrollmentList = jQuery( this );
					
					jQuery( this ).find( ".list_enrollment_details" ).each(
						function() {
							var listEnrollmentStaleInd = jQuery( this ).find( ".list_enrollment_stale_ind" );
							
							if(
								listEnrollmentStaleInd.text() != "Y" &&
								jQuery( this ).find( ".list_enrollment_student_id" ).text().substring( 2 ) == jQuery( data ).find( ".list_student_id" ).text().substring( 2 ) &&
								jQuery( this ).prev().find( ".list_enrollment_term" ).text() == jQuery( data ).find( ".list_student_enrollment_term" ).text()
							) {
								var listEnrollmentNextTwentyOffset = enrollmentList.parent().find( ".list_enrollment_next_twenty_offset" );
								listEnrollmentNextTwentyOffset.text( parseInt( listEnrollmentNextTwentyOffset.text() ) - 1 );
								
								listEnrollmentStaleInd.text( "Y" );
								
								reloadEnrollmentDialogs( true, true );
							}
						}
					);
				}
			)
		}
	}
}