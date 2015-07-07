package org.openhds.resource.controller;

import org.openhds.resource.contract.UuidIdentifiableRestController;
import org.openhds.resource.registration.UserRegistration;
import org.openhds.security.model.User;
import org.openhds.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/users")
@ExposesResourceFor(User.class)
class UserRestController extends UuidIdentifiableRestController<
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
    protected User register(UserRegistration registration) {
        return userService.createOrUpdate(registration.getUser());
    }

    @Override
    protected User register(UserRegistration registration, String id) {
        registration.getUser().setUuid(id);
        return register(registration);
    }
}
