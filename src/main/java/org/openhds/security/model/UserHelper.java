package org.openhds.security.model;

import org.openhds.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Wolfe on 7/7/2015.
 *
 * Used to break the self-dependency cycle of UserService
 *
 */
@Component
public class UserHelper {

    @Autowired
    private UserService userService;

    public User getCurrentUser(){
        return userService.getCurrentUser();
    }
}
