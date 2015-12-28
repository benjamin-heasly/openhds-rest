package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.OutMigration;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.update.OutMigrationRegistration;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.ResidencyService;
import org.openhds.service.impl.update.OutMigrationService;
import org.openhds.service.impl.update.VisitService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Ben on 5/26/15.
 */
public class OutMigrationRestControllerTest extends AuditableCollectedRestControllerTest<
        OutMigration, OutMigrationService, OutMigrationRestController> {
    
    @Autowired
    private IndividualService individualService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private ResidencyService residencyService;

    @Autowired
    @Override
    protected void initialize(OutMigrationService service, OutMigrationRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected OutMigration makeInvalidEntity() {
        return new OutMigration();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(OutMigration entity, String name, String id) {
        assertNotNull(entity);

        OutMigration savedOutMigration = service.findOne(id);
        assertNotNull(savedOutMigration);

        assertEquals(id, savedOutMigration.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getReason(), savedOutMigration.getReason());
    }

}
