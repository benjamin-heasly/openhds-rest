package org.openhds.domain.model.census;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.util.Description;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/13/15.
 */
@Description(description = "A record of an Individual belonging to a SocialGroup for some time interval.")
@Entity
@Table(name = "membership")
public class Membership extends AuditableCollectedEntity {

    @ManyToOne
    @Description(description="Individual the membership is associated with.")
    private Individual individual;

    @ManyToOne
    @Description(description="SocialGroup the membership is associated with.")
    private SocialGroup socialGroup;

    @NotNull
    @Size(min = 1)
    @Description(description="Relationship from the individual to the SocialGroup head.")
    String relationshipToGroupHead;

    @NotNull
    @Description(description="Start date of the membership.")
    ZonedDateTime startDate;

    @NotNull
    @Size(min = 1)
    @Description(description="Start type of the membership.")
    String startType;

    @Description(description="End date of the membership.")
    ZonedDateTime endDate;

    @Description(description="End type of the membership.")
    String endType;

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

    public String getRelationshipToGroupHead() {
        return relationshipToGroupHead;
    }

    public void setRelationshipToGroupHead(String bIsToA) {
        this.relationshipToGroupHead = bIsToA;
    }
}
