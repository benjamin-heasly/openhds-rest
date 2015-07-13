package org.openhds.resource.registration;

import org.openhds.domain.model.SocialGroup;
import org.openhds.domain.util.Description;

/**
 * Created by Ben on 6/3/15.
 * <p>
 * Register or update a project-specific data code.
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
