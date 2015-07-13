package org.openhds.service.impl;

import org.openhds.events.model.Event;
import org.openhds.service.AuditableServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Wolfe on 7/13/2015.
 */
public class EventServiceTest extends AuditableServiceTest<Event, EventService>{

    @Override
    protected Event makeInvalidEntity() {
        return new Event();
    }

    @Override
    protected Event makeValidEntity(String name, String id) {
        Event event = new Event();
        event.setUuid(id);
        event.setActionType(name);
        event.setEntityType(name);
        event.setEventData(name);
        return event;
    }

    @Override
    @Autowired
    protected void initialize(EventService service) {
        this.service = service;
    }
}
