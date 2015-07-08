package org.openhds.events.util;

import org.openhds.events.model.Event;
import org.openhds.service.impl.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Wolfe on 7/8/2015.
 *
 * This class, like ErrorLogger and UserHelper is necessary to break the self-depedency cycle created when
 * EventService is autowired by its super type AbstractAuditableService. It will autowire this instead and avoid that
 * problem.
 *
 */
@Component
public class EventPublisher {

    @Autowired
    private EventService eventService;

    public Event publish(Event event){
        return eventService.createOrUpdate(event);
    }

}
