package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.PregnancyResult;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.update.PregnancyResultRegistration;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.update.PregnancyOutcomeService;
import org.openhds.service.impl.update.PregnancyResultService;
import org.springframework.beans.factory.annotation.Autowired;

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
    private PregnancyOutcomeService pregnancyOutcomeService;

    @Autowired
    private IndividualService individualService;

    @Override
    @Autowired
    protected void initialize(PregnancyResultService service, PregnancyResultRestController controller) {
        this.service = service;
        this.controller = controller;
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

}
