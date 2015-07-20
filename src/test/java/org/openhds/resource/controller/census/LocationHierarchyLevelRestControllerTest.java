package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.LocationHierarchyLevel;
import org.openhds.resource.contract.AuditableRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.census.LocationHierarchyLevelRegistration;
import org.openhds.service.impl.census.LocationHierarchyLevelService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Ben on 5/26/15.
 */
public class LocationHierarchyLevelRestControllerTest extends AuditableRestControllerTest<
        LocationHierarchyLevel,
        LocationHierarchyLevelService,
        LocationHierarchyLevelRestController> {

    @Autowired
    @Override
    protected void initialize(LocationHierarchyLevelService service, LocationHierarchyLevelRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected LocationHierarchyLevel makeValidEntity(String name, String id) {
        LocationHierarchyLevel locationHierarchyLevel = new LocationHierarchyLevel();
        locationHierarchyLevel.setUuid(id);
        locationHierarchyLevel.setName(name);
        locationHierarchyLevel.setKeyIdentifier(0);
        return locationHierarchyLevel;
    }

    @Override
    protected LocationHierarchyLevel makeInvalidEntity() {
        return new LocationHierarchyLevel();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(LocationHierarchyLevel entity, String name, String id) {
        assertNotNull(entity);

        LocationHierarchyLevel savedLocationHierarchyLevel = service.findOne(id);
        assertNotNull(savedLocationHierarchyLevel);

        assertEquals(id, savedLocationHierarchyLevel.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getName(), savedLocationHierarchyLevel.getName());

    }

    @Override
    protected Registration<LocationHierarchyLevel> makeRegistration(LocationHierarchyLevel entity) {
        LocationHierarchyLevelRegistration registration = new LocationHierarchyLevelRegistration();
        registration.setLocationHierarchyLevel(entity);
        return registration;
    }
}
