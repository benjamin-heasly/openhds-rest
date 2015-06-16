package org.openhds.service;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wolfe on 6/11/15.
 */
public abstract class AbstractAuditableCollectedService<T> extends AbstractAuditableCreatedService<T>{

    public AbstractAuditableCollectedService(JpaRepository respository) {
        super(respository);
    }

}
