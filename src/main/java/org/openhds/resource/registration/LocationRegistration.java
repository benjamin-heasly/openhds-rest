package org.openhds.resource.registration;

import org.openhds.domain.model.census.Location;
import org.openhds.domain.util.Description;

/**
 * Created by Ben on 5/26/15.
 * <p>
 * Register a Location at a known place in the location hierarchy.
 */
@Description(description = "Register a Location at a known place in the location hierarchy.")
public class LocationRegistration extends Registration<Location> {

    private Location location;

    private String locationHierarchyUuid;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getLocationHierarchyUuid() {
        return locationHierarchyUuid;
    }

    public void setLocationHierarchyUuid(String locationHierarchyUuid) {
        this.locationHierarchyUuid = locationHierarchyUuid;
    }
}
