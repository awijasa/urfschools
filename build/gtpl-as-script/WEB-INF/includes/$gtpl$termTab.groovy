package web_inf.includes;out.print("""""");
	if( user == null ) redirect "/index.gtpl"
	else {
;
out.print("""
		<div id=\"term_tab\">
		  <div class=\"new_record_form ui-corner-all\">
		    """);
/* include#begin \WEB-INF\includes\addTerm.gtpl */
	html.form( action: "TermController.groovy", id: "new_term_form" ) {

		List<com.google.appengine.api.datastore.Entity> userPrivileges = data.UserPrivilege.findByUserEmail( user.getEmail() )
		
		/* School List Box */
		div( class: "add_term_school_required_ind", "*" )
		select( autocomplete: "off", class: "add_term_school", name: "termSchool" ) {
		  option( "School" )
		  
		  userPrivileges.each(
		  	{
		    	option( value: it.schoolName, it.schoolName )
		  	}
		  )
		}
		  
		/* Term No Text Field */
		div( class: "add_term_no_required_ind", "*" )
		div( class: "add_term_no_label ui-corner-all", "Term No" )
		input( autocomplete: "off", class: "add_term_no", name: "termNo", type: "text", value: "Term No" )
		  
		/* Year Text Field */
		div( class: "add_term_year_required_ind", "*" )
		div( class: "add_term_year_label ui-corner-all", "Year" )
		input( autocomplete: "off", class: "add_term_year", name: "year", type: "text", value: "Year" )
		  
		/* Start Date Text Field */
		div( class: "add_term_start_date_required_ind", "*" )
		div( class: "add_term_start_date_label ui-corner-all", "Start Date" )
		input( autocomplete: "off", class: "add_term_start_date", name: "startDate", type: "text", value: "Start Date" )
		  
		/* End Date Text Field */
		div( class: "add_term_end_date_required_ind", "*" )
		div( class: "add_term_end_date_label ui-corner-all", "End Date" )
		input( autocomplete: "off", class: "add_term_end_date", name: "endDate", type: "text", value: "End Date" )
		  
		table( class: "add_term_class_fees_header" ) {
			tr {
				td( "Class" )
				td( "Tuition Fee" )
				td( "Boarding Fee" )
			}
		}
		
		userPrivileges.eachWithIndex(
			{ userPrivilege, userPrivilegeIndex ->
				div( class: "add_term_class_fees", id: "add_term_school${ userPrivilegeIndex + 1 }_class_fees" ) {
				  	table {
				  		data.Class.findBySchoolName( userPrivilege.schoolName ).eachWithIndex(
				  			{ schoolClass, schoolClassIndex ->
						  		tr {
						  			td( schoolClass.getProperty( "class" ) )
						  			td {
										/* Tuition Fee Text Field */
										span( class: "add_term_tuition_fee_currency", "UGX" )
										input( autocomplete: "off", class: "add_term_tuition_fee ui-corner-all", name: "tuitionFee${ schoolClass.schoolName }${ schoolClass.getProperty( "class" ) }", type: "text" )
									}
								  
								  	td {
										/* Boarding Fee Text Field */
										span( class: "add_term_boarding_fee_currency", "UGX" )
										input( autocomplete: "off", class: "add_term_boarding_fee ui-corner-all", name: "boardingFee${ schoolClass.schoolName }${ schoolClass.getProperty( "class" ) }", type: "text" )
									}
								}
							}
						)
					}
				}
			}
		)
		  
		if( data.URFUser.findByEmail( user.getEmail() )?.adminPrivilege == "Modify" ) {
		
		  /* Add Term Link */
		  a( class: "add_term_link", href: "javascript:void( 0 )", "add term" )
		  img( class: "add_term_link_wait", src: "/images/ajax-loader.gif" )
		}
		  
		div( class: "required_symbol", "*" )
		div( class: "required_text", "required" )
		input( name: "action", type: "hidden", value: "save" )
	}
;
out.print("""

<script type=\"text/javascript\">
  
  /* Initialize the Term No Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( \".add_term_no\", jQuery( \".add_term_no\" ).val() );
  
  /* Initialize the Year Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( \".add_term_year\", jQuery( \".add_term_year\" ).val() );
  
  /* Initialize the Start Date Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( \".add_term_start_date\", jQuery( \".add_term_start_date\" ).val() );
  
  /* Initialize the End Date Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( \".add_term_end_date\", jQuery( \".add_term_end_date\" ).val() );
  
  jQuery( \".add_term_school\" ).change(
  	function() {
  		var addTermClassFees = jQuery( \".add_term_class_fees\" ).hide();
  		var selectedSchoolIndex = jQuery( this ).find( \"option:selected\" ).index();
  		
  		jQuery( \"#add_term_school\" + selectedSchoolIndex + \"_class_fees\" ).show();
  		
  		if( selectedSchoolIndex == 0 ) {
  			jQuery( \".add_term_class_fees_header\" ).hide();
  		}
  		else {
  			var addTermClassFeesHeader = jQuery( \".add_term_class_fees_header\" );
  			
  			addTermClassFeesHeader.find( \"td\" ).eq( 0 ).width( addTermClassFees.find( \"table\" ).eq( selectedSchoolIndex - 1 ).find( \"td\" ).eq( 0 ).width() );
  			addTermClassFeesHeader.find( \"td\" ).eq( 1 ).width( addTermClassFees.find( \"table\" ).eq( selectedSchoolIndex - 1 ).find( \"td\" ).eq( 1 ).width() );
  			addTermClassFeesHeader.find( \"td\" ).eq( 2 ).width( addTermClassFees.find( \"table\" ).eq( selectedSchoolIndex - 1 ).find( \"td\" ).eq( 2 ).width() );
  			jQuery( \".add_term_class_fees_header\" ).show();
  		}
  	}
  );
  
  /* Initialize the Start Date and End Date Picker. */
  jQuery( \".add_term_start_date, .add_term_end_date\" ).datepicker(
    {
      changeMonth: true
      , changeYear: true
      , dateFormat: \"M d yy\"
      , maxDate: \"+3Y\"
      , onSelect: function( dateText, inst ) {
        jQuery( this ).css( \"color\", \"#000000\" );
        jQuery( this ).css( \"font-style\", \"normal\" );
      }
      , yearRange: \"-40:+3\"
    }
  );
  
  /* Initialize the Add Term link. */
  jQuery( \".add_term_link\" ).click(
    function() {
      jQuery.ajax(
        {
          url: \"TermController.groovy\"
          , type: \"POST\"
          , data: jQuery( \"#new_term_form\" ).serialize()
          , beforeSend: function() {
            
              /* Switch the \"add term\" link with a spinning circle ball. */
              jQuery( \".add_term_link\" ).toggle()
              jQuery( \".add_term_link_wait\" ).toggle()
            }
          , complete: function() {
            
              /* Switch the spinning circle ball with the \"add term\" link. */
              jQuery( \".add_term_link\" ).toggle()
              jQuery( \".add_term_link_wait\" ).toggle()
            }
          , success: function( data ) {
              var termList = jQuery( \".term_list\" );
              var termListH3 = termList.children( \"h3\" );
              
              if( termListH3.length == 20 ) {
                termListH3.last().remove();
                termList.children( \"div\" ).last().remove();
                jQuery( \".list_term_next_twenty\" ).css( \"display\", \"inline\" );
              }
            
              /* Insert the newly added Term to the top of the Term list accordion. */
              termList.prepend( data ).accordion( \"refresh\" ).accordion( \"option\", \"active\", 0 ).scrollTop( 0 );
              
              /* Refer to /js/listInit.js. */
              initTermList();
  
              setTimeout(
                function() {
                  jQuery( \"html, body\" ).animate(
                    {
                      scrollTop: jQuery( \"#term_tab .list_record_form\" ).offset().top
                    }
                    , \"slow\"
                  );
                }
                , 500
              );
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
            alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
          }
        }
      );
    }
  );
</script>""");
/* include#end   \WEB-INF\includes\addTerm.gtpl */
out.print("""
		  </div>
		
		  <div class=\"new_record_radio\">
		        <input type=\"radio\" id=\"add_term_radio\" name=\"new_term_radio\" checked=\"checked\" /><label for=\"add_term_radio\">add new</label>
		        <input type=\"radio\" id=\"upload_terms_radio\" name=\"new_term_radio\" /><label for=\"upload_terms_radio\">upload</label>
		  </div>
		
		  <button class=\"middle_filter\">filter</button>
		  <button class=\"middle_sortby\">sort by</button>
		
		  <div class=\"list_record_form ui-corner-all\">
		    """); include '/WEB-INF/includes/listTerm.gtpl?limit=20&offset=0' ;
out.print("""
		  </div>
		
		  <div class=\"list_record_radio\">
		        <input type=\"radio\" id=\"list_terms_radio\" name=\"list_term_radio\" checked=\"checked\" /><label for=\"list_terms_radio\">list</label>
		        <input type=\"radio\" id=\"download_terms_radio\" name=\"list_term_radio\" /><label for=\"download_terms_radio\">download</label>
		  </div>
		</div>
"""); } ;
out.print("""

<script type=\"text/javascript\">
  var filterSortByDialogLeftPosition;
  
  filterSortByDialogLeftPosition = jQuery( \".main_content\" ).position().left + 144;
  
  var middleFilterSortByDialog = jQuery( \".middle_filter_sortby_dialog\" )

  var middleFilterSortBy = jQuery( \"#term_tab\" ).find( \".middle_filter, .middle_sortby\" );

  middleFilterSortBy.button({
      icons: {
        secondary: \"ui-icon-carat-1-s\"
      }
  })
    .click(
      function() {
        if( middleFilterSortByDialog.dialog( \"isOpen\" ) ) {
          middleFilterSortByDialog.dialog( \"close\" );
        }
        else {
          middleFilterSortByDialog.dialog( \"option\", \"position\",
          	{
          		my: \"left-294 top+13.5\"
          		, of: jQuery( \"#term_tab .middle_sortby\" )
          		, collision: \"fit\"
          	}
          );
          middleFilterSortByDialog.dialog( \"open\" );
        }
      }
    );
      
  var downloadRadioDialog = jQuery( \".download_radio_dialog\" );

  /* Initialize the list - download radio buttons. */
  var listRecordRadio = jQuery( \"#term_tab .list_record_radio\" ).buttonset();
  
  listRecordRadio.find( \"input:radio\" ).eq( 0 ).click(
    function() {
      if( downloadRadioDialog.dialog( \"isOpen\" ) ) {
        downloadRadioDialog.dialog( \"close\" );
      }
    }
  );
  
  listRecordRadio.find( \"input:radio\" ).eq( 1 ).click(
    function() {
      if( downloadRadioDialog.dialog( \"isOpen\" ) ) {
        downloadRadioDialog.dialog( \"close\" );
      }
      else {
        downloadRadioDialog.dialog( \"option\", \"position\",
        	{
          		my: \"left top+28\"
          		, of: jQuery( this )
          		, collision: \"fit\"
          	}
        );
        downloadRadioDialog.dialog( \"open\" );
      }
    }
  );

  var uploadRadioDialog = jQuery( \".upload_radio_dialog\" );

  /* Initialize the add new - upload radio buttons. */
  var newRecordRadio = jQuery( \"#term_tab .new_record_radio\" ).buttonset();
  
  newRecordRadio.find( \"input:radio\" ).eq( 0 ).click(
    function() {
      if( uploadRadioDialog.dialog( \"isOpen\" ) ) {
        uploadRadioDialog.dialog( \"close\" );
      }
    }
  );
  
  newRecordRadio.find( \"input:radio\" ).eq( 1 ).click(
    function() {
      if( uploadRadioDialog.dialog( \"isOpen\" ) ) {
        uploadRadioDialog.dialog( \"close\" );
      }
      else {
        uploadRadioDialog.dialog( \"option\", \"position\",
        	{
          		my: \"left top+28\"
          		, of: jQuery( this )
          		, collision: \"fit\"
          	}
        );
        uploadRadioDialog.dialog( \"open\" );
      }
    }
  );
</script>""");
