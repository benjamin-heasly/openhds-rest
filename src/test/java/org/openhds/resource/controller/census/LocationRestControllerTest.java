package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.Location;
import org.openhds.resource.contract.AuditableExtIdRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.census.LocationRegistration;
import org.openhds.service.impl.census.LocationHierarchyService;
import org.openhds.service.impl.census.LocationService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Ben on 5/26/15.
 */
public class LocationRestControllerTest extends AuditableExtIdRestControllerTest
        <Location, LocationService, LocationRestController> {

    @Autowired
    private LocationHierarchyService locationHierarchyService;

    @Autowired
    @Override
    protected void initialize(LocationService service, LocationRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected Location makeInvalidEntity() {
        return new Location();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(Location entity, String name, String id) {
        assertNotNull(entity);

        Location savedLocation = service.findOne(id);
        assertNotNull(savedLocation);

        assertEquals(id, savedLocation.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getName(), savedLocation.getName());

    }

}
