package org.openhds.resource.converter;

import org.openhds.repository.results.EntityIterator;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Ben on 6/22/15.
 */
public class XmlElementDelimiter implements EntityCollectionMessageWriter.Delimiter {
    @Override
    public void writePrefix(OutputStream outputStream, EntityIterator<?> entityIterator) throws IOException {
        outputStream.write("<".getBytes());
        outputStream.write(entityIterator.getCollectionName().getBytes());
        outputStream.write(">".getBytes());
    }

    @Override
    public void writeDelimiter(OutputStream outputStream, EntityIterator<?> entityIterator) throws IOException {

    }

    @Override
    public void writeSuffix(OutputStream outputStream, EntityIterator<?> entityIterator) throws IOException {
        outputStream.write("</".getBytes());
        outputStream.write(entityIterator.getCollectionName().getBytes());
        outputStream.write(">".getBytes());
    }
}
