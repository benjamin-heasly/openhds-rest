package org.openhds.service.impl.census;

import org.openhds.domain.model.ProjectCode;
import org.openhds.domain.model.census.*;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.census.IndividualRepository;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.openhds.service.impl.ProjectCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * Created by Wolfe on 7/13/2015.
 */
@Service
public class IndividualService extends AbstractAuditableExtIdService<Individual, IndividualRepository>{

    @Autowired
    private SocialGroupService socialGroupService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private ResidencyService residencyService;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    public IndividualService(IndividualRepository repository) {
        super(repository);
    }

    @Override
    public Individual makePlaceHolder(String id, String name) {
        Individual individual = new Individual();
        individual.setUuid(id);
        individual.setFirstName(name);
        individual.setExtId(name);

        initPlaceHolderCollectedFields(individual);

        return individual;
    }

    /*
    * This method is where the magic happens. The creation of individuals and locations has been the primary areas of
    * concern for the implementations of OpenHDS we have worked on and this method shows why. Most of the census
    * entities touch the individual or are touched by it in some way, even other individuals (i.e. relationships).
    *
    * Right now the method is more of a draft than anything else, none of the logic is broken out because it's hard
    * to determine what makes sense to break out and what doesn't. Originally we planned on breaking out the 'domain'
    * bits (i.e. the creation of the side effect entities) but this didn't really seem to make any sense when I actually
    * started writing code and thinking about it. Other than for organizations sake. The logic in that broken out
    * method wouldn't be used anywhere other than where it is being called now.
    *
    * I have added some broad 2do comments that capture some of the struggles and questions I ran into while thinking
    * about the sequence of events that take place in this method.
    * */
    //TODO: Is there a valid use-case for an overloaded of this method without all the extra params?
    public Individual recordIndividual(Individual individual,
                                       String socialGroupUuid,
                                       String locationUuid,
                                       String residencyUuid,
                                       String membershipUuid,
                                       String relationshipUuid,
                                       String fieldWorkerId) {

        individual.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));

        //Create the individual first, so that everything after is guaranteed to point at a real entity
        individual = createOrUpdate(individual);

        //TODO: Is the socialgroup created here?
        //TODO: Do we make two paths depending on if it's a member or head of household?
        SocialGroup socialGroup = socialGroupService.findOrMakePlaceHolder(socialGroupUuid);
        Location location = locationService.findOrMakePlaceHolder(locationUuid);

        //TODO: How are we going to start using project codes?
        //TODO: Do these side effect entities need to call findOrMakePlaceholder?
        /* It's my understanding that these entities are only ever created when an individual is created
         this means that a placeholder doesn't exist yet. However, there is really no harm in calling
         the method and overwriting whatever it returns, even if it's NOT a placeholder (e.g. some other
         implementation supports the explicit creation of things like memberships outside of the context of
         creating an individual... Should we be checking if the entity is return is a placeholder or not? if so, how?
         There is currently no normalized way of checking all entities 'types' (placeholder, unknown, normal, etc). */
        Residency residency = residencyService.findOrMakePlaceHolder(residencyUuid);
        residency.setStartDate(ZonedDateTime.now());
        residency.setStartType("PROJECT-CODE-GOES-HERE");
        residency.setIndividual(individual);
        residency.setLocation(location);
        residencyService.createOrUpdate(residency);

        Membership membership = membershipService.findOrMakePlaceHolder(membershipUuid);
        membership.setStartDate(ZonedDateTime.now());
        membership.setStartType("PROJECT-CODE-GOES-HERE");
        membership.setIndividual(individual);
        membership.setSocialGroup(socialGroup);
        //TODO: Is relationship to group head still a valid field of a membership?


        Relationship relationship = relationshipService.findOrMakePlaceHolder(relationshipUuid);
        //TODO: what relationship do we create here? Relationship to head of household?
        // If it's the head of household, how do we get it?


        return createOrUpdate(individual);
    }

  @Override
  public void validate(Individual entity, ErrorLog errorLog) {
    super.validate(entity, errorLog);

  }
}
