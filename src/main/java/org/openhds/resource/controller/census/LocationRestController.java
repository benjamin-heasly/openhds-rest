package org.openhds.resource.controller.census;

import org.openhds.domain.model.FieldWorker;
import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.census.Location;
import org.openhds.repository.queries.QueryValue;
import org.openhds.repository.results.EntityIterator;
import org.openhds.resource.contract.AuditableExtIdRestController;
import org.openhds.resource.registration.census.LocationRegistration;
import org.openhds.service.contract.AbstractUuidService;
import org.openhds.service.impl.FieldWorkerService;
import org.openhds.service.impl.census.LocationHierarchyService;
import org.openhds.service.impl.census.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Ben on 5/18/15.
 */
@RestController
@RequestMapping("/locations")
@ExposesResourceFor(Location.class)
public class LocationRestController extends AuditableExtIdRestController<
        Location,
        LocationRegistration,
        LocationService> {

    private final LocationService locationService;

    private final LocationHierarchyService locationHierarchyService;

    private final FieldWorkerService fieldWorkerService;

    @Autowired
    public LocationRestController(LocationService locationService,
                                  LocationHierarchyService locationHierarchyService,
                                  FieldWorkerService fieldWorkerService) {
        super(locationService);
        this.locationService = locationService;
        this.locationHierarchyService = locationHierarchyService;
        this.fieldWorkerService = fieldWorkerService;
    }

    @Override
    protected LocationRegistration makeSampleRegistration(Location entity) {
        LocationRegistration registration = new LocationRegistration();
        registration.setLocation(entity);
        registration.setLocationHierarchyUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        registration.setCollectedByUuid(AbstractUuidService.UNKNOWN_ENTITY_UUID);
        return registration;

    }

    @Override
    protected Location register(LocationRegistration registration) {
        checkRegistrationFields(registration.getLocation(), registration);
        return locationService.recordLocation(registration.getLocation(),
                registration.getLocationHierarchyUuid(),
                registration.getCollectedByUuid());
    }

    @Override
    protected Location register(LocationRegistration registration, String id) {
        registration.getLocation().setUuid(id);
        return register(registration);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public List<Location> search(@RequestParam Map<String, String> fields) {
        List<QueryValue> collect = fields.entrySet().stream().map(f -> new QueryValue(f.getKey(), f.getValue())).collect(Collectors.toList());
        QueryValue[] queryFields = {};
        queryFields = collect.toArray(queryFields);
        return locationService.findByMultipleValues(new Sort("name"), queryFields).toList();
    }

    @RequestMapping(value = "/findByFieldWorker", method = RequestMethod.GET)
    public List<Location> findByFieldWorker(@RequestParam String fieldWorkerId) {
        EntityIterator<FieldWorker> fieldWorkers = fieldWorkerService.findByFieldWorkerId(new Sort("fieldWorkerId"), fieldWorkerId);

        // This is hacky because we get back an entity iterator and it's not readily streamable
        List <Location> results = new ArrayList<>();

        for(FieldWorker fw: fieldWorkers) {
            EntityIterator<Location> locations = locationService.findByCollectedBy(new Sort("uuid"), fw);
            for(Location location: locations) {
                results.add(location);
            }
        }
        return results;
    }



    @RequestMapping(value = "/submitEdited/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> editLocation(@PathVariable String id, @RequestBody Map<String,String> locationStub) {

        Location loc = locationService.findOne(id);
        if(loc == null){
            return new ResponseEntity<String>("Could not find location", HttpStatus.NOT_FOUND);
        }

        if(locationStub.containsKey("name")){
            loc.setName(locationStub.get("name"));
        }

        if(locationStub.containsKey("type")){
            loc.setType(locationStub.get("type"));
        }

        if(locationStub.containsKey("status")){
            loc.setEntityStatus(locationStub.get("status"));
        }

        locationService.recordLocation(loc, loc.getLocationHierarchy().getUuid(), loc.getCollectedBy().getFieldWorkerId());


        return new ResponseEntity<String>("Location edit successful", HttpStatus.CREATED);
    }
}
