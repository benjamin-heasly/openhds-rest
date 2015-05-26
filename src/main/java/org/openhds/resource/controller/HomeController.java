package org.openhds.resource.controller;

import org.openhds.resource.EntityControllerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.OffsetDateTime;

import static org.openhds.resource.ResourceLinkAssembler.getControllerPath;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by Ben on 5/20/15.
 *
 * Entry point for clients connecting to OpenHDS.  Points to known resources.
 *
 */
@RestController
public class HomeController {

    private static final String CONTENTS_REL = "contents";

    private final EntityControllerRegistry entityControllerRegistry;

    @Autowired
    public HomeController(EntityControllerRegistry entityControllerRegistry) {
        this.entityControllerRegistry = entityControllerRegistry;
    }

    @RequestMapping("/")
    public Resource<String> home() {
        Resource<String> resource = new Resource<>("Welcome to OpenHDS.  The Current time UTC is " + getNowString());

        addSelfLink(resource);
        addControllerLinks(resource);

        return resource;
    }

    private String getNowString(){
        OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
        return now.toString();
    }

    private void addSelfLink(Resource<String> resource) {
        resource.add(linkTo(methodOn(HomeController.class).home()).withSelfRel());
        resource.add(linkTo(methodOn(HomeController.class).home()).withRel(CONTENTS_REL));
    }

    private void addControllerLinks(Resource<String> resource) {
        for (Class<? extends AbstractRestController> controllerClass : entityControllerRegistry.getControllers().values()) {
            // using controller class-level request mapping as the HATEOAS "rel" name
            resource.add(linkTo(controllerClass).withRel(getControllerPath(controllerClass)));
        }
    }
}
