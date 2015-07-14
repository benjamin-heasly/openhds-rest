package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.Death;
import org.openhds.repository.concrete.FieldWorkerRepository;
import org.openhds.repository.concrete.census.IndividualRepository;
import org.openhds.repository.concrete.update.VisitRepository;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.update.DeathRegistration;
import org.openhds.resource.registration.Registration;
import org.openhds.service.impl.update.DeathService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Wolfe on 7/14/2015.
 */
public class DeathRestControllerTest extends AuditableCollectedRestControllerTest<Death, DeathService, DeathRestController> {

    @Autowired
    private IndividualRepository individualRepository;

    @Autowired
    private FieldWorkerRepository fieldWorkerRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Override
    @Autowired
    protected void initialize(DeathService service, DeathRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected Death makeValidEntity(String name, String id) {
        Death death = new Death();
        death.setUuid(id);
        death.setDeathDate(ZonedDateTime.now().minusYears(1));
        death.setDeathCause(name);
        death.setDeathPlace(name);

        death.setCollectedBy(fieldWorkerRepository.findAll().get(0));
        death.setCollectionDateTime(ZonedDateTime.now());

        death.setIndividual(individualRepository.findAll().get(0));
        death.setVisit(visitRepository.findAll().get(0));

        return death;
    }

    @Override
    protected Death makeInvalidEntity() {
        return new Death();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(Death entity, String name, String id) {
        assertNotNull(entity);

        Death savedDeath = service.findOne(id);
        assertNotNull(savedDeath);

        assertEquals(id, savedDeath.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getDeathDate(), entity.getDeathDate());
    }

    @Override
    protected Registration<Death> makeRegistration(Death entity) {
        DeathRegistration registration = new DeathRegistration();
        registration.setDeath(entity);
        registration.setCollectedByUuid(fieldWorkerRepository.findAll().get(0).getUuid());
        registration.setIndividualUuid(individualRepository.findAll().get(0).getUuid());
        registration.setVisitUuid(visitRepository.findAll().get(0).getUuid());
        return registration;
    }
}
