package org.openhds.domain.model.census;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openhds.domain.contract.AuditableExtIdEntity;
import org.openhds.domain.util.Description;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by Wolfe on 7/13/2015.
 */
@Description(description = "A distinct individual within the study area.")
@Entity
@Table(name = "individual")
public class Individual extends AuditableExtIdEntity implements Serializable {

    private static final long serialVersionUID = 5226650143604788124L;

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


    @ManyToOne
    @Description(description = "The individual's mother.")
    private Individual mother;

    @ManyToOne
    @Description(description = "The individual's father.")
    private Individual father;

    @JsonIgnore
    @OneToMany(mappedBy = "individual")
    @Description(description = "The set of all residencies that the individual is a part of.")
    private Set<Residency> residencies = new HashSet<Residency>();

    @JsonIgnore
    @OneToMany(mappedBy = "individual")
    @Description(description = "The set of all memberships the individual is a part of.")
    private Set<Membership> allMemberships = new HashSet<Membership>();

    @JsonIgnore
    @OneToMany(mappedBy = "individualA")
    @Description(description = "The set of all relationships that the individual may have with another individual.")
    private Set<Relationship> relationshipsAsIndividualA = new HashSet<Relationship>();

    @JsonIgnore
    @OneToMany(mappedBy = "individualB")
    @Description(description = "The set of all relationships another individual may have with this individual.")
    private Set<Relationship> relationshipsAsIndividualB = new HashSet<Relationship>();

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

    public Set<Residency> getResidencies() {
        return residencies;
    }

    public void setResidencies(Set<Residency> residencies) {
        this.residencies = residencies;
    }

    public Set<Membership> getAllMemberships() {
        return allMemberships;
    }

    public void setAllMemberships(Set<Membership> allMemberships) {
        this.allMemberships = allMemberships;
    }

    public Set<Relationship> getRelationshipsAsIndividualA() {
        return relationshipsAsIndividualA;
    }

    public void setRelationshipsAsIndividualA(Set<Relationship> relationshipsAsIndividualA) {
        this.relationshipsAsIndividualA = relationshipsAsIndividualA;
    }

    public Set<Relationship> getRelationshipsAsIndividualB() {
        return relationshipsAsIndividualB;
    }

    public void setRelationshipsAsIndividualB(Set<Relationship> relationshipsAsIndividualB) {
        this.relationshipsAsIndividualB = relationshipsAsIndividualB;
    }

        @Override
    public String toString() {
        return "Individual{" +
                "firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                "} " + super.toString();
    }
}
