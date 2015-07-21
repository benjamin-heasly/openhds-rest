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
    protected Membership makeUnknownEntity() {
        Membership membership = new Membership();

        membership.setCollectedBy(fieldWorkerService.getUnknownEntity());
        membership.setCollectionDateTime(ZonedDateTime.now());

        membership.setSocialGroup(socialGroupService.getUnknownEntity());
        membership.setIndividual(individualService.getUnknownEntity());
        membership.setStartDate(ZonedDateTime.now().minusYears(1));
        membership.setStartType("unknown");
        membership.setRelationshipToGroupHead("unknown");

        return membership;
    }

    @Override
    public void validate(Membership membership, ErrorLog errorLog) {
        super.validate(membership, errorLog);
    }

    public Membership recordMembership(Membership membership, String individualId, String socialGroupId, String fieldWorkerId){
        membership.setIndividual(individualService.findOrMakePlaceHolder(individualId));
        membership.setSocialGroup(socialGroupService.findOrMakePlaceHolder(socialGroupId));
        membership.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        return createOrUpdate(membership);
    }

}
