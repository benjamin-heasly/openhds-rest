package org.openhds.repository.generator;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.contract.AuditableEntity;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.*;
import org.openhds.domain.model.update.*;
import org.openhds.errors.model.Error;
import org.openhds.errors.model.ErrorLog;
import org.openhds.events.model.Event;
import org.openhds.events.model.EventMetadata;
import org.openhds.repository.concrete.*;
import org.openhds.repository.concrete.census.*;
import org.openhds.repository.concrete.update.*;
import org.openhds.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by Ben on 5/18/15.
 * <p>
 * Initialize the db with some OpenHDS sample objects.
 */
@Component
public class SampleDataGenerator {

    @Autowired
    private RequiredDataGenerator requiredDataGenerator;

    @Autowired
    private LocationDataGenerator locationDataGenerator;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FieldWorkerRepository fieldWorkerRepository;

    @Autowired
    private IndividualRepository individualRepository;

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private ResidencyRepository residencyRepository;

    @Autowired
    private ErrorRepository errorRepository;

    @Autowired
    private ErrorLogRepository errorLogRepository;

    @Autowired
    private EventMetadataRepository eventMetadataRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SocialGroupRepository socialGroupRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private DeathRepository deathRepository;

    @Autowired
    private InMigationRepository inMigationRepository;

    @Autowired
    private OutMigationRepository outMigationRepository;

    @Autowired
    private PregnancyObservationRepository pregnancyObservationRepository;

    @Autowired
    private PregnancyOutcomeRepository pregnancyOutcomeRepository;

    @Autowired
    private PregnancyResultRepository pregnancyResultRepository;

    public void clearData() {
        eventMetadataRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();

        errorRepository.deleteAllInBatch();
        errorLogRepository.deleteAllInBatch();

        deathRepository.deleteAllInBatch();

        inMigationRepository.deleteAllInBatch();
        outMigationRepository.deleteAllInBatch();

        pregnancyResultRepository.deleteAllInBatch();

        pregnancyOutcomeRepository.deleteAllInBatch();

        pregnancyObservationRepository.deleteAllInBatch();

        visitRepository.deleteAllInBatch();

        residencyRepository.deleteAllInBatch();

        membershipRepository.deleteAllInBatch();

        relationshipRepository.deleteAllInBatch();

        socialGroupRepository.deleteAllInBatch();

        individualRepository.deleteAllInBatch();

        locationDataGenerator.clearData();
        requiredDataGenerator.clearData();
    }

    public void generateSampleData() {
        requiredDataGenerator.generateData();
        locationDataGenerator.generateData(1);

        addVisit("visit-a", "location-1");
        addVisit("visit-b", "location-2");
        addVisit("visit-c", "location-3");

        addIndividual("individual-a");
        addIndividual("individual-b");
        addIndividual("individual-c");

        addRelationship("relationship-type-a", "individual-a", "individual-b");
        addRelationship("relationship-type-b", "individual-c", "individual-a");
        addRelationship("relationship-type-c", "individual-c", "individual-c");

        addSocialGroup("social-group-a");
        addSocialGroup("social-group-b");
        addSocialGroup("social-group-c");

        addMembership("memberhip-type-a", "individual-a", "social-group-a");
        addMembership("memberhip-type-b", "individual-b", "social-group-b");
        addMembership("memberhip-type-c", "individual-c", "social-group-c");

        addResidency("resdoncy-toop-a", "individual-a", "location-1");
        addResidency("resdoncy-toop-b", "individual-b", "location-2");
        addResidency("resdoncy-toop-c", "individual-c", "location-3");

        addDeath("death-a", "individual-a", "visit-a");
        addDeath("death-b", "individual-b", "visit-b");

        addInMigration("migration-a");
        addInMigration("migration-b");

        addOutMigration("migration-a");
        addOutMigration("migration-b");

        addPregnancyObservation("individual-a");

        addPregnancyOutcome("individual-a", "individual-b", "visit-a");

        addPregnancyResult("birth", "individual-c");

        addPregnancyResult("stillborn", null);

        addErrorLog("sample error");

        addEvent("sample event", "sample system");
    }

