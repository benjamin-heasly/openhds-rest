package org.openhds.service.impl;

import org.openhds.domain.model.ProjectCode;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.ProjectCodeRepository;
import org.openhds.service.contract.AbstractUuidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by bsh on 7/1/2015.
 */
@Component
public class ProjectCodeService extends AbstractUuidService<ProjectCode, ProjectCodeRepository> {

    @Autowired
    public ProjectCodeService(ProjectCodeRepository projectCodeRepository) {
        super(projectCodeRepository);
    }

    @Override
    protected ProjectCode makeUnknownEntity() {
        ProjectCode projectCode = new ProjectCode();
        projectCode.setCodeName("unknown");
        projectCode.setCodeValue("unknown");
        return projectCode;
    }

    @Override
    public void validate(ProjectCode projectCode, ErrorLog errorLog) {
        super.validate(projectCode, errorLog);
    }

    public ProjectCode findByCodeName(String codeName) {
        return repository.findByCodeName(codeName).get();
    }

}
