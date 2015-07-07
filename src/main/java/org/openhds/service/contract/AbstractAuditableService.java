package org.openhds.service.contract;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.contract.AuditableRepository;
import org.openhds.repository.results.EntityIterator;
import org.openhds.security.model.User;
import org.openhds.security.model.UserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.time.ZonedDateTime;

/**
 * Created by wolfe on 6/11/15.
 */
public abstract class AbstractAuditableService
        <T extends AuditableEntity, V extends AuditableRepository<T>>
        extends AbstractUuidService<T, V> {

    private enum DateType {INSERT,VOID,MODIFIED};

    public AbstractAuditableService(V repository) {
        super(repository);
    }

    @Autowired
    private UserHelper userHelper;

    @Override
    public T createOrUpdate(T entity) {


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

        return super.createOrUpdate(entity);
    }

    public T findOne(String id) {
        return repository.findByDeletedFalseAndUuid(id);
    }

    public EntityIterator<T> findAll(Sort sort) {

        return iteratorFromPageable(repository::findByDeletedFalse, sort);

    }

    public EntityIterator<T> findByInsertDate(Sort sort, ZonedDateTime insertedAfter, ZonedDateTime insertedBefore) {

        return findByDate(DateType.INSERT,sort,insertedAfter, insertedBefore);

    }

    public EntityIterator<T> findByVoidDate(Sort sort, ZonedDateTime voidedAfter, ZonedDateTime voidedBefore) {

        return findByDate(DateType.VOID,sort,voidedAfter, voidedBefore);

    }

    public EntityIterator<T> findByLastModifiedDate(Sort sort, ZonedDateTime modifiedAfter, ZonedDateTime modifiedBefore) {

        return findByDate(DateType.MODIFIED,sort,modifiedAfter, modifiedBefore);


    }

    public void delete(T entity, String reason) {
        entity.setDeleted(true);
        entity.setVoidReason(reason);
        //TODO: setVoidBy when global user is accessible
        repository.save(entity);
    }

    public EntityIterator<T> findAllDeleted(Sort sort) {
        return iteratorFromPageable(pageable -> repository.findByDeletedTrue(pageable), sort);
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

    private EntityIterator<T> findByDate(DateType dateType, Sort sort, ZonedDateTime after, ZonedDateTime before){

        if (null != after && null != before) {
            switch (dateType){
                case VOID:
                    return iteratorFromPageable(
                            pageable -> repository.findByDeletedFalseAndVoidDateBetween(after, before, pageable), sort);
                case INSERT:
                    return iteratorFromPageable(
                            pageable -> repository.findByDeletedFalseAndInsertDateBetween(after, before, pageable), sort);
                case MODIFIED:
                    return iteratorFromPageable(
                            pageable -> repository.findByDeletedFalseAndLastModifiedDateBetween(after, before, pageable), sort);
            }

        } else if (null != after) {
            switch (dateType){
                case VOID:
                    return iteratorFromPageable(
                            pageable -> repository.findByDeletedFalseAndVoidDateAfter(after, pageable), sort);
                case INSERT:
                    return iteratorFromPageable(
                            pageable -> repository.findByDeletedFalseAndInsertDateAfter(after, pageable), sort);
                case MODIFIED:
                    return iteratorFromPageable(
                            pageable -> repository.findByDeletedFalseAndLastModifiedDateAfter(after, pageable), sort);
            }

        } else if (null != before) {
            switch (dateType){
                case VOID:
                    return iteratorFromPageable(
                            pageable -> repository.findByDeletedFalseAndVoidDateBefore(before, pageable), sort);
                case INSERT:
                    return iteratorFromPageable(
                            pageable -> repository.findByDeletedFalseAndInsertDateBefore(before, pageable), sort);
                case MODIFIED:
                    return iteratorFromPageable(
                            pageable -> repository.findByDeletedFalseAndLastModifiedDateBefore(before, pageable), sort);
            }
        }

        return findAll(sort);

    }
}
