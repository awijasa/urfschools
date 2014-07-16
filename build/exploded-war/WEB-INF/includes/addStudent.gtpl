<%
	List<com.google.appengine.api.datastore.Entity> userPrivilegeList = data.UserPrivilege.findByUserEmailAndPrivilege( user.getEmail(), "Modify" )
	
	html.form( action: "javascript:void( 0 )", id: "new_student_form" ) {
  
	  /* First Name Text Field */
	  div( class: "add_student_first_name_required_ind", "*" )
	  div( class: "add_student_first_name_label ui-corner-all", "First Name" )
	  input( autocomplete: "off", class: "add_student_first_name", name: "firstName", type: "text", value: "First Name" )
	  
	  /* Last Name Text Field */
	  div( class: "add_student_last_name_label ui-corner-all", "Last Name" )
	  input( autocomplete: "off", class: "add_student_last_name", name: "lastName", type: "text", value: "Last Name" )
	  
	  /* Birth Date Text Field */
	  div( class: "add_student_birth_date_label ui-corner-all", "Birth Date" )
	  input( autocomplete: "off", class: "add_student_birth_date", name: "birthDate", type: "text", value: "Birth Date" )
	  
	  /* Village Text Field */
	  div( class: "add_student_village_label ui-corner-all", "Village" )
	  input( autocomplete: "off", class: "add_student_village", name: "village", type: "text", value: "Village" )
	  
	  /* Add Student Link */
	  if( data.URFUser.findByEmail( user.getEmail() ) ) {
		  a( class: "add_student_link", href: "javascript:void( 0 )", "add student" )
		  img( class: "add_student_link_wait", src: "/images/ajax-loader.gif" )
	  }
	  
	  /* Anonymous Parent Information */
	  div( class: "add_student_parents_label", "Parents" )
	  table( class: "add_student_parent_deceased_table" ) {
	    data.ParentalRelationship.list().each(
	    	{ parentalRelationship ->
		        tr {
		          td( parentalRelationship.parentalRelationship )
		          td( ": Deceased" )
		          td {
		            input( id: "add_student_${ parentalRelationship.parentalRelationship }_deceased_toggle", name: "anonymous${ parentalRelationship.parentalRelationship }DeceasedInd", type: "checkbox" )
		          }
		        }
	    	}
    	)
	  }
	  
	  /* Gender List Box */
	  select( autocomplete: "off", class: "add_student_gender", name: "genderCode" ) {
	    option( "Gender" )
	    
	    data.Gender.list().each(
	    	{ gender ->
	      		option( value: gender.code, gender.desc )
	    	}
    	)
	  }
	  
	  /* Special Info/Condition Text Area */
	  div( class: "add_student_special_info_label ui-corner-all", "Special Info/Condition" )
	  textarea( autocomplete: "off", class: "add_student_special_info", name: "specialInfo", "Special Info/Condition" )
	  
	  /* Enrollment Section */
	  div( class: "add_student_enrollment_label", "Last Enrollment" )
	  
	  /* Last Enrollment School List Box */
	  div( class: "add_student_school_required_ind", "*" )
	  select( autocomplete: "off", class: "add_student_school", name: "enrollmentSchool" ) {
	    option( "School" )
	    
	    userPrivilegeList.each(
	    	{ userPrivilege ->
	        	option( value: userPrivilege.schoolName, userPrivilege.schoolName )
	    	}
    	)
	  }
	  
	  /* Last Enrollment Term List Boxes */
	  div( class: "add_student_enrollment_term_required_ind", style: "display: none", "*" )
	  
	  /* The empty Last Enrollment Term List Box that appears when no School is selected. */
	  select( autocomplete: "off", class: "add_student_enrollment_term", disabled: "disabled", id: "add_student_enrollment_term_school0", style: "display: none" ) {
	    option( value: "Enrollment Term", "Enrollment Term" )
	  }
	  
	  /* The Last Enrollment Term List Boxes that appear when their corresponding Schools are selected. */
	  userPrivilegeList.eachWithIndex(
	  	{ userPrivilege, i ->
	    	select( autocomplete: "off", class: "add_student_enrollment_term", id: "add_student_enrollment_term_school${ i + 1 }", name: "enrollmentTermSchool${ i + 1 }", style: "display: none" ) {
	      		option( value: "Enrollment Term", "Enrollment Term" )
	      
		        data.Term.findByTermSchool( userPrivilege.schoolName ).each(
		        	{ term ->
		          		option( value: "${ term.year } Term ${ term.termNo }", "${ term.year } Term ${ term.termNo }" )
	          		}
	          	)
	    	}
	  	}
	  )
	  
	  /* Last Enrollment Leave Term List Boxes */
	  select( autocomplete: "off", class: "add_student_leave_term", disabled: "disabled", id: "add_student_leave_term_school0", style: "display: none" ) {
	    option( value: "Leave Term", "Leave Term" )
	  }
	  
	  /* The Last Enrollment Leave Term List Boxes that appear when their corresponding Schools are selected. */
	  userPrivilegeList.eachWithIndex(
	  	{ userPrivilege, i ->
		    select( autocomplete: "off", class: "add_student_leave_term", id: "add_student_leave_term_school${ i + 1 }", name: "leaveTermSchool${ i + 1 }", style: "display: none" ) {
		      	option( value: "Leave Term", "Leave Term" )
		      
		      	data.Term.findByTermSchool( userPrivilege.schoolName ).each(
		      		{ term ->
		          		option( value: "${ term.year } Term ${ term.termNo }", "${ term.year } Term ${ term.termNo }" )
		      		}
	      		)
		    }
	  	}
	  )
	  
	  /* Last Enrollment First Class Attended List Boxes */
	  div( class: "add_student_first_class_attended_required_ind", style: "display: none", "*" )
	  select( autocomplete: "off", class: "add_student_first_class_attended", disabled: "disabled", id: "add_student_first_class_attended_school0", style: "display: none" ) {
	    option( value: "First Class Attended", "First Class Attended" )
	  }
	  
	  /* The Last Enrollment First Class Attended List Boxes that appear when their corresponding Schools are selected. */
	  userPrivilegeList.eachWithIndex(
	  	{ userPrivilege, i ->
		    select( autocomplete: "off", class: "add_student_first_class_attended", id: "add_student_first_class_attended_school${ i + 1 }", name: "enrollmentFirstClassAttendedSchool${ i + 1 }", style: "display: none" ) {
		    	option( value: "First Class Attended", "First Class Attended" )
		      
		        data.Class.findBySchoolName( userPrivilege.schoolName ).each(
		        	{ schoolClass ->
		        		option( value: schoolClass.getProperty( "class" ), schoolClass.getProperty( "class" ) )
	        		}
		      	)
		    }
	  	}
	  )
	  
	  /* Last Enrollment Last Class Attended List Boxes */
	  div( class: "add_student_last_class_attended_required_ind", style: "display: none", "*" )
	  select( autocomplete: "off", class: "add_student_last_class_attended", disabled: "disabled", id: "add_student_last_class_attended_school0", style: "display: none" ) {
	    option( value: "Last Class Attended", "Last Class Attended" )
	  }
	  
	  /* The Last Enrollment Last Class Attended List Boxes that appear when their corresponding Schools are selected. */
	  userPrivilegeList.eachWithIndex(
	  	{ userPrivilege, i ->
		    select( autocomplete: "off", class: "add_student_last_class_attended", id: "add_student_last_class_attended_school${ i + 1 }", name: "enrollmentLastClassAttendedSchool${ i + 1 }", style: "display: none" ) {
		      	option( value: "Last Class Attended", "Last Class Attended" )
		      
		        data.Class.findBySchoolName( userPrivilege.schoolName ).each(
		        	{ schoolClass ->
		          		option( value: schoolClass.getProperty( "class" ), schoolClass.getProperty( "class" ) )
	          		}
		      	)
		    }
	    }
	  )
	  
	  /* Last Enrollment Leave Reason Category List Boxes */
	  select( autocomplete: "off", class: "add_student_leave_reason_category", name: "enrollmentLeaveReasonCategory" ) {
	    option( "Leave Reason Category" )
	    
	    data.LeaveReasonCategory.list().each(
	    	{ leaveReasonCategory ->
	        	option( value: leaveReasonCategory.category, leaveReasonCategory.category )
	    	}
    	)
	  }
	  
	  /* Leave Reason Text Area */
	  div( class: "add_student_leave_reason_label ui-corner-all", "Leave Reason" )
	  textarea( autocomplete: "off", class: "add_student_leave_reason", name: "enrollmentLeaveReason", "Leave Reason" )
	  
	  div( class: "required_symbol", "*" )
	  div( class: "required_text", "required" )
	  input( name: "action", type: "hidden", value: "save" )
	}
