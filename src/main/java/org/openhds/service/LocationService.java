package org.openhds.service;

import org.openhds.domain.model.Location;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wolfe on 6/9/15.
 */

public interface LocationService extends OpenHdsService<Location> {

    List<Location> findByExtId(String extId);

}
