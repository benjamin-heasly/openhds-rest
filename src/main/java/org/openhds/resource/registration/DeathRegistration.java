package org.openhds.resource.registration;

import org.openhds.domain.model.update.Death;

/**
 * Created by Wolfe on 7/14/2015.
 */
public class DeathRegistration extends Registration<Death> {

    private Death death;

    private String visitUuid;

    private String individualUuid;

    public Death getDeath() {
        return death;
    }

    public void setDeath(Death death) {
        this.death = death;
    }

    public String getVisitUuid() {
        return visitUuid;
    }

    public void setVisitUuid(String visitUuid) {
        this.visitUuid = visitUuid;
    }

    public String getIndividualUuid() {
        return individualUuid;
    }

    public void setIndividualUuid(String individualUuid) {
        this.individualUuid = individualUuid;
    }
}
