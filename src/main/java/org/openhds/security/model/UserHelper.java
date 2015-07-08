package org.openhds.security.model;

import org.openhds.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Wolfe on 7/7/2015.
 *
 * This class, like ErrorLogger and EventPublisher is necessary to break the self-depedency cycle created when
 * UserService is autowired by its super type AbstractAuditableService. It will autowire this instead and avoid that
 * problem.
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
