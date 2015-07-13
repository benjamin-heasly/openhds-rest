package org.openhds.service.impl;

import org.openhds.domain.model.ProjectCode;
import org.openhds.service.UuidServiceTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by bsh on 7/13/15.
 */
public class ProjectCodeServiceTest extends UuidServiceTest<ProjectCode, ProjectCodeService> {

    @Autowired
    @Override
    protected void initialize(ProjectCodeService service) {
        this.service = service;
    }

    @Override
    protected ProjectCode makeInvalidEntity() {
        return new ProjectCode();
    }

    @Override
    protected ProjectCode makeValidEntity(String name, String id) {
        ProjectCode projectCode = new ProjectCode();
        projectCode.setUuid(id);
        projectCode.setCodeName(name);
        projectCode.setCodeValue(name);
        return projectCode;
    }
}
