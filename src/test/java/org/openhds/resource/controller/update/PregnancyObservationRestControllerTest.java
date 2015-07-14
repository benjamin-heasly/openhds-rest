package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.PregnancyObservation;
import org.openhds.repository.concrete.FieldWorkerRepository;
import org.openhds.repository.concrete.census.IndividualRepository;
import org.openhds.repository.concrete.update.VisitRepository;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.update.PregnancyObservationRegistration;
import org.openhds.service.impl.update.PregnancyObservationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Ben on 5/26/15.
 */
public class PregnancyObservationRestControllerTest extends AuditableCollectedRestControllerTest<
        PregnancyObservation, PregnancyObservationService, PregnancyObservationRestController> {

    @Autowired
    private FieldWorkerRepository fieldWorkerRepository;

    @Autowired
    private IndividualRepository individualRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    @Override
    protected void initialize(PregnancyObservationService service, PregnancyObservationRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected PregnancyObservation makeValidEntity(String name, String id) {
        PregnancyObservation pregnancyObservation = new PregnancyObservation();
        pregnancyObservation.setUuid(id);

        pregnancyObservation.setCollectedBy(fieldWorkerRepository.findAll().get(0));
        pregnancyObservation.setCollectionDateTime(ZonedDateTime.now());

        pregnancyObservation.setVisit(visitRepository.findAll().get(0));
        pregnancyObservation.setMother(individualRepository.findAll().get(0));
        pregnancyObservation.setPregnancyDate(ZonedDateTime.now().minusMonths(5));
        pregnancyObservation.setExpectedDeliveryDate(ZonedDateTime.now().plusMonths(5));

        pregnancyObservation.setStatusMessage(name);

        return pregnancyObservation;
    }

    @Override
    protected PregnancyObservation makeInvalidEntity() {
        return new PregnancyObservation();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(PregnancyObservation entity, String name, String id) {
        assertNotNull(entity);

        PregnancyObservation savedInMigration = service.findOne(id);
        assertNotNull(savedInMigration);

        assertEquals(id, savedInMigration.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getStatusMessage(), savedInMigration.getStatusMessage());
    }

    @Override
    protected Registration<PregnancyObservation> makeRegistration(PregnancyObservation entity) {
        PregnancyObservationRegistration registration = new PregnancyObservationRegistration();
        registration.setPregnancyObservation(entity);

        registration.setCollectedByUuid(fieldWorkerRepository.findAll().get(0).getUuid());
        registration.setVisitUuid(visitRepository.findAll().get(0).getUuid());
        registration.setMotherUuid(individualRepository.findAll().get(0).getUuid());

        return registration;
    }
}
