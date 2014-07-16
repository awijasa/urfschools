<%
  if( user == null ) redirect "/index.gtpl"
  else {

	html.a( class: "list_student_first_name_label", href: "javascript:void( 0 )", "First Name" )
	html.a( class: "list_student_last_name_label", href: "javascript:void( 0 )", "Last Name" )
	html.a( class: "list_student_sponsored_label", href: "javascript:void( 0 )", "Sponsored" )
	html.a( class: "list_student_classes_attended_label", href: "javascript:void( 0 )", "Classes Attended" )
	html.a( class: "list_student_enrollment_period_label", href: "javascript:void( 0 )", "Enrollment Period" )
	html.div( class: "actions_label", "Actions" )

	  int activeSeqNo = 0
	  int limit = Integer.parseInt( params.limit )
	  int offset
	  
	  if( params.offset != null )
	    offset = Integer.parseInt( params.offset )
	    
	  if( params.studentId != null && params.isLookup == null ) {
	    offset = 0
	    
	    List<com.google.appengine.api.datastore.Entity> studentWithLastEnrollmentList = data.StudentWithLastEnrollment.findByLimitAndOffset( limit, offset )
	    
	    while( studentWithLastEnrollmentList.size() > 0 ) {
	      boolean isOffsetFound = false
	      int sizeOfListDisplayed = studentWithLastEnrollmentList.size() == limit + 1? studentWithLastEnrollmentList.size() - 1: studentWithLastEnrollmentList.size()
	    
	      for( int i = 0; i < sizeOfListDisplayed; i++ ) {
	        com.google.appengine.api.datastore.Entity studentWithLastEnrollment = studentWithLastEnrollmentList.get( i )
	
	        if( studentWithLastEnrollment.studentId.equals( params.studentId ) ) {
	          activeSeqNo = i
	          isOffsetFound = true
	          break
	        }
	      }
	
	      if( isOffsetFound )
	        break
	
	      offset += limit
	
	      studentWithLastEnrollmentList = data.StudentWithLastEnrollment.findByLimitAndOffset( limit, offset )
	    }
	  }
	
	  if( offset > 0 ) {
	    html.a( class: "list_student_prev_twenty", href: "javascript:void( 0 )", "Prev 20 Students" )
	    html.img( class: "list_student_prev_twenty_wait", src: "/images/ajax-loader.gif" )
	  }
	    
	    html.div( class: "list_student_prev_twenty_limit", style: "display: none;", limit )
	    html.div( class: "list_student_prev_twenty_offset", style: "display: none;", offset - limit )
	      
		List<com.google.appengine.api.datastore.Entity> studentWithLastEnrollmentList
		
		html.div( class: "student_list" ) {
	  		if( params.isLookup == "Y" ) {
		  		studentWithLastEnrollmentList = new ArrayList()
				mkp.yieldUnescaped( formatter.ListItemFormatter.getStudentListItem( data.StudentWithLastEnrollment.findByStudentId( params.studentId ), null ) )
			}
			else {
				studentWithLastEnrollmentList = data.StudentWithLastEnrollment.findByLimitAndOffset( limit, offset )
				studentWithLastEnrollmentList.eachWithIndex() { obj, i ->
				  if( i < limit )
					mkp.yieldUnescaped( formatter.ListItemFormatter.getStudentListItem( obj, null ) )
				}
			}	
		}
	
		if( studentWithLastEnrollmentList.size() > limit )
	  		html.a( class: "list_student_next_twenty", href: "javascript:void( 0 )", "Next 20 Students" )
		else
	  		html.a( class: "list_student_next_twenty", href: "javascript:void( 0 )", style: "display: none;", "Next 20 Students" )
	
	  html.img( class: "list_student_next_twenty_wait", src: "/images/ajax-loader.gif" )
	  
	  html.div( class: "list_student_next_twenty_limit", style: "display: none;", limit )
	  html.div( class: "list_student_next_twenty_offset", style: "display: none;", offset + limit )
	  
	  html.div( class: "list_student_active_seq_no", style: "display: none;", activeSeqNo )
  }
