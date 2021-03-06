package org.openhds.service.impl.census;


import org.junit.Test;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.census.Membership;
import org.openhds.domain.model.census.SocialGroup;
import org.openhds.service.AuditableCollectedServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by bsh on 6/17/15.
 */
public class MembershipServiceTest extends AuditableCollectedServiceTest<Membership, MembershipService> {

    @Autowired
    private IndividualService individualService;

    @Autowired
    private SocialGroupService socialGroupService;

    @Override
    protected Membership makeInvalidEntity() {
        return new Membership();
    }

    @Override
    @Autowired
    protected void initialize(MembershipService service) {
        this.service = service;
    }

    @Test
    public void recordWithExistingReferences() {

        //Grab a valid entity
        Membership membership = makeValidEntity("validName", "validId");

        FieldWorker fieldWorker = membership.getCollectedBy();
        membership.setCollectedBy(null);

        Individual individual = membership.getIndividual();
        membership.setIndividual(null);

        SocialGroup socialGroup = membership.getSocialGroup();
        membership.setSocialGroup(null);

        // pass it all into the record method
        membership = service.recordMembership(membership, individual.getUuid(), socialGroup.getUuid(), fieldWorker.getUuid());


        //Check that the originals match the ones pulled out from findOrMakePlaceholder()
        assertNotNull(membership.getCollectedBy());
        assertEquals(membership.getCollectedBy(), fieldWorker);

        assertNotNull(membership.getIndividual());
        assertEquals(membership.getIndividual(), individual);

        assertNotNull(membership.getSocialGroup());
        assertEquals(membership.getSocialGroup(), socialGroup);

    }

    @Test
    public void recordWithNonexistentReferences() {

        //Make a new entity with no references
        Membership membership = makeValidEntity("validName", "validId");
        membership.setCollectedBy(null);
        membership.setIndividual(null);
        membership.setSocialGroup(null);

        //Pass it in with new reference uuids
        membership = service.recordMembership(membership, "induvudual", "suciulGroup", "feldwarker");

        //check that they were persisted
        assertNotNull(membership.getCollectedBy());
        assertEquals(membership.getCollectedBy().getUuid(), "feldwarker");
        assertNotNull(membership.getIndividual());
        assertEquals(membership.getIndividual().getUuid(), "induvudual");
        assertNotNull(membership.getSocialGroup());
        assertEquals(membership.getSocialGroup().getUuid(), "suciulGroup");

    }

    @Test
    public void correctEntityReferenceOnUpdate(){

        //Make a new entity with no references
        Membership membership = makeValidEntity("validName", "validId");
        membership.setCollectedBy(null);
        membership.setIndividual(null);
        membership.setSocialGroup(null);

        //Pass it in with new reference uuids
        membership = service.recordMembership(membership, "induvudual", "suciulGroup", "feldwarker");

        //make the "real" entity to overwrite the old one.
        Individual individual = individualService.makePlaceHolder("induvudual", "Billy");

        individualService.createOrUpdate(individual);

        //Get the service with the updated reference
        membership = service.findOne("validId");

        //cheggerout
        assertEquals(membership.getIndividual().getFirstName(), "Billy");

    }

}
