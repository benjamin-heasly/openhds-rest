#Supertypes
####AuditableEntity
#####What is it?
AuditableEntity is the abstract base type for all OpenHDS entities. The class provides fields useful for ‘auditing’ or keeping track of meta information pertaining to the concrete type like when it was created and last modified.
######Required Fields:
+ String uuid 
+ User insertBy 
+ Date insertDate 
+ User lastModifiedBy 
+ Date lastModifiedDate 
___
####AuditableCollectedEntity
(child of AuditableEntity)
#####What is it?
AuditableCollectedEntity is the abstract base type for all OpenHDS entities that are collected by a fieldworker.
######Dependencies:
+ FieldWorker
######Required Fields:
+ FieldWorker collectedBy 
+ Date collectionDateTime
___
####AuditableExtIdEntity
(child of AuditableCollectedEntity) 
#####What is it?
AuditableExtIdEntity is an entity that is identifiable by an id external to the OpenHDS, i.e. the functionality of the OpenHDS is not dependent on it. 
Required Fields:
String extId 
#Census Entities
####LocationHierarchy
(child of AuditableExtIdEntity)
#####What is it?
LocationHierarchy is a node in the tree that represents the study area. For example, at the top of a tree would be the The United States and it would have a child node for each of the 50 states. Each state node could have a child node for its cities and each city could have a child node for its districts and so on. LocationHierarchy extends AuditableExtIdEntity meaning it is identified by uuid as well as extId. 
#####Dependencies
+FieldWorker
+LocationHierarchy
+LocationHierarchyLevel
######Required Fields (LocationHierarchy):
+String name
+String extId
+LocationHierarchy parent
+LocationHierarchyLevel level
___
####LocationHierarchyLevel
(child of AuditableEntity)
#####What is it?
The levels of the LocationHierarchy tree are defined separately as LocationHierarchyLevels and referenced by each instance of LocationHierarchy. From the previous example, the list of LocationHierarchyLevels would be Country, State, City, District.
######Required Fields (LocationHierarchyLevel):
+int keyIdentifier
+String name
___
####Location
(child of AuditableExtIdEntity)
#####What is it?
Location is the leaf node on the LocationHierarchy tree, it is the smallest geographical unit within a study area. From the previous example for LocationHierarchy, each building within a district could be modeled with Location. 
Individuals are residents at locations.
######Dependencies
+ FieldWorker
+ LocationHierarchy
+ Domain Constraints
######Required Fields:
+ String name
+ String extId
+ LocationHierarchy parent
___
####Individual
(child of AuditableExtIdEntity)
#####What is it?
An Individual (person) within the study area that resides at a Location, is a member of a SocialGroup, and has a relationship to the head of a household.
######Dependencies
+ FieldWorker
######Required Fields:
+ String extId
+ String gender
+ Date dateOfBirth
+ String firstName
___
####SocialGroup
(child of AuditableExtIdEntity)
#####What is it?
A SocialGroup is any cultural or societal group or collective that Individuals are a member of, e.g. a church group, a household/family, a bowling team.
######Dependencies
+ FieldWorker
######Required Fields:
+ String groupName
+ String groupType
+ String extId
___
###Membership
(child of AuditableCollectedEntity)
####What is it?
A Membership models the relationship between an Individual and a SocialGroup.
######Dependencies
+ FieldWorker
+Individual
+ SocialGroup
######Required Fields:
+ Date startDate
+ String startType
+ Individual individual
+ SocialGroup socialGroup
___
####Relationship
(child of AuditableCollectedEntity)
#####What is it?
A Relationship models the relationship between two individuals. Specifically it models the relationship between an individual and the head of household that the individual is a part of.
######Dependencies
+ FieldWorker
+ Individual
######Required Fields:
+ Date startDate
+ String relationshipType
+ Individual individualA
+ Individual individualB
___
####Residency
(child of AuditableCollectedEntity)
#####What is it?
A Residency models the relationship between an Individual and a Location.
######Dependencies
+ FieldWorker 
+ Individual
+ Location
######Required Fields:
+ Date startDate
+ String relationshipType
+ Individual individualA
+ Individual individualB
___
#Update
####Visit
(child of AuditableExtIdEntity)
#####What is it?
A Visit models a FieldWorker’s follow-up visit to a particular location at a particular date. Visits are necessary for all “update” events that can take place within a population.
######Dependencies
+ FieldWorker
+ Location 
######Required Fields:
+ Date visitDate
+ Location location
___
####Death
(child of AuditableCollectedEntity)
#####What is it?
A Death models the death of one of the Individuals within the area of study.
######Dependencies
+ FieldWorker
+ Visit
+ Location 
######Required Fields:
+ Date deathDate
+ Visit visit
+ Individual individual
___
####InMigration
(child of AuditableCollectedEntity)
#####What is it?
An InMigration represents a migration between two Locations where the destination
Location is still inside the area of study. The origin Location can be within or outside the area of study and is model by the migrationType value of internal or external respectively.
Dependencies
######FieldWorker
+ Visit
+ Residency
+ Individual 
######Required Fields:
+ Visit visit
+ Date migrationDate
+ String migrationType
+ Residency residency
+ Individual individual
___
####OutMigration
(child of AuditableCollectedEntity)
#####What is it?
An OutMigration models the migration of an Individual to outside the area of study.
######Dependencies
+ FieldWorker
+ Visit
+ Residency
+ Individual 
######Required Fields:
+ Visit visit
+ Date migrationDate
+ Residency residency
+ Individual individual
___
####PregnancyObservation
(child of AuditableCollectedEntity)
#####What is it?
A PregnancyObservation models a FieldWorker’s observation of a pregnant Individual.
######Dependencies
+ FieldWorker
+ Visit
+ Individual
######Required Fields:
+ Visit visit
+ Date expectedDeliveryDate
+ Date pregnancyDate
+ Individual mother
___
####PregnancyOutcome
(child of AuditableCollectedEntity)
#####What is it?
A PregnancyOutcome models the outcome of an individual’s pregnancy.
######Dependencies
+ FieldWorker
+ Visit
+ Individual
######Required Fields:
+ Visit visit
+ Date outcomeDate
+ int childrenBorn
+ Individual mother
___
####PregnancyResult
(child of AuditableCollectedEntity)
#####What is it?
A PregnancyResult models the separate results of a larger PregnancyOutcome for an Individual’s pregnancy. 
######Dependencies
+ FieldWorker
+ Visit
+ Individual
######Required Fields:
+ Visit visit
+ String type
+ Individual child
