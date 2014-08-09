package web_inf.includes;out.print("""""");
  if( user == null ) redirect "/index.gtpl"
  else {

	html.a( class: "list_fee_student_id_label", href: "javascript:void( 0 )", "Student ID" )
	html.a( class: "list_fee_name_label", href: "javascript:void( 0 )", "Name" )
	html.a( class: "list_fee_term_label", href: "javascript:void( 0 )", "Term" )
	html.a( class: "list_fee_amount_label", href: "javascript:void( 0 )", "Amount" )
	html.div( class: "actions_label", "Actions" )

	  int activeSeqNo = 0
	  int limit = Integer.parseInt( params.limit )
	  int offset
	  
	  if( params.offset != null )
	    offset = Integer.parseInt( params.offset )
	    
	  if( params.studentId != null && params.studentId != "" &&
	  	params.name != null && params.name != "" &&
	  	params.schoolName != null && params.schoolName != "" &&
	  	params.classTermNo != null && params.classTermNo != "" &&
	  	params.classTermYear != null && params.classTermYear != "" &&
	  	params.isLookup == null ) {
	    offset = 0
	    
	    List<com.google.appengine.api.datastore.Entity> feeList = data.Fee.findByLimitAndOffset( limit, offset )
	    
	    while( feeList.size() > 0 ) {
	      boolean isOffsetFound = false
	      int sizeOfListDisplayed = feeList.size() == limit + 1? feeList.size() - 1: feeList.size()
	    
	      for( int i = 0; i < sizeOfListDisplayed; i++ ) {
	        com.google.appengine.api.datastore.Entity fee = feeList.get( i )
	
	        if( fee.studentId == params.studentId &&
	        	fee.name == params.name &&
	        	fee.schoolName == params.schoolName &&
	        	fee.classTermNo == Integer.parseInt( params.classTermNo ) &&
	        	fee.classTermYear == Integer.parseInt( params.classTermYear ) ) {
	          activeSeqNo = i
	          isOffsetFound = true
	          break
	        }
	      }
	
	      if( isOffsetFound )
	        break
	
	      offset += limit
	
	      feeList = data.Fee.findByLimitAndOffset( limit, offset )
	    }
	  }
	
	  if( offset > 0 ) {
	    html.a( class: "list_fee_prev_twenty", href: "javascript:void( 0 )", "Prev ${ limit } Fees" )
	    html.img( class: "list_fee_prev_twenty_wait", src: "/images/ajax-loader.gif" )
	  }
	    
	    html.div( class: "list_fee_prev_twenty_limit", style: "display: none;", limit )
	    html.div( class: "list_fee_prev_twenty_offset", style: "display: none;", offset - limit )
	      
		List<com.google.appengine.api.datastore.Entity> feeList
		
		html.div( class: "fee_list" ) {
	  		if( params.isLookup == "Y" ) {
		  		feeList = new ArrayList()
				mkp.yieldUnescaped( formatter.ListItemFormatter.getFeeListItem( data.Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear(
					params.name,
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
				feeList = data.Fee.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYearAndLimitAndOffset(
					params.studentId,
					params.schoolName,
					Integer.parseInt( params.enrollTermNo ),
					Integer.parseInt( params.enrollTermYear ),
					limit,
					offset
				)
				
				feeList.eachWithIndex(
					{ obj, i ->
						if( i < limit )
							mkp.yieldUnescaped( formatter.ListItemFormatter.getFeeListItem( obj ) )
					}
				)
			}
			else {
				feeList = data.Fee.findByLimitAndOffset( limit, offset )
				feeList.eachWithIndex(
					{ obj, i ->
					  if( i < limit )
						mkp.yieldUnescaped( formatter.ListItemFormatter.getFeeListItem( obj ) )
					}
				)
			}	
		}
	
		if( feeList.size() > limit )
	  		html.a( class: "list_fee_next_twenty", href: "javascript:void( 0 )", "Next ${ limit } Fees" )
		else
	  		html.a( class: "list_fee_next_twenty", href: "javascript:void( 0 )", style: "display: none;", "Next ${ limit } Fees" )
	
	  html.img( class: "list_fee_next_twenty_wait", src: "/images/ajax-loader.gif" )
	  
	  html.div( class: "list_fee_next_twenty_limit", style: "display: none;", limit )
	  html.div( class: "list_fee_next_twenty_offset", style: "display: none;", offset + limit )
	  
	  html.div( class: "list_fee_active_seq_no", style: "display: none;", activeSeqNo )
	  html.div( class: "list_fee_student_id_param", style: "display: none;", params.studentId?: "" )
	  html.div( class: "list_fee_school_name_param", style: "display: none;", params.schoolName?: "" )
	  html.div( class: "list_fee_enroll_term_no_param", style: "display: none;", params.enrollTermNo?: "" )
	  html.div( class: "list_fee_enroll_term_year_param", style: "display: none;", params.enrollTermYear?: "" )
  }
;
out.print("""
<script type=\"text/javascript\">
  initFeeList();
  
  var feeTabListRecordForm = jQuery( \"#fee_tab .list_record_form\" );
  
  jQuery( \".list_fee_prev_twenty\" ).off( \"click\" ).click(
    function() {
      var listFeePrevTwenty = jQuery( this );
      var listFee = listFeePrevTwenty.parent();
      var listFeePrevTwentyLimit = listFee.find( \".list_fee_prev_twenty_limit\" );
      var listFeePrevTwentyOffset = listFee.find( \".list_fee_prev_twenty_offset\" );
      var listFeePrevTwentyWait = listFee.find( \".list_fee_prev_twenty_wait\" );
      
      jQuery.ajax(
        {
          url: \"/listFee.gtpl\"
          , type: \"GET\"
          , data: jQuery.param(
              {
                limit: listFeePrevTwentyLimit.text()
                , offset: listFeePrevTwentyOffset.text()
                , studentId: listFee.find( \".list_fee_student_id_param\" ).text()
                , schoolName: listFee.find( \".list_fee_school_name_param\" ).text()
                , enrollTermNo: listFee.find( \".list_fee_enroll_term_no_param\" ).text()
                , enrollTermYear: listFee.find( \".list_fee_enroll_term_year_param\" ).text()
              }
            )
          , beforeSend: function() {
              listFeePrevTwenty.toggle();
              listFeePrevTwentyWait.toggle();
            }
          , success: function( data ) {
              listFee.toggle();
              listFee.children().remove();
              listFee.append( data );
              listFee.toggle();
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
        	  if( textStatus == null || jqXHR.getResponseHeader( \"Response-Phrase\" ) == null ) {
					alert( \"We're sorry. The servers did not respond on time. Please try again\" );
				}
				else {
					alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
				}
            }
        }
      );
    }
  );
  
  jQuery( \".list_fee_next_twenty\" ).off( \"click\" ).click(
    function() {
      var listFeeNextTwenty = jQuery( this );
      var listFee = listFeeNextTwenty.parent();
      var listFeeNextTwentyLimit = listFee.find( \".list_fee_next_twenty_limit\" );
      var listFeeNextTwentyOffset = listFee.find( \".list_fee_next_twenty_offset\" );
      var listFeeNextTwentyWait = listFee.find( \".list_fee_next_twenty_wait\" );
    
      jQuery.ajax(
        {
          url: \"/listFee.gtpl\"
          , type: \"GET\"
          , data: jQuery.param(
              {
                limit: listFeeNextTwentyLimit.text()
                , offset: listFeeNextTwentyOffset.text()
                , studentId: listFee.find( \".list_fee_student_id_param\" ).text()
                , schoolName: listFee.find( \".list_fee_school_name_param\" ).text()
                , enrollTermNo: listFee.find( \".list_fee_enroll_term_no_param\" ).text()
                , enrollTermYear: listFee.find( \".list_fee_enroll_term_year_param\" ).text()
              }
            )
          , beforeSend: function() {
              listFeeNextTwenty.toggle();
              listFeeNextTwentyWait.toggle();
            }
          , success: function( data ) {
              listFee.toggle();
              listFee.children().remove();
              listFee.append( data );
              listFee.toggle();
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
        	  if( textStatus == null || jqXHR.getResponseHeader( \"Response-Phrase\" ) == null ) {
					alert( \"We're sorry. The servers did not respond on time. Please try again\" );
				}
				else {
					alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
				}
            }
        }
      );
    }
  );
  
  var feeLists = jQuery( \".fee_list\" );
  
  feeLists.each(
        function() {
          if( jQuery( this ).parent().hasClass( \"list_record_form\" ) ) {
            if( navigator.userAgent.match( /(android)|(iPad)|(iPhone)|(iPod)|(webOS)/i ) ) {
              var feeList = jQuery( this );
              
              jQuery( this ).accordion(
                {
                  collapsible: false
                }
              ).show();

              jQuery( this ).css( \"overflow\", \"visible\" );
              jQuery( \".list_record_form .fee_list\" ).css( \"height\", \"1450px\" );
              jQuery( this ).parent().css( \"height\", \"1450px\" );
              jQuery( \".list_record_form .list_fee_next_twenty\" ).css( \"top\", \"1425px\" )
              jQuery( \".list_record_form .list_fee_next_twenty_wait\" ).css( \"top\", \"1425px\" )
            }
            else {
              var feeList = jQuery( this );
              
              jQuery( this ).accordion(
                {
                  active: false
                  , collapsible: true
                }
              ).show();

              jQuery( this ).scroll(
                function() {

                  /* If Scroll Bar position + computer screen's height >= web page's header + body heights OR Scroll Bar position = the top of Fee List section */
                  if( jQuery( window ).scrollTop() + jQuery( window ).height() >= feeTabListRecordForm.offset().top + feeTabListRecordForm.height() ||
                    jQuery( window ).scrollTop == feeTabListRecordForm.offset().top ) {
                    feeList.css( \"overflow\", \"auto\" );
                  }
                  else {
                    feeList.css( \"overflow\", \"hidden\" );
                  }
                }
              );

              jQuery( window ).scroll(
                function() {

                  /* If Scroll Bar position + computer screen's height >= web page's header + body heights OR Scroll Bar position = the top of Fee List section */
                  if( jQuery( window ).scrollTop() + jQuery( window ).height() >= feeTabListRecordForm.offset().top + feeTabListRecordForm.height() ||
                    jQuery( window ).scrollTop == feeTabListRecordForm.offset().top ) {
                    feeList.css( \"overflow\", \"auto\" );
                  }
                  else {
                    feeList.css( \"overflow\", \"hidden\" );
                  }
                }
              );
            }
          }
          else {
          	var activeSeqNo = parseInt( jQuery( this ).parent().find( \".list_fee_active_seq_no\" ).text() );
            var feeList = jQuery( this );
            
            jQuery( this ).accordion(
              {
                active: activeSeqNo
                , collapsible: false
              }
            ).show();
          }
        }
  );
</script>""");
