package org.openhds.domain.model.census;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.util.Description;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/13/15.
 */
@Description(description = "A record of an Individual belonging to a SocialGroup for some time interval.")
@Entity
@Table(name = "membership", indexes={@Index(columnList = "lastModifiedDate")})
public class Membership extends AuditableCollectedEntity implements Serializable {

    private static final long serialVersionUID = 3668816399895850928L;

    @NotNull(message = "Membership cannot have a null startDate.")
    @Description(description = "Start date of the membership.")
    ZonedDateTime startDate;

    @NotNull(message = "Membership cannot have a null startType.")
    @Size(min = 1)
    @Description(description = "Start type of the membership.")
    String startType;

    @Description(description = "End date of the membership.")
    ZonedDateTime endDate;

    @Description(description = "End type of the membership.")
    String endType;

    @NotNull(message = "Membership cannot have a null individual.")
    @ManyToOne
    @Description(description = "Individual the membership is associated with.")
    private Individual individual;

    @NotNull(message = "membership's socialgroup must not be null.")
    @ManyToOne
    @Description(description = "SocialGroup the membership is associated with.")
    private SocialGroup socialGroup;

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    public SocialGroup getSocialGroup() {
        return socialGroup;
    }

    public void setSocialGroup(SocialGroup socialGroup) {
        this.socialGroup = socialGroup;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public String getStartType() {
        return startType;
    }

    public void setStartType(String startType) {
        this.startType = startType;
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
    public String toString() {
        return "Membership{" +
                ", startDate=" + startDate +
                ", startType='" + startType + '\'' +
                ", endDate=" + endDate +
                ", endType='" + endType + '\'' +
                ", individual=" + individual +
                ", socialGroup=" + socialGroup +
                "} " + super.toString();
    }
}
