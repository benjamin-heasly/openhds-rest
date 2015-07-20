package org.openhds.service.impl.census;

import org.junit.Test;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.census.Relationship;
import org.openhds.service.AuditableCollectedServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Wolfe on 7/13/2015.
 */
public class RelationshipServiceTest extends AuditableCollectedServiceTest<Relationship, RelationshipService> {

    @Autowired
    private IndividualService individualService;

    @Override
    protected Relationship makeInvalidEntity() {
        return new Relationship();
    }

    @Override
    protected Relationship makeValidEntity(String name, String id) {
        Relationship relationship = new Relationship();
        relationship.setUuid(id);
        relationship.setRelationshipType(name);
        relationship.setStartDate(ZonedDateTime.now().minusYears(1));
        relationship.setIndividualA(individualService.findAll(UUID_SORT).toList().get(0));
        relationship.setIndividualB(individualService.findAll(UUID_SORT).toList().get(0));

        initCollectedFields(relationship);

        return relationship;
    }

    @Override
    @Autowired
    protected void initialize(RelationshipService service) {
        this.service = service;
    }

    public void recordWithExistingReferences() {

        //Grab a valid entity
        Relationship relationship = makeValidEntity("validName", "validId");

        FieldWorker fieldWorker = relationship.getCollectedBy();
        relationship.setCollectedBy(null);

        Individual individualA = relationship.getIndividualA();
        relationship.setIndividualA(null);

        Individual individualB = relationship.getIndividualB();
        relationship.setIndividualB(null);

        // pass it all into the record method
        relationship = service.recordRelationship(relationship, individualA.getUuid(), individualB.getUuid(), fieldWorker.getUuid());


        //Check that the originals match the ones pulled out from findOrMakePlaceholder()
        assertNotNull(relationship.getCollectedBy());
        assertEquals(relationship.getCollectedBy(), fieldWorker);

        assertNotNull(relationship.getIndividualA());
        assertEquals(relationship.getIndividualA(), individualA);

        assertNotNull(relationship.getIndividualB());
        assertEquals(relationship.getIndividualB(), individualB);

    }

    @Test
    public void recordWithNonexistentReferences() {

        //Make a new entity with no references
        Relationship relationship = makeValidEntity("validName", "validId");
        relationship.setCollectedBy(null);
        relationship.setIndividualA(null);
        relationship.setIndividualB(null);

        //Pass it in with new reference uuids
        relationship = service.recordRelationship(relationship, "induvudualA", "induvudualB", "feldwarker");

        //check that they were persisted
        assertNotNull(relationship.getCollectedBy());
        assertNotNull(relationship.getIndividualA());
        assertNotNull(relationship.getIndividualB());

    }
}
