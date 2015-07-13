package org.openhds.repository.util;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.contract.AuditableEntity;
import org.openhds.domain.model.*;
import org.openhds.errors.model.Error;
import org.openhds.errors.model.ErrorLog;
import org.openhds.events.model.Event;
import org.openhds.events.model.EventMetadata;
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

    @Autowired
    private IndividualRepository individualRepository;

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private ErrorRepository errorRepository;

    @Autowired
    private ErrorLogRepository errorLogRepository;

    @Autowired
    private EventMetadataRepository eventMetadataRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ProjectCodeRepository projectCodeRepository;

    @Autowired
    private SocialGroupRepository socialGroupRepository;

    @ Autowired
    private MembershipRepository membershipRepository;

    public void clearData() {
        eventMetadataRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();

        errorRepository.deleteAllInBatch();
        errorLogRepository.deleteAllInBatch();

        membershipRepository.deleteAllInBatch();

        relationshipRepository.deleteAllInBatch();

        locationRepository.deleteAllInBatch();

        socialGroupRepository.deleteAllInBatch();

        individualRepository.deleteAllInBatch();

        locationRepository.deleteAllInBatch();
        locationHierarchyRepository.deleteAllInBatch();
        locationHierarchyLevelRepository.deleteAllInBatch();

        fieldWorkerRepository.deleteAllInBatch();

        userRepository.deleteAllInBatch();
        roleRepository.deleteAllInBatch();
        privilegeRepository.deleteAllInBatch();

        projectCodeRepository.deleteAllInBatch();
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

        addIndividual("individual-a");
        addIndividual("individual-b");
        addIndividual("individual-c");

        addRelationship("individual-a", "individual-b");
        addRelationship("individual-c", "individual-a");
        addRelationship("individual-c", "individual-c");

        addSocialGroup("social-group-a");
        addSocialGroup("social-group-b");
        addSocialGroup("social-group-c");

        addMembership("memberhip-type-a", "individual-a", "social-group-a");
        addMembership("memberhip-type-b", "individual-b", "social-group-b");
        addMembership("memberhip-type-c", "individual-c", "social-group-c");

        addErrorLog("sample error");

        addEvent("sample event", "sample system");

        addProjectCode("test-code-1", "value-1");
        addProjectCode("test-code-2", "value-2");
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
        user.setUuid(name);
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
        auditableEntity.setLastModifiedBy(user);
        auditableEntity.setLastModifiedDate(auditableEntity.getInsertDate());
        auditableEntity.setUuid(UUID.randomUUID().toString());
    }

    private void initCollectedFields(AuditableCollectedEntity auditableCollectedEntity) {
        FieldWorker fieldWorker = fieldWorkerRepository.findAll().get(0);
        auditableCollectedEntity.setCollectedBy(fieldWorker);
        auditableCollectedEntity.setCollectionDateTime(ZonedDateTime.now());
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
        locationHierarchy.setLevel(locationHierarchyLevelRepository.findByDeletedFalseAndName(levelName).get());

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

    private void addErrorLog(String description) {
        ErrorLog errorLog = new ErrorLog();
        initAuditableFields(errorLog);
        initCollectedFields(errorLog);

        errorLog.setDataPayload(description);
        Error error = new Error();
        error.setErrorMessage(description);
        errorLog.getErrors().add(error);

        errorLogRepository.save(errorLog);
    }

    private void addEvent(String description, String system) {
        Event event = new Event();
        initAuditableFields(event);

        event.setActionType(Event.DEFAULT_ACTION);
        event.setEntityType(Event.DEFAULT_ENTITY);
        event.setEventData(description);

        EventMetadata defaultMetadata = new EventMetadata();
        defaultMetadata.setSystem(Event.DEFAULT_SYSTEM);
        defaultMetadata.setStatus(Event.DEFAULT_STATUS);
        event.getEventMetadata().add(defaultMetadata);

        eventRepository.save(event);
    }

    private void addIndividual (String name){
        Individual individual = new Individual();
        initAuditableFields(individual);
        initCollectedFields(individual);
        individual.setExtId(name);
        individual.setFirstName(name);
        individual.setMiddleName(name);
        individual.setLastName(name);
        individual.setDateOfBirth(ZonedDateTime.now().minusYears(1));

        individualRepository.save(individual);
    }

    private void addRelationship (String individualAId, String individualBId){
        Relationship relationship = new Relationship();
        initAuditableFields(relationship);
        initCollectedFields(relationship);
        relationship.setStartDate(ZonedDateTime.now().minusYears(1));
        relationship.setRelationshipType("surrogate-siamese-fathers-uncle");
        relationship.setIndividualA(individualRepository.findOne(individualAId));
        relationship.setIndividualB(individualRepository.findOne(individualBId));

        relationshipRepository.save(relationship);
    }

    private void addProjectCode(String name, String value) {
        ProjectCode projectCode = new ProjectCode();
        projectCode.setCodeName(name);
        projectCode.setCodeValue(value);
        projectCodeRepository.save(projectCode);
    }

    private void addSocialGroup(String name) {
        SocialGroup socialGroup = new SocialGroup();
        initAuditableFields(socialGroup);
        initCollectedFields(socialGroup);

        socialGroup.setExtId(name);
        socialGroup.setGroupName(name);
        socialGroupRepository.save(socialGroup);
    }

    private void addMembership(String type, String individualName, String socialGroupName) {
        Membership membership = new Membership();
        initAuditableFields(membership);
        initCollectedFields(membership);

        membership.setIndividual(individualRepository.findByExtId(individualName).get(0));
        membership.setSocialGroup(socialGroupRepository.findByExtId(socialGroupName).get(0));
        membership.setbIsToA(type);
        membership.setStartDate(ZonedDateTime.now().minusYears(1));
        membership.setStartType(type);

        membershipRepository.save(membership);
    }
}
