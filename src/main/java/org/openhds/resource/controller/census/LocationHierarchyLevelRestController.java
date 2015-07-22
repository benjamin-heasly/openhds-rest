package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.LocationHierarchyLevel;
import org.openhds.resource.contract.AuditableRestController;
import org.openhds.resource.registration.census.LocationHierarchyLevelRegistration;
import org.openhds.service.impl.census.LocationHierarchyLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/locationHierarchyLevels")
@ExposesResourceFor(LocationHierarchyLevel.class)
public class LocationHierarchyLevelRestController extends AuditableRestController<
        LocationHierarchyLevel,
        LocationHierarchyLevelRegistration,
        LocationHierarchyLevelService> {

    private final LocationHierarchyLevelService locationHierarchyLevelService;

    @Autowired
    public LocationHierarchyLevelRestController(LocationHierarchyLevelService locationHierarchyLevelService) {
        super(locationHierarchyLevelService);
        this.locationHierarchyLevelService = locationHierarchyLevelService;
    }

    @Override
    protected LocationHierarchyLevel register(LocationHierarchyLevelRegistration registration) {
        return locationHierarchyLevelService.recordLocationHierarchyLevel(registration.getLocationHierarchyLevel());
    }

    @Override
    protected LocationHierarchyLevel register(LocationHierarchyLevelRegistration registration, String id) {
        registration.getLocationHierarchyLevel().setUuid(id);
        return register(registration);
    }
}
