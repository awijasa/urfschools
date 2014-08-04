package web_inf.includes;out.print("""""");
  LinkedList<com.google.appengine.api.datastore.Entity> userPrivilegeList = data.UserPrivilege.findByUserEmailAndPrivilege( user.getEmail(), "Modify" )
  
  html.form( action: "javascript:void( 0 )", id: "new_enrollment_form" ) {
    
    /* Student Selector */
    div( class: "add_enrollment_student_selector_label", "Enrollment of" )
    a( class: "add_enrollment_student_selector", href: "javascript:void( 0 )", "select" )
    input( autocomplete: "off", class: "add_enrollment_student_id", name: "studentId", type: "hidden" )
    img( class: "add_enrollment_student_selector_wait", src: "/images/ajax-loader.gif" )
  
    /* School List Box */
    div( class: "add_enrollment_school_required_ind", "*" )
    select( autocomplete: "off", class: "add_enrollment_school", name: "schoolName" ) {
      option( value: "School", "School" )
      
      userPrivilegeList.each( {
        option( value: "${ it.schoolName }", "${ it.schoolName }" )
      } )
    }
    
    /* Term List Boxes */
    div( class: "add_enrollment_term_required_ind", style: "display: none", "*" )
    
    /* The empty Last Enrollment Term List Box that appears when no School is selected. */
    select( autocomplete: "off", class: "add_enrollment_term", disabled: "disabled", id: "add_enrollment_term_school0", style: "display: none" ) {
      option( value: "Enrollment Term", "Enrollment Term" )
    }
    
    /* The Term List Boxes that appear when their corresponding Schools are selected. */
    userPrivilegeList.eachWithIndex( { userPrivilege, i ->
        select( autocomplete: "off", class: "add_enrollment_term", id: "add_enrollment_term_school${ i + 1 }", name: "enrollTermSchool${ i + 1 }", style: "display: none" ) {
          option( value: "Enrollment Term", "Enrollment Term" )
          
          data.Term.findByTermSchool( userPrivilege.schoolName ).each(
            {
              option( value: "${ it.year } Term ${ it.termNo }", "${ it.year } Term ${ it.termNo }" )
            }
          )
        }
      }
    )
    
    /* Leave Term List Boxes */
    select( autocomplete: "off", class: "add_enrollment_leave_term", disabled: "disabled", id: "add_enrollment_leave_term_school0", style: "display: none" ) {
      option( value: "Leave Term", "Leave Term" )
    }
    
    /* The Leave Term List Boxes that appear when their corresponding Schools are selected. */
    userPrivilegeList.eachWithIndex( { userPrivilege, i ->
        select( autocomplete: "off", class: "add_enrollment_leave_term", id: "add_enrollment_leave_term_school${ i + 1 }", name: "leaveTermSchool${ i + 1 }", style: "display: none" ) {
          option( value: "Leave Term", "Leave Term" )
          
          data.Term.findByTermSchool( userPrivilege.schoolName ).each(
            {
              option( value: "${ it.year } Term ${ it.termNo }", "${ it.year } Term ${ it.termNo }" )
            }
          )
        }
      }
    )
    
    /* First Class Attended List Boxes */
    div( class: "add_enrollment_first_class_attended_required_ind", style: "display: none", "*" )
    select( autocomplete: "off", class: "add_enrollment_first_class_attended", disabled: "disabled", id: "add_enrollment_first_class_attended_school0", style: "display: none" ) {
      option( value: "First Class Attended", "First Class Attended" )
    }
    
    /* The First Class Attended List Boxes that appear when their corresponding Schools are selected. */
    userPrivilegeList.eachWithIndex( { userPrivilege, i ->
        select( autocomplete: "off", class: "add_enrollment_first_class_attended", id: "add_enrollment_first_class_attended_school${ i + 1 }", name: "firstClassAttendedSchool${ i + 1 }", style: "display: none" ) {
          option( value: "First Class Attended", "First Class Attended" )
          
          data.Class.findBySchoolName( userPrivilege.schoolName ).each(
            {
              option( value: "${ it.getProperty( "class" ) }", "${ it.getProperty( "class" ) }" )
            }
          )
        }
      }
    )
    
    /* Last Class Attended List Boxes */
    div( class: "add_enrollment_last_class_attended_required_ind", style: "display: none", "*" )
    select( autocomplete: "off", class: "add_enrollment_last_class_attended", disabled: "disabled", id: "add_enrollment_last_class_attended_school0", style: "display: none" ) {
      option( value: "Last Class Attended", "Last Class Attended" )
    }
    
    /* The Last Class Attended List Boxes that appear when their corresponding Schools are selected. */
    userPrivilegeList.eachWithIndex( { userPrivilege, i ->
        select( autocomplete: "off", class: "add_enrollment_last_class_attended", id: "add_enrollment_last_class_attended_school${ i + 1}", name: "lastClassAttendedSchool${ i + 1 }", style: "display: none" ) {
          option( value: "Last Class Attended", "Last Class Attended" )
          
          data.Class.findBySchoolName( userPrivilege.schoolName ).each(
            {
              option( value: "${ it.getProperty( "class" ) }", "${ it.getProperty( "class" ) }" )
            }
          )
        }
      }
    )
    
    /* Leave Reason Category List Box */
    select( autocomplete: "off", class: "add_enrollment_leave_reason_category", name: "leaveReasonCategory") {
      option( value: "Leave Reason Category", "Leave Reason Category" )
      
      data.LeaveReasonCategory.list().each(
        {
          option( value: "${ it.category }", "${ it.category }" )
        }
      )
    }
    
    /* Leave Reason Text Area */
    div( class: "add_enrollment_leave_reason_label ui-corner-all", "Leave Reason" )
    textarea( autocomplete: "off", class: "add_enrollment_leave_reason", name: "leaveReason", "Leave Reason" )
    
    /* Add Enrollment Link */
    if( data.URFUser.findByEmail( user.getEmail() ) ) {
	    a( class: "add_enrollment_link", href: "javascript:void( 0 )", "add enrollment" )
	    img( class: "add_enrollment_link_wait", src: "/images/ajax-loader.gif" )
    }
    
    div( class: "required_symbol", "*" )
    div( class: "required_text", "required" )
    input( name: "action", type: "hidden", value: "save" )
  }
