package org.openhds.service.impl.census;

import org.openhds.domain.model.census.LocationHierarchyLevel;
import org.openhds.service.AuditableServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Wolfe on 7/13/2015.
 */
public class LocationHierarchyLevelServiceTest extends AuditableServiceTest<LocationHierarchyLevel, LocationHierarchyLevelService> {

    @Override
    protected LocationHierarchyLevel makeInvalidEntity() {
        return new LocationHierarchyLevel();
    }

    @Override
    protected LocationHierarchyLevel makeValidEntity(String name, String id) {
        LocationHierarchyLevel locationHierarchyLevel = new LocationHierarchyLevel();
        locationHierarchyLevel.setUuid(id);
        locationHierarchyLevel.setName(name);
        return locationHierarchyLevel;
    }

    @Override
    @Autowired
    protected void initialize(LocationHierarchyLevelService service) {
        this.service = service;
    }
}
