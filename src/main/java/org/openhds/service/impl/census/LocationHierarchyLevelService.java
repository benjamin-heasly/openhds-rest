package org.openhds.service.impl.census;

import org.openhds.domain.model.census.LocationHierarchyLevel;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.census.LocationHierarchyLevelRepository;
import org.openhds.service.contract.AbstractAuditableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Wolfe on 7/1/2015.
 */
@Service
public class LocationHierarchyLevelService extends AbstractAuditableService<LocationHierarchyLevel, LocationHierarchyLevelRepository> {

    @Autowired
    public LocationHierarchyLevelService(LocationHierarchyLevelRepository locationHierarchyLevelRepository) {
        super(locationHierarchyLevelRepository);
    }

    @Override
    public LocationHierarchyLevel makePlaceHolder(String id, String name) {
        LocationHierarchyLevel locationHierarchyLevel = new LocationHierarchyLevel();
        locationHierarchyLevel.setUuid(id);
        locationHierarchyLevel.setName(name);
        locationHierarchyLevel.setKeyIdentifier(-1);
        return locationHierarchyLevel;
    }

    @Override
    public void validate(LocationHierarchyLevel entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);
    }

    public LocationHierarchyLevel findByName(String name) {
        return repository.findByDeletedFalseAndName(name).get();
    }

    public LocationHierarchyLevel recordLocationHierarchyLevel(LocationHierarchyLevel locationHierarchyLevel){
        return createOrUpdate(locationHierarchyLevel);
    }

}
