package org.openhds.service.impl.census;

import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.census.Membership;
import org.openhds.domain.model.census.SocialGroup;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.census.MembershipRepository;
import org.openhds.repository.results.EntityIterator;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Iterator;

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
        return createOrUpdate(membership);
    }

    public EntityIterator<Membership> findByIndividual(Sort sort, Individual individual) {
        return iteratorFromPageable(pageable -> repository.findByDeletedFalseAndIndividual(individual, pageable), sort);
    }

    public EntityIterator<Membership> findBySocialGroup(Sort sort, SocialGroup socialGroup) {
        return iteratorFromPageable(pageable -> repository.findByDeletedFalseAndSocialGroup(socialGroup, pageable), sort);
    }

    public EntityIterator<Membership> findByIndividualAndSocialGroup(Sort sort,Individual individual, SocialGroup socialGroup) {
        return iteratorFromPageable(pageable -> repository.findByDeletedFalseAndIndividualAndSocialGroup(individual, socialGroup, pageable), sort);
    }

}
