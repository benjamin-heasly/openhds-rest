package org.openhds.service;

import org.junit.After;
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
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by wolfe on 6/17/15.
 *
 *  These service unit tests test the type hierarchy for the services.
 *
 *  Each class and its subtypes has tests the test the methods specific to that
 *  particular layer of the hierarchy. For example: AuditableCollectedServiceTest only tests
 *  the methods added in AbstractAuditableCollectedService like findByCollectionTime.
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

    protected final Sort UUID_SORT = new Sort("uuid");

    @Before
    public void setup() throws Exception {
        initialize(service);
        sampleDataGenerator.clearData();
        sampleDataGenerator.generateSampleData();
    }

    @After
    public void tearDown() {
        sampleDataGenerator.clearData();
        sampleDataGenerator.generateSampleData();
    }

    @Test
    @WithUserDetails
    public void create() {

        int beforeCreationSize = service.findAll(UUID_SORT).toList().size();

        service.createOrUpdate(makeValidEntity("testEntity", "testEntity"));

        assertEquals(service.findAll(UUID_SORT).toList().size(), beforeCreationSize + 1);

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
        T entity = service.findAll(UUID_SORT).iterator().next();
        QueryValue queryValue = new QueryValue("uuid", entity.getUuid());

        // reuse the same property multiple times in the query
        List<T> results = service.findByMultipleValues(UUID_SORT, queryValue, queryValue, queryValue).toList();
        assertEquals(1, results.size());
        assertEquals(entity, results.get(0));
    }

    @Test
    public void findByMultipleValuesImpossible() {
        // query for uuid like an existing entity
        T entity = service.findAll(UUID_SORT).iterator().next();
        QueryValue queryValue = new QueryValue("uuid", entity.getUuid());
        QueryValue impossibleValue = new QueryValue("uuid", "not an id");

        // reuse the same property multiple times in the query
        List<T> results = service.findByMultipleValues(UUID_SORT, queryValue, queryValue, impossibleValue).toList();
        assertEquals(0, results.size());
    }

    @Test(expected = org.springframework.dao.DataAccessException.class)
    public void findByMultipleValuesInvalid() {
        // query for uuid like an existing entity
        T entity = service.findAll(UUID_SORT).iterator().next();
        QueryValue uuidValue = new QueryValue("uuid", entity.getUuid());
        QueryValue badValue = new QueryValue("notAProperty", entity.getUuid());

        service.findByMultipleValues(UUID_SORT, uuidValue, badValue);
    }

    @Test
    public void findByMultipleValuesRanged() {
        // query for uuid like an existing entity
        T entity = service.findAll(UUID_SORT).iterator().next();
        QueryValue queryValue = new QueryValue("uuid", entity.getUuid());

        // range by a single value
        QueryRange<String> queryRange = new QueryRange<>("uuid", entity.getUuid(), entity.getUuid());

        // reuse the same property multiple times in the query
        List<T> results = service.findByMultipleValuesRanged(UUID_SORT, queryRange, queryValue, queryValue, queryValue).toList();
        assertEquals(1, results.size());
        assertEquals(entity, results.get(0));
    }

    @Test(expected = org.springframework.dao.DataAccessException.class)
    public void findByMultipleValuesRangedInvalid() {
        // query for uuid like an existing entity
        T entity = service.findAll(UUID_SORT).iterator().next();
        QueryValue queryValue = new QueryValue("uuid", entity.getUuid());

        // range by a nonexistent property
        QueryRange<String> queryRange = new QueryRange<>("notAProperty", entity.getUuid(), entity.getUuid());

        // reuse the same property multiple times in the query
        service.findByMultipleValuesRanged(UUID_SORT, queryRange, queryValue, queryValue, queryValue).toList();
    }

    @Test()
    public void findByMultipleValuesRangedImpossible() {
        // query for uuid like an existing entity
        T entity = service.findAll(UUID_SORT).iterator().next();
        QueryValue queryValue = new QueryValue("uuid", entity.getUuid());

        // the range Z-A is impossible
        QueryRange<String> queryRange = new QueryRange<>("uuid", "Z", "A");

        // reuse the same property multiple times in the query
        List<T> results = service.findByMultipleValuesRanged(UUID_SORT, queryRange, queryValue, queryValue, queryValue).toList();
        assertEquals(0, results.size());
    }
}
