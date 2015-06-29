package org.openhds.resource.registration;

import org.openhds.errors.model.ErrorLog;

/**
 * Created by bsh on 6/29/15.
 */
public class ErrorLogRegistration extends Registration<ErrorLog> {

    private ErrorLog errorLog;

    public ErrorLog getErrorLog() {
        return errorLog;
    }

    public void setErrorLog(ErrorLog errorLog) {
        this.errorLog = errorLog;
    }
}
