package org.openhds.service.impl.update;

import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.domain.model.update.PregnancyOutcome;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.update.PregnancyOutcomeRepository;
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
 * Created by Wolfe on 7/15/2015.
 */
@Service
public class PregnancyOutcomeService extends AbstractAuditableCollectedService<PregnancyOutcome, PregnancyOutcomeRepository> {

    @Autowired
    private LocationHierarchyService locationHierarchyService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private VisitService visitService;

    @Autowired
    public PregnancyOutcomeService(PregnancyOutcomeRepository pregnancyOutcomeRepository) {
        super(pregnancyOutcomeRepository);
    }

    @Override
    public PregnancyOutcome makePlaceHolder(String id, String name) {
        PregnancyOutcome pregnancyOutcome = new PregnancyOutcome();
        pregnancyOutcome.setUuid(id);
        pregnancyOutcome.setFather(individualService.getUnknownEntity());
        pregnancyOutcome.setMother(individualService.getUnknownEntity());
        pregnancyOutcome.setVisit(visitService.getUnknownEntity());
        pregnancyOutcome.setOutcomeDate(ZonedDateTime.now().minusYears(1));

        initPlaceHolderCollectedFields(pregnancyOutcome);

        return pregnancyOutcome;
    }

    public PregnancyOutcome recordPregnancyOutcome(PregnancyOutcome pregnancyOutcome,
                                                   String motherId,
                                                   String fatherId,
                                                   String visitId,
                                                   String fieldWorkerId){

        pregnancyOutcome.setMother(individualService.findOrMakePlaceHolder(motherId));
        pregnancyOutcome.setFather(individualService.findOrMakePlaceHolder(fatherId));
        pregnancyOutcome.setVisit(visitService.findOrMakePlaceHolder(visitId));
        pregnancyOutcome.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));

        return createOrUpdate(pregnancyOutcome);
    }

    @Override
    public void validate(PregnancyOutcome entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);

        //TODO: check that outcomeDate is not in the future
        //TODO: check that mother is gender female
        //TODO: check that if not null : gender father is male

        //TODO: check that number of live births not greather than children born
    }

    @Override
    public Set<LocationHierarchy> findEnclosingLocationHierarchies(PregnancyOutcome entity) {
        return locationHierarchyService.findEnclosingLocationHierarchies(entity.getVisit()
                .getLocation()
                .getLocationHierarchy());
    }

    @Override
    public Page<PregnancyOutcome> findByEnclosingLocationHierarchy(Pageable pageable,
                                                                   String locationHierarchyUuid,
                                                                   ZonedDateTime modifiedAfter,
                                                                   ZonedDateTime modifiedBefore) {
        return locationHierarchyService.findOtherByEnclosingLocationHierarchy(pageable,
                locationHierarchyUuid,
                modifiedAfter,
                modifiedBefore,
                PregnancyOutcomeService::enclosed,
                repository);
    }

    private static Specification<PregnancyOutcome> enclosed(final List<LocationHierarchy> enclosing) {
        return (root, query, cb) -> root.get("visit")
                .get("location")
                .get("locationHierarchy")
                .in(enclosing);
    }
}
