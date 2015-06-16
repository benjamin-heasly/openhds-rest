package org.openhds.service;

import org.openhds.domain.contract.AuditableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wolfe on 6/11/15.
 */
public abstract class AbstractAuditableService<T> extends AbstractUuidService<T>{

    public AbstractAuditableService(JpaRepository respository) {super(respository);
    }

}
