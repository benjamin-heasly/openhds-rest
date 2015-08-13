# Registration Use Cases

Here are descriptions of several important use casees for **openhds-rest**.

Each one includes:

 - a little "narrative", or context about what a user is trying to accomplish
 - required and optional information user prodives in the registration
 - what happens to the registered entity
 - side-effects of the registration on other entities

After each registration, the user would be able to do queries to confirm expected effects and side-effects.  These queries are not discussed here.

These are all "happy path" scenarios.  They don't include things like authentication or validation errors.

*TODO: for usage details, see the integration test that corresponds to each of these use cases.*

# Simple Registrations
These are straightforward registrations with minimal side-effects.  These would support arbitrary entity creation or updates to existing entities.

Every entity in **openhds-rest** supports a similar simple regitration.  The examples below are representative.

## Location
A FieldWorker is conducting a census and records a new Location, or updates an existing Location.

The registration must include the Location itself, the uuid of the Location's LocationHierarchy, the uuid of the FieldWorker conducting the census, and the date-time of the registration event.

The Location will be associated with the given LocationHierarchy, and persisted.

There are no side-effects on other entities.

## Simple Individual
A FieldWorker is conducting a census and records a new Individual who is not part of a household, or updates an existing Individual.

The registration must include the Individual herself, the uuid of the FieldWorker conducting the census, and the date-time of the registration.  The registration may include the uuid of the Individual's mother and/or the uuid of the Individual's father.

The Individual will be associated with the given mother and father, and persisted.

There are no side-effects on other entities.

## Relationship
A FieldWorker is conducting a census and records a new non-household Relationship between two Individuals, or updates an existing Relationship.

The registration must include the Relationship itself, two Individuals A and B who are part of the Relationship, the uuid of the FieldWorker conducting the census, and the date-time of the registration.

The Relationhsip will be associated with the given Individuals A and B, and persisted.

There are no side-effects on other entities.

## Visit
A FieldWorker is recording demographic updates and records a new Visit at an existing Location, or updating an existing Visit.

The registration must include the Visit itself, the uuid of the Location visited, the uuid of the FieldWorker conducting the update, and the date-time of the registration.

The Visit will be associated with the given Location, and persisted.

There are no side-effects on other entities.

## Pregnancy Observation
A FieldWorker is recording demographic updates and records that a known Indivudal is pregnant, or updating an existing PregnancyObservation.

The registration must include the PregnancyObservation itself, the uuid of the Visit during which the pregnancy was observed, the uuid of the Individual who is pregnant, the uuid of the FieldWorker conducting the update, and the date-time of the registration.

The PregnancyObservation will be associated with the gien Visit and mother Individual, and persisted.

There are no side-effects on other entities.

## Pregnancy Outcome
A FieldWorker is recording demographic updates, and records that a pregnant mother has given birth, or her pregnancy otherwise ended, or is updating an existing PregnancyOutcome.

Note: the PregnancyOutcome records the end of the pregnancy itself, not any children born.  See PregnancyResult.

The registration must include the PregnancyOutcome itself, the uuid of the Visit during which the outcome was observerd, the uuid of the Individual who was pregnant, and the uuid of the FieldWorker conducting the update, and the date-time of the registration.  The registration may include the uuid of the father.

The PregnancyOutcome will be associated with the given Visit, mother, and father, and persisted.

There are no side-effects on other entities.

## User
An administrator is creating a new User who may use the REST API, or updating an existing User.

The registration must include the User herself.

The User will be persisted.

There are no side-effects on other entities.

## Project Code
An administrator is configuring a new **openhds-rest** deployment and setting up project-specific data codes.

the registration must include the ProjectCode itself.

The ProjectCode will be persisted.

There are no side-effects on other entities.

# Complex Registrations
These are compound registrations that have significant side-effects on multiple entities.  These should support common operations for demographic surveillance like initial census and demographic updates.

## Household Individual
A FieldWorker is conducting a census and records an Individual who is part of a household, or updates an Individual's household registration.

The registration must include the Individual herself.

The registration must also include the uuids of several related entities:

 - the Individual who is the head of the household
 - the Location where members of the household reside
 - the SocialGroup representing the household itself
 - the FieldWorker conducting the census

The registration may include the uuids of several other related entities:

 - the Individual who is her mother
 - the Individual who is her father
 - her Relationship to the head of the household
 - her Membership in the household SocialGroup
 - her Residency at the household Location
 
The registration must also include some additional information:

 - the date-time of the registration
 - the type of Relationship between the Individual and the Individual who is the head of the household
 
The registered Individual will be associated with the given mother and father, and persisted.

