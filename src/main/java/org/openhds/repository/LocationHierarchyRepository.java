package org.openhds.repository;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.domain.model.LocationHierarchy;
import org.openhds.domain.model.LocationHierarchyLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationHierarchyRepository extends JpaRepository<LocationHierarchy, String> {
    List<LocationHierarchy> findByExtId(String extId);
    List<LocationHierarchy> findByName(String name);
    List<LocationHierarchy> findByParent(LocationHierarchy parent);
    List<LocationHierarchy> findByLevel(LocationHierarchyLevel locationHierarchyLevel);
}
