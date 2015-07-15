package org.openhds.service.impl.census;

import org.openhds.domain.model.census.Relationship;
import org.openhds.repository.concrete.census.RelationshipRepository;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * Created by Wolfe on 7/13/2015.
 */
@Service
public class RelationshipService extends AbstractAuditableCollectedService<Relationship, RelationshipRepository> {

    @Autowired
    private IndividualService individualService;

    @Autowired
    public RelationshipService(RelationshipRepository repository) {
        super(repository);
    }

    @Override
    protected Relationship makeUnknownEntity() {
        Relationship relationship = new Relationship();
        relationship.setRelationshipType("unknown");
        relationship.setStartDate(ZonedDateTime.now());
        relationship.setIndividualA(individualService.getUnknownEntity());
        relationship.setIndividualB(individualService.getUnknownEntity());
        relationship.setCollectionDateTime(ZonedDateTime.now());
        relationship.setCollectedBy(fieldWorkerService.getUnknownEntity());
        return relationship;
    }
}
