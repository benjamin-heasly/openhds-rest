package org.openhds.resource.controller;

import org.openhds.domain.model.SocialGroup;
import org.openhds.resource.contract.AuditableExtIdRestController;
import org.openhds.resource.registration.SocialGroupRegistration;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.SocialGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/socialGroups")
@ExposesResourceFor(SocialGroup.class)
class SocialGroupRestController extends AuditableExtIdRestController<
        SocialGroup,
        SocialGroupRegistration,
        SocialGroupService> {

    private final SocialGroupService socialGroupService;

    private final FieldWorkerService fieldWorkerService;

    @Autowired
    public SocialGroupRestController(SocialGroupService socialGroupService,
                                     FieldWorkerService fieldWorkerService) {
        super(socialGroupService);
        this.socialGroupService = socialGroupService;
        this.fieldWorkerService = fieldWorkerService;
    }

    @Override
    protected SocialGroup register(SocialGroupRegistration registration) {
        SocialGroup socialGroup = registration.getSocialGroup();
        socialGroup.setCollectedBy(fieldWorkerService.findOne(registration.getCollectedByUuid()));
        return socialGroupService.createOrUpdate(socialGroup);
    }

    @Override
    protected SocialGroup register(SocialGroupRegistration registration, String id) {
        registration.getSocialGroup().setUuid(id);
        return register(registration);
    }
}
