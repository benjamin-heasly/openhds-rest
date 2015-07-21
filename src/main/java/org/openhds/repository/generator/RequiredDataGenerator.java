package org.openhds.repository.generator;

import org.openhds.domain.model.FieldWorker;
import org.openhds.repository.concrete.*;
import org.openhds.repository.util.ProjectCodeLoader;
import org.openhds.security.model.Privilege;
import org.openhds.security.model.Role;
import org.openhds.security.model.User;
import org.openhds.service.impl.FieldWorkerService;
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

    private static final String DEFAULT_USER_UUID = "DEFAULT_USER";
    private static final String DEFAULT_USER_USERNAME = "user";
    private static final String DEFAULT_USER_PASSWORD = "password";

    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    private final FieldWorkerService fieldWorkerService;
    private final FieldWorkerRepository fieldWorkerRepository;

    private final ProjectCodeLoader projectCodeLoader;
    private final ProjectCodeRepository projectCodeRepository;

    @Autowired
    public RequiredDataGenerator(UserService userService,
                                 UserRepository userRepository,
                                 RoleRepository roleRepository,
                                 PrivilegeRepository privilegeRepository,
                                 FieldWorkerService fieldWorkerService,
                                 FieldWorkerRepository fieldWorkerRepository,
                                 ProjectCodeLoader projectCodeLoader,
                                 ProjectCodeRepository projectCodeRepository) {

        this.userService = userService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.fieldWorkerService = fieldWorkerService;
        this.fieldWorkerRepository = fieldWorkerRepository;
        this.projectCodeLoader = projectCodeLoader;
        this.projectCodeRepository = projectCodeRepository;
    }

    public void generateData() {
        generatePrivileges();
        generateRoles();
        generateUsers();
        generateProjectCodes();
        generateFieldWorkers();
    }

    public void clearData() {
        fieldWorkerRepository.deleteAllInBatch();
        projectCodeRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        roleRepository.deleteAllInBatch();
        privilegeRepository.deleteAllInBatch();
    }

    private void generateProjectCodes() {
        // code loader checks internally which codes need to be loaded
        projectCodeLoader.loadAllCodes();
    }

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
        if (userService.hasRecords()) {
            return;
        }

        User user = new User();
        user.setUuid(DEFAULT_USER_UUID);
        user.setUsername(DEFAULT_USER_USERNAME);
        user.setPassword(DEFAULT_USER_PASSWORD);
        user.setFirstName("default user");
        user.setLastName("default user");
        user.setDescription("default user with root role (all privileges)");
        user.getRoles().add(userService.findRoleByName("root-role"));

        userService.createOrUpdate(user);
    }

    private void generateFieldWorkers() {
        if (fieldWorkerService.hasRecords()) {
            return;
        }

        FieldWorker fieldWorker = new FieldWorker();
        fieldWorker.setFirstName("default fieldworker");
        fieldWorker.setLastName("default fieldworker");
        fieldWorker.setFieldWorkerId("fieldworker");
        fieldWorker.setPassword("password");
        fieldWorker.setPasswordHash("password");
        fieldWorkerService.createOrUpdate(fieldWorker);
    }

}
