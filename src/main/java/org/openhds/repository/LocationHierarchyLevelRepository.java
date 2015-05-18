package org.openhds.repository;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.domain.model.LocationHierarchyLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationHierarchyLevelRepository extends JpaRepository<LocationHierarchyLevel, String> {
    Optional<LocationHierarchyLevel> findByKeyIdentifier(int key);
    Optional<LocationHierarchyLevel> findByName(String name);
}
