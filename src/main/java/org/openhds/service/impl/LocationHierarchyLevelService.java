package org.openhds.service.impl;

import org.openhds.domain.model.LocationHierarchyLevel;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.LocationHierarchyLevelRepository;
import org.openhds.service.contract.AbstractAuditableService;

/**
 * Created by Wolfe on 7/1/2015.
 */
public class LocationHierarchyLevelService extends AbstractAuditableService<LocationHierarchyLevel, LocationHierarchyLevelRepository>{

    public LocationHierarchyLevelService(LocationHierarchyLevelRepository locationHierarchyLevelRepository) {
        super(locationHierarchyLevelRepository);
    }

    @Override
    protected LocationHierarchyLevel makeUnknownEntity() {
        LocationHierarchyLevel locationHierarchyLevel = new LocationHierarchyLevel();
        locationHierarchyLevel.setName("unknown");
        locationHierarchyLevel.setKeyIdentifier(-1);
        return locationHierarchyLevel;
    }

    @Override
    public void validate(LocationHierarchyLevel entity, ErrorLog errorLog){
        super.validate(entity, errorLog);
    }

    public LocationHierarchyLevel findByName(String name){
        return repository.findByDeletedFalseAndName(name).get();
    }

}
