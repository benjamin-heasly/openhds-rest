package org.openhds.service.impl.census;

import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.domain.model.census.Residency;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.census.ResidencyRepository;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

/**
 * Created by Wolfe on 7/14/2015.
 */
@Service
public class ResidencyService extends AbstractAuditableCollectedService<Residency, ResidencyRepository> {

    @Autowired
    private IndividualService individualService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationHierarchyService locationHierarchyService;

    @Autowired
    public ResidencyService(ResidencyRepository repository) {
        super(repository);
    }

    @Override
    public Residency makePlaceHolder(String id, String name) {
        Residency residency = new Residency();
        residency.setUuid(id);
        residency.setEntityStatus(name);
        residency.setStartDate(ZonedDateTime.now().minusYears(1));
        residency.setStartType(name);
        residency.setIndividual(individualService.getUnknownEntity());
        residency.setLocation(locationService.getUnknownEntity());

        initPlaceHolderCollectedFields(residency);

        return residency;
    }


    public Residency recordResidency (Residency residency, String individualId, String locationId, String fieldWorkerId){
        residency.setIndividual(individualService.findOrMakePlaceHolder(individualId));
        residency.setLocation(locationService.findOrMakePlaceHolder(locationId));
        residency.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        residency.setEntityStatus(residency.NORMAL_STATUS);
        return createOrUpdate(residency);
    }

    @Override
    public Set<LocationHierarchy> findEnclosingLocationHierarchies(Residency entity) {
        return locationHierarchyService.findEnclosingLocationHierarchies(entity.getLocation().getLocationHierarchy());
    }

    @Override
    public Page<Residency> findByEnclosingLocationHierarchy(Pageable pageable,
                                                            String locationHierarchyUuid,
                                                            ZonedDateTime modifiedAfter,
                                                            ZonedDateTime modifiedBefore) {
        return locationHierarchyService.findOtherByEnclosingLocationHierarchy(pageable,
            locationHierarchyUuid,
            modifiedAfter,
            modifiedBefore,
            ResidencyService::enclosed,
            repository);
    }

    private static Specification<Residency> enclosed(final List<LocationHierarchy> enclosing) {
        return (root, query, cb) -> root.get("location").get("locationHierarchy").in(enclosing);
    }

    @Override
    public void validate(Residency residency, ErrorLog errorLog) {
        super.validate(residency, errorLog);

        if (residency.getStartDate().isAfter(residency.getCollectionDateTime())) {
            errorLog.appendError("Residency cannot have a startDate in the future.");
        }

        if (null != residency.getEndDate() &&
            residency.getStartDate().isAfter(residency.getEndDate())) {
            errorLog.appendError("Residency cannot have a startDate before its endDate.");
        }

        Set<Residency> residencies = residency.getIndividual().getResidencies();
        if (null != residencies){
            for(Residency existingResidency : residencies){
                if(null != residency.getUuid()
                && !existingResidency.equals(residency)
                && null != existingResidency.getEndDate()){
                    errorLog.appendError("Individual cannot have more than one open residency.");
                }
            }
        }

    }

}
