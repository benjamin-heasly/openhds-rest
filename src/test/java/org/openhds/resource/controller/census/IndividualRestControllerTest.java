package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.Individual;
import org.openhds.resource.contract.AuditableExtIdRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.census.IndividualRegistration;
import org.openhds.service.impl.census.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Wolfe on 7/13/2015.
 */
public class IndividualRestControllerTest extends AuditableExtIdRestControllerTest<Individual, IndividualService, IndividualRestController> {

    @Override
    @Autowired
    protected void initialize(IndividualService service, IndividualRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected Individual makeValidEntity(String name, String id) {
        Individual individual = new Individual();
        individual.setUuid(id);
        individual.setExtId(name);
        individual.setFirstName(name);
        individual.setDateOfBirth(ZonedDateTime.now().minusYears(1));
        individual.setCollectionDateTime(ZonedDateTime.now());

        return individual;
    }

    @Override
    protected Individual makeInvalidEntity() {
        return new Individual();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(Individual entity, String name, String id) {
        assertNotNull(entity);

        Individual savedIndividual= service.findOne(id);
        assertNotNull(savedIndividual);

        assertEquals(id, savedIndividual.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getFirstName(), savedIndividual.getFirstName());
    }

    @Override
    protected Registration<Individual> makeRegistration(Individual entity) {
        IndividualRegistration registration = new IndividualRegistration();
        registration.setIndividual(entity);
        registration.setCollectedByUuid(fieldWorkerService.findAll(UUID_SORT).toList().get(0).getUuid());
        return registration;
    }
}
