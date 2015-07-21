package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.InMigration;
import org.openhds.repository.concrete.FieldWorkerRepository;
import org.openhds.repository.concrete.census.IndividualRepository;
import org.openhds.repository.concrete.census.ResidencyRepository;
import org.openhds.repository.concrete.update.VisitRepository;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.update.InMigrationRegistration;
import org.openhds.service.impl.update.InMigrationService;
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
    private FieldWorkerRepository fieldWorkerRepository;

    @Autowired
    private IndividualRepository individualRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private ResidencyRepository residencyRepository;

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

        inMigration.setCollectedBy(fieldWorkerRepository.findAll().get(0));
        inMigration.setCollectionDateTime(ZonedDateTime.now());

        inMigration.setVisit(visitRepository.findAll().get(0));
        inMigration.setIndividual(individualRepository.findAll().get(0));
        inMigration.setResidency(residencyRepository.findAll().get(0));
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

        registration.setCollectedByUuid(fieldWorkerRepository.findAll().get(0).getUuid());
        registration.setVisitUuid(visitRepository.findAll().get(0).getUuid());
        registration.setIndividualUuid(individualRepository.findAll().get(0).getUuid());
        registration.setResidencyUuid(residencyRepository.findAll().get(0).getUuid());

        return registration;
    }
}
