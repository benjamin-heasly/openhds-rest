package org.openhds.service;

import org.junit.Test;
import org.openhds.domain.contract.AuditableEntity;
import org.openhds.errors.model.ErrorLogException;
import org.openhds.security.model.User;
import org.openhds.service.contract.AbstractAuditableService;
import org.openhds.service.contract.AbstractUuidService;
import org.openhds.service.impl.census.LocationHierarchyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by wolfe on 6/17/15.
 */

public abstract class AuditableServiceTest
        <T extends AuditableEntity, U extends AbstractAuditableService<T, ?>>
        extends UuidServiceTest<T, U> {

    /**
     * Simply check that results are not null after a findAll call
     */
    @Test
    public void findAll() {

        List<T> results = service.findAll(UUID_SORT).toList();
        assertNotNull(results.get(0));

    }

    @Test(expected = ErrorLogException.class)
    @WithUserDetails
    public void updateWithOldLastModified() {
        String id = "testId";

        //Create original and keep reference to it.
        T oldEntity = service.createOrUpdate(makeValidEntity("testEntity", id));

        //Update with new info
        T newEntity = service.createOrUpdate(makeValidEntity("blahg", id));

        //Try to persist oldEntity again w/ out of date lastModifiedDate
        service.createOrUpdate(oldEntity);

        //Check that the entity in the DB is the entity with the up to date lastModifiedDate
        assertEquals(service.findOne(id), newEntity);

    }


    @Test
    @WithUserDetails
    public void update() {

        String id = "testId";

        service.createOrUpdate(makeValidEntity("testEntity", id));

        assertNotNull(service.findOne(id));

        service.createOrUpdate(makeValidEntity("updatedTestEntity", id));

        assertNotNull(service.findOne(id));
    }

    // Sleep some millis before creating an entity, to ensure a fresh timestamp.
    protected T makeValidEntityWithDelay(String name, String id) throws Exception {
        Thread.sleep(100);
        return makeValidEntity(name, id);
    }

    /**
     * Creates an entity with a late insertDate and an entity with an early insertDate
     * checks that the results between those two times is exactly 2 and that everything before and after
     * is not 0.
     */
    @Test
    @WithUserDetails
    public void findByInsertDate() throws Exception {

        int initialCount = (int) service.countAll();

        T earlyEntity = makeValidEntityWithDelay("earlyEntity", "earlyEntity");
        ZonedDateTime earlyTime = service.createOrUpdate(earlyEntity).getInsertDate();

        T lateEntity = makeValidEntityWithDelay("lateEntity", "lateEntity");
        ZonedDateTime lateTime = service.createOrUpdate(lateEntity).getInsertDate();

        assertTrue(earlyTime.compareTo(lateTime) < 0);

        // exactly two records that we just inserted
        List<T> betweenReslts = service.findByInsertDate(UUID_SORT, earlyTime, lateTime).toList();
        assertEquals(2, betweenReslts.size());

        // same two which we just inserted
        List<T> afterReslts = service.findByInsertDate(UUID_SORT, earlyTime, null).toList();
        assertEquals(2, afterReslts.size());

        // pre-existing, plus two which we just inserted
        List<T> beforeReslts = service.findByInsertDate(UUID_SORT, null, lateTime).toList();
        assertEquals(2 + initialCount, beforeReslts.size());

    }

    @Test
    @WithUserDetails
    public void findByLastModifiedDate() throws Exception {

        int initialCount = (int) service.countAll();

        T earlyEntity = makeValidEntityWithDelay("earlyEntity", "earlyEntity");
        ZonedDateTime earlyTime = service.createOrUpdate(earlyEntity).getLastModifiedDate();

        T lateEntity = makeValidEntityWithDelay("lateEntity", "lateEntity");
        ZonedDateTime lateTime = service.createOrUpdate(lateEntity).getLastModifiedDate();

        assertTrue(earlyTime.compareTo(lateTime) < 0);

        // exactly two records that we just inserted
        List<T> betweenReslts = service.findByInsertDate(UUID_SORT, earlyTime, lateTime).toList();
        assertEquals(2, betweenReslts.size());

        // same two which we just inserted
        List<T> afterReslts = service.findByInsertDate(UUID_SORT, earlyTime, null).toList();
        assertEquals(2, afterReslts.size());

        // pre-existing, plus two which we just inserted
        List<T> beforeReslts = service.findByInsertDate(UUID_SORT, null, lateTime).toList();
        assertEquals(2 + initialCount, beforeReslts.size());

    }

    @Test
    @WithUserDetails
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
    @WithUserDetails
    public void findByInsertBy() {

        T entity = makeValidEntity("testEntity", "testEntity");
        User user = service.createOrUpdate(entity).getInsertBy();

        int entityCount = service.findByInsertBy(UUID_SORT, user).toList().size();
        service.createOrUpdate(makeValidEntity("anotherTestEntity", "anotherTestEntity"));

        assertEquals(service.findByInsertBy(UUID_SORT, user).toList().size(), entityCount + 1);

    }

    @Test
    @WithUserDetails
    public void findByLastModifiedBy() {

        T entity = makeValidEntity("testEntity", "testEntity");
        User user = service.createOrUpdate(entity).getLastModifiedBy();

        int entityCount = service.findByLastModifiedBy(UUID_SORT, user).toList().size();
        service.createOrUpdate(makeValidEntity("anotherTestEntity", "anotherTestEntity"));

        assertEquals(service.findByLastModifiedBy(UUID_SORT, user).toList().size(), entityCount + 1);

    }

    @Test
    @WithUserDetails
    public void findByLocationHierarchy() {

        // look for entities associated with the "unknown" location hierarchy
        String locationHierarchyUuid = AbstractUuidService.UNKNOWN_ENTITY_UUID;

        try {
            List<T> locationEntities = service.findByLocationHierarchy(UUID_SORT, locationHierarchyUuid).toList();

            // TODO: test content of entities
            assertFalse(true);

        } catch (UnsupportedOperationException e) {
            // this is OK, not all services have to support location-based queries
        }

    }
}