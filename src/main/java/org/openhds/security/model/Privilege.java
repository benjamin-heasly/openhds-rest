package org.openhds.security.model;

import org.hibernate.annotations.GenericGenerator;
import org.openhds.Description;
import org.openhds.domain.model.UuidIdentifiable;

import javax.persistence.*;
import java.io.Serializable;


/**
 * @author Dave Roberge
 */
@Description(description = "A Privilege represents a rights required to access service methods.")
@Entity
@Table(name = "privilege")
public class Privilege implements Serializable, UuidIdentifiable {

    private static final long serialVersionUID = -5969044695942713833L;

    public Privilege() {
    }

    public Privilege(String privilege) {
        this.privilege = privilege;
    }

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 32)
    private String uuid;

    @Description(description = "Name of the privilege.")
    private String privilege;

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Privilege)) {
            return false;
        }

        return privilege.equals(((Privilege) obj).privilege);
    }

    @Override
    public int hashCode() {
        if (privilege == null) {
            return super.hashCode();
        }

        return privilege.hashCode();
    }

    @Override
    public String toString() {
        return privilege;
    }
}
