package org.openhds.resource.registration;

import org.openhds.events.model.Event;

/**
 * Created by bsh on 6/29/15.
 */
public class EventRegistration extends Registration<Event> {

    private Event event;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
