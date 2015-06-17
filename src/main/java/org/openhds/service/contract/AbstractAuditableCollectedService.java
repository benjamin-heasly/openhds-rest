package org.openhds.service.contract;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.repository.AuditableCollectedRepository;

/**
 * Created by wolfe on 6/11/15.
 */
public abstract class AbstractAuditableCollectedService<T extends AuditableCollectedEntity, V extends AuditableCollectedRepository<T>>
        extends AbstractAuditableService<T,V> {

    public AbstractAuditableCollectedService(V repository) {
        super(repository);
    }

}
