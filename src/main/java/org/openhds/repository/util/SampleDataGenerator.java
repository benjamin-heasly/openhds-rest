package org.openhds.repository.util;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.contract.AuditableEntity;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.ProjectCode;
import org.openhds.domain.model.census.*;
import org.openhds.domain.model.update.*;
import org.openhds.errors.model.Error;
import org.openhds.errors.model.ErrorLog;
import org.openhds.events.model.Event;
import org.openhds.events.model.EventMetadata;
import org.openhds.repository.concrete.*;
import org.openhds.repository.concrete.census.*;
import org.openhds.repository.concrete.update.*;
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
    private ResidencyRepository residencyRepository;

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

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private DeathRepository deathRepository;

    @Autowired
    private InMigationRepository inMigationRepository;

    @Autowired
    private OutMigationRepository outMigationRepository;

    @Autowired
    private PregnancyObservationRepository pregnancyObservationRepository;

    @Autowired
    private PregnancyOutcomeRepository pregnancyOutcomeRepository;

    @Autowired
    private PregnancyResultRepository pregnancyResultRepository;

    public void clearData() {
        eventMetadataRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();

        errorRepository.deleteAllInBatch();
        errorLogRepository.deleteAllInBatch();

        deathRepository.deleteAllInBatch();

        inMigationRepository.deleteAllInBatch();
        outMigationRepository.deleteAllInBatch();

        pregnancyResultRepository.deleteAllInBatch();

        pregnancyOutcomeRepository.deleteAllInBatch();

        pregnancyObservationRepository.deleteAllInBatch();

        visitRepository.deleteAllInBatch();

        residencyRepository.deleteAllInBatch();

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

        addVisit("visit-a", "location-a");
        addVisit("visit-b", "location-b");
        addVisit("visit-c", "location-c");

        addIndividual("individual-a");
        addIndividual("individual-b");
        addIndividual("individual-c");

        addRelationship("relationship-type-a", "individual-a", "individual-b");
        addRelationship("relationship-type-b", "individual-c", "individual-a");
        addRelationship("relationship-type-c", "individual-c", "individual-c");

        addSocialGroup("social-group-a");
        addSocialGroup("social-group-b");
        addSocialGroup("social-group-c");

        addMembership("memberhip-type-a", "individual-a", "social-group-a");
        addMembership("memberhip-type-b", "individual-b", "social-group-b");
        addMembership("memberhip-type-c", "individual-c", "social-group-c");

        addResidency("resdoncy-toop-a", "individual-a", "location-a");
        addResidency("resdoncy-toop-b", "individual-b", "location-b");
        addResidency("resdoncy-toop-c", "individual-c", "location-c");

        addDeath("death-a", "individual-a", "visit-a");
        addDeath("death-b", "individual-b", "visit-b");

        addInMigration("migration-a");
        addInMigration("migration-b");

        addOutMigration("migration-a");
        addOutMigration("migration-b");

        addPregnancyObservation("individual-a");

        addPregnancyOutcome("individual-a", "individual-b", "visit-a");

        addPregnancyResult("birth", "individual-c");

        addPregnancyResult("stillborn", null);

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

    private void addRelationship (String relationshipType, String individualAName, String individualBName){
        Relationship relationship = new Relationship();
        initAuditableFields(relationship);
        initCollectedFields(relationship);
        relationship.setStartDate(ZonedDateTime.now().minusYears(1));
        relationship.setRelationshipType(relationshipType);
        relationship.setIndividualA(individualRepository.findByExtId(individualAName).get(0));
        relationship.setIndividualB(individualRepository.findByExtId(individualBName).get(0));

        relationshipRepository.save(relationship);
    }

    private void addMembership(String type, String individualName, String socialGroupName) {
        Membership membership = new Membership();
        initAuditableFields(membership);
        initCollectedFields(membership);

        membership.setIndividual(individualRepository.findByExtId(individualName).get(0));
        membership.setSocialGroup(socialGroupRepository.findByExtId(socialGroupName).get(0));
        membership.setRelationshipToGroupHead(type);
        membership.setStartDate(ZonedDateTime.now().minusYears(1));
        membership.setStartType(type);

        membershipRepository.save(membership);
    }

    private void addResidency(String type, String individualName, String locationName) {
        Residency residency = new Residency();
        initCollectedFields(residency);
        initAuditableFields(residency);

        residency.setIndividual(individualRepository.findByExtId(individualName).get(0));
        residency.setLocation(locationRepository.findByExtId(locationName).get(0));
        residency.setStartType(type);
        residency.setStartDate(ZonedDateTime.now().minusYears(1));

        residencyRepository.save(residency);
    }

    private void addVisit(String name, String locationName) {
        Visit visit = new Visit();
        initAuditableFields(visit);
        initCollectedFields(visit);

        visit.setExtId(name);
        visit.setLocation(locationRepository.findByExtId(locationName).get(0));
        visit.setVisitDate(ZonedDateTime.now());

        visitRepository.save(visit);
    }

    private void addDeath(String name, String individualName, String visitName) {
        Death death = new Death();
        initAuditableFields(death);
        initCollectedFields(death);

        death.setIndividual(individualRepository.findByExtId(individualName).get(0));
        death.setDeathCause(name);
        death.setDeathPlace(name);
        death.setDeathDate(ZonedDateTime.now().minusYears(1));
        death.setVisit(visitRepository.findByExtId(visitName).get(0));

        deathRepository.save(death);
    }

    private void addInMigration(String name) {
        InMigration inMigration = new InMigration();
        initAuditableFields(inMigration);
        initCollectedFields(inMigration);

        inMigration.setVisit(visitRepository.findAll().get(0));
        inMigration.setIndividual(individualRepository.findAll().get(0));
        inMigration.setResidency(residencyRepository.findAll().get(0));
        inMigration.setMigrationDate(ZonedDateTime.now().minusYears(1));
        inMigration.setOrigin(name);
        inMigration.setReason(name);
        inMigration.setMigrationType(name);

        inMigationRepository.save(inMigration);
    }

    private void addOutMigration(String name) {
        OutMigration outMigration = new OutMigration();
        initAuditableFields(outMigration);
        initCollectedFields(outMigration);

        outMigration.setVisit(visitRepository.findAll().get(0));
        outMigration.setIndividual(individualRepository.findAll().get(0));
        outMigration.setResidency(residencyRepository.findAll().get(0));
        outMigration.setMigrationDate(ZonedDateTime.now().minusYears(1));
        outMigration.setDestination(name);
        outMigration.setReason(name);

        outMigationRepository.save(outMigration);
    }

    private void addPregnancyObservation(String motherName) {
        PregnancyObservation pregnancyObservation = new PregnancyObservation();
        initAuditableFields(pregnancyObservation);
        initCollectedFields(pregnancyObservation);

        pregnancyObservation.setVisit(visitRepository.findAll().get(0));
        pregnancyObservation.setMother(individualRepository.findByExtId(motherName).get(0));
        pregnancyObservation.setExpectedDeliveryDate(ZonedDateTime.now().plusMonths(5));
        pregnancyObservation.setPregnancyDate(ZonedDateTime.now().minusMonths(5));

        pregnancyObservationRepository.save(pregnancyObservation);
    }

    private void addPregnancyOutcome(String motherName, String fatherName, String visitName){
        PregnancyOutcome pregnancyOutcome = new PregnancyOutcome();
        initAuditableFields(pregnancyOutcome);
        initCollectedFields(pregnancyOutcome);

        pregnancyOutcome.setVisit(visitRepository.findAll().get(0));
        pregnancyOutcome.setMother(individualRepository.findByExtId(motherName).get(0));
        pregnancyOutcome.setFather(individualRepository.findByExtId(fatherName).get(0));
        pregnancyOutcome.setChildrenBorn(1);
        pregnancyOutcome.setOutcomeDate(ZonedDateTime.now().minusYears(1));
        pregnancyOutcomeRepository.save(pregnancyOutcome);
    }

    private void addPregnancyResult(String type, String individualName){
        PregnancyResult pregnancyResult = new PregnancyResult();
        initAuditableFields(pregnancyResult);
        initCollectedFields(pregnancyResult);

        if(null != individualName)
            pregnancyResult.setChild(individualRepository.findByExtId(individualName).get(0));

        pregnancyResult.setType(type);
        pregnancyResult.setPregnancyOutcome(pregnancyOutcomeRepository.findAll().get(0));

        pregnancyResultRepository.save(pregnancyResult);
    }
}
