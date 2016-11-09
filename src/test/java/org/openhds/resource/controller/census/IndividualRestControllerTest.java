package org.openhds.resource.controller.census;

import org.junit.Test;
import org.openhds.domain.model.census.Individual;
import org.openhds.resource.contract.AuditableExtIdRestControllerTest;
import org.openhds.service.impl.ProjectCodeService;
import org.openhds.service.impl.census.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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
                .content(toJson(controller.getHouseholdSampleRegistration(entity))))
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
                .content(toXml(controller.getHouseholdSampleRegistration(entity))))
                .andExpect(status().isCreated())
                .andReturn();

        Individual responseEntity = fromXml(controller.getEntityClass(), mvcResult.getResponse().getContentAsString());
        verifyEntityExistsWithNameAndId(responseEntity, "test registration", entity.getUuid());
    }

    @Test
    @WithUserDetails
    public void postHouseholdSampleRegistrationJson() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(getHouseholdUrl() + "/sampleRegistration")
                .param("id", "sampleHouseholdPostId")
                .param("name", "sampleHouseholdPostName")
                .accept(regularJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(regularJson))
                .andReturn();

        this.mockMvc.perform(post(getHouseholdUrl())
                .contentType(regularJson)
                .content(mvcResult.getResponse().getContentAsString()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails
    public void postHouseholdSampleRegistrationXml() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(getHouseholdUrl() + "/sampleRegistration")
                .param("id", "sampleHouseholdPostId")
                .param("name", "sampleHouseholdPostName")
                .accept(regularXml))
                .andExpect(status().isOk())
                .andExpect(content().contentType(regularXml))
                .andReturn();

        this.mockMvc.perform(post(getHouseholdUrl())
                .contentType(regularXml)
                .content(mvcResult.getResponse().getContentAsString()))
                .andExpect(status().isCreated());
    }

    // PUT household

    @Test
    @WithUserDetails
    public void putNewHouseholdJson() throws Exception {
        final String uuid = UUID.randomUUID().toString();
        Individual entity = makeValidEntity("test household registration", "ignored id");

        MvcResult mvcResult = this.mockMvc.perform(put(getHouseholdUrl() + uuid)
                .contentType(regularJson)
                .content(toJson(controller.getHouseholdSampleRegistration(entity))))
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
                .content(toXml(controller.getHouseholdSampleRegistration(entity))))
                .andExpect(status().isCreated())
                .andReturn();

        Individual responseEntity = fromXml(controller.getEntityClass(), mvcResult.getResponse().getContentAsString());
        verifyEntityExistsWithNameAndId(responseEntity, "test registration", uuid);
    }

    @Test
    @WithUserDetails
    public void getHouseholdSampleRegistrationJson() throws Exception {
        mockMvc.perform(get(getHouseholdUrl() + "/sampleRegistration")
                .param("id", "sampleHouseholdId")
                .param("name", "sampleHouseholdName")
                .accept(regularJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(regularJson))
                .andExpect(jsonPath("$." + controller.getEntityFieldName() + ".uuid").value("sampleHouseholdId"));
    }

    @Test
    @WithUserDetails
    public void getHouseholdSampleRegistrationXml() throws Exception {
        mockMvc.perform(get(getHouseholdUrl() + "/sampleRegistration")
                .param("id", "sampleHouseholdId")
                .param("name", "sampleHouseholdName")
                .accept(regularXml))
                .andExpect(status().isOk())
                .andExpect(content().contentType(regularXml))
                .andExpect(xpath("/*/" + controller.getEntityFieldName() + "/" + "uuid").string("sampleHouseholdId"));
    }

    @Test
    @WithUserDetails
    public void lookupByMultipleFields() throws Exception {
        mockMvc.perform(get(getResourceUrl() + "/search")
                .param("firstName", "location-3-head")
                .accept(regularJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(regularJson));
    }

    @Test
    @WithUserDetails
    public void lookupByLocation() throws Exception {
        mockMvc.perform(get(getResourceUrl() + "/findByLocation")
                .param("locationUuid", "sampleHouseholdId")
                .accept(regularJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(regularJson));
    }

    @Test
    @WithUserDetails
    public void lookupByFieldWorker() throws Exception {
        mockMvc.perform(get(getResourceUrl() + "/findByFieldWorker")
                .param("fieldWorkerId", "fieldworker")
                .accept(regularJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(regularJson));
    }
}
