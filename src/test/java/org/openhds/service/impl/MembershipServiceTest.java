package org.openhds.service.impl;


import org.openhds.domain.model.Membership;
import org.openhds.service.AuditableCollectedServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

/**
 * Created by bsh on 6/17/15.
 */
public class MembershipServiceTest extends AuditableCollectedServiceTest<Membership, MembershipService> {

    @Autowired
    private FieldWorkerService fieldWorkerService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private SocialGroupService socialGroupService;

    @Override
    protected Membership makeInvalidEntity() {
        return new Membership();
    }

    @Override
    protected Membership makeValidEntity(String name, String id) {
        Membership membership = new Membership();
        membership.setUuid(id);

        membership.setCollectedBy(fieldWorkerService.findAll(UUID_SORT).toList().get(0));
        membership.setCollectionDateTime(ZonedDateTime.now());

        membership.setIndividual(individualService.findAll(UUID_SORT).toList().get(0));
        membership.setSocialGroup(socialGroupService.findAll(UUID_SORT).toList().get(0));
        membership.setbIsToA(name);
        membership.setStartDate(ZonedDateTime.now().minusYears(1));
        membership.setStartType(name);

        return membership;
    }

    @Override
    @Autowired
    protected void initialize(MembershipService service) {
        this.service = service;
    }

}
