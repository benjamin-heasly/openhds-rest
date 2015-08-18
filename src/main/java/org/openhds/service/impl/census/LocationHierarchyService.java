package org.openhds.service.impl.census;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.domain.model.census.LocationHierarchyLevel;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.census.LocationHierarchyRepository;
import org.openhds.repository.contract.AuditableRepository;
import org.openhds.repository.queries.LocationSpecifications;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.openhds.repository.results.PageMaker.makePage;

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
    private LocationHierarchyLevelService locationHierarchyLevelService;

    @Autowired
    public LocationHierarchyService(LocationHierarchyRepository repository) {
        super(repository);
    }

    @Override
    public LocationHierarchy makePlaceHolder(String id, String name) {
        LocationHierarchy locationHierarchy = new LocationHierarchy();
        locationHierarchy.setLevel(locationHierarchyLevelService.getUnknownEntity());
        locationHierarchy.setParent(getHierarchyRoot());
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
        root.setLevel(locationHierarchyLevelService.getUnknownEntity());
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

    public LocationHierarchy recordLocationHierarchy(LocationHierarchy locationHierarchy,
                                                     String parentId,
                                                     String levelId,
                                                     String fieldWorkerId){
        locationHierarchy.setParent(findOrMakePlaceHolder(parentId));
        locationHierarchy.setLevel(locationHierarchyLevelService.findOrMakePlaceHolder(levelId));
        locationHierarchy.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        return createOrUpdate(locationHierarchy);
    }

    public List<LocationHierarchy> findByParent(LocationHierarchy parent) {
        return repository.findByParent(parent);
    }

    public List<LocationHierarchy> findByLevel(LocationHierarchyLevel level) {
        return repository.findByLevel(level);
    }


    @Override
    public Page<LocationHierarchy> findByEnclosingLocationHierarchy(Pageable pageable, String locationHierarchyUuid) {
        return makePage(findByEnclosingLocationHierarchy(locationHierarchyUuid), pageable);
    }

    // find descendants of the given subtree root--whole subtree in memory!
    public List<LocationHierarchy> findByEnclosingLocationHierarchy(String locationHierarchyUuid) {
        LocationHierarchy locationHierarchy = findOne(locationHierarchyUuid);
        List<LocationHierarchy> subtree = new ArrayList<>();

        if (null != locationHierarchy) {
            collectSubtree(locationHierarchy, subtree);
        }

        return subtree;
    }

    // find other entities joined to the location hierarchy subtree at the given root
    public <T extends AuditableEntity> Page<T> findOtherByEnclosingLocationHierarchy(Pageable pageable,
                                                                                     String locationHierarchyUuid,
                                                                                     LocationSpecifications.LocationSpecification<T> locationSpecification,
                                                                                     AuditableRepository<T> otherRepository) {
        // whole hierarchy subtree in memory!
        List<LocationHierarchy> enclosing = findByEnclosingLocationHierarchy(locationHierarchyUuid);

        // page of results associated with the subtree
        return otherRepository.findAll(locationSpecification.getSpecification(enclosing), pageable);
    }

    // recursively add descendants of the given subtree root
    public List<LocationHierarchy> collectSubtree(LocationHierarchy root, List<LocationHierarchy> subtree) {
        subtree.add(root);
        List<LocationHierarchy> children = findByParent(root);
        for (LocationHierarchy child : children) {
            collectSubtree(child, subtree);
        }
        return subtree;
    }

    // add all ancestors of the given hierarchy
    public List<LocationHierarchy> collectAncestors(LocationHierarchy node, List<LocationHierarchy> ancestors) {
        ancestors.add(node);
        LocationHierarchy parent = node.getParent();
        while (null != parent) {
            ancestors.add(parent);
            parent = parent.getParent();
        }
        return ancestors;
    }

    @Override
    public List<LocationHierarchy> findEnclosingLocationHierarchies(LocationHierarchy entity) {
        List<LocationHierarchy> ancestors = new ArrayList<>();

        if (null != entity) {
            collectAncestors(entity, ancestors);
        }

        return ancestors;
    }
}
