package org.openhds.service.impl.update;

import org.openhds.domain.model.update.PregnancyResult;
import org.openhds.service.AuditableCollectedServiceTest;
import org.openhds.service.impl.census.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Wolfe on 7/15/2015.
 */
public class PregnancyResultServiceTest extends AuditableCollectedServiceTest<PregnancyResult, PregnancyResultService> {

    @Autowired
    private IndividualService individualService;

    @Autowired
    private PregnancyOutcomeService pregnancyOutcomeService;

    @Override
    protected PregnancyResult makeInvalidEntity() {
        return new PregnancyResult();
    }

    @Override
    protected PregnancyResult makeValidEntity(String name, String id) {
        PregnancyResult pregnancyResult = new PregnancyResult();
        pregnancyResult.setUuid(id);
        pregnancyResult.setType(name);
        pregnancyResult.setPregnancyOutcome(pregnancyOutcomeService.findAll(UUID_SORT).toList().get(0));
        pregnancyResult.setChild(individualService.findAll(UUID_SORT).toList().get(0));

        initCollectedFields(pregnancyResult);

        return pregnancyResult;
    }

    @Override
    @Autowired
    protected void initialize(PregnancyResultService service) {
        this.service = service;
    }

}
