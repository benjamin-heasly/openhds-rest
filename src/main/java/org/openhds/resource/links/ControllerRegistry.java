package org.openhds.resource.links;

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
 * <p>
 * Discover our REST controllers and which paths and entities they deal with.
 * Unlike Spring EntityLinks, expose this info for our own purposes.
 */
@Component
public class ControllerRegistry {
    private final ApplicationContext applicationContext;

    private final Map<Class<?>, Class<?>> entitiesToControllers = new HashMap<>();
    private final Map<Class<?>, Class<?>> controllersToEntities = new HashMap<>();
    private final Map<Class<?>, String> entitiesToPaths = new HashMap<>();
    private final Map<Class<?>, String> controllersToPaths = new HashMap<>();

    @Autowired
    public ControllerRegistry(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        discoverControllers();
    }

    private static String firstPathComponent(String[] paths) {
        return Paths.get(paths[0]).subpath(0, 1).toString();
    }

    // Search the application context for entities and their controllers.
    private void discoverControllers() {
        for (Class<?> controllerClass : getBeanClassesWithAnnotation(ExposesResourceFor.class)) {
            ExposesResourceFor exposesResourceFor = controllerClass.getAnnotation(ExposesResourceFor.class);
            Class<?> entityClass = exposesResourceFor.value();
            entitiesToControllers.put(entityClass, controllerClass);
            controllersToEntities.put(controllerClass, entityClass);
        }

        for (Class<?> controllerClass : getBeanClassesWithAnnotation(RequestMapping.class)) {
            RequestMapping requestMapping = controllerClass.getAnnotation(RequestMapping.class);
            String controllerPath = firstPathComponent(requestMapping.value());
            Class<?> entityClass = controllersToEntities.get(controllerClass);
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

    public Map<Class<?>, Class<?>> getEntitiesToControllers() {
        return Collections.unmodifiableMap(entitiesToControllers);
    }

    public Map<Class<?>, Class<?>> getControllersToEntities() {
        return Collections.unmodifiableMap(controllersToEntities);
    }

    public Map<Class<?>, String> getEntitiesToPaths() {
        return Collections.unmodifiableMap(entitiesToPaths);
    }

    public Map<Class<?>, String> getControllersToPaths() {
        return Collections.unmodifiableMap(controllersToPaths);
    }

    public <T> T getController(Class<T> controllerClass) {
        return applicationContext.getBean(controllerClass);
    }
}
