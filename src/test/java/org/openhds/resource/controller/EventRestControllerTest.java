package org.openhds.resource.controller;

import org.junit.Test;
import org.openhds.events.model.Event;
import org.openhds.events.model.EventMetadata;
import org.openhds.resource.contract.AuditableRestControllerTest;
import org.openhds.resource.registration.EventRegistration;
import org.openhds.resource.registration.Registration;
import org.openhds.service.impl.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Created by bsh on 6/29/15.
 */
public class EventRestControllerTest extends AuditableRestControllerTest
        <Event, EventService, EventRestController> {

    @Autowired
    @Override
    protected void initialize(EventService service, EventRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected Event makeValidEntity(String name, String id) {
        Event event = new Event();
        event.setUuid(id);
        event.setEntityType(name);
        event.setActionType(name);
        event.setEventData(name);

        return event;
    }

    @Override
    protected Event makeInvalidEntity() {
        return new Event();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(Event entity, String name, String id) {
        assertNotNull(entity);

        Event savedEvent = service.findOne(id);
        assertNotNull(savedEvent);

        assertEquals(id, savedEvent.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getEventData(), savedEvent.getEventData());
    }

    @Override
    protected Registration<Event> makeRegistration(Event entity) {
        EventRegistration eventRegistration = new EventRegistration();
        eventRegistration.setEvent(entity);
        return eventRegistration;
    }

    @Test
    @WithUserDetails
    public void queryIncrementsReadCount() throws Exception {
        Event event = postFancyAndReturn("test", "test-id", "test-action", "test-entity");
        for (EventMetadata eventMetadata : event.getEventMetadata()) {
            assertEquals(0, eventMetadata.getNumTimesRead());
        }

        // query with no params should return all events
        final int totalCount = (int) service.countAll();
        this.mockMvc.perform(get(getResourceUrl() + "query"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.events", hasSize(totalCount)))
                .andExpect(jsonPath("$._embedded.events[*].eventMetadata[*].numTimesRead").value(everyItem(is(1))));

        this.mockMvc.perform(get(getResourceUrl() + "query"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.events", hasSize(totalCount)))
                .andExpect(jsonPath("$._embedded.events[*].eventMetadata[*].numTimesRead").value(everyItem(is(2))));
    }

    @Test
    @WithUserDetails
    public void queryBulk() throws Exception {
        Event event = postFancyAndReturn("test", "test-id", "test-action", "test-entity");
        for (EventMetadata eventMetadata : event.getEventMetadata()) {
            assertEquals(0, eventMetadata.getNumTimesRead());
        }

        // query with no params should return all events
        final int totalCount = (int) service.countAll();
        this.mockMvc.perform(get(getResourceUrl() + "query/bulk")
                .accept(regularJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(totalCount)))
                .andExpect(jsonPath("$[*].eventMetadata[*].numTimesRead").value(everyItem(is(1))));

        this.mockMvc.perform(get(getResourceUrl() + "query/bulk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(totalCount)))
                .andExpect(jsonPath("$[*].eventMetadata[*].numTimesRead").value(everyItem(is(2))));
    }

    @Test
    @WithUserDetails
    public void queryByParameterValues() throws Exception {
        postFancyAndReturn("A", "A-id", "action-1", "entity-1");
        postFancyAndReturn("B", "B-id", "action-2", "entity-2");
        postFancyAndReturn("C", "C-id", "action-1", "entity-2");

        // should see A and C for action-1
        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("actionType", "action-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.events", hasSize(2)))
                .andExpect(jsonPath("$._embedded.events[*].actionType").value(everyItem(is("action-1"))))
                .andExpect(jsonPath("$._embedded.events[*].uuid").value(containsInAnyOrder("A-id", "C-id")));

        // should see B and C for entity-2
        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("entityType", "entity-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.events", hasSize(2)))
                .andExpect(jsonPath("$._embedded.events[*].entityType").value(everyItem(is("entity-2"))))
                .andExpect(jsonPath("$._embedded.events[*].uuid").value(containsInAnyOrder("B-id", "C-id")));

        // should see A action-1 entity-1
        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("actionType", "action-1")
                .param("entityType", "entity-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.events", hasSize(1)))
                .andExpect(jsonPath("$._embedded.events[0].actionType").value("action-1"))
                .andExpect(jsonPath("$._embedded.events[0].entityType").value("entity-1"))
                .andExpect(jsonPath("$._embedded.events[0].uuid").value("A-id"));

        // should see B action-2 entity-2
        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("actionType", "action-2")
                .param("entityType", "entity-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.events", hasSize(1)))
                .andExpect(jsonPath("$._embedded.events[0].actionType").value("action-2"))
                .andExpect(jsonPath("$._embedded.events[0].entityType").value("entity-2"))
                .andExpect(jsonPath("$._embedded.events[0].uuid").value("B-id"));

        // should see C for action-1 entity-2
        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("actionType", "action-1")
                .param("entityType", "entity-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.events", hasSize(1)))
                .andExpect(jsonPath("$._embedded.events[0].actionType").value("action-1"))
                .andExpect(jsonPath("$._embedded.events[0].entityType").value("entity-2"))
                .andExpect(jsonPath("$._embedded.events[0].uuid").value("C-id"));

        // should see nothing for action-1 entity-1
        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("actionType", "action-2")
                .param("entityType", "entity-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(0));
    }

    @Test
    @WithUserDetails
    public void queryBySystemAndStatus() throws Exception {
        postFancyAndReturn("A", "A-id", "action-1", "entity-1");

        // query by new system "system-1" should return all events
        final int totalCount = (int) service.countAll();
        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("system", "system-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.events", hasSize(totalCount)))
                .andExpect(jsonPath("$._embedded.events[*].eventMetadata[*].numTimesRead").value(everyItem(is(1))));

        // another query from system-1 should increment read count
        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("system", "system-1")
                .param("status", Event.READ_STATUS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.events", hasSize(totalCount)))
                .andExpect(jsonPath("$._embedded.events[*].eventMetadata[*].numTimesRead").value(everyItem(is(2))));

        // no events should be left unread by system-1
        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("system", "system-1")
                .param("status", Event.DEFAULT_STATUS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(0));

        // events should still look fresh for system-2
        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("system", "system-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.events", hasSize(totalCount)))
                .andExpect(jsonPath("$._embedded.events[*].eventMetadata[*].numTimesRead").value(everyItem(is(1))));

    }

    @Test
    @WithUserDetails
    public void queryByDateRange() throws Exception {
        Event first = postFancyAndReturn("A", "A-id", "action-1", "entity-1");
        Event second = postFancyAndReturn("B", "B-id", "action-1", "entity-1");
        Event third = postFancyAndReturn("C", "C-id", "action-1", "entity-1");

        // two events since the second event
        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("minDate", second.getInsertDate().toString())
                .accept(halJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.events", hasSize(2)))
                .andExpect(jsonPath("$._embedded.events[*].uuid").value(containsInAnyOrder("B-id", "C-id")));

        // all but one event before second
        final int totalCount = (int) service.countAll();
        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("maxDate", second.getInsertDate().toString())
                .accept(halJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.events", hasSize(totalCount - 1)));

        // three events between first and third
        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("minDate", first.getInsertDate().toString())
                .param("maxDate", third.getInsertDate().toString())
                .accept(halJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.events", hasSize(3)))
                .andExpect(jsonPath("$._embedded.events[*].uuid").value(containsInAnyOrder("A-id", "B-id", "C-id")));
    }

    private Event postFancyAndReturn(String name,
                                     String id,
                                     String actionType,
                                     String entityType) throws Exception {

        Event event = makeValidEntity(name, id);
        event.setEntityType(entityType);
        event.setActionType(actionType);

        MvcResult mvcResult = this.mockMvc.perform(post(getResourceUrl())
                .contentType(regularJson)
                .content(toJson(makeRegistration(event))))
                .andExpect(status().isCreated())
                .andReturn();

        Event created = fromJson(Event.class, mvcResult.getResponse().getContentAsString());
        return created;
    }
}
