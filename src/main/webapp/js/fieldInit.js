function autoResizeFieldBasedOnWidth( container, fieldClass, maxWidth ) {
    container.find( fieldClass ).each(
        function() {
            while( jQuery( this ).width() > maxWidth ) {
                jQuery( this ).css( "font-size", "-=1px" );
            }
        }
    );
    
    setTimeout(
        function() {
            container.find( fieldClass ).each(
                function() {
                    while( jQuery( this ).width() > maxWidth ) {
                        jQuery( this ).css( "font-size", "-=1px" );
                    }
                }
            );
        }
        , 500
    );
        
    setTimeout(
        function() {
            container.find( fieldClass ).each(
                function() {
                    while( jQuery( this ).width() > maxWidth ) {
                        jQuery( this ).css( "font-size", "-=1px" );
                    }
                }
            );
        }
        , 1000
    );
}

function initTextAreaValueWithName( textAreaIdentifier, textAreaName ) {
    jQuery( textAreaIdentifier ).blur(
      function(){
        if( jQuery( this ).val() == "" ) {
          jQuery( this ).val( textAreaName );
          jQuery( this ).css( "color", "#9b9898" );
          jQuery( this ).css( "font-style", "italic" );
        }
        
        jQuery( textAreaIdentifier + "_label" ).css( "display", "none" );
      }
    );
    
    jQuery( textAreaIdentifier ).focus(
      function(){
        if( jQuery( this ).val() == textAreaName ) {
          jQuery( this ).val( "" );
          jQuery( this ).css( "color", "#000000" );
          jQuery( this ).css( "font-style", "normal" );
        }
        
        jQuery( textAreaIdentifier + "_label" ).css( "display", "block" );
      }
    );
}

function initTextFieldValueWithName( textFieldIdentifier, textFieldName ) {
    jQuery( textFieldIdentifier ).blur(
      function(){
        if( jQuery( this ).val() == "" ) {
          jQuery( this ).val( textFieldName );
          jQuery( this ).css( "color", "#9b9898" );
          jQuery( this ).css( "font-style", "italic" );
        }
        
        jQuery( textFieldIdentifier + "_label" ).css( "display", "none" );
      }
    );
    
    jQuery( textFieldIdentifier ).focus(
      function(){
        if( jQuery( this ).val() == textFieldName ) {
          jQuery( this ).val( "" );
          jQuery( this ).css( "color", "#000000" );
          jQuery( this ).css( "font-style", "normal" );
        }
        
        jQuery( textFieldIdentifier + "_label" ).css( "display", "block" );
      }
    );
}