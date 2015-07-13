package org.openhds.domain.model;

import org.openhds.domain.contract.AuditableExtIdEntity;
import org.openhds.domain.util.Description;

import javax.persistence.*;
import java.io.Serializable;
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

//    @Description(description = "One individual who is head of the social group.")
//    @ManyToOne(cascade = {CascadeType.ALL})
//    private Individual groupHead;

    @Description(description = "Type of the social group.")
    private String groupType;

//    @Description(description = "The set of all memberships for this social group.")
//    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "socialGroup")
//    private Set<Membership> memberships;
}
