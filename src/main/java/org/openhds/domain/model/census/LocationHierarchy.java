package org.openhds.domain.model.census;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openhds.domain.contract.AuditableExtIdEntity;
import org.openhds.domain.util.Description;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Description(description = "A node in treelike representation of the study area geography.")
@Entity
@Table(name = "locationhierarchy", indexes={
        @Index(columnList = "lastModifiedDate"),
        @Index(columnList = "extId")})
public class LocationHierarchy extends AuditableExtIdEntity implements Serializable {

    private static final long serialVersionUID = -5334850119671675888L;

    @NotNull(message = "LocationHierarchy cannot have a null name.")
    @Description(description = "The name of this location hierarchy record.")
    private String name;

    @Description(description = "Parent location's name.")
    @ManyToOne
    private LocationHierarchy parent;

    @JsonIgnore
    @Description(description = "The set of all location hierarchies of which this is the parent.")
    @OneToMany(mappedBy = "parent")
    private Set<LocationHierarchy> children = new HashSet<>();

    @NotNull(message = "LocationHierarchy cannot have a null level.")
    @Description(description = "Level of the location hierarchy.")
    @ManyToOne
    private LocationHierarchyLevel level;

    @Override
    public String getExtId() {
        return extId;
    }

    @Override
    public void setExtId(String extId) {
        this.extId = extId;
    }

    public LocationHierarchy getParent() {
        return parent;
    }

    public void setParent(LocationHierarchy parent) {
        this.parent = parent;
    }

    public Set<LocationHierarchy> getChildren() {
        return children;
    }

    public void setChildren(Set<LocationHierarchy> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationHierarchyLevel getLevel() {
        return level;
    }

    public void setLevel(LocationHierarchyLevel level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "LocationHierarchy{" +
                ", name='" + name + '\'' +
                ", level=" + level +
                "} " + super.toString();
    }
}
