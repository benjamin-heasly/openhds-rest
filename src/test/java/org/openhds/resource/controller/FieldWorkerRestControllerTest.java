package org.openhds.resource.controller;

import org.junit.Test;
import org.openhds.domain.model.FieldWorker;
import org.openhds.resource.contract.AuditableRestControllerTest;
import org.openhds.resource.registration.FieldWorkerRegistration;
import org.openhds.resource.registration.Registration;
import org.openhds.service.impl.FieldWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Ben on 5/19/15.
 */
public class FieldWorkerRestControllerTest extends AuditableRestControllerTest<
        FieldWorker, FieldWorkerService, FieldWorkerRestController> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Override
    protected void initialize(FieldWorkerService service, FieldWorkerRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected FieldWorker makeInvalidEntity() {
        return new FieldWorker();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(FieldWorker entity, String name, String id) {
        assertNotNull(entity);

        FieldWorker savedFieldWorker = service.findOne(id);
        assertNotNull(savedFieldWorker);

        assertEquals(id, savedFieldWorker.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getFieldWorkerId(), savedFieldWorker.getFieldWorkerId());
    }

    @Override
    protected Registration<FieldWorker> makeRegistration(FieldWorker entity) {
        FieldWorkerRegistration registration = new FieldWorkerRegistration();
        registration.setFieldWorker(entity);
        registration.setPassword("password");
        return registration;
    }

    @Test
    @WithUserDetails
    public void registeredPasswordIsHashed() throws Exception {
        FieldWorker fieldWorker = makeValidEntity("test-user", "test-id");
        MvcResult mvcResult = this.mockMvc.perform(post(getResourceUrl())
                .contentType(regularJson)
                .content(toJson(makeRegistration(fieldWorker))))
                .andExpect(status().isCreated())
                .andReturn();

        FieldWorker created = fromJson(controller.getEntityClass(), mvcResult.getResponse().getContentAsString());
        assertTrue(passwordEncoder.matches("password", created.getPasswordHash()));
    }

}
