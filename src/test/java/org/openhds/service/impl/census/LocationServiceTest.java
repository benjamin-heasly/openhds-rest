package org.openhds.service.impl.census;


import org.openhds.domain.model.census.Location;
import org.openhds.service.AuditableExtIdServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wolfe on 6/17/15.
 */
public class LocationServiceTest extends AuditableExtIdServiceTest<Location, LocationService> {

    @Autowired
    private LocationHierarchyService locationHierarchyService;

    @Override
    protected Location makeInvalidEntity() {
        return new Location();
    }

    @Override
    protected Location makeValidEntity(String name, String id) {
        Location location = new Location();
        location.setUuid(id);
        location.setName(name);
        location.setExtId(name);
        location.setLocationHierarchy(locationHierarchyService.findAll(UUID_SORT).toList().get(0));

        initCollectedFields(location);

        return location;
    }

    @Override
    @Autowired
    protected void initialize(LocationService service) {
        this.service = service;
    }

}
