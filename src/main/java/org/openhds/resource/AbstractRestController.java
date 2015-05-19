package org.openhds.resource;

import org.openhds.domain.model.UuidIdentifiable;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Ben on 5/18/15.
 *
 * Common interface for REST controllers.
 *
 * readOne() and readAll() are required for automatic HATEOAS link building.
 *
 * I expect to add utility fields, but maybe this will turn out to be a plain Interface.
 *
 */
@RestController
public abstract class AbstractRestController <T extends UuidIdentifiable> {

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public abstract Resource<T> readOne(@PathVariable String id);

    @RequestMapping(method = RequestMethod.GET)
    public abstract List<T> readAll();
}