;
out.print("""

<script type=\"text/javascript\">
  var addEnrollmentListStudent = jQuery( \".add_enrollment_list_student\" );
    
  jQuery( \".add_enrollment_student_selector\" ).click(
    function() {
      if( jQuery( this ).text() == \"select\" ) {
      	var addEnrollmentListStudentFirstRequest = jQuery( \".add_enrollment_list_student_first_request\" );
      	
      	if( addEnrollmentListStudentFirstRequest.text() == \"Yes\" ) {
      		jQuery.ajax(
			  {
				url: \"/listStudent.gtpl\"
				, type: \"GET\"
				, data: jQuery.param(
					{
						limit: 20
					  , offset: 0
					  , windowScrollLeft: jQuery( window ).scrollLeft()
					  , windowScrollTop: jQuery( window ).scrollTop()
					}
				  )
				, beforeSend: function() {
					jQuery( \".add_enrollment_student_selector\" ).toggle();
					jQuery( \".add_enrollment_student_selector_wait\" ).toggle();
				  }
				, complete: function() {
					jQuery( \".add_enrollment_student_selector\" ).toggle();
					jQuery( \".add_enrollment_student_selector_wait\" ).toggle();
				  }
				, success: function( data ) {
					addEnrollmentListStudent.dialog( \"open\" );
					addEnrollmentListStudent.children().remove();
					addEnrollmentListStudent.append( data );
				  }
				, error: function( jqXHR, textStatus, errorThrown ) {
					alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
				  }
			  }
			);
      	
      		addEnrollmentListStudentFirstRequest.remove();
      	}
      	else {
        	addEnrollmentListStudent.dialog( \"open\" );
		}
      }
      else {
        jQuery.ajax(
          {
            url: \"/listStudent.gtpl\"
            , type: \"GET\"
            , data: jQuery.param(
                {
                  limit: 20
                  , offset: 0
                  , studentId: jQuery( this ).text()
                  , windowScrollLeft: jQuery( window ).scrollLeft()
				  , windowScrollTop: jQuery( window ).scrollTop()
                }
              )
            , beforeSend: function() {
                jQuery( \".add_enrollment_student_selector\" ).toggle();
                jQuery( \".add_enrollment_student_selector_wait\" ).toggle();
              }
            , complete: function() {
                jQuery( \".add_enrollment_student_selector\" ).toggle();
                jQuery( \".add_enrollment_student_selector_wait\" ).toggle();
              }
            , success: function( data ) {
                addEnrollmentListStudent.children().remove();
                addEnrollmentListStudent.append( data );
                addEnrollmentListStudent.dialog( \"open\" );
              }
            , error: function( jqXHR, textStatus, errorThrown ) {
                alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
              }
          }
        );
      }
    }
  );
  
  /* Bind a function to the School List Box's change event. */
  jQuery( \".add_enrollment_school\" ).change(
    function() {
      
      /* Display the correct Term List Box based on the selected School. */
      jQuery( \".add_enrollment_term\" ).css( \"display\", \"none\" );
      jQuery( \".add_enrollment_term option:selected\" ).prop( \"selected\", false );
      jQuery( \".add_enrollment_term option[value='Enrollment Term']\" ).prop( \"selected\", true );
      jQuery( \".add_enrollment_term_required_ind\" ).css( \"display\", \"block\" );
      jQuery( \"#add_enrollment_term_school\" + jQuery( \".add_enrollment_school option:selected\" ).index() ).css( \"display\", \"inline\" );
      
      /* Display the correct Leave Term List Box based on the selected School. */
      jQuery( \".add_enrollment_leave_term\" ).css( \"display\", \"none\" );
      jQuery( \".add_enrollment_leave_term option:selected\" ).prop( \"selected\", false );
      jQuery( \".add_enrollment_leave_term option[value='Leave Term']\" ).prop( \"selected\", true );
      jQuery( \"#add_enrollment_leave_term_school\" + jQuery( \".add_enrollment_school option:selected\" ).index() ).css( \"display\", \"inline\" );
      
      /* Display the correct First Class Attended List Box based on the selected School. */
      jQuery( \".add_enrollment_first_class_attended\" ).css( \"display\", \"none\" );
      jQuery( \".add_enrollment_first_class_attended option:selected\" ).prop( \"selected\", false );
      jQuery( \".add_enrollment_first_class_attended option[value='First Class Attended']\" ).prop( \"selected\", true );
      jQuery( \".add_enrollment_first_class_attended_required_ind\" ).css( \"display\", \"block\" );
      jQuery( \"#add_enrollment_first_class_attended_school\" + jQuery( \".add_enrollment_school option:selected\" ).index() ).css( \"display\", \"inline\" );
      
      /* Display the correct Last Class Attended List Box based on the selected School. */
      jQuery( \".add_enrollment_last_class_attended\" ).css( \"display\", \"none\" );
      jQuery( \".add_enrollment_last_class_attended option:selected\" ).prop( \"selected\", false );
      jQuery( \".add_enrollment_last_class_attended option[value='Last Class Attended']\" ).prop( \"selected\", true );
      jQuery( \".add_enrollment_last_class_attended_required_ind\" ).css( \"display\", \"block\" );
      jQuery( \"#add_enrollment_last_class_attended_school\" + jQuery( \".add_enrollment_school option:selected\" ).index() ).css( \"display\", \"inline\" );
    }
  );
    
  /* Initialize the Leave Reason Text Area. Refer to /js/fieldInit.js. */
  initTextAreaValueWithName( \".add_enrollment_leave_reason\", jQuery( \".add_enrollment_leave_reason\" ).val() );
  
  /* Initialize the Add Enrollment Link. */
  jQuery( \".add_enrollment_link\" ).click(
    function() {
    	var addEnrollmentSchool = jQuery( \".add_enrollment_school\" );
    	var schoolSelectedIndex = addEnrollmentSchool.find( \"option:selected\" ).index();
		var enrollmentTerm = jQuery( \"#add_enrollment_term_school\" + schoolSelectedIndex ).val();
		var enrollTermNo;
		var enrollTermYear;
		var leaveTerm = jQuery( \"#add_enrollment_leave_term_school\" + schoolSelectedIndex ).val();
		var leaveTermNo;
		var leaveTermYear;
		var addEnrollmentLink = jQuery( \".add_enrollment_link\" );
		var addEnrollmentLinkWait = jQuery( \".add_enrollment_link_wait\" );
		
		if( enrollmentTerm != \"Enrollment Term\" ) {
			enrollTermNo = enrollmentTerm.substring( 10 );
			enrollTermYear = enrollmentTerm.substring( 0, 4 );
		}
		
		if( leaveTerm == \"Leave Term\" ) {
			leaveTerm = \"now\"
		}
		else {
			leaveTermNo = leaveTerm.substring( 10 );
			leaveTermYear = leaveTerm.substring( 0, 4 );
		}
		
		jQuery.ajax(
				{
					url: \"/dialogClassAttended.gtpl\"
					, type: \"GET\"
					, data: jQuery.param(
							{
								action: \"save\"
								, termSchool: addEnrollmentSchool.val()
								, enrollTermNo: enrollTermNo
								, enrollTermYear: enrollTermYear
								, leaveTermNo: leaveTermNo
								, leaveTermYear: leaveTermYear
								, firstClassAttended: jQuery( \"#add_enrollment_first_class_attended_school\" + schoolSelectedIndex ).val()
								, lastClassAttended: jQuery( \"#add_enrollment_last_class_attended_school\" + schoolSelectedIndex ).val()
							}
						)
					, beforeSend: function() {
							addEnrollmentLink.hide();
							addEnrollmentLinkWait.show();
						}
					, complete: function() {
							addEnrollmentLink.show();
							addEnrollmentLinkWait.hide();
						}
					, success: function( data ) {
							var studentId = jQuery( \".add_enrollment_student_selector\" ).text();
							
							if( studentId == \"select\" ) {
								studentId = \"Anonymous\";
							}
							
							var dialogClassAttended = jQuery( \".dialog_class_attended\" );
							dialogClassAttended.dialog( \"option\", \"title\", \"Classes Attended by \" + studentId + \" from \" + enrollmentTerm + \" to \" + leaveTerm );
							dialogClassAttended.dialog( \"open\" );
							dialogClassAttended.children().remove();
							dialogClassAttended.append( data );
							
							jQuery( \".dialog_class_attended_save_link_bottom, .dialog_class_attended_save_link_top\" ).click(
								function() {
									var newEnrollmentForm = jQuery( \"#new_enrollment_form\" );
									
									dialogClassAttended.find( \"input, select\" ).each(
										function() {
											jQuery( this ).fadeOut();
											newEnrollmentForm.append( jQuery( this ) );
										}
									);
									
									jQuery.ajax(
								        {
								          url: \"EnrollmentController.groovy\"
								          , type: \"POST\"
								          , data: newEnrollmentForm.serialize()
								          , beforeSend: function() {
								            
								              /* Switch the \"add enrollment\" link with a spinning circle ball. */
								              jQuery( \".add_enrollment_link\" ).toggle()
								              jQuery( \".add_enrollment_link_wait\" ).toggle()
								              
								              dialogClassAttended.dialog( \"close\" );
								            }
								          , complete: function() {
								            
								              /* Switch the spinning circle ball with the \"add enrollment\" link. */
								              jQuery( \".add_enrollment_link\" ).toggle()
								              jQuery( \".add_enrollment_link_wait\" ).toggle()
								              
								              newEnrollmentForm.find( \"input[name^='boardingInd']\" ).remove();
								              newEnrollmentForm.find( \"select[name^='classAttended']\" ).remove();
								            }
								          , success: function( data ) {
								              var enrollmentList = jQuery( \".enrollment_list\" );
								              var enrollmentListH3 = enrollmentList.children( \"h3\" );
								              
								              if( enrollmentListH3.length == 20 ) {
								                enrollmentListH3.last().remove();
								                enrollmentList.children( \"div\" ).last().remove();
								                jQuery( \".list_enrollment_next_twenty\" ).css( \"display\", \"inline\" );
								              }
								            
								              /* Insert the newly added Enrollment to the top of the Enrollment list accordion. */
								              enrollmentList.prepend( data ).accordion( \"refresh\" ).accordion( \"option\", \"active\", 0 ).scrollTop( 0 );
								              
								              autoResizeFieldBasedOnWidth( enrollmentList.find( \".ui-accordion-header\" ).eq( 0 ), \"div.list_enrollment_first_name\", 150 );
								              
								              /* Refer to /js/listInit.js. */
								              initEnrollmentList();
								  
								              setTimeout(
								                function() {
								                  
								                  /* Scroll to the newly created Student record */
								                  jQuery( \"html, body\" ).animate(
								                    {
								                      scrollTop: jQuery( \"#enrollment_tab .list_record_form\" ).offset().top
								                    }
								                    , \"slow\"
								                  );
								                }
								                , 500
								              );
								            }
								          , error: function( jqXHR, textStatus, errorThrown ) {
								            
								            /* Display an error message popup if the AJAX call returned an error. */
								            alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
								          }
								        }
								      );
								}
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
