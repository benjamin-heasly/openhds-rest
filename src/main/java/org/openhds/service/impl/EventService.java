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

    public EntityIterator<Event> findBySystemAndStatus(Sort sort,
                                                       QueryRange<ZonedDateTime> queryRange,
                                                       QueryValue[] queryValues,
                                                       QueryValue[] metadataQueryValues) {
        Specification<Event> specification = EventSpecifications.multiValueRangedBySystemAndStatus(queryRange,
                queryValues,
                metadataQueryValues);
        return iteratorFromPageable(pageable -> repository.findAll(specification, pageable), sort);
    }

    @Override
    public Event createOrUpdate(Event entity) {
        addDefaultMetadata(entity);
        return super.createOrUpdate(entity);
    }

    private void addDefaultMetadata(Event entity) {
        if (null == entity.findMetadataForSystem(Event.DEFAULT_SYSTEM)) {
            EventMetadata defaultMetadata = new EventMetadata();
            defaultMetadata.setSystem(Event.DEFAULT_SYSTEM);
            defaultMetadata.setStatus(Event.DEFAULT_STATUS);
            entity.getEventMetadata().add(defaultMetadata);
        }
    }

    public Page<Event> findBySystemAndStatus(Pageable pageable,
                                             QueryRange<ZonedDateTime> queryRange,
                                             QueryValue[] queryValues,
                                             QueryValue[] metadataQueryValues) {
        Specification<Event> specification = EventSpecifications.multiValueRangedBySystemAndStatus(queryRange,
                queryValues,
                metadataQueryValues);
        return repository.findAll(specification, pageable);
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

        for (EventMetadata eventMetadata : event.getEventMetadata()) {
            if (null == system || eventMetadata.getSystem().equals(system)) {
                eventMetadata.setNumTimesRead(eventMetadata.getNumTimesRead() + 1);
                eventMetadata.setStatus(Event.READ_STATUS);
            }
        }

        return repository.save(event);
    }

}
