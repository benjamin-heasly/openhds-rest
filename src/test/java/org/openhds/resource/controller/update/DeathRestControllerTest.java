package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.Death;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.update.DeathRegistration;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.update.DeathService;
import org.openhds.service.impl.update.VisitService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Wolfe on 7/14/2015.
 */
public class DeathRestControllerTest extends AuditableCollectedRestControllerTest<Death, DeathService, DeathRestController> {

    @Autowired
    private IndividualService individualService;

    @Autowired
    private VisitService visitService;

    @Override
    @Autowired
    protected void initialize(DeathService service, DeathRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected Death makeInvalidEntity() {
        return new Death();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(Death entity, String name, String id) {
        assertNotNull(entity);

        Death savedDeath = service.findOne(id);
        assertNotNull(savedDeath);

        assertEquals(id, savedDeath.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getDeathDate(), entity.getDeathDate());
    }

    @Override
    protected Registration<Death> makeRegistration(Death entity) {
        DeathRegistration registration = new DeathRegistration();
        registration.setDeath(entity);
        registration.setCollectedByUuid(fieldWorkerService.findAll(UUID_SORT).toList().get(0).getUuid());
        registration.setIndividualUuid(individualService.findAll(UUID_SORT).toList().get(0).getUuid());
        registration.setVisitUuid(visitService.findAll(UUID_SORT).toList().get(0).getUuid());
        return registration;
    }
}
