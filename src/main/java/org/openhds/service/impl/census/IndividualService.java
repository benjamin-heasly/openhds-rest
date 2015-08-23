package org.openhds.service.impl.census;

import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.domain.model.census.Residency;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.census.IndividualRepository;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Subquery;
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
    public IndividualService(IndividualRepository repository) {
        super(repository);
    }

    @Override
    public Individual makePlaceHolder(String id, String name) {
        Individual individual = new Individual();
        individual.setUuid(id);
        individual.setFirstName(name);
        individual.setExtId(name);

        initPlaceHolderCollectedFields(individual);

        return individual;
    }

    public Individual recordIndividual(Individual individual, String fieldWorkerId) {
        individual.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));

        //TODO: Handle side effect creation for things like socialgroup and membership etc.

        return createOrUpdate(individual);
    }

    @Override
    public void validate(Individual entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);

        //TODO: check if not null : mother gender female
        //TODO: check if not null : father gender male
        //TODO: check it not null : birthday is not in future
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
