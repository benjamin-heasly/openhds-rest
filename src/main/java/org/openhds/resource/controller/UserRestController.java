package org.openhds.resource.controller;

import org.openhds.repository.UserRepository;
import org.openhds.resource.EntityControllerRegistry;
import org.openhds.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/users")
class UserRestController extends AbstractRestController<User> {

    private final UserRepository userRepository;

    @Autowired
    public UserRestController(EntityControllerRegistry entityControllerRegistry, UserRepository userRepository) {
        super(User.class, entityControllerRegistry);
        this.userRepository = userRepository;
    }

    @Override
    protected User findOne(String id) {
        return userRepository.findOne(id);
    }

    @Override
    protected Page<User> findPaged(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> add(@RequestBody User input) {
        User result = userRepository.save(input);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{username}")
                .buildAndExpand(result.getUsername()).toUri());
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.CREATED);
    }
}
