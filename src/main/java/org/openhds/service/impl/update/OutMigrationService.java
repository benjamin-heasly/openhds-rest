package org.openhds.service.impl.update;

import org.openhds.domain.model.update.OutMigration;
import org.openhds.errors.model.ErrorLog;
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
    public OutMigration makePlaceHolder(String id, String name) {
        OutMigration outMigration = new OutMigration();
        outMigration.setUuid(id);
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

        return createOrUpdate(outMigration);
    }

    @Override
    public void validate(OutMigration entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);
    }
}
