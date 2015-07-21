package org.openhds.resource.controller;

import org.junit.Test;
import org.openhds.errors.model.Error;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.FieldWorkerRepository;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.ErrorLogRegistration;
import org.openhds.resource.registration.Registration;
import org.openhds.service.impl.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by bsh on 6/29/15.
 */
public class ErrorLogRestControllerTest extends AuditableCollectedRestControllerTest
        <ErrorLog, ErrorLogService, ErrorLogRestController> {

    @Autowired
    FieldWorkerRepository fieldWorkerRepository;

    @Autowired
    @Override
    protected void initialize(ErrorLogService service, ErrorLogRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected ErrorLog makeValidEntity(String name, String id) {
        ErrorLog errorLog = new ErrorLog();
        errorLog.setUuid(id);
        errorLog.setDataPayload(name);

        Error error = new Error();
        error.setErrorMessage(name);
        errorLog.getErrors().add(error);

        errorLog.setCollectionDateTime(ZonedDateTime.now());

        return errorLog;
    }

    @Override
    protected ErrorLog makeInvalidEntity() {
        return new ErrorLog();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(ErrorLog entity, String name, String id) {
        assertNotNull(entity);

        ErrorLog savedErrorLog = service.findOne(id);
        assertNotNull(savedErrorLog);

        assertEquals(id, savedErrorLog.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getDataPayload(), savedErrorLog.getDataPayload());
    }

    @Override
    protected Registration<ErrorLog> makeRegistration(ErrorLog entity) {
        ErrorLogRegistration errorLogRegistration = new ErrorLogRegistration();
        errorLogRegistration.setErrorLog(entity);
        errorLogRegistration.setCollectedByUuid(fieldWorkerRepository.findAll().get(0).getUuid());
        return errorLogRegistration;
    }

    @Test
    @WithUserDetails
    public void query() throws Exception {
        ErrorLog first = insertFancyAndReturn("first", "first-id", "first-status", "first-assigned", "first-entity");
        ErrorLog second = insertFancyAndReturn("second", "second-id", "second-status", "second-assigned", "second-entity");
        ErrorLog third = insertFancyAndReturn("third", "third-id", "third-status", "third-assigned", "third-entity");

        // trivial query matches all
        final int totalCount = (int) service.countAll();
        this.mockMvc.perform(get(getResourceUrl() + "query"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(totalCount)));

        // several queries to match only the second
        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("resolutionStatus", second.getResolutionStatus()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(1)));

        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("entityType", second.getEntityType()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(1)));

        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("assignedTo", second.getAssignedTo()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(1)));

        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("fieldWorkerId", second.getCollectedBy().getFieldWorkerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(totalCount)));

        // queries by date range
        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("minDate", second.getInsertDate().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(2)));

        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("maxDate", second.getInsertDate().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(totalCount - 1)));

        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("minDate", first.getInsertDate().toString())
                .param("maxDate", third.getInsertDate().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(3)));

        // match only the second, emphatically!
        this.mockMvc.perform(get(getResourceUrl() + "query")
                .param("resolutionStatus", second.getResolutionStatus())
                .param("assignedTo", second.getAssignedTo())
                .param("entityType", second.getEntityType())
                .param("fieldWorkerId", second.getCollectedBy().getFieldWorkerId())
                .param("minDate", second.getInsertDate().toString())
                .param("maxDate", second.getInsertDate().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", is(1)));
    }

    private ErrorLog insertFancyAndReturn(String name,
                                          String id,
                                          String resolutionStatus,
                                          String assignedTo,
                                          String entityType) throws Exception {

        ErrorLog errorLog = makeValidEntity(name, id);
        errorLog.setResolutionStatus(resolutionStatus);
        errorLog.setAssignedTo(assignedTo);
        errorLog.setEntityType(entityType);

        MvcResult mvcResult = this.mockMvc.perform(post(getResourceUrl())
                .contentType(regularJson)
                .content(toJson(makeRegistration(errorLog))))
                .andExpect(status().isCreated())
                .andReturn();

        ErrorLog created = fromJson(ErrorLog.class, mvcResult.getResponse().getContentAsString());
        return created;
    }
}
