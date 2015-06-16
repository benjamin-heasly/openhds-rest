package org.openhds.service;

import org.openhds.domain.contract.AuditableExtIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wolfe on 6/11/15.
 */
public abstract class AbstractAuditableExtIdService<T extends AuditableExtIdEntity> extends AbstractAuditableCollectedService<T>{

    public AbstractAuditableExtIdService(JpaRepository respository) {
        super(respository);
    }

}
