package org.openhds.resource.contract;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.repository.AuditableCollectedRepository;

/**
 * Created by Ben on 6/16/15.
 */
public abstract class AuditableCollectedRestControllerTest <T extends AuditableCollectedEntity,
        U extends AuditableCollectedRepository<T>,
        V extends AuditableCollectedRestController<T, ?>>
        extends AuditableRestControllerTest<T, U, V> {
}
