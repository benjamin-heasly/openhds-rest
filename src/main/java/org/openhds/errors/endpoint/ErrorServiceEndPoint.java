package org.openhds.errors.endpoint;


import org.openhds.errors.model.ErrorLog;

public interface ErrorServiceEndPoint {

    void logError(ErrorLog errorLog);

    void updateError(ErrorLog errorLog);
}
