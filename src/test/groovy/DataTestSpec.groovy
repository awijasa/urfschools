import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.search.Document
import com.google.appengine.api.search.Index
import com.google.appengine.api.search.SearchServiceFactory
import data.Enrollment
import data.EnrollmentDocument
import data.Gender
import data.Term
import groovyx.gaelyk.spock.*

class DataTestSpec extends ConventionalGaelykUnitSpec {

    def "findByTermSchoolAndDate returns the correct Term"() {
        given: "three Hope Academy terms"
        def term = new Entity( "Term" )
        term.termSchool = "Hope Academy"
        term.termNo = 1
        term.year = 2011
        term.startDate = Date.parse( "MMM d yy", "Feb 1 2011" )
        term.endDate = Date.parse( "MMM d yy", "Apr 30 2011" )
        term.save()

        term = new Entity( "Term" )
        term.termSchool = "Hope Academy"
        term.termNo = 2
        term.year = 2011
        term.startDate = Date.parse( "MMM d yy", "May 1 2011" )
        term.endDate = Date.parse( "MMM d yy", "Jul 31 2011" )
        term.save()

        term = new Entity( "Term" )
        term.termSchool = "Hope Academy"
        term.termNo = 3
        term.year = 2011
        term.startDate = Date.parse( "MMM d yy", "Aug 1 2011" )
        term.endDate = Date.parse( "MMM d yy", "Oct 31 2011" )
        term.save()
        
        when: "parameters: termSchool='Hope Academy' and date='Apr 15 2011' are provided"
        term = Term.findByTermSchoolAndDate( "Hope Academy", Date.parse( "MMM d yy", "Apr 15 2011" ) )

        then: "the returned Term shall be 2011 Term 1"
        term.year == 2011
        term.termNo == 1
        
        when: "parameters: termSchool='Hope Academy' and date=null are provided"
        term = Term.findByTermSchoolAndDate( "Hope Academy", null )

        then: "the returned Term shall be null"
        term == null
    }
    
    def "findByTermSchoolAndTermRange returns the correct Terms"() {
        given: "three Hope Academy terms"
        def termTwo2012 = new Entity( "Term" )
        termTwo2012.termSchool = "Hope Academy"
        termTwo2012.termNo = 2
        termTwo2012.year = 2012
        termTwo2012.startDate = Date.parse( "MMM d yy", "May 1 2012" )
        termTwo2012.endDate = Date.parse( "MMM d yy", "Jul 31 2012" )
        termTwo2012.save()

        def termThree2012 = new Entity( "Term" )
        termThree2012.termSchool = "Hope Academy"
        termThree2012.termNo = 3
        termThree2012.year = 2012
        termThree2012.startDate = Date.parse( "MMM d yy", "Aug 1 2012" )
        termThree2012.endDate = Date.parse( "MMM d yy", "Oct 31 2012" )
        termThree2012.save()
        
        def termOne2013 = new Entity( "Term" )
        termOne2013.termSchool = "Hope Academy"
        termOne2013.termNo = 1
        termOne2013.year = 2013
        termOne2013.startDate = Date.parse( "MMM d yy", "Feb 1 2013" )
        termOne2013.endDate = Date.parse( "MMM d yy", "Apr 30 2013" )
        termOne2013.save()
        
        when: "parameters: termSchool='Hope Academy', startTerm=termTwo2012, endTerm=null, and currentDate='Aug 18 2012' are provided"
        def terms = Term.findByTermSchoolAndTermRange( "Hope Academy", termTwo2012, null, Date.parse( "MMM d yy", "Aug 18 2012" ) )
        def firstTerm = terms.first()
        def secondTerm = terms.last()

        then: "there shall be two returned Terms: 2012 Term 2 and 2012 Term 3"
        terms.size() == 2
        
        if( firstTerm.termNo == termTwo2012.termNo ) {
            firstTerm.termSchool == termTwo2012.termSchool
            firstTerm.year == termTwo2012.year
            firstTerm.startDate == termTwo2012.startDate
            firstTerm.endDate == termTwo2012.endDate
            
            secondTerm.termNo == termThree2012.termNo
            secondTerm.termSchool == termThree2012.termSchool
            secondTerm.year == termThree2012.year
            secondTerm.startDate == termThree2012.startDate
            secondTerm.endDate == termThree2012.endDate
        }
        else {
            firstTerm.termNo == termThree2012.termNo
            firstTerm.termSchool == termThree2012.termSchool
            firstTerm.year == termThree2012.year
            firstTerm.startDate == termThree2012.startDate
            firstTerm.endDate == termThree2012.endDate
            
            secondTerm.termNo == termTwo2012.termNo
            secondTerm.termSchool == termTwo2012.termSchool
            secondTerm.year == termTwo2012.year
            secondTerm.startDate == termTwo2012.startDate
            secondTerm.endDate == termTwo2012.endDate
        }
        
        when: "parameters: termSchool='Hope Academy', startTerm=termTwo2012, endTerm=termTwo2012, and currentDate='Aug 18 2012' are provided"
        terms = Term.findByTermSchoolAndTermRange( "Hope Academy", termTwo2012, termTwo2012, Date.parse( "MMM d yy", "Aug 18 2012" ) )
        def term = terms.first()

        then: "there shall be one returned Term: 2012 Term 2"
        terms.size() == 1
        
        term.termNo == termTwo2012.termNo
        term.termSchool == termTwo2012.termSchool
        term.year == termTwo2012.year
        term.startDate == termTwo2012.startDate
        term.endDate == termTwo2012.endDate
            
        when: "parameters: termSchool='Hope Academy', startTerm=termThree2012, endTerm=termOne2013, and currentDate='Aug 18 2012' are provided"
        terms = Term.findByTermSchoolAndTermRange( "Hope Academy", termThree2012, termOne2013, Date.parse( "MMM d yy", "Aug 18 2012" ) )
        term = terms.first()
                
        then: "there shall be one returned Term: 2012 Term 3"
        terms.size() == 1
        
        term.termNo == termThree2012.termNo
        term.termSchool == termThree2012.termSchool
        term.year == termThree2012.year
        term.startDate == termThree2012.startDate
        term.endDate == termThree2012.endDate
    }
    
