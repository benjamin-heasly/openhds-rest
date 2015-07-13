package org.openhds.resource.registration;

import org.openhds.domain.model.Membership;
import org.openhds.domain.util.Description;

/**
 * Created by Ben on 6/3/15.
 * <p>
 * Register or update a project-specific data code.
 */
@Description(description = "Register or update a Membership.")
public class MembershipRegistration extends Registration<Membership> {

    private Membership membership;

    private String individualUuid;

    private String socialGroupUuid;

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    public String getIndividualUuid() {
        return individualUuid;
    }

    public void setIndividualUuid(String individualUuid) {
        this.individualUuid = individualUuid;
    }

    public String getSocialGroupUuid() {
        return socialGroupUuid;
    }

    public void setSocialGroupUuid(String socialGroupUuid) {
        this.socialGroupUuid = socialGroupUuid;
    }
}
