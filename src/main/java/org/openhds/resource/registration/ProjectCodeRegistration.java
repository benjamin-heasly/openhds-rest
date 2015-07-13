package org.openhds.resource.registration;

import org.openhds.domain.model.ProjectCode;
import org.openhds.domain.util.Description;

/**
 * Created by Ben on 6/3/15.
 * <p>
 * Register or update a project-specific data code.
 */
@Description(description = "Register or update a data code.")
public class ProjectCodeRegistration extends Registration<ProjectCode> {

    private ProjectCode projectCode;

    public ProjectCode getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(ProjectCode projectCode) {
        this.projectCode = projectCode;
    }
}
