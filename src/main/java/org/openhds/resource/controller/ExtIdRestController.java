package org.openhds.resource.controller;

import org.openhds.domain.contract.ExtIdIdentifiable;
import org.openhds.resource.ResourceLinkAssembler;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Ben on 6/1/15.
 */
public abstract class ExtIdRestController <T extends ExtIdIdentifiable> extends EntityRestController<T> {

    public ExtIdRestController(ResourceLinkAssembler resourceLinkAssembler) {
        super(resourceLinkAssembler);
    }

    protected abstract List<T> findByExtId(String id);

    @RequestMapping(value = "/external/{id}", method = RequestMethod.GET)
    public Resources<?> readByExtId(@PathVariable String id) {
        List<T> entities = findByExtId(id);
        if (null == entities) {
            throw new NoSuchElementException("No entities found with external id: " + id);
        }
        Resources<?> resources = resourceLinkAssembler.wrapCollection(entities);
        resourceLinkAssembler.addByExtIdLink(resources, this.getClass(), id, "self");
        return resources;
    }

}
