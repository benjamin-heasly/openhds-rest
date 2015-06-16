package org.openhds.repository;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.domain.model.Location;
import org.openhds.domain.model.LocationHierarchy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends AuditableCollectedRepository<Location> {

    List<Location> findByName(String name);
    List<Location> findByLocationHierarchy(LocationHierarchy parent);

}