    def "findLastEnrollmentDocumentByStudentId returns the correct EnrollmentDocument Test 1"() {
        given: "Three EnrollmentDocuments"
        Entity appEntity = new Entity( "App" )
    	appEntity.name = "urfschools"
    	appEntity.save()
        
        Entity enrollment1 = new Entity( "Enrollment" )
        enrollment1.studentId = "0811awijaya"
        enrollment1.schoolName = "Hope Academy"
        enrollment1.enrollTermYear = 2011
        enrollment1.enrollTermNo = 3
        enrollment1.leaveTermYear = 2011
        enrollment1.leaveTermNo = 3
        enrollment1.save()
        
        Entity enrollment2 = new Entity( "Enrollment" )
        enrollment2.studentId = "0811awijaya"
        enrollment2.schoolName = "Test"
        enrollment2.enrollTermYear = 2012
        enrollment2.enrollTermNo = 2
        enrollment2.leaveTermYear = null
        enrollment2.leaveTermNo = null
        enrollment2.save()
        
        Entity enrollment3 = new Entity( "Enrollment" )
        enrollment3.studentId = "0811awijaya"
        enrollment3.schoolName = "Hope Academy"
        enrollment3.enrollTermYear = 2013
        enrollment3.enrollTermNo = 1
        enrollment3.leaveTermYear = null
        enrollment3.leaveTermNo = null
        enrollment3.save()
        
        Entity enrollmentMetaDataForTest = new Entity( "EnrollmentMetaData", appEntity.getKey() )
    	enrollmentMetaDataForTest.schoolName = "Test"
    	enrollmentMetaDataForTest.count = 4
    	enrollmentMetaDataForTest.save()
        
        Index enrollmentIndex = SearchServiceFactory.getSearchService().index( "Enrollment" )
        
		enrollmentIndex.put {
			document( id: "" + enrollment1.getKey().getId() ) {
				studentId atom: enrollment1.studentId
				enrollTermYear number: enrollment1.enrollTermYear
				enrollTermNo number: enrollment1.enrollTermNo
				enrollTermStartDate date: Date.parse( "MMM d yy", "Aug 1 2011" )
				leaveTermYear number: enrollment1.leaveTermYear
				leaveTermNo number: enrollment1.leaveTermNo
				leaveTermEndDate date: Date.parse( "MMM d yyyy", "Oct 31 2011" )
				schoolName text: enrollment1.schoolName
			}
		}
    
	    enrollmentIndex.put {
			document( id: "" + enrollment2.getKey().getId() ) {
				studentId atom: enrollment2.studentId
				enrollTermYear number: enrollment2.enrollTermYear
				enrollTermNo number: enrollment2.enrollTermNo
				enrollTermStartDate date: Date.parse( "MMM d yy", "May 1 2012" )
				leaveTermYear number: enrollment2.leaveTermYear
				leaveTermNo number: enrollment2.leaveTermNo
				leaveTermEndDate date: Date.parse( "MMM d yy", "Dec 31 2999" )
				schoolName text: enrollment2.schoolName
			}
		}
	    
	    enrollmentIndex.put {
			document( id: "" + enrollment3.getKey().getId() ) {
				studentId atom: enrollment3.studentId
				enrollTermYear number: enrollment3.enrollTermYear
				enrollTermNo number: enrollment3.enrollTermNo
				enrollTermStartDate date: Date.parse( "MMM d yy", "Feb 1 2013" )
				leaveTermYear number: enrollment3.leaveTermYear
				leaveTermNo number: enrollment3.leaveTermNo
				leaveTermEndDate date: Date.parse( "MMM d yy", "Dec 31 2999" )
				schoolName text: enrollment3.schoolName
			}
		}
	    
	    when: "studentId: 0811awijaya is supplied"
        Document lastEnrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0811awijaya" )
        Set<String> lastEnrollmentDocumentFieldNames = lastEnrollmentDocument.getFieldNames()
        
        then: "the result shall be enrollmentDocument3"
        lastEnrollmentDocument.getOnlyField( "studentId" ).getAtom() == enrollment3.studentId
        lastEnrollmentDocument.getOnlyField( "schoolName" ).getText() == enrollment3.schoolName
        lastEnrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() == enrollment3.enrollTermYear
        lastEnrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() == enrollment3.enrollTermNo
        lastEnrollmentDocument.getOnlyField( "enrollTermStartDate" ).getDate() == Date.parse( "MMM d yy", "Feb 1 2013" )
        !lastEnrollmentDocumentFieldNames.contains( "leaveTermYear" )
        !lastEnrollmentDocumentFieldNames.contains( "leaveTermNo" )
        lastEnrollmentDocument.getOnlyField( "leaveTermEndDate" ).getDate() == Date.parse( "MMM d yy", "Dec 31 2999" )
    }
    /*
    def "findLastEnrollmentDocumentByStudentId returns the correct EnrollmentDocument Test 2"() {
        given: "Three EnrollmentDocuments"
        Entity appEntity = new Entity( "App" )
    	appEntity.name = "urfschools"
    	appEntity.save()
        
        Entity enrollment1 = new Entity( "Enrollment" )
        enrollment1.studentId = "0811awijaya"
        enrollment1.schoolName = "Hope Academy"
        enrollment1.enrollTermYear = 2011
        enrollment1.enrollTermNo = 3
        enrollment1.leaveTermYear = 2011
        enrollment1.leaveTermNo = 3
        enrollment1.save()
        
        Entity enrollment2 = new Entity( "Enrollment" )
        enrollment2.studentId = "0811awijaya"
        enrollment2.schoolName = "Test"
        enrollment2.enrollTermYear = 2012
        enrollment2.enrollTermNo = 2
        enrollment2.leaveTermYear = null
        enrollment2.leaveTermNo = null
        enrollment2.save()
        
        Entity enrollment3 = new Entity( "Enrollment" )
        enrollment3.studentId = "0811awijaya"
        enrollment3.schoolName = "Hope Academy"
        enrollment3.enrollTermYear = 2013
        enrollment3.enrollTermNo = 1
        enrollment3.leaveTermYear = 2013
        enrollment3.leaveTermNo = 1
        enrollment3.save()
        
        Entity enrollmentMetaDataForTest = new Entity( "EnrollmentMetaData", appEntity.getKey() )
    	enrollmentMetaDataForTest.schoolName = "Test"
    	enrollmentMetaDataForTest.count = 4
    	enrollmentMetaDataForTest.save()
        
        Index enrollmentIndex = SearchServiceFactory.getSearchService().index( "Enrollment" )
        
		enrollmentIndex.put {
			document( id: "" + enrollment1.getKey().getId() ) {
				studentId atom: enrollment1.studentId
				enrollTermYear number: enrollment1.enrollTermYear
				enrollTermNo number: enrollment1.enrollTermNo
				enrollTermStartDate date: Date.parse( "MMM d yy", "Aug 1 2011" )
				leaveTermYear number: enrollment1.leaveTermYear
				leaveTermNo number: enrollment1.leaveTermNo
				leaveTermEndDate date: Date.parse( "MMM d yyyy", "Oct 31 2011" )
				schoolName text: enrollment1.schoolName
			}
		}
    
	    enrollmentIndex.put {
			document( id: "" + enrollment2.getKey().getId() ) {
				studentId atom: enrollment2.studentId
				enrollTermYear number: enrollment2.enrollTermYear
				enrollTermNo number: enrollment2.enrollTermNo
				enrollTermStartDate date: Date.parse( "MMM d yy", "May 1 2012" )
				leaveTermYear number: enrollment2.leaveTermYear
				leaveTermNo number: enrollment2.leaveTermNo
				leaveTermEndDate date: Date.parse( "MMM d yy", "Dec 31 2999" )
				schoolName text: enrollment2.schoolName
			}
		}
	    
	    enrollmentIndex.put {
			document( id: "" + enrollment3.getKey().getId() ) {
				studentId atom: enrollment3.studentId
				enrollTermYear number: enrollment3.enrollTermYear
				enrollTermNo number: enrollment3.enrollTermNo
				enrollTermStartDate date: Date.parse( "MMM d yy", "Feb 1 2013" )
				leaveTermYear number: enrollment3.leaveTermYear
				leaveTermNo number: enrollment3.leaveTermNo
				leaveTermEndDate date: Date.parse( "MMM d yy", "Apr 30 2013" )
				schoolName text: enrollment3.schoolName
			}
		}
	    
	    when: "studentId: 0811awijaya is supplied"
        Document lastEnrollmentDocument = EnrollmentDocument.findLastEnrollmentDocumentByStudentId( "0811awijaya" )
        Set<String> lastEnrollmentDocumentFieldNames = lastEnrollmentDocument.getFieldNames()
        
        then: "the result shall be enrollmentDocument2"
        lastEnrollmentDocument.getOnlyField( "studentId" ).getAtom() == enrollment2.studentId
        lastEnrollmentDocument.getOnlyField( "schoolName" ).getText() == enrollment2.schoolName
        lastEnrollmentDocument.getOnlyField( "enrollTermYear" ).getNumber() == enrollment2.enrollTermYear
        lastEnrollmentDocument.getOnlyField( "enrollTermNo" ).getNumber() == enrollment2.enrollTermNo
        !lastEnrollmentDocumentFieldNames.contains( "leaveTermYear" )
        !lastEnrollmentDocumentFieldNames.contains( "leaveTermNo" )
    }
    
    def "findLastEnrollmentByStudentId returns the correct Enrollment"() {
        given: "Three EnrollmentDocuments"
        Entity appEntity = new Entity( "App" )
    	appEntity.name = "urfschools"
    	appEntity.save()
        
        Entity enrollment1 = new Entity( "Enrollment" )
        enrollment1.studentId = "0811awijaya"
        enrollment1.schoolName = "Hope Academy"
        enrollment1.enrollTermYear = 2011
        enrollment1.enrollTermNo = 3
        enrollment1.leaveTermYear = 2011
        enrollment1.leaveTermNo = 3
        enrollment1.save()
        
        Entity enrollment2 = new Entity( "Enrollment" )
        enrollment2.studentId = "0811awijaya"
        enrollment2.schoolName = "Test"
        enrollment2.enrollTermYear = 2012
        enrollment2.enrollTermNo = 2
        enrollment2.leaveTermYear = 2013
        enrollment2.leaveTermNo = 2
        enrollment2.save()
        
        Entity enrollment3 = new Entity( "Enrollment" )
        enrollment3.studentId = "0811awijaya"
        enrollment3.schoolName = "Hope Academy"
        enrollment3.enrollTermYear = 2013
        enrollment3.enrollTermNo = 1
        enrollment3.leaveTermYear = 2013
        enrollment3.leaveTermNo = 1
        enrollment3.save()
        
        Entity enrollmentMetaDataForTest = new Entity( "EnrollmentMetaData", appEntity.getKey() )
    	enrollmentMetaDataForTest.schoolName = "Test"
    	enrollmentMetaDataForTest.count = 4
    	enrollmentMetaDataForTest.save()
        
        Index enrollmentIndex = SearchServiceFactory.getSearchService().index( "Enrollment" )
        
		enrollmentIndex.put {
			document( id: "" + enrollment1.getKey().getId() ) {
				studentId atom: enrollment1.studentId
				enrollTermYear number: enrollment1.enrollTermYear
				enrollTermNo number: enrollment1.enrollTermNo
				enrollTermStartDate date: Date.parse( "MMM d yy", "Aug 1 2011" )
				leaveTermYear number: enrollment1.leaveTermYear
				leaveTermNo number: enrollment1.leaveTermNo
				leaveTermEndDate date: Date.parse( "MMM d yyyy", "Oct 31 2011" )
				schoolName text: enrollment1.schoolName
			}
		}
    
	    enrollmentIndex.put {
			document( id: "" + enrollment2.getKey().getId() ) {
				studentId atom: enrollment2.studentId
				enrollTermYear number: enrollment2.enrollTermYear
				enrollTermNo number: enrollment2.enrollTermNo
				enrollTermStartDate date: Date.parse( "MMM d yy", "May 1 2012" )
				leaveTermYear number: enrollment2.leaveTermYear
				leaveTermNo number: enrollment2.leaveTermNo
				leaveTermEndDate date: Date.parse( "MMM d yy", "Jul 31 2013" )
				schoolName text: enrollment2.schoolName
			}
		}
	    
	    enrollmentIndex.put {
			document( id: "" + enrollment3.getKey().getId() ) {
				studentId atom: enrollment3.studentId
				enrollTermYear number: enrollment3.enrollTermYear
				enrollTermNo number: enrollment3.enrollTermNo
				enrollTermStartDate date: Date.parse( "MMM d yy", "Feb 1 2013" )
				leaveTermYear number: enrollment3.leaveTermYear
				leaveTermNo number: enrollment3.leaveTermNo
				leaveTermEndDate date: Date.parse( "MMM d yy", "Apr 30 2013" )
				schoolName text: enrollment3.schoolName
			}
		}
	    
	    when: "studentId: 0811awijaya is supplied"
        Entity lastEnrollment = Enrollment.findLastEnrollmentByStudentId( "0811awijaya" )
        
        then: "the result shall be enrollment2"
        lastEnrollment.studentId == enrollment2.studentId
        lastEnrollment.schoolName == enrollment2.schoolName
        lastEnrollment.enrollTermYear == enrollment2.enrollTermYear
        lastEnrollment.enrollTermNo == enrollment2.enrollTermNo
        lastEnrollment.leaveTermYear == enrollment2.leaveTermYear
        lastEnrollment.leaveTermNo == enrollment2.leaveTermNo
    }
    */
    def "findOverlappedEnrollment returns the correct Enrollment when comparing a supplied Enrollment to an existing Enrollment with a defined Leave Term"() {
        given: "an Enrollment from Aug 1 2012 to Apr 30 2013"
        Entity termOne2012 = new Entity( "Term" )
        termOne2012.termSchool = "Hope Academy"
        termOne2012.termNo = 1
        termOne2012.year = 2012
        termOne2012.startDate = Date.parse( "MMM d yy", "Feb 1 2012" )
        termOne2012.endDate = Date.parse( "MMM d yy", "Apr 30 2012" )
        termOne2012.save()
        
        Entity termTwo2012 = new Entity( "Term" )
        termTwo2012.termSchool = "Hope Academy"
        termTwo2012.termNo = 2
        termTwo2012.year = 2012
        termTwo2012.startDate = Date.parse( "MMM d yy", "May 1 2012" )
        termTwo2012.endDate = Date.parse( "MMM d yy", "Jul 31 2012" )
        termTwo2012.save()

        Entity termThree2012 = new Entity( "Term" )
        termThree2012.termSchool = "Hope Academy"
        termThree2012.termNo = 3
        termThree2012.year = 2012
        termThree2012.startDate = Date.parse( "MMM d yy", "May 1 2012" )
        termThree2012.endDate = Date.parse( "MMM d yy", "Aug 1 2012" )
        termThree2012.save()
        
        Entity termFour2012 = new Entity( "Term" )
        termFour2012.termSchool = "Hope Academy"
        termFour2012.termNo = 4
        termFour2012.year = 2012
        termFour2012.startDate = Date.parse( "MMM d yy", "Aug 1 2012" )
        termFour2012.endDate = Date.parse( "MMM d yy", "Oct 31 2012" )
        termFour2012.save()
        
        Entity termFive2012 = new Entity( "Term" )
        termFive2012.termSchool = "Hope Academy"
        termFive2012.termNo = 5
        termFive2012.year = 2012
        termFive2012.startDate = Date.parse( "MMM d yy", "Aug 15 2012" )
        termFive2012.endDate = Date.parse( "MMM d yy", "Oct 31 2012" )
        termFive2012.save()
        
        Entity termOne2013 = new Entity( "Term" )
        termOne2013.termSchool = "Hope Academy"
        termOne2013.termNo = 1
        termOne2013.year = 2013
        termOne2013.startDate = Date.parse( "MMM d yy", "Feb 1 2013" )
        termOne2013.endDate = Date.parse( "MMM d yy", "Apr 30 2013" )
        termOne2013.save()
        
        Entity termTwo2013 = new Entity( "Term" )
        termTwo2013.termSchool = "Hope Academy"
        termTwo2013.termNo = 2
        termTwo2013.year = 2013
        termTwo2013.startDate = Date.parse( "MMM d yy", "Apr 30 2013" )
        termTwo2013.endDate = Date.parse( "MMM d yy", "Jul 31 2013" )
        termTwo2013.save()
        
        Entity termThree2013 = new Entity( "Term" )
        termThree2013.termSchool = "Hope Academy"
        termThree2013.termNo = 3
        termThree2013.year = 2013
        termThree2013.startDate = Date.parse( "MMM d yy", "May 1 2013" )
        termThree2013.endDate = Date.parse( "MMM d yy", "Jul 31 2013" )
        termThree2013.save()
        
        Entity termFour2013 = new Entity( "Term" )
        termFour2013.termSchool = "Hope Academy"
        termFour2013.termNo = 4
        termFour2013.year = 2013
        termFour2013.startDate = Date.parse( "MMM d yy", "Aug 1 2013" )
        termFour2013.endDate = Date.parse( "MMM d yy", "Oct 31 2013" )
        termFour2013.save()
        
        Entity existingEnrollment = new Entity( "Enrollment" )
        existingEnrollment.studentId = "0212awijasa"
        existingEnrollment.schoolName = "Hope Academy"
        existingEnrollment.enrollTermYear = 2012
        existingEnrollment.enrollTermNo = 4
        existingEnrollment.leaveTermYear = 2013
        existingEnrollment.leaveTermNo = 1
        existingEnrollment.save()
        
        when: "an Enrollment from Feb 1 2012 to Jul 31 2012 is supplied"
        Entity suppliedEnrollment = new Entity( "Enrollment" )
        suppliedEnrollment.studentId = "0212awijasa"
        suppliedEnrollment.schoolName = "Hope Academy"
        suppliedEnrollment.enrollTermYear = 2012
        suppliedEnrollment.enrollTermNo = 1
        suppliedEnrollment.leaveTermYear = 2012
        suppliedEnrollment.leaveTermNo = 2
        
        Entity overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be no overlapped Enrollment"
        
        overlappedEnrollment == null
            
        when: "an Enrollment from Feb 1 2012 to Aug 1 2012 is supplied"
        suppliedEnrollment.leaveTermNo = 3
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Feb 1 2012 to Oct 31 2012 is supplied"
        suppliedEnrollment.leaveTermNo = 4
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Feb 1 2012 to Apr 30 2013 is supplied"
        suppliedEnrollment.leaveTermYear = 2013
        suppliedEnrollment.leaveTermNo = 1
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Feb 1 2012 to Jul 31 2013 is supplied"
        suppliedEnrollment.leaveTermNo = 3
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Feb 1 2012 to now is supplied"
        suppliedEnrollment.leaveTermYear = null
        suppliedEnrollment.leaveTermNo = null
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Aug 1 2012 to Oct 31 2012 is supplied"
        suppliedEnrollment.enrollTermNo = 4
        suppliedEnrollment.leaveTermYear = 2012
        suppliedEnrollment.leaveTermNo = 4
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Aug 1 2012 to Apr 30 2013 is supplied"
        suppliedEnrollment.leaveTermYear = 2013
        suppliedEnrollment.leaveTermNo = 1
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Aug 1 2012 to Jul 31 2013 is supplied"
        suppliedEnrollment.leaveTermNo = 3
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Aug 1 2012 to now is supplied"
        suppliedEnrollment.leaveTermYear = null
        suppliedEnrollment.leaveTermNo = null
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Aug 15 2012 to Oct 31 2012 is supplied"
        suppliedEnrollment.enrollTermNo = 5
        suppliedEnrollment.leaveTermYear = 2012
        suppliedEnrollment.leaveTermNo = 5
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Aug 15 2012 to Apr 30 2013 is supplied"
        suppliedEnrollment.leaveTermYear = 2013
        suppliedEnrollment.leaveTermNo = 1
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Aug 15 2012 to Jul 31 2013 is supplied"
        suppliedEnrollment.leaveTermNo = 3
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Aug 15 2012 to now is supplied"
        suppliedEnrollment.leaveTermYear = null
        suppliedEnrollment.leaveTermNo = null
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Apr 30 2013 to Oct 31 2013 is supplied"
        suppliedEnrollment.enrollTermYear = 2013
        suppliedEnrollment.enrollTermNo = 2
        suppliedEnrollment.leaveTermYear = 2013
        suppliedEnrollment.leaveTermNo = 4
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Apr 30 2013 to now is supplied"
        suppliedEnrollment.leaveTermYear = null
        suppliedEnrollment.leaveTermNo = null
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from May 1 2013 to Oct 31 2013 is supplied"
        suppliedEnrollment.enrollTermYear = 2013
        suppliedEnrollment.enrollTermNo = 3
        suppliedEnrollment.leaveTermYear = 2013
        suppliedEnrollment.leaveTermNo = 4
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be no overlapped Enrollment"
        
        overlappedEnrollment == null
        
        when: "an Enrollment from May 1 2013 to now is supplied"
        suppliedEnrollment.leaveTermYear = null
        suppliedEnrollment.leaveTermNo = null
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be no overlapped Enrollment"
        
        overlappedEnrollment == null
    }
    
