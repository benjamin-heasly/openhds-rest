package org.openhds.resource.controller;

import org.openhds.errors.model.ErrorLog;
import org.openhds.errors.model.Error;
import org.openhds.repository.concrete.ErrorLogRepository;
import org.openhds.repository.concrete.FieldWorkerRepository;
import org.openhds.resource.contract.AuditableCollectedRestControllerTest;
import org.openhds.resource.registration.ErrorLogRegistration;
import org.openhds.resource.registration.Registration;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by bsh on 6/29/15.
 */
public class ErrorLogRestControllerTest extends AuditableCollectedRestControllerTest
        <ErrorLog, ErrorLogRepository, ErrorLogRestController> {

    @Autowired
    FieldWorkerRepository fieldWorkerRepository;

    @Autowired
    @Override
    protected void initialize(ErrorLogRepository repository, ErrorLogRestController controller) {
        this.repository = repository;
        this.controller = controller;
    }

    @Override
    protected ErrorLog makeValidEntity(String name, String id) {
        ErrorLog errorLog = new ErrorLog();
        errorLog.setUuid(id);
        errorLog.setDataPayload(name);

        Error error = new Error();
        error.setErrorMessage(name);
        errorLog.getErrors().add(error);

        return errorLog;
    }

    @Override
    protected ErrorLog makeInvalidEntity() {
        return new ErrorLog();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(ErrorLog entity, String name, String id) {
        assertNotNull(entity);

        ErrorLog savedErrorLog = repository.findOne(id);
        assertNotNull(savedErrorLog);

        assertEquals(id, savedErrorLog.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getDataPayload(), savedErrorLog.getDataPayload());
    }

    @Override
    protected Registration<ErrorLog> makeRegistration(ErrorLog entity) {
        ErrorLogRegistration errorLogRegistration = new ErrorLogRegistration();
        errorLogRegistration.setErrorLog(entity);
        errorLogRegistration.setCollectedByUuid(fieldWorkerRepository.findAll().get(0).getUuid());
        return errorLogRegistration;
    }
}
