#Domain Documentation

This is a detailed description of the OpenHDS entities.

For a general description of the OpenHDS go [here](the-openhds-overview.md).

For a description of the OpenHDS registration use cases go [here](registration-use-cases.md).

Each section below has a brief description of the entity, the other entities it is dependent on, and a list of its required fields.

+ [Census Entities](#census-entities)
	+ [LocationHierarchy](#locationhierarchy)
	+ [LocationHierarchyLevel](#locationhierarchylevel)
	+ [Location](#location)
	+ [Individual](#individual)
	+ [SocialGroup](#socialgroup)
	
+ [Relation Entities](#relation-entities)
	+ [Membership](#membership)
	+ [Relationship](#relationship)
	+ [Residency](#residency)
	
+ [Update Entities](#update-entities)
	+ [Visit](#visit)
	+ [Death](#death)
	+ [InMigration](#inmigration)
	+ [OutMigration](#outmigration)
	+ [PregnancyObservation](#pregnancyobservation)
	+ [PregnancyOutcome](#pregnancyoutcome)
	+ [PregnancyResult](#pregnancyresult)

<br>
---
<br>

#Census Entities
<a id=“census-entities”></a>
<a id=“locationhierarchy”></a>
###LocationHierarchy
#####What is it?
`LocationHierarchy` is a node in the tree that represents the study area. For example, at the top of a tree would be the The United States and it would have a child node for each of the 50 states. Each state node could have a child node for its cities and each city could have a child node for its districts and so on. 

######Required Fields:
|Type | Name |
|-----|------|
`String`| uuid
`Date`| collectionDateTime
`FieldWorker`| collectedBy 
`String`| name  
`String`| extId
[`LocationHierarchy`](#locationhierarchy)| parent
[`LocationHierarchyLevel`](#locationhierarchylevel)| level


<br>
###LocationHierarchyLevel
<a id=“locationhierarchylevel”></a>
#####What is it?
The levels of the `LocationHierarchy` tree are defined separately as `LocationHierarchyLevel`s and referenced by each instance of `LocationHierarchy`. From the previous example, the list of `LocationHierarchyLevel`s would be Country, State, City, District.

######Required Fields:
|Type | Name |
|-----|------|
`String`| uuid
`int`| keyIdentifier
`String`| name 

<br>
###Location
<a id=“location”></a>
#####What is it?
`Location` is the leaf node on the `LocationHierarchy` tree, it is the smallest geographical unit within a study area. From the previous example for `LocationHierarchy`, each building within a district could be modeled with `Location`.

######Required Fields:
|Type |  Name 
|-----|------|
`String`| uuid	
`Date`| collectionDateTime 
`FieldWorker`| collectedBy
`String`| extId 
`String`| name
`String`| locationType
[`LocationHierarchy`](#locationhierarchy)| parent

<br>
###Individual
<a id=“individual”></a>
#####What is it?
An `Individual` (person) within the study area that resides at a `Location`, is a member of a `SocialGroup`.

######Required Fields:
|Type | Name |
|-----|------|
`String`| uuid
`Date`| collectionDateTime
`FieldWorker`| collectedBy
`String`| extId
`String`| gender
`Date` | dateOfBirth
`String`| firstName

<br>
###SocialGroup
<a id=“socialgroup”></a>
#####What is it?
A `SocialGroup` is any cultural or societal group or collective that `Individual`s are a member of, e.g. a church group, a household/family, a bowling team.
######Dependencies
+ FieldWorker

######Required Fields:
|Type | Name |
|-----|------|
`String`| uuid	
`Date`| collectionDateTime
`FieldWorker`| collectedBy
`String`| extId
`String`| groupName
`String`| groupType

<br>
#Relation Entities
<a id=“relation-entities”></a>
###Membership
<a id=“membership”></a>
####What is it?
A `Membership` models the connection between an `Individual` and a `SocialGroup`.

######Required Fields:
|Type | Name |
|-----|------|
`String`| uuid	
`Date`| collectionDateTime
`FieldWorker`| collectedBy
`Date`| startDate
`String`| startType
[`Individual`](#individual)| individual
[`SocialGroup`](#socialgroup)| socialGroup

<br>
###Relationship
<a id=“relationship”></a>
#####What is it?
A `Relationship` models the way two Individual’s are related. This can be anything from being someone’s pastor to being their mother.

######Required Fields:
|Type | Name |	
|-----|------|
`String`| uuid	
`Date`| collectionDateTime
`FieldWorker`| collectedBy
`Date`| startDate
`String`| relationshipType
[`Individual`](#individual)| individualA
[`Individual`](#individual)| individualB

<br>
###Residency
<a id=“residency”></a>
#####What is it?
A `Residency` models the connection between an `Individual` and a `Location`.

######Required Fields:
|Type | Name |
|-----|------|
`String`| uuid
`Date`| collectionDateTime
`FieldWorker`| collectedBy
`Date`| startDate
`String`| startType
[`Individual`](#individual)| individual
[`Location`](#location)| location

<br>
#Update Entities
<a id=“update-entities”></a>
###Visit
<a id=“visit”></a>
#####What is it?
A `Visit` models a `FieldWorker`’s follow-up visit to a particular `Location` at a particular date. `Visit`s are necessary for all update events that can take place within a population.

######Required Fields:
|Type | Name | 
|-----|------|
`String`| uuid
`Date`| collectionDateTime
`FieldWorker`| collectedBy
`String`| extId
`Date`| visitDate
[`Location`](#location)| location

<br>
###Death
<a id=“death”></a>
#####What is it?
A `Death` is record of the death of one of the `Individual`s within the area of study.

######Required Fields:
|Type | Name |
|-----|------|
`String`| uuid
`Date`| collectionDateTime
`FieldWorker`| collectedBy
`Date`| deathDate
[`Visit`](#visit)| visit
[`Individual`](#individual)| individual

<br>
###InMigration
<a id=“inmigration”></a>
#####What is it?
An `InMigration` is a record of an Individual’s migration between two `Location`s where the destination `Location` is still inside the area of study. 

######Required Fields:
|Type | Name |
|-----|------|
`String`| uuid
`Date`| collectionDateTime
`FieldWorker`| collectedBy
`Date`| migrationDate
`String`| migrationType
[`Visit`](#visit)| visit
[`Residency`](#residency)| residency
[`Individual`](#individual)| individual

<br>
###OutMigration
<a id=“outmigration”></a>
#####What is it?
An `OutMigration` models the migration of an `Individual` to outside the area of study.
 
######Required Fields:
|Type | Name |
|-----|------|
`String`| uuid
`Date`| collectionDateTime
`FieldWorker`| collectedBy
`Date`| migrationDate
[`Visit`](#visit)| visit
[`Residency`](#residency)| residency
[`Individual`](#individual)| individual

<br>
###PregnancyObservation
<a id=“pregnancyobservation”></a>
#####What is it?
A `PregnancyObservation` is a record of a `FieldWorker`’s observation of a pregnant `Individual`.

######Required Fields:
|Type | Name |	
|-----|------|
`String`| uuid
`Date`| collectionDateTime
`FieldWorker`| collectedBy
`Date`| expectedDeliveryDate
`Date`| pregnancyDate
[`Visit`](#visit)| visit
[`Individual`](#individual)| mother

<br>
###PregnancyOutcome
<a id=“pregnancyoutcome”></a>
#####What is it?
A `PregnancyOutcome` is a record of the outcome of an `Individual`’s pregnancy.

######Required Fields:
|Type | Name |	
|-----|------|
`String`| uuid	
`Date`| collectionDateTime
`FieldWorker`| collectedBy
`Date`| outcomeDate
[`Visit`](#visit)| visit
[`Individual`](#individual)| mother

<br>
###PregnancyResult
<a id=“pregnancyresult”></a>
#####What is it?
A `PregnancyResult` is a record of the separate results of a larger `PregnancyOutcome` for an `Individual`’s pregnancy. 

######Required Fields:
|Type | Name |
|-----|------|
`String`| uuid
`Date`| collectionDateTime
`FieldWorker`| collectedBy
`String`| type
[`Visit`](#visit)| visit
[`PregnancyOutcome`](#pregnancyoutcome)| pregnancyOutcome 

