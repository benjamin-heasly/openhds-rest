package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.InMigration;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.update.InMigrationRegistration;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.ResidencyService;
import org.openhds.service.impl.update.InMigrationService;
import org.openhds.service.impl.update.VisitService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Ben on 5/26/15.
 */
public class InMigrationRestControllerTest extends AuditableCollectedRestControllerTest<
        InMigration, InMigrationService, InMigrationRestController> {

    @Autowired
    private IndividualService individualService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private ResidencyService residencyService;

    @Autowired
    @Override
    protected void initialize(InMigrationService service, InMigrationRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected InMigration makeInvalidEntity() {
        return new InMigration();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(InMigration entity, String name, String id) {
        assertNotNull(entity);

        InMigration savedInMigration = service.findOne(id);
        assertNotNull(savedInMigration);

        assertEquals(id, savedInMigration.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getMigrationType(), savedInMigration.getMigrationType());
    }

}
