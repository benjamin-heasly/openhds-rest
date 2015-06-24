package org.openhds.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openhds.OpenHdsRestApplication;
import org.openhds.domain.contract.AuditableEntity;
import org.openhds.repository.util.SampleDataGenerator;
import org.openhds.security.model.User;
import org.openhds.service.contract.AbstractAuditableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by wolfe on 6/17/15.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OpenHdsRestApplication.class)
@WebAppConfiguration
public abstract class AuditableServiceTest<T extends AuditableEntity, U extends AbstractAuditableService<T, ?>> {

    @Autowired
    protected SampleDataGenerator sampleDataGenerator;

    protected U service;

    protected abstract T makeInvalidEntity();

    protected abstract T makeValidEntity(String name, String id);

    protected abstract void initialize(U service);

    @Before
    public void setup() throws Exception {

        initialize(service);
        resetData();

    }

    @Test
    public void findAll() {

        resetData();

        List<T> results = service.findAll(null).toList();
        assertNotNull(results.get(0));

    }

    @Test
    public void create() {

        resetData();

        service.createOrUpdate(makeValidEntity("testEntity", "testEntity"));

    }

    @Test
    public void update() {

        resetData();

        String id = "testId";

        service.createOrUpdate(makeValidEntity("testEntity", id));

        assertNotNull(service.findOne(id));

        service.createOrUpdate(makeValidEntity("updatedTestEntity", id));

        assertNotNull(service.findOne(id));


    }

    @Test
    public void findByInsertDate() {

        resetData();

        ZonedDateTime earlyTime = ZonedDateTime.parse("2007-12-03T10:15:30+01:00[Europe/Paris]");
        ZonedDateTime lateTime = ZonedDateTime.parse("2008-12-03T10:15:30+01:00[Europe/Paris]");

        T earlyEntity = makeValidEntity("earlyEntity", "earlyEntity");
        earlyEntity.setInsertDate(earlyTime);
        T lateEntity = makeValidEntity("lateEntity", "lateEntity");
        lateEntity.setInsertDate(lateTime);

        service.createOrUpdate(earlyEntity);
        service.createOrUpdate(lateEntity);

        List<T> betweenReslts = service.findByInsertDate(null, earlyTime, lateTime).toList();
        assertEquals(betweenReslts.size(), 2);

        List<T> afterReslts = service.findByInsertDate(null, earlyTime, null).toList();
        assertNotEquals(afterReslts.size(), 0);

        List<T> beforeReslts = service.findByInsertDate(null, null, lateTime).toList();
        assertNotEquals(beforeReslts.size(), 0);

    }

    @Test
    public void delete() {

        resetData();

        int entityCount = service.findAll(null).toList().size();
        int deletedCount = service.findAllDeleted(null).toList().size();
        T entity = makeValidEntity("testEntity", "testEntity");

        service.createOrUpdate(entity);

        assertEquals(service.findAll(null).toList().size(), entityCount + 1);

        service.delete(entity, "Test");

        assertEquals(service.findAll(null).toList().size(), entityCount);
        assertEquals(service.findAllDeleted(null).toList().size(), deletedCount + 1);

    }

    @Test
    public void findByUser() {

        resetData();

        T entity = makeValidEntity("testEntity", "testEntity");
        User user = entity.getInsertBy();

        int entityCount = service.findByInsertBy(null, user).toList().size();

        service.createOrUpdate(entity);

        assertEquals(service.findByInsertBy(null, user).toList().size(), entityCount + 1);

    }

    protected void resetData() {
        sampleDataGenerator.clearData();
        sampleDataGenerator.generateSampleData();
    }

}
