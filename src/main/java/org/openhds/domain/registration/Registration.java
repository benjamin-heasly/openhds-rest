package org.openhds.domain.registration;

import org.openhds.domain.model.UuidIdentifiable;

import java.util.Calendar;

/**
 * Created by Ben on 5/26/15.
 *
 * Part of the contract for data coming into OpenHDS.  Raw entities may not be registered by themselves.
 * They must be wrapped up in a Registration, which carries additional metadata.
 *
 * Some of the metadata is optional "form" data.  For example, a version number or external system would be logged
 * and might help when debugging.
 *
 * Subclasses must also declare entity data fields, such as the main entity being registered, and the Uuids of related
 * entities that must be created or updated.
 *
 */
public abstract class Registration<T extends UuidIdentifiable> {

    protected int registrationVersion;

    protected String registrationVersionName;

    protected Calendar registrationDateTime;

    protected String registrationSystemName;

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
}
