package org.openhds.service.impl;

import org.openhds.domain.model.Location;
import org.openhds.repository.LocationRepository;
import org.openhds.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wolfe on 6/9/15.
 */

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    LocationRepository locationRepository;

    @Override
    public Location create(Location location) {
        validate(location);
        return locationRepository.save(location);
    }

    @Override
    public Location findOne(String uuid) {
        return locationRepository.findOne(uuid);
    }

    @Override
    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    @Override
    public Page<Location> findPaged(Pageable pageable){
        return locationRepository.findAll(pageable);
    }

    @Override
    public Location update(Location location) {
        validate(location);
        return locationRepository.save(location);
    }

    @Override
    public void delete(Location location) {
        //TODO: flag for deletion?
    }



    @Override
    public boolean validate(Location location) {

        // example of what 'validation' could be - checks like this would most likely
        // be handled by annotations however.

        if(null != location.getExtId()){
            return true;
        }
        return false;
    }

    @Override
    public List<Location> findByExtId(String extId) {
        return findByExtId(extId);
    }
}
