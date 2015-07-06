package org.openhds.repository.concrete;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.domain.model.LocationHierarchyLevel;
import org.openhds.repository.contract.AuditableRepository;

import java.util.Optional;

public interface LocationHierarchyLevelRepository extends AuditableRepository<LocationHierarchyLevel> {

    Optional<LocationHierarchyLevel> findByDeletedFalseAndKeyIdentifier(int key);
    Optional<LocationHierarchyLevel> findByDeletedFalseAndName(String name);

}
