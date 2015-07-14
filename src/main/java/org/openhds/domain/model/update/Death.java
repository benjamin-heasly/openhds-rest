package org.openhds.domain.model.update;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.model.census.Individual;
import org.openhds.domain.util.Description;

import javax.persistence.CascadeType;
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

    @NotNull(message = "Individual may not be null in a death.")
    @ManyToOne(cascade = CascadeType.MERGE)
    @Description(description = "Individual who has died.")
    private Individual individual;

    @Description(description = "Place where the death occurred.")
    private String deathPlace;

    @Description(description = "Cause of the death.")
    private String deathCause;

    @NotNull(message = "You must provide a Death date")
    @Description(description = "Date of the Death.")
    private ZonedDateTime deathDate;

    @ManyToOne(cascade = CascadeType.MERGE)
    @Description(description = "Visit associated with the death, identified by external id.")
    private Visit visit;

    @Description(description = "Age of death in number of data.")
    private int ageAtDeath;

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

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

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public int getAgeAtDeath() {
        return ageAtDeath;
    }

    public void setAgeAtDeath(int ageAtDeath) {
        this.ageAtDeath = ageAtDeath;
    }
}
