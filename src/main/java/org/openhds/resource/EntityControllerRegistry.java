package org.openhds.resource;

import org.openhds.domain.model.UuidIdentifiable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ben on 5/20/15.
 *
 * Registry to obtain the controller class associated with a given entity class.
 *
 */
@Component
public class EntityControllerRegistry {

    private Map<Class<? extends UuidIdentifiable>, Class<? extends AbstractRestController>> controllers = new HashMap<>();

    public synchronized void register(Class<? extends UuidIdentifiable> entityClass, Class<? extends AbstractRestController> controllerClass) {
        controllers.put(entityClass, controllerClass);
    }

    public synchronized boolean isRegistered(Class<? extends UuidIdentifiable> entityClass) {
        return controllers.containsKey(entityClass);
    }

    public synchronized Class<? extends AbstractRestController> getControllerClass(Class<? extends UuidIdentifiable> entityClass) {
        if (isRegistered(entityClass)) {
            return controllers.get(entityClass);
        }
        return null;
    }
}
