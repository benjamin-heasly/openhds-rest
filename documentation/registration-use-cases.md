# Registration Use Cases

Here are descriptions of several important use casees for **openhds-rest**.

Each one includes:
 - a little "narrative", or context about what a user is trying to accomplish
 - information the user prodives in the registration
 - what happens to the registered entity
 - side effects of the registration on other entities

After each registration, the user would be able to do queries to confirm expected effects and side-effects.  These queries are not discussed here.

These are all "happy path" scenarios.  They don't include things like authentication or validation errors.

TODO: for usage details, see the integration test that corresponds to each of these use cases.

# Simple Registrations
These are straightforward registrations with minimal side-effects.  These would support arbitrary entity creation or updates to existing entities.

Every entity in **openhds-rest** supports a similar simple regitration.  The examples below are representative.

## Location
A FieldWorker is conducting a census and records a new Location, or updates an existing Location.

The registration must include the Location itself, the uuid of the Location's LocationHierarchy, the uuid of the FieldWorker conducting the census, and the date-time of the registration event.

The Location will be associated with the given LocationHierarchy, and persisted.

There are no side-effects on other entities.

## Individual (Simple)
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

## Individual (Household)
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
 - a Relationship will created or updated, between the registered Individual and the head of the household.  The Relationship will have the given type and the given uuid, if it was provided.  The Relationship start type will be `individualRegistration` and the start date will be the given date-time of the registration.
 - a Membership will created or updated, for the registered Individual in the household SocialGroup.  The Membership will have the given uuid, if it was provided.  The Membership start type will be `individualRegistration` and the start date will be the given date-time of the registration.
 - a Residency will created or updated, for the registered Individual at the household Location.  The Residency will have the given uuid, if it was provided.  The Residency start type will be `individualRegistration` and the start date will be the given date-time of the registration.

## Pregnancy Result
A FieldWorker is recording demographic updates, and records a birth or other result related to the end of a pregnancy, or is updating an existing PregnancyResult. 

Note: the PregnancyResult records children born and other results from a pregnancy, not the end of the pregnancy itself.  See PregnancyOutcome.

The registration must include the PregnancyResult itself, the uuid of the PregnancyOutcome associated with the same pregnancy, the uuid of the FieldWorker conducting the update, and the date-time of the registration.

The registration may contain the uuid of a child Individual who was born, the uuid of the child's household Residency, and the uuid of the child's household Membership.

The PregnancyResult will be associated with the given PregnancyOutcome and child, and persisted.

For live births only, the registration will have several side-effects:
 - 


## Death

## In-Migration

## Out-Migration

## Household Migration
