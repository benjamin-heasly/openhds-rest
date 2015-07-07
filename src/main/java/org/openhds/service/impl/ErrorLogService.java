package org.openhds.service.impl;

import org.openhds.domain.model.FieldWorker;
import org.openhds.errors.endpoint.ErrorServiceEndPoint;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.ErrorLogRepository;
import org.openhds.repository.results.EntityIterator;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class ErrorLogService extends AbstractAuditableCollectedService<ErrorLog, ErrorLogRepository> {

    @Autowired
    public ErrorLogService(ErrorLogRepository repository) {
        super(repository);
    }

    @Autowired
    private List<ErrorServiceEndPoint> errorEndPoints;

    @Autowired
    private FieldWorkerService fieldWorkerService;

    @Override
    protected ErrorLog makeUnknownEntity() {
        ErrorLog errorLog = new ErrorLog();
        errorLog.setCollectionDateTime(ZonedDateTime.now());
        errorLog.setCollectedBy(fieldWorkerService.getUnknownEntity());
        return errorLog;
    }

    public ErrorLog logError(ErrorLog errorLog) {
        if (null == errorLog.getCollectedBy()) {
            errorLog.setCollectedBy(fieldWorkerService.getUnknownEntity());
        }

        if (null == errorLog.getCollectionDateTime()) {
            errorLog.setCollectionDateTime(ZonedDateTime.now());
        }

        for (ErrorServiceEndPoint errorEndPoint : errorEndPoints) {
            errorEndPoint.logError(errorLog);
        }

        return errorLog;
    }

    public EntityIterator<ErrorLog> findAllErrorsByEntityType(String entityType, Sort sort) {
        return iteratorFromPageable(pageable -> repository.findByEntityType(entityType, pageable), sort);
    }

    public ErrorLog updateErrorLog(ErrorLog error) {
        for (ErrorServiceEndPoint errorEndPoint : errorEndPoints) {
            errorEndPoint.updateError(error);
        }

        return error;
    }

    public EntityIterator<ErrorLog> findAllErrorsByResolutionStatus(String resolutionStatus, Sort sort) {
        return iteratorFromPageable(pageable -> repository.findByResolutionStatus(resolutionStatus, pageable), sort);
    }

    public EntityIterator<ErrorLog> findAllErrorsByAssignment(String assignedTo, Sort sort) {
        return iteratorFromPageable(pageable -> repository.findByAssignedTo(assignedTo, pageable), sort);
    }

    public EntityIterator<ErrorLog> findAllErrorsByFieldWorker(FieldWorker fieldWorker, Sort sort) {
        return iteratorFromPageable(pageable -> repository.findByDeletedFalseAndCollectedBy(fieldWorker, pageable), sort);
    }


}
