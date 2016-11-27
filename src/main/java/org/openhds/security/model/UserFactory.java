package org.openhds.security.model;

import org.openhds.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    @Autowired
    private UserService userService;

    public User defaultUser() {
        User user =  generateUser("user", "root-role", "default user", "default user",
                "default user with root role (all privileges)");
        user.setUuid("DEFAULT_USER");
        return user;
    }

    public User defaultDataWorker() {
        User user =  generateUser("dataworker", "data-worker", "default data worker", "default data worker",
                "default data worker with data-worker role (read/write privileges)");
        user.setUuid("DEFAULT_DATA_WORKER");
        return user;
    }

    public User defaultDataManager() {
        User user =  generateUser("datamanager", "data-manager", "default data manager", "default data manager",
                "default data manager with data-manager role (read/write/edit privileges)");
        user.setUuid("DEFAULT_DATA_MANAGER");
        return user;
    }

    private User generateUser(String username, String role, String firstName, String lastName, String description) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setDescription(description);
        user.getRoles().add(userService.findRoleByName(role));

        return user;
    }
}
