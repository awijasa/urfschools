/*
Document   : Groovlet file: SessionController.groovy
Created on : Sun Jun 22 23:20:00 CST 2014
Author     : awijasa
 */

import groovy.xml.MarkupBuilder
import java.text.DecimalFormat
 
/* Get/Set Session Attributes */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the SessionController through urfschools.appspot.com. */
if( user == null && !localMode )
    response.sendError( response.SC_UNAUTHORIZED )
else {
	if( params.action == "getAttributes" ) {
		StringWriter writer = new StringWriter()
		
		new MarkupBuilder( writer ).attributes {
			enrollmentBirthDateFilter( session.getAttribute( "enrollmentBirthDateFilter" ) )
			enrollmentBirthDateFilterOperator( session.getAttribute( "enrollmentBirthDateFilterOperator" ) )
			enrollmentClassesAttendedFilter( session.getAttribute( "enrollmentClassesAttendedFilter" ) )
			enrollmentFeesDueFilter( session.getAttribute( "enrollmentFeesDueFilter" ) )
			enrollmentFeesDueFilterOperator( session.getAttribute( "enrollmentFeesDueFilterOperator" ) )
			enrollmentFirstNameFilter( session.getAttribute( "enrollmentFirstNameFilter" ) )
			enrollmentLastNameFilter( session.getAttribute( "enrollmentLastNameFilter" ) )
			enrollmentPeriodFilter( session.getAttribute( "enrollmentPeriodFilter" ) )
			enrollmentVillageFilter( session.getAttribute( "enrollmentVillageFilter" ) )
			studentBirthDateFilter( session.getAttribute( "studentBirthDateFilter" ) )
			studentBirthDateFilterOperator( session.getAttribute( "studentBirthDateFilterOperator" ) )
			studentClassesAttendedFilter( session.getAttribute( "studentClassesAttendedFilter" ) )
			studentEnrollmentPeriodFilter( session.getAttribute( "studentEnrollmentPeriodFilter" ) )
			studentFeesDueFilter( session.getAttribute( "studentFeesDueFilter" ) )
			studentFeesDueFilterOperator( session.getAttribute( "studentFeesDueFilterOperator" ) )
			studentFirstNameFilter( session.getAttribute( "studentFirstNameFilter" ) )
			studentLastNameFilter( session.getAttribute( "studentLastNameFilter" ) )
			studentVillageFilter( session.getAttribute( "studentVillageFilter" ) )
		}
		
		println writer.toString()
	}
	else if( params.action == "setAttributes" ) {
		DecimalFormat currencyFormat = new DecimalFormat( "#,##0.00" )
		
		if( params.kind == "Enrollment" ) {
			if( params.birthDate != null && params.birthDate != "" ) {
				try {
					session.setAttribute( "enrollmentBirthDateFilter", params.birthDate )
				}
				catch( Exception e ) {
					session.removeAttribute( "enrollmentBirthDateFilter" )
				}
			}
			else
				session.removeAttribute( "enrollmentBirthDateFilter" )
				
			if( params.birthDateOperator != null && params.birthDateOperator != "" )
				session.setAttribute( "enrollmentBirthDateFilterOperator", params.birthDateOperator )
			else
				session.removeAttribute( "enrollmentBirthDateFilterOperator" )
				
			if( params.classesAttended != null && params.classesAttended != "" ) {
				try {
					session.setAttribute( "enrollmentClassesAttendedFilter", params.classesAttended )
				}
				catch( Exception e ) {
					session.removeAttribute( "enrollmentClassesAttendedFilter" )
				}
			}
			else
				session.removeAttribute( "enrollmentClassesAttendedFilter" )
				
			if( params.enrollmentPeriod != null && params.enrollmentPeriod != "" ) {
				try {
					session.setAttribute( "enrollmentPeriodFilter", params.enrollmentPeriod )
				}
				catch( Exception e ) {
					session.removeAttribute( "enrollmentPeriodFilter" )
				}
			}
			else
				session.removeAttribute( "enrollmentPeriodFilter" )
				
			if( params.feesDue != null && params.feesDue != "" ) {
				try {
					session.setAttribute( "enrollmentFeesDueFilter", currencyFormat.parse( params.feesDue ) )
				}
				catch( Exception e ) {
					session.removeAttribute( "enrollmentFeesDueFilter" )
				}
			}
			else
				session.removeAttribute( "enrollmentFeesDueFilter" )
			
			if( params.feesDueOperator != null && params.feesDueOperator != "" )
				session.setAttribute( "enrollmentFeesDueFilterOperator", params.feesDueOperator )
			else
				session.removeAttribute( "enrollmentFeesDueFilterOperator" )
				
			if( params.firstName != null && params.firstName != "" ) {
				try {
					session.setAttribute( "enrollmentFirstNameFilter", params.firstName )
				}
				catch( Exception e ) {
					session.removeAttribute( "enrollmentFirstNameFilter" )
				}
			}
			else
				session.removeAttribute( "enrollmentFirstNameFilter" )
				
			if( params.lastName != null && params.lastName != "" ) {
				try {
					session.setAttribute( "enrollmentLastNameFilter", params.lastName )
				}
				catch( Exception e ) {
					session.removeAttribute( "enrollmentLastNameFilter" )
				}
			}
			else
				session.removeAttribute( "enrollmentLastNameFilter" )
				
			if( params.village != null && params.village != "" ) {
				try {
					session.setAttribute( "enrollmentVillageFilter", params.village )
				}
				catch( Exception e ) {
					session.removeAttribute( "enrollmentVillageFilter" )
				}
			}
			else
				session.removeAttribute( "enrollmentVillageFilter" )
		}
		else {
			if( params.birthDate != null && params.birthDate != "" ) {
				try {
					session.setAttribute( "studentBirthDateFilter", params.birthDate )
				}
				catch( Exception e ) {
					session.removeAttribute( "studentBirthDateFilter" )
				}
			}
			else
				session.removeAttribute( "studentBirthDateFilter" )
				
			if( params.birthDateOperator != null && params.birthDateOperator != "" )
				session.setAttribute( "studentBirthDateFilterOperator", params.birthDateOperator )
			else
				session.removeAttribute( "studentBirthDateFilterOperator" )
				
			if( params.classesAttended != null && params.classesAttended != "" ) {
				try {
					session.setAttribute( "studentClassesAttendedFilter", params.classesAttended )
				}
				catch( Exception e ) {
					session.removeAttribute( "studentClassesAttendedFilter" )
				}
			}
			else
				session.removeAttribute( "studentClassesAttendedFilter" )
				
			if( params.enrollmentPeriod != null && params.enrollmentPeriod != "" ) {
				try {
					session.setAttribute( "studentEnrollmentPeriodFilter", params.enrollmentPeriod )
				}
				catch( Exception e ) {
					session.removeAttribute( "studentEnrollmentPeriodFilter" )
				}
			}
			else
				session.removeAttribute( "studentEnrollmentPeriodFilter" )
			
			if( params.feesDue != null && params.feesDue != "" ) {
				try {
					session.setAttribute( "studentFeesDueFilter", currencyFormat.parse( params.feesDue ) )
				}
				catch( Exception e ) {
					session.removeAttribute( "studentFeesDueFilter" )
				}
			}
			else
				session.removeAttribute( "studentFeesDueFilter" )
			
			if( params.feesDueOperator != null && params.feesDueOperator != "" )
				session.setAttribute( "studentFeesDueFilterOperator", params.feesDueOperator )
			else
				session.removeAttribute( "studentFeesDueFilterOperator" )
				
			if( params.firstName != null && params.firstName != "" ) {
				try {
					session.setAttribute( "studentFirstNameFilter", params.firstName )
				}
				catch( Exception e ) {
					session.removeAttribute( "studentFirstNameFilter" )
				}
			}
			else
				session.removeAttribute( "studentFirstNameFilter" )
				
			if( params.lastName != null && params.lastName != "" ) {
				try {
					session.setAttribute( "studentLastNameFilter", params.lastName )
				}
				catch( Exception e ) {
					session.removeAttribute( "studentLastNameFilter" )
				}
			}
			else
				session.removeAttribute( "studentLastNameFilter" )
				
			if( params.village != null && params.village != "" ) {
				try {
					session.setAttribute( "studentVillageFilter", params.village )
				}
				catch( Exception e ) {
					session.removeAttribute( "studentVillageFilter" )
				}
			}
			else
				session.removeAttribute( "studentVillageFilter" )
		}
	}
}