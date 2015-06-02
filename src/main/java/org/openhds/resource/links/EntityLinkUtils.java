package org.openhds.resource.links;

import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.domain.util.ShallowCopier;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 6/2/15.
 *
 * Static utilities for building HATEOAS links around OpenHDS entities.
 *
 * Stick to vague super types when possible, like UuidIdentifiable and ResourceSupport.  Let callers refine them.
 *
 * These use EntityLinks for locating controller endpoints based on entity types.
 *
 */
public class EntityLinkUtils {

    // Wrap an OpenHDS entity in a HATEOAS resource.
    public static <T extends UuidIdentifiable> Resource<T> toResource(T entity, EntityLinks entityLinks, String collectionName) {
        // make a shallow copy of entity with stubs instead of a deep object graph
        List<ShallowCopier.StubReference> stubReport = new ArrayList<>();
        T copy = ShallowCopier.makeShallowCopy(entity, stubReport);

        // make a link to each stub object
        Resource<T> resource = new Resource<>(copy);
        addStubLinks(stubReport, entityLinks, resource);
        addSelfLink(copy, entityLinks, resource);
        addCollectionLink(copy, entityLinks, resource, collectionName);

        return resource;
    }

    // Wrap multiple OpenHDS entities in multiple HATEOAS resources, and wrap all of that in one big HATEOAS resources.
    public static <T extends UuidIdentifiable> Resources<?> wrapCollection(Iterable<T> entities, EntityLinks entityLinks, String collectionName) {
        List<Resource<T>> resourceList = new ArrayList<>();
        for (T entity : entities) {
            resourceList.add(toResource(entity, entityLinks, collectionName));
        }
        return new Resources<>(resourceList);
    }

    // Add the "self" link to an entity's canonical URI.
    public static void addSelfLink(UuidIdentifiable entity, EntityLinks entityLinks, ResourceSupport resource) {
        resource.add(entityLinks.linkToSingleResource(entity.getClass(), entity.getUuid()));
    }

    // Add a collection link to where we can find more similar entities.
    public static void addCollectionLink(UuidIdentifiable entity, EntityLinks entityLinks, ResourceSupport resource, String relName) {
        Class<?> entityClass = entity.getClass();
        resource.add(entityLinks.linkToCollectionResource(entityClass).withRel(relName));
    }

    // Add links to each "stub" object reachable from an entity.  For example, the "insertBy" user.
    public static void addStubLinks(List<ShallowCopier.StubReference> stubReport, EntityLinks entityLinks, ResourceSupport resource) {
        for (ShallowCopier.StubReference stubReference : stubReport) {
            UuidIdentifiable stub = stubReference.getStub();
            if (entityLinks.supports(stub.getClass())) {
                addStubLink(stub, entityLinks, resource, stubReference.getFieldName());
            }
        }
    }

    // Add a link to a "stub" object reachable from an entity.  For example, the "insertBy" user.
    public static void addStubLink(UuidIdentifiable entity, EntityLinks entityLinks, ResourceSupport resource, String relName) {
        resource.add(entityLinks.linkToSingleResource(entity.getClass(), entity.getUuid()).withRel(relName));
    }

}
