package org.openhds.service.impl;

import org.openhds.domain.model.Visit;
import org.openhds.repository.concrete.VisitRepository;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/13/15.
 */
@Component
public class VisitService extends AbstractAuditableExtIdService<Visit, VisitRepository> {

    @Autowired
    FieldWorkerService fieldWorkerService;

    @Autowired
    LocationService locationService;

    @Autowired
    public VisitService(VisitRepository repository) {
        super(repository);
    }

    @Override
    protected Visit makeUnknownEntity() {
        Visit visit = new Visit();
        visit.setExtId("unknown");
        visit.setCollectedBy(fieldWorkerService.getUnknownEntity());
        visit.setCollectionDateTime(ZonedDateTime.now());
        visit.setLocation(locationService.getUnknownEntity());
        visit.setVisitDate(ZonedDateTime.now().minusYears(1));
        return visit;
    }
}
