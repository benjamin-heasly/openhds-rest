package org.openhds.resource.contract;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.repository.contract.AuditableCollectedRepository;
import org.openhds.resource.registration.Registration;
import org.openhds.service.contract.AbstractAuditableCollectedService;

/**
 * Created by usm on 6/16/15.
 */
public abstract class AuditableCollectedRestController<
        T extends AuditableCollectedEntity,
        U extends Registration<T>,
        V extends AbstractAuditableCollectedService<T, ? extends AuditableCollectedRepository<T>>>
        extends AuditableRestController<T, U, V> {

    protected final V service;

    public AuditableCollectedRestController(V service) {
        super(service);
        this.service = service;
    }

    // use Registration<T> instead of U so that concrete subclass can have multiple flavors of registration
    protected void checkRegistrationFields(T entity, Registration<T> registration) {
        if (null == entity || null == registration) {
            return;
        }

        if (null == entity.getCollectionDateTime()) {
            entity.setCollectionDateTime(registration.getRegistrationDateTime());
        }
    }

}
