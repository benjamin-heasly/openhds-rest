package org.openhds.domain.model;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.util.Description;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by bsh on 7/13/15.
 */
@Description(description = "A record of an Individual living at a Location for some time interval.")
@Entity
@Table(name = "residency")
public class Residency extends AuditableCollectedEntity implements Serializable{

    private static final long serialVersionUID = -8660806978131352923L;

    @ManyToOne
    @JoinColumn(name = "individual")
    @Description(description = "Individual who resides at the Location.")
    private Individual individual;


    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Residency)) {
            return false;
        }

        final String otherUuid = ((Residency) other).getUuid();
        return null != uuid && null != otherUuid && uuid.equals(otherUuid);
    }

    @ManyToOne
    @JoinColumn(name = "location")
    @Description(description = "Location where the Individual resides.")
    private Location location;


}
