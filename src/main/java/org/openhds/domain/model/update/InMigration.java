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
@Description(description = "An InMigration represents a migration into the study area. It contains information about the Individual who is in-migrating to a particular Residency. It also contains information about the origin, date, and reason the Indiviudal is migrating as well as the Visit that is associated with the migration.")
@Entity
@Table(name = "inmigration")
public class InMigration extends AuditableCollectedEntity implements Serializable {

    public final static long serialVersionUID = 7889700709284952892L;

    @Description(description = "Name for where the individual came from.")
    private String origin;

    @Description(description = "Reason why the individual in-migrated.")
    private String reason;

    @NotNull(message = "InMigration cannot have a null migrationType.")
    @Description(description = "The type of in-migration, like internal vs external.")
    private String migrationType;

    @NotNull(message = "InMigration cannnot have a null migrationDate.")
    @Description(description = "Date of the in-migration.")
    private ZonedDateTime migrationDate;

    @NotNull(message = "InMigration cannot have a null visit.")
    @ManyToOne
    @Description(description = "The visit when and where the in-migration was recorded.")
    private Visit visit;

    @NotNull(message = "InMigration cannot have a null individual.")
    @ManyToOne
    @Description(description = "Individual who is migrating in/into the study area.")
    private Individual individual;

    @OneToOne
    @NotNull(message = "InMigration cannot have a null residency.")
    @Description(description = "The residency the individual is in-migrating to.")
    private Residency residency;

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMigrationType() {
        return migrationType;
    }

    public void setMigrationType(String migrationType) {
        this.migrationType = migrationType;
    }

    public ZonedDateTime getMigrationDate() {
        return migrationDate;
    }

    public void setMigrationDate(ZonedDateTime migrationDate) {
        this.migrationDate = migrationDate;
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

    public Residency getResidency() {
        return residency;
    }

    public void setResidency(Residency residency) {
        this.residency = residency;
    }

    @Override
    public String toString() {
        return "InMigration{" +
                "origin='" + origin + '\'' +
                ", reason='" + reason + '\'' +
                ", migrationType='" + migrationType + '\'' +
                ", migrationDate=" + migrationDate +
                ", visit=" + visit +
                ", individual=" + individual +
                ", residency=" + residency +
                "} " + super.toString();
    }
}
