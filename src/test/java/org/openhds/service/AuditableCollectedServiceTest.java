package org.openhds.service;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.helpers.AbstractTestHelper;
import org.openhds.service.contract.AbstractAuditableService;

/**
 * Created by wolfe on 6/17/15.
 */
public abstract class AuditableCollectedServiceTest<T extends AuditableCollectedEntity,
        U extends AbstractAuditableService, V extends AbstractTestHelper<T>> extends AuditableServiceTest<T,U,V>{


}
