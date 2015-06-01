package org.openhds.resource;

import org.openhds.domain.contract.ExtIdIdentifiable;
import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.domain.util.ShallowCopier;
import org.openhds.resource.controller.EntityRestController;
import org.openhds.resource.controller.ExtIdRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


/**
 * Created by Ben on 5/19/15.
 */
@Component
public class ResourceLinkAssembler extends ResourceAssemblerSupport<UuidIdentifiable, Resource> {

    private final EntityLinks entityLinks;

    private final ApplicationContext applicationContext;

    private final Map<Class<?>, Class<?>> entitiesToControllers = new HashMap<>();

    @Autowired
    public ResourceLinkAssembler(EntityLinks entityLinks, ApplicationContext applicationContext) {
        super(EntityRestController.class, Resource.class);
        this.entityLinks = entityLinks;
        this.applicationContext = applicationContext;
        discoverEntitiesAndControllers();
    }

    // Use collection request mapping as the controller's "rel".
    public String getControllerRelName(Class<?> entityClass) {
        Link link = entityLinks.linkToCollectionResource(entityClass);
        String[] pathParts = link.getHref().split("/");
        return pathParts[pathParts.length - 1];
    }

    // Search the application context for entities and their controllers.
    private void discoverEntitiesAndControllers() {
        for (Class<?> controllerClass : getBeanClassesWithAnnotation(ExposesResourceFor.class)) {
            ExposesResourceFor exposesResourceFor = controllerClass.getAnnotation(ExposesResourceFor.class);
            entitiesToControllers.put(exposesResourceFor.value(), controllerClass);
        }
    }

    // Search the application context for annotated classes.
    private Iterable<Class<?>> getBeanClassesWithAnnotation(Class<? extends Annotation> annotationClass) {
        Set<Class<?>> annotatedClasses = new HashSet<Class<?>>();
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            Annotation annotation = applicationContext.findAnnotationOnBean(beanName, annotationClass);
            if (annotation != null) {
                annotatedClasses.add(applicationContext.getType(beanName));
            }
        }
        return annotatedClasses;
    }

    public Map<Class<?>, Class<?>> getEntitiesToControllers() {
        return new HashMap<>(entitiesToControllers);
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

    private void addStubLinks(Resource<?> resource, List<ShallowCopier.StubReference> stubReport) {
        for (ShallowCopier.StubReference stubReference : stubReport) {
            UuidIdentifiable stub = stubReference.getStub();

            if (entityLinks.supports(stub.getClass())) {
                addStubLink(resource, stub, stubReference.getFieldName());
            }
        }
    }

    protected void addSelfLink(Resource resource, UuidIdentifiable entity) {
        resource.add(entityLinks.linkToSingleResource(entity.getClass(), entity.getUuid()));

        // TODO: Gross.  Reimplement with dynamic dispatch?
        if (entity instanceof ExtIdIdentifiable) {
            ExtIdIdentifiable extIdIdentifiable = (ExtIdIdentifiable) entity;
            Class<ExtIdRestController> extIdController = (Class<ExtIdRestController>) entitiesToControllers.get(entity.getClass());
            resource.add(linkTo(methodOn(extIdController).readByExtId(extIdIdentifiable.getExtId())).withRel("self-external"));
        }
    }

    private void addCollectionLink(Resource resource, UuidIdentifiable entity) {
        resource.add(entityLinks.linkToCollectionResource(entity.getClass()).withRel(getControllerRelName(entity.getClass())));
    }

    private void addStubLink(Resource resource, UuidIdentifiable entity, String relName) {
        resource.add(entityLinks.linkToSingleResource(entity.getClass(), entity.getUuid()).withRel(relName));
    }
}
