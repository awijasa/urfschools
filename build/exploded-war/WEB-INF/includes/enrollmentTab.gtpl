<%
	if( user == null ) redirect "/index.gtpl"
	else {
%>
		<div id="enrollment_tab">
		  <div class="new_record_form ui-corner-all">
		    <% include '/WEB-INF/includes/addEnrollment.gtpl' %>
		  </div>
		
		  <div class="new_record_radio">
		        <input type="radio" id="add_enrollment_radio" name="new_enrollment_radio" checked="checked" /><label for="add_enrollment_radio">add new</label>
		        <input type="radio" id="upload_enrollments_radio" name="new_enrollment_radio" /><label for="upload_enrollments_radio">upload</label>
		  </div>
		
		  <button class="middle_filter">filter</button>
		  <button class="middle_sortby">sort by</button>
		
		  <div class="list_record_form ui-corner-all">
		    <% include '/WEB-INF/includes/listEnrollment.gtpl?limit=20&offset=0' %>
		  </div>
		
		  <div class="list_record_radio">
		        <input type="radio" id="list_enrollments_radio" name="list_enrollment_radio" checked="checked" /><label for="list_enrollments_radio">list</label>
		        <input type="radio" id="download_enrollments_radio" name="list_enrollment_radio" /><label for="download_enrollments_radio">download</label>
		  </div>
		</div>
<% } %>

<script type="text/javascript">
  var middleFilterSortByDialog = jQuery( ".middle_filter_sortby_dialog" );

  var middleFilterSortBy = jQuery( "#enrollment_tab" ).find( ".middle_filter, .middle_sortby" );

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
          middleFilterSortByDialog.dialog( "option", "position", [jQuery( ".main_content" ).position().left + 144 - jQuery( window ).scrollLeft(), 620 - jQuery( window ).scrollTop()] );
          middleFilterSortByDialog.dialog( "open" );
        }
      }
    );

    var downloadRadioDialog = jQuery( ".download_radio_dialog" );

  /* Initialize the list - download radio buttons. */
  var listRecordRadio = jQuery( "#enrollment_tab .list_record_radio" ).buttonset();
  
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
        downloadRadioDialog.dialog( "option", "position", [jQuery( ".main_content" ).position().left - jQuery( window ).scrollLeft(), 640 - jQuery( window ).scrollTop()] );
        downloadRadioDialog.dialog( "open" );
      }
    }
  );
    
  var uploadRadioDialog = jQuery( ".upload_radio_dialog" );

  /* Initialize the add new - upload radio buttons. */
  var newRecordRadio = jQuery( "#enrollment_tab .new_record_radio" ).buttonset();
  
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
        uploadRadioDialog.dialog( "option", "position", [jQuery( ".main_content" ).position().left - jQuery( window ).scrollLeft(), 180 - jQuery( window ).scrollTop()] );
        uploadRadioDialog.dialog( "open" );
      }
    }
  );
</script>