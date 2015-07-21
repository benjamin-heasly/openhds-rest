package org.openhds.service.impl.update;

import org.junit.Test;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.census.Residency;
import org.openhds.domain.model.update.Death;
import org.openhds.domain.model.update.InMigration;
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
public class InMigrationServiceTest extends AuditableCollectedServiceTest<InMigration, InMigrationService> {

    @Autowired
    private VisitService visitService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private ResidencyService residencyService;

    @Autowired
    @Override
    protected void initialize(InMigrationService service) {
        this.service = service;
    }

    @Override
    protected InMigration makeInvalidEntity() {
        return new InMigration();
    }

    @Override
    protected InMigration makeValidEntity(String name, String id) {
        InMigration inMigration = new InMigration();
        inMigration.setUuid(id);
        inMigration.setVisit(visitService.findAll(UUID_SORT).toList().get(0));
        inMigration.setIndividual(individualService.findAll(UUID_SORT).toList().get(0));
        inMigration.setResidency(residencyService.findAll(UUID_SORT).toList().get(0));
        inMigration.setMigrationDate(ZonedDateTime.now().minusYears(1));
        inMigration.setMigrationType(name);

        initCollectedFields(inMigration);

        return inMigration;
    }

    @Test
    public void recordWithExistingReferences() {

        //Grab a valid entity
        InMigration inMigration = makeValidEntity("validName", "validId");

        FieldWorker fieldWorker = inMigration.getCollectedBy();
        inMigration.setCollectedBy(null);

        Visit visit = inMigration.getVisit();
        inMigration.setVisit(null);

        Individual individual = inMigration.getIndividual();
        inMigration.setIndividual(null);

        Residency residency = inMigration.getResidency();
        inMigration.setResidency(null);

        // pass it all into the record method
        inMigration = service.recordInMigration(inMigration,
                individual.getUuid(),
                residency.getUuid(),
                visit.getUuid(),
                fieldWorker.getUuid());


        //Check that the originals match the ones pulled out from findOrMakePlaceholder()
        assertNotNull(inMigration.getCollectedBy());
        assertEquals(inMigration.getCollectedBy(), fieldWorker);

        assertNotNull(inMigration.getIndividual());
        assertEquals(inMigration.getIndividual(), individual);

        assertNotNull(inMigration.getResidency());
        assertEquals(inMigration.getResidency(), residency);

        assertNotNull(inMigration.getVisit());
        assertEquals(inMigration.getVisit(), visit);



    }

    @Test
    public void recordWithNonexistentReferences(){

        //Make a new entity with no references
        InMigration inMigration = makeValidEntity("validName", "validId");
        inMigration.setCollectedBy(null);
        inMigration.setIndividual(null);
        inMigration.setResidency(null);
        inMigration.setVisit(null);


        //Pass it in with new reference uuids
        inMigration = service.recordInMigration(inMigration, "induvudual", "residuncy", "vusut", "feldwarker");

        //check that they were persisted
        assertNotNull(inMigration.getCollectedBy());
        assertEquals(inMigration.getCollectedBy().getUuid(), "feldwarker");

        assertNotNull(inMigration.getIndividual());
        assertEquals(inMigration.getIndividual().getUuid(), "induvudual");

        assertNotNull(inMigration.getResidency());
        assertEquals(inMigration.getResidency().getUuid(), "residuncy");

        assertNotNull(inMigration.getVisit());
        assertEquals(inMigration.getVisit().getUuid(), "vusut");


    }
}
