package org.openhds.errors.util;

import org.openhds.errors.model.ErrorLog;
import org.openhds.service.impl.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Wolfe on 7/6/2015.
 *
 * Used to break the self-dependency cycle of ErrorLogService autowiring itself
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
