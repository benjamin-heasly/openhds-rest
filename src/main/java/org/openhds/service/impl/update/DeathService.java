package org.openhds.service.impl.update;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.domain.model.census.Individual;
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
        death.setEntityStatus(name);
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
        death.setEntityStatus(death.NORMAL_STATUS);
        return createOrUpdate(death);
    }

    @Override
    public void validate(Death death, ErrorLog errorLog) {
        super.validate(death, errorLog);

        if(death.getDeathDate().isAfter(death.getCollectionDateTime())){
          errorLog.appendError("Death cannot have a deathDate in the future.");
        }

        Individual deadIndividual = death.getIndividual();
        if(deadIndividual.getEntityStatus().equals(AuditableEntity.NORMAL_STATUS) && !death.getIndividual().hasOpenResidency()){
          errorLog.appendError("Individual must have an open residency to be recorded as dead.");
        }

        Death existingDeath = death.getIndividual().getDeath();
        if(null != existingDeath
            && existingDeath.getEntityStatus().equals(AuditableEntity.NORMAL_STATUS)
            &&  null != death.getUuid()
            && !existingDeath.equals(death)){
          errorLog.appendError("Individual cannot have multiple deaths.");
        }
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
