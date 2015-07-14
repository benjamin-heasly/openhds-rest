package org.openhds.service.impl.update;

import org.openhds.domain.model.update.Death;
import org.openhds.repository.concrete.FieldWorkerRepository;
import org.openhds.repository.concrete.update.DeathRepository;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * Created by Wolfe on 7/14/2015.
 */
@Service
public class DeathService extends AbstractAuditableCollectedService<Death, DeathRepository> {

    @Autowired
    private FieldWorkerService fieldWorkerService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private VisitService visitService;

    @Autowired
    public DeathService(DeathRepository repository) {
        super(repository);
    }

    @Override
    protected Death makeUnknownEntity() {
        Death death = new Death();
        death.setIndividual(individualService.getUnknownEntity());
        death.setVisit(visitService.getUnknownEntity());
        death.setCollectedBy(fieldWorkerService.getUnknownEntity());
        death.setCollectionDateTime(ZonedDateTime.now());
        death.setDeathDate(ZonedDateTime.now().minusYears(1));
        death.setDeathPlace("unknown");
        death.setDeathCause("unknown");

        return death;
    }
}
