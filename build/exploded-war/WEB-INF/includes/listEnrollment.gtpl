<%
  if( user == null ) redirect "/index.gtpl"
  else {
	html.a( class: "list_enrollment_first_name_label", href: "javascript:void( 0 )", "First Name" )
	html.a( class: "list_enrollment_last_name_label", href: "javascript:void( 0 )", "Last Name" )
	html.a( class: "list_enrollment_sponsored_label", href: "javascript:void( 0 )", "Sponsored" )
	html.a( class: "list_enrollment_classes_attended_label", href: "javascript:void( 0 )", "Classes Attended" )
	html.a( class: "list_enrollment_period_label", href: "javascript:void( 0 )", "Enrollment Period" )
	html.div( class: "actions_label", "Actions" )
	
	int limit = Integer.parseInt( params.limit )
	int offset = Integer.parseInt( params.offset )

  	if( offset > 0 ) {
	    html.a( class: "list_enrollment_prev_twenty", href: "javascript:void( 0 )", "Prev 20 Enrollments" )
	    html.img( class: "list_enrollment_prev_twenty_wait", src: "/images/ajax-loader.gif" )
	}
    
    html.div( class: "list_enrollment_prev_twenty_limit", style: "display: none;", limit )
    html.div( class: "list_enrollment_prev_twenty_offset", style: "display: none;", offset - limit )
      
	List<com.google.appengine.api.datastore.Entity> enrollmentWithStudentList
	
	html.div( class: "enrollment_list" ) {
	    enrollmentWithStudentList = data.EnrollmentWithStudent.findByLimitAndOffset( limit, offset )
	    enrollmentWithStudentList.eachWithIndex() { obj, i ->
	      if( i < limit )
	        mkp.yieldUnescaped( formatter.ListItemFormatter.getEnrollmentListItem( obj, null ) )
	    }
	}

	if( enrollmentWithStudentList.size() > limit )
  		html.a( class: "list_enrollment_next_twenty", href: "javascript:void( 0 )", "Next 20 Enrollments" )
	else
  		html.a( class: "list_enrollment_next_twenty", href: "javascript:void( 0 )", style: "display: none;", "Next 20 Enrollments" )
  		
	html.img( class: "list_enrollment_next_twenty_wait", src: "/images/ajax-loader.gif" )
  
  	html.div( class: "list_enrollment_next_twenty_limit", style: "display: none;", limit )
  	html.div( class: "list_enrollment_next_twenty_offset", style: "display: none;", offset + limit )
	}
%>

<script type="text/javascript">
  initEnrollmentList();
  
  var enrollmentTabListRecordForm = jQuery( "#enrollment_tab .list_record_form" );
  
  jQuery( ".list_enrollment_prev_twenty" ).click(
    function() {
      jQuery.ajax(
        {
          url: "/listEnrollment.gtpl"
          , type: "GET"
          , data: jQuery.param(
              {
                limit: jQuery( ".list_enrollment_prev_twenty_limit" ).text()
                , offset: jQuery( ".list_enrollment_prev_twenty_offset" ).text()
              }
            )
          , beforeSend: function() {
              jQuery( ".list_enrollment_prev_twenty" ).toggle();
              jQuery( ".list_enrollment_prev_twenty_wait" ).toggle();
            }
          , success: function( data ) {
              enrollmentTabListRecordForm.toggle();
              enrollmentTabListRecordForm.children().remove();
              enrollmentTabListRecordForm.append( data );
              enrollmentTabListRecordForm.toggle();
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
              alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
            }
        }
      );
    }
  );
  
  var listEnrollmentNextTwenty = jQuery( ".list_enrollment_next_twenty" );
  var listEnrollmentNextTwentyWait = jQuery( ".list_enrollment_next_twenty_wait" );
  
  listEnrollmentNextTwenty.click(
    function() {
      jQuery.ajax(
        {
          url: "/listEnrollment.gtpl"
          , type: "GET"
          , data: jQuery.param(
              {
                limit: jQuery( ".list_enrollment_next_twenty_limit" ).text()
                , offset: jQuery( ".list_enrollment_next_twenty_offset" ).text()
              }
            )
          , beforeSend: function() {
              listEnrollmentNextTwenty.toggle();
              listEnrollmentNextTwentyWait.toggle();
            }
          , success: function( data ) {
              enrollmentTabListRecordForm.toggle();
              enrollmentTabListRecordForm.children().remove();
              enrollmentTabListRecordForm.append( data );
              enrollmentTabListRecordForm.toggle();
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
              alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
            }
        }
      );
    }
  );
  
  var enrollmentList = jQuery( ".enrollment_list" );
    
  if( navigator.userAgent.match( /(android)|(iPad)|(iPhone)|(iPod)|(webOS)/i ) ) {
    enrollmentList.accordion(
      {
        collapsible: false
        , activate: function( event, ui ) {
	      		jQuery( ".list_enrollment_period_lookup" ).fadeOut();
	      		ui.newHeader.find( ".list_enrollment_period_lookup" ).fadeIn();
	      	}
      }
    ).show();
  
    enrollmentList.css( "overflow", "visible" );
    enrollmentList.css( "height", "1450px" );
    enrollmentTabListRecordForm.css( "height", "1450px" );
    listEnrollmentNextTwenty.css( "top", "1425px" )
    listEnrollmentNextTwentyWait.css( "top", "1425px" )
    enrollmentList.find( ".list_enrollment_period_lookup" ).eq( 0 ).fadeIn();
  }
  else {
    enrollmentList.accordion(
      {
        active: false
        , collapsible: true
        , activate: function( event, ui ) {
	      		jQuery( ".list_enrollment_period_lookup" ).fadeOut();
	      		ui.newHeader.find( ".list_enrollment_period_lookup" ).fadeIn();
	      	}
      }
    ).show();
    
    enrollmentList.scroll(
      function() {
        
        /* If Scroll Bar position + computer screen's height >= web page's header + body heights OR Scroll Bar position = the top of Enrollment List section */
        if( jQuery( window ).scrollTop() + jQuery( window ).height() >= enrollmentTabListRecordForm.offset().top + enrollmentTabListRecordForm.height() ||
          jQuery( window ).scrollTop == enrollmentTabListRecordForm.offset().top ) {
          enrollmentList.css( "overflow", "auto" );
        }
        else {
          enrollmentList.css( "overflow", "hidden" );
        }
      }
    );

    jQuery( window ).scroll(
      function() {
        
        /* If Scroll Bar position + computer screen's height >= web page's header + body heights OR Scroll Bar position = the top of Enrollment List section */
        if( jQuery( window ).scrollTop() + jQuery( window ).height() >= enrollmentTabListRecordForm.offset().top + enrollmentTabListRecordForm.height() ||
          jQuery( window ).scrollTop == enrollmentTabListRecordForm.offset().top ) {
          enrollmentList.css( "overflow", "auto" );
        }
        else {
          enrollmentList.css( "overflow", "hidden" );
        }
      }
    );
  }
</script>