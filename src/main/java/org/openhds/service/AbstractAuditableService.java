package org.openhds.service;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.repository.AuditableRepository;

/**
 * Created by wolfe on 6/11/15.
 */
public abstract class AbstractAuditableService<T extends AuditableEntity> extends AbstractUuidService<T>{

    public AbstractAuditableService(AuditableRepository repository) {super(repository);
    }

}
