package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.Relationship;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.census.RelationshipRegistration;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;

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
    private IndividualService individualService;

    @Override
    @Autowired
    protected void initialize(RelationshipService service, RelationshipRestController controller) {
        this.service = service;
        this.controller = controller;
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
        registration.setIndividualAUuid(individualService.findAll(UUID_SORT).toList().get(0).getUuid());
        registration.setIndividualBUuid(individualService.findAll(UUID_SORT).toList().get(0).getUuid());
        registration.setCollectedByUuid(fieldWorkerService.findAll(UUID_SORT).toList().get(0).getUuid());
        return registration;
    }
}
