package org.openhds.resource;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Ben on 5/26/15.
 */
public class hateoasTraversalTest extends AbstractRestControllerTest {

    @Test
    @WithMockUser(username = username, password = password)
    public void followLinks() throws Exception {
        // find the locations controller from the home controller
        String locationsUrl = getAndExtractLinkHref("/", "$._links.locations.href");

        // find the first location from the list of locations
        String oneLocationUrl = getAndExtractLinkHref(locationsUrl, "$._embedded.locationList[0]._links.self.href");

        // find the user who inserted the location, from the location
        String insertByUrl = getAndExtractLinkHref(oneLocationUrl, "$._links.insertBy.href");

        // find the user's "self"
        String insertBySelfUrl = getAndExtractLinkHref(oneLocationUrl, "$._links.self.href");

    }

    private String getAndExtractLinkHref(String url, String linkPath) throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(url)
                .contentType(regularJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath(linkPath).exists())
                .andReturn();
        String linkHref = JsonPath.read(mvcResult.getResponse().getContentAsString(), linkPath);
        assertNotNull(linkHref);
        return linkHref;
    }
}