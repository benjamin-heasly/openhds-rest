package org.openhds.domain.model;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.util.Description;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/13/15.
 */
@Description(description = "A record of two Individuals having some relationship for some time interval.")
@Entity
@Table(name = "relationship")
public class Relationship extends AuditableCollectedEntity implements Serializable{


    private static final long serialVersionUID = -2104326927087468148L;

    @ManyToOne
    @JoinColumn(name = "individualA")
    @Description(description = "One of the individuals participating in the relationship.")
    private Individual individualA;

    @ManyToOne
    @JoinColumn(name = "individualB")
    @Description(description = "One of the individuals participating in the relationship.")
    private Individual individualB;

    @NotNull(message = "relationship type must not be null.")
    @Description(description = "The type of the relationship from individual a to individual b.")
    String relationshipType;

    @NotNull(message = "Relationship startDate must not be null.")
    @Description(description = "Start date of the relationship.")
    ZonedDateTime startDate;

    @Description(description = "End date of the relationship.")
    ZonedDateTime endDate;

    @Description(description = "End type of the relationship.")
    String endType;

    public Individual getIndividualA() {
        return individualA;
    }

    public void setIndividualA(Individual individualA) {
        this.individualA = individualA;
    }

    public Individual getIndividualB() {
        return individualB;
    }

    public void setIndividualB(Individual individualB) {
        this.individualB = individualB;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public String getEndType() {
        return endType;
    }

    public void setEndType(String endType) {
        this.endType = endType;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Relationship)) {
            return false;
        }

        final String otherUuid = ((Relationship) other).getUuid();
        return null != uuid && null != otherUuid && uuid.equals(otherUuid);
    }

    @Override
    public String toString() {
        return "Relationship{" +
                "individualA=" + individualA +
                ", individualB=" + individualB +
                ", relationshipType='" + relationshipType + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", endType='" + endType + '\'' +
                "} " + super.toString();
    }
}