This registration will cause several side-effects:

 - A Relationship will be created or updated, between the registered Individual and the head of the household.  The Relationship will have the given type and the given uuid, if it was provided.  The Relationship start type will be `individualRegistration` and the start date will be the given date-time of the registration.
 - A Membership will be created or updated, for the registered Individual in the household SocialGroup.  The Membership will have the given uuid, if it was provided.  The Membership start type will be `individualRegistration` and the start date will be the given date-time of the registration.
 - A Residency will be created or updated, for the registered Individual at the household Location.  The Residency will have the given uuid, if it was provided.  The Residency start type will be `individualRegistration` and the start date will be the given date-time of the registration.

## Pregnancy Result
A FieldWorker is recording demographic updates, and records a birth or other result related to the end of a pregnancy, or is updating an existing PregnancyResult. 

Note: the PregnancyResult records children born and other results from a pregnancy, not the end of the pregnancy itself.  See PregnancyOutcome.

The registration must include the PregnancyResult itself, the uuid of the PregnancyOutcome associated with the same pregnancy, the uuid of the FieldWorker conducting the update, and the date-time of the registration.

The registration may contain the uuid of a child Individual who was born, the uuid of the child's household Residency, and the uuid of the child's household Membership.

The PregnancyResult will be associated with the given PregnancyOutcome and child, and persisted.

For live births only, the registration will have several side-effects:

 - an Individual will be created or updated, for the child who was born.  The child's name and other fields will be taken from the registered PregnanchResult.   The child will be associated with the mother and father recorded with the given PregnancyOutcome.  The child will have the given uuid, if it was provided.
 - A Membership will be created or updated, for the child in the mother's household SocialGroup.  The Membership will have the given uuid, if it was provided.  The Membership start type will be `birth` and the start date will be the given date-time of the registration.
 - A Residency will be created or updated, for the registered Individual at the mother's household Location.  The Residency will have the given uuid, if it was provided.  The Residency start type will be `birth` and the start date will be the given date-time of the registration.

## Death
A FieldWorker is recording demographic updates, and records that an individual has died, or us updating an existing Death record.

The registration must include the Death itself, the uuid of the Individual who died the uuid of the Visit during which the Death was recorded, the uuid of the FieldWorker conducting the update, and the date-time of the registration.

The Death will be associated with the given Individual and Visit, and persisted.

The registration will have several side-effects for the Individual who died:

 - Any Residencies, Memberships, and Relationships associated with the Individual will be terminated with end type `death` and end date the date-time of the registration.
 - If there is a PregnancyObservation associated with the Individual, and no later PregnancyOutcome, a PregnancyOutcome will be created or updated, with no associated PregnancyResults.  The outcomeDate will be the date of Death.

## Out Migration
A FieldWorker is recording demographic updates and records that an Individual's recorded Residency has ended, or is updating an existing OutMigration.

The registration must include the OutMigration itself, the uuid of the Visit when the OutMigration was recorded, the uuid of the FieldWorker conducting the update, and the date-time of the registration.   The registration must include either the uuid of the Individual who is migrating or the uuid of the ended Residency.  If only the Individual is provided, then Individual's current (latest recorded) Residency will be used.

The OutMigration will be associated with the given Individual, Residency, and Visit, and persisted.

The registration will have a side-effect on the given (or current) Residency: it will be terminated with end type `outMigration` and end date taken from the OutMigration.

## In Migration
A FieldWorker is recording demographic updates and records that an Individual has begun a Residency at a new Location, or is updating an existing InMigration.

The registration must include the InMigration itself, the uuid of the Individual who has migrated, the uuid of the Location where the Individual has begun living, the uuid of the Visit when the InMigration was recorded, the uuid of the FieldWorker conducting the update, and the date-time of the registration.  The registration may include the uuid of the Individual's new Residency.

The InMigration will be associated with the given Individual, Location, and Visit, and persisted.

The registration will have a side effect: a Residency will be created or updated, for the given Individual at the given Location.  The Residency will have the given uuid, if it was provided.  The Residency start type will be `inMigration` and the start date will be taken from the InMigration.

## Household Migration
A FieldWorker is recording demographic updates and records that an entire household has moved to a new Locaiton, or is updating an existing household migration.

The registration must include the uuid of the household SocialGroup, the uuid of the Location where the household now lives, the uuid of the Visit when the InMigration was recorded, the uuid of the FieldWorker conducting the update, and the date-time of the registration.

This registration is not persisted, but has repeated side-effects.  For each Individual who has a Memebership in the given household SocialGroup:

 - The Individual will experience the side-effects of an OutMigration from her current Residency.
 - The Individual will experience the side-effects of an InMigration to the given Location.
