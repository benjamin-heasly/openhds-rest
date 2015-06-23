package org.openhds.resource.converter;

import org.openhds.repository.results.PageIterator;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Ben on 6/22/15.
 *
 */
public class XmlElementDelimiter implements PagedMessageWriter.Delimiter {
    @Override
    public void writePrefix(OutputStream outputStream, PageIterator<?> pageIterator) throws IOException {
        outputStream.write("<".getBytes());
        outputStream.write(pageIterator.getCollectionName().getBytes());
        outputStream.write(">".getBytes());
    }

    @Override
    public void writeDelimiter(OutputStream outputStream, PageIterator<?> pageIterator) throws IOException {

    }

    @Override
    public void writeSuffix(OutputStream outputStream, PageIterator<?> pageIterator) throws IOException {
        outputStream.write("</".getBytes());
        outputStream.write(pageIterator.getCollectionName().getBytes());
        outputStream.write(">".getBytes());
    }
}
