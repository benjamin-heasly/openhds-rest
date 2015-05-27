package org.openhds.repository.util;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.openhds.domain.contract.UuidIdentifiable;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Ben on 5/27/15.
 */
public class IfMissingUuidGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        if (object instanceof UuidIdentifiable) {
            String uuid = ((UuidIdentifiable) object).getUuid();
            if (null != uuid && !uuid.trim().isEmpty()) {
                return uuid;
            }
        }
        return UUID.randomUUID().toString();
    }
}
