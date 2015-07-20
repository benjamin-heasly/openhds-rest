package org.openhds.service.impl.census;

import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.Location;
import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.census.LocationRepository;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

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
    protected Location makeUnknownEntity() {
        Location location = new Location();
        location.setName("unknown");
        location.setExtId("unknown");
        location.setCollectedBy(fieldWorkerService.getUnknownEntity());
        location.setCollectionDateTime(ZonedDateTime.now());
        location.setLocationHierarchy(locationHierarchyService.getUnknownEntity());
        return location;
    }

    @Override
    public void validate(Location entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);

    }

    public Location recordLocation(Location location, String locationHierarchyId, String fieldWorkerId){
        location.setLocationHierarchy(locationHierarchyService.findOrMakePlaceHolder(locationHierarchyId));
        location.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        return createOrUpdate(location);
    }
}
