package org.openhds.service.contract;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.errors.model.ErrorLog;
import org.openhds.errors.model.ErrorLogException;
import org.openhds.repository.contract.AuditableRepository;
import org.openhds.repository.queries.QueryRange;
import org.openhds.repository.results.EntityIterator;
import org.openhds.security.model.User;
import org.openhds.security.model.UserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.ZonedDateTime;
import java.util.Set;

/**
 * Created by wolfe on 6/11/15.
 */
public abstract class AbstractAuditableService
        <T extends AuditableEntity, V extends AuditableRepository<T>>
        extends AbstractUuidService<T, V> {

    public AbstractAuditableService(V repository) {
        super(repository);
    }

    @Autowired
    private UserHelper userHelper;

    @Override
    public T createOrUpdate(T entity) {

        checkNonStaleModifiedDate(entity);
        setAuditableFields(entity);

        return super.createOrUpdate(entity);
    }

    @Override
    public T findOne(String id) {
        return repository.findByDeletedFalseAndUuid(id);
    }

    @Override
    public boolean exists(String id) {
        // exists is a first-pass check, findOne also checks isDeleted
        return repository.exists(id) && null != findOne(id);
    }

    public EntityIterator<T> findAll(Sort sort) {

        return iteratorFromPageable(repository::findByDeletedFalse, sort);

    }

    public Page<T> findAll(Pageable pageable) {

        return repository.findByDeletedFalse(pageable);

    }

    public EntityIterator<T> findByInsertDate(Sort sort, ZonedDateTime insertedAfter, ZonedDateTime insertedBefore) {

        return findByMultipleValuesRanged(sort, new QueryRange<>("insertDate", insertedAfter, insertedBefore));

    }

    public Page<T> findByInsertDate(Pageable pageable, ZonedDateTime insertedAfter, ZonedDateTime insertedBefore) {

        return findByMultipleValuesRanged(pageable, new QueryRange<>("insertDate", insertedAfter, insertedBefore));

    }

    public EntityIterator<T> findByVoidDate(Sort sort, ZonedDateTime voidedAfter, ZonedDateTime voidedBefore) {

        return findByMultipleValuesRanged(sort, new QueryRange<>("voidDate", voidedAfter, voidedBefore));

    }

    public Page<T> findByVoidDate(Pageable pageable, ZonedDateTime voidedAfter, ZonedDateTime voidedBefore) {

        return findByMultipleValuesRanged(pageable, new QueryRange<>("voidDate", voidedAfter, voidedBefore));

    }

    public EntityIterator<T> findByLastModifiedDate(Sort sort, ZonedDateTime modifiedAfter, ZonedDateTime modifiedBefore) {

        return findByMultipleValuesRanged(sort, new QueryRange<>("lastModifiedDate", modifiedAfter, modifiedBefore));

    }

    public Page<T> findByLastModifiedDate(Pageable pageable, ZonedDateTime modifiedAfter, ZonedDateTime modifiedBefore) {

        return findByMultipleValuesRanged(pageable, new QueryRange<>("lastModifiedDate", modifiedAfter, modifiedBefore));

    }

    public Page<T> findByEnclosingLocationHierarchy(Pageable pageable,
                                                    String locationHierarchyUuid,
                                                    ZonedDateTime modifiedAfter,
                                                    ZonedDateTime modifiedBefore) {

        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " can not do location-based queries");
    }

    public EntityIterator<T> findByEnclosingLocationHierarchy(Sort sort,
                                                              String locationHierarchyUuid,
                                                              ZonedDateTime modifiedAfter,
                                                              ZonedDateTime modifiedBefore) {

        return iteratorFromPageable(
                pageable -> findByEnclosingLocationHierarchy(pageable,
                        locationHierarchyUuid,
                        modifiedAfter,
                        modifiedBefore),
                sort);
    }

    public Set<LocationHierarchy> findEnclosingLocationHierarchies(T entity) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " can not do location-based queries");
    }

    public void delete(T entity, String reason) {
        checkEntityExists(entity.getUuid());
        entity.setDeleted(true);
        entity.setVoidReason(reason);
        repository.save(entity);
    }

    public void delete(String id, String reason) {
        checkEntityExists(id);
        T entity = findOne(id);
        delete(entity, reason);
    }

    public EntityIterator<T> findAllDeleted(Sort sort) {
        return iteratorFromPageable(repository::findByDeletedTrue, sort);
    }

    public Page<T> findAllDeleted(Pageable pageable) {
        return repository.findByDeletedTrue(pageable);
    }

    public EntityIterator<T> findByInsertBy(Sort sort, User user) {
        return iteratorFromPageable(pageable -> repository.findByDeletedFalseAndInsertBy(user, pageable), sort);
    }

    public EntityIterator<T> findByVoidBy(Sort sort, User user) {
        return iteratorFromPageable(pageable -> repository.findByDeletedFalseAndVoidBy(user, pageable), sort);
    }

    @Override
    public void validate(T entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);

        //TODO: Manual validation for AuditableService
    }

    public EntityIterator<T> findByLastModifiedBy(Sort sort, User user) {
        return iteratorFromPageable(pageable -> repository.findByDeletedFalseAndLastModifiedBy(user, pageable), sort);
    }

    protected void setAuditableFields(T entity) {
        User user = userHelper.getCurrentUser();
        ZonedDateTime now = ZonedDateTime.now();

        //Check to see if we're creating or updating the entity
        if (null == entity.getInsertDate()) {
            entity.setInsertDate(now);
        }

        if (null == entity.getInsertBy()) {
            entity.setInsertBy(user);
        }

        entity.setLastModifiedDate(now);
        entity.setLastModifiedBy(user);
    }

    protected void checkNonStaleModifiedDate(T entity) {

        T existing = findOne(entity.getUuid());
        if (null != existing
                && null != entity.getLastModifiedDate()
                && existing.getLastModifiedDate().isAfter(entity.getLastModifiedDate())) {

            ErrorLog errorLog = new ErrorLog();
            errorLog.appendError("Update candidate is out of date with database.");
            throw new ErrorLogException(errorLog);
        }
    }
}
