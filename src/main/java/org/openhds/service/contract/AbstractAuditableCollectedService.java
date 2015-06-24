package org.openhds.service.contract;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.model.FieldWorker;
import org.openhds.repository.AuditableCollectedRepository;
import org.openhds.repository.results.EntityIterator;
import org.springframework.data.domain.Sort;

import java.time.ZonedDateTime;

/**
 * Created by wolfe on 6/11/15.
 */
public abstract class AbstractAuditableCollectedService<T extends AuditableCollectedEntity, V extends AuditableCollectedRepository<T>>
        extends AbstractAuditableService<T,V> {

    public AbstractAuditableCollectedService(V repository) {
        super(repository);
    }

    public EntityIterator<T> findByCollectedBy(Sort sort, FieldWorker fieldWorker){
        return iteratorFromPageable(pageable -> repository.findByDeletedFalseAndCollectedBy(fieldWorker, pageable), sort);
    }

    public EntityIterator<T> findByCollectionDateTime(Sort sort, ZonedDateTime collectedAfter, ZonedDateTime collectedBefore) {

        if (null != collectedAfter && null != collectedBefore) {

            return iteratorFromPageable(
                    pageable -> repository.findByDeletedFalseAndCollectionDateTimeBetween(collectedAfter, collectedBefore, pageable), sort);

        } else if (null != collectedAfter) {

            return iteratorFromPageable(
                    pageable -> repository.findByDeletedFalseAndCollectionDateTimeAfter(collectedAfter, pageable), sort);

        } else if (null != collectedBefore) {

            return iteratorFromPageable(
                    pageable -> repository.findByDeletedFalseAndCollectionDateTimeBefore(collectedBefore, pageable), sort);

        } else {

            return findAll(sort);

        }

    }

}
