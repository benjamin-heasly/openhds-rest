package org.openhds.service.impl.update;

import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.domain.model.update.InMigration;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.update.InMigationRepository;
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
public class InMigrationService extends AbstractAuditableCollectedService<InMigration, InMigationRepository> {

    @Autowired
    private LocationHierarchyService locationHierarchyService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private ResidencyService residencyService;

    @Autowired
    public InMigrationService(InMigationRepository repository) {
        super(repository);
    }

    @Override
    public InMigration makePlaceHolder(String id, String name) {
        InMigration inMigration = new InMigration();
        inMigration.setUuid(id);
        inMigration.setVisit(visitService.getUnknownEntity());
        inMigration.setIndividual(individualService.getUnknownEntity());
        inMigration.setResidency(residencyService.getUnknownEntity());
        inMigration.setMigrationDate(ZonedDateTime.now().minusYears(1));
        inMigration.setMigrationType(name);
        inMigration.setReason(name);
        inMigration.setOrigin(name);

        initPlaceHolderCollectedFields(inMigration);

        return inMigration;
    }

    public InMigration recordInMigration(InMigration inMigration, String individualId, String residencyId, String visitId, String fieldWorkerId){
        inMigration.setIndividual(individualService.findOrMakePlaceHolder(individualId));
        inMigration.setResidency(residencyService.findOrMakePlaceHolder(residencyId));
        inMigration.setVisit(visitService.findOrMakePlaceHolder(visitId));
        inMigration.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));

        return createOrUpdate(inMigration);
    }

    @Override
    public void validate(InMigration entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);

        //TODO: check that migrationDate is not in the future
        //TODO: check that migrated individual is not registered as dead

    }

    @Override
    public Set<LocationHierarchy> findEnclosingLocationHierarchies(InMigration entity) {
        return locationHierarchyService.findEnclosingLocationHierarchies(entity.getVisit()
                .getLocation()
                .getLocationHierarchy());
    }

    @Override
    public Page<InMigration> findByEnclosingLocationHierarchy(Pageable pageable,
                                                              String locationHierarchyUuid,
                                                              ZonedDateTime modifiedAfter,
                                                              ZonedDateTime modifiedBefore) {
        return locationHierarchyService.findOtherByEnclosingLocationHierarchy(pageable,
                locationHierarchyUuid,
                modifiedAfter,
                modifiedBefore,
                InMigrationService::enclosed,
                repository);
    }

    private static Specification<InMigration> enclosed(final List<LocationHierarchy> enclosing) {
        return (root, query, cb) -> root.get("visit")
                .get("location")
                .get("locationHierarchy")
                .in(enclosing);
    }
}
