package org.openhds.service.impl.census;

import org.junit.Test;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.census.Location;
import org.openhds.domain.model.census.Residency;
import org.openhds.service.AuditableCollectedServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Wolfe on 7/14/2015.
 */
public class ResidencyServiceTest extends AuditableCollectedServiceTest<Residency, ResidencyService> {


    @Autowired
    private IndividualService individualService;

    @Autowired
    private LocationService locationService;

    @Override
    protected Residency makeInvalidEntity() {
        return new Residency();
    }

    @Override
    protected Residency makeValidEntity(String name, String id) {
        Residency residency = new Residency();
        residency.setUuid(id);
        residency.setIndividual(individualService.findAll(UUID_SORT).toList().get(0));
        residency.setLocation(locationService.findAll(UUID_SORT).toList().get(0));
        residency.setStartDate(ZonedDateTime.now().minusYears(1));
        residency.setStartType(name);

        initCollectedFields(residency);

        return residency;
    }

    @Override
    @Autowired
    protected void initialize(ResidencyService service) {
        this.service = service;
    }

    public void recordWithExistingReferences() {

        //Grab a valid entity
        Residency residency = makeValidEntity("validName", "validId");

        FieldWorker fieldWorker = residency.getCollectedBy();
        residency.setCollectedBy(null);

        Individual individual = residency.getIndividual();
        residency.setIndividual(null);

        Location location = residency.getLocation();
        residency.setLocation(null);

        // pass it all into the record method
        residency = service.recordResidency(residency, individual.getUuid(), location.getUuid(), fieldWorker.getUuid());


        //Check that the originals match the ones pulled out from findOrMakePlaceholder()
        assertNotNull(residency.getCollectedBy());
        assertEquals(residency.getCollectedBy(), fieldWorker);

        assertNotNull(residency.getIndividual());
        assertEquals(residency.getIndividual(), individual);

        assertNotNull(residency.getLocation());
        assertEquals(residency.getLocation(), location);

    }

    @Test
    public void recordWithNonexistentReferences() {

        //Make a new entity with no references
        Residency residency = makeValidEntity("validName", "validId");
        residency.setCollectedBy(null);
        residency.setIndividual(null);
        residency.setLocation(null);

        //Pass it in with new reference uuids
        residency = service.recordResidency(residency, "induvudual", "lucutiun", "feldwarker");

        //check that they were persisted
        assertNotNull(residency.getCollectedBy());
        assertNotNull(residency.getIndividual());
        assertNotNull(residency.getLocation());

    }
}
