package org.openhds.service.impl.update;

import org.junit.Test;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.update.Death;
import org.openhds.domain.model.update.Visit;
import org.openhds.service.AuditableCollectedServiceTest;
import org.openhds.service.impl.census.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Wolfe on 7/14/2015.
 */
public class DeathServiceTest extends AuditableCollectedServiceTest<Death, DeathService> {

    @Autowired
    private IndividualService individualService;

    @Autowired
    private VisitService visitService;

    @Override
    protected Death makeInvalidEntity() {
        return new Death();
    }

    @Override
    @Autowired
    protected void initialize(DeathService service) {
        this.service = service;
    }

    @Test
    public void recordWithExistingReferences() {

        //Grab a valid entity
        Death death = makeValidEntity("validName", "validId");

        FieldWorker fieldWorker = death.getCollectedBy();
        death.setCollectedBy(null);

        Visit visit = death.getVisit();
        death.setVisit(null);

        Individual individual = death.getIndividual();
        death.setIndividual(null);

        // pass it all into the record method
        death = service.recordDeath(death, individual.getUuid(), visit.getUuid(), fieldWorker.getUuid());


        //Check that the originals match the ones pulled out from findOrMakePlaceholder()
        assertNotNull(death.getCollectedBy());
        assertEquals(death.getCollectedBy(), fieldWorker);

        assertNotNull(death.getVisit());
        assertEquals(death.getVisit(), visit);

        assertNotNull(death.getIndividual());
        assertEquals(death.getIndividual(), individual);

    }

    @Test
    public void recordWithNonexistentReferences(){

        //Make a new entity with no references
        Death death = makeValidEntity("validName", "validId");
        death.setCollectedBy(null);
        death.setVisit(null);
        death.setIndividual(null);

        //Pass it in with new reference uuids
        death = service.recordDeath(death, "induvudual", "vusut", "feldwarker");

        //check that they were persisted
        assertNotNull(death.getCollectedBy());
        assertEquals(death.getCollectedBy().getUuid(), "feldwarker");

        assertNotNull(death.getVisit());
        assertEquals(death.getVisit().getUuid(), "vusut");

        assertNotNull(death.getIndividual());
        assertEquals(death.getIndividual().getUuid(), "induvudual");

    }
}
