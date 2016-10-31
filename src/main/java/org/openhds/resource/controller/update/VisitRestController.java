package org.openhds.resource.controller.update;

import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.update.Visit;
import org.openhds.repository.results.EntityIterator;
import org.openhds.resource.contract.AuditableExtIdRestController;
import org.openhds.resource.registration.update.VisitRegistration;
import org.openhds.service.contract.AbstractUuidService;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.LocationService;
import org.openhds.service.impl.update.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
    protected VisitRegistration makeSampleRegistration(Visit entity) {
        VisitRegistration registration = new VisitRegistration();
        registration.setVisit(entity);
        registration.setLocationUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        return registration;
    }

    @Override
    protected Visit register(VisitRegistration registration) {
        checkRegistrationFields(registration.getVisit(), registration);
        return visitService.recordVisit(registration.getVisit(),
                registration.getLocationUuid(),
                registration.getCollectedByUuid());
    }

    @Override
    protected Visit register(VisitRegistration registration, String id) {
        registration.getVisit().setUuid(id);
        return register(registration);
    }

    @RequestMapping(value = "/findByFieldWorker", method = RequestMethod.GET)
    public List<Visit> findByFieldWorker(@RequestParam String fieldWorkerId) {
        EntityIterator<FieldWorker> fieldWorkers = fieldWorkerService.findByFieldWorkerId(new Sort("fieldWorkerId"), fieldWorkerId);

        // This is hacky because we get back an entity iterator and it's not readily streamable
        List <Visit> results = new ArrayList<>();

        for(FieldWorker fw: fieldWorkers) {
            EntityIterator<Visit> visits = visitService.findByCollectedBy(new Sort("uuid"), fw);
            for(Visit visit: visits) {
                results.add(visit);
            }
        }
        return results;
    }

}
