package org.openhds.resource.controller;

import org.openhds.domain.model.Location;
import org.openhds.resource.contract.AuditableExtIdRestController;
import org.openhds.resource.registration.LocationRegistration;
import org.openhds.service.impl.LocationHierarchyService;
import org.openhds.service.impl.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/locations")
@ExposesResourceFor(Location.class)
class LocationRestController extends AuditableExtIdRestController<
        Location,
        LocationRegistration,
        LocationService> {

    private final LocationService locationService;

    private final LocationHierarchyService locationHierarchyService;

    @Autowired
    public LocationRestController(LocationService locationService,
                                  LocationHierarchyService locationHierarchyService) {
        super(locationService);
        this.locationService = locationService;
        this.locationHierarchyService = locationHierarchyService;
    }

    @Override
    protected Location register(LocationRegistration registration) {
        Location location = registration.getLocation();
        location.setLocationHierarchy(locationHierarchyService.findOne(registration.getLocationHierarchyUuid()));
        return locationService.createOrUpdate(location);
    }

    @Override
    protected Location register(LocationRegistration registration, String id) {
        registration.getLocation().setUuid(id);
        return register(registration);
    }

}
