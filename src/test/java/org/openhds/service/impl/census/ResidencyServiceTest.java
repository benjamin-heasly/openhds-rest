package org.openhds.service.impl.census;

import org.openhds.domain.model.census.Residency;
import org.openhds.service.AuditableCollectedServiceTest;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.LocationService;
import org.openhds.service.impl.census.ResidencyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

/**
 * Created by Wolfe on 7/14/2015.
 */
public class ResidencyServiceTest extends AuditableCollectedServiceTest<Residency, ResidencyService> {

    @Autowired
    private FieldWorkerService fieldWorkerService;

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

        residency.setCollectedBy(fieldWorkerService.findAll(UUID_SORT).toList().get(0));
        residency.setCollectionDateTime(ZonedDateTime.now());

        residency.setIndividual(individualService.findAll(UUID_SORT).toList().get(0));
        residency.setLocation(locationService.findAll(UUID_SORT).toList().get(0));
        residency.setStartDate(ZonedDateTime.now().minusYears(1));
        residency.setStartType(name);

        return residency;
    }

    @Override
    @Autowired
    protected void initialize(ResidencyService service) {
        this.service = service;
    }
}
