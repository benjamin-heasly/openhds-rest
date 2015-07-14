package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.Death;
import org.openhds.resource.contract.AuditableCollectedRestController;
import org.openhds.resource.registration.DeathRegistration;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.update.DeathService;
import org.openhds.service.impl.update.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Wolfe on 7/14/2015.
 */
@RestController
@RequestMapping("/deaths")
@ExposesResourceFor(Death.class)
public class DeathRestController extends AuditableCollectedRestController<
        Death,
        DeathRegistration,
        DeathService> {

    private final FieldWorkerService fieldWorkerService;

    private final VisitService visitService;

    private final IndividualService individualService;

    private final DeathService deathService;


    @Autowired
    public DeathRestController(DeathService deathService,
                               FieldWorkerService fieldWorkerService,
                               VisitService visitService,
                               IndividualService individualService) {
        super(deathService);
        this.deathService = deathService;
        this.fieldWorkerService = fieldWorkerService;
        this.visitService = visitService;
        this.individualService = individualService;
    }

    @Override
    protected Death register(DeathRegistration registration) {

        Death death = registration.getDeath();
        death.setCollectedBy(fieldWorkerService.findOne(registration.getCollectedByUuid()));
        death.setIndividual(individualService.findOne(registration.getIndividualUuid()));
        death.setVisit(visitService.findOne(registration.getVisitUuid()));
        return deathService.createOrUpdate(death);
    }

    @Override
    protected Death register(DeathRegistration registration, String id) {
        registration.getDeath().setUuid(id);
        return register(registration);
    }
}
