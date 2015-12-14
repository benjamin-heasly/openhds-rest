package org.openhds.resource.registration.census;

import org.openhds.domain.model.census.Individual;
import org.openhds.domain.util.Description;
import org.openhds.resource.registration.Registration;

@Description(description = "Register an Individual with enough information to place them in a household.")
public class IndividualHouseholdRegistration extends Registration<Individual> {

    private Individual individual;
    private String relationToHead;
    private String headOfHouseholdId;
    private String relationshipId;
    private String locationId;
    private String socialGroupId;
    private String motherId;
    private String fatherId;
    private String membershipId;
    private String residencyId;

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    public String getRelationToHead() {
        return relationToHead;
    }

    public void setRelationToHead(String relationToHead) {
        this.relationToHead = relationToHead;
    }

    public String getHeadOfHouseholdId() {
        return headOfHouseholdId;
    }

    public void setHeadOfHouseholdId(String headOfHouseholdId) {
        this.headOfHouseholdId = headOfHouseholdId;
    }

    public String getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getSocialGroupId() {
        return socialGroupId;
    }

    public void setSocialGroupId(String socialGroupId) {
        this.socialGroupId = socialGroupId;
    }

    public String getMotherId() {
        return motherId;
    }

    public void setMotherId(String motherId) {
        this.motherId = motherId;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public String getResidencyId() {
        return residencyId;
    }

    public void setResidencyId(String residencyId) {
        this.residencyId = residencyId;
    }
}
