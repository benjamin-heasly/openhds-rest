package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.Residency;
import org.openhds.resource.contract.AuditableCollectedRestController;
import org.openhds.resource.registration.ResidencyRegistration;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.LocationService;
import org.openhds.service.impl.census.ResidencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Wolfe on 7/14/2015.
 */
@RestController
@RequestMapping("/residencies")
@ExposesResourceFor(Residency.class)
public class ResidencyRestController extends AuditableCollectedRestController<
        Residency,
        ResidencyRegistration,
        ResidencyService> {

    private final ResidencyService residencyService;

    private final IndividualService individualService;

    private final LocationService locationService;

    private final FieldWorkerService fieldWorkerService;

    @Autowired
    public ResidencyRestController(ResidencyService residencyService,
                                   IndividualService individualService,
                                   LocationService locationService,
                                   FieldWorkerService fieldWorkerService) {
        super(residencyService);
        this.residencyService = residencyService;
        this.individualService = individualService;
        this.locationService = locationService;
        this.fieldWorkerService = fieldWorkerService;
    }

    @Override
    protected Residency register(ResidencyRegistration registration) {
        Residency residency = registration.getResidency();
        residency.setIndividual(individualService.findOne(registration.getIndividualUuid()));
        residency.setLocation(locationService.findOne(registration.getLocationUuid()));
        residency.setCollectedBy(fieldWorkerService.findOne(registration.getCollectedByUuid()));
        return residencyService.createOrUpdate(residency);
    }

    @Override
    protected Residency register(ResidencyRegistration registration, String id) {
        registration.getResidency().setUuid(id);
        return register(registration);
    }
}
