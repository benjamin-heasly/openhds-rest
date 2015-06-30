package org.openhds.resource.controller;

import org.openhds.domain.model.Location;
import org.openhds.repository.concrete.FieldWorkerRepository;
import org.openhds.repository.concrete.LocationHierarchyRepository;
import org.openhds.repository.concrete.LocationRepository;
import org.openhds.resource.contract.AuditableExtIdRestControllerTest;
import org.openhds.resource.registration.LocationRegistration;
import org.openhds.resource.registration.Registration;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Ben on 5/26/15.
 */
public class LocationRestControllerTest extends AuditableExtIdRestControllerTest<Location, LocationRepository, LocationRestController> {

    @Autowired
    private FieldWorkerRepository fieldWorkerRepository;

    @Autowired
    private LocationHierarchyRepository locationHierarchyRepository;

    @Autowired
    @Override
    protected void initialize(LocationRepository repository, LocationRestController controller) {
        this.repository = repository;
        this.controller = controller;
    }

    @Override
    protected Location makeValidEntity(String name, String id) {
        Location location = new Location();
        location.setUuid(id);
        location.setName(name);
        location.setExtId(name);
        location.setCollectionDateTime(ZonedDateTime.now());
        return location;
    }

    @Override
    protected Location makeInvalidEntity() {
        return new Location();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(Location entity, String name, String id) {
        assertNotNull(entity);

        Location savedLocation = repository.findOne(id);
        assertNotNull(savedLocation);

        assertEquals(id, savedLocation.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getName(), savedLocation.getName());

    }

    @Override
    protected Registration<Location> makeRegistration(Location entity) {
        LocationRegistration registration = new LocationRegistration();
        registration.setLocation(entity);
        registration.setLocationHierarchyUuid(locationHierarchyRepository.findAll().get(0).getUuid());
        registration.setCollectedByUuid(fieldWorkerRepository.findAll().get(0).getUuid());
        return registration;
    }
}
