# Registration Use Cases

Here are descriptions of several important use casees for openhds-rest.

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

Every entity in openhds-rest supports a similar simple regitration.  The examples below are representative.

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

The registration must include the Visit itself, the uuid of the Location visited, the uuid of the FieldWorker conducting the census, and the date-time of the registration.

The Visit will be associated with the given Location, and persisted.

There are no side-effects on other entities.

## User
An administrator is creating a new User who may use the REST API, or updating an existing User.

The registration must include the User herself.

The User will be persisted.

There are no side-effects on other entities.

## Project Code
An administrator is configuring a new openhds-rest deployment and setting up project-specific data codes.

the registration must include the ProjectCode itself.

The ProjectCode will be persisted.

There are no side-effects on other entities.

# Complex Registrations
These are compound registrations that have significant side-effects on multiple entities.  These should support common operations for demographic surveillance like initial census and demographic updates.

## Individual (Household)

## Pregnancy Observation

## Pregnancy Outcome

## Pregnancy Result

## Death

## In-Migration

## Out-Migration

## Household Migration