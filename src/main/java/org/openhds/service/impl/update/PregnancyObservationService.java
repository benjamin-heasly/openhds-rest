package org.openhds.service.impl.update;

import org.openhds.domain.model.update.PregnancyObservation;
import org.openhds.repository.concrete.update.PregnancyObservationRepository;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.openhds.service.impl.census.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/13/15.
 */
@Service
public class PregnancyObservationService extends AbstractAuditableCollectedService<
        PregnancyObservation, PregnancyObservationRepository> {

    @Autowired
    VisitService visitService;

    @Autowired
    IndividualService individualService;

    @Autowired
    public PregnancyObservationService(PregnancyObservationRepository repository) {
        super(repository);
    }

    @Override
    protected PregnancyObservation makeUnknownEntity() {
        PregnancyObservation pregnancyObservation = new PregnancyObservation();
        pregnancyObservation.setCollectedBy(fieldWorkerService.getUnknownEntity());
        pregnancyObservation.setCollectionDateTime(ZonedDateTime.now());

        pregnancyObservation.setVisit(visitService.getUnknownEntity());
        pregnancyObservation.setMother(individualService.getUnknownEntity());
        pregnancyObservation.setPregnancyDate(ZonedDateTime.now().minusMonths(5));
        pregnancyObservation.setExpectedDeliveryDate(ZonedDateTime.now().plusMonths(5));

        return pregnancyObservation;
    }
}
