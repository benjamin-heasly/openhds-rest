package org.openhds.service;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wolfe on 6/11/15.
 */
public abstract class AbstractAuditableCreatedService<T> extends AbstractAuditableService<T>{

    public AbstractAuditableCreatedService(JpaRepository respository) {
        super(respository);
    }

}
