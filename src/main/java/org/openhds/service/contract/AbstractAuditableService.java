package org.openhds.service.contract;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.repository.AuditableRepository;

/**
 * Created by wolfe on 6/11/15.
 */
public abstract class AbstractAuditableService<T extends AuditableEntity, V extends AuditableRepository<T>>
        extends AbstractUuidService<T,V>{

    public AbstractAuditableService(V repository) {super(repository);

    }

}
