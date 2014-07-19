package web_inf.includes;out.print("""<span class=\"student_filter_sortby_dialog_filter_button glyphicons glyphicons_filter\"></span>
<span class=\"student_filter_sortby_dialog_sort_button glyphicons glyphicons_sort\"></span>
<span class=\"student_filter_sortby_dialog_asc_sort_direction_button glyphicons glyphicons_arrow_up\"></span>
<span class=\"student_filter_sortby_dialog_dsc_sort_direction_button glyphicons glyphicons_arrow_down\"></span>
<span class=\"student_filter_sortby_dialog_sort_order\"></span>
<span class=\"student_filter_sortby_dialog_lookup_button ui-icon ui-icon-extlink\"></span>

<div class=\"student_filter_sortby_dialog_filter_panel\">
	<form action=\"javascript:void( 0 )\" class=\"student_filter_sortby_dialog_filter_form\">
		<table>
			<tr class=\"student_filter_sortby_dialog_first_name_filter_row\">
				<td class=\"student_filter_sortby_dialog_first_name_filter_label\">First Name</td>
				<td>:</td>
				<td>
					<input autocomplete=\"off\" class=\"student_filter_sortby_dialog_first_name_filter ui-corner-all\" name=\"firstName\" type=\"text\" value=\"${ org.apache.commons.lang3.StringEscapeUtils.escapeHtml4( session.getAttribute( "studentFirstNameFilter" )?: "" ) }\" />
				</td>
			</tr>
			
			<tr class=\"student_filter_sortby_dialog_last_name_filter_row\">
				<td class=\"student_filter_sortby_dialog_last_name_filter_label\">Last Name</td>
				<td>:</td>
				<td>
					<input autocomplete=\"off\" class=\"student_filter_sortby_dialog_last_name_filter ui-corner-all\" name=\"lastName\" type=\"text\" value=\"${ org.apache.commons.lang3.StringEscapeUtils.escapeHtml4( session.getAttribute( "studentLastNameFilter" )?: "" ) }\" />
				</td>
			</tr>
			
			<tr class=\"student_filter_sortby_dialog_classes_attended_filter_row\">
				<td class=\"student_filter_sortby_dialog_classes_attended_filter_label\">Classes Attended</td>
				<td>:</td>
				<td>
					<input autocomplete=\"off\" class=\"student_filter_sortby_dialog_classes_attended_filter ui-corner-all\" name=\"classesAttended\" type=\"text\" value=\"${ org.apache.commons.lang3.StringEscapeUtils.escapeHtml4( session.getAttribute( "studentClassesAttendedFilter" )?: "" ) }\" />
				</td>
			</tr>
			
			<tr class=\"student_filter_sortby_dialog_fees_due_filter_row\">
				<td class=\"student_filter_sortby_dialog_fees_due_filter_label\">Fees Due</td>
				<td>
					<select autocomplete=\"off\" class=\"student_filter_sortby_dialog_fees_due_filter_operator\" name=\"feesDueOperator\">
					""");
						if( session.getAttribute( "studentFeesDueFilterOperator" ) == "=" ) { ;
out.print("""
							<option selected=\"selected\">=</option>
					""");	}
						else { ;
out.print("""
							<option>=</option>
					""");	}
						if( session.getAttribute( "studentFeesDueFilterOperator" ) == ">" ) { ;
out.print("""
							<option selected=\"selected\">&gt;</option>
					""");	}
						else { ;
out.print("""
							<option>&gt;</option>
					""");	}
						if( session.getAttribute( "studentFeesDueFilterOperator" ) == "<" ) { ;
out.print("""
							<option selected=\"selected\">&lt;</option>
					""");	}
						else { ;
out.print("""
							<option>&lt;</option>
					""");	} ;
out.print("""
					</select>
				</td>
			
				<td>
					<table>
						<tr valign=\"middle\">
							<td class=\"student_filter_sortby_dialog_fees_due_filter_currency\">UGX</td>
							<td>
								<input autocomplete=\"off\" class=\"student_filter_sortby_dialog_fees_due_filter ui-corner-all\" name=\"feesDue\" type=\"text\" value=\"${ org.apache.commons.lang3.StringEscapeUtils.escapeHtml4( session.getAttribute( "studentFeesDueFilter" )? new java.text.DecimalFormat( "#,##0.00" ).format( session.getAttribute( "studentFeesDueFilter" ) ): "" ) }\" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
			
			<tr class=\"student_filter_sortby_dialog_filter_more_row\">
				<td colspan=3>
					<table>
						<tr valign=\"middle\">
							<td>
								<div>
									<a class=\"student_filter_sortby_dialog_filter_more\" href=\"javascript:void( 0 )\">more filters</a>
								</div>
							</td>
							<td>
								<div class=\"ui-icon ui-icon-carat-1-s\" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
			
			<tr class=\"student_filter_sortby_dialog_filter_less_row\">
				<td colspan=3>
					<table>
						<tr valign=\"middle\">
							<td>
								<div>
									<a class=\"student_filter_sortby_dialog_filter_less\" href=\"javascript:void( 0 )\">less filters</a>
								</div>
							</td>
							<td>
								<div class=\"ui-icon ui-icon-carat-1-n\" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
			
			<tr>
				<td align=\"right\" colspan=3>
					<a class=\"student_filter_sortby_dialog_filter_apply\" href=\"javascript:void( 0 )\">apply</a>
					<img class=\"student_filter_sortby_dialog_filter_apply_wait\" src=\"/images/ajax-loader.gif\" />
				</td>
			</tr>
		</table>
		
		<input name=\"action\" type=\"hidden\" value=\"setAttributes\" />
		<input name=\"kind\" type=\"hidden\" value=\"Student\" />
	</form>
</div>""");
