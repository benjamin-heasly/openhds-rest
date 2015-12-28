package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.Residency;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.census.ResidencyRegistration;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.LocationService;
import org.openhds.service.impl.census.ResidencyService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Wolfe on 7/14/2015.
 */
public class ResidencyRestControllerTest extends AuditableCollectedRestControllerTest<Residency, ResidencyService, ResidencyRestController> {

    @Autowired
    private IndividualService individualService;

    @Autowired
    private LocationService locationService;


    @Override
    @Autowired
    protected void initialize(ResidencyService service, ResidencyRestController controller) {
        this.service = service;
        this.controller = controller;
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

}
