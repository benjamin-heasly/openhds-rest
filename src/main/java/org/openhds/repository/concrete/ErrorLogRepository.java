package org.openhds.repository.concrete;

import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.contract.AuditableCollectedRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by Ben on 5/4/15.
 */
public interface ErrorLogRepository extends AuditableCollectedRepository<ErrorLog> {
    Page<ErrorLog> findByEntityType(String entityType, Pageable pageable);
    Page<ErrorLog> findByResolutionStatus(String resolutionStatus, Pageable pageable);
    Page<ErrorLog> findByAssignedTo(String assignedTo, Pageable pageable);
}
