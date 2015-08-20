What is the OpenHDS?


It is the Open Health and Demographic System. It is an intuitive and easy way for you to record populations and their
changes over time. The OpenHDS logically breaks down the geography of your area of study and allows you to capture
the major demographic events within it.

The OpenHDS view of the world:
The Hierarchy

The first thing to understand is the way the OpenHDS represents the area of study for your project.
If you have ever written a letter, you’ve done most of the work already. For every geographical location
on Earth there exists some way to represent that location hierarchically, much like we do when writing a
letter to someone. In general, you start from the most specific description of the location and slowly
become more general until the address you’re sending the letter to is well enough understood by the
postal service to make sense of and deliver the letter. The OpenHDS has a similar view of the world.
The regions, districts, prefectures, cities, etc that comprise the area of study are each represented
in the OpenHDS by an entity called LocationHierarchy. There is one “Root” LocationHierarchy that represents
the largest geographical denomination for the area of study. Underneath the “Root” LocationHierarchy is a
LocationHierarchy for every other geographical denomination each of which points to their ‘parent’ which
forms a tree-like structure that represents the entire area of study. The easiest way to understand this
is with a picture using a real world example…

![Figure 1](/documentation/figure-1.png)

#Census Entities
####LocationHierarchy
(child of `AuditableExtIdEntity`)
#####What is it?
`LocationHierarchy` is a node in the tree that represents the study area. For example, at the top of a tree would be the The United States and it would have a child node for each of the 50 states. Each state node could have a child node for its cities and each city could have a child node for its districts and so on. `LocationHierarchy` extends `AuditableExtIdEntity` meaning it is identified by uuid as well as extId. 
#####Dependencies
+ FieldWorker
+ LocationHierarchy
+ LocationHierarchyLevel

######Required Fields:
+ String name
+ String extId
+ LocationHierarchy parent
+ LocationHierarchyLevel level

___
####LocationHierarchyLevel
(child of `AuditableEntity`)
#####What is it?
The levels of the `LocationHierarchy` tree are defined separately as `LocationHierarchyLevel`s and referenced by each instance of `LocationHierarchy`. From the previous example, the list of `LocationHierarchyLevel`s would be Country, State, City, District.
######Required Fields:
+ int keyIdentifier
+ String name

___
####Location
(child of `AuditableExtIdEntity`)
#####What is it?
`Location` is the leaf node on the `LocationHierarchy` tree, it is the smallest geographical unit within a study area. From the previous example for `LocationHierarchy`, each building within a district could be modeled with `Location`. `Individual`s are residents at `Locations`.
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
(child of `AuditableExtIdEntity`)
#####What is it?
An `Individual` (person) within the study area that resides at a `Location`, is a member of a `SocialGroup`, and has a relationship to the head of a household.
######Dependencies
+ FieldWorker

######Required Fields:
+ String extId
+ String gender
+ Date dateOfBirth
+ String firstName

___
####SocialGroup
(child of `AuditableExtIdEntity`)
#####What is it?
A `SocialGroup` is any cultural or societal group or collective that `Individual`s are a member of, e.g. a church group, a household/family, a bowling team.
######Dependencies
+ FieldWorker

######Required Fields:
+ String groupName
+ String groupType
+ String extId

___
###Membership
(child of `AuditableCollectedEntity`)
####What is it?
A `Membership` models the relationship between an `Individual` and a `SocialGroup`.
######Dependencies
+ FieldWorker
+ Individual
+ SocialGroup

######Required Fields:
+ Date startDate
+ String startType
+ Individual individual
+ SocialGroup socialGroup

___
####Relationship
(child of `AuditableCollectedEntity`)
#####What is it?
A `Relationship` models the relationship between two `Individual`s. Specifically it models the relationship between an `Individual` and the head of household that the `Individual` is a part of.
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
(child of `AuditableCollectedEntity`)
#####What is it?
A `Residency` models the relationship between an `Individual` and a `Location`.
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
#Update Entities
####Visit
(child of `AuditableExtIdEntity`)
#####What is it?
A `Visit` models a `FieldWorker`’s follow-up visit to a particular `Location` at a particular date. `Visit`s are necessary for all “update” events that can take place within a population.
######Dependencies
+ FieldWorker
+ Location 

######Required Fields:
+ Date visitDate
+ Location location

___
####Death
(child of `AuditableCollectedEntity`)
#####What is it?
A `Death` models the death of one of the `Individual`s within the area of study.
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
(child of `AuditableCollectedEntity`)
#####What is it?
An `InMigration` represents a migration between two `Location`s where the destination
`Location` is still inside the area of study. The origin `Location` can be within or outside the area of study and is model by the migrationType value of internal or external respectively.
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
(child of `AuditableCollectedEntity`)
#####What is it?
An `OutMigration` models the migration of an `Individual` to outside the area of study.
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
(child of `AuditableCollectedEntity`)
#####What is it?
A `PregnancyObservation` models a `FieldWorker`’s observation of a pregnant `Individual`.
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
A `PregnancyOutcome` models the outcome of an `Individual`’s pregnancy.
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
A `PregnancyResult` models the separate results of a larger `PregnancyOutcome` for an `Individual`’s pregnancy. 
######Dependencies
+ FieldWorker
+ Visit
+ Individual

######Required Fields:
+ Visit visit
+ String type
+ Individual child

