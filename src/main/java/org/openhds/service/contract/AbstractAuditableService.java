package org.openhds.service.contract;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.repository.AuditableRepository;
import org.openhds.repository.results.EntityIterator;
import org.openhds.repository.results.PageIterator;
import org.openhds.repository.results.PagingEntityIterator;
import org.openhds.security.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.ZonedDateTime;

/**
 * Created by wolfe on 6/11/15.
 */
public abstract class AbstractAuditableService<T extends AuditableEntity, V extends AuditableRepository<T>>
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

    public T createOrUpdate(T entity){
        return repository.save(entity);
    }

    public T delete(T entity, String reason){
        entity.setDeleted(true);
        entity.setVoidReason(reason);
        //TODO: setVoidBy when global user is accessible
        return  repository.save(entity);
    }

    public EntityIterator<T> findAllDeleted(Sort sort){
        return iteratorFromPageable(pageable -> repository.findByDeletedTrue(pageable), sort);
    }

    public EntityIterator<T> findByInsertBy(Sort sort, User user){
        return iteratorFromPageable(pageable -> repository.findByDeletedFalseAndInsertBy(user, pageable), sort);
    }

    protected EntityIterator<T> iteratorFromPageable(PageIterator.PagedQueryable<T> pagedQueryable, Sort sort) {

        return new PagingEntityIterator<>(new PageIterator<>(pagedQueryable, sort));

    }
}
