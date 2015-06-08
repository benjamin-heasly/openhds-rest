package org.openhds.domain.contract;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.GenericGenerator;
import org.openhds.domain.util.Description;
import org.openhds.security.model.User;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Calendar;

/**
 * @author Dave Roberge
 */
@Description(description = "An AuditableEntity can be any entity stored in the database that needs to be audited.")
@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AuditableEntity implements UuidIdentifiable, Serializable {

    private static final long serialVersionUID = -4703049354466276068L;

    @Id
    @GeneratedValue(generator = "uuidIfMissing")
    @GenericGenerator(name = "uuidIfMissing",
            strategy = "org.openhds.repository.util.IfMissingUuidGenerator")
    @Column(length = 36)
    protected String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @Description(description = "The user that voided the data.")
    protected User voidBy;

    @Description(description = "Reason for voiding the data.")
    protected String voidReason;

    @Description(description = "Indicator for signaling some data to be deleted.")
    protected boolean deleted = false;

    @Description(description = "Date that the data was voided.")
    protected Calendar voidDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @Description(description = "User who inserted the data.")
    protected User insertBy;

    @Description(description = "Date of insertion.")
    protected Calendar insertDate;

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public User getVoidBy() {
        return voidBy;
    }

    public void setVoidBy(User voidBy) {
        this.voidBy = voidBy;
    }

    public String getVoidReason() {
        return voidReason;
    }

    public void setVoidReason(String voidReason) {
        this.voidReason = voidReason;
    }

    @XmlTransient
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Calendar getVoidDate() {
        return voidDate;
    }

    public void setVoidDate(Calendar voidDate) {
        this.voidDate = voidDate;
    }

    public User getInsertBy() {
        return insertBy;
    }

    public void setInsertBy(User insertBy) {
        this.insertBy = insertBy;
    }

    public Calendar getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Calendar insertDate) {
        this.insertDate = insertDate;
    }

    @Override
    public int hashCode() {
        if (null == uuid) {
            return 0;
        }
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final AuditableEntity other = (AuditableEntity) obj;
        if ((this.uuid == null) ? (other.uuid != null) : !this.uuid.equals(other.uuid)) {
            return false;
        }
        return true;
    }

}
