package org.openhds.service.contract;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.model.FieldWorker;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.contract.AuditableCollectedRepository;
import org.openhds.repository.queries.QueryRange;
import org.openhds.repository.results.EntityIterator;
import org.openhds.service.impl.FieldWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.time.ZonedDateTime;

/**
 * Created by wolfe on 6/11/15.
 */
public abstract class AbstractAuditableCollectedService<T extends AuditableCollectedEntity, V extends AuditableCollectedRepository<T>>
        extends AbstractAuditableService<T, V> {

    @Autowired
    protected FieldWorkerService fieldWorkerService;

    public AbstractAuditableCollectedService(V repository) {
        super(repository);
    }

    public EntityIterator<T> findByCollectedBy(Sort sort, FieldWorker fieldWorker) {
        return iteratorFromPageable(pageable -> repository.findByDeletedFalseAndCollectedBy(fieldWorker, pageable), sort);
    }

    public EntityIterator<T> findByCollectionDateTime(Sort sort, ZonedDateTime collectedAfter, ZonedDateTime collectedBefore) {

        return findByMultipleValuesRanged(sort, new QueryRange<>("collectionDateTime", collectedAfter, collectedBefore));

    }

    @Override
    public void validate(T entity, ErrorLog errorLog) {
        super.validate(entity, errorLog);
        errorLog.setCollectedBy(entity.getCollectedBy());

        //TODO: check that collectionDateTime is not in future

    }

    protected void initPlaceHolderCollectedFields(T entity){
        entity.setCollectedBy(fieldWorkerService.getUnknownEntity());
        entity.setCollectionDateTime(ZonedDateTime.now().plusHours(1));
    }

}
