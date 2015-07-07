package org.openhds.domain.contract;

import org.openhds.domain.util.Description;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by wolfe on 6/16/15.
 */
@MappedSuperclass
public abstract class AuditableExtIdEntity extends AuditableCollectedEntity implements ExtIdIdentifiable, Serializable {

    private static final long serialVersionUID = 3558923575991767767L;

    @NotNull(message = "entity extId may not be null")
    @Size(min = 1)
    @Description(description = "User-facing identifier for this entity.")
    protected String extId;

    @Override
    public String getExtId() {
        return this.extId;
    }

    @Override
    public void setExtId(String extId) {
        this.extId = extId;
    }
}
