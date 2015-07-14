package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.Individual;
import org.openhds.resource.contract.AuditableExtIdRestController;
import org.openhds.resource.registration.IndividualRegistration;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Wolfe on 7/13/2015.
 */
@RestController
@RequestMapping("/individuals")
@ExposesResourceFor(Individual.class)
public class IndividualRestController extends AuditableExtIdRestController<Individual,IndividualRegistration, IndividualService>{

    private final FieldWorkerService fieldWorkerService;

    private final IndividualService individualService;

    @Autowired
    public IndividualRestController(IndividualService individualService, FieldWorkerService fieldWorkerService) {
        super(individualService);
        this.individualService = individualService;
        this.fieldWorkerService = fieldWorkerService;
    }

    @Override
    protected Individual register(IndividualRegistration registration) {
        Individual individual = registration.getIndividual();
        individual.setCollectedBy(fieldWorkerService.findOne(registration.getCollectedByUuid()));
        return individualService.createOrUpdate(individual);
    }

    @Override
    protected Individual register(IndividualRegistration registration, String id) {
        registration.getIndividual().setUuid(id);
        return register(registration);
    }
}
