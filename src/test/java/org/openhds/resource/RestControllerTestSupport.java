package org.openhds.resource;

import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openhds.OpenHdsRestApplication;
import org.openhds.repository.util.SampleDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by Ben on 6/3/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OpenHdsRestApplication.class)
@WebAppConfiguration
public class RestControllerTestSupport {

    protected final MediaType halJson = new MediaType(
            MediaTypes.HAL_JSON.getType(),
            MediaTypes.HAL_JSON.getSubtype());

    protected final MediaType regularJson = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    protected final String username = "user";
    protected final String password = "password";

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected SampleDataGenerator sampleDataGenerator;

    @Autowired
    protected MappingJackson2HttpMessageConverter jsonMessageConverter;

    @Autowired
    protected MappingJackson2XmlHttpMessageConverter xmlMessageConverter;

    protected String extractJsonPath(MvcResult mvcResult, String linkPath) throws Exception {
        String linkHref = JsonPath.read(mvcResult.getResponse().getContentAsString(), linkPath);
        assertNotNull(linkHref);
        return linkHref;
    }

    @Before
    public void setup() throws Exception {
        sampleDataGenerator.clearData();
        sampleDataGenerator.generateSampleData();

        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        assertNotNull("the Json message converter must not be null", jsonMessageConverter);
        assertNotNull("the Xml message converter must not be null", xmlMessageConverter);
    }

    @Test
    public void initializes() throws Exception {
    }
}
