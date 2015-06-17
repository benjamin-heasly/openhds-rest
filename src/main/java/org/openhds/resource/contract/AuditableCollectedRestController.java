package org.openhds.resource.contract;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.repository.AuditableCollectedRepository;
import org.openhds.resource.registration.Registration;

/**
 * Created by usm on 6/16/15.
 */
public abstract class AuditableCollectedRestController<T extends AuditableCollectedEntity, U extends Registration<T>>
        extends AuditableRestController<T, U> {

    private final AuditableCollectedRepository<T> repository;

    public AuditableCollectedRestController(AuditableCollectedRepository<T> repository) {
        super(repository);
        this.repository = repository;
    }

}
