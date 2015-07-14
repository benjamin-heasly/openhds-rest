package org.openhds.service.impl.census;

import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.service.AuditableExtIdServiceTest;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.LocationHierarchyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

/**
 * Created by Wolfe on 7/13/2015.
 */
public class LocationHierarchyServiceTest extends AuditableExtIdServiceTest<LocationHierarchy, LocationHierarchyService>{

    @Autowired
    private FieldWorkerService fieldWorkerService;

    @Override
    protected LocationHierarchy makeInvalidEntity() {
        return new LocationHierarchy();
    }

    @Override
    protected LocationHierarchy makeValidEntity(String name, String id) {
        LocationHierarchy locationHierarchy = new LocationHierarchy();
        locationHierarchy.setUuid(id);
        locationHierarchy.setName(name);
        locationHierarchy.setExtId(name);
        locationHierarchy.setCollectedBy(fieldWorkerService.findAll(UUID_SORT).toList().get(0));
        locationHierarchy.setCollectionDateTime(ZonedDateTime.now());
        return locationHierarchy;
    }

    @Override
    @Autowired
    protected void initialize(LocationHierarchyService service) {
        this.service = service;
    }
}
