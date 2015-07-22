package org.openhds.service.impl.update;

import org.openhds.domain.model.update.PregnancyObservation;
import org.openhds.domain.model.update.PregnancyOutcome;
import org.openhds.repository.concrete.update.PregnancyOutcomeRepository;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.openhds.service.impl.census.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * Created by Wolfe on 7/15/2015.
 */
@Service
public class PregnancyOutcomeService extends AbstractAuditableCollectedService<PregnancyOutcome, PregnancyOutcomeRepository> {

    @Autowired
    private IndividualService individualService;

    @Autowired
    private VisitService visitService;

    @Autowired
    public PregnancyOutcomeService(PregnancyOutcomeRepository pregnancyOutcomeRepository) {
        super(pregnancyOutcomeRepository);
    }

    @Override
    public PregnancyOutcome makePlaceHolder(String id, String name) {
        PregnancyOutcome pregnancyOutcome = new PregnancyOutcome();
        pregnancyOutcome.setUuid(id);
        pregnancyOutcome.setFather(individualService.getUnknownEntity());
        pregnancyOutcome.setMother(individualService.getUnknownEntity());
        pregnancyOutcome.setVisit(visitService.getUnknownEntity());
        pregnancyOutcome.setOutcomeDate(ZonedDateTime.now().minusYears(1));

        initPlaceHolderCollectedFields(pregnancyOutcome);

        return pregnancyOutcome;
    }

    public PregnancyOutcome recordPregnancyOutcome(PregnancyOutcome pregnancyOutcome,
                                                   String motherId,
                                                   String fatherId,
                                                   String visitId,
                                                   String fieldWorkerId){

        pregnancyOutcome.setMother(individualService.findOrMakePlaceHolder(motherId));
        pregnancyOutcome.setFather(individualService.findOrMakePlaceHolder(fatherId));
        pregnancyOutcome.setVisit(visitService.findOrMakePlaceHolder(visitId));
        pregnancyOutcome.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));

        return createOrUpdate(pregnancyOutcome);
    }

}
