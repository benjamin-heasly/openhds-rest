package org.openhds.resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openhds.OpenHdsRestApplication;
import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.repository.generator.MasterDataGenerator;
import org.openhds.service.impl.census.LocationHierarchyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Sort;
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
 * <p>
 * Do some representative REST queries and print the execution times.
 * <p>
 * By default, ignore this during tests.
 * <p>
 * When running by hand, comment out the @Ignore.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OpenHdsRestApplication.class})
@WebAppConfiguration
public class QueryTimer {

    private static final int SAMPLE_DATA_SIZE = 3;

    private static final int QUERY_REPS = 10;

    private final Log log = LogFactory.getLog(this.getClass());

    private final StopWatch stopWatch = new StopWatch();

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MasterDataGenerator masterDataGenerator;

    @Autowired
    private LocationHierarchyService locationHierarchyService;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        masterDataGenerator.clearData();
        log.info("Generate data with size " + SAMPLE_DATA_SIZE);
        stopWatch.start();
        masterDataGenerator.generateData(SAMPLE_DATA_SIZE);
        stopWatch.stop();
        log.info("  execution time (ms): " + stopWatch.getLastTaskTimeMillis());
    }

    @Test
    @WithMockUser
    public void severalQueries() throws Exception {

        LocationHierarchy locationHierarchy = locationHierarchyService.findByExtId(new Sort("uuid"), "hierarchy-0-1")
                .iterator()
                .next();

        timeQuery("/individuals");
        timeQuery("/individuals/external/location-0-member");
        timeQuery("/individuals/?sort=lastModifiedDate");

        timeQuery("/locationHierarchies");
        timeQuery("/locationHierarchies/external/hierarchy-0-1");
        timeQuery("/locationHierarchies/bylocationhierarchy?locationHierarchyUuid=" + locationHierarchy.getUuid());
    }

    // repeat the given query and print the execution times
    private void timeQuery(String requestPath) throws Exception {
        List<Long> queryTimes = new ArrayList<>(QUERY_REPS);
        for (int i = 0; i < QUERY_REPS; i++) {
            stopWatch.start();
            doQuery(requestPath);
            stopWatch.stop();
            queryTimes.add(i, stopWatch.getLastTaskTimeMillis());
        }

        log.info("Query: GET " + requestPath);
        log.info("  execution times (ms): " + queryTimes.toString());
    }

    private void doQuery(String requestPath) throws Exception {
        mockMvc.perform(get(requestPath))
                .andExpect(status().isOk());
    }
}
