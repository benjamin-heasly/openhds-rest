package org.openhds.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openhds.OpenHdsRestApplication;
import org.openhds.domain.contract.AuditableEntity;
import org.openhds.repository.util.SampleDataGenerator;
import org.openhds.service.contract.AbstractAuditableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by wolfe on 6/17/15.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OpenHdsRestApplication.class)
@WebAppConfiguration
public abstract class AuditableServiceTest<T extends AuditableEntity,
        U extends AbstractAuditableService>{

    @Autowired
    protected SampleDataGenerator sampleDataGenerator;

    protected U service;

    protected abstract T makeInvalidEntity();
    protected abstract T makeValidEntity(String name, String id, ZonedDateTime insertDate);
    protected abstract void initialize(U service);

    @Before
    public void setup() throws Exception {

        initialize(service);
        resetData();

    }

    @Test
    public void findAll(){

        resetData();
        List<T> results = service.findAll(null).toList();
        assertNotNull(results.get(0));

    }

    @Test
    public void create(){

        resetData();
        service.createOrUpdate(makeValidEntity("testEntity", "testEntity", ZonedDateTime.now()));

    }

    @Test
    public void update() {

        String id = "testId";

        resetData();

        service.createOrUpdate(makeValidEntity("testEntity", id, ZonedDateTime.now()));

        assertNotNull(service.findOne(id));

        service.createOrUpdate(makeValidEntity("updatedTestEntity", id, ZonedDateTime.now()));

        assertNotNull(service.findOne(id));


    }

    @Test
    public void findByInsertDate(){

        resetData();
        ZonedDateTime earlyTime = ZonedDateTime.parse("2007-12-03T10:15:30+01:00[Europe/Paris]");
        ZonedDateTime lateTime = ZonedDateTime.parse("2008-12-03T10:15:30+01:00[Europe/Paris]");

        T earlyEntity = makeValidEntity("earlyEntity", "earlyEntity", earlyTime);
        T lateEntity = makeValidEntity("lateEntity", "lateEntity", lateTime);

        service.createOrUpdate(earlyEntity);
        service.createOrUpdate(lateEntity);

        List<T> betweenReslts = service.findByInsertDate(null,earlyTime,lateTime).toList();
        assertEquals(betweenReslts.size(), 2);

        List<T> afterReslts = service.findByInsertDate(null,earlyTime,null).toList();
        assertNotEquals(afterReslts.size(), 0);

        List<T> beforeReslts = service.findByInsertDate(null,null,lateTime).toList();
        assertNotEquals(beforeReslts.size(), 0);

    }


    private void resetData(){
        sampleDataGenerator.clearData();
        sampleDataGenerator.generateSampleData();
    }

}
