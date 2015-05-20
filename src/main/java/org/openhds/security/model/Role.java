package org.openhds.security.model;

import org.openhds.domain.util.Description;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dave Roberge
 */
@Description(description = "A Role contains a group of Grant." +
        "Roles are assigned to Users who may then act in OpenHDS with all of the associated Grant")
@Entity
@Table(name = "role")
public class Role implements Serializable {

    static final long serialVersionUID = 21L;

    @Description(description = "Name of the role.")
    @Id
    String name;

    @Description(description = "Description of the role.")
    String description;

    @Description(description = "Set of privileges which define the rights that actors have.")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="role_privileges", joinColumns = {@JoinColumn(name="role_uuid")}, inverseJoinColumns = @JoinColumn(name="privilege_uuid"))
    Set<Privilege> privileges = new HashSet<Privilege>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Set<Privilege> privileges) {
        this.privileges = privileges;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Role other = (Role) obj;
        if ((this.name == null) ? (other.name!= null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }


    @Override
    public String toString() {
        return name;
    }
}
