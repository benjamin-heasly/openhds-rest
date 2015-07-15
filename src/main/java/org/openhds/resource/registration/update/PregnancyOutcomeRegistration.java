package org.openhds.resource.registration.update;

import org.openhds.domain.model.update.PregnancyOutcome;
import org.openhds.resource.registration.Registration;

/**
 * Created by Wolfe on 7/15/2015.
 */
public class PregnancyOutcomeRegistration extends Registration<PregnancyOutcome> {

    private PregnancyOutcome pregnancyOutcome;

    private String motherUuid;

    private String fatherUuid;

    private String visitUuid;

    public PregnancyOutcome getPregnancyOutcome() {
        return pregnancyOutcome;
    }

    public void setPregnancyOutcome(PregnancyOutcome pregnancyOutcome) {
        this.pregnancyOutcome = pregnancyOutcome;
    }

    public String getMotherUuid() {
        return motherUuid;
    }

    public void setMotherUuid(String motherUuid) {
        this.motherUuid = motherUuid;
    }

    public String getFatherUuid() {
        return fatherUuid;
    }

    public void setFatherUuid(String fatherUuid) {
        this.fatherUuid = fatherUuid;
    }

    public String getVisitUuid() {
        return visitUuid;
    }

    public void setVisitUuid(String visitUuid) {
        this.visitUuid = visitUuid;
    }
}
