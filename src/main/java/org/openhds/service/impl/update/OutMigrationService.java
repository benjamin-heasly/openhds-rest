package org.openhds.service.impl.update;

import org.openhds.domain.model.update.OutMigration;
import org.openhds.repository.concrete.update.OutMigationRepository;
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
public class OutMigrationService extends AbstractAuditableCollectedService<OutMigration, OutMigationRepository> {

    @Autowired
    VisitService visitService;

    @Autowired
    IndividualService individualService;

    @Autowired
    ResidencyService residencyService;

    @Autowired
    public OutMigrationService(OutMigationRepository repository) {
        super(repository);
    }

    @Override
    protected OutMigration makeUnknownEntity() {
        OutMigration outMigration = new OutMigration();
        outMigration.setCollectedBy(fieldWorkerService.getUnknownEntity());
        outMigration.setCollectionDateTime(ZonedDateTime.now());

        outMigration.setVisit(visitService.getUnknownEntity());
        outMigration.setIndividual(individualService.getUnknownEntity());
        outMigration.setResidency(residencyService.getUnknownEntity());

        outMigration.setMigrationDate(ZonedDateTime.now().minusYears(1));
        outMigration.setReason("unknown");
        outMigration.setDestination("unknown");

        return outMigration;
    }
}
