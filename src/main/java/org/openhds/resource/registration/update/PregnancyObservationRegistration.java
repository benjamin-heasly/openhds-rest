package org.openhds.resource.registration.update;

import org.openhds.domain.model.update.PregnancyObservation;
import org.openhds.domain.util.Description;
import org.openhds.resource.registration.Registration;

/**
 * Created by Ben on 6/3/15.
 * <p>
 * Register or update a pregnancy observation.
 */
@Description(description = "Register or update a pregnancy observation.")
public class PregnancyObservationRegistration extends Registration<PregnancyObservation> {

    private PregnancyObservation pregnancyObservation;

    private String visitUuid;

    private String motherUuid;

    public PregnancyObservation getPregnancyObservation() {
        return pregnancyObservation;
    }

    public void setPregnancyObservation(PregnancyObservation pregnancyObservation) {
        this.pregnancyObservation = pregnancyObservation;
    }

    public String getVisitUuid() {
        return visitUuid;
    }

    public void setVisitUuid(String visitUuid) {
        this.visitUuid = visitUuid;
    }

    public String getMotherUuid() {
        return motherUuid;
    }

    public void setMotherUuid(String motherUuid) {
        this.motherUuid = motherUuid;
    }
}
