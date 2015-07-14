package org.openhds.service.impl.update;

import org.openhds.domain.model.update.InMigration;
import org.openhds.repository.concrete.update.InMigationRepository;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.ResidencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/13/15.
 */
@Component
public class InMigrationService extends AbstractAuditableCollectedService<InMigration, InMigationRepository> {

    @Autowired
    FieldWorkerService fieldWorkerService;

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
    protected InMigration makeUnknownEntity() {
        InMigration inMigration = new InMigration();
        inMigration.setCollectedBy(fieldWorkerService.getUnknownEntity());
        inMigration.setCollectionDateTime(ZonedDateTime.now());

        inMigration.setVisit(visitService.getUnknownEntity());
        inMigration.setIndividual(individualService.getUnknownEntity());
        inMigration.setResidency(residencyService.getUnknownEntity());

        inMigration.setMigrationDate(ZonedDateTime.now().minusYears(1));
        inMigration.setMigrationType("unknown");
        inMigration.setReason("unknown");
        inMigration.setOrigin("unknown");

        return inMigration;
    }
}
