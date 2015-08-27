package org.openhds.service.impl.update;

import org.openhds.domain.model.ProjectCode;
import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.domain.model.update.PregnancyResult;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.update.PregnancyResultRepository;
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
public class PregnancyResultService extends AbstractAuditableCollectedService<PregnancyResult, PregnancyResultRepository>{

    @Autowired
    private LocationHierarchyService locationHierarchyService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private PregnancyOutcomeService pregnancyOutcomeService;

    @Autowired
    public PregnancyResultService(PregnancyResultRepository repository) {
        super(repository);
    }

    @Override
    public PregnancyResult makePlaceHolder(String id, String name) {
        PregnancyResult pregnancyResult = new PregnancyResult();
        pregnancyResult.setUuid(id);
        pregnancyResult.setIsPlaceholder(true);
        pregnancyResult.setPregnancyOutcome(pregnancyOutcomeService.getUnknownEntity());
        pregnancyResult.setType(name);
        pregnancyResult.setChild(individualService.getUnknownEntity());

        initPlaceHolderCollectedFields(pregnancyResult);

        return pregnancyResult;
    }

    public PregnancyResult recordPregnancyResult(PregnancyResult pregnancyResult,
                                                 String pregnancyOutcomeId,
                                                 String childId,
                                                 String fieldWorkerId){

        pregnancyResult.setPregnancyOutcome(pregnancyOutcomeService.findOrMakePlaceHolder(pregnancyOutcomeId));
        pregnancyResult.setChild(individualService.findOrMakePlaceHolder(childId));
        pregnancyResult.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));

        return createOrUpdate(pregnancyResult);

    }

    @Override
    public void validate(PregnancyResult pregnancyResult, ErrorLog errorLog) {
        super.validate(pregnancyResult, errorLog);

        //TODO: are these valid concerns?
//        if(!projectCodeService.isValueInCodeGroup(pregnancyResult.getType(), ProjectCode.PREGNANCY_RESULT_TYPE)) {
//          errorLog.appendError("PregnancyResult cannot have a type of: ["+pregnancyResult.getType()+"].");
//        }
//
//        if(null != pregnancyResult.getChild()
//            && !pregnancyResult.getType().equals(projectCodeService.getValueForCodeName(ProjectCode.PREGNANCY_RESULT_LIVE_BIRTH))){
//          errorLog.appendError("PregnancyResult that results in a child cannot have a type of: ["+pregnancyResult.getType()+"].");
//        }
    }

    @Override
    public Set<LocationHierarchy> findEnclosingLocationHierarchies(PregnancyResult entity) {
        return locationHierarchyService.findEnclosingLocationHierarchies(entity.getPregnancyOutcome()
                .getVisit()
                .getLocation()
                .getLocationHierarchy());
    }

    @Override
    public Page<PregnancyResult> findByEnclosingLocationHierarchy(Pageable pageable,
                                                                  String locationHierarchyUuid,
                                                                  ZonedDateTime modifiedAfter,
                                                                  ZonedDateTime modifiedBefore) {
        return locationHierarchyService.findOtherByEnclosingLocationHierarchy(pageable,
                locationHierarchyUuid,
                modifiedAfter,
                modifiedBefore,
                PregnancyResultService::enclosed,
                repository);
    }

    private static Specification<PregnancyResult> enclosed(final List<LocationHierarchy> enclosing) {
        return (root, query, cb) -> root.get("pregnancyOutcome")
                .get("visit")
                .get("location")
                .get("locationHierarchy")
                .in(enclosing);
    }
}
