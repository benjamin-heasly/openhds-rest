package org.openhds.resource.controller;

import org.openhds.domain.model.ProjectCode;
import org.openhds.resource.contract.UuidIdentifiableRestController;
import org.openhds.resource.registration.ProjectCodeRegistration;
import org.openhds.service.impl.ProjectCodeService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by bsh on 7/13/15.
 */
public class ProjectCodeResource extends UuidIdentifiableRestController<
        ProjectCode,
        ProjectCodeRegistration,
        ProjectCodeService> {

    private final ProjectCodeService projectCodeService;

    @Autowired
    public ProjectCodeResource(ProjectCodeService service) {
        super(service);
        this.projectCodeService = service;
    }

    @Override
    protected ProjectCode register(ProjectCodeRegistration registration) {
        return projectCodeService.createOrUpdate(registration.getProjectCode());
    }

    @Override
    protected ProjectCode register(ProjectCodeRegistration registration, String id) {
        registration.getProjectCode().setUuid(id);
        return register(registration);
    }
}
