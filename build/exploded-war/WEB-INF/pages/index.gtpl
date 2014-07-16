<!DOCTYPE html>
<html>
  <head>
    <meta name="viewport" content="height=906, target-densityDpi=device-dpi, width=600" />
    <title>Welcome to URF Schools' Web Database</title>
    <link rel="stylesheet" type="text/css" href="/css/login.css" />
    <link rel="stylesheet" type="text/css" href="/css/jquery-ui-1.10.4.custom.min.css" />
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.0/jquery.min.js"></script>
    <script type="text/javascript">
      jQuery(
        function() {
          if( !navigator.userAgent.match( /(android)|(iPad)|(iPhone)|(iPod)/i ) ) {
            jQuery( ".login_content" ).css( "top", ( jQuery( window ).height() / 2 - 266 ) + "px" );
          }
        }
      );
    </script>
  </head>
  <body>
    <div class="login_content">
      
      <!-- Header -->
      <div class="login_header ui-corner-top">
        <a class="signin_link" href="${ users.createLoginURL( "/main.gtpl" ) }">Sign-in</a>
        <div class="webapp_title">URF SCHOOLS</div>
        <div class="slogan">Building Hope, Inspiring Lives, & Academic Excellence</div>
      </div>
      <div class="login_header_ribbon"></div>

      <!-- Body -->
      <img class="classroom_image ui-corner-bottom" src="/images/hope_classroom.png" />

      <div class="introduction">
        <p>Welcome to URF Schools' Web Database!</p>
        <p><b>Content</b></p>
        <p>This web database contains information about students, parents, and sponsors in schools funded by Uganda Rural Fund (URF).</p>
        <p><b>Access</b></p>
        <p>
          To request access to this URF web resource, please contact:
          <br>
          <a href="mailto:awijasa@ugandaruralfund.org">awijasa@ugandaruralfund.org</a>
        </p>
      </div>
    </div>
  </body>
</html>