package org.openhds.resource.contract;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.repository.contract.AuditableCollectedRepository;
import org.openhds.resource.registration.Registration;
import org.openhds.service.contract.AbstractAuditableCollectedService;

/**
 * Created by Ben on 6/16/15.
 */
public abstract class AuditableCollectedRestControllerTest <
        T extends AuditableCollectedEntity,
        U extends AbstractAuditableCollectedService<T, ? extends AuditableCollectedRepository<T>>,
        V extends AuditableCollectedRestController<T, ? extends Registration<T>, U>>
        extends AuditableRestControllerTest<T, U, V> {
}
