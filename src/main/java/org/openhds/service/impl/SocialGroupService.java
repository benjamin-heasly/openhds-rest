package org.openhds.service.impl;

import org.openhds.domain.model.SocialGroup;
import org.openhds.repository.concrete.SocialGroupRepository;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by bsh on 7/13/15.
 */
public class SocialGroupService extends AbstractAuditableCollectedService<SocialGroup, SocialGroupRepository> {

    @Autowired
    public SocialGroupService(SocialGroupRepository repository) {
        super(repository);
    }

    @Override
    protected SocialGroup makeUnknownEntity() {
        SocialGroup socialGroup = new SocialGroup();
        socialGroup.setGroupName("unknown");
        socialGroup.setExtId("unknown");
        return socialGroup;
    }
}
