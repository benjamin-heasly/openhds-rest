package org.openhds.service.impl.census;

import org.junit.Test;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.Individual;
import org.openhds.service.AuditableExtIdServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Wolfe on 7/13/2015.
 */
public class IndividualServiceTest extends AuditableExtIdServiceTest<Individual, IndividualService> {

    public static final String FIELDWORKER_ID = "feldwarker";
    public static final String SOCIALGROUP_ID = "suculgrup";
    public static final String LOCATION_ID = "lucution";
    public static final String RESIDENCY_ID = "rusuduncy";
    public static final String MEMBERSHIP_ID = "mumbershup";
    public static final String RELATIONSHIP_ID = "relutuionshup";

    @Override
    protected Individual makeInvalidEntity() {
        return new Individual();
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
        individual = service.recordIndividual(individual, SOCIALGROUP_ID,
                                                            LOCATION_ID,
                                                            RESIDENCY_ID,
                                                            MEMBERSHIP_ID,
                                                            RELATIONSHIP_ID,
                                                            fieldWorker.getUuid());


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
        individual = service.recordIndividual(individual, SOCIALGROUP_ID,
                                                            LOCATION_ID,
                                                            RESIDENCY_ID,
                                                            MEMBERSHIP_ID,
                                                            RELATIONSHIP_ID,
                                                            FIELDWORKER_ID);

        //check that they were persisted
        assertNotNull(individual.getCollectedBy());
        assertEquals(individual.getCollectedBy().getUuid(), FIELDWORKER_ID);

    }
}
