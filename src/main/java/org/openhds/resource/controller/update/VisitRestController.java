package org.openhds.resource.controller.update;

import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.update.*;
import org.openhds.repository.results.EntityIterator;
import org.openhds.resource.contract.AuditableExtIdRestController;
import org.openhds.resource.registration.update.VisitRegistration;
import org.openhds.service.contract.AbstractUuidService;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.LocationService;
import org.openhds.service.impl.update.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/visits")
@ExposesResourceFor(Visit.class)
public class VisitRestController extends AuditableExtIdRestController<
        Visit,
        VisitRegistration,
        VisitService> {

    private final VisitService visitService;

    private final LocationService locationService;

    private final FieldWorkerService fieldWorkerService;

    private final InMigrationService inMigrationService;

    private final OutMigrationService outMigrationService;

    private final DeathService deathService;

    private final PregnancyObservationService pregnancyObservationService;

    private final PregnancyOutcomeService pregnancyOutcomeService;

    @Autowired
    public VisitRestController(VisitService visitService,
                               LocationService locationService,
                               FieldWorkerService fieldWorkerService,
                               InMigrationService inMigrationService, OutMigrationService outMigrationService,
                               DeathService deathService, PregnancyObservationService pregnancyObservationService,
                               PregnancyOutcomeService pregnancyOutcomeService) {
        super(visitService);
        this.visitService = visitService;
        this.locationService = locationService;
        this.fieldWorkerService = fieldWorkerService;
        this.inMigrationService = inMigrationService;
        this.outMigrationService = outMigrationService;
        this.deathService = deathService;
        this.pregnancyObservationService = pregnancyObservationService;
        this.pregnancyOutcomeService = pregnancyOutcomeService;


    }

    @Override
    protected VisitRegistration makeSampleRegistration(Visit entity) {
        VisitRegistration registration = new VisitRegistration();
        registration.setVisit(entity);
        registration.setLocationUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        return registration;
    }

    @Override
    protected Visit register(VisitRegistration registration) {
        checkRegistrationFields(registration.getVisit(), registration);
        return visitService.recordVisit(registration.getVisit(),
                registration.getLocationUuid(),
                registration.getCollectedByUuid());
    }

    @Override
    protected Visit register(VisitRegistration registration, String id) {
        registration.getVisit().setUuid(id);
        return register(registration);
    }

    @RequestMapping(value = "/findByFieldWorker", method = RequestMethod.GET)
    public List<Visit> findByFieldWorker(@RequestParam String fieldWorkerId) {
        EntityIterator<FieldWorker> fieldWorkers = fieldWorkerService.findByFieldWorkerId(new Sort("fieldWorkerId"), fieldWorkerId);

        // This is hacky because we get back an entity iterator and it's not readily streamable
        List <Visit> results = new ArrayList<>();

        for(FieldWorker fw: fieldWorkers) {
            EntityIterator<Visit> visits = visitService.findByCollectedBy(new Sort("uuid"), fw);
            for(Visit visit: visits) {
                results.add(visit);
            }
        }
        return results;
    }

    private class EventStructure {
        public List<InMigration> inMigrations;
        public List<OutMigration> outMigrations;
        public List<Death> deaths;
        public List<PregnancyObservation> pregnancyObservations;
        public List<PregnancyOutcome> pregnancyOutcomes;

        // male
        private EventStructure(List<InMigration> inMigrations, List<OutMigration> outMigrations,
                               List<Death> deaths){
            this.inMigrations = inMigrations;
            this.outMigrations = outMigrations;
            this.deaths = deaths;
        }

        // female
        private EventStructure(List<InMigration> inMigrations, List<OutMigration> outMigrations,
                               List<Death> deaths, List<PregnancyObservation> pregnancyObservations,
                               List<PregnancyOutcome> pregnancyOutcomes ){
            this.inMigrations = inMigrations;
            this.outMigrations = outMigrations;
            this.deaths = deaths;
            this.pregnancyObservations = pregnancyObservations;
            this.pregnancyOutcomes = pregnancyOutcomes;

        }

    }


    @RequestMapping(value = "/getEvents", method = RequestMethod.GET)
    public EventStructure getEvents(@RequestParam String visitUuid) {


        // InMigrations
        EntityIterator<InMigration> inMigrations = inMigrationService.findAll(new Sort("uuid"));
        List<InMigration> filteredInMigrations = new ArrayList<>();
        for (InMigration inMigration: inMigrations) {
            if(     inMigration.getVisit().getUuid().equals(visitUuid) ) {
                filteredInMigrations.add(inMigration);
            }
        }

        // OutMigrations
        EntityIterator<OutMigration> outMigrations = outMigrationService.findAll(new Sort("uuid"));
        List<OutMigration> filteredOutMigrations = new ArrayList<>();
        for (OutMigration outMigration: outMigrations) {
            if(     outMigration.getVisit().getUuid().equals(visitUuid)) {
                filteredOutMigrations.add(outMigration);
            }
        }

        // Deaths
        EntityIterator<Death> deaths = deathService.findAll(new Sort("uuid"));
        List<Death> filteredDeaths = new ArrayList<>();
        for (Death death: deaths) {
            if(     death.getVisit().getUuid().equals(visitUuid) ) {
                filteredDeaths.add(death);
            }
        }


        // Pregnancy Observations
        EntityIterator<PregnancyObservation> pregnancyObservations = pregnancyObservationService.findAll(new Sort("uuid"));
        List<PregnancyObservation> filteredPregnancyObservations = new ArrayList<>();
        for (PregnancyObservation pregnancyObservation: pregnancyObservations) {
            if(     pregnancyObservation.getVisit().getUuid().equals(visitUuid) ) {
                filteredPregnancyObservations.add(pregnancyObservation);
            }
        }

        // Pregnancy Outcomes
        EntityIterator<PregnancyOutcome> pregnancyOutcomes = pregnancyOutcomeService.findAll(new Sort("uuid"));
        List<PregnancyOutcome> filteredPregnancyOutcomes = new ArrayList<>();
        for (PregnancyOutcome pregnancyOutcome: pregnancyOutcomes) {
            if(     pregnancyOutcome.getVisit().getUuid().equals(visitUuid) ) {
                filteredPregnancyOutcomes.add(pregnancyOutcome);
            }
        }

        return new EventStructure(filteredInMigrations, filteredOutMigrations, filteredDeaths,
                filteredPregnancyObservations, filteredPregnancyOutcomes);
        


    }


}
