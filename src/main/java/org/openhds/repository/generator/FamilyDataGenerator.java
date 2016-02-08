package org.openhds.repository.generator;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.contract.AuditableEntity;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.*;
import org.openhds.repository.concrete.census.*;
import org.openhds.security.model.User;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.UserService;
import org.openhds.service.impl.census.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

/**
 * Created by bsh on 4 August 2015.
 * <p>
 * Generates sample family data, including
 * Individual, SocialGroup, Relationship, Residency, and Membership.
 * <p>
 * For each entity, only inserts sample records if there are no records yet.
 * This behavior should support testing without messing up existing project data.
 * <p>
 * At each existing Location, creates a family of 10 Individuals.
 * <p>
 * Each family will be represented by:
 * - a SocialGroup "household"
 * - an Individual who is the "head of household"
 * - nine individuals who are "members of the household"
 * - 10 Residencies at the Location
 * - 10 Memberships in the SocialGroup
 * - 10 Relationships to the head of household
 */
@Component
public class FamilyDataGenerator implements DataGenerator {

    private final IndividualRepository individualRepository;
    private final IndividualService individualService;

    private final SocialGroupRepository socialGroupRepository;
    private final SocialGroupService socialGroupService;

    private final ResidencyRepository residencyRepository;
    private final ResidencyService residencyService;

    private final MembershipRepository membershipRepository;
    private final MembershipService membershipService;

    private final RelationshipRepository relationshipRepository;
    private final RelationshipService relationshipService;

    private final LocationService locationService;

    private final User user;

    private final FieldWorker fieldWorker;

    @Autowired
    public FamilyDataGenerator(IndividualRepository individualRepository,
                               IndividualService individualService,
                               SocialGroupRepository socialGroupRepository,
                               SocialGroupService socialGroupService,
                               ResidencyRepository residencyRepository,
                               ResidencyService residencyService,
                               MembershipRepository membershipRepository,
                               MembershipService membershipService,
                               RelationshipRepository relationshipRepository,
                               RelationshipService relationshipService,
                               LocationService locationService,
                               FieldWorkerService fieldWorkerService,
                               UserService userService) {
        this.individualRepository = individualRepository;
        this.individualService = individualService;
        this.socialGroupRepository = socialGroupRepository;
        this.socialGroupService = socialGroupService;
        this.residencyRepository = residencyRepository;
        this.residencyService = residencyService;
        this.membershipRepository = membershipRepository;
        this.membershipService = membershipService;
        this.relationshipRepository = relationshipRepository;
        this.relationshipService = relationshipService;
        this.locationService = locationService;

        this.user = userService.getUnknownEntity();
        this.fieldWorker = fieldWorkerService.getUnknownEntity();
    }

    @Override
    public void generateData(int size) {
        generateUnknowns();
        addFamilyToEach(locationService.findAll(new Sort("uuid")), size);
    }

    @Override
    public void generateData() {
        generateData(0);
    }

    @Override
    public void clearData() {
        relationshipRepository.deleteAllInBatch();
        membershipRepository.deleteAllInBatch();
        residencyRepository.deleteAllInBatch();
        individualRepository.deleteAllInBatch();
        socialGroupRepository.deleteAllInBatch();
    }

    // trigger services to create unknown entities ahead of time, for predictable entity counts
    private void generateUnknowns() {
        individualService.getUnknownEntity();
        socialGroupService.getUnknownEntity();
        residencyService.getUnknownEntity();
        relationshipService.getUnknownEntity();
        membershipService.getUnknownEntity();
    }

    // add a family at each location
    private void addFamilyToEach(Iterable<Location> locations, int size) {
        if (socialGroupService.hasRecords()
                || individualService.hasRecords()
                || residencyService.hasRecords()
                || membershipService.hasRecords()
                || relationshipService.hasRecords()) {
            return;
        }

        for (Location location : locations) {
            addFamily(location, size);
        }
    }

    // add a family at the given location
    private void addFamily(Location location, int size) {
        SocialGroup socialGroup = generateSocialGroup(location.getExtId());

        // always create the group head, then add more family members
        Individual head = generateIndividual(location.getName() + "-head", "FEMALE");
        generateRelationship(head, head, "self");
        generateMembership(head, socialGroup, "self");
        generateResidency(head, location);

        for (int i = 1; i < size; i++) {
            // mix of male and female
            String gender;
            if (0 == i%2) {
                gender = "FEMALE";
            } else {
                gender = "MALE";
            }

            Individual member = generateIndividual(location.getName() + "-member", gender);
            generateRelationship(member, head, "household-member");
            generateMembership(member, socialGroup, "household-member");
            generateResidency(member, location);
        }
    }

    private SocialGroup generateSocialGroup(String name) {
        SocialGroup socialGroup = new SocialGroup();
        setAuditableFields(socialGroup);
        setCollectedFields(socialGroup);

        socialGroup.setExtId(name);
        socialGroup.setGroupName(name);
        socialGroup.setGroupType("sample-household");

        // save using repository, not service, for performance
        return socialGroupRepository.save(socialGroup);
    }

    private Individual generateIndividual(String name, String gender) {
        Individual individual = new Individual();
        setAuditableFields(individual);
        setCollectedFields(individual);

        individual.setExtId(name);
        individual.setFirstName(name);
        individual.setLastName(name);
        individual.setGender(gender);

        // save using repository, not service, for performance
        return individualRepository.save(individual);
    }

    private void generateRelationship(Individual a, Individual b, String type) {
        Relationship relationship = new Relationship();
        setAuditableFields(relationship);
        setCollectedFields(relationship);

        relationship.setIndividualA(a);
        relationship.setIndividualB(b);
        relationship.setRelationshipType(type);
        relationship.setStartDate(ZonedDateTime.now());

        // save using repository, not service, for performance
        relationshipRepository.save(relationship);
    }

    private void generateMembership(Individual individual, SocialGroup socialGroup, String relationToHead) {
        Membership membership = new Membership();
        setAuditableFields(membership);
        setCollectedFields(membership);

        membership.setIndividual(individual);
        membership.setSocialGroup(socialGroup);
        membership.setStartDate(ZonedDateTime.now());
        membership.setStartType("generated-sample");

        // save using repository, not service, for performance
        membershipRepository.save(membership);
    }

    private void generateResidency(Individual individual, Location location) {
        Residency residency = new Residency();
        setAuditableFields(residency);
        setCollectedFields(residency);

        residency.setIndividual(individual);
        residency.setLocation(location);
        residency.setStartDate(ZonedDateTime.now());
        residency.setStartType("generated-sample");

        // save using repository, not service, for performance
        residencyRepository.save(residency);
    }

    private void setAuditableFields(AuditableEntity entity) {
        ZonedDateTime now = ZonedDateTime.now();

        //Check to see if we're creating or updating the entity
        if (null == entity.getInsertDate()) {
            entity.setInsertDate(now);
        }

        if (null == entity.getInsertBy()) {
            entity.setInsertBy(user);
        }

        entity.setLastModifiedDate(now);
        entity.setLastModifiedBy(user);
    }

    private void setCollectedFields(AuditableCollectedEntity entity) {
        entity.setCollectedBy(fieldWorker);
        entity.setCollectionDateTime(ZonedDateTime.now());
    }

}
