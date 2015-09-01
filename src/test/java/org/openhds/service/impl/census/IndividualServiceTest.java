package org.openhds.service.impl.census;

import org.junit.Test;
import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.ProjectCode;
import org.openhds.domain.model.census.Individual;
import org.openhds.service.AuditableExtIdServiceTest;
import org.openhds.service.impl.ProjectCodeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Wolfe on 7/13/2015.
 */
public class IndividualServiceTest extends AuditableExtIdServiceTest<Individual, IndividualService> {

    public static final String FIELDWORKER_ID = "feldwarker";
    public static final String SOCIALGROUP_ID = "suculgrup";
    public static final String LOCATION_ID = "lucutun";
    public static final String INDIVIDUAL_ID = "induuvudu";
    public static final String RELATIONSHIP_ID = "relutuup";
    public static final String RESIDENCY_ID = "rsususndy";
    public static final String MEMBERSHIP_ID = "mumburshub";

    @Autowired
    private LocationHierarchyService locationHierarchyService;
    @Autowired
    private RelationshipService relationshipService;
    @Autowired
    private ResidencyService residencyService;
    @Autowired
    private MembershipService membershipService;
    @Autowired
    private SocialGroupService socialGroupService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private ProjectCodeService projectCodeService;


    @Override
    protected Individual makeInvalidEntity() {
        return new Individual();
    }

    @Override
    @Autowired
    protected void initialize(IndividualService service) {
        this.service = service;
    }

    @Test
    public void recordWithExistingReferences() {

        //Grab a valid entity
        Individual individual = makeValidEntity("validName", "validId");

        FieldWorker fieldWorker = individual.getCollectedBy();
        individual.setCollectedBy(null);

        // pass it all into the record method
        individual = service.recordIndividual(individual, fieldWorker.getUuid());


        //Check that the originals match the ones pulled out from findOrMakePlaceholder()
        assertNotNull(individual.getCollectedBy());
        assertEquals(individual.getCollectedBy(), fieldWorker);

    }

    @Test
    public void recordWithNonexistentReferences(){

        //Make a new entity with no references
        Individual individual = makeValidEntity("validName", "validId");
        individual.setCollectedBy(null);

        //Pass it in with new reference uuids
        individual = service.recordIndividual(individual, FIELDWORKER_ID);

        //check that they were persisted
        assertNotNull(individual.getCollectedBy());
        assertEquals(individual.getCollectedBy().getUuid(), FIELDWORKER_ID);

    }

    @Test
    public void complexRecordValid(){

        Individual fancyIndividual = makeValidEntity("FancyMan", "FancyId");
        String relationshipType = projectCodeService.findByCodeGroup(ProjectCode.RELATIONSHIP_TYPE).get(0).getCodeValue();
        ZonedDateTime collectionDateTime = ZonedDateTime.now().plusHours(1);
        ZonedDateTime recordTime = ZonedDateTime.now().minusHours(1);

        service.recordIndividual(fancyIndividual,
            recordTime,
            relationshipType,
            INDIVIDUAL_ID,
            RELATIONSHIP_ID,
            LOCATION_ID,
            SOCIALGROUP_ID,
            FIELDWORKER_ID,
            "jo",
            "jill",
            MEMBERSHIP_ID,
            RESIDENCY_ID);

        assertEquals(service.findOne(fancyIndividual.getUuid()), fancyIndividual);
        assertEquals(membershipService.findOne(MEMBERSHIP_ID).getStartType(), "individualRegistration");
        assertEquals(residencyService.findOne(RESIDENCY_ID).getStartType(), "individualRegistration");
        assertEquals(relationshipService.findOne(RELATIONSHIP_ID).getRelationshipType(), relationshipType);


    }
}
