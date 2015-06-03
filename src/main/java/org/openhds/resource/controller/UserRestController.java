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

import javax.validation.ConstraintViolationException;

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
        // TODO: this implementation belongs in a User service.  Wolfe and Ben collab.
        if (null == registration.getUser().getUuid()) {
            throw new ConstraintViolationException("User username may not be null.", null);
        }

        return userRepository.save(registration.getUser());
    }

    @Override
    protected User register(UserRegistration registration, String id) {
        registration.getUser().setUuid(id);
        return register(registration);
    }

}
