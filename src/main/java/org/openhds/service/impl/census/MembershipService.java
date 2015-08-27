package org.openhds.service.impl.census;

import org.openhds.domain.model.census.Membership;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.census.MembershipRepository;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/1/2015.
 */
@Service
public class MembershipService extends AbstractAuditableCollectedService<Membership, MembershipRepository> {

    @Autowired
    private IndividualService individualService;

    @Autowired
    private SocialGroupService socialGroupService;

    @Autowired
    public MembershipService(MembershipRepository membershipRepository) {
        super(membershipRepository);
    }

    @Override
    public Membership makePlaceHolder(String id, String name) {
        Membership membership = new Membership();
        membership.setUuid(id);
        membership.setStatus(name);
        membership.setSocialGroup(socialGroupService.getUnknownEntity());
        membership.setIndividual(individualService.getUnknownEntity());
        membership.setStartDate(ZonedDateTime.now().minusYears(1));
        membership.setStartType(name);

        initPlaceHolderCollectedFields(membership);

        return membership;
    }

    @Override
    public void validate(Membership membership, ErrorLog errorLog) {
        super.validate(membership, errorLog);

        if(membership.getStartDate().isAfter(membership.getCollectionDateTime())){
            errorLog.appendError("Membership cannot have a startDate in the future.");
        }

        if(null != membership.getEndDate() &&
            membership.getStartDate().isAfter(membership.getEndDate())){
            errorLog.appendError("Membership cannot have a startDate before its endDate.");
        }

    }

    public Membership recordMembership(Membership membership, String individualId, String socialGroupId, String fieldWorkerId){
        membership.setIndividual(individualService.findOrMakePlaceHolder(individualId));
        membership.setSocialGroup(socialGroupService.findOrMakePlaceHolder(socialGroupId));
        membership.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        membership.setStatus(membership.NORMAL_STATUS);
        return createOrUpdate(membership);
    }

}
