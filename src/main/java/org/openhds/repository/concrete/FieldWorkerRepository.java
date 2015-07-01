package org.openhds.repository.concrete;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.domain.model.FieldWorker;
import org.openhds.repository.contract.AuditableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FieldWorkerRepository extends AuditableRepository<FieldWorker> {

    Page<FieldWorker> findByDeletedFalseAndFieldWorkerId(String extId, Pageable pageable);
    Page<FieldWorker> findByDeletedFalseAndFirstName(String firstName, Pageable pageable);
    Page<FieldWorker> findByDeletedFalseAndLastName(String lastName, Pageable pageable);

}