    def "findOverlappedEnrollment returns the correct Enrollment when comparing a supplied Enrollment to an existing Enrollment with no Leave Term"() {
        given: "an Enrollment from Aug 1 2012 to now"
        Entity termOne2012 = new Entity( "Term" )
        termOne2012.termSchool = "Hope Academy"
        termOne2012.termNo = 1
        termOne2012.year = 2012
        termOne2012.startDate = Date.parse( "MMM d yy", "Feb 1 2012" )
        termOne2012.endDate = Date.parse( "MMM d yy", "Apr 30 2012" )
        termOne2012.save()
        
        Entity termTwo2012 = new Entity( "Term" )
        termTwo2012.termSchool = "Hope Academy"
        termTwo2012.termNo = 2
        termTwo2012.year = 2012
        termTwo2012.startDate = Date.parse( "MMM d yy", "May 1 2012" )
        termTwo2012.endDate = Date.parse( "MMM d yy", "Jul 31 2012" )
        termTwo2012.save()

        Entity termThree2012 = new Entity( "Term" )
        termThree2012.termSchool = "Hope Academy"
        termThree2012.termNo = 3
        termThree2012.year = 2012
        termThree2012.startDate = Date.parse( "MMM d yy", "May 1 2012" )
        termThree2012.endDate = Date.parse( "MMM d yy", "Aug 1 2012" )
        termThree2012.save()
        
        Entity termFour2012 = new Entity( "Term" )
        termFour2012.termSchool = "Hope Academy"
        termFour2012.termNo = 4
        termFour2012.year = 2012
        termFour2012.startDate = Date.parse( "MMM d yy", "Aug 1 2012" )
        termFour2012.endDate = Date.parse( "MMM d yy", "Oct 31 2012" )
        termFour2012.save()
        
        Entity termOne2013 = new Entity( "Term" )
        termOne2013.termSchool = "Hope Academy"
        termOne2013.termNo = 1
        termOne2013.year = 2013
        termOne2013.startDate = Date.parse( "MMM d yy", "Feb 1 2013" )
        termOne2013.endDate = Date.parse( "MMM d yy", "Apr 30 2013" )
        termOne2013.save()
        
        Entity termTwo2013 = new Entity( "Term" )
        termTwo2013.termSchool = "Hope Academy"
        termTwo2013.termNo = 2
        termTwo2013.year = 2013
        termTwo2013.startDate = Date.parse( "MMM d yy", "May 1 2013" )
        termTwo2013.endDate = Date.parse( "MMM d yy", "Jul 31 2013" )
        termTwo2013.save()
        
        Entity existingEnrollment = new Entity( "Enrollment" )
        existingEnrollment.studentId = "0212awijasa"
        existingEnrollment.schoolName = "Hope Academy"
        existingEnrollment.enrollTermYear = 2012
        existingEnrollment.enrollTermNo = 4
        existingEnrollment.leaveTermYear = null
        existingEnrollment.leaveTermNo = null
        existingEnrollment.save()
        
        when: "an Enrollment from Feb 1 2012 to Jul 31 2012 is supplied"
        Entity suppliedEnrollment = new Entity( "Enrollment" )
        suppliedEnrollment.studentId = "0212awijasa"
        suppliedEnrollment.schoolName = "Hope Academy"
        suppliedEnrollment.enrollTermYear = 2012
        suppliedEnrollment.enrollTermNo = 1
        suppliedEnrollment.leaveTermYear = 2012
        suppliedEnrollment.leaveTermNo = 2
        
        Entity overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be no overlapped Enrollment"
        
        overlappedEnrollment == null
            
        when: "an Enrollment from Feb 1 2012 to Aug 1 2012 is supplied"
        suppliedEnrollment.leaveTermNo = 3
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Feb 1 2012 to Oct 31 2012 is supplied"
        suppliedEnrollment.leaveTermNo = 4
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Feb 1 2012 to now is supplied"
        suppliedEnrollment.leaveTermYear = null
        suppliedEnrollment.leaveTermNo = null
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Aug 1 2012 to Apr 30 2013 is supplied"
        suppliedEnrollment.enrollTermYear = 2012
        suppliedEnrollment.enrollTermNo = 4
        suppliedEnrollment.leaveTermYear = 2013
        suppliedEnrollment.leaveTermNo = 1
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Aug 1 2012 to now is supplied"
        suppliedEnrollment.leaveTermYear = null
        suppliedEnrollment.leaveTermNo = null
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Feb 1 2013 to Jul 31 2013 is supplied"
        suppliedEnrollment.enrollTermYear = 2013
        suppliedEnrollment.enrollTermNo = 1
        suppliedEnrollment.leaveTermYear = 2013
        suppliedEnrollment.leaveTermNo = 2
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
        
        when: "an Enrollment from Feb 1 2013 to now is supplied"
        suppliedEnrollment.leaveTermYear = null
        suppliedEnrollment.leaveTermNo = null
        
        overlappedEnrollment = Enrollment.findOverlappedEnrollment( suppliedEnrollment )

        then: "there shall be an overlapped Enrollment"
        
        overlappedEnrollment != null
    }
    
