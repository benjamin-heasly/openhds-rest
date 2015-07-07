package org.openhds.service.contract;

import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.errors.model.Error;
import org.openhds.errors.model.ErrorLog;
import org.openhds.errors.model.ErrorLogException;
import org.openhds.errors.util.ErrorLogger;
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
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

/**
 * Created by wolfe on 6/11/15.
 */
public abstract class AbstractUuidService<T extends UuidIdentifiable, V extends UuidIdentifiableRepository<T>> {

    public final static String UNKNOWN_ENTITY_UUID = "UNKNOWN";

    protected final V repository;

    @Autowired
    protected ErrorLogger errorLogger;

    public AbstractUuidService(V repository) {
        this.repository = repository;
    }

    protected abstract T makeUnknownEntity();

    private void persistUnknownEntity() {
        T unknownEntity = makeUnknownEntity();
        unknownEntity.setUuid(UNKNOWN_ENTITY_UUID);
        repository.save(unknownEntity);
    }

    public T getUnknownEntity() {
        if (!repository.exists(UNKNOWN_ENTITY_UUID)) {
            persistUnknownEntity();
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
        repository.delete(entity);
    }

    public void delete(String id, String reason) {
        repository.delete(id);
    }

    public T findOne(String id) {
        return repository.findOne(id);
    }

    public T createOrUpdate(T entity) {

        ErrorLog errorLog = new ErrorLog();
        validate(entity, errorLog);

        if (!errorLog.getErrors().isEmpty()) {
            errorLogger.log(errorLog);
            throw new ErrorLogException(errorLog);
        } else {
            repository.save(entity);
            //TODO: log event
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
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(entity);

        List<Error> errors = errorLog.getErrors();
        //Convert violation set to OpenHDS Errors
        for (ConstraintViolation violation : violations) {
            errors.add(new Error(violation.getMessage()));
        }

        //TODO: Manual validation for UuidService

    }
}
