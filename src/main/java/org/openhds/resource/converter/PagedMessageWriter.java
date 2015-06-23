package org.openhds.resource.converter;

import org.openhds.repository.results.PageIterator;
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
import java.util.Iterator;

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

    public interface Delimiter {
        void writePrefix(OutputStream outputStream, PageIterator<?> pageIterator) throws IOException;
        void writeDelimiter(OutputStream outputStream, PageIterator<?> pageIterator) throws IOException;
        void writeSuffix(OutputStream outputStream, PageIterator<?> pageIterator) throws IOException;
    }

    private final AbstractJackson2HttpMessageConverter converter;

    private final Delimiter delimiter;

    public PagedMessageWriter(AbstractJackson2HttpMessageConverter converter, Delimiter delimiter) {
        super(converter.getSupportedMediaTypes().toArray(new MediaType[0]));
        this.converter = converter;
        this.delimiter = delimiter;
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

        final OutputStream outputStream = outputMessage.getBody();

        delimiter.writePrefix(outputStream, pageIterator);

        while (pageIterator.hasNext()) {
            Page page = pageIterator.next();
            Iterator<?> objectIterator = page.getContent().iterator();
            while (objectIterator.hasNext()) {
                outputStream.write(converter.getObjectMapper().writeValueAsBytes(objectIterator.next()));

                // fussy: don't write delimiter after the last item
                if (objectIterator.hasNext() || pageIterator.hasNext()) {
                    delimiter.writeDelimiter(outputStream, pageIterator);
                } else {
                    delimiter.writeSuffix(outputStream, pageIterator);
                }
            }
        }
    }
}
