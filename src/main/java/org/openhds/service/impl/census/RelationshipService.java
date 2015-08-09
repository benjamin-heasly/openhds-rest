package org.openhds.service.impl.census;

import org.openhds.domain.model.census.Relationship;
import org.openhds.errors.model.ErrorLog;
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
    public Relationship makePlaceHolder(String id, String name) {
        Relationship relationship = new Relationship();
        relationship.setUuid(id);
        relationship.setRelationshipType(name);
        relationship.setStartDate(ZonedDateTime.now());
        relationship.setIndividualA(individualService.getUnknownEntity());
        relationship.setIndividualB(individualService.getUnknownEntity());

        initPlaceHolderCollectedFields(relationship);

        return relationship;
    }

    public Relationship recordRelationship(Relationship relationship, String individualAId, String individualBId, String fieldWorkerId){
        relationship.setIndividualA(individualService.findOrMakePlaceHolder(individualAId));
        relationship.setIndividualB(individualService.findOrMakePlaceHolder(individualBId));
        relationship.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        return createOrUpdate(relationship);
    }

    @Override
    public void validate(Relationship entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);
    }
}