    private void initAuditableFields(AuditableEntity auditableEntity) {
        User user = userRepository.findAll().get(0);
        auditableEntity.setInsertBy(user);
        auditableEntity.setInsertDate(ZonedDateTime.now());
        auditableEntity.setLastModifiedBy(user);
        auditableEntity.setLastModifiedDate(auditableEntity.getInsertDate());
        auditableEntity.setUuid(UUID.randomUUID().toString());
    }

    private void initCollectedFields(AuditableCollectedEntity auditableCollectedEntity) {
        FieldWorker fieldWorker = fieldWorkerRepository.findAll().get(0);
        auditableCollectedEntity.setCollectedBy(fieldWorker);
        auditableCollectedEntity.setCollectionDateTime(ZonedDateTime.now());
    }

    private void addFieldWorker(String name, String password) {
        FieldWorker fieldWorker = new FieldWorker();
        initAuditableFields(fieldWorker);

        fieldWorker.setFirstName(name);
        fieldWorker.setLastName(name);
        fieldWorker.setFieldWorkerId(name);
        fieldWorker.setPassword(password);
        fieldWorker.setPasswordHash(password);
        fieldWorkerRepository.save(fieldWorker);
    }

    private void addErrorLog(String description) {
        ErrorLog errorLog = new ErrorLog();
        initAuditableFields(errorLog);
        initCollectedFields(errorLog);

        errorLog.setDataPayload(description);
        Error error = new Error();
        error.setErrorMessage(description);
        errorLog.getErrors().add(error);

        errorLogRepository.save(errorLog);
    }

    private void addEvent(String description, String system) {
        Event event = new Event();
        initAuditableFields(event);

        event.setActionType(Event.DEFAULT_ACTION);
        event.setEntityType(Event.DEFAULT_ENTITY);
        event.setEventData(description);

        EventMetadata defaultMetadata = new EventMetadata();
        defaultMetadata.setSystem(Event.DEFAULT_SYSTEM);
        defaultMetadata.setStatus(Event.DEFAULT_STATUS);
        event.getEventMetadata().add(defaultMetadata);

        eventRepository.save(event);
    }

    private void addIndividual (String name){
        Individual individual = new Individual();
        initAuditableFields(individual);
        initCollectedFields(individual);
        individual.setExtId(name);
        individual.setFirstName(name);
        individual.setMiddleName(name);
        individual.setLastName(name);
        individual.setDateOfBirth(ZonedDateTime.now().minusYears(1));

        individualRepository.save(individual);
    }

    private void addSocialGroup(String name) {
        SocialGroup socialGroup = new SocialGroup();
        initAuditableFields(socialGroup);
        initCollectedFields(socialGroup);

        socialGroup.setExtId(name);
        socialGroup.setGroupName(name);
        socialGroupRepository.save(socialGroup);
    }

    private void addRelationship (String relationshipType, String individualAName, String individualBName){
        Relationship relationship = new Relationship();
        initAuditableFields(relationship);
        initCollectedFields(relationship);
        relationship.setStartDate(ZonedDateTime.now().minusYears(1));
        relationship.setRelationshipType(relationshipType);
        relationship.setIndividualA(individualRepository.findByExtId(individualAName).get(0));
        relationship.setIndividualB(individualRepository.findByExtId(individualBName).get(0));

        relationshipRepository.save(relationship);
    }

    private void addMembership(String type, String individualName, String socialGroupName) {
        Membership membership = new Membership();
        initAuditableFields(membership);
        initCollectedFields(membership);

        membership.setIndividual(individualRepository.findByExtId(individualName).get(0));
        membership.setSocialGroup(socialGroupRepository.findByExtId(socialGroupName).get(0));
        membership.setRelationshipToGroupHead(type);
        membership.setStartDate(ZonedDateTime.now().minusYears(1));
        membership.setStartType(type);

        membershipRepository.save(membership);
    }

    private void addResidency(String type, String individualName, String locationName) {
        Residency residency = new Residency();
        initCollectedFields(residency);
        initAuditableFields(residency);

        residency.setIndividual(individualRepository.findByExtId(individualName).get(0));
        residency.setLocation(locationRepository.findByExtId(locationName).get(0));
        residency.setStartType(type);
        residency.setStartDate(ZonedDateTime.now().minusYears(1));

        residencyRepository.save(residency);
    }

