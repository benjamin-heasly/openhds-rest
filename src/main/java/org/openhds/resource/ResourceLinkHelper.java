package org.openhds.resource;

import org.openhds.domain.ShallowCopier;
import org.openhds.domain.model.UuidIdentifiable;
import org.openhds.security.model.User;
import org.springframework.hateoas.Resource;

import java.util.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


/**
 * Created by Ben on 5/19/15.
 */
public class ResourceLinkHelper {

    public static final String COLLECTION_REL = "collection";

    private static Map<Class<?>, Class<? extends AbstractRestController>> controllerClasses = new HashMap<>();

    // TODO: This is not DRY.
    // Can we discover the mapping from item class to controller class by reflection?  By inspecting annotations?
    static {
        controllerClasses.put(User.class, UserRestController.class);
    }

    public static <T extends UuidIdentifiable> Resource<T> shallowResourceWithUuidLinks(T item) {

        List<ShallowCopier.StubReference> stubReport = new ArrayList<>();
        T copy = ShallowCopier.makeShallowCopy(item, stubReport);

        Resource<T> resource = new Resource<T>(item);
        addSelfLink(resource);
        addCollectionLink(resource);
        addUuidLinks(resource, stubReport);

        return resource;
    }

    private static <T extends UuidIdentifiable> void addSelfLink(Resource<T> resource) {
        Class<?> controllerClass = controllerClassForItem(resource.getContent());
        resource.add(linkTo(controllerClass).withRel(COLLECTION_REL));
    }

    private static <T extends UuidIdentifiable> void addCollectionLink(Resource<T> resource) {
        UuidIdentifiable item = resource.getContent();
        Class<? extends AbstractRestController> controllerClass = controllerClassForItem(item);
        resource.add(linkTo(methodOn(controllerClass).readOne(item.getUuid())).withSelfRel());
    }

    private static <T extends UuidIdentifiable> void addUuidLinks(Resource<T> resource, List<ShallowCopier.StubReference> stubReport) {
        for (ShallowCopier.StubReference stubReference: stubReport) {
            UuidIdentifiable original = stubReference.getOriginal();
            Class<? extends AbstractRestController> controllerClass = controllerClassForItem(original);
            resource.add(linkTo(methodOn(controllerClass).readOne(original.getUuid())).withRel(stubReference.getFieldName()));
        }
    }

    private static Class<? extends AbstractRestController> controllerClassForItem(UuidIdentifiable item) {
        final Class<?> itemClass = item.getClass();

        if (controllerClasses.containsKey(itemClass)) {
            return controllerClasses.get(itemClass);
        }
        throw new RuntimeException("No Controller exists for items of class: " + itemClass);
    }
}
