package org.openhds.service.impl.update;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.domain.model.ProjectCode;
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
        pregnancyObservation.setStatus(name);
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
        pregnancyObservation.setStatus(pregnancyObservation.NORMAL_STATUS);
        return createOrUpdate(pregnancyObservation);
    }

    @Override
    public void validate(PregnancyObservation pregnancyObservation, ErrorLog errorLog) {
        super.validate(pregnancyObservation, errorLog);

        //TODO: check that mother's last pregnancyObservation's pregnancyDate is not less than a year away from this one

        if(pregnancyObservation.getExpectedDeliveryDate().isBefore(pregnancyObservation.getCollectionDateTime())){
          errorLog.appendError("PregnancyObservation cannot have an expectedDeliverDate in the past.");
        }

        if(pregnancyObservation.getPregnancyDate().isAfter(pregnancyObservation.getCollectionDateTime())){
          errorLog.appendError("PregnancyObservation cannot have an pregnancyDate in the future.");
        }

        if(pregnancyObservation.getMother().getStatus().equals(AuditableEntity.NORMAL_STATUS) && null != pregnancyObservation.getMother().getDeath()){
          errorLog.appendError("PregnancyObservation cannot have a mother registered as dead.");
        }

        if(!pregnancyObservation.getMother().getGender().equals(projectCodeService.getValueForCodeName(ProjectCode.GENDER_FEMALE))){
          errorLog.appendError("PregnancyObservation cannot have a non-female Mother.");
        }

        if(pregnancyObservation.getMother().getStatus().equals(AuditableEntity.NORMAL_STATUS) && !pregnancyObservation.getMother().hasOpenResidency()){
          errorLog.appendError("PregnancyObservation cannot have a mother without an open residency .");
        }

      ZonedDateTime dateOfBirth = pregnancyObservation.getMother().getDateOfBirth();
      if(null != dateOfBirth) {
        int motherAge = ZonedDateTime.now().getYear() - dateOfBirth.getYear();
        if (motherAge < Integer.parseInt(projectCodeService.getValueForCodeName(ProjectCode.MIN_AGE_OF_PREGNANCY))) {
          errorLog.appendError("PregnancyObservation cannot have a mother under the age of 12.");
        }
      }

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
