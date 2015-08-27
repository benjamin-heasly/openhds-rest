package org.openhds.service.impl.census;

import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.domain.model.census.LocationHierarchyLevel;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.census.LocationHierarchyLevelRepository;
import org.openhds.service.contract.AbstractAuditableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Wolfe on 7/1/2015.
 */
@Service
public class LocationHierarchyLevelService extends AbstractAuditableService<LocationHierarchyLevel, LocationHierarchyLevelRepository> {

    @Autowired
    private LocationHierarchyService locationHierarchyService;

    @Autowired
    public LocationHierarchyLevelService(LocationHierarchyLevelRepository locationHierarchyLevelRepository) {
        super(locationHierarchyLevelRepository);
    }

    @Override
    public LocationHierarchyLevel makePlaceHolder(String id, String name) {
        LocationHierarchyLevel locationHierarchyLevel = new LocationHierarchyLevel();
        locationHierarchyLevel.setUuid(id);
        locationHierarchyLevel.setIsPlaceholder(true);
        locationHierarchyLevel.setName(name);
        locationHierarchyLevel.setKeyIdentifier(id.hashCode());
        return locationHierarchyLevel;
    }

    @Override
    public void validate(LocationHierarchyLevel entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);
    }

    // "find" the one single level associated with the given location hierarchy
    @Override
    public Page<LocationHierarchyLevel> findByEnclosingLocationHierarchy(Pageable pageable,
                                                                         String locationHierarchyUuid,
                                                                         ZonedDateTime modifiedAfter,
                                                                         ZonedDateTime modifiedBefore) {

        LocationHierarchy locationHierarchy = locationHierarchyService.findOne(locationHierarchyUuid);
        List<LocationHierarchyLevel> list = new ArrayList<>();

        if (null != locationHierarchy && locationHierarchy.isModifiedInRange(modifiedAfter, modifiedBefore)) {
            list.add(locationHierarchy.getLevel());
        }

        return new PageImpl<>(list, pageable, list.size());
    }

    // find all hierarchies on this level
    @Override
    public Set<LocationHierarchy> findEnclosingLocationHierarchies(LocationHierarchyLevel entity) {
        Set<LocationHierarchy> onLevel = new HashSet<>();
        onLevel.addAll(locationHierarchyService.findByLevel(entity));
        return onLevel;
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
