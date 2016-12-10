package org.openhds.domain.util;

import org.openhds.service.impl.census.LocationHierarchyService;
import org.openhds.service.impl.census.LocationService;
import org.openhds.service.impl.census.SocialGroupService;
import org.openhds.service.impl.update.VisitService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import org.openhds.service.impl.census.IndividualService;

import java.util.Map;

@Component
public class ExtIdGeneratorImpl implements ExtIdGenerator {

    private Long individualCounter = 0l;
    private Long locationCounter = 0l;
    private Long locationHierarchyCounter = 0l;
    private Long socialGroupCounter = 0l;
    private Long visitCounter = 0l;

    @Autowired IndividualService individualService;
    @Autowired LocationService locationService;
    @Autowired LocationHierarchyService locationHierarchyService;
    @Autowired SocialGroupService socialGroupService;
    @Autowired VisitService visitService;

    public String suggestNextId(Map<String, Object> data) {
        String type = (String)data.get("type");
        switch(type) {
        case "Individual":
            return individualExtId();
        case "Location":
            return locationExtId();
        case "LocationHierarchy":
            return locationHierarchyExtId();
        case "SocialGroup":
            return socialGroupExtId();
        case "Visit":
            return visitExtId();
        default:
            throw new IllegalArgumentException(type + " is not a valid entity type");
        }
    }

    public boolean validateExtId(String id, Map<String, Object> data) {
        String type = (String)data.get("type");

        switch(type) {
        case "Individual":
            return !individualService.findByExtId(new Sort("extId"), id).iterator().hasNext();
        case "Location":
            return !locationService.findByExtId(new Sort("extId"), id).iterator().hasNext();
        case "LocationHierarchy":
            return !locationHierarchyService.findByExtId(new Sort("extId"), id).iterator().hasNext();
        case "SocialGroup":
            return !socialGroupService.findByExtId(new Sort("extId"), id).iterator().hasNext();
        case "Visit":
            return !visitService.findByExtId(new Sort("extId"), id).iterator().hasNext();
        default:
            throw new IllegalArgumentException(type + " is not a valid entity type");
        }
    }

    private synchronized String individualExtId() {
        return "individual-" + individualCounter++;
    }

    private synchronized String locationExtId() {
        return "location-" + locationCounter++;
    }

    private synchronized String locationHierarchyExtId() {
        return "location-hierarchy-" + locationHierarchyCounter++;
    }

    private synchronized String socialGroupExtId() {
        return "social-group-" + socialGroupCounter++;
    }

    private synchronized String visitExtId() {
        return "visit-" + visitCounter++;
    }
}
