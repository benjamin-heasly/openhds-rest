package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.repository.concrete.FieldWorkerRepository;
import org.openhds.repository.concrete.census.LocationHierarchyLevelRepository;
import org.openhds.resource.contract.AuditableExtIdRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.census.LocationHierarchyRegistration;
import org.openhds.service.impl.census.LocationHierarchyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Ben on 5/26/15.
 */
public class LocationHierarchyRestControllerTest extends AuditableExtIdRestControllerTest<
        LocationHierarchy,
        LocationHierarchyService,
        LocationHierarchyRestController> {

    private static final Sort UUID_SORT = new Sort("uuid");

    @Autowired
    private LocationHierarchyLevelRepository locationHierarchyLevelRepository;

    @Autowired
    private FieldWorkerRepository fieldWorkerRepository;

    @Autowired
    @Override
    protected void initialize(LocationHierarchyService service, LocationHierarchyRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected LocationHierarchy makeValidEntity(String name, String id) {
        LocationHierarchy locationHierarchy = new LocationHierarchy();
        locationHierarchy.setUuid(id);
        locationHierarchy.setName(name);
        locationHierarchy.setExtId(name);
        locationHierarchy.setLevel(locationHierarchyLevelRepository.findAll().get(0));
        locationHierarchy.setCollectionDateTime(ZonedDateTime.now());
        locationHierarchy.setCollectedBy(fieldWorkerRepository.findAll().get(0));
        locationHierarchy.setParent(service.findAll(UUID_SORT).toList().get(0));
        return locationHierarchy;
    }

    @Override
    protected LocationHierarchy makeInvalidEntity() {
        return new LocationHierarchy();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(LocationHierarchy entity, String name, String id) {
        assertNotNull(entity);

        LocationHierarchy savedLocationHierarchy = service.findOne(id);
        assertNotNull(savedLocationHierarchy);

        assertEquals(id, savedLocationHierarchy.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getName(), savedLocationHierarchy.getName());

    }

    @Override
    protected Registration<LocationHierarchy> makeRegistration(LocationHierarchy entity) {
        LocationHierarchyRegistration registration = new LocationHierarchyRegistration();
        registration.setLocationHierarchy(entity);
        registration.setLevelUuid(locationHierarchyLevelRepository.findAll().get(0).getUuid());
        registration.setCollectedByUuid(fieldWorkerRepository.findAll().get(0).getUuid());
        return registration;
    }
}