%>

<script type="text/javascript">
  
  /* Initialize the First Name Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( ".add_student_first_name", jQuery( ".add_student_first_name" ).val() );
  
  /* Initialize the Last Name Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( ".add_student_last_name", jQuery( ".add_student_last_name" ).val() );
  
  /* Initialize the Birth Date Picker. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( ".add_student_birth_date", jQuery( ".add_student_birth_date" ).val() );
  
  jQuery( ".add_student_birth_date" ).datepicker(
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
  
  /* Initialize the Village Text Field. Refer to /js/fieldInit.js. */
  initTextFieldValueWithName( ".add_student_village", jQuery( ".add_student_village" ).val() );
  
  /* Initialize the Special Info Text Field. Refer to /js/fieldInit.js. */
  initTextAreaValueWithName( ".add_student_special_info", jQuery( ".add_student_special_info" ).val() );
  
  /* Bind a function to the School List Box's change event. */
  jQuery( ".add_student_school" ).change(
    function() {
      
      /* Display the correct Last Enrollment Term List Box based on the selected School. */
      jQuery( ".add_student_enrollment_term" ).css( "display", "none" );
      jQuery( ".add_student_enrollment_term option:selected" ).prop( "selected", false );
      jQuery( ".add_student_enrollment_term option[value='Enrollment Term']" ).prop( "selected", true );
      jQuery( ".add_student_enrollment_term_required_ind" ).css( "display", "block" );
      jQuery( "#add_student_enrollment_term_school" + jQuery( ".add_student_school option:selected" ).index() ).css( "display", "inline" );
      
      /* Display the correct Last Enrollment Leave Term List Box based on the selected School. */
      jQuery( ".add_student_leave_term" ).css( "display", "none" );
      jQuery( ".add_student_leave_term option:selected" ).prop( "selected", false );
      jQuery( ".add_student_leave_term option[value='Leave Term']" ).prop( "selected", true );
      jQuery( "#add_student_leave_term_school" + jQuery( ".add_student_school option:selected" ).index() ).css( "display", "inline" );
      
      /* Display the correct Last Enrollment First Class Attended List Box based on the selected School. */
      jQuery( ".add_student_first_class_attended" ).css( "display", "none" );
      jQuery( ".add_student_first_class_attended option:selected" ).prop( "selected", false );
      jQuery( ".add_student_first_class_attended option[value='First Class Attended']" ).prop( "selected", true );
      jQuery( ".add_student_first_class_attended_required_ind" ).css( "display", "block" );
      jQuery( "#add_student_first_class_attended_school" + jQuery( ".add_student_school option:selected" ).index() ).css( "display", "inline" );
      
      /* Display the correct Last Enrollment Last Class Attended List Box based on the selected School. */
      jQuery( ".add_student_last_class_attended" ).css( "display", "none" );
      jQuery( ".add_student_last_class_attended option:selected" ).prop( "selected", false );
      jQuery( ".add_student_last_class_attended option[value='Last Class Attended']" ).prop( "selected", true );
      jQuery( ".add_student_last_class_attended_required_ind" ).css( "display", "block" );
      jQuery( "#add_student_last_class_attended_school" + jQuery( ".add_student_school option:selected" ).index() ).css( "display", "inline" );
    }
  );
    
  /* Initialize the Leave Reason Text Area. Refer to /js/fieldInit.js. */
  initTextAreaValueWithName( ".add_student_leave_reason", jQuery( ".add_student_leave_reason" ).val() );
  
  /* Initialize the Add Student Link. */
  jQuery( ".add_student_link" ).click(
    function() {
    	var addStudentSchool = jQuery( ".add_student_school" );
    	var schoolSelectedIndex = addStudentSchool.find( "option:selected" ).index();
		var enrollmentTerm = jQuery( "#add_student_enrollment_term_school" + schoolSelectedIndex ).val();
		var enrollTermNo;
		var enrollTermYear;
		var leaveTerm = jQuery( "#add_student_leave_term_school" + schoolSelectedIndex ).val();
		var leaveTermNo;
		var leaveTermYear;
		var addStudentLink = jQuery( ".add_student_link" );
		var addStudentLinkWait = jQuery( ".add_student_link_wait" );
		
		if( enrollmentTerm != "Enrollment Term" ) {
			enrollTermNo = enrollmentTerm.substring( 10 );
			enrollTermYear = enrollmentTerm.substring( 0, 4 );
		}
		
		if( leaveTerm == "Leave Term" ) {
			leaveTerm = "now"
		}
		else {
			leaveTermNo = leaveTerm.substring( 10 );
			leaveTermYear = leaveTerm.substring( 0, 4 );
		}
		
		jQuery.ajax(
				{
					url: "/dialogClassAttended.gtpl"
					, type: "GET"
					, data: jQuery.param(
							{
								action: "save"
								, termSchool: addStudentSchool.val()
								, enrollTermNo: enrollTermNo
								, enrollTermYear: enrollTermYear
								, leaveTermNo: leaveTermNo
								, leaveTermYear: leaveTermYear
								, firstClassAttended: jQuery( "#add_student_first_class_attended_school" + schoolSelectedIndex ).val()
								, lastClassAttended: jQuery( "#add_student_last_class_attended_school" + schoolSelectedIndex ).val()
							}
						)
					, beforeSend: function() {
							addStudentLink.hide();
							addStudentLinkWait.show();
						}
					, complete: function() {
							addStudentLink.show();
							addStudentLinkWait.hide();
						}
					, success: function( data ) {
							var firstName = jQuery( ".add_student_first_name" ).val();
							var lastName = jQuery( ".add_student_last_name" ).val();
							var studentName;
							
							if( firstName == "First Name" ) {
								studentName = "Anonymous";
							}
							else if( lastName == "Last Name" ) {
								studentName = firstName;
							}
							else {
								studentName = firstName + " " + lastName;
							}
							var dialogClassAttended = jQuery( ".dialog_class_attended" );
							dialogClassAttended.dialog( "option", "title", "Classes Attended by " + studentName + " from " + enrollmentTerm + " to " + leaveTerm );
							dialogClassAttended.dialog( "open" );
							dialogClassAttended.children().remove();
							dialogClassAttended.append( data );
							
							jQuery( ".dialog_class_attended_save_link_bottom, .dialog_class_attended_save_link_top" ).click(
								function() {
									var newStudentForm = jQuery( "#new_student_form" );
									
									dialogClassAttended.find( "input, select" ).each(
										function() {
											jQuery( this ).fadeOut();
											newStudentForm.append( jQuery( this ) );
										}
									);
									
									jQuery.ajax(
								        {
								          url: "StudentController.groovy"
								          , type: "POST"
								          , data: newStudentForm.serialize()
								          , beforeSend: function() {
								          
								              /* Switch the "add student" link with a spinning circle ball. */
								              jQuery( ".add_student_link" ).toggle()
								              jQuery( ".add_student_link_wait" ).toggle()
								              
								              dialogClassAttended.dialog( "close" );
								            }
								          , complete: function() {
								            
								              /* Switch the spinning circle ball with the "add student" link. */
								              jQuery( ".add_student_link" ).toggle()
								              jQuery( ".add_student_link_wait" ).toggle()
								              
								              newStudentForm.find( "input[name^='boardingInd']" ).remove();
								              newStudentForm.find( "select[name^='classAttended']" ).remove();
								            }
								          , success: function( data ) {
								              var studentList = jQuery( ".list_record_form .student_list" );
								              var studentListH3 = studentList.children( "h3" );
								              
								              if( studentListH3.length == 20 ) {
								                studentListH3.last().remove();
								                studentList.children( "div" ).last().remove();
								                jQuery( ".list_record_form .list_student_next_twenty" ).css( "display", "inline" );
								              }
								            
								              /* Insert the newly added Student to the top of the Student list accordion. */
								              studentList.prepend( data ).accordion( "refresh" ).accordion( "option", "active", 0 ).scrollTop( 0 );
								              
								              /* Refer to /js/listInit.js. */
								              initStudentList();
								  
								              setTimeout(
								                function() {
								                  
								                  /* Scroll to the newly created Student record */
								                  jQuery( "html, body" ).animate(
								                    {
								                      scrollTop: jQuery( ".list_record_form" ).offset().top
								                    }
								                    , "slow"
								                  );
								                }
								                , 500
								              );
								            }
								          , error: function( jqXHR, textStatus, errorThrown ) {
								            
								            /* Display an error message popup if the AJAX call returned an error. */
								            alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
								          }
								        }
								     );
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
</script>