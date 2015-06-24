package org.openhds.service.impl;

import org.openhds.domain.model.FieldWorker;
import org.openhds.errors.endpoint.ErrorServiceEndPoint;
import org.openhds.errors.model.*;
import org.openhds.repository.concrete.ErrorLogRepository;
import org.openhds.repository.results.EntityIterator;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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
        errorLog.setCollectedBy(fieldWorkerService.getUnknownEntity());
        return errorLog;
    }

    public ErrorLog logError(ErrorLog error) {
        if (null == error.getCollectedBy()) {
            error.setCollectedBy(fieldWorkerService.getUnknownEntity());
        }

        for (ErrorServiceEndPoint errorEndPoint : errorEndPoints) {
            errorEndPoint.logError(error);
        }

        return error;
    }

    public ErrorLog findErrorById(String id) {
        return repository.findOne(id);
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
