package org.openhds.resource.controller;

import org.openhds.domain.model.Location;
import org.openhds.resource.registration.LocationRegistration;
import org.openhds.repository.FieldWorkerRepository;
import org.openhds.repository.LocationHierarchyRepository;
import org.openhds.repository.LocationRepository;
import org.openhds.repository.UserRepository;
import org.openhds.resource.links.EntityLinkAssembler;
import org.openhds.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolationException;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/locations")
@ExposesResourceFor(Location.class)
class LocationRestController extends ExtIdRestController<Location, LocationRegistration> {

    private final LocationService locationService;

    private final LocationHierarchyRepository locationHierarchyRepository;

    private final UserRepository userRepository;

    private final FieldWorkerRepository fieldWorkerRepository;

    @Autowired
    public LocationRestController(EntityLinkAssembler entityLinkAssembler,
                                  LocationService locationService,
                                  LocationHierarchyRepository locationHierarchyRepository,
                                  UserRepository userRepository,
                                  FieldWorkerRepository fieldWorkerRepository) {
        super(entityLinkAssembler);
        this.locationService = locationService;
        this.locationHierarchyRepository = locationHierarchyRepository;
        this.userRepository = userRepository;
        this.fieldWorkerRepository = fieldWorkerRepository;
    }

    @Override
    protected Location findOneCanonical(String id) {
        return locationService.findOne(id);
    }

    @Override
    protected Page<Location> findPaged(Pageable pageable) {
        return locationService.findPaged(pageable);
    }

    @Override
    protected List<Location> findByExtId(String id) {
        return locationService.findByExtId(id);
    }

    @Override
    protected Location register(LocationRegistration registration) {
        // TODO: this implementation belongs in a Location service.  Ben and Wolfe collab.
        Location location = registration.getLocation();

        if (null == location.getName()) {
            throw new ConstraintViolationException("Location name must not be null.", null);
        }

        // TODO: this should come from the authenticated Principal
        location.setInsertBy(userRepository.findAll().get(0));

        // fill in auditable fields
        location.setCollectedBy(fieldWorkerRepository.findOne(registration.getCollectedByUuid()));
        location.setLocationHierarchy(locationHierarchyRepository.findOne(registration.getLocationHierarchyUuid()));
        location.setInsertDate(Calendar.getInstance());

        return locationService.create(location);
    }

    @Override
    protected Location register(LocationRegistration registration, String id) {
        registration.getLocation().setUuid(id);
        return register(registration);
    }

}
