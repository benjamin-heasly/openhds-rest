package org.openhds.service.impl;

import org.openhds.domain.model.ProjectCode;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.ProjectCodeRepository;
import org.openhds.service.contract.AbstractUuidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public ProjectCode makePlaceHolder(String id, String name) {
        ProjectCode projectCode = new ProjectCode();
        projectCode.setUuid(id);
        projectCode.setCodeName(id);
        projectCode.setCodeValue(name);
        projectCode.setCodeGroup(name);
        return projectCode;
    }

    public ProjectCode createCode(String name, String value, String group, String description) {
        ProjectCode projectCode = new ProjectCode();
        projectCode.setCodeName(name);
        projectCode.setCodeValue(value);
        projectCode.setCodeGroup(group);
        projectCode.setDescription(description);
        return createOrUpdate(projectCode);
    }

    public ProjectCode createCode(String name, String value, String group) {
        return createCode(name, value, group, null);
    }

    public ProjectCode findByCodeName(String codeName) {
        return repository.findByCodeName(codeName).get();
    }

    public boolean codeNameExists(String codeName) {
        return repository.findByCodeName(codeName).isPresent();
    }

    public List<ProjectCode> findByCodeGroup(String codeGroup) {
        return repository.findByCodeGroup(codeGroup);
    }

    public String getValueForCodeName(String codeName) {
        ProjectCode projectCode = findByCodeName(codeName);
        return projectCode.getCodeValue();
    }

    public boolean isValueInCodeGroup(String value, String codeGroup) {
        List<ProjectCode> matches = repository.findByCodeGroupAndCodeValue(codeGroup, value);
        return !matches.isEmpty();
    }

}
