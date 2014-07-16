package web_inf.includes;out.print("""""");
  if( user == null ) redirect "/index.gtpl"
  else {
	html.a( class: "list_term_school_label", href: "javascript:void( 0 )", "School" )
	html.a( class: "list_term_no_label", href: "javascript:void( 0 )", "Term No" )
	html.a( class: "list_term_year_label", href: "javascript:void( 0 )", "Year" )
	html.a( class: "list_term_start_date_label", href: "javascript:void( 0 )", "Start Date" )
	html.a( class: "list_term_end_date_label", href: "javascript:void( 0 )", "End Date" )
	html.div( class: "actions_label", "Actions" )
	
	int limit = Integer.parseInt( params.limit )
	int offset = Integer.parseInt( params.offset )
	
	if( offset > 0 ) {
	    html.a( class: "list_term_prev_twenty", href: "javascript:void( 0 )", "Prev 20 Terms" )
	    html.img( class: "list_term_prev_twenty_wait", src: "/images/ajax-loader.gif" )
	}
	    
    html.div( class: "list_term_prev_twenty_limit", style: "display: none;", limit )
    html.div( class: "list_term_prev_twenty_offset", style: "display: none;", offset - limit )
	
	List<com.google.appengine.api.datastore.Entity> termList
	
	html.div( class: "term_list" ) {
	  com.google.appengine.api.datastore.Entity urfUser = data.URFUser.findByEmail( user.getEmail() )
	      
	    if( urfUser == null || urfUser.adminPrivilege?.equals( "Read" ) || urfUser.adminPrivilege?.equals( "Modify" ) ) {
	      termList = data.Term.findByLimitAndOffset( limit, offset )
	      termList.eachWithIndex(
	      	{ obj, i ->
		        if( i < limit )
		          mkp.yieldUnescaped( formatter.ListItemFormatter.getTermListItem( obj ) )
	      	}
      		)
  		}
	}
	    
  	if( termList.size() > limit )
    	html.a( class: "list_term_next_twenty", href: "javascript:void( 0 )", "Next 20 Terms" )
  	else
    	html.a( class: "list_term_next_twenty", href: "javascript:void( 0 )", style: "display: none;", "Next 20 Terms" )

    html.img( class: "list_term_next_twenty_wait", src: "/images/ajax-loader.gif" )

    html.div( class: "list_term_next_twenty_limit", style: "display: none;", limit )
    html.div( class: "list_term_next_twenty_offset", style: "display: none;", offset + limit )
	}
;
out.print("""

<script type=\"text/javascript\">
  initTermList();
  
  var termTabListRecordForm = jQuery( \"#term_tab .list_record_form\" );
  
  jQuery( \".list_term_prev_twenty\" ).click(
    function() {
      jQuery.ajax(
        {
          url: \"/listTerm.gtpl\"
          , type: \"GET\"
          , data: jQuery.param(
              {
                limit: jQuery( \".list_term_prev_twenty_limit\" ).text()
                , offset: jQuery( \".list_term_prev_twenty_offset\" ).text()
              }
            )
          , beforeSend: function() {
              jQuery( \".list_term_prev_twenty\" ).toggle();
              jQuery( \".list_term_prev_twenty_wait\" ).toggle();
            }
          , success: function( data ) {
              termTabListRecordForm.toggle();
              termTabListRecordForm.children().remove();
              termTabListRecordForm.append( data );
              termTabListRecordForm.toggle();
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
              alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
            }
        }
      );
    }
  );
  
  var listTermNextTwenty = jQuery( \".list_term_next_twenty\" );
  var listTermNextTwentyWait = jQuery( \".list_term_next_twenty_wait\" );
  
  listTermNextTwenty.click(
    function() {
      jQuery.ajax(
        {
          url: \"/listTerm.gtpl\"
          , type: \"GET\"
          , data: jQuery.param(
              {
                limit: jQuery( \".list_term_next_twenty_limit\" ).text()
                , offset: jQuery( \".list_term_next_twenty_offset\" ).text()
              }
            )
          , beforeSend: function() {
              listTermNextTwenty.toggle();
              listTermNextTwentyWait.toggle();
            }
          , success: function( data ) {
              termTabListRecordForm.toggle();
              termTabListRecordForm.children().remove();
              termTabListRecordForm.append( data );
              termTabListRecordForm.toggle();
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
              alert( textStatus + \" \" + jqXHR.getResponseHeader( \"Response-Phrase\" ) );
            }
        }
      );
    }
  );
  
  jQuery( \".term_list\" ).accordion(
    {
      active: false
      , collapsible: true
    }
  );
  
  var isMobile = navigator.userAgent.match( /(iPad)|(iPhone)|(iPod)|(android)|(webOS)/i );
  
  if( isMobile ) {
    jQuery( \".term_list\" ).css( \"overflow\", \"visible\" );
    jQuery( \".term_list\" ).css( \"height\", \"1305px\" );
    jQuery( \".list_record_form\" ).css( \"height\", \"1392px\" );
    jQuery( \"#admin_tabs\" ).css( \"height\", \"1922px\" );
  }
  else {
    jQuery( \".term_list\" ).scroll(
      function() {
        if( jQuery( window ).scrollTop() + jQuery( window ).height() >= jQuery( \"#term_tab .list_record_form\" ).offset().top + jQuery( \"#term_tab .list_record_form\" ).height() ||
          jQuery( window ).scrollTop == jQuery( \"#term_tab .list_record_form\" ).offset().top ) {
          jQuery( \".term_list\" ).css( \"overflow\", \"auto\" );
        }
        else {
          jQuery( \".term_list\" ).css( \"overflow\", \"hidden\" );
        }
      }
    );

    jQuery( window ).scroll(
      function() {
        if( jQuery( window ).scrollTop() + jQuery( window ).height() >= jQuery( \"#term_tab .list_record_form\" ).offset().top + jQuery( \"#term_tab .list_record_form\" ).height() ||
          jQuery( window ).scrollTop == jQuery( \"#term_tab .list_record_form\" ).offset().top ) {
          jQuery( \".term_list\" ).css( \"overflow\", \"auto\" );
        }
        else {
          jQuery( \".term_list\" ).css( \"overflow\", \"hidden\" );
        }
      }
    );
  }
</script>""");
