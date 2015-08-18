package org.openhds.service.impl.census;

import org.openhds.domain.model.census.Location;
import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.census.LocationRepository;
import org.openhds.repository.queries.LocationSpecifications;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wolfe on 6/9/15.
 */

@Service
public class LocationService extends AbstractAuditableExtIdService<Location, LocationRepository> {

    @Autowired
    LocationHierarchyService locationHierarchyService;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        super(locationRepository);
    }

    @Override
    public Location makePlaceHolder(String id, String name) {
        Location location = new Location();
        location.setUuid(id);
        location.setName(name);
        location.setExtId(name);
        location.setLocationHierarchy(locationHierarchyService.getUnknownEntity());

        initPlaceHolderCollectedFields(location);

        return location;
    }

    @Override
    public void validate(Location entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);
    }

    @Override
    public List<LocationHierarchy> findEnclosingLocationHierarchies(Location entity) {
        return locationHierarchyService.findEnclosingLocationHierarchies(entity.getLocationHierarchy());
    }

    @Override
    public Page<Location> findByEnclosingLocationHierarchy(Pageable pageable, String locationHierarchyUuid) {
        return locationHierarchyService.findOtherByEnclosingLocationHierarchy(pageable,
                locationHierarchyUuid,
                LocationSpecifications::enclosedLocations,
                repository);
    }

    public Location recordLocation(Location location, String locationHierarchyId, String fieldWorkerId){
        location.setLocationHierarchy(locationHierarchyService.findOrMakePlaceHolder(locationHierarchyId));
        location.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        return createOrUpdate(location);
    }
}
