package org.openhds.resource.controller;

import org.openhds.errors.model.ErrorLog;
import org.openhds.events.model.Event;
import org.openhds.repository.concrete.EventRepository;
import org.openhds.repository.concrete.UserRepository;
import org.openhds.resource.contract.AuditableRestController;
import org.openhds.resource.registration.EventRegistration;
import org.openhds.service.impl.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolationException;
import java.time.ZonedDateTime;

/**
 * Created by bsh on 6/30/15.
 */
@RestController
@RequestMapping("/events")
@ExposesResourceFor(Event.class)
public class EventRestController extends AuditableRestController<Event, EventRegistration> {

    private final EventService eventService;

    private final UserRepository userRepository;

    @Autowired
    public EventRestController(EventRepository eventRepository,
                               EventService eventService,
                               UserRepository userRepository) {
        super(eventRepository);
        this.eventService = eventService;
        this.userRepository = userRepository;
    }


    @Override
    protected Event register(EventRegistration registration) {
        Event event = registration.getEvent();

        if (null == event.getEventData()) {
            throw new ConstraintViolationException("Event data must not be null.", null);
        }

        // TODO: this looks like service stuff
        event.setInsertBy(userRepository.findAll().get(0));
        event.setLastModifiedBy(userRepository.findAll().get(0));
        event.setInsertDate(ZonedDateTime.now());
        event.setLastModifiedDate(ZonedDateTime.now());

        return eventService.createOrUpdate(event);
    }

    @Override
    protected Event register(EventRegistration registration, String id) {
        registration.getEvent().setUuid(id);
        return register(registration);
    }
}
