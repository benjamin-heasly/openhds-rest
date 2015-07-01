package org.openhds.service.impl;

import org.openhds.events.endpoint.EventEndpoint;
import org.openhds.events.model.Event;
import org.openhds.events.model.EventMetadata;
import org.openhds.repository.concrete.EventRepository;
import org.openhds.service.contract.AbstractAuditableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public void publishEvent(Event event) {
        for (EventEndpoint endpoint : eventEndpoints) {
            endpoint.publishEvent(event);
        }
    }

    public void incrementReadCount(Iterable<Event> events, String system) {
        for (Event event : events) {
            incrementReadCount(event, system);
        }
    }

    public Event incrementReadCount(Event event, String system) {

        if (null == system) {
            system = Event.DEFAULT_SYSTEM;
        }

        EventMetadata md = event.findMetadataForSystem(system);
        if (md == null) {
            md = new EventMetadata();
            md.setNumTimesRead(1);
            md.setStatus(Event.READ_STATUS);
            md.setSystem(system);
            event.getEventMetadata().add(md);
        } else {
            md.setNumTimesRead(md.getNumTimesRead() + 1);
            md.setStatus(Event.READ_STATUS);
            md.setSystem(system);
        }

        return repository.save(event);
    }

}
