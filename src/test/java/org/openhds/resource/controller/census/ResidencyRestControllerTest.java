package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.Residency;
import org.openhds.repository.concrete.FieldWorkerRepository;
import org.openhds.repository.concrete.census.IndividualRepository;
import org.openhds.repository.concrete.census.LocationRepository;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.ResidencyRegistration;
import org.openhds.service.impl.census.ResidencyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Wolfe on 7/14/2015.
 */
public class ResidencyRestControllerTest extends AuditableCollectedRestControllerTest<Residency, ResidencyService, ResidencyRestController> {

    @Autowired
    private IndividualRepository individualRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private FieldWorkerRepository fieldWorkerRepository;


    @Override
    @Autowired
    protected void initialize(ResidencyService service, ResidencyRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected Residency makeValidEntity(String name, String id) {
        Residency residency = new Residency();
        residency.setUuid(id);

        residency.setCollectedBy(fieldWorkerRepository.findAll().get(0));
        residency.setCollectionDateTime(ZonedDateTime.now());

        residency.setIndividual(individualRepository.findAll().get(0));
        residency.setLocation(locationRepository.findAll().get(0));
        residency.setStartDate(ZonedDateTime.now().minusYears(1));
        residency.setStartType(name);

        return residency;
    }

    @Override
    protected Residency makeInvalidEntity() {
        return new Residency();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(Residency entity, String name, String id) {

        assertNotNull(entity);

        Residency savedResidency = service.findOne(id);
        assertNotNull(savedResidency);

        assertEquals(id, savedResidency.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getStartType(), savedResidency.getStartType());

    }

    @Override
    protected Registration<Residency> makeRegistration(Residency entity) {
        ResidencyRegistration registration = new ResidencyRegistration();
        registration.setResidency(entity);
        registration.setIndividualUuid(individualRepository.findAll().get(0).getUuid());
        registration.setLocationUuid(locationRepository.findAll().get(0).getUuid());
        registration.setCollectedByUuid(fieldWorkerRepository.findAll().get(0).getUuid());
        return registration;
    }
}
