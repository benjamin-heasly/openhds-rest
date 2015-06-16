package org.openhds.resource.controller;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.resource.links.EntityLinkAssembler;
import org.openhds.resource.registration.Registration;

/**
 * Created by Ben on 6/16/15.
 */
public abstract class AuditableRestController<T extends AuditableEntity, U extends Registration<T>>
        extends UuidIdentifiableRestController<T, U> {

    public AuditableRestController(EntityLinkAssembler entityLinkAssembler) {
        super(entityLinkAssembler);
    }

}
