package org.openhds.service.impl.update;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.domain.model.ProjectCode;
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
        pregnancyOutcome.setEntityStatus(name);
        pregnancyOutcome.setFather(individualService.getUnknownEntity());
        pregnancyOutcome.getFather().setGender("MALE");
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
        pregnancyOutcome.setEntityStatus(pregnancyOutcome.NORMAL_STATUS);
        return createOrUpdate(pregnancyOutcome);
    }

    @Override
    public void validate(PregnancyOutcome pregnancyOutcome, ErrorLog errorLog) {
        super.validate(pregnancyOutcome, errorLog);

        if(pregnancyOutcome.getOutcomeDate().isAfter(pregnancyOutcome.getCollectionDateTime())){
          errorLog.appendError("PregnancyOutcome cannot have an outcomeDate in the future.");
        }

        if(!pregnancyOutcome.getMother().getGender().equals(projectCodeService.getValueForCodeName(ProjectCode.GENDER_FEMALE))){
          errorLog.appendError("PregnancyOutcome cannot have a non-female Mother.");
        }

        if(pregnancyOutcome.getMother().getEntityStatus().equals(AuditableEntity.NORMAL_STATUS) && !pregnancyOutcome.getMother().hasOpenResidency()){
          errorLog.appendError("PregnancyOutcome cannot have a mother without an open residency .");
        }

        ZonedDateTime dateOfBirth = pregnancyOutcome.getMother().getDateOfBirth();
        if(null != dateOfBirth) {
          int motherAge = ZonedDateTime.now().getYear() - dateOfBirth.getYear();
          if (motherAge < Integer.parseInt(projectCodeService.getValueForCodeName(ProjectCode.MIN_AGE_OF_PREGNANCY))) {
            errorLog.appendError("PregnancyOutcome cannot have a  mother under age of 12.");
          }
        }
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
