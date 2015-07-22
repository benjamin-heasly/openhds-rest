package org.openhds.service.impl;

import org.openhds.errors.model.ErrorLog;
import org.openhds.service.AuditableCollectedServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

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
    @Autowired
    protected void initialize(ErrorLogService service) {
        this.service = service;
    }

}
