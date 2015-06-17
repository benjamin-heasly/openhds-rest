package org.openhds.resource.contract;

import org.openhds.domain.contract.AuditableExtIdEntity;
import org.openhds.domain.contract.ExtIdIdentifiable;
import org.openhds.resource.registration.Registration;
import org.openhds.resource.links.EntityLinkAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by Ben on 6/1/15.
 */
public abstract class AuditableExtIdRestController<T extends AuditableExtIdEntity, U extends Registration<T>>
        extends AuditableCollectedRestController<T, U> {

    public static final String REL_SECTION = "section";

    public AuditableExtIdRestController(EntityLinkAssembler entityLinkAssembler) {
        super(entityLinkAssembler);
    }

    protected abstract List<T> findByExtId(String id);

    @RequestMapping(value = "/external/{id}", method = RequestMethod.GET)
    public Resources<?> readByExtId(@PathVariable String id) {
        List<T> entities = findByExtId(id);
        if (null == entities || 0 == entities.size()) {
            throw new NoSuchElementException("No entities found with external id: " + id);
        }
        Resources resources = entityLinkAssembler.wrapCollection(entities);
        addByExtIdLink(resources, id, Link.REL_SELF);
        addCollectionLink(resources);
        return resources;
    }

    private void addByExtIdLink(ResourceSupport resource, String extId, String relName) {
        resource.add(linkTo(methodOn(this.getClass()).readByExtId(extId)).withRel(relName));
    }

    private void addCollectionLink(ResourceSupport resource) {
        resource.add(linkTo(this.getClass()).withRel(EntityLinkAssembler.REL_COLLECTION));
    }

    @Override
    public void supplementResource(Resource resource) {
        ExtIdIdentifiable entity = (ExtIdIdentifiable) resource.getContent();
        addByExtIdLink(resource, entity.getExtId(), REL_SECTION);
    }
}
