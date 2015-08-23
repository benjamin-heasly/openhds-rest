package org.openhds.domain.model.update;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.model.census.Individual;
import org.openhds.domain.util.Description;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Created by Wolfe on 7/14/2015.
 */
@Description(description = "A death registered for an individual.")
@Entity
@Table(name = "death")
public class Death extends AuditableCollectedEntity implements Serializable {

    private static final long serialVersionUID = 7893269184667369629L;

    @Description(description = "Place where the death occurred.")
    private String deathPlace;

    @Description(description = "Cause of the death.")
    private String deathCause;

    @NotNull(message = "Death cannot have a null deathDate.")
    @Description(description = "Date of the Death.")
    private ZonedDateTime deathDate;

    @Description(description = "Age of death in number of data.")
    private int ageAtDeath;

    @NotNull(message = "Death cannot have a null visit.")
    @ManyToOne
    @Description(description = "Visit associated with the death, identified by external id.")
    private Visit visit;

    @NotNull(message = "Death cannot have a null individual.")
    @ManyToOne
    @Description(description = "Individual who has died.")
    private Individual individual;

    public String getDeathPlace() {
        return deathPlace;
    }

    public void setDeathPlace(String deathPlace) {
        this.deathPlace = deathPlace;
    }

    public String getDeathCause() {
        return deathCause;
    }

    public void setDeathCause(String deathCause) {
        this.deathCause = deathCause;
    }

    public ZonedDateTime getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(ZonedDateTime deathDate) {
        this.deathDate = deathDate;
    }

    public int getAgeAtDeath() {
        return ageAtDeath;
    }

    public void setAgeAtDeath(int ageAtDeath) {
        this.ageAtDeath = ageAtDeath;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    @Override
    public String toString() {
        return "Death{" +
                "individual=" + individual +
                ", deathPlace='" + deathPlace + '\'' +
                ", deathCause='" + deathCause + '\'' +
                ", deathDate=" + deathDate +
                ", visit=" + visit +
                ", ageAtDeath=" + ageAtDeath +
                "} " + super.toString();
    }
}
