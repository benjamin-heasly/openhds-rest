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

    public interface MessageStreamHelperFactory {
        MessageStreamHelper newMessageStreamHelper(OutputStream outputStream);
    }

    public interface MessageStreamHelper {
        void startMessageStream() throws IOException;
        void endMessageStream() throws IOException;
    }

    private final AbstractJackson2HttpMessageConverter converter;

    private final MessageStreamHelperFactory helperFactory;

    public PagedMessageWriter(AbstractJackson2HttpMessageConverter converter, MessageStreamHelperFactory helperFactory) {
        super(converter.getSupportedMediaTypes().toArray(new MediaType[0]));
        this.converter = converter;
        this.helperFactory = helperFactory;
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

        MessageStreamHelper helper = null;
        if (null != helperFactory) {
            helper = helperFactory.newMessageStreamHelper(outputStream);
            helper.startMessageStream();
            outputStream.flush();
        }

        while (pageIterator.hasNext()) {
            Page page = pageIterator.next();
            for (Object object : page) {
                outputStream.write(converter.getObjectMapper().writeValueAsBytes(object));
                outputStream.flush();
                //converter.getObjectMapper().writeValue(outputStream, object);
            }
        }

        if (null != helper) {
            helper.endMessageStream();
            outputStream.flush();
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
