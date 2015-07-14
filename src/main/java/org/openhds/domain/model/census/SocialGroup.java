package org.openhds.domain.model.census;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openhds.domain.contract.AuditableExtIdEntity;
import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.census.Membership;
import org.openhds.domain.util.Description;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bsh on 7/13/15.
 */
@Description(description = "A distinct group of people like a family or other organization.")
@Entity
@Table(name = "socialgroup")
public class SocialGroup extends AuditableExtIdEntity implements Serializable {

    public final static long serialVersionUID = -5592935530217622317L;

    @Description(description = "Name of the social group.")
    private String groupName;

    @Description(description = "One individual who is head of the social group.")
    @ManyToOne(cascade = {CascadeType.ALL})
    private Individual groupHead;

    @Description(description = "Type of the social group.")
    private String groupType;

    @JsonIgnore
    @Description(description = "The set of all memberships that include this SocialGroup.")
    @OneToMany(mappedBy = "socialGroup", cascade = { CascadeType.ALL })
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
}
