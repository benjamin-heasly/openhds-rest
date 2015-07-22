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
public class LocationHierarchyService extends AbstractAuditableExtIdService<
        LocationHierarchy,
        LocationHierarchyRepository>{

    public static final String ROOT_UUID = "HIERARCHY_ROOT";
    public static final String ROOT_EXT_ID = "hierarchy-root";

    @Autowired
    public LocationHierarchyService(LocationHierarchyRepository repository) {
        super(repository);
    }

    @Override
    public LocationHierarchy makePlaceHolder(String id, String name) {
        LocationHierarchy locationHierarchy = new LocationHierarchy();
        locationHierarchy.setUuid(id);
        locationHierarchy.setName(name);
        locationHierarchy.setExtId(name);

        initPlaceHolderCollectedFields(locationHierarchy);

        return locationHierarchy;
    }

    @Override
    public void validate(LocationHierarchy entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);
    }

    private LocationHierarchy createHierarchyRoot() {
        LocationHierarchy root = new LocationHierarchy();
        root.setUuid(ROOT_UUID);
        root.setName(ROOT_EXT_ID);
        root.setExtId(ROOT_EXT_ID);
        root.setCollectionDateTime(ZonedDateTime.now());
        root.setCollectedBy(fieldWorkerService.getUnknownEntity());
        return createOrUpdate(root);
    }

    public LocationHierarchy getHierarchyRoot() {
        if (!repository.exists(ROOT_UUID)) {
            return createHierarchyRoot();
        }
        return repository.findOne(ROOT_UUID);
    }

    public LocationHierarchy recordLocationHierarchy(LocationHierarchy locationHierarchy, String fieldWorkerId){
        locationHierarchy.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        return createOrUpdate(locationHierarchy);
    }
}
