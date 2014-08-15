<!DOCTYPE html>
<%
	if( user == null ) {
		Enumeration<String> sessionAttributeNames = session.getAttributeNames()
		        	
    	if( sessionAttributeNames.hasMoreElements() )
    		session.invalidate()
		        	
		html.html {
		  head {
		    meta( name: "viewport", content: "height=906, target-densityDpi=device-dpi, width=600" )
		    title( "Welcome to URF Schools' Web Database" )
		    link( rel: "stylesheet", type: "text/css", href: "/css/login.css" )
		    link( rel: "stylesheet", type: "text/css", href: "/css/jquery-ui-1.11.1.custom.min.css" )
		    script( type: "text/javascript", src: "//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js" )
%>
		    <script type="text/javascript">
				if (typeof jQuery == 'undefined') {
				    document.write(unescape("%3Cscript src='//ajax.aspnetcdn.com/ajax/jQuery/jquery-1.11.1.min.js' type='text/javascript'%3E%3C/script%3E"));
				}
				
				if (typeof jQuery == 'undefined') {
				    document.write(unescape("%3Cscript src='/js/jquery-1.11.1.min.js' type='text/javascript'%3E%3C/script%3E"));
				}
			</script>
		    <script type="text/javascript">
		      jQuery(
		        function() {
		          if( !navigator.userAgent.match( /(android)|(iPad)|(iPhone)|(iPod)/i ) ) {
		            jQuery( ".login_content" ).css( "top", ( jQuery( window ).height() / 2 - 266 ) + "px" );
		          }
		        }
		      );
		    </script>
<%
		  }
		  body {
		    div( class: "login_content" ) {
		      
		      /* Header */
		      div( class: "login_header ui-corner-top" ) {
		        a( class: "signin_link", href: users.createLoginURL( "/main.gtpl" ), "Sign-in" )
		        div( class: "webapp_title", "URF SCHOOLS" )
		        div( class: "slogan", "Building Hope, Inspiring Lives, & Academic Excellence" )
		      }
		      div( class: "login_header_ribbon" )
		
		      /* Body */
		      img( class: "classroom_image ui-corner-bottom", src: "/images/hope_classroom.png" )
		
		      div( class: "introduction" ) {
		        p( "Welcome to URF Schools' Web Database!" )
		        p {
		        	b( "Content" )
		        }
		        p( "This web database contains information about students, parents, and sponsors in schools funded by Uganda Rural Fund (URF)." )
		        p {
		        	b( "Access" )
	        	}
		        p {
		          mkp.yield( "To request access to this URF web resource, please contact:" )
		          br()
		          a( href: "mailto:awijasa@ugandaruralfund.org", "awijasa@ugandaruralfund.org" )
		        }
		      }
		    }
		  }
		}
	}
	else redirect "/main.gtpl"
%>