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

}
