package org.openhds.service.impl.update;

import org.openhds.domain.model.update.Death;
import org.openhds.repository.concrete.update.DeathRepository;
import org.openhds.service.contract.AbstractAuditableCollectedService;
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
    private IndividualService individualService;

    @Autowired
    private VisitService visitService;

    @Autowired
    public DeathService(DeathRepository repository) {
        super(repository);
    }

    @Override
    public Death makePlaceHolder(String id, String name) {
        Death death = new Death();
        death.setUuid(id);
        death.setIndividual(individualService.getUnknownEntity());
        death.setVisit(visitService.getUnknownEntity());
        death.setDeathDate(ZonedDateTime.now().minusYears(1));
        death.setDeathPlace(name);
        death.setDeathCause(name);

        initPlaceHolderCollectedFields(death);

        return death;
    }

    public Death recordDeath(Death death, String individualId, String visitId, String fieldWorkerId){
        death.setIndividual(individualService.findOrMakePlaceHolder(individualId));
        death.setVisit(visitService.findOrMakePlaceHolder(visitId));
        death.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        return createOrUpdate(death);
    }
}
