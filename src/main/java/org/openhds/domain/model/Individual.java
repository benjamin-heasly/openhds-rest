package org.openhds.domain.model;

import org.openhds.domain.contract.AuditableExtIdEntity;
import org.openhds.domain.util.Description;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.ZonedDateTime;


/**
 * Created by Wolfe on 7/13/2015.
 */
@Description(description = "A distinct individual within the study area.")
@Entity
@Table(name = "individual")
public class Individual extends AuditableExtIdEntity {

    @NotNull(message = "Individual must have a firstname.")
    @Description(description = "First name of the individual.")
    private String firstName;

    @Description(description = "Middle name of the individual.")
    private String middleName;

    @Description(description = "Last name of the individual.")
    private String lastName;

    @Description(description = "The gender of the individual.")
    private String gender;

    @Description(description = "Birth date of the individual.")
    private ZonedDateTime dateOfBirth;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = org.openhds.domain.model.Individual.class)
    @Description(description = "The individual's mother.")
    private Individual mother;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = org.openhds.domain.model.Individual.class)
    @Description(description = "The individual's father.")
    private Individual father;


//    @OneToMany(mappedBy = "individual", cascade = { CascadeType.ALL })
//    @OrderBy("startDate")
//    @Description(description = "The set of all residencies that the individual may have.")
//    private Set<Residency> allResidencies = new HashSet<Residency>();
//    @OneToMany(mappedBy = "individualA", cascade = { CascadeType.ALL })
//    @Description(description = "The set of all relationships that the individual may have with another individual.")
//    private Set<Relationship> allRelationships1 = new HashSet<Relationship>();
//    @OneToMany(mappedBy = "individualB", cascade = { CascadeType.ALL })
//    @Description(description = "The set of all relationships where another individual may have with this individual.")
//    private Set<Relationship> allRelationships2 = new HashSet<Relationship>();
//    @OneToMany(mappedBy = "individual", cascade = { CascadeType.ALL })
//    @Description(description = "The set of all memberships the individual is participating in.")
//    private Set<Membership> allMemberships = new HashSet<Membership>();


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ZonedDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(ZonedDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Individual getMother() {
        return mother;
    }

    public void setMother(Individual mother) {
        this.mother = mother;
    }

    public Individual getFather() {
        return father;
    }

    public void setFather(Individual father) {
        this.father = father;
    }
}
