package org.openhds.resource.converter;

import org.openhds.repository.util.PageIterator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Ben on 6/17/15.
 * <p>
 * Wrap a message converter to write multiple pages of results.
 *
 * Article with method override advice.
 * https://www.airpair.com/java/posts/spring-streams-memory-efficiency
 *
 */
public class PagedMessageWriter extends AbstractHttpMessageConverter<PageIterator<?>> {

    private final AbstractJackson2HttpMessageConverter converter;

    public PagedMessageWriter(AbstractJackson2HttpMessageConverter converter) {
        super(converter.getSupportedMediaTypes().toArray(new MediaType[0]));
        this.converter = converter;
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return PageIterator.class.isAssignableFrom(clazz) && canWrite(mediaType);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        // should not be called, since we override canRead/Write
        throw new UnsupportedOperationException();
    }

    @Override
    protected PageIterator<?> readInternal(Class clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writeInternal(PageIterator<?> pageIterator, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        final MediaType mediaType = chooseMediaTypeForMessage(outputMessage);

        final OutputStream outputStream = outputMessage.getBody();

        while (pageIterator.hasNext()) {
            Page page = pageIterator.next();
            for (Object object : page) {
                converter.getObjectMapper().writeValue(outputStream, object);
            }
        }

    }

    private MediaType chooseMediaTypeForMessage(HttpOutputMessage outputMessage) {
        MediaType messageType = outputMessage.getHeaders().getContentType();
        if (null != messageType) {
            return messageType;
        }
        return getSupportedMediaTypes().get(0);
    }
}
