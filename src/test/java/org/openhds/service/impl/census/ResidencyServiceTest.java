package org.openhds.service.impl.census;

import org.openhds.domain.model.census.Residency;
import org.openhds.service.AuditableCollectedServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

/**
 * Created by Wolfe on 7/14/2015.
 */
public class ResidencyServiceTest extends AuditableCollectedServiceTest<Residency, ResidencyService> {


    @Autowired
    private IndividualService individualService;

    @Autowired
    private LocationService locationService;

    @Override
    protected Residency makeInvalidEntity() {
        return new Residency();
    }

    @Override
    protected Residency makeValidEntity(String name, String id) {
        Residency residency = new Residency();
        residency.setUuid(id);
        residency.setIndividual(individualService.findAll(UUID_SORT).toList().get(0));
        residency.setLocation(locationService.findAll(UUID_SORT).toList().get(0));
        residency.setStartDate(ZonedDateTime.now().minusYears(1));
        residency.setStartType(name);

        initCollectedFields(residency);

        return residency;
    }

    @Override
    @Autowired
    protected void initialize(ResidencyService service) {
        this.service = service;
    }
}
