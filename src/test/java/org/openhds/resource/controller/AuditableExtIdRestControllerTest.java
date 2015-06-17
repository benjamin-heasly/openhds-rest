package org.openhds.resource.controller;

import org.junit.Test;
import org.openhds.domain.contract.AuditableExtIdEntity;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Ben on 6/8/15.
 */
public abstract class AuditableExtIdRestControllerTest<T extends AuditableExtIdEntity>
        extends AuditableCollectedRestControllerTest<T> {

    protected String getExternalResourceUrl() {
        return getResourceUrl() + "external/";
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void getExternalValid() throws Exception {
        T entity = getAnyExisting();
        mockMvc.perform(get(getExternalResourceUrl() + entity.getExtId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$_embedded.*[0][0].extId", is(entity.getExtId())));
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void getExternalInvalid() throws Exception {
        mockMvc.perform(get(getExternalResourceUrl() + "invalid id"))
                .andExpect(status().isNotFound());
    }

}
