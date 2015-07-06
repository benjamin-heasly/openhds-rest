package org.openhds.errors.model;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.util.Description;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Description(description = "A log entry containing multiple errors.")
@Entity
@Table(name = "errorlog")
public class ErrorLog extends AuditableCollectedEntity implements Serializable {

    private static final long serialVersionUID = 2447734552586256198L;

    @Column(length=65535)
    private String dataPayload;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = Error.class)
    @JoinColumn(name = "error_uuid")
    private List<Error> errors = new ArrayList<>();

    private String assignedTo;

    @Column
    private String entityType;

    private String resolutionStatus;

    private ZonedDateTime dateOfResolution;

    public String getDataPayload() {
        return dataPayload;
    }

    public void setDataPayload(String dataPayload) {
        this.dataPayload = dataPayload;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getResolutionStatus() {
        return resolutionStatus;
    }

    public void setResolutionStatus(String resolutionStatus) {
        this.resolutionStatus = resolutionStatus;
    }

    public ZonedDateTime getDateOfResolution() {
        return dateOfResolution;
    }

    public void setDateOfResolution(ZonedDateTime dateOfResolution) {
        this.dateOfResolution = dateOfResolution;
    }
}
