package org.openhds.resource.controller;

import org.openhds.domain.contract.AuditableEntity;

/**
 * Created by Ben on 6/16/15.
 */
public abstract class AuditableRestControllerTest <T extends AuditableEntity>
        extends UuidIdentifiableRestControllerTest<T> {
}
