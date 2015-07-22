package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.Membership;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.census.MembershipRegistration;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.MembershipService;
import org.openhds.service.impl.census.SocialGroupService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Ben on 5/26/15.
 */
public class MembershipRestControllerTest extends AuditableCollectedRestControllerTest<
        Membership,
        MembershipService,
        MembershipRestController> {

    @Autowired
    private IndividualService individualService;

    @Autowired
    private SocialGroupService socialGroupService;


    @Autowired
    @Override
    protected void initialize(MembershipService service, MembershipRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected Membership makeValidEntity(String name, String id) {
        Membership membership = new Membership();
        membership.setUuid(id);

        membership.setSocialGroup(socialGroupService.findAll(UUID_SORT).toList().get(0));
        membership.setIndividual(individualService.findAll(UUID_SORT).toList().get(0));
        membership.setCollectedBy(fieldWorkerService.findAll(UUID_SORT).toList().get(0));

        membership.setRelationshipToGroupHead(name);
        membership.setStartType(name);
        membership.setStartDate(ZonedDateTime.now().minusYears(1));
        membership.setCollectionDateTime(ZonedDateTime.now());

        return membership;
    }

    @Override
    protected Membership makeInvalidEntity() {
        return new Membership();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(Membership entity, String name, String id) {
        assertNotNull(entity);

        Membership savedMembership = service.findOne(id);
        assertNotNull(savedMembership);

        assertEquals(id, savedMembership.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getRelationshipToGroupHead(), savedMembership.getRelationshipToGroupHead());

    }

    @Override
    protected Registration<Membership> makeRegistration(Membership entity) {
        MembershipRegistration registration = new MembershipRegistration();
        registration.setMembership(entity);

        registration.setIndividualUuid(individualService.findAll(UUID_SORT).toList().get(0).getUuid());
        registration.setSocialGroupUuid(socialGroupService.findAll(UUID_SORT).toList().get(0).getUuid());
        registration.setCollectedByUuid(fieldWorkerService.findAll(UUID_SORT).toList().get(0).getUuid());

        return registration;
    }
}
