package org.openhds.resource.links;

import org.openhds.domain.contract.ExtIdIdentifiable;
import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.domain.util.ShallowCopier;
import org.openhds.resource.controller.EntityRestController;
import org.openhds.resource.controller.ExtIdRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


/**
 * Created by Ben on 5/19/15.
 */
@Component
public class ResourceLinkAssembler extends ResourceAssemblerSupport<UuidIdentifiable, Resource> {

    private final EntityLinks entityLinks;

    private final ControllerRegistry controllerRegistry;

    @Autowired
    public ResourceLinkAssembler(EntityLinks entityLinks, ControllerRegistry controllerRegistry) {
        super(EntityRestController.class, Resource.class);
        this.controllerRegistry = controllerRegistry;
        this.entityLinks = entityLinks;
    }

    @Override
    public Resource toResource(UuidIdentifiable entity) {
        // make a shallow copy of entity with stubs instead of a deep object graph
        List<ShallowCopier.StubReference> stubReport = new ArrayList<>();
        UuidIdentifiable copy = ShallowCopier.makeShallowCopy(entity, stubReport);

        // make a link to each stub object
        Resource<UuidIdentifiable> resource = new Resource<>(copy);
        addStubLinks(resource, stubReport);

        addSelfLink(resource, copy);
        addCollectionLink(resource, copy);

        return resource;
    }

    public <T extends UuidIdentifiable> Resources<?> wrapCollection(Iterable<T> entities) {
        ArrayList<Resource> resourceList = new ArrayList<>();
        for (T entity : entities) {
            resourceList.add(toResource(entity));
        }
        return new Resources<>(resourceList);
    }

    private void addStubLinks(Resource<?> resource, List<ShallowCopier.StubReference> stubReport) {
        for (ShallowCopier.StubReference stubReference : stubReport) {
            UuidIdentifiable stub = stubReference.getStub();

            if (entityLinks.supports(stub.getClass())) {
                addStubLink(resource, stub, stubReference.getFieldName());
            }
        }
    }

    protected void addSelfLink(ResourceSupport resourceSupport, UuidIdentifiable entity) {
        resourceSupport.add(entityLinks.linkToSingleResource(entity.getClass(), entity.getUuid()));

        // TODO: Replace instanceof with overridden method on...something.
        if (entity instanceof ExtIdIdentifiable) {
            ExtIdIdentifiable extIdIdentifiable = (ExtIdIdentifiable) entity;
            Class<ExtIdRestController> controllerClass =
                    (Class<ExtIdRestController>) controllerRegistry.getEntitiesToControllers().get(entity.getClass());
            addByExtIdLink(resourceSupport, controllerClass, extIdIdentifiable.getExtId(), "self-external");
        }
    }

    public void addCollectionLink(ResourceSupport resourceSupport, UuidIdentifiable entity) {
        Class<?> entityClass = entity.getClass();
        resourceSupport.add(entityLinks.linkToCollectionResource(entityClass).withRel(controllerRegistry.getEntitiesToPaths().get(entityClass)));
    }

    public void addStubLink(ResourceSupport resourceSupport, UuidIdentifiable entity, String relName) {
        resourceSupport.add(entityLinks.linkToSingleResource(entity.getClass(), entity.getUuid()).withRel(relName));
    }

    public void addByExtIdLink(ResourceSupport resourceSupport,
                                Class<? extends ExtIdRestController> controllerClass,
                                String extId,
                                String relName) {
        resourceSupport.add(linkTo(methodOn(controllerClass)
                .readByExtId(extId))
                .withRel(relName));
    }
}
