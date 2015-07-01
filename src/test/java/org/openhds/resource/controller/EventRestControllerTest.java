package org.openhds.resource.controller;

import org.junit.Test;
import org.openhds.events.model.Event;
import org.openhds.events.model.EventMetadata;
import org.openhds.repository.concrete.EventRepository;
import org.openhds.resource.contract.AuditableRestControllerTest;
import org.openhds.resource.registration.EventRegistration;
import org.openhds.resource.registration.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Created by bsh on 6/29/15.
 */
public class EventRestControllerTest extends AuditableRestControllerTest
        <Event, EventRepository, EventRestController> {

    @Autowired
    @Override
    protected void initialize(EventRepository repository, EventRestController controller) {
        this.repository = repository;
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

        Event savedEvent = repository.findOne(id);
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
    @WithMockUser(username = username, password = password)
    public void queryIncrementsReadCount() throws Exception {
        Event event = postFancyAndReturn("test", "test-id", "test-action", "test-entity");
        for (EventMetadata eventMetadata : event.getEventMetadata()) {
            assertEquals(0, eventMetadata.getNumTimesRead());
        }

        // query with no params should return all events
        final int totalCount = (int) repository.count();
        this.mockMvc.perform(get(getResourceUrl() + "query"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(totalCount)))
                .andExpect(jsonPath("$[*].eventMetadata[*].numTimesRead").value(everyItem(is(1))));

        this.mockMvc.perform(get(getResourceUrl() + "query"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(totalCount)))
                .andExpect(jsonPath("$[*].eventMetadata[*].numTimesRead").value(everyItem(is(2))));
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void queryByParameterValues() throws Exception {
        postFancyAndReturn("A", "A-id", "action-1", "entity-1");
        postFancyAndReturn("B", "B-id", "action-2", "entity-2");
        postFancyAndReturn("C", "C-id", "action-1", "entity-2");

        // should see A and C for action-1
        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("actionType", "action-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].actionType").value(everyItem(is("action-1"))))
                .andExpect(jsonPath("$[*].uuid").value(contains("A-id", "C-id")));
    }


//    @Test
//    @WithMockUser(username = username, password = password)
//    public void query() throws Exception {
//        ErrorLog first = postFancyAndReturn("first", "first-id", "first-status", "first-assigned", "first-entity");
//        ErrorLog second = postFancyAndReturn("second", "second-id", "second-status", "second-assigned", "second-entity");
//        ErrorLog third = postFancyAndReturn("third", "third-id", "third-status", "third-assigned", "third-entity");
//
//        // trivial query matches all
//        final int totalCount = (int) repository.count();
//        this.mockMvc.perform(get(getResourceUrl() + "query"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$._embedded." + controller.getResourceName(), hasSize(totalCount)));
//
//        // several queries to match only the second
//        this.mockMvc.perform(get(getResourceUrl() + "query")
//                .param("resolutionStatus", second.getResolutionStatus()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$._embedded." + controller.getResourceName(), hasSize(1)));
//
//        this.mockMvc.perform(get(getResourceUrl() + "query")
//                .param("entityType", second.getEntityType()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$._embedded." + controller.getResourceName(), hasSize(1)));
//
//        this.mockMvc.perform(get(getResourceUrl() + "query")
//                .param("assignedTo", second.getAssignedTo()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$._embedded." + controller.getResourceName(), hasSize(1)));
//
//        this.mockMvc.perform(get(getResourceUrl() + "query")
//                .param("fieldWorkerId", second.getCollectedBy().getFieldWorkerId()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$._embedded." + controller.getResourceName(), hasSize(totalCount)));
//
//        // queries by date range
//        this.mockMvc.perform(get(getResourceUrl() + "query")
//                .param("minDate", second.getInsertDate().toString()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$._embedded." + controller.getResourceName(), hasSize(2)));
//
//        this.mockMvc.perform(get(getResourceUrl() + "query")
//                .param("maxDate", second.getInsertDate().toString()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$._embedded." + controller.getResourceName(), hasSize(totalCount - 1)));
//
//        this.mockMvc.perform(get(getResourceUrl() + "query")
//                .param("minDate", first.getInsertDate().toString())
//                .param("maxDate", third.getInsertDate().toString()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$._embedded." + controller.getResourceName(), hasSize(3)));
//
//        // match only the second, emphatically!
//        this.mockMvc.perform(get(getResourceUrl() + "query")
//                .param("resolutionStatus", second.getResolutionStatus())
//                .param("assignedTo", second.getAssignedTo())
//                .param("entityType", second.getEntityType())
//                .param("fieldWorkerId", second.getCollectedBy().getFieldWorkerId())
//                .param("minDate", second.getInsertDate().toString())
//                .param("maxDate", second.getInsertDate().toString()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$._embedded." + controller.getResourceName(), hasSize(1)));
//    }

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
