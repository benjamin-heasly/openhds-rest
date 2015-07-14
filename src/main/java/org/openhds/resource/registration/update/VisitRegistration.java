package org.openhds.resource.registration.update;

import org.openhds.domain.model.update.Visit;
import org.openhds.domain.util.Description;
import org.openhds.resource.registration.Registration;

/**
 * Created by Ben on 6/3/15.
 * <p>
 * Register or update a visit.
 */
@Description(description = "Register or update a Visit.")
public class VisitRegistration extends Registration<Visit> {

    private Visit visit;

    private String locationUuid;

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public String getLocationUuid() {
        return locationUuid;
    }

    public void setLocationUuid(String locationUuid) {
        this.locationUuid = locationUuid;
    }
}
