package org.openhds.resource.controller;

import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.*;
import org.openhds.resource.contract.AuditableCollectedRestController;
import org.openhds.resource.registration.ErrorLogRegistration;
import org.openhds.service.impl.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by bsh on 6/29/15.
 */
public class ErrorLogRestController extends AuditableCollectedRestController<ErrorLog, ErrorLogRegistration> {

    private final ErrorLogService errorLogService;

    @Autowired
    public ErrorLogRestController(ErrorLogRepository errorLogRepository, ErrorLogService errorLogService) {
        super(errorLogRepository);
        this.errorLogService = errorLogService;
    }

    @Override
    protected ErrorLog register(ErrorLogRegistration registration) {
        return null;
    }

    @Override
    protected ErrorLog register(ErrorLogRegistration registration, String id) {
        return null;
    }
}
