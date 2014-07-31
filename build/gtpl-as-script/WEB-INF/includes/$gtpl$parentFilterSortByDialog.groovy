package web_inf.includes;out.print("""<span class=\"parent_filter_sortby_dialog_filter_button glyphicons glyphicons_filter\"></span>
<span class=\"parent_filter_sortby_dialog_sort_button glyphicons glyphicons_sort\"></span>
<span class=\"parent_filter_sortby_dialog_asc_sort_direction_button glyphicons glyphicons_arrow_up\"></span>
<span class=\"parent_filter_sortby_dialog_dsc_sort_direction_button glyphicons glyphicons_arrow_down\"></span>
<span class=\"parent_filter_sortby_dialog_sort_order\"></span>
<span class=\"parent_filter_sortby_dialog_lookup_button ui-icon ui-icon-extlink\"></span>

<div class=\"parent_filter_sortby_dialog_filter_panel\">
	<form action=\"javascript:void( 0 )\" class=\"parent_filter_sortby_dialog_filter_form\">
		<table>
			<tr class=\"parent_filter_sortby_dialog_first_name_filter_row\">
				<td class=\"parent_filter_sortby_dialog_first_name_filter_label\">First Name</td>
				<td>:</td>
				<td>
					<input autocomplete=\"off\" class=\"parent_filter_sortby_dialog_first_name_filter ui-corner-all\" name=\"firstName\" type=\"text\" value=\"${ org.apache.commons.lang3.StringEscapeUtils.escapeHtml4( session.getAttribute( "parentFirstNameFilter" )?: "" ) }\" />
				</td>
			</tr>
			
			<tr class=\"parent_filter_sortby_dialog_last_name_filter_row\">
				<td class=\"parent_filter_sortby_dialog_last_name_filter_label\">Last Name</td>
				<td>:</td>
				<td>
					<input autocomplete=\"off\" class=\"parent_filter_sortby_dialog_last_name_filter ui-corner-all\" name=\"lastName\" type=\"text\" value=\"${ org.apache.commons.lang3.StringEscapeUtils.escapeHtml4( session.getAttribute( "parentLastNameFilter" )?: "" ) }\" />
				</td>
			</tr>
			
			<tr class=\"parent_filter_sortby_dialog_deceased_ind_filter_row\">
				<td class=\"parent_filter_sortby_dialog_deceased_ind_filter_label\">Deceased</td>
				<td>:</td>
				<td>
					<select autocomplete=\"off\" class=\"parent_filter_sortby_dialog_deceased_ind_filter ui-corner-all\" name=\"deceasedInd\">
					""");
						if( session.getAttribute( "parentDeceasedIndFilter" ) == "" ) { ;
out.print("""
							<option selected=\"selected\"></option>
					""");	}
						else { ;
out.print("""
							<option></option>
					""");	}
						if( session.getAttribute( "parentDeceasedIndFilter" ) == "Y" ) { ;
out.print("""
							<option selected=\"selected\">Y</option>
					""");	}
						else { ;
out.print("""
							<option>Y</option>
					""");	}
						if( session.getAttribute( "parentDeceasedIndFilter" ) == "N" ) { ;
out.print("""
							<option selected=\"selected\">N</option>
					""");	}
						else { ;
out.print("""
							<option>N</option>
					""");	} ;
