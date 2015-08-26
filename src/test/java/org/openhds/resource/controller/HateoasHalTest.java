package org.openhds.resource.controller;

import org.junit.Test;
import org.openhds.resource.contract.AuditableExtIdRestController;
import org.openhds.resource.contract.RestControllerTestSupport;
import org.springframework.hateoas.Link;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Ben on 5/26/15.
 */
public class HateoasHalTest extends RestControllerTestSupport {

    @Test
    @WithMockUser(username = username, password = password)
    public void followLinks() throws Exception {
        // find the locations controller from the home controller
        String locationsUrl = getAndExtractJsonPath("/", "$._links.locations.href");

        // find the first location from the list of locations
        String oneLocationUrl = getAndExtractJsonPath(locationsUrl, "$._embedded.locations[0]._links." + Link.REL_SELF + ".href");

        // follow the "external id" link to f the same location
        String externalLocationUrl = getAndExtractJsonPath(oneLocationUrl, "$._links." + AuditableExtIdRestController.REL_SECTION + ".href");

        // find the user who inserted the location, from the location
        String insertByUrl = getAndExtractJsonPath(externalLocationUrl, "$._embedded.locations[0]._links.insertby.href");

        // find the user's "self"
        String insertBySelfUrl = getAndExtractJsonPath(insertByUrl, "$._links.self.href");
    }

    private String getAndExtractJsonPath(String url, String linkPath) throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(url)
                .contentType(regularJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath(linkPath).exists())
                .andReturn();
        return extractJsonPath(mvcResult, linkPath);
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void pluralCollectionNameJson() throws Exception {
        this.mockMvc.perform(get("/locations")
                .contentType(regularJson)
                .accept(halJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.locations").exists());
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void pluralCollectionNameXml() throws Exception {
        this.mockMvc.perform(get("/locations")
                .contentType(regularXml)
                .accept(regularXml))
                .andExpect(status().isOk())
                .andExpect(xpath("/pagedEntities/content").exists());
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void selfLinkJson() throws Exception {
        this.mockMvc.perform(get("/")
                .accept(halJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void selfLinkXml() throws Exception {
        this.mockMvc.perform(get("/")
                .accept(regularXml))
                .andExpect(status().isOk())
                .andExpect(xpath("/Resource/link/link[@rel='self']/@href").exists());
    }

}