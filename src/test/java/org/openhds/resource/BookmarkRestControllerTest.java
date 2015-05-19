package org.openhds.resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openhds.OpenHdsRestApplication;
import org.openhds.domain.Account;
import org.openhds.domain.Bookmark;
import org.openhds.repository.AccountRepository;
import org.openhds.repository.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



/**
 * Created by Ben on 5/4/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OpenHdsRestApplication.class)
@WebAppConfiguration
public class BookmarkRestControllerTest extends AbstractRestControllerTest {

    private Account account;

    private List<Bookmark> bookmarkList = new ArrayList<>();

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private AccountRepository accountRepository;


    @Before
    public void setup() throws Exception {
        super.setup();

        this.bookmarkRepository.deleteAllInBatch();
        this.accountRepository.deleteAllInBatch();

        this.account = accountRepository.save(new Account(username, "password"));
        this.bookmarkList.add(
                bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/1/" + username, "A description")));
        this.bookmarkList.add(
                bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/2/" + username, "A description")));
    }

    @Test
    @WithMockUser(username="invalid", password = "invalid", roles = {""})
    public void forbiddenUser() throws Exception {
        mockMvc.perform(post("/resource/")
                .content(this.json(new Bookmark()))
                .contentType(halJson))
                .andExpect(status().isForbidden());
    }

    @Test
    public void noUser() throws Exception {
        mockMvc.perform(post("/bookmarks/")
                .content(this.json(new Bookmark()))
                .contentType(halJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username= username, password = "password")
    public void readSingleBookmark() throws Exception {
        final String bookmarkPath = "$bookmark";

        mockMvc.perform(get("/bookmarks/" + this.bookmarkList.get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath(bookmarkPath + ".id", is(this.bookmarkList.get(0).getId().intValue())))
                .andExpect(jsonPath(bookmarkPath + ".uri", is("http://bookmark.com/1/" + username)))
                .andExpect(jsonPath(bookmarkPath + ".description", is("A description")));
    }

    @Test
    @WithMockUser(username= username, password = "password")
    public void readBookmarks() throws Exception {
        final String bookmarksPath = "$_embedded.bookmarkLinkInfoList";

        mockMvc.perform(get("/bookmarks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath(bookmarksPath, hasSize(2)))
                .andExpect(jsonPath(bookmarksPath + "[0].bookmark.id", is(this.bookmarkList.get(0).getId().intValue())))
                .andExpect(jsonPath(bookmarksPath + "[0].bookmark.uri", is("http://bookmark.com/1/" + username)))
                .andExpect(jsonPath(bookmarksPath + "[0].bookmark.description", is("A description")))
                .andExpect(jsonPath(bookmarksPath + "[1].bookmark.id", is(this.bookmarkList.get(1).getId().intValue())))
                .andExpect(jsonPath(bookmarksPath + "[1].bookmark.uri", is("http://bookmark.com/2/" + username)))
                .andExpect(jsonPath(bookmarksPath + "[1].bookmark.description", is("A description")));
    }

    @Test
    @WithMockUser(username= username, password = "password")
    public void createBookmark() throws Exception {
        String bookmarkJson = json(new Bookmark(this.account,
                "http://spring.io", "a bookmark to the best resource for Spring news and information"));
        this.mockMvc.perform(post("/bookmarks")
                .contentType(halJson)
                .content(bookmarkJson))
                .andExpect(status().isCreated());
    }
}
