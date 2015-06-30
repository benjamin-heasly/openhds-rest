package org.openhds.resource.contract;

import org.junit.Test;
import org.openhds.domain.contract.AuditableEntity;
import org.openhds.repository.contract.AuditableRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Ben on 6/16/15.
 */
public abstract class AuditableRestControllerTest<T extends AuditableEntity,
        U extends AuditableRepository<T>,
        V extends AuditableRestController<T, ?>>
        extends UuidIdentifiableRestControllerTest<T, U, V> {

    protected String getByDatePagedUrl() {
        return getResourceUrl() + "bydate/";
    }

    protected String getByDateBulkUrl() {
        return getByDatePagedUrl() + "bulk/";
    }

    protected String getVoidedResourceUrl() {
        return getResourceUrl() + "voided/";
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void getByDate() throws Exception {

        ZonedDateTime beforeInsert = ZonedDateTime.now();

        T entity = insertNewAndReturn();
        ZonedDateTime insertDate = entity.getInsertDate();

        ZonedDateTime afterInsert = ZonedDateTime.now();

        // sanity check time stamps
        assertTrue(beforeInsert.compareTo(insertDate) < 0);
        assertTrue(insertDate.compareTo(afterInsert) < 0);

        int preexistingRecords = (int) repository.count() - 1;
        mockMvc.perform(get(getByDatePagedUrl())
                .param("insertedBefore", beforeInsert.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$._embedded." + controller.getResourceName(), hasSize(preexistingRecords)));
        mockMvc.perform(get(getByDateBulkUrl())
                .param("insertedBefore", beforeInsert.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(regularJson))
                .andExpect(jsonPath("$", hasSize(preexistingRecords)));


        int newRecords = 1;
        mockMvc.perform(get(getByDatePagedUrl())
                .param("insertedAfter", beforeInsert.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$._embedded." + controller.getResourceName(), hasSize(newRecords)));
        mockMvc.perform(get(getByDateBulkUrl())
                .param("insertedAfter", beforeInsert.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(regularJson))
                .andExpect(jsonPath("$", hasSize(newRecords)));

        mockMvc.perform(get(getByDatePagedUrl())
                .param("insertedAfter", beforeInsert.toString())
                .param("insertedBefore", afterInsert.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$._embedded." + controller.getResourceName(), hasSize(newRecords)));
        mockMvc.perform(get(getByDateBulkUrl())
                .param("insertedAfter", beforeInsert.toString())
                .param("insertedBefore", afterInsert.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(regularJson))
                .andExpect(jsonPath("$", hasSize(newRecords)));

        int allRecords = (int) repository.count();
        mockMvc.perform(get(getByDatePagedUrl())
                .param("insertedBefore", afterInsert.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$._embedded." + controller.getResourceName(), hasSize(allRecords)));
        mockMvc.perform(get(getByDateBulkUrl())
                .param("insertedBefore", afterInsert.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(regularJson))
                .andExpect(jsonPath("$", hasSize(allRecords)));

        // no future records
        mockMvc.perform(get(getByDatePagedUrl())
                .param("insertedAfter", afterInsert.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$._embedded.", isEmptyOrNullString()));
        mockMvc.perform(get(getByDateBulkUrl())
                .param("insertedAfter", afterInsert.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(regularJson))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void getVoided() throws Exception {
        // make one
        T entity = insertNewAndReturn();
        mockMvc.perform(get(getResourceUrl() + entity.getUuid()))
                .andExpect(status().isOk());

        // nothing voided yet
        mockMvc.perform(get(getVoidedResourceUrl()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$._embedded.", isEmptyOrNullString()));

        // void it
        mockMvc.perform(delete(getResourceUrl() + entity.getUuid()))
                .andExpect(status().isNoContent());

        // one voided
        mockMvc.perform(get(getVoidedResourceUrl()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$._embedded." + controller.getResourceName(), hasSize(1)));

        // can't get it anymore
        mockMvc.perform(get(getResourceUrl() + entity.getUuid()))
                .andExpect(status().isNotFound());
    }

    private T insertNewAndReturn() throws Exception {
        T entity = makeValidEntity("test registration", "test id");

        MvcResult mvcResult = this.mockMvc.perform(post(getResourceUrl())
                .contentType(regularJson)
                .content(toJson(makeRegistration(entity))))
                .andExpect(status().isCreated())
                .andReturn();

        T created = fromJson(controller.getEntityClass(), mvcResult.getResponse().getContentAsString());
        return created;
    }

}
