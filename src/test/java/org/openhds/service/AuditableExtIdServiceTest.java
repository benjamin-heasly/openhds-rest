package org.openhds.service;

import org.junit.Test;
import org.openhds.domain.contract.AuditableExtIdEntity;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.junit.Assert.assertEquals;

/**
 * Created by wolfe on 6/17/15.
 */
public abstract class AuditableExtIdServiceTest
        <T extends AuditableExtIdEntity, U extends AbstractAuditableExtIdService<T, ?>>
        extends AuditableCollectedServiceTest<T, U> {

    /**
     * Persists an entity with a specific extId and checks that the result set from findByExtId has increased by 1
     */

    @Test
    @WithUserDetails
    public void findByExtId() {

        String id = "testEntity";

        T entity = makeValidEntity(id, id);

        int entityCount = service.findByExtId(UUID_SORT, id).toList().size();

        service.createOrUpdate(entity);

        assertEquals(service.findByExtId(UUID_SORT, id).toList().size(), entityCount + 1);


    }

}
