package org.openhds.resource.controller.census;

import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.ProjectCode;
import org.openhds.domain.model.census.*;
import org.openhds.domain.model.update.*;
import org.openhds.domain.util.ShallowCopier;
import org.openhds.domain.util.VisitEvents;
import org.openhds.repository.queries.QueryValue;
import org.openhds.repository.results.EntityIterator;
import org.openhds.resource.contract.AuditableExtIdRestController;
import org.openhds.resource.registration.census.IndividualHouseholdRegistration;
import org.openhds.resource.registration.census.IndividualRegistration;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.openhds.service.contract.AbstractUuidService;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.ProjectCodeService;
import org.openhds.service.impl.census.IndividualService;
import org.openhds.service.impl.census.MembershipService;
import org.openhds.service.impl.census.RelationshipService;
import org.openhds.service.impl.census.ResidencyService;
import org.openhds.service.impl.update.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by Wolfe on 7/13/2015.
 */
@RestController
@RequestMapping("/individuals")
@ExposesResourceFor(Individual.class)
public class IndividualRestController extends AuditableExtIdRestController<Individual, IndividualRegistration, IndividualService> {

    public static final String REL_HOUSEHOLD = "household";

    public static final String REL_HOUSEHOLD_Sample = "householdSampleRegistration";

    private final FieldWorkerService fieldWorkerService;

    private final ProjectCodeService projectCodeService;

    private final IndividualService individualService;

    private final ResidencyService residencyService;

    private final MembershipService membershipService;

    private final RelationshipService relationshipService;

    private final InMigrationService inMigrationService;

    private final OutMigrationService outMigrationService;

    private final DeathService deathService;

    private final PregnancyObservationService pregnancyObservationService;

    private final PregnancyOutcomeService pregnancyOutcomeService;

    @Autowired
    public IndividualRestController(IndividualService individualService, FieldWorkerService fieldWorkerService,
                                    ProjectCodeService projectCodeService, ResidencyService residencyService,
                                    MembershipService membershipService, RelationshipService relationshipService,
                                    InMigrationService inMigrationService, OutMigrationService outMigrationService,
                                    DeathService deathService, PregnancyObservationService pregnancyObservationService,
                                    PregnancyOutcomeService pregnancyOutcomeService) {

        super(individualService);
        this.individualService = individualService;
        this.fieldWorkerService = fieldWorkerService;
        this.projectCodeService = projectCodeService;
        this.residencyService = residencyService;
        this.membershipService = membershipService;
        this.relationshipService = relationshipService;
        this.inMigrationService = inMigrationService;
        this.outMigrationService = outMigrationService;
        this.deathService = deathService;
        this.pregnancyObservationService = pregnancyObservationService;
        this.pregnancyOutcomeService = pregnancyOutcomeService;
    }

    @Override
    public void addCollectionResourceLinks(ResourceSupport resource) {
        super.addCollectionResourceLinks(resource);

        resource.add(linkTo(methodOn(this.getClass())
                .insertHousehold(null, null))
                .withRel(REL_HOUSEHOLD));
        resource.add(withTemplateParams(linkTo(methodOn(this.getClass())
                        .readHouseholdSample(null, null))
                        .withRel(REL_SAMPLE),
                "id", "name"));
    }

    @Override
    protected IndividualRegistration makeSampleRegistration(Individual entity) {
        IndividualRegistration registration = new IndividualRegistration();
        registration.setIndividual(entity);
        return registration;
    }

    public IndividualHouseholdRegistration getHouseholdSampleRegistration(Individual entity) {
        IndividualHouseholdRegistration registration = new IndividualHouseholdRegistration();
        registration.setIndividual(entity);
        registration.setRelationToHead(projectCodeService.findByCodeGroup(ProjectCode.RELATIONSHIP_TYPE).get(0).getCodeValue());
        registration.setHeadOfHouseholdUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setRelationshipUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setLocationUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setSocialGroupUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setMotherUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setFatherUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setMembershipUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setResidencyUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setCollectedByUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setRegistrationDateTime(ZonedDateTime.now());
        registration.setRegistrationSystemName("systemName");
        registration.setRegistrationVersion(1);
        registration.setRegistrationVersionName("versionName");
        return registration;
    }

    @Override
    protected Individual register(IndividualRegistration registration) {
        checkRegistrationFields(registration.getIndividual(), registration);
        return individualService.recordIndividual(registration.getIndividual(), registration.getCollectedByUuid());
    }

