package org.openhds.resource.controller;

import org.openhds.repository.UserRepository;
import org.openhds.resource.links.EntityLinkAssembler;
import org.openhds.resource.registration.UserRegistration;
import org.openhds.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/users")
@ExposesResourceFor(User.class)
class UserRestController extends UuidRestController<User, UserRegistration> {

    private final UserRepository userRepository;

    @Autowired
    public UserRestController(EntityLinkAssembler entityLinkAssembler, UserRepository userRepository) {
        super(entityLinkAssembler);
        this.userRepository = userRepository;
    }

    @Override
    protected User findOneCanonical(String id) {
        return userRepository.findOne(id);
    }

    @Override
    protected Page<User> findPaged(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    protected User register(UserRegistration registration) {
        return userRepository.save(registration.getUser());
    }

    @Override
    protected User register(UserRegistration registration, String id) {
        registration.getUser().setUuid(id);
        return register(registration);
    }

}
