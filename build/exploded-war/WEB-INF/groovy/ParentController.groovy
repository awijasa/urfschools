/*
Document   : Groovlet file: ParentController.groovy
Created on : Tue Apr 23 18:50:00 CST 2013
Author     : awijasa
 */
 
import com.google.appengine.api.datastore.Entity
import data.AnonymousParentalRelationship
import data.AuthorizationException
import data.DuplicateEntityException
import data.EmptyRequiredFieldException
import data.Parent
import data.Relationship
import data.StaleRecordException
import data.StudentWithLastEnrollment
import data.URFUser
import data.UserPrivilege
import formatter.ListItemFormatter
 
/* Handle create, update, and delete operations on the Parent entity. */

/* Prevent mobile browsers from caching responses. */
response.setHeader( "Pragma", "no-cache" )
response.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0" )
response.setHeader( "Expires", "0" )
response.setHeader( "Last-Modified", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )
response.setHeader( "If-Modified-Since", new Date( 0 ).format( "EEE, dd MMM yyyy HH:mm:ss zzz" ) )

/* Respond with an Unauthorized error message if a user has not logged in when accessing the ParentController. */
if( URFUser.findByEmail( user?.getEmail() ) == null && !localMode )
    response.sendError( response.SC_UNAUTHORIZED )
