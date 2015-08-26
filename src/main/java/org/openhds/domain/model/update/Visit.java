package org.openhds.domain.model.update;

import org.openhds.domain.contract.AuditableExtIdEntity;
import org.openhds.domain.model.census.Location;
import org.openhds.domain.util.Description;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/14/15.
 */
@Description(description = "A Visit represents a FieldWorker's observation of a specific Location within the study area at a particular date.")
@Entity
@Table(name = "visit", indexes={
        @Index(columnList = "lastModifiedDate"),
        @Index(columnList = "extId")})
public class Visit  extends AuditableExtIdEntity implements Serializable {

    public final static long serialVersionUID = -211408757055967973L;

    @Description(description = "Location where the Visit took place.")
    @NotNull(message = "Visit must have a Location.")
    @ManyToOne
    private Location location;

    @Description(description = "Date when teh visit took place.")
    @NotNull(message = "Visit must have a visit date")
    private ZonedDateTime visitDate;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ZonedDateTime getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(ZonedDateTime visitDate) {
        this.visitDate = visitDate;
    }

    @Override
    public String toString() {
        return "Visit{" +
                "location=" + location +
                ", visitDate=" + visitDate +
                "} " + super.toString();
    }
}