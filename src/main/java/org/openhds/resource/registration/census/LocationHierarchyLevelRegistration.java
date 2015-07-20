package org.openhds.resource.registration.census;

import org.openhds.domain.model.census.LocationHierarchyLevel;
import org.openhds.domain.util.Description;
import org.openhds.resource.registration.Registration;

/**
 * Created by Ben on 5/26/15.
 * <p>
 * Register a LocationHierarchyLevel to organize the larger hierarchy structure.
 */
@Description(description = "Register a LocationHierarchyLevel to organize the larger hierarchy structure.")
public class LocationHierarchyLevelRegistration extends Registration<LocationHierarchyLevel> {

    private LocationHierarchyLevel locationHierarchyLevel;

    public LocationHierarchyLevel getLocationHierarchyLevel() {
        return locationHierarchyLevel;
    }

    public void setLocationHierarchyLevel(LocationHierarchyLevel locationHierarchyLevel) {
        this.locationHierarchyLevel = locationHierarchyLevel;
    }
}