    def "findOverlappedTerm returns the correct Term"() {
        given: "three Hope Academy terms, four overlapping terms, and two terms that don't overlap"
        def termOne2012 = new Entity( "Term" )
        termOne2012.termSchool = "Hope Academy"
        termOne2012.termNo = 1
        termOne2012.year = 2012
        termOne2012.startDate = Date.parse( "MMM d yy", "Feb 1 2012" )
        termOne2012.endDate = Date.parse( "MMM d yy", "Apr 30 2012" )
        termOne2012.save()
        
        def termTwo2012 = new Entity( "Term" )
        termTwo2012.termSchool = "Hope Academy"
        termTwo2012.termNo = 2
        termTwo2012.year = 2012
        termTwo2012.startDate = Date.parse( "MMM d yy", "May 1 2012" )
        termTwo2012.endDate = Date.parse( "MMM d yy", "Jul 31 2012" )
        termTwo2012.save()

        def termThree2012 = new Entity( "Term" )
        termThree2012.termSchool = "Hope Academy"
        termThree2012.termNo = 3
        termThree2012.year = 2012
        termThree2012.startDate = Date.parse( "MMM d yy", "Aug 1 2012" )
        termThree2012.endDate = Date.parse( "MMM d yy", "Oct 31 2012" )
        termThree2012.save()
        
        def stMaryTermOne2013 = new Entity( "Term" )
        stMaryTermOne2013.termSchool = "St. Mary's"
        stMaryTermOne2013.termNo = 1
        stMaryTermOne2013.year = 2013
        stMaryTermOne2013.startDate = Date.parse( "MMM d yy", "Feb 1 2013" )
        stMaryTermOne2013.endDate = Date.parse( "MMM d yy", "Apr 30 2013" )
        stMaryTermOne2013.save()
        
        def stMaryTermThree2011 = new Entity( "Term" )
        stMaryTermThree2011.termSchool = "St. Mary's"
        stMaryTermThree2011.termNo = 3
        stMaryTermThree2011.year = 2011
        stMaryTermThree2011.startDate = Date.parse( "MMM d yy", "Aug 1 2011" )
        stMaryTermThree2011.endDate = Date.parse( "MMM d yy", "Oct 31 2011" )
        
        def firstOverlappingTerm = new Entity( "Term" )
        firstOverlappingTerm.termSchool = "Hope Academy"
        firstOverlappingTerm.termNo = 4
        firstOverlappingTerm.year = 2012
        firstOverlappingTerm.startDate = Date.parse( "MMM d yy", "Jul 15 2012" )
        firstOverlappingTerm.endDate = Date.parse( "MMM d yy", "Aug 15 2012" )
        
        def secondOverlappingTerm = new Entity( "Term" )
        secondOverlappingTerm.termSchool = "Hope Academy"
        secondOverlappingTerm.termNo = 5
        secondOverlappingTerm.year = 2012
        secondOverlappingTerm.startDate = Date.parse( "MMM d yy", "Jul 15 2012" )
        secondOverlappingTerm.endDate = Date.parse( "MMM d yy", "Nov 15 2012" )

        def thirdOverlappingTerm = new Entity( "Term" )
        thirdOverlappingTerm.termSchool = "Hope Academy"
        thirdOverlappingTerm.termNo = 6
        thirdOverlappingTerm.year = 2012
        thirdOverlappingTerm.startDate = Date.parse( "MMM d yy", "Aug 15 2012" )
        thirdOverlappingTerm.endDate = Date.parse( "MMM d yy", "Oct 15 2012" )
        
        def fourthOverlappingTerm = new Entity( "Term" )
        fourthOverlappingTerm.termSchool = "Hope Academy"
        fourthOverlappingTerm.termNo = 7
        fourthOverlappingTerm.year = 2012
        fourthOverlappingTerm.startDate = Date.parse( "MMM d yy", "Aug 15 2012" )
        fourthOverlappingTerm.endDate = Date.parse( "MMM d yy", "Nov 15 2012" )
        
        def termOne2013 = new Entity( "Term" )
        termOne2013.termSchool = "Hope Academy"
        termOne2013.termNo = 1
        termOne2013.year = 2013
        termOne2013.startDate = Date.parse( "MMM d yy", "Feb 1 2013" )
        termOne2013.endDate = Date.parse( "MMM d yy", "Apr 30 2013" )
        
        def termThree2011 = new Entity( "Term" )
        termThree2011.termSchool = "Hope Academy"
        termThree2011.termNo = 3
        termThree2011.year = 2011
        termThree2011.startDate = Date.parse( "MMM d yy", "Aug 1 2011" )
        termThree2011.endDate = Date.parse( "MMM d yy", "Oct 31 2011" )
        
        when: "parameter: suppliedTerm=firstOverlappingTerm is provided"
        def overlappedTerm = Term.findOverlappedTerm( firstOverlappingTerm )

        then: "there shall be an overlapped Term: 2012 Term 3"
        
        overlappedTerm.termNo == termThree2012.termNo
        overlappedTerm.termSchool == termThree2012.termSchool
        overlappedTerm.year == termThree2012.year
        overlappedTerm.startDate == termThree2012.startDate
        overlappedTerm.endDate == termThree2012.endDate
            
        when: "parameter: suppliedTerm=secondOverlappingTerm is provided"
        overlappedTerm = Term.findOverlappedTerm( secondOverlappingTerm )

        then: "there shall be an overlapped Term: 2012 Term 3"
        
        overlappedTerm.termNo == termThree2012.termNo
        overlappedTerm.termSchool == termThree2012.termSchool
        overlappedTerm.year == termThree2012.year
        overlappedTerm.startDate == termThree2012.startDate
        overlappedTerm.endDate == termThree2012.endDate
        
        when: "parameter: suppliedTerm=thirdOverlappingTerm is provided"
        overlappedTerm = Term.findOverlappedTerm( thirdOverlappingTerm )

        then: "there shall be an overlapped Term: 2012 Term 3"
        
        overlappedTerm.termNo == termThree2012.termNo
        overlappedTerm.termSchool == termThree2012.termSchool
        overlappedTerm.year == termThree2012.year
        overlappedTerm.startDate == termThree2012.startDate
        overlappedTerm.endDate == termThree2012.endDate
        
        when: "parameter: suppliedTerm=fourthOverlappingTerm is provided"
        overlappedTerm = Term.findOverlappedTerm( fourthOverlappingTerm )

        then: "there shall be an overlapped Term: 2012 Term 3"
        
        overlappedTerm.termNo == termThree2012.termNo
        overlappedTerm.termSchool == termThree2012.termSchool
        overlappedTerm.year == termThree2012.year
        overlappedTerm.startDate == termThree2012.startDate
        overlappedTerm.endDate == termThree2012.endDate
        
        when: "parameter: suppliedTerm=termOne2013 is provided"
        overlappedTerm = Term.findOverlappedTerm( termOne2013 )

        then: "there shall be no overlapped Term"
        
        overlappedTerm == null
        
        when: "parameter: suppliedTerm=termThree2011 is provided"
        overlappedTerm = Term.findOverlappedTerm( termThree2011 )

        then: "there shall be no overlapped Term"
        
        overlappedTerm == null
    }
}

