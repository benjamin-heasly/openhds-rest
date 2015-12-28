package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.resource.contract.AuditableExtIdRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.census.LocationHierarchyRegistration;
import org.openhds.service.impl.census.LocationHierarchyLevelService;
import org.openhds.service.impl.census.LocationHierarchyService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Ben on 5/26/15.
 */
public class LocationHierarchyRestControllerTest extends AuditableExtIdRestControllerTest<
        LocationHierarchy,
        LocationHierarchyService,
        LocationHierarchyRestController> {

    @Autowired
    private LocationHierarchyLevelService locationHierarchyLevelService;

    @Autowired
    @Override
    protected void initialize(LocationHierarchyService service, LocationHierarchyRestController controller) {
        this.service = service;
        this.controller = controller;
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

}
