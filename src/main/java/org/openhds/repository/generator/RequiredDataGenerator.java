package org.openhds.repository.generator;

import org.openhds.domain.model.FieldWorker;
import org.openhds.security.model.Privilege;
import org.openhds.security.model.Role;
import org.openhds.security.model.User;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.ProjectCodeService;
import org.openhds.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static java.util.stream.Collectors.toSet;

/**
 * Created by bsh on 7/20/15.
 *
 * Generates data required for the application to run, including the initial
 * User, Role, Privilege, FieldWorker, and ProjectCodes.
 *
 * For each entity, only inserts required records if there are no records yet.
 * The should support project bootstrapping without messing up existing project data.
 *
 */
@Component
public class RequiredDataGenerator {

    private final UserService userService;

    private final FieldWorkerService fieldWorkerService;

    private final ProjectCodeService projectCodeService;

    @Autowired
    public RequiredDataGenerator(UserService userService,
                                 FieldWorkerService fieldWorkerService,
                                 ProjectCodeService projectCodeService) {
        this.userService = userService;
        this.fieldWorkerService = fieldWorkerService;
        this.projectCodeService = projectCodeService;
    }

    private void 


    private void generatePrivileges() {
        if (0 < userService.countPrivileges()) {
            return;
        }

        Arrays.stream(Privilege.Grant.values())
                .map(Privilege::new)
                .forEach(userService::createOrUpdate);
    }

    private void generateRoles() {
        if (0 < userService.countRoles()) {
            return;
        }

        Role role = new Role();
        role.setName("root-role");
        role.setDescription("role with every privilege");
        role.setPrivileges(Arrays.stream(Privilege.Grant.values())
                .map(Privilege::new)
                .collect(toSet()));
        userService.createOrUpdate(role);
    }

    private void generateUsers() {
        if (0 < userService.countAll()) {
            return;
        }

        User root = new User();
        root.setUsername("user");
        root.setFirstName("default user");
        root.setLastName("default user");
        root.setDescription("default user with root role (all privileges)");
        root.setPassword("password");
        root.getRoles().add(userService.findRoleByName("root-role"));
        userService.createOrUpdate(root);
    }

    private void generateFieldWorkers() {
        FieldWorker fieldWorker = new FieldWorker();
        fieldWorker.setFirstName("default fieldworker");
        fieldWorker.setLastName("default fieldworker");
        fieldWorker.setFieldWorkerId("fieldworker");
        fieldWorker.setPassword("password");
        fieldWorker.setPasswordHash("password");
        fieldWorkerService.createOrUpdate(fieldWorker);
    }

}
