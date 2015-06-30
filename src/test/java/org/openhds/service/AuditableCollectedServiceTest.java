package org.openhds.service;

import org.junit.Test;
import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.model.FieldWorker;
import org.openhds.service.contract.AbstractAuditableCollectedService;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by wolfe on 6/17/15.
 */
public abstract class AuditableCollectedServiceTest
        <T extends AuditableCollectedEntity, U extends AbstractAuditableCollectedService<T, ?>>
        extends AuditableServiceTest<T, U> {

    /**
     * Create an entity and save a reference to its collectedBy fieldworker, after creation check that
     * findByCollectedBy for that fieldworker returns 1 more than before.
     */

    @Test
    public void findByCollectedBy() {

        T entity = makeValidEntity("testEntity", "testEntity");

        FieldWorker fieldWorker = entity.getCollectedBy();

        int entityCount = service.findByCollectedBy(null, fieldWorker).toList().size();

        service.createOrUpdate(entity);

        assertEquals(service.findByCollectedBy(null, fieldWorker).toList().size(), entityCount + 1);
    }

    /**
     * Creates an entity with a late collectionDateTime and an entity with an early collectionDateTime
     * checks that the results between those two times is exactly 2 and that everything before and after
     * is not 0.
     */

    @Test
    public void findByCollectionDateTime() {

        ZonedDateTime earlyTime = ZonedDateTime.parse("2007-12-03T10:15:30+01:00[Europe/Paris]");
        ZonedDateTime lateTime = ZonedDateTime.parse("2008-12-03T10:15:30+01:00[Europe/Paris]");

        T earlyEntity = makeValidEntity("earlyEntity", "earlyEntity");
        earlyEntity.setCollectionDateTime(earlyTime);
        T lateEntity = makeValidEntity("lateEntity", "lateEntity");
        lateEntity.setCollectionDateTime(lateTime);

        service.createOrUpdate(earlyEntity);
        service.createOrUpdate(lateEntity);

        List<T> betweenReslts = service.findByCollectionDateTime(null, earlyTime, lateTime).toList();
        assertEquals(betweenReslts.size(), 2);

        List<T> afterReslts = service.findByCollectionDateTime(null, earlyTime, null).toList();
        assertNotEquals(afterReslts.size(), 0);

        List<T> beforeReslts = service.findByCollectionDateTime(null, null, lateTime).toList();
        assertNotEquals(beforeReslts.size(), 0);
    }
}
