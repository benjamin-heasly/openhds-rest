package org.openhds.resource.registration.census;

import org.openhds.domain.model.census.Individual;
import org.openhds.domain.util.Description;
import org.openhds.resource.registration.Registration;

@Description(description = "Register an Individual with enough information to place them in a household.")
public class IndividualHouseholdRegistration extends Registration<Individual> {

    private Individual individual;
    private String relationToHead;
    private String headOfHouseholdUuid;
    private String relationshipUuid;
    private String locationUuid;
    private String socialGroupUuid;
    private String motherUuid;
    private String fatherUuid;
    private String membershipUuid;
    private String residencyUuid;

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

    public String getHeadOfHouseholdUuid() {
        return headOfHouseholdUuid;
    }

    public void setHeadOfHouseholdUuid(String headOfHouseholdUuid) {
        this.headOfHouseholdUuid = headOfHouseholdUuid;
    }

    public String getRelationshipUuid() {
        return relationshipUuid;
    }

    public void setRelationshipUuid(String relationshipUuid) {
        this.relationshipUuid = relationshipUuid;
    }

    public String getLocationUuid() {
        return locationUuid;
    }

    public void setLocationUuid(String locationUuid) {
        this.locationUuid = locationUuid;
    }

    public String getSocialGroupUuid() {
        return socialGroupUuid;
    }

    public void setSocialGroupUuid(String socialGroupUuid) {
        this.socialGroupUuid = socialGroupUuid;
    }

    public String getMotherUuid() {
        return motherUuid;
    }

    public void setMotherUuid(String motherUuid) {
        this.motherUuid = motherUuid;
    }

    public String getFatherUuid() {
        return fatherUuid;
    }

    public void setFatherUuid(String fatherUuid) {
        this.fatherUuid = fatherUuid;
    }

    public String getMembershipUuid() {
        return membershipUuid;
    }

    public void setMembershipUuid(String membershipUuid) {
        this.membershipUuid = membershipUuid;
    }

    public String getResidencyUuid() {
        return residencyUuid;
    }

    public void setResidencyUuid(String residencyUuid) {
        this.residencyUuid = residencyUuid;
    }
}
