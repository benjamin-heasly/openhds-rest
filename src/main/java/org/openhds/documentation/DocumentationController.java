package org.openhds.documentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Ben on 8/13/15.
 * <p>
 * Serve documentation about openhds-rest.
 */
@Controller
@RequestMapping("/documentation")
public class DocumentationController {

    private final DocumentationRepository documentationRepository;

    @Autowired
    public DocumentationController(DocumentationRepository documentationRepository) {
        this.documentationRepository = documentationRepository;
    }

    @RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    String readDocumentation(@PathVariable("name") String name) throws IOException, URISyntaxException {
        return documentationRepository.getHtmlByName(name);
    }

}
