package org.openhds.service.impl.update;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.domain.model.update.OutMigration;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.update.OutMigationRepository;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.LocationHierarchyService;
import org.openhds.service.impl.census.ResidencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

/**
 * Created by bsh on 7/13/15.
 */
@Service
public class OutMigrationService extends AbstractAuditableCollectedService<OutMigration, OutMigationRepository> {

    @Autowired
    private LocationHierarchyService locationHierarchyService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private ResidencyService residencyService;

    @Autowired
    public OutMigrationService(OutMigationRepository repository) {
        super(repository);
    }

    @Override
    public OutMigration makePlaceHolder(String id, String name) {
        OutMigration outMigration = new OutMigration();
        outMigration.setUuid(id);
        outMigration.setStatus(name);
        outMigration.setVisit(visitService.getUnknownEntity());
        outMigration.setIndividual(individualService.getUnknownEntity());
        outMigration.setResidency(residencyService.getUnknownEntity());
        outMigration.setMigrationDate(ZonedDateTime.now().minusYears(1));
        outMigration.setReason(name);
        outMigration.setDestination(name);

        initPlaceHolderCollectedFields(outMigration);

        return outMigration;
    }

    public OutMigration recordOutMigration(OutMigration outMigration, String individualId, String residencyId, String visitId, String fieldWorkerId) {
        outMigration.setIndividual(individualService.findOrMakePlaceHolder(individualId));
        outMigration.setResidency(residencyService.findOrMakePlaceHolder(residencyId));
        outMigration.setVisit(visitService.findOrMakePlaceHolder(visitId));
        outMigration.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        outMigration.setStatus(outMigration.NORMAL_STATUS);
        return createOrUpdate(outMigration);
    }

    @Override
    public void validate(OutMigration outMigration, ErrorLog errorLog) {
        super.validate(outMigration, errorLog);

        if(outMigration.getMigrationDate().isAfter(outMigration.getCollectionDateTime())){
          errorLog.appendError("OutMigration cannot have a migrationDate in the future.");
        }

        if(outMigration.getIndividual().getStatus().equals(AuditableEntity.NORMAL_STATUS) && !outMigration.getIndividual().hasOpenResidency()){
          errorLog.appendError("Individual must have an open residency to a part of an OutMigration.");
        }

        //TODO: not working with current data generation design
        if(outMigration.getIndividual().getStatus().equals(AuditableEntity.NORMAL_STATUS) && null != outMigration.getIndividual().getDeath()){
          errorLog.appendError("Individual cannot be part of an OutMigration if recorded as dead.");
        }

    }

    @Override
    public Set<LocationHierarchy> findEnclosingLocationHierarchies(OutMigration entity) {
        return locationHierarchyService.findEnclosingLocationHierarchies(entity.getVisit()
                .getLocation()
                .getLocationHierarchy());
    }

    @Override
    public Page<OutMigration> findByEnclosingLocationHierarchy(Pageable pageable,
                                                               String locationHierarchyUuid,
                                                               ZonedDateTime modifiedAfter,
                                                               ZonedDateTime modifiedBefore) {
        return locationHierarchyService.findOtherByEnclosingLocationHierarchy(pageable,
                locationHierarchyUuid,
                modifiedAfter,
                modifiedBefore,
                OutMigrationService::enclosed,
                repository);
    }

    private static Specification<OutMigration> enclosed(final List<LocationHierarchy> enclosing) {
        return (root, query, cb) -> root.get("visit")
                .get("location")
                .get("locationHierarchy")
                .in(enclosing);
    }
}
