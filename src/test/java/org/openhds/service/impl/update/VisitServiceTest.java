package org.openhds.service.impl.update;

import org.junit.Test;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.Location;
import org.openhds.domain.model.update.Visit;
import org.openhds.service.AuditableExtIdServiceTest;
import org.openhds.service.impl.census.LocationService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by bsh on 7/13/15.
 */
public class VisitServiceTest extends AuditableExtIdServiceTest<Visit, VisitService> {

    @Autowired
    private LocationService locationService;

    @Autowired
    @Override
    protected void initialize(VisitService service) {
        this.service = service;
    }

    @Override
    protected Visit makeInvalidEntity() {
        return new Visit();
    }

    @Test
    public void recordWithExistingReferences() {

        //Grab a valid entity
        Visit visit = makeValidEntity("validName", "validId");

        //Remember the references and clear them
        Location location = visit.getLocation();
        visit.setLocation(null);

        FieldWorker fieldWorker = visit.getCollectedBy();
        visit.setCollectedBy(null);

        // pass it all into the record method
        visit = service.recordVisit(visit, location.getUuid(), fieldWorker.getUuid());


        //Check that the originals match the ones pulled out from findOrMakePlaceholder()
        assertNotNull(visit.getLocation());
        assertEquals(visit.getLocation(), location);

        assertNotNull(visit.getCollectedBy());
        assertEquals(visit.getCollectedBy(), fieldWorker);

    }

    @Test
    public void recordWithNonexistentReferences(){

        //Make a new entity with no references
        Visit visit = makeValidEntity("validName", "validId");
        visit.setLocation(null);
        visit.setCollectedBy(null);

        //Pass it in with new reference uuids
        visit = service.recordVisit(visit, "lucutiun", "feldwarker");

        //check that they were persisted
        assertNotNull(visit.getLocation());
        assertEquals(visit.getLocation().getUuid(), "lucutiun");
        assertNotNull(visit.getCollectedBy());
        assertEquals(visit.getCollectedBy().getUuid(), "feldwarker");

    }
}
