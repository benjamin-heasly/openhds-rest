package org.openhds.events.endpoint;

import org.openhds.events.model.Event;

public interface EventEndpoint {

    void publishEvent(Event event);
}
