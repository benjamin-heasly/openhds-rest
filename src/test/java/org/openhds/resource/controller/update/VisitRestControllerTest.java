package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.Visit;
import org.openhds.resource.contract.AuditableExtIdRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.update.VisitRegistration;
import org.openhds.service.impl.census.LocationService;
import org.openhds.service.impl.update.VisitService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Ben on 5/26/15.
 */
public class VisitRestControllerTest extends AuditableExtIdRestControllerTest
        <Visit, VisitService, VisitRestController> {

    @Autowired
    private LocationService locationService;

    @Autowired
    @Override
    protected void initialize(VisitService service, VisitRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected Visit makeInvalidEntity() {
        return new Visit();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(Visit entity, String name, String id) {
        assertNotNull(entity);

        Visit savedVisit = service.findOne(id);
        assertNotNull(savedVisit);

        assertEquals(id, savedVisit.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getExtId(), savedVisit.getExtId());
    }

    @Override
    protected Registration<Visit> makeRegistration(Visit entity) {
        VisitRegistration registration = new VisitRegistration();
        registration.setVisit(entity);
        registration.setCollectedByUuid(fieldWorkerService.findAll(UUID_SORT).toList().get(0).getUuid());
        registration.setLocationUuid(locationService.findAll(UUID_SORT).toList().get(0).getUuid());
        return registration;
    }
}
