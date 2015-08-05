package org.openhds.service.impl.census;

import org.junit.Test;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.domain.model.census.LocationHierarchyLevel;
import org.openhds.service.AuditableExtIdServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Wolfe on 7/13/2015.
 */
public class LocationHierarchyServiceTest extends AuditableExtIdServiceTest<LocationHierarchy, LocationHierarchyService> {

    @Autowired
    private LocationHierarchyLevelService locationHierarchyLevelService;

    @Autowired
    protected void initialize(LocationHierarchyService service) {
        this.service = service;
    }

    @Override
    protected LocationHierarchy makeInvalidEntity() {
        return new LocationHierarchy();
    }

    @Test
    public void recordWithExistingReferences() {

        //Grab a valid entity
        LocationHierarchy locationHierarchy = makeValidEntity("validName", "validId");

        FieldWorker fieldWorker = locationHierarchy.getCollectedBy();
        locationHierarchy.setCollectedBy(null);

        LocationHierarchy parent = locationHierarchy.getParent();
        locationHierarchy.setParent(null);

        LocationHierarchyLevel level = locationHierarchy.getLevel();
        locationHierarchy.setLevel(null);

        // pass it all into the record method
        locationHierarchy = service.recordLocationHierarchy(locationHierarchy,
                fieldWorker.getUuid(),
                parent.getUuid(),
                level.getUuid());


        //Check that the originals match the ones pulled out from findOrMakePlaceholder()
        assertNotNull(locationHierarchy.getCollectedBy());
        assertEquals(locationHierarchy.getCollectedBy(), fieldWorker);

        assertNotNull(locationHierarchy.getParent());
        assertEquals(locationHierarchy.getParent(), parent);

    }

    @Test
    public void recordWithNonexistentReferences(){

        //Make a new entity with no references
        LocationHierarchy locationHierarchy = makeValidEntity("validName", "validId");
        locationHierarchy.setCollectedBy(null);
        locationHierarchy.setParent(null);
        locationHierarchy.setLevel(null);

        //Pass it in with new reference uuids
        locationHierarchy = service.recordLocationHierarchy(locationHierarchy, "praren", "lovo", "feldwarker");

        //check that they were persisted
        assertNotNull(locationHierarchy.getCollectedBy());
        assertEquals(locationHierarchy.getCollectedBy().getUuid(), "feldwarker");
        assertEquals(locationHierarchy.getParent().getUuid(), "praren");
        assertEquals(locationHierarchy.getLevel().getUuid(), "lovo");

    }
}
