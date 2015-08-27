package org.openhds.domain.contract;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.GenericGenerator;
import org.openhds.domain.util.Description;
import org.openhds.security.model.User;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * @author Dave Roberge
 */
@Description(description = "An AuditableEntity can be any entity stored in the database that needs to be audited.")
@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AuditableEntity implements UuidIdentifiable, Serializable {

    public final static String PLACEHOLDER_STATUS = "PLACEHOLDER_STATUS";
    public final static String UNKNOWN_STATUS = "UNKNOWN_STATUS";
    public final static String NORMAL_STATUS = "NORMAL_STATUS";

    private static final long serialVersionUID = -4703049354466276068L;

    @Id
    @GeneratedValue(generator = "uuidIfMissing")
    @GenericGenerator(name = "uuidIfMissing",
            strategy = "org.openhds.repository.util.IfMissingUuidGenerator")
    @Column(length = 36)
    protected String uuid;

    @Description(description = "Marker for whether or not the entity is a placeholder or unk entity.")
    protected String entityStatus = NORMAL_STATUS;

    @Description(description = "Marker for soft delete / void of the record.")
    protected boolean deleted = false;

    @ManyToOne
    @Description(description = "The User who voided the record.")
    protected User voidBy;

    @Description(description = "Reason for voiding the record.")
    protected String voidReason;

    @Description(description = "Date that the record was voided.")
    protected ZonedDateTime voidDate;

    @ManyToOne
    @Description(description = "User who first inserted the record.")
    protected User insertBy;

    @Description(description = "Date of insertion.")
    protected ZonedDateTime insertDate;

    @ManyToOne
    @Description(description = "User who last updated inserted the record.")
    protected User lastModifiedBy;

    @Description(description = "Date of insertion.")
    protected ZonedDateTime lastModifiedDate;

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

    public String getEntityStatus() {
        return entityStatus;
    }
    public void setEntityStatus(String entityStatus) {
        this.entityStatus = entityStatus;
    }

    @XmlTransient
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public ZonedDateTime getVoidDate() {
        return voidDate;
    }

    public void setVoidDate(ZonedDateTime voidDate) {
        this.voidDate = voidDate;
    }

    public User getInsertBy() {
        return insertBy;
    }

    public void setInsertBy(User insertBy) {
        this.insertBy = insertBy;
    }

    public ZonedDateTime getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(ZonedDateTime insertDate) {
        this.insertDate = insertDate;
    }

    public User getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(User lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public ZonedDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(ZonedDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public boolean isModifiedInRange(ZonedDateTime modifiedAfter, ZonedDateTime modifiedBefore) {
        return ((null == modifiedAfter || lastModifiedDate.isEqual(modifiedAfter) || lastModifiedDate.isAfter(modifiedAfter)) &&
                (null == modifiedBefore || lastModifiedDate.isEqual(modifiedBefore) || lastModifiedDate.isBefore(modifiedBefore)));
    }

    @Override
    public int hashCode() {
        if (null == uuid) {
            return 0;
        }
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (getClass() != other.getClass()) {
            return false;
        }

        final String otherUuid = ((UuidIdentifiable) other).getUuid();
        return null != uuid && null != otherUuid && uuid.equals(otherUuid);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "uuid='" + uuid + '\'' +
                ", deleted=" + deleted +
                ", voidReason='" + voidReason + '\'' +
                ", voidDate=" + voidDate +
                ", insertDate=" + insertDate +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}
