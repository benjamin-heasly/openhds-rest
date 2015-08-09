package org.openhds.service.impl.update;

import org.openhds.domain.model.update.PregnancyObservation;
import org.openhds.errors.model.ErrorLog;
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
    public PregnancyObservation makePlaceHolder(String id, String name) {
        PregnancyObservation pregnancyObservation = new PregnancyObservation();
        pregnancyObservation.setUuid(id);
        pregnancyObservation.setVisit(visitService.getUnknownEntity());
        pregnancyObservation.setMother(individualService.getUnknownEntity());
        pregnancyObservation.setPregnancyDate(ZonedDateTime.now().minusMonths(5));
        pregnancyObservation.setExpectedDeliveryDate(ZonedDateTime.now().plusMonths(5));

        initPlaceHolderCollectedFields(pregnancyObservation);

        return pregnancyObservation;
    }

    public PregnancyObservation recordPregnancyObservation(PregnancyObservation pregnancyObservation,
                                                           String motherId,
                                                           String visitId,
                                                           String fieldWorkerId) {

        pregnancyObservation.setMother(individualService.findOrMakePlaceHolder(motherId));
        pregnancyObservation.setVisit(visitService.findOrMakePlaceHolder(visitId));
        pregnancyObservation.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));

        return createOrUpdate(pregnancyObservation);
    }

    @Override
    public void validate(PregnancyObservation entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);
    }
}
