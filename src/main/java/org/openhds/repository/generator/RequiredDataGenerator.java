package org.openhds.repository.generator;

import com.google.common.collect.Lists;
import org.openhds.domain.model.FieldWorker;
import org.openhds.repository.concrete.*;
import org.openhds.repository.util.ProjectCodeLoader;
import org.openhds.security.model.Privilege;
import org.openhds.security.model.Role;
import org.openhds.security.model.User;
import org.openhds.security.model.UserFactory;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.ProjectCodeService;
import org.openhds.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toSet;

/**
 * Created by bsh on 7/20/15.
 *
 * Generates data required for the application to run, including the initial
 * User, Role, Privilege, FieldWorker, and ProjectCodes.
 *
 * For each entity, only inserts required records if there are no records yet.
 * This behavior should support project bootstrapping and testing,
 * without messing up existing project data.
 *
 */
@Component
public class RequiredDataGenerator implements DataGenerator {

    private static final String DEFAULT_USER_PASSWORD = "password";
    private static final String DEFAULT_FIELD_WORKER_USERNAME = "fieldworker";
    private static final String DEFAULT_FIELD_WORKER_PASSWORD = "password";

    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    private final FieldWorkerService fieldWorkerService;
    private final FieldWorkerRepository fieldWorkerRepository;

    private final ProjectCodeService projectCodeService;
    private final ProjectCodeLoader projectCodeLoader;
    private final ProjectCodeRepository projectCodeRepository;

    // weak, fast hashing for default user and testing
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);

    @Autowired
    private UserFactory userFactory;

    @Autowired
    public RequiredDataGenerator(UserService userService,
                                 UserRepository userRepository,
                                 RoleRepository roleRepository,
                                 PrivilegeRepository privilegeRepository,
                                 FieldWorkerService fieldWorkerService,
                                 FieldWorkerRepository fieldWorkerRepository,
                                 ProjectCodeService projectCodeService,
                                 ProjectCodeLoader projectCodeLoader,
                                 ProjectCodeRepository projectCodeRepository) {

        this.userService = userService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.fieldWorkerService = fieldWorkerService;
        this.fieldWorkerRepository = fieldWorkerRepository;
        this.projectCodeService = projectCodeService;
        this.projectCodeLoader = projectCodeLoader;
        this.projectCodeRepository = projectCodeRepository;
    }

    @Override
    public void generateData(int size) {
        generatePrivileges();
        generateRoles();
        generateUsers();
        generateProjectCodes();
        generateFieldWorkers();
        generateUnknowns();
    }

    @Override
    public void generateData() {
        generateData(0);
    }

    @Override
    public void clearData() {
        fieldWorkerRepository.deleteAllInBatch();
        projectCodeRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        roleRepository.deleteAllInBatch();
        privilegeRepository.deleteAllInBatch();
    }

    public void createDefaultUser() {
        User user = userFactory.defaultUser();
        userService.recordUserWeakPassword(user, DEFAULT_USER_PASSWORD);
    }

    public void createDataWorkerUser() {
        User dataWorker = userFactory.defaultDataWorker();
        userService.recordUserWeakPassword(dataWorker, DEFAULT_USER_PASSWORD);
    }

    public void createDataManagerUser() {
        User dataManager = userFactory.defaultDataManager();
        userService.recordUserWeakPassword(dataManager, DEFAULT_USER_PASSWORD);
    }

    // trigger services to create unknown entities ahead of time, for predictable entity counts
    private void generateUnknowns() {
        fieldWorkerService.getUnknownEntity();
        projectCodeService.getUnknownEntity();
        userService.getUnknownEntity();
    }

    private void generateProjectCodes() {
        // code loader checks internally which codes need to be loaded
        projectCodeLoader.loadAllCodes();
    }

    private void generatePrivileges() {
        if (0 < userService.countPrivileges()) {
            return;
        }

        // save with repository instead of service for speed
        Arrays.stream(Privilege.Grant.values())
                .map(Privilege::new)
                .forEach(privilegeRepository::save);
    }

    private void generateRole(String name, String description, Privilege.Grant[] grants) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        role.setPrivileges(Arrays.stream(grants)
                .map(Privilege::new)
                .collect(toSet()));

        // save with repository instead of service for speed
        roleRepository.save(role);
    }

    private void generateRoles() {
        int roleCount = 3;
        if (roleCount <= userService.countRoles()) {
            return;
        }

        generateRole("root-role", "role with every privilege", Privilege.Grant.values());

        Privilege.Grant[] dataWorkerGrants = {
                Privilege.Grant.ROLE_CREATE_ENTITY,
                Privilege.Grant.ROLE_VIEW_ENTITY,
                Privilege.Grant.ROLE_USER
        };
        generateRole("data-worker", "role with read and write privilege", dataWorkerGrants);

        Privilege.Grant[] dataManagerGrants = {
                Privilege.Grant.ROLE_CREATE_ENTITY,
                Privilege.Grant.ROLE_VIEW_ENTITY,
                Privilege.Grant.ROLE_USER,
                Privilege.Grant.ROLE_EDIT_ENTITY,
                Privilege.Grant.ROLE_DELETE_ENTITY
        };

        generateRole("data-manager", "role with read, write and edit privilege", dataManagerGrants);
    }

    private void generateUsers() {
        if (userService.hasRecords()) {
            return;
        }
        createDefaultUser();
        createDataWorkerUser();
        createDataManagerUser();
    }

    private void generateFieldWorkers() {
        if (fieldWorkerService.hasRecords()) {
            return;
        }

        FieldWorker fieldWorker = new FieldWorker();
        fieldWorker.setFirstName("default fieldworker");
        fieldWorker.setLastName("default fieldworker");
        fieldWorker.setFieldWorkerId(DEFAULT_FIELD_WORKER_USERNAME);
        fieldWorker.setPasswordHash(passwordEncoder.encode(DEFAULT_FIELD_WORKER_PASSWORD));
        fieldWorkerService.createOrUpdate(fieldWorker);
    }

}
