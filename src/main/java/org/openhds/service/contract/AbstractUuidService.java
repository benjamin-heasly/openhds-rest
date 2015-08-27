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

    public final static String PLACEHOLDER_NAME = "PLACEHOLDER_NAME";

    public final static String UNKNOWN_NAME = "UNKNOWN_NAME";
    public final static String UNKNOWN_ENTITY_UUID = "UNKNOWN";

    protected final Sort UUID_SORT = new Sort("uuid");


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

    public abstract T makePlaceHolder(String id, String name);

    public T makePlaceHolder(String id) {
        return makePlaceHolder(id, PLACEHOLDER_NAME);
    }

    protected T makeUnknownEntity(){
        return makePlaceHolder(UNKNOWN_ENTITY_UUID, UNKNOWN_NAME);
    }

    private T persistUnknownEntity() {
        T unknownEntity = makeUnknownEntity();
        return createOrUpdate(unknownEntity);
    }

    public T getUnknownEntity() {
        if (!repository.exists(UNKNOWN_ENTITY_UUID)) {
            return persistUnknownEntity();
        }
        return repository.findOne(UNKNOWN_ENTITY_UUID);
    }

    // Are there any records, not counting the unknown entity?
    public boolean hasRecords() {
        return repository.exists(UNKNOWN_ENTITY_UUID) ? 1 < repository.count() : 0 < repository.count();
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

    public boolean exists(String id) {
        return repository.exists(id);
    }


    public T findOrMakePlaceHolder(String id){
        if (exists(id)) {
            return findOne(id);
        }

        T entity = makePlaceHolder(id);
        entity.setUuid(id);
        return createOrUpdate(entity);
    }

    public T createOrUpdate(T entity) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setEntityType(entity.getClass().getSimpleName());
        verify(entity, errorLog);
        validate(entity, errorLog);

        if (!errorLog.getErrors().isEmpty()) {
            errorLogger.log(errorLog);
            throw new ErrorLogException(errorLog);
        }

        T saved = repository.save(entity);

        Event event = new Event();
        event.setActionType(Event.PERSIST_ACTION);
        event.setEntityType(saved.getClass().getSimpleName());
        event.setEventData(saved.toString());
        eventPublisher.publish(event);

        return saved;
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

    }

    //This method 'verifies' that the data is 'whole' so to speak by firing off all the JSR-annotations,
    //the majority of which are @NotNull. This allows the rest of the validation to make checks to fields
    //without checking for nulls everywhere as well as catching would-be nulls ahead of time with an errorlog
    protected void verify(T entity, ErrorLog errorLog){
        //Fire JSR-303 annotations
        Set<ConstraintViolation<T>> violations = validator.validate(entity);

        List<Error> errors = errorLog.getErrors();
        //Convert violation set to OpenHDS Errors
        for (ConstraintViolation violation : violations) {
            errors.add(new Error(violation.getMessage()));
        }

        if (!errorLog.getErrors().isEmpty()) {
            errorLogger.log(errorLog);
            throw new ErrorLogException(errorLog);
        }
    }
}
