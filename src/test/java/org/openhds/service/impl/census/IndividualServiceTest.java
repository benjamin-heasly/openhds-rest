package org.openhds.service.impl.census;

import org.junit.Test;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.census.Location;
import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.service.AuditableExtIdServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Wolfe on 7/13/2015.
 */
public class IndividualServiceTest extends AuditableExtIdServiceTest<Individual, IndividualService> {

    @Override
    protected Individual makeInvalidEntity() {
        return new Individual();
    }

    @Override
    protected Individual makeValidEntity(String name, String id) {
        Individual individual = new Individual();
        individual.setUuid(id);
        individual.setExtId(name);
        individual.setFirstName(name);
        individual.setDateOfBirth(ZonedDateTime.now().minusYears(1));

        initCollectedFields(individual);

        return individual;
    }

    @Override
    @Autowired
    protected void initialize(IndividualService service) {
        this.service = service;
    }

    @Test
    public void recordWithExistingReferences() {

        //Grab a valid entity
        Individual individual = makeValidEntity("validName", "validId");

        FieldWorker fieldWorker = individual.getCollectedBy();
        individual.setCollectedBy(null);

        // pass it all into the record method
        individual = service.recordIndividual(individual, fieldWorker.getUuid());


        //Check that the originals match the ones pulled out from findOrMakePlaceholder()
        assertNotNull(individual.getCollectedBy());
        assertEquals(individual.getCollectedBy(), fieldWorker);

    }

    @Test
    public void recordWithNonexistentReferences(){

        //Make a new entity with no references
        Individual individual = makeValidEntity("validName", "validId");
        individual.setCollectedBy(null);

        //Pass it in with new reference uuids
        individual = service.recordIndividual(individual, "feldwarker");

        //check that they were persisted
        assertNotNull(individual.getCollectedBy());

    }
}
