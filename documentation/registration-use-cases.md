# Registration Use Cases

Here are descriptions of several important use casees for openhds-rest.

Each one includes:
 - a little "narrative", or context about what a user is trying to accomplish
 - information the user prodives in the registration
 - what happens to the registered entity
 - side effects of the registration on other entities

After each registration, the user would be able to do queries to confirm expected effects and side-effects.  These queries are not discussed here.

These are all "happy path" scenarios.  They don't include things like authentication or validation errors.

# Simple Registrations
These are straightforward registrations with minimal side-effects.  These would support arbitrary entity creation or updates to existing entities.

Every entity in openhds-rest supports a similar simple regitration.  The examples below are representative.

## Location
A FieldWorker is conducting a census and records a new Location, or updates an existing Location.

The registration must include the Location itself, the uuid of the Location's LocationHierarchy, the uuid of the FieldWorker conducting the census, and the date-time of the registration event.

The new Location will be associated with the given LocationHierarchy, and persisted.

There are no side-effects on other entities.

## Individual (Simple)
A FieldWorker is conducting a census and records a new Individual who is not part of a household, or updates an existing Individual.

The registration must include the Individual herself, the uuid of the FieldWorker conducting the census, and the date-time of the registration.  The registration may include the uuid of the Individual's mother and/or the uuid of the Individual's father.

The new Individual will be associated with the given mother and father, and persisted.

There are no side-effects on other entities.

## Relationship

## User

## Site Code

## Visit

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
