package org.openhds.resource.controller;

import org.openhds.resource.contract.UuidIdentifiableRestController;
import org.openhds.resource.registration.UserRegistration;
import org.openhds.security.model.Role;
import org.openhds.security.model.User;
import org.openhds.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/users")
@ExposesResourceFor(User.class)
public class UserRestController extends UuidIdentifiableRestController<
        User,
        UserRegistration,
        UserService> {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        super(userService);
        this.userService = userService;
    }

    @Override
    protected UserRegistration makeSampleRegistration(User entity) {
        UserRegistration registration = new UserRegistration();
        registration.setUser(entity);
        registration.setPassword("password");
        return registration;
    }

    @Override
    protected User register(UserRegistration registration) {
        return userService.recordUser(registration.getUser(), registration.getPassword());
    }

    @Override
    protected User register(UserRegistration registration, String id) {
        registration.getUser().setUuid(id);
        return register(registration);
    }

    @RequestMapping(value = "/roles/{username}", method = RequestMethod.GET)
    public Set<Role> getUserRoles(@PathVariable String username) {
        return userService.findByUsername(username).getRoles();
    }
}
