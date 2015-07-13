package org.openhds.service.impl;

import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.Individual;
import org.openhds.service.AuditableExtIdServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

/**
 * Created by Wolfe on 7/13/2015.
 */
public class IndividualServiceTest extends AuditableExtIdServiceTest<Individual, IndividualService> {

    @Autowired
    private FieldWorkerService fieldWorkerService;

    @Override
    protected Individual makeInvalidEntity() {
        return new Individual();
    }

    @Override
    protected Individual makeValidEntity(String name, String id) {
        Individual individual = new Individual();
        individual.setUuid(id);
        individual.setExtId(name);
        individual.setFirstName(name);
        individual.setDateOfBirth(ZonedDateTime.now().minusYears(1));
        individual.setCollectedBy(fieldWorkerService.findAll(UUID_SORT).toList().get(0));
        individual.setCollectionDateTime(ZonedDateTime.now());

        return individual;
    }

    @Override
    @Autowired
    protected void initialize(IndividualService service) {
        this.service = service;
    }
}
