package org.openhds.resource.controller;

import org.openhds.events.model.Event;
import org.openhds.repository.concrete.UserRepository;
import org.openhds.repository.queries.QueryRange;
import org.openhds.repository.queries.QueryValue;
import org.openhds.repository.results.EntityIterator;
import org.openhds.repository.results.PageIterator;
import org.openhds.repository.results.PagingEntityIterator;
import org.openhds.repository.results.ShallowCopyIterator;
import org.openhds.resource.contract.AuditableRestController;
import org.openhds.resource.registration.EventRegistration;
import org.openhds.service.impl.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.openhds.repository.util.QueryUtil.dateQueryRange;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by bsh on 6/30/15.
 */
@RestController
@RequestMapping("/events")
@ExposesResourceFor(Event.class)
public class EventRestController extends AuditableRestController<
        Event,
        EventRegistration,
        EventService> {

    public static final String REL_QUERY = "query";
    public static final String REL_QUERY_BULK = "querybulk";

    private final EventService eventService;

    @Autowired
    public EventRestController(EventService eventService,
                               UserRepository userRepository) {
        super(eventService);
        this.eventService = eventService;
    }

    private static void addIfPresent(Collection<QueryValue> properties, String propertyName, String value) {
        if (value != null && !value.trim().isEmpty()) {
            properties.add(new QueryValue(propertyName, value));
        }
    }

    @Override
    protected Event register(EventRegistration registration) {
        return eventService.createOrUpdate(registration.getEvent());
    }

    @Override
    protected Event register(EventRegistration registration, String id) {
        registration.getEvent().setUuid(id);
        return register(registration);
    }

    @Override
    public void addCollectionResourceLinks(ResourceSupport resource) {
        super.addCollectionResourceLinks(resource);

        resource.add(withTemplateParams(linkTo(methodOn(this.getClass())
                        .findEvents(null, null, null, null, null, null, null, null))
                        .withRel(REL_QUERY),
                "system", "status", "actionType", "entityType", "minDate", "maxDate"));

        resource.add(withTemplateParams(linkTo(methodOn(this.getClass())
                        .findEventsBulk(null, null, null, null, null, null, null))
                        .withRel(REL_QUERY_BULK),
                "system", "status", "actionType", "entityType", "minDate", "maxDate"));
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public PagedResources findEvents(Pageable pageable,
                                     PagedResourcesAssembler assembler,
                                     @RequestParam(value = "system", defaultValue = Event.DEFAULT_SYSTEM, required = false) String system,
                                     @RequestParam(value = "status", required = false) String status,
                                     @RequestParam(value = "actionType", required = false) String actionType,
                                     @RequestParam(value = "entityType", required = false) String entityType,
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                     @RequestParam(value = "minDate", required = false) ZonedDateTime minDate,
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                     @RequestParam(value = "maxDate", required = false) ZonedDateTime maxDate) {

        List<QueryValue> eventProperties = new ArrayList<>();
        addIfPresent(eventProperties, "actionType", actionType);
        addIfPresent(eventProperties, "entityType", entityType);

        List<QueryValue> metadataProperties = new ArrayList<>();
        addIfPresent(metadataProperties, "system", system);
        addIfPresent(metadataProperties, "status", status);

        QueryRange<ZonedDateTime> dateRange = dateQueryRange("lastModifiedDate", minDate, maxDate);

        Page<Event> events = eventService.findBySystemAndProperties(pageable,
                system,
                dateRange,
                eventProperties,
                metadataProperties);
        return toResource(events, assembler, linkTo(methodOn(this.getClass()).findEvents(pageable, assembler, system, status, actionType, entityType, minDate, maxDate)).withSelfRel());
    }

    @RequestMapping(value = "/query/bulk", method = RequestMethod.GET)
    public EntityIterator<Event> findEventsBulk(Sort sort,
                                                @RequestParam(value = "system", defaultValue = Event.DEFAULT_SYSTEM, required = false) String system,
                                                @RequestParam(value = "status", required = false) String status,
                                                @RequestParam(value = "actionType", required = false) String actionType,
                                                @RequestParam(value = "entityType", required = false) String entityType,
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                @RequestParam(value = "minDate", required = false) ZonedDateTime minDate,
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                @RequestParam(value = "maxDate", required = false) ZonedDateTime maxDate) {

        List<QueryValue> eventProperties = new ArrayList<>();
        addIfPresent(eventProperties, "actionType", actionType);
        addIfPresent(eventProperties, "entityType", entityType);

        List<QueryValue> metadataProperties = new ArrayList<>();
        addIfPresent(metadataProperties, "system", system);
        addIfPresent(metadataProperties, "status", status);

        QueryRange<ZonedDateTime> dateRange = dateQueryRange("lastModifiedDate", minDate, maxDate);

        PageIterator<Event> pageIterator = new PageIterator<>(
                (pageable) -> eventService.findBySystemAndProperties(pageable,
                        system,
                        dateRange,
                        eventProperties,
                        metadataProperties), sort);
        EntityIterator<Event> entityIterator = new PagingEntityIterator<>(pageIterator);
        entityIterator.setCollectionName(getResourceName());
        return new ShallowCopyIterator<>(entityIterator);
    }
}
