package org.openhds.service.impl.update;

import org.openhds.domain.model.update.PregnancyObservation;
import org.openhds.service.AuditableCollectedServiceTest;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/13/15.
 */
public class PregnancyObservationServiceTest extends AuditableCollectedServiceTest<
        PregnancyObservation, PregnancyObservationService> {

    @Autowired
    FieldWorkerService fieldWorkerService;

    @Autowired
    VisitService visitService;

    @Autowired
    IndividualService individualService;

    @Autowired
    @Override
    protected void initialize(PregnancyObservationService service) {
        this.service = service;
    }

    @Override
    protected PregnancyObservation makeInvalidEntity() {
        return new PregnancyObservation();
    }

    @Override
    protected PregnancyObservation makeValidEntity(String name, String id) {
        PregnancyObservation pregnancyObservation = new PregnancyObservation();
        pregnancyObservation.setUuid(id);

        pregnancyObservation.setCollectedBy(fieldWorkerService.findAll(UUID_SORT).toList().get(0));
        pregnancyObservation.setCollectionDateTime(ZonedDateTime.now());

        pregnancyObservation.setVisit(visitService.findAll(UUID_SORT).toList().get(0));
        pregnancyObservation.setMother(individualService.findAll(UUID_SORT).toList().get(0));
        pregnancyObservation.setPregnancyDate(ZonedDateTime.now().minusMonths(5));
        pregnancyObservation.setExpectedDeliveryDate(ZonedDateTime.now().plusMonths(5));

        return pregnancyObservation;
    }
}
