package org.openhds.documentation;

import org.apache.commons.io.IOUtils;
import org.pegdown.PegDownProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created by ben on 8/13/15.
 */
@Repository
public class DocumentationRepository {

    public static final String DOCUMENTATION_PATH = "documentation/";

    private static final ClassLoader classLoader = DocumentationRepository.class.getClassLoader();

    private final PegDownProcessor pegDownProcessor;

    @Autowired
    public DocumentationRepository(PegDownProcessor pegDownProcessor) {
        this.pegDownProcessor = pegDownProcessor;
    }

    public String getHtmlByName(String name) throws IOException, URISyntaxException {
        InputStream markdownFile = getResourceStream(DOCUMENTATION_PATH + name + ".md");
        String markdown = readStream(markdownFile, StandardCharsets.UTF_8.name());
        return pegDownProcessor.markdownToHtml(markdown);
    }

    private static InputStream getResourceStream(String name) {
        InputStream inputStream = classLoader.getResourceAsStream(name);
        if (null == inputStream) {
            throw new NoSuchElementException("Could not get resource: " + name);
        }
        return inputStream;
    }

    static String readStream(InputStream stream, String charsetName) throws IOException {
        return IOUtils.toString(stream, charsetName);
    }
}
