package web_inf.includes;out.print("""""");
	import com.google.appengine.api.datastore.Entity
	import data.AuthorizationException
	import data.Class
	import data.ClassAttended
	import data.InvalidClassLevelRangeException
	import data.Term
	import data.UserPrivilege
	import java.text.DecimalFormat
	
	/*
		Parameters:
		action
		studentId
		termSchool
		enrollTermNo
		enrollTermYear
		leaveTermNo
		leaveTermYear
		firstClassAttended
		lastClassAttended
	*/
	
	if( user == null )
		redirect "/index.gtpl"
	else {
		
		try {
			if( params.termSchool == "School" ) {
				
				/* Respond with an Internal Server error message if the School has not been specified. */
	            response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
	            response.setHeader( "Response-Phrase", "Please specify the School" )
			}
			else if( params.enrollTermNo == "" || params.enrollTermYear == "" ) {
					
				/* Respond with an Internal Server error message if the Enrollment Term has not been specified. */
	            response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
	            response.setHeader( "Response-Phrase", "Please specify the Enrollment Term" )
			}
			else if( params.firstClassAttended == "First Class Attended" ) {
				
				/* Respond with an Internal Server error message if the First Class Attended has not been specified. */
	            response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
	            response.setHeader( "Response-Phrase", "Please specify the First Class Attended" )
			}
			else if( params.lastClassAttended == "Last Class Attended" ) {
				
				/* Respond with an Internal Server error message if the First Class Attended has not been specified. */
	            response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
	            response.setHeader( "Response-Phrase", "Please specify the Last Class Attended" )
			}
			else if( params.termSchool == "Test" || UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), params.termSchool ) != null ) {
				List<Entity> classes = Class.findBySchoolName( params.termSchool )
				List<Entity> classesAttended
				int firstClassAttendedLevel
				int lastClassAttendedLevel
				
				if( params.action == "save" || params.action == "edit" ) {
					firstClassAttendedLevel = Class.findBySchoolNameAndClass( params.termSchool, params.firstClassAttended ).level
					lastClassAttendedLevel = Class.findBySchoolNameAndClass( params.termSchool, params.lastClassAttended ).level
					
					if( firstClassAttendedLevel > lastClassAttendedLevel )
						throw new InvalidClassLevelRangeException( "First Class Attended - Last Class Attended", params.firstClassAttended, params.lastClassAttended )
					
					classesAttended = Class.findBySchoolNameAndLevelRange( params.termSchool,
						firstClassAttendedLevel,
						lastClassAttendedLevel
					)
					
					html.a( class: "dialog_class_attended_save_link_top", href: "javascript:void( 0 )", "save" )
					html.a( class: "dialog_class_attended_cancel_link_top", href: "javascript:void( 0 )", "cancel" )
				}
				
				html.table {
					tr {
						
						if( params.action == "save" || params.action == "edit" ) {
							td( "Term" )
							td( "Class Attended" )
							td( "Boarding" )
						}
						else {
							td( "Term" )
							td( "Class Attended" )
							td( "Tuition Fee" )
							td( "Boarding Fee" )
							td( "Boarding" )
						}
					}
					
					Entity leaveTerm
				
					if( params.leaveTermNo == "" || params.leaveTermYear == "" )
						leaveTerm = null
					else
						leaveTerm = Term.findByTermSchoolAndTermNoAndYear( params.termSchool, Integer.parseInt( params.leaveTermNo ), Integer.parseInt( params.leaveTermYear ) )
					
					if( params.action == "save" || params.action == "edit" ) {
						Entity enrollmentTerm = Term.findByTermSchoolAndTermNoAndYear( params.termSchool, Integer.parseInt( params.enrollTermNo ), Integer.parseInt( params.enrollTermYear ) )
						
						Term.findYearsByTermSchoolAndTermRange( params.termSchool,
							enrollmentTerm,
							leaveTerm,
							new Date()
						).eachWithIndex(
							{ termYear, index ->
								Term.findByTermSchoolAndYearAndTermRange( params.termSchool, termYear, enrollmentTerm, leaveTerm, new Date() ).each(
									{ term ->
										tr( class: "dialog_class_attended_tr" ) {
											Entity actualClassAttended
											GString classTerm = "${ term.year } Term ${ term.termNo }"
											
											if( params.action == "edit" )
												actualClassAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( params.studentId, params.termSchool, term.termNo, term.year )
											
											td( classTerm )
											
											td {
												select( autocomplete: "off", name: "classAttended${ classTerm }" ) {
													classes.each(
														{																
															if( params.action == "edit" && actualClassAttended?.getProperty( "class" ) == it.getProperty( "class" ) && actualClassAttended?.classLevel >= firstClassAttendedLevel && actualClassAttended?.classLevel <= lastClassAttendedLevel )
																option( selected: "selected", value: it.getProperty( "class" ), it.getProperty( "class" ) )
															else if( index < classesAttended.size() && classesAttended.get( index ).getProperty( "class" ) == it.getProperty( "class" ) )
																option( selected: "selected", value: it.getProperty( "class" ), it.getProperty( "class" ) )
															else if( index >= classesAttended.size() && classesAttended.last().getProperty( "class" ) == it.getProperty( "class" ) )
																option( selected: "selected", value: it.getProperty( "class" ), it.getProperty( "class" ) )
															else
																option( value: it.getProperty( "class" ), it.getProperty( "class" ) )
														}
													)
												}
											}
											
											td {
												if( actualClassAttended == null || actualClassAttended.boardingInd == "Y" )
													input( autocomplete: "off", checked: "checked", name: "boardingInd${ classTerm }", type: "checkbox" )
												else
													input( autocomplete: "off", name: "boardingInd${ classTerm }", type: "checkbox" )
											}
										}
									}
								)
							}
						)
					}
					else {
						DecimalFormat currencyFormat = new DecimalFormat( "#,##0.00" )
						
						Term.findByTermSchoolAndTermRange( params.termSchool,
							Term.findByTermSchoolAndTermNoAndYear( params.termSchool, Integer.parseInt( params.enrollTermNo ), Integer.parseInt( params.enrollTermYear ) ),
							leaveTerm,
							new Date()
						).eachWithIndex(
							{ term, index ->
								tr( class: "dialog_class_attended_tr" ) {
									Entity classAttended = ClassAttended.findByStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( params.studentId, params.termSchool, term.termNo, term.year )
									
									td {
										a( class: "dialog_class_attended_term_link", href: "javascript:void( 0 )", "${ term.year } Term ${ term.termNo }" )
										span( "${ term.year } Term ${ term.termNo }" )
									}
									td( classAttended?.getProperty( "class" ) )
									td( classAttended == null? "": "UGX ${ currencyFormat.format( classAttended.tuitionFee ) }" )
									td( classAttended == null? "": "UGX ${ currencyFormat.format( classAttended.boardingFee ) }" )
									td( classAttended?.boardingInd?: "N" )
								}
							}
						)
					}
				}
				
				if( params.action == "save" || params.action == "edit" ) {
					html.a( class: "dialog_class_attended_save_link_bottom", href: "javascript:void( 0 )", "save" )
					html.a( class: "dialog_class_attended_cancel_link_bottom", href: "javascript:void( 0 )", "cancel" )
				}
				
				html.div( class: "dialog_class_attended_window_scroll_left", style: "display: none", params.windowScrollLeft )
  				html.div( class: "dialog_class_attended_window_scroll_top", style: "display: none", params.windowScrollTop )
			}
			else
				throw new AuthorizationException( params.termSchool )
		}
		catch( Exception e ) {
		
			/* Respond with an Internal Server error message if there is any Exception */
            response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
            response.setHeader( "Response-Phrase", e.getMessage() )
        }
	}
