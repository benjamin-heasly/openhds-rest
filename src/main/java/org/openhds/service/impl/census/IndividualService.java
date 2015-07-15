package org.openhds.service.impl.census;

import org.openhds.domain.model.census.Individual;
import org.openhds.repository.concrete.census.IndividualRepository;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * Created by Wolfe on 7/13/2015.
 */
@Service
public class IndividualService extends AbstractAuditableExtIdService<Individual, IndividualRepository>{

    @Autowired
    public IndividualService(IndividualRepository repository) {
        super(repository);
    }

    @Override
    protected Individual makeUnknownEntity() {
        Individual individual = new Individual();
        individual.setFirstName("unknown");
        individual.setExtId("unknown");
        individual.setCollectedBy(fieldWorkerService.getUnknownEntity());
        individual.setCollectionDateTime(ZonedDateTime.now());
        return individual;
    }
}
