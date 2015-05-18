package org.openhds.resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openhds.OpenhdsRestApplication;
import org.openhds.domain.Account;
import org.openhds.repository.AccountRepository;
import org.openhds.domain.Bookmark;
import org.openhds.repository.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;



/**
 * Created by Ben on 5/4/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OpenhdsRestApplication.class)
@WebAppConfiguration
public class BookmarkRestControllerTest {


    private MediaType contentType = new MediaType(
            MediaTypes.HAL_JSON.getType(),
            MediaTypes.HAL_JSON.getSubtype());

    private MockMvc mockMvc;

    private final String userName = "bdussault";

    private HttpMessageConverter messageConverter;

    private Account account;

    private List<Bookmark> bookmarkList = new ArrayList<>();

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

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

        this.bookmarkRepository.deleteAllInBatch();
        this.accountRepository.deleteAllInBatch();

        this.account = accountRepository.save(new Account(userName, "password"));
        this.bookmarkList.add(
                bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/1/" + userName, "A description")));
        this.bookmarkList.add(
                bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/2/" + userName, "A description")));
    }

    @Test
    @WithMockUser(username="invalid", password = "invalid", roles = {""})
    public void forbiddenUser() throws Exception {
        mockMvc.perform(post("/resource/")
                .content(this.json(new Bookmark()))
                .contentType(contentType))
                .andExpect(status().isForbidden());
    }

    @Test
    public void noUser() throws Exception {
        mockMvc.perform(post("/bookmarks/")
                .content(this.json(new Bookmark()))
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username=userName, password = "password")
    public void readSingleBookmark() throws Exception {
        final String bookmarkPath = "$bookmark";

        mockMvc.perform(get("/bookmarks/" + this.bookmarkList.get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath(bookmarkPath + ".id", is(this.bookmarkList.get(0).getId().intValue())))
                .andExpect(jsonPath(bookmarkPath + ".uri", is("http://bookmark.com/1/" + userName)))
                .andExpect(jsonPath(bookmarkPath + ".description", is("A description")));
    }

    @Test
    @WithMockUser(username=userName, password = "password")
    public void readBookmarks() throws Exception {
        final String bookmarksPath = "$_embedded.bookmarkHateoasSupportList";

        mockMvc.perform(get("/bookmarks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath(bookmarksPath, hasSize(2)))
                .andExpect(jsonPath(bookmarksPath + "[0].bookmark.id", is(this.bookmarkList.get(0).getId().intValue())))
                .andExpect(jsonPath(bookmarksPath + "[0].bookmark.uri", is("http://bookmark.com/1/" + userName)))
                .andExpect(jsonPath(bookmarksPath + "[0].bookmark.description", is("A description")))
                .andExpect(jsonPath(bookmarksPath + "[1].bookmark.id", is(this.bookmarkList.get(1).getId().intValue())))
                .andExpect(jsonPath(bookmarksPath + "[1].bookmark.uri", is("http://bookmark.com/2/" + userName)))
                .andExpect(jsonPath(bookmarksPath + "[1].bookmark.description", is("A description")));
    }

    @Test
    @WithMockUser(username=userName, password = "password")
    public void createBookmark() throws Exception {
        String bookmarkJson = json(new Bookmark(
                this.account, "http://spring.io", "a bookmark to the best resource for Spring news and information"));
        this.mockMvc.perform(post("/bookmarks")
                .contentType(contentType)
                .content(bookmarkJson))
                .andExpect(status().isCreated());
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.messageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
