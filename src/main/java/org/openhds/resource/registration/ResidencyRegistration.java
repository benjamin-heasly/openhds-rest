package org.openhds.resource.registration;

import org.openhds.domain.model.Residency;

/**
 * Created by Wolfe on 7/14/2015.
 */
public class ResidencyRegistration extends Registration<Residency> {

    private Residency residency;

    private String individualUuid;

    private String locationUuid;

    public Residency getResidency() {
        return residency;
    }

    public void setResidency(Residency residency) {
        this.residency = residency;
    }

    public String getIndividualUuid() {
        return individualUuid;
    }

    public void setIndividualUuid(String individualUuid) {
        this.individualUuid = individualUuid;
    }

    public String getLocationUuid() {
        return locationUuid;
    }

    public void setLocationUuid(String locationUuid) {
        this.locationUuid = locationUuid;
    }
}
