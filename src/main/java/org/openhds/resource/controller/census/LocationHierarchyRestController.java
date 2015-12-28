package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.resource.contract.AuditableExtIdRestController;
import org.openhds.resource.registration.census.LocationHierarchyRegistration;
import org.openhds.service.contract.AbstractUuidService;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.LocationHierarchyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/locationHierarchies")
@ExposesResourceFor(LocationHierarchy.class)
public class LocationHierarchyRestController extends AuditableExtIdRestController<
        LocationHierarchy,
        LocationHierarchyRegistration,
        LocationHierarchyService> {

    private final LocationHierarchyService locationHierarchyService;

    private final FieldWorkerService fieldWorkerService;

    @Autowired
    public LocationHierarchyRestController(LocationHierarchyService locationHierarchyService,
                                           FieldWorkerService fieldWorkerService) {
        super(locationHierarchyService);
        this.locationHierarchyService = locationHierarchyService;
        this.fieldWorkerService = fieldWorkerService;
    }

    @Override
    protected LocationHierarchyRegistration makeSampleRegistration(LocationHierarchy entity) {
        LocationHierarchyRegistration registration = new LocationHierarchyRegistration();
        registration.setLocationHierarchy(entity);
        registration.setLevelUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setParentUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        return registration;

    }

    @Override
    protected LocationHierarchy register(LocationHierarchyRegistration registration) {
        return locationHierarchyService.recordLocationHierarchy(registration.getLocationHierarchy(),
                registration.getParentUuid(),
                registration.getLevelUuid(),
                registration.getCollectedByUuid());
    }

    @Override
    protected LocationHierarchy register(LocationHierarchyRegistration registration, String id) {
        registration.getLocationHierarchy().setUuid(id);
        return register(registration);
    }
}
