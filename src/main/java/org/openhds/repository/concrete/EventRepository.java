package org.openhds.repository.concrete;

/**
 * Created by Ben on 5/4/15.
 */

import org.openhds.domain.model.LocationHierarchy;
import org.openhds.events.model.Event;
import org.openhds.repository.contract.AuditableRepository;

public interface EventRepository extends AuditableRepository<Event> {
}
