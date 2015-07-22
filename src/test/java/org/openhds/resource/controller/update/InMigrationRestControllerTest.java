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

import java.time.ZonedDateTime;

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
    protected InMigration makeValidEntity(String name, String id) {
        InMigration inMigration = new InMigration();
        inMigration.setUuid(id);

        inMigration.setCollectedBy(fieldWorkerService.findAll(UUID_SORT).toList().get(0));
        inMigration.setCollectionDateTime(ZonedDateTime.now());

        inMigration.setVisit(visitService.findAll(UUID_SORT).toList().get(0));
        inMigration.setIndividual(individualService.findAll(UUID_SORT).toList().get(0));
        inMigration.setResidency(residencyService.findAll(UUID_SORT).toList().get(0));
        inMigration.setMigrationDate(ZonedDateTime.now().minusYears(1));
        inMigration.setMigrationType(name);

        return inMigration;
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

    @Override
    protected Registration<InMigration> makeRegistration(InMigration entity) {
        InMigrationRegistration registration = new InMigrationRegistration();
        registration.setInMigration(entity);

        registration.setCollectedByUuid(fieldWorkerService.findAll(UUID_SORT).toList().get(0).getUuid());
        registration.setVisitUuid(visitService.findAll(UUID_SORT).toList().get(0).getUuid());
        registration.setIndividualUuid(individualService.findAll(UUID_SORT).toList().get(0).getUuid());
        registration.setResidencyUuid(residencyService.findAll(UUID_SORT).toList().get(0).getUuid());

        return registration;
    }
}
