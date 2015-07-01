package org.openhds.service.impl;

import org.openhds.repository.concrete.UserRepository;
import org.openhds.security.model.User;
import org.openhds.service.contract.AbstractUuidService;

/**
 * Created by Wolfe on 7/1/2015.
 */
public class UserService extends AbstractUuidService<User,UserRepository>{

    public UserService(UserRepository userRepository) {
        super(userRepository);
    }

    @Override
    protected User makeUnknownEntity() {
        User user = new User();
        user.setUsername("unknown");
        user.setFirstName("unknown");
        return user;
    }

}
