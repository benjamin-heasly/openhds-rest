package org.openhds.resource.controller;

import org.openhds.repository.UserRepository;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.UserRegistration;
import org.openhds.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Ben on 5/19/15.
 */
public class UserRestControllerTest extends UuidRestControllerTest<User> {

    @Autowired
    private UserRepository userRepository;

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
    protected Registration<User> makeRegistration(User entity) {
        UserRegistration registration = new UserRegistration();
        registration.setUser(entity);
        return registration;
    }

    @Override
    protected User getAnyExisting() {
        return userRepository.findAll().get(0);
    }

    @Override
    protected long getCount() {
        return userRepository.count();
    }

    @Override
    protected String getResourceName() {
        return "users";
    }
}
