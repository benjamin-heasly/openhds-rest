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
    private UpdateDataGenerator updateDataGenerator;

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

    public void clearData() {
        eventMetadataRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();

        errorRepository.deleteAllInBatch();
        errorLogRepository.deleteAllInBatch();

        updateDataGenerator.clearData();
        familyDataGenerator.clearData();
        locationDataGenerator.clearData();
        requiredDataGenerator.clearData();
    }

    public void generateSampleData() {
        requiredDataGenerator.generateData();
        locationDataGenerator.generateData(1);
        familyDataGenerator.generateData();
        updateDataGenerator.generateData();

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
}
