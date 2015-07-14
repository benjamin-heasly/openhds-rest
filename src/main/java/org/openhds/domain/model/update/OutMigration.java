package org.openhds.domain.model.update;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.census.Residency;
import org.openhds.domain.util.Description;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/14/15.
 */
@Description(description = "An OutMigration represents a migration out of the study area.")
@Entity
@Table(name = "outmigration")
public class OutMigration extends AuditableCollectedEntity implements Serializable {

    public final static long serialVersionUID = 6736599408170070468L;

    @NotNull
    @ManyToOne
    @Description(description = "The Individual who is out-migrating.")
    private Individual individual;

    @OneToOne
    @NotNull
    @Description(description = "The residency the Individual is leaving.")
    private Residency residency;

    @Description(description = "Name of where the Individual is going.")
    private String destination;

    @Description(description = "Reason for out-migrating.")
    private String reason;

    @NotNull
    @ManyToOne
    @Description(description = "The visit when and where the out-migration was recorded.")
    private Visit visit;

    @NotNull
    @Description(description = "Date of the out-migration.")
    private ZonedDateTime migrationDate;

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    public Residency getResidency() {
        return residency;
    }

    public void setResidency(Residency residency) {
        this.residency = residency;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public ZonedDateTime getMigrationDate() {
        return migrationDate;
    }

    public void setMigrationDate(ZonedDateTime migrationDate) {
        this.migrationDate = migrationDate;
    }
}
