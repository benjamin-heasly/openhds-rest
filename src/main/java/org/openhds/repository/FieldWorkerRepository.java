package org.openhds.repository;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.domain.model.FieldWorker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FieldWorkerRepository extends JpaRepository<FieldWorker, String> {
    Optional<FieldWorker> findByExtId(String extId);
    List<FieldWorker> findByFirstName(String name);
    List<FieldWorker> findByLastName(String name);
}
