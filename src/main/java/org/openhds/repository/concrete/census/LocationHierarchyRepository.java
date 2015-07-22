package org.openhds.repository.concrete.census;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.domain.model.census.LocationHierarchyLevel;
import org.openhds.repository.contract.AuditableExtIdRepository;

import java.util.List;

public interface LocationHierarchyRepository extends AuditableExtIdRepository<LocationHierarchy> {

    List<LocationHierarchy> findByParent(LocationHierarchy parent);
    List<LocationHierarchy> findByLevel(LocationHierarchyLevel level);

}
