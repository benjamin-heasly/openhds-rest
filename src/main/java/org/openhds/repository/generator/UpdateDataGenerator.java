package org.openhds.repository.generator;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.contract.AuditableEntity;
import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.census.Location;
import org.openhds.domain.model.census.Residency;
import org.openhds.domain.model.update.*;
import org.openhds.repository.concrete.update.*;
import org.openhds.security.model.User;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.UserService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.LocationService;
import org.openhds.service.impl.census.ResidencyService;
import org.openhds.service.impl.update.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

/**
 * Created by bsh on 4 August 2015.
 * <p>
 * Generates sample update data, including
 * Visit, Death, InMigration, OutMigration, PregnancyObservation, PregnancyOutcome, PregnancyResult
 * <p>
 * For each entity, only inserts sample records if there are no records yet.
 * This behavior should support testing without messing up existing project data.
 * <p>
 * At each existing Location, creates a Visit.
 * <p>
 * Each Visit will include:
 * - a death
 * - an in-migration
 * - an out-migration
 * - a pregnancy observation
 * - a pregnancy outcome
 * - a pregnancy result
 */
@Component
public class UpdateDataGenerator implements DataGenerator {

    private final VisitService visitService;
    private final VisitRepository visitRepository;

    private final DeathService deathService;
    private final DeathRepository deathRepository;

    private final InMigrationService inMigrationService;
    private final InMigationRepository inMigationRepository;

    private final OutMigrationService outMigrationService;
    private final OutMigationRepository outMigationRepository;

    private final PregnancyObservationService pregnancyObservationService;
    private final PregnancyObservationRepository pregnancyObservationRepository;

    private final PregnancyOutcomeService pregnancyOutcomeService;
    private final PregnancyOutcomeRepository pregnancyOutcomeRepository;

    private final PregnancyResultService pregnancyResultService;
    private final PregnancyResultRepository pregnancyResultRepository;

    private final IndividualService individualService;

    private final ResidencyService residencyService;

    private final LocationService locationService;

    private final FieldWorkerService fieldWorkerService;

    private final UserService userService;

    @Autowired
    public UpdateDataGenerator(VisitService visitService,
                               VisitRepository visitRepository,
                               DeathService deathService,
                               DeathRepository deathRepository,
                               InMigrationService inMigrationService,
                               InMigationRepository inMigationRepository,
                               OutMigrationService outMigrationService,
                               OutMigationRepository outMigationRepository,
                               PregnancyObservationService pregnancyObservationService,
                               PregnancyObservationRepository pregnancyObservationRepository,
                               PregnancyOutcomeService pregnancyOutcomeService,
                               PregnancyOutcomeRepository pregnancyOutcomeRepository,
                               PregnancyResultService pregnancyResultService,
                               PregnancyResultRepository pregnancyResultRepository,
                               IndividualService individualService,
                               ResidencyService residencyService,
                               LocationService locationService,
                               FieldWorkerService fieldWorkerService,
                               UserService userService) {
        this.visitService = visitService;
        this.visitRepository = visitRepository;
        this.deathService = deathService;
        this.deathRepository = deathRepository;
        this.inMigrationService = inMigrationService;
        this.inMigationRepository = inMigationRepository;
        this.outMigrationService = outMigrationService;
        this.outMigationRepository = outMigationRepository;
        this.pregnancyObservationService = pregnancyObservationService;
        this.pregnancyObservationRepository = pregnancyObservationRepository;
        this.pregnancyOutcomeService = pregnancyOutcomeService;
        this.pregnancyOutcomeRepository = pregnancyOutcomeRepository;
        this.pregnancyResultService = pregnancyResultService;
        this.pregnancyResultRepository = pregnancyResultRepository;
        this.individualService = individualService;
        this.residencyService = residencyService;
        this.locationService = locationService;
        this.fieldWorkerService = fieldWorkerService;
        this.userService = userService;
    }

    @Override
    public void generateData(int size) {
        generateUnknowns();
        addVisitToEach(locationService.findAll(new Sort("uuid")));
    }

    @Override
    public void generateData() {
        generateData(0);
    }

    @Override
    public void clearData() {
        pregnancyResultRepository.deleteAllInBatch();
        pregnancyOutcomeRepository.deleteAllInBatch();
        pregnancyObservationRepository.deleteAllInBatch();
        deathRepository.deleteAllInBatch();
        inMigationRepository.deleteAllInBatch();
        outMigationRepository.deleteAllInBatch();
        visitRepository.deleteAllInBatch();
    }

    // trigger services to create unknown entities ahead of time, for predictable entity counts
    private void generateUnknowns() {
        visitService.getUnknownEntity();
        outMigrationService.getUnknownEntity();
        inMigrationService.getUnknownEntity();
        deathService.getUnknownEntity();
        pregnancyOutcomeService.getUnknownEntity();
        pregnancyObservationService.getUnknownEntity();
        pregnancyResultService.getUnknownEntity();
    }

