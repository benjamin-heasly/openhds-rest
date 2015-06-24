package org.openhds.repository.concrete;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.domain.model.FieldWorker;
import org.openhds.repository.contract.AuditableRepository;

import java.util.List;

public interface FieldWorkerRepository extends AuditableRepository<FieldWorker> {
    List<FieldWorker> findByFirstName(String name);
    List<FieldWorker> findByLastName(String name);
}
