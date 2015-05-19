package org.openhds.resource;

import org.junit.Test;
import org.openhds.repository.UserRepository;
import org.openhds.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Ben on 5/19/15.
 */
public class UserRestControllerTest extends AbstractRestControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "invalid", password = "invalid", roles = {""})
    public void forbiddenUser() throws Exception {
        mockMvc.perform(post("/users/")
                .content(this.json(new User()))
                .contentType(halJson))
                .andExpect(status().isForbidden());
    }

    @Test
    public void noUser() throws Exception {
        mockMvc.perform(post("/users/")
                .content(this.json(new User()))
                .contentType(halJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void readSingleUser() throws Exception {
        mockMvc.perform(get("/users/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$.username", is(username)));
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void readUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(regularJson))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void createBookmark() throws Exception {
        User newUser = new User();
        newUser.setFirstName("first");
        newUser.setLastName("last");
        newUser.setUsername("test-username");
        newUser.setPassword("password");

        String userJson = json(newUser);
        this.mockMvc.perform(post("/users")
                .contentType(regularJson)
                .content(userJson))
                .andExpect(status().isCreated());

        // TODO: test setup should handle database isolation for all tests
        User toDelete = userRepository.findByUsername("test-username").get();
        userRepository.delete(toDelete);
    }
}