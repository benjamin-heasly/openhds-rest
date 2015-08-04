package org.openhds.repository.generator;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.contract.AuditableEntity;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.update.*;
import org.openhds.errors.model.Error;
import org.openhds.errors.model.ErrorLog;
import org.openhds.events.model.Event;
import org.openhds.events.model.EventMetadata;
import org.openhds.repository.concrete.*;
import org.openhds.repository.concrete.census.IndividualRepository;
import org.openhds.repository.concrete.census.LocationRepository;
import org.openhds.repository.concrete.census.ResidencyRepository;
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
    private FamilyDataGenerator familyDataGenerator;

    @Autowired
    private IndividualRepository individualRepository;

    @Autowired
    private ResidencyRepository residencyRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FieldWorkerRepository fieldWorkerRepository;

    @Autowired
    private ErrorRepository errorRepository;

    @Autowired
    private ErrorLogRepository errorLogRepository;

    @Autowired
    private EventMetadataRepository eventMetadataRepository;

    @Autowired
    private EventRepository eventRepository;

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

        familyDataGenerator.clearData();
        locationDataGenerator.clearData();
        requiredDataGenerator.clearData();
    }

    public void generateSampleData() {
        requiredDataGenerator.generateData();
        locationDataGenerator.generateData(1);
        familyDataGenerator.generateData();

        addVisit("visit-a", "location-1");
        addVisit("visit-b", "location-2");
        addVisit("visit-c", "location-3");

        addDeath("death-a", "location-1-head", "visit-a");
        addDeath("death-b", "location-2-head", "visit-b");

        addInMigration("migration-a");
        addInMigration("migration-b");

        addOutMigration("migration-a");
        addOutMigration("migration-b");

        addPregnancyObservation("location-1-head");

        addPregnancyOutcome("location-1-head", "location-2-head");

        addPregnancyResult("birth", "location-3-head");

        addPregnancyResult("stillborn", null);

        addErrorLog("sample error");

        addEvent("sample event");
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

    private void addEvent(String description) {
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

    private void addPregnancyOutcome(String motherName, String fatherName){
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
