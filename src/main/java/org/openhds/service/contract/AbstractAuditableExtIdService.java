package org.openhds.service.contract;

import org.openhds.domain.contract.AuditableExtIdEntity;
import org.openhds.repository.AuditableExtIdRepository;
import org.openhds.repository.results.EntityIterator;
import org.springframework.data.domain.Sort;

/**
 * Created by wolfe on 6/11/15.
 */
public abstract class AbstractAuditableExtIdService<T extends AuditableExtIdEntity, V extends AuditableExtIdRepository<T>>
        extends AbstractAuditableCollectedService<T, V>{

    public AbstractAuditableExtIdService(V repository) {
        super(repository);
    }

    //TODO: write test
    public EntityIterator<T> findByExtId(String extId, Sort sort) {
        return iteratorFromPageable(pageable -> repository.findByDeletedFalseAndExtId(extId, pageable), sort);
    }

}
