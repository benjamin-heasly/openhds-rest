package org.openhds.service.impl;

import org.openhds.errors.model.ErrorLog;
import org.openhds.service.AuditableCollectedServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

/**
 * Created by Wolfe on 7/13/2015.
 */
public class ErrorLogServiceTest extends AuditableCollectedServiceTest<ErrorLog, ErrorLogService> {

    @Autowired
    private FieldWorkerService fieldWorkerService;

    @Override
    protected ErrorLog makeInvalidEntity() {
        return new ErrorLog();
    }

    @Override
    protected ErrorLog makeValidEntity(String name, String id) {
        ErrorLog errorLog = new ErrorLog();
        errorLog.setUuid(id);
        errorLog.setResolutionStatus(name);
        errorLog.setEntityType(name);
        errorLog.setCollectionDateTime(ZonedDateTime.now());
        errorLog.setCollectedBy(fieldWorkerService.findAll(UUID_SORT).toList().get(0));
        errorLog.appendError("Test Error");
        return errorLog;


    }

    @Override
    @Autowired
    protected void initialize(ErrorLogService service) {
        this.service = service;
    }

}
