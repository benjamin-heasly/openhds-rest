package org.openhds.service.impl.update;

import org.junit.Test;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.update.PregnancyObservation;
import org.openhds.domain.model.update.PregnancyOutcome;
import org.openhds.domain.model.update.Visit;
import org.openhds.service.AuditableCollectedServiceTest;
import org.openhds.service.impl.census.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Wolfe on 7/15/2015.
 */
public class PregnancyOutcomeServiceTest extends AuditableCollectedServiceTest<PregnancyOutcome, PregnancyOutcomeService> {

    @Autowired
    private IndividualService individualService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private PregnancyResultService pregnancyResultService;

    @Override
    protected PregnancyOutcome makeInvalidEntity() {
        return new PregnancyOutcome();
    }

    @Override
    protected PregnancyOutcome makeValidEntity(String name, String id) {
        PregnancyOutcome pregnancyOutcome = new PregnancyOutcome();
        pregnancyOutcome.setUuid(id);
        pregnancyOutcome.setOutcomeDate(ZonedDateTime.now().minusYears(1));
        pregnancyOutcome.setPregnancyResults(pregnancyResultService.findAll(UUID_SORT).toList());
        pregnancyOutcome.setMother(individualService.findAll(UUID_SORT).toList().get(0));
        pregnancyOutcome.setFather(individualService.findAll(UUID_SORT).toList().get(0));
        pregnancyOutcome.setVisit(visitService.findAll(UUID_SORT).toList().get(0));

        initCollectedFields(pregnancyOutcome);

        return pregnancyOutcome;
    }

    @Override
    @Autowired
    protected void initialize(PregnancyOutcomeService service) {
        this.service = service;
    }

    @Test
    public void recordWithExistingReferences() {

        //Grab a valid entity
        PregnancyOutcome pregnancyOutcome = makeValidEntity("validName", "validId");

        FieldWorker fieldWorker = pregnancyOutcome.getCollectedBy();
        pregnancyOutcome.setCollectedBy(null);

        Visit visit = pregnancyOutcome.getVisit();
        pregnancyOutcome.setVisit(null);

        Individual mother = pregnancyOutcome.getMother();
        pregnancyOutcome.setMother(null);

        Individual father = pregnancyOutcome.getFather();
        pregnancyOutcome.setFather(null);

        // pass it all into the record method
        pregnancyOutcome = service.recordPregnancyOutcome(pregnancyOutcome,
                mother.getUuid(),
                father.getUuid(),
                visit.getUuid(),
                fieldWorker.getUuid());


        //Check that the originals match the ones pulled out from findOrMakePlaceholder()
        assertNotNull(pregnancyOutcome.getCollectedBy());
        assertEquals(pregnancyOutcome.getCollectedBy(), fieldWorker);

        assertNotNull(pregnancyOutcome.getVisit());
        assertEquals(pregnancyOutcome.getVisit(), visit);

        assertNotNull(pregnancyOutcome.getMother());
        assertEquals(pregnancyOutcome.getMother(), mother);

        assertNotNull(pregnancyOutcome.getFather());
        assertEquals(pregnancyOutcome.getFather(), father);

    }

    @Test
    public void recordWithNonexistentReferences(){

        //Make a new entity with no references
        PregnancyOutcome pregnancyOutcome = makeValidEntity("validName", "validId");
        pregnancyOutcome.setCollectedBy(null);
        pregnancyOutcome.setVisit(null);
        pregnancyOutcome.setMother(null);
        pregnancyOutcome.setFather(null);

        //Pass it in with new reference uuids
        pregnancyOutcome = service.recordPregnancyOutcome(pregnancyOutcome, "mutur", "fatur", "vusut", "feldwarker");

        //check that they were persisted
        assertNotNull(pregnancyOutcome.getCollectedBy());
        assertEquals(pregnancyOutcome.getCollectedBy().getUuid(), "feldwarker");

        assertNotNull(pregnancyOutcome.getVisit());
        assertEquals(pregnancyOutcome.getVisit().getUuid(), "vusut");

        assertNotNull(pregnancyOutcome.getMother());
        assertEquals(pregnancyOutcome.getMother().getUuid(), "mutur");

        assertNotNull(pregnancyOutcome.getFather());
        assertEquals(pregnancyOutcome.getFather().getUuid(), "fatur");

    }
}
