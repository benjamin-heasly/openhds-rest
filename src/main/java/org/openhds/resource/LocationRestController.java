package org.openhds.resource;

import org.openhds.domain.model.Location;
import org.openhds.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/locations")
class LocationRestController extends AbstractRestController<Location> {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationRestController(EntityControllerRegistry entityControllerRegistry, LocationRepository locationRepository) {
        super(Location.class, entityControllerRegistry);
        this.locationRepository = locationRepository;
    }

    @Override
    protected Location getOne(String id) {
        return locationRepository.findByExtId(id).get();
    }

    @Override
    protected Page<Location> getPaged(Pageable pageable) {
        return locationRepository.findAll(pageable);
    }
}
