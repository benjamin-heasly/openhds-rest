package org.openhds.service.contract;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.contract.AuditableRepository;
import org.openhds.repository.results.EntityIterator;
import org.openhds.security.model.User;
import org.springframework.data.domain.Sort;

import java.time.ZonedDateTime;

/**
 * Created by wolfe on 6/11/15.
 */
public abstract class AbstractAuditableService
        <T extends AuditableEntity, V extends AuditableRepository<T>>
        extends AbstractUuidService<T, V> {

    public AbstractAuditableService(V repository) {
        super(repository);
    }

    public T findOne(String id) {
        return repository.findByDeletedFalseAndUuid(id);
    }

    public EntityIterator<T> findAll(Sort sort) {

        return iteratorFromPageable(repository::findByDeletedFalse, sort);

    }

    public EntityIterator<T> findByInsertDate(Sort sort, ZonedDateTime insertedAfter, ZonedDateTime insertedBefore) {

        if (null != insertedAfter && null != insertedBefore) {

            return iteratorFromPageable(
                    pageable -> repository.findByDeletedFalseAndInsertDateBetween(insertedAfter, insertedBefore, pageable), sort);

        } else if (null != insertedAfter) {

            return iteratorFromPageable(
                    pageable -> repository.findByDeletedFalseAndInsertDateAfter(insertedAfter, pageable), sort);

        } else if (null != insertedBefore) {

            return iteratorFromPageable(
                    pageable -> repository.findByDeletedFalseAndInsertDateBefore(insertedBefore, pageable), sort);

        } else {

            return findAll(sort);

        }

    }

    public void delete(T entity, String reason){
        entity.setDeleted(true);
        entity.setVoidReason(reason);
        //TODO: setVoidBy when global user is accessible
        repository.save(entity);
    }

    public EntityIterator<T> findAllDeleted(Sort sort){
        return iteratorFromPageable(pageable -> repository.findByDeletedTrue(pageable), sort);
    }

    public EntityIterator<T> findByInsertBy(Sort sort, User user){
        return iteratorFromPageable(pageable -> repository.findByDeletedFalseAndInsertBy(user, pageable), sort);
    }

    @Override
    public void validate(T entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);

        //TODO: Manual validation for AuditableService
    }
}
