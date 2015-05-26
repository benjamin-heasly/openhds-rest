package org.openhds.resource.controller;

import org.openhds.domain.model.Location;
import org.openhds.domain.model.LocationHierarchy;
import org.openhds.domain.registration.LocationRegistration;
import org.openhds.repository.LocationHierarchyRepository;
import org.openhds.repository.LocationRepository;
import org.openhds.repository.UserRepository;
import org.openhds.resource.EntityControllerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.UUID;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/locations")
class LocationRestController extends AbstractRestController<Location> {

    private final LocationRepository locationRepository;

    private final LocationHierarchyRepository locationHierarchyRepository;

    private final UserRepository userRepository;

    @Autowired
    public LocationRestController(EntityControllerRegistry entityControllerRegistry,
                                  LocationRepository locationRepository,
                                  LocationHierarchyRepository locationHierarchyRepository,
                                  UserRepository userRepository) {
        super(Location.class, entityControllerRegistry);
        this.locationRepository = locationRepository;
        this.locationHierarchyRepository = locationHierarchyRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected Location findOne(String id) {
        return locationRepository.findOne(id);
    }

    @Override
    protected Page<Location> findPaged(Pageable pageable) {
        return locationRepository.findAll(pageable);
    }

    // TODO: can probably factor this into a superclass.
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    Resource<Location> insert(@RequestBody LocationRegistration locationRegistration) {
        Location location = registerLocation(locationRegistration);
        return resourceLinkAssembler.toResource(location);
    }

    // TODO: can probably factor this into a superclass.
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.CREATED)
    Resource<Location> replace(@RequestBody LocationRegistration locationRegistration, @PathVariable String id) {
        Location location = registerLocation(locationRegistration, id);
        return resourceLinkAssembler.toResource(location);
    }

    // TODO: this belongs in a Location service.  Ben and Wolfe collab.
    private Location registerLocation(LocationRegistration locationRegistration) {
        LocationHierarchy locationHierarchy = locationHierarchyRepository.findOne(locationRegistration.getLocationHierarchyUuid());
        Location location = locationRegistration.getLocation();

        // TODO: this should come from the authenticated Principal
        location.setInsertBy(userRepository.findAll().get(0));
        location.setLocationHierarchy(locationHierarchy);
        location.setInsertDate(Calendar.getInstance());
        location.setUuid(UUID.randomUUID().toString().replace("-", ""));

        return locationRepository.save(location);
    }

    // TODO: this belongs in a Location service.  Ben and Wolfe collab.
    private Location registerLocation(LocationRegistration locationRegistration, String id) {
        locationRegistration.getLocation().setUuid(id);
        return registerLocation(locationRegistration);
    }
}
