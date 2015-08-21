package org.openhds.resource.links;

import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.domain.util.ShallowCopier;
import org.openhds.resource.contract.UuidIdentifiableRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 6/2/15.
 */
@Component
public class EntityLinkAssembler implements ResourceAssembler<UuidIdentifiable, Resource> {

    public static final String REL_COLLECTION = "collection";

    private final EntityLinks entityLinks;

    private final ControllerRegistry controllerRegistry;

    @Autowired
    public EntityLinkAssembler(EntityLinks entityLinks, ControllerRegistry controllerRegistry) {
        this.entityLinks = entityLinks;
        this.controllerRegistry = controllerRegistry;
    }

    @Override
    public Resource toResource(UuidIdentifiable entity) {
        // make a shallow copy of entity with stubs instead of a deep object graph
        List<ShallowCopier.StubReference> stubReport = new ArrayList<>();
        UuidIdentifiable copy = ShallowCopier.makeShallowCopy(entity, stubReport);

        // make a link to each stub object
        Resource<?> resource = new Resource<>(copy);
        addStubLinks(stubReport, resource);
        addSelfLink(copy, resource);
        addCollectionLink(copy, resource);
        addSingleResourceLinks(copy, resource);

        return resource;
    }

    public Link getCollectionLink(Class<?> entityClass) {
        return entityLinks.linkToCollectionResource(entityClass);
    }

    // Add the "self" link to an entity's canonical URI.
    private void addSelfLink(UuidIdentifiable entity, Resource resource) {
        resource.add(entityLinks.linkToSingleResource(entity.getClass(), entity.getUuid()));
    }

    // Add a collection link to where we can find more similar entities.
    private void addCollectionLink(UuidIdentifiable entity, Resource resource) {
        resource.add(getCollectionLink(entity.getClass()).withRel(REL_COLLECTION));
    }

    // Add links to each "stub" object reachable from an entity.  For example, the "insertBy" user.
    private void addStubLinks(List<ShallowCopier.StubReference> stubReport, Resource resource) {
        for (ShallowCopier.StubReference stubReference : stubReport) {
            UuidIdentifiable stub = stubReference.getStub();
            if (entityLinks.supports(stub.getClass())) {
                addStubLink(stub, resource, stubReference.getFieldName());
            }
        }
    }

    // Add a link to a "stub" object reachable from an entity.  For example, the "insertBy" user.
    private void addStubLink(UuidIdentifiable entity, Resource resource, String relName) {
        resource.add(entityLinks.linkToSingleResource(entity.getClass(), entity.getUuid()).withRel(relName.toLowerCase()));
    }

    // Let an entity's controller add entity-specific links if it wants.
    private void addSingleResourceLinks(UuidIdentifiable entity, Resource resource) {
        Class<?> controllerClass = controllerRegistry.getEntitiesToControllers().get(entity.getClass());

        // controller registry only holds UuidIdentifiableRestController
        UuidIdentifiableRestController controller = (UuidIdentifiableRestController) controllerRegistry.getController(controllerClass);
        controller.addSingleResourceLinks(resource);
    }
}
