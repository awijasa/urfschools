package formatter

import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.search.Document
import com.google.appengine.api.users.User
import com.google.appengine.api.users.UserServiceFactory
import data.AnonymousParentalRelationship
import data.Class
import data.ClassAttended
import data.ClassFees
import data.Fee
import data.Gender
import data.LeaveReasonCategory
import data.Parent
import data.ParentalRelationship
import data.Payment
import data.Relationship
import data.School
import data.Student
import data.StudentDocument
import data.Term
import data.URFUser
import data.UserPrivilege
import groovy.xml.MarkupBuilder
import java.text.DecimalFormat
import org.apache.commons.lang3.StringEscapeUtils

class ListItemFormatter {
    static String getEnrollmentListItem( Document enrollmentDocument ) {
		try {
			Set<String> enrollmentDocumentFieldNames = enrollmentDocument.getFieldNames()
	        User user = UserServiceFactory.getUserService().getCurrentUser()
			Entity urfUser = URFUser.findByEmail( user?.getEmail() )
	        
	        DecimalFormat currencyFormat = new DecimalFormat( "#,##0.00" )
			
			List<Entity> userPrivilegeList = UserPrivilege.findByUserEmailAndPrivilege( user?.getEmail(), "Modify" )
	        
	        StringWriter writer = new StringWriter()
	        
	        new MarkupBuilder( writer ).h3 {
	            form( action: "javascript: void( 0 )", name: "enrollment_header_form" ) {
	                mkp.yieldUnescaped( "&nbsp;" )
	                
	                /* First Name */
	                div( class: "list_enrollment_first_name", enrollmentDocument.getOnlyField( "firstName" ).getText() )
	                
	                /* Last Name */
	                div( class: "list_enrollment_last_name", enrollmentDocumentFieldNames.contains( "lastName" )? enrollmentDocument.getOnlyField( "lastName" ).getText(): "" )
	                
	                /* Sponsored */
	                div( class: "list_enrollment_sponsored", "N" )
	                
	                /* List Boxes of Classes and Terms Attended */
	                div( class: "list_enrollment_classes_attended" ) {
	                    span( class: "list_enrollment_first_class_attended", enrollmentDocument.getOnlyField( "firstClassAttended" ).getText() )
	                    mkp.yield( " - " )
	                    span( class: "list_enrollment_last_class_attended", enrollmentDocument.getOnlyField( "lastClassAttended" ).getText() )
	                }
	                div( class: "list_enrollment_classes_attended_text", style: "display: none", enrollmentDocument.getOnlyField( "classesAttended" ).getText() )
	                div( class: "list_enrollment_first_class_attended_required_ind", "*" )
	                div( class: "list_enrollment_last_class_attended_required_ind", "*" )
	                
	                /* Get the list of Classes from each School that is modifiable by user */
	                List<Entity> classList = Class.findBySchoolName( enrollmentDocument.getOnlyField( "schoolName" ).getText() )
	
	                select( autocomplete: "off", class: "list_enrollment_first_class_attended", name: "firstClassAttended" ) {
	                    option( value: "First Class Attended" )
	
	                    classList.each(
	                        {
	                            option( value: it.getProperty( "class" ), it.getProperty( "class" ) )
	                        }
	                    )
	                }
	
	                select( autocomplete: "off", class: "list_enrollment_last_class_attended", name: "lastClassAttended" ) {
	                    option( value: "Last Class Attended" )
	
	                    classList.each(
	                        {
	                            option( value: it.getProperty( "class" ), it.getProperty( "class" ) )
	                        }
	                    )
	                }
					
					/* Get the list of Terms from each School that is modifiable by user */
	                List<Entity> termList = Term.findByTermSchool( enrollmentDocument.getOnlyField( "schoolName" ).getText() )
	
	                select( autocomplete: "off", class: "list_enrollment_leave_term", name: "leaveTerm" ) {
	                    option( value: "Leave Term", "now" )
	
	                    termList.each(
	                        {
	                            option( value: "${ it.year } Term ${ it.termNo }", "${ it.year } Term ${ it.termNo }" )
	                        }
	                    )
	                }
					
					button( class: "list_enrollment_period_lookup" )
					img( class: "list_enrollment_period_lookup_wait", src: "/images/ajax-loader.gif" )
	                
	                div( class: "list_enrollment_classes_attended_hyphen", "-" )
	                
	                div( class: "list_enrollment_period" ) {
						Integer enrollTermYear = enrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber()
						Integer enrollTermNo = enrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber()
						Integer leaveTermYear
						Integer leaveTermNo
						
						if( enrollmentDocumentFieldNames.contains( "leaveTermYear" ) ) {
							leaveTermYear = enrollmentDocument.getOnlyField( "leaveTermYear" ).getNumber()
							leaveTermNo = enrollmentDocument.getOnlyField( "leaveTermNo" ).getNumber()
						}
						
	                    span( class: "list_enrollment_term", "${ enrollTermYear } Term ${ enrollTermNo }" )
	                    mkp.yield( " - " )
	                    span( class: "list_enrollment_leave_term", "${ leaveTermYear == null? "now": leaveTermYear + " Term " + leaveTermNo }" )
	                }
					
					div( class: "list_enrollment_terms", style: "display: none", enrollmentDocument.getOnlyField( "termsEnrolled" ).getText() )
	            }
	        }
	        
	        new MarkupBuilder( writer ).div( class: "list_enrollment_details" ) {
	            form( action: "javascript: void( 0 )", name: "enrollment_details_form" ) {
	                
	                /* Birth Date */
	                a( class: "list_enrollment_birth_date_label", href: "javascript:void( 0 )", "Birth Date:" )
	                div( class: "list_enrollment_birth_date", enrollmentDocumentFieldNames.contains( "birthDate" )? enrollmentDocument.getOnlyField( "birthDate" ).getDate().format( "MMM d yyyy" ): "" )
	                
	                /* Village */
	                a( class: "list_enrollment_village_label", href: "javascript:void( 0 )", "Village:" )
	                div( class: "list_enrollment_village", enrollmentDocumentFieldNames.contains( "village" )? enrollmentDocument.getOnlyField( "village" ).getText(): "" )
	                
	                /* ID */
	                a( class: "list_enrollment_student_id_label", href: "javascript:void( 0 )", "ID" )
	                div( class: "list_enrollment_student_id", ": ${ enrollmentDocument.getOnlyField( "studentId" ).getAtom() }" )
	                
	                /* Parent/Guardians Table */
	                div( class: "list_enrollment_parents_label", "Parents/Guardians" )
	                button( class: "list_enrollment_parents_lookup" )
					img( class: "list_enrollment_parents_lookup_wait", src: "/images/ajax-loader.gif" )
	                div( class: "list_enrollment_parents_relationship_label", "Relationship" )
	                div( class: "list_enrollment_parents_first_name_label", "First Name" )
	                div( class: "list_enrollment_parents_last_name_label", "Last Name" )
	                div( class: "list_enrollment_parents_deceased_label", "Deceased" )
	                div( class: "list_enrollment_parents_profession_label", "Profession" )
	                
					div( class: "list_enrollment_parents_container" ) {
						table( class: "list_enrollment_parents" ) {
		                    AnonymousParentalRelationship.findByStudentId( enrollmentDocument.getOnlyField( "studentId" ).getAtom() ).each(
		                        { anonymousParent ->
		                            List<Entity> parentalRelationships = Relationship.findParentalRelationshipsByStudentIdAndParentalRelationship( enrollmentDocument.getOnlyField( "studentId" ).getAtom(), anonymousParent.parentalRelationship )
									
									if( parentalRelationships.size() == 0 ) {
										tr {
			                                /* Parental Relationship Text Field */
			                                td( class: "list_enrollment_parent_relationship", anonymousParent.parentalRelationship )
											
											/* Parent First Name Text Field */
											td( class: "list_enrollment_parent_first_name" )
											
											/* Parental Relationship Text Field */
											td( class: "list_enrollment_parent_last_name" )
			                                
			                                /* Parent Deceased Checkbox */
			                                td( class: "list_enrollment_parent_deceased" ) {
			                                    span( class: "list_enrollment_parent_deceased_ind", anonymousParent.deceasedInd )
			                                }
											
											/* Parent Profession Text Field */
											td( class: "list_enrollment_parent_profession" )
			                            }
									}
									else {
										parentalRelationships.each(
											{ parentalRelationship ->
												Entity parent = Parent.findByParentId( parentalRelationship.parentId )
												
												tr {
					                                /* Parental Relationship Text Field */
					                                td( class: "list_enrollment_parent_relationship", parentalRelationship.parentalRelationship )
													
													/* Parent First Name Text Field */
													td( class: "list_enrollment_parent_first_name", parent.firstName )
													
													/* Parental Relationship Text Field */
													td( class: "list_enrollment_parent_last_name", parent.lastName?: "" )
					                                
					                                /* Parent Deceased Checkbox */
					                                td( class: "list_enrollment_parent_deceased" ) {
					                                    span( class: "list_enrollment_parent_deceased_ind", parent.deceasedInd )
					                                }
													
													/* Parent Profession Text Field */
													td( class: "list_enrollment_parent_profession", parent.profession?: "" )
					                            }
											}
										)
									}
		                        }
		                    )
							
							Relationship.findGuardianRelationshipsByStudentId( enrollmentDocument.getOnlyField( "studentId" ).getAtom() ).each(
								{ guardianRelationship ->
									Entity guardian = Parent.findByParentId( guardianRelationship.parentId )
									
									tr {
										/* Parental Relationship Text Field */
										td( class: "list_enrollment_parent_relationship", guardianRelationship.guardianRelationship )
										
										/* Parent First Name Text Field */
										td( class: "list_enrollment_parent_first_name", guardian.firstName )
										
										/* Parental Relationship Text Field */
										td( class: "list_enrollment_parent_last_name", guardian.lastName?: "" )
										
										/* Parent Deceased Checkbox */
										td( class: "list_enrollment_parent_deceased" ) {
											span( class: "list_enrollment_parent_deceased_ind", guardian.deceasedInd )
										}
										
										/* Parent Profession Text Field */
										td( class: "list_enrollment_parent_profession", guardian.profession?: "" )
									}
								}
							)
		                }
	                }
	                
	                /* Gender Code */
	                a( class: "list_enrollment_gender_label", href: "javascript:void( 0 )", "Gender:" )
	                div( class: "list_enrollment_gender", enrollmentDocumentFieldNames.contains( "genderCode" )? enrollmentDocument.getOnlyField( "genderCode" ).getText(): "" )
	                
	                /* Special Info/Condition */
	                a( class: "list_enrollment_special_info_label", href: "javascript:void( 0 )", "Special Info/Condition:" )
	                div( class: "list_enrollment_special_info" ) {
						mkp.yieldUnescaped( StringEscapeUtils.escapeHtml4( enrollmentDocumentFieldNames.contains( "specialInfo" )? enrollmentDocument.getOnlyField( "specialInfo" ).getText(): "" ).replaceAll( "\n", "<br>" ) )
	                }
	                
	                div( class: "list_enrollment_label", "Enrollment" )
	                
	                /* School List Box */
	                a( class: "list_enrollment_school_label", href: "javascript:void( 0 )", "School:" )
	                div( class: "list_enrollment_school", enrollmentDocument.getOnlyField( "schoolName" ).getText() )
	                
	                /* Leave Reason Category List Box */
	                a( class: "list_enrollment_leave_reason_label", href: "javascript:void( 0 )", "Leave Reason:" )
	                div( class: "list_enrollment_leave_reason_category", enrollmentDocumentFieldNames.contains( "leaveReasonCategory" )? enrollmentDocument.getOnlyField( "leaveReasonCategory" ).getText(): "" )
	                select( autocomplete: "off", class: "list_enrollment_leave_reason_category", name: "leaveReasonCategory" ) {
	                    option( value: "Leave Reason Category" )
	                    
	                    LeaveReasonCategory.list().each(
	                        {
	                            option( value: it.category, it.category )
	                        }
	                    )
	                }
	                
	                /* Leave Reason Text Area */
	                div( class: "list_enrollment_leave_reason" ) {
						mkp.yieldUnescaped( StringEscapeUtils.escapeHtml4( enrollmentDocumentFieldNames.contains( "leaveReason" )? enrollmentDocument.getOnlyField( "leaveReason" ).getText(): "" ).replaceAll( "\n", "<br>" ) )
	                }
	                textarea( autocomplete: "off", class: "list_enrollment_leave_reason ui-corner-all", name: "leaveReason" )
	                
	                /* Tuition Fees */
	                a( class: "list_enrollment_tuition_fees_label", href: "javascript:void( 0 )", "Tuition Fees" )
	                div( class: "list_enrollment_tuition_fees", currencyFormat.format( enrollmentDocument.getOnlyField( "tuitionFees" ).getNumber() ) )
	                
	                /* Boarding Fees */
	                a( class: "list_enrollment_boarding_fees_label", href: "javascript:void( 0 )", "Boarding Fees" )
	                div( class: "list_enrollment_boarding_fees", currencyFormat.format( enrollmentDocument.getOnlyField( "boardingFees" ).getNumber() ) )
					
					/* Other Fees */
					a( class: "list_enrollment_other_fees_label", href: "javascript:void( 0 )", "Other Fees" )
					img( class: "list_enrollment_other_fees_wait", src: "/images/ajax-loader.gif" )
					div( class: "list_enrollment_other_fees", currencyFormat.format( enrollmentDocument.getOnlyField( "otherFees" ).getNumber() ) )
					
					/* Payments */
					a( class: "list_enrollment_payments_label", href: "javascript:void( 0 )", "Payments" )
					img( class: "list_enrollment_payments_wait", src: "/images/ajax-loader.gif" )
					div( class: "list_enrollment_payments", "- ${ currencyFormat.format( enrollmentDocument.getOnlyField( "payments" ).getNumber() ) }" )
					
					/* Fees Due */
					a( class: "list_enrollment_fees_due_label", href: "javascript:void( 0 )", "Fees Due" )
					div( class: "list_enrollment_fees_due", currencyFormat.format( enrollmentDocument.getOnlyField( "feesDue" ).getNumber() ) )
	                
	                /* Last Update */
	                div( class: "list_enrollment_last_update_label", "Last Update:" )
	                div( class: "list_enrollment_last_update_on_label", "on" )
	                div( class: "list_enrollment_last_update_on", enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate() )
	                input( class: "list_enrollment_last_update_date", name: "lastUpdateDate", type: "hidden", value: enrollmentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
	                div( class: "list_enrollment_last_update_by_label", "by" )
	                
					if( urfUser != null ) {
						div( class: "list_enrollment_last_update_by", enrollmentDocumentFieldNames.contains( "lastUpdateUser" )? enrollmentDocument.getOnlyField( "lastUpdateUser" ).getText(): "" )
		                input( class: "list_enrollment_last_update_user", name: "lastUpdateUser", type: "hidden", value: enrollmentDocumentFieldNames.contains( "lastUpdateUser" )? enrollmentDocument.getOnlyField( "lastUpdateUser" ).getText(): "" )
					}
	                
	                /* Will contain Y if the client determines that this Enrollment record is stale due to changes in other records */
					div( class: "list_enrollment_stale_ind", style: "display: none" )
					
					input( name: "id", type: "hidden", value: enrollmentDocument.getId() )
	                input( name: "studentId", type: "hidden", value: enrollmentDocument.getOnlyField( "studentId" ).getAtom() )
	                
	                if( UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), enrollmentDocument.getOnlyField( "schoolName" ).getText() )?.privilege?.equals( "Modify" ) ) {
	                    button( class: "list_enrollment_edit", type: "button" )
	                    button( class: "list_enrollment_delete", type: "button" )
	                    
	                    button( class: "list_enrollment_save", type: "button" )
	                    button( class: "list_enrollment_cancel", type: "button" )
	                }
					
					a( class: "list_enrollment_select", href: "javascript:void( 0 )", "select" )
	                
	                img( class: "list_enrollment_save_wait", src: "/images/ajax-loader.gif" )
	            }
	        }
	        
	        return writer.toString()
		}
		catch( Exception e ) {
			return e.getMessage()
		}
    }
	
	static String getFeeListItem( Entity fee ) {
		User user = UserServiceFactory.getUserService().getCurrentUser()
		Entity urfUser = URFUser.findByEmail( user?.getEmail() )
		
		DecimalFormat currencyFormat = new DecimalFormat( "#,##0.00" )
		
		StringWriter writer = new StringWriter()
		
		new MarkupBuilder( writer ).h3 {
			form( action: "javascript:void( 0 )", name: "fee_header_form" ) {
				mkp.yieldUnescaped( "&nbsp;" )

				/* Student ID Link */
				a( class: "list_fee_student_id", href: "javascript:void( 0 )", fee.studentId )
				div( class: "list_fee_student_id", fee.studentId )
				img( class: "list_fee_student_id_wait", src: "/images/ajax-loader.gif" )
				input( autocomplete: "off", class: "list_fee_student_id", name: "studentId", type: "hidden", value: fee.studentId )
				input( autocomplete: "off", class: "list_fee_school_name", name: "schoolName", type: "hidden", value: fee.schoolName )
				input( autocomplete: "off", class: "list_fee_enroll_term_no", name: "enrollTermNo", type: "hidden", value: fee.enrollTermNo )
				input( autocomplete: "off", class: "list_fee_enroll_term_year", name: "enrollTermYear", type: "hidden", value: fee.enrollTermYear )
				div( class: "list_fee_fees_due", StudentDocument.findByStudentId( fee.studentId ).getOnlyField( "feesDue" ).getNumber() )

				/* Name Text Field */
				div( class: "list_fee_name", fee.name )
				
				/* Term */
				div( class: "list_fee_term", "${ fee.classTermYear } Term ${ fee.classTermNo }" )
				input( autocomplete: "off", class: "list_fee_class_term_no", name: "classTermNo", type: "hidden", value: fee.classTermNo )
				input( autocomplete: "off", class: "list_fee_class_term_year", name: "classTermYear", type: "hidden", value: fee.classTermYear )

				/* Amount Text Field */
				div( class: "list_fee_amount_currency", "UGX" )
				div( class: "list_fee_amount_required_ind", "*" )
				div( class: "list_fee_amount", currencyFormat.format( fee.amount ) )
				input( autocomplete: "off", class: "list_fee_amount", name: "amount", type: "text", value: currencyFormat.format( fee.amount ) )
			}
		}
		
		new MarkupBuilder( writer ).div( class: "list_fee_details" ) {
			form( action: "javascript: void( 0 )", name: "fee_details_form" ) {
				
				/* Comment Text Area */
				a( class: "list_fee_comment_label", href: "javascript:void( 0 )", "Comment:" )
				div( class: "list_fee_comment" ) {
					mkp.yieldUnescaped( StringEscapeUtils.escapeHtml4( fee.comment )?.replaceAll( "\n", "<br>" )?: "" )
				}
				textarea( autocomplete: "off", class: "list_fee_comment ui-corner-all", name: "comment" )
				
				/* Last Update */
				div( class: "list_fee_last_update_label", "Last Update:" )
				div( class: "list_fee_last_update_on_label", "on" )
				div( class: "list_fee_last_update_on", fee.lastUpdateDate )
				input( class: "list_fee_last_update_date", name: "lastUpdateDate", type: "hidden", value: fee.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
				div( class: "list_fee_last_update_by_label", "by" )
				
				if( urfUser != null ) {
					div( class: "list_fee_last_update_by", fee.lastUpdateUser )
					input( class: "list_fee_last_update_user", name: "lastUpdateUser", type: "hidden", value: fee.lastUpdateUser?: "" )
				}
				
				input( autocomplete: "off", name: "id", type: "hidden", value: fee.getKey().getId() )
				
				if( UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), fee.schoolName )?.privilege?.equals( "Modify" ) ) {
					button( class: "list_fee_edit", type: "button" )
					button( class: "list_fee_delete", type: "button" )
					
					button( class: "list_fee_save", type: "button" )
					button( class: "list_fee_cancel", type: "button" )
				}
				
				a( class: "list_fee_select", href: "javascript:void( 0 )", "select" )
				
				img( class: "list_fee_save_wait", src: "/images/ajax-loader.gif" )
			}
		}
		
		return writer.toString()
	}
    
    static String getGenderListItem( Entity gender ) {
        User user = UserServiceFactory.getUserService().getCurrentUser()
        String adminPrivilege = URFUser.findByEmail( user?.getEmail() )?.adminPrivilege
        
        StringWriter writer = new StringWriter()
        
        new MarkupBuilder( writer ).tr( class: "gender_list_tr" ) {
            td( width: 163, style: "padding-right: 0px" ) {
                div( class: "ui-corner-left", style: "padding-left: 5px", gender.code )
            }
            
            td( style: "padding-left: 0px" ) {
                div( class: "ui-corner-right" ) {
                    form( action: "javascript:void( 0 )", name: "edit_gender_form" ) {
                        
                        /* Gender Desc Text Field */
                        span( class: "list_gender_desc", gender.desc )
                        input( autocomplete: "off", class: "list_gender_desc ui-corner-all", name: "desc", type: "text" )
                        
                        input( name: "action", type: "hidden", value: "edit" )
                        input( name: "id", type: "hidden", value: gender.getKey().getId() )
                        input( class: "list_gender_last_update_date", name: "lastUpdateDate", type: "hidden", value: gender.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
                        input( class: "list_gender_last_update_user", name: "lastUpdateUser", type: "hidden", value: gender.lastUpdateUser?: "" )
                    }
                }
            }
            
            td( width: 20 ) {
                if( adminPrivilege == "Modify" ) {
                    button( class: "list_gender_edit", type: "button" )
                    button( class: "list_gender_save", type: "button" )
                }
            }
            
            td( width: 20 ) {
                if( adminPrivilege == "Modify" ) {
                    form( action: "GenderController.groovy", method: "post", name: "delete_gender_form" ) {
                        input( name: "action", type: "hidden", value: "delete" )
                        input( name: "id", type: "hidden", value: gender.getKey().getId() )
                        input( class: "list_gender_last_update_date", name: "lastUpdateDate", type: "hidden", value: gender.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
                        input( class: "list_gender_last_update_user", name: "lastUpdateUser", type: "hidden", value: gender.lastUpdateUser?: "" )
                        
                        button( class: "list_gender_delete", type: "button" )
                        button( class: "list_gender_cancel", type: "button" )
                    }
                }
            }
        }
        
        return writer.toString()
    }
    
    static String getLeaveReasonCategoryListItem( Entity leaveReasonCategory ) {
        User user = UserServiceFactory.getUserService().getCurrentUser()
        
        StringWriter writer = new StringWriter()
        
        new MarkupBuilder( writer ).tr( class: "leave_reason_category_list_tr" ) {
            td {
                div( class: "ui-corner-all", style: "padding-left: 5px", leaveReasonCategory.category )
            }
            
            td( width: 20 )
            
            td( width: 20 ) {
                if( URFUser.findByEmail( user?.getEmail() )?.adminPrivilege == "Modify" )
                    form( action: "LeaveReasonCategoryController.groovy", method: "post", name: "delete_leave_reason_category_form" ) {
                        input( name: "action", type: "hidden", value: "delete" )
                        input( name: "id", type: "hidden", value: leaveReasonCategory.getKey().getId() )
                        input( name: "lastUpdateDate", type: "hidden", value: leaveReasonCategory.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
                        input( name: "lastUpdateUser", type: "hidden", value: leaveReasonCategory.lastUpdateUser?: "" )
                    
                        button( class: "list_leave_reason_category_delete", type: "button" )
                    }
            }
        }
        
        return writer.toString()
    }
    
    static String getParentalRelationshipListItem( Entity parentalRelationship ) {
        User user = UserServiceFactory.getUserService().getCurrentUser()
        
        StringWriter writer = new StringWriter()
        
        new MarkupBuilder( writer ).tr( class: "parental_relationship_list_tr" ) {
            td( width: 935, style: "padding-right: 0px" ) {
                div( class: "ui-corner-all", style: "padding-left: 5px", parentalRelationship.parentalRelationship )
            }
            
            td( width: 20 )
            
            td( width: 20 ) {
                if( URFUser.findByEmail( user?.getEmail() )?.adminPrivilege == "Modify" )
                    form( action: "ParentalRelationshipController.groovy", method: "post", name: "delete_parental_relationship_form" ) {
                        input( name: "action", type: "hidden", value: "delete" )
                        input( name: "id", type: "hidden", value: parentalRelationship.getKey().getId() )
                        input( name: "lastUpdateDate", type: "hidden", value: parentalRelationship.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
                        input( name: "lastUpdateUser", type: "hidden", value: parentalRelationship.lastUpdateUser?: "" )
                        
                        button( class: "list_parental_relationship_delete", type: "button" )
                    }
            }
        }
        
        return writer.toString()
    }
    
    static String getParentListItem( Entity parent, String studentIdNotToLink ) {
		Entity urfUser = URFUser.findByEmail( UserServiceFactory.getUserService().getCurrentUser()?.getEmail() )
    	StringWriter writer = new StringWriter()
    	
    	new MarkupBuilder( writer ).h3 {
    		form( action: "javascript:void( 0 )", name: "parent_header_form" ) {
                mkp.yieldUnescaped( "&nbsp;" )

                /* First Name Text Field */
                div( class: "list_parent_first_name", parent.firstName )
                div( class: "list_parent_first_name_required_ind", "*" )
                input( autocomplete: "off", class: "list_parent_first_name ui-corner-all", name: "firstName", type: "text" )

                /* Last Name Text Field */
				if( urfUser != null ) {
	                div( class: "list_parent_last_name", parent.lastName?: "" )
	                input( autocomplete: "off", class: "list_parent_last_name ui-corner-all", name: "lastName", type: "text" )
				}

                /* Deceased Checkbox */
                div( class: "list_parent_deceased", parent.deceasedInd?: "N" )
                label( class: "list_parent_deceased_label" ) {
                	if( parent.deceasedInd == "Y" )
                		input( autocomplete: "off", checked: "checked", name: "deceasedInd", type: "checkbox" )
                	else
                		input( autocomplete: "off", name: "deceasedInd", type: "checkbox" )
                		
                	mkp.yield( "Yes" )
                }

                /* Village Text Field */
                if( urfUser != null ) {
					div( class: "list_parent_village", parent.village )
	                input( autocomplete: "off", class: "list_parent_village ui-corner-all", name: "village", type: "text" )
	                
	                /* Primary Phone Text Field */
	                div( class: "list_parent_primary_phone", parent.primaryPhone )
	                input( autocomplete: "off", class: "list_parent_primary_phone ui-corner-all", name: "primaryPhone", type: "tel" )
                }
            }
    	}
    	
    	new MarkupBuilder( writer ).div( class: "list_parent_details" ) {
    		form( action: "javascript:void( 0 )", name: "parent_details_form" ) {
    			a( class: "list_parent_email_label", href: "javascript:void( 0 )", "Email" )
    			div( class: "list_parent_email_colon", ":" )
    			
				if( urfUser != null ) {
					div( class: "list_parent_email", parent.email )
	    			input( autocomplete: "off", class: "list_parent_email ui-corner-all", name: "email", type: "email" )
				}
    			
    			a( class: "list_parent_secondary_phone_label", href: "javascript:void( 0 )", "Secondary Phone:" )
    			
				if( urfUser != null ) {
					div( class: "list_parent_secondary_phone", parent.secondaryPhone )
	    			input( autocomplete: "off", class: "list_parent_secondary_phone ui-corner-all", name: "secondaryPhone", type: "tel" )
				}
    			
    			a( class: "list_parent_id_label", href: "javascript:void( 0 )", "ID:" )
    			
				if( urfUser != null )
					div( class: "list_parent_id", parent.parentId )
    			
    			a( class: "list_parent_profession_label", href: "javascript:void( 0 )", "Profession" )
    			div( class: "list_parent_profession_colon", ":" )
    			
				if( urfUser != null ) {
					div( class: "list_parent_profession", parent.profession )
	    			input( autocomplete: "off", class: "list_parent_profession ui-corner-all", name: "profession", type: "text" )
				}
    			
    			/* Parent of Section */
				div( class: "list_parent_parent_of_label", "Parent of" )
	
				/* Relationship List Box */
				select( autocomplete: "off", class: "list_parent_relationship_list" ) {
					option( "Relationship" )
		
					ParentalRelationship.list().each(
						{
							option( value: it.parentalRelationship, it.parentalRelationship )
						}
					)
				}
	
				div( class: "list_parent_of_label", "of" )
	
				a( class: "list_parent_child_selector", href: "javascript:void( 0 )", "select" )
				img( class: "list_parent_child_selector_wait", src: "/images/ajax-loader.gif" )
	
				button( class: "list_parent_child_button", type: "button" )
	
				div( class: "list_parent_child_table_container" ) {
					table( class: "list_parent_child_table" ) {
						Relationship.findParentalRelationshipsByParentAndUserPrivilege( parent ).each(
							{ relationship ->
								tr {
									td( class: "list_parent_child_table_relationship", relationship.parentalRelationship )
									td( class: "list_parent_child_table_selector" ) {
										mkp.yield( "of " )
										
										if( studentIdNotToLink == relationship.studentId )
											mkp.yield( relationship.studentId )
										else
											a( href: "javascript:void( 0 )", relationship.studentId )
											
										img( class: "list_parent_child_table_selector_wait", src: "/images/ajax-loader.gif" )
									}
									td {
										button( class: "list_parent_child_delete_button", type: "button" )
									}
								}
							}
						)
					}
				}
	
				/* Guardian of Section */
				div( class: "list_parent_guardian_of_label", "Guardian of" )
	
				/* Relationship Text Box */
				div( class: "list_parent_relationship_label ui-corner-all", "Relationship" )
				input( autocomplete: "off", class: "list_parent_relationship", name: "guardianRelationship", value: "Relationship" )
	
				div( class: "list_guardian_of_label", "of" )
	
				a( class: "list_parent_relative_selector", href: "javascript:void( 0 )", "select" )
				img( class: "list_parent_relative_selector_wait", src: "/images/ajax-loader.gif" )
	
				button( class: "list_parent_relative_button", type: "button" )
	
				div( class: "list_parent_relative_table_container" ) {
					table( class: "list_parent_relative_table" ){
						Relationship.findGuardianRelationshipsByParentAndUserPrivilege( parent ).each(
							{ relationship ->
								tr {
									td( class: "list_parent_relative_table_relationship", relationship.guardianRelationship )
									td( class: "list_parent_relative_table_selector" ) {
										mkp.yield( "of " )
										
										if( studentIdNotToLink == relationship.studentId )
											mkp.yield( relationship.studentId )
										else
											a( href: "javascript:void( 0 )", relationship.studentId )
											
										img( class: "list_parent_relative_table_selector_wait", src: "/images/ajax-loader.gif" )
									}
									td {
										button( class: "list_parent_relative_delete_button", type: "button" )
									}
								}
							}
						)
					}
				}
				
				/* Last Update */
                div( class: "list_parent_last_update_label", "Last Update:" )
                div( class: "list_parent_last_update_on_label", "on" )
                div( class: "list_parent_last_update_on", parent.lastUpdateDate )
                input( class: "list_parent_last_update_date", name: "lastUpdateDate", type: "hidden", value: parent.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
                div( class: "list_parent_last_update_by_label", "by" )
				
				if( urfUser != null ) {
	                div( class: "list_parent_last_update_by", parent.lastUpdateUser )
	                input( class: "list_parent_last_update_user", name: "lastUpdateUser", type: "hidden", value: parent.lastUpdateUser?: "" )
				}
                
                input( name: "id", type: "hidden", value: parent.getKey().getId() )
                
				if( urfUser != null ) {
	                button( class: "list_parent_edit", type: "button" )
					button( class: "list_parent_delete", type: "button" )
					
					button( class: "list_parent_save", type: "button" )
					button( class: "list_parent_cancel", type: "button" )
				}
				
				a( class: "list_parent_select", href: "javascript:void( 0 )", "select" )
                
                img( class: "list_parent_save_wait", src: "/images/ajax-loader.gif" )
    		}
    	}
    	
    	return writer.toString()
    }
	
	static String getPaymentListItem( Entity payment ) {
		User user = UserServiceFactory.getUserService().getCurrentUser()
		Entity urfUser = URFUser.findByEmail( user?.getEmail() )
		
		DecimalFormat currencyFormat = new DecimalFormat( "#,##0.00" )
		
		StringWriter writer = new StringWriter()
		
		new MarkupBuilder( writer ).h3 {
			form( action: "javascript:void( 0 )", name: "payment_header_form" ) {
				mkp.yieldUnescaped( "&nbsp;" )

				/* Student ID Link */
				a( class: "list_payment_student_id", href: "javascript:void( 0 )", payment.studentId )
				div( class: "list_payment_student_id", payment.studentId )
				img( class: "list_payment_student_id_wait", src: "/images/ajax-loader.gif" )
				input( autocomplete: "off", class: "list_payment_student_id", name: "studentId", type: "hidden", value: payment.studentId )
				input( autocomplete: "off", class: "list_payment_school_name", name: "schoolName", type: "hidden", value: payment.schoolName )
				input( autocomplete: "off", class: "list_payment_enroll_term_no", name: "enrollTermNo", type: "hidden", value: payment.enrollTermNo )
				input( autocomplete: "off", class: "list_payment_enroll_term_year", name: "enrollTermYear", type: "hidden", value: payment.enrollTermYear )
				div( class: "list_payment_fees_due", StudentDocument.findByStudentId( payment.studentId ).getOnlyField( "feesDue" ).getNumber() )

				/* Funding Source Link */
				if( payment.fundingSource == "School" || payment.fundingSource == "Self" )
					div( class: "list_payment_funding_source", payment.fundingSource )
				else
					a( class: "list_payment_funding_source", href: "javascript:void( 0 )", payment.fundingSource )
					
				img( class: "list_payment_funding_source_wait", src: "/images/ajax-loader.gif" )
				input( autocomplete: "off", class: "list_payment_funding_source", name: "fundingSource", type: "hidden", value: payment.fundingSource )

				/* Term */
				div( class: "list_payment_term", "${ payment.classTermYear } Term ${ payment.classTermNo }" )
				input( autocomplete: "off", class: "list_payment_class_term_no", name: "classTermNo", type: "hidden", value: payment.classTermNo )
				input( autocomplete: "off", class: "list_payment_class_term_year", name: "classTermYear", type: "hidden", value: payment.classTermYear )

				/* Amount Text Field */
				div( class: "list_payment_amount_currency", "UGX" )
				div( class: "list_payment_amount_required_ind", "*" )
				div( class: "list_payment_amount", currencyFormat.format( payment.amount ) )
				input( autocomplete: "off", class: "list_payment_amount", name: "amount", type: "text", value: currencyFormat.format( payment.amount ) )
			}
		}
		
		new MarkupBuilder( writer ).div( class: "list_payment_details" ) {
			form( action: "javascript: void( 0 )", name: "payment_details_form" ) {
				
				/* Comment Text Area */
				a( class: "list_payment_comment_label", href: "javascript:void( 0 )", "Comment:" )
				div( class: "list_payment_comment" ) {
					mkp.yieldUnescaped( StringEscapeUtils.escapeHtml4( payment.comment )?.replaceAll( "\n", "<br>" )?: "" )
				}
				textarea( autocomplete: "off", class: "list_payment_comment ui-corner-all", name: "comment" )
				
				/* Last Update */
				div( class: "list_payment_last_update_label", "Last Update:" )
				div( class: "list_payment_last_update_on_label", "on" )
				div( class: "list_payment_last_update_on", payment.lastUpdateDate )
				input( class: "list_payment_last_update_date", name: "lastUpdateDate", type: "hidden", value: payment.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
				div( class: "list_payment_last_update_by_label", "by" )
				
				if( urfUser != null ) {
					div( class: "list_payment_last_update_by", payment.lastUpdateUser )
					input( class: "list_payment_last_update_user", name: "lastUpdateUser", type: "hidden", value: payment.lastUpdateUser?: "" )
				}
				
				input( autocomplete: "off", name: "id", type: "hidden", value: payment.getKey().getId() )
				
				if( UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), payment.schoolName )?.privilege?.equals( "Modify" ) ) {
					button( class: "list_payment_edit", type: "button" )
					button( class: "list_payment_delete", type: "button" )
					
					button( class: "list_payment_save", type: "button" )
					button( class: "list_payment_cancel", type: "button" )
				}
				
				a( class: "list_payment_select", href: "javascript:void( 0 )", "select" )
				
				img( class: "list_payment_save_wait", src: "/images/ajax-loader.gif" )
			}
		}
		
		return writer.toString()
	}
    
    static String getSchoolListItem( Entity school, Integer schoolIndex, Entity userPrivilege ) {
        User user = UserServiceFactory.getUserService().getCurrentUser()
        Entity urfUser = URFUser.findByEmail( user?.getEmail() )
        String schoolPrivilege = ( userPrivilege?.privilege )?: ( UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), school.name )?.privilege )
		
		if( schoolIndex == null )
			schoolIndex = 1
        
        if( ( school.name.equals( "Test" ) && urfUser == null ) || schoolPrivilege?.equals( "Read" ) || schoolPrivilege?.equals( "Modify" ) ) {
            StringWriter writer = new StringWriter()
                
            new MarkupBuilder( writer ).tr( class: "school_list_tr" ) {
                
                /* School Name Text Field */
                td( width: 335, style: "padding-right: 0px" ) {
                    div( class: "list_school_name_col", school.name )
                }
                
                td( style: "padding-left: 0px" ) {
                    div( class: "list_school_classes_col" ) {
                        Class.findBySchool( school ).eachWithIndex(
                            { schoolClass, index ->
                                GString listSchoolClassId = "list_school${ schoolIndex }_class${ index + 1 }"
								input( id: listSchoolClassId, type: "checkbox" )
								label( for: listSchoolClassId, schoolClass.getProperty( "class" ) )
								input( name: "class${ index + 1 }", type: "hidden", value: schoolClass.getProperty( "class" ) )
                            }
                        )
                        
                        /* New Class Text Field */
                        br()
                        form( action: "javascript:void( 0 )", name: "edit_school_form" ) {
                            input( name: "action", type: "hidden", value: "edit" )
                            input( name: "schoolName", type: "hidden", value: school.name )
                            input( class: "list_school_last_update_date", name: "lastUpdateDate", type: "hidden", value: school.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
                            input( class: "list_school_last_update_user", name: "lastUpdateUser", type: "hidden", value: school.lastUpdateUser )
                            
                            input( autocomplete: "off", class: "list_school_add_class ui-corner-all", name: "string", type: "text", value: "New Class" )
                            button( class: "list_school_add_class_button", type: "button" )
                            img( class: "list_school_add_class_button_wait", src: "/images/ajax-loader.gif" )
							
							div( class: "list_school_class_controls" ) {
								span( class: "list_school_decrement_class_level list_school_disabled glyphicons glyphicons_arrow_up" )
								span( class: "list_school_increment_class_level list_school_disabled glyphicons glyphicons_arrow_down" )
								span( class: "list_school_delete_class list_school_disabled glyphicons glyphicons_trash" )
                            }
                        }
                    }
                }
                
                td( width: 20 ) {
                    if( urfUser?.adminPrivilege?.equals( "Modify" ) && schoolPrivilege?.equals( "Modify" ) ) {
                        button( class: "list_school_edit", type: "button" )
                        button( class: "list_school_save", type: "button" )
                        img( class: "list_school_save_wait", src: "/images/ajax-loader.gif" )
                    }
                }
                
                td( width: 20 ) {
                    if( urfUser?.adminPrivilege?.equals( "Modify" ) && schoolPrivilege?.equals( "Modify" ) ) {
                        form( action: "SchoolController.groovy", method: "post", name: "delete_school_form" ) {
                            input( name: "action", type: "hidden", value: "delete" )
                            input( name: "id", type: "hidden", value: school.getKey().getId() )
                            input( name: "schoolName", type: "hidden", value: school.name )
                            input( class: "list_school_last_update_date", name: "lastUpdateDate", type: "hidden", value: school.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
                            input( class: "list_school_last_update_user", name: "lastUpdateUser", type: "hidden", value: school.lastUpdateUser )
                            
                            button( class: "list_school_delete", type: "button" )
                            button( class: "list_school_cancel", type: "button" )
                        }
                    }
                }
            }
            
            return writer.toString()
        }
    }
    
    static String getStudentListItem( Document studentDocument, Entity student ) {
		try {
			Set<String> studentDocumentFieldNames = studentDocument.getFieldNames()
			User user = UserServiceFactory.getUserService().getCurrentUser()
			Entity urfUser = URFUser.findByEmail( user?.getEmail() )
	        
	        DecimalFormat currencyFormat = new DecimalFormat( "#,##0.00" )
	        
	        List<Entity> userPrivilegeList = UserPrivilege.findByUserEmailAndPrivilege( user?.getEmail(), "Modify" )
	        
	        StringWriter writer = new StringWriter()
	        
	        new MarkupBuilder( writer ).h3 {
	            form( action: "javascript:void( 0 )", name: "student_header_form" ) {
	                mkp.yieldUnescaped( "&nbsp;" )
	
	                /* First Name Text Field */
	                div( class: "list_student_first_name", studentDocument.getOnlyField( "firstName" ).getText() )
	                div( class: "list_student_first_name_required_ind", "*" )
	                input( autocomplete: "off", class: "list_student_first_name ui-corner-all", name: "firstName", type: "text" )
	
	                /* Last Name Text Field */
	                div( class: "list_student_last_name", studentDocumentFieldNames.contains( "lastName" )? studentDocument.getOnlyField( "lastName" ).getText(): "" )
	                input( autocomplete: "off", class: "list_student_last_name ui-corner-all", name: "lastName", type: "text" )
	
	                /* Sponsored Text Field */
	                div( class: "list_student_sponsored", "N" )
	
	                /* List Boxes of Classes and Terms Attended */
	                div( class: "list_student_classes_attended" ) {
	                    span( class: "list_student_first_class_attended", studentDocument.getOnlyField( "lastEnrollmentFirstClassAttended" ).getText() )
	                    mkp.yield( " - " )
	                    span( class: "list_student_last_class_attended", studentDocument.getOnlyField( "lastEnrollmentLastClassAttended" ).getText() )
	                }
	
	                div( class: "list_student_classes_attended_text", style: "display: none", studentDocument.getOnlyField( "classesAttended" ).getText() )
	                div( class: "list_student_first_class_attended_required_ind", "*" )
	                div( class: "list_student_last_class_attended_required_ind", "*" )
	
	                /* Get the list of Classes from the Last Enrollment School. */
	                List<Entity> classList = Class.findBySchoolName( studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText() )
	                    
	                select( autocomplete: "off", class: "list_student_first_class_attended", name: "enrollmentFirstClassAttended" ) {
	                    option( value: "First Class Attended" )
	
	                    classList.each(
	                        {
	                            option( value: it.getProperty( "class" ), it.getProperty( "class" ) )
	                        }
	                    )
	                }
	                    
	                select( autocomplete: "off", class: "list_student_last_class_attended", name: "enrollmentLastClassAttended" ) {
	                    option( value: "Last Class Attended" )
	
	                    classList.each(
	                        {
	                            option( value: it.getProperty( "class" ), it.getProperty( "class" ) )
	                        }
	                    )
	                }
					
					/* Get the list of Terms from the Last Enrollment School. */
	                List<Entity> termList = Term.findByTermSchool( studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText() )
	
	                select( autocomplete: "off", class: "list_student_leave_term", name: "leaveTerm" ) {
	                    option( value: "Leave Term", "now" )
	
	                    termList.each(
	                        {
	                            option( value: "${ it.year } Term ${ it.termNo }", "${ it.year } Term ${ it.termNo }" )
	                        }
	                    )
	                }
					
					button( class: "list_student_enrollment_period_lookup" )
					img( class: "list_student_enrollment_period_lookup_wait", src: "/images/ajax-loader.gif" )
	                
	                div( class: "list_student_classes_attended_hyphen", "-" )
	                div( class: "list_student_enrollment_period" ) {
						Integer lastEnrollmentTermYear = studentDocument.getOnlyField( "lastEnrollmentTermYear" ).getNumber()
						Integer lastEnrollmentTermNo = studentDocument.getOnlyField( "lastEnrollmentTermNo" ).getNumber()
						Integer lastEnrollmentLeaveTermYear
						Integer lastEnrollmentLeaveTermNo
						
						if( studentDocumentFieldNames.contains( "lastEnrollmentLeaveTermYear" ) ) {
							lastEnrollmentLeaveTermYear = studentDocument.getOnlyField( "lastEnrollmentLeaveTermYear" ).getNumber()
							lastEnrollmentLeaveTermNo = studentDocument.getOnlyField( "lastEnrollmentLeaveTermNo" ).getNumber()
						}
						
	                    span( class: "list_student_enrollment_term", "${ lastEnrollmentTermYear } Term ${ lastEnrollmentTermNo }" )
	                    mkp.yield( " - " )
	                    span( class: "list_student_leave_term", lastEnrollmentLeaveTermYear? "${ lastEnrollmentLeaveTermYear } Term ${ lastEnrollmentLeaveTermNo }": "now" )
	                }
					
					div( class: "list_student_enrollment_terms", style: "display: none", studentDocument.getOnlyField( "termsEnrolled" ).getText() )
	            }
	        }
	        
	        new MarkupBuilder( writer ).div( class: "list_student_details" ) {
	            form( action: "javascript: void( 0 )", name: "student_details_form" ) {
	                
	                /* Birth Date Text Field */
	                a( class: "list_student_birth_date_label", href: "javascript:void( 0 )", "Birth Date:" )
	                div( class: "list_student_birth_date", "${ studentDocumentFieldNames.contains( "birthDate" )? studentDocument.getOnlyField( "birthDate" ).getDate().format( "MMM d yyyy" ): "" }" )
	                input( autocomplete: "off", class: "list_student_birth_date ui-corner-all", name: "birthDate", type: "text" )
	                
	                /* Village Text Field */
	                a( class: "list_student_village_label", href: "javascript:void( 0 )", "Village:" )
	                div( class: "list_student_village", studentDocumentFieldNames.contains( "village" )? studentDocument.getOnlyField( "village" ).getText(): "" )
	                input( autocomplete: "off", class: "list_student_village ui-corner-all", name: "village", type: "text" )
	                
	                /* ID Text Field */
	                a( class: "list_student_id_label", href: "javascript:void( 0 )", "ID" )
	                div( class: "list_student_id", ": ${ studentDocument.getOnlyField( "studentId" ).getAtom() }" )
	                
	                /* Parent/Guardians Table */
	                div( class: "list_student_parents_label", "Parents/Guardians" )
	                button( class: "list_student_parents_lookup" )
					img( class: "list_student_parents_lookup_wait", src: "/images/ajax-loader.gif" )
	                div( class: "list_student_parents_relationship_label", "Relationship" )
	                div( class: "list_student_parents_first_name_label", "First Name" )
	                div( class: "list_student_parents_last_name_label", "Last Name" )
	                div( class: "list_student_parents_deceased_label", "Deceased" )
	                div( class: "list_student_parents_profession_label", "Profession" )
	                
	                div( class: "list_student_parents_container" ) {
						table( class: "list_student_parents" ) {
		                    AnonymousParentalRelationship.findByStudent( student?: Student.findByStudentId( studentDocument.getOnlyField( "studentId" ).getAtom() ) ).each(
		                        { anonymousParent ->
		                            List<Entity> parentalRelationships = Relationship.findParentalRelationshipsByStudentIdAndParentalRelationship( studentDocument.getOnlyField( "studentId" ).getAtom(), anonymousParent.parentalRelationship )
									
									if( parentalRelationships.size() == 0 ) {
										tr {
			                                /* Parental Relationship Text Field */
			                                td( class: "list_student_parent_relationship", anonymousParent.parentalRelationship )
											
											/* Parent First Name Text Field */
											td( class: "list_student_parent_first_name" )
											
											/* Parental Relationship Text Field */
											td( class: "list_student_parent_last_name" )
			                                
			                                /* Parent Deceased Checkbox */
			                                td( class: "list_student_parent_deceased" ) {
			                                    span( class: "list_student_parent_deceased_ind", anonymousParent.deceasedInd )
												input( class: "list_student_parent_deceased_toggle", name: "anonymous${ anonymousParent.parentalRelationship }DeceasedInd", type: "checkbox" )
			                                }
											
											/* Parent Profession Text Field */
											td( class: "list_student_parent_profession" )
			                            }
									}
									else {
										parentalRelationships.each(
											{ parentalRelationship ->
												Entity parent = Parent.findByParentId( parentalRelationship.parentId )
												
												tr {
					                                /* Parental Relationship Text Field */
					                                td( class: "list_student_parent_relationship", parentalRelationship.parentalRelationship )
													
													/* Parent First Name Text Field */
													td( class: "list_student_parent_first_name", parent.firstName )
													
													/* Parental Relationship Text Field */
													td( class: "list_student_parent_last_name", parent.lastName?: "" )
					                                
					                                /* Parent Deceased Checkbox */
					                                td( class: "list_student_parent_deceased" ) {
					                                    span( class: "list_student_parent_deceased_ind", parent.deceasedInd )
														input( class: "list_student_anonymous_parent_deceased_ind", name: "anonymous${ anonymousParent.parentalRelationship }DeceasedInd", type: "hidden", value: parent.deceasedInd )
														input( class: "list_student_parent_deceased_toggle", name: "${ parent.parentId }DeceasedInd", type: "checkbox" )
					                                }
													
													/* Parent Profession Text Field */
													td( class: "list_student_parent_profession", parent.profession?: "" )
					                            }
											}
										)
									}
		                        }
		                    )
							
							Relationship.findGuardianRelationshipsByStudentId( studentDocument.getOnlyField( "studentId" ).getAtom() ).each(
								{ guardianRelationship ->
									Entity guardian = Parent.findByParentId( guardianRelationship.parentId )
									
									tr {
										/* Parental Relationship Text Field */
										td( class: "list_student_parent_relationship", guardianRelationship.guardianRelationship )
										
										/* Parent First Name Text Field */
										td( class: "list_student_parent_first_name", guardian.firstName )
										
										/* Parental Relationship Text Field */
										td( class: "list_student_parent_last_name", guardian.lastName?: "" )
										
										/* Parent Deceased Checkbox */
										td( class: "list_student_parent_deceased" ) {
											span( class: "list_student_parent_deceased_ind", guardian.deceasedInd )
											input( class: "list_student_parent_deceased_toggle", name: "${ guardian.parentId }DeceasedInd", type: "checkbox" )
										}
										
										/* Parent Profession Text Field */
										td( class: "list_student_parent_profession", guardian.profession?: "" )
									}
								}
							)
		                }
	                }
	                
	                /* Gender Code List Box */
	                a( class: "list_student_gender_label", href: "javascript:void( 0 )", "Gender:" )
	                div( class: "list_student_gender", studentDocumentFieldNames.contains( "genderCode" )? studentDocument.getOnlyField( "genderCode" ).getText(): "" )
	                
	                select( autocomplete: "off", class: "list_student_gender", name: "genderCode" ) {
	                    option( value: "Gender" )
	                    
	                    Gender.list().each(
	                        {
	                            option( value: it.code, it.code )
	                        }
	                    )
	                }
	                
	                /* Special Info/Condition Text Area */
	                a( class: "list_student_special_info_label", href: "javascript:void( 0 )", "Special Info/Condition:" )
	                div( class: "list_student_special_info" ) {
						mkp.yieldUnescaped( StringEscapeUtils.escapeHtml4( studentDocumentFieldNames.contains( "specialInfo" )? studentDocument.getOnlyField( "specialInfo" ).getText(): "" ).replaceAll( "\n", "<br>" ) )
	                }
	                textarea( autocomplete: "off", class: "list_student_special_info ui-corner-all", name: "specialInfo" )
	                
	                div( class: "list_student_enrollment_label", "Last Enrollment" )
	                
	                /* Last Enrollment School List Box */
	                a( class: "list_student_school_label", href: "javascript:void( 0 )", "School:" )
	                div( class: "list_student_school", studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText() )
	                
	                /* Last Enrollment Leave Reason Category List Box */
	                a( class: "list_student_leave_reason_label", href: "javascript:void( 0 )", "Leave Reason:" )
	                div( class: "list_student_leave_reason_category", studentDocumentFieldNames.contains( "lastEnrollmentLeaveReasonCategory" )? studentDocument.getOnlyField( "lastEnrollmentLeaveReasonCategory" ).getText(): "" )
	                
	                select( autocomplete: "off", class: "list_student_leave_reason_category", name: "enrollmentLeaveReasonCategory" ) {
	                    option( value: "Leave Reason Category" )
	                    
	                    LeaveReasonCategory.list().each(
	                        {
	                            option( value: it.category, it.category )
	                        }
	                    )
	                }
	                
	                /* Last Enrollment Leave Reason Text Area */
	                div( class: "list_student_leave_reason" ) {
						mkp.yieldUnescaped( StringEscapeUtils.escapeHtml4( studentDocumentFieldNames.contains( "lastEnrollmentLeaveReason" )? studentDocument.getOnlyField( "lastEnrollmentLeaveReason" ).getText(): "" ).replaceAll( "\n", "<br>" ) )
	                }
	                textarea( autocomplete: "off", class: "list_student_leave_reason ui-corner-all", name: "enrollmentLeaveReason" )
	                
	                /* Last Enrollment Tuition Fees */
	                a( class: "list_student_tuition_fees_label", href: "javascript:void( 0 )", "Tuition Fees" )
	                div( class: "list_student_tuition_fees", currencyFormat.format( studentDocument.getOnlyField( "tuitionFees" ).getNumber() ) )
	                
	                /* Last Enrollment Boarding Fees */
	                a( class: "list_student_boarding_fees_label", href: "javascript:void( 0 )", "Boarding Fees" )
	                div( class: "list_student_boarding_fees", currencyFormat.format( studentDocument.getOnlyField( "boardingFees" ).getNumber() ) )
					
					/* Last Enrollment Other Fees */
					a( class: "list_student_other_fees_label", href: "javascript:void( 0 )", "Other Fees" )
					img( class: "list_student_other_fees_wait", src: "/images/ajax-loader.gif" )
					div( class: "list_student_other_fees", currencyFormat.format( studentDocument.getOnlyField( "otherFees" ).getNumber() ) )
					
					/* Last Enrollment Payments */
					a( class: "list_student_payments_label", href: "javascript:void( 0 )", "Payments" )
					img( class: "list_student_payments_wait", src: "/images/ajax-loader.gif" )
					div( class: "list_student_payments", "- ${ currencyFormat.format( studentDocument.getOnlyField( "payments" ).getNumber() ) }" )
					
					/* Last Enrollment Fees Due */
					a( class: "list_student_fees_due_label", href: "javascript:void( 0 )", "Fees Due" )
					div( class: "list_student_fees_due_label", "Fees Due" )
					div( class: "list_student_fees_due", currencyFormat.format( studentDocument.getOnlyField( "feesDue" ).getNumber() ) )
	                
	                /* Last Update */
	                div( class: "list_student_last_update_label", "Last Update:" )
	                div( class: "list_student_last_update_on_label", "on" )
	                div( class: "list_student_last_update_on", studentDocument.getOnlyField( "lastUpdateDate" ).getDate() )
	                input( class: "list_student_last_update_date", name: "lastUpdateDate", type: "hidden", value: studentDocument.getOnlyField( "lastUpdateDate" ).getDate().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
	                div( class: "list_student_last_update_by_label", "by" )
	                
					if( urfUser != null ) {
						div( class: "list_student_last_update_by", studentDocumentFieldNames.contains( "lastUpdateUser" )? studentDocument.getOnlyField( "lastUpdateUser" ).getText(): "" )
		                input( class: "list_student_last_update_user", name: "lastUpdateUser", type: "hidden", value: studentDocumentFieldNames.contains( "lastUpdateUser" )? studentDocument.getOnlyField( "lastUpdateUser" ).getText(): "" )
					}
					
					/* Will contain Y if the client determines that this Student record is stale due to changes in other records */
					div( class: "list_student_stale_ind", style: "display: none" )
	                
	                input( name: "id", type: "hidden", value: studentDocument.getId() )
	                input( name: "studentId", type: "hidden", value: studentDocument.getOnlyField( "studentId" ).getAtom() )
	                
	                if( UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), studentDocument.getOnlyField( "lastEnrollmentSchool" ).getText() )?.privilege?.equals( "Modify" ) ) {
	                    button( class: "list_student_edit", type: "button" )
	                    button( class: "list_student_delete", type: "button" )
	                    
	                    button( class: "list_student_save", type: "button" )
	                    button( class: "list_student_cancel", type: "button" )
	                }
					
					a( class: "list_student_select", href: "javascript:void( 0 )", "select" )
	                
	                img( class: "list_student_save_wait", src: "/images/ajax-loader.gif" )
	            }
	        }
	        
	        return writer.toString()
		}
		catch( Exception e ) {
			return e.getMessage()
		}
    }
    
    static String getTermListItem( Entity term ) {
        User user = UserServiceFactory.getUserService().getCurrentUser()
        
        DecimalFormat currencyFormat = new DecimalFormat( "#,##0.##" )
        
        StringWriter writer = new StringWriter()
        
        new MarkupBuilder( writer ).h3 {
            form( action: "javascript:void( 0 )", name: "term_header_form" ) {
                mkp.yieldUnescaped( "&nbsp;" )
                
                /* School */
                div( class: "list_term_school", term.termSchool )
                
                /* Term No */
                div( class: "list_term_no", term.termNo )
                
                /* Year */
                div( class: "list_term_year", term.year )
                
                /* Start Date Text Field */
                div( class: "list_term_start_date", term.startDate.format( "MMM d yyyy" ) )
                input( autocomplete: "off", class: "list_term_start_date ui-corner-all", name: "startDate", type: "text" )
                
                /* End Date Text Field */
                div( class: "list_term_end_date", term.endDate.format( "MMM d yyyy" ) )
                input( autocomplete: "off", class: "list_term_end_date ui-corner-all", name: "endDate", type: "text" )
            }
        }
        
        new MarkupBuilder( writer ).div( class: "list_term_details" ) {
            form( action: "javascript:void( 0 )", name: "term_details_form" ) {
                
				table( class: "list_term_class_fees_header" ) {
					tr {
						td( "Class" )
						td( "Tuition Fee" )
						td( "Boarding Fee" )
					}
				}
				
				div( class: "list_term_class_fees" ) {
					table {
						ClassFees.findByTerm( term ).each(
							{ classFees ->
								tr {
									td( classFees.getProperty( "class" ) )
									td {
										/* Tuition Fee Text Field */
										String tuitionFee = classFees.tuitionFee == null? "": currencyFormat.format( classFees.tuitionFee )
										span( class: "list_term_tuition_fee_currency", "UGX" )
										span( class: "list_term_tuition_fee", tuitionFee )
										input( autocomplete: "off", class: "list_term_tuition_fee ui-corner-all", name: "tuitionFee${ classFees.schoolName }${ classFees.getProperty( "class" ) }", type: "text", value: tuitionFee )
									}
								
									td {
										/* Boarding Fee Text Field */
										String boardingFee = classFees.boardingFee == null? "": currencyFormat.format( classFees.boardingFee )
										span( class: "list_term_boarding_fee_currency", "UGX" )
										span( class: "list_term_boarding_fee", boardingFee )
										input( autocomplete: "off", class: "list_term_boarding_fee ui-corner-all", name: "boardingFee${ classFees.schoolName }${ classFees.getProperty( "class" ) }", type: "text", value: boardingFee )
									}
								}
							}
						)
					}
				}
				
				input( name: "id", type: "hidden", value: term.getKey().getId() )
                input( class: "list_term_last_update_date", name: "lastUpdateDate", type: "hidden", value: term.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
                input( class: "list_term_last_update_user", name: "lastUpdateUser", type: "hidden", value: term.lastUpdateUser?: "" )
                
                if( URFUser.findByEmail( user?.getEmail() )?.adminPrivilege?.equals( "Modify" ) && UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), term.termSchool )?.privilege?.equals( "Modify" ) ) {
                    button( class: "list_term_edit", type: "button" )
                    button( class: "list_term_delete", type: "button" )
                    
                    button( class: "list_term_save", type: "button" )
                    button( class: "list_term_cancel", type: "button" )
                    
                    img( class: "list_term_save_wait", src: "/images/ajax-loader.gif" )
                }
            }
        }
        
        return writer.toString()
    }
    
    static String getURFUserListItem( Entity urfUser ) {
        User user = UserServiceFactory.getUserService().getCurrentUser()
        
        StringWriter writer = new StringWriter()
        
        new MarkupBuilder( writer ).h3 {
            form( action: "javascript:void( 0 )", name: "urfuser_header_form" ) {
                mkp.yieldUnescaped( "&nbsp;" )
                
                /* Email */
                div( class: "list_urfuser_email", urfUser.email )
                
                /* Sponsor Data Privilege List Box */
                div( class: "list_urfuser_sponsor_data_privilege", urfUser.sponsorDataPrivilege?: "None" )
                select( autocomplete: "off", class: "list_urfuser_sponsor_data_privilege", name: "sponsorDataPrivilege" ) {
                    option( value: "None", "None" )
                    option( value: "Read", "Read" )
                    option( value: "Modify", "Modify" )
                }
                
                /* Admin Privilege List Box */
                div( class: "list_urfuser_admin_privilege", urfUser.adminPrivilege?: "None" )
                select( autocomplete: "off", class: "list_urfuser_admin_privilege", name: "adminPrivilege" ) {
                    option( value: "None", "None" )
                    option( value: "Read", "Read" )
                    option( value: "Modify", "Modify" )
                }
            }
        }
        
        new MarkupBuilder( writer ).div( class: "list_urfuser_details" ) {
            form( action: "javascript:void( 0 )", name: "urfuser_details_form" ) {
            
                /* School Data Privilege Table */
                div( class: "list_urfuser_school_data_privilege" ) {
                    fieldset( class: "ui-corner-all" ) {
                        legend( "School Data Privilege" )
                        table( class: "list_urfuser_school_data_privilege_table" ) {
                            School.list().each(
                                { school ->
                                    Entity userSchoolPrivilege = UserPrivilege.findByURFUserAndSchoolName( urfUser, school.name )

                                    tr {
                                        /* School Name */
                                        td( school.name )

                                        /* School Data Privilege Radio Buttons */
                                        td {
                                            label {
                                                input( autocomplete: "off", name: "${ school.getKey().getId() }Privilege", type: "radio", checked: "checked", value: "None" )
                                                mkp.yield( "None" )
                                            }

                                            if( userSchoolPrivilege == null )
                                                span( "None" )
                                        }

                                        td {
                                            label {
                                                input( autocomplete: "off", name: "${ school.getKey().getId() }Privilege", type: "radio", value: "Read" )
                                                mkp.yield( "Read" )
                                            }

                                            if( userSchoolPrivilege?.privilege == "Read" )
                                                span( "Read" )
                                        }

                                        td {
                                            label {
                                                input( autocomplete: "off", name: "${ school.getKey().getId() }Privilege", type: "radio", value: "Modify" )
                                                mkp.yield( "Modify" )
                                            }

                                            if( userSchoolPrivilege?.privilege == "Modify" )
                                                span( "Modify" )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
                
                input( name: "id", type: "hidden", value: urfUser.getKey().getId() )
                input( class: "list_urfuser_last_update_date", name: "lastUpdateDate", type: "hidden", value: urfUser.lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) )
                input( class: "list_urfuser_last_update_user", name: "lastUpdateUser", type: "hidden", value: urfUser.lastUpdateUser?: "" )
                
                if( URFUser.findByEmail( user?.getEmail() )?.adminPrivilege?.equals( "Modify" ) ) {
                    button( class: "list_urfuser_edit", type: "button" )
                    button( class: "list_urfuser_delete", type: "button" )
                    
                    button( class: "list_urfuser_save", type: "button" )
                    button( class: "list_urfuser_cancel", type: "button" )
                    
                    img( class: "list_urfuser_save_wait", src: "/images/ajax-loader.gif" )
                }
            }
        }
        
        return writer.toString()
    }
}