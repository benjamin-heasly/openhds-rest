package org.openhds.errors.endpoint.impl;

import org.openhds.errors.endpoint.ErrorServiceEndPoint;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.ErrorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseEndPoint implements ErrorServiceEndPoint {

    @Autowired
    private ErrorLogRepository errorLogRepository;

    @Override
    public void logError(ErrorLog errorLog) {
        errorLogRepository.save(errorLog);
    }

    @Override
    public void updateError(ErrorLog errorLog) {
        errorLogRepository.save(errorLog);
    }

}
