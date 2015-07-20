package org.openhds.resource.registration.census;

import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.domain.util.Description;
import org.openhds.resource.registration.Registration;

/**
 * Created by Ben on 5/26/15.
 * <p>
 * Register a LocationHierarchy within the larger hierarchy structure.
 */
@Description(description = "Register a LocationHierarchy within the larger hierarchy structure. ")
public class LocationHierarchyRegistration extends Registration<LocationHierarchy> {

    private LocationHierarchy locationHierarchy;

    private String parentUuid;

    private String levelUuid;

    public LocationHierarchy getLocationHierarchy() {
        return locationHierarchy;
    }

    public void setLocationHierarchy(LocationHierarchy locationHierarchy) {
        this.locationHierarchy = locationHierarchy;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    public String getLevelUuid() {
        return levelUuid;
    }

    public void setLevelUuid(String levelUuid) {
        this.levelUuid = levelUuid;
    }
}
