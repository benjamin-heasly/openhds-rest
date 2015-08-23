package org.openhds.service.impl.update;

import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.domain.model.update.PregnancyObservation;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.update.PregnancyObservationRepository;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.LocationHierarchyService;
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
public class PregnancyObservationService extends AbstractAuditableCollectedService<
        PregnancyObservation, PregnancyObservationRepository> {

    @Autowired
    private LocationHierarchyService locationHierarchyService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    public PregnancyObservationService(PregnancyObservationRepository repository) {
        super(repository);
    }

    @Override
    public PregnancyObservation makePlaceHolder(String id, String name) {
        PregnancyObservation pregnancyObservation = new PregnancyObservation();
        pregnancyObservation.setUuid(id);
        pregnancyObservation.setVisit(visitService.getUnknownEntity());
        pregnancyObservation.setMother(individualService.getUnknownEntity());
        pregnancyObservation.setPregnancyDate(ZonedDateTime.now().minusMonths(5));
        pregnancyObservation.setExpectedDeliveryDate(ZonedDateTime.now().plusMonths(5));

        initPlaceHolderCollectedFields(pregnancyObservation);

        return pregnancyObservation;
    }

    public PregnancyObservation recordPregnancyObservation(PregnancyObservation pregnancyObservation,
                                                           String motherId,
                                                           String visitId,
                                                           String fieldWorkerId) {

        pregnancyObservation.setMother(individualService.findOrMakePlaceHolder(motherId));
        pregnancyObservation.setVisit(visitService.findOrMakePlaceHolder(visitId));
        pregnancyObservation.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));

        return createOrUpdate(pregnancyObservation);
    }

    @Override
    public void validate(PregnancyObservation entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);

        //TODO: check that expectedDeliveryDate is not in the past
        //TODO: check that pregnancyDate is not in the future
        //TODO: check that individual is not recorded as dead
        //TODO: check that individual is female
    }

    @Override
    public Set<LocationHierarchy> findEnclosingLocationHierarchies(PregnancyObservation entity) {
        return locationHierarchyService.findEnclosingLocationHierarchies(entity.getVisit()
                .getLocation()
                .getLocationHierarchy());
    }

    @Override
    public Page<PregnancyObservation> findByEnclosingLocationHierarchy(Pageable pageable,
                                                                       String locationHierarchyUuid,
                                                                       ZonedDateTime modifiedAfter,
                                                                       ZonedDateTime modifiedBefore) {
        return locationHierarchyService.findOtherByEnclosingLocationHierarchy(pageable,
                locationHierarchyUuid,
                modifiedAfter,
                modifiedBefore,
                PregnancyObservationService::enclosed,
                repository);
    }

    private static Specification<PregnancyObservation> enclosed(final List<LocationHierarchy> enclosing) {
        return (root, query, cb) -> root.get("visit")
                .get("location")
                .get("locationHierarchy")
                .in(enclosing);
    }
}