;
out.print("""

<script type=\"text/javascript\">
	jQuery( \".dialog_class_attended_cancel_link_bottom, .dialog_class_attended_cancel_link_top\" ).off( \"click\" ).click(
		function() {
			jQuery( \".dialog_class_attended\" ).dialog( \"close\" );
		}
	);
	
	/*
	 * Turn off the hovers previously bound to School list's rows, before binding the existing rows and the unbound new rows. Then, highlight the associated
	 * School record when the cursor hovers on a School list's row.
	 */
	if( navigator.userAgent.match( /(iPad)|(iPhone)|(iPod)|(android)|(webOS)/i ) ) {
		jQuery( \".dialog_class_attended tr\" ).not( \":eq( 0 )\" ).removeClass( \"dialog_class_attended_tr_hover\" ).addClass( \"dialog_class_attended_tr\" );
	}
	
	jQuery( \".dialog_class_attended tr\" ).not( \":eq( 0 )\" ).add( jQuery( \".add_payment_dialog_class_attended tr\" ).not( \":eq( 0 )\" ) ).off( \"hover\" ).hover(
			function() {
				jQuery( this ).removeClass( \"dialog_class_attended_tr\" ).addClass( \"dialog_class_attended_tr_hover\" );
			}
			, function() {
				jQuery( this ).removeClass( \"dialog_class_attended_tr_hover\" ).addClass( \"dialog_class_attended_tr\" );
			}
	);
	
	jQuery( \".dialog_class_attended_term_link\" ).off( \"click\" ).click(
		function() {
			if( jQuery( this ).parents( \".add_fee_dialog_class_attended\" ).length > 0 ) {
				jQuery( \".add_fee_term_selector\" ).text( jQuery( this ).text() );
				jQuery( \".add_fee_class_term_no\" ).val( jQuery( this ).text().substring( 10 ) );
				jQuery( \".add_fee_class_term_year\" ).val( jQuery( this ).text().substring( 0, 4 ) );
				jQuery( \".add_fee_dialog_class_attended\" ).dialog( \"close\" );
				
				var addFeeDialogClassAttended = jQuery( this ).parents( \".add_fee_dialog_class_attended\" );
				jQuery( \"html, body\" ).animate(
					{
						scrollLeft: addFeeDialogClassAttended.find( \".dialog_class_attended_window_scroll_left\" ).text()
						, scrollTop: addFeeDialogClassAttended.find( \".dialog_class_attended_window_scroll_top\" ).text()
					}
					, \"slow\"
				);
			}
			else if( jQuery( this ).parents( \".add_payment_dialog_class_attended\" ).length > 0 ) {
				jQuery( \".add_payment_term_selector\" ).text( jQuery( this ).text() );
				jQuery( \".add_payment_class_term_no\" ).val( jQuery( this ).text().substring( 10 ) );
				jQuery( \".add_payment_class_term_year\" ).val( jQuery( this ).text().substring( 0, 4 ) );
				jQuery( \".add_payment_dialog_class_attended\" ).dialog( \"close\" );
				
				var addPaymentDialogClassAttended = jQuery( this ).parents( \".add_payment_dialog_class_attended\" );
				jQuery( \"html, body\" ).animate(
					{
						scrollLeft: addPaymentDialogClassAttended.find( \".dialog_class_attended_window_scroll_left\" ).text()
						, scrollTop: addPaymentDialogClassAttended.find( \".dialog_class_attended_window_scroll_top\" ).text()
					}
					, \"slow\"
				);
			}
		}
	);
</script>""");
