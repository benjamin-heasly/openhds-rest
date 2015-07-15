package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.PregnancyResult;
import org.openhds.repository.concrete.FieldWorkerRepository;
import org.openhds.repository.concrete.census.IndividualRepository;
import org.openhds.repository.concrete.update.PregnancyOutcomeRepository;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.update.PregnancyResultRegistration;
import org.openhds.service.impl.update.PregnancyResultService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Wolfe on 7/15/2015.
 */
public class PregnancyResultRestControllerTest extends AuditableCollectedRestControllerTest<
        PregnancyResult,
        PregnancyResultService,
        PregnancyResultRestController> {

    @Autowired
    private PregnancyOutcomeRepository pregnancyOutcomeRepository;

    @Autowired
    private IndividualRepository individualRepository;

    @Autowired
    private FieldWorkerRepository fieldWorkerRepository;

    @Override
    @Autowired
    protected void initialize(PregnancyResultService service, PregnancyResultRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected PregnancyResult makeValidEntity(String name, String id) {
        PregnancyResult pregnancyResult = new PregnancyResult();
        pregnancyResult.setUuid(id);
        pregnancyResult.setType(name);

        pregnancyResult.setPregnancyOutcome(pregnancyOutcomeRepository.findAll().get(0));
        pregnancyResult.setChild(individualRepository.findAll().get(0));

        pregnancyResult.setCollectedBy(fieldWorkerRepository.findAll().get(0));
        pregnancyResult.setCollectionDateTime(ZonedDateTime.now());

        return pregnancyResult;
    }

    @Override
    protected PregnancyResult makeInvalidEntity() {
        return new PregnancyResult();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(PregnancyResult entity, String name, String id) {
        assertNotNull(entity);

        PregnancyResult savedPregnancyResult = service.findOne(id);
        assertNotNull(savedPregnancyResult);

        assertEquals(id, savedPregnancyResult.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getType(), savedPregnancyResult.getType());

    }

    @Override
    protected Registration<PregnancyResult> makeRegistration(PregnancyResult entity) {
        PregnancyResultRegistration registration = new PregnancyResultRegistration();
        registration.setPregnancyResult(entity);

        registration.setChildUuid(individualRepository.findAll().get(0).getUuid());
        registration.setPregnancyOutcomeUuid(pregnancyOutcomeRepository.findAll().get(0).getUuid());
        registration.setCollectedByUuid(fieldWorkerRepository.findAll().get(0).getUuid());

        return registration;
    }

}
