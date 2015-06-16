package org.openhds.repository;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.domain.model.Location;
import org.openhds.domain.model.LocationHierarchy;

import java.util.List;

public interface LocationRepository extends AuditableExtIdRepository<Location> {

    List<Location> findByName(String name);
    List<Location> findByLocationHierarchy(LocationHierarchy parent);

}
