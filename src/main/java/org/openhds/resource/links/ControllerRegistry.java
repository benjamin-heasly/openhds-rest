package org.openhds.resource.links;

import org.openhds.resource.contract.UuidIdentifiableRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Ben on 6/2/15.
 *
 * Discover our REST controllers and which paths and entities they deal with.
 * Unlike Spring EntityLinks, expose this info for our own purposes.
 *
 */
@Component
public class ControllerRegistry {
    private final ApplicationContext applicationContext;

    private final Map<Class<?>, Class<? extends UuidIdentifiableRestController>> entitiesToControllers = new HashMap<>();
    private final Map<Class<?>, String> entitiesToPaths = new HashMap<>();
    private final Map<Class<? extends UuidIdentifiableRestController>, String> controllersToPaths = new HashMap<>();

    @Autowired
    public ControllerRegistry(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        discoverControllers();
    }

    // Search the application context for entities and their controllers.
    private void discoverControllers() {
        for (Class<?> clazz : getBeanClassesWithAnnotation(ExposesResourceFor.class)) {
            Class<? extends UuidIdentifiableRestController> controllerClass = (Class<? extends UuidIdentifiableRestController>) clazz;

            ExposesResourceFor exposesResourceFor = controllerClass.getAnnotation(ExposesResourceFor.class);
            Class<?> entityClass = exposesResourceFor.value();
            entitiesToControllers.put(entityClass, controllerClass);

            RequestMapping requestMapping = controllerClass.getAnnotation(RequestMapping.class);
            String controllerPath = firstPathComponent(requestMapping.value());
            entitiesToPaths.put(entityClass, controllerPath);
            controllersToPaths.put(controllerClass, controllerPath);
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

    private static String firstPathComponent(String[] paths) {
        return Paths.get(paths[0]).subpath(0, 1).toString();
    }

    public Map<Class<?>, Class<? extends UuidIdentifiableRestController>> getEntitiesToControllers() {
        return Collections.unmodifiableMap(entitiesToControllers);
    }

    public Map<Class<?>, String> getEntitiesToPaths() {
        return Collections.unmodifiableMap(entitiesToPaths);
    }

    public Map<Class<? extends UuidIdentifiableRestController>, String> getControllersToPaths() {
        return Collections.unmodifiableMap(controllersToPaths);
    }

    public <T extends UuidIdentifiableRestController> T getController(Class<T> controllerClass) {
        return applicationContext.getBean(controllerClass);
    }
}
