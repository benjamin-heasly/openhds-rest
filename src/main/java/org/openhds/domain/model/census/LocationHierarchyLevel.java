package org.openhds.domain.model.census;

import org.openhds.domain.contract.AuditableEntity;
import org.openhds.domain.util.Description;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Description(description = "A LocationHierarchyLevel represents one tier in the representation of the study area geography.")
@Entity
@Table(name = "locationhierarchylevel")
public class LocationHierarchyLevel extends AuditableEntity implements Serializable {

    private static final long serialVersionUID = -1070569257732332545L;

    @Column(unique = true)
    @Description(description = "A key to identify this level.")
    int keyIdentifier;

    @NotNull(message = "LocationHierarchyLevel cannot have a null name.")
    @Description(description = "The name of this location hierarchy level.")
    String name;

    public int getKeyIdentifier() {
        return keyIdentifier;
    }

    public void setKeyIdentifier(int keyIdentifier) {
        this.keyIdentifier = keyIdentifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "LocationHierarchyLevel{" +
                "keyIdentifier=" + keyIdentifier +
                ", name='" + name + '\'' +
                "} " + super.toString();
    }
}
