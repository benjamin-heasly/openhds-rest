package org.openhds.resource.controller;

import org.openhds.domain.model.Relationship;
import org.openhds.resource.contract.AuditableCollectedRestController;
import org.openhds.resource.registration.RelationshipRegistration;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.IndividualService;
import org.openhds.service.impl.RelationshipService;
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

    private RelationshipService relationshipService;

    private IndividualService individualService;

    private FieldWorkerService fieldWorkerService;


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
    protected Relationship register(RelationshipRegistration registration) {
        Relationship relationship = registration.getRelationship();
        relationship.setIndividualA(individualService.findOne(registration.getIndividualAUuid()));
        relationship.setIndividualB(individualService.findOne(registration.getIndividualBUuid()));
        relationship.setCollectedBy(fieldWorkerService.findOne(registration.getCollectedByUuid()));
        return relationshipService.createOrUpdate(relationship);
    }

    @Override
    protected Relationship register(RelationshipRegistration registration, String id) {
        registration.getRelationship().setUuid(id);
        return register(registration);
    }
}
