package org.openhds.service.impl.census;

import org.openhds.domain.model.census.SocialGroup;
import org.openhds.repository.concrete.census.SocialGroupRepository;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.openhds.service.impl.FieldWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/13/15.
 */
@Component
public class SocialGroupService extends AbstractAuditableExtIdService<SocialGroup, SocialGroupRepository> {

    @Autowired
    FieldWorkerService fieldWorkerService;

    @Autowired
    public SocialGroupService(SocialGroupRepository repository) {
        super(repository);
    }

    @Override
    protected SocialGroup makeUnknownEntity() {
        SocialGroup socialGroup = new SocialGroup();
        socialGroup.setGroupName("unknown");
        socialGroup.setExtId("unknown");
        socialGroup.setCollectedBy(fieldWorkerService.getUnknownEntity());
        socialGroup.setCollectionDateTime(ZonedDateTime.now());
        return socialGroup;
    }
}
