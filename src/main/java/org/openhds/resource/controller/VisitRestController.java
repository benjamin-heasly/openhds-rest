package org.openhds.resource.controller;

import org.openhds.domain.model.Visit;
import org.openhds.resource.contract.AuditableExtIdRestController;
import org.openhds.resource.registration.VisitRegistration;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.LocationService;
import org.openhds.service.impl.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/visits")
@ExposesResourceFor(Visit.class)
class VisitRestController extends AuditableExtIdRestController<
        Visit,
        VisitRegistration,
        VisitService> {

    private final VisitService visitService;

    private final LocationService locationService;

    private final FieldWorkerService fieldWorkerService;

    @Autowired
    public VisitRestController(VisitService visitService,
                               LocationService locationService,
                               FieldWorkerService fieldWorkerService) {
        super(visitService);
        this.visitService = visitService;
        this.locationService = locationService;
        this.fieldWorkerService = fieldWorkerService;
    }

    @Override
    protected Visit register(VisitRegistration registration) {
        Visit visit = registration.getVisit();
        visit.setCollectedBy(fieldWorkerService.findOne(registration.getCollectedByUuid()));
        visit.setLocation(locationService.findOne(registration.getLocationUuid()));
        return visitService.createOrUpdate(visit);
    }

    @Override
    protected Visit register(VisitRegistration registration, String id) {
        registration.getVisit().setUuid(id);
        return register(registration);
    }
}
