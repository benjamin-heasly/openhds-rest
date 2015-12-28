package org.openhds.resource.controller;

import org.junit.Test;
import org.openhds.resource.contract.UuidIdentifiableRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.UserRegistration;
import org.openhds.security.model.User;
import org.openhds.service.impl.UserService;
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
public class UserRestControllerTest extends UuidIdentifiableRestControllerTest
        <User, UserService, UserRestController> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Override
    protected void initialize(UserService service, UserRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected User makeInvalidEntity() {
        return new User();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(User entity, String name, String id) {
        assertNotNull(entity);

        User savedUser = service.findOne(id);
        assertNotNull(savedUser);

        assertEquals(id, savedUser.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getUsername(), savedUser.getUsername());
    }

    @Test
    @WithUserDetails
    public void registeredPasswordIsHashed() throws Exception {
        User user = makeValidEntity("test-user", "test-id");
        MvcResult mvcResult = this.mockMvc.perform(post(getResourceUrl())
                .contentType(regularJson)
                .content(toJson(makeRegistration(user))))
                .andExpect(status().isCreated())
                .andReturn();

        User created = fromJson(controller.getEntityClass(), mvcResult.getResponse().getContentAsString());
        assertTrue(passwordEncoder.matches("password", created.getPasswordHash()));
    }
}
