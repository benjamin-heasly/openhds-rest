package org.openhds.service;


import org.openhds.domain.model.Location;
import org.openhds.helpers.LocationTestHelper;
import org.openhds.service.impl.LocationService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wolfe on 6/17/15.
 */
public class LocationServiceTest extends AuditableExtIdServiceTest<Location,LocationService,LocationTestHelper> {


    @Override
    @Autowired
    protected void initialize(LocationService service, LocationTestHelper helper) {
        this.service = service;
        this.helper = helper;
    }
}
