package org.openhds.resource.controller;

import org.openhds.domain.model.ProjectCode;
import org.openhds.resource.contract.UuidIdentifiableRestControllerTest;
import org.openhds.resource.registration.ProjectCodeRegistration;
import org.openhds.resource.registration.Registration;
import org.openhds.service.impl.ProjectCodeService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by bsh on 7/13/15.
 */
public class ProjectCodeRestControllerTest extends UuidIdentifiableRestControllerTest<
        ProjectCode,
        ProjectCodeService,
        ProjectCodeResource> {

    @Autowired
    @Override
    protected void initialize(ProjectCodeService service, ProjectCodeResource controller) {
        this.service = service;
        this.controller = controller;
    }

    @Override
    protected ProjectCode makeValidEntity(String name, String id) {
        ProjectCode projectCode = new ProjectCode();
        projectCode.setUuid(id);
        projectCode.setCodeName(name);
        projectCode.setCodeValue(name);
        projectCode.setCodeGroup(name);
        return projectCode;
    }

    @Override
    protected ProjectCode makeInvalidEntity() {
        return new ProjectCode();
    }

    @Override
    protected void verifyEntityExistsWithNameAndId(ProjectCode entity, String name, String id) {
        assertNotNull(entity);

        ProjectCode savedProjectCode = service.findOne(id);
        assertNotNull(savedProjectCode);

        assertEquals(id, savedProjectCode.getUuid());
        assertEquals(id, entity.getUuid());
        assertEquals(entity.getCodeName(), savedProjectCode.getCodeName());

    }

    @Override
    protected Registration<ProjectCode> makeRegistration(ProjectCode entity) {
        ProjectCodeRegistration projectCodeRegistration = new ProjectCodeRegistration();
        projectCodeRegistration.setProjectCode(entity);
        return projectCodeRegistration;
    }
}
