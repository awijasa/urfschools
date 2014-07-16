<%
  import com.google.appengine.api.datastore.Entity
  import com.google.appengine.api.search.Document
  import data.EnrollmentDocument
  import data.Fee
  import data.StudentDocument
  import groovyx.net.http.URIBuilder
  
  if( user == null || !localMode ) redirect "/index.gtpl"
%>
<html>
  <head>
    <title>Fee Edit Test</title>
  </head>
  <body>
    <%
      Entity fee
      
      fee = Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      Date lastUpdateDate = new Date( fee.lastUpdateDate.getTime() )
      
      /* Fee Edit Test with a missing action parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "id", fee.getKey().getId() )
        .addQueryParam( "amount", "76,543.21" )
        .addQueryParam( "comment", "Won Project Runway" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      fee = Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert fee.amount != 76543.21, "User was able to edit a Fee using a missing action parameter."
      assert fee.comment != "Won Project Runway", "User was able to edit a Fee using a missing action parameter."
      
	  Document enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() != 76543.21, "User was able to update a EnrollmentDocument's otherFees using a missing action parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 296543.21, "User was able to update a EnrollmentDocument's feesDue using a missing action parameter"
      
      Document studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() != 76543.21, "User was able to update a StudentDocument's otherFees using a missing action parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 296543.21, "User was able to update a StudentDocument's feesDue using a missing action parameter"
      
      /* Fee Edit Test with an invalid action parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "modify" )
        .addQueryParam( "id", fee.getKey().getId() )
        .addQueryParam( "amount", "76,543.21" )
        .addQueryParam( "comment", "Won Project Runway" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      fee = Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert fee.amount != 76543.21, "User was able to edit a Fee using an invalid action parameter."
      assert fee.comment != "Won Project Runway", "User was able to edit a Fee using an invalid action parameter."
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() != 76543.21, "User was able to update a EnrollmentDocument's otherFees using an invalid action parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 296543.21, "User was able to update a EnrollmentDocument's feesDue using an invalid action parameter"
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() != 76543.21, "User was able to update a StudentDocument's otherFees using an invalid action parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 296543.21, "User was able to update a StudentDocument's feesDue using an invalid action parameter"
            
      /* Feet Edit Test with a missing id parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "studentId", "0202aoey" )
        .addQueryParam( "name", "Admission" )
        .addQueryParam( "schoolName", "Saint John's College MN" )
        .addQueryParam( "enrollTermNo", "1" )
        .addQueryParam( "enrollTermYear", "1902" )
        .addQueryParam( "classTermNo", "1" )
        .addQueryParam( "classTermYear", "1902" )
        .addQueryParam( "amount", "76,543.21" )
        .addQueryParam( "comment", "Won Project Runway" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      fee = Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert fee.amount != 76543.21, "User was able to edit a Fee using a missing id parameter."
      assert fee.comment != "Won Project Runway", "User was able to edit a Fee using a missing id parameter."
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() != 76543.21, "User was able to update a EnrollmentDocument's otherFees using a missing id parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 296543.21, "User was able to update a EnrollmentDocument's feesDue using a missing id parameter"

      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() != 76543.21, "User was able to update a StudentDocument's otherFees using a missing id parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 296543.21, "User was able to update a StudentDocument's feesDue using a missing id parameter"
      
      /* Fee Edit Test with a missing amount parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "id", fee.getKey().getId() )
        .addQueryParam( "comment", "Won Project Runway" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      fee = Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert fee.amount != 76543.21, "User was able to edit a Fee using a missing amount parameter."
      assert fee.comment != "Won Project Runway", "User was able to edit a Fee using a missing amount parameter."
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() != 76543.21, "User was able to update a EnrollmentDocument's otherFees using a missing amount parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 296543.21, "User was able to update a EnrollmentDocument's feesDue using a missing amount parameter"
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() != 76543.21, "User was able to update a StudentDocument's otherFees using a missing amount parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 296543.21, "User was able to update a StudentDocument's feesDue using a missing amount parameter"
      
      /* Fee Edit Test with a missing lastUpdateDate parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "id", fee.getKey().getId() )
        .addQueryParam( "comment", "Won Project Runway" ).toURL() )
      
      fee = Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert fee.amount != 76543.21, "User was able to edit a Fee using a missing lastUpdateDate parameter."
      assert fee.comment != "Won Project Runway", "User was able to edit a Fee using a missing lastUpdateDate parameter."
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() != 76543.21, "User was able to update a EnrollmentDocument's otherFees using a missing lastUpdateDate parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 296543.21, "User was able to update a EnrollmentDocument's feesDue using a missing lastUpdateDate parameter"

      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() != 76543.21, "User was able to update a StudentDocument's otherFees using a missing lastUpdateDate parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 296543.21, "User was able to update a StudentDocument's feesDue using a missing lastUpdateDate parameter"
            
      /* Fee Edit Test with an invalid lastUpdateDate parameter. */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "id", fee.getKey().getId() )
        .addQueryParam( "comment", "Won Project Runway" )
        .addQueryParam( "lastUpdateDate", new Date().format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      fee = Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert fee.amount != 76543.21, "User was able to edit a Fee using an invalid lastUpdateDate parameter."
      assert fee.comment != "Won Project Runway", "User was able to edit a Feet using an invalid lastUpdateDate parameter."
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() != 76543.21, "User was able to update a EnrollmentDocument's otherFees using an invalid lastUpdateDate parameter"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() != 296543.21, "User was able to update a EnrollmentDocument's feesDue using an invalid lastUpdateDate parameter"

      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() != 76543.21, "User was able to update a StudentDocument's otherFees using an invalid lastUpdateDate parameter"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() != 296543.21, "User was able to update a StudentDocument's feesDue using an invalid lastUpdateDate parameter"
            
      /* Fee Edit Test */
      urlFetch.fetch( new URIBuilder( "http://localhost:8080/FeeController.groovy" )
        .addQueryParam( "action", "edit" )
        .addQueryParam( "id", fee.getKey().getId() )
        .addQueryParam( "amount", "76,543.21" )
        .addQueryParam( "comment", "Won Project Runway" )
        .addQueryParam( "lastUpdateDate", lastUpdateDate.format( "MMM d yyyy HH:mm:ss.SSS zzz" ) ).toURL() )
      
      fee = Fee.findByNameAndStudentIdAndSchoolNameAndClassTermNoAndClassTermYear( "Admission", "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert fee.amount == 76543.21, "User was unable to edit a Fee"
      assert fee.comment == "Won Project Runway", "User was unable to edit a Fee"
      
	  enrollmentDocument = EnrollmentDocument.findByStudentIdAndSchoolNameAndEnrollTermNoAndEnrollTermYear( "0202aoey", "Saint John's College MN", 1, 1902 )
      
      assert enrollmentDocument.getOnlyField( "otherFees" ).getNumber() == 76543.21, "User was unable to update a EnrollmentDocument's otherFees"
      assert enrollmentDocument.getOnlyField( "feesDue" ).getNumber() == 296543.21, "User was unable to update a EnrollmentDocument's feesDue"
      
      studentDocument = StudentDocument.findByStudentId( "0202aoey" )
      
      assert studentDocument.getOnlyField( "otherFees" ).getNumber() == 76543.21, "User was unable to update a StudentDocument's otherFees"
      assert studentDocument.getOnlyField( "feesDue" ).getNumber() == 296543.21, "User was unable to update a StudentDocument's feesDue"
    %>
    <p>Fee Edit Test is successful.</p>
  </body>
</html>