    private void addVisit(String name, String locationName) {
        Visit visit = new Visit();
        initAuditableFields(visit);
        initCollectedFields(visit);

        visit.setExtId(name);
        visit.setLocation(locationRepository.findByExtId(locationName).get(0));
        visit.setVisitDate(ZonedDateTime.now());

        visitRepository.save(visit);
    }

    private void addDeath(String name, String individualName, String visitName) {
        Death death = new Death();
        initAuditableFields(death);
        initCollectedFields(death);

        death.setIndividual(individualRepository.findByExtId(individualName).get(0));
        death.setDeathCause(name);
        death.setDeathPlace(name);
        death.setDeathDate(ZonedDateTime.now().minusYears(1));
        death.setVisit(visitRepository.findByExtId(visitName).get(0));

        deathRepository.save(death);
    }

    private void addInMigration(String name) {
        InMigration inMigration = new InMigration();
        initAuditableFields(inMigration);
        initCollectedFields(inMigration);

        inMigration.setVisit(visitRepository.findAll().get(0));
        inMigration.setIndividual(individualRepository.findAll().get(0));
        inMigration.setResidency(residencyRepository.findAll().get(0));
        inMigration.setMigrationDate(ZonedDateTime.now().minusYears(1));
        inMigration.setOrigin(name);
        inMigration.setReason(name);
        inMigration.setMigrationType(name);

        inMigationRepository.save(inMigration);
    }

    private void addOutMigration(String name) {
        OutMigration outMigration = new OutMigration();
        initAuditableFields(outMigration);
        initCollectedFields(outMigration);

        outMigration.setVisit(visitRepository.findAll().get(0));
        outMigration.setIndividual(individualRepository.findAll().get(0));
        outMigration.setResidency(residencyRepository.findAll().get(0));
        outMigration.setMigrationDate(ZonedDateTime.now().minusYears(1));
        outMigration.setDestination(name);
        outMigration.setReason(name);

        outMigationRepository.save(outMigration);
    }

    private void addPregnancyObservation(String motherName) {
        PregnancyObservation pregnancyObservation = new PregnancyObservation();
        initAuditableFields(pregnancyObservation);
        initCollectedFields(pregnancyObservation);

        pregnancyObservation.setVisit(visitRepository.findAll().get(0));
        pregnancyObservation.setMother(individualRepository.findByExtId(motherName).get(0));
        pregnancyObservation.setExpectedDeliveryDate(ZonedDateTime.now().plusMonths(5));
        pregnancyObservation.setPregnancyDate(ZonedDateTime.now().minusMonths(5));

        pregnancyObservationRepository.save(pregnancyObservation);
    }

    private void addPregnancyOutcome(String motherName, String fatherName, String visitName){
        PregnancyOutcome pregnancyOutcome = new PregnancyOutcome();
        initAuditableFields(pregnancyOutcome);
        initCollectedFields(pregnancyOutcome);

        pregnancyOutcome.setVisit(visitRepository.findAll().get(0));
        pregnancyOutcome.setMother(individualRepository.findByExtId(motherName).get(0));
        pregnancyOutcome.setFather(individualRepository.findByExtId(fatherName).get(0));
        pregnancyOutcome.setChildrenBorn(1);
        pregnancyOutcome.setOutcomeDate(ZonedDateTime.now().minusYears(1));
        pregnancyOutcomeRepository.save(pregnancyOutcome);
    }

    private void addPregnancyResult(String type, String individualName){
        PregnancyResult pregnancyResult = new PregnancyResult();
        initAuditableFields(pregnancyResult);
        initCollectedFields(pregnancyResult);

        if(null != individualName)
            pregnancyResult.setChild(individualRepository.findByExtId(individualName).get(0));

        pregnancyResult.setType(type);
        pregnancyResult.setPregnancyOutcome(pregnancyOutcomeRepository.findAll().get(0));

        pregnancyResultRepository.save(pregnancyResult);
    }
}
