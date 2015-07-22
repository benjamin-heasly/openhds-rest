package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.SocialGroup;
import org.openhds.resource.contract.AuditableExtIdRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.census.SocialGroupRegistration;
import org.openhds.service.impl.census.SocialGroupService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Ben on 5/26/15.
 */
public class SocialGroupRestControllerTest extends AuditableExtIdRestControllerTest
        <SocialGroup, SocialGroupService, SocialGroupRestController> {

    @Autowired
    @Override
    protected void initialize(SocialGroupService service, SocialGroupRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected SocialGroup makeInvalidEntity() {
        return new SocialGroup();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(SocialGroup entity, String name, String id) {
        assertNotNull(entity);

        SocialGroup savedSocialGroup = service.findOne(id);
        assertNotNull(savedSocialGroup);

        assertEquals(id, savedSocialGroup.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getGroupName(), savedSocialGroup.getGroupName());
    }

    @Override
    protected Registration<SocialGroup> makeRegistration(SocialGroup entity) {
        SocialGroupRegistration registration = new SocialGroupRegistration();
        registration.setSocialGroup(entity);
        registration.setCollectedByUuid(fieldWorkerService.findAll(UUID_SORT).toList().get(0).getUuid());
        return registration;
    }
}
