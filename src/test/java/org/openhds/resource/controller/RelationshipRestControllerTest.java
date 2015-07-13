package org.openhds.resource.controller;

import org.openhds.domain.model.Relationship;
import org.openhds.repository.concrete.FieldWorkerRepository;
import org.openhds.repository.concrete.IndividualRepository;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.RelationshipRegistration;
import org.openhds.service.impl.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Wolfe on 7/13/2015.
 */
public class RelationshipRestControllerTest extends AuditableCollectedRestControllerTest<
        Relationship,
        RelationshipService,
        RelationshipRestController> {

    @Autowired
    private FieldWorkerRepository fieldWorkerRepository;

    @Autowired
    private IndividualRepository individualRepository;

    @Override
    @Autowired
    protected void initialize(RelationshipService service, RelationshipRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected Relationship makeValidEntity(String name, String id) {
        Relationship relationship = new Relationship();
        relationship.setUuid(id);
        relationship.setRelationshipType(name);
        relationship.setIndividualB(individualRepository.findAll().get(0));
        relationship.setIndividualA(individualRepository.findAll().get(0));
        relationship.setStartDate(ZonedDateTime.now().minusYears(1));
        relationship.setCollectionDateTime(ZonedDateTime.now());
        return relationship;
    }

    @Override
    protected Relationship makeInvalidEntity() {
        return new Relationship();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(Relationship entity, String name, String id) {
        assertNotNull(entity);

        Relationship savedRelationship = service.findOne(id);
        assertNotNull(savedRelationship);

        assertEquals(id, savedRelationship.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getRelationshipType(), savedRelationship.getRelationshipType());
    }

    @Override
    protected Registration<Relationship> makeRegistration(Relationship entity) {
        RelationshipRegistration registration = new RelationshipRegistration();
        registration.setRelationship(entity);
        registration.setIndividualAId(individualRepository.findAll().get(0).getUuid());
        registration.setIndividualBId(individualRepository.findAll().get(0).getUuid());
        registration.setCollectedByUuid(fieldWorkerRepository.findAll().get(0).getUuid());
        return registration;
    }
}