else {
	
	/* Delete a Parent entity identified by the id parameter when the action parameter value is "delete". */
	if( params.action == "delete" ) {
		Entity parent
		List<Entity> relationships
		
		try {
			parent = datastore.get( "Parent", Long.parseLong( params.id ) )
			
			if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != parent.lastUpdateDate ||
                ( params.lastUpdateUser?: "" ) != ( parent.lastUpdateUser?: "" ) )
                throw new StaleRecordException( params.action, "Parent" )
			
			relationships = Relationship.findByParentId( parent.parentId )
			
			relationships.each(
				{
					String schoolName = StudentWithLastEnrollment.findByStudentId( it.studentId ).lastEnrollmentSchool
					if( /* Test Script */ user == null || UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), schoolName )?.privilege == "Modify" )
						it.delete()
					else
						throw new AuthorizationException( schoolName )
				}
			)
			
			parent.delete()
			Parent.deleteMemcache()
			
			Parent.findByLimitAndOffset( 0, Integer.parseInt( params.nextTwentyOffset ) - 1 ).each() {
                /*
                 * Respond with the HTML code that is required to display the Parent entity that can fill the empty space that resulted
                 * from the deleted record within an accordion.
                 */
                println ListItemFormatter.getParentListItem( it )
            }
		}
		catch( Exception e ) {
			try { parent?.save() } catch( Exception saveException ) {}
			try { relationships*.save() } catch( Exception saveException ) {}
		
			/* Respond with an Internal Server error message if the delete fails. */
			response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
			response.setHeader( "Response-Phrase", e.getMessage() )
		}
	}
	
	/* Save a new Parent entity and its new Relationship when the action parameter value is "save". */
	else if( params.action == "edit" || params.action == "save" ) {
		Entity anonymousParentalRelationship
		List<Entity> anonymousParentalRelationshipBackups = new ArrayList()
		List<Entity> newRelationships = new ArrayList()
		Entity parent
		Entity parentBackup
		String parentId
		List<Entity> relationshipBackups = new ArrayList()
		
		try {
			if( params.action == "edit" ) {
				parent = datastore.get( "Parent", Long.parseLong( params.id ) )
				parentBackup = datastore.get( "Parent", Long.parseLong( params.id ) )
				
				if( Date.parse( "MMM d yyyy HH:mm:ss.SSS zzz", params.lastUpdateDate ) != parent.lastUpdateDate ||
					( params.lastUpdateUser?: "" ) != ( parent.lastUpdateUser?: "" ) )
					throw new StaleRecordException( params.action, "Parent" )
				
				parentId = parent.parentId
			}
		
			String email
			String firstName
			String lastName
			String primaryPhone
			String profession
			String secondaryPhone
			String village
		
			/* If the firstName parameter value is "First Name" or empty, do not put it into the Parent entity's firstName field. */
            if( params.firstName != null && !params.firstName.equals( "First Name" ) && !params.firstName.equals( "" ) )
                firstName = params.firstName
                
            /* If the firstName parameter value is "First Name" or empty, and firstName is a required Parent entity's field, throw an EmptyRequiredFieldException. */
            else if( Parent.isRequired( "firstName" ) )
                throw new EmptyRequiredFieldException( "Parent", "firstName" )

            /* If the lastName parameter value is "Last Name" or empty, do not put it into the Parent entity's lastName field. */
            if( params.lastName != null && !params.lastName.equals( "Last Name" ) && !params.lastName.equals( "" ) )
                lastName = params.lastName
                
            /* If the lastName parameter value is "Last Name" or empty, and lastName is a required Parent entity's field, throw an EmptyRequiredFieldException. */
            else if( Parent.isRequired( "lastName" ) )
                throw new EmptyRequiredFieldException( "Parent", "lastName" )
                
            /* If the village parameter value is "Village" or empty, do not put it into the Parent entity's village field. */
            if( params.village != null && !params.village.equals( "Village" ) && !params.village.equals( "" ) )
                village = params.village
                
            /* If the village parameter value is "Village" or empty, and village is a required Parent entity's field, throw an EmptyRequiredFieldException. */
            else if( Parent.isRequired( "village" ) )
                throw new EmptyRequiredFieldException( "Parent", "village" )
                
            /* If the primaryPhone parameter value is "Primary Phone" or empty, do not put it into the Parent entity's primaryPhone field. */
            if( params.primaryPhone != null && !params.primaryPhone.equals( "Primary Phone" ) && !params.primaryPhone.equals( "" ) )
                primaryPhone = params.primaryPhone
                
            /* If the primaryPhone parameter value is "Primary Phone" or empty, and primaryPhone is a required Parent entity's field, throw an EmptyRequiredFieldException. */
            else if( Parent.isRequired( "primaryPhone" ) )
                throw new EmptyRequiredFieldException( "Parent", "primaryPhone" )
                
            /* If the secondaryPhone parameter value is "Secondary Phone" or empty, do not put it into the Parent entity's secondaryPhone field. */
            if( params.secondaryPhone != null && !params.secondaryPhone.equals( "Secondary Phone" ) && !params.secondaryPhone.equals( "" ) )
                secondaryPhone = params.secondaryPhone
                
            /* If the secondaryPhone parameter value is "Secondary Phone" or empty, and secondaryPhone is a required Parent entity's field, throw an EmptyRequiredFieldException. */
            else if( Parent.isRequired( "secondaryPhone" ) )
                throw new EmptyRequiredFieldException( "Parent", "secondaryPhone" )
                
            /* If the email parameter value is "Email" or empty, do not put it into the Parent entity's email field. */
            if( params.email != null && !params.email.equals( "Email" ) && !params.email.equals( "" ) )
                email = params.email
                
            /* If the email parameter value is "Email" or empty, and email is a required Parent entity's field, throw an EmptyRequiredFieldException. */
            else if( Parent.isRequired( "email" ) )
                throw new EmptyRequiredFieldException( "Parent", "email" )
                
            /* If the profession parameter value is "Profession" or empty, do not put it into the Parent entity's profession field. */
            if( params.profession != null && !params.profession.equals( "Profession" ) && !params.profession.equals( "" ) )
                profession = params.profession
                
            /* If the profession parameter value is "Profession" or empty, and profession is a required Parent entity's field, throw an EmptyRequiredFieldException. */
            else if( Parent.isRequired( "profession" ) )
                throw new EmptyRequiredFieldException( "Parent", "profession" )
                
            if( params.action == "save" ) {
            	if( lastName == null )
            		parentId = firstName.toLowerCase()
            	else
            		parentId = firstName.toLowerCase().substring( 0, 1 ) + lastName.toLowerCase()
            		
            	String baseParentId = parentId
            	
            	for( int i = 1; Parent.findByParentId( parentId ) != null; i++ )
                    parentId = baseParentId + i
                    
                parent = new Entity( "Parent" )
            }
                
            parent.parentId = parentId
            parent.firstName = firstName
            parent.lastName = lastName
            parent.deceasedInd = params.deceasedInd? "Y": "N"
            parent.village = village
            parent.primaryPhone = primaryPhone
            parent.secondaryPhone = secondaryPhone
            parent.email = email
            parent.profession = profession
            parent.lastUpdateDate = new Date()
            parent.lastUpdateUser = user?.getEmail()
            
            parent.save()
            Parent.deleteMemcache()
            
            /* Create new Relationship entities associated to the Parent entity */
            
            /*
            	Iterate through each parameter until the parentRelationship<seq>, childId<seq>, guardianRelationship<seq>, and relativeId<seq>
            	parameters are found.
            */
            Enumeration allParamNames = request.getParameterNames()
            
            while( allParamNames.hasMoreElements() ) {
            	def paramName = allParamNames.nextElement()
            	
            	Entity relationship
            	
            	if( paramName.startsWith( "deleteChildId" ) || paramName.startsWith( "deleteRelativeId" ) ) {
            		String studentId = request.getParameter( paramName )
            		String schoolName = StudentWithLastEnrollment.findByStudentId( studentId ).lastEnrollmentSchool
            		relationship = Relationship.findByParentIdAndStudentId( parent.parentId, studentId )
            		
					if( paramName.startsWith( "deleteChildId" ) ) {
						anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( studentId, relationship.parentalRelationship )
						anonymousParentalRelationshipBackups.add( AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( studentId, relationship.parentalRelationship ) )
						
						if( anonymousParentalRelationship.deceasedInd != parent.deceasedInd ) {
							anonymousParentalRelationship.deceasedInd = parent.deceasedInd
							anonymousParentalRelationship.lastUpdateDate = new Date()
							anonymousParentalRelationship.lastUpdateUser = user?.getEmail()
							anonymousParentalRelationship.save()
						}
					}
					
            		if( /* Test Script */ user == null || UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), schoolName )?.privilege == "Modify" )
            			relationship.delete()
            		else
            			throw new AuthorizationException( schoolName )
            			
            		relationshipBackups.add( relationship )
            	}
            	else if( paramName.startsWith( "newGuardianRelationship" ) ) {
            		String relativeIdParam = request.getParameter( "newRelativeId${ paramName.substring( 23 ) }" )
            		String studentId
            		
            		/* If the newRelativeId parameter value is empty, do not put it into the Relationship entity's studentId field. */
					if( relativeIdParam != null && relativeIdParam != "" )
						studentId = relativeIdParam
				
					/* If the newRelativeId parameter value is empty and newRelativeId is a required Relationship entity's field, throw an EmptyRequiredFieldException. */
					else if( Relationship.isRequired( "studentId" ) )
						throw new EmptyRequiredFieldException( "Relationship", "studentId" )
            	
            		relationship = new Entity( "Relationship", parent.getKey() )
            		relationship.parentId = parentId
            		relationship.guardianRelationship = request.getParameter( paramName )
            		relationship.studentId = studentId
            		relationship.lastUpdateDate = new Date()
            		relationship.lastUpdateUser = user?.getEmail()
            		
            		String schoolName = StudentWithLastEnrollment.findByStudentId( relationship.studentId ).lastEnrollmentSchool
            		
            		if( /* Test Script */ user == null || UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), schoolName )?.privilege == "Modify" ) {
            			if( Relationship.findByParentIdAndStudentId( relationship.parentId, relationship.studentId ) == null )
            				relationship.save()
            			else
            				throw new DuplicateEntityException( relationship, Relationship.getPrimaryKey() )
					}
            		else
            			throw new AuthorizationException( schoolName )
            			
            		newRelationships.add( relationship )
            	}
            	else if( paramName.startsWith( "newParentRelationship" ) ) {
            		String childIdParam = request.getParameter( "newChildId${ paramName.substring( 21 ) }" )
            		String studentId
            		
            		/* If the newChildId parameter value is empty, do not put it into the Relationship entity's studentId field. */
					if( childIdParam != null && childIdParam != "" )
						studentId = childIdParam
				
					/* If the newChildId parameter value is empty and newChildId is a required Relationship entity's field, throw an EmptyRequiredFieldException. */
					else if( Relationship.isRequired( "studentId" ) )
						throw new EmptyRequiredFieldException( "Relationship", "studentId" )
						
            		relationship = new Entity( "Relationship", parent.getKey() )
            		relationship.parentId = parentId
            		relationship.parentalRelationship = request.getParameter( paramName )
            		relationship.studentId = studentId
            		relationship.lastUpdateDate = new Date()
            		relationship.lastUpdateUser = user?.getEmail()
            		
            		String schoolName = StudentWithLastEnrollment.findByStudentId( relationship.studentId ).lastEnrollmentSchool
            		
            		if( /* Test Script */ user == null || UserPrivilege.findByUserEmailAndSchoolName( user?.getEmail(), schoolName )?.privilege == "Modify" ) {
            			if( Relationship.findByParentIdAndStudentId( relationship.parentId, relationship.studentId ) == null )
            				relationship.save()
            			else
            				throw new DuplicateEntityException( relationship, Relationship.getPrimaryKey() )
					}
            		else
            			throw new AuthorizationException( schoolName )
            			
            		newRelationships.add( relationship )
            	}
            }
			
			Relationship.findParentalRelationshipsByParentId( parentId ).each(
				{
					anonymousParentalRelationship = AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( it.studentId, it.parentalRelationship )
					anonymousParentalRelationshipBackups.add( AnonymousParentalRelationship.findByStudentIdAndParentalRelationship( it.studentId, it.parentalRelationship ) )
					
					if( anonymousParentalRelationship.deceasedInd != parent.deceasedInd ) {
						anonymousParentalRelationship.deceasedInd = parent.deceasedInd
						anonymousParentalRelationship.lastUpdateDate = new Date()
						anonymousParentalRelationship.lastUpdateUser = user?.getEmail()
						anonymousParentalRelationship.save()
					}
				}
			)
            
            println ListItemFormatter.getParentListItem( parent )
		}
		catch( Exception e ) {
			try { anonymousParentalRelationshipBackups*.save() } catch( Exception saveException ) {}
			try { newRelationships*.delete() } catch( Exception deleteException ) {}
			try { relationshipBackups*.save() } catch( Exception saveException ) {}
			
			if( params.action == "save" ) {
				try { parent?.delete() } catch( Exception deleteException ) {}
			}
			else {
				try { parentBackup?.save() } catch( Exception saveException ) {}
			}
		
			/* Respond with an Internal Server error message if the save fails. */
			response.setStatus( response.SC_INTERNAL_SERVER_ERROR )
			response.setHeader( "Response-Phrase", e.getMessage() )
		}
	}
}