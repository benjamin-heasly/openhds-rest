package org.openhds.resource;

import org.junit.Test;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.LocationHierarchy;
import org.openhds.domain.registration.LocationRegistration;
import org.openhds.repository.FieldWorkerRepository;
import org.openhds.repository.LocationHierarchyRepository;
import org.openhds.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Created by Ben on 5/26/15.
 */
public class LocationRestControllerTest extends AbstractRestControllerTest {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private FieldWorkerRepository fieldWorkerRepository;

    @Autowired
    private LocationHierarchyRepository locationHierarchyRepository;

    @Test
    @WithMockUser(username = username, password = password)
    public void postNewLocation() throws Exception {
        LocationHierarchy locationHierarchy = locationHierarchyRepository.findByExtId("bottom-one").get();

        Location location = new Location();
        location.setName("new-location");
        location.setExtId("new-location");
        location.setCollectedBy(fieldWorkerRepository.findAll().get(0));

        LocationRegistration locationRegistration = new LocationRegistration();
        locationRegistration.setLocation(location);
        locationRegistration.setLocationHierarchyUuid(locationHierarchy.getUuid());

        String jsonBody = this.toJson(locationRegistration);
        mockMvc.perform(post("/locations")
                .content(jsonBody)
                .contentType(regularJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(halJson));
    }
}