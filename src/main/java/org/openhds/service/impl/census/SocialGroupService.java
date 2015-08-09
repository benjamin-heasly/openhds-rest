package org.openhds.service.impl.census;

import org.openhds.domain.model.census.SocialGroup;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.census.SocialGroupRepository;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/13/15.
 */
@Service
public class SocialGroupService extends AbstractAuditableExtIdService<SocialGroup, SocialGroupRepository> {

    @Autowired
    public SocialGroupService(SocialGroupRepository repository) {
        super(repository);
    }

    @Override
    public SocialGroup makePlaceHolder(String id, String name) {
        SocialGroup socialGroup = new SocialGroup();
        socialGroup.setUuid(id);
        socialGroup.setGroupName(name);
        socialGroup.setExtId(name);

        initPlaceHolderCollectedFields(socialGroup);

        return socialGroup;
    }

    public SocialGroup recordSocialGroup(SocialGroup socialGroup, String fieldWorkerId){
        socialGroup.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        return createOrUpdate(socialGroup);
    }

    @Override
    public void validate(SocialGroup entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);
    }
}
