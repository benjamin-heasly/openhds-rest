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
 * Created by bsh on 7/14/15.
 */
@Description(description = "A Pregnancy Observation is used to monitor a pregnancy. It contains information about the mother and the expected delivery date.")
@Entity
@Table(name = "pregnancyobservation")
public class PregnancyObservation extends AuditableCollectedEntity implements Serializable {

    public final static long serialVersionUID = -4737117368371754337L;

    @NotNull
    @Description(description = "Expected delivery date.")
    private ZonedDateTime expectedDeliveryDate;

    @NotNull
    @Description(description = "Recorded date of the start of the pregnancy.")
    private ZonedDateTime pregnancyDate;

    @NotNull
    @ManyToOne
    @Description(description = "The visit when and where this pregnancy was observed.")
    private Visit visit;

    @NotNull
    @ManyToOne
    @Description(description = "The mother who is pregnant.")
    private Individual mother;

    public ZonedDateTime getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(ZonedDateTime expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public ZonedDateTime getPregnancyDate() {
        return pregnancyDate;
    }

    public void setPregnancyDate(ZonedDateTime pregnancyDate) {
        this.pregnancyDate = pregnancyDate;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public Individual getMother() {
        return mother;
    }

    public void setMother(Individual mother) {
        this.mother = mother;
    }

    @Override
    public String toString() {
        return "PregnancyObservation{" +
                "expectedDeliveryDate=" + expectedDeliveryDate +
                ", pregnancyDate=" + pregnancyDate +
                ", visit=" + visit +
                ", mother=" + mother +
                "} " + super.toString();
    }
}
