package org.openhds.errors.model;

/**
 * Created by Wolfe on 6/30/2015.
 */
public class ErrorLogException extends RuntimeException {

    private ErrorLog errorLog;

    public ErrorLogException(ErrorLog errorLog){
        this.errorLog = errorLog;
    }

    public ErrorLog getErrorLog() {
        return errorLog;
    }

    public void setErrorLog(ErrorLog errorLog) {
        this.errorLog = errorLog;
    }
}
