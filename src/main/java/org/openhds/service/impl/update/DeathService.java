package org.openhds.service.impl.update;

import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.domain.model.update.Death;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.update.DeathRepository;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.LocationHierarchyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

/**
 * Created by Wolfe on 7/14/2015.
 */
@Service
public class DeathService extends AbstractAuditableCollectedService<Death, DeathRepository> {

    @Autowired
    private LocationHierarchyService locationHierarchyService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private VisitService visitService;

    @Autowired
    public DeathService(DeathRepository repository) {
        super(repository);
    }

    @Override
    public Death makePlaceHolder(String id, String name) {
        Death death = new Death();
        death.setUuid(id);
        death.setIndividual(individualService.getUnknownEntity());
        death.setVisit(visitService.getUnknownEntity());
        death.setDeathDate(ZonedDateTime.now().minusYears(1));
        death.setDeathPlace(name);
        death.setDeathCause(name);

        initPlaceHolderCollectedFields(death);

        return death;
    }

    public Death recordDeath(Death death, String individualId, String visitId, String fieldWorkerId){
        death.setIndividual(individualService.findOrMakePlaceHolder(individualId));
        death.setVisit(visitService.findOrMakePlaceHolder(visitId));
        death.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        return createOrUpdate(death);
    }

    @Override
    public void validate(Death entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);

      //TODO: check that deathDate is not in the future
      //TODO: check that the individual is not already registered as dead
      //TODO: check that the individual has an open residency
    }

    @Override
    public Set<LocationHierarchy> findEnclosingLocationHierarchies(Death entity) {
        return locationHierarchyService.findEnclosingLocationHierarchies(entity.getVisit()
                .getLocation()
                .getLocationHierarchy());
    }

    @Override
    public Page<Death> findByEnclosingLocationHierarchy(Pageable pageable,
                                                        String locationHierarchyUuid,
                                                        ZonedDateTime modifiedAfter,
                                                        ZonedDateTime modifiedBefore) {
        return locationHierarchyService.findOtherByEnclosingLocationHierarchy(pageable,
                locationHierarchyUuid,
                modifiedAfter,
                modifiedBefore,
                DeathService::enclosed,
                repository);
    }

    private static Specification<Death> enclosed(final List<LocationHierarchy> enclosing) {
        return (root, query, cb) -> root.get("visit")
                .get("location")
                .get("locationHierarchy")
                .in(enclosing);
    }
}