    // add a visit at each location
    private void addVisitToEach(Iterable<Location> locations) {
        if (visitService.hasRecords()
                || outMigrationService.hasRecords()
                || inMigrationService.hasRecords()
                || deathService.hasRecords()
                || pregnancyObservationService.hasRecords()
                || pregnancyOutcomeService.hasRecords()
                || pregnancyResultService.hasRecords()) {
            return;
        }

        Individual individual = individualService.getUnknownEntity();
        Residency residency = residencyService.getUnknownEntity();

        for (Location locaiton : locations) {
            addVisit(locaiton, individual, residency);
        }
    }

    // add a visit and other updates at the given location
    private void addVisit(Location location, Individual individual, Residency residency) {
        Visit visit = generateVisit(location);
        generateOutMigration(visit, individual, residency);
        generateInMigration(visit, individual, residency);
        generatePregnancyObservation(visit, individual);
        generatePregnancyOutcomeAndResult(visit, individual);
    }

    private void generateOutMigration(Visit visit, Individual individual, Residency residency) {
        OutMigration outMigration = new OutMigration();
        setAuditableFields(outMigration);
        setCollectedFields(outMigration);

        outMigration.setVisit(visit);
        outMigration.setIndividual(individual);
        outMigration.setMigrationDate(ZonedDateTime.now());
        outMigration.setDestination("sample-destination");
        outMigration.setReason("sample-reason");
        outMigration.setResidency(residency);
    }

    private void generateInMigration(Visit visit, Individual individual, Residency residency) {
        InMigration inMigration = new InMigration();
        setAuditableFields(inMigration);
        setCollectedFields(inMigration);

        inMigration.setVisit(visit);
        inMigration.setResidency(residency);
        inMigration.setIndividual(individual);
        inMigration.setMigrationDate(ZonedDateTime.now());
        inMigration.setMigrationType("sample-migration");
        inMigration.setOrigin("sample-origin");
        inMigration.setReason("sample-reason");

        inMigationRepository.save(inMigration);
    }

    private void generateDeath(Visit visit, Individual individual) {
        Death death = new Death();
        setAuditableFields(death);
        setCollectedFields(death);

        death.setVisit(visit);
        death.setIndividual(individual);
        death.setDeathDate(ZonedDateTime.now());

        deathRepository.save(death);
    }

    private void generatePregnancyObservation(Visit visit, Individual individual) {
        PregnancyObservation pregnancyObservation = new PregnancyObservation();
        setAuditableFields(pregnancyObservation);
        setCollectedFields(pregnancyObservation);

        pregnancyObservation.setVisit(visit);
        pregnancyObservation.setMother(individual);
        pregnancyObservation.setPregnancyDate(ZonedDateTime.now().minusMonths(5));
        pregnancyObservation.setExpectedDeliveryDate(ZonedDateTime.now().plusMonths(5));

        pregnancyObservationRepository.save(pregnancyObservation);
    }

    private void generatePregnancyOutcomeAndResult(Visit visit, Individual individual) {
        PregnancyOutcome pregnancyOutcome = new PregnancyOutcome();
        setAuditableFields(pregnancyOutcome);
        setCollectedFields(pregnancyOutcome);

        pregnancyOutcome.setVisit(visit);
        pregnancyOutcome.setMother(individual);
        pregnancyOutcome.setOutcomeDate(ZonedDateTime.now());

        pregnancyOutcomeRepository.save(pregnancyOutcome);

        generatePregnancyResult(pregnancyOutcome);
    }

    private void generatePregnancyResult(PregnancyOutcome pregnancyOutcome) {
        PregnancyResult pregnancyResult = new PregnancyResult();
        setAuditableFields(pregnancyResult);
        setCollectedFields(pregnancyResult);

        pregnancyResult.setPregnancyOutcome(pregnancyOutcome);
        pregnancyResult.setType("sample-result");
    }

    // create a visit at the given location
    private Visit generateVisit(Location location) {
        Visit visit = new Visit();
        setAuditableFields(visit);
        setCollectedFields(visit);

        visit.setExtId(location.getExtId() + "-visit");
        visit.setLocation(location);
        visit.setVisitDate(ZonedDateTime.now());

        return visitRepository.save(visit);
    }

    private void setAuditableFields(AuditableEntity entity) {
        User user = userService.getUnknownEntity();
        ZonedDateTime now = ZonedDateTime.now();

        //Check to see if we're creating or updating the entity
        if (null == entity.getInsertDate()) {
            entity.setInsertDate(now);
        }

        if (null == entity.getInsertBy()) {
            entity.setInsertBy(user);
        }

        entity.setLastModifiedDate(now);
        entity.setLastModifiedBy(user);
    }

    private void setCollectedFields(AuditableCollectedEntity entity) {
        entity.setCollectedBy(fieldWorkerService.getUnknownEntity());
        entity.setCollectionDateTime(ZonedDateTime.now());
    }

}
