/*
 * Initialize some Enrollment list elements:
 * 1. Edit Button.
 * 2. Delete Button.
 * 3. Enrollment Delete action.
 */
function initEnrollmentList() {
	jQuery( ".list_enrollment_last_update_on" ).each(
			function() {
				jQuery( this ).text( new Date( jQuery( this ).text() ).toLocaleDateString() );
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
										limit: 20
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
	
	jQuery( ".list_enrollment_boarding_fees_label" ).off( "click" ).click(
			function() {
				var listEnrollmentDetails = jQuery( this ).parent().parent();
				var enrollmentListH3 = listEnrollmentDetails.prev();
				var listEnrollmentPeriodLookupWait = enrollmentListH3.find( ".list_enrollment_period_lookup_wait" );
				
				jQuery( this ).after( listEnrollmentPeriodLookupWait );
				listEnrollmentPeriodLookupWait.css( "left", "70px" ).css( "top", "300px" );
				
				enrollmentListH3.find( ".list_enrollment_period_lookup" ).click();
			}
	);
	
	jQuery( ".list_enrollment_tuition_fees_label" ).off( "click" ).click(
			function() {
				var listEnrollmentDetails = jQuery( this ).parent().parent();
				var enrollmentListH3 = listEnrollmentDetails.prev();
				var listEnrollmentPeriodLookupWait = enrollmentListH3.find( ".list_enrollment_period_lookup_wait" );
				
				jQuery( this ).after( listEnrollmentPeriodLookupWait );
				listEnrollmentPeriodLookupWait.css( "left", "70px" ).css( "top", "265px" );
				
				enrollmentListH3.find( ".list_enrollment_period_lookup" ).click();
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
				enrollmentListItemDetails.find( "textarea.list_enrollment_leave_reason" ).val( enrollmentListItemDetails.find( "div.list_enrollment_leave_reason" ).text() );

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
										, success: function( data ) {
											genderListItem.remove();
										}
							, error: function( jqXHR, textStatus, errorThrown ) {
								alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
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
										, success: function( data ) {
											leaveReasonCategoryListItem.remove();
										}
							, error: function( jqXHR, textStatus, errorThrown ) {
								alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
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
				jQuery( this ).text( new Date( jQuery( this ).text() ).toLocaleDateString() );
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
						var parentList = jQuery( ".list_record_form .parent_list" );
						var parentToDeleteDiv = jQuery( this ).parent().parent();
	
						jQuery.ajax(
								{
									url: "ParentController.groovy"
										, type: "POST"
											, data: jQuery( this ).parent().serialize() + "&action=delete&nextTwentyOffset=" + jQuery( ".list_record_form .list_parent_next_twenty_offset" ).text()
											, success: function( data ) {
												parentList.toggle();
												parentToDeleteDiv.prev( "h3" ).remove();
												parentToDeleteDiv.remove();
	
												/* Insert the replacing Parent to the bottom of the Parent list accordion. */
												parentList.append( data )
	
												/* Refer to /js/listInit.js. */
												initParentList();
	
												if( parentList.children( "h3" ).length < 20 )
													jQuery( ".list_record_form .list_parent_next_twenty" ).css( "display", "none" );
	
												parentList.accordion( "refresh" ).accordion( "option", "active", false );
												parentList.toggle();
	
											}
								, error: function( jqXHR, textStatus, errorThrown ) {
									alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
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
	
					jQuery( ".list_record_form .parent_list" ).accordion( "option", "collapsible", false );
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
										, success: function( data ) {
											parentalRelationshipListItem.remove();
										}
							, error: function( jqXHR, textStatus, errorThrown ) {
								alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
							}
							}
					);
				}
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
								, success: function( data ) {
										schoolListItem.remove();
										initSchoolList();
									}
								, error: function( jqXHR, textStatus, errorThrown ) {
										alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
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
				studentListItemDetails.find( "textarea.list_student_special_info" ).val( studentListItemDetails.find( "div.list_student_special_info" ).text() );
				studentListItemDetails.find( "textarea.list_student_leave_reason" ).val( studentListItemDetails.find( "div.list_student_leave_reason" ).text() );

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
	
	jQuery( ".list_student_boarding_fees_label" ).off( "click" ).click(
			function() {
				var listStudentDetails = jQuery( this ).parent().parent();
				var studentListH3 = listStudentDetails.prev();
				var listStudentEnrollmentPeriodLookupWait = studentListH3.find( ".list_student_enrollment_period_lookup_wait" );
				
				jQuery( this ).after( listStudentEnrollmentPeriodLookupWait );
				listStudentEnrollmentPeriodLookupWait.css( "left", "70px" ).css( "top", "300px" );
				
				studentListH3.find( ".list_student_enrollment_period_lookup" ).click();
			}
	);
	
	jQuery( ".list_student_tuition_fees_label" ).off( "click" ).click(
			function() {
				var listStudentDetails = jQuery( this ).parent().parent();
				var studentListH3 = listStudentDetails.prev();
				var listStudentEnrollmentPeriodLookupWait = studentListH3.find( ".list_student_enrollment_period_lookup_wait" );
				
				jQuery( this ).after( listStudentEnrollmentPeriodLookupWait );
				listStudentEnrollmentPeriodLookupWait.css( "left", "70px" ).css( "top", "265px" );
				
				studentListH3.find( ".list_student_enrollment_period_lookup" ).click();
			}
	);
	
	jQuery( ".list_student_last_update_on" ).each(
			function() {
				jQuery( this ).text( new Date( jQuery( this ).text() ).toLocaleDateString() );
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

				if( listStudentSelect.parents( ".add_enrollment_list_student" ).length > 0 ) {
					jQuery( ".add_enrollment_student_selector" ).text( studentId );
					jQuery( ".add_enrollment_student_id" ).val( studentId );
					jQuery( ".add_enrollment_list_student" ).dialog( "close" );
				}
				else if( listStudentSelect.parents( ".add_parent_child_list_student" ).length > 0 ) {
					jQuery( ".add_parent_child_selector" ).text( studentId );
					jQuery( ".add_parent_child_list_student" ).dialog( "close" );
				}
				else if( listStudentSelect.parents( ".add_parent_relative_list_student" ).length > 0 ) {
					jQuery( ".add_parent_relative_selector" ).text( studentId );
					jQuery( ".add_parent_relative_list_student" ).dialog( "close" );
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
				}
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

function saveEditedEnrollment( enrollmentListItemHeader, enrollmentListItemDetails ) {
	jQuery.ajax(
		{
			url: "EnrollmentController.groovy"
			, type: "POST"
			, data: enrollmentListItemDetails.find( "form" ).serialize() + "&" + enrollmentListItemHeader.find( "form" ).serialize() + "&action=edit"
			, success: function( data ) {
					
					/* Assign display values based on their existing edit field values */
					enrollmentListItemDetails.find( "div.list_enrollment_leave_reason" ).text( enrollmentListItemDetails.find( "textarea.list_enrollment_leave_reason" ).val() );

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

					/* Renew the Last Update information. */
					enrollmentListItemDetails.find( ".list_enrollment_last_update_on" ).text( new Date( jQuery( data ).find( ".list_enrollment_last_update_on" ).text() ).toLocaleDateString() );
					enrollmentListItemDetails.find( ".list_enrollment_last_update_date" ).val( jQuery( data ).find( ".list_enrollment_last_update_date" ).val() );
					enrollmentListItemDetails.find( ".list_enrollment_last_update_by" ).text( jQuery( data ).find( ".list_enrollment_last_update_by" ).text() );
					enrollmentListItemDetails.find( ".list_enrollment_last_update_user" ).val( jQuery( data ).find( ".list_enrollment_last_update_user" ).val() );
					
					enrollmentListItemDetails.find( "select[name^='classAttended']" ).remove();
					enrollmentListItemDetails.find( "input[name^='boardingInd']" ).remove();
				}
			, error: function( jqXHR, textStatus, errorThrown ) {
					enrollmentListItemDetails.find( ".list_enrollment_save" ).toggle();
					enrollmentListItemDetails.find( ".list_enrollment_cancel" ).toggle();
					enrollmentListItemDetails.find( ".list_enrollment_save_wait" ).toggle();
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
						studentListItemHeader.find( "div.list_student_last_name" ).text( studentListItemHeader.find( "input.list_student_last_name" ).val() );
						studentListItemDetails.find( "div.list_student_birth_date" ).text( studentListItemDetails.find( "input.list_student_birth_date" ).val() );
						studentListItemDetails.find( "div.list_student_village" ).text( studentListItemDetails.find( "input.list_student_village" ).val() );
						studentListItemDetails.find( "div.list_student_special_info" ).text( studentListItemDetails.find( "textarea.list_student_special_info" ).val() );
						studentListItemDetails.find( "div.list_student_leave_reason" ).text( studentListItemDetails.find( "textarea.list_student_leave_reason" ).val() );

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
					}
				, error: function( jqXHR, textStatus, errorThrown ) {
						studentListItemDetails.find( ".list_student_save" ).toggle();
						studentListItemDetails.find( ".list_student_cancel" ).toggle();
						studentListItemDetails.find( ".list_student_save_wait" ).toggle();
						alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
					}
			}
	);
}

/* Toggle the display elements with editable elements. */
function toggleEnrollmentListItemElements( enrollmentListItemHeader, enrollmentListItemDetails ) {
	enrollmentListItemDetails.find( ".list_enrollment_edit" ).toggle();
	enrollmentListItemDetails.find( ".list_enrollment_delete" ).toggle();

	enrollmentListItemHeader.find( "div" ).not( ".list_enrollment_first_name, .list_enrollment_last_name, .list_enrollment_period, .list_enrollment_sponsored" ).toggle();
	enrollmentListItemHeader.find( "span.list_enrollment_leave_term" ).toggle();
	enrollmentListItemHeader.find( "input" ).toggle();
	enrollmentListItemHeader.find( "select" ).toggle();

	enrollmentListItemDetails.find( ".list_enrollment_leave_reason" ).toggle();
	enrollmentListItemDetails.find( ".list_enrollment_leave_reason_category" ).toggle();
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

	parentListItemHeader.find( "div" ).toggle();
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

	studentListItemHeader.find( "div" ).not( ".list_student_sponsored, .list_student_enrollment_period" ).toggle();
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