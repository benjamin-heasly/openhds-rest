package org.openhds.domain.util;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import org.openhds.service.impl.census.IndividualService;

@Component
public class ExtIdGenerator {

    private Long individualCounter = 0l;
    private Long locationCounter = 0l;
    private Long locationHierarchyCounter = 0l;
    private Long socialGroupCounter = 0l;
    private Long visitCounter = 0l;

    public String suggestNextId(String type) {

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

    public boolean validateExtId(String id, String type) {

        boolean result = false;

        switch(type) {
        case "Individual":
            break;
        case "Location":
            break;
        case "LocationHierarchy":
            break;
        case "SocialGroup":
            break;
        case "Visit":
            break;
        default:
            throw new IllegalArgumentException(type + " is not a valid entity type");
        }

        return result;
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
