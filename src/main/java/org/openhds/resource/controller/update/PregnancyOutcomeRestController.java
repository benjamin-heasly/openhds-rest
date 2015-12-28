package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.PregnancyOutcome;
import org.openhds.resource.contract.AuditableCollectedRestController;
import org.openhds.resource.registration.update.PregnancyOutcomeRegistration;
import org.openhds.service.contract.AbstractUuidService;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.update.PregnancyOutcomeService;
import org.openhds.service.impl.update.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Wolfe on 7/15/2015.
 */
@RestController
@RequestMapping("/pregnancyOutcomes")
@ExposesResourceFor(PregnancyOutcome.class)
public class PregnancyOutcomeRestController extends AuditableCollectedRestController<
        PregnancyOutcome,
        PregnancyOutcomeRegistration,
        PregnancyOutcomeService> {


    private final IndividualService individualService;

    private final FieldWorkerService fieldWorkerService;

    private final VisitService visitService;

    @Autowired
    public PregnancyOutcomeRestController(PregnancyOutcomeService service, IndividualService individualService, FieldWorkerService fieldWorkerService, VisitService visitService) {
        super(service);
        this.individualService = individualService;
        this.fieldWorkerService = fieldWorkerService;
        this.visitService = visitService;
    }

    @Override
    protected PregnancyOutcomeRegistration makeSampleRegistration(PregnancyOutcome entity) {
        PregnancyOutcomeRegistration registration = new PregnancyOutcomeRegistration();
        registration.setPregnancyOutcome(entity);
        registration.setMotherUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setFatherUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setVisitUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        return registration;
    }

    @Override
    protected PregnancyOutcome register(PregnancyOutcomeRegistration registration) {
        return service.recordPregnancyOutcome(registration.getPregnancyOutcome(),
                registration.getMotherUuid(),
                registration.getFatherUuid(),
                registration.getVisitUuid(),
                registration.getCollectedByUuid());

    }

    @Override
    protected PregnancyOutcome register(PregnancyOutcomeRegistration registration, String id) {
        registration.getPregnancyOutcome().setUuid(id);
        return register(registration);
    }
}
