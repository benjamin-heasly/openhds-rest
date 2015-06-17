package org.openhds.repository;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.domain.model.LocationHierarchyLevel;

import java.util.Optional;

public interface LocationHierarchyLevelRepository extends AuditableRepository<LocationHierarchyLevel> {
    Optional<LocationHierarchyLevel> findByKeyIdentifier(int key);
    Optional<LocationHierarchyLevel> findByName(String name);
}
