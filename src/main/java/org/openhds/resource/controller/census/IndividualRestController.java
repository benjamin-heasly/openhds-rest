package org.openhds.resource.controller.census;

import org.openhds.domain.model.census.Individual;
import org.openhds.resource.contract.AuditableExtIdRestController;
import org.openhds.resource.registration.census.IndividualHouseholdRegistration;
import org.openhds.resource.registration.census.IndividualRegistration;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by Wolfe on 7/13/2015.
 */
@RestController
@RequestMapping("/individuals")
@ExposesResourceFor(Individual.class)
public class IndividualRestController extends AuditableExtIdRestController<Individual,IndividualRegistration, IndividualService>{

    public static final String REL_HOUSEHOLD = "household";

    private final FieldWorkerService fieldWorkerService;

    private final IndividualService individualService;

    @Autowired
    public IndividualRestController(IndividualService individualService, FieldWorkerService fieldWorkerService) {
        super(individualService);
        this.individualService = individualService;
        this.fieldWorkerService = fieldWorkerService;
    }

    @Override
    public void addCollectionResourceLinks(ResourceSupport resource) {
        super.addCollectionResourceLinks(resource);

        resource.add(linkTo(methodOn(this.getClass())
                .insertHousehold(null, null))
                .withRel(REL_HOUSEHOLD));
    }

    @Override
    protected IndividualRegistration makeSampleRegistration(Individual entity) {
        IndividualRegistration registration = new IndividualRegistration();
        registration.setIndividual(entity);
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

    protected Individual register(IndividualHouseholdRegistration registration) {
        return individualService.recordIndividual(
                registration.getIndividual(),
                registration.getRegistrationDateTime(),
                registration.getRelationToHead(),
                registration.getHeadOfHouseholdId(),
                registration.getRelationshipId(),
                registration.getLocationId(),
                registration.getSocialGroupId(),
                registration.getCollectedByUuid(),
                registration.getMotherId(),
                registration.getFatherId(),
                registration.getMembershipId(),
                registration.getResidencyId());
    }

    protected Individual register(IndividualHouseholdRegistration registration, String id) {
        registration.getIndividual().setUuid(id);
        return register(registration);
    }

}
