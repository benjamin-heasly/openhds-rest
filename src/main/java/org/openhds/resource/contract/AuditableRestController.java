package org.openhds.resource.contract;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.repository.contract.AuditableRepository;
import org.openhds.repository.results.EntityIterator;
import org.openhds.repository.results.PageIterator;
import org.openhds.repository.results.PagingEntityIterator;
import org.openhds.repository.results.ShallowCopyIterator;
import org.openhds.resource.registration.Registration;
import org.openhds.service.contract.AbstractAuditableService;
import org.openhds.service.impl.census.LocationHierarchyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.ZonedDateTime;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by Ben on 6/16/15.
 */
public abstract class AuditableRestController<
        T extends AuditableEntity,
        U extends Registration<T>,
        V extends AbstractAuditableService<T, ? extends AuditableRepository<T>>>
        extends UuidIdentifiableRestController<T, U, V> {

    public static final String REL_BY_DATE = "bydate";
    public static final String REL_BY_DATE_BULK = "bydatebulk";
    public static final String REL_BY_LOCATION_HIERARCHY = "bylocationhierarchy";
    public static final String REL_BY_LOCATION_HIERARCHY_BULK = "bylocationhierarchybulk";
    public static final String REL_VOIDED = "voided";

    private final V service;

    public AuditableRestController(V service) {
        super(service);
        this.service = service;
    }

    @Override
    public void addCollectionResourceLinks(ResourceSupport resource) {
        super.addCollectionResourceLinks(resource);

        resource.add(withTemplateParams(linkTo(methodOn(this.getClass())
                        .readByDatePaged(null, null, null, null))
                        .withRel(REL_BY_DATE),
                "afterDate", "beforeDate"));
        resource.add(withTemplateParams(linkTo(methodOn(this.getClass())
                        .readByDateBulk(null, null, null))
                        .withRel(REL_BY_DATE_BULK),
                "afterDate", "beforeDate"));
        resource.add(withTemplateParams(linkTo(methodOn(this.getClass())
                        .readByLocationHierarchyPaged(null, null, null, null, null))
                        .withRel(REL_BY_LOCATION_HIERARCHY),
                "locationHierarchyUuid", "afterDate", "beforeDate"));
        resource.add(withTemplateParams(linkTo(methodOn(this.getClass())
                        .readByLocationHierarchyBulk(null, null, null, null))
                        .withRel(REL_BY_LOCATION_HIERARCHY_BULK),
                "locationHierarchyUuid", "afterDate", "beforeDate"));
        resource.add(linkTo(methodOn(this.getClass())
                .readVoidedPaged(null, null))
                .withRel(REL_VOIDED));
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
        return toResource(entities, assembler, linkTo(methodOn(this.getClass()).readByDatePaged(pageable, assembler, afterDate, beforeDate)).withSelfRel());
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
                                                       @RequestParam(required = false)
                                                       String locationHierarchyUuid,
                                                       @RequestParam(required = false)
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                       ZonedDateTime afterDate,
                                                       @RequestParam(required = false)
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                       ZonedDateTime beforeDate) {

        if (null == locationHierarchyUuid) {
            locationHierarchyUuid = LocationHierarchyService.ROOT_UUID;
        }

        Page<T> entities = service.findByEnclosingLocationHierarchy(pageable, locationHierarchyUuid, afterDate, beforeDate);
        return toResource(entities, assembler, linkTo(methodOn(this.getClass()).readByLocationHierarchyPaged(pageable, assembler, locationHierarchyUuid, afterDate, beforeDate)).withSelfRel());
    }

    // records associated with given LocationHierarchy sub-tree
    @RequestMapping(value = "/bylocationhierarchy/bulk", method = RequestMethod.GET)
    public EntityIterator<T> readByLocationHierarchyBulk(Sort sort,
                                                         @RequestParam(required = false)
                                                         String locationHierarchyUuid,
                                                         @RequestParam(required = false)
                                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                         ZonedDateTime afterDate,
                                                         @RequestParam(required = false)
                                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                         ZonedDateTime beforeDate) {

        final String hierarchyUuid = null == locationHierarchyUuid ? LocationHierarchyService.ROOT_UUID : locationHierarchyUuid;

        PageIterator<T> pageIterator = new PageIterator<>((pageable) -> service.findByEnclosingLocationHierarchy(pageable, hierarchyUuid, afterDate, beforeDate), sort);
        EntityIterator<T> entityIterator = new PagingEntityIterator<>(pageIterator);
        entityIterator.setCollectionName(getResourceName());
        return new ShallowCopyIterator<>(entityIterator);
    }

    // for auditing
    @RequestMapping(value = "/voided", method = RequestMethod.GET)
    public PagedResources readVoidedPaged(Pageable pageable, PagedResourcesAssembler assembler) {
        Page<T> entities = service.findAllDeleted(pageable);
        return toResource(entities, assembler, linkTo(methodOn(this.getClass()).readVoidedPaged(pageable, assembler)).withSelfRel());
    }
}