out.print("""
					</select>
				</td>
			</tr>
			
			<tr class=\"parent_filter_sortby_dialog_village_filter_row\">
				<td class=\"parent_filter_sortby_dialog_village_filter_label\">Village</td>
				<td>:</td>
				<td>
					<input autocomplete=\"off\" class=\"parent_filter_sortby_dialog_village_filter ui-corner-all\" name=\"village\" type=\"text\" value=\"${ org.apache.commons.lang3.StringEscapeUtils.escapeHtml4( session.getAttribute( "parentVillageFilter" )?: "" ) }\" />
				</td>
			</tr>
			
			<tr class=\"parent_filter_sortby_dialog_primary_phone_filter_row\">
				<td class=\"parent_filter_sortby_dialog_primary_phone_filter_label\">Primary Phone</td>
				<td>:</td>
				<td>
					<input autocomplete=\"off\" class=\"parent_filter_sortby_dialog_primary_phone_filter ui-corner-all\" name=\"primaryPhone\" type=\"tel\" value=\"${ org.apache.commons.lang3.StringEscapeUtils.escapeHtml4( session.getAttribute( "parentPrimaryPhoneFilter" )?: "" ) }\" />
				</td>
			</tr>
			
			<tr class=\"parent_filter_sortby_dialog_email_filter_row\">
				<td class=\"parent_filter_sortby_dialog_email_filter_label\">Email</td>
				<td>:</td>
				<td>
					<input autocomplete=\"off\" class=\"parent_filter_sortby_dialog_email_filter ui-corner-all\" name=\"email\" type=\"email\" value=\"${ org.apache.commons.lang3.StringEscapeUtils.escapeHtml4( session.getAttribute( "parentEmailFilter" )?: "" ) }\" />
				</td>
			</tr>
			
			<tr class=\"parent_filter_sortby_dialog_secondary_phone_filter_row\">
				<td class=\"parent_filter_sortby_dialog_secondary_phone_filter_label\">Secondary Phone</td>
				<td>:</td>
				<td>
					<input autocomplete=\"off\" class=\"parent_filter_sortby_dialog_secondary_phone_filter ui-corner-all\" name=\"secondaryPhone\" type=\"tel\" value=\"${ org.apache.commons.lang3.StringEscapeUtils.escapeHtml4( session.getAttribute( "parentSecondaryPhoneFilter" )?: "" ) }\" />
				</td>
			</tr>
			
			<tr class=\"parent_filter_sortby_dialog_profession_filter_row\">
				<td class=\"parent_filter_sortby_dialog_profession_filter_label\">Profession</td>
				<td>:</td>
				<td>
					<input autocomplete=\"off\" class=\"parent_filter_sortby_dialog_profession_filter ui-corner-all\" name=\"profession\" type=\"text\" value=\"${ org.apache.commons.lang3.StringEscapeUtils.escapeHtml4( session.getAttribute( "parentProfessionFilter" )?: "" ) }\" />
				</td>
			</tr>
			
			<tr class=\"parent_filter_sortby_dialog_parent_id_filter_row\">
				<td class=\"parent_filter_sortby_dialog_parent_id_filter_label\">ID</td>
				<td>:</td>
				<td>
					<input autocomplete=\"off\" class=\"parent_filter_sortby_dialog_parent_id_filter ui-corner-all\" name=\"parentId\" type=\"text\" value=\"${ org.apache.commons.lang3.StringEscapeUtils.escapeHtml4( session.getAttribute( "parentIdFilter" )?: "" ) }\" />
				</td>
			</tr>
			
			<tr class=\"parent_filter_sortby_dialog_filter_more_row\">
				<td colspan=3>
					<table>
						<tr valign=\"middle\">
							<td>
								<div>
									<a class=\"parent_filter_sortby_dialog_filter_more\" href=\"javascript:void( 0 )\">more filters</a>
								</div>
							</td>
							<td>
								<div class=\"ui-icon ui-icon-carat-1-s\" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
			
			<tr class=\"parent_filter_sortby_dialog_filter_less_row\">
				<td colspan=3>
					<table>
						<tr valign=\"middle\">
							<td>
								<div>
									<a class=\"parent_filter_sortby_dialog_filter_less\" href=\"javascript:void( 0 )\">less filters</a>
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
					<a class=\"parent_filter_sortby_dialog_filter_apply\" href=\"javascript:void( 0 )\">apply</a>
					<img class=\"parent_filter_sortby_dialog_filter_apply_wait\" src=\"/images/ajax-loader.gif\" />
				</td>
			</tr>
		</table>
		
		<input name=\"action\" type=\"hidden\" value=\"setAttributes\" />
		<input name=\"kind\" type=\"hidden\" value=\"Parent\" />
	</form>
</div>""");
