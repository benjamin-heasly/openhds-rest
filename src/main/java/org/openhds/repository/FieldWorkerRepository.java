package org.openhds.repository;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.domain.model.FieldWorker;

import java.util.List;

public interface FieldWorkerRepository extends AuditableRepository<FieldWorker> {
    List<FieldWorker> findByExtId(String extId);
    List<FieldWorker> findByFirstName(String name);
    List<FieldWorker> findByLastName(String name);
}
