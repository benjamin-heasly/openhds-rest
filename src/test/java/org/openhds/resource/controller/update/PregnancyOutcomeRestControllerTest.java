package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.PregnancyOutcome;
import org.openhds.repository.concrete.FieldWorkerRepository;
import org.openhds.repository.concrete.census.IndividualRepository;
import org.openhds.repository.concrete.update.PregnancyResultRepository;
import org.openhds.repository.concrete.update.VisitRepository;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.update.PregnancyOutcomeRegistration;
import org.openhds.service.impl.update.PregnancyOutcomeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Wolfe on 7/15/2015.
 */
public class PregnancyOutcomeRestControllerTest extends AuditableCollectedRestControllerTest<
        PregnancyOutcome, PregnancyOutcomeService, PregnancyOutcomeRestController> {

    @Autowired
    private PregnancyResultRepository pregnancyResultRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private IndividualRepository individualRepository;

    @Autowired
    private FieldWorkerRepository fieldWorkerRepository;

    @Override
    @Autowired
    protected void initialize(PregnancyOutcomeService service, PregnancyOutcomeRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected PregnancyOutcome makeValidEntity(String name, String id) {
        PregnancyOutcome pregnancyOutcome = new PregnancyOutcome();
        pregnancyOutcome.setUuid(id);
        pregnancyOutcome.setOutcomeDate(ZonedDateTime.now().minusYears(1));

        pregnancyOutcome.setPregnancyResults(pregnancyResultRepository.findAll());
        pregnancyOutcome.setMother(individualRepository.findAll().get(0));
        pregnancyOutcome.setFather(individualRepository.findAll().get(0));

        pregnancyOutcome.setVisit(visitRepository.findAll().get(0));

        pregnancyOutcome.setCollectedBy(fieldWorkerRepository.findAll().get(0));
        pregnancyOutcome.setCollectionDateTime(ZonedDateTime.now());

        return pregnancyOutcome;
    }

    @Override
    protected PregnancyOutcome makeInvalidEntity() {
        return new PregnancyOutcome();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(PregnancyOutcome entity, String name, String id) {
        assertNotNull(entity);

        PregnancyOutcome savedPregnancyOutcome = service.findOne(id);
        assertNotNull(savedPregnancyOutcome);

        assertEquals(id, savedPregnancyOutcome.getUuid());
        assertEquals(id, entity.getUuid());
    }

    @Override
    protected Registration<PregnancyOutcome> makeRegistration(PregnancyOutcome entity) {
        PregnancyOutcomeRegistration registration = new PregnancyOutcomeRegistration();
        registration.setPregnancyOutcome(entity);

        registration.setMotherUuid(individualRepository.findAll().get(0).getUuid());
        registration.setFatherUuid(individualRepository.findAll().get(0).getUuid());
        registration.setVisitUuid(visitRepository.findAll().get(0).getUuid());
        registration.setCollectedByUuid(fieldWorkerRepository.findAll().get(0).getUuid());

        return registration;
    }

}
