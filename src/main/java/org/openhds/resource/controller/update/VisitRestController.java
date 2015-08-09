package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.Visit;
import org.openhds.resource.contract.AuditableExtIdRestController;
import org.openhds.resource.registration.update.VisitRegistration;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.LocationService;
import org.openhds.service.impl.update.VisitService;
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
public class VisitRestController extends AuditableExtIdRestController<
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
        return visitService.recordVisit(registration.getVisit(),
                registration.getLocationUuid(),
                registration.getCollectedByUuid());
    }

    @Override
    protected Visit register(VisitRegistration registration, String id) {
        registration.getVisit().setUuid(id);
        return register(registration);
    }
}
