package org.openhds.service.impl.update;

import org.openhds.domain.model.update.InMigration;
import org.openhds.repository.concrete.update.InMigationRepository;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.ResidencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/13/15.
 */
@Service
public class InMigrationService extends AbstractAuditableCollectedService<InMigration, InMigationRepository> {

    @Autowired
    VisitService visitService;

    @Autowired
    IndividualService individualService;

    @Autowired
    ResidencyService residencyService;

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
}
