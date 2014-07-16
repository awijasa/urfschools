<%
  if( user == null ) redirect "/index.gtpl"
  else {
	html.a( class: "list_urfuser_email_label", href: "javascript:void( 0 )", "Email" )
	html.a( class: "list_urfuser_sponsor_data_privilege_label", href: "javascript:void( 0 )", "Sponsor Data Privilege" )
	html.a( class: "list_urfuser_admin_privilege_label", href: "javascript:void( 0 )", "Admin Privilege" )
	html.div( class: "actions_label", "Actions" )

	int limit = Integer.parseInt( params.limit )
	int offset = Integer.parseInt( params.offset )
	
	if( offset > 0 ) {
	    html.a( class: "list_urfuser_prev_twenty", href: "javascript:void( 0 )", "Prev 20 Users" )
	    html.img( class: "list_urfuser_prev_twenty_wait", src: "/images/ajax-loader.gif" )
	}
    
    html.div( class: "list_urfuser_prev_twenty_limit", style: "display: none;", limit )
    html.div( class: "list_urfuser_prev_twenty_offset", style: "display: none;", offset - limit )

	List<com.google.appengine.api.datastore.Entity> urfUserList
	
	html.div( class: "urfuser_list" ) {
	    String adminPrivilege = data.URFUser.findByEmail( user.getEmail() )?.adminPrivilege
	      
	    if( adminPrivilege?.equals( "Read" ) || adminPrivilege?.equals( "Modify" ) ) {
	      urfUserList = data.URFUser.findByLimitAndOffset( limit, offset )
	      
	      urfUserList.eachWithIndex(
	      	{ obj, i ->
		        if( i < limit )
		          mkp.yieldUnescaped( formatter.ListItemFormatter.getURFUserListItem( obj ) )
	      	}
      		)
  		}
	}
    
	if( urfUserList?.size() > limit )
        html.a( class: "list_urfuser_next_twenty", href: "javascript:void( 0 )", "Next 20 Users" )
    else
        html.a( class: "list_urfuser_next_twenty", href: "javascript:void( 0 )", style: "display: none;", "Next 20 Users" )

    html.img( class: "list_urfuser_next_twenty_wait", src: "/images/ajax-loader.gif" )

    html.div( class: "list_urfuser_next_twenty_limit", style: "display: none;", limit )
    html.div( class: "list_urfuser_next_twenty_offset", style: "display: none;", offset + limit )
	}
%>

<script type="text/javascript">
  initURFUserList();
  
  var userTabListRecordForm = jQuery( "#user_tab .list_record_form" );
  
  jQuery( ".list_urfuser_prev_twenty" ).click(
    function() {
      jQuery.ajax(
        {
          url: "/listURFUser.gtpl"
          , type: "GET"
          , data: jQuery.param(
              {
                limit: jQuery( ".list_urfuser_prev_twenty_limit" ).text()
                , offset: jQuery( ".list_urfuser_prev_twenty_offset" ).text()
              }
            )
          , beforeSend: function() {
              jQuery( ".list_urfuser_prev_twenty" ).toggle();
              jQuery( ".list_urfuser_prev_twenty_wait" ).toggle();
            }
          , success: function( data ) {
              userTabListRecordForm.toggle();
              userTabListRecordForm.children().remove();
              userTabListRecordForm.append( data );
              userTabListRecordForm.toggle();
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
              alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
            }
        }
      );
    }
  );
  
  var listURFUserNextTwenty = jQuery( ".list_urfuser_next_twenty" );
  var listURFUserNextTwentyWait = jQuery( ".list_urfuser_next_twenty_wait" );
  
  listURFUserNextTwenty.click(
    function() {
      jQuery.ajax(
        {
          url: "/listURFUser.gtpl"
          , type: "GET"
          , data: jQuery.param(
              {
                limit: jQuery( ".list_urfuser_next_twenty_limit" ).text()
                , offset: jQuery( ".list_urfuser_next_twenty_offset" ).text()
              }
            )
          , beforeSend: function() {
              listURFUserNextTwenty.toggle();
              listURFUserNextTwentyWait.toggle();
            }
          , success: function( data ) {
              userTabListRecordForm.toggle();
              userTabListRecordForm.children().remove();
              userTabListRecordForm.append( data );
              userTabListRecordForm.toggle();
            }
          , error: function( jqXHR, textStatus, errorThrown ) {
              alert( textStatus + " " + jqXHR.getResponseHeader( "Response-Phrase" ) );
            }
        }
      );
    }
  );
  
  jQuery( ".urfuser_list" ).accordion(
    {
      active: false
      , collapsible: true
    }
  );
  
  var isMobile = navigator.userAgent.match( /(iPad)|(iPhone)|(iPod)|(android)|(webOS)/i );
  
  if( isMobile ) {
    jQuery( ".urfuser_list" ).css( "overflow", "visible" );
    jQuery( ".urfuser_list" ).css( "height", "1305px" );
    jQuery( ".list_record_form" ).css( "height", "1392px" );
    jQuery( "#admin_tabs" ).css( "height", "1922px" );
  }
  else {
    jQuery( ".urfuser_list" ).scroll(
      function() {
        if( jQuery( window ).scrollTop() + jQuery( window ).height() >= jQuery( "#user_tab .list_record_form" ).offset().top + jQuery( "#user_tab .list_record_form" ).height() ||
          jQuery( window ).scrollTop == jQuery( "#user_tab .list_record_form" ).offset().top ) {
          jQuery( ".urfuser_list" ).css( "overflow", "auto" );
        }
        else {
          jQuery( ".urfuser_list" ).css( "overflow", "hidden" );
        }
      }
    );

    jQuery( window ).scroll(
      function() {
        if( jQuery( window ).scrollTop() + jQuery( window ).height() >= jQuery( "#user_tab .list_record_form" ).offset().top + jQuery( "#user_tab .list_record_form" ).height() ||
          jQuery( window ).scrollTop == jQuery( "#user_tab .list_record_form" ).offset().top ) {
          jQuery( ".urfuser_list" ).css( "overflow", "auto" );
        }
        else {
          jQuery( ".urfuser_list" ).css( "overflow", "hidden" );
        }
      }
    );
  }
</script>