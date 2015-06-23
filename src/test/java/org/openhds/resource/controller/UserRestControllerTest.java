package org.openhds.resource.controller;

import org.openhds.repository.UserRepository;
import org.openhds.resource.contract.UuidIdentifiableRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.UserRegistration;
import org.openhds.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Ben on 5/19/15.
 */
public class UserRestControllerTest extends UuidIdentifiableRestControllerTest<User, UserRepository, UserRestController> {

    @Autowired
    @Override
    protected void initialize(UserRepository repository, UserRestController controller) {
        this.repository = repository;
        this.controller = controller;
    }

    @Override
    protected User makeValidEntity(String name, String id) {
        User user = new User();
        user.setUuid(id);
        user.setFirstName(name);
        user.setLastName(name);
        user.setUsername(name);
        user.setPassword("password");
        return user;
    }

    @Override
    protected User makeInvalidEntity() {
        return new User();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(User entity, String name, String id) {
        assertNotNull(entity);

        User savedUser = repository.findOne(id);
        assertNotNull(savedUser);

        assertEquals(id, savedUser.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getUsername(), savedUser.getUsername());
    }

    @Override
    protected Registration<User> makeRegistration(User entity) {
        UserRegistration registration = new UserRegistration();
        registration.setUser(entity);
        return registration;
    }

}
