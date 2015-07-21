package org.openhds.service.impl.census;

import org.junit.Test;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.census.SocialGroup;
import org.openhds.service.AuditableExtIdServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by bsh on 7/13/15.
 */
public class SocialGroupServiceTest extends AuditableExtIdServiceTest<SocialGroup, SocialGroupService> {

    @Autowired
    @Override
    protected void initialize(SocialGroupService service) {
        this.service = service;
    }

    @Override
    protected SocialGroup makeInvalidEntity() {
        return new SocialGroup();
    }

    @Override
    protected SocialGroup makeValidEntity(String name, String id) {
        SocialGroup socialGroup = new SocialGroup();
        socialGroup.setUuid(id);
        socialGroup.setGroupName(name);
        socialGroup.setExtId(name);

        initCollectedFields(socialGroup);

        return socialGroup;
    }

    @Test
    public void recordWithExistingReferences() {

        //Grab a valid entity
        SocialGroup socialGroup = makeValidEntity("validName", "validId");

        FieldWorker fieldWorker = socialGroup.getCollectedBy();
        socialGroup.setCollectedBy(null);

        // pass it all into the record method
        socialGroup = service.recordSocialGroup(socialGroup, fieldWorker.getUuid());


        //Check that the originals match the ones pulled out from findOrMakePlaceholder()
        assertNotNull(socialGroup.getCollectedBy());
        assertEquals(socialGroup.getCollectedBy(), fieldWorker);

    }

    @Test
    public void recordWithNonexistentReferences(){

        //Make a new entity with no references
        SocialGroup socialGroup = makeValidEntity("validName", "validId");
        socialGroup.setCollectedBy(null);

        //Pass it in with new reference uuids
        socialGroup = service.recordSocialGroup(socialGroup, "feldwarker");

        //check that they were persisted
        assertNotNull(socialGroup.getCollectedBy());
        assertEquals(socialGroup.getCollectedBy().getUuid(), "feldwarker");

    }
}
