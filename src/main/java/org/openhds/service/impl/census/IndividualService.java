package org.openhds.service.impl.census;

import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.ProjectCode;
import org.openhds.domain.model.census.*;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.census.IndividualRepository;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.management.relation.Relation;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Subquery;
import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Wolfe on 7/13/2015.
 */
@Service
public class IndividualService extends AbstractAuditableExtIdService<Individual, IndividualRepository>{

    @Autowired
    private LocationHierarchyService locationHierarchyService;
    @Autowired
    private RelationshipService relationshipService;
    @Autowired
    private ResidencyService residencyService;
    @Autowired
    private MembershipService membershipService;
    @Autowired
    private SocialGroupService socialGroupService;
    @Autowired
    private LocationService locationService;


    @Autowired
    public IndividualService(IndividualRepository repository) {
        super(repository);
    }

    @Override
    public Individual makePlaceHolder(String id, String name) {
        Individual individual = new Individual();
        individual.setUuid(id);
        individual.setEntityStatus(name);
        individual.setFirstName(name);
        individual.setExtId(name);
        individual.setGender("FEMALE");

        initPlaceHolderCollectedFields(individual);

        return individual;
    }

    public Individual recordIndividual(Individual individual, String fieldWorkerId) {
        individual.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        individual.setEntityStatus(individual.NORMAL_STATUS);
        return createOrUpdate(individual);
    }

    public Individual recordIndividual(Individual individual,
                                       ZonedDateTime recordTime,
                                       String relationToHead,
                                       String headOfHouseholdUuid,
                                       String relationshipUuid,
                                       String locationUuid,
                                       String socialGroupUuid,
                                       String fieldWorkerUuid,
                                       String motherUuid,
                                       String fatherUuid,
                                       String membershipUuid,
                                       String residencyUuid){

        String startType = "individualRegistration";

        FieldWorker collectedBy = fieldWorkerService.findOrMakePlaceHolder(fieldWorkerUuid);
        ZonedDateTime collectionDateTime = individual.getCollectionDateTime();

        individual.setMother(findOrMakePlaceHolder(motherUuid));
        individual.setFather(findOrMakePlaceHolder(fatherUuid));
        individual.setCollectedBy(collectedBy);
        individual.setStatusMessage(individual.NORMAL_STATUS);
        individual = createOrUpdate(individual);

        Relationship relationship = relationshipService.findOrMakePlaceHolder(relationshipUuid);
        Individual headOfHousehold = findOrMakePlaceHolder(headOfHouseholdUuid);
        relationship.setIndividualA(individual);
        relationship.setIndividualB(headOfHousehold);
        relationship.setRelationshipType(relationToHead);
        relationship.setCollectedBy(collectedBy);
        relationship.setCollectionDateTime(collectionDateTime);
        relationship.setStartDate(recordTime);
        relationship.setEntityStatus(relationship.NORMAL_STATUS);
        relationshipService.createOrUpdate(relationship);

        Membership membership = membershipService.findOrMakePlaceHolder(membershipUuid);
        membership.setIndividual(individual);
        membership.setSocialGroup(socialGroupService.findOrMakePlaceHolder(socialGroupUuid));
        membership.setStartType(startType);
        membership.setStartDate(recordTime);
        membership.setEntityStatus(membership.NORMAL_STATUS);
        membership.setCollectedBy(collectedBy);
        membership.setCollectionDateTime(collectionDateTime);
        membershipService.createOrUpdate(membership);

        Residency residency = residencyService.makePlaceHolder(residencyUuid);
        residency.setIndividual(individual);
        residency.setLocation(locationService.findOrMakePlaceHolder(locationUuid));
        residency.setCollectedBy(collectedBy);
        residency.setCollectionDateTime(collectionDateTime);
        residency.setStartType(startType);
        residency.setStartDate(recordTime);
        residency.setEntityStatus(residency.NORMAL_STATUS);
        residencyService.createOrUpdate(residency);

        return individual;
    }

    @Override
    public void validate(Individual individual, ErrorLog errorLog) {
        super.validate(individual, errorLog);

        if(null != individual.getFather() &&
            !individual.getFather().getGender().equals(projectCodeService.getValueForCodeName(ProjectCode.GENDER_MALE))){
            errorLog.appendError("Individual cannot have a non-male Father.");
        }

        if(null != individual.getMother() &&
            !individual.getMother().getGender().equals(projectCodeService.getValueForCodeName(ProjectCode.GENDER_FEMALE))){
            errorLog.appendError("Individual cannot have a non-female Mother.");
        }

        if(null != individual.getDateOfBirth() &&
            individual.getDateOfBirth().isAfter(individual.getCollectionDateTime())){
            errorLog.appendError("Individual cannot have a birthday in the future.");
        }

        if(!projectCodeService.isValueInCodeGroup(individual.getGender(), ProjectCode.GENDER)){
            errorLog.appendError("Individual cannot have a gender of: ["+individual.getGender()+"].");
        }

    }

    // all hierarchies associated with active residencies
    @Override
    public Set<LocationHierarchy> findEnclosingLocationHierarchies(Individual entity) {
        Set<LocationHierarchy> locationHierarchies = new HashSet<>();
        for (Residency residency : entity.collectActiveResidencies(new HashSet<>())) {
            locationHierarchies.addAll(locationHierarchyService.findEnclosingLocationHierarchies(residency.getLocation().getLocationHierarchy()));
        }
        return locationHierarchies;
    }

    @Override
    public Page<Individual> findByEnclosingLocationHierarchy(Pageable pageable,
                                                             String locationHierarchyUuid,
                                                             ZonedDateTime modifiedAfter,
                                                             ZonedDateTime modifiedBefore) {
        return locationHierarchyService.findOtherByEnclosingLocationHierarchy(pageable,
                locationHierarchyUuid,
                modifiedAfter,
                modifiedBefore,
                IndividualService::enclosed,
                repository);
    }

    // individuals with an active residency at an enclosed location
    private static Specification<Individual> enclosed(final List<LocationHierarchy> enclosing) {
        return (root, query, cb) -> {
            Join<Individual, Residency> residencyJoin = root.join("residencies");
            Subquery<Residency> subquery = query.subquery(Residency.class);
            subquery.from(Residency.class);
            subquery.select(residencyJoin);
            subquery.where(cb.and(cb.isNull(residencyJoin.get("endDate")),
                    residencyJoin.get("location").get("locationHierarchy").in(enclosing)));
            return cb.exists(subquery);
        };
    }
}
