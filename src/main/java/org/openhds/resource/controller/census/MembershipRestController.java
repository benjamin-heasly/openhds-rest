package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.Membership;
import org.openhds.resource.contract.AuditableCollectedRestController;
import org.openhds.resource.registration.census.MembershipRegistration;
import org.openhds.service.contract.AbstractUuidService;
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
    protected MembershipRegistration makeSampleRegistration(Membership entity) {
        MembershipRegistration registration = new MembershipRegistration();
        registration.setMembership(entity);
        registration.setIndividualUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setSocialGroupUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setCollectedByUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        return registration;
    }

    @Override
    protected Membership register(MembershipRegistration registration) {
        return membershipService.recordMembership(registration.getMembership(),
                registration.getIndividualUuid(),
                registration.getSocialGroupUuid(),
                registration.getCollectedByUuid());
    }

    @Override
    protected Membership register(MembershipRegistration registration, String id) {
        registration.getMembership().setUuid(id);
        return register(registration);
    }
}
