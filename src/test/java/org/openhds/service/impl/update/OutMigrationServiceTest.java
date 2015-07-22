package org.openhds.service.impl.update;

import org.junit.Test;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.census.Residency;
import org.openhds.domain.model.update.InMigration;
import org.openhds.domain.model.update.OutMigration;
import org.openhds.domain.model.update.Visit;
import org.openhds.service.AuditableCollectedServiceTest;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.ResidencyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by bsh on 7/13/15.
 */
public class OutMigrationServiceTest extends AuditableCollectedServiceTest<OutMigration, OutMigrationService> {

    @Autowired
    private VisitService visitService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private ResidencyService residencyService;

    @Autowired
    @Override
    protected void initialize(OutMigrationService service) {
        this.service = service;
    }

    @Override
    protected OutMigration makeInvalidEntity() {
        return new OutMigration();
    }

    @Test
    public void recordWithExistingReferences() {

        //Grab a valid entity
        OutMigration outMigration = makeValidEntity("validName", "validId");

        FieldWorker fieldWorker = outMigration.getCollectedBy();
        outMigration.setCollectedBy(null);

        Visit visit = outMigration.getVisit();
        outMigration.setVisit(null);

        Individual individual = outMigration.getIndividual();
        outMigration.setIndividual(null);

        Residency residency = outMigration.getResidency();
        outMigration.setResidency(null);

        // pass it all into the record method
        outMigration = service.recordOutMigration(outMigration,
                individual.getUuid(),
                residency.getUuid(),
                visit.getUuid(),
                fieldWorker.getUuid());


        //Check that the originals match the ones pulled out from findOrMakePlaceholder()
        assertNotNull(outMigration.getCollectedBy());
        assertEquals(outMigration.getCollectedBy(), fieldWorker);

        assertNotNull(outMigration.getIndividual());
        assertEquals(outMigration.getIndividual(), individual);

        assertNotNull(outMigration.getResidency());
        assertEquals(outMigration.getResidency(), residency);

        assertNotNull(outMigration.getVisit());
        assertEquals(outMigration.getVisit(), visit);



    }

    @Test
    public void recordWithNonexistentReferences(){

        //Make a new entity with no references
        OutMigration outMigration = makeValidEntity("validName", "validId");
        outMigration.setCollectedBy(null);
        outMigration.setIndividual(null);
        outMigration.setResidency(null);
        outMigration.setVisit(null);


        //Pass it in with new reference uuids
        outMigration = service.recordOutMigration(outMigration, "induvudual", "residuncy", "vusut", "feldwarker");

        //check that they were persisted
        assertNotNull(outMigration.getCollectedBy());
        assertEquals(outMigration.getCollectedBy().getUuid(), "feldwarker");

        assertNotNull(outMigration.getIndividual());
        assertEquals(outMigration.getIndividual().getUuid(), "induvudual");

        assertNotNull(outMigration.getResidency());
        assertEquals(outMigration.getResidency().getUuid(), "residuncy");

        assertNotNull(outMigration.getVisit());
        assertEquals(outMigration.getVisit().getUuid(), "vusut");


    }
}
