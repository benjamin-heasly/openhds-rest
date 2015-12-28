package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.PregnancyObservation;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.update.PregnancyObservationRegistration;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.update.PregnancyObservationService;
import org.openhds.service.impl.update.VisitService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Ben on 5/26/15.
 */
public class PregnancyObservationRestControllerTest extends AuditableCollectedRestControllerTest<
        PregnancyObservation, PregnancyObservationService, PregnancyObservationRestController> {
    
    @Autowired
    private IndividualService individualService;

    @Autowired
    private VisitService visitService;

    @Autowired
    @Override
    protected void initialize(PregnancyObservationService service, PregnancyObservationRestController controller) {
        this.service = service;
        this.controller = controller;
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

}
