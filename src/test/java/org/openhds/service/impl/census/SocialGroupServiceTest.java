package org.openhds.service.impl.census;

import org.openhds.domain.model.census.SocialGroup;
import org.openhds.service.UuidServiceTest;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.SocialGroupService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/13/15.
 */
public class SocialGroupServiceTest extends UuidServiceTest<SocialGroup, SocialGroupService> {

    @Autowired
    FieldWorkerService fieldWorkerService;

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

        socialGroup.setCollectedBy(fieldWorkerService.findAll(UUID_SORT).toList().get(0));
        socialGroup.setCollectionDateTime(ZonedDateTime.now());

        return socialGroup;
    }
}
