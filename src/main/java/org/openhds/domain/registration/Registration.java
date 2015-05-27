package org.openhds.domain.registration;

import org.openhds.domain.model.UuidIdentifiable;

import java.util.Calendar;

/**
 * Created by Ben on 5/26/15.
 *
 * Part of the contract for data coming into OpenHDS: entities may not be registered in "raw" form.  They must be
 * wrapped up in a Registration.
 *
 * The Registration carries form-level metadata, like registrationVersion, which should be logged to aid auditing and
 * debugging.
 *
 * The Registration also carries id information to link the registered entity with other entities that must be found
 * or created.  The client can work with this flat representation and it only has to fill in correct ids.   This avoids
 * asking the client to construct a complex object graph to represent a new entity with all its relationships.  This
 * would be brittle and it would expose too much implementation to the client.
 *
 */
public abstract class Registration<T extends UuidIdentifiable> {

    protected int registrationVersion;

    protected String registrationVersionName;

    protected Calendar registrationDateTime;

    protected String registrationSystemName;

    protected String collectedByUuid;

    public int getRegistrationVersion() {
        return registrationVersion;
    }

    public void setRegistrationVersion(int registrationVersion) {
        this.registrationVersion = registrationVersion;
    }

    public String getRegistrationVersionName() {
        return registrationVersionName;
    }

    public void setRegistrationVersionName(String registrationVersionName) {
        this.registrationVersionName = registrationVersionName;
    }

    public Calendar getRegistrationDateTime() {
        return registrationDateTime;
    }

    public void setRegistrationDateTime(Calendar registrationDateTime) {
        this.registrationDateTime = registrationDateTime;
    }

    public String getRegistrationSystemName() {
        return registrationSystemName;
    }

    public void setRegistrationSystemName(String registrationSystemName) {
        this.registrationSystemName = registrationSystemName;
    }

    public String getCollectedByUuid() {
        return collectedByUuid;
    }

    public void setCollectedByUuid(String collectedByUuid) {
        this.collectedByUuid = collectedByUuid;
    }
}
