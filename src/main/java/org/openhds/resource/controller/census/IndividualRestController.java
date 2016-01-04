package org.openhds.resource.controller.census;

import org.openhds.domain.model.ProjectCode;
import org.openhds.domain.model.census.Individual;
import org.openhds.domain.util.ShallowCopier;
import org.openhds.resource.contract.AuditableExtIdRestController;
import org.openhds.resource.registration.census.IndividualHouseholdRegistration;
import org.openhds.resource.registration.census.IndividualRegistration;
import org.openhds.service.contract.AbstractUuidService;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.ProjectCodeService;
import org.openhds.service.impl.census.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;

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

    @Autowired
    public IndividualRestController(IndividualService individualService, FieldWorkerService fieldWorkerService, ProjectCodeService projectCodeService) {
        super(individualService);
        this.individualService = individualService;
        this.fieldWorkerService = fieldWorkerService;
        this.projectCodeService = projectCodeService;
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

}
