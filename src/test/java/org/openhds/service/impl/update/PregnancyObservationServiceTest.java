package org.openhds.service.impl.update;

import org.junit.Test;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.update.PregnancyObservation;
import org.openhds.domain.model.update.Visit;
import org.openhds.service.AuditableCollectedServiceTest;
import org.openhds.service.impl.census.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by bsh on 7/13/15.
 */
public class PregnancyObservationServiceTest extends AuditableCollectedServiceTest<
        PregnancyObservation, PregnancyObservationService> {

    @Autowired
    private VisitService visitService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    @Override
    protected void initialize(PregnancyObservationService service) {
        this.service = service;
    }

    @Override
    protected PregnancyObservation makeInvalidEntity() {
        return new PregnancyObservation();
    }

    @Test
    public void recordWithExistingReferences() {

        //Grab a valid entity
        PregnancyObservation pregnancyObservation = makeValidEntity("validName", "validId");

        FieldWorker fieldWorker = pregnancyObservation.getCollectedBy();
        pregnancyObservation.setCollectedBy(null);

        Visit visit = pregnancyObservation.getVisit();
        pregnancyObservation.setVisit(null);

        Individual mother = pregnancyObservation.getMother();
        pregnancyObservation.setMother(null);

        // pass it all into the record method
        pregnancyObservation = service.recordPregnancyObservation(pregnancyObservation, mother.getUuid(), visit.getUuid(), fieldWorker.getUuid());


        //Check that the originals match the ones pulled out from findOrMakePlaceholder()
        assertNotNull(pregnancyObservation.getCollectedBy());
        assertEquals(pregnancyObservation.getCollectedBy(), fieldWorker);

        assertNotNull(pregnancyObservation.getVisit());
        assertEquals(pregnancyObservation.getVisit(), visit);

        assertNotNull(pregnancyObservation.getMother());
        assertEquals(pregnancyObservation.getMother(), mother);

    }

    @Test
    public void recordWithNonexistentReferences(){

        //Make a new entity with no references
        PregnancyObservation pregnancyObservation = makeValidEntity("validName", "validId");
        pregnancyObservation.setCollectedBy(null);
        pregnancyObservation.setVisit(null);
        pregnancyObservation.setMother(null);

        //Pass it in with new reference uuids
        pregnancyObservation = service.recordPregnancyObservation(pregnancyObservation, "mutur", "vusut", "feldwarker");

        //check that they were persisted
        assertNotNull(pregnancyObservation.getCollectedBy());
        assertEquals(pregnancyObservation.getCollectedBy().getUuid(), "feldwarker");

        assertNotNull(pregnancyObservation.getVisit());
        assertEquals(pregnancyObservation.getVisit().getUuid(), "vusut");

        assertNotNull(pregnancyObservation.getMother());
        assertEquals(pregnancyObservation.getMother().getUuid(), "mutur");

    }
}
