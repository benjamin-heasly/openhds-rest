package org.openhds.service.impl.census;

import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.census.LocationHierarchyRepository;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * Created by wolfe on 6/23/15.
 */
@Service
public class LocationHierarchyService extends AbstractAuditableExtIdService<LocationHierarchy, LocationHierarchyRepository>{

    @Autowired
    public LocationHierarchyService(LocationHierarchyRepository repository) {
        super(repository);
    }

    @Override
    protected LocationHierarchy makeUnknownEntity() {
        LocationHierarchy locationHierarchy = new LocationHierarchy();
        locationHierarchy.setName("unknown");
        locationHierarchy.setExtId("unknown");
        locationHierarchy.setCollectionDateTime(ZonedDateTime.now());
        locationHierarchy.setCollectedBy(fieldWorkerService.getUnknownEntity());
        return locationHierarchy;
    }

    @Override
    public void validate(LocationHierarchy entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);
    }
}
