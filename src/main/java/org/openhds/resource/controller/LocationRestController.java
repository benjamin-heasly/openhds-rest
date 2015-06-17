package org.openhds.resource.controller;

import org.openhds.domain.model.Location;
import org.openhds.repository.FieldWorkerRepository;
import org.openhds.repository.LocationHierarchyRepository;
import org.openhds.repository.LocationRepository;
import org.openhds.repository.UserRepository;
import org.openhds.resource.contract.AuditableExtIdRestController;
import org.openhds.resource.registration.LocationRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolationException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/locations")
@ExposesResourceFor(Location.class)
class LocationRestController extends AuditableExtIdRestController<Location, LocationRegistration> {

    private final LocationRepository locationRepository;

    private final LocationHierarchyRepository locationHierarchyRepository;

    private final UserRepository userRepository;

    private final FieldWorkerRepository fieldWorkerRepository;

    @Autowired
    public LocationRestController(LocationRepository locationRepository,
                                  LocationHierarchyRepository locationHierarchyRepository,
                                  UserRepository userRepository,
                                  FieldWorkerRepository fieldWorkerRepository) {
        this.locationRepository = locationRepository;
        this.locationHierarchyRepository = locationHierarchyRepository;
        this.userRepository = userRepository;
        this.fieldWorkerRepository = fieldWorkerRepository;
    }

    @Override
    protected Location findOneCanonical(String id) {
        return locationRepository.findByDeletedFalseAndUuid(id);
    }

    @Override
    protected Page<Location> findPaged(Pageable pageable) {
        return locationRepository.findByDeletedFalse(pageable);
    }

    @Override
    protected Page<Location> findPagedByInsertDate(Pageable pageable, ZonedDateTime insertedAfter, ZonedDateTime insertedBefore) {
        // TODO: this is probably a method of AuditableService
        if (null == insertedAfter) {
            if (null == insertedBefore) {
                return locationRepository.findByDeletedFalse(pageable);
            } else {
                return locationRepository.findByDeletedFalseAndInsertDateBefore(insertedBefore, pageable);
            }
        } else {
            if (null == insertedBefore) {
                return locationRepository.findByDeletedFalseAndInsertDateAfter(insertedAfter, pageable);
            } else {
                return locationRepository.findByDeletedFalseAndInsertDateBetween(insertedAfter, insertedBefore, pageable);
            }
        }
    }

    @Override
    protected List<Location> findByExtId(String id) {
        return locationRepository.findByExtId(id);
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
        location.setInsertDate(ZonedDateTime.now());

        return locationRepository.save(location);
    }

    @Override
    protected Location register(LocationRegistration registration, String id) {
        registration.getLocation().setUuid(id);
        return register(registration);
    }

    @Override
    protected void update(Location entity) {
        locationRepository.save(entity);
    }

    @Override
    protected Page<Location> findVoided(Pageable pageable) {
        return locationRepository.findByDeletedTrue(pageable);
    }

    @Override
    protected Stream<Location> findBulk(Sort sort) {
        Iterable<Location> locationIterable = locationRepository.findAll(sort);
        return StreamSupport.stream(locationIterable.spliterator(), false);
    }
}
