package org.openhds.resource.controller.census;

import org.junit.Test;
import org.openhds.domain.model.ProjectCode;
import org.openhds.domain.model.census.Individual;
import org.openhds.resource.contract.AuditableExtIdRestControllerTest;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.registration.census.IndividualHouseholdRegistration;
import org.openhds.resource.registration.census.IndividualRegistration;
import org.openhds.service.impl.ProjectCodeService;
import org.openhds.service.impl.census.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.openhds.service.contract.AbstractUuidService.UNKNOWN_ENTITY_UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Created by Wolfe on 7/13/2015.
 */
public class IndividualRestControllerTest extends AuditableExtIdRestControllerTest<Individual, IndividualService, IndividualRestController> {

    @Autowired
    private ProjectCodeService projectCodeService;

    @Override
    @Autowired
    protected void initialize(IndividualService service, IndividualRestController controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected Individual makeInvalidEntity() {
        return new Individual();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(Individual entity, String name, String id) {
        assertNotNull(entity);

        Individual savedIndividual= service.findOne(id);
        assertNotNull(savedIndividual);

        assertEquals(id, savedIndividual.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getFirstName(), savedIndividual.getFirstName());
    }

    // POST household

    protected String getHouseholdUrl() {
        return getResourceUrl() + "household/";
    }

    @Test
    @WithUserDetails
    public void postNewHouseholdJson() throws Exception {
        Individual entity = makeValidEntity("test household registration", "test id");

        MvcResult mvcResult = this.mockMvc.perform(post(getHouseholdUrl())
                .contentType(regularJson)
                .content(toJson(makeHouseholdRegistration(entity))))
                .andExpect(status().isCreated())
                .andReturn();

        Individual responseEntity = fromJson(controller.getEntityClass(), mvcResult.getResponse().getContentAsString());
        verifyEntityExistsWithNameAndId(responseEntity, "test registration", entity.getUuid());
    }

    @Test
    @WithUserDetails
    public void postNewHouseholdXml() throws Exception {
        Individual entity = makeValidEntity("test household registration", "test id");

        MvcResult mvcResult = this.mockMvc.perform(post(getHouseholdUrl())
                .contentType(regularXml)
                .accept(regularXml)
                .content(toXml(makeHouseholdRegistration(entity))))
                .andExpect(status().isCreated())
                .andReturn();

        Individual responseEntity = fromXml(controller.getEntityClass(), mvcResult.getResponse().getContentAsString());
        verifyEntityExistsWithNameAndId(responseEntity, "test registration", entity.getUuid());
    }

    // PUT household

    @Test
    @WithUserDetails
    public void putNewHouseholdJson() throws Exception {
        final String uuid = UUID.randomUUID().toString();
        Individual entity = makeValidEntity("test household registration", "ignored id");

        MvcResult mvcResult = this.mockMvc.perform(put(getHouseholdUrl() + uuid)
                .contentType(regularJson)
                .content(toJson(makeHouseholdRegistration(entity))))
                .andExpect(status().isCreated())
                .andReturn();

        Individual responseEntity = fromJson(controller.getEntityClass(), mvcResult.getResponse().getContentAsString());
        verifyEntityExistsWithNameAndId(responseEntity, "test registration", uuid);
    }

    @Test
    @WithUserDetails
    public void putNewHouseholdXml() throws Exception {
        final String uuid = UUID.randomUUID().toString();
        Individual entity = makeValidEntity("test household registration", "ignored id");

        MvcResult mvcResult = this.mockMvc.perform(put(getHouseholdUrl() + uuid)
                .contentType(regularXml)
                .accept(regularXml)
                .content(toXml(makeHouseholdRegistration(entity))))
                .andExpect(status().isCreated())
                .andReturn();

        Individual responseEntity = fromXml(controller.getEntityClass(), mvcResult.getResponse().getContentAsString());
        verifyEntityExistsWithNameAndId(responseEntity, "test registration", uuid);
    }

    protected IndividualHouseholdRegistration makeHouseholdRegistration(Individual entity) {
        IndividualHouseholdRegistration registration = new IndividualHouseholdRegistration();

        ZonedDateTime recordTime = ZonedDateTime.now().minusHours(1);
        String relationshipType = projectCodeService.findByCodeGroup(ProjectCode.RELATIONSHIP_TYPE).get(0).getCodeValue();

        registration.setIndividual(entity);
        registration.setCollectedByUuid(fieldWorkerService.findAll(UUID_SORT).toList().get(0).getUuid());
        registration.setRegistrationDateTime(recordTime);
        registration.setRelationToHead(relationshipType);
        registration.setHeadOfHouseholdId(UNKNOWN_ENTITY_UUID);
        registration.setRelationshipId(UNKNOWN_ENTITY_UUID);
        registration.setLocationId(UNKNOWN_ENTITY_UUID);
        registration.setSocialGroupId(UNKNOWN_ENTITY_UUID);
        registration.setFatherId(UNKNOWN_ENTITY_UUID);
        registration.setMotherId(UNKNOWN_ENTITY_UUID);
        registration.setMembershipId(UNKNOWN_ENTITY_UUID);
        registration.setResidencyId(UNKNOWN_ENTITY_UUID);

        return registration;
    }
}
