package org.openhds.resource.registration.update;

import org.openhds.domain.model.update.InMigration;
import org.openhds.domain.util.Description;
import org.openhds.resource.registration.Registration;

/**
 * Created by Ben on 6/3/15.
 * <p>
 * Register or update an in-migration.
 */
@Description(description = "Register or update an in-migration.")
public class InMigrationRegistration extends Registration<InMigration> {

    private InMigration inMigration;

    private String visitUuid;

    private String individualUuid;

    private String residencyUuid;

    public InMigration getInMigration() {
        return inMigration;
    }

    public void setInMigration(InMigration inMigration) {
        this.inMigration = inMigration;
    }

    public String getVisitUuid() {
        return visitUuid;
    }

    public void setVisitUuid(String visitUuid) {
        this.visitUuid = visitUuid;
    }

    public String getIndividualUuid() {
        return individualUuid;
    }

    public void setIndividualUuid(String individualUuid) {
        this.individualUuid = individualUuid;
    }

    public String getResidencyUuid() {
        return residencyUuid;
    }

    public void setResidencyUuid(String residencyUuid) {
        this.residencyUuid = residencyUuid;
    }
}
