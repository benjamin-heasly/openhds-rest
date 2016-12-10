package org.openhds.resource.contract;

import org.openhds.domain.contract.AuditableExtIdEntity;
import org.openhds.domain.contract.ExtIdIdentifiable;
import org.openhds.domain.util.ExtIdGenerator;
import org.openhds.repository.contract.AuditableExtIdRepository;
import org.openhds.repository.results.EntityIterator;
import org.openhds.repository.results.PageIterator;
import org.openhds.repository.results.PagingEntityIterator;
import org.openhds.repository.results.ShallowCopyIterator;
import org.openhds.resource.registration.Registration;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.alps.Ext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;
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

    private final ExtIdGenerator extIdGenerator;

    public AuditableExtIdRestController(V service, ExtIdGenerator extIdGenerator) {
        super(service);
        this.service = service;
        this.extIdGenerator = extIdGenerator;
    }

    @RequestMapping(value = "/external/{id}", method = RequestMethod.GET)
    public EntityIterator<T>  readByExtId(@PathVariable String id) {


        EntityIterator<T> entityIterator =  service.findByExtId(new Sort("extId"), id);
        entityIterator.setCollectionName(getResourceName());
        return new ShallowCopyIterator<>(entityIterator);

    }

    private Link byExtIdLink(String extId, String relName) {
        return linkTo(methodOn(this.getClass()).readByExtId(extId)).withRel(relName);
    }

    @Override
    public void addSingleResourceLinks(Resource resource) {
        ExtIdIdentifiable entity = (ExtIdIdentifiable) resource.getContent();
        resource.add(byExtIdLink(entity.getExtId(), REL_SECTION));
    }

    @RequestMapping(value = "/generateExtId", method = RequestMethod.POST)
    public String generateExtId(@RequestBody Map<String, Object> data) {
        return "\"" + extIdGenerator.suggestNextId(data) + "\"";
    }

    @RequestMapping(value = "/validateExtId/{extId}", method = RequestMethod.POST)
    public boolean validateExtId(@PathVariable String extId, @RequestBody Map<String, Object> data) {
        return extIdGenerator.validateExtId(extId, data);
    }

}
