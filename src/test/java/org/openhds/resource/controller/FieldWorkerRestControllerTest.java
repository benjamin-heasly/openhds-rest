package org.openhds.resource.controller;

import org.openhds.domain.model.FieldWorker;
import org.openhds.resource.contract.AuditableRestControllerTest;
import org.openhds.resource.registration.FieldWorkerRegistration;
import org.openhds.resource.registration.Registration;
import org.openhds.service.impl.FieldWorkerService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Ben on 5/19/15.
 */
public class FieldWorkerRestControllerTest extends AuditableRestControllerTest<
        FieldWorker, FieldWorkerService, FieldWorkerRestController> {

    @Autowired
    @Override
    protected void initialize(FieldWorkerService service, FieldWorkerRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected FieldWorker makeInvalidEntity() {
        return new FieldWorker();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(FieldWorker entity, String name, String id) {
        assertNotNull(entity);

        FieldWorker savedFieldWorker = service.findOne(id);
        assertNotNull(savedFieldWorker);

        assertEquals(id, savedFieldWorker.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getFieldWorkerId(), savedFieldWorker.getFieldWorkerId());
    }

    @Override
    protected Registration<FieldWorker> makeRegistration(FieldWorker entity) {
        FieldWorkerRegistration registration = new FieldWorkerRegistration();
        registration.setFieldWorker(entity);
        return registration;
    }

}
