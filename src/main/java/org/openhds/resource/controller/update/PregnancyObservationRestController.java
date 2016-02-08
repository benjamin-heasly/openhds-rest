package org.openhds.resource.controller.update;

import org.openhds.domain.model.update.PregnancyObservation;
import org.openhds.resource.contract.AuditableCollectedRestController;
import org.openhds.resource.registration.update.PregnancyObservationRegistration;
import org.openhds.service.contract.AbstractUuidService;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.update.PregnancyObservationService;
import org.openhds.service.impl.update.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/pregnancyObservations")
@ExposesResourceFor(PregnancyObservation.class)
public class PregnancyObservationRestController extends AuditableCollectedRestController<
        PregnancyObservation,
        PregnancyObservationRegistration,
        PregnancyObservationService> {

    private final PregnancyObservationService pregnancyObservationService;

    private final VisitService visitService;

    private final IndividualService individualService;

    private final FieldWorkerService fieldWorkerService;

    @Autowired
    public PregnancyObservationRestController(PregnancyObservationService pregnancyObservationService,
                                              VisitService visitService,
                                              IndividualService individualService,
                                              FieldWorkerService fieldWorkerService) {
        super(pregnancyObservationService);
        this.pregnancyObservationService = pregnancyObservationService;
        this.visitService = visitService;
        this.individualService = individualService;
        this.fieldWorkerService = fieldWorkerService;
    }

    @Override
    protected PregnancyObservationRegistration makeSampleRegistration(PregnancyObservation entity) {
        PregnancyObservationRegistration registration = new PregnancyObservationRegistration();
        registration.setPregnancyObservation(entity);
        registration.setVisitUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setMotherUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        return registration;
    }

    @Override
    protected PregnancyObservation register(PregnancyObservationRegistration registration) {
        checkRegistrationFields(registration.getPregnancyObservation(), registration);
        return pregnancyObservationService.recordPregnancyObservation(registration.getPregnancyObservation(),
                registration.getMotherUuid(),
                registration.getVisitUuid(),
                registration.getCollectedByUuid());
    }

    @Override
    protected PregnancyObservation register(PregnancyObservationRegistration registration, String id) {
        registration.getPregnancyObservation().setUuid(id);
        return register(registration);
    }
}
