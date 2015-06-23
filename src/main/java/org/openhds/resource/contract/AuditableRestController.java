package org.openhds.resource.contract;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.repository.AuditableRepository;
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
import java.util.NoSuchElementException;

/**
 * Created by Ben on 6/16/15.
 */
public abstract class AuditableRestController<T extends AuditableEntity, U extends Registration<T>>
        extends UuidIdentifiableRestController<T, U> {

    private final AuditableRepository<T> repository;

    public AuditableRestController(AuditableRepository<T> repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    protected Page<T> findAll(Pageable pageable) {
        return repository.findByDeletedFalse(pageable);
    }

    @Override
    protected T findOne(String id) {
        return repository.findByDeletedFalseAndUuid(id);
    }

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

    protected Page<T> findPagedByInsertDate(Pageable pageable, ZonedDateTime insertedAfter, ZonedDateTime insertedBefore) {
        // TODO: this is probably a method of AuditableService
        if (null == insertedAfter) {
            if (null == insertedBefore) {
                return repository.findByDeletedFalse(pageable);
            } else {
                return repository.findByDeletedFalseAndInsertDateBefore(insertedBefore, pageable);
            }
        } else {
            if (null == insertedBefore) {
                return repository.findByDeletedFalseAndInsertDateAfter(insertedAfter, pageable);
            } else {
                return repository.findByDeletedFalseAndInsertDateBetween(insertedAfter, insertedBefore, pageable);
            }
        }
    }


    // for auditing
    @RequestMapping(value = "/voided", method = RequestMethod.GET)
    public PagedResources readPagedByInertDate(Pageable pageable, PagedResourcesAssembler assembler) {
        Page<T> entities = repository.findByDeletedTrue(pageable);
        return assembler.toResource(entities, entityLinkAssembler);
    }

    @Override
    protected void removeOneCanonical(String id, String voidReason) {
        // TODO: this should be in Auditable Service
        T entity = findOne(id);
        if (null == entity) {
            throw new NoSuchElementException("No entity found with id " + id);
        }

        entity.setDeleted(true);
        entity.setVoidDate(ZonedDateTime.now());
        entity.setVoidReason(voidReason);
        // TODO: entity.setVoidBy( authenticated principal );
        repository.save(entity);
    }

}