    @Override
    protected Individual register(IndividualRegistration registration, String id) {
        registration.getIndividual().setUuid(id);
        return register(registration);
    }

    @RequestMapping(value = "/household", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    protected Resource insertHousehold(@RequestBody IndividualHouseholdRegistration registration, HttpServletResponse response) {
        Individual entity = register(registration);
        addLocationHeader(response, entity);
        return entityLinkAssembler.toResource(entity);
    }

    @RequestMapping(value = "/household/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.CREATED)
    protected Resource replaceHousehold(@RequestBody IndividualHouseholdRegistration registration, @PathVariable String id) {
        Individual entity = register(registration, id);
        return entityLinkAssembler.toResource(entity);
    }

    @RequestMapping(value = "/household/sampleRegistration", method = RequestMethod.GET)
    public IndividualHouseholdRegistration readHouseholdSample(@RequestParam(required = false, defaultValue = AbstractUuidService.UNKNOWN_ENTITY_UUID) String id,
                                                               @RequestParam(required = false, defaultValue = "unknown") String name) {
        Individual entity = ShallowCopier.makeShallowCopy(individualService.makePlaceHolder(id, name), null);
        return getHouseholdSampleRegistration(entity);
    }

    protected Individual register(IndividualHouseholdRegistration registration) {
        checkRegistrationFields(registration.getIndividual(), registration);
        return individualService.recordIndividual(
                registration.getIndividual(),
                registration.getRegistrationDateTime(),
                registration.getRelationToHead(),
                registration.getHeadOfHouseholdUuid(),
                registration.getRelationshipUuid(),
                registration.getLocationUuid(),
                registration.getSocialGroupUuid(),
                registration.getCollectedByUuid(),
                registration.getMotherUuid(),
                registration.getFatherUuid(),
                registration.getMembershipUuid(),
                registration.getResidencyUuid());
    }

    protected Individual register(IndividualHouseholdRegistration registration, String id) {
        registration.getIndividual().setUuid(id);
        return register(registration);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public List<Individual> search(@RequestParam Map<String, String> fields) {
        List<QueryValue> collect = fields.entrySet().stream().map(f -> new QueryValue(f.getKey(), f.getValue())).collect(Collectors.toList());
        QueryValue[] queryFields = {};
        queryFields = collect.toArray(queryFields);
        return individualService.findByMultipleValues(new Sort("firstName"), queryFields).toList();
    }

    @RequestMapping(value = "/findByLocation", method = RequestMethod.GET)
    public List<Individual> findByLocation(@RequestParam String locationUuid) {
        EntityIterator<Residency> residencies = residencyService.findAll(new Sort("uuid"));
        List<Residency> filteredResidencies = new ArrayList<>();
        for (Residency residency: residencies) {
            if(residency.getLocation().getUuid().equals(locationUuid)) {
                filteredResidencies.add(residency);
            }
        }
        // TODO: This executes a query for each uuid. It should be batched.
        return filteredResidencies.stream()
                .map(residency -> residency.getIndividual().getUuid())
                .map(uuid -> individualService.findOne(uuid))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/findBySocialGroup", method = RequestMethod.GET)
    public List<Individual> findBySocialGroup(@RequestParam String socialGroupUuid) {
        EntityIterator<Membership> memberships = membershipService.findAll(new Sort("uuid"));
        List<Membership> filteredMemberships = new ArrayList<>();
        for (Membership membership: memberships) {
            if(membership.getSocialGroup().getUuid().equals(socialGroupUuid)) {
                filteredMemberships.add(membership);
            }
        }
        // TODO: This executes a query for each uuid. It should be batched.
        return filteredMemberships.stream()
                .map(membership -> membership.getIndividual().getUuid())
                .map(uuid -> individualService.findOne(uuid))
                .collect(Collectors.toList());
    }


    @RequestMapping(value = "/getResidencies", method = RequestMethod.GET)
    public List<Residency> getResidencies(@RequestParam String individualUuid) {
        EntityIterator<Residency> residencies = residencyService.findAll(new Sort("uuid"));

        List<Residency> filteredResidencies = new ArrayList<>();
        for (Residency residency: residencies) {
            if(residency.getIndividual().getUuid().equals(individualUuid)) {
                filteredResidencies.add(residency);
            }
        }
        return filteredResidencies;
    }

    @RequestMapping(value = "/getMemberships", method = RequestMethod.GET)
    public List<Membership> getMembershipsForIndividual(@RequestParam String individualUuid) {
        EntityIterator<Membership> memberships = membershipService.findAll(new Sort("uuid"));

        List<Membership> filteredMemberships = new ArrayList<>();
        for (Membership membership: memberships) {
            if(membership.getIndividual().getUuid().equals(individualUuid)) {
                filteredMemberships.add(membership);
            }
        }
        return filteredMemberships;
    }

    @RequestMapping(value = "/getRelationships", method = RequestMethod.GET)
    public List<Relationship> getRelationshipsForIndividual(@RequestParam String individualUuid) {
        EntityIterator<Relationship> relationships = relationshipService.findAll(new Sort("uuid"));

        List<Relationship> filteredRelationships = new ArrayList<>();
        for (Relationship relationship: relationships) {
            if(     relationship.getIndividualA().getUuid().equals(individualUuid) ||
                    relationship.getIndividualB().getUuid().equals(individualUuid) ) {
                filteredRelationships.add(relationship);
            }
        }
        return filteredRelationships;
    }

    @RequestMapping(value = "/getEvents", method = RequestMethod.GET)
    public VisitEvents getEvents(@RequestParam String individualUuid) {

        Individual indiv = individualService.findOne(individualUuid);

        // InMigrations
        EntityIterator<InMigration> inMigrations = inMigrationService.findAll(new Sort("uuid"));
        List<InMigration> filteredInMigrations = new ArrayList<>();
        for (InMigration inMigration: inMigrations) {
            if(     inMigration.getIndividual().getUuid().equals(individualUuid) ) {
                filteredInMigrations.add(inMigration);
            }
        }
        // OutMigrations
        EntityIterator<OutMigration> outMigrations = outMigrationService.findAll(new Sort("uuid"));
        List<OutMigration> filteredOutMigrations = new ArrayList<>();
        for (OutMigration outMigration: outMigrations) {
            if(     outMigration.getIndividual().getUuid().equals(individualUuid) ) {
                filteredOutMigrations.add(outMigration);
            }
        }
        // Deaths
        EntityIterator<Death> deaths = deathService.findAll(new Sort("uuid"));
        List<Death> filteredDeaths = new ArrayList<>();
        for (Death death: deaths) {
            if(     death.getIndividual().getUuid().equals(individualUuid) ) {
                filteredDeaths.add(death);
            }
        }

        List<PregnancyOutcome> filteredPregnancyOutcomes = new ArrayList<>();
        List<PregnancyObservation> filteredPregnancyObservations = new ArrayList<>();

        if(!indiv.getGender().equals(("MALE"))){
            // Pregnancy Observations
            EntityIterator<PregnancyObservation> pregnancyObservations = pregnancyObservationService.findAll(new Sort("uuid"));
            for (PregnancyObservation pregnancyObservation: pregnancyObservations) {
                if(     pregnancyObservation.getMother().getUuid().equals(individualUuid) ) {
                    filteredPregnancyObservations.add(pregnancyObservation);
                }
            }
            // Pregnancy Outcomes
            EntityIterator<PregnancyOutcome> pregnancyOutcomes = pregnancyOutcomeService.findAll(new Sort("uuid"));
            for (PregnancyOutcome pregnancyOutcome: pregnancyOutcomes) {
                if(     pregnancyOutcome.getMother().getUuid().equals(individualUuid) ) {
                    filteredPregnancyOutcomes.add(pregnancyOutcome);
                }
            }
        }

        VisitEvents events = new VisitEvents(filteredInMigrations, filteredOutMigrations, filteredDeaths,
                filteredPregnancyObservations, filteredPregnancyOutcomes);
        return events;

    }

    @RequestMapping(value = "/findByFieldWorker", method = RequestMethod.GET)
    public List<Individual> findByFieldWorker(@RequestParam String fieldWorkerId) {
        EntityIterator<FieldWorker> fieldWorkers = fieldWorkerService.findByFieldWorkerId(new Sort("fieldWorkerId"), fieldWorkerId);

        return StreamSupport.stream(fieldWorkers.spliterator(), false)
                .flatMap(fw -> StreamSupport.stream(individualService.findByCollectedBy(new Sort("uuid"), fw).spliterator(), false))
                .collect(Collectors.toList());
    }

}



