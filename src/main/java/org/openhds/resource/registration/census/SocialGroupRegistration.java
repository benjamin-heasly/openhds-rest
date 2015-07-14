package org.openhds.resource.registration.census;

import org.openhds.domain.model.census.SocialGroup;
import org.openhds.domain.util.Description;
import org.openhds.resource.registration.Registration;

/**
 * Created by Ben on 6/3/15.
 * <p>
 * Register or update a Social Group.
 */
@Description(description = "Register or update a SocialGroup.")
public class SocialGroupRegistration extends Registration<SocialGroup> {

    private SocialGroup socialGroup;

    public SocialGroup getSocialGroup() {
        return socialGroup;
    }

    public void setSocialGroup(SocialGroup socialGroup) {
        this.socialGroup = socialGroup;
    }
}
