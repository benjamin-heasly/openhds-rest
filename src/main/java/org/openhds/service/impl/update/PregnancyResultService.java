package org.openhds.service.impl.update;

import org.openhds.domain.model.update.PregnancyResult;
import org.openhds.repository.concrete.update.PregnancyResultRepository;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

/**
 * Created by Wolfe on 7/15/2015.
 */
public class PregnancyResultService extends AbstractAuditableCollectedService<PregnancyResult, PregnancyResultRepository>{

    @Autowired
    private IndividualService individualService;

    @Autowired
    private PregnancyOutcomeService pregnancyOutcomeService;

    @Autowired
    private FieldWorkerService fieldWorkerService;

    @Autowired
    public PregnancyResultService(PregnancyResultRepository repository) {
        super(repository);
    }

    @Override
    protected PregnancyResult makeUnknownEntity() {
        PregnancyResult pregnancyResult = new PregnancyResult();
        pregnancyResult.setPregnancyOutcome(pregnancyOutcomeService.getUnknownEntity());
        pregnancyResult.setType("unknown");
        pregnancyResult.setChild(individualService.getUnknownEntity());

        pregnancyResult.setCollectionDateTime(ZonedDateTime.now());
        pregnancyResult.setCollectedBy(fieldWorkerService.getUnknownEntity());

        return null;
    }

}
