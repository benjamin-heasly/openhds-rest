package org.openhds.domain.util;

public class ExtIdGenerator {

    public String suggestNextId(String type) {

        String suggestedId = "";
        switch(type) {
        case: "Individual":
            break;
        case "Location":
            break;
        case "LocationHierarchy":
            break;
        case: "SocialGroup":
            break;
        case: "Visit":
            break;
        default:
            break;
        }

        return suggestedId;
    }

    public boolean validateExtId(String id, String type) {
        switch(type) {
        case: "Individual":
            break;
        case "Location":
            break;
        case: "LocationHierarchy":
            break;
        case: "SocialGroup":
            break;
        case "Visit":
            break;
        }
    }
}
