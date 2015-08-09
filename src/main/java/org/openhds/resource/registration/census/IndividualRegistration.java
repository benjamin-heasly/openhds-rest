package org.openhds.resource.registration.census;

import org.openhds.domain.model.census.Individual;
import org.openhds.domain.util.Description;
import org.openhds.resource.registration.Registration;

/**
 * Created by Wolfe on 7/13/2015.
 */

@Description(description = "Register an Individual at a Location in the location hierarchy.")
public class IndividualRegistration extends Registration<Individual> {

    private Individual individual;

    private String socialGroupUuid;

    private String locationUuid;

    private String residencyUuid;

    private String membershipUuid;

    private String relationshipUuid;

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    public String getSocialGroupUuid() {
        return socialGroupUuid;
    }

    public void setSocialGroupUuid(String socialGroupUuid) {
        this.socialGroupUuid = socialGroupUuid;
    }

    public String getLocationUuid() {
        return locationUuid;
    }

    public void setLocationUuid(String locationUuid) {
        this.locationUuid = locationUuid;
    }

    public String getResidencyUuid() {
        return residencyUuid;
    }

    public void setResidencyUuid(String residencyUuid) {
        this.residencyUuid = residencyUuid;
    }

    public String getMembershipUuid() {
        return membershipUuid;
    }

    public void setMembershipUuid(String membershipUuid) {
        this.membershipUuid = membershipUuid;
    }

    public String getRelationshipUuid() {
        return relationshipUuid;
    }

    public void setRelationshipUuid(String relationshipUuid) {
        this.relationshipUuid = relationshipUuid;
    }

}
