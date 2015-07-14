package org.openhds.service.impl.update;

import org.openhds.domain.model.update.Visit;
import org.openhds.service.AuditableExtIdServiceTest;
import org.openhds.service.UuidServiceTest;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.LocationService;
import org.openhds.service.impl.update.VisitService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/13/15.
 */
public class VisitServiceTest extends AuditableExtIdServiceTest<Visit, VisitService> {

    @Autowired
    FieldWorkerService fieldWorkerService;

    @Autowired
    LocationService locationService;

    @Autowired
    @Override
    protected void initialize(VisitService service) {
        this.service = service;
    }

    @Override
    protected Visit makeInvalidEntity() {
        return new Visit();
    }

    @Override
    protected Visit makeValidEntity(String name, String id) {
        Visit visit = new Visit();
        visit.setUuid(id);

        visit.setExtId(name);
        visit.setCollectedBy(fieldWorkerService.findAll(UUID_SORT).toList().get(0));
        visit.setCollectionDateTime(ZonedDateTime.now());

        visit.setLocation(locationService.findAll(UUID_SORT).toList().get(0));
        visit.setVisitDate(ZonedDateTime.now().minusYears(1));

        return visit;
    }
}
