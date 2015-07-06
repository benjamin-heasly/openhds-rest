package org.openhds.events.endpoint.impl;

import org.openhds.events.endpoint.EventEndpoint;
import org.openhds.events.model.Event;
import org.openhds.repository.concrete.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseEventEndpoint implements EventEndpoint {

    @Autowired
    private EventRepository eventRepository;

    @Override
    public void publishEvent(Event event) {
        eventRepository.save(event);
    }
}
