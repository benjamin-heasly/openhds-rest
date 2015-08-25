package org.openhds.service.impl.census;

import org.openhds.domain.model.census.Location;
import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.census.LocationRepository;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

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
        location.setType("RURAL");
        location.setLocationHierarchy(locationHierarchyService.getUnknownEntity());

        initPlaceHolderCollectedFields(location);

        return location;
    }

    @Override
    public void validate(Location location, ErrorLog errorLog) {
        super.validate(location, errorLog);

        if(!projectCodeService.isValueInCodeGroup(location.getType(), projectCodeService.LOCATION_TYPE)){
            errorLog.appendError("Location cannot have a type of: ["+location.getType()+"].");
        }

    }

    @Override
    public Set<LocationHierarchy> findEnclosingLocationHierarchies(Location entity) {
        return locationHierarchyService.findEnclosingLocationHierarchies(entity.getLocationHierarchy());
    }

    @Override
    public Page<Location> findByEnclosingLocationHierarchy(Pageable pageable,
                                                           String locationHierarchyUuid,
                                                           ZonedDateTime modifiedAfter,
                                                           ZonedDateTime modifiedBefore) {
        return locationHierarchyService.findOtherByEnclosingLocationHierarchy(pageable,
                locationHierarchyUuid,
                modifiedAfter,
                modifiedBefore,
                LocationService::enclosed,
                repository);
    }

    private static Specification<Location> enclosed(final List<LocationHierarchy> enclosing) {
        return (root, query, cb) -> root.get("locationHierarchy").in(enclosing);
    }


    public Location recordLocation(Location location, String locationHierarchyId, String fieldWorkerId){
        location.setLocationHierarchy(locationHierarchyService.findOrMakePlaceHolder(locationHierarchyId));
        location.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        return createOrUpdate(location);
    }
}
