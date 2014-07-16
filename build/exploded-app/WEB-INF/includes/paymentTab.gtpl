<%
	if( user == null ) redirect "/index.gtpl"
	else {
%>
		<div id="payment_tab">
		  <div class="new_record_form ui-corner-all">
		    <% include '/WEB-INF/includes/addPayment.gtpl' %>
		  </div>
		
		  <div class="new_record_radio">
		        <input type="radio" id="add_payment_radio" name="new_payment_radio" checked="checked" /><label for="add_payment_radio">add new</label>
		        <input type="radio" id="upload_payments_radio" name="new_payment_radio" /><label for="upload_payments_radio">upload</label>
		  </div>
		
		  <button class="middle_filter">filter</button>
		  <button class="middle_sortby">sort by</button>
		
		  <div class="list_record_form ui-corner-all">
		    <% include '/WEB-INF/includes/listPayment.gtpl?limit=20&offset=0' %>
		  </div>
		
		  <div class="list_record_radio">
		        <input type="radio" id="list_payments_radio" name="list_payment_radio" checked="checked" /><label for="list_payments_radio">list</label>
		        <input type="radio" id="download_payments_radio" name="list_payment_radio" /><label for="download_payments_radio">download</label>
		  </div>
		</div>
<% } %>

<script type="text/javascript">
  var middleFilterSortByDialog = jQuery( ".middle_filter_sortby_dialog" );

  var middleFilterSortBy = jQuery( "#payment_tab" ).find( ".middle_filter, .middle_sortby" );

  middleFilterSortBy.button({
      icons: {
        secondary: "ui-icon-carat-1-s"
      }
  })
    .click(
      function() {
        if( middleFilterSortByDialog.dialog( "isOpen" ) ) {
          middleFilterSortByDialog.dialog( "close" );
        }
        else {
          middleFilterSortByDialog.dialog( "option", "position",
          	{
          		my: "left-294 top+13.5"
          		, of: jQuery( "#payment_tab .middle_sortby" )
          		, collision: "fit"
          	}
          );
          middleFilterSortByDialog.dialog( "open" );
        }
      }
    );

    var downloadRadioDialog = jQuery( ".download_radio_dialog" );

  /* Initialize the list - download radio buttons. */
  var listRecordRadio = jQuery( "#payment_tab .list_record_radio" ).buttonset();
  
  listRecordRadio.find( "input:radio" ).eq( 0 ).click(
    function() {
      if( downloadRadioDialog.dialog( "isOpen" ) ) {
        downloadRadioDialog.dialog( "close" );
      }
    }
  );
  
  listRecordRadio.find( "input:radio" ).eq( 1 ).click(
    function() {
      if( downloadRadioDialog.dialog( "isOpen" ) ) {
        downloadRadioDialog.dialog( "close" );
      }
      else {
        downloadRadioDialog.dialog( "option", "position",
        	{
          		my: "left top+28"
          		, of: jQuery( this )
          		, collision: "fit"
          	}
        );
        downloadRadioDialog.dialog( "open" );
      }
    }
  );
    
  var uploadRadioDialog = jQuery( ".upload_radio_dialog" );

  /* Initialize the add new - upload radio buttons. */
  var newRecordRadio = jQuery( "#payment_tab .new_record_radio" ).buttonset();
  
  newRecordRadio.find( "input:radio" ).eq( 0 ).click(
    function() {
      if( uploadRadioDialog.dialog( "isOpen" ) ) {
        uploadRadioDialog.dialog( "close" );
      }
    }
  );
  
  newRecordRadio.find( "input:radio" ).eq( 1 ).click(
    function() {
      if( uploadRadioDialog.dialog( "isOpen" ) ) {
        uploadRadioDialog.dialog( "close" );
      }
      else {
        uploadRadioDialog.dialog( "option", "position",
			{
          		my: "left top+28"
          		, of: jQuery( this )
          		, collision: "fit"
          	}
        );
        uploadRadioDialog.dialog( "open" );
      }
    }
  );
</script>