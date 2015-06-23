package org.openhds.resource.contract;

import org.junit.Test;
import org.openhds.domain.contract.AuditableEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Ben on 6/16/15.
 */
public abstract class AuditableRestControllerTest <T extends AuditableEntity>
        extends UuidIdentifiableRestControllerTest<T> {

    protected String getByInsertDateResourceUrl() {
        return getResourceUrl() + "byinsertdate/";
    }

    protected String getVoidedResourceUrl() {
        return getResourceUrl() + "voided/";
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void getByInsertDate() throws Exception {

        ZonedDateTime beforeInsert = ZonedDateTime.now();

        T entity = insertNewAndReturn();
        ZonedDateTime insertDate = entity.getInsertDate();

        ZonedDateTime afterInsert = ZonedDateTime.now();

        // sanity check time stamps
        assertTrue(beforeInsert.compareTo(insertDate) < 0);
        assertTrue(insertDate.compareTo(afterInsert) < 0);

        // all but one record < beforeInsert
        mockMvc.perform(get(getByInsertDateResourceUrl())
                .param("insertedBefore", beforeInsert.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$._embedded." + getResourceName(), hasSize((int) getCount() - 1)));

        // one record > beforeInsert
        mockMvc.perform(get(getByInsertDateResourceUrl())
                .param("insertedAfter", beforeInsert.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$._embedded." + getResourceName(), hasSize(1)));

        // all records < afterInsert
        mockMvc.perform(get(getByInsertDateResourceUrl())
                .param("insertedBefore", afterInsert.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$._embedded." + getResourceName(), hasSize((int) getCount())));

        // no records > afterInsert
        mockMvc.perform(get(getByInsertDateResourceUrl())
                .param("insertedAfter", afterInsert.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$._embedded.", isEmptyOrNullString()));

        // one record > beforeInsert and < afterInsert
        mockMvc.perform(get(getByInsertDateResourceUrl())
                .param("insertedAfter", beforeInsert.toString())
                .param("insertedBefore", afterInsert.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$._embedded." + getResourceName(), hasSize(1)));
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
                .andExpect(jsonPath("$._embedded." + getResourceName(), hasSize(1)));

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

        return fromJson(getEntityClass(), mvcResult.getResponse().getContentAsString());
    }

}
