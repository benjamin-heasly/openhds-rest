package org.openhds.resource.registration;

import org.openhds.domain.util.Description;
import org.openhds.security.model.User;

/**
 * Created by Ben on 6/3/15.
 *
 * Register a User who may access the system.
 *
 */
@Description(description = "Register an OpenHDS user.")
public class UserRegistration extends Registration<User> {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
