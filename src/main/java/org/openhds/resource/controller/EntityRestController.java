package org.openhds.resource.controller;

import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.resource.ResourceLinkAssembler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

/**
 * Created by Ben on 5/18/15.
 *
 * Common interface for REST controllers.
 *
 */
@RestController
public abstract class EntityRestController<T extends UuidIdentifiable> {

    protected final ResourceLinkAssembler resourceLinkAssembler;

    public EntityRestController(ResourceLinkAssembler resourceLinkAssembler) {
        this.resourceLinkAssembler = resourceLinkAssembler;
    }

    protected abstract T findOneCanonical(String id);

    protected abstract Page<T> findPaged(Pageable pageable);

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Resource<T> readOneCanonical(@PathVariable String id) {
        T entity = findOneCanonical(id);
        if (null == entity) {
            throw new NoSuchElementException("No entity found with canonical id: " + id);
        }
        return resourceLinkAssembler.toResource(entity);
    }

    @RequestMapping(method = RequestMethod.GET)
    public PagedResources<T> readAll(Pageable pageable, PagedResourcesAssembler assembler) {
        Page<T> entities = findPaged(pageable);
        return assembler.toResource(entities, resourceLinkAssembler);
    }

}
