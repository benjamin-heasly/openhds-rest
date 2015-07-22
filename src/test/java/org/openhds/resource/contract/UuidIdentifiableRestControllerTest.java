package org.openhds.resource.contract;

import org.junit.Test;
import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.repository.contract.UuidIdentifiableRepository;
import org.openhds.resource.controller.RestControllerTestSupport;
import org.openhds.resource.registration.Registration;
import org.openhds.service.contract.AbstractUuidService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Created by Ben on 5/4/15.
 */
public abstract class UuidIdentifiableRestControllerTest<
        T extends UuidIdentifiable,
        U extends AbstractUuidService<T, ? extends UuidIdentifiableRepository<T>>,
        V extends UuidIdentifiableRestController<T, ? extends Registration<T>, U>>
        extends RestControllerTestSupport {

    protected U service;

    protected V controller;

    protected final Sort UUID_SORT = new Sort("uuid");

    protected abstract void initialize(U service, V controller);

    protected abstract T makeValidEntity(String name, String id);

    protected abstract T makeInvalidEntity();

    protected abstract void verifyEntityExistsWithNameAndId(T entity, String name, String id);

    protected abstract Registration<T> makeRegistration(T entity);

    protected String getResourceUrl() {
        return "/" + controller.getResourceName() + "/";
    }

    protected T findAnyExisting() {
        return service.findAll(UUID_SORT).toList().get(0);
    }

    protected T makeUpdateEntity(String name) {
        T original = findAnyExisting();
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
    @WithUserDetails
    public void postNewJson() throws Exception {
        T entity = makeValidEntity("test registration", "test id");

        MvcResult mvcResult = this.mockMvc.perform(post(getResourceUrl())
                .contentType(regularJson)
                .content(toJson(makeRegistration(entity))))
                .andExpect(status().isCreated())
                .andReturn();

        T responseEntity = fromJson(controller.getEntityClass(), mvcResult.getResponse().getContentAsString());
        verifyEntityExistsWithNameAndId(responseEntity, "test registration", entity.getUuid());
    }

    @Test
    @WithUserDetails
    public void postNewXml() throws Exception {
        T entity = makeValidEntity("test registration", "test id");

        MvcResult mvcResult = this.mockMvc.perform(post(getResourceUrl())
                .contentType(regularXml)
                .accept(regularXml)
                .content(toXml(makeRegistration(entity))))
                .andExpect(status().isCreated())
                .andReturn();

        T responseEntity = fromXml(controller.getEntityClass(), mvcResult.getResponse().getContentAsString());
        verifyEntityExistsWithNameAndId(responseEntity, "test registration", entity.getUuid());
    }

    @Test
    @WithUserDetails
    public void postUpdate() throws Exception {
        T entity = makeUpdateEntity("test update");

        MvcResult mvcResult = this.mockMvc.perform(post(getResourceUrl())
                .contentType(regularJson)
                .content(toJson(makeRegistration(entity))))
                .andExpect(status().isCreated())
                .andReturn();

        T responseEntity = fromJson(controller.getEntityClass(), mvcResult.getResponse().getContentAsString());
        verifyEntityExistsWithNameAndId(responseEntity, "test update", entity.getUuid());
    }

    @Test
    @WithUserDetails
    public void postInvalid() throws Exception {
        this.mockMvc.perform(post(getResourceUrl())
                .contentType(regularJson)
                .content(toJson(makeRegistration(makeInvalidEntity()))))
                .andExpect(status().is4xxClientError());
    }

    // PUT

    @Test
    @WithUserDetails
    public void putNew() throws Exception {
        final String uuid = UUID.randomUUID().toString();
        T entity = makeValidEntity("test registration", "ignored id");

        MvcResult mvcResult = this.mockMvc.perform(put(getResourceUrl() + uuid)
                .contentType(regularJson)
                .content(toJson(makeRegistration(entity))))
                .andExpect(status().isCreated())
                .andReturn();

        T responseEntity = fromJson(controller.getEntityClass(), mvcResult.getResponse().getContentAsString());
        verifyEntityExistsWithNameAndId(responseEntity, "test registration", uuid);
    }

    @Test
    @WithUserDetails
    public void putNewXml() throws Exception {
        final String uuid = UUID.randomUUID().toString();
        T entity = makeValidEntity("test registration", "ignored id");

        MvcResult mvcResult = this.mockMvc.perform(put(getResourceUrl() + uuid)
                .contentType(regularXml)
                .accept(regularXml)
                .content(toXml(makeRegistration(entity))))
                .andExpect(status().isCreated())
                .andReturn();

        T responseEntity = fromXml(controller.getEntityClass(), mvcResult.getResponse().getContentAsString());
        verifyEntityExistsWithNameAndId(responseEntity, "test registration", uuid);
    }

    @Test
    @WithUserDetails
    public void putUpdate() throws Exception {
        T entity = makeUpdateEntity("test update");

        MvcResult mvcResult = this.mockMvc.perform(put(getResourceUrl() + entity.getUuid())
                .contentType(regularJson)
                .content(toJson(makeRegistration(entity))))
                .andExpect(status().isCreated())
                .andReturn();

        T responseEntity = fromJson(controller.getEntityClass(), mvcResult.getResponse().getContentAsString());
        verifyEntityExistsWithNameAndId(responseEntity, "test update", entity.getUuid());
    }

    @Test
    @WithUserDetails
    public void putInvalid() throws Exception {
        final String uuid = UUID.randomUUID().toString();

        this.mockMvc.perform(put(getResourceUrl() + uuid)
                .contentType(regularJson)
                .content(toJson(makeRegistration(makeInvalidEntity()))))
                .andExpect(status().is4xxClientError());
    }

    // GET

    @Test
    @WithUserDetails
    public void getSingleValid() throws Exception {
        T entity = findAnyExisting();
        mockMvc.perform(get(getResourceUrl() + entity.getUuid())
                .accept(halJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$.uuid", is(entity.getUuid())));
    }

    @Test
    @WithUserDetails
    public void getSingleValidXml() throws Exception {
        T entity = findAnyExisting();
        mockMvc.perform(get(getResourceUrl() + entity.getUuid())
                .accept(regularXml))
                .andExpect(status().isOk())
                .andExpect(content().contentType(regularXml))
                .andExpect(xpath("/Resource/uuid").string(entity.getUuid()));
    }

    @Test
    @WithUserDetails
    public void getSingleInvalid() throws Exception {
        mockMvc.perform(get(getResourceUrl() + "invalid id"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails
    public void getAll() throws Exception {
        mockMvc.perform(get(getResourceUrl()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$._embedded." + controller.getResourceName(), hasSize((int) service.countAll())));
    }

    @Test
    @WithUserDetails
    public void getPaged() throws Exception {
        // get the first page of size 1
        mockMvc.perform(get(getResourceUrl())
                .param("page", "0")
                .param("size", "1")
                .param("sort", "uuid"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(jsonPath("$._embedded." + controller.getResourceName(), hasSize(1)))
                .andExpect(jsonPath("$.page.size", is(1)))
                .andExpect(jsonPath("$.page.number", is(0)));
    }

    @Test
    @WithUserDetails
    public void getBulkJson() throws Exception {
        mockMvc.perform(get(getResourceUrl() + "/bulk")
                .param("sort", "uuid")
                .accept(regularJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(regularJson))
                .andExpect(jsonPath("$", hasSize((int) service.countAll())));
    }

    @Test
    @WithUserDetails
    public void getBulkXml() throws Exception {
        mockMvc.perform(get(getResourceUrl() + "/bulk")
                .param("sort", "uuid")
                .accept(regularXml))
                .andExpect(status().isOk())
                .andExpect(content().contentType(regularXml))
                .andExpect(xpath("/" + controller.getResourceName() + "/*").nodeCount((int) service.countAll()));
    }

    // DELETE

    @Test
    @WithUserDetails
    public void deleteCollection() throws Exception {
        mockMvc.perform(delete(getResourceUrl()))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @WithUserDetails
    public void deleteInvalid() throws Exception {
        final String invalidId = "not an id";
        mockMvc.perform(delete(getResourceUrl() + invalidId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails
    public void deleteExisting() throws Exception {
        T entity = findAnyExisting();
        MvcResult mvcResult = mockMvc.perform(delete(getResourceUrl() + entity.getUuid()))
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus(),
                isOneOf(HttpStatus.CONFLICT.value(), HttpStatus.NO_CONTENT.value()));
    }

    @Test
    @WithUserDetails
    public void deleteNew() throws Exception {
        T entity = makeValidEntity("delete me", "test id");
        this.mockMvc.perform(post(getResourceUrl())
                .contentType(regularJson)
                .content(toJson(makeRegistration(entity))))
                .andExpect(status().isCreated());

        mockMvc.perform(get(getResourceUrl() + entity.getUuid()))
                .andExpect(status().isOk());

        mockMvc.perform(delete(getResourceUrl() + entity.getUuid()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(getResourceUrl() + entity.getUuid()))
                .andExpect(status().isNotFound());
    }

}
