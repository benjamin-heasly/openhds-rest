package org.openhds.events.model;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.domain.util.Description;

import javax.persistence.Entity;
import javax.persistence.Table;

@Description(description = "Metadata regarding an atomic event in OpenHDS")
@Entity
@Table(name = "eventmetadata")
public class EventMetadata extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    private String status;

    private String system;

    private String result;

    private int numTimesRead;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getNumTimesRead() {
        return numTimesRead;
    }

    public void setNumTimesRead(int numTimesRead) {
        this.numTimesRead = numTimesRead;
    }
}
