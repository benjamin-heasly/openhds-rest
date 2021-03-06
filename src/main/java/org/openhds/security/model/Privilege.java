package org.openhds.security.model;

import org.openhds.domain.util.Description;

import javax.persistence.*;
import java.io.Serializable;


/**
 * @author Dave Roberge
 */
@Description(description = "A Privilege represents a right granted to access particular service methods.")
@Entity
@Table(name = "privilege")
public class Privilege implements Serializable {

    private static final long serialVersionUID = -5969044695942713833L;

    public enum Grant {
        ROLE_USER,
        ROLE_CREATE_ENTITY,
        ROLE_VIEW_ENTITY,
        ROLE_EDIT_ENTITY,
        ROLE_DELETE_ENTITY,
        ROLE_CREATE_USER,
        ROLE_DELETE_USER
    }

    public Privilege() {
    }

    public Privilege(Grant grant) {
        setGrant(grant);
    }

    @Description(description = "The access right granted by this Privilege.")
    @Id
    @Enumerated(EnumType.STRING)
    private Grant grant;

    public Grant getGrant() {
        return grant;
    }

    public void setGrant(Grant grant) {
        this.grant = grant;
    }

    @Override
    public int hashCode() {
        if (null == grant) {
            return 0;
        }
        return grant.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (getClass() != other.getClass()) {
            return false;
        }

        final Grant otherGrant = ((Privilege) other).getGrant();
        return null != grant && null != otherGrant && grant.equals(otherGrant);
    }

    @Override
    public String toString() {
        return grant.toString();
    }
}
