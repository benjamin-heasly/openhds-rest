package org.openhds.service.impl;

import org.openhds.events.endpoint.EventEndpoint;
import org.openhds.events.model.Event;
import org.openhds.events.model.EventMetadata;
import org.openhds.events.queries.EventSpecifications;
import org.openhds.repository.concrete.EventRepository;
import org.openhds.repository.queries.QueryRange;
import org.openhds.repository.queries.QueryValue;
import org.openhds.repository.results.EntityIterator;
import org.openhds.service.contract.AbstractAuditableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by bsh on 6/30/15.
 */
@Component
public class EventService extends AbstractAuditableService <Event, EventRepository> {

    @Autowired
    public EventService(EventRepository repository) {
        super(repository);
    }

    @Autowired
    private List<EventEndpoint> eventEndpoints;

    @Override
    protected Event makeUnknownEntity() {
        return new Event();
    }

    public Page<Event> findWithoutSystem(Pageable pageable,
                                         QueryRange<ZonedDateTime> queryRange,
                                         List<QueryValue> eventProperties,
                                         String system) {

        QueryValue[] queryValues = eventProperties.toArray(new QueryValue[eventProperties.size()]);
        Specification<Event> specification = EventSpecifications.multiValueRangedWithoutSystem(queryRange,
                queryValues,
                system);

        return repository.findAll(specification, pageable);
    }

    public Page<Event> findByProperties(Pageable pageable,
                                        QueryRange<ZonedDateTime> queryRange,
                                        List<QueryValue> eventProperties,
                                        List<QueryValue> metadataProperties) {

        QueryValue[] queryValues = eventProperties.toArray(new QueryValue[eventProperties.size()]);
        QueryValue[] metadataQueryValues = metadataProperties.toArray(new QueryValue[metadataProperties.size()]);

        Specification<Event> specification = EventSpecifications.multiValueRangedMatchingValues(queryRange,
                queryValues,
                metadataQueryValues);
        return repository.findAll(specification, pageable);
    }

    public Page<Event> findBySystemAndProperties(Pageable pageable,
                                                 String system,
                                                 QueryRange<ZonedDateTime> dateRange,
                                                 List<QueryValue> eventProperties,
                                                 List<QueryValue> metadataProperties) {

        if (null == system) {
            system = Event.DEFAULT_SYSTEM;
        }

        // find matching events that have not been seen by this system
        //  add default metadata to them
        Page<Event> unseenEvents = findWithoutSystem(pageable, dateRange, eventProperties, system);
        for (Event event : unseenEvents) {
            addSystemMetadata(event, system);
        }
        repository.save(unseenEvents);

        // search by event and metadata properties
        //  update them for read counts
        Page<Event> matchingEvents = findByProperties(pageable, dateRange, eventProperties, metadataProperties);
        incrementReadCountForSystem(matchingEvents, system);

        // return a fresh page over the matching events
        return findByProperties(pageable, dateRange, eventProperties, metadataProperties);
    }

    @Override
    public Event createOrUpdate(Event entity) {
        addSystemMetadata(entity, Event.DEFAULT_SYSTEM);
        return super.createOrUpdate(entity);
    }

    private void addSystemMetadata(Event entity, String system) {
        if (null == entity.findMetadataForSystem(system)) {
            EventMetadata defaultMetadata = new EventMetadata();
            defaultMetadata.setSystem(system);
            defaultMetadata.setStatus(Event.DEFAULT_STATUS);
            entity.getEventMetadata().add(defaultMetadata);
        }
    }

    public void publishEvent(Event event) {
        for (EventEndpoint endpoint : eventEndpoints) {
            endpoint.publishEvent(event);
        }
    }

    public void incrementReadCountForSystem(Iterable<Event> events, String system) {
        for (Event event : events) {
            incrementReadCountForSystem(event, system);
        }
    }

    public Event incrementReadCountForSystem(Event event, String system) {

        addSystemMetadata(event, system);

        for (EventMetadata eventMetadata : event.getEventMetadata()) {
            if (eventMetadata.getSystem().equals(system)) {
                eventMetadata.setNumTimesRead(eventMetadata.getNumTimesRead() + 1);
                eventMetadata.setStatus(Event.READ_STATUS);
            }
        }

        return repository.save(event);
    }

}
