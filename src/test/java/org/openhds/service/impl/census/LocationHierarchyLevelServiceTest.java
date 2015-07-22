package org.openhds.service.impl.census;

import org.openhds.domain.model.census.LocationHierarchyLevel;
import org.openhds.service.AuditableServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Wolfe on 7/13/2015.
 */
public class LocationHierarchyLevelServiceTest extends AuditableServiceTest<LocationHierarchyLevel, LocationHierarchyLevelService> {

    @Autowired
    protected void initialize(LocationHierarchyLevelService service) {
        this.service = service;
    }

    @Override
    protected LocationHierarchyLevel makeInvalidEntity() {
        return new LocationHierarchyLevel();
    }
}
