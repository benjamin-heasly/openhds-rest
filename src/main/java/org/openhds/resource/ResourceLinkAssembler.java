package org.openhds.resource;

import org.openhds.domain.util.ShallowCopier;
import org.openhds.domain.model.UuidIdentifiable;
import org.openhds.resource.controller.AbstractRestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.core.AnnotationMappingDiscoverer;
import org.springframework.hateoas.core.MappingDiscoverer;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


/**
 * Created by Ben on 5/19/15.
 */
public class ResourceLinkAssembler<T extends UuidIdentifiable> extends ResourceAssemblerSupport<T, Resource> {

    private static final MappingDiscoverer REQUEST_MAPPING_DISCOVERER = new AnnotationMappingDiscoverer(RequestMapping.class);

    private final EntityControllerRegistry entityControllerRegistry;

    public ResourceLinkAssembler(Class<? extends AbstractRestController> controllerClass, EntityControllerRegistry entityControllerRegistry) {
        super(controllerClass, Resource.class);
        this.entityControllerRegistry = entityControllerRegistry;
    }

    public static String getControllerPath(Class<? extends AbstractRestController> controllerClass) {
        String mapping = REQUEST_MAPPING_DISCOVERER.getMapping(controllerClass);
        return mapping.replace("/", "");
    }

    @Override
    public Resource toResource(T entity) {
        List<ShallowCopier.StubReference> stubReport = new ArrayList<>();
        T copy = ShallowCopier.makeShallowCopy(entity, stubReport);

        Resource<T> resource = new Resource<T>(copy);
        addSelfLink(resource);
        addCollectionLink(resource);
        addUuidLinks(resource, stubReport);

        return resource;
    }

    private void addSelfLink(Resource<T> resource) {
        T entity = resource.getContent();
        Class<? extends AbstractRestController> controllerClass = entityControllerRegistry.getControllerClass(entity.getClass());
        resource.add(linkTo(methodOn(controllerClass).readOneCanonical(entity.getUuid())).withSelfRel());
    }

    private void addCollectionLink(Resource<T> resource) {
        T entity = resource.getContent();
        Class<? extends AbstractRestController> controllerClass = entityControllerRegistry.getControllerClass(entity.getClass());
        resource.add(linkTo(controllerClass).withRel(getControllerPath(controllerClass)));
    }

    private void addUuidLinks(Resource<T> resource, List<ShallowCopier.StubReference> stubReport) {
        for (ShallowCopier.StubReference stubReference : stubReport) {
            UuidIdentifiable stub = stubReference.getStub();

            if (entityControllerRegistry.isRegistered(stub.getClass())) {
                Class<? extends AbstractRestController> controllerClass = entityControllerRegistry.getControllerClass(stub.getClass());
                resource.add(linkTo(methodOn(controllerClass).readOneCanonical(stub.getUuid())).withRel(stubReference.getFieldName()));
            }
        }
    }
}
