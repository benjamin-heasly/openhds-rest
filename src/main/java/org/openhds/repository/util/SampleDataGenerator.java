package org.openhds.repository.util;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.contract.AuditableEntity;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.LocationHierarchy;
import org.openhds.domain.model.LocationHierarchyLevel;
import org.openhds.repository.concrete.*;
import org.openhds.security.model.Privilege;
import org.openhds.security.model.Role;
import org.openhds.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.UUID;

import static java.util.stream.Collectors.toSet;

/**
 * Created by Ben on 5/18/15.
 * <p>
 * Initialize the db with some OpenHDS sample objects.
 */
@Component
public class SampleDataGenerator {

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FieldWorkerRepository fieldWorkerRepository;

    @Autowired
    private LocationHierarchyLevelRepository locationHierarchyLevelRepository;

    @Autowired
    private LocationHierarchyRepository locationHierarchyRepository;

    @Autowired
    private LocationRepository locationRepository;

    public void clearData() {
        locationRepository.deleteAllInBatch();
        locationHierarchyRepository.deleteAllInBatch();
        locationHierarchyLevelRepository.deleteAllInBatch();

        fieldWorkerRepository.deleteAllInBatch();

        userRepository.deleteAllInBatch();
        roleRepository.deleteAllInBatch();
        privilegeRepository.deleteAllInBatch();
    }

    public void generateSampleData() {
        addPrivileges(Privilege.Grant.values());

        addRole("user-role", Privilege.Grant.values());
        addUser("user", "password", "user-role");

        addRole("empty-role");
        addUser("non-user", "password", "empty-role");

        addFieldWorker("fieldworker", "password");

        addLocationHierarchyLevel(0, "root-level");
        addLocationHierarchyLevel(1, "top-level");
        addLocationHierarchyLevel(2, "bottom-level");

        addLocationHierarchy("top", null, "top-level");
        addLocationHierarchy("bottom-one", "top", "bottom-level");
        addLocationHierarchy("bottom-two", "top", "bottom-level");

        addLocation("location-a", "bottom-one");
        addLocation("location-b", "bottom-one");
        addLocation("location-c", "bottom-two");
        addLocation("location-d", "bottom-two");
        addLocation("duplicated", "bottom-two");
        addLocation("duplicated", "bottom-two");

    }

    private void addPrivileges(Privilege.Grant... grants) {
        Arrays.stream(grants)
                .map(Privilege::new)
                .forEach(privilegeRepository::save);
    }

    private void addRole(String name, Privilege.Grant... grants) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(name);
        role.setPrivileges(Arrays.stream(grants)
                .map(Privilege::new)
                .collect(toSet()));
        roleRepository.save(role);
    }

    private void addUser(String name, String password, String roleName) {
        User user = new User();
        user.setFirstName(name);
        user.setLastName(name);
        user.setUsername(name);
        user.setPassword(password);
        user.getRoles().add(roleRepository.findByName(roleName).get());
        userRepository.save(user);
    }

    private void initAuditableFields(AuditableEntity auditableEntity) {
        User user = userRepository.findAll().get(0);
        auditableEntity.setInsertBy(user);
        auditableEntity.setInsertDate(ZonedDateTime.now());
        auditableEntity.setUuid(UUID.randomUUID().toString());
    }

    private void initCollectedFields(AuditableCollectedEntity auditableCollectedEntity) {
        FieldWorker fieldWorker = fieldWorkerRepository.findAll().get(0);
        auditableCollectedEntity.setCollectedBy(fieldWorker);
    }

    private void addFieldWorker(String name, String password) {
        FieldWorker fieldWorker = new FieldWorker();
        initAuditableFields(fieldWorker);

        fieldWorker.setFirstName(name);
        fieldWorker.setLastName(name);
        fieldWorker.setFieldWorkerId(name);
        fieldWorker.setPassword(password);
        fieldWorker.setPasswordHash(password);
        fieldWorkerRepository.save(fieldWorker);
    }

    private void addLocationHierarchyLevel(int keyIdentifier, String name) {
        LocationHierarchyLevel locationHierarchyLevel = new LocationHierarchyLevel();
        initAuditableFields(locationHierarchyLevel);

        locationHierarchyLevel.setKeyIdentifier(keyIdentifier);
        locationHierarchyLevel.setName(name);
        locationHierarchyLevelRepository.save(locationHierarchyLevel);
    }

    private void addLocationHierarchy(String name, String parentName, String levelName) {
        LocationHierarchy locationHierarchy = new LocationHierarchy();
        initAuditableFields(locationHierarchy);
        initCollectedFields(locationHierarchy);

        locationHierarchy.setName(name);
        locationHierarchy.setExtId(name);
        locationHierarchy.setLevel(locationHierarchyLevelRepository.findByName(levelName).get());

        if (null != parentName) {
            locationHierarchy.setParent(locationHierarchyRepository.findByExtId(parentName).get(0));
        }

        locationHierarchyRepository.save(locationHierarchy);
    }

    private void addLocation(String name, String hierarchyName) {
        Location location = new Location();
        initAuditableFields(location);
        initCollectedFields(location);

        location.setName(name);
        location.setExtId(name);
        location.setLocationHierarchy(locationHierarchyRepository.findByExtId(hierarchyName).get(0));

        locationRepository.save(location);
    }
}
