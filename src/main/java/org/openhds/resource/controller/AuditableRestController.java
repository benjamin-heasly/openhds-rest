package org.openhds.resource.controller;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.resource.links.EntityLinkAssembler;
import org.openhds.resource.registration.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public abstract class AuditableRestController<T extends AuditableEntity, U extends Registration<T>>
        extends UuidIdentifiableRestController<T, U> {

    public AuditableRestController(EntityLinkAssembler entityLinkAssembler) {
        super(entityLinkAssembler);
    }

    protected abstract Page<T> findPagedByInsertDate(Pageable pageable, ZonedDateTime insertedAfter, ZonedDateTime insertedBefore);

    // insertedAfter <= insertDate < insertedBefore
    @RequestMapping(value = "/byinsertdate", method = RequestMethod.GET)
    public PagedResources readPagedByInertDate(Pageable pageable, PagedResourcesAssembler assembler,
                                               @RequestParam(required = false)
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                               ZonedDateTime insertedAfter,
                                               @RequestParam(required = false)
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                               ZonedDateTime insertedBefore) {

        Page<T> entities = findPagedByInsertDate(pageable, insertedAfter, insertedBefore);
        return assembler.toResource(entities, entityLinkAssembler);
    }
}
