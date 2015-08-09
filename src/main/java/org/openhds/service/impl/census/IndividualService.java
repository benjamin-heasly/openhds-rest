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
    public Individual makePlaceHolder(String id, String name) {
        Individual individual = new Individual();
        individual.setUuid(id);
        individual.setFirstName(name);
        individual.setExtId(name);

        initPlaceHolderCollectedFields(individual);

        return individual;
    }

    public Individual recordIndividual(Individual individual,
                                       String socialGroupUuid,
                                       String locationUuid,
                                       String residencyUuid,
                                       String membershipUuid,
                                       String relationshipUuid,
                                       String fieldWorkerId) {
        individual.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        return createOrUpdate(individual);
    }
}
