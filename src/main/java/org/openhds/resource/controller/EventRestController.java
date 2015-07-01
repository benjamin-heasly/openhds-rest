package org.openhds.resource.controller;

import org.openhds.events.model.Event;
import org.openhds.repository.concrete.EventRepository;
import org.openhds.repository.concrete.UserRepository;
import org.openhds.repository.queries.QueryRange;
import org.openhds.repository.queries.QueryValue;
import org.openhds.repository.results.EntityIterator;
import org.openhds.repository.results.UpdatingIterator;
import org.openhds.resource.contract.AuditableRestController;
import org.openhds.resource.registration.EventRegistration;
import org.openhds.service.impl.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolationException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.openhds.repository.util.QueryUtil.dateQueryRange;

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

    @RequestMapping(value = "query", method = RequestMethod.GET)
    public EntityIterator<Event> findEvents(@RequestParam(value="system", required=false) String system,
                                            @RequestParam(value="status", required=false) String status,
                                            @RequestParam(value="actionType", required=false) String actionType,
                                            @RequestParam(value="entityType", required=false) String entityType,
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                            @RequestParam(value = "minDate", required = false) ZonedDateTime minDate,
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                            @RequestParam(value = "maxDate", required = false) ZonedDateTime maxDate) {

        List<QueryValue> properties = new ArrayList<>();
        addIfPresent(properties, "system", system, Event.DEFAULT_SYSTEM);
        addIfPresent(properties, "status", status, Event.DEFAULT_STATUS);
        addIfPresent(properties, "actionType", actionType, null);
        addIfPresent(properties, "entityType", entityType, null);

        QueryRange<ZonedDateTime> dateRange = dateQueryRange("lastModifiedDate", minDate, maxDate);

        EntityIterator<Event> events = eventService.findByMultipleValuesRanged(
                new Sort("lastModifiedDate"),
                dateRange,
                properties.toArray(new QueryValue[properties.size()]));

        // increment read counts as each event goes out the wire
        return new UpdatingIterator<>(events, (event) -> eventService.incrementReadCount(event, system));
    }

    private static void addIfPresent(Collection<QueryValue> properties, String propertyName, String value, String defaultValue) {
        if (value != null && !value.trim().isEmpty()) {
            properties.add(new QueryValue(propertyName, value));
        } else if (null != defaultValue) {
            properties.add(new QueryValue(propertyName, defaultValue));
        }
    }

}
