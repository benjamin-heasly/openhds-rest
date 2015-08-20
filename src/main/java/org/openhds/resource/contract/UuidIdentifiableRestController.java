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
import java.util.List;
import java.util.NoSuchElementException;

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

    // optionally add entity-specific links to a HATEOAS resource
    public void supplementResource(Resource resource) {
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
        return assembler.toResource(entities, entityLinkAssembler);
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

    private <U extends UuidIdentifiableRestController> Link buildTemplatedLink(Class<U> controllerClass) {
        Link link = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(controllerClass).readPaged(null, null)).withRel("yourRel");
        DummyInvocationUtils.LastInvocationAware invocations = (DummyInvocationUtils.LastInvocationAware) ControllerLinkBuilder.methodOn(controllerClass).readPaged(null, null);
        DummyInvocationUtils.MethodInvocation invocation = invocations.getLastInvocation();
        Method method = invocation.getMethod();

        //taken from ControllerLinkBuilder
        MappingDiscoverer discoverer = new AnnotationMappingDiscoverer(RequestMapping.class);
        String mapping = discoverer.getMapping(controllerClass, method);

        UriTemplate uriTemplate = new UriTemplate(mapping);
        List<TemplateVariable> variables = link.getVariables();

        //the templated link
        Link templatedLink = new Link(uriTemplate.with(new TemplateVariables(variables)), link.getRel());
        return templatedLink;
    }
}
