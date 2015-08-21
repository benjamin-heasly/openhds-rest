package org.openhds.resource.contract;

import org.openhds.domain.contract.AuditableExtIdEntity;
import org.openhds.domain.contract.ExtIdIdentifiable;
import org.openhds.repository.contract.AuditableExtIdRepository;
import org.openhds.resource.registration.Registration;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.NoSuchElementException;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by Ben on 6/1/15.
 */
public abstract class AuditableExtIdRestController<
        T extends AuditableExtIdEntity,
        U extends Registration<T>,
        V extends AbstractAuditableExtIdService<T, ? extends AuditableExtIdRepository<T>>>
        extends AuditableCollectedRestController<T, U, V> {

    public static final String REL_SECTION = "section";

    private final V service;

    public AuditableExtIdRestController(V service) {
        super(service);
        this.service = service;
    }

    @RequestMapping(value = "/external/{id}", method = RequestMethod.GET)
    public Resources<?> readByExtId(Pageable pageable,
                                    PagedResourcesAssembler assembler,
                                    @PathVariable String id) {

        Page<T> entities = service.findByExtId(pageable, id);

        if (null == entities || 0 == entities.getTotalElements()) {
            throw new NoSuchElementException("No entities found with external id: " + id);
        }

        return toResource(entities, assembler, byExtIdLink(id, Link.REL_SELF));
    }

    private Link byExtIdLink(String extId, String relName) {
        return linkTo(methodOn(this.getClass()).readByExtId(null, null, extId)).withRel(relName);
    }

    @Override
    public void addSingleResourceLinks(Resource resource) {
        ExtIdIdentifiable entity = (ExtIdIdentifiable) resource.getContent();
        resource.add(byExtIdLink(entity.getExtId(), REL_SECTION));
    }

}
