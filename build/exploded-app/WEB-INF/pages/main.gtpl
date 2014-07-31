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
	    link( rel: "stylesheet", type: "text/css", href: "//fonts.googleapis.com/css?family=Didact+Gothic" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/jquery-ui-1.11.0.custom.min.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addEnrollment.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addFee.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addGender.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addLeaveReasonCategory.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addParent.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addParentalRelationship.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addPayment.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addSchool.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addStudent.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addTerm.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/addURFUser.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/adminTabs.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/dialogClassAttended.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listEnrollment.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listFee.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listGender.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listLeaveReasonCategory.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listParent.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listParentalRelationship.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listPayment.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listSchool.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listStudent.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listTerm.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/listURFUser.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/main.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/enrollmentFilterSortByDialog.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/parentFilterSortByDialog.css" )
	    link( rel: "stylesheet", type: "text/css", href: "/css/studentFilterSortByDialog.css" )   
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
<%
	    script( type: "text/javascript", src: "//ajax.googleapis.com/ajax/libs/jqueryui/1.11.0/jquery-ui.min.js" )
%>
		<script type="text/javascript">
			if (typeof jQuery.ui == 'undefined') {
			    document.write(unescape("%3Cscript src='//ajax.aspnetcdn.com/ajax/jquery.ui/1.10.2/jquery-ui.min.js' type='text/javascript'%3E%3C/script%3E"));
			}
			
			if (typeof jQuery.ui == 'undefined') {
			    document.write(unescape("%3Cscript src='/js/jquery-ui-1.11.0.min.js' type='text/javascript'%3E%3C/script%3E"));
			}
		</script>
<%
	    script( type: "text/javascript", src: "/js/fieldInit.js" )
	    script( type: "text/javascript", src: "/js/listInit.js" )
	    script( type: "text/javascript", src: "/js/main.js" )
	  }
	  
	  body( onLoad: "initMain()" ) {
		  div( class: "filters" ) {
			div( class: "enrollment_birth_date_filter", session.getAttribute( "enrollmentBirthDateFilter" ) )
			div( class: "enrollment_birth_date_filter_operator", session.getAttribute( "enrollmentBirthDateFilterOperator" ) )
			div( class: "enrollment_classes_attended_filter", session.getAttribute( "enrollmentClassesAttendedFilter" ) )
			div( class: "enrollment_fees_due_filter", session.getAttribute( "enrollmentFeesDueFilter" ) )
			div( class: "enrollment_fees_due_filter_operator", session.getAttribute( "enrollmentFeesDueFilterOperator" ) )
			div( class: "enrollment_first_name_filter", session.getAttribute( "enrollmentFirstNameFilter" ) )
			div( class: "enrollment_last_name_filter", session.getAttribute( "enrollmentLastNameFilter" ) )
			div( class: "enrollment_period_filter", session.getAttribute( "enrollmentPeriodFilter" ) )
			div( class: "enrollment_village_filter", session.getAttribute( "enrollmentVillageFilter" ) )
			div( class: "parent_deceased_ind_filter", session.getAttribute( "parentDeceasedIndFilter" ) )
			div( class: "parent_email_filter", session.getAttribute( "parentEmailFilter" ) )
			div( class: "parent_first_name_filter", session.getAttribute( "parentFirstNameFilter" ) )
			div( class: "parent_id_filter", session.getAttribute( "parentIdFilter" ) )
			div( class: "parent_last_name_filter", session.getAttribute( "parentLastNameFilter" ) )
			div( class: "parent_primary_phone_filter", session.getAttribute( "parentPrimaryPhoneFilter" ) )
			div( class: "parent_profession_filter", session.getAttribute( "parentProfessionFilter" ) )
			div( class: "parent_secondary_phone_filter", session.getAttribute( "parentSecondaryPhoneFilter" ) )
			div( class: "parent_village_filter", session.getAttribute( "parentVillageFilter" ) )
			div( class: "student_birth_date_filter", session.getAttribute( "studentBirthDateFilter" ) )
			div( class: "student_birth_date_filter_operator", session.getAttribute( "studentBirthDateFilterOperator" ) )
			div( class: "student_classes_attended_filter", session.getAttribute( "studentClassesAttendedFilter" ) )
			div( class: "student_enrollment_period_filter", session.getAttribute( "studentEnrollmentPeriodFilter" ) )
			div( class: "student_fees_due_filter", session.getAttribute( "studentFeesDueFilter" ) )
			div( class: "student_fees_due_filter_operator", session.getAttribute( "studentFeesDueFilterOperator" ) )
			div( class: "student_first_name_filter", session.getAttribute( "studentFirstNameFilter" ) )
			div( class: "student_last_name_filter", session.getAttribute( "studentLastNameFilter" ) )
			div( class: "student_village_filter", session.getAttribute( "studentVillageFilter" ) )
		  }
    
		  div( class: "sort_options" ) {
		    	if( session.getAttribute( "areSortOptionsSet" ) == null ) {
			    	div( class: "student_enrollment_period_sort_direction", "dsc" )
			    	div( class: "student_enrollment_period_sort_order", 1 )
			    	div( class: "student_first_name_sort_direction", "asc" )
			    	div( class: "student_first_name_sort_order", 2 )
			    	div( class: "student_last_name_sort_direction", "asc" )
			    	div( class: "student_last_name_sort_order", 3 )
			    	
			    	div( class: "enrollment_period_sort_direction", "dsc" )
			    	div( class: "enrollment_period_sort_order", 1 )
			    	div( class: "enrollment_first_name_sort_direction", "asc" )
			    	div( class: "enrollment_first_name_sort_order", 2 )
			    	div( class: "enrollment_last_name_sort_direction", "asc" )
			    	div( class: "enrollment_last_name_sort_order", 3 )
			    	
			    	div( class: "parent_first_name_sort_direction", "asc" )
			    	div( class: "parent_first_name_sort_order", 1 )
			    	div( class: "parent_last_name_sort_direction", "asc" )
			    	div( class: "parent_last_name_sort_order", 2 )
			    	
			    	session.setAttribute( "studentEnrollmentPeriodSortDirection", "dsc" )
			    	session.setAttribute( "studentEnrollmentPeriodSortOrder", 1 )
			    	session.setAttribute( "studentFirstNameSortDirection", "asc" )
			    	session.setAttribute( "studentFirstNameSortOrder", 2 )
			    	session.setAttribute( "studentLastNameSortDirection", "asc" )
			    	session.setAttribute( "studentLastNameSortOrder", 3 )
			    	
			    	session.setAttribute( "enrollmentPeriodSortDirection", "dsc" )
			    	session.setAttribute( "enrollmentPeriodSortOrder", 1 )
			    	session.setAttribute( "enrollmentFirstNameSortDirection", "asc" )
			    	session.setAttribute( "enrollmentFirstNameSortOrder", 2 )
			    	session.setAttribute( "enrollmentLastNameSortDirection", "asc" )
			    	session.setAttribute( "enrollmentLastNameSortOrder", 3 )
			    	
			    	session.setAttribute( "parentFirstNameSortDirection", "asc" )
			    	session.setAttribute( "parentFirstNameSortOrder", 1 )
			    	session.setAttribute( "parentLastNameSortDirection", "asc" )
			    	session.setAttribute( "parentLastNameSortOrder", 2 )
			    	
			    	session.setAttribute( "areSortOptionsSet", "Y" )
			    	session.setMaxInactiveInterval( -1 )
		    	}
		    	else {
		    		if( session.getAttribute( "studentEnrollmentPeriodSortDirection" ) != null )
		    			div( class: "student_enrollment_period_sort_direction", session.getAttribute( "studentEnrollmentPeriodSortDirection" ) )
		    			
		    		if( session.getAttribute( "studentEnrollmentPeriodSortOrder" ) != null )
		    			div( class: "student_enrollment_period_sort_order", session.getAttribute( "studentEnrollmentPeriodSortOrder" ) )
		    			
		    		if( session.getAttribute( "studentFirstNameSortDirection" ) != null )
		    			div( class: "student_first_name_sort_direction", session.getAttribute( "studentFirstNameSortDirection" ) )
		    			
		    		if( session.getAttribute( "studentFirstNameSortOrder" ) != null )
		    			div( class: "student_first_name_sort_order", session.getAttribute( "studentFirstNameSortOrder" ) )
		    			
		    		if( session.getAttribute( "studentLastNameSortDirection" ) != null )
		    			div( class: "student_last_name_sort_direction", session.getAttribute( "studentLastNameSortDirection" ) )
		    			
		    		if( session.getAttribute( "studentLastNameSortOrder" ) != null )
		    			div( class: "student_last_name_sort_order", session.getAttribute( "studentLastNameSortOrder" ) )
		    			
		    		if( session.getAttribute( "enrollmentPeriodSortDirection" ) != null )
		    			div( class: "enrollment_period_sort_direction", session.getAttribute( "enrollmentPeriodSortDirection" ) )
		    			
		    		if( session.getAttribute( "enrollmentPeriodSortOrder" ) != null )
		    			div( class: "enrollment_period_sort_order", session.getAttribute( "enrollmentPeriodSortOrder" ) )
		    			
		    		if( session.getAttribute( "enrollmentFirstNameSortDirection" ) != null )
		    			div( class: "enrollment_first_name_sort_direction", session.getAttribute( "enrollmentFirstNameSortDirection" ) )
		    			
		    		if( session.getAttribute( "enrollmentFirstNameSortOrder" ) != null )
		    			div( class: "enrollment_first_name_sort_order", session.getAttribute( "enrollmentFirstNameSortOrder" ) )
		    			
		    		if( session.getAttribute( "enrollmentLastNameSortDirection" ) != null )
		    			div( class: "enrollment_last_name_sort_direction", session.getAttribute( "enrollmentLastNameSortDirection" ) )
		    			
		    		if( session.getAttribute( "enrollmentLastNameSortOrder" ) != null )
		    			div( class: "enrollment_last_name_sort_order", session.getAttribute( "enrollmentLastNameSortOrder" ) )
		    			
		    		if( session.getAttribute( "parentFirstNameSortDirection" ) != null )
		    			div( class: "parent_first_name_sort_direction", session.getAttribute( "parentFirstNameSortDirection" ) )
		    			
		    		if( session.getAttribute( "parentFirstNameSortOrder" ) != null )
		    			div( class: "parent_first_name_sort_order", session.getAttribute( "parentFirstNameSortOrder" ) )
		    			
		    		if( session.getAttribute( "parentLastNameSortDirection" ) != null )
		    			div( class: "parent_last_name_sort_direction", session.getAttribute( "parentLastNameSortDirection" ) )
		    			
		    		if( session.getAttribute( "parentLastNameSortOrder" ) != null )
		    			div( class: "parent_last_name_sort_order", session.getAttribute( "parentLastNameSortOrder" ) )
		    	}
		    }
		    
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
						a( href: "/feeTab.gtpl" ) {
							span( "Fee" )
						}
						span( class: "tab_refresh ui-icon ui-icon-arrowrefresh-1-e", "Refresh Tab" )
					}
					li {
						a( href: "/paymentTab.gtpl" ) {
							span( "Payment" )
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
		    
		    div( class: "add_fee_dialog_class_attended", title: "Select a Term" ) {
				div( class: "add_fee_dialog_class_attended_first_request", style: "display: none", "Yes" )
			}
			
			div( class: "add_fee_list_enrollment", title: "Select the payer's Enrollment" ) {
		    	div( class: "add_fee_list_enrollment_first_request", style: "display: none", "Yes" )
		    }
		    
		    div( class: "add_parent_child_list_student", title: "Select a Student Child" ) {
				div( class: "add_parent_child_list_student_first_request", style: "display: none", "Yes" )
			}
			
			div( class: "add_parent_relative_list_student", title: "Select a Student Relative" ) {
				div( class: "add_parent_relative_list_student_first_request", style: "display: none", "Yes" )
			}
			
			div( class: "add_payment_dialog_class_attended", title: "Select a Term" ) {
				div( class: "add_payment_dialog_class_attended_first_request", style: "display: none", "Yes" )
			}
			
			div( class: "add_payment_list_enrollment", title: "Select the beneficiary's Enrollment" ) {
		    	div( class: "add_payment_list_enrollment_first_request", style: "display: none", "Yes" )
		    }
		    
		    div( class: "add_payment_list_funding_source", title: "Select the Payment's Funding Source" ) {
		    	div( class: "add_payment_list_funding_source_first_request", style: "display: none", "Yes" )
		    }
		    
		    div( class: "add_student_matching_list_student", title: "You may not need to create a New Student. Select/Edit one of these Matching Students" )
			
		    div( class: "dialog_class_attended", title: "Classes attended by the selected Student" )
		    
		    div( class: "dialog_enrollment_lookup", title: "Lookup an Enrollment" )
		    
		    div( class: "dialog_list_fee", title: "Other Fees" )
		    
		    div( class: "dialog_list_parent", title: "Parents of the selected Student" )
			
			div( class: "dialog_list_payment", title: "Payments" )
			
			div( class: "dialog_parent_lookup", title: "Lookup a Parent/Guardian" )
			
			div( class: "dialog_student_lookup", title: "Lookup a Student" )
			
			div( class: "list_parent_child_list_student", title: "Select a Student Child" ) {
				div( class: "list_parent_child_list_student_first_request", style: "display: none", "Yes" )
			}
			
			div( class: "list_parent_relative_list_student", title: "Select a Student Relative" ) {
				div( class: "list_parent_relative_list_student_first_request", style: "display: none", "Yes" )
			}
	%>		
			<div class="student_birth_date_filter_sortby_dialog">
				<% include '/WEB-INF/includes/studentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="student_boarding_fees_filter_sortby_dialog">
				<% include '/WEB-INF/includes/studentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="student_classes_attended_filter_sortby_dialog">
				<% include '/WEB-INF/includes/studentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="student_enrollment_period_filter_sortby_dialog">
				<% include '/WEB-INF/includes/studentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="student_fees_due_filter_sortby_dialog">
				<% include '/WEB-INF/includes/studentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="student_first_name_filter_sortby_dialog">
				<% include '/WEB-INF/includes/studentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="student_gender_filter_sortby_dialog">
				<% include '/WEB-INF/includes/studentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="student_id_filter_sortby_dialog">
				<% include '/WEB-INF/includes/studentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="student_last_name_filter_sortby_dialog">
				<% include '/WEB-INF/includes/studentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="student_leave_reason_filter_sortby_dialog">
				<% include '/WEB-INF/includes/studentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="student_other_fees_filter_sortby_dialog">
				<% include '/WEB-INF/includes/studentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="student_payments_filter_sortby_dialog">
				<% include '/WEB-INF/includes/studentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="student_school_filter_sortby_dialog">
				<% include '/WEB-INF/includes/studentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="student_special_info_filter_sortby_dialog">
				<% include '/WEB-INF/includes/studentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="student_sponsored_filter_sortby_dialog">
				<% include '/WEB-INF/includes/studentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="student_tuition_fees_filter_sortby_dialog">
				<% include '/WEB-INF/includes/studentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="student_village_filter_sortby_dialog">
				<% include '/WEB-INF/includes/studentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="enrollment_birth_date_filter_sortby_dialog">
				<% include '/WEB-INF/includes/enrollmentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="enrollment_boarding_fees_filter_sortby_dialog">
				<% include '/WEB-INF/includes/enrollmentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="enrollment_classes_attended_filter_sortby_dialog">
				<% include '/WEB-INF/includes/enrollmentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="enrollment_period_filter_sortby_dialog">
				<% include '/WEB-INF/includes/enrollmentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="enrollment_fees_due_filter_sortby_dialog">
				<% include '/WEB-INF/includes/enrollmentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="enrollment_first_name_filter_sortby_dialog">
				<% include '/WEB-INF/includes/enrollmentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="enrollment_gender_filter_sortby_dialog">
				<% include '/WEB-INF/includes/enrollmentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="enrollment_student_id_filter_sortby_dialog">
				<% include '/WEB-INF/includes/enrollmentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="enrollment_last_name_filter_sortby_dialog">
				<% include '/WEB-INF/includes/enrollmentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="enrollment_leave_reason_filter_sortby_dialog">
				<% include '/WEB-INF/includes/enrollmentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="enrollment_other_fees_filter_sortby_dialog">
				<% include '/WEB-INF/includes/enrollmentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="enrollment_payments_filter_sortby_dialog">
				<% include '/WEB-INF/includes/enrollmentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="enrollment_school_filter_sortby_dialog">
				<% include '/WEB-INF/includes/enrollmentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="enrollment_special_info_filter_sortby_dialog">
				<% include '/WEB-INF/includes/enrollmentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="enrollment_sponsored_filter_sortby_dialog">
				<% include '/WEB-INF/includes/enrollmentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="enrollment_tuition_fees_filter_sortby_dialog">
				<% include '/WEB-INF/includes/enrollmentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="enrollment_village_filter_sortby_dialog">
				<% include '/WEB-INF/includes/enrollmentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="parent_deceased_ind_filter_sortby_dialog">
				<% include '/WEB-INF/includes/parentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="parent_email_filter_sortby_dialog">
				<% include '/WEB-INF/includes/parentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="parent_first_name_filter_sortby_dialog">
				<% include '/WEB-INF/includes/parentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="parent_id_filter_sortby_dialog">
				<% include '/WEB-INF/includes/parentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="parent_last_name_filter_sortby_dialog">
				<% include '/WEB-INF/includes/parentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="parent_primary_phone_filter_sortby_dialog">
				<% include '/WEB-INF/includes/parentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="parent_profession_filter_sortby_dialog">
				<% include '/WEB-INF/includes/parentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="parent_secondary_phone_filter_sortby_dialog">
				<% include '/WEB-INF/includes/parentFilterSortByDialog.gtpl' %>
			</div>
			
			<div class="parent_village_filter_sortby_dialog">
				<% include '/WEB-INF/includes/parentFilterSortByDialog.gtpl' %>
			</div>
<%
	  }
	  }
  }
%>