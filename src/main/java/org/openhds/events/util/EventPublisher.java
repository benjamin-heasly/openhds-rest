package org.openhds.events.util;

import org.openhds.events.model.Event;
import org.openhds.service.impl.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Wolfe on 7/8/2015.
 *
 * used to break the self dependency cycle
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
