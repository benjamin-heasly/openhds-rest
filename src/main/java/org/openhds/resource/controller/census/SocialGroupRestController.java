package org.openhds.resource.controller.census;

import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.Location;
import org.openhds.domain.model.census.Membership;
import org.openhds.domain.model.census.SocialGroup;
import org.openhds.domain.util.ExtIdGenerator;
import org.openhds.repository.queries.QueryValue;
import org.openhds.repository.results.EntityIterator;
import org.openhds.resource.contract.AuditableExtIdRestController;
import org.openhds.resource.registration.census.SocialGroupRegistration;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.MembershipService;
import org.openhds.service.impl.census.SocialGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/socialGroups")
@ExposesResourceFor(SocialGroup.class)
public class SocialGroupRestController extends AuditableExtIdRestController<
        SocialGroup,
        SocialGroupRegistration,
        SocialGroupService> {

    private final SocialGroupService socialGroupService;

    private final FieldWorkerService fieldWorkerService;

    private final MembershipService membershipService;

    @Autowired
    public SocialGroupRestController(SocialGroupService socialGroupService,
                                     FieldWorkerService fieldWorkerService,
                                     MembershipService membershipService,
                                     ExtIdGenerator extIdGenerator) {
        super(socialGroupService, extIdGenerator);
        this.socialGroupService = socialGroupService;
        this.fieldWorkerService = fieldWorkerService;
        this.membershipService = membershipService;
    }

    @Override
    protected SocialGroupRegistration makeSampleRegistration(SocialGroup entity) {
        SocialGroupRegistration registration = new SocialGroupRegistration();
        registration.setSocialGroup(entity);
        return registration;

    }

    @Override
    protected SocialGroup register(SocialGroupRegistration registration) {
        checkRegistrationFields(registration.getSocialGroup(), registration);
        return socialGroupService.recordSocialGroup(registration.getSocialGroup(), registration.getCollectedByUuid());
    }

    @Override
    protected SocialGroup register(SocialGroupRegistration registration, String id) {
        registration.getSocialGroup().setUuid(id);
        return register(registration);
    }

    @RequestMapping(value = "/getMemberships", method = RequestMethod.GET)
    public List<Membership> getMembershipsForIndividual(@RequestParam String socialGroupUuid) {
        EntityIterator<Membership> memberships = membershipService.findAll(new Sort("uuid"));

        List<Membership> filteredMemberships = new ArrayList<>();
        for (Membership membership: memberships) {
            if(membership.getSocialGroup().getUuid().equals(socialGroupUuid)) {
                filteredMemberships.add(membership);
            }
        }
        return filteredMemberships;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public List<SocialGroup> search(@RequestParam Map<String, String> fields) {
        List<QueryValue> collect = fields.entrySet().stream().map(f -> new QueryValue(f.getKey(), f.getValue())).collect(Collectors.toList());
        QueryValue[] queryFields = {};
        queryFields = collect.toArray(queryFields);
        return socialGroupService.findByMultipleValues(new Sort("groupName"), queryFields).toList();
    }

    @RequestMapping(value = "/findByFieldWorker", method = RequestMethod.GET)
    public List<SocialGroup> findByFieldWorker(@RequestParam String fieldWorkerId) {
        EntityIterator<FieldWorker> fieldWorkers = fieldWorkerService.findByFieldWorkerId(new Sort("fieldWorkerId"), fieldWorkerId);

        return StreamSupport.stream(fieldWorkers.spliterator(), false)
                .flatMap(fw -> StreamSupport.stream(socialGroupService.findByCollectedBy(new Sort("uuid"), fw).spliterator(), false))
                .collect(Collectors.toList());
    }
}
