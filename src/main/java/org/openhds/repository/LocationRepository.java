package org.openhds.repository;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.domain.model.Location;
import org.openhds.domain.model.LocationHierarchy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, String> {
    Optional<Location> findByExtId(String extId);
    List<Location> findByName(String name);
    List<Location> findByLocationHierarchy(LocationHierarchy parent);
}
