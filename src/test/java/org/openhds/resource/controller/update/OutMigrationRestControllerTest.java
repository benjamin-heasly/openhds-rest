package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.OutMigration;
import org.openhds.repository.concrete.FieldWorkerRepository;
import org.openhds.repository.concrete.census.IndividualRepository;
import org.openhds.repository.concrete.census.ResidencyRepository;
import org.openhds.repository.concrete.update.VisitRepository;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.update.OutMigrationRegistration;
import org.openhds.service.impl.update.OutMigrationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Ben on 5/26/15.
 */
public class OutMigrationRestControllerTest extends AuditableCollectedRestControllerTest<
        OutMigration, OutMigrationService, OutMigrationRestController> {

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
    protected void initialize(OutMigrationService service, OutMigrationRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected OutMigration makeValidEntity(String name, String id) {
        OutMigration outMigration = new OutMigration();
        outMigration.setUuid(id);

        outMigration.setCollectedBy(fieldWorkerRepository.findAll().get(0));
        outMigration.setCollectionDateTime(ZonedDateTime.now());

        outMigration.setVisit(visitRepository.findAll().get(0));
        outMigration.setIndividual(individualRepository.findAll().get(0));
        outMigration.setResidency(residencyRepository.findAll().get(0));
        outMigration.setMigrationDate(ZonedDateTime.now().minusYears(1));
        outMigration.setReason(name);

        return outMigration;
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

    @Override
    protected Registration<OutMigration> makeRegistration(OutMigration entity) {
        OutMigrationRegistration registration = new OutMigrationRegistration();
        registration.setOutMigration(entity);

        registration.setCollectedByUuid(fieldWorkerRepository.findAll().get(0).getUuid());
        registration.setVisitUuid(visitRepository.findAll().get(0).getUuid());
        registration.setIndividualUuid(individualRepository.findAll().get(0).getUuid());
        registration.setResidencyUuid(residencyRepository.findAll().get(0).getUuid());

        return registration;
    }
}
