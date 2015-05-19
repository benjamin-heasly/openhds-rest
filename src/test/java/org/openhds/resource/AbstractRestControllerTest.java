package org.openhds.resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openhds.OpenHdsRestApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


/**
 * Created by Ben on 5/4/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OpenHdsRestApplication.class)
@WebAppConfiguration
public abstract class AbstractRestControllerTest {

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

    protected HttpMessageConverter messageConverter;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    private void setConverters(HttpMessageConverter<?>[] converters) {
        this.messageConverter = Arrays.asList(converters)
                .stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .get();
        Assert.assertNotNull("the JSON message converter must not be null", this.messageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.messageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
