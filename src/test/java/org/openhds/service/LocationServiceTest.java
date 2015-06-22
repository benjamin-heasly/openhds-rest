package org.openhds.service;


import org.openhds.domain.model.Location;
import org.openhds.service.impl.LocationService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wolfe on 6/17/15.
 */
public class LocationServiceTest extends AuditableExtIdServiceTest<Location,LocationService> {


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
        return location;
    }

    @Override
    @Autowired
    protected void initialize(LocationService service) {
        this.service = service;
    }
}
