package org.openhds.resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openhds.OpenHdsRestApplication;
import org.openhds.repository.generator.MasterDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StopWatch;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by ben on 8/26/15.
 *
 * Do some representative REST queries and print the execution times.
 *
 * By default, small SAMPLE_DATA_SIZE=0, so that this doesn't slow the tests.
 *
 * When running by hand, increase SAMPLE_DATA_SIZE to see how things scale.  SAMPLE_DATA_SIZE=5 -> million+ records.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OpenHdsRestApplication.class})
@WebAppConfiguration
public class QueryTimer {

    private static final int SAMPLE_DATA_SIZE = 2;

    private static final int QUERY_REPS = 10;

    private final Log log = LogFactory.getLog(this.getClass());

    private final StopWatch stopWatch = new StopWatch();

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MasterDataGenerator masterDataGenerator;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        masterDataGenerator.clearData();
        masterDataGenerator.generateData(SAMPLE_DATA_SIZE);
    }

    @Test
    @WithMockUser
    public void severalQueries() throws Exception {
        timeQuery("/individuals");
        timeQuery("/individuals/external/location-0-member");
        timeQuery("/individuals/bylocationhierarchy?locationHierarchyUuid=UNKNOWN");

    }

    // repeat the given query and print the execution times
    private void timeQuery(String requestPath) throws Exception {
        List<Long> queryTimes = new ArrayList<>(QUERY_REPS);
        for (int i=0; i<QUERY_REPS; i++) {
            stopWatch.start();
            doQuery(requestPath);
            stopWatch.stop();
            queryTimes.add(i, stopWatch.getLastTaskTimeMillis());
        }

        log.info("Query: " + requestPath);
        log.info("  execution times (ms): " + queryTimes.toString());
    }

    private void doQuery(String requestPath) throws Exception {
        mockMvc.perform(get(requestPath))
                .andExpect(status().isOk());
    }
}
