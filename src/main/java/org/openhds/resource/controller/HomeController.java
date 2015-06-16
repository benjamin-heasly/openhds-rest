package org.openhds.resource.controller;

import org.openhds.resource.links.ControllerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.ZonedDateTime;

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

    private final EntityLinks entityLinks;

    private final ControllerRegistry controllerRegistry;

    @Autowired
    public HomeController(EntityLinks entityLinks,
                          ControllerRegistry controllerRegistry) {
        this.entityLinks = entityLinks;
        this.controllerRegistry = controllerRegistry;
    }

    @RequestMapping("/")
    public Resource<String> home() {
        Resource<String> resource = new Resource<>("Welcome to OpenHDS.  The Current time UTC is " + getNowString());

        addSelfLink(resource);
        addControllerLinks(resource);

        return resource;
    }

    private String getNowString(){
        ZonedDateTime now = ZonedDateTime.now(Clock.systemUTC());
        return now.toString();
    }

    private void addSelfLink(Resource<String> resource) {
        resource.add(linkTo(methodOn(HomeController.class).home()).withSelfRel());
    }

    private void addControllerLinks(Resource<String> resource) {
        for (Class<?> entityClass : controllerRegistry.getEntitiesToControllers().keySet()) {
            resource.add(entityLinks.linkToCollectionResource(entityClass)
                    .withRel(controllerRegistry.getEntitiesToPaths().get(entityClass)));
        }
    }
}
