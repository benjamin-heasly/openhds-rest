package org.openhds.domain.model.census;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openhds.domain.contract.AuditableExtIdEntity;
import org.openhds.domain.util.Description;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bsh on 7/13/15.
 */
@Description(description = "A distinct group of people like a family or other organization.")
@Entity
@Table(name = "socialGroup")
public class SocialGroup extends AuditableExtIdEntity implements Serializable {

    public final static long serialVersionUID = -5592935530217622317L;

    @NotNull(message = "SocialGroup cannot have a null groupName.")
    @Description(description = "Name of the social group.")
    private String groupName;

    @NotNull(message = "SocialGroup cannot have a null groupType.")
    @Description(description = "Type of the social group.")
    private String groupType;

    @JsonIgnore
    @Description(description = "The set of all memberships that include this SocialGroup.")
    @OneToMany(mappedBy = "socialGroup")
    private Set<Membership> memberships = new HashSet<>();

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public Set<Membership> getMemberships() {
        return memberships;
    }

    public void setMemberships(Set<Membership> memberships) {
        this.memberships = memberships;
    }

    @Override
    public String toString() {
        return "SocialGroup{" +
                "groupName='" + groupName + '\'' +
                ", groupType='" + groupType + '\'' +
                "} " + super.toString();
    }
}
