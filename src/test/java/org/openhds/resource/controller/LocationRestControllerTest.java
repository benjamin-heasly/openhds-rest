package org.openhds.resource.controller;

import org.openhds.domain.model.Location;
import org.openhds.repository.FieldWorkerRepository;
import org.openhds.repository.LocationHierarchyRepository;
import org.openhds.repository.LocationRepository;
import org.openhds.resource.registration.LocationRegistration;
import org.openhds.resource.registration.Registration;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Ben on 5/26/15.
 */
public class LocationRestControllerTest extends AuditableExtIdRestControllerTest<Location> {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private FieldWorkerRepository fieldWorkerRepository;

    @Autowired
    private LocationHierarchyRepository locationHierarchyRepository;

    @Override
    protected Location makeValidEntity(String name, String id) {
        Location location = new Location();
        location.setUuid(id);
        location.setName(name);
        location.setExtId(name);
        return location;
    }

    @Override
    protected Location makeInvalidEntity() {
        return new Location();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(Location entity, String name, String id) {
        assertNotNull(entity);

        Location savedLocation = locationRepository.findOne(id);
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

    @Override
    protected Location getAnyExisting() {
        return locationRepository.findAll().get(0);
    }

    @Override
    protected long getCount() {
        return locationRepository.count();
    }

    @Override
    protected String getResourceName() {
        return "locations";
    }

    @Override
    protected Class<Location> getEntityClass() {
        return Location.class;
    }

    private LocationRegistration makeLocationRegistration(String hierarchyName, String name) {
        Location location = new Location();
        location.setName(name);
        location.setExtId(name);

        LocationRegistration locationRegistration = new LocationRegistration();
        locationRegistration.setLocation(location);
        locationRegistration.setLocationHierarchyUuid(locationHierarchyRepository.findByExtId(hierarchyName).get(0).getUuid());
        locationRegistration.setCollectedByUuid(fieldWorkerRepository.findAll().get(0).getUuid());

        return locationRegistration;
    }

}
