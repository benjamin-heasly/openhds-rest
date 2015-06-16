package org.openhds.service.impl;

import org.openhds.domain.model.Location;
import org.openhds.repository.LocationRepository;
import org.openhds.service.AbstractAuditableCollectedService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

/**
 * Created by wolfe on 6/9/15.
 */

@Service
public class LocationService extends AbstractAuditableCollectedService<Location>{

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        super(locationRepository);
    }

}
