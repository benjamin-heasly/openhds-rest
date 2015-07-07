package org.openhds.resource.contract;

import org.junit.Test;
import org.openhds.domain.contract.AuditableExtIdEntity;
import org.openhds.repository.contract.AuditableExtIdRepository;
import org.openhds.resource.registration.Registration;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Ben on 6/8/15.
 */
public abstract class AuditableExtIdRestControllerTest<
        T extends AuditableExtIdEntity,
        U extends AbstractAuditableExtIdService<T, ? extends AuditableExtIdRepository<T>>,
        V extends AuditableExtIdRestController<T, ? extends Registration<T>, U>>
        extends AuditableCollectedRestControllerTest<T, U, V> {

    protected String getExternalResourceUrl() {
        return getResourceUrl() + "external/";
    }

    @Test
    @WithUserDetails
    public void getExternalValid() throws Exception {
        T entity = findAnyExisting();
        mockMvc.perform(get(getExternalResourceUrl() + entity.getExtId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$_embedded.*[0][0].extId", is(entity.getExtId())));
    }

    @Test
    @WithUserDetails
    public void getExternalInvalid() throws Exception {
        mockMvc.perform(get(getExternalResourceUrl() + "invalid id"))
                .andExpect(status().isNotFound());
    }

}
