package org.openhds.resource.controller;

import org.openhds.domain.model.Membership;
import org.openhds.repository.concrete.FieldWorkerRepository;
import org.openhds.repository.concrete.IndividualRepository;
import org.openhds.repository.concrete.LocationHierarchyRepository;
import org.openhds.repository.concrete.SocialGroupRepository;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.MembershipRegistration;
import org.openhds.resource.registration.Registration;
import org.openhds.service.impl.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by Ben on 5/26/15.
 */
public class MembershipRestControllerTest extends AuditableCollectedRestControllerTest<
        Membership, MembershipService, MembershipRestController> {

    @Autowired
    private IndividualRepository individualRepository;

    @Autowired
    private SocialGroupRepository socialGroupRepository;

    @Autowired
    private FieldWorkerRepository fieldWorkerRepository;

    @Autowired
    private LocationHierarchyRepository locationHierarchyRepository;

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

        membership.setSocialGroup(socialGroupRepository.findAll().get(0));
        membership.setIndividual(individualRepository.findAll().get(0));
        membership.setCollectedBy(fieldWorkerRepository.findAll().get(0));
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
        assertEquals(entity.getbIsToA(), savedMembership.getbIsToA());

    }

    @Override
    protected Registration<Membership> makeRegistration(Membership entity) {
        MembershipRegistration registration = new MembershipRegistration();
        registration.setMembership(entity);

        registration.setIndividualUuid(individualRepository.findAll().get(0).getUuid());
        registration.setSocialGroupUuid(socialGroupRepository.findAll().get(0).getUuid());
        registration.setCollectedByUuid(fieldWorkerRepository.findAll().get(0).getUuid());

        return registration;
    }
}
