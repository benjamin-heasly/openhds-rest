package org.openhds.resource.controller;

import org.openhds.resource.ResourceLinkAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.OffsetDateTime;

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

    private final EntityLinks entityLinks;

    private final ResourceLinkAssembler resourceLinkAssembler;

    @Autowired
    public HomeController(EntityLinks entityLinks,
                          ResourceLinkAssembler resourceLinkAssembler) {
        this.entityLinks = entityLinks;
        this.resourceLinkAssembler = resourceLinkAssembler;
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
        for (Class<?> entityClass : resourceLinkAssembler.getEntitiesToControllers().keySet()) {
            resource.add(entityLinks.linkToCollectionResource(entityClass)
                    .withRel(resourceLinkAssembler.getControllerRelName(entityClass)));
        }
    }
}
