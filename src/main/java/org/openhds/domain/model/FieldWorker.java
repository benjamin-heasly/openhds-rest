package org.openhds.domain.model;

import org.openhds.domain.util.Description;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Description(description = "A Field Worker represents a surveyor working in the study area.")
@Entity
@Table(name = "fieldworker")
public class FieldWorker extends AuditableEntity implements Serializable {

    private static final long serialVersionUID = -7550088299362704483L;

    @NotNull
    @Description(description = "User-facing Id of the field worker")
    String extId;

    @Description(description = "First name of the field worker.")
    String firstName;

    @Description(description = "Last name of the field worker.")
    String lastName;

    @Description(description = "Password entered for a new field worker.")
    @Transient
    String password;

    @NotNull
    @Description(description = "Hashed version of a field worker's password.")
    String passwordHash;

    @Description(description = "The ID prefix used in individual extId generation.")
    int idPrefix;


    public int getIdPrefix() {
        return idPrefix;
    }

    public void setIdPrefix(int idPrefix) {
        this.idPrefix = idPrefix;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof FieldWorker)) {
            return false;
        }

        final String otherUuid = ((FieldWorker) other).getUuid();
        return null != uuid && null != otherUuid && uuid.equals(otherUuid);
    }

}
