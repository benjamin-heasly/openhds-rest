package org.openhds.service.impl.update;

import org.openhds.domain.model.update.PregnancyOutcome;
import org.openhds.service.AuditableCollectedServiceTest;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

/**
 * Created by Wolfe on 7/15/2015.
 */
public class PregnancyOutcomeServiceTest extends AuditableCollectedServiceTest<PregnancyOutcome, PregnancyOutcomeService> {

    @Autowired
    private IndividualService individualService;

    @Autowired
    private FieldWorkerService fieldWorkerService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private PregnancyResultService pregnancyResultService;

    @Override
    protected PregnancyOutcome makeInvalidEntity() {
        return new PregnancyOutcome();
    }

    @Override
    protected PregnancyOutcome makeValidEntity(String name, String id) {
        PregnancyOutcome pregnancyOutcome = new PregnancyOutcome();
        pregnancyOutcome.setUuid(id);
        pregnancyOutcome.setOutcomeDate(ZonedDateTime.now().minusYears(1));

        pregnancyOutcome.setPregnancyResults(pregnancyResultService.findAll(UUID_SORT).toList());
        pregnancyOutcome.setMother(individualService.findAll(UUID_SORT).toList().get(0));
        pregnancyOutcome.setFather(individualService.findAll(UUID_SORT).toList().get(0));

        pregnancyOutcome.setVisit(visitService.findAll(UUID_SORT).toList().get(0));

        pregnancyOutcome.setCollectedBy(fieldWorkerService.findAll(UUID_SORT).toList().get(0));
        pregnancyOutcome.setCollectionDateTime(ZonedDateTime.now());

        return pregnancyOutcome;
    }

    @Override
    @Autowired
    protected void initialize(PregnancyOutcomeService service) {
        this.service = service;
    }
}
