package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.Membership;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.census.MembershipRegistration;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.MembershipService;
import org.openhds.service.impl.census.SocialGroupService;
import org.springframework.beans.factory.annotation.Autowired;

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
    }

}
