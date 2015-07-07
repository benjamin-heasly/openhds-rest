package org.openhds.resource.converter;

import org.openhds.repository.results.EntityIterator;
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
 * Wrap an existing message converter to iterate many results.
 * <p>
 * Article with method override advice.
 * https://www.airpair.com/java/posts/spring-streams-memory-efficiency
 */
public class EntityCollectionMessageWriter extends AbstractHttpMessageConverter<EntityIterator<?>> {

    private final AbstractJackson2HttpMessageConverter converter;
    private final Delimiter delimiter;

    public EntityCollectionMessageWriter(AbstractJackson2HttpMessageConverter converter, Delimiter delimiter) {
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
        return EntityIterator.class.isAssignableFrom(clazz) && canWrite(mediaType);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        // should not be called, since we override canRead/Write
        throw new UnsupportedOperationException();
    }

    @Override
    protected EntityIterator<?> readInternal(Class clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writeInternal(EntityIterator<?> entityIterator, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        final OutputStream outputStream = outputMessage.getBody();

        delimiter.writePrefix(outputStream, entityIterator);

        Iterator<?> iterator = entityIterator.iterator();

        // empty collection
        if (!iterator.hasNext()) {
            delimiter.writeSuffix(outputStream, entityIterator);
            return;
        }

        while (iterator.hasNext()) {
            outputStream.write(converter.getObjectMapper().writeValueAsBytes(iterator.next()));

            // fussy: don't write delimiter after the last item
            if (iterator.hasNext()) {
                delimiter.writeDelimiter(outputStream, entityIterator);
            } else {
                delimiter.writeSuffix(outputStream, entityIterator);
            }
        }
    }

    public interface Delimiter {
        void writePrefix(OutputStream outputStream, EntityIterator<?> entityIterator) throws IOException;

        void writeDelimiter(OutputStream outputStream, EntityIterator<?> entityIterator) throws IOException;

        void writeSuffix(OutputStream outputStream, EntityIterator<?> entityIterator) throws IOException;
    }

}
