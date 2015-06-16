package org.openhds.resource.controller;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.resource.links.EntityLinkAssembler;
import org.openhds.resource.registration.Registration;

/**
 * Created by usm on 6/16/15.
 */
public abstract class AuditableCollectedRestController <T extends AuditableCollectedEntity, U extends Registration<T>>
        extends AuditableRestController<T, U> {

    public AuditableCollectedRestController(EntityLinkAssembler entityLinkAssembler) {
        super(entityLinkAssembler);
    }

}
