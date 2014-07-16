<!DOCTYPE html>
<%
  import com.google.appengine.api.datastore.Entity
  import data.URFUser

  if( user == null ) redirect "/index.gtpl"
  else {
  
  	Entity urfUser = URFUser.findByEmail( user.getEmail() )

	html.html {
	  head {
	    meta( name: "viewport", content: "height=1576, width=1200" )
	    title( "URF Schools' Web Database" )
	    link( rel: "stylesheet", type: "text/css", href: "https://fonts.googleapis.com/css?family=Didact+Gothic" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/jquery-ui-1.10.4.custom.min.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addEnrollment.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addGender.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addLeaveReasonCategory.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addParent.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addParentalRelationship.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addSchool.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addStudent.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addTerm.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addURFUser.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/adminTabs.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/dialogClassAttended.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listEnrollment.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listGender.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listLeaveReasonCategory.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listParent.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listParentalRelationship.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listSchool.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listStudent.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listTerm.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listURFUser.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/main.css" )   
	    script( type: "text/javascript", src: "https://ajax.googleapis.com/ajax/libs/jquery/2.1.0/jquery.min.js" )
	    script( type: "text/javascript", src: "https://ajax.googleapis.com/ajax/libs/jqueryui/1.10.2/jquery-ui.min.js" )
	    script( type: "text/javascript", src: "/js/fieldInit.js" )
	    script( type: "text/javascript", src: "/js/listInit.js" )
	    script( type: "text/javascript", src: "/js/main.js" )
	  }
	  
	  body( onLoad: "initMain()" ) {
	    div( class: "main_content" ) {
	      
	      /* Header */
	      div( class: "main_header ui-corner-top" ) {
	        a( class: "signout_link", href: users.createLogoutURL( "/index.gtpl" ), "Sign out" )
	        div( class: "webapp_title", "URF SCHOOLS" )
	        div( class: "slogan", "Building Hope, Inspiring Lives, & Academic Excellence" )
	        
	        button( class: "header_filter", "filter" )
	        button( class: "header_sortby", "sort by" )
	        
	        div( class: "header_filter_sortby_dialog" ) {
	        	mkp.yield( "Email " )
	        	a( href: "mailto:awijasa@ugandaruralfund.org", "awijasa@ugandaruralfund.org" )
	        	mkp.yield( " and describe the filters/sort functions that you want." )
	        }
	
	        div( class: "middle_filter_sortby_dialog" ) {
	        	mkp.yield( "Email " )
	        	a( href: "mailto:awijasa@ugandaruralfund.org", "awijasa@ugandaruralfund.org" )
	        	mkp.yield( " and describe the filters/sort functions that you want." )
	        }
	
	        div( class: "download_radio_dialog" ) {
	        	mkp.yield( "Email " )
	        	a( href: "mailto:awijasa@ugandaruralfund.org", "awijasa@ugandaruralfund.org" )
	        	mkp.yield( " and describe the data/file that you want downloaded." )
	        }
	
	        div( class: "upload_radio_dialog" ) {
	        	mkp.yield( "Email " )
	        	a( href: "mailto:awijasa@ugandaruralfund.org", "awijasa@ugandaruralfund.org" )
	        	mkp.yield( " and describe the data/file that you want uploaded." )
	        }
	      }
	      div( class: "main_header_ribbon ui-corner-bottom" )
	      
	      /* Body */
	      div( id: "tabs" ) {
			String sponsorDataPrivilege
			
			ul {
				li {
					a( href: "/studentTab.gtpl" ) {
						span( "Student" )
					}
					span( class: "tab_refresh ui-icon ui-icon-arrowrefresh-1-e", "Refresh Tab" )
				}
				li {
					a( href: "/enrollmentTab.gtpl" ) {
						span( "Enrollment" )
					}
					span( class: "tab_refresh ui-icon ui-icon-arrowrefresh-1-e", "Refresh Tab" )
				}
	        	li {
	        		a( href: "/parentTab.gtpl" ) {
	        			span( "Parent" )
	        		}
	        		span( class: "tab_refresh ui-icon ui-icon-arrowrefresh-1-e", "Refresh Tab" )
	        	}
	
	            sponsorDataPrivilege = urfUser?.sponsorDataPrivilege
	
	            if( urfUser == null || sponsorDataPrivilege?.equals( "Read" ) || sponsorDataPrivilege?.equals( "Modify" ) ) {
	            	li {
	            		a( href: "#sponsor_tab", "Sponsor" )
	            	}
	            }
	          
	            String adminPrivilege = urfUser?.adminPrivilege
	                  
	            if( urfUser == null || adminPrivilege?.equals( "Read" ) || adminPrivilege?.equals( "Modify" ) ) {
	            	li {
	            		a( href: "/adminTabs.gtpl" ) {
	            			span( "Admin" )
	            		}
	            		span( class: "tab_refresh ui-icon ui-icon-arrowrefresh-1-e", "Refresh Tab" )
	            	}
	            }
			}
	        
	        if( urfUser == null || sponsorDataPrivilege?.equals( "Read" ) || sponsorDataPrivilege?.equals( "Modify" ) )
	          div( id: "sponsor_tab" )
	      }
	      
	      div( class: "progress_bar" )
	    }
	    
	    /* Dialogs/Sub-Forms */
	    div( class: "add_enrollment_list_student", title: "Select a Student who will enroll" ) {
	    	div( class: "add_enrollment_list_student_first_request", style: "display: none", "Yes" )
	    }
	    
	    div( class: "add_parent_child_list_student", title: "Select a Student Child" ) {
			div( class: "add_parent_child_list_student_first_request", style: "display: none", "Yes" )
		}
		
		div( class: "add_parent_relative_list_student", title: "Select a Student Relative" ) {
			div( class: "add_parent_relative_list_student_first_request", style: "display: none", "Yes" )
		}
		
	    div( class: "dialog_class_attended", title: "Classes attended by the selected Student" )
	    
	    div( class: "dialog_list_parent", title: "Parents of the selected Student" )
		
		div( class: "dialog_student_lookup", title: "Lookup a Student" )
		
		div( class: "list_parent_child_list_student", title: "Select a Student Child" ) {
			div( class: "list_parent_child_list_student_first_request", style: "display: none", "Yes" )
		}
		
		div( class: "list_parent_relative_list_student", title: "Select a Student Relative" ) {
			div( class: "list_parent_relative_list_student_first_request", style: "display: none", "Yes" )
		}
	  }
	}
	}
%>