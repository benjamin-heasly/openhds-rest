package org.openhds;

import org.openhds.domain.model.*;
import org.openhds.repository.*;
import org.openhds.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.util.Calendar;
import java.util.UUID;

/**
 * Created by Ben on 5/18/15.
 *
 * This is just for proof of concept.  Initialize the db with some OpenHDS test objects.
 *
 */
public class ProofOfConceptInitializer implements CommandLineRunner {

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

    @Override
    public void run(String... args) throws Exception {
        addUser("user", "password");
        addFieldWorker("fieldworker", "password");

        addLocationHierarchyLevel(0, "root-level");
        addLocationHierarchyLevel(1, "top-level");
        addLocationHierarchyLevel(2, "bottom-level");

        addLocationHierarchy("top", null, "top-level", "fieldworker");
        addLocationHierarchy("bottom-one", "top", "bottom-level", "fieldworker");
        addLocationHierarchy("bottom-two", "top", "bottom-level", "fieldworker");

        addLocation("location-a", "bottom-one");
        addLocation("location-b", "bottom-one");
        addLocation("location-c", "bottom-two");
        addLocation("location-d", "bottom-two");
    }

    private void addUser(String name, String password) {
        User user = new User();
        user.setFirstName(name);
        user.setLastName(name);
        user.setUsername(name);
        user.setPassword(password);
        userRepository.save(user);
    }

    private void initAuditableFields(AuditableEntity auditableEntity) {
        User user = userRepository.findAll().get(0);
        auditableEntity.setInsertBy(user);
        auditableEntity.setInsertDate(Calendar.getInstance());
        auditableEntity.setUuid(UUID.randomUUID().toString().replace("-", ""));
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
        fieldWorker.setExtId(name);
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

    private void addLocationHierarchy(String name, String parentName, String levelName, String fieldWorkerName) {
        LocationHierarchy locationHierarchy = new LocationHierarchy();
        initAuditableFields(locationHierarchy);
        initCollectedFields(locationHierarchy);

        locationHierarchy.setName(name);
        locationHierarchy.setExtId(name);
        locationHierarchy.setLevel(locationHierarchyLevelRepository.findByName(levelName).get());

        if (null != parentName) {
            locationHierarchy.setParent(locationHierarchyRepository.findByExtId(parentName).get());
        }

        locationHierarchyRepository.save(locationHierarchy);
    }

    private void addLocation(String name, String hierarchyName) {
        Location location = new Location();
        initAuditableFields(location);
        initCollectedFields(location);

        location.setName(name);
        location.setExtId(name);
        location.setLocationHierarchy(locationHierarchyRepository.findByExtId(hierarchyName).get());
    }
}
