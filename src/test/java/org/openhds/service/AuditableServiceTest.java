package org.openhds.service;

import org.junit.Test;
import org.openhds.domain.contract.AuditableEntity;
import org.openhds.security.model.User;
import org.openhds.service.contract.AbstractAuditableService;
import org.springframework.data.domain.Sort;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by wolfe on 6/17/15.
 */

public abstract class AuditableServiceTest
        <T extends AuditableEntity, U extends AbstractAuditableService<T, ?>>
        extends UuidServiceTest<T, U> {

    private static final Sort UUID_SORT = new Sort("uuid");

    /**
     * Simply check that results are not null after a findAll call
     */
    @Test
    public void findAll() {

        List<T> results = service.findAll(UUID_SORT).toList();
        assertNotNull(results.get(0));

    }



    @Test
    public void update() {

        String id = "testId";

        service.createOrUpdate(makeValidEntity("testEntity", id));

        assertNotNull(service.findOne(id));

        service.createOrUpdate(makeValidEntity("updatedTestEntity", id));

        assertNotNull(service.findOne(id));


    }

    /**
     * Creates an entity with a late insertDate and an entity with an early insertDate
     * checks that the results between those two times is exactly 2 and that everything before and after
     * is not 0.
     */

    @Test
    public void findByInsertDate() {

        ZonedDateTime earlyTime = ZonedDateTime.parse("2007-12-03T10:15:30+01:00[Europe/Paris]");
        ZonedDateTime lateTime = ZonedDateTime.parse("2008-12-03T10:15:30+01:00[Europe/Paris]");

        T earlyEntity = makeValidEntity("earlyEntity", "earlyEntity");
        earlyEntity.setInsertDate(earlyTime);
        T lateEntity = makeValidEntity("lateEntity", "lateEntity");
        lateEntity.setInsertDate(lateTime);

        service.createOrUpdate(earlyEntity);
        service.createOrUpdate(lateEntity);

        List<T> betweenReslts = service.findByInsertDate(UUID_SORT, earlyTime, lateTime).toList();
        assertEquals(betweenReslts.size(), 2);

        List<T> afterReslts = service.findByInsertDate(UUID_SORT, earlyTime, null).toList();
        assertNotEquals(afterReslts.size(), 0);

        List<T> beforeReslts = service.findByInsertDate(UUID_SORT, null, lateTime).toList();
        assertNotEquals(beforeReslts.size(), 0);

    }

    @Test
    public void findByLastModifiedDate() {

        ZonedDateTime earlyTime = ZonedDateTime.parse("2007-12-03T10:15:30+01:00[Europe/Paris]");
        ZonedDateTime lateTime = ZonedDateTime.parse("2008-12-03T10:15:30+01:00[Europe/Paris]");

        T earlyEntity = makeValidEntity("earlyEntity", "earlyEntity");
        earlyEntity.setLastModifiedDate(earlyTime);
        T lateEntity = makeValidEntity("lateEntity", "lateEntity");
        lateEntity.setLastModifiedDate(lateTime);

        service.createOrUpdate(earlyEntity);
        service.createOrUpdate(lateEntity);

        List<T> betweenReslts = service.findByLastModifiedDate(UUID_SORT, earlyTime, lateTime).toList();
        assertEquals(betweenReslts.size(), 2);

        List<T> afterReslts = service.findByLastModifiedDate(UUID_SORT, earlyTime, null).toList();
        assertNotEquals(afterReslts.size(), 0);

        List<T> beforeReslts = service.findByLastModifiedDate(UUID_SORT, null, lateTime).toList();
        assertNotEquals(beforeReslts.size(), 0);

    }

    @Test
    public void delete() {

        int entityCount = service.findAll(UUID_SORT).toList().size();
        int deletedCount = service.findAllDeleted(UUID_SORT).toList().size();
        T entity = makeValidEntity("testEntity", "testEntity");

        service.createOrUpdate(entity);

        assertEquals(service.findAll(UUID_SORT).toList().size(), entityCount + 1);

        service.delete(entity, "Test");

        assertEquals(service.findAll(UUID_SORT).toList().size(), entityCount);
        assertEquals(service.findAllDeleted(UUID_SORT).toList().size(), deletedCount + 1);

    }

    @Test
    public void findByInsertBy() {

        T entity = makeValidEntity("testEntity", "testEntity");
        User user = entity.getInsertBy();

        int entityCount = service.findByInsertBy(UUID_SORT, user).toList().size();

        service.createOrUpdate(entity);

        assertEquals(service.findByInsertBy(UUID_SORT, user).toList().size(), entityCount + 1);

    }

    @Test
    public void findByLastModifiedBy() {

        T entity = makeValidEntity("testEntity", "testEntity");
        User user = entity.getLastModifiedBy();

        int entityCount = service.findByLastModifiedBy(UUID_SORT, user).toList().size();

        service.createOrUpdate(entity);

        assertEquals(service.findByLastModifiedBy(UUID_SORT, user).toList().size(), entityCount + 1);

    }

}
