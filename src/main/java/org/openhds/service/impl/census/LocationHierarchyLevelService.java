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
    protected LocationHierarchyLevel makeUnknownEntity() {
        LocationHierarchyLevel locationHierarchyLevel = new LocationHierarchyLevel();
        locationHierarchyLevel.setName("unknown");
        locationHierarchyLevel.setKeyIdentifier(-1);
        return locationHierarchyLevel;
    }

    @Override
    public void validate(LocationHierarchyLevel entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);
    }

    @Override
    public LocationHierarchyLevel findOrMakePlaceHolder(String uuid){
        LocationHierarchyLevel entity = findOne(uuid);
        if (null == entity){
            entity = makeUnknownEntity();
            entity.setUuid(uuid);
            entity.setKeyIdentifier(uuid.hashCode());
            createOrUpdate(entity);
        }
        return entity;
    }

    public LocationHierarchyLevel findByName(String name) {
        return repository.findByDeletedFalseAndName(name).get();
    }

    public LocationHierarchyLevel findByKeyIdentifier(int keyIdentifier) {
        return repository.findByDeletedFalseAndKeyIdentifier(keyIdentifier).get();
    }

    public boolean levelKeyIdentifierExists(int keyIdentifier) {
        return repository.findByDeletedFalseAndKeyIdentifier(keyIdentifier).isPresent();
    }

    public LocationHierarchyLevel recordLocationHierarchyLevel(LocationHierarchyLevel locationHierarchyLevel){
        return createOrUpdate(locationHierarchyLevel);
    }

}