%>
<script type="text/javascript">
  initStudentList();
  
  var studentTabListRecordForm = jQuery( "#student_tab .list_record_form" );
  
  jQuery( ".list_student_prev_twenty" ).off( "click" ).click(
    function() {
      var listStudentPrevTwenty = jQuery( this );
      var listStudent = listStudentPrevTwenty.parent();
      var listStudentPrevTwentyLimit = listStudent.find( ".list_student_prev_twenty_limit" );
      var listStudentPrevTwentyOffset = listStudent.find( ".list_student_prev_twenty_offset" );
      var listStudentPrevTwentyWait = listStudent.find( ".list_student_prev_twenty_wait" );
      
      jQuery.ajax(
        {
          url: "/listStudent.gtpl"
          , type: "GET"
          , data: jQuery.param(
              {
                limit: listStudentPrevTwentyLimit.text()
                , offset: listStudentPrevTwentyOffset.text()
              }
            )
          , beforeSend: function() {
              listStudentPrevTwenty.toggle();
              listStudentPrevTwentyWait.toggle();
            }
          , success: function( data ) {
              listStudent.toggle();
              listStudent.children().remove();
              listStudent.append( data );
              listStudent.toggle();
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
              alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
            }
        }
      );
    }
  );
  
  jQuery( ".list_student_next_twenty" ).off( "click" ).click(
    function() {
      var listStudentNextTwenty = jQuery( this );
      var listStudent = listStudentNextTwenty.parent();
      var listStudentNextTwentyLimit = listStudent.find( ".list_student_next_twenty_limit" );
      var listStudentNextTwentyOffset = listStudent.find( ".list_student_next_twenty_offset" );
      var listStudentNextTwentyWait = listStudent.find( ".list_student_next_twenty_wait" );
    
      jQuery.ajax(
        {
          url: "/listStudent.gtpl"
          , type: "GET"
          , data: jQuery.param(
              {
                limit: listStudentNextTwentyLimit.text()
                , offset: listStudentNextTwentyOffset.text()
              }
            )
          , beforeSend: function() {
              listStudentNextTwenty.toggle();
              listStudentNextTwentyWait.toggle();
            }
          , success: function( data ) {
              listStudent.toggle();
              listStudent.children().remove();
              listStudent.append( data );
              listStudent.toggle();
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
              alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
            }
        }
      );
    }
  );
  
  var studentLists = jQuery( ".student_list" );
  
  studentLists.each(
        function() {
          if( jQuery( this ).parent().hasClass( "list_record_form" ) ) {
            if( navigator.userAgent.match( /(android)|(iPad)|(iPhone)|(iPod)|(webOS)/i ) ) {
              var studentList = jQuery( this );
              
              jQuery( this ).accordion(
                {
                  collapsible: false
                  , activate: function( event, ui ) {
                  		studentList.find( ".list_student_enrollment_period_lookup" ).fadeOut();
                  		ui.newHeader.find( ".list_student_enrollment_period_lookup" ).fadeIn();
                  	}
                }
              ).show();

              jQuery( this ).css( "overflow", "visible" );
              jQuery( ".list_record_form .student_list" ).css( "height", "1450px" );
              jQuery( this ).parent().css( "height", "1450px" );
              jQuery( ".list_record_form .list_student_next_twenty" ).css( "top", "1425px" )
              jQuery( ".list_record_form .list_student_next_twenty_wait" ).css( "top", "1425px" )
              jQuery( this ).find( ".list_student_enrollment_period_lookup" ).eq( 0 ).fadeIn();
            }
            else {
              var studentList = jQuery( this );
              
              jQuery( this ).accordion(
                {
                  active: false
                  , collapsible: true
                  , activate: function( event, ui ) {
                  		studentList.find( ".list_student_enrollment_period_lookup" ).fadeOut();
                  		ui.newHeader.find( ".list_student_enrollment_period_lookup" ).fadeIn();
                  	}
                }
              ).show();

              jQuery( this ).scroll(
                function() {

                  /* If Scroll Bar position + computer screen's height >= web page's header + body heights OR Scroll Bar position = the top of Student List section */
                  if( jQuery( window ).scrollTop() + jQuery( window ).height() >= studentTabListRecordForm.offset().top + studentTabListRecordForm.height() ||
                    jQuery( window ).scrollTop == studentTabListRecordForm.offset().top ) {
                    studentList.css( "overflow", "auto" );
                  }
                  else {
                    studentList.css( "overflow", "hidden" );
                  }
                }
              );

              jQuery( window ).scroll(
                function() {

                  /* If Scroll Bar position + computer screen's height >= web page's header + body heights OR Scroll Bar position = the top of Student List section */
                  if( jQuery( window ).scrollTop() + jQuery( window ).height() >= studentTabListRecordForm.offset().top + studentTabListRecordForm.height() ||
                    jQuery( window ).scrollTop == studentTabListRecordForm.offset().top ) {
                    studentList.css( "overflow", "auto" );
                  }
                  else {
                    studentList.css( "overflow", "hidden" );
                  }
                }
              );
            }
          }
          else {
          	var activeSeqNo = parseInt( jQuery( this ).parent().find( ".list_student_active_seq_no" ).text() );
            var studentList = jQuery( this );
            
            jQuery( this ).accordion(
              {
                active: activeSeqNo
                , collapsible: false
                , activate: function( event, ui ) {
                  		studentList.find( ".list_student_enrollment_period_lookup" ).fadeOut();
                  		ui.newHeader.find( ".list_student_enrollment_period_lookup" ).fadeIn();
                  	}
              }
            ).show();
            
            jQuery( this ).find( ".list_student_enrollment_period_lookup" ).eq( activeSeqNo ).fadeIn();
          }
        }
  );
</script>