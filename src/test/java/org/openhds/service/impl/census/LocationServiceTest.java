package org.openhds.service.impl.census;


import org.junit.Test;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.Location;
import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.service.AuditableExtIdServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
/**
 * Created by wolfe on 6/17/15.
 */
public class LocationServiceTest extends AuditableExtIdServiceTest<Location, LocationService> {

    @Autowired
    private LocationHierarchyService locationHierarchyService;

    @Override
    protected Location makeInvalidEntity() {
        return new Location();
    }

    @Override
    protected Location makeValidEntity(String name, String id) {
        Location location = new Location();
        location.setUuid(id);
        location.setName(name);
        location.setExtId(name);
        location.setLocationHierarchy(locationHierarchyService.findAll(UUID_SORT).toList().get(0));

        initCollectedFields(location);

        return location;
    }

    @Override
    @Autowired
    protected void initialize(LocationService service) {
        this.service = service;
    }

    @Test
    public void recordWithExistingReferences() {

        //Grab a valid entity
        Location location = makeValidEntity("validName", "validId");

        //Remember the references and clear them
        LocationHierarchy locationHierarchy = location.getLocationHierarchy();
        location.setLocationHierarchy(null);

        FieldWorker fieldWorker = location.getCollectedBy();
        location.setCollectedBy(null);

        // pass it all into the record method
        location = service.recordLocation(location, locationHierarchy.getUuid(), fieldWorker.getUuid());


        //Check that the originals match the ones pulled out from findOrMakePlaceholder()
        assertNotNull(location.getLocationHierarchy());
        assertEquals(location.getLocationHierarchy(), locationHierarchy);

        assertNotNull(location.getCollectedBy());
        assertEquals(location.getCollectedBy(), fieldWorker);

    }

    @Test
    public void recordWithNonexistentReferences(){

        //Make a new entity with no references
        Location location = makeValidEntity("validName", "validId");
        location.setLocationHierarchy(null);
        location.setCollectedBy(null);

        //Pass it in with new reference uuids
        location = service.recordLocation(location, "lucutiunHiararchy", "feldwarker");

        //check that they were persisted
        assertNotNull(location.getLocationHierarchy());
        assertNotNull(location.getCollectedBy());

    }

}
