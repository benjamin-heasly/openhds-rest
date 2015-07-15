package org.openhds.service.impl.census;

import org.openhds.domain.model.census.SocialGroup;
import org.openhds.service.AuditableExtIdServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

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
}
