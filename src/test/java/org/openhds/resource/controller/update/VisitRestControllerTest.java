package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.Visit;
import org.openhds.repository.concrete.FieldWorkerRepository;
import org.openhds.repository.concrete.census.LocationRepository;
import org.openhds.resource.contract.AuditableExtIdRestControllerTest;
import org.openhds.resource.controller.update.VisitRestController;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.VisitRegistration;
import org.openhds.service.impl.update.VisitService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Ben on 5/26/15.
 */
public class VisitRestControllerTest extends AuditableExtIdRestControllerTest
        <Visit, VisitService, VisitRestController> {

    @Autowired
    private FieldWorkerRepository fieldWorkerRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    @Override
    protected void initialize(VisitService service, VisitRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected Visit makeValidEntity(String name, String id) {
        Visit visit = new Visit();
        visit.setUuid(id);

        visit.setExtId(name);
        visit.setCollectedBy(fieldWorkerRepository.findAll().get(0));
        visit.setCollectionDateTime(ZonedDateTime.now());

        visit.setLocation(locationRepository.findAll().get(0));
        visit.setVisitDate(ZonedDateTime.now().minusYears(1));

        return visit;
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
        registration.setCollectedByUuid(fieldWorkerRepository.findAll().get(0).getUuid());
        registration.setLocationUuid(locationRepository.findAll().get(0).getUuid());
        return registration;
    }
}
