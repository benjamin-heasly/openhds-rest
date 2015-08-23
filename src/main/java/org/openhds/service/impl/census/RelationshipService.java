package org.openhds.service.impl.census;

import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.domain.model.census.Relationship;
import org.openhds.domain.model.census.Residency;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.census.RelationshipRepository;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Wolfe on 7/13/2015.
 */
@Service
public class RelationshipService extends AbstractAuditableCollectedService<Relationship, RelationshipRepository> {

    @Autowired
    private IndividualService individualService;

    @Autowired
    private LocationHierarchyService locationHierarchyService;

    @Autowired
    public RelationshipService(RelationshipRepository repository) {
        super(repository);
    }

    @Override
    public Relationship makePlaceHolder(String id, String name) {
        Relationship relationship = new Relationship();
        relationship.setUuid(id);
        relationship.setRelationshipType(name);
        relationship.setStartDate(ZonedDateTime.now());
        relationship.setIndividualA(individualService.getUnknownEntity());
        relationship.setIndividualB(individualService.getUnknownEntity());

        initPlaceHolderCollectedFields(relationship);

        return relationship;
    }

    public Relationship recordRelationship(Relationship relationship, String individualAId, String individualBId, String fieldWorkerId){
        relationship.setIndividualA(individualService.findOrMakePlaceHolder(individualAId));
        relationship.setIndividualB(individualService.findOrMakePlaceHolder(individualBId));
        relationship.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        return createOrUpdate(relationship);
    }

    @Override
    public void validate(Relationship entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);
    }

    // all hierarchies associated with active residencies for either individual
    @Override
    public Set<LocationHierarchy> findEnclosingLocationHierarchies(Relationship entity) {
        Set<LocationHierarchy> locationHierarchies = new HashSet<>();

        for (Residency residency : entity.getIndividualA().collectActiveResidencies(new HashSet<>())) {
            locationHierarchies.addAll(locationHierarchyService.findEnclosingLocationHierarchies(residency.getLocation().getParent()));
        }

        for (Residency residency : entity.getIndividualB().collectActiveResidencies(new HashSet<>())) {
            locationHierarchies.addAll(locationHierarchyService.findEnclosingLocationHierarchies(residency.getLocation().getParent()));
        }

        return locationHierarchies;
    }

    @Override
    public Page<Relationship> findByEnclosingLocationHierarchy(Pageable pageable,
                                                               String locationHierarchyUuid,
                                                               ZonedDateTime modifiedAfter,
                                                               ZonedDateTime modifiedBefore) {
        return locationHierarchyService.findOtherByEnclosingLocationHierarchy(pageable,
                locationHierarchyUuid,
                modifiedAfter,
                modifiedBefore,
                RelationshipService::enclosed,
                repository);
    }

    // relationships where either individual has an active residency at an enclosed location
    private static Specification<Relationship> enclosed(final List<LocationHierarchy> enclosing) {
        return (root, query, cb) -> {
            Predicate individualAPredicate = hasEnclosedResidency(root.join("individualA"),
                    query,
                    cb,
                    enclosing);
            Predicate individualBPredicate = hasEnclosedResidency(root.join("individualB"),
                    query,
                    cb,
                    enclosing);
            return cb.or(individualAPredicate, individualBPredicate);
        };
    }

    private static Predicate hasEnclosedResidency(Join<Relationship, Individual> individualJoin,
                                                  CriteriaQuery query,
                                                  CriteriaBuilder cb,
                                                  final List<LocationHierarchy> enclosing) {

        Join<Individual, Residency> residencyJoin = individualJoin.join("residencies");

        Subquery<Residency> subquery = query.subquery(Residency.class);
        subquery.from(Residency.class);
        subquery.select(residencyJoin);
        subquery.where(cb.and(cb.isNull(residencyJoin.get("endDate")),
                residencyJoin.get("location").get("locationHierarchy").in(enclosing)));

        return cb.exists(subquery);
    }

}
