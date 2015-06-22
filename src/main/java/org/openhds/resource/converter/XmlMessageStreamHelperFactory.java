package org.openhds.resource.converter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Ben on 6/17/15.
 */
public class XmlMessageStreamHelperFactory implements PagedMessageWriter.MessageStreamHelperFactory {

    public static final String ROOT_ELEMENT_NAME = "data";

    @Override
    public PagedMessageWriter.MessageStreamHelper newMessageStreamHelper(OutputStream outputStream) {
        return new XmlMessageStreamHelper(outputStream);
    }

    private static class XmlMessageStreamHelper implements PagedMessageWriter.MessageStreamHelper {

        private final OutputStream outputStream;

        private XMLStreamWriter xmlStreamWriter;

        public XmlMessageStreamHelper(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void startMessageStream() throws IOException {
            try {
                xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream);
                xmlStreamWriter.writeStartDocument();
                xmlStreamWriter.writeStartElement(ROOT_ELEMENT_NAME);
                xmlStreamWriter.flush();

            } catch (XMLStreamException e) {
                throw new IOException(e);
            }
        }

        @Override
        public void endMessageStream() throws IOException {
            try {
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeEndDocument();
            } catch (XMLStreamException e) {
                throw new IOException(e);
            }
        }
    }
}
