package org.openhds.helpers;

import org.openhds.domain.model.Location;
import org.springframework.stereotype.Component;

/**
 * Created by wolfe on 6/17/15.
 */
@Component
public class LocationTestHelper extends AbstractTestHelper<Location>{

    @Override
    public Location makeValidEntity(String name, String id) {
        Location location = new Location();
        location.setUuid(id);
        location.setName(name);
        location.setExtId(name);
        return location;
    }

    @Override
    public Location makeInvalidEntity() {
        return new Location();
    }

}
