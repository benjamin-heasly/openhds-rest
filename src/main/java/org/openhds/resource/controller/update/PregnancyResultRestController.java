package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.PregnancyResult;
import org.openhds.resource.contract.AuditableCollectedRestController;
import org.openhds.resource.registration.update.PregnancyResultRegistration;
import org.openhds.service.contract.AbstractUuidService;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.update.PregnancyOutcomeService;
import org.openhds.service.impl.update.PregnancyResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Wolfe on 7/15/2015.
 */
@RestController
@RequestMapping("/pregnancyResults")
@ExposesResourceFor(PregnancyResult.class)
public class PregnancyResultRestController extends AuditableCollectedRestController<
        PregnancyResult,
        PregnancyResultRegistration,
        PregnancyResultService> {


    private final PregnancyOutcomeService pregnancyOutcomeService;

    private final IndividualService individualService;

    private final FieldWorkerService fieldWorkerService;

    private final PregnancyResultService pregnancyResultService;

    @Autowired
    public PregnancyResultRestController(PregnancyResultService service,
                                         PregnancyOutcomeService pregnancyOutcomeService,
                                         IndividualService individualService,
                                         FieldWorkerService fieldWorkerService) {
        super(service);
        this.pregnancyResultService = service;
        this.pregnancyOutcomeService = pregnancyOutcomeService;
        this.individualService = individualService;
        this.fieldWorkerService = fieldWorkerService;
    }

    @Override
    protected PregnancyResultRegistration makeSampleRegistration(PregnancyResult entity) {
        PregnancyResultRegistration registration = new PregnancyResultRegistration();
        registration.setPregnancyResult(entity);
        registration.setChildUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setPregnancyOutcomeUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        return registration;
    }

    @Override
    protected PregnancyResult register(PregnancyResultRegistration registration) {
        checkRegistrationFields(registration.getPregnancyResult(), registration);
        return pregnancyResultService.recordPregnancyResult(registration.getPregnancyResult(),
                registration.getPregnancyOutcomeUuid(),
                registration.getChildUuid(),
                registration.getCollectedByUuid());
    }

    @Override
    protected PregnancyResult register(PregnancyResultRegistration registration, String id) {
        registration.getPregnancyResult().setUuid(id);
        return register(registration);
    }
}
