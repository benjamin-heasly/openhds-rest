package org.openhds.resource;

import org.openhds.domain.ShallowCopier;
import org.openhds.domain.model.UuidIdentifiable;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


/**
 * Created by Ben on 5/19/15.
 */
public class ResourceLinkAssembler<T extends UuidIdentifiable> extends ResourceAssemblerSupport<T, Resource> {

    private final EntityControllerRegistry entityControllerRegistry;

    public ResourceLinkAssembler(Class<? extends AbstractRestController> controllerClass, EntityControllerRegistry entityControllerRegistry) {
        super(controllerClass, Resource.class);
        this.entityControllerRegistry = entityControllerRegistry;
    }

    @Override
    public Resource toResource(T entity) {
        List<ShallowCopier.StubReference> stubReport = new ArrayList<>();
        T copy = ShallowCopier.makeShallowCopy(entity, stubReport);

        Resource<T> resource = new Resource<T>(copy);
        addUuidLinks(resource, stubReport);

        return resource;
    }

    private void addUuidLinks(Resource<T> resource, List<ShallowCopier.StubReference> stubReport) {
        for (ShallowCopier.StubReference stubReference : stubReport) {
            UuidIdentifiable stub = stubReference.getStub();

            if (entityControllerRegistry.isRegistered(stub.getClass())) {
                Class<? extends AbstractRestController> controllerClass = entityControllerRegistry.getControllerClass(stub.getClass());
                resource.add(linkTo(methodOn(controllerClass).readOne(stub.getUuid())).withRel(stubReference.getFieldName()));
            }
        }
    }
}
