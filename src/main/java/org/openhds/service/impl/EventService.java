package org.openhds.service.impl;

import org.openhds.events.endpoint.EventEndpoint;
import org.openhds.events.model.Event;
import org.openhds.repository.concrete.EventRepository;
import org.openhds.service.contract.AbstractAuditableService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by bsh on 6/30/15.
 */
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

}
