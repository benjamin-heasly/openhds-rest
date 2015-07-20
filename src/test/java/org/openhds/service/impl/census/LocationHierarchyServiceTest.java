package org.openhds.service.impl.census;

import org.junit.Test;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.service.AuditableExtIdServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Wolfe on 7/13/2015.
 */
public class LocationHierarchyServiceTest extends AuditableExtIdServiceTest<LocationHierarchy, LocationHierarchyService> {

    @Override
    protected LocationHierarchy makeInvalidEntity() {
        return new LocationHierarchy();
    }

    @Override
    protected LocationHierarchy makeValidEntity(String name, String id) {
        LocationHierarchy locationHierarchy = new LocationHierarchy();
        locationHierarchy.setUuid(id);
        locationHierarchy.setName(name);
        locationHierarchy.setExtId(name);

        initCollectedFields(locationHierarchy);

        return locationHierarchy;
    }

    @Override
    @Autowired
    protected void initialize(LocationHierarchyService service) {
        this.service = service;
    }

    @Test
    public void recordWithExistingReferences() {

        //Grab a valid entity
        LocationHierarchy locationHierarchy = makeValidEntity("validName", "validId");

        FieldWorker fieldWorker = locationHierarchy.getCollectedBy();
        locationHierarchy.setCollectedBy(null);

        // pass it all into the record method
        locationHierarchy = service.recordLocationHierarchy(locationHierarchy, fieldWorker.getUuid());


        //Check that the originals match the ones pulled out from findOrMakePlaceholder()
        assertNotNull(locationHierarchy.getCollectedBy());
        assertEquals(locationHierarchy.getCollectedBy(), fieldWorker);

    }

    @Test
    public void recordWithNonexistentReferences(){

        //Make a new entity with no references
        LocationHierarchy locationHierarchy = makeValidEntity("validName", "validId");
        locationHierarchy.setCollectedBy(null);

        //Pass it in with new reference uuids
        locationHierarchy = service.recordLocationHierarchy(locationHierarchy, "feldwarker");

        //check that they were persisted
        assertNotNull(locationHierarchy.getCollectedBy());
        assertEquals(locationHierarchy.getCollectedBy().getUuid(), "feldwarker");

    }
}
