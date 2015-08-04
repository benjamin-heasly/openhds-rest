package org.openhds.repository.generator;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.contract.AuditableEntity;
import org.openhds.errors.model.Error;
import org.openhds.errors.model.ErrorLog;
import org.openhds.events.model.Event;
import org.openhds.events.model.EventMetadata;
import org.openhds.repository.concrete.ErrorLogRepository;
import org.openhds.repository.concrete.ErrorRepository;
import org.openhds.repository.concrete.EventMetadataRepository;
import org.openhds.repository.concrete.EventRepository;
import org.openhds.security.model.User;
import org.openhds.service.impl.ErrorLogService;
import org.openhds.service.impl.EventService;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

/**
 * Created by bsh on 4 August 2015.
 * <p>
 * Generates miscellaneous data, including
 * Error, ErrorLog, Event, EventMetadata
 * <p>
 * For each entity, only inserts sample records if there are no records yet.
 * This behavior should support testing without messing up existing project data.
 * <p>
 * Creates one of each entity.
 */
@Component
public class MiscellaneousDataGenerator implements DataGenerator {

    private final EventService eventService;
    private final EventRepository eventRepository;
    private final EventMetadataRepository eventMetadataRepository;

    private final ErrorLogService errorLogService;
    private final ErrorLogRepository errorLogRepository;
    private final ErrorRepository errorRepository;

    private final FieldWorkerService fieldWorkerService;

    private final UserService userService;

    @Autowired
    public MiscellaneousDataGenerator(EventService eventService,
                                      EventRepository eventRepository,
                                      EventMetadataRepository eventMetadataRepository,
                                      ErrorLogService errorLogService,
                                      ErrorLogRepository errorLogRepository,
                                      ErrorRepository errorRepository,
                                      FieldWorkerService fieldWorkerService,
                                      UserService userService) {
        this.eventService = eventService;
        this.eventRepository = eventRepository;
        this.eventMetadataRepository = eventMetadataRepository;
        this.errorLogService = errorLogService;
        this.errorLogRepository = errorLogRepository;
        this.errorRepository = errorRepository;
        this.fieldWorkerService = fieldWorkerService;
        this.userService = userService;
    }

    @Override
    public void generateData(int size) {
        generateUnknowns();
        generateEvent("sample-event");
        generateErrorLog("sample-error");
    }

    @Override
    public void generateData() {
        generateData(0);
    }

    @Override
    public void clearData() {
        eventMetadataRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();
        errorRepository.deleteAllInBatch();
        errorLogRepository.deleteAllInBatch();
    }

    // trigger services to create unknown entities ahead of time, for predictable entity counts
    private void generateUnknowns() {
        eventService.getUnknownEntity();
        errorLogService.getUnknownEntity();
    }

    private void generateErrorLog(String description) {
        ErrorLog errorLog = new ErrorLog();
        setAuditableFields(errorLog);
        setCollectedFields(errorLog);

        errorLog.setDataPayload(description);

        Error error = new Error();
        error.setErrorMessage(description);
        errorLog.getErrors().add(error);

        errorLogRepository.save(errorLog);
    }

    private void generateEvent(String description) {
        Event event = new Event();
        setAuditableFields(event);

        event.setActionType(Event.DEFAULT_ACTION);
        event.setEntityType(Event.DEFAULT_ENTITY);
        event.setEventData(description);

        EventMetadata defaultMetadata = new EventMetadata();
        defaultMetadata.setSystem(Event.DEFAULT_SYSTEM);
        defaultMetadata.setStatus(Event.DEFAULT_STATUS);
        event.getEventMetadata().add(defaultMetadata);

        eventRepository.save(event);
    }

    private void setAuditableFields(AuditableEntity entity) {
        User user = userService.getUnknownEntity();
        ZonedDateTime now = ZonedDateTime.now();

        //Check to see if we're creating or updating the entity
        if (null == entity.getInsertDate()) {
            entity.setInsertDate(now);
        }

        if (null == entity.getInsertBy()) {
            entity.setInsertBy(user);
        }

        entity.setLastModifiedDate(now);
        entity.setLastModifiedBy(user);
    }

    private void setCollectedFields(AuditableCollectedEntity entity) {
        entity.setCollectedBy(fieldWorkerService.getUnknownEntity());
        entity.setCollectionDateTime(ZonedDateTime.now());
    }

}
