<%
  if( user == null ) redirect "/index.gtpl"
  else {

	html.a( class: "list_payment_student_id_label", href: "javascript:void( 0 )", "Student ID" )
	html.a( class: "list_payment_funding_source_label", href: "javascript:void( 0 )", "Funding Source" )
	html.a( class: "list_payment_term_label", href: "javascript:void( 0 )", "Term" )
	html.a( class: "list_payment_amount_label", href: "javascript:void( 0 )", "Amount" )
	html.div( class: "actions_label", "Actions" )

	  int activeSeqNo = 0
	  int limit = Integer.parseInt( params.limit )
	  int offset
	  
	  if( params.offset != null )
	    offset = Integer.parseInt( params.offset )
	    
	  if( params.studentId != null && params.studentId != "" &&
	  	params.fundingSource != null && params.fundingSource != "" &&
	  	params.schoolName != null && params.schoolName != "" &&
	  	params.classTermNo != null && params.classTermNo != "" &&
	  	params.classTermYear != null && params.classTermYear != "" &&
	  	params.isLookup == null ) {
	    offset = 0
	    
	    List<com.google.appengine.api.datastore.Entity> paymentList = data.Payment.findByLimitAndOffset( limit, offset )
	    
	    while( paymentList.size() > 0 ) {
	      boolean isOffsetFound = false
	      int sizeOfListDisplayed = paymentList.size() == limit + 1? paymentList.size() - 1: paymentList.size()
	    
	      for( int i = 0; i < sizeOfListDisplayed; i++ ) {
	        com.google.appengine.api.datastore.Entity payment = paymentList.get( i )
	
	        if( payment.studentId == params.studentId &&
	        	payment.fundingSource == params.fundingSource &&
	        	payment.schoolName == params.schoolName &&
	        	payment.classTermNo == Integer.parseInt( params.classTermNo ) &&
	        	payment.classTermYear == Integer.parseInt( params.classTermYear ) ) {
	          activeSeqNo = i
	          isOffsetFound = true
	          break
	        }
	      }
	
	      if( isOffsetFound )
	        break
	
	      offset += limit
	
	      paymentList = data.Payment.findByLimitAndOffset( limit, offset )
	    }
	  }
	
	  if( offset > 0 ) {
	    html.a( class: "list_payment_prev_twenty", href: "javascript:void( 0 )", "Prev ${ limit } Payments" )
	    html.img( class: "list_payment_prev_twenty_wait", src: "/images/ajax-loader.gif" )
	  }
	    
	    html.div( class: "list_payment_prev_twenty_limit", style: "display: none;", limit )
	    html.div( class: "list_payment_prev_twenty_offset", style: "display: none;", offset - limit )
	      
		List<com.google.appengine.api.datastore.Entity> paymentList
		
		html.div( class: "payment_list" ) {
	  		if( params.isLookup == "Y" ) {
		  		paymentList = new ArrayList()
				mkp.yieldUnescaped( formatter.ListItemFormatter.getPaymentListItem( data.Payment.findByFundingSourceAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear(
					params.fundingSource,
					params.studentId,
					params.schoolName,
					Integer.parseInt( params.classTermNo ),
					Integer.parseInt( params.classTermYear )
				) ) )
			}
			else if( params.studentId != null && params.studentId != "" &&
				params.schoolName != null && params.schoolName != "" &&
				params.enrollTermNo != null && params.enrollTermNo != "" &&
				params.enrollTermYear != null && params.enrollTermYear != "" ) {
				paymentList = data.Payment.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYearAndLimitAndOffset(
					params.studentId,
					params.schoolName,
					Integer.parseInt( params.enrollTermNo ),
					Integer.parseInt( params.enrollTermYear ),
					limit,
					offset
				)
				
				paymentList.eachWithIndex(
					{ obj, i ->
						if( i < limit )
							mkp.yieldUnescaped( formatter.ListItemFormatter.getPaymentListItem( obj ) )
					}
				)
			}
			else {
				paymentList = data.Payment.findByLimitAndOffset( limit, offset )
				paymentList.eachWithIndex(
					{ obj, i ->
					  if( i < limit )
						mkp.yieldUnescaped( formatter.ListItemFormatter.getPaymentListItem( obj ) )
					}
				)
			}	
		}
	
		if( paymentList.size() > limit )
	  		html.a( class: "list_payment_next_twenty", href: "javascript:void( 0 )", "Next ${ limit } Payments" )
		else
	  		html.a( class: "list_payment_next_twenty", href: "javascript:void( 0 )", style: "display: none;", "Next ${ limit } Payments" )
	
	  html.img( class: "list_payment_next_twenty_wait", src: "/images/ajax-loader.gif" )
	  
	  html.div( class: "list_payment_next_twenty_limit", style: "display: none;", limit )
	  html.div( class: "list_payment_next_twenty_offset", style: "display: none;", offset + limit )
	  
	  html.div( class: "list_payment_active_seq_no", style: "display: none;", activeSeqNo )
	  html.div( class: "list_payment_student_id_param", style: "display: none;", params.studentId?: "" )
	  html.div( class: "list_payment_school_name_param", style: "display: none;", params.schoolName?: "" )
	  html.div( class: "list_payment_enroll_term_no_param", style: "display: none;", params.enrollTermNo?: "" )
	  html.div( class: "list_payment_enroll_term_year_param", style: "display: none;", params.enrollTermYear?: "" )
  }
