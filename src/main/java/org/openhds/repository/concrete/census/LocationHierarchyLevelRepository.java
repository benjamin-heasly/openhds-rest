package org.openhds.repository.concrete.census;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.domain.model.census.LocationHierarchyLevel;
import org.openhds.repository.contract.AuditableRepository;

import java.util.Optional;

public interface LocationHierarchyLevelRepository extends AuditableRepository<LocationHierarchyLevel> {

    Optional<LocationHierarchyLevel> findByDeletedFalseAndName(String name);
    Optional<LocationHierarchyLevel> findByDeletedFalseAndKeyIdentifier(int keyIdentifier);

}
