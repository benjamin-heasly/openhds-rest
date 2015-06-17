package org.openhds.service;

import org.openhds.domain.contract.AuditableExtIdEntity;
import org.openhds.helpers.AbstractTestHelper;
import org.openhds.service.contract.AbstractAuditableExtIdService;

/**
 * Created by wolfe on 6/17/15.
 */
public abstract class AuditableExtIdServiceTest<T extends AuditableExtIdEntity,
        U extends AbstractAuditableExtIdService, V extends AbstractTestHelper<T>> extends AuditableCollectedServiceTest<T, U, V> {
}
