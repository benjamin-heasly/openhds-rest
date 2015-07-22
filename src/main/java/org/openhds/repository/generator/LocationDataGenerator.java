package org.openhds.repository.generator;

import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.Location;
import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.domain.model.census.LocationHierarchyLevel;
import org.openhds.repository.concrete.census.LocationHierarchyLevelRepository;
import org.openhds.repository.concrete.census.LocationHierarchyRepository;
import org.openhds.repository.concrete.census.LocationRepository;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.LocationHierarchyLevelService;
import org.openhds.service.impl.census.LocationHierarchyService;
import org.openhds.service.impl.census.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by bsh on 7/21/15.
 * <p>
 * Generates sample location data, including
 * LocationHierarchyLevel, LocationHierarchy, and Location.
 * <p>
 * For each entity, only inserts sample records if there are no records yet.
 * This behavior should support testing without messing up existing project data.
 * <p>
 * Takes a parameter h which determines how many sample records to create.
 * <p>
 * The sample location hierarchy will always be a 10-way tree with 10 locations
 * at each leaf of the location hierarchy tree.
 * <p>
 * The number of LocationHierarchyLevel records will be equal to h.
 * <p>
 * The number of LocationHierarchy records will be the number of nodes in an a
 * 10-ary tree with h levels: (10^h - 1) / 9.
 * <p>
 * The number of Location records will be the number of leaf nodes in an a
 * 10-ary tree with h levels, times 10: 10^h.
 */
@Component
public class LocationDataGenerator {

    public static final int ARITY = 10;

    private final LocationHierarchyLevelService locationHierarchyLevelService;
    private final LocationHierarchyLevelRepository locationHierarchyLevelRepository;

    private final LocationHierarchyService locationHierarchyService;
    private final LocationHierarchyRepository locationHierarchyRepository;

    private final LocationService locationService;
    private final LocationRepository locationRepository;

    private final FieldWorkerService fieldWorkerService;

    @Autowired
    public LocationDataGenerator(LocationHierarchyLevelService locationHierarchyLevelService,
                                 LocationHierarchyLevelRepository locationHierarchyLevelRepository,
                                 LocationHierarchyService locationHierarchyService,
                                 LocationHierarchyRepository locationHierarchyRepository,
                                 LocationService locationService,
                                 LocationRepository locationRepository,
                                 FieldWorkerService fieldWorkerService) {

        this.locationHierarchyLevelService = locationHierarchyLevelService;
        this.locationHierarchyLevelRepository = locationHierarchyLevelRepository;
        this.locationHierarchyService = locationHierarchyService;
        this.locationHierarchyRepository = locationHierarchyRepository;
        this.locationService = locationService;
        this.locationRepository = locationRepository;
        this.fieldWorkerService = fieldWorkerService;
    }

    public void generateData(int h) {
        generateLevels(h);
        generateHierarchies(h);
        generateLocations(h);
    }

    public void clearData() {
        locationRepository.deleteAllInBatch();
        locationHierarchyRepository.deleteAllInBatch();
        locationHierarchyLevelRepository.deleteAllInBatch();
    }

    private void generateLevels(int h) {
        if (locationHierarchyLevelService.hasRecords()) {
            return;
        }

        for (int i = 1; i <= h; i++) {
            LocationHierarchyLevel locationHierarchyLevel = new LocationHierarchyLevel();
            locationHierarchyLevel.setKeyIdentifier(i);
            locationHierarchyLevel.setName(String.format("location-hierarchy-level-%d", i));
            locationHierarchyLevelService.recordLocationHierarchyLevel(locationHierarchyLevel);
        }
    }

    // define h levels
    private void generateHierarchies(int h) {
        LocationHierarchy root = locationHierarchyService.getHierarchyRoot();
        LocationHierarchyLevel level = locationHierarchyLevelService.findByKeyIdentifier(1);
        FieldWorker fieldWorker = fieldWorkerService.getUnknownEntity();
        generateChildHierarchies(root, level, fieldWorker);
    }

    // recursively build the ARITY-ary tree
    private void generateChildHierarchies(LocationHierarchy parent, LocationHierarchyLevel level, FieldWorker fieldWorker) {

        int levelId = level.getKeyIdentifier();

        LocationHierarchyLevel nextLevel = null;
        if (locationHierarchyLevelService.levelKeyIdentifierExists(levelId + 1)) {
            nextLevel = locationHierarchyLevelService.findByKeyIdentifier(levelId + 1);
        }

        // add ARITY children
        for (int i = 1; i <= ARITY; i++) {
            LocationHierarchy child = new LocationHierarchy();
            String extId = parent.getExtId() + "-" + i;
            child.setExtId(extId);
            child.setName(extId);
            child.setCollectionDateTime(ZonedDateTime.now());

            locationHierarchyService.recordLocationHierarchy(child,
                    parent.getUuid(),
                    level.getUuid(),
                    fieldWorker.getUuid());

            // base case: bottom of the tree
            if (null == nextLevel) {
                continue;
            }

            // keep adding grandchildren
            generateChildHierarchies(child, nextLevel, fieldWorker);
        }

    }

    // add ARITY locations at each leaf of the hierarchy
    private void generateLocations(int h) {
        LocationHierarchyLevel bottomLevel = locationHierarchyLevelService.findByKeyIdentifier(h);
        List<LocationHierarchy> leafNodes = locationHierarchyService.findByLevel(bottomLevel);

        FieldWorker fieldWorker = fieldWorkerService.getUnknownEntity();

        for (LocationHierarchy leaf : leafNodes) {
            for (int i = 1; i <= ARITY; i++) {
                Location location = new Location();

                String extId = String.format("location-%d", i);
                location.setExtId(extId);
                location.setName(extId);
                location.setDescription("sample location");
                location.setCollectionDateTime(ZonedDateTime.now());

                locationService.recordLocation(location, leaf.getUuid(), fieldWorker.getUuid());
            }
        }
    }

}
