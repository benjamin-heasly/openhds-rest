package org.openhds.service.impl.update;

import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.domain.model.update.Visit;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.update.VisitRepository;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.openhds.service.impl.census.LocationHierarchyService;
import org.openhds.service.impl.census.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

/**
 * Created by bsh on 7/13/15.
 */
@Service
public class VisitService extends AbstractAuditableExtIdService<Visit, VisitRepository> {

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationHierarchyService locationHierarchyService;

    @Autowired
    public VisitService(VisitRepository repository) {
        super(repository);
    }

    @Override
    public Visit makePlaceHolder(String id, String name) {
        Visit visit = new Visit();
        visit.setUuid(id);
        visit.setIsPlaceholder(true);
        visit.setExtId(name);
        visit.setLocation(locationService.getUnknownEntity());
        visit.setVisitDate(ZonedDateTime.now().minusYears(1));

        initPlaceHolderCollectedFields(visit);

        return visit;
    }

    public Visit recordVisit(Visit visit, String locationId, String fieldWorkerId){
        visit.setLocation(locationService.findOrMakePlaceHolder(locationId));
        visit.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        return createOrUpdate(visit);
    }

    @Override
    public void validate(Visit visit, ErrorLog errorLog) {
        super.validate(visit, errorLog);

        if(visit.getVisitDate().isAfter(visit.getCollectionDateTime())){
            errorLog.appendError("Visit cannot have a visitDate in the future.");
        }
    }

    @Override
    public Set<LocationHierarchy> findEnclosingLocationHierarchies(Visit entity) {
        return locationHierarchyService.findEnclosingLocationHierarchies(entity.getLocation().getLocationHierarchy());
    }

    @Override
    public Page<Visit> findByEnclosingLocationHierarchy(Pageable pageable,
                                                        String locationHierarchyUuid,
                                                        ZonedDateTime modifiedAfter,
                                                        ZonedDateTime modifiedBefore) {
        return locationHierarchyService.findOtherByEnclosingLocationHierarchy(pageable,
                locationHierarchyUuid,
                modifiedAfter,
                modifiedBefore,
                VisitService::enclosed,
                repository);
    }

    private static Specification<Visit> enclosed(final List<LocationHierarchy> enclosing) {
        return (root, query, cb) -> root.get("location").get("locationHierarchy").in(enclosing);
    }
}
