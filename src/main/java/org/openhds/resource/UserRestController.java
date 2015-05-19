package org.openhds.resource;

import org.openhds.repository.UserRepository;
import org.openhds.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/users")
class UserRestController extends AbstractRestController<User> {

    private final UserRepository userRepository;

    @Autowired
    public UserRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    @Override
    public Resource<User> readOne(@PathVariable String id) {
        return ResourceLinkHelper.shallowResourceWithUuidLinks(userRepository.findByUsername(id).get());
    }

    @Override
    public List<User> readAll() {
        return userRepository.findAll();
    }
}
