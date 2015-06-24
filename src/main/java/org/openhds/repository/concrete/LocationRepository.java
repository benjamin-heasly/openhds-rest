package org.openhds.repository.concrete;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.domain.model.Location;
import org.openhds.domain.model.LocationHierarchy;
import org.openhds.repository.contract.AuditableExtIdRepository;

import java.util.List;

public interface LocationRepository extends AuditableExtIdRepository<Location> {

    List<Location> findByName(String name);
    List<Location> findByLocationHierarchy(LocationHierarchy parent);

}
