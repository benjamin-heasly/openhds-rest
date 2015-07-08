package org.openhds.errors.util;

import org.openhds.errors.model.ErrorLog;
import org.openhds.service.impl.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Wolfe on 7/6/2015.
 *
 * This class, like EventPublisher and UserHelper is necessary to break the self-depedency cycle created when
 * ErrorLogService is autowired by its super type AbstractAuditableService. It will autowire this instead and avoid that
 * problem.
 *
 */
@Component
public class ErrorLogger {

    @Autowired
    private ErrorLogService errorLogService;

    public ErrorLog log(ErrorLog errorLog){
        return errorLogService.createOrUpdate(errorLog);
    }

}
