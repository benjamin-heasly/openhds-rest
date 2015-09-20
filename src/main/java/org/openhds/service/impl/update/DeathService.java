package org.openhds.service.impl.update;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.domain.model.census.*;
import org.openhds.domain.model.update.Death;
import org.openhds.domain.model.update.PregnancyObservation;
import org.openhds.domain.model.update.PregnancyOutcome;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.update.DeathRepository;
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
 * Created by Wolfe on 7/14/2015.
 */
@Service
public class DeathService extends AbstractAuditableCollectedService<Death, DeathRepository> {

    @Autowired
    private LocationHierarchyService locationHierarchyService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private PregnancyOutcomeService pregnancyOutcomeService;

    @Autowired
    public DeathService(DeathRepository repository) {
        super(repository);
    }

    @Override
    public Death makePlaceHolder(String id, String name) {
        Death death = new Death();
        death.setUuid(id);
        death.setEntityStatus(name);
        death.setIndividual(individualService.getUnknownEntity());
        death.setVisit(visitService.getUnknownEntity());
        death.setDeathDate(ZonedDateTime.now().minusYears(1));
        death.setDeathPlace(name);
        death.setDeathCause(name);

        initPlaceHolderCollectedFields(death);

        return death;
    }

    public Death recordDeath(Death death, ZonedDateTime recordTime, String individualId, String visitId, String fieldWorkerId){
        death.setIndividual(individualService.findOrMakePlaceHolder(individualId));
        death.setVisit(visitService.findOrMakePlaceHolder(visitId));
        death.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        death.setEntityStatus(death.NORMAL_STATUS);
        Death persistedDeath = createOrUpdate(death);

        String endType = "death";

        Individual deadIndividual = persistedDeath.getIndividual();
        if (deadIndividual.getEntityStatus().equals(deadIndividual.NORMAL_STATUS)){

            ZonedDateTime latestPregnancyObservation = null;
            for(PregnancyObservation pregnancyObservation : deadIndividual.getPregnancyObservations()){
                if(null == latestPregnancyObservation || latestPregnancyObservation.isBefore(pregnancyObservation.getPregnancyDate())){
                  latestPregnancyObservation = pregnancyObservation.getPregnancyDate();
                }
            }

            ZonedDateTime latestPregnancyOutcome = null;
            for(PregnancyOutcome pregnancyOutcome : deadIndividual.getPregnancyOutcomes()){
                if(null == latestPregnancyOutcome || latestPregnancyOutcome.isBefore(pregnancyOutcome.getOutcomeDate())){
                  latestPregnancyOutcome = pregnancyOutcome.getOutcomeDate();
                }
            }

            if(null != latestPregnancyObservation
                && null != latestPregnancyOutcome
                && latestPregnancyObservation.isAfter(latestPregnancyOutcome)){

                PregnancyOutcome pregnancyOutcome = new PregnancyOutcome();
                pregnancyOutcome.setCollectedBy(persistedDeath.getCollectedBy());
                pregnancyOutcome.setCollectionDateTime(persistedDeath.getCollectionDateTime());
                pregnancyOutcome.setEntityStatus(pregnancyOutcome.NORMAL_STATUS);
                pregnancyOutcome.setMother(deadIndividual);
                pregnancyOutcome.setOutcomeDate(persistedDeath.getDeathDate());
                pregnancyOutcome.setVisit(persistedDeath.getVisit());
                pregnancyOutcomeService.createOrUpdate(pregnancyOutcome);

            }

            for(Membership membership : deadIndividual.getMemberships()){
              membership.setEndDate(recordTime);
              membership.setEndType(endType);
            }
            for(Relationship relationship : deadIndividual.getRelationshipsAsIndividualA()){
              relationship.setEndDate(recordTime);
              relationship.setEndType(endType);
            }
            for(Relationship relationship : deadIndividual.getRelationshipsAsIndividualB()){
              relationship.setEndDate(recordTime);
              relationship.setEndType(endType);
            }
            for(Residency residency : deadIndividual.getResidencies()){
              residency.setEndDate(recordTime);
              residency.setEndType(endType);
            }

        }

        return persistedDeath;
    }

    @Override
    public void validate(Death death, ErrorLog errorLog) {
        super.validate(death, errorLog);

        if(death.getDeathDate().isAfter(death.getCollectionDateTime())){
          errorLog.appendError("Death cannot have a deathDate in the future.");
        }

        Individual deadIndividual = death.getIndividual();
        if(deadIndividual.getEntityStatus().equals(AuditableEntity.NORMAL_STATUS) && !death.getIndividual().hasOpenResidency()){
          errorLog.appendError("Individual must have an open residency to be recorded as dead.");
        }

        Death existingDeath = death.getIndividual().getDeath();
        if(null != existingDeath
            && existingDeath.getEntityStatus().equals(AuditableEntity.NORMAL_STATUS)
            &&  null != death.getUuid()
            && !existingDeath.equals(death)){
          errorLog.appendError("Individual cannot have multiple deaths.");
        }
    }

    @Override
    public Set<LocationHierarchy> findEnclosingLocationHierarchies(Death entity) {
        return locationHierarchyService.findEnclosingLocationHierarchies(entity.getVisit()
                .getLocation()
                .getLocationHierarchy());
    }

    @Override
    public Page<Death> findByEnclosingLocationHierarchy(Pageable pageable,
                                                        String locationHierarchyUuid,
                                                        ZonedDateTime modifiedAfter,
                                                        ZonedDateTime modifiedBefore) {
        return locationHierarchyService.findOtherByEnclosingLocationHierarchy(pageable,
                locationHierarchyUuid,
                modifiedAfter,
                modifiedBefore,
                DeathService::enclosed,
                repository);
    }

    private static Specification<Death> enclosed(final List<LocationHierarchy> enclosing) {
        return (root, query, cb) -> root.get("visit")
                .get("location")
                .get("locationHierarchy")
                .in(enclosing);
    }
}
