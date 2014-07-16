package web_inf.includes;out.print("""""");
	html.span( class: "enrollment_filter_sortby_dialog_filter_button glyphicons glyphicons_filter" )
	html.span( class: "enrollment_filter_sortby_dialog_sort_button glyphicons glyphicons_sort" )
	html.span( class: "enrollment_filter_sortby_dialog_asc_sort_direction_button glyphicons glyphicons_arrow_up" )
	html.span( class: "enrollment_filter_sortby_dialog_dsc_sort_direction_button glyphicons glyphicons_arrow_down" )
	html.span( class: "enrollment_filter_sortby_dialog_sort_order" )
	html.span( class: "enrollment_filter_sortby_dialog_lookup_button ui-icon ui-icon-extlink" )
	
	html.div( class: "enrollment_filter_sortby_dialog_filter_panel" ) {
		form( action: "javascript:void( 0 )", class: "enrollment_filter_sortby_dialog_filter_form" ) {
			table {
				tr( class: "enrollment_filter_sortby_dialog_first_name_filter_row" ) {
					td( class: "enrollment_filter_sortby_dialog_first_name_filter_label", "First Name" )
				
					td( ":" )
				
					td {
						input( autocomplete: "off", class: "enrollment_filter_sortby_dialog_first_name_filter ui-corner-all", name: "firstName", type: "text", value: session.getAttribute( "enrollmentFirstNameFilter" )?: "" )
					}
				}
				
				tr( class: "enrollment_filter_sortby_dialog_last_name_filter_row" ) {
					td( class: "enrollment_filter_sortby_dialog_last_name_filter_label", "Last Name" )
				
					td( ":" )
				
					td {
						input( autocomplete: "off", class: "enrollment_filter_sortby_dialog_last_name_filter ui-corner-all", name: "lastName", type: "text", value: session.getAttribute( "enrollmentLastNameFilter" )?: "" )
					}
				}
				
				tr( class: "enrollment_filter_sortby_dialog_fees_due_filter_row" ) {
					td( class: "enrollment_filter_sortby_dialog_fees_due_filter_label", "Fees Due" )
				
					td {
						select( autocomplete: "off", class: "enrollment_filter_sortby_dialog_fees_due_filter_operator", name: "feesDueOperator" ) {
							if( session.getAttribute( "enrollmentFeesDueFilterOperator" ) == "=" )
								option( selected: "selected", "=" )
							else
								option( "=" )
							
							if( session.getAttribute( "enrollmentFeesDueFilterOperator" ) == ">" )
								option( selected: "selected", ">" )
							else
								option( ">" )
								
							if( session.getAttribute( "enrollmentFeesDueFilterOperator" ) == "<" )
								option( selected: "selected", "<" )
							else
								option( "<" )
						}
					}
				
					td {
						table {
							tr( valign: "middle" ) {
								td( class: "enrollment_filter_sortby_dialog_fees_due_filter_currency", "UGX" )
								td {
									input( autocomplete: "off", class: "enrollment_filter_sortby_dialog_fees_due_filter ui-corner-all", name: "feesDue", type: "text", value: session.getAttribute( "enrollmentFeesDueFilter" )? new java.text.DecimalFormat( "#,##0.00" ).format( session.getAttribute( "enrollmentFeesDueFilter" ) ): "" )
								}
							}
						}
					}
				}
				
				tr( class: "enrollment_filter_sortby_dialog_filter_more_row" ) {
					td( colspan: 3 ) {
						table {
							tr( valign: "middle" ) {
								td {
									div {
										a( class: "enrollment_filter_sortby_dialog_filter_more", href: "javascript:void( 0 )", "more filters" )
									}
								}
								td {
									div( class: "ui-icon ui-icon-carat-1-s" )
								}
							}
						}
					}
				}
				
				tr( class: "enrollment_filter_sortby_dialog_filter_less_row" ) {
					td( colspan: 3 ) {
						table {
							tr( valign: "middle" ) {
								td {
									div {
										a( class: "enrollment_filter_sortby_dialog_filter_less", href: "javascript:void( 0 )", "less filters" )
									}
								}
								td {
									div( class: "ui-icon ui-icon-carat-1-n" )
								}
							}
						}
					}
				}
				
				tr {
					td( align: "right", colspan: 3 ) {
						a( class: "enrollment_filter_sortby_dialog_filter_apply", href: "javascript:void( 0 )", "apply" )
						img( class: "enrollment_filter_sortby_dialog_filter_apply_wait", src: "/images/ajax-loader.gif" )
					}
				}
			}
			
			input( name: "action", type: "hidden", value: "setAttributes" )
			input( name: "kind", type: "hidden", value: "Enrollment" )
		}
	}
;
