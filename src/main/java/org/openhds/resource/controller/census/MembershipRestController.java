package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.Membership;
import org.openhds.resource.contract.AuditableCollectedRestController;
import org.openhds.resource.registration.MembershipRegistration;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.MembershipService;
import org.openhds.service.impl.census.SocialGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/memberships")
@ExposesResourceFor(Membership.class)
class MembershipRestController extends AuditableCollectedRestController<
        Membership,
        MembershipRegistration,
        MembershipService> {

    private final MembershipService membershipService;

    private final IndividualService individualService;

    private final SocialGroupService socialGroupService;

    private final FieldWorkerService fieldWorkerService;

    @Autowired
    public MembershipRestController(MembershipService membershipService,
                                    IndividualService individualService,
                                    SocialGroupService socialGroupService,
                                    FieldWorkerService fieldWorkerService) {
        super(membershipService);
        this.membershipService = membershipService;
        this.individualService = individualService;
        this.socialGroupService = socialGroupService;
        this.fieldWorkerService = fieldWorkerService;
    }

    @Override
    protected Membership register(MembershipRegistration registration) {
        Membership membership = registration.getMembership();
        membership.setIndividual(individualService.findOne(registration.getIndividualUuid()));
        membership.setSocialGroup(socialGroupService.findOne(registration.getSocialGroupUuid()));
        membership.setCollectedBy(fieldWorkerService.findOne(registration.getCollectedByUuid()));
        return membershipService.createOrUpdate(membership);
    }

    @Override
    protected Membership register(MembershipRegistration registration, String id) {
        registration.getMembership().setUuid(id);
        return register(registration);
    }
}
