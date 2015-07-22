package org.openhds.service.contract;

import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.errors.model.Error;
import org.openhds.errors.model.ErrorLog;
import org.openhds.errors.model.ErrorLogException;
import org.openhds.errors.util.ErrorLogger;
import org.openhds.events.model.Event;
import org.openhds.events.util.EventPublisher;
import org.openhds.repository.contract.UuidIdentifiableRepository;
import org.openhds.repository.queries.QueryRange;
import org.openhds.repository.queries.QueryValue;
import org.openhds.repository.queries.Specifications;
import org.openhds.repository.results.EntityIterator;
import org.openhds.repository.results.PageIterator;
import org.openhds.repository.results.PagingEntityIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Created by wolfe on 6/11/15.
 */
public abstract class AbstractUuidService<T extends UuidIdentifiable, V extends UuidIdentifiableRepository<T>> {

    public final static String UNKNOWN_ENTITY_UUID = "UNKNOWN";

    protected final V repository;

    @Autowired
    protected Validator validator;

    @Autowired
    protected ErrorLogger errorLogger;

    @Autowired
    protected EventPublisher eventPublisher;

    public AbstractUuidService(V repository) {
        this.repository = repository;
    }

    protected abstract T makeUnknownEntity();

    private T persistUnknownEntity() {
        T unknownEntity = makeUnknownEntity();
        unknownEntity.setUuid(UNKNOWN_ENTITY_UUID);
        return createOrUpdate(unknownEntity);
    }

    public T getUnknownEntity() {
        if (!repository.exists(UNKNOWN_ENTITY_UUID)) {
            return persistUnknownEntity();
        }
        return repository.findOne(UNKNOWN_ENTITY_UUID);
    }

    public long countAll() {
        return repository.count();
    }

    public EntityIterator<T> findAll(Sort sort) {
        return iteratorFromPageable(repository::findAll, sort);
    }

    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public void delete(T entity, String reason) {
        checkEntityExists(entity.getUuid());
        repository.delete(entity);
    }

    public void delete(String id, String reason) {
        checkEntityExists(id);
        repository.delete(id);
    }

    protected void checkEntityExists(String id) {
        if (repository.exists(id)) {
            return;
        }
        throw new NoSuchElementException("No such entity with uuid " + id
                + " in repository " + getClass().getSimpleName());
    }

    public T findOne(String id) {
        return repository.findOne(id);
    }

    public T findOrMakePlaceHolder(String uuid){
        T entity = findOne(uuid);
        if (null == entity){
            entity = makeUnknownEntity();
            entity.setUuid(uuid);
            createOrUpdate(entity);
        }
        return entity;
    }

    public T createOrUpdate(T entity) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setEntityType(entity.getClass().getSimpleName());

        validate(entity, errorLog);

        if (!errorLog.getErrors().isEmpty()) {
            errorLogger.log(errorLog);
            throw new ErrorLogException(errorLog);
        } else {
            repository.save(entity);

            Event event = new Event();
            event.setActionType(Event.PERSIST_ACTION);
            event.setEntityType(entity.getClass().getSimpleName());
            event.setEventData(entity.toString());
            eventPublisher.publish(event);
        }

        return entity;
    }

    public EntityIterator<T> findByMultipleValues(Sort sort, QueryValue... queryValues) {
        Specification<T> specification = Specifications.multiValue(queryValues);
        return iteratorFromPageable(pageable -> repository.findAll(specification, pageable), sort);
    }

    public Page<T> findByMultipleValues(Pageable pageable, QueryValue... queryValues) {
        Specification<T> specification = Specifications.multiValue(queryValues);
        return repository.findAll(specification, pageable);
    }

    public <R extends Comparable> EntityIterator<T> findByMultipleValuesRanged(Sort sort, QueryRange<R> queryRange, QueryValue... queryValues) {
        Specification<T> specification = Specifications.rangedMultiValue(queryRange, queryValues);
        return iteratorFromPageable(pageable -> repository.findAll(specification, pageable), sort);
    }

    public <R extends Comparable> Page<T> findByMultipleValuesRanged(Pageable pageable, QueryRange<R> queryRange, QueryValue... queryValues) {
        Specification<T> specification = Specifications.rangedMultiValue(queryRange, queryValues);
        return repository.findAll(specification, pageable);
    }

    // Iterate entities based on paged queries.
    protected EntityIterator<T> iteratorFromPageable(PageIterator.PagedQueryable<T> pagedQueryable, Sort sort) {
        return new PagingEntityIterator<>(new PageIterator<>(pagedQueryable, sort));
    }

    public void validate(T entity, ErrorLog errorLog) {

        //Fire JSR-303 annotations
        Set<ConstraintViolation<T>> violations = validator.validate(entity);

        List<Error> errors = errorLog.getErrors();
        //Convert violation set to OpenHDS Errors
        for (ConstraintViolation violation : violations) {
            errors.add(new Error(violation.getMessage()));
        }

        //TODO: Manual validation for UuidService

    }
}
