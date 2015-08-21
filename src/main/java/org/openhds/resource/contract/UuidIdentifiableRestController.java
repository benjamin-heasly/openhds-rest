package org.openhds.resource.contract;

import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.repository.contract.UuidIdentifiableRepository;
import org.openhds.repository.results.EntityIterator;
import org.openhds.repository.results.PageIterator;
import org.openhds.repository.results.PagingEntityIterator;
import org.openhds.repository.results.ShallowCopyIterator;
import org.openhds.resource.links.ControllerRegistry;
import org.openhds.resource.links.EntityLinkAssembler;
import org.openhds.resource.registration.Registration;
import org.openhds.service.contract.AbstractUuidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.hateoas.core.AnnotationMappingDiscoverer;
import org.springframework.hateoas.core.DummyInvocationUtils;
import org.springframework.hateoas.core.MappingDiscoverer;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by Ben on 5/18/15.
 * <p>
 * Common interface for REST controllers.
 */
@RestController
public abstract class UuidIdentifiableRestController<
        T extends UuidIdentifiable,
        U extends Registration<T>,
        V extends AbstractUuidService<T, ? extends UuidIdentifiableRepository<T>>> {

    public static final String REL_BULK = "bulk";

    private final V service;

    @Autowired
    protected EntityLinkAssembler entityLinkAssembler;

    @Autowired
    protected ControllerRegistry controllerRegistry;

    public UuidIdentifiableRestController(V service) {
        this.service = service;
    }

    public String getResourceName() {
        return controllerRegistry.getControllersToPaths().get(this.getClass());
    }

    public Class<T> getEntityClass() {
        return (Class<T>) controllerRegistry.getControllersToEntities().get(this.getClass());
    }

    // templates to be implemented with entity services, etc.
    protected abstract T register(U registration);

    protected abstract T register(U registration, String id);

    // add links to a single-record resource
    public void addSingleResourceLinks(Resource resource) {
    }

    // add links to a collection resource
    public void addCollectionResourceLinks(ResourceSupport resource) {
        resource.add(linkTo(this.getClass()).withRel(EntityLinkAssembler.REL_COLLECTION));
        resource.add(linkTo(methodOn(this.getClass()).readBulk(null)).withRel(REL_BULK));
    }

    // convert a page of results to a resource with links, using the given self link
    public PagedResources<T> toResource(Page<T> entities, PagedResourcesAssembler assembler, Link selfLink) {
        PagedResources<T> pagedResources = assembler.toResource(entities, entityLinkAssembler, selfLink);
        addCollectionResourceLinks(pagedResources);
        return pagedResources;
    }

    // convert a page of results to a resource with links, using the default self link
    public PagedResources<T> toResource(Page<T> entities, PagedResourcesAssembler assembler) {
        PagedResources<T> pagedResources = assembler.toResource(entities, entityLinkAssembler);
        addCollectionResourceLinks(pagedResources);
        return pagedResources;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Resource<?> readOneCanonical(@PathVariable String id) {
        T entity = service.findOne(id);
        if (null == entity) {
            throw new NoSuchElementException("No entity found with canonical id: " + id);
        }
        return entityLinkAssembler.toResource(entity);
    }

    @RequestMapping(method = RequestMethod.GET)
    public PagedResources readPaged(Pageable pageable, PagedResourcesAssembler assembler) {
        Page<T> entities = service.findAll(pageable);
        return toResource(entities, assembler);
    }

    @RequestMapping(value = "/bulk", method = RequestMethod.GET)
    public EntityIterator<T> readBulk(Sort sort) {
        PageIterator<T> pageIterator = new PageIterator<>(service::findAll, sort);
        EntityIterator<T> entityIterator = new PagingEntityIterator<>(pageIterator);
        entityIterator.setCollectionName(getResourceName());
        return new ShallowCopyIterator<>(entityIterator);
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
        service.delete(id, reason);
    }

    private void addLocationHeader(HttpServletResponse response, T entity) {
        response.setHeader(HttpHeaders.LOCATION, ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(entity.getUuid()).toUri().toString());
    }

    protected static Link withTemplateParams(Link link, String ... paramNames) {
        List<TemplateVariable> templateVariables = new ArrayList<>();
        templateVariables.addAll(link.getVariables());
        for (String name : paramNames) {
            templateVariables.add(new TemplateVariable(name, TemplateVariable.VariableType.REQUEST_PARAM));
        }
        UriTemplate uriTemplate = new UriTemplate(link.getHref(), new TemplateVariables(templateVariables));
        return new Link(uriTemplate, link.getRel());
    }
}
