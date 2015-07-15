package org.openhds.service.impl.update;

import org.openhds.domain.model.update.InMigration;
import org.openhds.service.AuditableCollectedServiceTest;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.ResidencyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/13/15.
 */
public class InMigrationServiceTest extends AuditableCollectedServiceTest<InMigration, InMigrationService> {

    @Autowired
    private VisitService visitService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private ResidencyService residencyService;

    @Autowired
    @Override
    protected void initialize(InMigrationService service) {
        this.service = service;
    }

    @Override
    protected InMigration makeInvalidEntity() {
        return new InMigration();
    }

    @Override
    protected InMigration makeValidEntity(String name, String id) {
        InMigration inMigration = new InMigration();
        inMigration.setUuid(id);
        inMigration.setVisit(visitService.findAll(UUID_SORT).toList().get(0));
        inMigration.setIndividual(individualService.findAll(UUID_SORT).toList().get(0));
        inMigration.setResidency(residencyService.findAll(UUID_SORT).toList().get(0));
        inMigration.setMigrationDate(ZonedDateTime.now().minusYears(1));
        inMigration.setMigrationType(name);

        initCollectedFields(inMigration);

        return inMigration;
    }
}
