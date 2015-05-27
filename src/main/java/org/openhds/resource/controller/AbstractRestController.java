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

/**
 * Created by Ben on 5/18/15.
 *
 * Common interface for REST controllers.
 *
 * readOneCanonical() is required for automatic HATEOAS link building.
 *
 */
@RestController
public abstract class AbstractRestController <T extends UuidIdentifiable> {

    protected final ResourceLinkAssembler resourceLinkAssembler;

    public AbstractRestController(ResourceLinkAssembler resourceLinkAssembler) {
        this.resourceLinkAssembler = resourceLinkAssembler;
    }

    protected abstract T findOne(String id);

    protected abstract Page<T> findPaged(Pageable pageable);

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Resource<T> readOneCanonical(@PathVariable String id) {
        T entity = findOne(id);
        return resourceLinkAssembler.toResource(entity);
    }

    @RequestMapping(method = RequestMethod.GET)
    public PagedResources<T> readAll(Pageable pageable, PagedResourcesAssembler assembler) {
        Page<T> entities = findPaged(pageable);
        return assembler.toResource(entities, resourceLinkAssembler);
    }

}
