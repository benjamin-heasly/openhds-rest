package org.openhds.resource.controller;

import org.junit.Test;
import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.resource.registration.Registration;
import org.springframework.http.MediaType;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Ben on 5/4/15.
 */
public abstract class UuidRestControllerTest<T extends UuidIdentifiable> extends RestControllerTestSupport {

    protected abstract T makeValidEntity(String name, String id);

    protected abstract T makeInvalidEntity();

    protected abstract void verifyEntityExistsWithNameAndId(T entity, String name, String id);

    protected abstract Registration<T> makeRegistration(T entity);

    protected abstract T getAnyExisting();

    protected abstract long getCount();

    protected abstract String getResourceName();

    protected abstract Class<T> getEntityClass();

    protected String getResourceUrl() {
        return "/" + getResourceName() + "/";
    }

    protected T makeUpdateEntity(String name) {
        T original = getAnyExisting();
        return makeValidEntity(name, original.getUuid());
    }

    protected String toJson(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        jsonMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    protected T fromJson(Class<T> targetClass, String message) throws IOException {
        MockHttpInputMessage mockHttpInputMessage = new MockHttpInputMessage(message.getBytes());
        return (T) jsonMessageConverter.read(targetClass, mockHttpInputMessage);
    }

    protected String toXml(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        xmlMessageConverter.write(o, MediaType.APPLICATION_XML, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    protected T fromXml(Class<T> targetClass, String message) throws IOException {
        MockHttpInputMessage mockHttpInputMessage = new MockHttpInputMessage(message.getBytes());
        return (T) xmlMessageConverter.read(targetClass, mockHttpInputMessage);
    }

    // User auth

    @Test
    @WithMockUser(username = "invalid", password = "invalid", roles = {""})
    public void forbiddenPost() throws Exception {
        mockMvc.perform(post(getResourceUrl())
                .contentType(regularJson)
                .content(toJson(makeRegistration(makeValidEntity("test registration", "test id")))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void noUserPost() throws Exception {
        mockMvc.perform(post(getResourceUrl())
                .contentType(regularJson)
                .content(toJson(makeRegistration(makeValidEntity("test registration", "test id")))))
                .andExpect(status().isUnauthorized());
    }

    // POST

    @Test
    @WithMockUser(username = username, password = password)
    public void postNewJson() throws Exception {
        T entity = makeValidEntity("test registration", "test id");

        MvcResult mvcResult = this.mockMvc.perform(post(getResourceUrl())
                .contentType(regularJson)
                .content(toJson(makeRegistration(entity))))
                .andExpect(status().isCreated())
                .andReturn();

        T responseEntity = fromJson(getEntityClass(), mvcResult.getResponse().getContentAsString());
        verifyEntityExistsWithNameAndId(responseEntity, "test registration", entity.getUuid());
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void postNewXml() throws Exception {
        T entity = makeValidEntity("test registration", "test id");

        MvcResult mvcResult = this.mockMvc.perform(post(getResourceUrl())
                .contentType(regularXml)
                .accept(regularXml)
                .content(toXml(makeRegistration(entity))))
                .andExpect(status().isCreated())
                .andReturn();

        T responseEntity = fromXml(getEntityClass(), mvcResult.getResponse().getContentAsString());
        verifyEntityExistsWithNameAndId(responseEntity, "test registration", entity.getUuid());
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void postUpdate() throws Exception {
        T entity = makeUpdateEntity("test update");

        MvcResult mvcResult = this.mockMvc.perform(post(getResourceUrl())
                .contentType(regularJson)
                .content(toJson(makeRegistration(entity))))
                .andExpect(status().isCreated())
                .andReturn();

        T responseEntity = fromJson(getEntityClass(), mvcResult.getResponse().getContentAsString());
        verifyEntityExistsWithNameAndId(responseEntity, "test update", entity.getUuid());
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void postInvalid() throws Exception {
        this.mockMvc.perform(post(getResourceUrl())
                .contentType(regularJson)
                .content(toJson(makeRegistration(makeInvalidEntity()))))
                .andExpect(status().is4xxClientError());
    }

    // PUT

    @Test
    @WithMockUser(username = username, password = password)
    public void putNew() throws Exception {
        final String uuid = UUID.randomUUID().toString();

        MvcResult mvcResult = this.mockMvc.perform(put(getResourceUrl() + uuid)
                .contentType(regularJson)
                .content(toJson(makeRegistration(makeValidEntity("test registration", "test id")))))
                .andExpect(status().isCreated())
                .andReturn();

        String responseUuid = extractJsonPath(mvcResult, "$.uuid");
        assertEquals(uuid, responseUuid);
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void putNewXml() throws Exception {
        final String uuid = UUID.randomUUID().toString();

        MvcResult mvcResult = this.mockMvc.perform(put(getResourceUrl() + uuid)
                .contentType(regularXml)
                .accept(regularXml)
                .content(toXml(makeRegistration(makeValidEntity("test registration", "test id")))))
                .andExpect(status().isCreated())
                .andReturn();

        String responseUuid = extractXmlPath(mvcResult, "/Resource/uuid");
        assertEquals(uuid, responseUuid);
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void putUpdate() throws Exception {
        final String uuid = UUID.randomUUID().toString();

        MvcResult mvcResult = this.mockMvc.perform(put(getResourceUrl() + uuid)
                .contentType(regularJson)
                .content(toJson(makeRegistration(makeUpdateEntity("test update")))))
                .andExpect(status().isCreated())
                .andReturn();

        String responseUuid = extractJsonPath(mvcResult, "$.uuid");
        assertEquals(uuid, responseUuid);
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void putInvalid() throws Exception {
        final String uuid = UUID.randomUUID().toString();

        this.mockMvc.perform(put(getResourceUrl() + uuid)
                .contentType(regularJson)
                .content(toJson(makeRegistration(makeInvalidEntity()))))
                .andExpect(status().is4xxClientError());
    }

    // GET

    @Test
    @WithMockUser(username = username, password = password)
    public void getSingleValid() throws Exception {
        T entity = getAnyExisting();
        mockMvc.perform(get(getResourceUrl() + entity.getUuid()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$.uuid", is(entity.getUuid())));
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void getSingleInvalid() throws Exception {
        mockMvc.perform(get(getResourceUrl() + "invalid id"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void getAll() throws Exception {
        mockMvc.perform(get(getResourceUrl()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$._embedded." + getResourceName(), hasSize((int) getCount())));
    }

    @Test
    @WithMockUser(username = username, password = password)
    public void getPaged() throws Exception {
        // get the first page of size 1
        mockMvc.perform(get(getResourceUrl())
                .param("page", "0")
                .param("size", "1")
                .param("sort", "uuid"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$._embedded." + getResourceName(), hasSize(1)))
                .andExpect(jsonPath("$.page.size", is(1)))
                .andExpect(jsonPath("$.page.number", is(0)));
    }

}
