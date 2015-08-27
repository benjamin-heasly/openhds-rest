package org.openhds.service.impl.census;

import org.openhds.domain.model.ProjectCode;
import org.openhds.domain.model.census.SocialGroup;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.census.SocialGroupRepository;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        socialGroup.setStatus(name);
        socialGroup.setGroupName(name);
        socialGroup.setExtId(name);
        socialGroup.setGroupType(projectCodeService.findByCodeGroup(ProjectCode.SOCIALGROUP_TYPE).get(0).getCodeValue());

        initPlaceHolderCollectedFields(socialGroup);

        return socialGroup;
    }

    public SocialGroup recordSocialGroup(SocialGroup socialGroup, String fieldWorkerId){
        socialGroup.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        socialGroup.setStatus(socialGroup.NORMAL_STATUS);
        return createOrUpdate(socialGroup);
    }

    @Override
    public void validate(SocialGroup socialGroup, ErrorLog errorLog) {
        super.validate(socialGroup, errorLog);

        if(!projectCodeService.isValueInCodeGroup(socialGroup.getGroupType(), ProjectCode.SOCIALGROUP_TYPE)) {
            errorLog.appendError("SocialGroup cannot have a type of: ["+socialGroup.getGroupType()+"].");
        }
    }
}
