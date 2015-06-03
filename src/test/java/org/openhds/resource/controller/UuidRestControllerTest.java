package org.openhds.resource.controller;

import org.springframework.http.MediaType;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.io.IOException;

/**
 * Created by Ben on 5/4/15.
 */
public abstract class UuidRestControllerTest <T> extends RestControllerTestSupport {

    protected abstract T makeValidEntity(String name, String id);

    protected abstract T makeInvalidEntity(String name, String id);

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

}
