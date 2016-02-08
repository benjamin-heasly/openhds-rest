package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.Relationship;
import org.openhds.resource.contract.AuditableCollectedRestController;
import org.openhds.resource.registration.census.RelationshipRegistration;
import org.openhds.service.contract.AbstractUuidService;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Wolfe on 7/13/2015.
 */
@RestController
@RequestMapping("/relationships")
@ExposesResourceFor(Relationship.class)
public class RelationshipRestController extends AuditableCollectedRestController<
        Relationship,
        RelationshipRegistration,
        RelationshipService> {

    private final RelationshipService relationshipService;

    private final IndividualService individualService;

    private final FieldWorkerService fieldWorkerService;


    @Autowired
    public RelationshipRestController(RelationshipService relationshipService,
                                      IndividualService individualService,
                                      FieldWorkerService fieldWorkerService) {
        super(relationshipService);
        this.relationshipService = relationshipService;
        this.individualService = individualService;
        this.fieldWorkerService = fieldWorkerService;
    }

    @Override
    protected RelationshipRegistration makeSampleRegistration(Relationship entity) {
        RelationshipRegistration registration = new RelationshipRegistration();
        registration.setRelationship(entity);
        registration.setIndividualAUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setIndividualBUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setCollectedByUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        return registration;

    }

    @Override
    protected Relationship register(RelationshipRegistration registration) {
        checkRegistrationFields(registration.getRelationship(), registration);
        return relationshipService.recordRelationship(registration.getRelationship(),
                registration.getIndividualAUuid(),
                registration.getIndividualBUuid(),
                registration.getCollectedByUuid());
    }

    @Override
    protected Relationship register(RelationshipRegistration registration, String id) {
        registration.getRelationship().setUuid(id);
        return register(registration);
    }
}
