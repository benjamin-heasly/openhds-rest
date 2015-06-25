package org.openhds.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openhds.OpenHdsRestApplication;
import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.repository.queries.QueryRange;
import org.openhds.repository.queries.QueryValue;
import org.openhds.repository.util.SampleDataGenerator;
import org.openhds.service.contract.AbstractUuidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by wolfe on 6/17/15.
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OpenHdsRestApplication.class)
@WebAppConfiguration
public abstract class UuidServiceTest<T extends UuidIdentifiable, U extends AbstractUuidService<T, ?>> {

    @Autowired
    protected SampleDataGenerator sampleDataGenerator;

    protected U service;

    protected abstract T makeInvalidEntity();

    protected abstract T makeValidEntity(String name, String id);

    protected abstract void initialize(U service);

    @Before
    public void setup() throws Exception {
        initialize(service);
        sampleDataGenerator.clearData();
        sampleDataGenerator.generateSampleData();
    }


    @Test
    public void create() {

        int beforeCreationSize = service.findAll(null).toList().size();

        service.createOrUpdate(makeValidEntity("testEntity", "testEntity"));

        assertEquals(service.findAll(null).toList().size(), beforeCreationSize + 1);

    }

    @Test
    public void getUnknownEntity() {
        T unknownEntity = service.getUnknownEntity();
        assertNotNull(unknownEntity);
        assertEquals(AbstractUuidService.UNKNOWN_ENTITY_UUID, unknownEntity.getUuid());
    }

    @Test
    public void findByMultipleValues() {
        // query for uuid like an existing entity
        T entity = service.findAll(null).iterator().next();
        QueryValue queryValue = new QueryValue("uuid", entity.getUuid());

        // reuse the same property multiple times in the query
        List<T> results = service.findByMultipleValues(null, queryValue, queryValue, queryValue).toList();
        assertEquals(1, results.size());
        assertEquals(entity, results.get(0));
    }

    @Test
    public void findByMultipleValuesImpossible() {
        // query for uuid like an existing entity
        T entity = service.findAll(null).iterator().next();
        QueryValue queryValue = new QueryValue("uuid", entity.getUuid());
        QueryValue impossibleValue = new QueryValue("uuid", "not an id");

        // reuse the same property multiple times in the query
        List<T> results = service.findByMultipleValues(null, queryValue, queryValue, impossibleValue).toList();
        assertEquals(0, results.size());
    }

    @Test(expected = org.springframework.dao.DataAccessException.class)
    public void findByMultipleValuesInvalid() {
        // query for uuid like an existing entity
        T entity = service.findAll(null).iterator().next();
        QueryValue uuidValue = new QueryValue("uuid", entity.getUuid());
        QueryValue badValue = new QueryValue("notAProperty", entity.getUuid());

        service.findByMultipleValues(null, uuidValue, badValue);
    }

    @Test
    public void findByMultipleValuesRanged() {
        // query for uuid like an existing entity
        T entity = service.findAll(null).iterator().next();
        QueryValue queryValue = new QueryValue("uuid", entity.getUuid());

        // range by a single value
        QueryRange queryRange = new QueryRange("uuid", entity.getUuid(), entity.getUuid());

        // reuse the same property multiple times in the query
        List<T> results = service.findByMultipleValuesranged(null, queryRange, queryValue, queryValue, queryValue).toList();
        assertEquals(1, results.size());
        assertEquals(entity, results.get(0));
    }

    @Test(expected = org.springframework.dao.DataAccessException.class)
    public void findByMultipleValuesRangedInvalid() {
        // query for uuid like an existing entity
        T entity = service.findAll(null).iterator().next();
        QueryValue queryValue = new QueryValue("uuid", entity.getUuid());

        // range by a nonexistent property
        QueryRange queryRange = new QueryRange("notAProperty", entity.getUuid(), entity.getUuid());

        // reuse the same property multiple times in the query
        List<T> results = service.findByMultipleValuesranged(null, queryRange, queryValue, queryValue, queryValue).toList();
    }

    @Test()
    public void findByMultipleValuesRangedImpossible() {
        // query for uuid like an existing entity
        T entity = service.findAll(null).iterator().next();
        QueryValue queryValue = new QueryValue("uuid", entity.getUuid());

        // the range Z-A is impossible
        QueryRange queryRange = new QueryRange("uuid", "Z", "A");

        // reuse the same property multiple times in the query
        List<T> results = service.findByMultipleValuesranged(null, queryRange, queryValue, queryValue, queryValue).toList();
        assertEquals(0, results.size());
    }
}
