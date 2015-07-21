package org.openhds.service.impl.update;

import org.openhds.domain.model.update.Visit;
import org.openhds.repository.concrete.update.VisitRepository;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.openhds.service.impl.census.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/13/15.
 */
@Service
public class VisitService extends AbstractAuditableExtIdService<Visit, VisitRepository> {

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

    public Visit recordVisit(Visit visit, String locationId, String fieldWorkerId){
        visit.setLocation(locationService.findOrMakePlaceHolder(locationId));
        visit.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        return createOrUpdate(visit);
    }
}
