package org.openhds.service.impl.update;

import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.domain.model.census.Membership;
import org.openhds.domain.model.census.Residency;
import org.openhds.domain.model.update.PregnancyOutcome;
import org.openhds.domain.model.update.PregnancyResult;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.update.PregnancyResultRepository;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.LocationHierarchyService;
import org.openhds.service.impl.census.MembershipService;
import org.openhds.service.impl.census.ResidencyService;
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
    private ResidencyService residencyService;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    public PregnancyResultService(PregnancyResultRepository repository) {
        super(repository);
    }

    @Override
    public PregnancyResult makePlaceHolder(String id, String name) {
        PregnancyResult pregnancyResult = new PregnancyResult();
        pregnancyResult.setUuid(id);
        pregnancyResult.setEntityStatus(name);
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

        if(null != childId){
          pregnancyResult.setChild(individualService.findOrMakePlaceHolder(childId));
        }
        pregnancyResult.setPregnancyOutcome(pregnancyOutcomeService.findOrMakePlaceHolder(pregnancyOutcomeId));
        pregnancyResult.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        pregnancyResult.setEntityStatus(pregnancyResult.NORMAL_STATUS);
        return createOrUpdate(pregnancyResult);

    }

    public PregnancyResult recordPregnancyResult(PregnancyResult pregnancyResult,
                                                 ZonedDateTime recordTime,
                                                 String pregnancyOutcomeId,
                                                 String childId,
                                                 String fieldWorkerId,
                                                 String childMembershipId,
                                                 String childResidencyId){

        String startType = "birth";

        FieldWorker collectedBy = fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId);
        ZonedDateTime collectionDateTime = pregnancyResult.getCollectionDateTime();

        pregnancyResult.setPregnancyOutcome(pregnancyOutcomeService.findOrMakePlaceHolder(pregnancyOutcomeId));
        pregnancyResult.setCollectedBy(collectedBy);
        pregnancyResult.setEntityStatus(pregnancyResult.NORMAL_STATUS);

        // non-live birth
        if(null == childId){
            return createOrUpdate(pregnancyResult);
        }

        // live birth
        pregnancyResult.setChild(individualService.findOrMakePlaceHolder(childId));
        createOrUpdate(pregnancyResult);

        Individual child = pregnancyResult.getChild();
        PregnancyOutcome outcome = pregnancyResult.getPregnancyOutcome();

        if(outcome.getEntityStatus().equals(outcome.NORMAL_STATUS)){
            Individual mother = outcome.getMother();
            child.setFather(outcome.getFather());
            child.setMother(mother);
            individualService.createOrUpdate(child);

            if(mother.getEntityStatus().equals(Individual.NORMAL_STATUS)){
                for(Residency residency : mother.getResidencies()){
                    if(null != residency.getEndDate()){
                        Residency childResidency = new Residency();
                        childResidency.setUuid(childResidencyId);
                        childResidency.setStartDate(recordTime);
                        childResidency.setStartType(startType);
                        childResidency.setLocation(residency.getLocation());
                        childResidency.setIndividual(child);
                        childResidency.setCollectedBy(collectedBy);
                        childResidency.setCollectionDateTime(collectionDateTime);
                        childResidency.setEntityStatus(childResidency.NORMAL_STATUS);
                        residencyService.createOrUpdate(childResidency);
                        break;
                    }
                }

                for(Membership membership : mother.getMemberships()){
                    if(null != membership.getEndDate()){
                        Membership childMembership = new Membership();
                        childMembership.setUuid(childMembershipId);
                        childMembership.setStartDate(recordTime);
                        childMembership.setStartType(startType);
                        childMembership.setSocialGroup(membership.getSocialGroup());
                        childMembership.setCollectedBy(collectedBy);
                        childMembership.setCollectionDateTime(recordTime);
                        childMembership.setIndividual(child);
                        childMembership.setEntityStatus(childMembership.NORMAL_STATUS);
                        membershipService.createOrUpdate(childMembership);
                        break;
                    }
                }
            }


        }


        return pregnancyResult;

    }

    @Override
    public void validate(PregnancyResult pregnancyResult, ErrorLog errorLog) {
        super.validate(pregnancyResult, errorLog);

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