%>
<script type="text/javascript">
  initPaymentList();
  
  var paymentTabListRecordForm = jQuery( "#payment_tab .list_record_form" );
  
  jQuery( ".list_payment_prev_twenty" ).off( "click" ).click(
    function() {
      var listPaymentPrevTwenty = jQuery( this );
      var listPayment = listPaymentPrevTwenty.parent();
      var listPaymentPrevTwentyLimit = listPayment.find( ".list_payment_prev_twenty_limit" );
      var listPaymentPrevTwentyOffset = listPayment.find( ".list_payment_prev_twenty_offset" );
      var listPaymentPrevTwentyWait = listPayment.find( ".list_payment_prev_twenty_wait" );
      
      jQuery.ajax(
        {
          url: "/listPayment.gtpl"
          , type: "GET"
          , data: jQuery.param(
              {
                limit: listPaymentPrevTwentyLimit.text()
                , offset: listPaymentPrevTwentyOffset.text()
                , studentId: listPayment.find( ".list_payment_student_id_param" ).text()
                , schoolName: listPayment.find( ".list_payment_school_name_param" ).text()
                , enrollTermNo: listPayment.find( ".list_payment_enroll_term_no_param" ).text()
                , enrollTermYear: listPayment.find( ".list_payment_enroll_term_year_param" ).text()
              }
            )
          , beforeSend: function() {
              listPaymentPrevTwenty.toggle();
              listPaymentPrevTwentyWait.toggle();
            }
          , success: function( data ) {
              listPayment.toggle();
              listPayment.children().remove();
              listPayment.append( data );
              listPayment.toggle();
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
        	  if( textStatus == null || jqXHR.getResponseHeader( "Response-Phrase" ) == null ) {
					alert( "We're sorry. The servers did not respond on time. Please try again" );
				}
				else {
					alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
				}
            }
        }
      );
    }
  );
  
  jQuery( ".list_payment_next_twenty" ).off( "click" ).click(
    function() {
      var listPaymentNextTwenty = jQuery( this );
      var listPayment = listPaymentNextTwenty.parent();
      var listPaymentNextTwentyLimit = listPayment.find( ".list_payment_next_twenty_limit" );
      var listPaymentNextTwentyOffset = listPayment.find( ".list_payment_next_twenty_offset" );
      var listPaymentNextTwentyWait = listPayment.find( ".list_payment_next_twenty_wait" );
    
      jQuery.ajax(
        {
          url: "/listPayment.gtpl"
          , type: "GET"
          , data: jQuery.param(
              {
                limit: listPaymentNextTwentyLimit.text()
                , offset: listPaymentNextTwentyOffset.text()
                , studentId: listPayment.find( ".list_payment_student_id_param" ).text()
                , schoolName: listPayment.find( ".list_payment_school_name_param" ).text()
                , enrollTermNo: listPayment.find( ".list_payment_enroll_term_no_param" ).text()
                , enrollTermYear: listPayment.find( ".list_payment_enroll_term_year_param" ).text()
              }
            )
          , beforeSend: function() {
              listPaymentNextTwenty.toggle();
              listPaymentNextTwentyWait.toggle();
            }
          , success: function( data ) {
              listPayment.toggle();
              listPayment.children().remove();
              listPayment.append( data );
              listPayment.toggle();
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
        	  if( textStatus == null || jqXHR.getResponseHeader( "Response-Phrase" ) == null ) {
					alert( "We're sorry. The servers did not respond on time. Please try again" );
				}
				else {
					alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
				}
            }
        }
      );
    }
  );
  
  var paymentLists = jQuery( ".payment_list" );
  
  paymentLists.each(
        function() {
          if( jQuery( this ).parent().hasClass( "list_record_form" ) ) {
            if( navigator.userAgent.match( /(android)|(iPad)|(iPhone)|(iPod)|(webOS)/i ) ) {
              var paymentList = jQuery( this );
              
              jQuery( this ).accordion(
                {
                  collapsible: false
                }
              ).show();

              jQuery( this ).css( "overflow", "visible" );
              jQuery( ".list_record_form .payment_list" ).css( "height", "1450px" );
              jQuery( this ).parent().css( "height", "1450px" );
              jQuery( ".list_record_form .list_payment_next_twenty" ).css( "top", "1425px" )
              jQuery( ".list_record_form .list_payment_next_twenty_wait" ).css( "top", "1425px" )
            }
            else {
              var paymentList = jQuery( this );
              
              jQuery( this ).accordion(
                {
                  active: false
                  , collapsible: true
                }
              ).show();

              jQuery( this ).scroll(
                function() {

                  /* If Scroll Bar position + computer screen's height >= web page's header + body heights OR Scroll Bar position = the top of Payment List section */
                  if( jQuery( window ).scrollTop() + jQuery( window ).height() >= paymentTabListRecordForm.offset().top + paymentTabListRecordForm.height() ||
                    jQuery( window ).scrollTop == paymentTabListRecordForm.offset().top ) {
                    paymentList.css( "overflow", "auto" );
                  }
                  else {
                    paymentList.css( "overflow", "hidden" );
                  }
                }
              );

              jQuery( window ).scroll(
                function() {

                  /* If Scroll Bar position + computer screen's height >= web page's header + body heights OR Scroll Bar position = the top of Payment List section */
                  if( jQuery( window ).scrollTop() + jQuery( window ).height() >= paymentTabListRecordForm.offset().top + paymentTabListRecordForm.height() ||
                    jQuery( window ).scrollTop == paymentTabListRecordForm.offset().top ) {
                    paymentList.css( "overflow", "auto" );
                  }
                  else {
                    paymentList.css( "overflow", "hidden" );
                  }
                }
              );
            }
          }
          else {
          	var activeSeqNo = parseInt( jQuery( this ).parent().find( ".list_payment_active_seq_no" ).text() );
            var paymentList = jQuery( this );
            
            jQuery( this ).accordion(
              {
                active: activeSeqNo
                , collapsible: false
              }
            ).show();
          }
        }
  );
</script>