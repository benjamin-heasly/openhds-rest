package org.openhds.resource;

import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.domain.util.ShallowCopier;
import org.openhds.resource.controller.EntityRestController;
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
    public String getControllerRel(Class<?> entityClass) {
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

        // add basic links
        resource.add(entityLinks.linkToSingleResource(copy.getClass(), copy.getUuid()));
        resource.add(entityLinks.linkToCollectionResource(copy.getClass()).withRel(getControllerRel(copy.getClass())));

        return resource;
    }

    private void addStubLinks(Resource<?> resource, List<ShallowCopier.StubReference> stubReport) {
        for (ShallowCopier.StubReference stubReference : stubReport) {
            UuidIdentifiable stub = stubReference.getStub();

            if (entityLinks.supports(stub.getClass())) {
                resource.add(entityLinks.linkToSingleResource(stub.getClass(), stub.getUuid()).withRel(stubReference.getFieldName()));
            }
        }
    }
}
