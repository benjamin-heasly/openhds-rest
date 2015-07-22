package org.openhds.service;

import org.junit.Test;
import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.model.FieldWorker;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.openhds.service.impl.FieldWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by wolfe on 6/17/15.
 */
public abstract class AuditableCollectedServiceTest
        <T extends AuditableCollectedEntity, U extends AbstractAuditableCollectedService<T, ?>>
        extends AuditableServiceTest<T, U> {


    @Autowired
    protected FieldWorkerService fieldWorkerService;

    /**
     * Create an entity and save a reference to its collectedBy fieldworker, after creation check that
     * findByCollectedBy for that fieldworker returns 1 more than before.
     */

    @Test
    @WithUserDetails
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
    @WithUserDetails
    public void findByCollectionDateTime() {

        T earlyEntity = makeValidEntity("earlyEntity", "earlyEntity");
        ZonedDateTime earlyTime = service.createOrUpdate(earlyEntity).getCollectionDateTime();

        T lateEntity = makeValidEntity("lateEntity", "lateEntity");
        ZonedDateTime lateTime = service.createOrUpdate(lateEntity).getCollectionDateTime();

        assertTrue(earlyTime.compareTo(lateTime) < 0);

        List<T> betweenReslts = service.findByCollectionDateTime(UUID_SORT, earlyTime, lateTime).toList();
        assertEquals(betweenReslts.size(), 2);

        List<T> afterReslts = service.findByCollectionDateTime(UUID_SORT, earlyTime, null).toList();
        assertNotEquals(afterReslts.size(), 0);

        List<T> beforeReslts = service.findByCollectionDateTime(UUID_SORT, null, lateTime).toList();
        assertNotEquals(beforeReslts.size(), 0);
    }

    protected void initCollectedFields(T entity){
        entity.setCollectedBy(fieldWorkerService.getUnknownEntity());
        entity.setCollectionDateTime(ZonedDateTime.now());
    }
}
