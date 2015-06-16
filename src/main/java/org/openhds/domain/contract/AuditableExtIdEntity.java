package org.openhds.domain.contract;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by wolfe on 6/16/15.
 */
public class AuditableExtIdEntity extends AuditableCollectedEntity implements ExtIdIdentifiable, Serializable {

    private static final long serialVersionUID = 3558923575991767767L;

    @NotNull
    private String extId;

    @Override
    public String getExtId() {
        return this.extId;
    }

    @Override
    public void setExtId(String extId) {
        this.extId = extId;
    }
}
