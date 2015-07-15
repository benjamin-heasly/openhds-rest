package org.openhds.resource.registration.update;

import org.openhds.domain.model.update.PregnancyResult;
import org.openhds.resource.registration.Registration;

/**
 * Created by Wolfe on 7/15/2015.
 */
public class PregnancyResultRegistration extends Registration<PregnancyResult> {

    private PregnancyResult pregnancyResult;

    private String pregnancyOutcomeUuid;

    private String childUuid;

    public PregnancyResult getPregnancyResult() {
        return pregnancyResult;
    }

    public void setPregnancyResult(PregnancyResult pregnancyResult) {
        this.pregnancyResult = pregnancyResult;
    }

    public String getPregnancyOutcomeUuid() {
        return pregnancyOutcomeUuid;
    }

    public void setPregnancyOutcomeUuid(String pregnancyOutcomeUuid) {
        this.pregnancyOutcomeUuid = pregnancyOutcomeUuid;
    }

    public String getChildUuid() {
        return childUuid;
    }

    public void setChildUuid(String childUuid) {
        this.childUuid = childUuid;
    }
}
