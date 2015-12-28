package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.Residency;
import org.openhds.resource.contract.AuditableCollectedRestController;
import org.openhds.resource.registration.census.ResidencyRegistration;
import org.openhds.service.contract.AbstractUuidService;
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
    protected ResidencyRegistration makeSampleRegistration(Residency entity) {
        ResidencyRegistration registration = new ResidencyRegistration();
        registration.setResidency(entity);
        registration.setIndividualUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setLocationUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setCollectedByUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        return registration;

    }

    @Override
    protected Residency register(ResidencyRegistration registration) {
        return residencyService.recordResidency(registration.getResidency(),
                registration.getIndividualUuid(),
                registration.getLocationUuid(),
                registration.getCollectedByUuid());
    }

    @Override
    protected Residency register(ResidencyRegistration registration, String id) {
        registration.getResidency().setUuid(id);
        return register(registration);
    }
}
