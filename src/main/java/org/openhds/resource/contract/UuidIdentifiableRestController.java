package org.openhds.resource.contract;

import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.repository.UuidIdentifiableRepository;
import org.openhds.repository.results.PageIterator;
import org.openhds.resource.links.EntityLinkAssembler;
import org.openhds.resource.registration.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;

/**
 * Created by Ben on 5/18/15.
 * <p>
 * Common interface for REST controllers.
 */
@RestController
public abstract class UuidIdentifiableRestController<T extends UuidIdentifiable, U extends Registration<T>> {

    @Autowired
    protected EntityLinkAssembler entityLinkAssembler;

    private final UuidIdentifiableRepository<T> repository;

    public UuidIdentifiableRestController(UuidIdentifiableRepository<T> repository) {
        this.repository = repository;
    }

    // templates to be implemented with entity services, etc.
    protected abstract T register(U registration);
    protected abstract T register(U registration, String id);
    protected abstract void removeOneCanonical(String id, String reason);

    protected Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    protected T findOne(String id) {
        return repository.findOne(id);
    }

    // optionally add entity-specific links to a HATEOAS resource
    public void supplementResource(Resource resource) {
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Resource<?> readOneCanonical(@PathVariable String id) {
        T entity = findOne(id);
        if (null == entity) {
            throw new NoSuchElementException("No entity found with canonical id: " + id);
        }
        return entityLinkAssembler.toResource(entity);
    }

    @RequestMapping(method = RequestMethod.GET)
    public PagedResources readPaged(Pageable pageable, PagedResourcesAssembler assembler) {
        Page<T> entities = findAll(pageable);
        return assembler.toResource(entities, entityLinkAssembler);
    }

    // TODO: should just expose a Sort, not a Pageable.  Make Pageable an implementation detail.
    @RequestMapping(value = "/bulk", method = RequestMethod.GET)
    public PageIterator<T> readBulk(Pageable pageable) {
        // TODO: use resource name, not controller class name
        // TODO: how to expose only shallow copies?
        return new PageIterator<>(repository, pageable, this.getClass().getSimpleName());
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    Resource insert(@RequestBody U registration, HttpServletResponse response) {
        T entity = register(registration);
        addLocationHeader(response, entity);
        return entityLinkAssembler.toResource(entity);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.CREATED)
    Resource replace(@RequestBody U registration, @PathVariable String id) {
        T entity = register(registration, id);
        return entityLinkAssembler.toResource(entity);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOneCanonical(@PathVariable String id, @RequestParam(required = false) String reason) {
        removeOneCanonical(id, reason);
    }

    private void addLocationHeader(HttpServletResponse response, T entity) {
        response.setHeader(HttpHeaders.LOCATION, ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(entity.getUuid()).toUri().toString());
    }
}
