package org.openhds.service.impl.update;

import org.openhds.domain.model.update.Death;
import org.openhds.service.AuditableCollectedServiceTest;
import org.openhds.service.impl.census.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

/**
 * Created by Wolfe on 7/14/2015.
 */
public class DeathServiceTest extends AuditableCollectedServiceTest<Death, DeathService> {

    @Autowired
    private IndividualService individualService;

    @Autowired
    private VisitService visitService;

    @Override
    protected Death makeInvalidEntity() {
        return new Death();
    }

    @Override
    protected Death makeValidEntity(String name, String id) {
        Death death = new Death();
        death.setUuid(id);
        death.setDeathDate(ZonedDateTime.now().minusYears(1));
        death.setDeathCause(name);
        death.setDeathPlace(name);
        death.setIndividual(individualService.findAll(UUID_SORT).toList().get(0));
        death.setVisit(visitService.findAll(UUID_SORT).toList().get(0));

        initCollectedFields(death);

        return death;
    }

    @Override
    @Autowired
    protected void initialize(DeathService service) {
        this.service = service;
    }
}
