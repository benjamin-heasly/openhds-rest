package org.openhds.service;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.repository.AuditableCollectedRepository;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wolfe on 6/11/15.
 */
public abstract class AbstractAuditableCollectedService<T extends AuditableCollectedEntity> extends AbstractAuditableService<T> {

    public AbstractAuditableCollectedService(AuditableCollectedRepository repository) {
        super(repository);
    }

}