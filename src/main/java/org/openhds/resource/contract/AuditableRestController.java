package org.openhds.resource.contract;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.repository.contract.AuditableRepository;
import org.openhds.repository.results.EntityIterator;
import org.openhds.repository.results.PageIterator;
import org.openhds.repository.results.PagingEntityIterator;
import org.openhds.repository.results.ShallowCopyIterator;
import org.openhds.resource.registration.Registration;
import org.openhds.service.contract.AbstractAuditableService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.PagedResources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.ZonedDateTime;

/**
 * Created by Ben on 6/16/15.
 */
public abstract class AuditableRestController<
        T extends AuditableEntity,
        U extends Registration<T>,
        V extends AbstractAuditableService<T, ? extends AuditableRepository<T>>>
        extends UuidIdentifiableRestController<T, U, V> {

    private final V service;

    public AuditableRestController(V service) {
        super(service);
        this.service = service;
    }

    // afterDate <= lastModifiedDate < beforeDate
    @RequestMapping(value = "/bydate", method = RequestMethod.GET)
    public PagedResources readByDatePaged(Pageable pageable,
                                          PagedResourcesAssembler assembler,
                                          @RequestParam(required = false)
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                          ZonedDateTime afterDate,
                                          @RequestParam(required = false)
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                          ZonedDateTime beforeDate) {

        Page<T> entities = service.findByLastModifiedDate(pageable, afterDate, beforeDate);
        return assembler.toResource(entities, entityLinkAssembler);
    }

    // afterDate <= lastModifiedDate < beforeDate, no HATEOAS
    @RequestMapping(value = "/bydate/bulk", method = RequestMethod.GET)
    public EntityIterator<T> readByDateBulk(Sort sort,
                                            @RequestParam(required = false)
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                            ZonedDateTime afterDate,
                                            @RequestParam(required = false)
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                            ZonedDateTime beforeDate) {

        PageIterator<T> pageIterator = new PageIterator<>((pageable) -> service.findByLastModifiedDate(pageable, afterDate, beforeDate), sort);
        EntityIterator<T> entityIterator = new PagingEntityIterator<>(pageIterator);
        entityIterator.setCollectionName(getResourceName());
        return new ShallowCopyIterator<>(entityIterator);
    }

    // records associated with given LocationHierarchy sub-tree
    @RequestMapping(value = "/bylocationhierarchy", method = RequestMethod.GET)
    public PagedResources readByLocationHierarchyPaged(Pageable pageable,
                                                       PagedResourcesAssembler assembler,
                                                       @RequestParam(required = true)
                                                       String locationHierarchyUuid) {

        Page<T> entities = service.findByLocationHierarchy(pageable, locationHierarchyUuid);
        return assembler.toResource(entities, entityLinkAssembler);
    }

    // records associated with given LocationHierarchy sub-tree
    @RequestMapping(value = "/bylocationhierarchy/bulk", method = RequestMethod.GET)
    public EntityIterator<T> readByLocationHierarchyBulk(Sort sort,
                                                         @RequestParam(required = true)
                                                         String locationHierarchyUuid) {

        PageIterator<T> pageIterator = new PageIterator<>((pageable) -> service.findByLocationHierarchy(pageable, locationHierarchyUuid), sort);
        EntityIterator<T> entityIterator = new PagingEntityIterator<>(pageIterator);
        entityIterator.setCollectionName(getResourceName());
        return new ShallowCopyIterator<>(entityIterator);
    }

    // for auditing
    @RequestMapping(value = "/voided", method = RequestMethod.GET)
    public PagedResources readPagedByInertDate(Pageable pageable, PagedResourcesAssembler assembler) {
        Page<T> entities = service.findAllDeleted(pageable);
        return assembler.toResource(entities, entityLinkAssembler);
    }

}
