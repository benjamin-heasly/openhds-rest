package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.PregnancyOutcome;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.update.PregnancyOutcomeRegistration;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.update.PregnancyOutcomeService;
import org.openhds.service.impl.update.PregnancyResultService;
import org.openhds.service.impl.update.VisitService;
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
    private PregnancyResultService pregnancyResultService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private IndividualService individualService;

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

        pregnancyOutcome.setPregnancyResults(pregnancyResultService.findAll(UUID_SORT).toList());
        pregnancyOutcome.setMother(individualService.findAll(UUID_SORT).toList().get(0));
        pregnancyOutcome.setFather(individualService.findAll(UUID_SORT).toList().get(0));

        pregnancyOutcome.setVisit(visitService.findAll(UUID_SORT).toList().get(0));

        pregnancyOutcome.setCollectedBy(fieldWorkerService.findAll(UUID_SORT).toList().get(0));
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

        registration.setMotherUuid(individualService.findAll(UUID_SORT).toList().get(0).getUuid());
        registration.setFatherUuid(individualService.findAll(UUID_SORT).toList().get(0).getUuid());
        registration.setVisitUuid(visitService.findAll(UUID_SORT).toList().get(0).getUuid());
        registration.setCollectedByUuid(fieldWorkerService.findAll(UUID_SORT).toList().get(0).getUuid());

        return registration;
    }

}
